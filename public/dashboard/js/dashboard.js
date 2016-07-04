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

$(window).load(function() {
    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").delay(1000).fadeOut("slow");
});

$(document).ready(function(){
    /* Section Disable */
    $("#basicProfileSection").show();
    $("#skillProfileSection").hide();
    $("#educationProfileSection").hide();

    $("#totalWorkExperience").hide();
    $("#educationalInstitute").hide();
    $("#isEmployedForm").hide();
    $("#isEmployedSelect").hide();

    checkUserLogin();
    
    /* ajax commands to fetch all localities and jobs*/
    try {
        $.ajax({
            type: "POST",
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
            type: "POST",
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
            type: "POST",
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

    var i;
    for(i=1;i<=31;i++){
        var option = document.createElement("option");
        option.value = ('0' + i).slice(-2);
        option.textContent = i;
        $('#dob_day').append(option);
    }

    for(i=1;i<=12;i++){
        option = document.createElement("option");
        option.value = ('0' + i).slice(-2);
        var monthName;
        switch(i){
            case 1: monthName = "January"; break;
            case 2: monthName = "February"; break;
            case 3: monthName = "March"; break;
            case 4: monthName = "April"; break;
            case 5: monthName = "May"; break;
            case 6: monthName = "June"; break;
            case 7: monthName = "July"; break;
            case 8: monthName = "August"; break;
            case 9: monthName = "September"; break;
            case 10: monthName = "October"; break;
            case 11: monthName = "November"; break;
            case 12: monthName = "December"; break;
        }
        option.textContent = monthName;
        $('#dob_month').append(option);
    }
    for(i=new Date().getFullYear();i>=1940;i--){
        option = document.createElement("option");
        option.value = i;
        option.textContent = i;
        $('#dob_year').append(option);
    }
});

try {
    $.ajax({
        type: "GET",
        url: "/getCandidateInfoDashboard",
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
    var i;
    $('#candidateTotalExperienceYear, #candidateTotalExperienceMonth')
        .find('option')
        .remove();

    for(i=0;i<=30;i++){
        var option = document.createElement("option");
        option.value = i;
        if(i<2){
            option.textContent = i + " year";
        } else {
            option.textContent = i + " years";
        }
        $('#candidateTotalExperienceYear').append(option);
    }

    for(i=0;i<=11;i++){
        var option = document.createElement("option");
        option.value = i;
        if(i<2){
            option.textContent = i + " month";
        } else {
            option.textContent = i + " months";
        }
        $('#candidateTotalExperienceMonth').append(option);
    }
    try {
        $.ajax({
            type: "POST",
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
            type: "POST",
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
            type: "POST",
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
    $("#skillAnswer").html('');
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


function createBtn(singleSkill, type){
    var headLbl = document.createElement("label");
    headLbl.className = "btn btn-custom-check skillBtn";
    headLbl.textContent = type;
    var s = singleSkill.skill.skillName.split(" ");
    headLbl.onclick = function () {
        document.getElementById(s[0] + "_" + s[1] + "_"+type).checked = true;
        document.getElementById(s[0] + "_" + s[1] + "_"+type).click();
    };
    var o = document.createElement("input");
    o.type = "radio";
    o.style = "display: inline-block";
    o.name = singleSkill.skill.skillName;
    o.id = s[0] + "_" + s[1] + "_"+type;

    o.value = type=="Yes"? 1 : 0;
    o.onclick = function () {
        var id = singleSkill.skill.skillId;
        var answer = type=="Yes"? true : false;
        var item = {};
        var pos;
        check = 0;

        item ["id"] = id;
        item ["answer"] = answer;
        for (var i in skillMap) {
            if (skillMap[i].id == id) {
                check = 1;
                pos = i;
                break;
            }
        }
        if (check == 0)
            skillMap.push(item);
        else
            skillMap[pos] = item;
    };
    headLbl.appendChild(o);

    return headLbl;
}

function processDataCheckSkills(returnedData) {

    var count =0;
    var table = document.getElementById("skillTable");
    $('#skillTable').empty();
    returnedData.forEach(function (singleSkill) {
        count++;
        var row = table.insertRow(0);

        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);
        
        var ques = document.createElement("div");
        ques.id = "skillQues";
        ques.textContent = singleSkill.skill.skillQuestion;
        
        var lbl = document.createElement("div");
        lbl.className = "btn-group";
        lbl.id = "skillOption";

        cell1.appendChild(ques);
        lbl.appendChild(createBtn(singleSkill, "Yes"));
        lbl.appendChild(createBtn(singleSkill, "No"));
        cell2.appendChild(lbl);

    });
    if(count == 0){
        $(".skillSection").hide();
    }
}

function prefillSkills(candidateSkillList){
    $('table#skillTable tr').each(function(){
        $(this).find('input').each(function(){
            //do your stuff, you can use $(this) to get current cell
            var skillResponse = document.createElement("INPUT");
            skillResponse= $(this).get(0);
            candidateSkillList.forEach(function (skillElement) {
                if(skillResponse.name == skillElement.skillName && skillResponse.value == skillElement.skillResponse){
                    skillResponse.checked = true;
                    skillResponse.click();
                }
            });
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
    $('#candidateMotherTongue').html('');
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

            cell1.innerHTML = l[i];
            cell2.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" id=\"languageBtn\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"r\" value=0 >Read</label></div>";
            cell3.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" id=\"languageBtn\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"w\" value=0 >Write</label></div>";
            cell4.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" id=\"languageBtn\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"s\" value=0 >Speak</label></div>";
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
        var option ='<label class="btn btn-custom-check" onchange="checkInstitute()" style=\"width: 124px\"><input type="radio" name="highestEducation" id=\"highestEducation' + id + '\" value=\"' + id + '\">' + name + '</label>';
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
    $("#jobCount").html(Object.keys(candidateInformation.jobApplicationList).length);
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