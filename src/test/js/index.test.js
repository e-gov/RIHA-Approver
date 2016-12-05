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
      }
    }
  ];

  it('fills table with info system data', function() {
    loadFixtures('table.html');

    new Approver()._createTableRows(data);

    var rows = $('tbody tr');

    expect(rows.length).toBe(2);
    expect(rows.hasClass('hidden')).toBe(false);
    expect(rows.hasClass('template-row')).toBe(false);
    expect($(rows[0]).find('.name').text()).toBe('Eesti kirikute, koguduste ja koguduste liitude register');
    expect($(rows[0]).find('.owner').text()).toBe('70000562');
    expect($(rows[0]).find('.last-modified').text()).toBe('2015-08-05T08:29:58.328468');
    expect($(rows[0]).find('.approved').text()).toBe('');
    expect($(rows[0]).find('.approve button').data('id')).toBe('70000562|Eesti kirikute, koguduste ja koguduste liitude register');
  });

  it('does not fail if no status defined', function() {
    new Approver()._createTableRows([{}]);
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

