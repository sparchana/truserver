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
        if(applyJobFlag == 1){
            $("#myLoginModal").modal("hide");
            applyJob(applyJobId, prefLocation, false);
            $("#customSubMsg").html("Logging in ...");
            $('#customSubMsg').modal({backdrop: 'static', keyboard: false});
            var jp_id = applyJobId;
            applyJobFlag = 0;
            applyJobId = 0;
            setTimeout(function(){
                window.location = "/dashboard/appliedJobs/?assessment=true&jp_id="+jp_id;
            }, 3000);
        } else{
            window.location = "/dashboard";
        }
        localStorage.setItem("mobile", "+91" + candidateMobile);
        localStorage.setItem("name", returnedData.candidateFirstName);
        localStorage.setItem("lastName", returnedData.candidateLastName);
        localStorage.setItem("assessed", returnedData.isAssessed);
        localStorage.setItem("minProfile", returnedData.minProfile);
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

function requestOtp(phone) {
        candidateMobile = phone;
        document.getElementById("resetCheckUserBtn").disabled = true;
        try {
            var s = {
                resetPasswordMobile : phone
            };
            $.ajax({
                type: "POST",
                url: "/findUserAndSendOtp",
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
            alert("Enter a valid mobile number");
        }
        else if(phoneRes == 1){ // mobile no. less than 1 digits
            alert("Enter 10 digit mobile number");
        }
        else {
            requestOtp(phone);
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
        var userPwd = $('#candidateNewPassword').val();
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
                var password = $('#candidateNewPassword').val();
                var s = {
                    candidatePassword : password,
                    candidateAuthMobile : phone
                };
                $.ajax({
                    type: "POST",
                    url: "/addPassword",
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

