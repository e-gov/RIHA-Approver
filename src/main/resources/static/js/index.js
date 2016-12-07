"use strict";

function Approver() {

  var self = this;

  self.init = function() {
    loadInfoSystems();

    $('body').on('click', '.approve button', self.approveInfoSystem);
  };

  function loadInfoSystems() {
    $.getJSON('/infosystems/', function(data) {
      self._createTableRows(data);
      $('#info-systems-table').DataTable({paging: false});
    });
  }

  self.approveInfoSystem = function (event) {
    var approveButton = $(event.target);
    $.post('/approve/', {id: approveButton.data('id')})
      .done(function (result) {
        approveButton.attr('disabled', 'disabled');
        approveButton.closest('tr').find('.approved').text(result.approved);
      });
  };

  self._canNotBeApproved = function (infosystem) {
    return infosystem.approved && infosystem.status
      && new Date(infosystem.status.timestamp) < new Date(infosystem.approved);
  };

  self._timeSince = function (timestamp) {
    return moment.utc(timestamp).local().fromNow();
  };

  self._createTableRows = function(data) {
    var template = $('.template-row');

    var tbody = $('tbody');
    data.forEach(function (infosystem) {
      var newRow = $(template).clone().removeClass('hidden').removeClass('template-row');
      newRow.attr('title', JSON.stringify(infosystem));
      newRow.find('.owner').text(infosystem.owner);
      newRow.find('.name').text(infosystem.name);
      var button = newRow.find('button').attr('data-id', infosystem.meta.URI);
      if (self._canNotBeApproved(infosystem)) {
        button.attr('disabled', 'disabled');
      }
      newRow.find('.last-modified').text(infosystem.status ? self._timeSince(infosystem.status.timestamp) : '');
      newRow.find('.approved').text(infosystem.approved);
      tbody.append(newRow);
    });
  }
}
