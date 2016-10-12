/**
 * Created by dodo on 12/10/16.
 */

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
    }
}
