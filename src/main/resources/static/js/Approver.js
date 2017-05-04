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

// A function to create a Json Web Token, it encodes header, payload and secret values and then
// returns them as one long string, seperated by '.' . 
// Because it's mockup the header, payload an signature info is hard coded string value.
  function createJWT(){
 	 var txtHeader = '{ "alg":"HS256", "typ":"JWT" }';
 	 var txtPayload = '{ "iss":"RIHA autoriseerija", "iat":1491903351, "exp":1491989751, "sub":{ "isikukood":"60107110134", "nimi":{ "eesnimi":"'+$('#first_name').val()+'", "perekonnanimi":"'+$('#last_name').val()+'" } }, "asutus":{ "registrikood":"'+$('#register_code').val()+'", "nimetus":"'+$('#institution_name').val()+'" }, "rollid":{ "roll":"HINDAJA" } }';
 	 var txtsecret = 'password';
 	 
 	 var base64Header = getBase64Encoded(txtHeader);
 	 var base64Payload = getBase64Encoded(txtPayload);
 	 
 	 var signature = CryptoJS.HmacSHA256(base64Header + '.' + base64Payload, txtsecret);
 	 var base64Sign = CryptoJS.enc.Base64.stringify(signature);
 	 
 	 var token = base64Header + '.' + base64Payload + '.' + base64Sign;
 	 return token;
  }
  
  function saveCookie(){
	  document.cookie = 'Authorization token=' + createJWT();
  }
  
  function clearCookie(cookie_name){
	  var cookie_date = new Date ( );  // current date & time
	  cookie_date.setTime ( cookie_date.getTime() - 1 );
	  document.cookie = cookie_name += "=; expires=" + cookie_date.toGMTString();

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
    
    var firstName = $('#first_name').val();
    var lastName = $('#last_name').val();
    var regCode = $('#register_code').val();
    var instName = $('#institution_name').val();
    
    if(firstName == '' || lastName == '' || regCode == '' || instName == ''){
    	alert('Palun täidke kõik väljad!');
    } else {
    	saveCookie();
    	
    	$.post('/approve/', {id: infosystemRow.data('id'), status: clickedButton.val()})
        .done(function (result) {
          infosystemRow.find('.approved').text(result.timestamp);
          infosystemRow.find('.approval-status').text(result.status);
          clearCookie();
        });
    	
    }
    
    
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
