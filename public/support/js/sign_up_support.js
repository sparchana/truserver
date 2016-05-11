/**
 * Created by batcoder1 on 9/5/16.
 */

var localityArray = [];
var jobArray = [];
var timeShiftArray = [];
var transportationArray = [];
var educationArray = [];
var languageArray = [];

$(document).ready(function(){
    $("#candidateSignUpSupportForm input").prop("disabled", true);
    $("#saveBtn").prop("disabled", true);
    $("#cancelBtn").prop("disabled", true);

    /* ajax commands to fetch all localities and jobs*/
    try {
        $.ajax({
            type: "GET",
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
            type: "GET",
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
            url: "/getAllShift",
            data: false,
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
            url: "/getAllTransportation",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckTransportation
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getAllEducation",
            data: false,
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
            url: "/getAllLanguage",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckLanguage
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function getLocality(){
    return localityArray;
}

function getJob(){
    return jobArray;
}

function getTimeShift(){
    return timeShiftArray;
}

function processDataCheckLocality(returnedData) {
    returnedData.forEach(function(locality)
    {
        var id = locality.localityId;
        var name = locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        localityArray.push(item);
    });
}

function processDataCheckJobs(returnedData) {
    returnedData.forEach(function(job)
    {
        var id = job.jobRoleId;
        var name = job.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option=$('<option value=' + id + '></option>').text(name);
        $('#candidateCurrentJobRole').append(option);
        var option=$('<option value=' + id + '></option>').text(name);
        $('#candidatePastJobRole').append(option);
        jobArray.push(item);
    });


}

function processDataCheckShift(returnedData) {
    returnedData.forEach(function(timeshift)
    {
        var id = timeshift.timeShiftId;
        var name = timeshift.timeShiftName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option=$('<option value=' + id + '></option>').text(name);
        $('#currentWorkShift').append(option);
        timeShiftArray.push(item);
    });
}

function processDataCheckTransportation(returnedData) {
    returnedData.forEach(function(transportation)
    {
        var id = transportation.transportationModeId;
        var name = transportation.transportationModeName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option=$('<option value=' + id + '></option>').text(name);
        $('#selectTransportation').append(option);
        transportationArray.push(item);
    });
}

function processDataCheckEducation(returnedData) {
    returnedData.forEach(function(education)
    {
        var id = education.educationId;
        var name = education.educationName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option=$('<option value=' + id + '></option>').text(name);
        $('#candidateHighestEducation').append(option);
        educationArray.push(item);
    });
}

function processDataCheckLanguage(returnedData) {

    returnedData.forEach(function(language)
    {
        var id = language.languageId;
        var name = language.languageName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option=$('<option value=' + id + '></option>').text(name);
        $('#candidateMotherTongue').append(option);
        languageArray.push(item);
    });
}
function selectMotherTongue(){
    document.getElementById("checkbox").checked = true;
}

function onCallYes(){
    $("#saveBtn").prop("disabled", false);
    $("#cancelBtn").prop("disabled", false);
    $("#candidateSignUpSupportForm input").prop("disabled", false);
    $('#callNoClass').hide();
    $('#callYesClass').show();
}

function onCallNo(){
    $("#saveBtn").prop("disabled", true);
    $("#cancelBtn").prop("disabled", true);
    $("#candidateSignUpSupportForm input").prop("disabled", true);
    $('#callYesClass').hide();
    $('#callNoClass').show();
}

function employedYes(){
    $('#employedForm').show();
}

function employedNo(){
    $('#employedForm').hide();
}

function generateSkills(){
    var selectedJobPref = $('#candidateJobPref').val();
    var selectedJobPref_array = selectedJobPref.split(',');
    for(var i = 0; i < selectedJobPref_array.length; i++)
    {
        try {
            $.ajax({
                type: "GET",
                url: "/getAllSkills/" + selectedJobPref_array[i],
                data: false,
                contentType: false,
                processData: false,
                success: processDataCheckEducation
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function processDataSignUpSupportSubmit(returnedData) {
    console.log("returedData :" + returnedData.status);
}

// form_candidate ajax script
$(function() {
    $("#candidateSignUpSupportForm").submit(function(eventObj) {
        eventObj.preventDefault();
        var localitySelected = $('#candidateLocalityPref').val();
        var jobSelected = $('#candidateJobPref').val();
        if (localitySelected == "") {
            alert("Please Enter your Job Localities");
        } else if (jobSelected == "") {
            alert("Please Enter the Jobs you are Interested --" + jobSelected);
        }
        else{
            document.getElementById("saveBtn").disabled = true;
            try {
                var selected = $('#candidateDob').val();
                var c_dob = new Date(selected);
                var d = {
                    //mandatory fields
                    candidateName: $('#candidateName').val(),
                    candidateMobile: $('#candidateMobile').val(),
                    candidateLocality: $('#candidateLocality').val(),
                    candidateJobPref: $('#candidateJobPref').val(),

                    //others
                    candidateDob: c_dob,
                    candidateAge: $('#candidateAge').val(),
                    candidatePhoneType: $('#candidatePhoneType').val(),
                    candidateGender: $('input:radio[name="gender"]:checked').val(),
                    candidateHomeLocality: $('#candidateHomeLocality').val(),
                    candidateMaritalStatus: $('input:radio[name="married"]:checked').val(),
                    candidateEmail: $('#candidateEmail').val(),
                    candidateIsEmployed: $('input:radio[name="employed"]:checked').val(),
                    candidateTotalExperience: $('#candidateTotalExperience').val(),

                    candidateCurrentCompany: $('#candidateCurrentCompany').val(),
                    candidateCurrentJobLocation: $('#candidateCurrentJobLocation').val(),
                    candidateTransportation: $('#selectTransportation').val(),
                    candidateCurrentWorkShift: $('#currentWorkShift').val(),
                    candidateCurrentJobRole: $('#candidateCurrentJobRole').val(),
                    candidateCurrentJobDesignation: $('#candidateCurrentJobDesignation').val(),
                    candidateCurrentSalary: $('#candidateCurrentCompany').val(),
                    candidateCurrentJobDuration: $('#candidateCurrentJobDuration').val(),

                    candidatePastJobCompany: $('#candidatePastCompany').val(),
                    candidatePastJobRole: $('#candidatePastJobRole').val(),
                    candidatePastJobSalary: $('#candidatePastJobSalary').val(),

                    candidateEducationLevel: $('#candidateHighestEducation').val(),
                    /*candidateDegree: $('#candidateDe').val(),*/
                    candidateEducationInstitute: $('#candidateEducationInstitute').val(),

                    candidateTimeShiftPref: $('#candidateTimeShiftPref').val(),

                    candidateMotherTongue: $('#candidateMotherTongue').val(),

                    candidateIdProof: $('#candidateIdProof').val(),
                    candidateSalarySlip: $('input:radio[name="payslip"]:checked').val(),
                    candidateAppointmentLetter: $('input:radio[name="appointmentLetter"]:checked').val()
                };

                $.ajax({
                    type: "POST",
                    url: "/signUpSupport",
                    data: d,
                    success: processDataSignUpSupportSubmit
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }); // end of submit
}); // end of function
