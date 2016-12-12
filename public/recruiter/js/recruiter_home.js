/**
 * Created by dodo on 10/10/16.
 */

var newCount = 0;

var globalCandidateId;
var globalJpId;

var allReason = [];

$(window).load(function() {

    setTimeout(function(){
        $(".homeNav").addClass("active");
        $(".homeNavMobile").addClass("active");

        if(newCount == 0){
            $(".badge").hide();
        } else{
            $(".badge").show();
            $("#pendingApproval").addClass("newNotification").html(newCount + " new");
            $("#pendingApprovalMobile").addClass("newNotification").html(newCount + " new");
        }
    }, 100);
});

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

$(window).resize(function(){
    var width = $(window).width();
    if(width < 780){
        $("#editBtn").removeClass("btn-large").addClass("btn-small");
    } else{
        $("#editBtn").removeClass("btn-small").addClass("btn-large");
    }
});

$(document).scroll(function(){
    if ($(this).scrollTop() > 30) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});

$(document).ready(function(){
    checkRecruiterLogin();

    var width = $(window).width();
    if(width < 780){
        $("#editBtn").removeClass("btn-large").addClass("btn-small");
    } else{
        $("#editBtn").removeClass("btn-small").addClass("btn-large");
    }

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

function processDataNotSelectedReason(returnedData){
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allReason.push(item);
    });
}

function processDataGetJobPostDetails(returnedData) {
    var jobPostList = [];
    $.each(returnedData, function (key, value) {
        jobPostList.push(value);
    });
    newCount = 0;

    if(jobPostList.length == 0){
        $("#noInterviews").show();
    }

    var interviews = "";
    var x, i;
    var jpId = [];
    jobPostList.forEach(function (jobPost) {
        var interviewDays;

        newCount += jobPost.pendingCount;
        if (Object.keys(jobPost.jobPost.interviewDetailsList).length > 0) {
            var interviewDetailsList = jobPost.jobPost.interviewDetailsList;
            if (interviewDetailsList[0].interviewDays != null) {
                interviewDays = interviewDetailsList[0].interviewDays.toString(2);

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

            var today = new Date();
            if(interviewDays.charAt(today.getDay() - 1) == 1){ // today's schedule
                jpId.push(parseInt(jobPost.jobPost.jobPostId));
                var slotsToday = "";
                interviewDetailsList.forEach(function (slots) {
                    slotsToday += slots.interviewTimeSlot.interviewTimeSlotName + ", ";
                });

                interviews += '<div class="row" style="padding: 0 24px 0 24px">' +
                    '<div class="col s12 m5" style="font-size: 16px"><b>' + jobPost.jobPost.jobPostTitle + '</b></div>' +
                    '<div class="col s12 m4">' + slotsToday.substring(0, (slotsToday.length) -2 ) + '</div>' +
                    '<div class="col s12 m3"><a href="/recruiter/job/track/' + jobPost.jobPost.jobPostId + '" target="_blank">' +
                    '<button class="btn waves-effect waves-light" style="margin-top: -6px" type="submit" name="action">Track<i class="material-icons right">send</i></button>' +
                    '</a></div></div>';
            }
        }
    });

    if(jpId.length > 0){
        var d = {
            jpId: jpId
        };
        try {
            $.ajax({
                type: "POST",
                url: "/getTodayInterviewDetails",
                async: false,
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataInterviewToday
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function processDataInterviewToday(returnedData) {
    var parent = $("#tableBody");
    $("#noInterviews").show();
    var interviews = "";
    var lastUpdate = "";
    if(returnedData != null && Object.keys(returnedData).length > 0){
        returnedData.forEach(function (application) {
            var status = '<td style="color: #5a5a5a"><b>Not Available</b></td>';
            var homeLocality = "Not available";
            if(application.candidate.locality != null){
                homeLocality = application.candidate.locality.localityName;
            }

            if(application.lastUpdate != null) {
                var lastUpdateDate = new Date(application.lastUpdate);
                var timing = "";
                if(lastUpdateDate.getHours() > 12){
                    timing = lastUpdateDate.getHours() - 12 + ":" + lastUpdateDate.getMinutes() + " pm";
                } else{
                    timing = lastUpdateDate.getHours() + ":" + lastUpdateDate.getMinutes() + " am";
                }
                lastUpdate = " (" + lastUpdateDate.getDate() + "-" + getMonthVal(lastUpdateDate.getMonth() + 1) + "-"
                    + lastUpdateDate.getFullYear() + ", " + timing + ")";
            }

            if(application.currentStatus.statusId > JWF_STATUS_INTERVIEW_CONFIRMED){
                if(application.currentStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING || application.currentStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_DELAYED){ //not going or delayed
                    status = '<td style="color: red"><b>' + application.currentStatus.statusTitle + lastUpdate +'</b></td>'
                } else if(application.currentStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_STARTED || application.currentStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED) {
                    status = '<td style="color: green"><b>' + application.currentStatus.statusTitle + lastUpdate +'</b></td>'
                } else { // started or reached
                    status = '<td style="color: #5a5a5a"><b>-</b></td>'
                }
            }

            var feedback = '<td><a class="waves-effect waves-light btn" onclick="openFeedbackModal(' + application.candidate.candidateId + ', ' + application.jobPostWorkflow.jobPost.jobPostId + ')">Add Feedback</a></td>';
            if(application.currentStatus.statusId > JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){
                feedback = '<td style="color: red"><b> ' + application.currentStatus.statusTitle + '</b></td>';
                if(application.currentStatus.statusId == JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED){
                    feedback = '<td style="color: green"><b> ' + application.currentStatus.statusTitle + lastUpdate +'</b></td>';
                }
            }

            interviews += '<tr>' +
                '<td class="jobTitle"><a href="/recruiter/job/track/' + application.jobPostWorkflow.jobPost.jobPostId + '" target="_blank"><b>' + application.jobPostWorkflow.jobPost.jobPostTitle + '</b></a></td>' +
                '<td>' + application.candidate.candidateFullName + '</td>' +
                '<td>' + application.jobPostWorkflow.scheduledInterviewTimeSlot.interviewTimeSlotName + '</td>' +
                '<td>' + homeLocality + '</td>' +
                status +
                feedback +
                '</tr>';
        });

        $("#todayInterviewTable").show();
        $("#noInterviews").hide();
        parent.append(interviews);
    } else{
        $("#noInterviews").show();
        $("#todayInterviewTable").hide();
    }
}

function openFeedbackModal(candidateId, jpId) {
    globalCandidateId = candidateId;
    globalJpId = jpId;

    $("#reasonVal").html('');
    var defaultOption = $('<option value="0" selected></option>').text("Select a reason");
    $('#reasonVal').append(defaultOption);

    allReason.forEach(function (reason) {
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
                    jobPostId : globalJpId,
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

function processDataRecruiterProfile(returnedData) {
    if (returnedData == '0') {
        logoutRecruiter();
    } else{
        $("#recName").html(returnedData.recruiterProfileName);
        $("#recMobile").html(returnedData.recruiterProfileMobile);
        if(returnedData.recCompany != null)
            $("#recCompany").html(returnedData.recCompany.companyName);

        if(returnedData.recruiterLinkedinProfile != null && returnedData.recruiterLinkedinProfile != "")
            $("#recLinkedin").html(returnedData.recruiterLinkedinProfile);

        if(returnedData.company != null){
            if(returnedData.company.companyWebsite != null && returnedData.company.companyWebsite != ""){
                $("#recCompanyWebsite").html(returnedData.company.companyWebsite);
            }
        }


        if(returnedData.recruiterProfileEmail != null && returnedData.recruiterProfileEmail != "")
            $("#recEmail").html(returnedData.recruiterProfileEmail);

        var remainingContactCredits = 0;
        var remainingInterviewCredits;

        var creditHistoryList = returnedData.recruiterCreditHistoryList;
        creditHistoryList.reverse();
        var toCheckContactCreditCount = true;
        var toCheckInterviewCreditCount = true;
        creditHistoryList.forEach(function (creditHistory){
            try{
                if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 1){
                    if(toCheckContactCreditCount){
                        $("#remainingContactCredits").html(creditHistory.recruiterCreditsAvailable);
                        $("#remainingContactCreditsMobile").html(creditHistory.recruiterCreditsAvailable);
                        remainingContactCredits = parseInt(creditHistory.recruiterCreditsAvailable);
                        toCheckContactCreditCount = false;
                    }
                } else{
                    if(toCheckInterviewCreditCount){
                        if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 2){
                            $("#remainingInterviewCredits").html(creditHistory.recruiterCreditsAvailable);
                            $("#remainingInterviewCreditsMobile").html(creditHistory.recruiterCreditsAvailable);
                            toCheckInterviewCreditCount = false;
                        }
                    }
                }
                if((toCheckContactCreditCount == false) && (toCheckInterviewCreditCount ==false)){
                    return false;
                }
            } catch(err){}
        });

        if(remainingContactCredits > 0){
            $("#contactCreditCount").html(remainingContactCredits);
            $("#hasCredit").show();
            $("#hasNoCredit").hide();
        } else{
            $("#hasCredit").hide();
            $("#hasNoCredit").show();
        }
    }
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
            }
        }
        if($("#interviewCreditAmount").val() != ""){
            interviewCredits = parseInt($("#interviewCreditAmount").val());
            if(interviewCredits < 1){
                notifyError("Interview credits cannot be less than 1");
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

function closeCreditModal() {
    $("#modalBuyCredits").closeModal();
}

function closeFeedbackModal() {
    $("#addFeedback").closeModal();
}

function openCreditModal(){
    $("#successMsg").hide();
    $("#modalBuyCredits").openModal();
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
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

