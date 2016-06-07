/**
 * Created by batcoder1 on 28/4/16.
 */

var returnedOtp;
var candidateMobile;
function processDataResetCheckUser(returnedData) {
    console.log("returedData :" + returnedData.status + " " + returnedData.otp);
    if(returnedData.status == 1) {
        returnedOtp = returnedData.otp;
        document.getElementById("helpText").innerHTML = "Enter OTP sent on " + $('#resetPasswordMobile').val();
        $('#form_password_reset_otp').show();
        $('#form_forgot_password').hide();
        $('#noUserLogin').hide();
    }

    else {
        document.getElementById("resetCheckUserBtn").disabled = false;
        $('#noUserLogin').show();
    }
}

function processDataPostReset(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {

        localStorage.setItem("mobile", "+91" + candidateMobile);
        localStorage.setItem("name", returnedData.candidateName);
        localStorage.setItem("lastName", returnedData.candidateLastName);
        localStorage.setItem("id", returnedData.candidateId);
        localStorage.setItem("leadId", returnedData.leadId);
        localStorage.setItem("assessed", returnedData.isAssessed);
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
            candidateMobile = phone;
            var s = {
                resetPasswordMobile : phone
            };
            $.ajax({
                type: "POST",
                url: "/findUserAndSendOtp",
                data: s,
                success: processDataResetCheckUser
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit

    $("#form_password_reset_otp").submit(function(eventObj) {
        eventObj.preventDefault();
        var userOtp = $('#candidateForgotOtp').val();
        if(userOtp == returnedOtp){
            $('#form_password_reset_otp').hide();
            $('#form_password_reset_new').show();
            $('#wrongOtp').hide();
        }
        else {
            $('#wrongOtp').show();
        }
    }); // end of submit

    $("#form_password_reset_new").submit(function(eventObj) {
        eventObj.preventDefault();
        if(($('#candidateNewPassword').val()).length < 6){
            alert("Minimum 6 characters password required");
        }
        else{
            document.getElementById("resetNewPasswordBtn").disabled = true;
            try {
                var phone = candidateMobile;
                var password = $('#candidateNewPassword').val();
                console.log("phone: " + phone);
                var s = {
                    candidatePassword : password,
                    candidateAuthMobile : phone,
                };
                $.ajax({
                    type: "POST",
                    url: "/addPassword",
                    data: s,
                    success: processDataPostReset
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }); // end of submit
}); // end of function

