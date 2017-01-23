var jobPostId;
var candidateInfo;
var candidateId;

var appliedJobSection;
var popularJobsSection;

var prefLocation;
var prefLocationName;

var prefTimeSlot;

var scheduledInterviewDate;

var globalJpId;
var globalInterviewStatus;
var rescheduledDate;
var jobRoleName;
var companyName;

var allReasons = [];
var allEta = [];
var globalStatus = 0;

var triggerNotGoingModal;
var triggerEtaModal;

var parentConfirmedCount = 0;
var parentPendingConfirmationCount = 0;
var parentCompletedCount = 0;

var interviewsTodayCount = 0;
var confirmedInterviewCount = 0;
var rescheduledCount = 0;

var candidateLat = null;
var candidateLng = null;


$(window).resize(function(){
    var w = window.innerWidth;
    if(w < 640){
        $(".candidatePartner").removeClass("row-eq-height");
    } else{
        $(".candidatePartner").removeClass("row-eq-height").addClass("row-eq-height");
    }
});

function scrapeCandidateIdFromUrl(){
    var pathname = window.location.pathname; // Returns path only
    var partnerUrl = pathname.split('/');
    var cId = partnerUrl[(partnerUrl.length) - 2];
    candidateId = parseInt(cId);
}

$(document).ready(function(){
    var w = window.innerWidth;
    appliedJobSection = false;
    popularJobsSection = true;
    $("#appliedJobsSection").hide();

    try {
        $.ajax({
            type: "POST",
            url: "/getAllInterviewNotGoingReasons",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetAllReason
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "POST",
            url: "/getAllCandidateETA",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetAllEta
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }


    try {
        $.ajax({
            url: "/getCandidateMatchingJobs/" + localStorage.getItem("candidateId"),
            type: "POST",
            data: false,
            dataType: "json",
            contentType: false,
            processData: false,
            success: processDataAllJobPosts
        });
    } catch (exception) {}
    if(localStorage.getItem("appliedJobs") == '1'){
        $("#appliedJobsSection").show();
        $("#applyJobs").hide();
        $(".viewPopularJobs").removeClass("white").addClass("white");
        $(".viewAppliedJobs").removeClass("white");
        localStorage.setItem("appliedJobs", "0");
    }
});

function processDataGetAllReason(returnedData) {
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allReasons.push(item);
    });
}

function processDataGetAllEta(returnedData) {
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allEta.push(item);
    });
}

function toggleTabs(index) {
    if(index == 0){
        popularJobsSection = true;
        appliedJobSection = false;
        $("#applyJobs").show();
        $("#appliedJobsSection").hide();
        $(".viewPopularJobs").removeClass("white");
        $(".viewAppliedJobs").removeClass("white").addClass("white");
    } else{
        getAllAppliedJobs();
        popularJobsSection = true;
        appliedJobSection = false;
        $("#applyJobs").hide();
        $("#appliedJobsSection").show();
        $(".viewPopularJobs").removeClass("white").addClass("white");
        $(".viewAppliedJobs").removeClass("white");

    }
}

function getAllAppliedJobs() {
    try {
        $.ajax({
            type: "GET",
            url: "/getAppliedJobsByPartnerForCandidate/" + localStorage.getItem("candidateId"),
            data: false,
            async: true,
            contentType: false,
            processData: false,
            success: processDataAndGetPartnerCandidateAppliedJobs
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function  processDataAndGetPartnerCandidateAppliedJobs(returnedData) {
    var candidateJobApplication = returnedData;

    candidateJobApplication.reverse();
    prePopulateJobSection(candidateJobApplication);
}

function prePopulateJobSection(jobApplication) {

    interviewsTodayCount = 0;
    confirmedInterviewCount = 0;
    rescheduledCount = 0;

    var parentPendingConfirmation = $('#myAppliedJobsPendingConfirmation');
    var parentConfirmed = $('#myAppliedJobsConfirmed');
    var parentCompleted = $('#myAppliedJobsCompleted');

    parentPendingConfirmation.html('');
    parentConfirmed.html('');
    parentCompleted.html('');

    var appliedJobList = [];
    var rescheduled = [];
    var underReview = [];
    var rejected = [];
    var todayInterview = [];
    var upcomingInterview = [];
    var pastInterview = [];
    var completedInterview = [];

    var rescheduledFlag = false;
    var underReviewFlag = false;
    var rejectedFlag = false;
    var todayInterviewFlag = false;
    var upcomingInterviewFlag = false;
    var pastInterviewFlag = false;
    var completedInterviewFlag = false;

    var today = new Date();

    jobApplication.forEach(function (appliedJob) {

        if(appliedJob.status.statusId == JWF_STATUS_INTERVIEW_RESCHEDULE){

            //rescheduled interviews
            rescheduled.push(appliedJob);
        } else if(appliedJob.status.statusId <= JWF_STATUS_INTERVIEW_SCHEDULED){

            //under review interviews
            underReview.push(appliedJob);
        } else if(appliedJob.status.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE || appliedJob.status.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT){

            //rejected interviews
            rejected.push(appliedJob);
        } else if(appliedJob.status.statusId >= JWF_STATUS_INTERVIEW_CONFIRMED && appliedJob.status.statusId <= JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){

            //confirmed Interviews
            var interviewDate = new Date(appliedJob.scheduledInterviewDate);
            if(interviewDate.getDate() == today.getDate() && interviewDate.getMonth() == today.getMonth() && interviewDate.getFullYear() == today.getFullYear()) {

                // today's schedule
                todayInterview.push(appliedJob);
            } else if(today.getTime() < interviewDate.getTime()){

                // upcoming interviews
                upcomingInterview.push(appliedJob);
            } else{

                // past interviews
                pastInterview.push(appliedJob);
            }
        } else if(appliedJob.status.statusId > JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED) {

            //completed interviews
            completedInterview.push(appliedJob);
        }
    });

    if(rescheduled.length > 0){
        rescheduledCount = 1;
    }

    if(todayInterview.length > 0){
        interviewsTodayCount = 1;
    }

    if(upcomingInterview.length > 0){
        confirmedInterviewCount = 1;
    }

    rescheduled.forEach(function (rescheduledInterview) {
        appliedJobList.push(rescheduledInterview);
    });


    underReview.forEach(function (underReviewInterview) {
        appliedJobList.push(underReviewInterview);
    });

    rejected.forEach(function (rejectedInterview) {
        appliedJobList.push(rejectedInterview);
    });

    todayInterview.forEach(function (interviewToday) {
        appliedJobList.push(interviewToday);
    });

    upcomingInterview.forEach(function (upcoming) {
        appliedJobList.push(upcoming);
    });

    pastInterview.forEach(function (past) {
        appliedJobList.push(past);
    });

    completedInterview.forEach(function (completed) {
        appliedJobList.push(completed);
    });

    var count = 0;
    appliedJobList.forEach(function (jobPost) {
        count++;
        if (count) {
            /* get all localities of the jobApplication */
            var jobLocality = jobPost.jobPost.jobPostToLocalityList;
            var localities = "";
            var allLocalities = "";
            var loopCount = 0;
            jobLocality.forEach(function (locality) {
                loopCount++;
                if (loopCount > 2) {
                    return false;
                } else {
                    var name = locality.locality.localityName;
                    localities += name;
                    if (loopCount < Object.keys(jobLocality).length) {
                        localities += ", ";
                    }
                }
            });

            loopCount = 0;
            jobLocality.forEach(function (locality) {
                loopCount++;
                var name = locality.locality.localityName;
                allLocalities += name;
                if (loopCount < Object.keys(jobLocality).length) {
                    allLocalities += ", ";
                }
            });

            var hotJobItem = document.createElement("div");
            hotJobItem.id = "hotJobItem";

            if(jobPost.status.statusId == JWF_STATUS_INTERVIEW_RESCHEDULE){
                if(!rescheduledFlag){
                    var rescheduledHeader = document.createElement("div");
                    rescheduledHeader.textContent = "Recruiter has rescheduled below interview(s) -  Please confirm interview timing ";
                    rescheduledHeader.className = "headerRibbon";
                    rescheduledHeader.style = "padding: 8px; text-align: center";
                    parentPendingConfirmation.append(rescheduledHeader);
                    rescheduledFlag = true;
                }
                parentPendingConfirmation.append(hotJobItem);
                parentPendingConfirmationCount++;
            } else if(jobPost.status.statusId < JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT) {
                if(!underReviewFlag){
                    var underReviewHeader = document.createElement("div");
                    underReviewHeader.textContent = "Application(s) Under Review - You will receive a notification once recruiter shortlists you";
                    underReviewHeader.className = "headerRibbon";
                    underReviewHeader.style = "padding: 8px; text-align: center";
                    parentPendingConfirmation.append(underReviewHeader);
                    underReviewFlag = true;
                }
                parentPendingConfirmation.append(hotJobItem);
                parentPendingConfirmationCount++;
            } else if (jobPost.status.statusId > JWF_STATUS_INTERVIEW_RESCHEDULE && jobPost.status.statusId < JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED){
                var interviewDate = new Date(jobPost.scheduledInterviewDate);
                var todayDay = new Date();
                if(interviewDate.getDate() == todayDay.getDate() && interviewDate.getMonth() == todayDay.getMonth() && interviewDate.getFullYear() == todayDay.getFullYear()) {
                    if(!todayInterviewFlag){
                        var todayInterviewHeader = document.createElement("div");
                        todayInterviewHeader.textContent = "Interview(s) Today. Please update your status";
                        todayInterviewHeader.className = "headerRibbon";
                        todayInterviewHeader.style = "padding: 8px; text-align: center";
                        parentConfirmed.append(todayInterviewHeader);
                        todayInterviewFlag = true;
                    }
                } else if(todayDay.getTime() < interviewDate.getTime()){
                    if(!upcomingInterviewFlag){
                        var upcomingInterviewHeader = document.createElement("div");
                        upcomingInterviewHeader.textContent = "Upcoming Interview(s)";
                        upcomingInterviewHeader.className = "headerRibbon";
                        upcomingInterviewHeader.style = "padding: 8px; text-align: center";
                        parentConfirmed.append(upcomingInterviewHeader);
                        upcomingInterviewFlag = true;
                    }
                } else{
                    if(!pastInterviewFlag){
                        var pastInterviewHeader = document.createElement("div");
                        pastInterviewHeader.textContent = "Past Interview(s)";
                        pastInterviewHeader.className = "headerRibbon";
                        pastInterviewHeader.style = "padding: 8px; text-align: center";
                        parentConfirmed.append(pastInterviewHeader);
                        pastInterviewFlag = true;
                    }
                }
                parentConfirmed.append(hotJobItem);
                parentConfirmedCount++;
            } else if (jobPost.status.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT || jobPost.status.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE){
                if(!rejectedFlag){
                    var rejectedHeader = document.createElement("div");
                    rejectedHeader.textContent = "Below application(s) were not shortlisted by the recruiter";
                    rejectedHeader.className = "headerRibbon";
                    rejectedHeader.style = "padding: 8px; text-align: center";
                    parentPendingConfirmation.append(rejectedHeader);
                    rejectedFlag = true;
                }
                parentPendingConfirmation.append(hotJobItem);
                parentPendingConfirmationCount++;
            } else if (jobPost.status.statusId > JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){
                if(!completedInterviewFlag){
                    var completedInterviewHeader = document.createElement("div");
                    completedInterviewHeader.textContent = "Completed Interview(s)";
                    completedInterviewHeader.className = "headerRibbon";
                    completedInterviewHeader.style = "padding: 8px; text-align: center";
                    parentCompleted.append(completedInterviewHeader);
                    completedInterviewFlag = true;
                }

                parentCompleted.append(hotJobItem);
                parentCompletedCount++;
            } else {
                parentPendingConfirmation.append(hotJobItem);
                parentPendingConfirmationCount++;
            }

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
            jobLogo.src = jobPost.jobPost.company.companyLogo;
            jobLogo.setAttribute('width', '80%');
            jobLogo.id = "jobLogoMyJob";
            col.appendChild(jobLogo);

            var jobBodyCol = document.createElement("div");
            jobBodyCol.className = "col-sm-10";
            jobBodyCol.id = "jobBody";
            rowDiv.appendChild(jobBodyCol);

            var titleRow = document.createElement("div");
            titleRow.className = "row";
            jobBodyCol.appendChild(titleRow);

            var titleRowOne = document.createElement("div");
            titleRowOne.className = "col-sm-6";
            titleRow.appendChild(titleRowOne);

            var jobTitle = document.createElement("h4");
            jobTitle.textContent = jobPost.jobPost.jobPostTitle + " | " + jobPost.jobPost.company.companyName;
            titleRowOne.appendChild(jobTitle);

            if(jobPost.assessmentRequired == true){
                var jobBodyAssessmentAlert = document.createElement("div");
                jobBodyAssessmentAlert.className = "col-sm-3 jobBodyAssessmentAlert";
                jobBodyCol.appendChild(jobBodyAssessmentAlert);

                var assessmentAlertDiv = document.createElement("div");
                assessmentAlertDiv.className = "assessmentAlertDiv";
                assessmentAlertDiv.id = "ajp_"+jobPost.jobPost.jobRole.jobRoleId;

                assessmentAlertDiv.className+=" red";
                assessmentAlertDiv.textContent = "Complete Assessment Now";
                jobBodyAssessmentAlert.onclick = function () {
                    var jrId = jobPost.jobPost.jobRole.jobRoleId;
                    getAssessmentQuestions(jrId, null);
                };
                jobBodyAssessmentAlert.appendChild(assessmentAlertDiv);
                titleRow.appendChild(jobBodyAssessmentAlert);
            }

            var titleRowThree = document.createElement("div");
            titleRowThree.className = "col-sm-6";
            titleRowThree.id = "interview_status_div_" + jobPost.jobPost.jobPostId;
            titleRowThree.style = "margin-top: 8px; padding: 0";
            titleRow.appendChild(titleRowThree);

            var divInterviewStatus = document.createElement("span");
            divInterviewStatus.className = "appliedDate";
            divInterviewStatus.id = "interview_status_val_" + jobPost.jobPost.jobPostId;

            var dir = document.createElement("span");
            if(jobPost.status.statusId < JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT){
                divInterviewStatus.textContent = "Job application under review";
                divInterviewStatus.style = "color: #eb9800; font-weight: 600; padding: 0";
                if(jobPost.scheduledInterviewDate != null){
                    divInterviewStatus.textContent = "Interview scheduled on " + new Date(jobPost.scheduledInterviewDate).getDate()
                        + "/" + (new Date(jobPost.scheduledInterviewDate).getMonth() + 1) + "/" + new Date(jobPost.scheduledInterviewDate).getFullYear()
                        + " between " + jobPost.scheduledInterviewTimeSlot.interviewTimeSlotName + ". Application under review";
                }
            } else{
                if(jobPost.status.statusId > JWF_STATUS_INTERVIEW_RESCHEDULE && jobPost.status.statusId < JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED){
                    if(jobPost.interviewLocationLat != null){
                        dir.className = "navigationBtn";
                        dir.textContent = "Directions";
                        dir.onclick = function () {
                            if(candidateLat != null){
                                window.open('https://www.google.com/maps/dir/' + candidateLat + ', ' + candidateLng + '/'+ jobPost.interviewLocationLat + ', ' + jobPost.interviewLocationLng);
                            } else{
                                window.open('http://maps.google.com/?q='+ jobPost.interviewLocationLat +',' + jobPost.interviewLocationLng);
                            }
                        };
                    }
                    divInterviewStatus.textContent = "Interview confirmed on " + new Date(jobPost.scheduledInterviewDate).getDate() + "/" + (new Date(jobPost.scheduledInterviewDate).getMonth() + 1) + "/" + new Date(jobPost.scheduledInterviewDate).getFullYear() + " between " + jobPost.scheduledInterviewTimeSlot.interviewTimeSlotName;
                    divInterviewStatus.style = "color: green; font-weight: 600";

                } else if(jobPost.status.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT){
                    divInterviewStatus.textContent = "Application rejected";
                    divInterviewStatus.style = "color: red; font-weight: 600";
                } else if(jobPost.status.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE){
                    divInterviewStatus.textContent = "Application rejected by the Candidate";
                    divInterviewStatus.style = "color: red; font-weight: 600";
                } else if(jobPost.status.statusId == JWF_STATUS_INTERVIEW_RESCHEDULE){
                    divInterviewStatus.textContent = "Recruiter has rescheduled your interview on " + new Date(jobPost.scheduledInterviewDate).getDate() + "/" + (new Date(jobPost.scheduledInterviewDate).getMonth() + 1) + "/" + new Date(jobPost.scheduledInterviewDate).getFullYear() + " between " + jobPost.scheduledInterviewTimeSlot.interviewTimeSlotName;
                    divInterviewStatus.style = "padding: 0; color: orange; font-weight: 600";

                    var br = document.createElement("br");
                    divInterviewStatus.appendChild(br);

                    // accept interview
                    var candidateInterviewAccept = document.createElement("span");
                    candidateInterviewAccept.className = "accept";
                    candidateInterviewAccept.onclick = function () {
                        globalLat = jobPost.interviewLocationLat;
                        globalLng = jobPost.interviewLocationLng;

                        rescheduledDate = "Scheduled on " + new Date(jobPost.scheduledInterviewDate).getDate() + "/" + (new Date(jobPost.scheduledInterviewDate).getMonth() + 1) + "/" + new Date(jobPost.scheduledInterviewDate).getFullYear() + " between " + jobPost.scheduledInterviewTimeSlot.interviewTimeSlotName;
                        confirmInterview(jobPost.jobPost.jobPostId, 1); //rejecting id value = 1
                    };
                    divInterviewStatus.appendChild(candidateInterviewAccept);

                    var iconImg = document.createElement("img");
                    iconImg.src = "/assets/dashboard/img/reached.svg";
                    iconImg.setAttribute('height', '26px');
                    candidateInterviewAccept.appendChild(iconImg);

                    var actionText = document.createElement("span");
                    actionText.textContent = " Accept";
                    actionText.style = "color: black";
                    divInterviewStatus.appendChild(actionText);

                    //reject interview
                    var candidateInterviewReject = document.createElement("span");
                    candidateInterviewReject.className = "reject";
                    candidateInterviewReject.onclick = function () {
                        rescheduledDate = "Scheduled on " + new Date(jobPost.scheduledInterviewDate).getDate() + "/" + (new Date(jobPost.scheduledInterviewDate).getMonth() + 1) + "/" + new Date(jobPost.scheduledInterviewDate).getFullYear() + " between " + jobPost.scheduledInterviewTimeSlot.interviewTimeSlotName;
                        confirmInterview(jobPost.jobPost.jobPostId, 0); //rejecting id value = 0
                    };

                    actionText = document.createElement("span");
                    actionText.textContent = " Reject";
                    actionText.style = "color: black";
                    divInterviewStatus.appendChild(candidateInterviewReject);
                    divInterviewStatus.appendChild(actionText);

                    iconImg = document.createElement("img");
                    iconImg.src = "/assets/dashboard/img/not_going.svg";
                    iconImg.setAttribute('height', '26px');
                    candidateInterviewReject.appendChild(iconImg);
                } else if(jobPost.status.statusId > 13){
                    divInterviewStatus.textContent = jobPost.status.statusTitle;
                    if(jobPost.status.statusId == 14){
                        divInterviewStatus.style = "color: green; font-size: 14px; font-weight: 600";
                    } else{
                        divInterviewStatus.style = "color: red; font-size: 14px; font-weight: 600";
                    }
                }
            }
            titleRowThree.appendChild(divInterviewStatus);
            titleRowThree.appendChild(dir);

            var hr = document.createElement("hr");
            jobBodyCol.appendChild(hr);

            var jobBodyDetails = document.createElement("div");
            jobBodyDetails.className = "row";
            jobBodyDetails.id = "jobBodyDetails";
            jobBodyCol.appendChild(jobBodyDetails);

            /*  salary  */

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
            if (jobPost.jobPost.jobPostMaxSalary != 0 && jobPost.jobPost.jobPostMaxSalary != null) {
                salaryDiv.textContent = jobPost.jobPost.jobPostMinSalary + " - " + jobPost.jobPost.jobPostMaxSalary + " monthly";
            } else {
                salaryDiv.textContent = jobPost.jobPost.jobPostMinSalary + " monthly";
            }

            jobBodySubRowCol.appendChild(salaryDiv);

            /*  experience  */

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
            expDiv.textContent = "Exp: " + jobPost.jobPost.jobPostExperience.experienceType;
            jobBodySubRowColExp.appendChild(expDiv);

            /*  Location  */

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

            if (((jobLocality.length) - 2) > 0) {
                var tooltip = document.createElement("a");
                tooltip.id = "locationMsg_" + jobPost.jobPost.jobPostId;
                tooltip.title = allLocalities;
                tooltip.style = "color: #2980b9";
                tooltip.textContent = " more";
                jobBodySubRowColLoc.appendChild(tooltip);
            }
            $("#locationMsg_" + jobPost.jobPost.jobPostId).attr("data-toggle", "tooltip");

            var hr = document.createElement("hr");
            jobBodyCol.appendChild(hr);

            var titleRowStatus = document.createElement("div");
            titleRowStatus.className = "row col-sm-8";
            titleRowStatus.style = "margin-top: 8px; padding: 0 12px 6px 12px; font-size: 12px";
            jobBodyCol.appendChild(titleRowStatus);

            if(jobPost.status.statusId > JWF_STATUS_INTERVIEW_RESCHEDULE && jobPost.status.statusId <= JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){

                var recruiterBody = document.createElement("div");
                var recruiterInfo = "";
                recruiterBody.textContent = "Recruiter Info: ";
                recruiterBody.style = "margin-top: 8px; margin-right: 12px; font-weight: bold";
                titleRowStatus.appendChild(recruiterBody);

                //computing recruiter info
                if(jobPost.jobPost.recruiterProfile != null){
                    recruiterInfo = jobPost.jobPost.recruiterProfile.recruiterProfileName + " (" + jobPost.jobPost.recruiterProfile.recruiterProfileMobile + ")";
                } else {
                    recruiterInfo = "";
                }

                var recruiterInfoBody = document.createElement("span");
                recruiterInfoBody.style = "font-weight: normal";
                recruiterInfoBody.textContent = recruiterInfo;
                if(recruiterInfo == ""){
                    recruiterInfoBody.textContent = "Information not available";
                }
                recruiterBody.appendChild(recruiterInfoBody);

                //computing Address
                var addressBody = document.createElement("div");
                addressBody.textContent = "Interview Address : ";
                addressBody.style = "margin-top: 8px; margin-right: 12px; font-weight: bold";
                titleRowStatus.appendChild(addressBody);

                var address = "";

                if(jobPost.jobPost.interviewFullAddress != null && jobPost.jobPost.interviewFullAddress != ""){
                    address = jobPost.jobPost.interviewFullAddress;
                } else {
                    address = "";
                }
                var addressInfoBody = document.createElement("span");
                addressInfoBody.style = "font-weight: normal";
                addressInfoBody.textContent = address;
                if(address == ""){
                    addressInfoBody.textContent = "Address not available";
                }
                addressBody.appendChild(addressInfoBody);

            }

            if(jobPost.preScreenRequired){
                if(candidateId == null ) {
                    scrapeCandidateIdFromUrl();
                }
                // jpId is jobPostId
                var jpId = jobPost.jobPost.jobPostId;
                jobRoleName = jobPost.jobPost.jobRole.jobName;
                companyName = jobPost.jobPost.company.companyName;

                var preScreenBody = document.createElement("span");
                preScreenBody.textContent = "Pre-Screen Candidate";
                preScreenBody.style = "margin-top: 8px; padding: 8px; border-radius: 4px; color: white; background: #3159b3; margin-right: 12px; font-weight: bold";
                preScreenBody.onclick = function () {
                    openPartnerPreScreenModal(jpId, candidateId);
                };

                titleRowStatus.appendChild(preScreenBody);
            } else{
                if(jobPost.status.statusId < JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT){
                    preScreenBody = document.createElement("span");
                    preScreenBody.textContent = "Pre-Screen Complete";
                    preScreenBody.style = "margin-top: 8px; padding: 8px; cursor: pointer; color: green; background: none; margin-right: 12px; font-weight: bold";

                    titleRowStatus.appendChild(preScreenBody);

                }
            }



            if(jobPost.status != null){
                if(jobPost.status.statusId > JWF_STATUS_INTERVIEW_RESCHEDULE && jobPost.status.statusId < JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED){
                    var today = new Date();
                    var interviewDate = new Date(jobPost.scheduledInterviewDate);
                    if(interviewDate.getDate() == today.getDate() && interviewDate.getMonth() == today.getMonth() && interviewDate.getFullYear() == today.getFullYear()){ // today's schedule
                        //interview for this job is scheduled today, hence allow to update status

                        var statusUpdateBody = document.createElement("div");
                        statusUpdateBody.style = "margin-top: 6px; margin-bottom: 6px";
                        titleRowStatus.appendChild(statusUpdateBody);

                        var statusBody = document.createElement("span");
                        statusBody.style = "font-weight: bold";
                        statusBody.textContent = "Current Status: ";
                        statusUpdateBody.appendChild(statusBody);

                        var currentStatus = document.createElement("span");
                        statusBody.appendChild(currentStatus);

                        //visual status options
                        var interviewStatusOption = document.createElement("div");
                        interviewStatusOption.id = "status_options_" + jobPost.jobPost.jobPostId;
                        interviewStatusOption.style = "margin-top: 12px";
                        titleRowStatus.appendChild(interviewStatusOption);

                        //not going div
                        var col1 = document.createElement("div");
                        col1.style = "display: none; display: inline-block; margin-right: 8px; text-align: center";
                        col1.className = "statusOption";
                        col1.id = "not_going_" + jobPost.jobPost.jobPostId;
                        col1.onclick = function () {
                            globalStatus = 1;
                            globalJpId = jobPost.jobPost.jobPostId;
                            triggerNotGoingModal = true;
                            triggerEtaModal = false;
                            updateStatus();
                        };
                        interviewStatusOption.appendChild(col1);

                        var notGoingOption = document.createElement("span");
                        col1.appendChild(notGoingOption);

                        var img = document.createElement("img");
                        img.src = "/assets/dashboard/img/not_going.svg";
                        img.setAttribute('height', '28px');
                        notGoingOption.appendChild(img);

                        var text = document.createElement("div");
                        text.textContent = "Not Going";
                        notGoingOption.appendChild(text);


                        //delayed div
                        var col2 = document.createElement("div");
                        col2.style = "display: none; display: inline-block; margin-right: 8px; text-align: center";
                        col2.className = "statusOption";
                        col2.id = "delayed_" + jobPost.jobPost.jobPostId;
                        col2.onclick = function () {
                            globalStatus = 2;
                            globalJpId = jobPost.jobPost.jobPostId;
                            triggerNotGoingModal = false;
                            triggerEtaModal = true;
                            updateStatus();
                        };
                        interviewStatusOption.appendChild(col2);

                        var delayedOption = document.createElement("span");
                        col2.appendChild(delayedOption);

                        img = document.createElement("img");
                        img.src = "/assets/dashboard/img/delayed.svg";
                        img.setAttribute('height', '28px');
                        delayedOption.appendChild(img);

                        text = document.createElement("div");
                        text.textContent = "Delayed";
                        delayedOption.appendChild(text);


                        //started div
                        var col3 = document.createElement("div");
                        col3.className = "statusOption";
                        col3.id = "started_" + jobPost.jobPost.jobPostId;
                        col3.onclick = function () {
                            globalStatus = 3;
                            globalJpId = jobPost.jobPost.jobPostId;
                            triggerNotGoingModal = false;
                            triggerEtaModal = true;
                            updateStatus();
                        };
                        interviewStatusOption.appendChild(col3);

                        var startedOption = document.createElement("span");
                        col3.appendChild(startedOption);

                        img = document.createElement("img");
                        img.src = "/assets/dashboard/img/started.svg";
                        img.setAttribute('height', '28px');
                        startedOption.appendChild(img);

                        text = document.createElement("div");
                        text.textContent = "On the way";
                        startedOption.appendChild(text);


                        //reached div
                        var col4 = document.createElement("div");
                        col4.style = "display: none; display: inline-block; margin-right: 8px; text-align: center";
                        col4.className = "statusOption";
                        col4.id = "reached_" + jobPost.jobPost.jobPostId;
                        col4.onclick = function () {
                            globalStatus = 4;
                            globalJpId = jobPost.jobPost.jobPostId;
                            triggerNotGoingModal = false;
                            triggerEtaModal = false;
                            updateStatus();
                        };
                        interviewStatusOption.appendChild(col4);

                        var reachedOption = document.createElement("span");
                        col4.appendChild(reachedOption);

                        img = document.createElement("img");
                        img.src = "/assets/dashboard/img/reached.svg";
                        img.setAttribute('height', '28px');
                        reachedOption.appendChild(img);

                        text = document.createElement("div");
                        text.textContent = "Reached";
                        reachedOption.appendChild(text);


                        if(jobPost.status.statusId != JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){
                            if(jobPost.status.statusId == JWF_STATUS_INTERVIEW_CONFIRMED || jobPost.status.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING){
                                currentStatus.textContent = "Status not Specified";
                                currentStatus.style = "font-weight: bold; margin-right: 4px; color: grey";

                                if(jobPost.status.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING){
                                    currentStatus.textContent = "Not Going";
                                    currentStatus.style = "font-weight: bold; margin-right: 4px; color: red";
                                } else{
                                    $("#not_going_" + jobPost.jobPost.jobPostId).show();
                                }

                                $("#delayed_" + jobPost.jobPost.jobPostId).show();
                                $("#started_" + jobPost.jobPost.jobPostId).show();
                                $("#reached_" + jobPost.jobPost.jobPostId).show();
                            } else if(jobPost.status.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_DELAYED){
                                currentStatus.textContent = "Delayed";
                                currentStatus.style = "font-weight: bold; margin-right: 4px; color: red";

                                $("#not_going_" + jobPost.jobPost.jobPostId).hide();
                                $("#delayed_" + jobPost.jobPost.jobPostId).hide();
                                $("#started_" + jobPost.jobPost.jobPostId).show();
                                $("#reached_" + jobPost.jobPost.jobPostId).show();
                            } else if(jobPost.status.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_ON_THE_WAY) {
                                currentStatus.textContent = "On the Way";
                                currentStatus.style = "font-weight: bold; margin-right: 4px; color: green";

                                $("#not_going_" + jobPost.jobPost.jobPostId).hide();
                                $("#started_" + jobPost.jobPost.jobPostId).hide();
                                $("#delayed_" + jobPost.jobPost.jobPostId).show();
                                $("#reached_" + jobPost.jobPost.jobPostId).show();
                            }
                        } else{
                            $("#status_options_" + jobPost.jobPost.jobPostId).hide();
                            statusBody.textContent = "Reached";
                            statusBody.style = "font-weight: bold; margin-right: 4px; color: green";
                        }
                    }
                }
            }

            var titleRowTwo = document.createElement("div");
            titleRowTwo.className = "row col-sm-4";
            titleRowTwo.id = "appliedOnId";
            titleRowTwo.style = "padding: 0";
            jobBodyCol.appendChild(titleRowTwo);

            var fetchedAppliedDate = jobPost.creationTimestamp;

            var divAppliedDate = document.createElement("div");
            divAppliedDate.className = "appliedDate";
            divAppliedDate.style = "margin-top: 12px";
            divAppliedDate.textContent = "Last Update: " + new Date(fetchedAppliedDate).getDate() + "/" + (new Date(fetchedAppliedDate).getMonth() + 1) + "/" + new Date(fetchedAppliedDate).getFullYear();
            titleRowTwo.appendChild(divAppliedDate);
        }

/*
        var appliedJob = $("#apply_btn_" + jobPost.jobPost.jobPostId);
        appliedJob.addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied");
        appliedJob.attr('onclick','').unbind('click');
*/
    });

    $("#myAppliedJobsPendingConfirmation").hide();
    $("#noPendingConfirmationApplication").hide();
    $("#myAppliedJobsConfirmed").hide();
    $("#noConfirmedApplication").hide();
    $("#myAppliedJobsCompleted").hide();
    $("#noCompletedApplication").hide();

    if(todayInterview){
        if(parentPendingConfirmationCount == 0){
            $("#noPendingConfirmationApplication").show();
            $("#myAppliedJobsPendingConfirmation").hide();
        } else{
            $("#myAppliedJobsPendingConfirmation").show();
            $("#noPendingConfirmationApplication").hide();
        }
    }

    if(interviewsTodayCount > 0){
        tabTwo();
    } else if(rescheduledCount > 0){
        tabOne();
    } else if(confirmedInterviewCount > 0){
        tabTwo();
    }
}

function confirmUpdateStatusNotGoing(){
    if($("#notGoingReason").val() > 0){
        updateStatus();
    } else{
        alert("Please select a reason for not going for interview");
    }
}

function updateStatus() { //updating current status
    var notGoingReason = 0;
    if($("#notGoingReason").val() != null && $("#notGoingReason").val() != 0){
        notGoingReason = $("#notGoingReason").val();
    }

    confirmCandidateStatusUpdate(notGoingReason);
}

function confirmCandidateStatusUpdate(notGoingReason){
    try {
        $.ajax({
            type: "POST",
            url: "/updateStatus/" + localStorage.getItem("candidateId") + "/" + globalJpId + "/" + globalStatus + "/" + notGoingReason,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForUpdateStatus
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function confirmInterview(jpId, status) {
    globalJpId = jpId;
    globalInterviewStatus = status;
    try {
        $.ajax({
            type: "POST",
            url: "/confirmInterview/" + parseInt(jpId) + "/" + status,
            async: true,
            contentType: false,
            data: false,
            success: processDataConfirmInterview
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
}

function processDataConfirmInterview(returnedData) {
    if(returnedData != 0){
        $("#interview_status_val_" + globalJpId).remove();
        var divInterviewStatus = document.createElement("span");
        var dir = document.createElement("span");

        divInterviewStatus.className = "appliedDate";
        divInterviewStatus.id = "interview_status_val_" + globalJpId;
        if(globalInterviewStatus == 1){
            alert("Job application accepted");
            divInterviewStatus.textContent = rescheduledDate;
            divInterviewStatus.style = "color: green; font-weight: 600";
            if(globalLat != null){

                //show directions button only if job post latitude and longitude is available
                dir.className = "navigationBtn";
                dir.textContent = "Directions";
                dir.onclick = function () {

                    //checking if candidate home locality is available. If yes, show the source and destination, else just the destination
                    if(candidateLat != null){
                        window.open('https://www.google.com/maps/dir/' + candidateLat + ', ' + candidateLng + '/'+ globalLat + ', ' + globalLng);
                    } else{
                        window.open('http://maps.google.com/?q='+ globalLat +',' + globalLng);
                    }
                };
            }

        } else {
            alert("Job application rejected");
            divInterviewStatus.textContent = "Job Application declined by the Candidate";
            divInterviewStatus.style = "color: red; font-weight: 600";
        }

        $("#interview_status_div_" + globalJpId).append(divInterviewStatus);
        $("#interview_status_div_" + globalJpId).append(dir);
    } else{
        alert("Something went wrong. Please try again later");
    }
}


function confirmUpdateStatusNotGoing(){
    if($("#notGoingReason").val() > 0){
        triggerEtaModal = false;
        triggerNotGoingModal = false;
        confirmCandidateStatusUpdate($("#notGoingReason").val());
    } else{
        alert("Please select an option");
    }
}

function processDataForUpdateStatus(returnedData) {
    $("#notGoingModal").modal("hide");
    if(returnedData == 1){
        if(triggerNotGoingModal){
            $('#notGoingReason').html('');
            var defaultOption = $('<option value="0"></option>').text("Select a reason");
            $('#notGoingReason').append(defaultOption);

            allReasons.forEach(function (reason) {
                var id = reason.id;
                var name = reason.name;
                var option = $('<option value=' + id + '></option>').text(name);
                $('#notGoingReason').append(option);
            });
            $("#notGoingModal").modal("show");
            $("#heading").html("Reason for not Going");
            $("#subHeading").html("Reason for not going for the interview");

        } else if(triggerEtaModal){
            $('#notGoingReason').html('');
            var defaultOption = $('<option value="0"></option>').text("Select an option");
            $('#notGoingReason').append(defaultOption);

            allEta.forEach(function (reason) {
                var id = reason.id;
                var name = reason.name;
                var option = $('<option value=' + id + '></option>').text(name);
                $('#notGoingReason').append(option);
            });
            $("#notGoingModal").modal("show");
            $("#heading").html("Reaching in?");
            $("#subHeading").html("Reaching the interview location in?");

        } else{
            alert("Status updated successfully");
            getAllAppliedJobs();
        }
    } else{
        alert("Something went wrong. Please try again later");
    }
}

function createAndAppendDivider(title) {
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

function processDataAllJobPosts(returnedData) {
    var jobPostCount = Object.keys(returnedData).length;
    $("#partnerLoader").hide();
    if(jobPostCount > 0){
        var count = 0;
        var parent = $("#hotJobs");
        //returnedData.reverse();
        createAndAppendDivider("Popular Jobs");
        var isDividerPresent = false;

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

        returnedData.forEach(function (jobPost){
            count++;
            if(count){
                //!* get all localities of the jobPost *!/
                var jobLocality = jobPost.jobPostToLocalityList;
                var localities = "";
                var allLocalities = "";
                var loopCount = 0;

                if(jobPost.source != null && jobPost.source > 0 && !isDividerPresent){
                    createAndAppendDivider("Other Jobs");
                    isDividerPresent = true;
                }

                jobLocality.forEach(function (locality) {
                    loopCount ++;
                    if(loopCount > 2){
                        return false;
                    } else{
                        var name = locality.locality.localityName;
                        localities += name;
                        if(loopCount < Object.keys(jobLocality).length){
                            localities += ", ";
                        }
                    }
                });
                loopCount = 0;
                jobLocality.forEach(function (locality) {
                    loopCount++;
                    var name = locality.locality.localityName;
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
                jobBodySubRowCol.style = "padding: 0";
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
                if(jobPost.jobPostMaxSalary == "0"){
                    salaryDiv.textContent = rupeeFormatSalary(jobPost.jobPostMinSalary) + " monthly";
                } else{
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
                jobBodySubRowColExp.style = "padding: 0";
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
                expDiv.textContent = validateExperience(jobPost.jobPostExperience);
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
                jobBodySubRowColLoc.style = "padding: 0";
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
                interviewIncentiveRowCol.style = "padding: 0";
                interviewIncentiveRow.appendChild(interviewIncentiveRowCol);

                var incentiveIconDiv = document.createElement("span");
                incentiveIconDiv.style = "display : inline-block;top:0";
                interviewIncentiveRowCol.appendChild(incentiveIconDiv);

                var incentiveIcon = document.createElement("img");
                incentiveIcon.src = "/assets/partner/img/coin.png";
                incentiveIcon.setAttribute('height', '20px');
                incentiveIcon.style = "margin: -4px 0 0 -5px";
                incentiveIconDiv.appendChild(incentiveIcon);

                var interviewIncentiveVal = document.createElement("span");
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
                joiningIncentiveRowCol.style = "padding: 0";
                joiningIncentiveRow.appendChild(joiningIncentiveRowCol);

                incentiveIconDiv = document.createElement("span");
                incentiveIconDiv.style = "display : inline-block;top:0";
                joiningIncentiveRowCol.appendChild(incentiveIconDiv);

                incentiveIcon = document.createElement("img");
                incentiveIcon.src = "/assets/partner/img/coin.png";
                incentiveIcon.setAttribute('height', '20px');
                incentiveIcon.style = "margin: -4px 0 0 -5px";
                incentiveIconDiv.appendChild(incentiveIcon);

                var joiningIncentiveVal = document.createElement("span");
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

                //!*  apply button *!/
                var applyBtnDiv = document.createElement("div");
                applyBtnDiv.className = "col-sm-2";
                applyBtnDiv.id = "applyBtnDiv_" + jobPost.jobPostId;
                rowDiv.appendChild(applyBtnDiv);

                var applyBtn = document.createElement("div");
                applyBtn.className = "jobApplyBtn";
                applyBtn.id = "apply_btn_" + jobPost.jobPostId;
                var applyJobText ;

                if(jobPost.applyBtnStatus != null && jobPost.applyBtnStatus != CTA_BTN_APPLY){
                    console.log("jobPost.applyBtnStatus : "+ jobPost.applyBtnStatus);
                    if(jobPost.applyBtnStatus == CTA_BTN_INTERVIEW_REQUIRED) {
                        applyJobText = "Book Interview";
                    } else if(jobPost.applyBtnStatus == CTA_BTN_DEACTIVE){
                        console.log("cta deactivated");
                        applyJobText = "Apply";
                        applyBtn.style = "background:#ffa726";
                        applyBtn.onclick = function () {
                            jobCardUtil.method.notifyMsg(jobCardUtil.deActivationMessage, 'error');
                        };
                    } else if(jobPost.applyBtnStatus == CTA_BTN_ALREADY_APPLIED) {
                        applyJobText = "Applied";
                        applyBtn.disabled =  true;
                        applyBtn.style = "cursor: default; background: #ffa726";
                    } else if(jobPost.applyBtnStatus == CTA_BTN_INTERVIEW_CLOSED) {
                        applyJobText = "Application closed";
                        applyBtn.disabled =  true;
                        applyBtn.style = "cursor: default; background: #ffa726";
                    }
                } else {
                    applyJobText = "Apply";
                }

                applyBtn.textContent = applyJobText;
                applyBtnDiv.appendChild(applyBtn);
                if(jobPost.applyBtnStatus != CTA_BTN_ALREADY_APPLIED
                    && jobPost.applyBtnStatus != CTA_BTN_DEACTIVE
                    && jobPost.applyBtnStatus != CTA_BTN_INTERVIEW_CLOSED){

                    applyBtn.onclick = function () {
                        $('#jobApplyConfirm').modal();
                        jobPostId = jobPost.jobPostId;
                        jobLocalityArray = [];
                        addLocalitiesToModal(jobPostId);
                    };
                }

                var infoBtn = document.createElement("div");
                infoBtn.className = "jobInfoBtn";
                infoBtn.textContent = "Job info";
                infoBtn.id = "info_btn_" + jobPost.jobPostId;
                applyBtnDiv.appendChild(infoBtn);
                infoBtn.onclick = function () {
                    $("#job_detail_view_" + jobPost.jobPostId).slideToggle(300);
                    if($(this).text() == 'Hide Info'){
                        $(this).text('Job Info');
                    } else {
                        $(this).text('Hide Info');
                    }
                };

                var helpText = document.createElement("div");
                if(jobPost.applyBtnStatus == CTA_BTN_INTERVIEW_CLOSED) {
                    helpText.textContent = "Will reopen on " + day + "-" + month + "-" + nextMonday.getFullYear();
                }

                helpText.style = "font-size: 11px";
                applyBtnDiv.appendChild(helpText);


                // job post info view
                var jobDetailDiv = document.createElement("div");
                jobDetailDiv.id = "job_detail_view_" + jobPost.jobPostId;
                jobDetailDiv.className = "jobDetailCardView";
                parent.append(jobDetailDiv);

                var jobDetailHeading = document.createElement("h4");
                jobDetailHeading.textContent = "Job Details";
                jobDetailHeading.style = "font-weight: 600";
                jobDetailDiv.appendChild(jobDetailHeading);

                var jobDetailBody = document.createElement("div");
                jobDetailBody.className = "row";
                jobDetailBody.style = "margin-top: 16px";
                jobDetailDiv.appendChild(jobDetailBody);

                var jobDetailColOne = document.createElement("div");
                jobDetailColOne.className = "col-sm-3";
                jobDetailBody.appendChild(jobDetailColOne);

                // job post time shift
                var jobPostWorkShift = document.createElement("div");
                jobPostWorkShift.style = "display : inline-block; margin: 4px";
                jobDetailColOne.appendChild(jobPostWorkShift);

                var jobShiftIcon = document.createElement("img");
                jobShiftIcon.src = "/assets/common/img/details/time-passing.svg";
                jobShiftIcon.setAttribute('height', '16px');
                jobShiftIcon.style = "margin-top: -4px; display : inline-block";
                jobPostWorkShift.appendChild(jobShiftIcon);

                var jobShiftVal = document.createElement("div");
                jobShiftVal.setAttribute('height', '16px');
                jobShiftVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if(jobPost.jobPostShift != null){
                    jobShiftVal.textContent = "Shift: " + jobPost.jobPostShift.timeShiftName;
                } else{
                    jobShiftVal.textContent = "Time shift not specified";
                }
                jobPostWorkShift.appendChild(jobShiftVal);

                var jobDetailColTwo = document.createElement("div");
                jobDetailColTwo.className = "col-sm-3";
                jobDetailBody.appendChild(jobDetailColTwo);

                // job post time shift
                var jobPostEducation = document.createElement("div");
                jobPostEducation.style = "display : inline-block; margin: 4px";
                jobDetailColTwo.appendChild(jobPostEducation);

                var jobEducationIcon = document.createElement("img");
                jobEducationIcon.src = "/assets/common/img/details/science-book.svg";
                jobEducationIcon.setAttribute('height', '16px');
                jobEducationIcon.style = "margin-top: -4px; display : inline-block";
                jobPostEducation.appendChild(jobEducationIcon);

                var jobEducationVal = document.createElement("div");
                jobEducationVal.setAttribute('height', '16px');
                jobEducationVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if(jobPost.jobPostEducation != null){
                    jobEducationVal.textContent = "Education " + jobPost.jobPostEducation.educationName;
                } else{
                    jobEducationVal.textContent = "Education not specified";
                }
                jobPostEducation.appendChild(jobEducationVal);

                var jobDetailColThree = document.createElement("div");
                jobDetailColThree.className = "col-sm-3";
                jobDetailBody.appendChild(jobDetailColThree);

                // job post working hours
                var jobPostWorkTimings = document.createElement("div");
                jobPostWorkTimings.style = "display : inline-block; margin: 4px";
                jobDetailColThree.appendChild(jobPostWorkTimings);

                var jobWorkTimingsIcon = document.createElement("img");
                jobWorkTimingsIcon.src = "/assets/common/img/details/calendar.svg";
                jobWorkTimingsIcon.setAttribute('height', '16px');
                jobWorkTimingsIcon.style = "margin-top: -4px; display : inline-block";
                jobPostWorkTimings.appendChild(jobWorkTimingsIcon);

                var jobWorkTimingsVal = document.createElement("div");
                jobWorkTimingsVal.setAttribute('height', '16px');
                jobWorkTimingsVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.jobPostStartTime != null && jobPost.jobPostEndTime != null) {
                    var valStart;
                    var valEnd;
                    if (jobPost.jobPostStartTime > 12) {
                        jobPost.jobPostStartTime = jobPost.jobPostStartTime - 12;
                        valStart = "PM";
                    }
                    else {
                        valStart = "AM";
                    }
                    if (jobPost.jobPostEndTime > 12) {
                        jobPost.jobPostEndTime = jobPost.jobPostEndTime - 12;
                        valEnd = "PM";
                    }
                    else {
                        valEnd = "AM";
                    }
                    jobWorkTimingsVal.textContent = jobPost.jobPostStartTime + " " + valStart + " - " + jobPost.jobPostEndTime + " " + valEnd;

                } else{
                    jobWorkTimingsVal.textContent = "Job timing not specified";
                }
                jobPostWorkTimings.appendChild(jobWorkTimingsVal);

                var jobDetailColFour = document.createElement("div");
                jobDetailColFour.className = "col-sm-3";
                jobDetailBody.appendChild(jobDetailColFour);

                // job post holidays
                var jobPostHolidays = document.createElement("div");
                jobPostHolidays.style = "display : inline-block; margin: 4px";
                jobDetailColFour.appendChild(jobPostHolidays);

                var jobHolidaysIcon = document.createElement("img");
                jobHolidaysIcon.src = "/assets/common/img/details/calendar.svg";
                jobHolidaysIcon.setAttribute('height', '16px');
                jobHolidaysIcon.style = "margin-top: -4px; display : inline-block";
                jobPostHolidays.appendChild(jobHolidaysIcon);

                var jobHolidaysVal = document.createElement("div");
                jobHolidaysVal.setAttribute('height', '16px');
                jobHolidaysVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.jobPostWorkingDays != "" && jobPost.jobPostWorkingDays != null) {
                    var workingDays = jobPost.jobPostWorkingDays.toString(2);
                    var i;
                    /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
                    if (workingDays.length != 7) {
                        var x = 7 - workingDays.length;
                        var modifiedWorkingDays = "";

                        for (i = 0; i < x; i++) {
                            modifiedWorkingDays += "0";
                        }
                        modifiedWorkingDays += workingDays;
                        workingDays = modifiedWorkingDays;
                    }
                    var holiday = "";
                    var arryDay = workingDays.split("");
                    if (arryDay[0] != 1) {
                        holiday += "Mon, ";
                    }
                    if (arryDay[1] != 1) {
                        holiday += "Tue, ";
                    }
                    if (arryDay[2] != 1) {
                        holiday += "Wed, ";
                    }
                    if (arryDay[3] != 1) {
                        holiday += "Thu, ";
                    }
                    if (arryDay[4] != 1) {
                        holiday += "Fri, ";
                    }
                    if (arryDay[5] != 1) {

                        holiday += "Sat, ";
                    }
                    if (arryDay[6] != 1) {
                        holiday += "Sun ";
                    }
                    jobHolidaysVal.textContent = holiday + " - Holiday";
                } else{
                    jobHolidaysVal.textContent = "Holidays not specified";
                }
                jobPostHolidays.appendChild(jobHolidaysVal);

                //second line of details
                var jobPostIncentive = document.createElement("div");
                jobPostIncentive.style = "display : inline-block; margin: 6px 12px 6px 18px";
                jobDetailDiv.appendChild(jobPostIncentive);

                var jobPostIncentiveIcon = document.createElement("img");
                jobPostIncentiveIcon.src = "/assets/common/img/details/coins.svg";
                jobPostIncentiveIcon.setAttribute('height', '16px');
                jobPostIncentiveIcon.style = "margin-top: -4px; display : inline-block";
                jobPostIncentive.appendChild(jobPostIncentiveIcon);

                var jobPostIncentiveHeading = document.createElement("div");
                jobPostIncentiveHeading.setAttribute('height', '16px');
                jobPostIncentiveHeading.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                jobPostIncentiveHeading.textContent = "Incentives:";
                jobPostIncentive.appendChild(jobPostIncentiveHeading);

                var jobPostIncentiveVal = document.createElement("div");
                jobPostIncentiveVal.setAttribute('height', '16px');
                jobPostIncentiveVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if(jobPost.jobPostIncentives != ""){
                    jobPostIncentiveVal.textContent = jobPost.jobPostIncentives;
                } else{
                    jobPostIncentiveVal.textContent = "Incentives not specified";
                }
                jobPostIncentive.appendChild(jobPostIncentiveVal);

                var breakTag = document.createElement("br");
                jobDetailDiv.appendChild(breakTag);

                //Third line of details
                var jobPostMinimumRequirement = document.createElement("div");
                jobPostMinimumRequirement.style = "display : inline-block; margin: 6px 12px 6px 18px";
                jobDetailDiv.appendChild(jobPostMinimumRequirement);

                var jobPostMinimumRequirementIcon = document.createElement("img");
                jobPostMinimumRequirementIcon.src = "/assets/common/img/details/list.svg";
                jobPostMinimumRequirementIcon.setAttribute('height', '16px');
                jobPostMinimumRequirementIcon.style = "margin-top: -4px; display : inline-block";
                jobPostMinimumRequirement.appendChild(jobPostMinimumRequirementIcon);

                var jobPostMinimumRequirementHeading = document.createElement("div");
                jobPostMinimumRequirementHeading.setAttribute('height', '16px');
                jobPostMinimumRequirementHeading.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                jobPostMinimumRequirementHeading.textContent = "Minimum Requirement:";
                jobPostMinimumRequirement.appendChild(jobPostMinimumRequirementHeading);

                var jobPostMinimumRequirementVal = document.createElement("div");
                jobPostMinimumRequirementVal.setAttribute('height', '16px');
                jobPostMinimumRequirementVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.jobPostMinRequirement != null && jobPost.jobPostMinRequirement != "") {
                    jobPostMinimumRequirementVal.textContent = jobPost.jobPostMinRequirement;
                } else{
                    jobPostMinimumRequirementVal.textContent = "Minimum requirement not specified";
                }
                jobPostMinimumRequirement.appendChild(jobPostMinimumRequirementVal);

                breakTag = document.createElement("br");
                jobDetailDiv.appendChild(breakTag);

                //Job Description
                var jobPostDescription = document.createElement("div");
                jobPostDescription.style = "display : inline-block; margin: 6px 12px 6px 18px";
                jobDetailDiv.appendChild(jobPostDescription);

                var jobPostDescriptionIcon = document.createElement("img");
                jobPostDescriptionIcon.src = "/assets/common/img/details/job_desc.svg";
                jobPostDescriptionIcon.setAttribute('height', '16px');
                jobPostDescriptionIcon.style = "margin-top: -4px; display : inline-block";
                jobPostDescription.appendChild(jobPostDescriptionIcon);

                var jobPostDescriptionHeading = document.createElement("div");
                jobPostDescriptionHeading.setAttribute('height', '16px');
                jobPostDescriptionHeading.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                jobPostDescriptionHeading.textContent = "Job Description:";
                jobPostDescription.appendChild(jobPostDescriptionHeading);

                var jobPostDescriptionVal = document.createElement("div");
                jobPostDescriptionVal.setAttribute('height', '16px');
                jobPostDescriptionVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.jobPostDescription != null && jobPost.jobPostDescription != "") {
                    jobPostDescriptionVal.style = "margin-top: -4px; display : block; margin: 4px 21px 8px 21px";

                    breakTag = document.createElement("br");
                    jobDetailDiv.appendChild(breakTag);

                    jobPostDescriptionVal.textContent = jobPost.jobPostDescription;
                } else{
                    jobPostDescriptionVal.textContent = "Job description not specified";
                }
                jobPostDescription.appendChild(jobPostDescriptionVal);

                //company details
                var companyDetailHeading = document.createElement("h4");
                companyDetailHeading.textContent = "Company Details";
                companyDetailHeading.style = "font-weight: 600";
                jobDetailDiv.appendChild(companyDetailHeading);

                var companyDetailBody = document.createElement("div");
                companyDetailBody.className = "row";
                companyDetailBody.style = "margin-top: 16px";
                jobDetailDiv.appendChild(companyDetailBody);

                var companyDetailColOne = document.createElement("div");
                companyDetailColOne.className = "col-sm-3";
                companyDetailBody.appendChild(companyDetailColOne);

                //company location
                var companyLocation = document.createElement("div");
                companyLocation.style = "display : inline-block; margin: 4px";
                companyDetailColOne.appendChild(companyLocation);

                var companyLocationIcon = document.createElement("img");
                companyLocationIcon.src = "/assets/common/img/details/buildings.svg";
                companyLocationIcon.setAttribute('height', '16px');
                companyLocationIcon.style = "margin-top: -4px; display : inline-block";
                companyLocation.appendChild(companyLocationIcon);

                var companyLocationVal = document.createElement("div");
                companyLocationVal.setAttribute('height', '16px');
                companyLocationVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.company.companyLocality != null) {
                    companyLocationVal.textContent = "Location: " + jobPost.company.companyLocality.localityName;
                } else{
                    companyLocationVal.textContent = "Company location not specified";
                }
                companyLocation.appendChild(companyLocationVal);

                var companyDetailColTwo = document.createElement("div");
                companyDetailColTwo.className = "col-sm-3";
                companyDetailBody.appendChild(companyDetailColTwo);

                //company type
                var companyType = document.createElement("div");
                companyType.style = "display : inline-block; margin: 4px";
                companyDetailColTwo.appendChild(companyType);

                var companyTypeIcon = document.createElement("img");
                companyTypeIcon.src = "/assets/common/img/details/group.svg";
                companyTypeIcon.setAttribute('height', '16px');
                companyTypeIcon.style = "margin-top: -4px; display : inline-block";
                companyType.appendChild(companyTypeIcon);

                var companyTypeVal = document.createElement("div");
                companyTypeVal.setAttribute('height', '16px');
                companyTypeVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.company.compType != null) {
                    companyTypeVal.textContent = "Company type: " + jobPost.company.compType.companyTypeName;
                } else{
                    companyTypeVal.textContent = "Company type not specified";
                }
                companyType.appendChild(companyTypeVal);

                var companyDetailColThree = document.createElement("div");
                companyDetailColThree.className = "col-sm-3";
                companyDetailBody.appendChild(companyDetailColThree);

                //company website
                var companyWebsite = document.createElement("div");
                companyWebsite.style = "display : inline-block; margin: 4px";
                companyDetailColThree.appendChild(companyWebsite);

                var companyWebsiteIcon = document.createElement("img");
                companyWebsiteIcon.src = "/assets/common/img/details/internet.svg";
                companyWebsiteIcon.setAttribute('height', '16px');
                companyWebsiteIcon.style = "margin-top: -4px; display : inline-block";
                companyWebsite.appendChild(companyWebsiteIcon);

                var companyWebsiteVal = document.createElement("div");
                companyWebsiteVal.setAttribute('height', '16px');
                companyWebsiteVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.company.companyWebsite != null && jobPost.company.companyWebsite != "") {
                    companyWebsiteVal.textContent = jobPost.company.companyWebsite;
                } else{
                    companyWebsiteVal.textContent = "Company type not specified";
                }
                companyWebsite.appendChild(companyWebsiteVal);

                //Job Description
                var companyDescription = document.createElement("div");
                companyDescription.style = "display : inline-block; margin: 6px 12px 6px 18px";
                jobDetailDiv.appendChild(companyDescription);

                var companyDescriptionIcon = document.createElement("img");
                companyDescriptionIcon.src = "/assets/common/img/details/list.svg";
                companyDescriptionIcon.setAttribute('height', '16px');
                companyDescriptionIcon.style = "margin-top: -4px; display : inline-block";
                companyDescription.appendChild(companyDescriptionIcon);

                var companyDescriptionHeading = document.createElement("div");
                companyDescriptionHeading.setAttribute('height', '16px');
                companyDescriptionHeading.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                companyDescriptionHeading.textContent = "Company Description:";
                companyDescription.appendChild(companyDescriptionHeading);

                var companyDescriptionVal = document.createElement("div");
                companyDescriptionVal.setAttribute('height', '16px');
                companyDescriptionVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.company.companyDescription != null && jobPost.company.companyDescription != "") {
                    companyDescriptionVal.style = "margin-top: -4px; display : block; margin: 4px 21px 8px 21px";

                    breakTag = document.createElement("br");
                    jobDetailDiv.appendChild(breakTag);

                    companyDescriptionVal.textContent = jobPost.company.companyDescription;
                } else{
                    companyDescriptionVal.textContent = "Company description not specified";
                }
                companyDescription.appendChild(companyDescriptionVal);

            }
        });
    }
    //getting all the applied jobs
    getAllAppliedJobs();
    getCandidateInfo();
}

openPartnerPreScreenModal = function (jobPostId, candidateId) {
    // actorId defined which modal to display
    globalPalette.color.main.headerColor= "#26A69A";
    var decoratorPromise = new Promise( function(resolve, reject) {
                resolve(initDecorator(globalPalette));
        });
    decoratorPromise.then(function (decorator) {
        decorator.columnVisible = [1,2,3,4,6];

        // display only Min Requirement
        decorator.textContainers.noteContainer.visibility = false;
        decorator.textContainers.minReqContainer.className = "col-lg-12 form-group remove-padding-left";

        // remove callConnected
        decorator.callYesNoRequired = false;
        if(jobRoleName != null && companyName!= null){
            decorator.preScreen.title = "Job Application Form: "+jobRoleName+" @ "+companyName;
        } else {
            decorator.preScreen.title = "Job Application Form"
        }
        decorator.table.mainTable.title = "Job Requirements : Please verify and update candidate's details ";
        decorator.table.mainTable.minReqTable.className = "mdl-grid--no-spacing";
        decorator.finalSubmissionButton.className = "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent modal-submit";
        decorator.table.otherTable.title = "Job Details: ";
        decorator.textContainers.minReqContainer.title = "Other Requirements";
        decorator.edit.title = "Update Info";
        decorator.finalSubmissionButton.enable = true;


        // footerMessage
        decorator.modalFooter.footerMessage = "I confirm and accept T&C";

        var isSupport = false;
        if( !decorator.callYesNoRequired) {
            getPreScreenContent(jobPostId, candidateId, false, decorator, false, isSupport);
        }
    }, function (err) {
        console.log(err);
    });
};

function getCandidateInfo() {
    try {
        $.ajax({
            url: "/checkPartnerCandidate/" + localStorage.getItem("candidateId"),
            type: "POST",
            async: false,
            data: false,
            dataType: "json",
            contentType: false,
            processData: false,
            success: processDataGetCandidateInfo
        });
    } catch (exception) {}
}

function processDataGetCandidateInfo(returnedData){
    candidateInfo = returnedData;
    document.getElementById("userName").innerHTML = returnedData.candidateFirstName;
    document.getElementById("userMobile").innerHTML = returnedData.candidateMobile;
    if (returnedData.candidateDOB != null) {
        var date = JSON.parse(returnedData.candidateDOB);
        var yr = new Date(date).getFullYear();
        var month = ('0' + parseInt(new Date(date).getMonth() + 1)).slice(-2);
        var d = ('0' + new Date(date).getDate()).slice(-2);
        var today = new Date();
        var birthDate = new Date(yr + "-" + month + "-" + d);
        var age = today.getFullYear() - birthDate.getFullYear();
        var m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        document.getElementById("userAge").innerHTML = ", " + age + " yrs";
    }
    if (returnedData.candidateGender != null) {
        if (returnedData.candidateGender == 0) {
            try{
                document.getElementById("userGender").innerHTML = ", Male";
                $("#userImgPartner").attr('src', '/assets/dashboard/img/userMale.svg');
            } catch(err){}
        } else {
            try{
                document.getElementById("userGender").innerHTML = ", Female";
                $("#userImg").attr('src', '/assets/dashboard/img/userFemale.svg');
            } catch(err){}
        }
    }

    try {
        var jobRoles = "";
        var count = 0;
        var jobPref = returnedData.jobPreferencesList;
        if(jobPref.length > 0){
            jobPref.forEach(function (job){
                count ++;
                var name = job.jobRole.jobName;
                jobRoles += name;
                if(count < Object.keys(jobPref).length){
                    jobRoles += ", ";
                }
            });
            document.getElementById("userJobs").innerHTML = jobRoles;
        }
    } catch(err){
        console.log(err);
    }
    try {
        if(returnedData.locality != null){
            document.getElementById("userLocality").innerHTML = returnedData.locality.localityName;
        }
    } catch(err){
        console.log("getCandidateLocalityPref error"+err);
    }

    /* Time Shift */
    if (returnedData.timeShiftPreference != null) {
        document.getElementById("userShift").innerHTML = returnedData.timeShiftPreference.timeShift.timeShiftName;
        if(returnedData.timeShiftPreference.timeShift.timeShiftId == 5){
            document.getElementById("userShift").innerHTML = returnedData.timeShiftPreference.timeShift.timeShiftName + " Shift";
        }
    }

    /* candidate Education */
    try{
        if(returnedData.candidateEducation.education != null) {
            document.getElementById("userEducationLevel").innerHTML = returnedData.candidateEducation.education.educationName;
        }
    } catch(err){}

    /* Work Experience */
    if(returnedData.candidateTotalExperience != null){
        if(returnedData.candidateTotalExperience == 0) {
            document.getElementById("userTotalExperience").innerHTML = "Fresher";
            document.getElementById("userCurrentCompany").innerHTML = "Not Applicable";
        }
        else {
            var totalExperience = parseInt(returnedData.candidateTotalExperience);
            var yrs = parseInt((totalExperience / 12)).toString();
            var month = totalExperience % 12;
            if(yrs == 0 && month != 0){
                document.getElementById("userTotalExperience").innerHTML = month + " months";
            } else if(month == 0 && yrs != 0){
                document.getElementById("userTotalExperience").innerHTML = yrs + " years";

            } else{
                document.getElementById("userTotalExperience").innerHTML = yrs + " yrs and " + month + " mnths";
            }

            /* Current Company and Salary */
            if (Object.keys(returnedData.jobHistoryList).length > 0) {
                returnedData.jobHistoryList.forEach(function (pastJob) {
                    if(pastJob.currentJob == true){
                        if(pastJob.candidatePastCompany != null){
                            document.getElementById("userCurrentCompany").innerHTML = pastJob.candidatePastCompany;
                        } else{
                            document.getElementById("userCurrentCompany").innerHTML = "Not Specified";
                        }
                        return false;
                    }
                });
            }

            if(returnedData.candidateLastWithdrawnSalary != null){
                if(returnedData.candidateLastWithdrawnSalary == "0"){
                    document.getElementById("userCurrentSalary").innerHTML = "Not Applicable";
                } else{
                    document.getElementById("userCurrentSalary").innerHTML = "&#x20B9;" + returnedData.candidateLastWithdrawnSalary + "/month";
                }
            }
        }
    }

    //candidate Lat/lng
    if(returnedData.locality != null){
        if(returnedData.locality.lat != null){
            candidateLat = returnedData.locality.lat;
            candidateLng = returnedData.locality.lng;
        }
    }
}

function addLocalitiesToModal(jobPostId) {
    try {
        $.ajax({
            type: "POST",
            url: "/getJobPostInfo/" + jobPostId + "/0",
            data: false,
            contentType: false,
            processData: false,
            success: processDataForJobPostLocation
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataForJobPostLocation(returnedData) {
    $("#applyButton").hide();
    document.getElementById("applyJobCandidateName").innerHTML = candidateInfo.candidateFirstName;
    document.getElementById("applyJobCandidateNameSecond").innerHTML = candidateInfo.candidateFirstName;
    $("#jobNameConfirmation").html(returnedData.jobPostTitle);
    $("#companyNameConfirmation").html(returnedData.company.companyName);
    var i;
    $('#jobLocality').html('');
    var defaultOption = $('<option value="-1"></option>').text("Select Preferred Location");
    $('#jobLocality').append(defaultOption);
    var jobLocality = returnedData.jobPostToLocalityList;
    jobLocality.forEach(function (locality) {
        var item = {};
        item ["id"] = locality.locality.localityId;
        item ["name"] = " " + locality.locality.localityName;
        jobLocalityArray.push(item);
        var option = $('<option value=' + locality.locality.localityId + '></option>').text(locality.locality.localityName);
        $('#jobLocality').append(option);
    });
    // if (Object.keys(returnedData.interviewDetailsList).length > 0) {
    //     //slots
    //     $('#interviewSlot').html('');
    //     var defaultOption = $('<option value="-1"></option>').text("Select Time Slot");
    //     $('#interviewSlot').append(defaultOption);
    //
    //     var interviewDetailsList = returnedData.interviewDetailsList;
    //     if (interviewDetailsList[0].interviewDays != null) {
    //         var interviewDays = interviewDetailsList[0].interviewDays.toString(2);
    //
    //         /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
    //         if (interviewDays.length != 7) {
    //             x = 7 - interviewDays.length;
    //             var modifiedInterviewDays = "";
    //
    //             for (i = 0; i < x; i++) {
    //                 modifiedInterviewDays += "0";
    //             }
    //             modifiedInterviewDays += interviewDays;
    //             interviewDays = modifiedInterviewDays;
    //         }
    //     }
    //     //slots
    //     var today = new Date();
    //     for (i = 2; i < 9; i++) {
    //         // 0 - > sun 1 -> mon ...
    //         var x = new Date(today.getFullYear(), today.getMonth(), today.getDate() + i);
    //         if (checkSlotAvailability(x, interviewDays)) {
    //             interviewDetailsList.forEach(function (timeSlot) {
    //                 var dateSlotSelectedId = x.getFullYear() + "-" + (x.getMonth() + 1) + "-" + x.getDate() + "_" + timeSlot.interviewTimeSlot.interviewTimeSlotId;
    //                 var option = $('<option value="' + dateSlotSelectedId + '"></option>').text(getDayVal(x.getDay()) + ", " + x.getDate() + " " + getMonthVal((x.getMonth() + 1)) + " (" + timeSlot.interviewTimeSlot.interviewTimeSlotName + ")");
    //                 $('#interviewSlot').append(option);
    //             });
    //         }
    //     }
    //     $('#interviewSection').show();
    // } else{
    //     $('#interviewSection').hide();
    // }
}

function validateExperience(jobPostExperience) {
    if(jobPostExperience == null){
        return "Exp: Not Specified";
    } else {
        return "Exp: " + jobPostExperience.experienceType;
    }
}

function getDayVal(month){
    switch(month) {
        case 0:
            return "Sun";
            break;
        case 1:
            return "Mon";
            break;
        case 2:
            return "Tue";
            break;
        case 3:
            return "Wed";
            break;
        case 4:
            return "Thu";
            break;
        case 5:
            return "Fri";
            break;
        case 6:
            return "Sat";
            break;
    }
}

function getMonthVal(month){
    switch(month) {
        case 1:
            return "Jan";
            break;
        case 2:
            return "Feb";
            break;
        case 3:
            return "Mar";
            break;
        case 4:
            return "Apr";
            break;
        case 5:
            return "May";
            break;
        case 6:
            return "Jun";
            break;
        case 7:
            return "Jul";
            break;
        case 8:
            return "Aug";
            break;
        case 9:
            return "Sep";
            break;
        case 10:
            return "Oct";
            break;
        case 11:
            return "Nov";
            break;
        case 12:
            return "Dec";
            break;
    }
}

function checkSlotAvailability(x, interviewDays) {
    if(x.getDay() == 1 && interviewDays.charAt(0) == '1'){ // monday
        return true;
    } else if(x.getDay() == 2 && interviewDays.charAt(1) == '1'){ //tue
        return true;
    } else if(x.getDay() == 3 && interviewDays.charAt(2) == '1'){ //wed
        return true;
    } else if(x.getDay() == 4 && interviewDays.charAt(3) == '1'){ //thu
        return true;
    } else if(x.getDay() == 5 && interviewDays.charAt(4) == '1'){ //fri
        return true;
    } else if(x.getDay() == 6 && interviewDays.charAt(5) == '1'){ //sat
        return true;
    } else if(x.getDay() == 0 && interviewDays.charAt(6) == '1'){ //sun
        return true;
    }
}

function confirmApply() {
    $("#applyButton").addClass("jobApplied").removeClass("jobApplyBtnModal").prop('disabled',true).html("Applying");
    // checking if the candidate exists + if the partner has created this particular candidate or not
    var candidateId = localStorage.getItem("candidateId");
    try {
        $.ajax({
            type: "POST",
            url: "/checkPartnerCandidate/" + candidateId,
            data: false,
            contentType: false,
            processData: false,
            success: function (returnedData) {
                processDataCheckCandidate(returnedData, candidateId)
            }
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataCheckCandidate(returnedData, candidateId) {
    if(returnedData != '0'){
        applyJobSubmit(jobPostId, candidateId, returnedData.candidateMobile, prefLocation, prefTimeSlot, scheduledInterviewDate, true);
    } else{
        //Partner doesn't own the candidate
        window.location = "/partner/myCandidates";
    }
    $("#apply_btn_" + jobPostId).addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied").click(false);
    $("#applyBtnDiv_" + jobPostId).prop('disabled',true).click(false);
}

$(function() {
    $("#jobLocality").change(function (){
        prefLocation = $(this).val();
        prefLocationName = $("#jobLocality option:selected").text();
        $("#applyButton").show();

    });

    // $("#interviewSlot").change(function (){
    //     if($(this).val() != -1 && $("#jobLocality").val() != -1){
    //         var combinedValue = $(this).val().split("_");
    //         scheduledInterviewDate = combinedValue[0];
    //         prefTimeSlot = combinedValue[1];
    //
    //         prefLocation = $("#jobLocality").val();
    //         prefLocationName = $("#jobLocality option:selected").text();
    //         $("#applyButton").show();
    //     } else{
    //         $("#applyButton").hide();
    //     }
    // });
});

function confirmInterview(jpId, status) {
    globalJpId = jpId;
    globalInterviewStatus = status;
    try {
        $.ajax({
            type: "POST",
            url: "/partnerConfirmInterview/" + localStorage.getItem("candidateId") + "/" + parseInt(jpId) + "/" + status,
            async: true,
            contentType: false,
            data: false,
            success: processDataConfirmInterview
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
}

function processDataConfirmInterview(returnedData) {
    if(returnedData != 0){
        $("#status_" + globalJpId).html('');
        var divInterviewStatus = document.createElement("span");
        divInterviewStatus.id = "status_val_" + globalJpId;
        if(globalInterviewStatus == 1){
            alert("Job application accepted");
            divInterviewStatus.textContent = "Interview Accepted";
        } else {
            alert("Job application rejected");
            divInterviewStatus.textContent = "Interview recjected by Candidate/Partner";
        }

        $("#status_" + globalJpId).append(divInterviewStatus);
    } else{
        alert("Something went wrong. Please try after sometime")
    }
}

function navigateToLocation(lat, lng){
    window.open('http://maps.google.com/?q='+ lat +',' + lng);
}

function tabOne() {
    $("#tabOne").addClass("activeTab");
    $("#tabTwo").removeClass("activeTab");
    $("#tabThree").removeClass("activeTab");

    $("#myAppliedJobsConfirmed").hide();
    if(parentPendingConfirmationCount > 0){
        $("#myAppliedJobsPendingConfirmation").show();
        $("#noPendingConfirmationApplication").hide();
    } else{
        $("#myAppliedJobsPendingConfirmation").hide();
        $("#noPendingConfirmationApplication").show();
    }
    $("#myAppliedJobsConfirmed").hide();
    $("#myAppliedJobsCompleted").hide();

    //hiding no application msg
    $("#noConfirmedApplication").hide();
    $("#noCompletedApplication").hide();
}

function tabTwo() {
    $("#tabOne").removeClass("activeTab");
    $("#tabTwo").addClass("activeTab");
    $("#tabThree").removeClass("activeTab");

    if(parentConfirmedCount > 0){
        $("#myAppliedJobsConfirmed").show();
        $("#noConfirmedApplication").hide();
    } else{
        $("#myAppliedJobsConfirmed").hide();
        $("#noConfirmedApplication").show();
    }
    $("#myAppliedJobsPendingConfirmation").hide();
    $("#myAppliedJobsCompleted").hide();

    //hiding no application msg
    $("#noPendingConfirmationApplication").hide();
    $("#noCompletedApplication").hide();
}

function tabThree() {
    $("#tabOne").removeClass("activeTab");
    $("#tabTwo").removeClass("activeTab");
    $("#tabThree").addClass("activeTab");

    $("#myAppliedJobsConfirmed").hide();
    $("#myAppliedJobsPendingConfirmation").hide();

    if(parentCompletedCount > 0){
        $("#myAppliedJobsCompleted").show();
        $("#noCompletedApplication").hide();
    } else{
        $("#myAppliedJobsCompleted").hide();
        $("#noCompletedApplication").show();
    }
    $("#myAppliedJobsRejected").hide();

    //hiding no application msg
    $("#noPendingConfirmationApplication").hide();
    $("#noConfirmedApplication").hide();
}