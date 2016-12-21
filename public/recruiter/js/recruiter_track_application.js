/**
 * Created by dodo on 18/11/16.
 */

var jobPostId;
var todayDay;
var globalCandidateId;

var notSelectedReason = [];

$(document).scroll(function(){
    if ($(this).scrollTop() > 80) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});

$(document).ready(function(){
    checkRecruiterLogin();
    getRecruiterInfo();

    todayDay = new Date();
    var pathname = window.location.pathname; // Returns path only
    var jobPostIdUrl = pathname.split('/');
    jobPostId = jobPostIdUrl[(jobPostIdUrl.length)-1];

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

function processDataForJobPost(returnedData) {
    if(returnedData != "0"){
        $("#job_title").html(returnedData.jobPostTitle);
        $("#job_role").html(returnedData.jobRole.jobName);

        var salary = "₹" + rupeeFormatSalary(returnedData.jobPostMinSalary);
        if(returnedData.jobPostMaxSalary != null && returnedData.jobPostMaxSalary != 0 && returnedData.jobPostMaxSalary != undefined){
            salary += " - ₹" + rupeeFormatSalary(returnedData.jobPostMaxSalary);
        }
        $("#job_salary").html(salary);

        if(returnedData.jobPostEducation != null){
            $("#job_education").html(returnedData.jobPostEducation.educationName);
        }

        if(returnedData.jobPostExperience != null){
            $("#job_exp").html(returnedData.jobPostExperience.experienceType);
        }

        var jobLocality = "";
        if(returnedData.jobPostToLocalityList != null){
            returnedData.jobPostToLocalityList.forEach(function (locality) {
                jobLocality += locality.locality.localityName + ", ";
            });
            $("#job_localities").html(jobLocality.substring(0, (jobLocality.length - 2)));
        }
        if (Object.keys(returnedData.interviewDetailsList).length > 0) {
            //slots
            $('#select_date').html('');
            var i;
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
            //slots
            var today = new Date();

            $('#select_date').tokenize().tokenAdd(0, getDayVal(today.getDay()) + ", " + today.getDate() + " " + getMonthVal((today.getMonth() + 1)));

            for (i = -7; i < 9; i++) {
                // 0 - > sun 1 -> mon ...
                var x = new Date(today.getFullYear(), today.getMonth(), today.getDate() + i);
                if (checkSlotAvailability(x, interviewDays)) {
                    var dateSlotSelectedId = x.getFullYear() + "-" + (x.getMonth() + 1) + "-" + x.getDate();
                    var option = $('<option value="' + dateSlotSelectedId + '"></option>').text(getDayVal(x.getDay()) + ", " + x.getDate() + " " + getMonthVal((x.getMonth() + 1)));
                    $('#select_date').append(option);
                }
            }
        }
    } else{
        logoutRecruiter();
    }
}

function openMyJobApplication() {
    window.open("/recruiter/jobApplicants/" + jobPostId);
}

function getJobApplications() {
    $("#page_heading").html("Track application - " + ('0' + todayDay.getDate()).slice(-2) + '-' + getMonthVal((todayDay.getMonth()+1)) + '-' + todayDay.getFullYear());
    $("#candidateContainer").html('');
    $("#loadingIcon").show();
    $("#noApplications").hide();
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

function processDataUnlockedCandidates(returnedData) {
    returnedData.forEach(function (unlockedCandidate){
        try {
            $("#candidate_" + unlockedCandidate.candidate.candidateId).html(unlockedCandidate.candidate.candidateMobile);
            $("#unlock_candidate_" + unlockedCandidate.candidate.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 btn").addClass("contactUnlocked right").removeAttr('onclick');
        } catch (err){}
    });
}

function processDataForJobApplications(returnedData) {
    var candidateCount = 0;
    var parent = $("#candidateContainer");
    $("#loadingIcon").hide();
    parent.html('');
    if(returnedData != "0"){
        var candidateList = [];
        $.each(returnedData, function (key, value) {
            if (value != null) {
                candidateList.push(value);
            }
        });

        candidateList.reverse();
        candidateList.forEach(function (value){
            if(value.extraData.interviewDate != null){
                var date = new Date(value.extraData.interviewDate);
                var interviewDay = date.getDate();
                var interviewMonth = date.getMonth() + 1;

                if((todayDay.getDate() == interviewDay) && ((todayDay.getMonth() + 1) == interviewMonth)){
                    if(value.extraData.workflowStatus.statusId > JWF_STATUS_INTERVIEW_RESCHEDULE){
                        candidateCount ++;

                        //calling render candidate card method to render candidate card
                        renderIndividualCandidateCard(value, parent, view_tracking_candidate);
                    }
                    $('.tooltipped').tooltip({delay: 50});
                }
            }
        });

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

        if(candidateCount == 0){
            notifyError("No application found!");
            $("#noApplications").show();
        } else{
            notifySuccess(candidateCount + " application(s) found!");
        }
    } else {
        logoutRecruiter();
    }
}


function closeCreditModal() {
    $("#modalBuyCredits").closeModal();
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
        $("#unlock_candidate_" + returnedData.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 btn").addClass("contactUnlocked right").removeAttr('onclick');
    } else if(returnedData.status == 2){
        notifySuccess("You have already unlocked the candidate");
        getRecruiterInfo();
        $("#unlock_candidate_" + returnedData.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 btn").addClass("contactUnlocked right").removeAttr('onclick');
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
    var creditHistoryList = returnedData.recruiterCreditHistoryList;
    creditHistoryList.reverse();
    var contactCreditCount = 0;
    var interviewCreditCount = 0;
    creditHistoryList.forEach(function (creditHistory){
        if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 1){
            if(contactCreditCount == 0){
                $("#remainingContactCredits").html(creditHistory.recruiterCreditsAvailable);
                $("#remainingContactCreditsMobile").html(creditHistory.recruiterCreditsAvailable);
                contactCreditCount = 1;
            }
        } else{
            if(interviewCreditCount == 0){
                if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 2){
                    $("#remainingInterviewCredits").html(creditHistory.recruiterCreditsAvailable);
                    $("#remainingInterviewCreditsMobile").html(creditHistory.recruiterCreditsAvailable);
                    interviewCreditCount = 1;
                }
            }
        }
        if(contactCreditCount > 0 && interviewCreditCount > 0){
            return false;
        }
    });
}

function closeFeedbackModal() {
    $("#addFeedback").closeModal();
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

function openFeedbackModal(candidateId) {
    globalCandidateId = candidateId;
    $("#reasonVal").html('');
    var defaultOption = $('<option value="0" selected></option>').text("Select a reason");
    $('#reasonVal').append(defaultOption);

    notSelectedReason.forEach(function (reason) {
        var option = $('<option value=' + reason.id + '></option>').text(reason.name);
        $('#reasonVal').append(option);
    });

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
        notifySuccess("Feedback updated successfully. Refreshing the page..");
        setTimeout(function () {
            location.reload();
        }, 2000);
    } else if(returnedData == -1){
        notifyError("You are out of interview credits. Please purchase interview credits!");
        openCreditModal();
    } else{
        notifyError("Something went wrong. Please try again later");
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
    if(interviewDays.charAt(x.getDay() - 1) == '1'){
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
    window.location = "/recruiter";
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}

function validateSelectDate (val, text) {
    if(val.localeCompare(text) == 0){
        $('#select_date').tokenize().tokenRemove(val);
        notifyError("Please select a valid date from the list");
    } else{
        todayDay = new Date();
        if(val != 0){
            todayDay = new Date(val);
        }
        getJobApplications();
    }
}
