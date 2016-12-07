/**
 * Created by hawk on 29/11/16.
 */
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

/* function for required for shift */

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

/* function for required for ID proof */
function getIdProofs(){
    return idProofArray;
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

/* function for required for locality*/
function getLocality(){
    return localityArray;
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

/* function for required for language */

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

/* function for required for education */
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

/* function for required for assets */

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
    }
}
function processDataGetAssets(returnedAssets) {
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

/* function for required for Experience */
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

/* function for required for Job Role */
function getJob(){
    return jobArray;
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

/* function for required for skills */
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

/* Prefill the data */

function processDataAndFillAllFields(returnedData) {
    candidateInformation = returnedData;
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


/*Notification function */
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