"use strict";

function Approver() {

  var self = this;

  self.init = function() {
    loadInfoSystems();
  };

  function loadInfoSystems() {
    $.get('https://raw.githubusercontent.com/e-gov/RIHA-API/master/riha_live.json', function(data) {
      self._createTableRows(JSON.parse(data));
    });
  }

  self._createTableRows = function(data) {
    var template = $('.template-row');

    var tbody = $('tbody');
    data.forEach(function (infoSystem) {
      var newRow = $(template).clone().removeClass('hidden').removeClass('template-row');
      newRow.find('.owner').text(infoSystem.owner);
      newRow.find('.name').text(infoSystem.name);
      newRow.find('.last-modified').text(infoSystem.status ? infoSystem.status.timestamp : '');
      tbody.append(newRow);
    });
  }
}
