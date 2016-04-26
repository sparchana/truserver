/**
 * Created by batcoder1 on 25/4/16.
 */

function processData(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        var userMobile = document.getElementById("candidateMobile").value;
        $('#autoCandidateMobile').val($('#candidateMobile').val());
        $('#candidateName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
        $('#form_signup_candidate').hide();
        $('#form_otp').show();

    }

    else if(returnedData.status == 3){
        $('#candidateName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
    }
    else {
        $('#candidateName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
    }
}

function processDataVerifyOtp(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        $('#form_otp').hide();
        $('#thanksMsg').show();
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

// form_candidate ajax script
$(function() {
    $("#form_signup_candidate").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var name  = $('#candidateName').val();
            var phone = $('#candidateMobile').val();
            var email = $('#candidateEmail').val();
            console.log("phone: " + phone);
            $.ajax({
                type: "POST",
                url: "/signUpSubmit",
                data: $("#form_signup_candidate").serialize(),
                dataType: "json",
                success: processData
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
