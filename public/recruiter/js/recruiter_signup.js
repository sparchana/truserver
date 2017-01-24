/**
 * Created by adarsh on 10/9/16.
 */
var returnedOtp;
var recruiterMobileVal;

const message_info = 'I';
const message_warning = 'W';
const message_error = 'E';
var recruiterCompany = null;
var recruiterCompanyName = null;

function processDataLeadSubmit(returnedData) {

	if(returnedData.hasOwnProperty("messages")) {
		returnedData.messages.forEach(function (message) {
			if(message.type == message_error) {notifyError(message.text);}
			else if(message.type == message_info) {notifySuccess(message.text);}
			else if(message.type == message_warning) {notifyWarning(message.text);}
		});
	}

    if(returnedData.status == 1){
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

function processDataSignUpSubmit(returnedData) {
    if(returnedData.status == 1){
        $('#modalSignUp').closeModal();
        $("#modalOtp").openModal();

        $("#otpSection").show();
        $("#passwordSection").hide();

        returnedOtp = returnedData.otp;
    } else if(returnedData.status == 3){
        $("#SignSubmitUpBtn").removeClass("disabled");
        notifyError("Recruiter with the above mobile number already exists!");
    } else{
        $("#SignSubmitUpBtn").removeClass("disabled");
        notifyError("Something went wrong. Please try again later");
    }

}

// verify_otp_form ajax script
$(function() {
    $("#verifyOtpForm").submit(function(eventObj) {
        eventObj.preventDefault();
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
    });
});

// save_password_form ajax script
$(function() {
    $("#savePasswordForm").submit(function(eventObj) {
        eventObj.preventDefault();
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
    });
});

function processDataAddAuth(returnedData) {
    if(returnedData.status == 1){
        $('#modalOtp').closeModal();
        if(returnedData.firstTime == 1){
            $('#messageModal').openModal();
            setTimeout(function(){
                window.location = "/recruiter/home";
            }, 4000);

        } else{
            window.location = "/recruiter/home";
        }
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

// signup_recruiter_form ajax script
$(function() {
    $("#modalSignUp").submit(function(eventObj) {
        eventObj.preventDefault();
        var recruiterName = $("#rec_name").val();
        var recruiterMobile = $("#rec_mobile").val();
        var recruiterEmail = $("#rec_email").val();
        var recruiterCompanyCode = $("#rec_company_code").val();
        recruiterCompany = $("#rec_company").val();

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
        } else if($('#is_private_recruiter').prop('checked')){
            if(recruiterCompanyCode == "" || recruiterCompanyCode == null){
                notifyError("Please enter Company Code");
                statusCheck = 0;
            }
        } else if(!$('#is_private_recruiter').prop('checked')){
            if(recruiterCompany == "" || recruiterCompany == null) {
                notifyError("Please enter your company");
                statusCheck = 0;
            }
        }

        //checking if the company selected is from the list or its is a new company
        //if parseInt() of the the selected value is NaN, it is a new company
        if(isNaN(parseInt(recruiterCompany))){
            recruiterCompany = 0;
        }

        if(statusCheck){
            var companyId = parseInt(recruiterCompany);

            $("#SignSubmitUpBtn").addClass("disabled");
            if($('#is_private_recruiter').prop('checked')){
                companyId = null;
            } else{
                recruiterCompanyCode = null;
            }

            var d = {
                recruiterName : recruiterName,
                recruiterMobile : recruiterMobile,
                recruiterEmail : recruiterEmail,
                recruiterCompany : companyId,
                recruiterCompanyName : recruiterCompanyName,
                companyCode : recruiterCompanyCode
            };

            recruiterMobileVal =  "+91" + d.recruiterMobile;

            if(recruiterCompanyCode != null){
                try {
                    $.ajax({
                        type: "POST",
                        url: "/checkExistingCompany/" + recruiterCompanyCode,
                        async: true,
                        contentType: false,
                        data: false,
                        success: function (returnedData) {
                            if(returnedData == 1){
                                recruiterSignUpSubmit(d);
                            } else{
                                notifyError("Company code is incorrect");

                                $("#SignSubmitUpBtn").removeClass("disabled");
                            }
                        }
                    });
                } catch (exception) {
                    console.log("exception occured!!" + exception.stack);
                }
            } else{
                recruiterSignUpSubmit(d);
            }
        }
    });
});

function recruiterSignUpSubmit(d) {
    $.ajax({
        type: "POST",
        url: "/recruiterSignUp",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(d),
        success: processDataSignUpSubmit
    });
}

// signup_recruiter_form ajax script
$(function() {
    $("#requestEnquiry").submit(function(eventObj) {
        eventObj.preventDefault();
        var recruiterLeadMobile = $("#mobileNumber").val();
        var recruiterLeadRequirement = $("#recruiterRequirement").val();
        var jobLocalitySelected = $("#jobLocationOption").val();
        var jobRoleSelected = $("#jobRoleOption").val();

        var statusCheck = 1;
        var res = 2;//validateMobile(recruiterLeadMobile);

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
        } else if(Object.keys(recruiterLeadRequirement).length == 0) {
            //notifyError("Please specify work shift");
            //statusCheck=0;
        }

        if(statusCheck == 1){
            $("#requestLeadBtn").addClass("disabled");
            processLeadRequest(jobRoleSelected, jobLocalitySelected, recruiterLeadMobile, recruiterLeadRequirement);
        }
    });
});

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
    } else if(Object.keys(recruiterLeadRequirement).length == 0) {
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
    var preferredJobLocationList = {};

    var i;
    for (i = 0; i < Object.keys(jobRoleSelected).length; i++) {
        /* job role preferences */
        preferredJobRoleList.push(parseInt(jobRoleSelected[i]));
        /* job locality preferences */
        var array = [];
        array = Object.keys(jobRoleSelected)
        preferredJobLocationList[jobRoleSelected[i]] = array;
    }

    var d = {
        recruiterLeadMobile : recruiterLeadMobile,
        recruiterRequirement : recruiterLeadRequirement[0],
        recruiterJobLocality : [preferredJobLocationList],
        recruiterLeadSourceType : 0,
        recruiterLeadSourceName : 0,
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

function notifyWarning(msg) {
	Materialize.toast(msg, 3000, 'rounded', null, '#f7a000');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}

function validateCompanyVal(val, text) {
    recruiterCompanyName = text;
}
