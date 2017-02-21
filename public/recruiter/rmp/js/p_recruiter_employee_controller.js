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

        method: {
            init: function () {
                zapp.get.jpIdListFromUrl();
                zapp.get.allEmployee();
            }
        },
        render: {
            buklSendSms: function () {
              console.log("bulk send comes here");
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
                            "emptyTable": "No Employee found!"
                        },
                        "order": [[1, "desc"]],
                        responsive: true,
                        "destroy": true,
                        "dom": 'Bfrtip',
                        "buttons": [
                            'copy', 'csv', 'excel',
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

              var urlParams = window.location.search.split('=');
              var jpId = null;
              if(urlParams[0] == "?jpId") {
                  jpId = parseInt(urlParams[1]);
              }
              if(zapp.sEmployeeList.length > 0){
                  $("#sendSms").addClass("disabled");
                  var s = {
                      employeeIdList: zapp.sEmployeeList,
                      jobPostIdList :zapp.jpIdList,
                      smsType : 3
                  };

                  $.ajax({
                      type: "POST",
                      url: "/bulkSendSmsEmployee",
                      contentType: "application/json; charset=utf-8",
                      data: JSON.stringify(s),
                      success: zapp.render.buklSendSms
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