/**
 * Created by dodo on 10/10/16.
 */

var globalCandidateId;
var globalJpId;

var allReason = [];

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
    var interviews = "";
    var x, i;
    var jpId = [];
    returnedData.forEach(function (jobPost) {
        var interviewDays;

        if (Object.keys(jobPost.interviewDetailsList).length > 0) {
            var interviewDetailsList = jobPost.interviewDetailsList;
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
                jpId.push(parseInt(jobPost.jobPostId));
                var slotsToday = "";
                interviewDetailsList.forEach(function (slots) {
                    slotsToday += slots.interviewTimeSlot.interviewTimeSlotName + ", ";
                });

                interviews += '<div class="row" style="padding: 0 24px 0 24px">' +
                    '<div class="col s12 m5" style="font-size: 16px"><b>' + jobPost.jobPostTitle + '</b></div>' +
                    '<div class="col s12 m4">' + slotsToday.substring(0, (slotsToday.length) -2 ) + '</div>' +
                    '<div class="col s12 m3"><a href="/recruiter/job/track/' + jobPost.jobPostId + '" target="_blank">' +
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

        try {
            $.ajax({
                type: "POST",
                url: "/getPendingCandidateApproval",
                async: true,
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataPendingApproval
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function processDataPendingApproval(returnedData) {
    $("#pendingApproval").addClass("newNotification").html(returnedData + " new");
}
function processDataInterviewToday(returnedData) {
    var parent = $("#tableBody");
    var interviews = "";
    if(returnedData != null && Object.keys(returnedData).length > 0){
        returnedData.forEach(function (application) {
            var status = '<td style="color: #5a5a5a"><b>Not Available</b></td>';
            var homeLocality = "Not available";
            if(application.candidate.locality != null){
                homeLocality = application.candidate.locality.localityName;
            }

            if(application.currentStatus.statusId > 9){
                if(application.currentStatus.statusId == 10 || application.currentStatus.statusId == 11){ //not going or delayed
                    status = '<td style="color: red"><b>' + application.currentStatus.statusTitle + '</b></td>'
                } else if(application.currentStatus.statusId == 12 || application.currentStatus.statusId == 13) {
                    status = '<td style="color: green"><b>' + application.currentStatus.statusTitle + '</b></td>'
                } else { // started or reached
                    status = '<td style="color: #5a5a5a"><b>-</b></td>'
                }
            }

            var feedback = '<td><a class="waves-effect waves-light btn" onclick="openFeedbackModal(' + application.candidate.candidateId + ', ' + application.jobPostWorkflow.jobPost.jobPostId + ')">Add Feedback</a></td>';
            if(application.currentStatus.statusId > 13){
                feedback = '<td style="color: red"><b> ' + application.currentStatus.statusTitle + '</b></td>';
                if(application.currentStatus.statusId == 14){
                    feedback = '<td style="color: green"><b> ' + application.currentStatus.statusTitle + '</b></td>';
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
        openCreditModal()
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

        var remainingContactCredits;
        var remainingInterviewCredits;

        var creditHistoryList = returnedData.recruiterCreditHistoryList;
        creditHistoryList.reverse();
        var contactCreditCount = 0;
        var interviewCreditCount = 0;
        creditHistoryList.forEach(function (creditHistory){
            try{
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
            } catch(err){}
        });
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

