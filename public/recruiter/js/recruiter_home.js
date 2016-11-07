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
    if ($(this).scrollTop() > 40) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});

$(document).ready(function(){
    $(".button-collapse").sideNav();

    $('.dropdown-button').dropdown({
            inDuration: 300,
            outDuration: 225,
            constrain_width: false, // Does not change width of dropdown to that of the activator
            gutter: 0, // Spacing from edge
            alignment: 'left' // Displays dropdown with edge aligned to the left of button
        }
    );
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
});

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
