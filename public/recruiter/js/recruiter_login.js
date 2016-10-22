/**
 * Created by dodo on 11/10/16.
 */

var recruiterMobile;
function processDataLogin(returnedData) {
    if(returnedData.status == 1){
        if(returnedData.isCandidateVerified == 0){ //recruiter has not been verified
            requestOtp(recruiterMobile)
        } else{
            window.location = "/recruiter/home";
            notifySuccess("Login successful");
        }

    } else if(returnedData.status == 2){
        notifyError("Looks like something went wrong. Please try again later");
    } else if(returnedData.status == 3){
        notifyError("Recruiter does not exists");
    } else if(returnedData.status == 4){
        $("#loginSubmitBtn").removeClass("disabled");
        notifyError("Incorrect login credentials");
    }
}

function openLoginModal() {
    $("#passwordBtn").removeClass("disabled");
    $("#loginMobileNumber").val('');
    $("#password").val('');
    $("#modalSignUp").closeModal();
    $("#modalLogIn").openModal();
}

// login_recruiter_form ajax script
$(function() {
    $("#loginModal").submit(function(eventObj) {
        eventObj.preventDefault();
        var mobile = $("#loginMobileNumber").val();
        var password = $("#password").val();

        var res = validateMobile(mobile);

        var statusCheck = 1;
        if(res == 0){
            notifyError("Enter a valid mobile number");
            statusCheck = 0;
        } else if(res == 1){
            notifyError("Enter 10 digit mobile number");
            statusCheck = 0;
        }
        if(statusCheck == 1){
            $("#loginSubmitBtn").addClass("disabled");
            var d = {
                candidateLoginMobile: mobile,
                candidateLoginPassword: password
            };
            recruiterMobile = mobile;
            $.ajax({
                type: "POST",
                url: "/recruiterLoginSubmit",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataLogin
            });
        }
    }); // end of submit
}); // end of function