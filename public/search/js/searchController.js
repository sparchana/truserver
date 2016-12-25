/*
 * truSearch: TruJobs Search plugin
 * Version 1.0.0
 *
 * Copyright (c) 2016 TruJobs.in (http://trujobs.in)
 *
 * Created by zero on 23/12/16.
 *
 */

;(function ($) {
    'use strict';

    var DEFAULT_VALUES = {
        D_SEARCH_URL: "jobs-in-bangalore",
        D_SEARCH_KEYWORD_IDENTIFIER: "jobs-in-bangalore",
        D_LOCALITY_IDENTIFIER: "near-",
        D_EDU_IDENTIFIER: "-pass",
        D_EXP_IDENTIFIER: "-experience"
    };
    var app = {
        allJobRole: [],
        allLocation: [],
        allEducation: [],
        allExperience: [],
        currentURL: window.location.pathname,
        currentSearchParams: {},
        currentFilterParams: {},
        currentSortParams: {},
        currentSearchURL: window.location.pathname.split('/')[window.location.pathname.split('/').length - 2],

        // basic getter/setter types method
        bMethods : {
            init: function () {
                console.log("init");
                if (!(app.currentSearchURL == DEFAULT_VALUES.D_SEARCH_URL)) {
                    app.do.prepareSearchParamFromURL();
                    app.do.search();
                }
                app.render.renderTextSearch();
                app.render.renderLocation();
                app.render.renderEducation();
                app.render.renderExperince();
                app.run.urlChangeDetector();
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
            getAllEducation: function () {
                //ajax call and save data to allEducation
                if (app.allEducation.length == 0) {
                    return $.ajax({type: 'POST', url: '/getAllEducation'});
                } else {
                    // new promise says its already there
                    return new Promise(function (resolve, reject) {
                        resolve(null);
                    });
                }
            },
            getAllExperience: function () {
                //ajax call and save data to allExperience
                if (app.allExperience.length == 0) {
                    return $.ajax({type: 'POST', url: '/getAllExperience'});
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
            renderLocation: function () {

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
                    console.log("render location");
                    var option = $('<option value="0"></option>').text("All Bangalore");
                    $('#searchLocation').append(option);
                    app.allLocation.forEach(function (locality) {
                        var id = locality.localityId;
                        var name = locality.localityName;
                        option = $('<option value=' + id + '></option>').text(name);
                        $('#searchLocation').append(option);
                    });

                    $('#searchLocation').tokenize({
                        displayDropdownOnFocus: true,
                        placeholder: "Location",
                        newElements: true,
                        nbDropdownElements: 1000,
                        maxElements: 1
                    });
                }).catch(function (fromReject) {
                    console.log(fromReject);
                });

            },
            renderEducation: function () {

                var promise = new Promise(function (resolve, reject) {
                        app.bMethods.getAllEducation().then(
                            function (returnedData) {
                                if (returnedData != null) {
                                    returnedData.forEach(function (education) {
                                        app.allEducation.push(education);
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
                    console.log("render education");
                    var option ;
                    var first = {};
                    var initId = null;
                    app.allEducation.forEach(function (education) {
                        var id = education.educationId;
                        var name = education.educationName;
                        option = $('<option value=' + id + '></option>').text(name);
                        $('#searchEducation').append(option);

                        if(initId == null) {
                            initId = id;
                        }

                        if(name == "Any") {
                            console.log("found any");
                            first = {'id':  id, 'name': name };
                        }
                    });

                    $('#searchEducation').tokenize({
                        displayDropdownOnFocus: true,
                        placeholder: "Education",
                        newElements: true,
                        nbDropdownElements: 1000,
                        maxElements: 1
                    });

                    $('#searchEducation').tokenize().tokenRemove(initId);
                    $('#searchEducation').tokenize().tokenAdd(first.id, first.name);
                }).catch(function (fromReject) {
                    console.log(fromReject);
                });

            },
            renderExperince: function () {

                var promise = new Promise(function (resolve, reject) {
                        app.bMethods.getAllExperience().then(
                            function (returnedData) {
                                if (returnedData != null) {
                                    returnedData.forEach(function (experience) {
                                        app.allExperience.push(experience);
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
                    console.log("render experience");
                    var option;
                    var first = {};
                    var initId = null;

                    app.allExperience.forEach(function (experience) {
                        var id = experience.experienceId;
                        var name = experience.experienceType;
                        option = $('<option value=' + id + '></option>').text(name);

                        if(initId == null) {
                            initId = id;
                        }

                        if(experience.experienceType == "Any") {
                            console.log("found any");
                            first = {'id':  id, 'name': name };
                        }

                        $('#searchExperience').append(option);
                    });

                    $('#searchExperience').tokenize({
                        displayDropdownOnFocus: true,
                        placeholder: "Experience",
                        newElements: true,
                        nbDropdownElements: 1000,
                        maxElements: 1
                    });

                    $('#searchExperience').tokenize().tokenRemove(initId);
                    $('#searchExperience').tokenize().tokenAdd(first.id, first.name);

                }).catch(function (fromReject) {
                    console.log(fromReject);
                });
            },
            renderTextSearch: function () {
                var input = document.getElementById("searchText");
                var awesomplete = new Awesomplete('input[data-multiple]', {
                    minChars: 1,
                    autoFirst: true,
                    filter: function(text, input) {
                        return Awesomplete.FILTER_CONTAINS(text, input.match(/[^,]*$/)[0]);
                    },

                    replace: function(text) {
                        var before = this.input.value.match(/^.+,\s*|/)[0];
                        this.input.value = before + text + ", ";
                    }
                });

                $.ajax({
                    url: '/ss/?key=' + input.textContent,
                    type: 'GET',
                    dataType: 'json'
                }).success(function(data) {
                        var list = [];
                        $.each(data, function(key, value) {
                            list.push(value.jobName);
                        });
                        awesomplete.list = list;
                });
                $('.awesomplete').css('width','100%')
            }
        },
        // action perform methods
        do: {
            search: function () {
                // ajax call
                var d = {
                    searchParamRequest: app.currentSearchParams,
                    filterParamRequest: app.filterParamRequest,
                    sortParamRequest: app.sortParamRequest
                };

                $.ajax({
                    type: "POST",
                    url: "/api/search/",
                    async: true,
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: function (returnedData) {
                        app.do.parseSearchResponse(returnedData);
                    },
                    error: function (xhr, a, message) {
                        console.log("error: " + message);
                    }
                });
            },
            resetFilters: function () {
                console.log("reset filter ");
            },
            modifyURL: function (url) {
                // TODO ideally this should change after the result is returned
                window.history.pushState("object or string", "Title", "/s/"+url);
                return "/s/"+url;
            },
            prepareURL: function () {
                var _searchStr = document.getElementById("searchText").value;

                if(_searchStr != null || _searchStr.trim() != ""){
                    _searchStr+= "-'";
                }
                // forced in-bangalore
                _searchStr += DEFAULT_VALUES.D_SEARCH_KEYWORD_IDENTIFIER;

                // replace all  all non-alphanumeric characters
                _searchStr = _searchStr.replace(/[^a-z0-9]+/gi, '-');

                // replace first occurance of -
                _searchStr = _searchStr.replace(/^(-)+/,"");

                var _location = document.getElementById("searchLocation");
                var _text =  _location.options[_location.selectedIndex];
                if(_location.value != 0 && _text!= null){
                    _searchStr += "_"+DEFAULT_VALUES.D_LOCALITY_IDENTIFIER + _text.innerHTML.replace(/\s+/g, '-');
                }

                var _education = document.getElementById("searchEducation");
                _text = _education.options[_education.selectedIndex];

                if(_education.value != 0 && _text != null && _text.innerHTML.toLowerCase() != "any"){
                    _searchStr += "_for-" + _text.innerHTML.replace(/[^a-z0-9]+/gi, '-') + DEFAULT_VALUES.D_EDU_IDENTIFIER;
                }

                var _experience = document.getElementById("searchExperience");
                _text = _experience.options[_experience.selectedIndex];
                if(_experience.value != 0 && _text != null && _text.innerHTML.toLowerCase() != "any"){
                    _searchStr += "_for-" + _text.innerHTML.replace(/[^a-z0-9]+/gi, '-') + DEFAULT_VALUES.D_EXP_IDENTIFIER;
                }

                return (_searchStr+"/").toLowerCase();
            },
            prepareSearchParamFromURL: function() {

                // interprates url and create search params
                try {
                    var url = window.location.pathname.split('/');
                    var _searchUrl = url[url.length - 2];
                    app.currentSearchURL = _searchUrl;
                    app.currentSearchParams = {};
                    if(_searchUrl != null){
                        var list = _searchUrl.split('_');
                        // run identifier on this array;
                        var i;
                        for (i = 0; i < list.length; i++) {
                            if(isEmpty(list[i])) {
                                continue;
                            }
                            var _param = list[i];
                            if(_param.search(DEFAULT_VALUES.D_SEARCH_KEYWORD_IDENTIFIER) != -1) {
                                _param = _param.replace(DEFAULT_VALUES.D_SEARCH_KEYWORD_IDENTIFIER, '');
                                _param = _param.trim();
                                if(_param.length > 0) {
                                    _param = _param.split('-');
                                    app.currentSearchParams["keywordList"] = _param;
                                }
                            } else if (_param.search(DEFAULT_VALUES.D_LOCALITY_IDENTIFIER) != -1) {
                                _param = _param.replace(DEFAULT_VALUES.D_LOCALITY_IDENTIFIER, '');
                                if(!isEmpty(_param)){
                                    app.currentSearchParams["locationName"] = _param.replace(/[^a-z0-9]+/gi, ' ').trim();
                                }
                            } else if (_param.search(DEFAULT_VALUES.D_EDU_IDENTIFIER) != -1) {
                                _param = _param.replace(DEFAULT_VALUES.D_EDU_IDENTIFIER, '');
                                _param = _param.replace("for", '');
                                _param = _param.trim();
                                if(!isEmpty(_param)){
                                    app.currentSearchParams["educationText"] = _param.replace(/[^a-z0-9]+/gi, ' ').trim();
                                }
                            } else if (_param.search(DEFAULT_VALUES.D_EXP_IDENTIFIER) != -1) {
                                _param = _param.replace(DEFAULT_VALUES.D_EXP_IDENTIFIER, '');
                                _param = _param.replace("for", '');
                                if(!isEmpty(_param)){
                                    app.currentSearchParams["experienceText"] = _param.replace(/[^a-z0-9]+/gi, ' ').trim();
                                }
                            }
                        }
                    }
                } catch (exception){
                    console.log("exception in interpreting url: "+exception.stack);
                }
            },
            parseSearchResponse : function (data) {
                console.log(data);
            }
        },
        // action validator methods
        run: {
            searchValidation: function () {

            },
            urlValidation: function () {

            },
            urlChangeDetector: function () {
                // TODO detect url change and re-trigger search
            }
        }
    };

    // control flow
    app.bMethods.init();

    // resetFilters even listeners
    document.getElementById("resetFilters").addEventListener("click", function(){
        app.do.resetFilters();
    });
    document.getElementById("resetFilters_").addEventListener("click", function(){
        app.do.resetFilters();
    });

    // search click listener
    document.getElementById("searchBtn").addEventListener("click", function(){
        app.currentURL = app.do.modifyURL(app.do.prepareURL());
        app.do.prepareSearchParamFromURL();
        app.do.search();
    });

    // public methods
    function isEmpty(str) {
        return (!str || 0 === str.length);
    }

}(jQuery));
