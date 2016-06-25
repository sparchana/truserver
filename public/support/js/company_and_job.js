/**
 * Created by batcoder1 on 25/6/16.
 */
var localityArray = [];
var jobArray = [];

function getLocality() {
    return localityArray;
}

function getJob() {
    return jobArray;
}

function processDataCheckLocality(returnedData) {
    if (returnedData != null) {
        returnedData.forEach(function (locality) {
            var id = locality.localityId;
            var name = locality.localityName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            localityArray.push(item);
        });
    }
}

function processDataCheckJobs(returnedData) {
    returnedData.forEach(function (job) {
        var id = job.jobRoleId;
        var name = job.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        jobArray.push(item);
    });
}

function processDataCheckShift(returnedData) {
    if (returnedData != null) {
        var defaultOption = $('<option value=""></option>').text("Select Preferred Shift");
        $('#jobPostWorkShift').append(defaultOption);
        returnedData.forEach(function (timeshift) {
            var id = timeshift.timeShiftId;
            var name = timeshift.timeShiftName;
            var option = $('<option value=' + id + '></option>').text(name);
            $('#jobPostWorkShift').append(option);
        });
    }
}

function processDataCheckCompany(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select Company");
    $('#jobPostCompany').append(defaultOption);
    returnedData.forEach(function (company) {
        var id = company.companyId;
        var name = company.companyName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostCompany').append(option);
    });
}

function processDataCheckEducation(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select Education");
    $('#jobPostEducation').append(defaultOption);
    returnedData.forEach(function (education) {
        var id = education.educationId;
        var name = education.educationName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostEducation').append(option);
    });
}

function processDataCheckExperience(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select Experience");
    $('#jobPostExperience').append(defaultOption);
    returnedData.forEach(function (experience) {
        var id = experience.experienceId;
        var name = experience.experienceType;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostExperience').append(option);
    });
}

function processDataCheckJobStatus(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select Job status");
    $('#jobPostStatus').append(defaultOption);
    returnedData.forEach(function (status) {
        var id = status.jobStatusId;
        var name = status.jobStatusName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostStatus').append(option);
    });
}

function processDataCheckCompanyStatus(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select Company status");
    $('#companyStatus').append(defaultOption);
    returnedData.forEach(function (status) {
        var id = status.companyStatusId;
        var name = status.companyStatusName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#companyStatus').append(option);
    });
}

function processDataCheckCompanyType(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select Company type");
    $('#companyType').append(defaultOption);
    returnedData.forEach(function (type) {
        var id = type.companyTypeId;
        var name = type.companyTypeName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#companyType').append(option);
    });
}

$(document).ready(function () {
    /* ajax commands to fetch all localities and jobs*/
    try {
        $.ajax({
            type: "GET",
            url: "/getAllLocality",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckLocality
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getAllJobs",
            data: false,
            async: false,
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
            url: "/getAllShift",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckShift
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getAllCompany",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckCompany
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getAllEducation",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckEducation
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getAllExperience",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckExperience
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getAllJobStatus",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckJobStatus
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getAllCompanyType",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckCompanyType
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getAllCompanyStatus",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckCompanyStatus
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    var i;
    var defaultOption = $('<option value=""></option>').text("Select Job start time");
    $('#jobPostStartTime').append(defaultOption);

    defaultOption = $('<option value=""></option>').text("Select Job End time");
    $('#jobPostEndTime').append(defaultOption);
    for(i=0;i<=24;i++){
        var option = document.createElement("option");
        option.value = i;
        option.textContent = i + ":00 hrs";
        $('#jobPostStartTime').append(option);
    }
    for(i=0;i<=24;i++) {

        option = document.createElement("option");
        option.value = i;
        option.textContent = i + ":00 hrs";
        $('#jobPostEndTime').append(option);
    }
});