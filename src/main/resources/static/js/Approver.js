"use strict";

function Approver(infosystemsUrl) {

  var approvalsUrl = '/approvals';

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

  self._addApprovalsData = function (data) {
    data.forEach(function (approval) {
      var row = $('tbody tr[data-id="' + approval.uri + '"]');
      $(row.find('.approved')).text(approval.timestamp);
      $(row.find('.approval-status')).text(approval.status);
    })
  };

  self.approveInfosystem = function (event) {
    var clickedButton = $(event.target);
    var infosystemRow = clickedButton.closest('tr');
    $.post('/approve/', {id: infosystemRow.data('id'), status: clickedButton.val()})
      .done(function (result) {
        infosystemRow.find('.approved').text(result.timestamp);
        infosystemRow.find('.approval-status').text(result.status);
      });
  };

  self._createTableRows = function(data) {
    var template = $('#row-template').html();

    var tbody = $('tbody');
    data.forEach(function (infosystem) {
      var newRow = $(template);
      newRow.attr('data-id', infosystem.uri);
      newRow.attr('title', JSON.stringify(infosystem));
      newRow.find('.owner').text(infosystem.owner.code);
      newRow.find('.name').text(infosystem.name);
      newRow.find('.last-modified').text(infosystem.meta && infosystem.meta.system_status ? infosystem.meta.system_status.timestamp : '');
      newRow.find('.status').text(infosystem.meta && infosystem.meta.system_status ?  infosystem.meta.system_status.status : '');
      tbody.append(newRow);
    });
  }
}
