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
    $("#preloader").delay(500).fadeOut("slow");
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

function processDataPartnerProfile(returnedData) {
    if(returnedData == '0'){
        logoutPartner();
    }
    if(returnedData != null){
        //name
        if(returnedData.partnerFirstName != null){
            $("#partnerNameHeading").html("Hi! " + returnedData.partnerFirstName + "! Welcome to TruJobs");
            if(returnedData.partnerLastName != null){
                $("#partnerName").html(returnedData.partnerFirstName + " " + returnedData.partnerLastName);
            } else{
                $("#partnerName").html(returnedData.partnerFirstName);
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
        if(returnedData.partnerCompany == null || returnedData.partnerCompany == ""){
            $("#organizationName").html("Not Specified");
        } else {
            $("#organizationName").html(returnedData.partnerCompany);
        }

        //partner company type
        if(returnedData.partnerType != null){
            $("#organizationType").html(returnedData.partnerType.partnerTypeName);
            if(returnedData.partnerType.partnerTypeId == 7){
                $("#organizationType").html("Private Partner (  " + ")");
            }
        }

        //partner company location
        if(returnedData.locality != null){
            $("#organizationLocation").html(returnedData.locality.localityName + ", Bangalore");
        }
    }
}