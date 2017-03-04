/*
 * Created by zero on 25/01/17.
 *
 */


var zapp = (function () {
    'use strict';

    var zapp = {
        rows : [],
        companyName: "",
        sEmployeeList: [],
        jpIdList: [],
        smsText: null,

        method: {
            init: function () {
                zapp.get.jpIdListFromUrl();
                zapp.get.allEmployee();
            }
        },
        render: {
            buklSendSmsResponse: function (returnedData) {
              console.log(returnedData);
                if(returnedData != null) {
                    if(returnedData.status == 1) {
                        // success
                        notifySuccess("Sms Successfully sent");
                    } else {
                        // failed
                        returnedData.messages.forEach(function (msg) {
                            notifyError(msg.text);
                        });
                    }
                }
            },
            employeeDataTable: function () {
                try {
                    var table = $('table#employeeTable').DataTable({
                        "ajax": {
                            "type": "GET",
                            "url": '/recruiter/getAllEmployee',
                            "dataSrc": function (returnedData) {

                                console.log(returnedData);
                                zapp.rows = returnedData;

                                $("#employeeTable").show();
                                $("#employeeTableContainer").show();
                                $("#loadingIcon").hide();

                                var returned_data = [];

                                zapp.rows.forEach(function (partner) {

                                    returned_data.push({
                                        'partnerId': partner.partnerId,
                                        'employeeName': zapp.do.concatString(partner.firstName, partner.lastName ),
                                        'employeeMobile': partner.mobile,
                                        'employeeEmailId': partner.emailId,
                                        'employeeId': partner.employeeId,
                                        'employeeLocalityName': partner.locality,
                                    });
                                });

                                return returned_data;
                            }
                        },
                        "deferRender": true,
                        "rowId": "partnerId",
                        "columns": [
                            { "data": "employeeName" },
                            { "data": "employeeMobile" },
                            { "data": "employeeEmailId" },
                            { "data": "employeeLocalityName" },
                            { "data": "employeeId" }
                        ],
                        "language": {
                            "emptyTable": "No Employee found!",
                            buttons: {
                                selectAll: "Select All",
                                selectNone: "Select None"
                            }
                        },
                        "order": [[1, "desc"]],
                        responsive: true,
                        "destroy": true,
                        "dom": 'Bfrtip',
                        "buttons": [
                            'copy', 'csv', 'excel','selectAll', 'selectNone',
                            {
                                text: 'Send SMS',
                                action: function ( e, dt, node, config ) {
                                    if(table.rows({selected: true}).ids().length == 0){
                                        notifyError("Please select at least 1 employee");
                                        return;
                                    }

                                    var selectedCandidateIds = table.rows({selected: true}).ids();
                                    var arrayLength = selectedCandidateIds.length;
                                    zapp.sEmployeeList = [];
                                    for (var i = 0; i < arrayLength; i++) {
                                        zapp.sEmployeeList.push(parseInt(selectedCandidateIds[i]));
                                    }

                                    $("#smsText").val('');
                                    $("#totalCount").html("Total " + zapp.sEmployeeList.length + " Employee");
                                    $("#sendSmsModal").openModal();

                                    notifySuccess( table.rows({selected: true}).data().length +' row(s) selected' );
                                }
                            }
                        ],
                        "select": {
                            "style": 'multi'
                        }
                    });
                } catch (exception) {
                    console.log("exception occured!!" + exception);
                }
            }
        },
        do: {
            concatString: function (a, b) {
                if (a == null && b == null) {
                    return "NA";
                }
                if (b != null) {
                    return a + " " + b;
                } else return a;
            },
          sendSms: function () {
              console.log("sms send trigger");

              zapp.smsText = $("#smsText").val();

              if(zapp.sEmployeeList.length > 0){

                  // prep jp_id_list
                  zapp.get.jpIdListFromUrl();

                  if(zapp.smsText == null) {
                      console.log("empty message");
                  }

                  $("#sendSms").addClass("disabled");
                  var s = {
                      employeeIdList: zapp.sEmployeeList,
                      jobPostIdList :zapp.jpIdList,
                      smsText :zapp.smsText,
                      smsType : 3
                  };

                  $.ajax({
                      type: "POST",
                      url: "/recruiter/bulkSendSmsEmployee",
                      contentType: "application/json; charset=utf-8",
                      data: JSON.stringify(s),
                      success: zapp.render.buklSendSmsResponse
                  });
              } else{
                  notifyError("Please select atleast 1 candidate to send SMS");
              }
          }
        },
        get: {
            allEmployee: function () {
                zapp.render.employeeDataTable();
            },
            jpIdListFromUrl: function () {
                zapp.jpIdList = [];
                var jpIdList = window.location.search.split('=')[1].split(",");
                jpIdList.forEach(function(jpId)
                {
                    zapp.jpIdList.push(parseInt(jpId));
                });
             }
        },
        validate: {
            sms: function () {
                if($("#smsText").val() == ""){
                    $("#sendSms").removeClass("disabled").addClass("disabled");
                } else{
                    $("#sendSms").removeClass("disabled");
                }
            }
        }
    };

    zapp.method.init();

    // sendSms Listner
    $('#sendSms').on('click', function(event) {
       zapp.do.sendSms();
    });


    return zapp;
}());


function checkSmsText() {
    zapp.validate.sms();
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}


function closeSmsModal() {
    $("#sendSmsModal").closeModal();
}