/**
 * Created by adarsh on 10/9/16.
 */
var returnedOtp;
var recruiterMobile;

function processDataSignUpSubmit(returnedData) {
    returnedOtp = returnedData.otp;
}

/*
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
                console.log(d);
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
*/
function processDataAddAuth(returnedData) {
    console.log(returnedData);
}


function signUpRecruiter4(){
    var d = {
        recruiterPassword: "testing",
        recruiterAuthMobile: "+919949999999"
    };
    $.ajax({
        type: "POST",
        url: "/addRecruiterPassword",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(d),
        success: processDataAddAuth
    });
}

function signUpRecruiter2(){
    var d = {
        candidateLoginMobile: "+919949999999",
        candidateLoginPassword: "testing"
    };
    $.ajax({
        type: "POST",
        url: "/recruiterLoginSubmit",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(d),
        success: processDataAddAuth
    });
}


function signUpRecruiter5(){
    var d = {
        recruiterName : "Test1",
        recruiterMobile : "+919989999999",
        recruiterEmail : "asd@gmail.com",
        recruiterCompanyName : "test company"
    };

    recruiterMobile =  "+91" + d.recruiterMobile;
    $.ajax({
        type: "POST",
        url: "/recruiterSignUp",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(d),
        success: processDataSignUpSubmit
    });
}

function signUpRecruiter(){
    var x = [];
    x.push(1);
    x.push(2);

    var y = [];
    y.push(5);
    y.push(6);
    y.push(7);
    var d = {
        recruiterMobile : "+918883338833",
        recruiterRequirement : "Delivery boy",
        recruiterJobLocality : x,
        recruiterJobRole : y
    };

    $.ajax({
        type: "POST",
        url: "/addRecruiterLead",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(d),
        success: processDataSignUpSubmit
    });
}


/*
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

            var d = {
                partnerName : partnerName,
                partnerLastName : partnerLastName,
                partnerMobile : phone,
                partnerType : selectedPartnerType,
                partnerLocality : localitySelected
            };

            $.ajax({
                type: "POST",
                url: "/partnerSignUp",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataSignUpSubmit
            });
        }
    }); // end of submit
}); // end of function*/
