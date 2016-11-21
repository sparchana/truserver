/**
 * Created by dodo on 10/10/16.
 */

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

function processDataGetJobPostDetails(returnedData) {
    var interviews = "";
    var x, i;
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

    if(interviews == ""){
        interviews = '<center style="font-size: 18px">No Interviews Today</center>';
        $("#today_interview").append(interviews);

    } else{
        var interviewBody = '<div class="row" style="padding: 0 24px 0 24px; color: #56B4D3">' +
            '<div class="col s12 m5" style="font-size: 18px; font-weight: bold">Job Title</div>' +
            '<div class="col s12 m4"  style="font-size: 18px; font-weight: bold">Today\'s Interview Slot(s)</div>' +
            '<div class="col s12 m3"  style="font-size: 18px; font-weight: bold">Action</div></div><hr>' +
            interviews;
        $("#today_interview").append(interviewBody);
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
            if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 1){
                if(contactCreditCount == 0){
                    remainingContactCredits = creditHistory.recruiterCreditsAvailable;
                    contactCreditCount = 1;
                }
            } else{
                if(interviewCreditCount == 0){
                    if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 2){
                        remainingInterviewCredits = creditHistory.recruiterCreditsAvailable;
                        interviewCreditCount = 1;
                    }
                }
            }
            if(contactCreditCount > 0 && interviewCreditCount > 0){
                return false;
            }
        });

        if(remainingContactCredits > 0){
            $("#contactCreditCount").html(remainingContactCredits)
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

function openCreditModal(){
    $("#successMsg").hide();
    $("#modalBuyCredits").openModal();
}
