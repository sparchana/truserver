/**
 * Created by batcoder1 on 25/4/16.
 */
var returnedOtp;
var candidateMobile;
function processDataSignUpSubmit(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        returnedOtp = returnedData.otp;
        $('#myRegistrationModal').modal('show');
        $('#authMobile').val($('#candidateMobile').val());
        $('#candidateName').val('');
        $('#candidateSecondName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
        document.getElementById("helpTextSignUp").innerHTML = "Enter OTP sent on " + candidateMobile;
        $('#form_otp').show();

    }

    else if(returnedData.status == 3){
        alert("User already exists! Please login to continue");
        window.location = "/";
    }
    else {
        $('#errorMsg').show();
        $('#candidateName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
    }
}

function processDataAddAuth(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        // Store
        localStorage.setItem("mobile", "+91" + candidateMobile);
        localStorage.setItem("name", returnedData.candidateName);
        localStorage.setItem("lastName", returnedData.candidateLastName);
        localStorage.setItem("id", returnedData.candidateId);
        localStorage.setItem("leadId", returnedData.leadId);
        localStorage.setItem("assessed", returnedData.isAssessed);
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
                var secondName  = $('#candidateSecondName').val();
                var phone = $('#candidateMobile').val();
                candidateMobile = phone;
                $('#alreadyMsgCandidate').hide();
                var d = {
                    candidateName : name,
                    candidateSecondName : secondName,
                    candidateMobile : phone,
                    candidateLocality : $('#candidateLocality').val(),
                    candidateJobPref : $('#candidateJobPref').val()
                };

                $.ajax({
                    type: "POST",
                    url: "/signUp",
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
            var userOtp = $('#candidateOtp').val();
            if(userOtp == returnedOtp){
                    $('#incorrectOtpMsg').hide();
                    $('#form_otp').hide();
                    $('#form_auth').show();
                    $('#errorMsg').hide();
                    $('#incorrectMsg').hide();

                }
                else {
                    $('#incorrectOtpMsg').show();
                    $('#errorMsg').hide();
                    $('#candidateOtp').val('');
                }

    }); // end of submit
}); // end of function

// form_auth ajax script
$(function() {
    $("#form_auth").submit(function(eventObj) {
        eventObj.preventDefault();
        if(($('#candidatePassword').val()).length < 6){
            alert("Minimum 6 characters password required");
        }
        else {
            document.getElementById("btnSubmit").disabled = true;
            try {
                var authPassword = $('#candidatePassword').val();
                var authMobile = candidateMobile;
                var d = {
                    candidatePassword: authPassword,
                    candidateAuthMobile: authMobile
                }
                console.log("userMobile: " + authMobile);
                $.ajax({
                    type: "POST",
                    url: "/addPassword",
                    data: d,
                    success: processDataAddAuth
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }

    }); // end of submit
}); // end of function
