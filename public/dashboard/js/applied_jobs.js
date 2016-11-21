/**
 * Created by batcoder1 on 20/6/16.
 */

var globalJpId;
var globalInterviewStatus;
var rescheduledDate;

var allReasons = [];

$(window).load(function () {
    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").delay(1000).fadeOut("slow");
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

function processDataAndFetchAppliedJobs(returnedData) {
    var candidateJobApplication = returnedData;

    if (Object.keys(candidateJobApplication).length > 0) {
        candidateJobApplication.reverse();
        prePopulateJobSection(candidateJobApplication);
    } else {
        var parent = $('#myAppliedJobs');
        var centerDiv = document.createElement("center");
        parent.append(centerDiv);
        var notAppliedImg = document.createElement("img");
        notAppliedImg.style = "width: 80px; margin-top: 6%";
        notAppliedImg.src = "/assets/dashboard/img/sadFace.png";
        centerDiv.appendChild(notAppliedImg);

        var notAppliedMsg = document.createElement("div");
        notAppliedMsg.style = "padding: 16px";
        notAppliedMsg.textContent = "Uh oh! Looks like you have not applied to any of the jobs yet!";
        centerDiv.appendChild(notAppliedMsg);
    }
}

function prePopulateJobSection(jobApplication) {
    var parentConfirmed = $('#myAppliedJobsConfirmed');
    var parentUnderReview = $('#myAppliedJobsUnderReview');
    var parentRejected = $('#myAppliedJobsRejected');

    parentConfirmed.html('');
    parentUnderReview.html('');
    parentRejected.html('');

    var centerDiv = document.createElement("center");

    var count = 0;
    jobApplication.forEach(function (jobPost) {
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

            if(jobPost.status.statusId < 6) {
                parentUnderReview.append(hotJobItem);
            } else if (jobPost.status.statusId == 6 || (jobPost.status.statusId > 9 && jobPost.status.statusId < 14)){
                parentConfirmed.append(hotJobItem);
            } else if (jobPost.status.statusId == 7 || jobPost.status.statusId == 8){
                parentRejected.append(hotJobItem);
            } else {
                parentUnderReview.append(hotJobItem);
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
            titleRowOne.className = "col-sm-9";
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


            console.log(jobPost);
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

            var titleRowThree = document.createElement("div");
            titleRowThree.className = "row col-sm-8";
            titleRowThree.id = "interview_status_div_" + jobPost.jobPost.jobPostId;
            titleRowThree.style = "margin-top: 8px; padding: 0";
            jobBodyCol.appendChild(titleRowThree);

            var divInterviewStatus = document.createElement("span");
            divInterviewStatus.className = "appliedDate";
            divInterviewStatus.id = "interview_status_val_" + jobPost.jobPost.jobPostId;

            var dir = document.createElement("span");
            if(jobPost.status.statusId < 6){
                divInterviewStatus.textContent = "Job application under review";
                divInterviewStatus.style = "color: #eb9800; font-weight: 600";
            } else{
                if(jobPost.status.statusId == 6 || (jobPost.status.statusId > 9 && jobPost.status.statusId < 14)){
                    if(jobPost.interviewLocationLat != null){
                        dir.className = "navigationBtn";
                        dir.textContent = "Directions";
                        dir.onclick = function () {
                            window.open('http://maps.google.com/?q='+ jobPost.interviewLocationLat +',' + jobPost.interviewLocationLng);
                        };

                        divInterviewStatus.textContent = "Interview confirmed on " + new Date(jobPost.scheduledInterviewDate).getDate() + "/" + (new Date(jobPost.scheduledInterviewDate).getMonth() + 1) + "/" + new Date(jobPost.scheduledInterviewDate).getFullYear() + " between " + jobPost.scheduledInterviewTimeSlot.interviewTimeSlotName;
                        divInterviewStatus.style = "color: green; font-weight: 600";
                    }

                } else if(jobPost.status.statusId == 7){
                    divInterviewStatus.textContent = "Application rejected";
                    divInterviewStatus.style = "color: red; font-weight: 600";
                } else if(jobPost.status.statusId == 8){
                    divInterviewStatus.textContent = "Application rejected by the Candidate";
                    divInterviewStatus.style = "color: red; font-weight: 600";
                } else if(jobPost.status.statusId == 9){
                    divInterviewStatus.textContent = "Interview Rescheduled on " + new Date(jobPost.scheduledInterviewDate).getDate() + "/" + (new Date(jobPost.scheduledInterviewDate).getMonth() + 1) + "/" + new Date(jobPost.scheduledInterviewDate).getFullYear() + " between " + jobPost.scheduledInterviewTimeSlot.interviewTimeSlotName;
                    divInterviewStatus.style = "color: orange; font-weight: 600";

                    // accept interview
                    var candidateInterviewAccept = document.createElement("span");
                    candidateInterviewAccept.className = "accept tooltipped";
                    candidateInterviewAccept.setAttribute("data-postiton", "top");
                    candidateInterviewAccept.setAttribute("data-delay", "50");
                    candidateInterviewAccept.setAttribute("data-tooltip", "Confirm Interview");
                    candidateInterviewAccept.onclick = function () {
                        rescheduledDate = "Interview scheduled on " + new Date(jobPost.scheduledInterviewDate).getDate() + "/" + (new Date(jobPost.scheduledInterviewDate).getMonth() + 1) + "/" + new Date(jobPost.scheduledInterviewDate).getFullYear() + " between " + jobPost.scheduledInterviewTimeSlot.interviewTimeSlotName;
                        confirmInterview(jobPost.jobPost.jobPostId, 1); //rejecting id value = 1
                    };
                    divInterviewStatus.appendChild(candidateInterviewAccept);

                    var iconImg = document.createElement("img");
                    iconImg.src = "/assets/recruiter/img/icons/accept.svg";
                    iconImg.setAttribute('height', '16px');
                    iconImg.setAttribute('width', '14px');
                    candidateInterviewAccept.appendChild(iconImg);

                    //reject interview
                    var candidateInterviewReject = document.createElement("span");
                    candidateInterviewReject.className = "reject tooltipped";
                    candidateInterviewReject.setAttribute("data-postiton", "top");
                    candidateInterviewReject.setAttribute("data-delay", "50");
                    candidateInterviewReject.setAttribute("data-tooltip", "Confirm Interview");
                    candidateInterviewReject.onclick = function () {
                        rescheduledDate = "Interview scheduled on " + new Date(jobPost.scheduledInterviewDate).getDate() + "/" + (new Date(jobPost.scheduledInterviewDate).getMonth() + 1) + "/" + new Date(jobPost.scheduledInterviewDate).getFullYear() + " between " + jobPost.scheduledInterviewTimeSlot.interviewTimeSlotName;
                        confirmInterview(jobPost.jobPost.jobPostId, 0); //rejecting id value = 0
                    };
                    divInterviewStatus.appendChild(candidateInterviewReject);

                    iconImg = document.createElement("img");
                    iconImg.src = "/assets/recruiter/img/icons/reject.svg";
                    iconImg.setAttribute('height', '16px');
                    iconImg.setAttribute('width', '14px');
                    candidateInterviewReject.appendChild(iconImg);
                }
            }
            titleRowThree.appendChild(divInterviewStatus);
            titleRowThree.appendChild(dir);

            var titleRowTwo = document.createElement("div");
            titleRowTwo.className = "row col-sm-4";
            titleRowTwo.id = "appliedOnId";
            titleRowTwo.style = "padding: 0";
            jobBodyCol.appendChild(titleRowTwo);

            var fetchedAppliedDate = jobPost.creationTimestamp;

            var divAppliedDate = document.createElement("div");
            divAppliedDate.className = "appliedDate";
            divAppliedDate.textContent = "Last Update: " + new Date(fetchedAppliedDate).getDate() + "/" + (new Date(fetchedAppliedDate).getMonth() + 1) + "/" + new Date(fetchedAppliedDate).getFullYear();
            titleRowTwo.appendChild(divAppliedDate);


            var titleRowStatus = document.createElement("div");
            titleRowStatus.className = "row col-sm-12";
            titleRowStatus.style = "margin-top: 8px; padding: 0 12px 6px 12px; font-size: 12px";
            jobBodyCol.appendChild(titleRowStatus);

            if(jobPost.status != null){
                if(jobPost.status.statusId == 6 || (jobPost.status.statusId > 9 && jobPost.status.statusId < 13)){
                    var today = new Date();
                    var interviewDate = new Date(jobPost.scheduledInterviewDate);
                    if(interviewDate.getDate() == today.getDate() && interviewDate.getMonth() == today.getMonth() && interviewDate.getFullYear() == today.getFullYear()){ // today's schedule
                        //interview for this job is scheduled today, hence allow to update status

                        var defaultOp = $('<option value="0"></option>').text("Select a Status");
                        var op1 = $('<option value="1"></option>').text("Not Going");
                        var op2 = $('<option value="2"></option>').text("Delayed");
                        var op3 = $('<option value="3"></option>').text("Started");
                        var op4 = $('<option value="4"></option>').text("Reached");

                        var statusUpdateBody = document.createElement("span");
                        titleRowStatus.appendChild(statusUpdateBody);

                        var statusBody = document.createElement("span");
                        statusBody.textContent = "Update your status: ";
                        statusUpdateBody.appendChild(statusBody);

                        var currentStatus = document.createElement("span");
                        statusBody.appendChild(currentStatus);

                        var statusBodySelect = document.createElement("select");
                        statusBodySelect.className = "selectDropdown";
                        statusBodySelect.id = "candidate_interview_status_" + jobPost.jobPost.jobPostId;
                        statusBody.appendChild(statusBodySelect);

                        if(jobPost.status.statusId == 6 || jobPost.status.statusId == 10){
                            currentStatus.textContent = "Status not Specified";
                            currentStatus.style = "font-weight: bold; margin-right: 4px; color: grey";
                            $("#candidate_interview_status_" + jobPost.jobPost.jobPostId).append(defaultOp);
                            if(jobPost.status.statusId == 10){
                                currentStatus.textContent = "Not Going";
                                currentStatus.style = "font-weight: bold; margin-right: 4px; color: red";
                            } else{
                                $("#candidate_interview_status_" + jobPost.jobPost.jobPostId).append(op1);
                            }

                            $("#candidate_interview_status_" + jobPost.jobPost.jobPostId).append(op2);
                            $("#candidate_interview_status_" + jobPost.jobPost.jobPostId).append(op3);
                            $("#candidate_interview_status_" + jobPost.jobPost.jobPostId).append(op4);
                        } else if(jobPost.status.statusId == 11){
                            currentStatus.textContent = "Delayed";
                            currentStatus.style = "font-weight: bold; margin-right: 4px; color: red";
                            $("#candidate_interview_status_" + jobPost.jobPost.jobPostId).append(defaultOp);
                            $("#candidate_interview_status_" + jobPost.jobPost.jobPostId).append(op3);
                            $("#candidate_interview_status_" + jobPost.jobPost.jobPostId).append(op4);
                        } else if(jobPost.status.statusId == 12) {
                            currentStatus.textContent = "Started";
                            currentStatus.style = "font-weight: bold; margin-right: 4px; color: green";
                            $("#candidate_interview_status_" + jobPost.jobPost.jobPostId).append(defaultOp);
                            $("#candidate_interview_status_" + jobPost.jobPost.jobPostId).append(op4);
                        }

                        var statusUpdateBtn = document.createElement("span");
                        statusUpdateBtn.className = "navigationBtn";
                        statusUpdateBtn.textContent = "Update";
                        statusUpdateBtn.style = "margin: 4px";
                        statusUpdateBtn.onclick = function () {
                            globalJpId = jobPost.jobPost.jobPostId;
                            if($("#candidate_interview_status_" + globalJpId).val() == 1){ //to capture reason for not going
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
                            } else{
                                updateStatus();
                            }
                        };
                        statusBody.appendChild(statusUpdateBtn);
                    }
                }
            }
        }
    });
}

function confirmUpdateStatusNotGoing(){
    if($("#notGoingReason").val() > 0){
        updateStatus();
    } else{
        alert("Please select a reason for not going for interview");
    }
}

function updateStatus() {
    if($("#candidate_interview_status_" + globalJpId).val() > 0){
        try {
            $.ajax({
                type: "POST",
                url: "/updateStatusCandidate/" + globalJpId + "/" + $("#candidate_interview_status_" + globalJpId).val() + "/" + $("#notGoingReason").val(),
                data: false,
                contentType: false,
                processData: false,
                success: processDataForUpdateStatus
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    } else {
        alert("Please select a status");
    }
}

function processDataForUpdateStatus(returnedData) {
    $("#notGoingModal").modal("hide");
    if(returnedData == 1){
        alert("Status updated successfully");
        setTimeout(function () {
            location.reload();
        }, 2000);
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
        divInterviewStatus.className = "appliedDate";
        divInterviewStatus.id = "interview_status_val_" + globalJpId;
        if(globalInterviewStatus == 1){
            alert("Job application accepted");
            divInterviewStatus.textContent = rescheduledDate;
            divInterviewStatus.style = "color: green; font-weight: 600";
        } else {
            alert("Job application rejected");
            divInterviewStatus.textContent = "Job Application declined by the Candidate";
            divInterviewStatus.style = "color: red; font-weight: 600";
        }

        $("#interview_status_div_" + globalJpId).append(divInterviewStatus);
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

function tabOne() {
    $("#tabOne").addClass("activeTab");
    $("#tabTwo").removeClass("activeTab");
    $("#tabThree").removeClass("activeTab");

    $("#myAppliedJobsConfirmed").show();
    $("#myAppliedJobsUnderReview").hide();
    $("#myAppliedJobsRejected").hide();
}

function tabTwo() {
    $("#tabOne").removeClass("activeTab");
    $("#tabTwo").addClass("activeTab");
    $("#tabThree").removeClass("activeTab");

    $("#myAppliedJobsConfirmed").hide();
    $("#myAppliedJobsUnderReview").show();
    $("#myAppliedJobsRejected").hide();
}

function tabThree() {
    $("#tabOne").removeClass("activeTab");
    $("#tabTwo").removeClass("activeTab");
    $("#tabThree").addClass("activeTab");

    $("#myAppliedJobsConfirmed").hide();
    $("#myAppliedJobsUnderReview").hide();
    $("#myAppliedJobsRejected").show();
}