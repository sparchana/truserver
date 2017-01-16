
var applyInShort = (function ($) {

    'use strict';
    var applyInShort = {
        isNavBarLoaded : false,
        loginStatus: null,
        candidateId: null,
        jobPostId: null,
        missingDataURL: null,
        missingData: null,
        method: {
            init: function () {
                applyInShort.validation.checkIsUserLoggedIn();
                applyInShort.method.prepareRequestParam();
                applyInShort.render.applyJobForm();
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
            },
            ending: function () {
                $('#footer_inc').load('/footer');
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

                    console.log(applyInShort.missingData);

                }).catch(function (fromReject) {
                    console.log(fromReject);
                });

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