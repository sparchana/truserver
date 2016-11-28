/**
 * Created by adarsh on 14/9/16.
 */

var languageArray = [];
var skillMap = [];
var educationArray = [];
var languageMap = [];
var localityArray = [];
var jobArray = [];
var assetArray = [];
var idProofArray = [];

var jobPrefArray = [];
var localityPrefArray = [];
var currentLocationArray = [];
var candidateSkill = [];
var currentJobRole = [];
var candidateIdProofArray = [];
var candidateAssetArray = [];


var jobPrefString;

function getLocality(){
    return localityArray;
}

function getJob() {
    return jobArray;
}

function getAssets(){
    return assetArray;
}
$(document).ready(function() {
    /* Section Disable */
    $("#totalWorkExperience").hide();
    $("#educationalInstitute").hide();
    $("#isEmployedForm").hide();
    $("#isEmployedSelect").hide();
    checkPartnerLogin();

    $("#registerBtnSubmit").addClass("btn-primary").removeClass("appliedBtn").prop('disabled', true).html("Save");

    //getting all partner types
    try {
        $.ajax({
            type: "POST",
            url: "/getAllPartnerType",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckPartnerType
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
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

    //date of birth
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
    for(i = new Date().getFullYear() - 18;i>=new Date().getFullYear() - 80;i--){
        option = document.createElement("option");
        option.value = i;
        option.textContent = i;
        $('#dob_year').append(option);
    }

    //total work experience
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

    //education
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
    var pathname = window.location.pathname; // Returns path only
    var leadId = pathname.split('/');
    leadId = leadId[(leadId.length) - 1];
    if(leadId != '0'){ //this is for viewing a profile
        if(leadId != undefined){
            $("#registerBtnSubmit").addClass("btn-primary").removeClass("appliedBtn").prop('disabled', false).html("Save");

            try {
                $.ajax({
                    type: "POST",
                    url: "/getPartnerCandidate/" + leadId,
                    data: false,
                    async: false,
                    contentType: false,
                    processData: false,
                    success: processDataAndFillAllFields,
                    error: false
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }

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

    //getting all jobRoles
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
    /* ajax commands to fetch all id proofs*/
    try {
        $.ajax({
            type: "POST",
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


});


function processDataCheckJobs(returnedData) {
    jobArray = [];
    returnedData.forEach(function(jobRole)
    {
        var id = jobRole.jobRoleId;
        var name = jobRole.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        jobArray.push(item);
    });

}

function processDataCheckLocality(returnedData) {
    localityArray = [];
    returnedData.forEach(function(locality) {
        var id = locality.localityId;
        var name = locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        localityArray.push(item);
    });
}

function processDataAndFillAllFields(returnedData) {
    if (returnedData == "0" || returnedData == "-1") {
        notifyError("You are not authorized to view other candidate's details!!");
    } else {
        $("#candidateFirstName").val(returnedData.candidateFirstName);
        if (returnedData.candidateLastName == "null" || returnedData.candidateLastName == null) {
            $("#candidateLastName").val(undefined);
        } else {
            $("#candidateLastName").val(returnedData.candidateLastName);
        }

        $("#candidateMobile").val(returnedData.candidateMobile.substring(3, 13));
        $("#candidateMobile").attr("disabled", "disabled");

        /* get Candidate's job preference */
        try {
            var jobPref = returnedData.jobPreferencesList;
            jobPref.forEach(function (job) {
                var id = job.jobRole.jobRoleId;
                var name = job.jobRole.jobName;
                var item = {};
                item ["id"] = id;
                item ["name"] = name;
                jobPrefArray.push(item);
                jobPrefString +=id + ",";
            });
            jobPrefString = jobPrefString.substring(0, jobPrefString.length - 1);
        } catch (err) {
            console.log(err);
        }

        /* get Candidate's home location */
        if (returnedData.locality != null) {
            try {
                var item = {};
                item ["id"] = returnedData.locality.localityId;
                item ["name"] = returnedData.locality.localityName;
                currentLocationArray.push(item);

            } catch (err) {
                console.log("homeLocality" + err);
            }
        }
        /* get Candidate's idProofs */
        console.log(returnedData.idProofReferenceList);
        try {
            var idProof = returnedData.idProofReferenceList;
            var tempIdProofList = []
            idProof.forEach(function (singleIdProof) {
                tempIdProofList.push(singleIdProof.idProof);
                var id = singleIdProof.idProof.idProofId;
                var name = singleIdProof.idProof.idProofName;
                var number = singleIdProof.idProofNumber;
                var item = {};
                item ["id"] = id;
                item ["name"] = name;
                item ["number"] = number;
                candidateIdProofArray.push(item);
            });
            if(candidateIdProofArray != null && candidateIdProofArray.length > 0) {
                generateIdProof(tempIdProofList);
            }
        } catch (err) {
            console.log(err);
        }

        /* get Candidate's assets */
        try {
            var assets = returnedData.candidateAssetList;
            assets.forEach(function (singleAsset) {
                var id = singleAsset.asset.assetId;
                var name = singleAsset.asset.assetTitle;
                var item = {};
                item ["id"] = id;
                item ["name"] = name;
                candidateAssetArray.push(item);
            });
        } catch (err) {
            console.log(err);
        }

        // populate past company and past sal fields
        try {
            var jobHistory = returnedData.jobHistoryList;
            jobHistory.forEach(function (historyItem) {
                $("#candidateCurrentCompany").val(historyItem.candidatePastCompany);
                // job role here
            });
        } catch (err) {
            console.log(err);
        }


        try {
            if (returnedData.candidateDOB != null) {
                var date = JSON.parse(returnedData.candidateDOB);
                var yr = new Date(date).getFullYear();
                var month = ('0' + parseInt(new Date(date).getMonth() + 1)).slice(-2);
                var d = ('0' + new Date(date).getDate()).slice(-2);
                $("#candidateDob").val(yr + "-" + month + "-" + d);
                $("#dob_day").val(d);
                $("#dob_month").val(month);
                $("#dob_year").val(yr);
            }
        } catch (err) {
            console.log(err);
        }

        try {
            if (returnedData.candidateGender != null) {
                if (returnedData.candidateGender == 0) {
                    $('input[id=genderMale]').attr('checked', true);
                    $('input[id=genderMale]').parent().addClass('active').siblings().removeClass('active');
                } else {
                    $('input[id=genderFemale]').attr('checked', true);
                    $('input[id=genderFemale]').parent().addClass('active').siblings().removeClass('active');
                }
            }
        } catch (err) {
            console.log(err);
        }

        try {
            if (returnedData.candidateIsEmployed != null) {
                if (returnedData.candidateIsEmployed == 1) {
                    $('input[id=isEmployedYes]').attr('checked', true);
                    $('input[id=isEmployedYes]').parent().addClass('active').siblings().removeClass('active');
                    $("#isEmployedForm").show();
                } else {
                    $('input[id=isEmployedNo]').attr('checked', true);
                    $('input[id=isEmployedNo]').parent().addClass('active').siblings().removeClass('active');
                    $("#isEmployedForm").hide();
                }
            }
        } catch (err) {
            console.log(err);
        }

        try {
            if (returnedData.timeShiftPreference != null) {
                $("#candidateTimeShift").val(returnedData.timeShiftPreference.timeShift.timeShiftId);
            }
            if(returnedData.candidateLastWithdrawnSalary != null){
                $('#candidateLastWithdrawnSalary').val(returnedData.candidateLastWithdrawnSalary);
            }
            if (returnedData.candidateTotalExperience != null) {
                var totalExperience = parseInt(returnedData.candidateTotalExperience);
                if (totalExperience == 0) {
                    document.getElementById("fresher").checked = true;
                    $('#fresher').parent().addClass('active').siblings().removeClass('active');
                    $("#totalWorkExperience").hide();
                    $("#lastWithdrawnSalaryLayout").hide();
                    $("#isEmployedSelect").hide();

                }
                else {
                    $("#lastWithdrawnSalaryLayout").show();
                    $("#isEmployedSelect").show();
                    document.getElementById("experienced").checked = true;
                    $('#experienced').parent().addClass('active').siblings().removeClass('active');
                    $("#totalWorkExperience").show();
                    $("#candidateTotalExperienceYear").val(parseInt((totalExperience / 12)).toString()); // years
                    $("#candidateTotalExperienceMonth").val(totalExperience % 12); // years

                    if(returnedData.jobHistoryList != null){
                        var candidatePastJobList = returnedData.jobHistoryList;
                        candidatePastJobList.forEach(function (jobHistory) {
                            if(jobHistory.candidatePastCompany != null && jobHistory.candidatePastCompany != "" && jobHistory.currentJob != false && jobHistory.jobRole != null){
                                $("#candidateCurrentCompany").val(jobHistory.candidatePastCompany);
                                var item = {};
                                item ["id"] = jobHistory.jobRole.jobRoleId;
                                item ["name"] = jobHistory.jobRole.jobName;
                                currentJobRole.push(item);
                            }
                        });
                    }
                }
            }
        } catch (err) {
            console.log(err);
        }

        try {
            if (returnedData.candidateEducation != null) {
                if (returnedData.candidateEducation.education != null) {
                    document.getElementById("highestEducation" + returnedData.candidateEducation.education.educationId).checked = true;
                    $("#highestEducation" + returnedData.candidateEducation.education.educationId).parent().addClass('active').siblings().removeClass('active');
                    if(returnedData.candidateEducation.education.educationId > 3){
                        $("#educationalInstitute").show();
                    } else{
                        $("#educationalInstitute").hide();
                    }
                }
                if (returnedData.candidateEducation.degree != null) {
                    $("#candidateHighestDegree").val(returnedData.candidateEducation.degree.degreeId);
                }
                if (returnedData.candidateEducation.candidateLastInstitute != null) {
                    $("#candidateEducationInstitute").val(returnedData.candidateEducation.candidateLastInstitute);
                }
                if (returnedData.candidateEducation.candidateEducationCompletionStatus != null) {
                    if (returnedData.candidateEducation.candidateEducationCompletionStatus == '1') {
                        // hasCompletedEducation
                        $('input[id=eduCompleted]').attr('checked', true);
                    } else {
                        $('input[id=eduCompletedNot]').attr('checked', true);
                    }
                }
            }
        } catch (err) {
            console.log(err);
        }


        if (returnedData.languageKnownList != null) {
            prefillLanguageTable(returnedData.languageKnownList);
        }

        if (returnedData.candidateSkillList != null) {
            var skillList = returnedData.candidateSkillList;
            skillList.forEach(function (skillElement) {
                var obj = {};
                obj["skillName"] = skillElement.skill.skillName;
                obj["skillResponse"] = skillElement.candidateSkillResponse;
                candidateSkill.push(obj);
            });
        }
    }
}
function processDataCheckIdProofs(returnedData) {
    returnedData.forEach(function (idProof) {
        var id = idProof.idProofId;
        var name = idProof.idProofName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        idProofArray.push(item);
    });
}
function generateIdProof(idProofJson){
    // create table
    if(idProofJson == null) {
        var selectedIdProofIds = $('#candidateIdProof').val();
        console.log("generateIdproof input field: " + selectedIdProofIds);
        if (selectedIdProofIds != null && selectedIdProofIds != "") {
            try {
                $.ajax({
                    type: "GET",
                    url: "/getAllIdProofs/" + selectedIdProofIds,
                    data: false,
                    contentType: false,
                    processData: false,
                    success: processDocs
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }else{
            $('#docTableTable').empty();
        }
    }else{
        console.log(idProofJson);
        processDocs(idProofJson);
    }
}
function processDocs(returnedData) {
    var count = 0;
    var table = document.getElementById("docTableTable");
    $('#docTableTable').empty();
    returnedData.forEach(function (idProof) {
        console.log(JSON.stringify(idProof));
        count++;
        var row = table.insertRow(0);

        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);

        cell1.innerHTML = idProof.idProofName +" Number";
        var ip = document.createElement("INPUT");
        ip.setAttribute("type", "text");
        ip.setAttribute("id", "idProofValue_"+idProof.idProofId);
        ip.onchange = validateIp;
        cell2.appendChild(ip);
    });
    prefillDocs(candidateIdProofArray);
}
function validateIp() {
    var id = this.id.split("_")[1];
    // aadhaar validation
    if(id == 3) {
        console.log(this.value);
        if(!validateAadhar(this.value)){
            // $('#saveBtn').prop('disabled', true);
            console.log("errror");
            notifyError("Invalid Aadhaar Card Number. (Example: 100120023003)", 'danger');
        } else {
            // $('#saveBtn').prop('disabled', false);
        }
    }
    if(id == 1) {
        console.log(this.value);
        if (!validateDL(this.value)){
            // $('#saveBtn').prop('disabled', true);
            notifyError("Invalid Driving Licence Number. (Example: TN7520130008800 or TN-7520130008800)", 'danger');
        } else {
            // $('#saveBtn').prop('disabled', false);
        }
    }
    if(id == 2) {
        console.log(this.value);
        if(!validatePASSPORT(this.value)){
            // $('#saveBtn').prop('disabled', true);
            notifyError("Invalid Pass Port Number. (Example: A12 34567)", 'danger');
        } else {
            // $('#saveBtn').prop('disabled', false);
        }
    }
    if(id == 4){
        console.log(this.value);
        if(!validatePAN(this.value)){
            // $('#saveBtn').prop('disabled', true);
            notifyError("Invalid PAN Card Number. (Example: ABCDE1234Z)", 'danger');
        } else {
            // $('#saveBtn').prop('disabled', false);
        }
    }
}
function prefillDocs(data) {
    if(data != null && data.length>0) {
        data.forEach(function (iProof) {
            if(iProof.number != null) {
                $('#idProofValue_'+iProof.id).val(iProof.number);
            }
        });
    }
}

function prefillLanguageTable(languageKnownList) {
    $('table#languageTable tr').each(function () {
        $(this).find('input').each(function () {
            //do your stuff, you can use $(this) to get current cell
            var x = document.createElement("INPUT");
            x = $(this).get(0);
            languageKnownList.forEach(function (languageKnown) {
                if (x.id == languageKnown.language.languageId) {
                    if (languageKnown.verbalAbility == "1" && x.name == "s") {
                        x.checked = true;
                        $(x).parent().addClass('active').siblings().removeClass('active');
                    } else if (languageKnown.readWrite == "1" && x.name == "rw") {
                        x.checked = true;
                        $(x).parent().addClass('active').siblings().removeClass('active');
                    } else if (languageKnown.understanding == "1" && x.name == "u") {
                        x.checked = true;
                        $(x).parent().addClass('active').siblings().removeClass('active');
                    }
                }
            });
        });
    });
}

function prefillSkills(candidateSkillList) {
    $('table#skillTable tr').each(function () {
        $(this).find('input').each(function () {
            //do your stuff, you can use $(this) to get current cell
            var skillResponse = document.createElement("INPUT");
            skillResponse = $(this).get(0);
            candidateSkillList.forEach(function (skillElement) {
                if (skillResponse.name == skillElement.skillName && skillResponse.value == skillElement.skillResponse) {
                    skillResponse.checked = true;
                    skillResponse.click();
                }
            });
        });
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

function processDataCheckShift(returnedData) {
    if(returnedData != null ){
        var defaultOption=$('<option value="-1"></option>').text("Select Preferred Shift");
        $('#candidateTimeShift').append(defaultOption);
        returnedData.forEach(function(timeshift)
        {
            var id = timeshift.timeShiftId;
            var name = timeshift.timeShiftName;
            var option=$('<option value=' + id + '></option>').text(name);
            $('#candidateTimeShift').append(option);
        });
    }
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
        var option ='<label class="btn btn-custom-check educationBtn" onchange="checkInstitute()"><input type="radio" name="highestEducation" id=\"highestEducation' + id + '\" value=\"' + id + '\">' + name + '</label>';
        $('#candidateHighestEducation').append(option);
        educationArray.push(item);
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



// form_candidate ajax script
$(function () {
    $('#candidateMobile').change(function () {
        var res = validateMobile($('#candidateMobile').val());
        if(res == 2){
            notifyInfo("Please wait while we check if the candidate already exists");
            $.ajax({
                type: "GET",
                url: "/partner/ifExists/"+$('#candidateMobile').val(),
                contentType: "application/json; charset=utf-8",
                success: ifMobileExists
            });
        } else {
            notifyErrorWithPrompt($("#candidateMobile"), "Please enter a valid phone number");
        }
    });
});

function ifMobileExists(returnedId) {
    if(returnedId != null && returnedId != "0"){
        document.getElementById("partnerCandidateProfile").reset();
        notifyError("Candidate already exists in the database. Create a different candidate");
    } else{
        $("#registerBtnSubmit").addClass("btn-primary").removeClass("appliedBtn").prop('disabled', false).html("Save");
    }
}
function getAssetsForJobRole(){
    var jobRoleId = $('#candidateJobPref').val();
    if (jobRoleId != null && jobRoleId !== ''){
        if(jobRoleId != 0){
            try {
                $.ajax({
                    type: "GET",
                    url: "/support/api/getAssetReqForJobRole/?job_role_ids="+jobRoleId,
                    data: false,
                    async: false,
                    contentType: false,
                    processData: false,
                    success: processDataGetAssets
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
        getAssets();
    }
}
function generateSkills(){
    var selectedJobPref = $('#candidateJobPref').val();
    if (selectedJobPref != null && selectedJobPref !== '') {
        $("#skillQuestion").html('');
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
    prefillSkills(candidateSkill);
}
function processDataGetAssets(returnedAssets){
    while(assetArray.length > 0){
        assetArray.pop();
    }
    if(returnedAssets != null){
        returnedAssets.forEach(function (asset) {
            var id = asset.assetId;
            var name = asset.assetTitle;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            assetArray.push(item);
        });
    }
}
function processDataCheckSkills(returnedData) {
    $(".skillSection").show();
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
        var skillQuestion = singleSkill.skill.skillQuestion;
        skillQuestion = skillQuestion.replace("Do", "Does");
        skillQuestion = skillQuestion.replace("you", "your candidate");
        skillQuestion = skillQuestion.replace("Have", "Has");
        skillQuestion = skillQuestion.replace("have", "has");
        skillQuestion = skillQuestion.replace("Are", "Is");
        ques.textContent = skillQuestion;

        var lbl = document.createElement("div");
        lbl.className = "btn-group";
        lbl.setAttribute("data-toggle", "buttons");
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
            cell1.style.width = '262px';

            cell1.innerHTML = "<div id=\"languageName\">" + l[i] + "</div>";
            cell2.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check educationBtn\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"u\" value=0 >Understand</label></div>";
            cell3.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check educationBtn\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"s\" value=0 >Speak</label></div>";
            cell4.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check educationBtn\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"rw\" value=0 >Read/Write</label></div>";
        }
    }
}

// edit partner profile ajax script
$(function() {
    $("#partnerCandidateProfile").submit(function(eventObj) {
        eventObj.preventDefault();
        //entered values
        var statusCheck = 1;

        var firstName = $('#candidateFirstName').val();
        var lastName = $('#candidateLastName').val();
        var phone = $('#candidateMobile').val();

        var selectedGender = $('input:radio[name="gender"]:checked').val();
        var selectedHomeLocality = $('#candidateHomeLocality').val();
        var jobSelected = $('#candidateJobPref').val();
        var selectedTimeShift = $('#candidateTimeShift').val();
        var selectedDob = $('#dob_year').val() + "-" + $('#dob_month').val() + "-" + $('#dob_day').val();
        var c_dob = String(selectedDob);

        //experience
        var experienceStatus = $('input:radio[name="workExperience"]:checked').val(); // 1 -> "experienced"; 0 -> fresher

        var expMonth = parseInt($('#candidateTotalExperienceMonth').val());
        var expYear = parseInt($('#candidateTotalExperienceYear').val());
        var totalExp = expMonth + (12*expYear);

        var currentlyEmployed = $('input:radio[name="isEmployed"]:checked').val();
        var candidateCurrentCompanyVal = $('#candidateCurrentCompany').val();
        var candidateCurrentJobRole = $('#candidateCurrentJobRole').val();
        var candidateLastWithdrawnSalary = $('#candidateLastWithdrawnSalary').val();

        // idproof document details

        var candidateDocumentIdList = $('#candidateIdProof').val().split(",");
        var candidatePreferredAsset = [];
        var assetList = $('#candidateAsset').val().split(",");
        /* Candidate asset list  */
        for (i = 0; i < assetList.length; i++) {
            candidatePreferredAsset.push(parseInt(assetList[i]));
        }

        var documentValues = [];
        candidateDocumentIdList.forEach(function (id) {
            console.log($('#idProofValue_'+ id).val());
            var item = {};
            item["idProofId"] = parseInt(id);
            item["idProofValue"] = $('#idProofValue_'+ id).val();
            documentValues.push(item);
        });
        console.log(JSON.stringify(documentValues));
        //document value verification
        documentValues.forEach(function(id){
            if(id.idProofId == null){
                notifyError("Please Select Document");
                statusCheck=0;
            }
            var isChecked = id.idProofId;
            var isValid = validateInput(isChecked, id.idProofValue.trim());
            if (isChecked && !isValid) {
                statusCheck = 0;
                $.notify("Please provide valid document details.", 'error');
            }
        });

        // language and skills
        try {
            var languageKnown = $('#languageTable input:checked').map(function () {
                    var check = 0;
                    var id = this.id;
                    var name = this.name;
                    var item = {};
                    var pos;

                    for (var i in languageMap) {
                        if (languageMap[i].id == id) {
                            pos = i;
                            check = 1;
                            break;
                        }
                    }
                    if (check == 0) {
                        item["id"] = id;
                        item["u"] = 0;
                        item["rw"] = 0;
                        item["s"] = 0;
                        if (name == "u")
                            item["u"] = 1;
                        else if (name == "rw")
                            item["rw"] = 1;
                        else
                            item["s"] = 1;
                        languageMap.push(item);
                    }
                    else {
                        if (name == "u")
                            languageMap[pos].u = 1;
                        else if (name == "rw")
                            languageMap[pos].rw = 1;
                        else
                            languageMap[pos].s = 1;
                    }
                }).get();
            } catch(e){} // selected language is in "LanguageMap" object and "skillMap" object has all the selected skills

        // education
        var highestEducation = $('input:radio[name="highestEducation"]:checked').val();
        var selectedDegree = $('#candidateHighestDegree').val();
        var candidateInstitute = $('#candidateEducationInstitute').val();
        var educationCompletionStatus = $('input:radio[name="candidateEducationCompletionStatus"]:checked').val();

        //validation

        var firstNameCheck = validateName(firstName);

        //checking first name
        switch(firstNameCheck){
            case 0: notifyErrorWithPrompt($("#candidateFirstName"), "First name contains number. Please Enter a valid first Name"); statusCheck=0; break;
            case 2: notifyErrorWithPrompt($("#candidateFirstName"), "First Name cannot be blank spaces. Enter a valid first name"); statusCheck=0; break;
            case 3: notifyErrorWithPrompt($("#candidateFirstName"), "First name contains special symbols. Enter a valid first name"); statusCheck=0; break;
            case 4: notifyErrorWithPrompt($("#candidateFirstName"), "Please enter candidate's first name"); statusCheck=0; break;
        }
        if(lastName != "" || lastName != undefined){
            var lastNameCheck = validateName(lastName);
            //checking first name
            switch(lastNameCheck){
                case 0: notifyErrorWithPrompt($("#candidateLastName"), "Last name contains number. Please Enter a valid last Name"); statusCheck=0; break;
                case 2: notifyErrorWithPrompt($("#candidateLastName"), "Last Name cannot be blank spaces. Enter a valid last name"); statusCheck=0; break;
                case 3: notifyErrorWithPrompt($("#candidateLastName"), "Last name contains special symbols. Enter a valid last name"); statusCheck=0; break;
                case 4: notifyErrorWithPrompt($("#candidateLastName"), "Please enter candidate's last name"); statusCheck=0; break;
            }
        } else {
            lastName = null;
        }
        var res = validateMobile(phone);
        if(res == 0){
            notifyErrorWithPrompt($("#candidateMobile"), "Please enter a valid phone number");
            statusCheck=0;
        } else if(res == 1){
            notifyErrorWithPrompt($("#candidateMobile"), "Enter 10 digit mobile number");
            statusCheck=0;
        }  else if(jobSelected == "") {
            notifyErrorWithPrompt($("#candidateJobPrefField"), "Please select your candidate's interested jobs");
            statusCheck=0;
        } else if(selectedHomeLocality == "") {
            notifyErrorWithPrompt($("#candidateHomeLocalityField"), "Please Enter candidate's Home Locality");
            statusCheck=0;
        } else if(selectedTimeShift == -1){
            notifyErrorWithPrompt($("#candidateTimeShift"), "Please select Preferred Work Shift");
            statusCheck=0;
        }  else if($('#dob_day').val() == "" || $('#dob_month').val() == "" || $('#dob_year').val() == ""){
            notifyErrorWithPrompt($("#candidateDobField"), "Please Select Date of Birth");
            statusCheck=0;
        } else if(selectedGender == undefined) {
            statusCheck=0;
            notifyErrorWithPrompt($("#candidateGenderField"), "Please Select Gender");
        } else if(experienceStatus == null){
            notifyErrorWithPrompt($("#candidateWorkExpField"), "Please Select work experience");
            statusCheck=0;
        } else if(candidateLastWithdrawnSalary > 99999){
            notifyErrorWithPrompt($("#candidateLastWithdrawnSalary"), "Please Enter a valid Salary");
            statusCheck=0;
        } else if(!isValidSalary(candidateLastWithdrawnSalary)){
            notifyErrorWithPrompt($("#candidateLastWithdrawnSalary"), "Salary can't have special characters");
            statusCheck=0;
        } else if(experienceStatus == 1 && totalExp == 0){
            notifyErrorWithPrompt($("#candidateTotalWorkExpField"), "Select Total Years of Experience");
            statusCheck=0;
        } else if(experienceStatus == 1 && currentlyEmployed == null){
            notifyErrorWithPrompt($("#currentlyWorkingField"), "Please answer \"Is the candidate currently working?\"");
            statusCheck=0;
        } else if((experienceStatus == 1)  && (candidateLastWithdrawnSalary == undefined || candidateLastWithdrawnSalary == null || candidateLastWithdrawnSalary == "" || candidateLastWithdrawnSalary == "0") ){
            notifyErrorWithPrompt($("#candidateLastWithdrawnSalary"), "Enter enter Last Withdrawn Salary");
            statusCheck=0;
        } else if(languageMap.length == 0 || languageMap.length == null){
            notifyErrorWithPrompt($("#languageField"), "Select candidate's known language");
            statusCheck=0;
        } else if(highestEducation == undefined){
            notifyErrorWithPrompt($("#educationField"), "Select Highest Education");
            statusCheck=0;
        } else if(((highestEducation == 4) || (highestEducation == 5)) && selectedDegree == -1){
            notifyErrorWithPrompt($("#candidateHighestDegree"), "Please select candidate's Degree");
            statusCheck=0;
        }

        if(statusCheck == 1){
            $("#registerBtnSubmit").addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Saving");
            var candidatePreferredJob = [];
            jobSelected = $('#candidateJobPref').val().split(",");
            /* Candidate job role preferences  */
            for (var i = 0; i < jobSelected.length; i++) {
                candidatePreferredJob.push(parseInt(jobSelected[i]));
            }

            if(experienceStatus == 0){
                totalExp = 0;
                candidateCurrentCompanyVal = null;
                candidateCurrentJobRole = null;
                candidateLastWithdrawnSalary = null;
            }
            if(currentlyEmployed == 0){
                candidateCurrentCompanyVal = null;
                candidateCurrentJobRole = null;
            }
            if(highestEducation < 4){
                selectedDegree = null;
                candidateInstitute = null;
            }

            candidateUnVerifiedMobile = phone;

            var d = {
                //mandatory fields
                leadSource: 25, //partner channel is '25'
                candidateFirstName: firstName,
                candidateSecondName: lastName,
                candidateMobile: phone,
                candidateJobPref: candidatePreferredJob,
                candidateHomeLocality: selectedHomeLocality,
                candidateTimeShiftPref: selectedTimeShift,
                candidateIdProofList: documentValues,
                candidateAssetList: candidatePreferredAsset,

                //experience
                candidateDob: c_dob,
                candidateGender: selectedGender,
                candidateIsEmployed: currentlyEmployed,
                candidateTotalExperience: totalExp,
                candidateCurrentCompany: candidateCurrentCompanyVal,
                candidateCurrentJobRoleId: candidateCurrentJobRole,
                candidateLastWithdrawnSalary: candidateLastWithdrawnSalary,

                // education
                candidateEducationLevel: highestEducation,
                candidateDegree: selectedDegree,
                candidateEducationInstitute: candidateInstitute,
                candidateEducationCompletionStatus: parseInt(educationCompletionStatus),

                // language
                candidateLanguageKnown: languageMap,

                //skill
                candidateSkills: skillMap,

                deactivationStatus: false
            };
            $.ajax({
                type: "POST",
                url: "/partnerCreateCandidateSubmit",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataSignUpSupportSubmit
            });
        }
    }); // end of submit
}); // end of function
function validateInput(idProofId, value) {
    // aadhaar validation
    if (idProofId == 3) {
        if (!validateAadhar(value)) {
            return false;
        } else {
            return true;
        }
    } else if (idProofId == 1) {
        if (!validateDL(value)) {
            return false;
        } else {
            return true;
        }
    } else if (idProofId == 2) {
        if (!validatePASSPORT(value)) {
            return false;
        } else {
            return true;
        }
    } else if (idProofId == 4) {
        if (!validatePAN(value)) {
            return false;
        } else {
            return true;
        }
    } else{
        return true;
    }
}
function processDataSignUpSupportSubmit(returnedData) {
    if(returnedData.status == "1"){ //success
        if(returnedData.otp != 0){
            $("#messagePromptModal").modal("show");
            $('#customMsgIcon').attr('src', "/assets/partner/img/applied.png");
            $("#customMsg").html("Thank you for registering! To complete registration please enter the OTP that was sent to candidate's mobile number:" +  $('#candidateMobile').val());
        } else{
            window.location = "/partner/myCandidates";
        }
    } else if(returnedData.status == "-1"){
        logoutPartner();
    } else{
        $("#registerBtnSubmit").addClass("btn-primary").removeClass("appliedBtn").prop('disabled', false).html("Save");
        notifyError("Something went wrong. Please try again later");
    }
}

function verifyCandidateOtp(){
    var candidateOtp = $("#candidateOtp").val();
    if(validateOtp(candidateOtp) == 0){
        notifyError("Please enter a valid 4 digit otp!");
    } else{
        var d = {
            candidateMobile: candidateUnVerifiedMobile,
            userOtp: candidateOtp
        };
        $("#verifyOtp").prop('disabled',true);
        $.ajax({
            type: "POST",
            url: "/verifyCandidateUsingOtp",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataVerifyCandidate
        });

    }
}
function getIdProofs(){
    return idProofArray;
}
$(function () {
    $('#candidateIdProof').change(function () {
        generateIdProof(null);
    });
    $('#candidateJobPref').change(function () {
        generateSkills();
        getAssetsForJobRole();
        $("#candidateAsset").tokenInput('destroy');
        $("#candidateAsset").tokenInput(getAssets(), {
            theme: "facebook",
            placeholder: "What assets do you own?",
            hintText: "Start typing (eg. Smartphone, Bike, Car)",
            minChars: 0,
            prePopulate: candidateAssetArray,
            preventDuplicates: true
        });
        // generateExperience($('#candidateJobPref').val());
        // prefillCandidatePastJobExp(candidatePastJobExp);
        // unlockcurrentJobRadio();
    });
}); // end of function
function processDataVerifyCandidate(returnedData) {
    $("#verifyOtp").prop('disabled', false);
    if(returnedData.status == 1){
        $('#customMsgIcon').attr('src', "/assets/partner/img/correct.png");
        $("#customMsg").html("Verification completed! Candidate is successfully registered.");
        $("#candidateOtp").hide();
        $("#verifyOtp").hide();
        $("#homeBtn").show();
    } else if(returnedData.status == 2){
        $('#customMsgIcon').attr('src', "/assets/partner/img/wrong.png");
        $("#customMsg").html("Incorrect OTP. Please enter correct OTP!");
    } else{
        $("#customMsg").html("Something went wrong! Please try again");
        $('#customMsgIcon').attr('src', "/assets/partner/img/wrong.png");
    }
}

function notifyInfo(msg){
    $.notify(msg, "info");
}

function notifyError(msg){
    $.notify(msg, "error");
}

function notifySuccess(msg){
    $.notify(msg, "success");
}

function notifyErrorWithPrompt(element, msg){
    $(window).scrollTop(element.offset().top - 60);
    element.notify(
        msg,
        { position:"bottom center" }
    );
    notifyError(msg);
}