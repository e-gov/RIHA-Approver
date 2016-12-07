describe('Approver', function() {

  var data = [
    {
      "name": "Eesti kirikute, koguduste ja koguduste liitude register",
      "shortname": "Eesti kirikuregister",
      "owner": "70000562",
      "documentation": "https://riha.eesti.ee/riha/main/inf/eesti_kirikute_koguduste_ja_koguduste_liitude_register",
      "status": {
        "staatus": "L\u00f5petatud",
        "timestamp": "2015-08-05T08:29:58.328468"
      },
      "meta": {
        "URI": "/70000562/Eesti kirikuregister",
        "timestamp": "2015-09-05T00:36:26.255215"
      }
    },
    {
      "name": "\u00d5pilaste ja \u00fcli\u00f5pilaste register",
      "shortname": "\u00d5ppurite register",
      "owner": "70000740",
      "documentation": "https://riha.eesti.ee/riha/main/inf/opilaste_ja_uliopilaste_register",
      "status": {
        "staatus": "L\u00f5petatud",
        "timestamp": "2013-11-08T15:46:15.121725"
      },
      "meta": {
        "URI": "/70000740/\u00d5ppurite register",
        "timestamp": "2013-11-14T13:43:55.546948"
      },
      "approved": "2016-09-05T00:36:26"
    }
  ];

  it('fills table with info system data', function() {
    loadFixtures('table.html');
    var approver = new Approver();
    spyOn(approver, '_timeSince').and.returnValue("1 day ago");

    approver._createTableRows(data);

    var rows = $('tbody tr:not(.template-row)');

    expect(rows.length).toBe(2);
    expect(rows.hasClass('hidden')).toBe(false);
    expect(rows.hasClass('template-row')).toBe(false);
    expect($(rows[0]).find('.name').text()).toBe('Eesti kirikute, koguduste ja koguduste liitude register');
    expect($(rows[0]).find('.owner').text()).toBe('70000562');
    expect($(rows[0]).data('id')).toBe('/70000562/Eesti kirikuregister');
    expect($(rows[0]).find('.last-modified').text()).toBe('1 day ago');
    expect($(rows[0]).find('.status').text()).toBe('Lõpetatud');
    expect(approver._timeSince).toHaveBeenCalledWith('2015-08-05T08:29:58.328468');
  });

  describe('is approvable', function() {
    it('if not yet approved', function() {
      expect(new Approver()._isApprovable("2015-01-01T10:00:00", null)).toBe(true);
    });

    it('if approved before latest modification date', function() {
      expect(new Approver()._isApprovable("2016-01-01T10:00:00", '2010-01-01T10:00:00')).toBe(true);
    });

    it('false if approved after latest modification date', function() {
      expect(new Approver()._isApprovable("2010-01-01T10:00:00", '2011-01-01T10:00:00')).toBe(false);
    });
  });

  describe('adds approval', function() {
    it('to approved infosystem', function() {
      loadFixtures('table.html');
      var approver = new Approver();
      $('tbody tr').attr('data-id', '/owner/shortname');
      $('tbody td.last-modified').text('2016-01-01T10:00:00');

      spyOn(approver, '_isApprovable').and.returnValue(false);
      approver._addApprovalsData([{"id":"/owner/shortname", "timestamp":"2015-01-01T10:00:00"}]);

      expect($('tbody .approved').text()).toBe('2015-01-01T10:00:00');
      expect($('.approve button').attr('disabled')).toBe('disabled');
      expect(approver._isApprovable).toHaveBeenCalledWith('2016-01-01T10:00:00', '2015-01-01T10:00:00');
    });

    it('to infosystem approved before latest modification', function() {
      loadFixtures('table.html');
      var approver = new Approver();
      $('tbody tr').attr('data-id', '/owner/shortname');
      $('tbody td.last-modified').text('2015-01-01T10:00:00');

      spyOn(approver, '_isApprovable').and.returnValue(true);
      approver._addApprovalsData([{"id":"/owner/shortname", "timestamp":"2016-01-01T10:00:00"}]);

      expect($('tbody .approved').text()).toBe('2016-01-01T10:00:00');
      expect($('.approve button').is('[disabled]')).toBe(false);
      expect(approver._isApprovable).toHaveBeenCalledWith('2015-01-01T10:00:00', '2016-01-01T10:00:00');
    });
  });
  
  describe('Approve button', function() {
    it('changes info system status to Approved and sets approval timestamp', function() {
      setFixtures(
        '<tr>' +
          '<td class="approved"></td>' +
          '<td class="approve"><button data-id="1000-RIA">Kinnita</button></td>' +
        '</tr>');
      spyOn($, 'post').and.returnValue(promise({approved: '2016-12-05T15:29:00.128468'}));
      var event  = {target: $('button')};

      new Approver().approveInfoSystem(event);

      expect($('.approved').text()).toBe('2016-12-05T15:29:00.128468');
      expect($('.approve button').attr('disabled')).toBe('disabled');
    });
  });
});

