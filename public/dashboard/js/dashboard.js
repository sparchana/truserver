/**
 * Created by batcoder1 on 4/6/16.
 */

var skillMap = [];
var languageMap = [];
var localityArray = [];
var jobArray = [];
var educationArray = [];
var languageArray = [];
var idProofArray = [];
var check = 0;
var selectedJobPref_array;

/* candidate Preference array */
var jobPrefArray = [];
var localityPrefArray = [];
var currentJobLocationArray = [];
var currentJobRoleArray = [];
var currentLocationArray = [];
var pastJobRoleArray = [];
var candidateIdProofArray = [];

var endPoint;

/* candidate Data returned JSON */
var candidateInformation;

$(document).ready(function(){
    /* Section Disable */
    $("#basicProfileSection").show();
    $("#skillProfileSection").hide();
    $("#educationProfileSection").hide();

    $("#totalWorkExperience").hide();
    $("#educationalInstitute").hide();
    $("#isEmployedForm").hide();
    $("#isEmployedSelect").hide();

    var pathname = window.location.pathname; // Returns path only
    var leadId = pathname.split('/');
    leadId = leadId[(leadId.length)-1];
    endPoint = leadId;
    if(leadId == "basic" || leadId == "skill" || leadId == "education" || leadId == "dashboard" || leadId == "newEditProfile"){
        leadId = localStorage.getItem("leadId");
    }

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

});

leadId = "96337872";

try {
    $.ajax({
        type: "GET",
        url: "/getCandidateInfo/" + leadId,
        data: false,
        async: false,
        contentType: false,
        processData: false,
        success: processDataAndFillAllFields
    });
} catch (exception) {
    console.log("exception occured!!" + exception);
}

function fetchSkillAjaxApis() {
    try {
        $.ajax({
            type: "GET",
            url: "/getAllLanguage",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckLanguage
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    generateSkills();
}

function fetchEducationAjaxApis() {
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
            url: "/getAllDegree",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckDegree
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function generateSkills(){
    var myNode = document.getElementById("skill_details");
    /*myNode.innerHTML = '';*/
    var selectedJobPref = $('#candidateJobPref').val();
    try {
        $.ajax({
            type: "GET",
            url: "/getAllSkills/" + selectedJobPref,
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckSkills
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataCheckSkills(returnedData) {
    var skillParent = $("#skillQuestion");
    var skillQualifierParent = $("#skillAnswer");
    skillParent.html('');
    skillQualifierParent.html('');

    var count =0;
    returnedData.forEach(function (singleSkill) {
        count++;
        var q = document.createElement("h5");
        q.style = "padding: 5px";
        var question = singleSkill.skill.skillQuestion;
        q.textContent = question + "       ";
        skillParent.append(q);

        var object = singleSkill.skill.skillQualifierList;

        var lbl = document.createElement("div");
        lbl.className = "btn-group";
        skillQualifierParent.append(lbl);

        object.forEach(function (x) {
            var headLbl = document.createElement("label");
            headLbl.className = "btn btn-custom-check";
            headLbl.textContent = x.qualifier;
            lbl.appendChild(headLbl);

            var o = document.createElement("input");
            o.type = "radio";
            o.style = "display: inline-block";
            o.name = singleSkill.skill.skillName;
            o.value = x.qualifier;
            o.onclick = function () {
                check=0;
                var id = singleSkill.skill.skillId;
                var name = x.qualifier;
                var item = {};
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
            headLbl.appendChild(o);
        });
        var br = document.createElement("div");
        br.id = "skillBreak";
        skillQualifierParent.append(br);
    });
    if(count == 0){
        $(".skillSection").hide();
    }
}

function prefillSkills(candidateSkillList){
    $('#skillAnswer input').each(function() {
        var skillResponse = document.createElement("INPUT");
        skillResponse= $(this).get(0);
        candidateSkillList.forEach(function (skillElement) {
            if(skillResponse.name == skillElement.skillName && skillResponse.value == skillElement.skillResponse){
                skillResponse.checked = true;
                skillResponse.click();
            }
        });
    });
}

/* Start of Ajax returned function */

function processDataCheckLocality(returnedData) {
    if(returnedData != null){
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
    if(returnedData != null ){
        var defaultOption=$('<option value="-1"></option>').text("Select Preferred Shift");
        $('#currentWorkShift').append(defaultOption);
        $('#candidateTimeShiftPref').append(defaultOption);
        returnedData.forEach(function(timeshift)
        {
            var id = timeshift.timeShiftId;
            var name = timeshift.timeShiftName;
            var option=$('<option value=' + id + '></option>').text(name);
            $('#currentWorkShift').append(option);

            var option=$('<option value=' + id + '></option>').text(name);
            $('#candidateTimeShiftPref').append(option);
        });
    }
}

function processDataCheckLanguage(returnedData) {
    var arrayLang =[];
    var arrayLangId =[];
    var defaultOption=$('<option value="-1"></option>').text("Select");
    $('#candidateMotherTongue').append(defaultOption);
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
    $('#languageTable').empty();
    for(i=0;i<l.length; i++) {
        if(lId[i] == 1 || lId[i] == 2 || lId[i] == 3 || lId[i] == 4 || lId[i] == 5){
            var row = table.insertRow(0);

            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);
            var cell3 = row.insertCell(2);
            var cell4 = row.insertCell(3);
            var cell5 = row.insertCell(4);

            cell1.innerHTML = l[i];
            cell2.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
            cell3.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"r\" value=0 >Read</label></div>";
            cell4.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"w\" value=0 >Write</label></div>";
            cell5.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"s\" value=0 >Speak</label></div>";
        }
    }

}

function processDataCheckDegree(returnedData) {
    var defaultOption=$('<option value="-1"></option>').text("Select Degree");
    $('#candidateHighestDegree').append(defaultOption);
    returnedData.forEach(function(degree)
    {
        var id = degree.degreeId;
        var name = degree.degreeName;
        var option=$('<option value=' + id + '></option>').text(name);
        $('#candidateHighestDegree').append(option);
    });
}

function processDataCheckEducation(returnedData) {
    $('#candidateHighestEducation').html('');
    returnedData.forEach(function(education)
    {
        var id = education.educationId;
        var name = education.educationName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option ='<label class="btn btn-custom-check" onchange="checkInstitute()" style=\"width: 124px\"><input type="radio" name="highestEducation" id=\"' + id + '\" value=\"' + id + '\">' + name + '</label>';
        $('#candidateHighestEducation').append(option);
        educationArray.push(item);
    });
}

function checkInstitute() {
    var selectedEducation = $('input:radio[name="highestEducation"]:checked').val();
    if(selectedEducation == 4 || selectedEducation == 5){
        $("#educationalInstitute").show();
    }
    else{
        $("#educationalInstitute").hide();
    }
}

/* End of Ajax returned function */

/* Prefill the data */

function processDataAndFillAllFields(returnedData) {
    candidateInformation = returnedData;
    /* get Candidate's job preference */
    try {
        var jobPref = returnedData.jobPreferencesList;
        jobPref.forEach(function (job){
            var id = job.jobRole.jobRoleId;
            var name = job.jobRole.jobName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            jobPrefArray.push(item);
        });
    } catch(err){
        console.log(err);
    }

    try {
        var localityPref = returnedData.localityPreferenceList;
        if(localityPref != null){
            localityPref.forEach(function (individualLocality){
                var id = individualLocality.locality.localityId;
                var name = individualLocality.locality.localityName;
                var item = {};
                item ["id"] = id;
                item ["name"] = name;
                localityPrefArray.push(item);
            });
        }

    } catch(err){
        console.log("getCandidateLocalityPref error"+err);
    }
}

/* end prefill*/

function getLocality(){
    return localityArray;
}

function getJob(){
    return jobArray;
}

function getIdProofs(){
    return idProofArray;
}