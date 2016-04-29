/**
 * Created by batcoder1 on 25/4/16.
 */

function processDataSignUpSubmit(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        $('#autoCandidateMobile').val($('#candidateMobile').val());
        $('#candidateName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
        $('#form_signup_candidate').hide();
        document.getElementById("helpText").innerHTML = "Enter OTP sent on " + $('#autoCandidateMobile').val();
        $('#form_otp').show();

    }

    else if(returnedData.status == 3){
        $('#alreadyMsgCandidate').show();
        $('#candidateName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
    }
    else {
        $('#errorMsg').show();
        $('#candidateName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
    }
}

function processDataVerifyOtp(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        $('#candidateAuthMobile').val($('#autoCandidateMobile').val());
        $('#form_otp').hide();
        $('#form_auth').show();
        $('#errorMsg').hide();
        $('#incorrectMsg').hide();
        
    }
    else if(returnedData.status == 4){
        $('#errorMsg').hide();
        $('#incorrectMsg').show();
        $('#candidateOtp').val('');
    }

    else {
        $('#incorrectMsg').hide();
        $('#errorMsg').show();
    }
}

function processDataAddAuth(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        // Store
        localStorage.setItem("mobile", $('#candidateAuthMobile').val());
        localStorage.setItem("name", returnedData.candidateName);
        window.location = "/dashboard";
    }

    else {
        $('#errorMsg').show();
    }
}

// form_candidate ajax script
$(function() {
    $("#form_signup_candidate").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var name  = $('#candidateName').val();
            var phone = $('#candidateMobile').val();
            console.log("phone: " + phone);
            var d = {
                candidateName : name,
                candidateMobile : phone,
                candidateLocality : $('#candidateLocality').val(),
                candidateJobPref : $('#candidateJobPref').val()
            };

            $.ajax({
                type: "POST",
                url: "/signUpSubmit",
                data: d,
                success: processDataSignUpSubmit
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit
}); // end of function

// form_otp ajax script
$(function() {
    $("#form_otp").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var userOtp = $('#candidateOtp').val();
            var userMobile = $('#autoCandidateMobile').val();
            console.log("userOtp: " + userOtp);
            $.ajax({
                type: "POST",
                url: "/verifyOtp",
                data: $("#form_otp").serialize(),
                dataType: "json",
                success: processDataVerifyOtp
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit
}); // end of function

// form_auth ajax script
$(function() {
    $("#form_auth").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var authPassword = $('#candidatePassword').val();
            var authMobile = $('#authMobile').val();
            console.log("userMobile: " + authMobile);
            $.ajax({
                type: "POST",
                url: "/addPassword",
                data: $("#form_auth").serialize(),
                dataType: "json",
                success: processDataAddAuth
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit
}); // end of function
