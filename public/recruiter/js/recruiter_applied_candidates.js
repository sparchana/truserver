/**
 * Created by hawk on 21/10/16.
 */
var jobPostId;
var globalCandidateId;
var globalInterviewStatus;
var rescheduledDate;
var rescheduledSlot;
var candidateCardData;

var globalInterviewDay = null;
var globalInterviewSlot = null;
var globalSchedule = null;

var allTimeSlots = [];
var allReason = [];

var oldDate = null;
var notSelectedReason = [];

function openTrackInterview() {
    window.location = "/recruiter/job/track/" + jobPostId;
}

$(document).scroll(function(){
    if ($(this).scrollTop() > 40) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});

$(document).ready(function() {
    // checkRecruiterLogin();
    getRecruiterInfo();
    $('ul.tabs').tabs();
    var pathname = window.location.pathname; // Returns path only
    var jobPostIdUrl = pathname.split('/');
    jobPostId = jobPostIdUrl[(jobPostIdUrl.length)-1];
    getAllCandidates();

    try {
        $.ajax({
            type: "POST",
            url: "/getRecruiterJobPostInfo/" + jobPostId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForJobPost
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

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

$(window).load(function(){
    try {
        $.ajax({
            type: "POST",
            url: "/getAllRecruiterJobPosts",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetJobPostDetails
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataGetJobPostDetails(returnedData){
    var jobPostList = returnedData;
    var newCount = 0;

    if(jobPostList.length == 0){
        $("#noInterviews").show();
    }

    jobPostList.forEach(function (jobPost) {
        newCount += jobPost.pendingCount;
        newCount += jobPost.upcomingCount;
    });

    if(newCount == 0){
        $(".newNotification").hide();
    } else {
        $(".newNotification").show();
        $("#pendingApproval").html(newCount);
        $("#pendingApprovalMobile").html(newCount);
    }

}

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

function processDataForJobPost(returnedData) {
    $("#jobPostTitle").html("Job Applications for " + returnedData.jobPostTitle);
}

function getAllCandidates() {
    try {
        $.ajax({
            type: "POST",
            url: "/getAllJobApplicants/" + parseInt(jobPostId),
            data: false,
            contentType: false,
            processData: false,
            success: processDataForJobApplications
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
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

function processDataUnlockedCandidates(returnedData) {
    returnedData.forEach(function (unlockedCandidate){
        try {
            $("#candidate_" + unlockedCandidate.candidate.candidateId).html(unlockedCandidate.candidate.candidateMobile);
            $("#unlock_candidate_" + unlockedCandidate.candidate.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 customUnlockBtn").addClass("contactUnlocked right").removeAttr('onclick');
        } catch (err){}
    });
}

function tabChange1() {
    $("#tab1").addClass("activeTab");
    $("#tab2").removeClass("activeTab");
    $("#tab3").removeClass("activeTab");

    $("#tab1Parent").addClass("activeParent");
    $("#tab2Parent").removeClass("activeParent");
    $("#tab3Parent").removeClass("activeParent");
}

function tabChange2() {
    $("#tab1").removeClass("activeTab");
    $("#tab2").addClass("activeTab");
    $("#tab3").removeClass("activeTab");

    $("#tab1Parent").removeClass("activeParent");
    $("#tab2Parent").addClass("activeParent");
    $("#tab3Parent").removeClass("activeParent");
}

function tabChange3() {
    $("#tab1").removeClass("activeTab");
    $("#tab2").removeClass("activeTab");
    $("#tab3").addClass("activeTab");

    $("#tab1Parent").removeClass("activeParent");
    $("#tab2Parent").removeClass("activeParent");
    $("#tab3Parent").addClass("activeParent");
}

function processDataForJobApplications(returnedData) {
    var candidateList = [];

    var acceptInterview = [];
    var contactCandidates = [];
    var pendingConfirmation = [];
    var rejectedList = [];
    var interviewTodayList = [];
    var upcomingInterviews = [];
    var pastInterviews = [];
    var completedInterviews = [];


    pendingCount = 0;
    confirmedCount = 0;
    completedCount = 0;
    approvalCount = 0;

    var upcomingInterviewCount = 0;
    var interviewTodayCount = 0;
    var actionNeededCount = 0;

    pendingParent.html('');
    confirmedParent.html('');
    completedParent.html('');

    if(returnedData != "0"){

        returnedData.forEach(function (value) {
            if (value != null) {
                if (value.extraData.workflowStatus != null) {
                    if (value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_RESCHEDULE) {

                        //awaiting confirmation from recruiter
                        pendingConfirmation.push(value);
                    } else if (value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT || value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE) {

                        //pushing all the rejected applications in rejected list which will come at last
                        rejectedList.push(value);
                    } else if (value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_SCHEDULED) {

                        //pushing all the action needed applications which will come on top
                        acceptInterview.push(value);
                        actionNeededCount++;

                    } else if (value.extraData.workflowStatus.statusId > JWF_STATUS_INTERVIEW_RESCHEDULE && value.extraData.workflowStatus.statusId < JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED) {
                        var todayDay = new Date();
                        var interviewDate = new Date(value.extraData.interviewDate);
                        var interviewDay = interviewDate.getDate();
                        var interviewMonth = interviewDate.getMonth() + 1;

                        //checking today's interview, if yes, it should be on top
                        if ((todayDay.getDate() == interviewDay) && ((todayDay.getMonth() + 1) == interviewMonth)) {

                            //push in todays interview list
                            interviewTodayList.push(value);
                            interviewTodayCount++;
                        } else if (todayDay.getTime() < interviewDate.getTime()) {

                            //else push in the common list
                            upcomingInterviews.push(value);
                            upcomingInterviewCount++;
                        } else {
                            //else push in the common list
                            pastInterviews.push(value);
                        }
                    } else {
                        //pushing in the common list
                        completedInterviews.push(value);
                    }
                } else {
                    //applications with null status goes in the common list
                    contactCandidates.push(value);
                }
            }
        });

        acceptInterview.forEach(function (val) {
            candidateList.push(val);
        });

        contactCandidates.forEach(function (val) {
            candidateList.push(val);
        });

        pendingConfirmation.forEach(function (val) {
            candidateList.push(val);
        });

        rejectedList.forEach(function (val) {
            candidateList.push(val);
        });

        interviewTodayList.forEach(function (val) {
            candidateList.push(val);
        });

        upcomingInterviews.forEach(function (val) {
            candidateList.push(val);
        });

        pastInterviews.forEach(function (val) {
            candidateList.push(val);
        });

        completedInterviews.forEach(function (val) {
            candidateList.push(val);
        });

        acceptInterviewFlag = false;
        contactCandidatesFlag = false;
        pendingConfirmationFlag = false;
        rejectedListFlag = false;
        interviewTodayListFlag = false;
        upcomingInterviewsFlag = false;
        pastInterviewsFlag = false;
        completedInterviewsFlag = false;

        actionNeeded = false;

        candidateList.forEach(function (value){

            //calling render candidate card method to render candidate card

            //passing parent as null here because in this case we have 3 different parents. The parent is computed in the method itself
            renderIndividualCandidateCard(value, null, view_applied_candidate);
        });


        $('.tooltipped').tooltip({delay: 50});

        if(pendingCount == 0){
            $("#pendingCount").hide();
            $("#noPendingApplication").show();
        } else{
            $("#noPendingApplication").hide();
            if(actionNeededCount > 0){
                $("#pendingCount").html(actionNeededCount);
                $("#pendingCount").show();
            } else{
                $("#pendingCount").hide();
            }
        }

        if(confirmedCount == 0){
            $("#confirmedCount").hide();
            $("#noConfirmedApplication").show();
        } else{
            $("#noConfirmedApplication").hide();
            if((interviewTodayCount + upcomingInterviewCount) > 0){
                $("#confirmedCount").html((interviewTodayCount + upcomingInterviewCount));
                $("#confirmedCount").show();
            } else{
                $("#confirmedCount").hide();
            }
        }

        if(completedCount == 0){
            $("#noCompletedApplication").show();
        } else{
            $("#noCompletedApplication").hide();
        }

        try {
            $.ajax({
                type: "POST",
                url: "/recruiter/api/getUnlockedCandidates/",
                async: true,
                contentType: false,
                data: false,
                success: processDataUnlockedCandidates
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }

        $("#loadingIcon").hide();

        //if there is any action need to be taken, activate the confirmed tab
        if(actionNeededCount > 0){
            $('ul.tabs').tabs('select_tab', 'pending');
        } else if(interviewTodayCount > 0){  //if there is any today's interview lined up, activate the confirmed tab
            $('ul.tabs').tabs('select_tab', 'confirmed');
        } else if(pendingCount == 0 && confirmedCount > 0){
            $('ul.tabs').tabs('select_tab', 'confirmed');
        }
    } else{
        logoutRecruiter();
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

function confirmRejectInterview(){
    if($("#reject_reason").val() != 0){
        globalInterviewStatus = 2;
        setInterviewStatus(globalCandidateId, 2, globalInterviewDay, globalInterviewSlot, $("#reject_reason").val());
    } else{
        notifyError("Please specify the reason for the job application rejection");
    }
}

function setInterviewStatus(candidateId, status, rescheduledDate, rescheduledSlot, reason) {
    globalCandidateId = candidateId;
    globalInterviewStatus = status;

    var d = {
        candidateId: candidateId,
        jobPostId: jobPostId,
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

function processDataInterviewStatus(returnedData) {
    $('.tooltipped').tooltip('remove');
    $("#modalRescheduleSlot").closeModal();
    if(returnedData == "1"){
        $("#interview_status_option_" + globalCandidateId).remove();

        var candidateInterviewStatusVal = document.createElement("span");
        if(globalInterviewStatus == 1){
            notifySuccess("Interview Confirmed"); //accepted
            candidateInterviewStatusVal.textContent = "Interview Confirmed";
            candidateInterviewStatusVal.style = "margin-left: 8px; color: green; font-weight: 600";
        } else if(globalInterviewStatus == 2){ //rejected by recruiter
            $("#modalRejectReason").closeModal();
            notifySuccess("Interview Rejected");
            candidateInterviewStatusVal.textContent = "Interview Rejected";
            candidateInterviewStatusVal.style = "margin-left: 8px; color: red; font-weight: 600";
        } else if(globalInterviewStatus == 3){
            notifySuccess("Interview Rescheduled");
            candidateInterviewStatusVal.textContent = "Interview Rescheduled. Awaiting Candidate's confirmation";
            candidateInterviewStatusVal.style = "margin-left: 8px; color: orange; font-weight: 600";
            var newDate = new Date(rescheduledDate);
            var i, newSlot;
            for(i=0; i<Object.keys(allTimeSlots).length; i++){
                if(allTimeSlots[i].id == rescheduledSlot){
                    newSlot = allTimeSlots[i].name;
                }
            }
            $("#interview_date_" + globalCandidateId).html(('0' + newDate.getDate()).slice(-2) + '-' + getMonthVal((newDate.getMonth()+1)) + " @" + newSlot);
        }
        $("#interview_div_" + globalCandidateId).append(candidateInterviewStatusVal);
        getAllCandidates();
    } else{
        notifyError("Something went wrong. Please try again later. Refreshing page..");
        setTimeout(function(){
            location.reload(true); // hard refresh set 'ture' for current page to reload latest js, css
        }, 2000);
    }
}

//feedback
function openFeedbackModal(candidateId) {
    globalCandidateId = candidateId;
    $("#reasonVal").html('');
    var defaultOption = $('<option value="0" selected></option>').text("Select a reason");
    $('#reasonVal').append(defaultOption);

    notSelectedReason.forEach(function (reason) {
        var option = $('<option value=' + reason.id + '></option>').text(reason.name);
        $('#reasonVal').append(option);
    });

    $("#otherReason").hide();
    $("#feedbackOption").val(0);
    $("#reasonVal").val(0);
    $("#feedbackNote").val('');

    $("#addFeedback").openModal();

    $("#feedbackOption").change(function (){
        if($(this).val() == 2 || $(this).val() == 4){
            $("#otherReason").show();
        } else{
            $("#otherReason").hide();
        }
    });
}

function confirmAddFeedback() {
    if($("#feedbackOption").val() > 0){
        if(($("#feedbackOption").val() == 2 || $("#feedbackOption").val() == 4) && $("#reasonVal").val() == 0){
            notifyError("Please select a reason");
        } else{
            try {
                var d = {
                    candidateId: globalCandidateId,
                    jobPostId : jobPostId,
                    feedbackStatus : $("#feedbackOption").val(),
                    feedbackComment : $("#feedbackNote").val(),
                    rejectReason: $("#reasonVal").val()
                };

                $.ajax({
                    type: "POST",
                    url: "/updateFeedback",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataUpdateFeedBack
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    } else{
        notifyError("Please select a feedback option");
    }
}

function processDataUpdateFeedBack(returnedData) {
    if(returnedData == 1){
        notifySuccess("Feedback updated successfully");
        $("#candidate_card_" + globalCandidateId).remove();

        $("#addFeedback").closeModal();
/*        setTimeout(function () {
            location.reload();
        }, 2000);*/

        if($("#feedbackOption").val() == FEEDBACK_OPTION_SELECTED){
            candidateCardData.extraData.workflowStatus.statusId = JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED;
        } else if($("#feedbackOption").val() == FEEDBACK_OPTION_REJECTED){
            candidateCardData.extraData.workflowStatus.statusId = JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_REJECTED;
        } else if($("#feedbackOption").val() == FEEDBACK_OPTION_NO_SHOW){
            candidateCardData.extraData.workflowStatus.statusId = JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NO_SHOW;
        } else{
            candidateCardData.extraData.workflowStatus.statusId = JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NOT_QUALIFIED;
        }

        candidateCardData.extraData.workflowStatus.statusTitle = $("#feedbackOption option:selected").text();
        renderIndividualCandidateCard(candidateCardData, null, view_applied_candidate);

    } else if(returnedData == -1){
        notifyError("You are out of interview credits. Please purchase interview credits!");
        openCreditModal();
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

function closeCreditModal() {
    $("#modalBuyCredits").closeModal();
}

function closeRescheduleModal() {
    $("#modalRescheduleSlot").closeModal();
}

function closeRejectModal() {
    $("#modalRejectReason").closeModal();
}

function openCreditModal(){
    $("#successMsg").hide();
    $("#modalBuyCredits").openModal();
}

function submitCredits() {
    $("#successMsg").hide();
    var contactCreditStatus = 1;
    var interviewCreditStatus = 1;

    if($("#contactCreditAmount").val() == undefined || $("#contactCreditAmount").val() == null || $("#contactCreditAmount").val() == ""){
        contactCreditStatus = 0;
    }
    if($("#interviewCreditAmount").val() == undefined || $("#interviewCreditAmount").val() == null || $("#interviewCreditAmount").val() == ""){
        interviewCreditStatus = 0;
    }

    if(interviewCreditStatus == 0 && contactCreditStatus == 0){
        notifyError("Please specify no. of credits!");
    } else{
        contactCreditStatus = 1;
        interviewCreditStatus = 1;

        var contactCredits = 0;
        var interviewCredits = 0;
        if($("#contactCreditAmount").val() != ""){
            contactCredits = parseInt($("#contactCreditAmount").val());
            if(contactCredits < 1){
                notifyError("Contact credits cannot be less than 1");
                contactCreditStatus = 0;
            } else if(contactCredits > 10000){
                notifyError("Contact credits cannot be greater than 10000");
                contactCreditStatus = 0;
            }
        }

        if($("#interviewCreditAmount").val() != ""){
            interviewCredits = parseInt($("#interviewCreditAmount").val());
            if(interviewCredits < 1){
                notifyError("Interview credits cannot be less than 1");
                interviewCreditStatus = 0;
            } else if(interviewCredits > 10000){
                notifyError("Interview credits cannot be greater than 10000");
                interviewCreditStatus = 0;
            }
        }

        if(interviewCreditStatus != 0 && contactCreditStatus != 0){
            $("#requestCredits").addClass("disabled");
            var d = {
                noOfContactCredits: contactCredits,
                noOfInterviewCredits: interviewCredits
            };

            $.ajax({
                type: "POST",
                url: "/recruiter/api/requestCredits/",
                async: true,
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataAddCreditRequest
            });
        }
    }
}

function processDataAddCreditRequest(returnedData) {
    $("#requestCredits").removeClass("disabled");
    if(returnedData.status == 1){
        $("#successMsg").show();
        notifySuccess("Thanks! We have received your request to buy more credits. Our business team will contact you within 24hrs")
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

function unlockContact(candidateId){
    if(candidateId != null || candidateId != undefined){
        candidateIdVal = candidateId;
        try {
            $.ajax({
                type: "POST",
                url: "/recruiter/unlockCandidateContact/" + candidateId,
                async: false,
                contentType: false,
                processData: false,
                success: processDataUnlockCandidate
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

function processDataUnlockCandidate(returnedData) {
    if(returnedData.status == 1){
        notifySuccess("Contact successfully unlocked");
        getRecruiterInfo();
        $("#candidate_" + candidateIdVal).html(returnedData.candidateMobile);
        $("#unlock_candidate_" + returnedData.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 customUnlockBtn").addClass("contactUnlocked right").removeAttr('onclick');
    } else if(returnedData.status == 2){
        notifySuccess("You have already unlocked the candidate");
        getRecruiterInfo();
        $("#unlock_candidate_" + returnedData.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 customUnlockBtn").addClass("contactUnlocked right").removeAttr('onclick');
        $("#candidate_" + candidateIdVal).html(returnedData.candidateMobile);
    } else if(returnedData.status == 3){
        notifyError("Out of credits! Please recharge");
        openCreditModal();
    }
}

function getRecruiterInfo() {
    try {
        $.ajax({
            type: "GET",
            url: "/getRecruiterProfileInfo",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataRecruiterProfile(returnedData) {
    if(returnedData == 0){
        logoutRecruiter();
    } else{
        if(returnedData.recruiterAccessLevel == 0){
            $("#creditView").show();
            $("#creditViewMobile").show();
            $("#recruiterMsg").show();
            $("#recruiterHIW").show();
            $("#remainingContactCredits").html(returnedData.contactCreditCount);
            $("#remainingContactCreditsMobile").html(returnedData.contactCreditCount);
            $("#remainingInterviewCredits").html(returnedData.interviewCreditCount);
            $("#remainingInterviewCreditsMobile").html(returnedData.interviewCreditCount);
        } else{
            $("#creditView").hide();
            $("#creditViewMobile").hide();
            $("#recruiterMsg").hide();
            $("#recruiterHIW").hide();
        }
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

function validateTimeSlot(val, text) {
    if(val.localeCompare(text) == 0){
        $('#rescheduleDateAndSlot').tokenize().tokenRemove(val);
        notifyError("Please select a valid date and time from the dropdown list");
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

function logoutRecruiter() {
    try {
        $.ajax({
            type: "GET",
            url: "/logoutRecruiter",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataLogoutRecruiter
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataLogoutRecruiter() {
    window.location = "/recruiter#signin";
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}

function closeFeedbackModal() {
    $("#addFeedback").closeModal();
}