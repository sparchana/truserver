/**
 * Created by batcoder1 on 9/5/16.
 */

var skillMap = [];
var languageMap = [];
var localityArray = [];
var jobArray = [];
var timeShiftArray = [];
var transportationArray = [];
var educationArray = [];
var languageArray = [];
var idProofArray = [];
var degreeArray = [];
var check = 0;
var jobId;
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

    try {
        $.ajax({
            type: "GET",
            url: "/getAllDegree",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckDegree
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

function getDegree(){
    return degreeArray;
}

function getJobName(id){
    var jobArray = getJob();
    var returnJobName;
    jobArray.forEach(function (job) {
        if(job.id == id){
            returnJobName = job.name;
            return true;
        }
    });
    return returnJobName;
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

function processDataCheckDegree(returnedData) {
    returnedData.forEach(function(degree)
    {
        var id = degree.degreeId;
        var name = degree.degreeName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option=$('<option value=' + id + '></option>').text(name);
        $('#candidateHighestDegree').append(option);
        degreeArray.push(item);
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
        cell2.innerHTML = "<input id=" + lId[i] + " type=\"checkbox\" name=\"r\" value=0 >";
        cell3.innerHTML = "<input id=" + lId[i] + " type=\"checkbox\" name=\"w\" value=0 >";
        cell4.innerHTML = "<input id=" + lId[i] + " type=\"checkbox\" name=\"s\" value=0 >";
    }

}


function processLeadUpdate(returnedData) {
    if(returnedData.leadType != '0' && returnedData.leadType != '1') {
        // existing data hence pre fill form
    } else {
        clearModal();
        alert('Unable to show data');
    }
    window.location="/support";
}

function onCallYes(leadId){
    $("#saveBtn").prop("disabled", false);
    $("#cancelBtn").prop("disabled", false);
    $("#candidateSignUpSupportForm input").prop("disabled", false);
    $('#callNoClass').hide();
    $('#callYesClass').show();

    var value = "Out Bound Call Successfuly got connected";
    //update leadStatus to TTC
    NProgress.start();
    $.ajax({
        type: "GET",
        url: "/updateLeadStatus/"+leadId+"/1/"+value,
        processData: false,
        success: false
    });
    NProgress.done();
}



function onCallNo(leadId){
    $("#saveBtn").prop("disabled", true);
    $("#cancelBtn").prop("disabled", true);
    $("#candidateSignUpSupportForm input").prop("disabled", true);
    $('#callYesClass').hide();
    $('#callNoClass').show();
}

function saveResponse(id) {
    var value = $('#callResponse').val();

    // update status and interaction
    $.ajax({
        type: "GET",
        url: "/updateLeadStatus/"+id+"/1/"+value,
        processData: false,
        success: processLeadUpdate
    });

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
        head.innerHTML = "Skills for " + getJobName(jobId);
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
                        break;
                    }
                }
                if(check == 0)
                    skillMap.push(item);
                else
                    skillMap[pos] = item;
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
    var len = selectedJobPref_array.length;
    if(len==1){
        head.innerHTML = "No Skills found for the above Job Roles";
    }
    for(var i = 0; i < selectedJobPref_array.length; i++)
    {
        jobId = selectedJobPref_array[i];
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
    console.log(JSON.stringify(returnedData));
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
                check=0;
                var id = this.id;
                var name = this.name;
                var item = {}
                var pos;

                for(var i in languageMap){
                    if(languageMap[i].id == id){
                        pos=i;
                        check=1;
                        break;
                    }
                }
                if(check==0){
                    item["id"] = id;
                    item["r"] = 0;
                    item["w"] = 0;
                    item["s"] = 0;
                    if(name == "r")
                        item["r"] = 1;
                    else if(name == "w")
                        item["w"] = 1;
                    else
                        item["s"] = 1;
                    languageMap.push(item);
                }
                else{
                    if(name == "r")
                        languageMap[pos].r = 1;
                    else if(name == "w")
                        languageMap[pos].w = 1;
                    else
                        languageMap[pos].s = 1;
                }
            }).get();

            document.getElementById("saveBtn").disabled = true;
            try {
                console.log(languageMap);
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
                    candidateGender: ($('input:radio[name="gender"]:checked').val()),
                    candidateHomeLocality: $('#candidateHomeLocality').val(),
                    candidateMaritalStatus: ($('input:radio[name="married"]:checked').val()),
                    candidateEmail: $('#candidateEmail').val(),
                    candidateIsEmployed: ($('input:radio[name="employed"]:checked').val()),
                    candidateTotalExperience: ($('#candidateTotalExperience').val()),

                    candidateCurrentCompany: $('#candidateCurrentCompany').val(),
                    candidateCurrentJobLocation: $('#candidateCurrentJobLocation').val(),
                    candidateTransportation: ($('#selectTransportation').val()),
                    candidateCurrentWorkShift: ($('#currentWorkShift').val()),
                    candidateCurrentJobRole: $('#candidateCurrentJobRole').val(),
                    candidateCurrentJobDesignation: $('#candidateCurrentJobDesignation').val(),
                    candidateCurrentSalary: ($('#candidateCurrentJobSalary').val()),
                    candidateCurrentJobDuration: ($('#candidateCurrentJobDuration').val()),

                    candidatePastJobCompany: $('#candidatePastCompany').val(),
                    candidatePastJobRole: $('#candidatePastJobRole').val(),
                    candidatePastJobSalary: ($('#candidatePastJobSalary').val()),

                    candidateEducationLevel: ($('#candidateHighestEducation').val()),
                    candidateDegree: ($('#candidateHighestDegree').val()),
                    candidateEducationInstitute: $('#candidateEducationInstitute').val(),

                    candidateTimeShiftPref: $('#candidateTimeShiftPref').val(),

                    candidateMotherTongue: ($('#candidateMotherTongue').val()),
                    candidateLanguageKnown: languageMap,

                    candidateSkills: skillMap,

                    candidateIdProof: $('#candidateIdProof').val(),
                    candidateSalarySlip: ($('input:radio[name="payslip"]:checked').val()),
                    candidateAppointmentLetter: ($('input:radio[name="appointmentLetter"]:checked').val())
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
