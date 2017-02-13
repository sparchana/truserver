/*
 * truSearch: TruJobs Job Card module
 * Version 1.0.0
 *
 * Copyright (c) 2016 TruJobs.in (http://trujobs.in)
 *
 * Created by Hawk on 04/01/17.
 *
 * Dependency: jQuery, bootstrap.min.js
 *
 * Pass a parent div to which needs JobCard appended
 *
 */
var candidateMobileCheck = localStorage.getItem("mobile");
var cardModule = (function ($) {
    'use strict';

    String.prototype.capitalizeFirstLetter = function() {
        return this.charAt(0).toUpperCase() + this.slice(1);
    };

    String.prototype.toTitleCase = function () {
        return this.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
    };

    var cardModule = {
        deActivationMessage: null,
        applicationSuccess: "You have successfully applied",
        applicationFail: "Something went wrong. Try after sometime",
        method: {
            genNewJobCard: function (_jobPostList, _parent) {
                if(cardModule.deActivationMessage == null) {
                    cardModule.method.getDeActivateMessage();
                }

                _jobPostList.forEach(function (jobPost) {
                    //!* get all localities of the jobPost *!/
                    var _jobLocality = jobPost.jobPostToLocalityList;
                    var _localities = "";
                    var _allLocalities = "";
                    var _loopCount = 0;

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
                    jobTitle.textContent = jobPost.jobPostTitle;
                    jobTitle.onclick = function () {
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
                    jobBodyCol.appendChild(jobTitle);

                    var jobCompany= document.createElement("p");
                    jobCompany.textContent = jobPost.jobRole.jobName + " Job | "+ jobPost.company.companyName;
                    jobCompany.style = "color:rgba(0, 159, 219, 0.99);font-weight:600";
                    jobCompany.onclick = function () {
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
                    jobBodyCol.appendChild(jobCompany);


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
                    bodyCol.id = "jobSalaryCard";
                    jobBodyDetailsFirst.appendChild(bodyCol);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "row hidden-xs";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Salary";
                    bodyCol.appendChild(subDivHint);

                    var subRowForData = document.createElement("div");
                    subRowForData.className = "row";
                    subRowForData.style = "margin-bottom:2px";
                    bodyCol.appendChild(subRowForData);

                    var salaryIconDiv = document.createElement("div");
                    salaryIconDiv.className="col-sm-2 hidden-xs";
                    salaryIconDiv.id="iconsProp";
                    subRowForData.appendChild(salaryIconDiv);

                    var salaryIcon = document.createElement("img");
                    salaryIcon.src = "/assets/common/img/details/rupee.svg";
                    salaryIcon.setAttribute('height', '16px');
                    salaryIconDiv.appendChild(salaryIcon);

                    var salaryDataDiv = document.createElement("div");
                    salaryDataDiv.className="col-xs-12 col-sm-10";
                    salaryDataDiv.id="textContentProp";
                    subRowForData.appendChild(salaryDataDiv);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "col-xs-4 hidden-sm hidden-md hidden-lg";
                    subDivHint.id = "hintTextPropM";
                    subDivHint.textContent = "Salary";
                    salaryDataDiv.appendChild(subDivHint);

                    var salaryDiv = document.createElement("div");
                    salaryDiv.style = "display: inline-block";

                    if (jobPost.jobPostMaxSalary == "0" || jobPost.jobPostMaxSalary == null) {
                        salaryDiv.textContent = rupeeFormatForSalary(jobPost.jobPostMinSalary) + " monthly";
                    } else {
                        salaryDiv.textContent = rupeeFormatForSalary(jobPost.jobPostMinSalary) + " - " + rupeeFormatForSalary(jobPost.jobPostMaxSalary) + " monthly";
                    }
                    salaryDataDiv.appendChild(salaryDiv);

                    //!*  experience  *!/

                    var bodyCol = document.createElement("div");
                    bodyCol.className = "col-sm-6 col-md-4";
                    bodyCol.id = "jobExperienceCard";
                    jobBodyDetailsSecond.appendChild(bodyCol);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "row hidden-xs";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Experience";
                    bodyCol.appendChild(subDivHint);

                    var subRowForData = document.createElement("div");
                    subRowForData.className = "row";
                    subRowForData.style = "margin-bottom:2px";
                    bodyCol.appendChild(subRowForData);

                    var expIconDiv = document.createElement("div");
                    expIconDiv.className="col-sm-2 hidden-xs";
                    expIconDiv.id="iconsProp";
                    subRowForData.appendChild(expIconDiv);

                    var expIcon = document.createElement("img");
                    expIcon.src = "/assets/common/img/details/quality.svg";
                    expIcon.setAttribute('height', '16px');
                    expIconDiv.appendChild(expIcon);

                    var expDataDiv = document.createElement("div");
                    expDataDiv.className="col-xs-12 col-sm-10";
                    expDataDiv.id="textContentProp";
                    subRowForData.appendChild(expDataDiv);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "col-xs-4 hidden-sm hidden-md hidden-lg";
                    subDivHint.id = "hintTextPropM";
                    subDivHint.textContent = "Experience";
                    expDataDiv.appendChild(subDivHint);

                    var expDiv = document.createElement("div");
                    expDiv.style = "display: inline-block;";
                    expDiv.textContent = cardModule.validate.experience(jobPost.jobPostExperience);
                    expDataDiv.appendChild(expDiv);

                    // gender div

                    var genderCol = document.createElement("div");
                    genderCol.className = "col-sm-6 col-md-4";
                    genderCol.id = "jobGenderCard";
                    jobBodyDetailsFirst.appendChild(genderCol);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "row hidden-xs";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Gender";
                    genderCol.appendChild(subDivHint);

                    var subRowForData = document.createElement("div");
                    subRowForData.className = "row";
                    subRowForData.style = "margin-bottom:2px";
                    genderCol.appendChild(subRowForData);

                    var genderIconDiv = document.createElement("div");
                    genderIconDiv.className="col-sm-2 hidden-xs";
                    genderIconDiv.id="iconsProp";
                    subRowForData.appendChild(genderIconDiv);

                    var genderIcon = document.createElement("img");
                    genderIcon.src = "/assets/common/img/details/gender.svg";
                    genderIcon.setAttribute('height', '16px');
                    genderIconDiv.appendChild(genderIcon);

                    var genderDataDiv = document.createElement("div");
                    genderDataDiv.className="col-xs-12 col-sm-10";
                    genderDataDiv.id="textContentProp";
                    subRowForData.appendChild(genderDataDiv);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "col-xs-4 hidden-sm hidden-md hidden-lg";
                    subDivHint.id = "hintTextPropM";
                    subDivHint.textContent = "Gender";
                    genderDataDiv.appendChild(subDivHint);

                    var genderDiv = document.createElement("div");
                    genderDiv.style = "display: inline-block";
                    genderDiv.textContent = cardModule.validate.gender(jobPost.gender);
                    genderDataDiv.appendChild(genderDiv);

                    //!*  Education  *!/

                    var bodyColEdu = document.createElement("div");
                    bodyColEdu.className = "col-sm-6 col-md-4";
                    bodyColEdu.id = "jobEducationCard";
                    jobBodyDetailsSecond.appendChild(bodyColEdu);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "row hidden-xs";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Education";
                    bodyColEdu.appendChild(subDivHint);

                    var jobBodySubRowEdu = document.createElement("div");
                    jobBodySubRowEdu.className = "row";
                    jobBodySubRowEdu.style = "margin-bottom:2px";
                    bodyColEdu.appendChild(jobBodySubRowEdu);

                    var eduIconDiv = document.createElement("div");
                    eduIconDiv.id="iconsProp";
                    eduIconDiv.className = "col-sm-2 hidden-xs";
                    jobBodySubRowEdu.appendChild(eduIconDiv);

                    var eduIcon = document.createElement("img");
                    eduIcon.src = "/assets/common/img/details/science-book.svg";
                    eduIcon.setAttribute('height', '16px');
                    eduIconDiv.appendChild(eduIcon);

                    var eduDataDiv = document.createElement("div");
                    eduDataDiv.className="col-xs-12 col-sm-10";
                    eduDataDiv.id="textContentProp";
                    jobBodySubRowEdu.appendChild(eduDataDiv);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "col-xs-4 hidden-sm hidden-md hidden-lg";
                    subDivHint.id = "hintTextPropM";
                    subDivHint.textContent = "Education";
                    eduDataDiv.appendChild(subDivHint);

                    var EducationDiv = document.createElement("div");
                    EducationDiv.style = "display: inline-block";
                    EducationDiv.textContent = cardModule.validate.education(jobPost.jobPostEducation);
                    eduDataDiv.appendChild(EducationDiv);

                    // age div

                    var ageCol = document.createElement("div");
                    ageCol.className = "col-sm-6 col-md-4";
                    ageCol.id = "jobAgeCard";
                    jobBodyDetailsSecond.appendChild(ageCol);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "row hidden-xs";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Max Age";
                    ageCol.appendChild(subDivHint);

                    var subRowForData = document.createElement("div");
                    subRowForData.className = "row";
                    subRowForData.style = "margin-bottom:2px";
                    ageCol.appendChild(subRowForData);

                    var ageIconDiv = document.createElement("div");
                    ageIconDiv.className="col-sm-2 hidden-xs";
                    ageIconDiv.id="iconsProp";
                    subRowForData.appendChild(ageIconDiv);

                    var ageIcon = document.createElement("img");
                    ageIcon.src = "/assets/common/img/details/age.svg";
                    ageIcon.setAttribute('height', '16px');
                    ageIconDiv.appendChild(ageIcon);

                    var ageDataDiv = document.createElement("div");
                    ageDataDiv.className="col-xs-12 col-sm-10";
                    ageDataDiv.id="textContentProp";
                    subRowForData.appendChild(ageDataDiv);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "col-xs-4 hidden-sm hidden-md hidden-lg";
                    subDivHint.id = "hintTextPropM";
                    subDivHint.textContent = "Max Age";
                    ageDataDiv.appendChild(subDivHint);

                    var ageDiv = document.createElement("div");
                    ageDiv.style = "display: inline-block";
                    ageDiv.textContent = cardModule.validate.maxAge(jobPost.jobPostMaxAge);
                    ageDataDiv.appendChild(ageDiv);

                    // Location div

                    var bodyColLocation = document.createElement("div");
                    bodyColLocation.className = "col-sm-6 col-md-4";
                    bodyColLocation.id = "jobTimeShiftCard";
                    jobBodyDetailsFirst.appendChild(bodyColLocation);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "row hidden-xs";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Location";
                    bodyColLocation.appendChild(subDivHint);

                    var subRowForData = document.createElement("div");
                    subRowForData.className = "row";
                    subRowForData.style = "margin-bottom:2px";
                    bodyColLocation.appendChild(subRowForData);

                    var locationIconDiv = document.createElement("div");
                    locationIconDiv.id="iconsProp";
                    locationIconDiv.className = "col-sm-2 hidden-xs";
                    subRowForData.appendChild(locationIconDiv);

                    var locationIcon = document.createElement("img");
                    locationIcon.src = "/assets/common/img/details/buildings.svg";
                    locationIcon.setAttribute('height', '16px');
                    locationIconDiv.appendChild(locationIcon);

                    var locationDataDiv = document.createElement("div");
                    locationDataDiv.className="col-xs-12 col-sm-10";
                    locationDataDiv.id="textContentProp";
                    subRowForData.appendChild(locationDataDiv);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "col-xs-4 hidden-sm hidden-md hidden-md hidden-lg";
                    subDivHint.id = "hintTextPropM";
                    subDivHint.textContent = "Location";
                    locationDataDiv.appendChild(subDivHint);

                    var locDiv = document.createElement("div");
                    locDiv.style = "display: inline-block";
                    locDiv.textContent = _localities;
                    locationDataDiv.appendChild(locDiv);

                    if (((_jobLocality.length) - 2) > 0) {
                        var tooltip = document.createElement("a");
                        tooltip.id = "locationMsg_" + jobPost.jobPostId;
                        tooltip.title = _allLocalities;
                        tooltip.style = "color: #2980b9";
                        tooltip.textContent = " more";
                        locDiv.appendChild(tooltip);
                    }

                    $("#locationMsg_" + jobPost.jobPostId).attr("data-toggle", "tooltip");
                    $(function () {
                        $('[data-toggle="tooltip"]').tooltip()
                    });

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
                    postedOnDiv.id = "postedDate";
                    postedOnDiv.textContent = "Posted: " + cardModule.parse.createdOnDate(jobPost.jobPostCreateTimestamp);
                    rowDivApplyButton.appendChild(postedOnDiv);


                    var applyBtnDiv = document.createElement("div");
                    applyBtnDiv.className = "col-sm-6";
                    rowDivApplyButton.appendChild(applyBtnDiv);

                    var applyBtnRow = document.createElement("div");
                    applyBtnRow.className = "row";
                    applyBtnRow.style = "margin:0";

                    var reopenRow = document.createElement("div");
                    reopenRow.className = "row";
                    reopenRow.id = "reopenTextProp";

                    var nextMonday = new Date();
                    nextMonday.setDate(nextMonday.getDate() + (1 + 7 - nextMonday.getDay()) % 7);

                    var day = nextMonday.getDate();
                    if(day < 10){
                        day = "0" + day;
                    }

                    var month = nextMonday.getMonth() + 1;
                    if(month < 10){
                        month = "0" + month;
                    }
                    var textReopen = document.createElement("font");
                    textReopen.textContent = "Application will reopen on " + day + "-" + month + "-" + nextMonday.getFullYear();
                    textReopen.style = "font-size:12px";
                    reopenRow.appendChild(textReopen);

                    //!*  more button *!/
                    var jobMoreCol = document.createElement("div");
                    jobMoreCol.className = "col-sm-2 hidden-xs";
                    jobMoreCol.id = "jobMore";
                    rowDiv.appendChild(jobMoreCol);

                    var jobApplyCol = document.createElement("div");
                    jobApplyCol.className = " col-xs-12 hidden-sm hidden-md hidden-lg";
                    jobApplyCol.id = "jobMore";
                    rowDiv.appendChild(jobApplyCol);

                    // vacancies div
                    var vacanciesDiv = document.createElement("div");
                    vacanciesDiv.style ="margin-bottom:6px";
                    vacanciesDiv.textContent = "Vacancies : " + jobPost.jobPostVacancies;
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
                    var applyBtn = document.createElement("button");
                    applyBtn.className = "jobApplyBtn2";
                    var applyJobText ;

                    if(jobPost.applyBtnStatus != null && jobPost.applyBtnStatus != 4){
                        if(jobPost.applyBtnStatus == CTA_BTN_INTERVIEW_REQUIRED) {
                            applyJobText = "Book Interview";
                            applyBtn.style = "background:#039be5;font-weight:bold";
                        } else if(jobPost.applyBtnStatus == CTA_BTN_ALREADY_APPLIED) {
                            applyJobText = "Already Applied";
                            applyBtn.disabled =  true;
                            applyBtn.style = "background:#ffa726;font-weight:bold";
                        } else if(jobPost.applyBtnStatus == CTA_BTN_INTERVIEW_CLOSED) {
                            applyJobText = "Application Closed";
                            applyBtn.disabled =  true;
                            applyBtn.style = "background:#ffa726;font-weight:bold";
                        }else if(jobPost.applyBtnStatus == CTA_BTN_CALL_TO_APPLY) {
                            applyBtn.style = "background:#00e676;font-weight:bold";
                        }

                    } else {
                        applyJobText = "Apply";
                        applyBtn.style = "background:#00c853";
                    }
                    applyBtn.textContent = applyJobText;
                    var w = window.innerWidth;
                    if( w > 786 ){
                        applyBtnRow.appendChild(applyBtn);
                    } else{
                        jobApplyCol.appendChild(applyBtn);
                    }
                    applyBtnDiv.appendChild(applyBtnRow);

                    if(jobPost.applyBtnStatus == 5){
                        applyBtnDiv.appendChild(reopenRow);
                    }
                    applyBtn.onclick = function () {
                            if(jobPost.applyBtnStatus == 7){
                                if(candidateMobileCheck != null){
                                    cardModule.method.genRecruiterContactModal(
                                        jobPost.recruiterProfile.recruiterProfileName,
                                        jobPost.jobPostId);
                                    cardModule.method.callToApplyAction(jobPost.jobPostId);
                                } else{
                                    cardModule.method.genRecruiterContactModal(
                                        jobPost.recruiterProfile.recruiterProfileName,
                                        jobPost.jobPostId);
                                }

                        } else{
                            var jobPostBreak = jobPost.jobPostTitle.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'-');
                            jobPostBreak = jobPostBreak.toLowerCase();
                            var jobCompany = jobPost.company.companyName.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'-');
                            jobCompany = jobCompany.toLowerCase();
                            try {
                                window.location.href = "/jobs/" + jobPostBreak + "-jobs-in-bengaluru-at-" + jobCompany + "-" + jobPost.jobPostId;
                            } catch (exception) {
                                console.log("exception occured!!" + exception);
                            }
                        }
                    };

                    /*if(jobPost.applyBtnStatus != null && jobPost.applyBtnStatus != 4){
                        if(jobPost.applyBtnStatus == 2) {
                            applyJobText = "Book Interview";
                        } else if(jobPost.applyBtnStatus == 3) {
                            applyJobText = "Already Applied";
                            applyBtn.disabled =  true;
                            applyBtn.style = "background:#ffa726";
                        } else if(jobPost.applyBtnStatus == 5) {
                            applyJobText = "Application Closed";
                            applyBtn.disabled =  true;
                            applyBtn.style = "background:#ffa726";
                        } else if(jobPost.applyBtnStatus == 6) {
                            applyJobText = "Apply";
                            applyBtn.style = "background:#ffa726";
                            applyBtn.onclick = function () {
                                notifyMsg(cardModule.deActivationMessage, 'danger');
                            };
                        }
                    } else {
                        applyJobText = "Apply";
                    }*/
                    applyBtn.textContent = applyJobText;
                    if(jobPost.applyBtnStatus == CTA_BTN_CALL_TO_APPLY){

                        var icon = document.createElement("img");
                        icon.setAttribute('src',"/assets/common/img/callToApply.svg");
                        icon.style = "height:18px;margin-top:-4px;";
                        applyBtn.appendChild(icon);

                        var icon = document.createElement("span");
                        icon.style = "font-size:14px;margin-left:5px";
                        icon.textContent= "CALL NOW";
                        applyBtn.appendChild(icon);

                    }
                });
            },
            getDeActivateMessage: function () {
                //ajax call || its a promise
                $.ajax({type: 'POST', url: '/getDeActivationMessage'}).then(function (returnedData) {
                        if (returnedData != null
                            && returnedData.deActivationMessage != null
                            && cardModule.deActivationMessage == null) {

                            cardModule.deActivationMessage = returnedData.deActivationMessage;
                        } else {
                            cardModule.deActivationMessage = "";
                        }
                    },
                    function (xhr, state, error) {
                    });
            },
            callToApplyAction: function (jobPostId) {

                var candidateName;
                var candidateMobileNumber;
                var submitButton = $("#recruiterContactModalBtn");

                if(candidateMobileCheck == null) {
                    candidateName = $("#candidateNameRecruiterContactModal").val();
                    candidateMobileNumber = $("#candidateMobileRecruiterContactModal").val();
                } else{
                    candidateName = localStorage.getItem("name");
                    candidateMobileNumber = (localStorage.getItem("mobile")).slice(3) ;
                }
                if(cardModule.validate.candidateNameValidation(candidateName) && cardModule.validate.candidateMobileValidation(candidateMobileNumber)){
                    submitButton.prop("disabled","true");
                    submitButton.html("Please wait...");

                    var d = {
                        candidateMobile:  candidateMobileNumber,
                        candidateName: candidateName,
                        jobId: jobPostId
                    };

                    $.ajax({
                            type: 'POST', url: '/quickApply/',
                            contentType: "application/json; charset=utf-8",
                            data: JSON.stringify(d)
                        }
                    ).then(function (returnedData) {
                            if (returnedData != null) {

                                if(returnedData.status == 1){
                                    var recruiterNumber = returnedData.recruiterMobile;
                                    submitButton.prop("disabled","true");
                                    submitButton.html("Call : " +recruiterNumber.slice(3,recruiterNumber.length)) ;

                                    var w = window.innerWidth;
                                    if (w < 786) {
                                        document.location.href = "tel:" + recruiterNumber.slice(3,recruiterNumber.length);
                                    }
                                } else{
                                    notifyMsg(cardModule.applicationFail,'danger');
                                }

                            } else {
                                notifyMsg(cardModule.applicationFail,'danger');
                            }
                        },
                        function (xhr, state, error) {
                        });
                }
            },
            genRecruiterContactModal: function (recruiterName,jobPostId) {

                var w = window.innerWidth;

                $("#recruiterModal").html("");
                var parent = $("#recruiterModal");

                var recruiterContactModal = document.createElement("div");
                recruiterContactModal.className = "modal fade";
                recruiterContactModal.id = "recruiterContact";
                recruiterContactModal.role = "dailog";
                parent.append(recruiterContactModal);

                var recruiterContactModalDialog = document.createElement("div");
                recruiterContactModalDialog.className = "modal-dialog";
                if(w > 786){
                    recruiterContactModalDialog.style = "margin-top:10%";
                }else{
                    recruiterContactModalDialog.style = "margin-top:40%";
                }
                recruiterContactModal.appendChild(recruiterContactModalDialog);

                var recruiterContactModalContent = document.createElement("div");
                recruiterContactModalContent.className = "modal-content";
                recruiterContactModalDialog.appendChild(recruiterContactModalContent);

                var recruiterContactModalBody = document.createElement("div");
                recruiterContactModalBody.className = "modal-body";
                recruiterContactModalBody.style = "padding:0";
                recruiterContactModalContent.appendChild(recruiterContactModalBody);

                var recruiterFirstName = recruiterName.split(" ");
                if(recruiterFirstName[0].length <= 3){
                    recruiterFirstName[0] = recruiterFirstName[0]+" "+recruiterFirstName[1];
                }

                var modalHeadingText = document.createElement("h4");
                modalHeadingText.style ="text-align:center;font-weight:bold;font-size:16px";
                modalHeadingText.textContent ="Call HR ("+recruiterFirstName[0]+") and Go !!";
                recruiterContactModalBody.appendChild(modalHeadingText);

                var hr = document.createElement("hr");
                recruiterContactModalBody.appendChild(hr);

                var recruiterContactMainDiv = document.createElement("div");
                recruiterContactMainDiv.className = "row";
                recruiterContactMainDiv.id = "contactRecruiter";
                recruiterContactMainDiv.style = "margin-top: 0; margin-right: 2%; margin-left: 2%";
                recruiterContactModalBody.appendChild(recruiterContactMainDiv);
                if(candidateMobileCheck == null){
                    var candidateName = document.createElement("input");
                    candidateName.className= "form-control input-md";
                    candidateName.id = "candidateNameRecruiterContactModal";
                    candidateName.style = "margin:5px 0";
                    candidateName.type = "type";
                    candidateName.placeholder = "Name (e.g. Ankit Singh)";
                    candidateName.setAttribute('autofocus', 'autofocus');
                    recruiterContactMainDiv.appendChild(candidateName);

                    var candidateMobile = document.createElement("input");
                    candidateMobile.className= "form-control input-md";
                    candidateMobile.id = "candidateMobileRecruiterContactModal";
                    candidateMobile.type = "type";
                    candidateMobile.style = "margin:5px 0";
                    candidateMobile.setAttribute("maxlength","10");
                    candidateMobile.placeholder = "Your Phone No (e.g. 9998887770)";
                    recruiterContactMainDiv.appendChild(candidateMobile);
                }

                var submitButton = document.createElement("button");
                submitButton.className = "btn btn-default";
                submitButton.id = "recruiterContactModalBtn";
                submitButton.style = "font-size:18px;background:#00e676;margin-top: 8px; padding-top: 3%; padding-bottom: 3%; padding-right: 8%; padding-left: 8%; width: 100%;font-weight:bold";
                submitButton.type = "button";
                submitButton.onclick = function() {
                    //Call to action function for non-login candidate
                    cardModule.method.callToApplyAction(jobPostId);
                };
                recruiterContactMainDiv.appendChild(submitButton);

                var icon = document.createElement("img");
                icon.setAttribute('src',"/assets/common/img/callToApply.svg");
                icon.style = "height:22px;margin-top:-8px;margin-left:-15px";
                submitButton.appendChild(icon);

                var icon = document.createElement("span");
                icon.style = "font-size:18px;margin-left:15px";
                icon.textContent= "CALL NOW";
                submitButton.appendChild(icon);

                $("#recruiterContact").modal("show");
            }
        },
        validate: {
            candidateMobileValidation:function(phone){
                var res = validateMobile(phone);
                var validationResult = true;
                if(res == 0){
                    notifyMsg("Enter a valid mobile number",'danger');
                    validationResult = false;
                } else if(res == 1){
                    notifyMsg("Enter 10 digit mobile number",'danger');
                    validationResult = false;
                }

                return validationResult;
            },
            candidateNameValidation:function (firstName) {
                var firstNameCheck = validateName(firstName);
                var validationResult = true;
                switch(firstNameCheck){
                    case 0: notifyMsg("Name contains number. Please Enter a valid  name",'danger'); validationResult = false; break;
                    case 2: notifyMsg("Name cannot be blank spaces. Enter a valid  name",'danger'); validationResult = false; break;
                    case 3: notifyMsg("Name contains special symbols. Enter a valid  name",'danger'); validationResult = false; break;
                    case 4: notifyMsg("Please enter your first name",'danger'); validationResult = false; break;
                }
            return validationResult;
            },
            education: function (education) {
                if(education != null ){
                    return education.educationName;
                } else {
                    return "Not Specified"
                }
            },
            workShift: function (jobPostShift) {
                if(jobPostShift == null) {
                    return "";
                } else if(jobPostShift.timeShiftName){
                    return jobPostShift.timeShiftName;
                }
            },
            gender : function (gender) {
                if(gender == null || gender == 2 ){
                    return "Any";
                } else if (gender == 0){
                    return "Male";
                } else if (gender == 1){
                    return "Female";
                }
                return "Not Specified";
            },
            maxAge: function (age) {
                if(age == null) {
                    return "Not Specified";
                }  else {
                    return age+" yrs";
                }
            },
            experience: function (jobPostExperience) {
                if(jobPostExperience == null){
                    return "NA";
                } else {
                    return jobPostExperience.experienceType;
                }
            }
        },
        parse: {
            createdOnDate: function (timestamp) {
                var jobDate = new Date(timestamp);
                var currentDate = new Date();

                var daysDiff= cardModule.parse.daysDiff(jobDate, currentDate);

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

    return cardModule;
}(jQuery));

// public card module handler
function genNewJobCard(_jobPostList, parent) {
    console.log("jobcard working on " + _jobPostList.length + " jobpost");
    cardModule.method.genNewJobCard(_jobPostList, parent);
}


function notifyMsg(msg, type) {
    if(typeof $.notify == 'function'){
        $.notify({
            message: msg,
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        }, {
            type: type,
            placement: {
                from: "top",
                align: "center"
            }
        });
    } else {
        alert(msg);
    }
};

function rupeeFormatForSalary(sal){
    if(sal != null){
        sal = sal.toString();
        var lastThree = sal.substring(sal.length-3);
        var otherNumbers = sal.substring(0, sal.length-3);
        if(otherNumbers != '')
            lastThree = ',' + lastThree;
        return otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree;
    }
    return "";
}