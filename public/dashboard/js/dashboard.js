/**
 * Created by batcoder1 on 4/6/16.
 */
var candidateId;
var skillMap = [];
var languageMap = [];
var localityArray = [];
var jobArray = [];
var educationArray = [];
var languageArray = [];
var idProofArray = [];
var assetArray = [];
var check = 0;

/* candidate Preference array */
var jobPrefArray = [];
var localityPrefArray = [];
var currentLocationArray = [];
var candidateIdProofArray = [];
var candidateAssetArray = [];

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

    $("#assetContainer").hide();
    $("#totalWorkExperience").hide();
    $("#educationalInstitute").hide();
    $("#isEmployedForm").hide();
    $("#isEmployedSelect").hide();

    getAssetsForJobRole();

    checkUserLogin();
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
    for(i = new Date().getFullYear() - 18;i>=new Date().getFullYear() - 80;i--){
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
    var arrayLang =[];
    var arrayLangId =[];
    var defaultOption=$('<option value="-1"></option>').text("Select");
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
            cell2.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check educationBtn\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"u\" value=0 >Understand</label></div>";
            cell3.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check educationBtn\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"s\" value=0 >Speak</label></div>";
            cell4.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check educationBtn\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"rw\" value=0 >Read/Write</label></div>";
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
    returnedData.forEach(function(education) {
        if(education.educationId != 6){
            var id = education.educationId;
            var name = education.educationName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            var option ='<label class="btn btn-custom-check educationBtn" onchange="checkInstitute()"><input type="radio" name="highestEducation" id=\"highestEducation' + id + '\" value=\"' + id + '\">' + name + '</label>';
            $('#candidateHighestEducation').append(option);
            educationArray.push(item);
        }
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
    candidateId = returnedData.candidateId;
    viewDownloadResume(candidateId);
    $("#jobCount").html(Object.keys(candidateInformation.jobApplicationList).length);

    try{
        var id = returnedData.locality.localityId;
        var name = returnedData.locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        currentLocationArray.push(item);
    } catch (err){}

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
        if(localityPref != null) {
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
        if(candidateAssetArray.length > 0) {
            $("#assetContainer").show();
        }else{
            $("#assetContainer").hide();
        }
    } catch (err) {
        console.log(err);
    }

    /* get Candidate's idProofs */
    try {
        var idProof = returnedData.idProofReferenceList;
        var tempIdProofList = [];
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
        console.log(err.stack);
    }
}
/* end prefill*/
function prefillDocs(data) {
    if(data != null && data.length>0) {
        data.forEach(function (iProof) {
            if(iProof.number != null) {
                $('#idProofValue_'+iProof.id).val(iProof.number);
            }
        });
    }
}
function validateIp() {
    var id = this.id.split("_")[1];
    // aadhaar validation
    if(id == 3) {
        if(!validateAadhar(this.value)){
            // $('#saveBtn').prop('disabled', true);
            notifyError("Invalid Aadhaar Card Number. (Example: 100120023003)", 'danger');
        } else {
            // $('#saveBtn').prop('disabled', false);
        }
    }
    if(id == 1) {
        if (!validateDL(this.value)){
            // $('#saveBtn').prop('disabled', true);
            notifyError("Invalid Driving Licence Number. (Example: TN7520130008800 or TN-7520130008800)", 'danger');
        } else {
            // $('#saveBtn').prop('disabled', false);
        }
    }
    if(id == 2) {
        if(!validatePASSPORT(this.value)){
            // $('#saveBtn').prop('disabled', true);
            notifyError("Invalid Pass Port Number. (Example: A12 34567)", 'danger');
        } else {
            // $('#saveBtn').prop('disabled', false);
        }
    }
    if(id == 4){
        if(!validatePAN(this.value)){
            // $('#saveBtn').prop('disabled', true);
            notifyError("Invalid PAN Card Number. (Example: ABCDE1234Z)", 'danger');
        } else {
            // $('#saveBtn').prop('disabled', false);
        }
    }
}
function processDocs(returnedData) {
    var count = 0;
    var table = document.getElementById("docTableTable");
    if(table == null) return;
    $('#docTableTable').empty();
    returnedData.forEach(function (idProof) {
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
function generateIdProof(idProofJson){
    // create table
    if(idProofJson == null) {
        var selectedIdProofIds = $('#candidateIdProof').val();
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
        }
        else{
            $('#docTableTable').empty();
        }
    } else {
        processDocs(idProofJson);
    }

}
function getLocality(){
    return localityArray;
}

function getJob(){
    return jobArray;
}
function getAssets(){
    return assetArray;
}
function getAssetsForJobRole(){
    var jobRoleId = $('#candidateJobPref').val();
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
        }else{
            getAssets();
            $("#assetContainer").hide();
        }
}
function processDataGetAssets(returnedAssets) {
    while(assetArray.length > 0){
        assetArray.pop();
    }
    if(returnedAssets != null || returnedAssets !="") {
        returnedAssets.forEach(function (asset) {
            var id = asset.assetId;
            var name = asset.assetTitle;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            assetArray.push(item);
        });
        if(assetArray.length > 0) {
            $("#assetContainer").show();
        }else{
            $("#assetContainer").hide();
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
function getIdProofs(){
    return idProofArray;
}
$(function () {
    $('#candidateIdProof').change(function () {
        generateIdProof(null);
    });
    $('#candidateJobPref').change(function () {
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
        /*generateSkills();
        generateExperience($('#candidateJobPref').val());
        prefillCandidatePastJobExp(candidatePastJobExp);
        unlockcurrentJobRadio();*/
    });
}); // end of function
function generateExperience(jobPrefString) {
    var selectedJobPref = jobPrefString;
    if (selectedJobPref != null && selectedJobPref !== '') {
        try {
            $.ajax({
                type: "POST",
                url: "/getJobExpQuestion/" + selectedJobPref,
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataCheckExp
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}
function notifyError(msg, type) {
    $.notify({
        message: msg,
        animate: {
            enter: 'animated lightSpeedIn',
            exit: 'animated lightSpeedOut'
        }
    }, {
        type: type,
        placement: {
            from: "top",
            align: "center"
        }
    });
};