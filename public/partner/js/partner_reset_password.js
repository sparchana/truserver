/**
 * Created by batcoder1 on 28/4/16.
 */

var returnedOtp;
var candidateMobile;
function processDataResetCheckUser(returnedData) {
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
    if(returnedData.status == 1) {
        localStorage.setItem("mobile", "+91" + candidateMobile);
        localStorage.setItem("name", returnedData.candidateFirstName);
        window.location = "/partner/home";
    } else if(returnedData.status == 2){
        document.getElementById("resetNewPasswordBtn").disabled = false;
        $('#incorrectMsg').show();
        $('#errorMsg').hide();
    } else {
        document.getElementById("resetNewPasswordBtn").disabled = false;
        $('#incorrectMsg').hide();
        $('#errorMsg').show();
    }
}

// form_forgot_password ajax script
$(function() {
    $("#form_forgot_password").submit(function(eventObj) {
        eventObj.preventDefault();
        var phone = $('#resetPasswordMobile').val();
        var phoneRes = validateMobile(phone);
        if(phoneRes == 0){ // invalid mobile
            alert("Enter a valid mobile number");
        }
        else if(phoneRes == 1){ // mobile no. less than 1 digits
            alert("Enter 10 digit mobile number");
        }
        else{
            candidateMobile = phone;
            document.getElementById("resetCheckUserBtn").disabled = true;
            try {
                var s = {
                    resetPasswordMobile : phone
                };
                $.ajax({
                    type: "POST",
                    url: "/findPartnerAndSendOtp",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(s),
                    success: processDataResetCheckUser
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }); // end of submit

    $("#form_password_reset_otp").submit(function(eventObj) {
        eventObj.preventDefault();
        var userOtp = $('#partnerForgotOtp').val();

        if(validateOtp(userOtp) == 0){
            alert("Please enter a valid 4 digit otp!");
        } else{
            if(userOtp == returnedOtp){
                $('#form_password_reset_otp').hide();
                $('#form_password_reset_new').show();
                $('#wrongOtp').hide();
            }
            else {
                $('#wrongOtp').show();
            }
        }
    }); // end of submit

    $("#form_password_reset_new").submit(function(eventObj) {
        eventObj.preventDefault();
        var userPwd = $('#partnerNewPassword').val();
        var passwordCheck = validatePassword(userPwd);
        if(passwordCheck == 0){
            alert("Please set min 6 chars for password");
        } else if(passwordCheck == 1){
            alert("Password cannot have blank spaces. Enter a valid password");
        }
        else{
            document.getElementById("resetNewPasswordBtn").disabled = true;
            try {
                var phone = candidateMobile;
                var password = userPwd;
                console.log("phone: " + phone);
                var s = {
                    partnerPassword : password,
                    partnerAuthMobile : phone,
                };
                $.ajax({
                    type: "POST",
                    url: "/addPartnerPassword",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(s),
                    success: processDataPostReset
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }); // end of submit
}); // end of function

