var jobPostId;
var oldDate = null;
var globalCandidateId = null;
var globalInterviewStatus = null;
var rescheduledDate;
var rescheduledSlot;

var globalInterviewDay = null;
var globalInterviewSlot = null;
var globalSchedule = null;
var allTimeSlots = [];
var allReason = [];

var notSelectedReason = [];


$(document).ready(function(){
    checkRecruiterLogin();

    var pathname = window.location.pathname; // Returns path only
    var jobPostIdUrl = pathname.split('/');
    jobPostId = jobPostIdUrl[(jobPostIdUrl.length)-1];

    tabChange1();

    try {
        $.ajax({
            type: "POST",
            url: "/getAllNotSelectedReasons",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataNotSelectedReason
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "POST",
            url: "/getAllTimeSlots",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetAllTimeSlots
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "POST",
            url: "/getAllInterviewRejectReasons",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetAllReason
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    $("#rescheduleDateAndSlot").change(function (){
        if($(this).val() != -1){
            try{
                var combinedValue = $("#rescheduleDateAndSlot").val().split("_");
                rescheduledDate = combinedValue[0];
                rescheduledSlot = combinedValue[1];

            } catch(err){}

            $("#rescheduleInterviewDateBtn").show();
        } else{
            $("#rescheduleInterviewDateBtn").hide();
        }
    });
});

function processDataNotSelectedReason(returnedData) {
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        notSelectedReason.push(item);
    });
}

function processDataGetAllTimeSlots(returnedData) {
    returnedData.forEach(function(timeSlot) {
        var id = timeSlot.interviewTimeSlotId;
        var name = timeSlot.interviewTimeSlotName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allTimeSlots.push(item);
    });
}

function processDataGetAllReason(returnedData) {
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allReason.push(item);
    });
}

function processDataGetSmsReport(returnedData) {
    $("#loadingIcon").hide();
    if(returnedData == 0){
        logoutRecruiter();
    } else{
        if(returnedData.length > 0){
            $('.allSms').html('');
            var parent = $('.allSms');
            returnedData.reverse();
            returnedData.forEach(function (smsObject) {

                var mainDiv =  document.createElement("div");
                parent.append(mainDiv);

                var outerRow = document.createElement("div");
                outerRow.className = 'row';
                outerRow.id="outerBoxMain";
                mainDiv.appendChild(outerRow);

                var colDate = document.createElement("div");
                colDate.className = 'col s12 m1 l1';
                colDate.style = 'margin-top:8px';
                outerRow.appendChild(colDate);

                var postedOn = new Date(smsObject.creationTimeStamp);
                colDate.textContent = ('0' + postedOn.getDate()).slice(-2) + '-' + getMonthVal((postedOn.getMonth()+1)) + '-' + postedOn.getFullYear()

                var spanSentOn  = document.createElement("div");
                spanSentOn.className = "col s4 hide-on-med-and-up right-align";
                spanSentOn.textContent= "Date:";
                spanSentOn.style = "font-weight: 600;font-size:12px";
                colDate.appendChild(spanSentOn);

                var colCandidateName= document.createElement("div");
                colCandidateName.className = 'col s12 m2 l2';
                colCandidateName.style = 'margin-top:8px';
                colCandidateName.textContent = toTitleCase(smsObject.candidate.candidateFullName);
                outerRow.appendChild(colCandidateName);

                var spanCandidateName = document.createElement("div");
                spanCandidateName.className = "col s4 hide-on-med-and-up right-align";
                spanCandidateName.textContent= "Candidate :";
                spanCandidateName.style = "font-weight: 600;font-size:12px";
                colCandidateName.appendChild(spanCandidateName);

                var colCandidateMobile = document.createElement("div");
                colCandidateMobile.className = 'col s12 m2 l2';
                colCandidateMobile.style = 'margin-top:8px';
                colCandidateMobile.textContent = smsObject.candidate.candidateMobile;
                outerRow.appendChild(colCandidateMobile);

                var spanCandidateMobile  = document.createElement("div");
                spanCandidateMobile.className = "col s4  hide-on-med-and-up right-align";
                spanCandidateMobile.textContent= "Mobile :";
                spanCandidateMobile.style = "font-weight: 600;font-size:12px";
                colCandidateMobile.appendChild(spanCandidateMobile);

                var colSmsStatus = document.createElement("div");
                colSmsStatus.className = 'col s12 m1 l1';
                colSmsStatus.style = 'margin-top: 8px';
                outerRow.appendChild(colSmsStatus);

                colSmsStatus.textContent = "Pending";

                if(smsObject.smsDeliveryStatus != null){
                    colSmsStatus.textContent = smsObject.smsDeliveryStatus.statusName;
                }

                var spanSmsStatus  = document.createElement("div");
                spanSmsStatus.className = "col s4 hide-on-med-and-up right-align";
                spanSmsStatus.textContent= "SMS Status: ";
                spanSmsStatus.style = "font-weight: 600;font-size:12px";
                colSmsStatus.appendChild(spanSmsStatus);

                var colApplicationStatus = document.createElement("div");
                colApplicationStatus.className = 'col s12 m2 l2';
                colApplicationStatus.style = 'margin-top: 8px';
                outerRow.appendChild(colApplicationStatus);

                if(smsObject.hasApplied == 0)
                    colApplicationStatus.textContent = "Not Applied";
                 else{
                    colApplicationStatus.textContent = "Applied";
                }
                var spanApplicationStatus  = document.createElement("div");
                spanApplicationStatus.className = "col s4 hide-on-med-and-up right-align";
                spanApplicationStatus.textContent= "Application Status:";
                spanApplicationStatus.style = "font-weight: 600;font-size:12px;";
                colApplicationStatus.appendChild(spanApplicationStatus);

                var colSmsText = document.createElement("div");
                colSmsText.className = 'col s12 m2 l4';
                colSmsText.style = 'margin-top: 8px';
                outerRow.appendChild(colSmsText);

                colSmsText.textContent = smsObject.smsText;

                var spanSmsText = document.createElement("div");
                spanSmsText.className = "col s4 hide-on-med-and-up right-align";
                spanSmsText.textContent= "SMS Message: ";
                spanSmsText.style = "font-weight: 600;font-size:12px;";
                colApplicationStatus.appendChild(spanSmsText);

            });

            $("#smsReportTable").show();
        } else{
            $("#noSmsSent").show();
        }
    }
}

function processDataJobApplications(returnedData) {
    $("#loadingIcon").hide();

    if(returnedData.length > 0){
        $('.allApplications').html('');
        var parent = $('.allApplications');
        returnedData.forEach(function (workflowObj) {
            var mainDiv =  document.createElement("div");
            parent.append(mainDiv);

            var outerRow = document.createElement("div");
            outerRow.className = 'row';
            outerRow.id="outerBoxMain";
            mainDiv.appendChild(outerRow);

            var colCandidateName= document.createElement("div");
            colCandidateName.className = 'col s12 m2 l2';
            colCandidateName.style = 'margin-top:8px';
            colCandidateName.textContent = toTitleCase(workflowObj.candidate.candidateFullName);
            outerRow.appendChild(colCandidateName);

            var spanCandidateName = document.createElement("div");
            spanCandidateName.className = "col s4 hide-on-med-and-up right-align";
            spanCandidateName.textContent= "Candidate :";
            spanCandidateName.style = "font-weight: 600;font-size:12px";
            colCandidateName.appendChild(spanCandidateName);

            var colCandidateMobile = document.createElement("div");
            colCandidateMobile.className = 'col s12 m2 l2';
            colCandidateMobile.style = 'margin-top:8px';
            colCandidateMobile.textContent = workflowObj.candidate.candidateMobile;
            outerRow.appendChild(colCandidateMobile);

            var spanCandidateMobile  = document.createElement("div");
            spanCandidateMobile.className = "col s4  hide-on-med-and-up right-align";
            spanCandidateMobile.textContent= "Mobile :";
            spanCandidateMobile.style = "font-weight: 600;font-size:12px";
            colCandidateMobile.appendChild(spanCandidateMobile);

            var colChannel = document.createElement("div");
            colChannel.className = 'col s12 m1 l2';
            colChannel.style = 'margin-top: 8px';
            outerRow.appendChild(colChannel);

            colChannel.textContent = "Not Available";

            var spanChannel  = document.createElement("div");
            spanChannel.className = "col s4 hide-on-med-and-up right-align";
            spanChannel.textContent= "Channel: ";
            spanChannel.style = "font-weight: 600;font-size:12px";
            colChannel.appendChild(spanChannel);

            var colAction = document.createElement("div");
            colAction.className = 'col s12 m2 l4';
            colAction.id = "interview_status_option_" + workflowObj.candidate.candidateId;
            colAction.style = 'margin-top: 8px';
            colAction.textContent = "No action required";

            var colStatus = document.createElement("div");
            colStatus.className = 'col s12 m2 l2';
            colStatus.style = 'margin-top: 8px';
            outerRow.appendChild(colStatus);

            if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_SELECTED){
                colStatus.textContent = "Applied";
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_PRESCREEN_ATTEMPTED){
                colStatus.textContent = "Attempted pre screen";
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_PRESCREEN_FAILED){
                colStatus.textContent = "Pre screen failed";
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_PRESCREEN_COMPLETED){
                colStatus.textContent = "Pre screen Complete";
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_SCHEDULED){
                colStatus.textContent = "Interview Scheduled";
                colAction.textContent = "";

                //action button
                var candidateInterviewAcceptParent = document.createElement("span");
                candidateInterviewAcceptParent.style = "display: inline-block; font-size: 12px";
                candidateInterviewAcceptParent.onclick = function () {
                    oldDate = new Date(workflowObj.extraData.interviewDate);
                    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                    globalInterviewSlot = workflowObj.extraData.interviewSlot.interviewTimeSlotId;
                    globalSchedule = workflowObj.extraData.interviewSchedule;
                    confirmInterviewStatus(workflowObj.candidate.candidateId);
                };
                colAction.appendChild(candidateInterviewAcceptParent);

                var candidateInterviewAccept = document.createElement("span");
                candidateInterviewAccept.className = "accept";
                candidateInterviewAcceptParent.appendChild(candidateInterviewAccept);

                var iconImg = document.createElement("img");
                iconImg.src = "/assets/dashboard/img/reached.svg";
                iconImg.setAttribute('height', '24px');
                candidateInterviewAccept.appendChild(iconImg);

                var actionText = document.createElement("span");
                actionText.textContent = " Accept";
                candidateInterviewAcceptParent.appendChild(actionText);

                var candidateInterviewRejectParent = document.createElement("span");
                candidateInterviewRejectParent.style = "display: inline-block; font-size: 12px";
                candidateInterviewRejectParent.onclick = function () {
                    oldDate = new Date(workflowObj.extraData.interviewDate);
                    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                    globalInterviewSlot = workflowObj.extraData.interviewSlot.interviewTimeSlotId;
                    globalSchedule = workflowObj.extraData.interviewSchedule;
                    rejectInterview(workflowObj.candidate.candidateId);
                };
                colAction.appendChild(candidateInterviewRejectParent);

                var candidateInterviewReject = document.createElement("span");
                candidateInterviewReject.className = "reject";
                candidateInterviewRejectParent.appendChild(candidateInterviewReject);

                iconImg = document.createElement("img");
                iconImg.src = "/assets/dashboard/img/not_going.svg";
                iconImg.setAttribute('height', '24px');
                candidateInterviewReject.appendChild(iconImg);

                actionText = document.createElement("span");
                actionText.textContent = " Reject";
                candidateInterviewRejectParent.appendChild(actionText);

                var candidateInterviewRescheduleParent = document.createElement("span");
                candidateInterviewRescheduleParent.style = "display: inline-block";
                candidateInterviewRescheduleParent.onclick = function () {
                    globalCandidateId = workflowObj.candidate.candidateId;
                    oldDate = new Date(workflowObj.extraData.interviewDate);
                    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                    globalInterviewSlot = workflowObj.extraData.interviewSlot.interviewTimeSlotId;
                    globalSchedule = workflowObj.extraData.interviewSchedule;

                    showSlotModal();
                };
                colAction.appendChild(candidateInterviewRescheduleParent);

                var candidateInterviewReschedule = document.createElement("span");
                candidateInterviewReschedule.className = "reschedule";
                candidateInterviewRescheduleParent.appendChild(candidateInterviewReschedule);

                iconImg = document.createElement("img");
                iconImg.src = "/assets/dashboard/img/reschedule.svg";
                iconImg.setAttribute('height', '24px');
                candidateInterviewReschedule.appendChild(iconImg);

                actionText = document.createElement("span");
                actionText.textContent = " Reschedule";
                candidateInterviewRescheduleParent.appendChild(actionText);

            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT){
                colStatus.textContent = "Rejected";
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE){
                colStatus.textContent = "Rejected by candidate";
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_RESCHEDULE){
                colStatus.textContent = "Rescheduled. Awaiting candidate's response";
            } else{
                colStatus.textContent = "N/A";
            }

            var spanStatus = document.createElement("div");
            spanStatus.className = "col s4 hide-on-med-and-up right-align";
            spanStatus.textContent= "Status:";
            spanStatus.style = "font-weight: 600;font-size:12px;";
            colStatus.appendChild(spanStatus);

            //append action section
            outerRow.appendChild(colAction);


            var spanAction = document.createElement("div");
            spanAction.className = "col s4 hide-on-med-and-up right-align";
            spanAction.textContent= "Action: ";
            spanAction.style = "font-weight: 600;font-size:12px;";
            colAction.appendChild(spanAction);
         });

        $("#trackApplicationTable").show();
    } else{
        $("#noApplications").show();
    }
}

function checkRecruiterLogin() {
    try {
        $.ajax({
            type: "GET",
            url: "/checkRecruiterSession",
            data: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterSession
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataRecruiterSession(returnedData) {
    if(returnedData == 0){
        logoutRecruiter();
    }
}

function tabChange1() {
    $("#tab1").addClass("activeTab");
    $("#tab2").removeClass("activeTab");
    $("#tab3").removeClass("activeTab");

    $("#tab1Parent").addClass("activeParent");
    $("#tab2Parent").removeClass("activeParent");
    $("#tab3Parent").removeClass("activeParent");

    $("#loaderText").html("Fetching SMS Reports");
    $("#loadingIcon").show();

    hideTab2();
    hideTab3();

    try {
        $.ajax({
            type: "POST",
            url: "/getSentSms/" + jobPostId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataGetSmsReport,
            error: function (jqXHR, exception) {
                $("#somethingWentWrong").show();
                $("#loadingIcon").hide();
            }

        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function tabChange2() {
    $("#tab1").removeClass("activeTab");
    $("#tab2").addClass("activeTab");
    $("#tab3").removeClass("activeTab");

    $("#tab1Parent").removeClass("activeParent");
    $("#tab2Parent").addClass("activeParent");
    $("#tab3Parent").removeClass("activeParent");

    $("#loaderText").html("Fetching Job Applicants");
    $("#loadingIcon").show();

    hideTab1();
    hideTab3();

    try {
        $.ajax({
            type: "POST",
            url: "/getAppliedCandidates/" + jobPostId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataJobApplications,
            error: function (jqXHR, exception) {
                $("#somethingWentWrong").show();
                $("#loadingIcon").hide();
            }

        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function setInterviewStatus(candidateId, status, rescheduledDate, rescheduledSlot, reason) {
    globalCandidateId = candidateId;
    globalInterviewStatus = status;

    var d = {
        candidateId: candidateId,
        jobPostId: 925,
        interviewStatus: status,
        rescheduledDate: rescheduledDate,
        rescheduledSlot: rescheduledSlot,
        reason: reason,
        interviewSchedule: globalSchedule
    };

    try {
        $.ajax({
            type: "POST",
            url: "/recruiter/api/updateInterviewStatus",
            async: false,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataInterviewStatus
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
}

function processDataInterviewStatus(returnedData) {
    $('.tooltipped').tooltip('remove');
    $("#modalRescheduleSlot").closeModal();
    if(returnedData == "1"){
        $("#interview_status_option_" + globalCandidateId).html('');

        var candidateInterviewStatusVal = document.createElement("span");
        if(globalInterviewStatus == 1){
            notifySuccess("Interview Confirmed"); //accepted
            $("#interview_status_option_" + globalCandidateId).html('Interview Confirmed');
        } else if(globalInterviewStatus == 2){ //rejected by recruiter
            $("#modalRejectReason").closeModal();
            notifySuccess("Interview Rejected");
            $("#interview_status_option_" + globalCandidateId).html('Interview Rejected');
        } else if(globalInterviewStatus == 3){
            notifySuccess("Interview Rescheduled");
            $("#interview_status_option_" + globalCandidateId).html('Interview Rescheduled. Awaiting Candidate\'s confirmation');
            var newDate = new Date(rescheduledDate);
            var i, newSlot;
            for(i=0; i<Object.keys(allTimeSlots).length; i++){
                if(allTimeSlots[i].id == rescheduledSlot){
                    newSlot = allTimeSlots[i].name;
                }
            }
        }
    } else{
        notifyError("Something went wrong. Please try again later. Refreshing page..");
        setTimeout(function(){
            location.reload(true); // hard refresh set 'ture' for current page to reload latest js, css
        }, 2000);
    }
}

function confirmInterviewStatus(candidateId) {
    globalCandidateId = candidateId;
    globalInterviewStatus = "1";
    setInterviewStatus(globalCandidateId, globalInterviewStatus, globalInterviewDay, globalInterviewSlot, null);
}

function rejectInterview(candidateId) {
    globalCandidateId = candidateId;
    globalInterviewStatus = 3;

    $("#reject_reason").html('');

    var defaultOption = $('<option value="0" selected></option>').text("Select a reason");
    $('#reject_reason').append(defaultOption);

    allReason.forEach(function (reason) {
        var option = $('<option value=' + reason.id + '></option>').text(reason.name);
        $('#reject_reason').append(option);
    });
    $("#modalRejectReason").openModal();
}

function rescheduleInterviewStatus() {
    globalInterviewStatus = "3";
    setInterviewStatus(globalCandidateId, globalInterviewStatus, rescheduledDate, rescheduledSlot, null);
}

function confirmRejectInterview(){
    if($("#reject_reason").val() != 0){
        globalInterviewStatus = 2;
        setInterviewStatus(globalCandidateId, 2, globalInterviewDay, globalInterviewSlot, $("#reject_reason").val());
    } else{
        notifyError("Please specify the reason for the job application rejection");
    }
}

function showSlotModal() {
    $("#modalRescheduleSlot").openModal();
    $("#rescheduleInterviewDateBtn").hide();
    try {
        $.ajax({
            type: "POST",
            url: "/getJobPostInfo/" + parseInt(jobPostId) + "/0",
            data: false,
            contentType: false,
            processData: false,
            success: processDataForJobPostInfo
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataForJobPostInfo(returnedData) {
    if (Object.keys(returnedData.interviewDetailsList).length > 0) {
        //slots
        var i;
        $('#rescheduleDateAndSlot').html('');
        var defaultOption = $('<option value="-1"></option>').text("Select Time Slot");
        $('#rescheduleDateAndSlot').append(defaultOption);

        var interviewDetailsList = returnedData.interviewDetailsList;
        if (interviewDetailsList[0].interviewDays != null) {
            var interviewDays = interviewDetailsList[0].interviewDays.toString(2);

            /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
            if (interviewDays.length != 7) {
                x = 7 - interviewDays.length;
                var modifiedInterviewDays = "";

                for (i = 0; i < x; i++) {
                    modifiedInterviewDays += "0";
                }
                modifiedInterviewDays += interviewDays;
                interviewDays = modifiedInterviewDays;
            }
        }

        var oldSelectedDate = new Date(oldDate);
        //slots
        var today = new Date();
        for (i = 2; i < 9; i++) {
            // 0 - > sun 1 -> mon ...
            var x = new Date(today.getFullYear(), today.getMonth(), today.getDate() + i);
            if (checkSlotAvailability(x, interviewDays)) {
                interviewDetailsList.forEach(function (timeSlot) {
                    var dateSlotSelectedId = x.getFullYear() + "-" + (x.getMonth() + 1) + "-" + x.getDate() + "_" + timeSlot.interviewTimeSlot.interviewTimeSlotId;
                    var option = $('<option value="' + dateSlotSelectedId + '"></option>').text(getDayVal(x.getDay()) + ", " + x.getDate() + " " + getMonthVal((x.getMonth() + 1)) + " (" + timeSlot.interviewTimeSlot.interviewTimeSlotName + ")");

                    if((oldSelectedDate.getDate() == x.getDate()) && (oldSelectedDate.getMonth() == x.getMonth()) && (globalInterviewSlot == timeSlot.interviewTimeSlot.interviewTimeSlotId)){} else{
                        $('#rescheduleDateAndSlot').append(option);
                    }
                });
            }
        }
    } else{
        $("#modalRescheduleSlot").closeModal();
        notifyError("No Slots available!");
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


function tabChange3() {
    $("#tab1").removeClass("activeTab");
    $("#tab2").removeClass("activeTab");
    $("#tab3").addClass("activeTab");

    $("#tab1Parent").removeClass("activeParent");
    $("#tab2Parent").removeClass("activeParent");
    $("#tab3Parent").addClass("activeParent");
    hideTab1();
    hideTab2();

    try {
        $.ajax({
            type: "POST",
            url: "/getConfirmedApplication/" + jobPostId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataConfirmedApplication,
            error: function (jqXHR, exception) {
                $("#somethingWentWrong").show();
                $("#loadingIcon").hide();
            }

        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataConfirmedApplication(returnedData) {
    $("#loadingIcon").hide();

    if(returnedData.length > 0){
        $('.allConfirmed').html('');
        var parent = $('.allConfirmed');
        returnedData.forEach(function (workflowObj) {
            var mainDiv =  document.createElement("div");
            parent.append(mainDiv);

            var outerRow = document.createElement("div");
            outerRow.className = 'row';
            outerRow.id="outerBoxMain";
            mainDiv.appendChild(outerRow);

            var colCandidateName= document.createElement("div");
            colCandidateName.className = 'col s12 m2 l2';
            colCandidateName.style = 'margin-top:8px';
            colCandidateName.textContent = toTitleCase(workflowObj.candidate.candidateFullName);
            outerRow.appendChild(colCandidateName);

            var spanCandidateName = document.createElement("div");
            spanCandidateName.className = "col s4 hide-on-med-and-up right-align";
            spanCandidateName.textContent= "Candidate :";
            spanCandidateName.style = "font-weight: 600;font-size:12px";
            colCandidateName.appendChild(spanCandidateName);

            var colCandidateMobile = document.createElement("div");
            colCandidateMobile.className = 'col s12 m2 l2';
            colCandidateMobile.style = 'margin-top:8px';
            colCandidateMobile.textContent = workflowObj.candidate.candidateMobile;
            outerRow.appendChild(colCandidateMobile);

            var spanCandidateMobile  = document.createElement("div");
            spanCandidateMobile.className = "col s4  hide-on-med-and-up right-align";
            spanCandidateMobile.textContent= "Mobile :";
            spanCandidateMobile.style = "font-weight: 600;font-size:12px";
            colCandidateMobile.appendChild(spanCandidateMobile);

            var colInterviewDateAndTime = document.createElement("div");
            colInterviewDateAndTime.className = 'col s12 m2 l2';
            colInterviewDateAndTime.style = 'margin-top: 8px';
            outerRow.appendChild(colInterviewDateAndTime);

            var interviewDate = new Date(workflowObj.extraData.interviewDate);

            colInterviewDateAndTime.textContent = interviewDate.getFullYear() + "-" + (interviewDate.getMonth() + 1) + "-" + interviewDate.getDate() +
                " @ " + workflowObj.extraData.interviewSlot.interviewTimeSlotName;

            var spanInterviewDate = document.createElement("div");
            spanInterviewDate.className = "col s4 hide-on-med-and-up right-align";
            spanInterviewDate.textContent= "Interview Date & Slot:";
            spanInterviewDate.style = "font-weight: 600;font-size:12px;";
            colInterviewDateAndTime.appendChild(spanInterviewDate);

            var colCandidateStatus = document.createElement("div");
            colCandidateStatus.className = 'col s12 m2 l3';
            colCandidateStatus.style = 'margin-top: 8px';
            outerRow.appendChild(colCandidateStatus);

            colCandidateStatus.textContent = "Status";

            var spanCandidateStatus = document.createElement("div");
            spanCandidateStatus.className = "col s4 hide-on-med-and-up right-align";
            spanCandidateStatus.textContent= "Candidate Status:";
            spanCandidateStatus.style = "font-weight: 600;font-size:12px;";
            colCandidateStatus.appendChild(spanCandidateStatus);

            var colFeedback = document.createElement("div");
            colFeedback.className = 'col s12 m2 l3';
            colFeedback.style = 'margin-top: 8px';
            outerRow.appendChild(colFeedback);

            colFeedback.textContent = "Interview Feedback";

            var spanFeedback = document.createElement("div");
            spanFeedback.className = "col s4 hide-on-med-and-up right-align";
            spanFeedback.textContent= "Interview Feedback:";
            spanFeedback.style = "font-weight: 600;font-size:12px;";
            colFeedback.appendChild(spanFeedback);

        });

        $("#confirmedApplicationTable").show();
    } else{
        $("#noConfirmedApplications").show();
    }

}

function hideTab1() {
    $("#noSmsSent").hide();
    $("#smsReportTable").hide();
}
function hideTab2() {
    $("#noApplications").hide();
    $("#trackApplicationTable").hide();
}

function hideTab3() {
    $("#noConfirmedApplication").hide();
    $("#confirmedApplicationTable").hide();
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

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}
