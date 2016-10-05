/**
 * Created by adarsh on 10/9/16.
 */
var returnedOtp;
var recruiterMobile;

function processDataLeadSubmit(returnedData) {
    console.log(returnedData);
    if(returnedData.status = 1){
        alert("Thanks! We will get in touch shortly!");
        location.reload();
    } else{
        notifyError("Oops! Looks like something went wrong! Please try again after some time1");
    }
}

function processDataSignUpSubmit(returnedData) {
    returnedOtp = returnedData.otp;
}

function processDataAddAuth(returnedData) {
    console.log(returnedData);
}


function addRecruiterPassword(){
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

function recruiterLoginSubmit(){
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


function signUpRecruiter(){
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

function requestLead(){
    var recruiterLeadMobile = $("#mobileNumber").val();
    var recruiterLeadRequirement = $("#recruiterRequirement").val();
    var jobLocalitySelected = $("#locationOption").val();
    var jobRoleSelected = $("#jobRoleOption").val();

    var statusCheck = 1;
    var res = validateMobile(recruiterLeadMobile);

    if(res == 0){
        notifyError("Enter a valid mobile number");
        statusCheck=0;
    } else if(res == 1){
        notifyError("Enter 10 digit mobile number");
        statusCheck=0;
    } else if(jobLocalitySelected == "") {
        notifyError("Please Enter the localities where you are looking for employees");
        statusCheck=0;
    } else if(jobRoleSelected == "") {
        notifyError("Please Enter the required job roles");
        statusCheck=0;
    } else if(recruiterLeadRequirement == "") {
        notifyError("Please Enter your hiring requirement");
        statusCheck=0;
    }

    if(statusCheck == 1){
        var preferredJobRoleList = [];
        var preferredJobLocationList = [];

        var jobPref = jobRoleSelected.split(",");
        var jobLocalityPref = jobLocalitySelected.split(",");

        var i;
        /* job role preferences  */
        for (i = 0; i < jobPref.length; i++) {
            preferredJobRoleList.push(parseInt(jobPref[i]));
        }

        /* job role preferences  */
        for (i = 0; i < jobLocalityPref.length; i++) {
            preferredJobLocationList.push(parseInt(jobLocalityPref[i]));
        }

        var d = {
            recruiterMobile : recruiterLeadMobile,
            recruiterRequirement : recruiterLeadRequirement,
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
}

function notifyError(msg){
    $.notify(msg, "error");
}
