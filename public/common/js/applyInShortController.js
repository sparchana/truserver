
var applyInShort = (function ($) {

    'use strict';
    var applyInShort = {
        isNavBarLoaded : false,
        loginStatus: null,
        candidateId: null,
        jobPostId: null,
        missingDataURL: null,
        missingData: null,
        jobTitle: null,
        companyName: null,
        method: {
            init: function () {
                applyInShort.validation.checkIsUserLoggedIn();
                applyInShort.method.prepareRequestParam();
                applyInShort.render.applyJobForm();

            },
            ending: function () {
                $('#footer_inc').load('/footer');
            },
            getUserLogInStatus: function () {
                if (!applyInShort.isNavBarLoaded) {
                    return $.ajax({type: 'GET', url: '/checkNavBar'});
                } else {
                    // new promise says its already there
                    return new Promise(function (resolve, reject) {
                        resolve(null);
                    });
                }
            },
            getMissingData: function () {
                if (applyInShort.missingDataURL != null) {
                    return $.ajax({type: 'GET', url: applyInShort.missingDataURL});
                } else {
                    // new promise says its already there
                    return new Promise(function (resolve, reject) {
                        resolve(null);
                    });
                }
            },
            prepareRequestParam: function () {
                var pathNameList = window.location.pathname.split('-');
                applyInShort.jobPostId = pathNameList[pathNameList.length -1];
                var searchParam = window.location.search.split("&")[0].split("=");
                applyInShort.candidateId = searchParam[searchParam.length -1];

                applyInShort.missingDataURL =
                             "/apply/inshort/api/getMissingDate?candidateId="+applyInShort.candidateId
                            +"&jobPostId="+applyInShort.jobPostId;
            }
        },
        fetch: {
          missingData: function () {
              return new Promise(function (resolve, reject) {
                      applyInShort.method.getMissingData().then(
                          function (returnedData) {
                              if (returnedData != null) {
                                  applyInShort.missingData = returnedData;
                              }
                              resolve();
                          },
                          function (xhr, state, error) {
                              reject(error);
                          }
                      )
                  }
              );
          }
        },
        render: {
            applyJobForm: function () {

                var promise = applyInShort.fetch.missingData();

                promise.then(function () {

                    /* render locality card */
                    if(applyInShort.missingData.localityPopulateResponse != null) {

                        applyInShort.render.jobLocalityCard(applyInShort.missingData.localityPopulateResponse);
                    }

                    /* TODO render prescreen card */
                    if(applyInShort.missingData.shortPSPopulateResponse != null) {

                    }


                    /* render interview slot card */
                    if(applyInShort.missingData.interviewSlotPopulateResponse != null
                        && applyInShort.missingData.interviewSlotPopulateResponse.interviewResponse.status == 2
                        && applyInShort.missingData.interviewSlotPopulateResponse.interviewSlotMap != null) {

                        applyInShort.render.interviewSlotCard(applyInShort.missingData.interviewSlotPopulateResponse.interviewSlotMap);
                    } else {
                        $('#jobInterviewSlotDiv').hide();
                    }

                    console.log(applyInShort.missingData);

                }).catch(function (fromReject) {
                    console.log(fromReject);
                });

            },
            jobLocalityCard: function (localityResponse) {
                console.log("rendering jobLocality card");

                var localityMap = localityResponse.localityMap;

                applyInShort.jobTitle = localityResponse.jobTitle;
                applyInShort.companyName = localityResponse.companyName;

                $('#locality_jobNameConfirmation').html(applyInShort.jobTitle);
                $('#locality_companyNameConfirmation').html(applyInShort.companyName);

                if(localityMap != null) {
                    var option = $('<option value=0></option>').text("Select Location");
                    $('#jobLocality').append(option);
                    for (var value in localityMap) {
                        var id = value;
                        var title = localityMap[value];
                        var option = $('<option value=' + id + '></option>').text(title);
                        $('#jobLocality').append(option);
                    }
                }
                /* clear mem */
                localityMap = null;
            },
            interviewSlotCard: function (slotMap) {
                console.log("rendering interview slot card");

                if(slotMap == null) {
                    return;
                }


                $('#interviewJobTitle').html(applyInShort.jobTitle);
                $('#interviewCompanyName').html(applyInShort.companyName);

                if(slotMap != null) {
                    var option = $('<option value=0></option>').text("Select Interview slot");
                    $('#interViewSlot').append(option);
                    for (var value in slotMap) {
                        var date = slotMap[value].interviewDateMillis;
                        var id = date +"_"+slotMap[value].interviewTimeSlot.slotId;
                        var title = value;
                        var option = $('<option value=' + id + '></option>').text(title);
                        $('#interViewSlot').append(option);
                    }
                }
            },
            preScreenCard: function () {

            }

        },
        validation: {
            checkIsUserLoggedIn: function () {
                var promise = new Promise(function (resolve, reject) {
                        applyInShort.method.getUserLogInStatus().then(
                            function (returnedData) {
                                if (returnedData != null) {
                                    applyInShort.loginStatus = returnedData;
                                }
                                resolve();
                            },
                            function (xhr, state, error) {
                                reject(error);
                            }
                        )
                    }
                );
                promise.then(function () {
                    if (applyInShort.loginStatus == '1') {
                        $('#nav_bar_inc').load('/navBarLoggedIn');
                        $(".jobApplyBtnV2").show();
                        $("#incentiveSection").hide();
                    } else if (applyInShort.loginStatus == '2') {
                        $('#nav_bar_inc').load('/partnerNavBarLoggedIn');
                        $(".jobApplyBtnV2").hide();
                        $("#incentiveSection").show();
                    } else {
                        $('#nav_bar_inc').load('/navBar');
                        $("#incentiveSection").hide();
                    }
                    applyInShort.isNavBarLoaded = true;
                }).catch(function (fromReject) {
                    console.log(fromReject);
                });
            },
        }

    };

    applyInShort.method.init();
    applyInShort.method.ending();

    return applyInShort;
}(jQuery));