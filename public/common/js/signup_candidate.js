/**
 * Created by batcoder1 on 25/4/16.
 */
var returnedOtp;
var candidateMobile;
var applyJobFlag = 0;
var applyJobId = 0;
function processDataSignUpSubmit(returnedData) {
    if(returnedData.status == 1) {
        returnedOtp = returnedData.otp;
        try{
            $('#mySignUpModal').modal('hide');
        } catch (err){
            console.log(err);
        }
        $('#myRegistrationModal').modal('show');
        $('#authMobile').val($('#candidateMobile').val());
        $('#candidateFirstName').val('');
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
        $('#candidateFirstName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
    }
}

function processDataAddAuth(returnedData) {
    if(returnedData.status == 1) {
        // Store
        localStorage.setItem("gender", returnedData.gender);
        localStorage.setItem("mobile", "+91" + candidateMobile);
        localStorage.setItem("name", returnedData.candidateFirstName);
        localStorage.setItem("lastName", returnedData.candidateLastName);
        localStorage.setItem("id", returnedData.candidateId);
        localStorage.setItem("leadId", returnedData.leadId);
        localStorage.setItem("assessed", returnedData.isAssessed);
        localStorage.setItem("minProfile", returnedData.minProfile);

        if(applyJobFlag == 1){
            $("#myRegistrationModal").modal("hide");
            applyJobSubmitViaCandidate(applyJobId, prefLocation, prefTimeSlot, scheduledInterviewDate, true);
            // applyJob(applyJobId, prefLocation, false);
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
        var firstName = $('#candidateFirstName').val();
        var lastName = $('#candidateSecondName').val();
        var phone = $('#candidateMobile').val();
        var firstNameCheck = validateName(firstName);
        if(lastName != ""){
            var lastNameCheck = validateName(lastName);
        }
        var res = validateMobile(phone);
        var homeLocalitySelected = $('#candidateHomeLocality').val();
        var jobSelected = $('#candidateJobPref').val();

        //checking first name
        switch(firstNameCheck){
            case 0: alert("First name contains number. Please Enter a valid First Name"); statusCheck=0; break;
            case 2: alert("First Name cannot be blank spaces. Enter a valid first name"); statusCheck=0; break;
            case 3: alert("First name contains special symbols. Enter a valid first name"); statusCheck=0; break;
            case 4: alert("Please enter your first name"); statusCheck=0; break;
        }

        if(res == 0){
            alert("Enter a valid mobile number");
            statusCheck=0;
        } else if(res == 1){
            alert("Enter 10 digit mobile number");
            statusCheck=0;
        } else if(homeLocalitySelected == "") {
            alert("Please Enter your Home Locality");
            statusCheck=0;
        }
        else if(jobSelected == "") {
            alert("Please Enter the Jobs you are Interested");
            statusCheck=0;
        }

        //checking last name
        switch(lastNameCheck){
            case 0: alert("Last name contains number. Please Enter a valid Last Name"); statusCheck=0; break;
            case 2: alert("Last Name cannot be blank spaces. Enter a valid Last name"); statusCheck=0; break;
            case 3: alert("Last name contains special symbols. Enter a valid Last name"); statusCheck=0; break;
            case 4: alert("Please enter your Last name"); statusCheck=0; break;
        }
        
        if(statusCheck == 1){
            candidateMobile = phone;
            $("#registerBtnSubmit").addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Please Wait");
            document.getElementById("registerBtnSubmit").disabled = true;
            try {
                var candidatePreferredJob = [];

                var jobPref = $('#candidateJobPref').val().split(",");

                var i;
                for(i=0;i<jobPref.length; i++){
                    candidatePreferredJob.push(parseInt(jobPref[i]));
                }

                $('#alreadyMsgCandidate').hide();
                var d = {
                    candidateFirstName : firstName,
                    candidateSecondName : lastName,
                    candidateMobile : phone,
                    candidateHomeLocality : homeLocalitySelected,
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

$(function() {
    $("#form_signup_candidate_modal").submit(function(eventObj) {
        eventObj.preventDefault();
        var statusCheck = 1;
        var firstName = $('#candidateFirstNameModal').val();
        var lastName = $('#candidateSecondNameModal').val();
        var phone = $('#candidateMobileModal').val();
        var firstNameCheck = validateName(firstName);
        if(lastName != ""){
            var lastNameCheck = validateName(lastName);
        }
        var res = validateMobile(phone);
        var homeLocalitySelected = $('#candidateHomeLocalityModal').val();
        var jobSelected = $('#candidateJobPrefModal').val();

        //checking first name
        switch(firstNameCheck){
            case 0: alert("First name contains number. Please Enter a valid First Name"); statusCheck=0; break;
            case 2: alert("First Name cannot be blank spaces. Enter a valid first name"); statusCheck=0; break;
            case 3: alert("First name contains special symbols. Enter a valid first name"); statusCheck=0; break;
            case 4: alert("Please enter your first name"); statusCheck=0; break;
        }

        if(res == 0){
            alert("Enter a valid mobile number");
            statusCheck=0;
        } else if(res == 1){
            alert("Enter 10 digit mobile number");
            statusCheck=0;
        } else if(homeLocalitySelected == "") {
            alert("Please Enter your Home Locality");
            statusCheck=0;
        }
        else if(jobSelected == "") {
            alert("Please Enter the Jobs you are Interested");
            statusCheck=0;
        }

        //checking last name
        switch(lastNameCheck){
            case 0: alert("Last name contains number. Please Enter a valid Last Name"); statusCheck=0; break;
            case 2: alert("Last Name cannot be blank spaces. Enter a valid Last name"); statusCheck=0; break;
            case 3: alert("Last name contains special symbols. Enter a valid Last name"); statusCheck=0; break;
            case 4: alert("Please enter your Last name"); statusCheck=0; break;
        }

        if(statusCheck == 1){
            candidateMobile = phone;
            try{
                document.getElementById("registerBtnSubmit").disabled = true;
            } catch (e){}
            try{
                document.getElementById("registerBtnSubmitModal").disabled = true;
            } catch(e){}
            try {
                var candidatePreferredJob = [];

                var jobPref = $('#candidateJobPrefModal').val().split(",");

                var i;
                for(i=0;i<jobPref.length; i++){
                    candidatePreferredJob.push(parseInt(jobPref[i]));
                }

                $('#alreadyMsgCandidate').hide();
                var d = {
                    candidateFirstName : firstName,
                    candidateSecondName : lastName,
                    candidateMobile : phone,
                    candidateHomeLocality : homeLocalitySelected,
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

//verify otp
function verifyOtp(){
    var userOtp = $('#candidateOtp').val();
    if(validateOtp(userOtp) == 0){
        alert("Please enter a valid 4 digit otp!");
    } else{
        if (userOtp == returnedOtp) {
            $('#incorrectOtpMsg').hide();
            $('#form_otp').hide();
            $('#form_auth').show();
            $('#errorMsg').hide();
            $('#incorrectMsg').hide();
        } else {
            $('#incorrectOtpMsg').show();
            $('#errorMsg').hide();
            $('#candidateOtp').val('');
        }
    }
}

// form_auth ajax script
$(function() {
    $("#form_auth").submit(function(eventObj) {
        eventObj.preventDefault();
        var userPwd = $('#candidatePassword').val();
        var passwordCheck = validatePassword(userPwd);
        if(passwordCheck == 0){
            alert("Please set min 6 chars for password");
        } else if(passwordCheck == 1){
            alert("Password cannot have blank spaces. Enter a valid password");
        }
        else{
            document.getElementById("btnSubmit").disabled = true;
            try {
                var authPassword = $('#candidatePassword').val();
                var authMobile = candidateMobile;
                var d = {
                    candidatePassword: authPassword,
                    candidateAuthMobile: authMobile
                };
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
