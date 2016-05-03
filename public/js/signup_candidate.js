/**
 * Created by batcoder1 on 25/4/16.
 */

function processDataSignUpSubmit(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        $('#myRegistrationModal').modal('show');
        $('#autoCandidateMobile').val($('#candidateMobile').val());
        $('#candidateName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
        $('#candidateMobile').val('');
        $('#candidateName').val('');
        document.getElementById("helpTextSignup").innerHTML = "Enter OTP sent on " + $('#autoCandidateMobile').val();
        $('#form_otp').show();

    }

    else if(returnedData.status == 3){
        document.getElementById("registerBtn").disabled = false;
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
        $('#incorrectOtpMsg').hide();
        $('#candidateAuthMobile').val($('#autoCandidateMobile').val());
        $('#form_otp').hide();
        $('#form_auth').show();
        $('#errorMsg').hide();
        $('#incorrectMsg').hide();
        
    }
    else if(returnedData.status == 4){
        $('#incorrectOtpMsg').show();
        $('#errorMsg').hide();
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
        localStorage.setItem("mobile", "+91" + $('#candidateAuthMobile').val());
        localStorage.setItem("name", returnedData.candidateName);
        localStorage.setItem("id", returnedData.candidateId);
        console.log(returnedData.candidateId);
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
        var localitySelected = $('#candidateLocality').val();
        var jobSelected = $('#candidateJobPref').val();
        if (localitySelected == "") {
            alert("Please Enter your Job Localities");
        } else if (jobSelected == "") {
            alert("Please Enter the Jobs you are Interested");
        }
        else{
            document.getElementById("registerBtn").disabled = true;
            try {
                var name  = $('#candidateName').val();
                var phone = $('#candidateMobile').val();
                console.log("phone: " + phone);
                console.log($('#candidateLocality').val());
                $('#alreadyMsgCandidate').hide();
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
        document.getElementById("btnSubmit").disabled = true;
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
