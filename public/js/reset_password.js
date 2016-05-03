/**
 * Created by batcoder1 on 28/4/16.
 */
/* forgot password | reset password */

function processDataResetCheckUser(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        document.getElementById("helpText").innerHTML = "Enter OTP sent on " + $('#resetPasswordMobile').val();
        $('#forgotPasswordAutoMobile').val($('#resetPasswordMobile').val());
        $('#form_password_reset_otp').show();
        $('#form_forgot_password').hide();
        $('#noUserLogin').hide();
    }

    else {
        document.getElementById("resetCheckUserBtn").disabled = false;
        $('#noUserLogin').show();
    }
}

function processDataCheckResetOtp(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        $('#forgotPasswordNewMobile').val($('#forgotPasswordAutoMobile').val());
        $('#form_password_reset_otp').hide();
        $('#form_password_reset_new').show();
        $('#wrongOtp').hide();
    }
        
    else {
        $('#wrongOtp').show();
    }
}

function processDataPostReset(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        localStorage.setItem("mobile", returnedData.candidateMobile);
        localStorage.setItem("name", returnedData.candidateName);
        localStorage.setItem("id", returnedData.candidateId);
        window.location = "/dashboard";
    }

    else if(returnedData.status == 2){
        document.getElementById("resetNewPasswordBtn").disabled = false;
        $('#incorrectMsg').show();
        $('#errorMsg').hide();
    }

    else {
        document.getElementById("resetNewPasswordBtn").disabled = false;
        $('#incorrectMsg').hide();
        $('#errorMsg').show();
    }
}

// form_forgot_password ajax script
$(function() {
    $("#form_forgot_password").submit(function(eventObj) {
        eventObj.preventDefault();
        document.getElementById("resetCheckUserBtn").disabled = true;
        try {
            var phone = $('#resetPasswordMobile').val();
            console.log("phone: " + phone);
            var s = {
                resetPasswordMobile : phone
            };
            $.ajax({
                type: "POST",
                url: "/checkCandidate",
                data: s,
                success: processDataResetCheckUser
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit

    $("#form_password_reset_otp").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var phone = $('#forgotPasswordAutoMobile').val();
            var otp = $('#candidateForgotOtp').val();
            console.log("phone: " + phone);
            var s = {
                candidateForgotOtp : otp,
                candidateForgotMobile : phone,
            };
            $.ajax({
                type: "POST",
                url: "/checkResetOtp",
                data: s,
                success: processDataCheckResetOtp
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit

    $("#form_password_reset_new").submit(function(eventObj) {
        eventObj.preventDefault();
        document.getElementById("resetNewPasswordBtn").disabled = true;
        try {
            var phone = $('#forgotPasswordNewMobile').val();
            var password = $('#candidateNewPassword').val();
            console.log("phone: " + phone);
            var s = {
                candidateNewPassword : password,
                forgotPasswordNewMobile : phone,
            };
            $.ajax({
                type: "POST",
                url: "/savePassword",
                data: s,
                success: processDataPostReset
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit
}); // end of function

