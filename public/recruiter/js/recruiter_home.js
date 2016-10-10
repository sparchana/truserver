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
    console.log(returnedData);
    if (returnedData == '0') {
        logoutRecruiter();
    } else{
        $("#recName").html(returnedData.recruiterProfileName);
        $("#recMobile").html(returnedData.recruiterProfileMobile);
        if(returnedData.recCompany != null)
            $("#recCompany").html(returnedData.recCompany.companyName);

        if(returnedData.recruiterLinkedinProfile != null)
            $("#recLinkedin").html(returnedData.recruiterLinkedinProfile);

        if(returnedData.recruiterProfileLandline != "0")
            $("#recLandline").html(returnedData.recruiterProfileLandline);

        if(returnedData.recruiterProfileEmail != null)
            $("#recEmail").html(returnedData.recruiterProfileEmail);
    }
}