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
        index: 0,
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
                    url: "/api/search/?p="+app.index,
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
                // form result, and render it in card
                // pagination is required

                if(data != null) {
                    var jobPostList = data.results.allJobPost;
                    var jobPostCount = Object.keys(jobPostList).length;
                    if(jobPostCount > 0){
                        var numberOfPages = parseInt(data.results.totalJobs)/5;
                        var rem = parseInt(data.results.totalJobs) % 5;
                        if(rem > 0){
                            numberOfPages ++;
                        }
                        if(app.index == 0){
                            app.do.pagination(numberOfPages);
                        }

                        $("#hotJobs").html("");
                        var count = 0;
                        var parent = $("#hotJobs");
                        //returnedData.reverse();
                        $("#jobLoaderDiv").hide();

                        app.do.createAndAppendDivider("Popular Jobs");
                        var isDividerPresent = false;
                        jobPostList.forEach(function (jobPost){
                            count++;
                            if(count){
                                //!* get all localities of the jobPost *!/
                                var jobLocality = jobPost.jobPostToLocalityList;
                                var localities = "";
                                var allLocalities = "";
                                var loopCount = 0;

                                if(jobPost.source != null && jobPost.source > 0 && !isDividerPresent){
                                    app.do.createAndAppendDivider("Other Jobs");
                                    isDividerPresent = true;
                                }

                                app.allLocation.forEach(function (locality) {
                                    loopCount ++;
                                    if(loopCount > 2){
                                        return false;
                                    } else{
                                        var name = locality.name;
                                        localities += name;
                                        if(loopCount < Object.keys(jobLocality).length){
                                            localities += ", ";
                                        }
                                    }
                                });
                                loopCount = 0;
                                app.allLocation.forEach(function (locality) {
                                    loopCount++;
                                    var name = locality.name;
                                    allLocalities += name;
                                    if(loopCount < Object.keys(jobLocality).length){
                                        allLocalities += ", ";
                                    }
                                });

                                var hotJobItem = document.createElement("div");
                                hotJobItem.id = "hotJobItem";
                                parent.append(hotJobItem);

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

                                var hr = document.createElement("hr");
                                jobBodyCol.appendChild(hr);

                                var jobBodyDetails = document.createElement("div");
                                jobBodyDetails.className = "row";
                                jobBodyDetails.id = "jobBodyDetails";
                                jobBodyCol.appendChild(jobBodyDetails);

                                //!*  salary  *!/

                                var bodyCol = document.createElement("div");
                                bodyCol.className = "col-sm-4";
                                bodyCol.id = "jobSalary";
                                jobBodyDetails.appendChild(bodyCol);

                                var jobBodySubRow = document.createElement("div");
                                jobBodySubRow.className = "row";
                                bodyCol.appendChild(jobBodySubRow);

                                var jobBodySubRowCol = document.createElement("div");
                                jobBodySubRowCol.className = "col-sm-12";
                                jobBodySubRow.appendChild(jobBodySubRowCol);

                                var salaryIconDiv = document.createElement("div");
                                salaryIconDiv.style = "display : inline-block; margin: 4px;top:0";
                                jobBodySubRowCol.appendChild(salaryIconDiv);

                                var salaryIcon = document.createElement("img");
                                salaryIcon.src = "/assets/common/img/salary.svg";
                                salaryIcon.setAttribute('height', '15px');
                                salaryIcon.style = "margin-top: -4px";
                                salaryIconDiv.appendChild(salaryIcon);


                                var salaryDiv = document.createElement("div");
                                salaryDiv.style = "display: inline-block; font-size: 14px";
                                if(jobPost.jobPostMaxSalary == "0" || jobPost.jobPostMaxSalary == null){
                                    salaryDiv.textContent = rupeeFormatSalary(jobPost.jobPostMinSalary) + " monthly";
                                } else {
                                    salaryDiv.textContent = rupeeFormatSalary(jobPost.jobPostMinSalary) + " - " + rupeeFormatSalary(jobPost.jobPostMaxSalary) + " monthly";
                                }
                                jobBodySubRowCol.appendChild(salaryDiv);

                                //!*  experience  *!/

                                var bodyColExp = document.createElement("div");
                                bodyColExp.className = "col-sm-3";
                                bodyColExp.id = "jobExp";
                                jobBodyDetails.appendChild(bodyColExp);

                                var jobBodySubRowExp = document.createElement("div");
                                jobBodySubRowExp.className = "row";
                                bodyColExp.appendChild(jobBodySubRowExp);

                                var jobBodySubRowColExp = document.createElement("div");
                                jobBodySubRowColExp.className = "col-sm-12";
                                jobBodySubRowExp.appendChild(jobBodySubRowColExp);

                                var expIconDiv = document.createElement("div");
                                expIconDiv.style = "display : inline-block; margin: 4px;top:0";
                                jobBodySubRowColExp.appendChild(expIconDiv);

                                var expIcon = document.createElement("img");
                                expIcon.src = "/assets/common/img/workExp.svg";
                                expIcon.setAttribute('height', '15px');
                                expIcon.style = "margin-top: -4px";
                                expIconDiv.appendChild(expIcon);

                                var expDiv = document.createElement("div");
                                expDiv.style = "display: inline-block; font-size: 14px";
                                expDiv.textContent = "Exp: " + jobPost.jobPostExperience.experienceType;
                                jobBodySubRowColExp.appendChild(expDiv);

                                //!*  Location  *!/

                                var bodyColLoc = document.createElement("div");
                                bodyColLoc.className = "col-sm-5";
                                bodyColLoc.id = "jobLocation";
                                jobBodyDetails.appendChild(bodyColLoc);

                                var jobBodySubRowLoc = document.createElement("div");
                                jobBodySubRowLoc.className = "row";
                                bodyColLoc.appendChild(jobBodySubRowLoc);

                                var jobBodySubRowColLoc = document.createElement("div");
                                jobBodySubRowColLoc.className = "col-sm-12";
                                jobBodySubRowLoc.appendChild(jobBodySubRowColLoc);

                                var locIconDiv = document.createElement("div");
                                locIconDiv.style = "display : inline-block; margin: 4px;top:0";
                                jobBodySubRowColLoc.appendChild(locIconDiv);

                                var locIcon = document.createElement("img");
                                locIcon.src = "/assets/common/img/location.svg";
                                locIcon.setAttribute('height', '15px');
                                locIcon.style = "margin-top: -4px";
                                locIconDiv.appendChild(locIcon);

                                var locDiv = document.createElement("div");
                                locDiv.style = "display: inline-block; font-size: 14px";
                                locDiv.textContent = localities;
                                jobBodySubRowColLoc.appendChild(locDiv);

                                if(((jobLocality.length) - 2) > 0 ){
                                    var tooltip = document.createElement("a");
                                    tooltip.id = "locationMsg_" + jobPost.jobPostId;
                                    tooltip.title = allLocalities;
                                    tooltip.style = "color: #2980b9";
                                    tooltip.textContent = " more";
                                    jobBodySubRowColLoc.appendChild(tooltip);
                                }

                                $("#locationMsg_" + jobPost.jobPostId).attr("data-toggle", "tooltip");
                                $(function () {
                                    $('[data-toggle="tooltip"]').tooltip()
                                });

                                if(localStorage.getItem("incentives") == "1"){
                                    var incentiveDetails = document.createElement("div");
                                    incentiveDetails.className = "row";
                                    incentiveDetails.id = "incentiveDetails";
                                    jobBodyCol.appendChild(incentiveDetails);

                                    //!*  interview incentive  *!/

                                    var interviewIncentiveCol = document.createElement("div");
                                    interviewIncentiveCol.className = "col-sm-4";
                                    incentiveDetails.appendChild(interviewIncentiveCol);

                                    var interviewIncentiveRow = document.createElement("div");
                                    interviewIncentiveRow.className = "row";
                                    interviewIncentiveCol.appendChild(interviewIncentiveRow);

                                    var interviewIncentiveRowCol = document.createElement("div");
                                    interviewIncentiveRowCol.className = "col-sm-12";
                                    interviewIncentiveRow.appendChild(interviewIncentiveRowCol);

                                    var incentiveIconDiv = document.createElement("div");
                                    incentiveIconDiv.style = "display : inline-block;top:0";
                                    interviewIncentiveRowCol.appendChild(incentiveIconDiv);

                                    var incentiveIcon = document.createElement("img");
                                    incentiveIcon.src = "/assets/partner/img/coin.png";
                                    incentiveIcon.setAttribute('height', '20px');
                                    incentiveIcon.style = "margin: -4px 0 0 -5px";
                                    incentiveIconDiv.appendChild(incentiveIcon);

                                    var interviewIncentiveVal = document.createElement("div");
                                    interviewIncentiveVal.className = "incentiveEmptyBody";
                                    interviewIncentiveVal.style = "display: inline-block;";
                                    if(jobPost.jobPostPartnerInterviewIncentive == null || jobPost.jobPostPartnerInterviewIncentive == 0){
                                        interviewIncentiveVal.textContent = "Interview incentive not specified";
                                    } else{
                                        interviewIncentiveVal.textContent = "₹" + rupeeFormatSalary(jobPost.jobPostPartnerInterviewIncentive) + " interview incentive";
                                        incentiveIcon.src = "/assets/partner/img/money-bag.png";
                                        interviewIncentiveVal.className = "incentiveBody";
                                    }
                                    interviewIncentiveRowCol.appendChild(interviewIncentiveVal);

                                    //!*  joining incentive  *!/

                                    var joiningIncentiveCol = document.createElement("div");
                                    joiningIncentiveCol.className = "col-sm-4";
                                    incentiveDetails.appendChild(joiningIncentiveCol);

                                    var joiningIncentiveRow = document.createElement("div");
                                    joiningIncentiveRow.className = "row";
                                    joiningIncentiveCol.appendChild(joiningIncentiveRow);

                                    var joiningIncentiveRowCol = document.createElement("div");
                                    joiningIncentiveRowCol.className = "col-sm-12";
                                    joiningIncentiveRow.appendChild(joiningIncentiveRowCol);

                                    incentiveIconDiv = document.createElement("div");
                                    incentiveIconDiv.style = "display : inline-block;top:0";
                                    joiningIncentiveRowCol.appendChild(incentiveIconDiv);

                                    incentiveIcon = document.createElement("img");
                                    incentiveIcon.src = "/assets/partner/img/coin.png";
                                    incentiveIcon.setAttribute('height', '20px');
                                    incentiveIcon.style = "margin: -4px 0 0 -5px";
                                    incentiveIconDiv.appendChild(incentiveIcon);

                                    var joiningIncentiveVal = document.createElement("div");
                                    joiningIncentiveVal.className = "incentiveEmptyBody";
                                    joiningIncentiveVal.style = "display: inline-block;";
                                    if(jobPost.jobPostPartnerJoiningIncentive == null || jobPost.jobPostPartnerJoiningIncentive == 0){
                                        joiningIncentiveVal.textContent = "Joining Incentive not specified";
                                    } else{
                                        joiningIncentiveVal.textContent =  "₹" + rupeeFormatSalary(jobPost.jobPostPartnerJoiningIncentive) + " joining incentive";
                                        incentiveIcon.src = "/assets/partner/img/money-bag.png";
                                        joiningIncentiveVal.className = "incentiveBody";
                                    }
                                    incentiveIconDiv.appendChild(joiningIncentiveVal);
                                }

                                //!*  apply button *!/
                                var applyBtnDiv = document.createElement("div");
                                applyBtnDiv.className = "col-sm-2";
                                rowDiv.appendChild(applyBtnDiv);

                                var applyBtn = document.createElement("div");
                                applyBtn.className = "jobApplyBtn";
                                applyBtn.textContent = "View & Apply";
                                applyBtnDiv.appendChild(applyBtn);
                                applyBtn.onclick=function(){
                                    var jobPostBreak = jobPost.jobPostTitle.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'_');
                                    jobPostBreak = jobPostBreak.toLowerCase();
                                    var jobCompany = jobPost.company.companyName.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'_');
                                    jobCompany = jobCompany.toLowerCase();
                                    try {
                                        window.location.href = "/jobs/" + jobPostBreak + "/bengaluru/" + jobCompany + "/" + jobPost.jobPostId;
                                    } catch (exception) {
                                        console.log("exception occured!!" + exception);
                                    }
                                }
                            }
                        });

                    }
                }

                // TODO add placeholder in html
                $(".first").hide();
                $(".last").hide();
                $(".prev a").html("<<");
                $(".next a").html(">>");
            },
            pagination: function (noOfPages) {
                $('#jobCardControl').twbsPagination({
                    totalPages: noOfPages,
                    visiblePages: 5,
                    onPageClick: function (event, page) {
                        if(page > 0 ){
                            app.index = (page - 1)*5;
                        }
                        else{
                            app.index = 0;
                        }
                        app.do.search();
                        $(".page-link").click(function(){
                            $('html, body').animate({scrollTop: $("#job_cards_inc").offset().top - 100}, 800);
                        });
                    }
                });
            },
            createAndAppendDivider: function (title) {
                var parent = $("#hotJobs");

                var mainDiv = document.createElement("div");
                mainDiv.id = "hotJobItemDivider";
                parent.append(mainDiv);

                var otherJobIcon = document.createElement("img");
                otherJobIcon.src = "/assets/common/img/suitcase.png";
                otherJobIcon.style = "width: 42px; margin: 8px";
                otherJobIcon.setAttribute("display", "inline-block");
                mainDiv.appendChild(otherJobIcon);

                var hotJobItem = document.createElement("span");
                hotJobItem.setAttribute("display", "inline-block");
                hotJobItem.textContent = title;

                mainDiv.appendChild(hotJobItem);
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

    $('#job_cards_inc').load('/jobPostCardView');

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
