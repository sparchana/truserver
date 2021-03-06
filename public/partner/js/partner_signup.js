/**
 * Created by adarsh on 10/9/16.
 */
var returnedOtp;
var candidateMobile;

function checkPartnerType() {
    if($("#partnerType").val() == 7){
        $("#companyIdDiv").show(200);
    } else{
        $("#companyIdDiv").hide(200);
    }
}

function processDataSignUpSubmit(returnedData) {
    console.log(returnedData);
    if(returnedData.status == 1) {
        returnedOtp = returnedData.otp;

        $('#partnerOtpVerificationModel').modal('show');
        $('#authMobile').val($('#candidateMobile').val());
        $('#partnerName').val('');
        $('#partnerMobile').val('');
        document.getElementById("helpTextSignUp").innerHTML = "Enter OTP sent on " + candidateMobile;
        $('#form_otp').show();

    } else if(returnedData.status == 3){
        alert("Partner already exists! Please login to continue");
        window.location = "/partner";
    } else {
        $("#registerPartnerBtnSubmit").addClass("btn-primary registerPartnerBtnSubmit").removeClass("appliedBtn").prop('disabled', false).html("Register for FREE");
        document.getElementById("registerPartnerBtnSubmit").disabled = false;
        $('#errorMsg').show();
        $('#partnerMobile').val('');
        $('#partnerName').val('');
    }
}

function processDataAddAuth(returnedData) {
    if(returnedData.status == 1) {
        // Store
        window.location = "/partner/home";

    } else {
        $('#errorMsg').show();
    }
}

$(function() {
    $("#form_otp").submit(function(eventObj) {
        eventObj.preventDefault();
        var userOtp = $('#partnerOtp').val();
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

$(function() {
    $("#form_auth").submit(function(eventObj) {
        eventObj.preventDefault();
        var userPwd = $('#partnerPassword').val();
        var passwordCheck = validatePassword(userPwd);
        if(passwordCheck == 0){
            alert("Please set min 6 chars for password");
        } else if(passwordCheck == 1){
            alert("Password cannot have blank spaces. Enter a valid password");
        }
        else{
            document.getElementById("btnSubmit").disabled = true;
            try {
                var authPassword = $('#partnerPassword').val();
                var authMobile = candidateMobile;
                var d = {
                    partnerPassword: authPassword,
                    partnerAuthMobile: authMobile
                };
                $.ajax({
                    type: "POST",
                    url: "/addPartnerPassword",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataAddAuth
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    });
}); // end of function

// form ajax script
$(function() {
    $("#partner_signup_form").submit(function(eventObj) {
        eventObj.preventDefault();
        var statusCheck = 1;
        var partnerName = $('#partnerFirstName').val();
        var partnerLastName = $('#partnerLastName').val();
        var phone = $('#partnerMobile').val();
        var checkPartnerName = validateName(partnerName);
        var res = validateMobile(phone);
        var selectedPartnerType = $('#partnerType').val();
        var localitySelected = $('#partnerLocality').val();
        var companyId = null;

        //checking first name
        switch(checkPartnerName){
            case 0: alert("Your first name contains number. Please Enter a valid first name"); statusCheck=0; break;
            case 2: alert("Your first name cannot be blank spaces. Enter a valid first name"); statusCheck=0; break;
            case 3: alert("Your first name contains special symbols. Enter a valid first name"); statusCheck=0; break;
            case 4: alert("Please enter your first name"); statusCheck=0; break;
        }

        if(res == 0){
            alert("Enter a valid mobile number");
            statusCheck = 0;
        } else if(res == 1){
            alert("Enter 10 digit mobile number");
            statusCheck=0;
        } else if(localitySelected == "") {
            alert("Please select your Locality");
            statusCheck=0;
        } else if(selectedPartnerType == -1) {
            alert("Please select organization type");
            statusCheck=0;
        } else if(selectedPartnerType == 7 && $("#privatePartnerCompanyId").val() == ""){
            alert("Please provide company unique code");
            statusCheck=0;
        }

        if(partnerLastName != ""){
            var checkPartnerLastName = validateName(partnerLastName);
            //checking last name
            switch(checkPartnerLastName){
                case 0: alert("Your last name contains number. Please Enter a valid last name"); statusCheck=0; break;
                case 2: alert("Your last name cannot be blank spaces. Enter a valid last name"); statusCheck=0; break;
                case 3: alert("Your last name contains special symbols. Enter a valid last name"); statusCheck=0; break;
                case 4: alert("Please enter your last name"); statusCheck=0; break;
            }
        } else{
            partnerLastName = null;
        }

        if(statusCheck == 1){

            candidateMobile = phone;
            $("#registerPartnerBtnSubmit").addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Please Wait");

            document.getElementById("registerBtnSubmit").disabled = true;

            if(selectedPartnerType == 7){
                companyId = $("#privatePartnerCompanyId").val();

                try {
                    $.ajax({
                        type: "POST",
                        url: "/checkExistingCompany/" + companyId,
                        async: true,
                        contentType: false,
                        data: false,
                        success: function (returnedData) {
                            if(returnedData == 1){
                                var d = {
                                    partnerName : partnerName,
                                    partnerLastName : partnerLastName,
                                    partnerMobile : phone,
                                    partnerType : selectedPartnerType,
                                    partnerLocality : localitySelected,
                                    partnerCompanyCode : companyId
                                };

                                partnerSignUpSubmit(d);
                            } else{
                                notifyError("Company code is incorrect");

                                $("#registerPartnerBtnSubmit").addClass("btn-primary").removeClass("appliedBtn").prop('disabled',false).html("Register for FREE");
                                document.getElementById("registerBtnSubmit").disabled = false;
                            }
                        }
                    });
                } catch (exception) {
                    console.log("exception occured!!" + exception.stack);
                }
            } else{
                var d = {
                    partnerName : partnerName,
                    partnerLastName : partnerLastName,
                    partnerMobile : phone,
                    partnerType : selectedPartnerType,
                    partnerLocality : localitySelected,
                    partnerCompanyCode : companyId
                };
                partnerSignUpSubmit(d);
            }
        }
    }); // end of submit
}); // end of function

function partnerSignUpSubmit(d) {
    $.ajax({
        type: "POST",
        url: "/partnerSignUp",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(d),
        success: processDataSignUpSubmit
    });
}