/**
 * Created by adarsh on 14/9/16.
 */

var languageArray = [];
var skillMap = [];
var educationArray = [];
var languageMap = [];
var localityArray = [];
var jobArray = [];

var jobPrefArray = [];
var localityPrefArray = [];
var currentLocationArray = [];
var candidateSkill = [];

var jobPrefString;

function getLocality(){
    return localityArray;
}

function getJob() {
    return jobArray;
}

function checkPartnerLogin() {
    try {
        $.ajax({
            type: "GET",
            url: "/checkPartnerSession",
            data: false,
            contentType: false,
            processData: false,
            success: processDataPartnerSession
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataPartnerSession(returnedData) {
    if(returnedData == 0){
        logoutUser();
    }
}

$(document).ready(function() {
    /* Section Disable */
    $("#totalWorkExperience").hide();
    $("#educationalInstitute").hide();
    $("#isEmployedForm").hide();
    $("#isEmployedSelect").hide();
    checkPartnerLogin();

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
    console.log(returnedData);
    if (returnedData == "0") {
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
                }
                else {
                    document.getElementById("experienced").checked = true;
                    $('#experienced').parent().addClass('active').siblings().removeClass('active');
                    $("#totalWorkExperience").show();
                    $("#candidateTotalExperienceYear").val(parseInt((totalExperience / 12)).toString()); // years
                    $("#candidateTotalExperienceMonth").val(totalExperience % 12); // years

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
                url: "/partner/ifExists/"+$('#candidateMobile').val(),
                contentType: "application/json; charset=utf-8",
                success: ifMobileExists
            });
        } else {
            notifyError("Please enter a valid phone number");
        }
    });

    $('#candidateJobPref').change(function () {
        generateSkills();
    });
});

function ifMobileExists(returnedId) {
    if(returnedId != null && returnedId != "0"){
        document.getElementById("partnerCandidateProfile").reset();
        notifyError("Candidate already exists in the database. Create a different candidate");
    } else{
        $.notify({
            message: "Candidate with the specified mobile doesn't exists! Please continue",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'success'
        });
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
            case 0: notifyError("First name contains number. Please Enter a valid First Name"); statusCheck=0; break;
            case 2: notifyError("First Name cannot be blank spaces. Enter a valid first name"); statusCheck=0; break;
            case 3: notifyError("First name contains special symbols. Enter a valid first name"); statusCheck=0; break;
            case 4: notifyError("Please enter your first name"); statusCheck=0; break;
        }
        if(lastName != "" || lastName != undefined){
            var lastNameCheck = validateName(lastName);
            //checking first name
            switch(lastNameCheck){
                case 0: notifyError("Last name contains number. Please Enter a valid last Name"); statusCheck=0; break;
                case 2: notifyError("Last Name cannot be blank spaces. Enter a valid last name"); statusCheck=0; break;
                case 3: notifyError("Last name contains special symbols. Enter a valid last name"); statusCheck=0; break;
                case 4: notifyError("Please enter your last name"); statusCheck=0; break;
            }
        } else {
            lastName = null;
        }
        var res = validateMobile(phone);
        if(res == 0){
            notifyError("Enter a valid mobile number");
            statusCheck=0;
        } else if(res == 1){
            notifyError("Enter 10 digit mobile number");
            statusCheck=0;
        }  else if(jobSelected == "") {
            notifyError("Please Enter the Jobs you are Interested");
            statusCheck=0;
        } else if(selectedHomeLocality == "") {
            notifyError("Please Enter your Home Locality");
            statusCheck=0;
        } else if(selectedTimeShift == -1){
            statusCheck=0;
            notifyError("Please Enter Your Preferred Work Shift");
        }  else if($('#dob_day').val() == "" || $('#dob_month').val() == "" || $('#dob_year').val() == ""){
            statusCheck=0;
            notifyError("Please Select your Date of Birth");
        } else if(selectedGender == undefined) {
            statusCheck=0;
            notifyError("Please Select your Gender");
        } else if(experienceStatus == null){
            statusCheck=0;
            notifyError("Please Select your work experience");
        } else if(candidateLastWithdrawnSalary > 99999){
            statusCheck=0;
            notifyError("Please Enter a valid Salary")
        } else if(experienceStatus == 1 && totalExp == 0){
            notifyError("Select Total Years of Experience");
            statusCheck=0;
        } else if(experienceStatus == 1 && currentlyEmployed == null){
            statusCheck=0;
            notifyError("Please answer \"Are you currently working?\"");
        } else if((experienceStatus == 1)  && (candidateLastWithdrawnSalary == null || candidateLastWithdrawnSalary == "" || candidateLastWithdrawnSalary) == "0"){
            statusCheck=0;
            notifyError("Enter your Last Withdrawn Salary");
        } else if(languageMap.length == 0 || languageMap.length == null){
            notifyError("Select specify candidate's known language");
            statusCheck=0;
        } else if(highestEducation == undefined){
            notifyError("Select your Highest Education");
            statusCheck=0;
        } else if(((highestEducation == 4) || (highestEducation == 5)) && selectedDegree == -1){
            notifyError("Please select your Degree");
            statusCheck=0;
        }

        if(statusCheck == 1){
            var candidatePreferredJob = [];
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

            var d = {
                //mandatory fields
                candidateFirstName: firstName,
                candidateSecondName: lastName,
                candidateMobile: phone,
                candidateJobPref: candidatePreferredJob,
                candidateHomeLocality: selectedHomeLocality,
                candidateTimeShiftPref: selectedTimeShift,

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

function processDataSignUpSupportSubmit(returnedData) {
    if(returnedData.status == "1"){ //success
        window.location = "/partner/home";
    } else if(returnedData.status == "-1"){
        logoutUser();
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

function notifyError(msg){
    $.notify({
        message: msg,
        animate: {
            enter: 'animated lightSpeedIn',
            exit: 'animated lightSpeedOut'
        }
    },{
        type: 'danger'
    });
}