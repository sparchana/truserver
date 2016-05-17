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
var selectedJobPref_array;

$.fn.sortSelect = function() {
    var op = this.children("option");
    op.sort(function(a, b) {
        return a.text > b.text ? 1 : -1;
    })
    return this.empty().append(op);
}

$(document).ready(function(){
    var pathname = window.location.pathname; // Returns path only
    var leadId = pathname.split('/');
    leadId = leadId[(leadId.length)-1];

    $("#candidateSignUpSupportForm input").prop("disabled", true);
    $("#saveBtn").prop("disabled", true);
    $("#cancelBtn").prop("disabled", true);

    /* ajax commands to fetch leads Info */
    try {
        $.ajax({
            type: "GET",
            url: "/getUserInfo/" + leadId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckUserMobile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }


    /* ajax commands to fetch leads Info */
    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateInfo/" + leadId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataAndFillAllFields
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

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



$('#candidateHighestDegree').sortSelect();

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

function processDataCheckUserMobile(returnedData) {
    $("#candidateMobile").val(returnedData.substring(3,13));
}

function processDataAndFillAllFields(returnedData) {
    $("#candidateFirstName").val(returnedData.candidateName);
    $("#candidateLastName").val(returnedData.candidateLastName);
    $("#candidateMobile").val(returnedData.candidateMobile.substring(3,13));
    var date = JSON.parse(returnedData.candidateDOB);
    var yr = new Date(date).getFullYear();
    var month = ('0' + new Date(date).getMonth()).slice(-2);
    var d = ('0' + new Date(date).getDate()).slice(-2);
    $("#candidateDob").val(yr+"-"+month+"-"+d);
    $("#candidatePhoneType").val(returnedData.candidatePhoneType);
    if(returnedData.candidateGender !=null && returnedData.candidateGender == 0) {
        $('input[id=genderMale]').attr('checked', true);
    } else {
        $('input[id=genderFemale]').attr('checked', true);
    }
    if(returnedData.candidateMaritalStatus == 1) {
        $('input[id=marriedNot]').attr('checked', true);
    } else {
        $('input[id=married]').attr('checked', true);
    }
    $("#candidateEmail").val(returnedData.candidateEmail);
    if(returnedData.candidateIsEmployed == 1){
        $('input[id=employed]').attr('checked', true);
        $('#employedForm').show();
    } else {
        $('input[id=employedNot]').attr('checked', true);
    }
    $("#candidateCurrentCompany").val(returnedData.candidateCurrentJobDetail.candidateCurrentCompany);
    console.log(returnedData.candidateCurrentJobDetail.candidateCurrentCompany);
    $("#candidateCurrentJobDesignation").val(returnedData.candidateCurrentJobDetail.candidateCurrentDesignation);
    $("#candidateCurrentSalary").val(returnedData.candidateCurrentJobDetail.candidateCurrentSalary);
    $("#candidateCurrentJobDuration").val(returnedData.candidateCurrentJobDetail.candidateCurrentJobDuration); // months
    $("#selectTransportation").val(returnedData.candidateCurrentJobDetail.candidateTransportationMode.transportationModeId);
    $("#currentWorkShift").val(returnedData.candidateCurrentJobDetail.candidateCurrentWorkShift.timeShiftId);
    $("#candidateTotalExperience").val(returnedData.candidateTotalExperience); // months

    $("#candidateHighestEducation").val(returnedData.candidateEducation.education.educationId);
    $("#candidateHighestDegree").val(returnedData.candidateEducation.degree.degreeId);
    $("#candidateEducationInstitute").val(returnedData.candidateEducation.candidateLastInstitute);
    $("#candidateMotherTongue").val(returnedData.motherTongue.languageId);
    if(returnedData.candidateSalarySlip !=null && returnedData.candidateSalarySlip == 1){
        // hasPaySlip
        $('input[id=payslipY]').attr('checked', true);
    } else {
        $('input[id=payslipN]').attr('checked', true);
    }
    if(returnedData.candidateAppointmentLetter!=null && returnedData.candidateAppointmentLetter == 1){
        // hasPaySlip
        $('input[id=appointmentLetterY]').attr('checked', true);
    } else {
        $('input[id=appointmentLetterN]').attr('checked', true);
    }
    try {
        var jobHistory = returnedData.jobHistoryList;
        jobHistory.forEach(function (historyItem){
            $("#candidatePastCompany").val(historyItem.candidatePastCompany);
            $("#candidatePastJobSalary").val(historyItem.candidatePastSalary);
            // job role here
        });
    } catch(err){
        console.log(err);
    }
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

function cancelAndRedirect() {
    window.location = "/support";
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
    var jobName = getJobName(selectedJobPref_array[check++]);
    var parent = $('#skill_details');
    var head = document.createElement("label");
    head.style.display = "block";
    head.innerHTML = "<br>Skills for " + jobName;
    parent.append(head);

    returnedData.forEach(function (singleSkill) {
        var q = document.createElement("h5");

        var question = singleSkill.skill.skillQuestion;
        q.textContent = question + "       ";
        head.appendChild(q);

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
            op.innerHTML = "&nbsp;" + x.qualifier + "  &nbsp;&nbsp;&nbsp";

            q.appendChild(o);
            q.appendChild(op);
        });
    });
}


function generateSkills(){
    var myNode = document.getElementById("skill_details");
    myNode.innerHTML = '';
    var selectedJobPref = $('#candidateJobPref').val();
    selectedJobPref_array = selectedJobPref.split(',');
    check=0;
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
    if(returnedData.status == 1){ // save successful
        alert("Candidate record saved successfully");
        window.location = "/support";
    }
    else if(returnedData.status == 2){ // save failed
        alert("Unable to save the candidate record!");
        window.location = "/support";
    }
    else { // candidate exists
        alert("Candidate Already Exists.sig");
        window.location = "/support";
    }
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
                var c_dob = String(selectedDob);

                /* calculate total experience in months */
                var expMonth = parseInt($('#candidateTotalExperienceMonth').val());
                var expYear = parseInt($('#candidateTotalExperienceYear').val());
                var totalExp = expMonth + (12*expYear);

                var d = {
                    //mandatory fields
                    candidateFirstName: $('#candidateFirstName').val(),
                    candidateSecondName: $('#candidateSecondName').val(),
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
                    candidateTotalExperience: totalExp,

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
