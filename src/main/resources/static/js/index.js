"use strict";

function Approver() {

  var self = this;

  self.init = function() {
    loadInfoSystems();

    $('body').on('click', '.approve button', self.approveInfoSystem);
  };

  function loadInfoSystems() {
    $.getJSON('https://raw.githubusercontent.com/e-gov/RIHA-API/master/riha_live.json', function(data) {
      self._createTableRows(data);
    });
  }

  self.approveInfoSystem = function (event) {
    var approveButton = $(event.target);
    $.post('/save/', {id: approveButton.data('id')})
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
