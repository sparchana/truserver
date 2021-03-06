/*
 * truSearch: TruJobs Search mini plugin
 * Version 1.0.0
 *
 * Copyright (c) 2016 TruJobs.in (http://trujobs.in)
 *
 * Created by zero on 30/12/16.
 *
 */

(function ($) {

    String.prototype.capitalizeFirstLetter = function() {
        return this.charAt(0).toUpperCase() + this.slice(1);
    };

    String.prototype.toTitleCase = function () {
        return this.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
    };
    var DEFAULT_VALUES = {
        D_SEARCH_KEYWORD_IDENTIFIER: "jobs-in-bangalore",
        D_LOCALITY_IDENTIFIER: "near-",
        D_LOCATION_ALL_BANGALORE: {}
    };
    var app = {
        allJobRole: [],
        allLocation: [],
        currentURL: null,
        suggestion: null,

        // basic getter/setter types method
        bMethods: {
            init: function () {
                console.log("init");
                app.render.renderTextSearch();
                app.render.renderLocation();

            },
            getAllJobRole: function () {
                //ajax call and save data to allJobRole
                if (app.allJobRole.length == 0) {
                    return $.ajax({type: 'POST', url: '/getAllJobs'});
                } else {
                    // new promise says its already there
                    return new Promise(function (resolve, reject) {
                        resolve(null);
                    });
                }
            },
            getAllLocation: function () {
                //ajax call || its a promise
                if (app.allLocation.length == 0) {
                    return $.ajax({type: 'POST', url: '/getAllLocality'});
                } else {
                    // new promise says its already there
                    return new Promise(function (resolve, reject) {
                        resolve(null);
                    });
                }
            },
            getAllLanguage: function () {
                //ajax call and save data to allExperience
                if (app.allLanguage.length == 0) {
                    return $.ajax({type: 'POST', url: '/getAllLanguage'});
                } else {
                    // new promise says its already there
                    return new Promise(function (resolve, reject) {
                        resolve(null);
                    });
                }
            }
        },
        // basic ui rendering methods
        render: {
            renderJobRole: function () {
                var promise = new Promise(function (resolve, reject) {
                        app.bMethods.getAllJobRole().then(
                            function (returnedData) {
                                if (returnedData != null) {
                                    returnedData.forEach(function (jobRole) {
                                        var id = jobRole.jobRoleId;
                                        var name = jobRole.jobName;
                                        var item = {};
                                        item ["id"] = id;
                                        item ["name"] = name;
                                        app.allJobRole.push(item);
                                    });
                                }
                                resolve();
                            },
                            function (xhr, state, error) {
                                reject(error);
                            }
                        );
                    }
                );

            },
            renderLocation: function (locality) {
                if (locality != null) {
                    $('#searchLocation').tokenize({
                        displayDropdownOnFocus: true,
                        placeholder: "Location",
                        newElements: true,
                        nbDropdownElements: 1000,
                        maxElements: 1
                    }).tokenRemove($('#searchLocation').val()[0]).tokenAdd(locality.localityId, locality.localityName);
                }
                // this if check prevents static token suggestion from being duplicated
                else if ($("#searchLocation option[value='1']").length == 0) {
                    var promise = new Promise(function (resolve, reject) {
                            app.bMethods.getAllLocation().then(
                                function (returnedData) {
                                    if (returnedData != null) {
                                        returnedData.forEach(function (locality) {
                                            app.allLocation.push(locality);
                                        });
                                    }
                                    resolve();
                                },
                                function (xhr, state, error) {
                                    reject(error);
                                }
                            );
                        }
                    );

                    promise.then(function () {
                        DEFAULT_VALUES.D_LOCATION_ALL_BANGALORE = {id: "0", name: "All Bangalore"};
                        var option = $('<option value="0"></option>').text("All Bangalore");
                        $('#searchLocation').append(option);

                        app.allLocationArray = [];
                        app.allLocation.forEach(function (locality) {
                            var id = locality.localityId;
                            var name = locality.localityName;
                            option = $('<option value=' + id + '></option>').text(name);
                            $('#searchLocation').append(option);

                            var item = {};
                            item ["id"] = id;
                            item ["name"] = name;
                            app.allLocationArray.push(item);
                        });

                        console.log("app.allLocation size: " + app.allLocation.length);
                        $('#searchLocation').tokenize({
                            displayDropdownOnFocus: true,
                            placeholder: "Location",
                            newElements: true,
                            nbDropdownElements: 1000,
                            maxElements: 1
                        });

                        $('#searchLocation').tokenize().tokenRemove('0');
                        $('#searchLocation').tokenize().tokenAdd(DEFAULT_VALUES.D_LOCATION_ALL_BANGALORE.id, DEFAULT_VALUES.D_LOCATION_ALL_BANGALORE.name);

                    }).catch(function (fromReject) {
                        console.log(fromReject);
                    });
                }
            },
            // render filter
            renderTextSearch: function () {
                var input = $("#searchText");
                var numberOfKeywords = input.val().split(",");

                if(app.suggestion == null){
                    app.suggestion = new Awesomplete('input[data-multiple]', {
                        minChars: 1,
                        autoFirst: true,
                        filter: function (text, input) {
                            return Awesomplete.FILTER_CONTAINS(text, input.match(/[^,]*$/)[0]);
                        },

                        replace: function (text) {
                            var before = this.input.value.match(/^.+,\s*|/)[0];
                            this.input.value = before + text + ", ";
                        }
                    });
                }
                $('.awesomplete').css('width', '100%');

                if((input.val() == null || input.val().length < 3) ){
                    return;
                }

                $.ajax({
                    url: '/ss/?key=' + input.val(),
                    type: 'GET',
                    dataType: 'json'
                }).success(function (data) {
                    var list = [];
                    $.each(data, function (key, value) {
                        if(!(input.val().toLowerCase().indexOf(value.toLowerCase() + ",") >= 0)){
                            list.push(value);
                        }
                    });
                    app.suggestion.list = list;
                    if(app.suggestion.list != null && app.suggestion.list.length == 0) {
                        // when no suggestions are returned. do something here...
                    }
                });
            }
        },
        // action perform methods
        do: {
            redirectToSearchPage: function () {

                // redirect with the formed url
                window.location = "/s/"+app.currentURL;
            },
            fillSearchTextBoxWithKeywords: function (keywordList) {
                var searchBoxText = keywordList.join(" ") + ", ";
                document.getElementById("searchText").value = searchBoxText.toTitleCase();
            },
            prepNmodifyURL:function () {
                return app.do.modifyURL(app.do.prepareURL());
            },
            modifyURL: function (url) {
                // TODO ideally this should change after the result is returned
                window.history.pushState("object or string", "Title", "/s/" + url);
                return "/s/" + url;
            },
            prepareURL: function () {
                var _searchStr = document.getElementById("searchText").value;

                if (_searchStr != null || _searchStr.trim() != "") {
                    _searchStr += "-'";
                }
                // forced in-bangalore
                _searchStr += DEFAULT_VALUES.D_SEARCH_KEYWORD_IDENTIFIER;

                // replace all  all non-alphanumeric characters
                _searchStr = _searchStr.replace(/[^a-z0-9]+/gi, '-');

                // replace first occurance of -
                _searchStr = _searchStr.replace(/^(-)+/, "");

                var _location = document.getElementById("searchLocation");
                var _text = _location.options[_location.selectedIndex];
                if (_location.value != 0 && _text != null) {
                    _searchStr += "_" + DEFAULT_VALUES.D_LOCALITY_IDENTIFIER + _text.innerHTML.replace(/\s+/g, '-');
                }

                return (_searchStr + "/").toLowerCase();
            }
        },
        // action validator methods
        run: {
            validateWorkShift: function (jobPostShift) {
                if(jobPostShift == null) {
                    return "";
                } else if(jobPostShift.timeShiftName){
                    return jobPostShift.timeShiftName;
                }
            },
            urlChangeDetector: function () {
                if (window.history && window.history.pushState) {

                    $(window).on('popstate', function () {
                        location.reload();
                    });

                }
            },
            basicReset: function () {
                app.page = 1;
                app.isPaginationEnabled = false;


                if ($('#searchLocation').val() == null) {
                    $('#searchLocation').tokenize({
                        displayDropdownOnFocus: true,
                        placeholder: "Location",
                        nbDropdownElements: 1000,
                        maxElements: 1
                    }).tokenAdd(DEFAULT_VALUES.D_LOCATION_ALL_BANGALORE.id,
                        DEFAULT_VALUES.D_LOCATION_ALL_BANGALORE.name);
                }
            }
        }
    };
    // search click listener
    document.getElementById("searchBtn").addEventListener("click", function () {
        app.page = 1; // reset page to 1 for new search
        app.currentURL = app.do.prepareURL();

        app.do.redirectToSearchPage();
    });

    // control flow
    app.bMethods.init();

    // this detect the typing even on the search bar
    $('#searchText').on('keyup',function (event) {
        if(!(event.keyCode >= 32 && event.keyCode <= 47) && (event.keyCode != 13)){
            app.render.renderTextSearch();
            // trigger suggestion only when typing alpha numeric is happening
        }
    });


}(jQuery));


function showField(){
    $('#mainFieldSearch').removeClass("col-lg-9").addClass("col-lg-5");
}