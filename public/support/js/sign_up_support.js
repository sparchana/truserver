/**
 * Created by batcoder1 on 9/5/16.
 */

var skillMap = [];
var localityArray = [];
var jobArray = [];
var timeShiftArray = [];
var transportationArray = [];
var educationArray = [];
var languageArray = [];
var idProofArray = [];
var check = 0;
var head = document.createElement("label");

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

    try {
        $.ajax({
            type: "GET",
            url: "/getAllIdProof",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckIdProofs
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

function getIdProofs(){
    return idProofArray;
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

function processDataCheckIdProofs(returnedData) {
    returnedData.forEach(function(idProof)
    {
        var id = idProof.idProofId;
        var name = idProof.idProofName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        idProofArray.push(item);
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
    var arrayLang =[];
    var arrayLangId =[];
    returnedData.forEach(function(language)
    {
        var id = language.languageId;
        var name = language.languageName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        arrayLang.push(name);
        arrayLangId.push(id);
        var option=$('<option value=' + id + '></option>').text(name);
        $('#candidateMotherTongue').append(option);

        languageArray.push(item);
    });
    populateLanguages(arrayLang.reverse(), arrayLangId.reverse());
}

function populateLanguages(l, lId) {
    var i;
    var table = document.getElementById("languageTable");
    for(i=0;i<l.length; i++) {
        var row = table.insertRow(0);

        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);
        var cell3 = row.insertCell(2);
        var cell4 = row.insertCell(3);

        cell1.innerHTML = l[i];
        cell2.innerHTML = "<input type=\"checkbox\" name=" + lId[i] + "_R " + " value=0 >";
        cell3.innerHTML = "<input type=\"checkbox\" name=" + lId[i] + "_W " + " value=0 >";
        cell4.innerHTML = "<input type=\"checkbox\" name=" + lId[i] + "_S " + " value=0 >";
    }

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

function processDataCheckSkills(returnedData) {
    var parent = $('#skill_details');

    parent.append(head);

    returnedData.forEach(function (singleSkill) {
        var q = document.createElement("h5");
        head.innerHTML = "Skills for " + singleSkill.skill.skillName;
        var question = singleSkill.skill.skillQuestion;
        q.textContent = question;
        parent.append(q);

        var object = singleSkill.skill.skillQualifierList;

        object.forEach(function (x) {
            var o = document.createElement("input");
            o.type = "radio";
            o.name = singleSkill.skill.skillName;
            o.value = x.qualifier;
            o.onclick = function () {
                check=0;
                var id = singleSkill.skill.skillId;
                var name = x.qualifier;
                var item = {}
                var pos;

                item ["id"] = id;
                item ["qualifier"] = name;
                for(var i in skillMap){
                    if(skillMap[i].id == id){
                        check = 1;  
                        pos=i;
                    }
                }
                if(check == 0)
                    skillMap.push(item);
                else
                    skillMap[pos] = item;

                console.log(skillMap);
            };

            var op = document.createElement("label");
            op.innerHTML = x.qualifier;

            parent.append(o);
            parent.append(op);
        });
    });
}

function generateSkills(){
    var myNode = document.getElementById("skill_details");
    myNode.innerHTML = '';
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
                success: processDataCheckSkills
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function processDataSignUpSupportSubmit(returnedData) {
    console.log("returedData:candidateName :" + returnedData.candidateName);
    console.log("returedData:candidateMobile :" + returnedData.candidateMobile);
    console.log("returedData:candidateLocality :" + returnedData.candidateLocality);
    console.log("returedData:candidateJobInterest :" + returnedData.candidateJobInterest);
    console.log("returedData:candidateDob :" + returnedData.candidateDob);
    console.log("returedData:candidatePhoneType :" + returnedData.candidatePhoneType);
    console.log("returedData:candidateGender :" + returnedData.candidateGender);
    console.log("returedData:candidateHomeLocality :" + returnedData.candidateHomeLocality);
    console.log("returedData:candidateMaritalStatus :" + returnedData.candidateMaritalStatus);
    console.log("returedData:candidateEmail :" + returnedData.candidateEmail);
    console.log("returedData:candidateIsEmployed :" + returnedData.candidateIsEmployed);
    console.log("returedData:candidateTotalExperience :" + returnedData.candidateTotalExperience);
    console.log("returedData:candidateCurrentCompany :" + returnedData.candidateCurrentCompany);
    console.log("returedData:candidateCurrentJobLocation :" + returnedData.candidateCurrentJobLocation);
    console.log("returedData:candidateTransportation :" + returnedData.candidateTransportation);
    console.log("returedData:candidateCurrentWorkShift :" + returnedData.candidateCurrentWorkShift);
    console.log("returedData:candidateCurrentJobRole :" + returnedData.candidateCurrentJobRole);
    console.log("returedData:candidateCurrentJobDesignation :" + returnedData.candidateCurrentJobDesignation);
    console.log("returedData:candidateCurrentSalary :" + returnedData.candidateCurrentSalary);
    console.log("returedData:candidateCurrentJobDuration :" + returnedData.candidateCurrentJobDuration);
    console.log("returedData:candidatePastJobCompany :" + returnedData.candidatePastJobCompany);
    console.log("returedData:candidatePastJobRole :" + returnedData.candidatePastJobRole);
    console.log("returedData:candidatePastJobSalary :" + returnedData.candidatePastJobSalary);
    console.log("returedData:candidateEducationLevel :" + returnedData.candidateEducationLevel);
    console.log("returedData:candidateEducationInstitute :" + returnedData.candidateEducationInstitute);
    console.log("returedData:candidateTimeShiftPref :" + returnedData.candidateTimeShiftPref);
    console.log("returedData:candidateMotherTongue :" + returnedData.candidateMotherTongue);
    console.log("returedData:candidateLanguageKnown :" + returnedData.candidateLanguageKnown);
    console.log("returedData:candidateSkills :" + (returnedData.candidateSkills));
    console.log("returedData:candidateIdProof :" + returnedData.candidateIdProof);
    console.log("returedData:candidateSalarySlip :" + returnedData.candidateSalarySlip);
    console.log("returedData:candidateAppointmentLetter :" + returnedData.candidateAppointmentLetter);
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
            alert("Please Enter the Jobs you are Interested");
        }
        else{
            var languageKnown = $('#languageTable input:checked').map(function() {
                return this.name;
            }).get();
            alert(languageKnown);

            document.getElementById("saveBtn").disabled = true;
            try {
                var selectedDob = $('#candidateDob').val();
/*                var c_dob = new Date(selectedDob);*/
                var c_dob = String(selectedDob);
                var d = {
                    //mandatory fields
                    candidateName: $('#candidateName').val(),
                    candidateMobile: $('#candidateMobile').val(),
                    candidateLocality: $('#candidateLocalityPref').val(),
                    candidateJobInterest: $('#candidateJobPref').val(),

                    //others
                    candidateDob: c_dob,
                    candidatePhoneType: $('#candidatePhoneType').val(),
                    candidateGender: parseInt($('input:radio[name="gender"]:checked').val()),
                    candidateHomeLocality: $('#candidateHomeLocality').val(),
                    candidateMaritalStatus: parseInt($('input:radio[name="married"]:checked').val()),
                    candidateEmail: $('#candidateEmail').val(),
                    candidateIsEmployed: parseInt($('input:radio[name="employed"]:checked').val()),
                    candidateTotalExperience: parseInt($('#candidateTotalExperience').val()),

                    candidateCurrentCompany: $('#candidateCurrentCompany').val(),
                    candidateCurrentJobLocation: $('#candidateCurrentJobLocation').val(),
                    candidateTransportation: parseInt($('#selectTransportation').val()),
                    candidateCurrentWorkShift: parseInt($('#currentWorkShift').val()),
                    candidateCurrentJobRole: $('#candidateCurrentJobRole').val(),
                    candidateCurrentJobDesignation: $('#candidateCurrentJobDesignation').val(),
                    candidateCurrentSalary: parseInt($('#candidateCurrentJobSalary').val()),
                    candidateCurrentJobDuration: parseInt($('#candidateCurrentJobDuration').val()),

                    candidatePastJobCompany: $('#candidatePastCompany').val(),
                    candidatePastJobRole: $('#candidatePastJobRole').val(),
                    candidatePastJobSalary: parseInt($('#candidatePastJobSalary').val()),

                    candidateEducationLevel: parseInt($('#candidateHighestEducation').val()),
                    candidateEducationInstitute: $('#candidateEducationInstitute').val(),

                    candidateTimeShiftPref: $('#candidateTimeShiftPref').val(),

                    candidateMotherTongue: parseInt($('#candidateMotherTongue').val()),
                    candidateLanguageKnown: languageKnown,
                    
                    candidateSkills: skillMap,

                    candidateIdProof: $('#candidateIdProof').val(),
                    candidateSalarySlip: parseInt($('input:radio[name="payslip"]:checked').val()),
                    candidateAppointmentLetter: parseInt($('input:radio[name="appointmentLetter"]:checked').val())
                };

                $.ajax({
                    type: "POST",
                    url: "/signUpSupport",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataSignUpSupportSubmit
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }); // end of submit
}); // end of function
