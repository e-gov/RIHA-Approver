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

  function getBase64Encoded(rawStr){
 	 var wordArray = CryptoJS.enc.Utf8.parse(rawStr);
 	 var result = CryptoJS.enc.Base64.stringify(wordArray);
 	 return result;
  }
  
//because it's mockup the header, payload an signature info is hard coded.
  function createJWT(){
 	 var txtHeader = '{ "alg":"HS256", "typ":"JWT" }';
 	 var txtPayload = '{ "iss":"RIHA autoriseerija", "iat":1491903351, "exp":1491989751, "sub":{ "isikukood":"60107110134", "nimi":{ "eesnimi":"Eero", "perekonnanimi":"Vegmann" } }, "asutus":{ "registrikood":"70006317", "nimetus":"Riigi Infosüsteemi Amet" }, "rollid":{ "roll":"HINDAJA" } }';
 	 var txtsecret = 'password';
 	 
 	 var base64Header = getBase64Encoded(txtHeader);
 	 var base64Payload = getBase64Encoded(txtPayload);
 	 
 	 var signature = CryptoJS.HmacSHA256(base64Header + "." + base64Payload, txtsecret);
 	 var base64Sign = CryptoJS.enc.Base64.stringify(signature);
 	 
 	 console.log(base64Header + "." + base64Payload + "." + base64Sign);
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
        createJWT();
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
      newRow.find('.objective').text(infosystem.objective);
      newRow.find('.last-modified').text(infosystem.meta && infosystem.meta.system_status ? infosystem.meta.system_status.timestamp : '');
      newRow.find('.status').text(infosystem.meta && infosystem.meta.system_status ?  infosystem.meta.system_status.status : '');
      tbody.append(newRow);
    });
  }
}
