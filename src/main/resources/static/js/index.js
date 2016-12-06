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

  self._createTableRows = function(data) {
    var template = $('.template-row');

    var tbody = $('tbody');
    data.forEach(function (infoSystem) {
      var newRow = $(template).clone().removeClass('hidden').removeClass('template-row');
      newRow.find('.owner').text(infoSystem.owner);
      newRow.find('.name').text(infoSystem.name);
      newRow.find('button').attr('data-id', infoSystem.owner + '|' + infoSystem.name);
      newRow.find('.last-modified').text(infoSystem.status ? infoSystem.status.timestamp : '');
      tbody.append(newRow);
    });
  }
}
