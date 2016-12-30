/*
 * truSearch: TruJobs Search plugin
 * Version 1.0.0
 *
 * Copyright (c) 2016 TruJobs.in (http://trujobs.in)
 *
 * Created by zero on 23/12/16.
 *
 */

var app = (function ($) {
    'use strict';

    String.prototype.capitalizeFirstLetter = function() {
        return this.charAt(0).toUpperCase() + this.slice(1);
    };

    String.prototype.toTitleCase = function () {
        return this.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
    };
    var DEFAULT_VALUES = {
        D_SEARCH_URL: "jobs-in-bangalore",
        D_SEARCH_KEYWORD_IDENTIFIER: "jobs-in-bangalore",
        D_LOCALITY_IDENTIFIER: "near-",
        D_EDU_IDENTIFIER: "-pass",
        D_EXP_IDENTIFIER: "-experience",
        D_JOBS_PER_PAGE: 0,
        D_EDU_ANY: {},
        D_EXP_ANY: {},
        D_LOCATION_ALL_BANGALORE: {}
    };
    var app = {
        isUserLoggedIn: false,
        shouldDoSearch: true,
        allJobRole: [],
        allLocation: [],
        allLocationArray: [],
        allEducation: [],
        allExperience: [],
        allLanguage: [],
        allSalaryOptions: [
            {id: 0, name: " Any"},
            {id: 8, name: " 8000 & More"}, // value = val * 1000
            {id: 10, name: " 10000 & More"},
            {id: 12, name: " 12000 & More"},
            {id: 15, name: " 15000 & More"},
            {id: 20, name: " 20000 & More"}
        ],
        suggestion: null,
        currentURL: window.location.pathname,
        currentSearchURL: window.location.pathname.split('/')[window.location.pathname.split('/').length - 2],
        currentSearchParams: {},
        currentFilterParams: {
            selectedGender: null,
            selectedSalary: 0, // any
            selectedLanguageIdList: []
        },
        currentSortParams: {
            sortBy: 5 // default set to sort by relevance
        },
        page: 1,
        isPaginationEnabled: false,

        // basic getter/setter types method
        bMethods: {
            init: function () {
                console.log("init");
                app.page = 1;
                if (!(app.currentSearchURL == DEFAULT_VALUES.D_SEARCH_URL)) {
                    app.do.prepareSearchParamFromURL();
                }
                app.render.renderTextSearch();
                app.render.renderLocation();
                app.render.renderEducation();
                app.render.renderExperience();

                // render filter paramas
                app.render.renderLanguage();
                app.render.renderSalaryFilter();
                app.run.urlChangeDetector();

                app.do.search(true);

                document.getElementById('sortByRelevance').checked = true;
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
                    console.log('re render with localityname; ' + locality.localityName);
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
                        console.log("render location");
                        DEFAULT_VALUES.D_LOCATION_ALL_BANGALORE = {id: "0", name: "All Bangalore"};
                        var option = $('<option value="0"></option>').text("All Bangalore");
                        $('#searchLocation').append(option);

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
            renderEducation: function (education) {

                if (education != null) {
                    console.log('re render with education; ' + education.educationName);
                    $('#searchEducation').tokenize({
                        displayDropdownOnFocus: true,
                        placeholder: "Education",
                        nbDropdownElements: 1000,
                        maxElements: 1
                    }).tokenRemove($('#searchEducation').val()[0]).tokenAdd(education.educationId, education.educationName);
                }
                // this if check prevents static token suggestion from being duplicated
                else if ($("#searchEducation option[value='1']").length == 0) {

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
                        var option;
                        var first = {};
                        var initId = null;
                        app.allEducation.forEach(function (education) {
                            var id = education.educationId;
                            var name = education.educationName;
                            option = $('<option value=' + id + '></option>').text(name);
                            $('#searchEducation').append(option);

                            if (initId == null) {
                                initId = id;
                            }

                            if (name == "Any") {
                                console.log("found any");
                                first = {'id': id, 'name': name};
                                DEFAULT_VALUES.D_EDU_ANY = first;
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
                        $('#searchEducation').tokenize().tokenAdd(DEFAULT_VALUES.D_EDU_ANY.id, DEFAULT_VALUES.D_EDU_ANY.name);
                    }).catch(function (fromReject) {
                        console.log(fromReject);
                    });
                }

            },
            renderExperience: function (experience) {

                if (experience != null) {

                    $('#searchExperience').tokenize({
                        displayDropdownOnFocus: true,
                        placeholder: "Experience",
                        nbDropdownElements: 1000,
                        maxElements: 1
                    }).tokenRemove($('#searchExperience').val()[0]).tokenAdd(experience.experienceId, experience.experienceType);
                }
                // this if check prevents static token suggestion from being duplicated
                else if ($("#searchExperience option[value='1']").length == 0) {

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

                            if (initId == null) {
                                initId = id;
                            }

                            if (experience.experienceType == "Any") {
                                console.log("found any");
                                first = {'id': id, 'name': name};
                                DEFAULT_VALUES.D_EXP_ANY = first;
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
                        $('#searchExperience').tokenize().tokenAdd(DEFAULT_VALUES.D_EXP_ANY.id, DEFAULT_VALUES.D_EXP_ANY.name);

                    }).catch(function (fromReject) {
                        console.log(fromReject);
                    });
                }

            },
            // render filter
            renderLanguage: function () {
                var promise = new Promise(function (resolve, reject) {
                        app.bMethods.getAllLanguage().then(
                            function (returnedData) {
                                if (returnedData != null) {
                                    returnedData.forEach(function (language) {
                                        app.allLanguage.push(language);
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
                    console.log("render language filter");

                    var parent = $("#languageFilterDiv");

                    app.allLanguage.forEach(function (language) {

                        var mainDiv = document.createElement("div");
                        parent.append(mainDiv);

                        var languageInput = document.createElement("input");
                        languageInput.type = "checkbox";
                        languageInput.onclick = function () {
                            checkOnFilterChange();
                        };
                        languageInput.id = "lang_" + language.languageId;
                        languageInput.setAttribute("value", language.languageId);
                        mainDiv.appendChild(languageInput);

                        var languageLabel = document.createElement("label");
                        languageLabel.style = "font-size: 14px";
                        languageLabel.setAttribute("for", "lang_" + language.languageId);
                        languageLabel.textContent = language.languageName;
                        mainDiv.appendChild(languageLabel);

                    });

                }).catch(function (fromReject) {
                    console.log(fromReject);
                });

            },
            renderSalaryFilter: function () {

                console.log("render salary filter");

                var parent = $("#salaryFilterDiv");
                app.allSalaryOptions.forEach(function (salary) {

                    var mainDiv = document.createElement("div");
                    parent.append(mainDiv);

                    var salaryInput = document.createElement("input");
                    salaryInput.type = "radio";
                    salaryInput.name = "salaryFilter";


                    salaryInput.id = "sal_" + salary.id;
                    salaryInput.setAttribute("value", salary.name);
                    mainDiv.appendChild(salaryInput);

                    var salaryLabel = document.createElement("label");
                    salaryLabel.style = "font-size: 14px";
                    salaryLabel.setAttribute("for", "sal_" + salary.id);
                    salaryLabel.textContent = salary.name;
                    mainDiv.appendChild(salaryLabel);

                });

                document.getElementById('sal_0').checked = true;
            },
            renderTextSearch: function () {
                var input = $("#searchText");
                var numberOfKeywords = input.val().split(",");
                if((input.val() == null || input.val().length < 3) ){
                    console.log("no need to get suggestion");
                    return;
                }
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
                $('.awesomplete').css('width', '100%')
            }
        },
        // action perform methods
        do: {
            fillSearchTextBoxWithKeywords: function (keywordList) {
                var searchBoxText = keywordList.join(" ") + ", ";
                document.getElementById("searchText").value = searchBoxText.toTitleCase();
            },
            search: function (isBasicResetRequired) {
                if(!app.shouldDoSearch){
                    console.log("no search!");
                    return;
                }
                console.log("do search ");
                if (isBasicResetRequired) {
                    app.run.basicReset();
                }


                // ajax call
                var d = {
                    searchParamRequest: app.currentSearchParams,
                    filterParamRequest: app.currentFilterParams,
                    sortParamRequest: app.currentSortParams
                };

                console.log("/api/search/?page=" + app.page);
                $.ajax({
                    type: "POST",
                    url: "/api/search/?page=" + app.page,
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

                var _education = document.getElementById("searchEducation");
                _text = _education.options[_education.selectedIndex];

                if (_education.value != 0 && _text != null && _text.innerHTML.toLowerCase() != "any") {
                    _searchStr += "_for-" + _text.innerHTML.replace(/[^a-z0-9]+/gi, '-') + DEFAULT_VALUES.D_EDU_IDENTIFIER;
                }

                var _experience = document.getElementById("searchExperience");
                _text = _experience.options[_experience.selectedIndex];
                if (_experience.value != 0 && _text != null && _text.innerHTML.toLowerCase() != "any") {
                    _searchStr += "_for-" + _text.innerHTML.replace(/[^a-z0-9]+/gi, '-') + DEFAULT_VALUES.D_EXP_IDENTIFIER;
                }

                return (_searchStr + "/").toLowerCase();
            },
            prepareSearchParamFromURL: function () {

                // interprets url and create search params
                try {
                    var url = window.location.pathname.split('/');
                    var _searchUrl = url[url.length - 2];

                    app.currentSearchParams = {};
                    if (_searchUrl != null) {
                        app.currentSearchURL = _searchUrl;
                        if(_searchUrl.indexOf(DEFAULT_VALUES.D_SEARCH_KEYWORD_IDENTIFIER)  < 0){
                            // redirect to 404
                            // window.location = "/pageNotFound" ;
                            app.do.noJobsFound(true);
                            return;
                        } else {
                            app.do.noJobsFound(false);
                        }

                        var list = _searchUrl.split('_');
                        // run identifier on this array;
                        var i;
                        for (i = 0; i < list.length; i++) {
                            if (isEmpty(list[i])) {
                                continue;
                            }
                            var _param = list[i];
                            if (_param.search(DEFAULT_VALUES.D_SEARCH_KEYWORD_IDENTIFIER) != -1) {
                                _param = _param.replace(DEFAULT_VALUES.D_SEARCH_KEYWORD_IDENTIFIER, '');
                                _param = _param.trim();
                                if (_param.length > 0) {
                                    _param = _param.split('-');
                                    app.currentSearchParams["keywordList"] = _param;
                                }
                            } else if (_param.search(DEFAULT_VALUES.D_LOCALITY_IDENTIFIER) != -1) {
                                _param = _param.replace(DEFAULT_VALUES.D_LOCALITY_IDENTIFIER, '');
                                if (!isEmpty(_param)) {
                                    app.currentSearchParams["locationName"] = _param.replace(/[^a-z0-9]+/gi, ' ').trim();
                                }
                            } else if (_param.search(DEFAULT_VALUES.D_EDU_IDENTIFIER) != -1) {
                                _param = _param.replace(DEFAULT_VALUES.D_EDU_IDENTIFIER, '');
                                _param = _param.replace("for", '');
                                _param = _param.trim();
                                if (!isEmpty(_param)) {
                                    app.currentSearchParams["educationText"] = _param.replace(/[^a-z0-9]+/gi, ' ').trim();
                                }
                            } else if (_param.search(DEFAULT_VALUES.D_EXP_IDENTIFIER) != -1) {
                                _param = _param.replace(DEFAULT_VALUES.D_EXP_IDENTIFIER, '');
                                _param = _param.replace("for", '');
                                if (!isEmpty(_param)) {
                                    app.currentSearchParams["experienceText"] = _param.replace(/[^a-z0-9]+/gi, ' ').trim();
                                }
                            }
                        }
                    }
                } catch (exception) {
                    console.log("exception in interpreting url: " + exception.stack);
                }
            },
            noJobsFound: function (data) {
                if(data){
                    $('#noJobsDiv').show();

                    $('#job_cards_inc').hide();
                    $('#jobCardControl').hide();
                    app.shouldDoSearch = false;
                } else {
                    $('#noJobsDiv').hide();

                    $('#job_cards_inc').show();
                    $('#jobCardControl').show();
                    app.shouldDoSearch = true;
                }
                $("#hotJobs").html("");

                $("#jobLoaderDiv").hide();

                return;
            },
            parseSearchResponse: function (data) {
                console.log(data);
                // form result, and render it in card
                // pagination is required

                if (data != null) {

                    app.isUserLoggedIn = data.isUserLoggedIn;
                    if(app.isUserLoggedIn){
                        $('#nav_bar_inc').load('/navBarLoggedIn');
                    } else {
                        $('#nav_bar_inc').load('/navBar');
                    }

                    // append search params to the UI
                    app.render.renderLocation(data.searchParams.locality);
                    app.render.renderEducation(data.searchParams.education);
                    app.render.renderExperience(data.searchParams.experience);

                    app.mark.selectedLanguageFilter(data.filterParams.languageList);
                    app.mark.selectedGenderFilter(data.filterParams.gender);
                    app.do.fillSearchTextBoxWithKeywords(data.searchParams.searchKeywords);

                    console.log("data.isURLInvalid: " + data.isURLInvalid);
                    if(data.isURLInvalid){
                        app.do.noJobsFound(true);
                        return;
                    } else {
                        app.do.noJobsFound(false);
                    }

                    var _jobPostList = data.results.allJobPost;
                    var _jobPostCount = Object.keys(_jobPostList).length;
                    if (_jobPostCount > 0) {
                        if (DEFAULT_VALUES.D_JOBS_PER_PAGE == 0) {
                            DEFAULT_VALUES.D_JOBS_PER_PAGE = parseInt(data.results.jobsPerPage);
                            console.log("max row" + DEFAULT_VALUES.D_JOBS_PER_PAGE);
                        }
                        var _numberOfPages = Math.floor(parseInt(data.results.totalJobs) / DEFAULT_VALUES.D_JOBS_PER_PAGE);
                        var _rem = parseInt(data.results.totalJobs) % DEFAULT_VALUES.D_JOBS_PER_PAGE;
                        if (_rem > 0) {
                            _numberOfPages++;
                        }
                        console.log("no of pages : " + _numberOfPages);
                        if (!app.isPaginationEnabled) {
                            app.do.pagination(_numberOfPages);
                        }

                        $('#job_cards_inc').show();
                        $('#jobCardControl').show();

                        $("#hotJobs").html("");
                        var _count = 0;
                        var _parent = $("#hotJobs");
                        //returnedData.reverse();

                        $("#jobLoaderDiv").hide();
                        $('#noJobsDiv').hide();

                        var jobSearchTitle = app.currentSearchURL.capitalizeFirstLetter().replace(/[^a-z0-9]+/gi, ' ');
                        console.log("jobSearchTitle: "+ jobSearchTitle);

                        app.do.createAndAppendDivider(" Showing 1-5 of "+data.results.totalJobs+" jobs matching your search");
                        // var _isDividerPresent = false;
                        _jobPostList.forEach(function (jobPost) {
                            _count++;
                            if (_count) {
                                //!* get all localities of the jobPost *!/
                                var _jobLocality = jobPost.jobPostToLocalityList;
                                var _localities = "";
                                var _allLocalities = "";
                                var _loopCount = 0;
                                //
                                // if (jobPost.source != null && jobPost.source > 0 && !_isDividerPresent) {
                                //     app.do.createAndAppendDivider("Other Jobs");
                                //     _isDividerPresent = true;
                                // }

                                _jobLocality.forEach(function (locality) {
                                    _loopCount++;
                                    if (_loopCount > 2) {
                                        return false;
                                    } else {
                                        var name = locality.locality.localityName;
                                        _localities += name;
                                        if (_loopCount < Object.keys(_jobLocality).length) {
                                            _localities += ", ";
                                        }
                                    }
                                });
                                _loopCount = 0;
                                _jobLocality.forEach(function (locality) {
                                    _loopCount++;
                                    var name = locality.locality.localityName;
                                    _allLocalities += name;
                                    if (_loopCount < Object.keys(_jobLocality).length) {
                                        _allLocalities += ", ";
                                    }
                                });

                                var hotJobItem = document.createElement("div");
                                hotJobItem.id = "hotJobItem";
                                _parent.append(hotJobItem);

                                var centreTag = document.createElement("center");
                                hotJobItem.appendChild(centreTag);

                                var rowDiv = document.createElement("div");
                                rowDiv.className = "row";
                                rowDiv.style = "margin: 0; padding: 0";
                                centreTag.appendChild(rowDiv);

                                var col = document.createElement("div");
                                col.className = "col-sm-2";
                                rowDiv.appendChild(col);

                                var jobLogo = document.createElement("img");
                                jobLogo.src = jobPost.company.companyLogo;
                                jobLogo.setAttribute('width', '80%');
                                jobLogo.id = "jobLogo";
                                col.appendChild(jobLogo);

                                var jobBodyCol = document.createElement("div");
                                jobBodyCol.className = "col-sm-8";
                                jobBodyCol.id = "jobBody";
                                rowDiv.appendChild(jobBodyCol);

                                var jobTitle = document.createElement("h4");
                                jobTitle.textContent = jobPost.jobPostTitle + " | " + jobPost.company.companyName;
                                jobBodyCol.appendChild(jobTitle);

                                var jobRoleName= document.createElement("p");
                                jobRoleName.textContent = jobPost.jobRole.jobName + " Job";
                                jobRoleName.style = "color:#5d5";
                                jobBodyCol.appendChild(jobRoleName);

                                var hr = document.createElement("hr");
                                centreTag.appendChild(hr);

                                var rowDivDetails = document.createElement("div");
                                rowDivDetails.className = "row";
                                rowDivDetails.style = "margin: 0; padding: 0";
                                centreTag.appendChild(rowDivDetails);

                                var jobBodyDetails = document.createElement("div");
                                jobBodyDetails.className = "row";
                                jobBodyDetails.id = "jobBodyDetails";
                                jobBodyDetails.style = "margin:0";
                                rowDivDetails.appendChild(jobBodyDetails);


                                var jobBodyDetailsFirst = document.createElement("div");
                                jobBodyDetailsFirst.className = "row";
                                jobBodyDetailsFirst.id = "jobBodyDetailsFir";
                                jobBodyDetailsFirst.style = "margin:0";
                                jobBodyDetails.appendChild(jobBodyDetailsFirst);


                                var jobBodyDetailsSecond = document.createElement("div");
                                jobBodyDetailsSecond.className = "row";
                                jobBodyDetailsSecond.id = "jobBodyDetailsSec";
                                jobBodyDetailsSecond.style = "margin:0";
                                jobBodyDetails.appendChild(jobBodyDetailsSecond);


                                //!*  salary  *!/

                                var bodyCol = document.createElement("div");
                                bodyCol.className = "col-sm-6 col-md-4";
                                bodyCol.id = "jobSalary";
                                jobBodyDetailsFirst.appendChild(bodyCol);

                                var subDivHint = document.createElement("div");
                                subDivHint.className = "row";
                                subDivHint.style= "display: inline-block;margin:0 0 0 30px;color: #9f9f9f;font-size: 12px;padding:4px";
                                subDivHint.textContent = "Salary";
                                bodyCol.appendChild(subDivHint);

                                var subRowForData = document.createElement("div");
                                subRowForData.className = "row";
                                bodyCol.appendChild(subRowForData);

                                var salaryIconDiv = document.createElement("div");
                                salaryIconDiv.className="col-xs-2";
                                salaryIconDiv.style = "padding-right:0;margin-top:-2px";
                                subRowForData.appendChild(salaryIconDiv);

                                var salaryIcon = document.createElement("img");
                                salaryIcon.src = "/assets/common/img/salary.svg";
                                salaryIcon.setAttribute('height', '20px');
                                salaryIconDiv.appendChild(salaryIcon);

                                var salaryDataDiv = document.createElement("div");
                                salaryDataDiv.className="col-xs-10";
                                salaryDataDiv.style="padding:0;margin-left:-2px";
                                subRowForData.appendChild(salaryDataDiv);

                                var salaryDiv = document.createElement("div");
                                salaryDiv.style = "display: inline-block";
                                if (jobPost.jobPostMaxSalary == "0" || jobPost.jobPostMaxSalary == null) {
                                    salaryDiv.textContent = rupeeFormatSalary(jobPost.jobPostMinSalary) + " monthly";
                                } else {
                                    salaryDiv.textContent = rupeeFormatSalary(jobPost.jobPostMinSalary) + " - " + rupeeFormatSalary(jobPost.jobPostMaxSalary) + " monthly";
                                }
                                salaryDataDiv.appendChild(salaryDiv);

                                //!*  experience  *!/

                                var bodyCol = document.createElement("div");
                                bodyCol.className = "col-sm-6 col-md-4";
                                bodyCol.id = "jobSalary";
                                jobBodyDetailsSecond.appendChild(bodyCol);

                                var subDivHint = document.createElement("div");
                                subDivHint.className = "row";
                                subDivHint.style= "display: inline-block;margin:0 0 0 30px;color: #9f9f9f;font-size: 12px;padding:4px";
                                subDivHint.textContent = "Experience";
                                bodyCol.appendChild(subDivHint);

                                var subRowForData = document.createElement("div");
                                subRowForData.className = "row";
                                bodyCol.appendChild(subRowForData);

                                var expIconDiv = document.createElement("div");
                                expIconDiv.className="col-xs-2";
                                expIconDiv.style = "padding-right:0;margin-top:-2px";
                                subRowForData.appendChild(expIconDiv);

                                var expIcon = document.createElement("img");
                                expIcon.src = "/assets/common/img/salary.svg";
                                expIcon.setAttribute('height', '20px');
                                expIconDiv.appendChild(expIcon);

                                var expDataDiv = document.createElement("div");
                                expDataDiv.className="col-xs-10";
                                expDataDiv.style="padding:0;margin-left:-2px";
                                subRowForData.appendChild(expDataDiv);

                                var expDiv = document.createElement("div");
                                expDiv.style = "display: inline-block;";
                                expDiv.textContent = jobPost.jobPostExperience.experienceType;
                                expDataDiv.appendChild(expDiv);

                                //!*  Location  *!/

                                var bodyColLoc = document.createElement("div");
                                bodyColLoc.className = "col-sm-6 col-md-4";
                                bodyColLoc.id = "jobLocation";
                                jobBodyDetailsFirst.appendChild(bodyColLoc);

                                var subDivHint = document.createElement("div");
                                subDivHint.className = "row";
                                subDivHint.style= "display: inline-block;margin:0 0 0 30px;color: #9f9f9f;font-size: 12px;padding:4px";
                                subDivHint.textContent = "Location";
                                bodyColLoc.appendChild(subDivHint);

                                var jobBodySubRowLoc = document.createElement("div");
                                jobBodySubRowLoc.className = "row";
                                bodyColLoc.appendChild(jobBodySubRowLoc);

                                var locIconDiv = document.createElement("div");
                                locIconDiv.style = "padding-right:0;margin-top:-2px";
                                locIconDiv.className = "col-xs-2";
                                jobBodySubRowLoc.appendChild(locIconDiv);

                                var locIcon = document.createElement("img");
                                locIcon.src = "/assets/common/img/location.svg";
                                locIcon.setAttribute('height', '20px');
                                locIconDiv.appendChild(locIcon);

                                var locDataDiv = document.createElement("div");
                                locDataDiv.className="col-xs-10";
                                locDataDiv.style="padding:0;margin-left:-2px";
                                jobBodySubRowLoc.appendChild(locDataDiv);

                                var locDiv = document.createElement("div");
                                locDiv.style = "display: inline-block";
                                locDiv.textContent = _localities;
                                locDataDiv.appendChild(locDiv);

                                if (((_jobLocality.length) - 2) > 0) {
                                    var tooltip = document.createElement("a");
                                    tooltip.id = "locationMsg_" + jobPost.jobPostId;
                                    tooltip.title = _allLocalities;
                                    tooltip.style = "color: #2980b9";
                                    tooltip.textContent = " more";
                                    locDataDiv.appendChild(tooltip);
                                }

                                $("#locationMsg_" + jobPost.jobPostId).attr("data-toggle", "tooltip");
                                $(function () {
                                    $('[data-toggle="tooltip"]').tooltip()
                                });

                                // gender div

                                var genderCol = document.createElement("div");
                                genderCol.className = "col-sm-6 col-md-4";
                                genderCol.id = "jobGender";
                                jobBodyDetailsSecond.appendChild(genderCol);

                                var subDivHint = document.createElement("div");
                                subDivHint.className = "row";
                                subDivHint.style= "display: inline-block;margin:0 0 0 30px;color: #9f9f9f;font-size: 12px;padding:4px";
                                subDivHint.textContent = "Gender";
                                genderCol.appendChild(subDivHint);

                                var subRowForData = document.createElement("div");
                                subRowForData.className = "row";
                                genderCol.appendChild(subRowForData);

                                var genderIconDiv = document.createElement("div");
                                genderIconDiv.className="col-xs-2";
                                genderIconDiv.style = "padding-right:0;margin-top:-2px";
                                subRowForData.appendChild(genderIconDiv);

                                var genderIcon = document.createElement("img");
                                genderIcon.src = "/assets/common/img/salary.svg";
                                genderIcon.setAttribute('height', '20px');
                                genderIconDiv.appendChild(genderIcon);

                                var genderDataDiv = document.createElement("div");
                                genderDataDiv.className="col-xs-10";
                                genderDataDiv.style="padding:0;margin-left:-2px";
                                subRowForData.appendChild(genderDataDiv);

                                var genderDiv = document.createElement("div");
                                genderDiv.style = "display: inline-block";
                                genderDiv.textContent = app.run.validateGender(jobPost.gender);
                                genderDataDiv.appendChild(genderDiv);

                                // age div

                                var ageCol = document.createElement("div");
                                ageCol.className = "col-sm-6 col-md-4";
                                ageCol.id = "jobGender";
                                jobBodyDetailsSecond.appendChild(ageCol);

                                var subDivHint = document.createElement("div");
                                subDivHint.className = "row";
                                subDivHint.style= "display: inline-block;margin:0 0 0 30px;color: #9f9f9f;font-size: 12px;padding:4px";
                                subDivHint.textContent = "Max Age";
                                ageCol.appendChild(subDivHint);

                                var subRowForData = document.createElement("div");
                                subRowForData.className = "row";
                                ageCol.appendChild(subRowForData);

                                var ageIconDiv = document.createElement("div");
                                ageIconDiv.className="col-xs-2";
                                ageIconDiv.style = "padding-right:0;margin-top:-2px";
                                subRowForData.appendChild(ageIconDiv);

                                var ageIcon = document.createElement("img");
                                ageIcon.src = "/assets/common/img/salary.svg";
                                ageIcon.setAttribute('height', '20px');
                                ageIconDiv.appendChild(ageIcon);

                                var ageDataDiv = document.createElement("div");
                                ageDataDiv.className="col-xs-10";
                                ageDataDiv.style="padding:0;margin-left:-2px";
                                subRowForData.appendChild(ageDataDiv);

                                var ageDiv = document.createElement("div");
                                ageDiv.style = "display: inline-block";
                                ageDiv.textContent = app.run.validateMaxAge(jobPost.jobPostMaxAge) + " yrs" ;
                                ageDataDiv.appendChild(ageDiv);

                                // timeshift div

                                var bodyColTime = document.createElement("div");
                                bodyColTime.className = "col-sm-6 col-md-4";
                                bodyColTime.id = "jobLocation";
                                jobBodyDetailsFirst.appendChild(bodyColTime);

                                var subDivHint = document.createElement("div");
                                subDivHint.className = "row";
                                subDivHint.style= "display: inline-block;margin:0 0 0 30px;color: #9f9f9f;font-size: 12px;padding:4px";
                                subDivHint.textContent = "Time Shift";
                                bodyColTime.appendChild(subDivHint);

                                var subRowForData = document.createElement("div");
                                subRowForData.className = "row";
                                bodyColTime.appendChild(subRowForData);

                                var timeIconDiv = document.createElement("div");
                                timeIconDiv.style = "padding-right:0;margin-top:-2px";
                                timeIconDiv.className = "col-xs-2";
                                subRowForData.appendChild(timeIconDiv);

                                var timeIcon = document.createElement("img");
                                timeIcon.src = "/assets/common/img/location.svg";
                                timeIcon.setAttribute('height', '20px');
                                timeIconDiv.appendChild(timeIcon);

                                var timeDataDiv = document.createElement("div");
                                timeDataDiv.className="col-xs-10";
                                timeDataDiv.style="padding:0;margin-left:-2px";
                                subRowForData.appendChild(timeDataDiv);

                                var timeDiv = document.createElement("div");
                                timeDiv.style = "display: inline-block";
                                timeDiv.textContent = app.run.validateWorkShift(jobPost.jobPostShift);
                                timeDataDiv.appendChild(timeDiv);

                                var hr = document.createElement("hr");
                                centreTag.appendChild(hr);

                                //!*  apply div button *!/
                                var rowDivApplyButton = document.createElement("div");
                                rowDivApplyButton.className = "row";
                                rowDivApplyButton.style = "margin: 0; padding: 0";
                                centreTag.appendChild(rowDivApplyButton);


                                // posted on div
                                var postedOnDiv = document.createElement("div");
                                postedOnDiv.className = "col-sm-6";
                                postedOnDiv.style = "margin-top:12px;text-align:left";
                                postedOnDiv.textContent = "Posted: " + app.parse.createdOnDate(jobPost.jobPostCreateTimestamp);
                                rowDivApplyButton.appendChild(postedOnDiv);


                                var applyBtnDiv = document.createElement("div");
                                applyBtnDiv.className = "col-sm-6";
                                rowDivApplyButton.appendChild(applyBtnDiv);


                                //!*  more button *!/
                                var jobMoreCol = document.createElement("div");
                                jobMoreCol.className = "col-sm-2";
                                jobMoreCol.id = "jobMore";
                                rowDiv.appendChild(jobMoreCol);

                                // vacancies div
                                var vacanciesDiv = document.createElement("div");
                                vacanciesDiv.textContent = "Vacancies: " + jobPost.jobPostVacancies;
                                jobMoreCol.appendChild(vacanciesDiv);


                                var moreBtn = document.createElement("div");
                                moreBtn.className = "jobMoreBtn";
                                moreBtn.textContent = "More Info";
                                jobMoreCol.appendChild(moreBtn);
                                moreBtn.onclick = function () {
                                    var jobPostBreak = jobPost.jobPostTitle.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'-');
                                    jobPostBreak = jobPostBreak.toLowerCase();
                                    var jobCompany = jobPost.company.companyName.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'-');
                                    jobCompany = jobCompany.toLowerCase();
                                    try {
                                        window.location.href = "/jobs/" + jobPostBreak + "-jobs-in-bengaluru-at-" + jobCompany + "-" + jobPost.jobPostId;
                                    } catch (exception) {
                                        console.log("exception occured!!" + exception);
                                    }
                                };


                                //!*  apply button *!/
                                var applyBtn = document.createElement("div");
                                applyBtn.className = "jobApplyBtn";
                                var applyJobText ;
                                if(jobPost.applyBtnStatus != null && jobPost.applyBtnStatus != 4){
                                    if(jobPost.applyBtnStatus == 2) {
                                        applyJobText = "Book Interview";
                                    } else if(jobPost.applyBtnStatus == 3) {
                                        applyJobText = "Already Applied";
                                        applyBtn.disabled =  true;
                                    }
                                } else {
                                    applyJobText = "Apply";
                                }
                                applyBtn.textContent = applyJobText;

                                applyBtnDiv.appendChild(applyBtn);

                                applyBtn.onclick = function () {
                                    var jobPostBreak = jobPost.jobPostTitle.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'-');
                                    jobPostBreak = jobPostBreak.toLowerCase();
                                    var jobCompany = jobPost.company.companyName.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'-');
                                    jobCompany = jobCompany.toLowerCase();
                                    try {
                                        window.location.href = "/jobs/" + jobPostBreak + "-jobs-in-bengaluru-at-" + jobCompany + "-" + jobPost.jobPostId;
                                    } catch (exception) {
                                        console.log("exception occured!!" + exception);
                                    }
                                };
                            }
                        });

                    } else {
                        // no jobs found
                        // reset current job card and navigator

                        $('#job_cards_inc').hide();
                        $('#jobCardControl').hide();

                        $("#jobLoaderDiv").hide();
                        $('#noJobsDiv').show();

                    }

                    return app.do.modifyURL(app.do.prepareURL());

                }

                $(".first").hide();
                $(".last").hide();
                $(".prev a").html("<<");
                $(".next a").html(">>");
            },
            pagination: function (noOfPages) {
                // this boolean prevents from looping into pagination when search is triggered
                console.log("render page navigator | noOfPages: " + noOfPages);
                // c.f http://esimakin.github.io/twbs-pagination/
                // ' Call destroy method and then initialize it with new options.'
                $('#jobCardControl').twbsPagination('destroy');
                $('#jobCardControl').twbsPagination({
                    totalPages: noOfPages,
                    visiblePages: 5,
                    onPageClick: function (event, page) {
                        if (page > 0) {
                            console.log("page: " + page);
                            app.page = page;

                        }
                        if (app.isPaginationEnabled) {
                            app.do.search(false);
                        }
                        $(".page-link").click(function () {
                            $('html, body').animate({scrollTop: $("#job_cards_inc").offset().top - 100}, 800);
                        });
                    }
                });
                app.isPaginationEnabled = true;
            },
            createAndAppendDivider: function (title) {
                var parent = $("#hotJobs");

                var mainDiv = document.createElement("div");
                mainDiv.id = "hotJobItemDivider";
                parent.append(mainDiv);

                // var otherJobIcon = document.createElement("img");
                // otherJobIcon.src = "/assets/common/img/suitcase.png";
                // otherJobIcon.style = "width: 42px; margin: 8px";
                // otherJobIcon.setAttribute("display", "inline-block");
                // mainDiv.appendChild(otherJobIcon);

                var hotJobItem = document.createElement("span");
                hotJobItem.setAttribute("display", "inline-block");
                hotJobItem.textContent = title;
                mainDiv.appendChild(hotJobItem);

            },
            updateOnFilterChange: function () {
                console.log("update language filter");
                //language filter
                app.currentFilterParams.selectedLanguageIdList = [];
                $('#languageFilterDiv input:checked').each(function () {
                    console.log("added id: " + parseInt($(this).attr('value')));
                    app.currentFilterParams.selectedLanguageIdList.push(parseInt($(this).attr('value')));
                });
                if (app.currentFilterParams.selectedLanguageIdList.length > 0) {
                    $("#language_filter").show();
                } else {
                    $("#language_filter").hide();
                }

                app.do.search(true);
            },
            updateGenderFilter: function (genderId) {
                $("#gender_filter").show();
                app.currentFilterParams.selectedGender = genderId;

                console.log(genderId);

                app.do.search(true);
            },
            updateSalaryFilter: function (salaryId) {
                $("#salary_filter").show();
                app.currentFilterParams.selectedSalary = parseInt(salaryId) * 1000;

                console.log("salary gt : " + salaryId);

                app.do.search(true);
            },
            updateSortBy: function (value) {
                /*
                 5 sort by salary relevance
                 2 sort by datePosted : newest on top
                 3 sort by salary high to low
                 4 sort by salary low to high
                 */
                // current its assumed that on server the value  2, 3, 4, 5 are defined in same way

                app.currentSortParams.sortBy = parseInt(value);

                console.log("sort by : " + value);

                app.do.search(true);
            },
            resetFilters: function () {
                console.log("reset filter");
                $("#gender_filter").hide();
                $("#salary_filter").hide();
                $("#language_filter").hide();


                $('input:checkbox').removeAttr('checked');
                $('input:radio').removeAttr('checked');

                document.getElementById('sortByRelevance').checked = true;
                document.getElementById('sal_0').checked = true;

                app.currentFilterParams.selectedGender = null;
                app.currentFilterParams.selectedLanguageIdList = [];
                app.currentSortParams.sortBy = 1; // default set to sort by relevance

                app.do.search(true);
            }
        },
        // ui filter marking
        mark: {
            selectedLanguageFilter: function (languageList) {
                if(languageList == null || languageList.length == 0) {
                    return;
                }

                languageList.forEach(function (language) {
                    document.getElementById('lang_'+language.languageId).checked = true;
                });

            },
            selectedGenderFilter: function (id) {
                if(id == null || id == 2) {
                    return;
                }
                document.getElementById('gender_'+id).checked = true;

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
            searchValidation: function () {

            },
            urlValidation: function () {

            },
            urlChangeDetector: function () {
                if (window.history && window.history.pushState) {

                    $(window).on('popstate', function () {
                        location.reload();
                    });

                }
            },
            validateGender : function (gender) {
                if(gender == null || gender == 2 ){
                    return "Any";
                } else if (gender == 0){
                    return "Female";
                } else if (gender == 1){
                    return "Male";
                }
                return "";
            },
            validateMaxAge: function (age) {
              if(age == null) {
                  return "";
              }  else {
                  return age;
              }
            },
            basicReset: function () {
                console.log("basic reset");
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
                if ($('#searchEducation').val() == null) {
                    $('#searchEducation').tokenize({
                        displayDropdownOnFocus: true,
                        placeholder: "Education",
                        nbDropdownElements: 1000,
                        maxElements: 1
                    }).tokenAdd(DEFAULT_VALUES.D_EDU_ANY.id,
                        DEFAULT_VALUES.D_EDU_ANY.name);
                }

                if ($('#searchExperience').val() == null) {
                    $('#searchExperience').tokenize({
                        displayDropdownOnFocus: true,
                        placeholder: "Experience",
                        nbDropdownElements: 1000,
                        maxElements: 1
                    }).tokenAdd(DEFAULT_VALUES.D_EXP_ANY.id,
                        DEFAULT_VALUES.D_EXP_ANY.name);
                }

            }
        },
        parse: {
            createdOnDate: function (timestamp) {
                var jobDate = new Date(timestamp);
                var currentDate = new Date();

                var daysDiff= app.parse.daysDiff(jobDate, currentDate);

                if(daysDiff > 90 ) {
                    return " 3 months ago";
                } else if(jobDate.getDate() == currentDate.getUTCDate()) {
                    return " Today";
                } else {
                    return daysDiff + " days ago";
                }

            },
            daysDiff: function(firstDate, secondDate){

                return Math.round((secondDate-firstDate)/(1000*60*60*24));
            }
        }
    };

    // loading card view elements here
    $('#job_cards_inc').load('/jobPostCardView');

    // control flow
    app.bMethods.init();

    // resetFilters even listeners
    document.getElementById("resetFilters").addEventListener("click", function () {
        app.do.resetFilters();
    });
    document.getElementById("resetFilters_").addEventListener("click", function () {
        app.do.resetFilters();
    });

    // search click listener
    document.getElementById("searchBtn").addEventListener("click", function () {
        app.page = 1; // reset page to 1 for new search
        app.currentURL = app.do.prepNmodifyURL();
        app.do.prepareSearchParamFromURL();
        app.do.search(true);
    });

    // scroll to top listener
    document.getElementById("scrollToTop").addEventListener("click", function () {
        $('body').scrollTop(0);
    });

    // this detect the typing even on the search bar
    $('#searchText').on('keyup',function (event) {
        if(((event.keyCode >= 48 && event.keyCode <= 57)
            || (event.keyCode >= 65 && event.keyCode <= 90) )){
            // trigger suggestion only when typing alpha numeric is happening
            app.render.renderTextSearch();
        }
    });

    // gender filter listner
    $("input[name=filterGender]:radio").change(function () {
        app.do.updateGenderFilter(this.value);
        console.log("gender filter ");
    });

    // salary filter listner
    $("input[name=salaryFilter]:radio").change(function () {
        app.do.updateSalaryFilter(this.id.split("_")[1]);
        console.log("salary  filter id: " + this.id.split("_")[1]);
    });

    // sort listener
    $("input[name=sortBy]:radio").change(function () {
        app.do.updateSortBy(this.value);
        console.log("sort by");
    });

    // public methods
    function isEmpty(str) {
        return (!str || 0 === str.length);
    }

    return app;
}(jQuery));


// exposed methods

function checkOnFilterChange() {
    app.do.updateOnFilterChange();
}


function getJob(){
    app.render.renderJobRole();
    return app.allJobRole;
};
function getLocality(){
    console.log("all locality size: "+ app.allLocationArray.length);
    return app.allLocationArray;
};

/* register box */
$(window).scroll(function(){
    console.log(app.isUserLoggedIn);
    if(!app.isUserLoggedIn){
        var w = window.innerWidth;
        if ($(this).scrollTop() > 350) {
            $('.registerBox').fadeIn();
            if(w > 400){
                $('.registerBox').css("width","120px");
            }
            else{
                $('.registerBox').css("width","100%");
            }
        } else {
            $('.registerBox').fadeOut();
            $('.registerBox').css("width","50px");
        }
    }
});


// for nav bar imports
function openLogin() {
    $("#signInPopup").html("Sign In");
    document.getElementById("resetCheckUserBtn").disabled = false;
    document.getElementById("resetNewPasswordBtn").disabled = false;
    $('#form_login_candidate').show();
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_forgot_password').hide();
    $('#errorMsgReset').hide();
    $('#form_password_reset_otp').hide();
    $('#form_password_reset_new').hide();
    $('#noPasswordLogin').hide();
}

function openSignUp() {
    $("#myLoginModal").modal("hide");
}
