/**
 * Created by adarsh on 12/9/16.
 */
var partnerSessionStatus;

$(window).load(function() {
    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").delay(1000).fadeOut("slow");
});

$(document).ready(function(){
    checkPartnerLogin();
    try {
        $.ajax({
            type: "GET",
            url: "/getPartnerProfileInfo",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataPartnerProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function checkPartnerLogin() {
    try {
        $.ajax({
            type: "GET",
            url: "/checkPartnerSession",
            data: false,
            contentType: false,
            processData: false,
            success: processDataPartnerSession
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataPartnerSession(returnedData) {
    if(returnedData == 0){
        logoutUser();
    }
}

function processDataPartnerProfile(returnedData) {
    if(returnedData != null){
        //name
        if(returnedData.partnerFirstName != null){
            if(returnedData.partnerLastName != null){
                $("#partnerName").html(returnedData.partnerFirstName + " " + returnedData.partnerLastName);
            } else{
                $("#partnerName").html(returnedData.partnerFirstName);
                $("#partnerNameHeading").html("Hi! " + returnedData.partnerFirstName + "! Welcome to TruJobs");
            }
        }
        //mobile
        if(returnedData.partnerMobile != null){
            $("#partnerMobile").html(returnedData.partnerMobile);
        }

        //email
        if(returnedData.partnerEmail != null){
            $("#partnerEmail").html(returnedData.partnerEmail);
        }

        //partner company name
        if(returnedData.partnerCompany != null){
            $("#organizationName").html(returnedData.partnerCompany);
        }

        //partner company type
        if(returnedData.partnerType != null){
            $("#organizationType").html(returnedData.partnerType.partnerTypeName);
        }

        //partner company location
        if(returnedData.locality != null){
            $("#organizationLocation").html(returnedData.locality.localityName + ", Bangalore");
        }
    }
}

function logoutUser() {
    localStorage.clear();
    window.location = "/partner";
    try {
        $.ajax({
            type: "GET",
            url: "/logoutUser",
            data: false,
            contentType: false,
            processData: false,
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

