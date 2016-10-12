/**
 * Created by dodo on 12/10/16.
 */

var companyId;

function processDataCheckLocality(returnedData) {
    returnedData.forEach(function(locality) {
        var id = locality.localityId;
        var name = locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#rec_company_locality').append(option);
    });
}

function processDataCheckJobs(returnedData) {
    returnedData.forEach(function(job) {
        var id = job.jobRoleId;
        var name = job.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option = $('<option value=' + id + '></option>').text(name);
    });
}

function logoutRecruiter() {
    try {
        $.ajax({
            type: "GET",
            url: "/logoutRecruiter",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataLogoutRecruiter
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataLogoutRecruiter() {
    window.location = "/recruiter";
}

$(document).scroll(function(){
    if ($(this).scrollTop() > 80) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});

$(document).ready(function(){
    checkRecruiterLogin();
    try {
        $.ajax({
            type: "POST",
            url: "/getAllLocality",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckLocality
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "POST",
            url: "/getAllJobs",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckJobs
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getRecruiterProfileInfo",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function checkRecruiterLogin() {
    try {
        $.ajax({
            type: "GET",
            url: "/checkRecruiterSession",
            data: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterSession
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataRecruiterSession(returnedData) {
    if(returnedData == 0){
        logoutRecruiter();
    }
}

function processDataRecruiterProfile(returnedData) {
    if (returnedData == '0') {
        logoutRecruiter();
    } else{
        $("#rec_name").val(returnedData.recruiterProfileName);
        $("#rec_mobile").val(returnedData.recruiterProfileMobile);

        if(returnedData.recruiterProfileEmail != null){
            $("#rec_email").val(returnedData.recruiterProfileEmail);
        }

        if(returnedData.recruiterProfileLandline != null && returnedData.recruiterProfileLandline != "0"){
            $("#rec_landline").val(returnedData.recruiterProfileLandline);
        }

        if(returnedData.recruiterLinkedinProfile != null){
            $("#rec_linkedin").val(returnedData.recruiterLinkedinProfile);
        }

        if(returnedData.recruiterAlternateMobile != null){
            $("#rec_alternate_mobile").val(returnedData.recruiterAlternateMobile);
        }

        if(returnedData.recruiterAlternateMobile != null){
            $("#rec_alternate_mobile").val(returnedData.recruiterAlternateMobile);
        }
        if(returnedData.company != null){
            companyId = returnedData.company.companyId;
        }
    }
}

function saveForm() {
    var status = 1;
    var recruiterName = validateName($("#rec_name").val());

    //checking first name
    switch(recruiterName){
        case 0: alert("Recruiter's name contains number. Please Enter a valid name"); status=0; break;
        case 2: alert("Recruiter's name cannot be blank spaces. Enter a valid name"); status=0; break;
        case 3: alert("Recruiter's name contains special symbols. Enter a valid name"); status=0; break;
        case 4: alert("Please enter recruiter's name"); status=0; break;
    }
    if(status == 1){
        try{
            var rec = {
                recruiterMobile: ($("#rec_mobile").val()).substring(3, 13),
                recruiterName: $("#rec_name").val(),
                recruiterLandline: $("#rec_landline").val(),
                recruiterEmail: $("#rec_email").val(),
                recruiterLinkedinProfile: $("#rec_linkedin").val(),
                recruiterAlternateMobile: $("#rec_alternate_mobile").val(),
                recruiterCompany: companyId
            };
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        $.ajax({
            type: "POST",
            url: "/addRecruiter",
            async: false,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(rec),
            success: processDataAddRecruiter
        });
    }
}

function processDataAddRecruiter(returnedData) {
    console.log(returnedData);
    if(returnedData.status == 4){
        notifySuccess("Profile updated successfully!");
    } else{
        notifyError("Something went wrong. Please try again later!");
    }
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}

