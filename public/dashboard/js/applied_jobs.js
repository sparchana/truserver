/**
 * Created by batcoder1 on 20/6/16.
 */

var globalJpId;
var globalInterviewStatus;
var rescheduledDate;

var allReasons = [];
var allEta = [];

var parentConfirmedCount = 0;
var parentPendingConfirmationCount = 0;
var parentCompletedCount = 0;

var interviewsTodayCount = 0;
var confirmedInterviewCount = 0;
var rescheduledCount = 0;

var globalLat = null;
var globalLng = null;
var candidateLat = null;
var candidateLng = null;
var globalStatus = 0;
var triggerNotGoingModal = false;
var triggerEtaModal = false;

$(window).load(function () {
    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").fadeOut();
});

$(document).ready(function () {
    checkUserLogin();
    if(localStorage.getItem("gender") == 1){
        $("#userImg").attr('src', '/assets/dashboard/img/userFemale.svg');
    } else if(localStorage.getItem("gender") == 0){
        $("#userImg").attr('src', '/assets/dashboard/img/userMale.svg');
    }

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

    getCandidateAppliedJobs();

    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateInfoDashboard",
            data: false,
            async: true,
            contentType: false,
            processData: false,
            success: processDataAndFillMinProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    $("#notGoingModal").on('hidden.bs.modal', function () {
        location.reload();
    })

});

function getCandidateAppliedJobs() {
    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateJobApplication",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataAndFetchAppliedJobs
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

}

function processDataAndFillMinProfile(returnedData) {
    if(returnedData.locality != null){
        if(returnedData.locality.lat != null){
            candidateLat = returnedData.locality.lat;
            candidateLng = returnedData.locality.lng;
        }
    }
}

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

function processDataAndFetchAppliedJobs(returnedData) {
    var candidateJobApplication = returnedData;

    candidateJobApplication.reverse();
    prePopulateJobSection(candidateJobApplication);
}

function prePopulateJobSection(jobApplication) {
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
            jobLogo.id = "jobLogo";
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
                    divInterviewStatus.textContent = "You have scheduled your interview on " + new Date(jobPost.scheduledInterviewDate).getDate()
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

                        divInterviewStatus.textContent = "Interview confirmed on " + new Date(jobPost.scheduledInterviewDate).getDate() + "/" + (new Date(jobPost.scheduledInterviewDate).getMonth() + 1) + "/" + new Date(jobPost.scheduledInterviewDate).getFullYear() + " between " + jobPost.scheduledInterviewTimeSlot.interviewTimeSlotName;
                        divInterviewStatus.style = "color: green; font-weight: 600";
                    }

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
                var addressBody = document.createElement("div");
                var address = "";

                //computing Address
                if(Object.keys(jobPost.jobPost.interviewDetailsList).length > 0){
                    address = jobPost.jobPost.interviewDetailsList[0].interviewFullAddress;
                } else {
                    if(jobPost.jobPost.jobPostAddress != null && jobPost.jobPost.jobPostAddress != ""){
                        address = jobPost.jobPost.jobPostAddress;
                    } else {
                        address = "";
                    }
                }

                addressBody.textContent = "Interview Address : " + address;
                if(address == ""){
                    addressBody.textContent = "Interview Address : " + "Address not available";
                }

                addressBody.style = "margin-top: 8px; margin-right: 12px";
                titleRowStatus.appendChild(addressBody);
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
    });

    if(todayInterview)
    if(parentPendingConfirmationCount == 0){
        $("#noPendingConfirmationApplication").show();
        $("#myAppliedJobsPendingConfirmation").hide();
    } else{
        $("#myAppliedJobsPendingConfirmation").show();
        $("#noPendingConfirmationApplication").hide();
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
    try {
        $.ajax({
            type: "POST",
            url: "/updateStatusCandidate/" + globalJpId + "/" + globalStatus + "/" + notGoingReason,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForUpdateStatus
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataForUpdateStatus(returnedData) {
    $("#notGoingModal").modal("hide");
    if(returnedData == 1){
        //checking which modal need to be poped up, not going modal or ETA modal
        if(triggerNotGoingModal){
            triggerNotGoingModal = false;
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
            triggerEtaModal = false;
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
            //no modal poping up if status selected was reached
            alert("Status updated successfully");
            location.reload();
        }
    } else{
        alert("Something went wrong. Please try again later");
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
