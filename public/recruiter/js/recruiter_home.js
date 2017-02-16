/**
 * Created by dodo on 10/10/16.
 */

var newCount = 0;

var globalCandidateId;
var globalJpId;

var allReason = [];

$(window).load(function() {
    if(window.location.href.indexOf('#signin') != -1) {
        $("#modalLogIn").openModal();
    }
    setTimeout(function(){
        $(".homeNav").addClass("active");
        $(".homeNavMobile").addClass("active");

        if(newCount == 0){
            $(".newNotification").hide();
        } else{
            $(".newNotification").show();
            $("#pendingApproval").html(newCount);
            $("#pendingApprovalMobile").html(newCount);
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
            url: "/getAllRecruiterJobPosts/?view=2",
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
    var jobPostList = returnedData;
    newCount = 0;

    if(jobPostList.length == 0){
        $("#noInterviews").show();
        $("#loadingIcon").hide();
    }

    var jpId = [];
    jobPostList.forEach(function (jobPost) {
        newCount += jobPost.pendingCount;
        newCount += jobPost.upcomingCount;
        jpId.push(parseInt(jobPost.jobPost.jobPostId));
    });

    if(jpId.length > 0){
        var d = {
            jpId: jpId
        };
        try {
            $.ajax({
                type: "POST",
                url: "/getTodayInterviewDetails",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataInterviewToday,
                error: function (jqXHR, exception) {
                    $("#somethingWentWrong").show();
                    $("#loadingIcon").hide();
                }
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
    var reason = "";
    if(returnedData != null && Object.keys(returnedData).length > 0){
        returnedData.forEach(function (application) {
            var status = '<td style="color: #5a5a5a"><b>Not Available</b></td>';
            var homeLocality = "Not available";
            if(application.candidate.locality != null){
                homeLocality = application.candidate.locality.localityName;
            }

            //checking if the last update is null ori not. If not, extracting the date and time of the last update
            if(application.lastUpdate != null) {
                var lastUpdateDate = new Date(application.lastUpdate);
                var timing = "";

                if(lastUpdateDate.getHours() == 12){
                    timing = minuteHourFormat(lastUpdateDate.getHours()) + ":" + minuteHourFormat(lastUpdateDate.getMinutes()) + " pm";
                } else if(lastUpdateDate.getHours() > 12){
                    timing = minuteHourFormat(lastUpdateDate.getHours() - 12) + ":" + minuteHourFormat(lastUpdateDate.getMinutes()) + " pm";
                } else{
                    timing = minuteHourFormat(lastUpdateDate.getHours()) + ":" + minuteHourFormat(lastUpdateDate.getMinutes()) + " am";
                }
                lastUpdate = " (Reported - " + lastUpdateDate.getDate() + "-" + getMonthVal(lastUpdateDate.getMonth() + 1) + "-"
                    + lastUpdateDate.getFullYear() + ", " + timing + ")";

                //if the update was done on or one day before the interview, setting the label as 'today' or 'yesterday'.
                var today = new Date();
                if(lastUpdateDate.getDate() == today.getDate() && lastUpdateDate.getMonth() == today.getMonth()){
                    lastUpdate = " (Reported - Today at: " + timing + ")";
                } else if(lastUpdateDate.getDate() == (today.getDate() -1) && lastUpdateDate.getMonth() == today.getMonth()){
                    lastUpdate = " (Reported - Yesterday at: " + timing + ")";
                }
            }

            if(application.reason != null){
                if(application.currentStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING) { //not going
                    reason = ' [Reason: ' + application.reason.reasonName + ']';
                } else{
                    reason = ' [Reaching: ' + application.reason.reasonName + ']';
                }
            }

            //setting current status here with respective text colours.
            if(application.currentStatus.statusId > JWF_STATUS_INTERVIEW_CONFIRMED){
                if(application.currentStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING || application.currentStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_DELAYED){ //not going or delayed
                    status = '<td style="color: red"><b>' + application.currentStatus.statusTitle + reason +'</b><br><font style="font-size: 12px">' + lastUpdate + '</font></td>'
                } else if(application.currentStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_ON_THE_WAY || application.currentStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED) {
                    status = '<td style="color: green"><b>' + application.currentStatus.statusTitle + reason +'</b><br><font style="font-size: 12px">' + lastUpdate + '</font></td>'
                } else { // started or reached
                    status = '<td style="color: #5a5a5a"><b>-</b></td>'
                }
            }

            //setting feedback button
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

    $("#loadingIcon").hide();
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
    } else{
        if(returnedData.recruiterAccessLevel == 0){
            $("#creditView").show();
            $("#creditViewMobile").show();
            $("#recruiterMsg").show();
            $("#recruiterHIW").show();
            $("#recruiterHighlights").show();
            $("#companyCode").hide();
        } else{

            $("#companyCode").show();
            $("#companyCode").html("Company Unique Code : " + returnedData.company.companyCode);
            $("#creditView").hide();
            $("#creditViewMobile").hide();
            $("#recruiterMsg").hide();
            $("#recruiterHIW").hide();
            $("#recruiterHighlights").hide();

            try {
                $.ajax({
                    type: "GET",
                    url: "/checkPrivateRecruiterPartnerAccount",
                    data: false,
                    contentType: false,
                    processData: false,
                    success: processDataCheckpartnerAccount
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }
}

function processDataRecruiterProfile(returnedData) {
    if (returnedData == '0') {
        logoutRecruiter();
    } else{
        $("#recName").html(returnedData.recruiterProfileName);
        $("#recMobile").html(returnedData.recruiterProfileMobile);
        if(returnedData.recCompany != null){
            $("#recCompany").html(returnedData.recCompany.companyName);
            if(returnedData.recruiterAccessLevel == 1){
                var parent = $("#recCompany");
                var privateLabel = document.createElement("span");
                privateLabel.textContent = "Private";
                privateLabel.style = "margin-left: 4px; border-radius: 4px; font-size: 12px; font-weight: bold; padding: 4px; color: white; background: #00a1ff";
                parent.append(privateLabel);
            } else if(returnedData.recruiterAccessLevel == 2){
                var parent = $("#recCompany");
                var privateLabel = document.createElement("span");
                privateLabel.textContent = "Admin";
                privateLabel.style = "margin-left: 4px; border-radius: 4px; font-size: 12px; font-weight: bold; padding: 4px; color: white; background: #00a1ff";
                parent.append(privateLabel);
            }


        }

        if(returnedData.recruiterLinkedinProfile != null && returnedData.recruiterLinkedinProfile != "")
            $("#recLinkedin").html(returnedData.recruiterLinkedinProfile);

        if(returnedData.company != null){
            if(returnedData.company.companyWebsite != null && returnedData.company.companyWebsite != ""){
                $("#recCompanyWebsite").html(returnedData.company.companyWebsite);
            }
        }


        if(returnedData.recruiterProfileEmail != null && returnedData.recruiterProfileEmail != "")
            $("#recEmail").html(returnedData.recruiterProfileEmail);

        $("#remainingContactCredits").html(returnedData.contactCreditCount);
        $("#remainingContactCreditsMobile").html(returnedData.contactCreditCount);
        $("#remainingInterviewCredits").html(returnedData.interviewCreditCount);
        $("#remainingInterviewCreditsMobile").html(returnedData.interviewCreditCount);

        var remainingContactCredits = returnedData.contactCreditCount;
        var remainingInterviewCredits = returnedData.interviewCreditCount;

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

function processDataCheckpartnerAccount(returnedData) {
    if(returnedData == 1){
        $("#accountSwitcher").show();
        $("#accountSwitcherMobile").show();
    } else{
        $("#accountSwitcher").hide();
        $("#accountSwitcherMobile").hide();
    }
}

function switchToPartner() {
    try {
        $.ajax({
            type: "GET",
            url: "/switchToPartner",
            data: false,
            contentType: false,
            processData: false,
            success: processDataPartnerSwitch
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataPartnerSwitch(returnedData) {
    if(returnedData == 1){
        window.location = "/partner";
    } else{
        notifyError("Something went wrong");
    }
}


