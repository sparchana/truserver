/*
 * truSearch: TruJobs Job Card module
 * Version 1.0.0
 *
 * Copyright (c) 2016 TruJobs.in (http://trujobs.in)
 *
 * Created by Hawk on 04/01/17.
 *
 * Dependency: jQuery, validation.js
 *
 * Pass a parent div to which needs JobCard appended
 *
 */


var cardModule = (function ($) {
    'use strict';

    String.prototype.capitalizeFirstLetter = function() {
        return this.charAt(0).toUpperCase() + this.slice(1);
    };

    String.prototype.toTitleCase = function () {
        return this.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
    };

    var cardModule = {
        method: {
            genNewJobCard: function (_jobPostList, _parent) {
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
                    jobBodyCol.appendChild(jobTitle);

                    var jobCompany= document.createElement("p");
                    jobCompany.textContent = jobPost.jobRole.jobName + " Job | "+ jobPost.company.companyName;
                    jobCompany.style = "color:rgba(0, 159, 219, 0.99);font-weight:600";
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
                    subDivHint.className = "row";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Salary";
                    bodyCol.appendChild(subDivHint);

                    var subRowForData = document.createElement("div");
                    subRowForData.className = "row";
                    subRowForData.style = "margin-bottom:2px";
                    bodyCol.appendChild(subRowForData);

                    var salaryIconDiv = document.createElement("div");
                    salaryIconDiv.className="col-xs-2";
                    salaryIconDiv.id="iconsProp";
                    subRowForData.appendChild(salaryIconDiv);

                    var salaryIcon = document.createElement("img");
                    salaryIcon.src = "/assets/common/img/details/rupee.svg";
                    salaryIcon.setAttribute('height', '16px');
                    salaryIconDiv.appendChild(salaryIcon);

                    var salaryDataDiv = document.createElement("div");
                    salaryDataDiv.className="col-xs-10";
                    salaryDataDiv.id="textContentProp";
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
                    bodyCol.id = "jobExperienceCard";
                    jobBodyDetailsSecond.appendChild(bodyCol);

                    var subDivHint = document.createElement("div");
                    subDivHint.className = "row";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Experience";
                    bodyCol.appendChild(subDivHint);

                    var subRowForData = document.createElement("div");
                    subRowForData.className = "row";
                    subRowForData.style = "margin-bottom:2px";
                    bodyCol.appendChild(subRowForData);

                    var expIconDiv = document.createElement("div");
                    expIconDiv.className="col-xs-2";
                    expIconDiv.id="iconsProp";
                    subRowForData.appendChild(expIconDiv);

                    var expIcon = document.createElement("img");
                    expIcon.src = "/assets/common/img/details/quality.svg";
                    expIcon.setAttribute('height', '16px');
                    expIconDiv.appendChild(expIcon);

                    var expDataDiv = document.createElement("div");
                    expDataDiv.className="col-xs-10";
                    expDataDiv.id="textContentProp";
                    subRowForData.appendChild(expDataDiv);

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
                    subDivHint.className = "row";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Gender";
                    genderCol.appendChild(subDivHint);

                    var subRowForData = document.createElement("div");
                    subRowForData.className = "row";
                    subRowForData.style = "margin-bottom:2px";
                    genderCol.appendChild(subRowForData);

                    var genderIconDiv = document.createElement("div");
                    genderIconDiv.className="col-xs-2";
                    genderIconDiv.id="iconsProp";
                    subRowForData.appendChild(genderIconDiv);

                    var genderIcon = document.createElement("img");
                    genderIcon.src = "/assets/common/img/details/gender.svg";
                    genderIcon.setAttribute('height', '16px');
                    genderIconDiv.appendChild(genderIcon);

                    var genderDataDiv = document.createElement("div");
                    genderDataDiv.className="col-xs-10";
                    genderDataDiv.id="textContentProp";
                    subRowForData.appendChild(genderDataDiv);

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
                    subDivHint.className = "row";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Education";
                    bodyColEdu.appendChild(subDivHint);

                    var jobBodySubRowEdu = document.createElement("div");
                    jobBodySubRowEdu.className = "row";
                    jobBodySubRowEdu.style = "margin-bottom:2px";
                    bodyColEdu.appendChild(jobBodySubRowEdu);

                    var eduIconDiv = document.createElement("div");
                    eduIconDiv.id="iconsProp";
                    eduIconDiv.className = "col-xs-2";
                    jobBodySubRowEdu.appendChild(eduIconDiv);

                    var eduIcon = document.createElement("img");
                    eduIcon.src = "/assets/common/img/details/science-book.svg";
                    eduIcon.setAttribute('height', '16px');
                    eduIconDiv.appendChild(eduIcon);

                    var eduDataDiv = document.createElement("div");
                    eduDataDiv.className="col-xs-10";
                    eduDataDiv.id="textContentProp";
                    jobBodySubRowEdu.appendChild(eduDataDiv);

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
                    subDivHint.className = "row";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Max Age";
                    ageCol.appendChild(subDivHint);

                    var subRowForData = document.createElement("div");
                    subRowForData.className = "row";
                    subRowForData.style = "margin-bottom:2px";
                    ageCol.appendChild(subRowForData);

                    var ageIconDiv = document.createElement("div");
                    ageIconDiv.className="col-xs-2";
                    ageIconDiv.id="iconsProp";
                    subRowForData.appendChild(ageIconDiv);

                    var ageIcon = document.createElement("img");
                    ageIcon.src = "/assets/common/img/details/age.svg";
                    ageIcon.setAttribute('height', '16px');
                    ageIconDiv.appendChild(ageIcon);

                    var ageDataDiv = document.createElement("div");
                    ageDataDiv.className="col-xs-10";
                    ageDataDiv.id="textContentProp";
                    subRowForData.appendChild(ageDataDiv);

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
                    subDivHint.className = "row";
                    subDivHint.id = "hintTextProp";
                    subDivHint.textContent = "Location";
                    bodyColLocation.appendChild(subDivHint);

                    var subRowForData = document.createElement("div");
                    subRowForData.className = "row";
                    subRowForData.style = "margin-bottom:2px";
                    bodyColLocation.appendChild(subRowForData);

                    var locationIconDiv = document.createElement("div");
                    locationIconDiv.id="iconsProp";
                    locationIconDiv.className = "col-xs-2";
                    subRowForData.appendChild(locationIconDiv);

                    var locationIcon = document.createElement("img");
                    locationIcon.src = "/assets/common/img/details/buildings.svg";
                    locationIcon.setAttribute('height', '16px');
                    locationIconDiv.appendChild(locationIcon);

                    var locationDataDiv = document.createElement("div");
                    locationDataDiv.className="col-xs-10";
                    locationDataDiv.id="textContentProp";
                    subRowForData.appendChild(locationDataDiv);

                    var jobRoleAddress = document.createElement("p");
                    jobRoleAddress.style = "color:#000";
                    locationDataDiv.appendChild(jobRoleAddress);

                    var locDiv = document.createElement("div");
                    locDiv.style = "display: inline-block";
                    locDiv.textContent = _localities;
                    jobRoleAddress.appendChild(locDiv);

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
                    postedOnDiv.style = "margin-top:6px;text-align:left";
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
                    reopenRow.style = "margin-top:10px;float:right";

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
                    jobMoreCol.className = "col-sm-2";
                    jobMoreCol.id = "jobMore";
                    rowDiv.appendChild(jobMoreCol);

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
                            applyBtn.disabled =  true;
                            applyBtn.style = "background:#ffa726";
                            applyBtn.onclick = function () {
                                alert("Looks like your profile is temporarily suspended for new job applications. Please check back after 'deactivation expiry date' or call us at 8880007799 to request re-activation.'")
                            };
                        }
                    } else {
                        applyJobText = "Apply";
                    }
                    applyBtn.textContent = applyJobText;
                    applyBtnRow.appendChild(applyBtn);
                    applyBtnDiv.appendChild(applyBtnRow);
                    if(jobPost.applyBtnStatus == 5){
                        applyBtnDiv.appendChild(reopenRow);
                    }
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
                });
            }
        },
        validate: {
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