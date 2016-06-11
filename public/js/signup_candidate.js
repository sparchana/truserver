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
        var statusCheck = 1;
        var firstName = $('#candidateName').val();
        var lastName = $('#candidateSecondName').val();
        var phone = $('#candidateMobile').val();
        var firstNameCheck = validateName(firstName);
        var lastNameCheck = validateName(lastName);
        var res = validateMobile(phone);
        var localitySelected = $('#candidateLocality').val();
        var jobSelected = $('#candidateJobPref').val();
        
        if(firstNameCheck == 0){
            alert("Please Enter First Name");
            statusCheck=0;
        } 
        else if(lastNameCheck == 0){
            alert("Please Enter your Last Name");
            statusCheck=0;
        }
        else if(res == 0){ // invalid mobile
            alert("Enter a valid mobile number");
            statusCheck=0;
        }
        else if(res == 1){ // mobile no. less than 1 digits
            alert("Enter 10 digit mobile number");
            statusCheck=0;
        }
        else if(localitySelected == "") {
            alert("Please Enter your Job Localities");
            statusCheck=0;
        }
        else if(jobSelected == "") {
            alert("Please Enter the Jobs you are Interested");
            statusCheck=0;
        }
        if(statusCheck == 1){
            candidateMobile = phone;
            document.getElementById("registerBtn").disabled = true;
            try {
                var candidatePreferredJob = [];
                var candidatePreferredLocality = [];

                var jobPref = $('#candidateJobPref').val().split(",");
                var localityPref = $('#candidateLocality').val().split(",");

                var i;
                for(i=0;i<jobPref.length; i++){
                    candidatePreferredJob.push(parseInt(jobPref[i]));
                }

                for(i=0;i<localityPref.length; i++){
                    candidatePreferredLocality.push(parseInt(localityPref[i]));
                }

                $('#alreadyMsgCandidate').hide();
                var d = {
                    candidateName : firstName,
                    candidateSecondName : lastName,
                    candidateMobile : phone,
                    candidateLocality : candidatePreferredLocality,
                    candidateJobPref : candidatePreferredJob
                };

                $.ajax({
                    type: "POST",
                    url: "/signUp",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
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
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataAddAuth
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }

    }); // end of submit
}); // end of function
