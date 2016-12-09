"use strict";

function Approver(infosystemsUrl) {

  var approvalsUrl = '/approvals/';

  var self = this;

  self.init = function() {
    loadInfosystems();
    $('body').on('click', '.approve button', self.approveInfosystem);
  };

  function loadInfosystems() {
    $.getJSON(infosystemsUrl, function(data) {
      self._createTableRows(data);
      loadApprovals();
    });
  }

  function loadApprovals () {
    $.getJSON(approvalsUrl, function (data) {
      self._addApprovalsData(data);
      $('#info-systems-table').DataTable({
        language: { "url": "/js/vendor/jquery.dataTables.i18n.json" },
        paging: false,
        order: []
      });
    })
  }

  self._isApprovable = function (lastModified, approval) {
    return new Date(lastModified) > new Date(approval);
  };

  self._addApprovalsData = function (data) {
    data.forEach(function (approval) {
      var row = $('tbody tr[data-id="' + approval.id + '"]');
      $(row.find('.approved')).text(approval.timestamp);
      $(row.find('.approval-status')).text(approval.status);
      if (!self._isApprovable(row.find('.last-modified').text(), approval.timestamp)) {
        row.find('button').attr('disabled', 'disabled');
      }
    })
  };

  self.approveInfosystem = function (event) {
    var clickedButton = $(event.target);
    var infosystemRow = clickedButton.closest('tr');
    $.post('/approve/', {id: infosystemRow.data('id'), status: clickedButton.val()})
      .done(function (result) {
        infosystemRow.find('button').attr('disabled', 'disabled');
        infosystemRow.find('.approved').text(result.timestamp);
        infosystemRow.find('.approval-status').text(result.status);
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
      newRow.find('.status').text(infosystem.status ? infosystem.status.staatus : '');
      tbody.append(newRow);
    });
  }
}
