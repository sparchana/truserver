/**
 * Created by batcoder1 on 4/6/16.
 */

var candidateSkill = [];
var currentLocationArray = [];

var candidateInfo;
var candidateFirstName;
var candidateLastName;
var candidateMobile;

/* start of javascript */
$(document).ready(function(){
    $("#educationalInstitute").hide();
    prefillBasicProfile();
    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateInfoDashboard",
            data: false,
            async: true,
            contentType: false,
            processData: false,
            success: processDataAndFillMinProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataAndFillMinProfile(returnedData) {
    candidateInfo = returnedData;
    if(returnedData.candidateLastName == "" || returnedData.candidateLastName == null){
        document.getElementById("userName").innerHTML = returnedData.candidateFirstName;
    } else{
        document.getElementById("userName").innerHTML = returnedData.candidateFirstName + " " + returnedData.candidateLastName;
    }
    document.getElementById("userMobile").innerHTML = returnedData.candidateMobile;

    minProfileComplete = returnedData.isMinProfileComplete;
    if(returnedData.isMinProfileComplete == 0){ // profile not complete
        $(".profileComplete").hide();
        $(".profileIncomplete").show();
        localStorage.setItem("minProfile", 0);
    } else{
        $(".profileComplete").show();
        $(".profileIncomplete").hide();
        localStorage.setItem("minProfile", 1);
    }
    if(returnedData.candidateIsAssessed == 1){
        $(".assessmentIncomplete").hide();
        $(".assessmentComplete").show();
    } else {
        var options = {'showRowNumber': true};
        var data;
        var query = new google.visualization.Query('https://docs.google.com/spreadsheets/d/1HwEWPzZD4BFCyeRf5HO_KqNXyaMporxYQfg5lhOoA2g/edit#gid=496359801');

        function sendAndDraw() {
            var val = localStorage.getItem("mobile");
            query.setQuery('select C where C=' + val.substring(3, 13));
            query.send(handleQueryResponse);
        }

        function handleQueryResponse(response) {
            if (response.isError()) {
                return;
            }
            data = response.getDataTable();
            new google.visualization.Table(document.getElementById('table')).draw(data, options);
            var data2 = document.getElementsByClassName('google-visualization-table-td google-visualization-table-td-number').length;
            if(data2 == 0){
                $(".assessmentIncomplete").show();
                $(".assessmentComplete").hide();
            }
            else{
                $.ajax({
                    type: "GET",
                    url: "/updateIsAssessedToAssessed",
                    processData: false,
                    success: processAssessedStatus
                });
                $(".assessmentIncomplete").hide();
                $(".assessmentComplete").show();
                $.ajax({
                    type: "GET",
                    url: "/updateIsAssessedToAssessed",
                    processData: false,
                    success: processAssessedStatus
                });
            }
        }
        google.setOnLoadCallback(sendAndDraw);
    }

    if (returnedData.candidateGender != null) {
        localStorage.setItem("gender", returnedData.candidateGender);
        if (returnedData.candidateGender == 0) {
            try{
                document.getElementById("userGender").innerHTML = ", Male";
                $("#userImg").attr('src', '/assets/dashboard/img/userMale.svg');
            } catch(err){}
        } else {
            try{
                document.getElementById("userGender").innerHTML = ", Female";
                $("#userImg").attr('src', '/assets/dashboard/img/userFemale.svg');
            } catch(err){}
        }
    } else{
        try{
            $("#userImg").attr('src', '/assets/dashboard/img/userMale.svg');
        } catch(err){}
    }
    if (returnedData.candidateDOB != null) {
        var date = JSON.parse(returnedData.candidateDOB);
        var yr = new Date(date).getFullYear();
        var month = ('0' + parseInt(new Date(date).getMonth() + 1)).slice(-2);
        var d = ('0' + new Date(date).getDate()).slice(-2);
        var today = new Date();
        var birthDate = new Date(yr + "-" + month + "-" + d);
        var age = today.getFullYear() - birthDate.getFullYear();
        var m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        document.getElementById("userAge").innerHTML = ", " + age + " years";
    }
    try {
        var jobRoles = "";
        var count = 0;
        var jobPref = returnedData.jobPreferencesList;
        if(jobPref.length > 0){
            jobPref.forEach(function (job){
                count ++;
                var name = job.jobRole.jobName;
                jobRoles += name;
                if(count < Object.keys(jobPref).length){
                    jobRoles += ", ";
                }
            });
            document.getElementById("userJobs").innerHTML = jobRoles;
        }
    } catch(err){
        console.log(err);
    }

    try {
        if(returnedData.locality != null){
            document.getElementById("userLocality").innerHTML = returnedData.locality.localityName;
        }
    } catch(err){
        console.log("getCandidateLocalityPref error"+err);
    }

    //Time Shift
    if (returnedData.timeShiftPreference != null) {
        document.getElementById("userShift").innerHTML = returnedData.timeShiftPreference.timeShift.timeShiftName;
        if(returnedData.timeShiftPreference.timeShift.timeShiftId == 5){
            document.getElementById("userShift").innerHTML = returnedData.timeShiftPreference.timeShift.timeShiftName + " Shift";
        }
    }

    ///!* candidate Education *!/
    try{
        if(returnedData.candidateEducation.education != null) {
            document.getElementById("userEducationLevel").innerHTML = returnedData.candidateEducation.education.educationName;
        }
    } catch(err){}

    ///!* Work Experience *!/
    if(returnedData.candidateTotalExperience != null){
        if(returnedData.candidateTotalExperience == 0) {
            document.getElementById("userTotalExperience").innerHTML = "Fresher";
            document.getElementById("userCurrentSalary").innerHTML = "Not Applicable";
            document.getElementById("userCurrentCompany").innerHTML = "Not Applicable";
        }
        else {
            var totalExperience = parseInt(returnedData.candidateTotalExperience);
            var yrs = parseInt((totalExperience / 12)).toString();
            var month = totalExperience % 12;
            if(yrs == 0 && month != 0){
                document.getElementById("userTotalExperience").innerHTML = month + " months";
            } else if(month == 0 && yrs != 0){
                document.getElementById("userTotalExperience").innerHTML = yrs + " years";

            } else{
                document.getElementById("userTotalExperience").innerHTML = yrs + " yrs and " + month + " mnths";
            }

            ///!* Current Company and Salary *!/
            if (Object.keys(returnedData.jobHistoryList).length > 0) {
                returnedData.jobHistoryList.forEach(function (pastJob) {
                    if(pastJob.currentJob == true){
                        if(pastJob.candidatePastCompany != null){
                            document.getElementById("userCurrentCompany").innerHTML = pastJob.candidatePastCompany;
                        } else{
                            document.getElementById("userCurrentCompany").innerHTML = "Not Specified";
                        }
                        return false;
                    }
                });
            }

            if(returnedData.candidateLastWithdrawnSalary != null){
                if(returnedData.candidateLastWithdrawnSalary == "0"){
                    document.getElementById("userCurrentSalary").innerHTML = "Not Applicable";
                } else{
                    document.getElementById("userCurrentSalary").innerHTML = "&#x20B9;" + returnedData.candidateLastWithdrawnSalary + "/month";
                }
            }
        }
    }

    var appliedJobs = returnedData.jobApplicationList;
    $("#jobCount").html(Object.keys(appliedJobs).length);
    appliedJobs.forEach(function (jobApplication) {
        $("#apply_btn_" + jobApplication.jobPost.jobPostId).addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied");
        $("#applyBtnDiv_" + jobApplication.jobPost.jobPostId).prop('disabled',true);
    });

}

function processDataCandidateSaveBasicProfile(returnedData) {
    localStorage.setItem("minProfile", returnedData.minProfile);
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
    localStorage.setItem("minProfile", returnedData.minProfile);
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
    localStorage.setItem("minProfile", returnedData.minProfile);
    window.location = "/dashboard";
}

function prefillBasicProfile() {
    $("#jobCount").html(Object.keys(candidateInformation.jobApplicationList).length);
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

    /* get Candidate's home location */
    if (candidateInformation.locality != null) {
        try {
            var item = {};
            item ["id"] = candidateInformation.locality.localityId;
            item ["name"] = candidateInformation.locality.localityName;
            currentLocationArray.push(item);
        } catch (err) {
        }
    }

    if($("#candidateHomeLocality").val() == ""){
        $("#candidateHomeLocality").tokenInput(getLocality(), {
            theme: "facebook",
            placeholder: "Where do you Live?",
            hintText: "Start typing jobs (eg. BTM Layout, Bellandur..)",
            minChars: 0,
            prePopulate: currentLocationArray,
            tokenLimit: 1,
            preventDuplicates: true
        });
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
                if(new Date(date).getFullYear() > 1936
                    && new Date(date).getFullYear() < 1998){
                    $("#dob_year").val(yr);
                }
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
                $("#userImg").attr('src', '/assets/dashboard/img/userMale.svg');
            } else {
                document.getElementById("genderFemale").checked = true;
                $('#genderFemale').parent().addClass('active').siblings().removeClass('active');
                $("#userImg").attr('src', '/assets/dashboard/img/userFemale.svg');
            }
        } else {
            $("#userImg").attr('src', '/assets/dashboard/img/userMale.svg');
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
            $("#lastWithdrawnSalaryLayout").hide();
            $("#isEmployedNo").click();
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
                $("#lastWithdrawnSalaryLayout").show();
                $("#totalWorkExperience").show();
                $("#isEmployedSelect").show();
                document.getElementById("experienced").checked = true;
                $('#experienced').parent().addClass('active').siblings().removeClass('active');
            } catch (err){
                console.log("try catch");
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
                if(candidateInformation.candidateLastWithdrawnSalary != null){
                    try{
                        $("#candidateLastWithdrawnSalary").val(candidateInformation.candidateLastWithdrawnSalary);
                    } catch(err){
                        console.log(err);
                    }
                }
                var currentJobRole = [];

                if(candidateInformation.jobHistoryList != null){
                    var candidatePastJobList = candidateInformation.jobHistoryList;
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
            } catch(err){
                console.log(err);
            }

        }
    }

    if($("#candidateCurrentJobRole").val() == ""){
        $("#candidateCurrentJobRole").tokenInput(getJob(), {
            theme: "facebook",
            hintText: "Start typing jobs (eg. Cook, Delivery boy..)",
            minChars: 0,
            tokenLimit: 1,
            prePopulate: currentJobRole,
            preventDuplicates: true
        });
    }

    /* language and skills */

    if(candidateInformation.languageKnownList != null) {
        prefillLanguageTable(candidateInformation.languageKnownList);
    }

    if(candidateInformation.candidateSkillList != null) {
        var skillList = candidateInformation.candidateSkillList;
        skillList.forEach(function (skillElement) {
            var obj = {};
            obj["skillName"] = skillElement.skill.skillName;
            obj["skillResponse"] = skillElement.candidateSkillResponse;
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
                if(candidateInformation.candidateEducation.candidateEducationCompletionStatus != null){
                    if(candidateInformation.candidateEducation.candidateEducationCompletionStatus == "1"){
                        $('input[id=eduCompleted]').attr('checked', true);
                    } else {
                        $('input[id=eduCompletedNot]').attr('checked', true);
                    }
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

$("#saveBasicProfileBtn").click(function(){
    saveCandidateBasicProfile();
});

$("#saveExperienceProfileBtn").click(function(){
    saveCandidateExperienceDetails();
});

$("#saveEducationProfileBtn").click(function(){
    saveCandidateEducationDetails();
});

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

    var homeLocalitySelected = $('#candidateHomeLocality').val();
    var jobSelected = $('#candidateJobPref').val();
    var selectedDob = $('#dob_year').val() + "-" + $('#dob_month').val() + "-" + $('#dob_day').val();
    var c_dob = String(selectedDob);
    var selectedDate = new Date(c_dob);
    var todayDate = new Date();
    var dobCheck=1;
    if(selectedDate>todayDate){
        dobCheck=0;
    }
    
    //checking first name
    switch(firstNameCheck){
        case 0: notifyError("First name contains number. Please Enter a valid First Name"); statusCheck=0; break;
        case 2: notifyError("First Name cannot be blank spaces. Enter a valid first name"); statusCheck=0; break;
        case 3: notifyError("First name contains special symbols. Enter a valid first name"); statusCheck=0; break;
        case 4: notifyError("Please enter your first name"); statusCheck=0; break;
    }

    if(res == 0){
        notifyError("Enter a valid mobile number");
        statusCheck=0;
    } else if(res == 1){
        notifyError("Enter 10 digit mobile number");
        statusCheck=0;
    } else if(homeLocalitySelected == "") {
        notifyError("Please Enter your Home location");
        statusCheck=0;
    } else if(jobSelected == "") {
        notifyError("Please Enter the Jobs you are Interested");
        statusCheck=0;
    } else if($('#candidateTimeShiftPref').val() == -1){
        statusCheck=0;
        notifyError("Please Enter Your Preferred Work Shift");
    }  else if($('#dob_day').val() == "" || $('#dob_month').val() == "" || $('#dob_year').val() == ""){
        statusCheck=0;
        notifyError("Please Select your Date of Birth");
    } else if(dobCheck == 0){
        notifyError("Please select a valid date of birth");
        statusCheck=0;
    } else if(selectedGender == undefined) {
        statusCheck=0;
        notifyError("Please Select your Gender");
    }

    //checking last name
    switch(lastNameCheck){
        case 0: notifyError("Last name contains number. Please Enter a valid Last Name"); statusCheck=0; break;
        case 2: notifyError("Last Name cannot be blank spaces. Enter a valid Last name"); statusCheck=0; break;
        case 3: notifyError("Last name contains special symbols. Enter a valid Last name"); statusCheck=0; break;
        case 4: notifyError("Please enter your Last name"); statusCheck=0; break;
    }

    if(statusCheck == 1){
        document.getElementById("saveBtn").disabled = true;
        var candidatePreferredJob = [];

        var jobPref = $('#candidateJobPref').val().split(",");

        var i;
        for(i=0;i<jobPref.length; i++){
            candidatePreferredJob.push(parseInt(jobPref[i]));
        }

        try {
            var d = {
                //mandatory fields
                candidateFirstName: $('#candidateFirstName').val(),
                candidateSecondName: $('#candidateSecondName').val(),
                candidateMobile: candidateInfo.candidateMobile,
                candidateJobPref: candidatePreferredJob,
                candidateHomeLocality: homeLocalitySelected,

                //others
                candidateDob: c_dob,
                candidateTimeShiftPref: $('#candidateTimeShiftPref').val(),
                candidateGender: ($('input:radio[name="gender"]:checked').val())
            };

            candidateFirstName = d.candidateFirstName;
            candidateLastName = d.candidateSecondName;

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
        notifyError("Please Select your work experience");
    } else if($('#candidateCurrentJobSalary').val() > 99999){
        notifyError("Please Enter a valid Salary")
    }
    else{
        /* calculate total experience in months */
        var expMonth = parseInt($('#candidateTotalExperienceMonth').val());
        var expYear = parseInt($('#candidateTotalExperienceYear').val());
        var totalExp = expMonth + (12*expYear);
        if(experienceStatus == 0 ){
            totalExp=0;
        }

        if(experienceStatus == 1 && totalExp == 0){
            notifyError("Select Total Years of Experience");
        } else if(experienceStatus == 1 && $('input:radio[name="isEmployed"]:checked').val() == null){
            notifyError("Please answer \"Are you currently working?\"");
        } else if((experienceStatus == 1)  && ($('#candidateLastWithdrawnSalary').val() == null || $('#candidateLastWithdrawnSalary').val() == "" || $('#candidateLastWithdrawnSalary').val() == "0")){
            notifyError("Enter your Last Withdrawn Salary");
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

                var candidateCurrentCompanyVal = "";
                var candidateLastWithdrawnSalary = "";

                candidateLastWithdrawnSalary = $('#candidateLastWithdrawnSalary').val();

                if($('input:radio[name="isEmployed"]:checked').val() == 0){
                    candidateCurrentCompanyVal = null;
                }
                else{
                    candidateCurrentCompanyVal = $('#candidateCurrentCompany').val();
                }

                var d = {
                    candidateMobile: candidateInfo.candidateMobile,
                    candidateFirstName: candidateFirstName,
                    candidateSecondName: candidateLastName,

                    candidateTotalExperience: totalExp,
                    candidateIsEmployed: $('input:radio[name="isEmployed"]:checked').val(),
                    candidateCurrentCompany: candidateCurrentCompanyVal,
                    candidateCurrentJobRoleId: parseInt($('#candidateCurrentJobRole').val()),
                    candidateLastWithdrawnSalary: candidateLastWithdrawnSalary,

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
        notifyError("Select your Highest Education");
    }
    else{
        if(((highestEducation == 4) || (highestEducation == 5)) && $('#candidateHighestDegree').val() == -1){
            notifyError("Please select your Degree");
        }
        else{
            var selectedDegree = $('#candidateHighestDegree').val();
            document.getElementById("saveBtn").disabled = true;
            try {
                var d = {
                    candidateMobile: candidateInfo.candidateMobile,
                    candidateFirstName: candidateFirstName,
                    candidateSecondName: candidateLastName,

                    candidateEducationLevel: $('input:radio[name="highestEducation"]:checked').val(),
                    candidateDegree: selectedDegree,
                    candidateEducationInstitute: $('#candidateEducationInstitute').val(),
                    candidateEducationCompletionStatus: parseInt($('input:radio[name="candidateEducationCompletionStatus"]:checked').val())
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