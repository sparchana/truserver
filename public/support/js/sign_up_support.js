/**
 * Created by batcoder1 on 9/5/16.
 */

var skillMap = [];
var languageMap = [];
var localityArray = [];
var jobArray = [];
var transportationArray = [];
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

var candidateSkill = [];

$(document).ready(function(){
    var pathname = window.location.pathname; // Returns path only
    var leadId = pathname.split('/');
    leadId = leadId[(leadId.length)-1];
    if(leadId == "basic" || leadId == "skill" || leadId == "education" || leadId == "dashboard"){
        leadId = localStorage.getItem("leadId");
    }

    $("#candidateSignUpSupportForm input").prop("disabled", true);
    $("#totalWorkExperience").hide();
    $("#educationalInstitute").hide();
    $("#isEmployedForm").hide();
    $("#isEmployedSelect").hide();

    /* ajax commands to fetch leads Info */
    try {
        $.ajax({
            type: "GET",
            url: "/getUserInfo/" + leadId,
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckUserMobile
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
            url: "/getAllTransportation",
            data: false,
            async: false,
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

    try {
        $.ajax({
            type: "GET",
            url: "/getAllIdProof",
            data: false,
            async: false,
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
            async: false,
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

function getIdProofs(){
    return idProofArray;
}

function processDataCheckUserMobile(returnedData) {
    $("#candidateMobile").val(returnedData.substring(3,13));
}

function processDataAndFillAllFields(returnedData) {
    $("#candidateFirstName").val(returnedData.candidateName);
    $("#candidateSecondName").val(returnedData.candidateLastName);
    $("#candidateMobile").val(returnedData.candidateMobile.substring(3,13));

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

    /* get Candidate's home location */
    if(returnedData.locality != null){
        try {
            var item = {};
            item ["id"] = returnedData.locality.localityId;
            item ["name"] = returnedData.locality.localityName;
            currentLocationArray.push(item);
        } catch(err){
            console.log("homeLocality" + err);
        }
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

    if(returnedData.candidateCurrentJobDetail != null){
        /* get Candidate's current job details preference */
        try {
            var id = returnedData.candidateCurrentJobDetail.jobRole.jobRoleId;
            var name = returnedData.candidateCurrentJobDetail.jobRole.jobName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            currentJobRoleArray.push(item);
        } catch(err){
            console.log(err);
        }

        /* get Candidate's current job details preference */
        try {
            if(returnedData.candidateCurrentJobDetail.candidateCurrentJobLocation != null && returnedData.candidateCurrentJobDetail.candidateCurrentJobLocation.localityId != null){
                var id = returnedData.candidateCurrentJobDetail.candidateCurrentJobLocation.localityId;
                var name = returnedData.candidateCurrentJobDetail.candidateCurrentJobLocation.localityName;
                var item ={};
                item ["id"] = id;
                item ["name"] = name;
                currentJobLocationArray.push(item);
            }
        } catch(err){
            console.log(err);
        }
    }



    if(returnedData.jobHistoryList != null){
        /* get Candidate's past job role */
        try {
            var pastJobRole = returnedData.jobHistoryList;
            pastJobRole.forEach(function (pastJob){
                var id = pastJob.jobRole.jobRoleId;
                var name = pastJob.jobRole.jobName;
                var item = {};
                item ["id"] = id;
                item ["name"] = name;
                pastJobRoleArray.push(item);
            });
        } catch(err){
            console.log(err);
        }
    }


    /* get Candidate's idProofs */
    try {
        var idProof = returnedData.idProofReferenceList;
        idProof.forEach(function (singleIdProof){
            var id = singleIdProof.idProof.idProofId;
            var name = singleIdProof.idProof.idProofName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            candidateIdProofArray.push(item);
        });
    } catch(err){
        console.log(err);
    }

    try {
        if(returnedData.candidateDOB != null){
            var date = JSON.parse(returnedData.candidateDOB);
            var yr = new Date(date).getFullYear();
            var month = ('0' + parseInt(new Date(date).getMonth() + 1)).slice(-2);
            var d = ('0' + new Date(date).getDate()).slice(-2);
            $("#candidateDob").val(yr + "-" + month + "-" + d);
            $("#dob_day").val(d);
            $("#dob_month").val(month);
            console.log("year = " + yr);
            $("#dob_year").val(yr);
        }
    } catch(err){
        console.log(err);
    }

    try {
        $("#candidatePhoneType").val(returnedData.candidatePhoneType);
    } catch(err){
        console.log(err);
    }

    try {
        if (returnedData.candidateGender != null) {
            if (returnedData.candidateGender == 0) {
                document.getElementById("genderMale").checked = true;
                $('#genderMale').parent().addClass('active').siblings().removeClass('active');
                /* for support */
                $('input[id=genderMale]').attr('checked', true);
            } else {
                document.getElementById("genderFemale").checked = true;
                $('#genderFemale').parent().addClass('active').siblings().removeClass('active');
                /* for support */
                $('input[id=genderFemale]').attr('checked', true);
            }
        }

        if(returnedData.candidateMaritalStatus != null){
            if (returnedData.candidateMaritalStatus == 1) {
                $('input[id=marriedNot]').attr('checked', true);
            } else {
                $('input[id=married]').attr('checked', true);
            }
        }
    } catch(err){
        console.log(err);
    }
    try {
        $("#candidateEmail").val(returnedData.candidateEmail);
    } catch(err){
        console.log(err);
    }

    try {
        if(returnedData.candidateIsEmployed != null){
            if (returnedData.candidateIsEmployed == 1) {
                $("#isEmployedSelect").show();
                $('input[id=employed]').attr('checked', true);
                $('#employedForm').show();
                /* candidate dashboard */
                document.getElementById("isEmployedYes").checked = true;
                $("#isEmployedForm").show();
                $('#isEmployedYes').parent().addClass('active').siblings().removeClass('active');

            } else {
                $('input[id=employedNot]').attr('checked', true);
                /* candidate dashboard */
                document.getElementById("isEmployedNo").checked = true;
                $("#isEmployedForm").hide();
                $('#isEmployedNo').parent().addClass('active').siblings().removeClass('active');
            }
        }
    } catch(err){
        console.log(err);
    }

    try {
        if(returnedData.candidateCurrentJobDetail != null){
            if(returnedData.candidateCurrentJobDetail.candidateCurrentCompany != null){
                $("#candidateCurrentCompany").val(returnedData.candidateCurrentJobDetail.candidateCurrentCompany);
            }
            if(returnedData.candidateCurrentJobDetail.candidateCurrentDesignation != null){
                $("#candidateCurrentJobDesignation").val(returnedData.candidateCurrentJobDetail.candidateCurrentDesignation);
            }
            if(returnedData.candidateCurrentJobDetail.candidateCurrentSalary != null){
                $("#candidateCurrentJobSalary").val(returnedData.candidateCurrentJobDetail.candidateCurrentSalary);
            }
            if(returnedData.candidateCurrentJobDetail.candidateCurrentJobDuration != null){
                var currentJobDuration = parseInt(returnedData.candidateCurrentJobDetail.candidateCurrentJobDuration);
                $("#candidateCurrentJobDurationYear").val(parseInt((currentJobDuration / 12)).toString()); // years
                $("#candidateCurrentJobDurationMonth").val(currentJobDuration % 12); // months

            }
        }

    } catch(err){
        console.log(err);
    }

    try {
        if(returnedData.candidateCurrentJobDetail != null){
            if(returnedData.candidateCurrentJobDetail.candidateTransportationMode != null){
                $("#selectTransportation").val(returnedData.candidateCurrentJobDetail.candidateTransportationMode.transportationModeId);
            }
            if(returnedData.candidateCurrentJobDetail.candidateCurrentWorkShift != null){
                $("#currentWorkShift").val(returnedData.candidateCurrentJobDetail.candidateCurrentWorkShift.timeShiftId);
            }
        }
        if(returnedData.timeShiftPreference != null){
            $("#candidateTimeShiftPref").val(returnedData.timeShiftPreference.timeShift.timeShiftId);
        }
        if(returnedData.candidateTotalExperience != null){
            if(returnedData.candidateTotalExperience == 0){
                document.getElementById("fresher").checked = true;
                $('#fresher').parent().addClass('active').siblings().removeClass('active');
            } else{
                var totalExperience = parseInt(returnedData.candidateTotalExperience);
                try{
                    $("#candidateTotalExperienceYear").val(parseInt((totalExperience / 12)).toString()); // years
                    $("#candidateTotalExperienceMonth").val(totalExperience % 12); // years
                } catch (err){
                    console.log("try catch");
                }
                try{
                    $("#totalWorkExperience").show();
                    document.getElementById("experienced").checked = true;
                    $('#experienced').parent().addClass('active').siblings().removeClass('active');
                } catch (err){
                    console.log("try catch");
                }
            }
        }
    } catch(err){
        console.log(err);
    }

    try {
        if(returnedData.candidateEducation != null){
            if(returnedData.candidateEducation.education != null){
                document.getElementById(returnedData.candidateEducation.education.educationId).checked = true;
                $("#" + returnedData.candidateEducation.education.educationId).parent().addClass('active').siblings().removeClass('active');
                if(returnedData.candidateEducation.education.educationId == 4 || returnedData.candidateEducation.education.educationId == 5){
                    $("#educationalInstitute").show();
                }
            }
            if(returnedData.candidateEducation.degree != null){
                $("#candidateHighestDegree").val(returnedData.candidateEducation.degree.degreeId);
            }
            if(returnedData.candidateEducation != null){
                $("#candidateEducationInstitute").val(returnedData.candidateEducation.candidateLastInstitute);
            }
            if(returnedData.motherTongue != null){
                $("#candidateMotherTongue").val(returnedData.motherTongue.languageId);
            }
        }
    } catch(err){
        console.log(err);
    }

    try {
        if (returnedData.candidateSalarySlip != null) {
            if(returnedData.candidateSalarySlip == '1'){
                $('input[id=payslipY]').attr('checked', true);
            }
            else {
                $('input[id=payslipN]').attr('checked', true);
            }
        }
        if(returnedData.candidateAppointmentLetter != null ){
            if (returnedData.candidateAppointmentLetter == '1') {
                // hasPaySlip
                $('input[id=appointmentLetterY]').attr('checked', true);
            } else {
                $('input[id=appointmentLetterN]').attr('checked', true);
            }
        }
    } catch(err){
        console.log(err);
    }

    if(returnedData.languageKnownList != null) {
        prefillLanguageTable(returnedData.languageKnownList);
    }

    if(returnedData.candidateSkillList != null) {
        var skillList = returnedData.candidateSkillList;
        skillList.forEach(function (skillElement) {
           var obj = {};
            obj["skillName"] = skillElement.skill.skillName;
            obj["skillResponse"] = skillElement.skillQualifier.qualifier;
            candidateSkill.push(obj);
        });
    }
}

function prefillLanguageTable(languageKnownList) {
    $('table#languageTable tr').each(function(){
        $(this).find('input').each(function(){
            //do your stuff, you can use $(this) to get current cell
            var x = document.createElement("INPUT");
            x= $(this).get(0);
            languageKnownList.forEach(function (languageKnown) {
                if(x.id == languageKnown.language.languageId){
                    if(languageKnown.verbalAbility == "1" && x.name == "s") {
                        x.checked = true;
                        $(x).parent().addClass('active').siblings().removeClass('active');
                    } else if (languageKnown.readingAbility == "1" && x.name == "r") {
                        x.checked = true;
                        $(x).parent().addClass('active').siblings().removeClass('active');
                    } else if(languageKnown.writingAbility == "1" && x.name == "w") {
                        x.checked = true;
                        $(x).parent().addClass('active').siblings().removeClass('active');
                    }
                }
            });
        });
    });
}


function prefillSkills(candidateSkillList){
    $('#skill_details h5 input').each(function() {
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

function checkInstitute() {
    var selecetdEducation = $('input:radio[name="highestEducation"]:checked').val();
    if(selecetdEducation == 4 || selecetdEducation == 5){
        $("#educationalInstitute").show();
    }
    else{
        $("#educationalInstitute").hide();
    }
}

function processDataCheckTransportation(returnedData) {
    if(returnedData != null){
        var defaultOption=$('<option value="-1"></option>').text("Select");
        $('#selectTransportation').append(defaultOption);
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
}

function processDataCheckEducation(returnedData) {
    returnedData.forEach(function(education)
    {
        var id = education.educationId;
        var name = education.educationName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option ='<label class="btn btn-custom-check" onchange="checkInstitute()" style=\"width: 124px\"><input type="radio" name="highestEducation" id=\"' + id + '\" value=\"' + id + '\" required>' + name + '</label>';
        $('#candidateHighestEducation').append(option);
        educationArray.push(item);
    });
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

function processLeadUpdate(returnedData) {
    if(returnedData.leadType != '0' && returnedData.leadType != '1') {
        // existing data hence pre fill form
    } else {
        clearModal();
        alert('Unable to show data');
    }
    window.location="/support";
}

function activateEdit() {
    $("#saveBtn").prop("disabled", false);
    $("#cancelBtn").prop("disabled", false);
    $("#candidateSignUpSupportForm input").prop("disabled", false);
    $('#callNoClass').hide();
    $('#callYesClass').show();
}

function onCallYes(leadId){
    activateEdit();
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
   // var jobName = getJobName(selectedJobPref_array[check++]);
    var parent = $('#skill_details');

    var skillParent = $("#skillName");
    var skillQualifierParent = $("#skillQualifiers");

    var head = document.createElement("label");
    head.style.display = "block";
    // head.innerHTML = "<br>Skills for " + jobName;
    skillParent.append(head);
    parent.append(head);

    returnedData.forEach(function (singleSkill) {
        var q = document.createElement("h5");

        var question = singleSkill.skill.skillName;
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

            var op = document.createElement("label");
            op.innerHTML = "&nbsp;" + x.qualifier + "  &nbsp;&nbsp;&nbsp";

            q.appendChild(o);
            q.appendChild(op);
        });
    });
    prefillSkills(candidateSkill);
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

function processDataSignUpSupportSubmit(returnedData) {
    if(returnedData.status == 1){ // save successful
        console.log("Candidate record saved successfully");
        window.location = "/support";
    }
    else if(returnedData.status == 2){ // save failed
        console.log("Unable to save the candidate record!");
        window.location = "/support";
    }
    else { // candidate exists
        console.log("Candidate Already Exists.sig");
        window.location = "/support";
    }
}
function prefillAll() {
    /* ajax commands to fetch candidate's Info */
    var pathname = window.location.pathname; // Returns path only
    var leadId = pathname.split('/');
    leadId = leadId[(leadId.length)-1];
    if(leadId == "basic" || leadId == "skill" || leadId == "education" || leadId == "dashboard"){
        leadId = localStorage.getItem("leadId");
    }

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

}
function saveProfileForm(){
    var localitySelected = $('#candidateLocalityPref').val();
    var jobSelected = $('#candidateJobPref').val();

    if (localitySelected == "") {
        alert("Please Enter your Job Localities");
    } else if (jobSelected == "") {
        alert("Please Enter the Jobs you are Interested");
    } else if($('#candidateHighestDegree').val() == -1){
        alert("Select a Degree");
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
            var selectedDob = $('#candidateDob').val();
            var c_dob = String(selectedDob);
            /* calculate total experience in months */
            var expMonth = parseInt($('#candidateTotalExperienceMonth').val());
            var expYear = parseInt($('#candidateTotalExperienceYear').val());
            var totalExp = expMonth + (12*expYear);

            /* calculate current job duration in months */
            var currentJobMonth = parseInt($('#candidateCurrentJobDurationMonth').val());
            var currentJobYear = parseInt($('#candidateCurrentJobDurationYear').val());
            var currentJobDuration = currentJobMonth + (12 * currentJobYear);
            var motherTongue ="";
            var higherEducation ="";
            var workShift ="";

            if(($('#candidateMotherTongue').val()) != -1){
                motherTongue = $('#candidateMotherTongue').val();
            }
            if(($('#candidateHighestEducation').val()) != -1){
                higherEducation = $('input:radio[name="highestEducation"]:checked').val();
            }
            if(($('#currentWorkShift').val()) != -1){
                workShift = $('#currentWorkShift').val();
            }

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
                candidateCurrentWorkShift: workShift,
                candidateCurrentJobRole: $('#candidateCurrentJobRole').val(),
                candidateCurrentJobDesignation: $('#candidateCurrentJobDesignation').val(),
                candidateCurrentSalary: ($('#candidateCurrentJobSalary').val()),
                candidateCurrentJobDuration: currentJobDuration,

                candidatePastJobCompany: $('#candidatePastCompany').val(),
                candidatePastJobRole: $('#candidatePastJobRole').val(),
                candidatePastJobSalary: ($('#candidatePastJobSalary').val()),

                candidateEducationLevel: higherEducation,
                candidateDegree: ($('#candidateHighestDegree').val()),
                candidateEducationInstitute: $('#candidateEducationInstitute').val(),

                candidateTimeShiftPref: $('#candidateTimeShiftPref').val(),

                candidateMotherTongue: motherTongue,
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
}

// form_candidate ajax script
$(function() {
    $('#candidateJobPref').change(function () {
        generateSkills();
    });
    // auto save code : incomplete
    /*  $('#candidateSignUpSupportForm').change(function () {
        var name = $('#candidateFirstName').val();
        var phone = $('#candidateMobile').val();
        var localitySelected = $('#candidateLocalityPref').val();
        var jobSelected = $('#candidateJobPref').val();
        if((phone.length == 10) && !(phone == "") && !(name == "") && !(localitySelected == "") && !(jobSelected = "") && ()){
            saveProfileForm();
        }
    }); */
    var pathname = window.location.pathname; // Returns path only
    var tempLeadId = pathname.split('/');
    tempLeadId = tempLeadId[(tempLeadId.length)-1];

    if(tempLeadId == 0){
        $('h4#callConfirmation').remove();
        activateEdit();
    }
    prefillAll();
    $("#candidateSignUpSupportForm").submit(function(eventObj) {
        eventObj.preventDefault();
        saveProfileForm();
    }); // end of submit
}); // end of function
