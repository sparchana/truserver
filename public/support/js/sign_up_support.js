/**
 * Created by batcoder1 on 9/5/16.
 */

var skillMap = [];
var languageMap = [];
var localityArray = [];
var jobArray = [];
var assetArray = [];
var transportationArray = [];
var educationArray = [];
var leadSourceArray = [];
var deactivationReasonArray = [];
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
var candidateAssetArray = [];

var candidateSkill = [];
var candidateExps;
var candidatePastJobExp;
var jobPrefString = "";
var check = 0;
var agentAcLvl;

var candidatejobPrefRole;

function getLocality() {
    return localityArray;
}
function getAssets(){
    return assetArray;
}

function getJob() {
    return jobArray;
}

function getIdProofs() {
    return idProofArray;
}
function getAssetsForJobRole(){
    console.log("Result : "+jobPrefString);
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
        $("#assetContainer").hide();
    }
}
function processDataGetAssets(returnedAssets) {
    assetArray = [];
    if(returnedAssets != null || returnedAssets !=""){
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
            $("#candidateAsset").tokenInput('destroy');
            $("#candidateAsset").tokenInput(getAssets(), {
                theme: "facebook",
                placeholder: "What assets do you own?",
                hintText: "Start typing (eg. Smartphone, Bike, Car)",
                minChars: 0,
                prePopulate: candidateAssetArray,
                preventDuplicates: true
            });
        }else{
            $("#assetContainer").hide();
        }
    }
}
$(document).ready(function () {
    var pathname = window.location.pathname; // Returns path only
    var leadId = pathname.split('/');
    leadId = leadId[(leadId.length) - 2];
    if (leadId == "dashboard") {
        leadId = localStorage.getItem("leadId");
    }

    $("#candidateSignUpSupportForm *").prop("disabled", true);

    /* ajax commands to fetch leads Info */
    try {
        $.ajax({
            type: "GET",
            url: "/getLeadMobile/" + leadId,
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

    try {
        $.ajax({
            type: "POST",
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
            url: "/getAllLeadSource",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckLeadSource
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    
    try {
        $.ajax({
            type: "POST",
            url: "/getAllDeactivationReason",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckDeactivationReason
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

    try {
        $.ajax({
            type: "GET",
            url: "/getSupportAgent",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processSupportAgentData
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});
function processSupportAgentData(supportAgentData) {
    agentAcLvl = supportAgentData.agentAccessLevel;

    if(agentAcLvl == 0){
        // Hide Follow up
        $('#btnFloatFollowUp').prop('disabled', true);

        // Hide deactivation panel
        $('#header-deactivate').hide();
        $('#CandidateStatusDetail_panel').hide();
    }
}

function processDataCheckDeactivationReason(reasonList) {
    var defaultOption = $('<option value="-1" selected></option>').text("Select");
    $('#deactivationReason').append(defaultOption);
    reasonList.forEach(function (reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#deactivationReason').append(option);
        deactivationReasonArray.push(item);
    });
}


function processDataCheckUserMobile(returnedData) {
    $("#candidateMobile").val(returnedData.substring(3, 13));
}

function processDataAndFillAllFields(returnedData) {
    if (returnedData == "0") {
    } else {

        $("#candidateFirstName").val(returnedData.candidateFirstName);
        if (returnedData.candidateLastName == "null" || returnedData.candidateLastName == null) {
            $("#candidateSecondName").val("");
        } else {
            $("#candidateSecondName").val(returnedData.candidateLastName);
        }

        $("#candidateMobile").val(returnedData.candidateMobile.substring(3, 13));
        if(returnedData.candidateSecondMobile != null){
            $("#candidateSecondMobile").val(returnedData.candidateSecondMobile.substring(3, 13));
        }
        if(returnedData.candidateThirdMobile){
            $("#candidateThirdMobile").val(returnedData.candidateThirdMobile.substring(3, 13));
        }

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


        try {
            var localityPref = returnedData.localityPreferenceList;
            if (localityPref != null) {
                localityPref.forEach(function (individualLocality) {
                    var id = individualLocality.locality.localityId;
                    var name = individualLocality.locality.localityName;
                    var item = {};
                    item ["id"] = id;
                    item ["name"] = name;
                    localityPrefArray.push(item);
                });
            }

        } catch (err) {
            console.log("getCandidateLocalityPref error" + err);
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

        if (returnedData.lead != null) {
            if (returnedData.lead.leadSource != null) {
                $('#leadSource').val(returnedData.lead.leadSource.leadSourceId);
            }
        }

        // populate past company and past sal fields
        try {
            var jobHistory = returnedData.jobHistoryList;
            jobHistory.forEach(function (historyItem) {
                $("#candidatePastCompany").val(historyItem.candidatePastCompany);
                $("#candidatePastJobSalary").val(historyItem.candidatePastSalary);
                // job role here
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
            $("#candidatePhoneType").val(returnedData.candidatePhoneType);
        } catch (err) {
            console.log(err);
        }


        try {
            if (returnedData.candidateGender != null) {
                if (returnedData.candidateGender == 0) {
                    $('input[id=genderMale]').attr('checked', true);
                } else {
                    $('input[id=genderFemale]').attr('checked', true);
                }
            }
        } catch (err) {
            console.log(err);
        }
        try {
            $("#candidateEmail").val(returnedData.candidateEmail);
        } catch (err) {
            console.log(err);
        }

        try {
            if (returnedData.candidateIsEmployed != null) {
                if (returnedData.candidateIsEmployed == 1) {
                    $('input[id=employed]').attr('checked', true);
                } else {
                    $('input[id=employedNot]').attr('checked', true);
                }
            }
        } catch (err) {
            console.log(err);
        }

        try {
            if (returnedData.timeShiftPreference != null) {
                $("#candidateTimeShiftPref").val(returnedData.timeShiftPreference.timeShift.timeShiftId);
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
                }
                else {
                    document.getElementById("experienced").checked = true;
                    $('#experienced').parent().addClass('active').siblings().removeClass('active');
                    $("#totalWorkExperience").show();
                    $("#candidateTotalExperienceYear").val(parseInt((totalExperience / 12)).toString()); // years
                    $("#candidateTotalExperienceMonth").val(totalExperience % 12); // month

                    candidateExps = returnedData.candidateExpList;
                    candidatePastJobExp = returnedData.jobHistoryList;
                    if(candidateExps != null){
                        generateExperience(jobPrefString);
                        prefillCandidateExp(candidateExps);
                        if(candidatePastJobExp != null){
                            prefillCandidatePastJobExp(candidatePastJobExp);
                        }
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

        try {
            if (returnedData.candidateExperienceLetter != null) {
                if (returnedData.candidateExperienceLetter == true) {
                    // hasPaySlip
                    $('input[id=experienceLetterY]').attr('checked', true);
                } else {
                    $('input[id=experienceLetterN]').attr('checked', true);
                }
            }
        } catch (err) {
            console.log(err);
        }

        if(returnedData.candidateStatusDetail != null){
            prefillCandidateDeActiveStatus(returnedData.candidateStatusDetail);
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


function prefillCandidateDeActiveStatus(candidateStatusDetail){
    if(candidateStatusDetail != null){
        $('#deactivationStatus').attr('checked', true);
        $('#deactivation-sub-options').collapse('show');
        $('#deactivationReason').val(candidateStatusDetail.reason.reasonId);
        $("#deactivation-expiry-panel").show();
        $('#deactivationExpiryDate').val(candidateStatusDetail.statusExpiryDate);
    }
}

function prefillCandidatePastJobExp(candidatePastJobExp) {
    if(candidatePastJobExp != null){
        var prevRow = -1;
        var count = 0;
        candidatePastJobExp.forEach(function (jobHistory) {
            if(jobHistory.jobRole !=  null){
                var jobRoleId = parseInt(jobHistory.jobRole.jobRoleId);
                if(prevRow != jobRoleId){
                    prevRow = jobRoleId;
                    count = 0;
                }
                count++;
                if(jobHistory.candidatePastCompany != null){
                    $('#expPastCompany_'+jobRoleId+' #candidatePastCompany_'+jobRoleId+'_'+count).val(jobHistory.candidatePastCompany);
                    if(jobHistory.currentJob != null && jobHistory.currentJob != false){
                        // set radio to true
                        $('#radio_'+jobRoleId+'_'+count).prop('checked',true);
                    }
                }
            }
        });
    }
}


function prefillCandidateExp(candidateExpList) {
    if(candidateExpList != null){
        var arr = [];
        candidateExpList.forEach(function (candidateExp) {
            if(candidateExp.jobExpQuestion.expCategory.expCategoryName == 'Duration'){
                $("#expDuration_" + candidateExp.jobExpQuestion.jobExpQuestionId).val(candidateExp.jobExpResponse.jobExpResponseOption.jobExpResponseOptionId);
            } else {
                arr.push(""+candidateExp.jobExpResponse.jobExpResponseOption.jobExpResponseOptionId);
                $("#expOther_" + candidateExp.jobExpQuestion.jobExpQuestionId).val(arr);
                $("#expOther_" + candidateExp.jobExpQuestion.jobExpQuestionId).multiselect('rebuild');
            }
            checkExpDurationSelection(candidateExp.jobExpQuestion.jobExpQuestionId, candidateExp.jobExpQuestion.jobRole.jobRoleId);
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

function processDataCheckLocality(returnedData) {
    if (returnedData != null) {
        returnedData.forEach(function (locality) {
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
    returnedData.forEach(function (idProof) {
        var id = idProof.idProofId;
        var name = idProof.idProofName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        idProofArray.push(item);
    });
}

function processDataCheckDegree(returnedData) {
    var defaultOption = $('<option value="-1"></option>').text("Select Degree");
    $('#candidateHighestDegree').append(defaultOption);
    returnedData.forEach(function (degree) {
        var id = degree.degreeId;
        var name = degree.degreeName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#candidateHighestDegree').append(option);
    });
}

function processDataCheckJobs(returnedData) {
    returnedData.forEach(function (job) {
        var id = job.jobRoleId;
        var name = job.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        jobArray.push(item);
    });
}
function processDataCheckAssets(returnedData) {
    assetArray = [];
    returnedData.forEach(function (asset) {
        var id = asset.assetId;
        var name = asset.assetTitle;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        assetArray.push(item);
    });
}

function processDataCheckShift(returnedData) {
    if (returnedData != null) {
        var defaultOption = $('<option value="-1"></option>').text("Select Preferred Shift");
        $('#candidateTimeShiftPref').append(defaultOption);
        returnedData.forEach(function (timeshift) {
            var id = timeshift.timeShiftId;
            var name = timeshift.timeShiftName;
            var option = $('<option value=' + id + '></option>').text(name);
            $('#candidateTimeShiftPref').append(option);
        });
    }
}

function processDataCheckTransportation(returnedData) {
    if (returnedData != null) {
        var defaultOption = $('<option value="-1"></option>').text("Select");
        returnedData.forEach(function (transportation) {
            var id = transportation.transportationModeId;
            var name = transportation.transportationModeName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            var option = $('<option value=' + id + '></option>').text(name);
            transportationArray.push(item);
        });
    }
}
function processDataCheckLeadSource(returnedData) {
    var defaultOption = $('<option value="-1" selected></option>').text("Select");
    $('#leadSource').append(defaultOption);
    returnedData.forEach(function (leadSource) {
        var id = leadSource.leadSourceId;
        var name = leadSource.leadSourceName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#leadSource').append(option);
        leadSourceArray.push(item);
    });
}

function processDataCheckEducation(returnedData) {
    returnedData.forEach(function (education) {
        if(education.educationId != 6){
            var id = education.educationId;
            var name = education.educationName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            var option = '<label class="btn btn-custom-check" style=\"width: 124px\"><input type="radio" name="highestEducation" id=\"highestEducation' + id + '\" value=\"' + id + '\">' + name + '</label>';
            $('#candidateHighestEducation').append(option);
            educationArray.push(item);
        }
    });
}

function processDataCheckLanguage(returnedData) {
    var arrayLang = [];
    var arrayLangId = [];
    var defaultOption = $('<option value="-1"></option>').text("Select");
    returnedData.forEach(function (language) {
        var id = language.languageId;
        var name = language.languageName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        arrayLang.push(name);
        arrayLangId.push(id);
        var option = $('<option value=' + id + '></option>').text(name);
        languageArray.push(item);
    });
    populateLanguages(arrayLang.reverse(), arrayLangId.reverse());
}

function populateLanguages(l, lId) {
    var i;
    var table = document.getElementById("languageTable");
    for (i = 0; i < l.length; i++) {
        if (lId[i] == 1 || lId[i] == 2 || lId[i] == 3 || lId[i] == 4 || lId[i] == 5) {
            var row = table.insertRow(0);

            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);
            var cell3 = row.insertCell(2);
            var cell4 = row.insertCell(3);
            var cell5 = row.insertCell(4);

            cell1.innerHTML = l[i];
            cell2.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
            cell3.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"u\" value=0 >Understand</label></div>";
            cell4.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"s\" value=0 >Speak</label></div>";
            cell5.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"rw\" value=0 >Read/Write</label></div>";
        }
    }
}

function processLeadUpdate(returnedData) {
    if (returnedData.leadType != '0' && returnedData.leadType != '1') {
        // existing data hence pre fill form
    } else {
        clearModal();
        $.notify({
            title: "Invalid Input: ",
            message: "Unable to show data",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
    }
    window.location = "/support";
}

function activateEdit() {
    $("#saveBtn").prop("disabled", false);
    $("#cancelBtn").prop("disabled", false);
    $("#candidateSignUpSupportForm *").prop("disabled", false);
    if ($("#candidateSecondMobile").val().length == 10) {
        $("#candidateThirdMobile").prop("disabled", false);
    } else {
        $("#candidateThirdMobile").prop("disabled", true);
    }

    // if this is not a partner who has logged-in, enable the follow up button
    if (agentAcLvl != 0) {
        $('#btnFloatFollowUp').prop('disabled', false);
    }
    $('#callNoClass').hide();

    if ($('#candidateMobile').val().length == 10) {
        $('#panel-note').show();
    }
}

function onInterestedNo(leadId) {
    $("#candidateSignUpSupportForm *").prop("disabled", true);
    var value = "Lead not interested anymore = Lost";
    updateLeadStatus(leadId, 3, value);
    window.location = "/support";
}

function updateLeadStatus(leadId, leadStatus, value) {
    NProgress.start();
    $.ajax({
        type: "GET",
        url: "/updateLeadStatus/" + leadId + "/" + leadStatus + "/" + value,
        processData: false,
        success: false
    });
    NProgress.done();

}


function onInterestedYes(leadId) {
    activateEdit();
    var value = "CONNECTED";
    updateLeadStatus(leadId, 1, value);
}

function onCallYes(leadId) {
    var pathname = window.location.pathname; // Returns path only
    var pathElement = pathname.split('/');
    var urlSection = pathElement = pathElement[(pathElement.length) - 3];

    $('#callYesClass').addClass('animated fadeIn');
    $('#callNoClass').hide();
    $('#callYesClass').show();
    if(urlSection == 'candidateSignupSupport' && $('#candidateFirstName').val() != ""){
        $('#callYesClass').hide();
        activateEdit();
    }
}

function cancelAndRedirect() {
    if (agentAcLvl != 0) {
        window.location = "/support";
    }
    else {
        window.location = "/createCandidateForm";
    }
}


function onCallNo(leadId) {
    $("#saveBtn").prop("disabled", true);
    $("#cancelBtn").prop("disabled", true);
    $("#candidateSignUpSupportForm *").prop("disabled", true);
    $('#callYesClass').hide();
    $('#callNoClass').show();
    // also saveResponse gets trigger after selecting No and clicking on SaveBtn
}

function saveResponse(leadId) {
    var value = $('#callResponse').val();

    // update status and interaction
    $.ajax({
        type: "GET",
        url: "/updateLeadStatus/"+leadId+"/1/"+value,
        processData: false,
        success: processLeadUpdate
    });
}

function employedYes() {
    $('input:radio[name="currentJob"]').attr('disabled', false);
}

function employedNo() {
    $('input:radio[name="currentJob"]').attr('disabled',true);
    $('input:radio[name="currentJob"]:checked').attr('checked',false);
}

function resetFolloupBox() {
    $('.well').removeClass(' created');
    $('.well').removeClass(' updated');
    $('#btnFollowUp').text('Schedule FollowUp');
    $('#btnFollowUp').removeClass(' animated fadeIn');
    $('#datetimepickerValue').val("");
}

function followUpRequired() {
    $('#followUpScheduler').show();
    if (!$('#followUpRequired').is(':checked')) {
        $('#followUpScheduler').hide();
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

    var count = 0;
    var table = document.getElementById("skillTable");
    $('#skillTable').empty();
    returnedData.forEach(function (singleSkill) {
        count++;
        var row = table.insertRow(0);

        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);

        var btnGroup = document.createElement("div");
        btnGroup.className = "btn-group";
        cell1.innerHTML = singleSkill.skill.skillQuestion;
        btnGroup.appendChild(createBtn(singleSkill, "Yes"));
        btnGroup.appendChild(createBtn(singleSkill, "No"));
        cell2.appendChild(btnGroup);
    });
    if (count == 0) {
        $(".skillSection").hide();
    }
    $(".btn-group").attr("data-toggle", "buttons");
    $(".btn-group").removeClass('active');
    prefillSkills(candidateSkill);
}

function getMinMonthDuration(selectedDurationValue){
    switch(selectedDurationValue) {
        case "2": // > 6 months
            return "6";
            break;
        case "3": // > 1 yrs
            return "12";
            break;
        case "4": // > 2 yrs
            return "24";
            break;
        case "5": // > 4 yrs
            return "48";
            break;
        case "145": // > 6 yrs
            return "72";
            break;
        default: return "0";

    }
}

function validateExpDuration(){
    var yr = parseInt($('#candidateTotalExperienceYear').val());
    var months = parseInt($('#candidateTotalExperienceMonth').val());
    var totalExpInMonths = yr * 12 + months;
    var totalSelectedExpValue = 0;

    $('#expDurationTable tr').each(function () {
        totalSelectedExpValue += parseInt(getMinMonthDuration($(this).find('select').val()));
    });
    if (totalSelectedExpValue > totalExpInMonths) {
        $.notify({
            title: "Invalid Input: ",
            message: "Total Experience does not match with individual experience",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
        return 0;
    } else {
        return 1;
    }
}

function checkExpDurationSelection(questionId, jobRoleId){
    var fresherId = $('#expDuration_'+questionId).val();
    if(fresherId == "1"){
        //its fresher hence hide Header = 'expHeader_' & Body ='expBody_'
        $('#expHeader_' + jobRoleId).hide();
        $('[id=expBody_'+jobRoleId+']').find('select').val('');
        $('[id=expBody_'+jobRoleId+']').find('select').multiselect('rebuild');
        $('[id=expBody_'+jobRoleId+']').hide();
        $('[id=expPastCompanyHead_'+jobRoleId+']').hide();
        $('[id=expPastCompanyBody_'+jobRoleId+']').hide();
    } else {
        $('#expHeader_' + jobRoleId).show();
        $('[id=expBody_'+jobRoleId+']').show();
        $('[id=expPastCompanyHead_'+jobRoleId+']').show();
        $('[id=expPastCompanyBody_'+jobRoleId+']').show();
    }
}

function processDataCheckExp(returnedData) {
    var selectIdList =[];
    var count = 0;
    var tableExpDuration = document.getElementById("expDurationTable");
    var tableExpOther = document.getElementById("expOtherTable");
    $('#expDurationTable').empty();
    $('#expOtherTable').empty();
    var prevJobRoleId = -99;
    returnedData.forEach(function (singleQuestion) {
        if(singleQuestion.expCategory.expCategoryName == 'Duration'){
            count++;
            var row = tableExpDuration.insertRow(0);

            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);

            cell1.innerHTML = '<div id="jobExpDurationQuestionId_'+singleQuestion.jobExpQuestionId+'">'+singleQuestion.jobExpQuestion+'</div>';

            var selectList = document.createElement("select");
            selectList.setAttribute("id", "expDuration_"+singleQuestion.jobExpQuestionId);
            selectList.setAttribute("onchange", "checkExpDurationSelection("+singleQuestion.jobExpQuestionId+", "+singleQuestion.jobRole.jobRoleId+")");
            var option = document.createElement("option");
            option.setAttribute("value", "-1");
            option.text = "Select";
            selectList.appendChild(option);
            var expDurationResponseList = singleQuestion.jobExpResponseList;

            expDurationResponseList.forEach(function (expDurationResponse) {
                if(expDurationResponse != null){
                    var option = document.createElement("option");
                    option.setAttribute("value", expDurationResponse.jobExpResponseOption.jobExpResponseOptionId);
                    option.text = expDurationResponse.jobExpResponseOption.jobExpResponseOptionName;
                    selectList.appendChild(option);
                }
            });
            cell2.appendChild(selectList);
            $('#expDurationTable tr:last').after(row);

            if(prevJobRoleId != singleQuestion.jobRole.jobRoleId){
                // create a new title row
                prevJobRoleId = singleQuestion.jobRole.jobRoleId;
                var jobRoleId = singleQuestion.jobRole.jobRoleId;

                var tr = tableExpOther.insertRow(0);
                tr.id = "expHeader_" + jobRoleId;
                tr.setAttribute("style", "background-color:#337ab7; color:white;");
                var td = tr.insertCell(0);
                tr.insertCell(1);
                td.innerHTML = '<div style="font-weight: bold;"> Tell us about your '+singleQuestion.jobRole.jobName+' experience </div>';
                td.setAttribute("width", "100%");
                $('#expOtherTable tr:last').after(tr);


                var trPastCompany = tableExpOther.insertRow(1);
                trPastCompany.id = "expPastCompanyHead_" + jobRoleId;
                trPastCompany.setAttribute("style", "background-color:#80CBC4; color:white;");
                var tdPastCompany = trPastCompany.insertCell(0);
                tdPastCompany.setAttribute("colspan", "2");
                tdPastCompany.innerHTML = '<div style="font-weight: bold;"> In which company have you worked before as '+singleQuestion.jobRole.jobName+'</div>';
                tdPastCompany.setAttribute("width", "100%");
                $('#expOtherTable tr:last').after(trPastCompany);

                var trPastCompanyName = tableExpOther.insertRow(2);
                trPastCompanyName.id = "expPastCompanyBody_" +jobRoleId;
                var tdCompanyName = trPastCompanyName.insertCell(0);
                tdCompanyName.id = "expPastCompany_" + jobRoleId;
                tdCompanyName.setAttribute("colspan", "2");
                tdCompanyName.innerHTML =
                    '<div class="form-group col-xs-4" ><input id="candidatePastCompany_'+jobRoleId+'_1" type="text" class="form-control col-xs-4" "><input type="radio" id="radio_'+jobRoleId+'_1" name="currentJob" value="'+jobRoleId+'_1" onclick="validateCurrentJob($(this).val())" disabled>&nbsp; Current Job?</div>'+
                    '<div class="form-group col-xs-4" ><input id="candidatePastCompany_'+jobRoleId+'_2" type="text" class="form-control col-xs-4" "><input type="radio" id="radio_'+jobRoleId+'_2" name="currentJob" value="'+jobRoleId+'_2" onclick="validateCurrentJob($(this).val())" disabled>&nbsp; Current Job?</div>'+
                    '<div class="form-group col-xs-4" ><input id="candidatePastCompany_'+jobRoleId+'_3" type="text" class="form-control col-xs-4" "><input type="radio" id="radio_'+jobRoleId+'_3" name="currentJob" value="'+jobRoleId+'_3" onclick="validateCurrentJob($(this).val())" disabled>&nbsp; Current Job?</div>';
                $('#expOtherTable tr:last').after(trPastCompanyName);
            }
            count++;
        }
        else {
            var row = tableExpOther.insertRow(0);
            row.id = "expBody_" + singleQuestion.jobRole.jobRoleId;

            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);

            cell1.innerHTML = '<div id="jobExpQuestionId_'+singleQuestion.jobExpQuestionId+'">'+singleQuestion.jobExpQuestion+'</div>';

            var selectList = document.createElement("select");
            selectList.multiple = true;
            selectList.setAttribute("id", "expOther_"+singleQuestion.jobExpQuestionId);
            selectIdList.push("expOther_"+singleQuestion.jobExpQuestionId);

            var expOtherResponseList = singleQuestion.jobExpResponseList;

            expOtherResponseList.forEach(function (expOtherResponse) {
                if(expOtherResponse != null){
                    var option = document.createElement("option");
                    option.setAttribute("value", expOtherResponse.jobExpResponseOption.jobExpResponseOptionId);
                    option.text = expOtherResponse.jobExpResponseOption.jobExpResponseOptionName;
                    selectList.appendChild(option);
                }
            });
            cell2.appendChild(selectList);
            $('#expOtherTable tr:last').after(row);
        }
    });
    if (count == 0) {
    }
    selectIdList.forEach( function(selectId){
        $('#'+selectId).multiselect({
            dropRight: true,
            numberDisplayed: 0,
            buttonWidth: '100%'
        });
    });
    $(".btn-group").attr("data-toggle", "buttons");
    $(".btn-group").removeClass('active');
    if(candidateExps != null){
        prefillCandidateExp(candidateExps);
    }
}

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

function generateSkills() {
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
}

function processDataSignUpSupportSubmit(returnedData) {
    if (returnedData.status == 1) { // save successful
        if (agentAcLvl == 0) {
            window.location = "/createCandidateForm";
        }
        else {
            window.location = "/support";
        }
    }
    else if (returnedData.status == 2) { // save failed
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
    leadId = leadId[(leadId.length) - 2];
    if (leadId == "dashboard") {
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
            success: processDataAndFillAllFields,
            error: false
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function prefillInteractionNote(leadId) {

        // trigger api to download interaction note for this candidate
        $.ajax({
            type: "GET",
            url: "/getInteractionNote/" + leadId + "/4",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processInteractionNote,
            error: false
        });
}

function validateCurrentJob(currentJobValue) {
    var cellValue = $('#candidatePastCompany_'+currentJobValue).val();
    if(cellValue == "") {

        $.notify({
            title: "Invalid Input: ",
            message: "Please specify company name",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
        $('input:radio[name="currentJob"]:checked').attr('checked',false);
    }
    if($('input:radio[name="employed"]:checked').val() == "0"){
        $('input:radio[name="currentJob"]:checked').attr('checked',false);
        $('input:radio[name="currentJob"]').attr('disabled',true);
        $.notify({
            title: "Invalid Input: ",
            message: "Please mark Currently Employed as Yes to specify current compan",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
    }
}

function processInteractionNote(interactionNoteList) {
    interactionNoteList.forEach(function (interaction) {
        var div = document.createElement('div');
        div.className = 'panel panel-default';

        var dynamicHtml = '<br><div class="panel panel-default">'+
            '<div class="panel-heading"><h6 class="panel-title">'+
            '<a data-toggle="collapse" data-parent="#accordion" href="#collapse'+interaction.interactionId+'">'+interaction.userInteractionTimestamp+'</a>'+
            '</h6></div><div id="collapse'+interaction.interactionId+'" class="panel-collapse collapse">'+
            '<div class="panel-body">'+ interaction.userNote +'</div>'+
            '</div></div>';

        div.innerHTML = dynamicHtml;
        $( ".accordion-inner" ).before( div );
    });
}

function processUpdateFollowUp(returnedData) {
    if(returnedData.status == '1'){
        $('.well').removeClass(' created');
        $('.well').removeClass(' updated');
        $('.well').addClass(' created');
        $('#btnFollowUp').text('Scheduled');
        $('#btnFollowUp').addClass(' animated fadeIn');
    } else if(returnedData.status == '2'){
        $('.well').removeClass(' created');
        $('.well').removeClass(' updated');
        $('.well').addClass(' updated');
        $('#btnFollowUp').text('Scheduled');
        $('#btnFollowUp').addClass(' animated fadeIn');
    }
}

function updateFollowUpApiTrigger(){
    var scheduleTime = $('#datetimepickerValue').val();
    var d = {
        leadMobile: $('#candidateMobile').val(),
        followUpDateTime: new Date(scheduleTime).getTime()
    };
    $.ajax({
        type: "POST",
        url: "/addOrUpdateFollowUp",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(d),
        success: processUpdateFollowUp
    });
}

function updateFollowUpValue() {
    var scheduleTime = $('#datetimepickerValue').val();
    if ($('#followUpRequired').is(':checked') && scheduleTime.length != 0) {
        updateFollowUpApiTrigger();
    } else {
        $.notify({
            title: "Invalid Input: ",
            message: "Please specify Follow Up Date/Time",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
    }
}

function enablePanelFollowUp() {
    $('#panel-followUp').show();
}

function saveProfileForm() {
    var statusCheck = 1;
    var firstName = $('#candidateFirstName').val();
    var lastName = $('#candidateSecondName').val();
    var phone = $('#candidateMobile').val();
    var secondPhone = $('#candidateSecondMobile').val();
    var thirdPhone = $('#candidateThirdMobile').val();
    var firstNameCheck = validateName(firstName);
    if (lastName != "") {
        var lastNameCheck = validateName(lastName);
    }
    var res = validateMobile(phone);

    var localitySelected = $('#candidateHomeLocality').val();
    var jobSelected = $('#candidateJobPref').val();

    var selectedDob = $('#candidateDob').val();
    var c_dob = String(selectedDob);
    var selectedDate = new Date(c_dob);
    var todayDate = new Date();
    var dobCheck = 1;

    /* calculate total experience in months */
    var expMonth = parseInt($('#candidateTotalExperienceMonth').val());
    var expYear = parseInt($('#candidateTotalExperienceYear').val());
    var totalExp = expMonth + (12 * expYear);


    /* document details*/
    var candidateDocumentIdList = $('#candidateIdProof').val().split(",");

    var documentValues = [];
    candidateDocumentIdList.forEach(function (id) {
        var item = {};
        item["idProofId"] = parseInt(id);
        item["idProofValue"] = $('#idProofValue_'+ id).val();
        documentValues.push(item);
    });

    //document value verification
    documentValues.forEach(function(id){
        if(id.idProofId == ""){
            $.notify({
                title: "Invalid Input: ",
                message: "Please select document",
                animate: {
                    enter: 'animated lightSpeedIn',
                    exit: 'animated lightSpeedOut'
                }
            },{
                type: 'danger'
            });
            statusCheck=0;
        }
        else{
            if(id.idProofValue == ""){
                $.notify({
                    title: "Invalid Input: ",
                    message: "Please provide document details",
                    animate: {
                        enter: 'animated lightSpeedIn',
                        exit: 'animated lightSpeedOut'
                    }
                },{
                    type: 'danger'
                });
                statusCheck=0;
            }
            else{
                var isChecked = id.idProofId;
                var isValid = validateInput(isChecked, id.idProofValue.trim());
                if (isChecked && !isValid) {
                    statusCheck = 0;
                    $.notify({
                        title: "Invalid Input: ",
                        message: "Please provide valid document details.",
                        animate: {
                            enter: 'animated lightSpeedIn',
                            exit: 'animated lightSpeedOut'
                        }
                    },{
                        type: 'danger'
                    });
                }

            }
        }
    });


    /* deactivation requirements */
    if($('#deactivationStatus').is(':checked')){
        var expiryDate = $("#deactivationExpiryDate").val();
        var currentDate = new Date();
        if(expiryDate == ""){
            $.notify({
            title: "Invalid Input: ",
            message: "Please provide De-Activation expiry Date",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
            statusCheck = 0;
        } else if(currentDate.getTime() >= new Date(expiryDate).getTime()) {
            $.notify({
            title: "Invalid Input: ",
            message: "Please provide a valid De-Activation expiry date",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
            statusCheck = 0;
        }
        var deactivationReason = $('#deactivationReason').val();
        if(deactivationReason < 0){
            $.notify({
            title: "Invalid Input: ",
            message: "Please provide a valid reason for deactivation",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
            statusCheck = 0;
        }
    }

    if (selectedDate > todayDate) {
        dobCheck = 0;
    }

    // check alternate number format
    var secondMobile = validateMobile(secondPhone);
    if(secondMobile != 2 && secondPhone != ""){
        statusCheck = 0;
        $.notify({
            title: "Invalid Input: ",
            message: "Alternate number should be 10 digit",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
    }
    var thirdMobile= validateMobile(thirdPhone);
    if(thirdMobile != 2 && thirdPhone != ""){
        statusCheck = 0;
        $.notify({
            title: "Invalid Input: ",
            message: "Alternate number should be 10 digit",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
    }

    //checking first name
    switch (firstNameCheck) {
        case 0:
            $.notify({
            title: "Invalid Input: ",
            message: "First name contains number. Please Enter a valid First Name",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
            statusCheck = 0;
            break;
        case 2:
            $.notify({
            title: "Invalid Input: ",
            message: "First Name cannot be blank spaces. Enter a valid first name",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
            statusCheck = 0;
            break;
        case 3:
            $.notify({
            title: "Invalid Input: ",
            message: "First name contains special symbols. Enter a valid first name",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
            statusCheck = 0;
            break;
        case 4:
            $.notify({
            title: "Invalid Input: ",
            message: "Please enter your first name",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
            statusCheck = 0;
            break;
    }

    if (res == 0) {
        $.notify({
            title: "Invalid Input: ",
            message: "Enter a valid mobile number",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
        statusCheck = 0;
    } else if (res == 1) {
        $.notify({
            title: "Invalid Input: ",
            message: "Enter 10 digit mobile number",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
        statusCheck = 0;
    } else if ($("#candidateHomeLocality").val() == "") {
        $.notify({
            title: "Invalid Input: ",
            message: "Please Enter your current Localities",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
        statusCheck = 0;
    } else if (jobSelected == "") {
        $.notify({
            title: "Invalid Input: ",
            message: "Please Enter the Jobs you are Interested",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
        statusCheck = 0;
    } else if (dobCheck == 0) {
        $.notify({
            title: "Invalid Input: ",
            message: "Please enter valid date of birth",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
        statusCheck = 0;
    } else if ($('#candidateTotalExperienceYear').val() > 30) {
        $.notify({
            title: "Invalid Input: ",
            message: "Please enter valid years of experience",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
        statusCheck = 0;
    } else if (($('input:radio[name="workExperience"]:checked').val() == 1) && totalExp == 0) {
        $.notify({
            title: "Invalid Input: ",
            message: "Please select total years of experience",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
        statusCheck = 0;
    }

    //checking last name
    switch (lastNameCheck) {
        case 0:
            $.notify({
            title: "Invalid Input: ",
            message: "Last name contains number. Please Enter a valid Last Name",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
            statusCheck = 0;
            break;
        case 2:
            $.notify({
            title: "Invalid Input: ",
            message: "Last Name cannot be blank spaces. Enter a valid Last name",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
            statusCheck = 0;
            break;
        case 3:
            $.notify({
            title: "Invalid Input: ",
            message: "Last name contains special symbols. Enter a valid Last name",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
            statusCheck = 0;
            break;
        case 4:
            $.notify({
            title: "Invalid Input: ",
            message: "Please enter your Last name",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
            statusCheck = 0;
            break;
    }
    if($('#leadSource').val() == '-1'){
        $.notify({
            title: "Invalid Input: ",
            message: "Please select a Lead Source",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });
        statusCheck = 0;
    }
    if(statusCheck != 0){
        statusCheck = validateExpDuration();
        /*console.log("statusCheck for statusCheck: " + statusCheck);*/
    }
    if(Offline.state == "down"){
        $.notify({
                title: "Error !!:",
                message: "Check Your Internet Connection and Please press save again",
                animate: {
                    enter: 'animated lightSpeedIn',
                    exit: 'animated lightSpeedOut'
                }
            },{
                type: 'danger'
            });
            $("#saveBtn").attr("disabled", false);
        statusCheck = 0;
    };


    if (statusCheck == 1) {
        var languageKnown = $('#languageTable input:checked').map(function () {
            check = 0;
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

        if (($('input:radio[name="workExperience"]:checked').val() == 0)) {
            totalExp = 0;
        } else if (($('input:radio[name="workExperience"]:checked').val() == undefined)) {
            totalExp = null;
        }

        document.getElementById("saveBtn").disabled = true;
        try {
            /* calculate current job duration in months */
            var currentJobMonth = parseInt($('#candidateCurrentJobDurationMonth').val());
            var currentJobYear = parseInt($('#candidateCurrentJobDurationYear').val());
            var currentJobDuration = currentJobMonth + (12 * currentJobYear);
            var motherTongue = "";
            var higherEducation = "";
            var workShift = "";

            if (($('#candidateHighestEducation').val()) != -1) {
                higherEducation = $('input:radio[name="highestEducation"]:checked').val();
            }

            var candidatePreferredJob = [];
            var candidatePreferredAsset = [];

            var jobPref = $('#candidateJobPref').val().split(",");
            var assetList = $('#candidateAsset').val().split(",");
            var i;

            /* Candidate job role preferences  */
            for (i = 0; i < jobPref.length; i++) {
                candidatePreferredJob.push(parseInt(jobPref[i]));
            }
            /* Candidate asset list  */
            for (i = 0; i < assetList.length; i++) {
                candidatePreferredAsset.push(parseInt(assetList[i]));
            }

            // past job company names
            var pastJobArray = [];

            if(candidatePreferredJob !=  null){
                candidatePreferredJob.forEach(function(jobRoleId){
                    var candidatePastCompanyFirst = $('#candidatePastCompany_'+ jobRoleId + '_1').val();
                    var candidatePastCompanySecond = $('#candidatePastCompany_'+ jobRoleId + '_2').val();
                    var candidatePastCompanyThird = $('#candidatePastCompany_'+ jobRoleId + '_3').val();

                    pastJobArray.push(constructPastJobObj(jobRoleId, candidatePastCompanyFirst, 1));
                    pastJobArray.push(constructPastJobObj(jobRoleId, candidatePastCompanySecond, 2));
                    pastJobArray.push(constructPastJobObj(jobRoleId, candidatePastCompanyThird, 3));
                }) ;
            }

            /* Candidate ID proof */
            var candidateIdProofArray = [];
            var candidateIdProof = $('#candidateIdProof').val().split(",");
            for (i = 0; i < candidateIdProof.length; i++) {
                candidateIdProofArray.push(parseInt(candidateIdProof[i]));
            }

            if(!$('#followUpRequired').is(':checked')) {
                $('#datetimepickerValue').val("");
                updateFollowUpApiTrigger();
            }

            var expList = [];

            // iterate through two table and prep data
            $('#expDurationTable tr').each(function(){
                var item = {};
                $(this).find('td').each(function(){
                    $(this).find('div').each(function(){
                        item ["jobExpQuestionId"] = parseInt($(this).attr('id').split("_").slice(-1).pop());
                    });
                    $(this).find('select').each(function(){
                        item ["jobExpResponseIdArray"] = parseInt($(this).val());
                    });
                });
                if(!jQuery.isEmptyObject(item)){
                    expList.push(item);
                };
            });

            var idProofList = [];

            // TODO implement revalidation before submission of all doc type
            $('#docTableTable tr').each(function(){
                var item = {};
                $(this).find('td').each(function(){
                    $(this).find('input').each(function(){
                        item ["idProofId"] = parseInt($(this).attr('id').split("_").slice(-1).pop());
                        item ["idProofValue"] = $(this).val();
                    });
                });
                if(!jQuery.isEmptyObject(item)){
                    idProofList.push(item);
                };
            });

            $('#expOtherTable tr').each(function(){
                var item = {};
                $(this).find('td').each(function(){
                    $(this).find('input').each(function(){
                        if($(this).attr('id')!= undefined){
                            item ["jobExpQuestionId"] = parseInt($(this).attr('id').split("_").slice(-1).pop());
                        }
                    });
                    $(this).find('select ').each(function(){
                        if($(this).val() != null){
                            item ["jobExpResponseIdArray"] = $(this).val().map(function(x) {
                                return parseInt(x);
                            });
                        }
                    });
                });
                if(!jQuery.isEmptyObject(item)) {
                    expList.push(item);
                };
            });

            var d = {
                //mandatory fields
                candidateFirstName: $('#candidateFirstName').val(),
                candidateSecondName: $('#candidateSecondName').val(),
                candidateMobile: $('#candidateMobile').val(),
                candidateSecondMobile: $('#candidateSecondMobile').val(),
                candidateThirdMobile: $('#candidateThirdMobile').val(),
                candidateJobPref: candidatePreferredJob,

                leadSource: $('#leadSource').val(),
                //others
                candidateDob: c_dob,
                candidateGender: ($('input:radio[name="gender"]:checked').val()),
                candidateHomeLocality: parseInt($('#candidateHomeLocality').val()),
                candidateEmail: $('#candidateEmail').val(),
                candidateIsEmployed: ($('input:radio[name="employed"]:checked').val()),
                candidateTotalExperience: totalExp,

                candidateEducationLevel: higherEducation,
                candidateDegree: ($('#candidateHighestDegree').val()),
                candidateEducationInstitute: $('#candidateEducationInstitute').val(),

                candidateTimeShiftPref: $('#candidateTimeShiftPref').val(),

                candidateLanguageKnown: languageMap,

                candidateSkills: skillMap,

                candidateIdProofList: idProofList,
                candidateAssetList: candidatePreferredAsset,
                candidateExperienceLetter: parseInt($('input:radio[name="experienceLetter"]:checked').val()),

                supportNote: ($('#supportNote').val()),

                expList: expList,
                candidateEducationCompletionStatus: parseInt($('input:radio[name="candidateEducationCompletionStatus"]:checked').val()),
                pastCompanyList: pastJobArray,
                candidateLastWithdrawnSalary: parseInt($('#candidateLastWithdrawnSalary').val()),
                deactivationStatus: $('#deactivationStatus').is(':checked'),
                deactivationReason: $('#deactivationReason').val(),
                deactivationExpiryDate: $('#deactivationExpiryDate').val()
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

function constructPastJobObj(jobRoleId, cName ,i){
   /* if(jobRoleId != null && (cName !=  null || cName != "")){

    }*/
    var candidatePastJobObj = {};
    candidatePastJobObj ["jobRoleId"] = parseInt(jobRoleId);
    candidatePastJobObj ["companyName"] = cName;
    candidatePastJobObj ["current"] = checkIsCurrent(jobRoleId, i);
    return candidatePastJobObj;
}

function checkIsCurrent(jobRoleId, i){
    try {

        var currentJob = $('input[name=currentJob]:checked').val();
        if(currentJob != null){
            var currentJobRoleId = currentJob.split("_")[0];
            var IdxMarkedAsCurrent = currentJob.split("_")[1];
            if(currentJobRoleId == jobRoleId && IdxMarkedAsCurrent == i){
                return true;
            } else {
                return false;
            }
        }
    } catch (e){
        console.log("exception !!" + e);
    }
}

function clickFresher(){
    $("#totalWorkExperience").hide();
}

function unlockcurrentJobRadio() {
    $('input[name=currentJob]').attr('disabled', false);
}

function clickExperienced(){
    $("#totalWorkExperience").show();
    generateExperience($('#candidateJobPref').val());
    if($('input[name=employed]:checked').val() == "1") {
        unlockcurrentJobRadio();
    }
}

function ifMobileExists(returnedId) {
    if(returnedId != null && returnedId != "0"){
        window.location = "/candidateSignupSupport/"+returnedId+"/false";
    }
}


function validateIp() {
    var id = this.id.split("_")[1];
    // aadhaar validation
    if(id == 3) {
        if(!validateAadhar(this.value)){
            $('#saveBtn').prop('disabled', true);
            notifyError("Invalid Aadhaar Card Number. (Example: 100120023003)", 'danger');
        } else {
            $('#saveBtn').prop('disabled', false);
        }
    }
    if(id == 1) {
        if (!validateDL(this.value)){
            $('#saveBtn').prop('disabled', true);
            notifyError("Invalid Driving Licence Number. (Example: TN7520130008800 or TN-7520130008800)", 'danger');
        } else {
            $('#saveBtn').prop('disabled', false);
        }
    }
    if(id == 2) {
        if(!validatePASSPORT(this.value)){
            $('#saveBtn').prop('disabled', true);
            notifyError("Invalid Pass Port Number. (Example: A12 34567)", 'danger');
        } else {
            $('#saveBtn').prop('disabled', false);
        }
    }
    if(id == 4){
        if(!validatePAN(this.value)){
           $('#saveBtn').prop('disabled', true);
            notifyError("Invalid PAN Card Number. (Example: ABCDE1234Z)", 'danger');
        } else {
            $('#saveBtn').prop('disabled', false);
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

function processDocs(returnedData) {
    var count = 0;
    var table = document.getElementById("docTableTable");
    $('#docTableTable').empty();
    returnedData.forEach(function (idProof) {
        count++;
        var row = table.insertRow(0);

        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);

        cell1.innerHTML = idProof.idProofName;
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
        if (selectedIdProofIds != null && selectedIdProofIds !='') {
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

// form_candidate ajax script
$(function () {
    /* offline check init */
    var run = function(){
        if (Offline.state === 'up')
            Offline.check();
    };
    setInterval(run, 3000);

    var pathname = window.location.pathname; // Returns path only
    var pathElement = pathname.split('/');
    pathElement = pathElement[(pathElement.length) - 2];

    $('#candidateMobile').change(function () {
        if ($('#candidateMobile').val().length == 10) {
            $('#panel-note').show();

            // if this is not a partner who has logged-in, enable the follow up button
            if (agentAcLvl != 0) {
                $('#btnFloatFollowUp').prop('disabled', false);
            }
            $.notify({
                message: "Please wait while we check if the candidate already exists.",
                animate: {
                    enter: 'animated lightSpeedIn',
                    exit: 'animated lightSpeedOut'
                }
            },{
                type: 'warning'
            });
            $.ajax({
                type: "GET",
                url: "/support/ifExists/"+$('#candidateMobile').val(),
                contentType: "application/json; charset=utf-8",
                success: ifMobileExists
            });
        }
    });

    $('#candidateIdProof').change(function () {
        generateIdProof(null);
    });
    $('#candidateJobPref').change(function () {
        generateSkills();
        generateExperience($('#candidateJobPref').val());
        prefillCandidatePastJobExp(candidatePastJobExp);
        unlockcurrentJobRadio();
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
    });
    $('#candidateSecondMobile').change(function () {
        if($(this).val().length == 10){
            $("#candidateThirdMobile").prop("disabled", false);
        }
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

    disableOrEnableOnCallPanel();

    // path element = 0 means the leadId = 0, which means the user has opened a new candidate create form
    // and is not trying to edit an exiting candidate
    // in this case we remove the prev-notes and follow-up sections.
    if (pathElement == 0) {
        $('div#panel-prevNotes').remove();
        $('#btnFloatFollowUp').prop('disabled', true);
    }
    else {
        prefillInteractionNote(pathElement);
    }

    prefillAll();

    $("#candidateSignUpSupportForm").submit(function (eventObj) {
        eventObj.preventDefault();
        saveProfileForm();
        setTimeout(function () {
            $.notify({
                title: "Oops! Something Went Wrong! ",
                message: "Please Press Save Button Again",
                animate: {
                    enter: 'animated lightSpeedIn',
                    exit: 'animated lightSpeedOut'
                }
            },{
                type: 'danger'
            });
            $("#saveBtn").attr("disabled", false);
        }, 15000);
    }); // end of submit

    $('#datetimepicker').datetimepicker({
        showClear: true,
        minDate: new Date(),
        format: 'MM/DD hh:mm a',
        useCurrent: true,
        dayViewHeaderFormat: 'MMMM',
        toolbarPlacement: 'default',
        showClose: true
    });

    $( "#candidateDob").datepicker({ dateFormat: 'yy-mm-dd', changeYear: true});
    $( "#deactivationExpiryDate").datepicker({ dateFormat: 'yy-mm-dd', changeYear: true});
}); // end of function


function disableOrEnableOnCallPanel() {
    var isOnCallTrigger = $("#isOnCallTrigger").val();

    if (isOnCallTrigger == "false") {
        $('h4#callConfirmation').hide();
        $('div#callYesClass').hide();
        activateEdit();
    }
    else {
        $('h4#callConfirmation').show();
    }
}

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
    }
    else{
        return true;
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


