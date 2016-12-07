"use strict";

function Approver() {

  var infosystemsUrl = 'https://raw.githubusercontent.com/e-gov/RIHA-API/master/riha_live.json';
  var approvalsUrl = '/approvals/';

  var self = this;

  self.init = function() {
    loadInfosystems();
    $('body').on('click', '.approve button', self.approveInfoSystem);
  };

  function loadInfosystems() {
    $.getJSON(infosystemsUrl, function(data) {
      self._createTableRows(data);
      loadApprovals();
      $('#info-systems-table').DataTable({paging: false});
    });
  }

  function loadApprovals () {
    $.getJSON(approvalsUrl, function (data) {
      self._addApprovalsData(data);
    })
  }

  self._isApprovable = function (lastModified, approval) {
    return new Date(lastModified) > new Date(approval);
  };

  self._addApprovalsData = function (data) {
    data.forEach(function (approval) {
      var row = $('tbody tr[data-id="' + approval.id + '"]');
      $(row.find('.approved')).text(approval.timestamp);
      if (!self._isApprovable(row.find('.last-modified').text(), approval.timestamp)) {
        row.find('button').attr('disabled', 'disabled');
      }
    })
  };

  self.approveInfoSystem = function (event) {
    var approveButton = $(event.target);
    $.post('/approve/', {id: approveButton.closest('tr').data('id')})
      .done(function (result) {
        approveButton.attr('disabled', 'disabled');
        approveButton.closest('tr').find('.approved').text(result.approved);
      });
  };

  self._timeSince = function (timestamp) {
    return timestamp;
//     return moment.utc(timestamp).local().fromNow();
  };

  self._createTableRows = function(data) {
    var template = $('.template-row');

    var tbody = $('tbody');
    data.forEach(function (infosystem) {
      var newRow = $(template).clone().removeClass('hidden').removeClass('template-row').attr('data-id', infosystem.meta.URI);
      newRow.attr('title', JSON.stringify(infosystem));
      newRow.find('.owner').text(infosystem.owner);
      newRow.find('.name').text(infosystem.name);
      newRow.find('.last-modified').text(infosystem.status ? self._timeSince(infosystem.status.timestamp) : '');
      tbody.append(newRow);
    });
  }
}
