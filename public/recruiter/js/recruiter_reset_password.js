/**
 * Created by dodo on 22/10/16.
 */

var recruiterMobile;

function processDataResetCheckUser(returnedData) {
    if(returnedData.status == 1) {
        returnedOtp = returnedData.otp;
        document.getElementById("helpText").innerHTML = "Enter OTP sent on " + $('#resetPasswordMobile').val();
        $('#form_password_reset_otp').show();
        $('#form_forgot_password').hide();
        $('#loginModal').hide();
        $('#noUserLogin').hide();
    }

    else {
        document.getElementById("resetCheckUserBtn").disabled = false;
        notifyError("Recruiter does not exists! please signup");
    }
}

function processDataPostReset(returnedData) {
    console.log(returnedData);
    if(returnedData.status == 1){
        window.location = "/recruiter/home";
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

function requestOtp(phone) {
    recruiterMobile = phone;
    document.getElementById("resetCheckUserBtn").disabled = true;
    try {
        var s = {
            resetPasswordMobile : phone
        };
        $.ajax({
            type: "POST",
            url: "/findRecruiterAndSendOtp",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(s),
            success: processDataResetCheckUser
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

// form_forgot_password ajax script
$(function() {
    $("#form_forgot_password").submit(function(eventObj) {
        eventObj.preventDefault();
        var phone = $('#resetPasswordMobile').val();
        var phoneRes = validateMobile(phone);
        if(phoneRes == 0){ // invalid mobile
            notifyError("Enter a valid mobile number");
        }
        else if(phoneRes == 1){ // mobile no. less than 1 digits
            notifyError("Enter 10 digit mobile number");
        }
        else{
            requestOtp(phone);
        }
    }); // end of submit

    $("#form_password_reset_otp").submit(function(eventObj) {
        eventObj.preventDefault();
        var userOtp = $('#recruiterForgotOtp').val();

        if(validateOtp(userOtp) == 0){
            notifyError("Please enter a valid 4 digit otp!");
        } else{
            if(userOtp == returnedOtp){
                $('#form_password_reset_otp').hide();
                $('#form_password_reset_new').show();
                $('#wrongOtp').hide();
            }
            else {
                notifyError("Incorrect OTP")
            }
        }
    }); // end of submit

    $("#form_password_reset_new").submit(function(eventObj) {
        eventObj.preventDefault();
        var userPwd = $('#recruiterNewPassword').val();
        var passwordCheck = validatePassword(userPwd);
        if(passwordCheck == 0){
            notifyError("Please set min 6 chars for password");
        } else if(passwordCheck == 1){
            notifyError("Password cannot have blank spaces. Enter a valid password");
        }
        else{
            document.getElementById("resetNewPasswordBtn").disabled = true;
            try {
                var s = {
                    recruiterAuthMobile : recruiterMobile,
                    recruiterPassword : userPwd
                };
                $.ajax({
                    type: "POST",
                    url: "/addRecruiterPassword",
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
