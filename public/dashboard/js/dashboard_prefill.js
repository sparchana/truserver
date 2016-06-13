/**
 * Created by batcoder1 on 4/6/16.
 */

var candidateSkill = [];

/* start of javascript */
$(document).ready(function(){
    $("#educationalInstitute").hide();
    prefillBasicProfile();
});

function processDataCandidateSaveBasicProfile(returnedData) {
    var heading = document.getElementById('basicHeading');
    heading.style.color = '#9f9f9f';
    heading = document.getElementById('skillsHeading');
    heading.style.color = '#2980b9';
    heading = document.getElementById('educationHeading');
    heading.style.color = '#9f9f9f';

    document.getElementById('basicImg').src = "/assets/dashboard/img/basic_disable.png";
    document.getElementById('skillImg').src = "/assets/dashboard/img/skills_enable.png";
    document.getElementById('educationImg').src = "/assets/dashboard/img/education_disable.png";

    $("#basicProfileSection").hide();
    $("#skillProfileSection").show();
    $("#educationProfileSection").hide();

    fetchSkillAjaxApis();
    prefillSkillProfile();
}

function processDataCandidateExperienceUpdate(returnedData) {
    var heading = document.getElementById('basicHeading');
    heading.style.color = '#9f9f9f';
    heading = document.getElementById('skillsHeading');
    heading.style.color = '#9f9f9f';
    heading = document.getElementById('educationHeading');
    heading.style.color = '#2980b9';

    document.getElementById('basicImg').src = "/assets/dashboard/img/basic_disable.png";
    document.getElementById('skillImg').src = "/assets/dashboard/img/skills_disable.png";
    document.getElementById('educationImg').src = "/assets/dashboard/img/education_enable.png";

    $("#basicProfileSection").hide();
    $("#skillProfileSection").hide();
    $("#educationProfileSection").show();

    fetchEducationAjaxApis();
    prefillEducationProfile();
}

function processDataCandidateEducationUpdate(returnedData) {
    window.location = "/dashboard";
}

function prefillBasicProfile() {
    /* candidate First and Last name */
    $("#candidateFirstName").val(candidateInformation.candidateFirstName);
    if (candidateInformation.candidateLastName == "null" || candidateInformation.candidateLastName == null) {
        $("#candidateSecondName").val("");
    } else {
        $("#candidateSecondName").val(candidateInformation.candidateLastName);
    }

    /* candidate mobile */
    $("#candidateMobile").val(candidateInformation.candidateMobile.substring(3, 13));

    /* Time Shift */
    if (candidateInformation.timeShiftPreference != null) {
        $("#candidateTimeShiftPref").val(candidateInformation.timeShiftPreference.timeShift.timeShiftId);
    }

    /* Candidate DOB */
    try {
        if (candidateInformation.candidateDOB != null) {
            var date = JSON.parse(candidateInformation.candidateDOB);
            var yr = new Date(date).getFullYear();
            var month = ('0' + parseInt(new Date(date).getMonth() + 1)).slice(-2);
            var d = ('0' + new Date(date).getDate()).slice(-2);
            try {
                $("#candidateDob").val(yr + "-" + month + "-" + d);
                $("#dob_day").val(d);
                $("#dob_month").val(month);
                $("#dob_year").val(yr);

            } catch (err) {
            }
        }
    } catch (err) {
        console.log(err);
    }

    try {
        if (candidateInformation.candidateGender != null) {
            if (candidateInformation.candidateGender == 0) {
                document.getElementById("genderMale").checked = true;
                $('#genderMale').parent().addClass('active').siblings().removeClass('active');
                $("#userGenderIcon").attr('src', '/assets/dashboard/img/male.png');
            } else {
                document.getElementById("genderFemale").checked = true;
                $('#genderFemale').parent().addClass('active').siblings().removeClass('active');
                $("#userGenderIcon").attr('src', '/assets/dashboard/img/female.png');
            }
        }
    } catch(err){
        console.log(err);
    }
}
/* end of basic profile prefill */

function prefillSkillProfile(){
    /* total experience */
    if(candidateInformation.candidateTotalExperience != null){
        if(candidateInformation.candidateTotalExperience == 0 && candidateInformation.candidateIsEmployed != 1){
            document.getElementById("fresher").checked = true;
            $('#fresher').parent().addClass('active').siblings().removeClass('active');
        } else{
            var totalExperience = parseInt(candidateInformation.candidateTotalExperience);
            try{
                $("#candidateTotalExperienceYear").val(parseInt((totalExperience / 12)).toString()); // years
                $("#candidateTotalExperienceMonth").val(totalExperience % 12); // years
            } catch (err){
                console.log("try catch");
            }
            try{
                $("#totalWorkExperience").show();
                $("#isEmployedSelect").show();
                document.getElementById("experienced").checked = true;
                $('#experienced').parent().addClass('active').siblings().removeClass('active');
            } catch (err){
                console.log("try catch");
            }
        }
    }

    /* is Employed */
    try {
        if(candidateInformation.candidateIsEmployed != null){
            if (candidateInformation.candidateIsEmployed == 1) {
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
    /* current company details */
    try {
        if(candidateInformation.candidateCurrentJobDetail != null){
            if(candidateInformation.candidateCurrentJobDetail.candidateCurrentCompany != null && candidateInformation.candidateCurrentJobDetail.candidateCurrentCompany != ""){
                try{
                    $("#candidateCurrentCompany").val(candidateInformation.candidateCurrentJobDetail.candidateCurrentCompany);
                } catch(err){
                    console.log(err);
                }
            }
            if(candidateInformation.candidateCurrentJobDetail.candidateCurrentSalary != null){
                try{
                    $("#candidateCurrentJobSalary").val(candidateInformation.candidateCurrentJobDetail.candidateCurrentSalary);
                } catch(err){
                    console.log(err);
                }
            }
        }
    } catch(err){
        console.log(err);
    }

    /* language and skills */
    try{
        if(candidateInformation.motherTongue != null){
            $("#candidateMotherTongue").val(candidateInformation.motherTongue.languageId);
        }
        else{
            $("#candidateMotherTongue").val(-1);
        }
    }
    catch(err){
        console.log(err);
    }

    if(candidateInformation.languageKnownList != null) {
        prefillLanguageTable(candidateInformation.languageKnownList);
    }

    if(candidateInformation.candidateSkillList != null) {
        var skillList = candidateInformation.candidateSkillList;
        skillList.forEach(function (skillElement) {
            var obj = {};
            obj["skillName"] = skillElement.skill.skillName;
            obj["skillResponse"] = skillElement.skillQualifier.qualifier;
            candidateSkill.push(obj);
        });
    }
    $(".btn-group").attr("data-toggle", "buttons");
    $(".btn-group").removeClass('active');
    prefillSkills(candidateSkill);
}
/* end of skill prefill */

$("#editBasic").click(function(){
    document.getElementById("saveBtn").disabled = false;
    var heading = document.getElementById('basicHeading');
    heading.style.color = '#2980b9';
    heading = document.getElementById('skillsHeading');
    heading.style.color = '#9f9f9f';
    heading = document.getElementById('educationHeading');
    heading.style.color = '#9f9f9f';

    document.getElementById('basicImg').src = "/assets/dashboard/img/basic_enable.png";
    document.getElementById('skillImg').src = "/assets/dashboard/img/skills_disable.png";
    document.getElementById('educationImg').src = "/assets/dashboard/img/education_disable.png";

    $("#skillProfileSection").hide();
    $("#educationProfileSection").hide();
    $("#basicProfileSection").show();
});

$("#editSkills").click(function(){
    document.getElementById("saveBtn").disabled = false;

    var heading = document.getElementById('basicHeading');
    heading.style.color = '#9f9f9f';
    heading = document.getElementById('skillsHeading');
    heading.style.color = '#2980b9';
    heading = document.getElementById('educationHeading');
    heading.style.color = '#9f9f9f';

    document.getElementById('basicImg').src = "/assets/dashboard/img/basic_disable.png";
    document.getElementById('skillImg').src = "/assets/dashboard/img/skills_enable.png";
    document.getElementById('educationImg').src = "/assets/dashboard/img/education_disable.png";

    $("#basicProfileSection").hide();
    $("#educationProfileSection").hide();
    $("#skillProfileSection").show();
    fetchSkillAjaxApis();
    prefillSkillProfile();
});

$("#editEducation").click(function(){
    document.getElementById("saveBtn").disabled = false;

    var heading = document.getElementById('basicHeading');
    heading.style.color = '#9f9f9f';
    heading = document.getElementById('skillsHeading');
    heading.style.color = '#9f9f9f';
    heading = document.getElementById('educationHeading');
    heading.style.color = '#2980b9';

    document.getElementById('basicImg').src = "/assets/dashboard/img/basic_disable.png";
    document.getElementById('skillImg').src = "/assets/dashboard/img/skills_disable.png";
    document.getElementById('educationImg').src = "/assets/dashboard/img/education_enable.png";

    $("#skillProfileSection").hide();
    $("#basicProfileSection").hide();
    $("#educationProfileSection").show();

    fetchEducationAjaxApis();
    prefillEducationProfile();
});


$("#educationBack").click(function(){
    document.getElementById("saveBtn").disabled = false;

    var heading = document.getElementById('basicHeading');
    heading.style.color = '#9f9f9f';
    heading = document.getElementById('skillsHeading');
    heading.style.color = '#2980b9';
    heading = document.getElementById('educationHeading');
    heading.style.color = '#9f9f9f';

    document.getElementById('basicImg').src = "/assets/dashboard/img/basic_disable.png";
    document.getElementById('skillImg').src = "/assets/dashboard/img/skills_enable.png";
    document.getElementById('educationImg').src = "/assets/dashboard/img/education_disable.png";

    $("#educationProfileSection").hide();
    $("#skillProfileSection").show();
});

$("#skillBack").click(function(){
    document.getElementById("saveBtn").disabled = false;

    var heading = document.getElementById('basicHeading');
    heading.style.color = '#2980b9';
    heading = document.getElementById('skillsHeading');
    heading.style.color = '#9f9f9f';
    heading = document.getElementById('educationHeading');
    heading.style.color = '#9f9f9f';

    document.getElementById('basicImg').src = "/assets/dashboard/img/basic_enable.png";
    document.getElementById('skillImg').src = "/assets/dashboard/img/skills_disable.png";
    document.getElementById('educationImg').src = "/assets/dashboard/img/education_disable.png";

    $("#skillProfileSection").hide();
    $("#basicProfileSection").show();
});

function prefillEducationProfile(){
    try {
            if(candidateInformation.candidateEducation.education != null){
                document.getElementById("highestEducation" + candidateInformation.candidateEducation.education.educationId).checked = true;
                $("#highestEducation" + candidateInformation.candidateEducation.education.educationId).parent().addClass('active').siblings().removeClass('active');
                if(candidateInformation.candidateEducation.education.educationId == 4 || candidateInformation.candidateEducation.education.educationId == 5){
                    $("#educationalInstitute").show();
                }
            }
            if(candidateInformation.candidateEducation.degree != null){
                $("#candidateHighestDegree").val(candidateInformation.candidateEducation.degree.degreeId);
            }
            if(candidateInformation.candidateEducation.candidateLastInstitute != null){
                $("#candidateEducationInstitute").val(candidateInformation.candidateEducation.candidateLastInstitute);
            }
    } catch(err){
        console.log(err);
    }
}
/* end of education pre fill */

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


$("#candidateUpdateBasicProfile").submit(function(eventObj) {
    eventObj.preventDefault();
    saveCandidateBasicProfile();
}); // end of submit

$("#candidateUpdateLanguageAndSkills").submit(function(eventObj) {
    eventObj.preventDefault();
    saveCandidateExperienceDetails();
}); // end of submit

$("#candidateUpdateEducationDetails").submit(function(eventObj) {
    eventObj.preventDefault();
    saveCandidateEducationDetails();
}); // end of submit

function saveCandidateBasicProfile(){
    var statusCheck = 1;
    var firstName = $('#candidateFirstName').val();
    var lastName = $('#candidateSecondName').val();
    var phone = $('#candidateMobile').val();
    var firstNameCheck = validateName(firstName);
    if(lastName != ""){
        var lastNameCheck = validateName(lastName);        
    }
    var res = validateMobile(phone);
    var selectedGender = $('input:radio[name="gender"]:checked').val();

    var localitySelected = $('#candidateLocalityPref').val();
    var jobSelected = $('#candidateJobPref').val();
    var selectedDob = $('#dob_year').val() + "-" + $('#dob_month').val() + "-" + $('#dob_day').val();
    var c_dob = String(selectedDob);
    var selectedDate = new Date(c_dob);
    var todayDate = new Date();
    var dobCheck=1;
    console.log(selectedDate + " " + todayDate);
    if(selectedDate>todayDate){
        dobCheck=0;
    }
    
    //checking first name
    switch(firstNameCheck){
        case 0: alert("First name contains number. Please Enter a valid First Name"); statusCheck=0; break;
        case 2: alert("First Name cannot be blank spaces. Enter a valid first name"); statusCheck=0; break;
        case 3: alert("First name contains special symbols. Enter a valid first name"); statusCheck=0; break;
        case 4: alert("Please enter your first name"); statusCheck=0; break;
    }

    if(res == 0){
        alert("Enter a valid mobile number");
        statusCheck=0;
    } else if(res == 1){
        alert("Enter 10 digit mobile number");
        statusCheck=0;
    } else if(localitySelected == "") {
        alert("Please Enter your Job Localities");
        statusCheck=0;
    } else if(jobSelected == "") {
        alert("Please Enter the Jobs you are Interested");
        statusCheck=0;
    } else if($('#candidateTimeShiftPref').val() == -1){
        statusCheck=0;
        alert("Please Enter Your Preferred Work Shift");
    } else if($('#dob_day').val() == ""){
        statusCheck=0;
        alert("Please Select your Birth day");
    } else if($('#dob_month').val() == ""){
        statusCheck=0;
        alert("Please Select your Birth month");
    } else if($('#dob_year').val() == ""){
        statusCheck=0;
        alert("Please Select your Birth year");
    } else if(dobCheck == 0){
        alert("Please select a valid date of birth");
        statusCheck=0;
    } else if(selectedGender == undefined) {
        statusCheck=0;
        alert("Please Select your Gender");
    }

    //checking last name
    switch(lastNameCheck){
        case 0: alert("Last name contains number. Please Enter a valid Last Name"); statusCheck=0; break;
        case 2: alert("Last Name cannot be blank spaces. Enter a valid Last name"); statusCheck=0; break;
        case 3: alert("Last name contains special symbols. Enter a valid Last name"); statusCheck=0; break;
        case 4: alert("Please enter your Last name"); statusCheck=0; break;
    }

    if(statusCheck == 1){
        document.getElementById("saveBtn").disabled = true;
        var candidatePreferredJob = [];
        var candidatePreferredLocality = [];

        var jobPref = $('#candidateJobPref').val().split(",");
        var localityPref = $('#candidateLocalityPref').val().split(",");

        var i;
        for(i=0;i<jobPref.length; i++){
            candidatePreferredJob.push(parseInt(jobPref[i]));
        }

        for(i=0;i<localityPref.length; i++){
            candidatePreferredLocality.push(parseInt(localityPref[i]));
        }
        try {
            var d = {
                //mandatory fields
                candidateFirstName: $('#candidateFirstName').val(),
                candidateSecondName: $('#candidateSecondName').val(),
                candidateMobile: $('#candidateMobile').val(),
                candidateLocality: candidatePreferredLocality,
                candidateJobPref: candidatePreferredJob,

                //others
                candidateDob: c_dob,
                candidateTimeShiftPref: $('#candidateTimeShiftPref').val(),
                candidateGender: ($('input:radio[name="gender"]:checked').val())
            };

            localStorage.setItem("name", d.candidateFirstName);
            localStorage.setItem("lastName", d.candidateSecondName);

            $.ajax({
                type: "POST",
                url: "/candidateUpdateBasicProfile",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataCandidateSaveBasicProfile
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function saveCandidateExperienceDetails(){
    var experienceStatus = $('input:radio[name="workExperience"]:checked').val();
    if(experienceStatus == null){
        alert("Please Select your work experience");
    } else if($('#candidateCurrentJobSalary').val() > 99999){
        alert("Please Enter a valid Salary")
    }
    else{
        /* calculate total experience in months */
        var expMonth = parseInt($('#candidateTotalExperienceMonth').val());
        var expYear = parseInt($('#candidateTotalExperienceYear').val());
        var totalExp = expMonth + (12*expYear);

        if(experienceStatus == 1 && $('input:radio[name="isEmployed"]:checked').val() == null){
            alert("Select Current Employment Status");
        } else if((experienceStatus == 1) && ($('input:radio[name="isEmployed"]:checked').val() == 1) && ($('#candidateCurrentJobSalary').val() == null || $('#candidateCurrentJobSalary').val() == "" || $('#candidateCurrentJobSalary').val() == "0")){
            alert("Enter your current salary");
        }
        else if(experienceStatus == 1 && totalExp == 0){
            alert("Select Total Years of Experience");
        }
        else{
            document.getElementById("saveBtn").disabled = true;
            try {
                var languageKnown = $('#languageTable input:checked').map(function() {
                    var check=0;
                    var id = this.id;
                    var name = this.name;
                    var item = {};
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

                var candidateCurrentCompanyVal = "";
                var candidateCurrentSalaryVal = "";

                if($('input:radio[name="isEmployed"]:checked').val() == 0){
                    candidateCurrentCompanyVal = null;
                    candidateCurrentSalaryVal = 0;
                }
                else{
                    candidateCurrentCompanyVal = $('#candidateCurrentCompany').val();
                    candidateCurrentSalaryVal = $('#candidateCurrentJobSalary').val();
                }

                var d = {
                    candidateMobile: localStorage.getItem("mobile"),
                    candidateFirstName: localStorage.getItem("name"),
                    candidateSecondName: localStorage.getItem("lastName"),

                    candidateTotalExperience: totalExp,
                    candidateIsEmployed: $('input:radio[name="isEmployed"]:checked').val(),
                    candidateCurrentCompany: candidateCurrentCompanyVal,
                    candidateCurrentSalary: candidateCurrentSalaryVal,

                    candidateMotherTongue: ($('#candidateMotherTongue').val()),
                    candidateLanguageKnown: languageMap,

                    candidateSkills: skillMap
                };

                $.ajax({
                    type: "POST",
                    url: "/candidateUpdateExperienceDetails",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataCandidateExperienceUpdate
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }
}

function saveCandidateEducationDetails(){
    var highestEducation = $('input:radio[name="highestEducation"]:checked').val();
    if(highestEducation == undefined){
        alert("Select your Highest Education");
    }
    else{
        if(((highestEducation == 4) || (highestEducation == 5)) && $('#candidateHighestDegree').val() == -1){
            alert("Please select your Degree");
        }
        else{
            var selectedDegree = $('#candidateHighestDegree').val();
            document.getElementById("saveBtn").disabled = true;
            try {
                var d = {
                    candidateMobile: localStorage.getItem("mobile"),
                    candidateFirstName: localStorage.getItem("name"),
                    candidateSecondName: localStorage.getItem("lastName"),

                    candidateEducationLevel: $('input:radio[name="highestEducation"]:checked').val(),
                    candidateDegree: selectedDegree,
                    candidateEducationInstitute: $('#candidateEducationInstitute').val(),
                };

                $.ajax({
                    type: "POST",
                    url: "/candidateUpdateEducationDetails",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataCandidateEducationUpdate
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }
}