/**
 * Created by adarsh on 10/9/16.
 */
var returnedOtp;
var recruiterMobileVal;

function processDataLogin(returnedData) {
    console.log(returnedData);
    if(returnedData.status == 1){
        window.location = "/recruiter/home";
        notifySuccess("Login successful");
    } else if(returnedData.status == 2){
        notifyError("Looks like something went wrong. Please try again later");
    } else if(returnedData.status == 3){
        notifyError("Recruiter does not exists");
    } else if(returnedData.status == 4){
        notifyError("Incorrect login credentials");
    }
}

function processDataLeadSubmit(returnedData) {
    if(returnedData.status = 1){
        notifySuccess("Thanks! Our business team will get in touch with you within 24 hours!");

        //for mobile inputs
        $('#recEnquiry').closeModal();
        $("#requestLeadModal").removeClass("disabled");
        $('#mobileNumberModal').val('');
        $('#jobRoleOptionModal').tokenize().clear();
        $('#jobLocationOptionModal').tokenize().clear();
        $('#recruiterRequirementModal').tokenize().clear();

        $('#modalHire').closeModal();
        $("#requestLeadBtn").removeClass("disabled");
        $('#mobileNumber').val('');
        $('#jobRoleOption').tokenize().clear();
        $('#jobLocationOption').tokenize().clear();
        $('#recruiterRequirement').tokenize().clear();

    } else{
        notifyError("Oops! Looks like something went wrong! Please try again after some time!");
    }
}

function resetHireForm() {
    $('#mobileNumber').val('');
    $('#jobRoleOption').tokenize().clear();
    $('#jobLocationOption').tokenize().clear();
    $('#recruiterRequirement').tokenize().clear();
}

function resetHireFormMobile() {
    $('#mobileNumberModal').val('');
    $('#jobRoleOptionModal').tokenize().clear();
    $('#jobLocationOptionModal').tokenize().clear();
    $('#recruiterRequirementModal').tokenize().clear();
}

function openSignUpModal() {
    $("#SignSubmitUpBtn").removeClass("disabled");
    $("#rec_name").val('');
    $("#rec_email").val('');
    $("#rec_mobile").val('');
    $("#rec_company").val('');
    $("#modalLogIn").closeModal();
    $("#modalSignUp").openModal();
}

function openLoginModal() {
    $("#passwordBtn").removeClass("disabled");
    $("#loginMobileNumber").val('');
    $("#password").val('');
    $("#modalSignUp").closeModal();
    $("#modalLogIn").openModal();
}

function processDataSignUpSubmit(returnedData) {
    if(returnedData.status == 1){
        $('#modalSignUp').closeModal();
        $("#modalOtp").openModal();

        $("#otpSection").show();
        $("#passwordSection").hide();

        returnedOtp = returnedData.otp;
    }
}

function verifyOtp(){
    var recruiterOtp = $("#rec_otp").val();
    if(validateOtp(recruiterOtp) == 0){
        notifyError("Please enter a valid 4 digit otp!");
    } else{
        if(recruiterOtp == returnedOtp){
            $("#otpSection").hide();
            $("#passwordSection").show();
        }
        else {
            notifyError("OTP incorrect!");
        }
    }
}

function processDataAddAuth(returnedData) {
    if(returnedData.status == 1){
        notifySuccess("Success");
        $('#modalOtp').closeModal();
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}


function savePassword(){
    var recruiterPassword = $("#rec_password").val();

    if(recruiterPassword.length > 4){
        $("#passwordBtn").addClass("disabled");
        var d = {
            recruiterPassword: recruiterPassword,
            recruiterAuthMobile: recruiterMobileVal
        };
        $.ajax({
            type: "POST",
            url: "/addRecruiterPassword",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataAddAuth
        });
    } else{
        notifyError("Please enter a valid password");
    }
}

function recruiterLoginSubmit(){
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
        $.ajax({
            type: "POST",
            url: "/recruiterLoginSubmit",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataLogin
        });
    }
}


function signUpRecruiter(){
    var recruiterName = $("#rec_name").val();
    var recruiterMobile = $("#rec_mobile").val();
    var recruiterEmail = $("#rec_email").val();
    var recruiterCompany = $("#rec_company").val();

    var nameCheck = validateName(recruiterName);
    var statusCheck = 1;

    var res = validateMobile(recruiterMobile);
    //checking recruiter name
    switch(nameCheck){
        case 0: notifyError("Name contains number. Please Enter a valid Name"); statusCheck=0; break;
        case 2: notifyError("Name cannot be blank spaces. Enter a valid name"); statusCheck=0; break;
        case 3: notifyError("Name contains special symbols. Enter a valid name"); statusCheck=0; break;
        case 4: notifyError("Please enter your name"); statusCheck=0; break;
    }

    if(res == 0){
        notifyError("Enter a valid mobile number");
        statusCheck = 0;
    } else if(res == 1){
        notifyError("Enter 10 digit mobile number");
        statusCheck = 0;
    } else if(!validateEmail(recruiterEmail)){
        notifyError("Enter a valid email");
        statusCheck = 0;
    } else if(recruiterCompany == "") {
        notifyError("Please enter your company");
        statusCheck = 0;
    }

    if(statusCheck){
        var d = {
            recruiterName : recruiterName,
            recruiterMobile : recruiterMobile,
            recruiterEmail : recruiterEmail,
            recruiterCompanyName : recruiterCompany
        };

        recruiterMobileVal =  "+91" + d.recruiterMobile;
        $.ajax({
            type: "POST",
            url: "/recruiterSignUp",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataSignUpSubmit
        });
    }
}

function requestLead(){
    var recruiterLeadMobile = $("#mobileNumber").val();
    var recruiterLeadRequirement = $("#recruiterRequirement").val();
    var jobLocalitySelected = $("#jobLocationOption").val();
    var jobRoleSelected = $("#jobRoleOption").val();

    var statusCheck = 1;
    var res = validateMobile(recruiterLeadMobile);

    if(res == 0){
        notifyError("Enter a valid mobile number");
        statusCheck=0;
    } else if(res == 1) {
        notifyError("Enter 10 digit mobile number");
        statusCheck = 0;
    } else if(jobRoleSelected == null) {
        notifyError("Please enter the required job roles");
        statusCheck=0;
    } else if(jobLocalitySelected == null) {
        notifyError("Please enter the localities where you are looking for employees");
        statusCheck=0;
    } else if(recruiterLeadRequirement == null) {
        notifyError("Please specify work shift");
        statusCheck=0;
    }

    if(statusCheck == 1){
        $("#requestLeadBtn").addClass("disabled");
        processLeadRequest(jobRoleSelected, jobLocalitySelected, recruiterLeadMobile, recruiterLeadRequirement);
    }
}

function requestModalLead(){
    var recruiterLeadMobile = $("#mobileNumberModal").val();
    var recruiterLeadRequirement = $("#recruiterRequirementModal").val();
    var jobLocalitySelected = $("#jobLocationOptionModal").val();
    var jobRoleSelected = $("#jobRoleOptionModal").val();

    var statusCheck = 1;
    var res = validateMobile(recruiterLeadMobile);

    if(res == 0){
        notifyError("Enter a valid mobile number");
        statusCheck=0;
    } else if(res == 1) {
        notifyError("Enter 10 digit mobile number");
        statusCheck = 0;
    } else if(jobRoleSelected == null) {
        notifyError("Please enter the required job roles");
        statusCheck=0;
    } else if(jobLocalitySelected == null) {
        notifyError("Please enter the localities where you are looking for employees");
        statusCheck=0;
    } else if(recruiterLeadRequirement == null) {
        notifyError("Please specify work shift");
        statusCheck=0;
    }

    if(statusCheck == 1) {
        $("#requestLeadModal").addClass("disabled");
        processLeadRequest(jobRoleSelected, jobLocalitySelected, recruiterLeadMobile, recruiterLeadRequirement);
    }
}

function processLeadRequest(jobRoleSelected, jobLocalitySelected, recruiterLeadMobile, recruiterLeadRequirement) {
    var preferredJobRoleList = [];
    var preferredJobLocationList = [];

    var i;
    /* job role preferences  */
    for (i = 0; i < Object.keys(jobRoleSelected).length; i++) {
        preferredJobRoleList.push(parseInt(jobRoleSelected[i]));
    }

    /* job locality preferences  */
    for (i = 0; i < Object.keys(jobLocalitySelected).length; i++) {
        preferredJobLocationList.push(parseInt(jobLocalitySelected[i]));
    }

    var d = {
        recruiterMobile : recruiterLeadMobile,
        recruiterRequirement : recruiterLeadRequirement[0],
        recruiterJobLocality : preferredJobLocationList,
        recruiterJobRole : preferredJobRoleList
    };

    $.ajax({
        type: "POST",
        url: "/addRecruiterLead",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(d),
        success: processDataLeadSubmit
    });
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}