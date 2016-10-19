/**
 * Created by dodo on 18/10/16.
 */

var jpId = 0;
var jpCompanyId = 3;
var jpRecruiterId = 46;

function processDataCheckLocality(returnedData) {
    if (returnedData != null) {
        returnedData.forEach(function (locality) {
            var id = locality.localityId;
            var name = locality.localityName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            var option = $('<option value=' + id + '></option>').text(name);
            $('#jobPostLocality').append(option);
        });
    }
}

function processDataCheckJobs(returnedData) {
    returnedData.forEach(function (job) {
        var id = job.jobRoleId;
        var name = job.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostJobRole').append(option);
    });
}

function processDataCheckShift(returnedData) {
    if (returnedData != null) {
        returnedData.forEach(function (timeshift) {
            var id = timeshift.timeShiftId;
            var name = timeshift.timeShiftName;
            var option = $('<option value=' + id + '></option>').text(name);
            $('#jobPostShift').append(option);
        });
    }
}

function processDataCheckEducation(returnedData) {
    returnedData.forEach(function (education) {
        var id = education.educationId;
        var name = education.educationName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostEducation').append(option);
    });
}

function processDataCheckExperience(returnedData) {
    returnedData.forEach(function (experience) {
        var id = experience.experienceId;
        var name = experience.experienceType;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostExperience').append(option);
    });
}

function processDataGetAllLanguage(returnLanguage) {
    returnLanguage.forEach(function (langauge) {
        var id = langauge.languageId;
        var name = langauge.languageName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostLanguage').append(option);
    });
}

function processDataGetAllIdProof(returnedIdProofs) {
    returnedIdProofs.forEach(function (idProof) {
        var id = idProof.idProofId;
        var name = idProof.idProofName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostIdProof').append(option);
    });
}

function processDataGetAllAsset(returnedAssets) {
    returnedAssets.forEach(function (asset) {
        var id = asset.assetId;
        var name = asset.assetTitle;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostAsset').append(option);
    });
}

$(document).scroll(function(){
    if ($(this).scrollTop() > 80) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});
function toJobDetail(){
    $('ul.tabs').tabs('select_tab', 'jobDetails');
    $('body').scrollTop(0);
}
function toJobRequirement(){
    $('ul.tabs').tabs('select_tab', 'jobRequirement');
    $('body').scrollTop(0);
}

function checkRecruiterLogin() {
    try {
        $.ajax({
            type: "GET",
            url: "/checkRecruiterSession",
            data: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterSession
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataRecruiterSession(returnedData) {
    if(returnedData == "0"){
        logoutRecruiter();
    } else{
        jpRecruiterId = returnedData.recruiterProfileId;
        jpCompanyId = returnedData.company.companyId;
    }
}


$(document).ready(function () {
    checkRecruiterLogin();
    var pathname = window.location.pathname; // Returns path only
    var jobPostIdUrl = pathname.split('/');
    var jobPostId = jobPostIdUrl[(jobPostIdUrl.length)-1];
    if(jobPostId != 0){
        try {
            $.ajax({
                type: "POST",
                url: "/getRecruiterJobPostInfo/" + jobPostId,
                data: false,
                contentType: false,
                processData: false,
                success: processDataForJobPost
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
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
            url: "/getAllExperience",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckExperience
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
            success: processDataGetAllLanguage
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
            success: processDataGetAllIdProof
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    try {
        $.ajax({
            type: "POST",
            url: "/getAllAsset",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetAllAsset
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }


    var i;

    for(i=0;i<=24;i++){
        var option = document.createElement("option");
        option.value = i;
        option.textContent = i + ":00 hrs";
        $('#jobPostStartTime').append(option);
    }
    for(i=0;i<=24;i++) {
        option = document.createElement("option");
        option.value = i;
        option.textContent = i + ":00 hrs";
        $('#jobPostEndTime').append(option);
    }

});

function saveJob() {
    var status;

    var vacancies = $("#jobPostVacancies").val();

    var startTime = $("#jobPostStartTime").val();
    var endTime = $("#jobPostEndTime").val();

    var minSalary = $("#jobPostMinSalary").val();
    var maxSalary = $("#jobPostMaxSalary").val();

    var jobLocalitySelected = $("#jobPostLocality").val();
    var jobPostLanguage = $('#jobPostLanguage').val();
    var jobPostWorkShift = $("#jobPostShift").val();
    var jobPostEducation = $("#jobPostEducation").val();
    var jobPostExperience = $("#jobPostExperience").val();

    //experience conversion
    if (jobPostExperience != null) {
        jobPostExperience = parseInt(jobPostExperience);
    }

    //education conversion
    if (jobPostEducation != null) {
        jobPostEducation = parseInt(jobPostEducation);
    }

    //work shift conversion
    if (jobPostWorkShift != null) {
        jobPostWorkShift = parseInt(jobPostWorkShift);
    }

    //vacancies conversion
    if (vacancies != null && vacancies != "") {
        vacancies = parseInt(vacancies);
    }

    // salary conversion
    if (minSalary != null && minSalary != "") {
        minSalary = parseInt(minSalary);
    }
    if (maxSalary != null && maxSalary != "") {
        maxSalary = parseInt(maxSalary);
    }

    //job post timing conversion
    if (startTime != null) {
        startTime = parseInt(startTime[0]);
    }
    if (endTime != null) {
        endTime = parseInt(endTime[0]);
    }

    var preferredJobLocationList = [];
    var jobPostLanguageList = [];

    var k;
    if(jobLocalitySelected != null){
        for (k = 0; k < Object.keys(jobLocalitySelected).length; k++) {
            preferredJobLocationList.push(parseInt(jobLocalitySelected[k]));
        }
    }

    if(jobPostLanguage != null){
        for (k = 0; k < Object.keys(jobPostLanguage).length; k++) {
            jobPostLanguageList.push(parseInt(jobPostLanguage[k]));
        }
    }

    var jobPostDocument = $('#jobPostIdProof').val();
    var jobPostAsset = $('#jobPostAsset').val();

    status = 1;

    if($("#jobPostTitle").val() == ""){
        notifyError("Please enter Job Post Title");
        status = 0;
    } else if($("#jobPostJobRole").val() == null){
        notifyError("Please enter job role");
        status = 0;
    } else if(vacancies == ""){
        notifyError("Please enter no. of vacancies");
        status = 0;
    } else if(vacancies < 1){
        notifyError("Please enter no. of vacancies");
        status = 0;
    } else if(jobLocalitySelected == null){
        notifyError("Please enter job localities");
        status = 0;
    } else if(minSalary == ""){
        notifyError("Please enter Job Post Minimum salary");
        status = 0;
    } else if(isValidSalary(minSalary) == false){
        notifyError("Please enter valid min salary");
        status = 0;
    } else if(maxSalary != 0 && (isValidSalary(maxSalary) == false)){
        notifyError("Please enter valid max salary");
        status = 0;
    } else if(maxSalary != 0 && (maxSalary <= minSalary)){
        notifyError("Max salary should be greater than min salary");
        status = 0;
    } else if($("input[name='jobPostGender']:checked").val() == null || $("input[name='jobPostGender']:checked").val() == undefined){
        notifyError("Please specify gender");
        status = 0;
    } else if(startTime != null){
        if(endTime != null){
            if(startTime >= endTime){
                notifyError("Start time cannot be more than end time");
                status = 0;
            }
        } else{
            notifyError("Please select job end time");
            status = 0;
        }
    } else if(jobPostExperience == null){
        notifyError("Please enter Job Post Experience required");
        status = 0;
    }

    // checking age, location, gender
    var maxAge = $("#jobPostMaxAge").val();
    var jobPostGender = parseInt($("input[name='jobPostGender']:checked").val());
    if (status != 0 ){
        if (!isValidAge(maxAge)) {
            $("#jobPostMaxAge").removeClass('invalid').addClass('invalid');
            notifyError("Please enter Job Post Max Age Requirement");
            status = 0;
        }
        if (jobPostGender == null || jobPostGender == -1) {
            notifyError("Please enter Job Post Gender Requirement");
            status = 0;
        }
        if(jobPostAsset == null){
            notifyError("Please enter Job Post assets required");
            status = 0;
        } else if(jobPostDocument == null){
            notifyError("Please enter Job Post documents required");
            status = 0;
        }
    }

    if(status == 1){
        var i;
        var workingDays = "";

        for(i=1;i<=7;i++){
            if($("#working_" + i).is(":checked")){
                workingDays += "1";
            } else{
                workingDays += "0";
            }
        }

        try {
            var d = {
                jobPostId: jpId,
                jobPostMinSalary: minSalary,
                jobPostMaxSalary: maxSalary,
                jobPostStartTime: startTime,
                jobPostEndTime: endTime,
                jobPostWorkingDays: workingDays,
                jobPostIsHot: 1,
                jobPostDescription: $("#jobPostDescription").val(),
                jobPostTitle: $("#jobPostTitle").val(),
                jobPostMinRequirement: $("#jobPostMinRequirement").val(),
                jobPostVacancies: vacancies,
                jobPostLocalities: jobLocalitySelected,
                jobPostJobRoleId: parseInt(($("#jobPostJobRole").val())[0]),
                jobPostCompanyId: jpCompanyId,
                jobPostShiftId: jobPostWorkShift,
                jobPostPricingPlanId: 1,
                jobPostEducationId: jobPostEducation,
                jobPostStatusId: 1,
                jobPostExperienceId: jobPostExperience,
                jobPostRecruiterId: jpRecruiterId,
                partnerInterviewIncentive: 400,
                partnerJoiningIncentive: 2000,
                jobPostLanguage: jobPostLanguageList,
                jobPostMaxAge: maxAge,
                jobPostGender: jobPostGender,
                jobPostDocument: jobPostDocument,
                jobPostAsset: jobPostAsset
            };
            $.ajax({
                type: "POST",
                url: "/recruiter/api/addJobPost",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataAddJobPost
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function processDataAddJobPost(returnedData) {
    if(returnedData.status == 1){
        notifySuccess("Job post successfully created!");
    } else if(returnedData.status == 2){
        notifySuccess("Job post successfully updated!");
    } else{
        notifyError("Something went wrong. Please try again later!");
    }
}


function processDataForJobPost(returnedData) {
    console.log(returnedData);
    if(returnedData != "0"){
        jpId = returnedData.jobPostId;
        if(returnedData.company != null ){
            jpCompanyId = returnedData.company.companyId;
        }

        $("#jobPostTitle").val(returnedData.jobPostTitle);

        $("#jobPostDescription").val(returnedData.jobPostDescription);

        // gender, language, age
        if (returnedData.jobPostMaxAge != null) {
            $("#jobPostMaxAge").val(returnedData.jobPostMaxAge);
        }
        if (returnedData.gender !=null) {
            if(returnedData.gender == 0){
                $("#male").attr('checked', true);
            } else if(returnedData.gender == 1) {
                $("#female").attr('checked', true);
            } else{
                $("#any").attr('checked', true);
            }
        }
        if (returnedData.jobPostLanguageRequirement != null) {
            var req = returnedData.jobPostLanguageRequirement;
            req.forEach(function (languageRequirement) {
                if(languageRequirement != null){
                    $('#jobPostLanguage').tokenize().tokenAdd(languageRequirement.language.languageId, languageRequirement.language.languageName);
                }
            });
        }

        $("#jobPostMinSalary").val(returnedData.jobPostMinSalary);
        $("#jobPostMaxSalary").val(returnedData.jobPostMaxSalary);

        if(returnedData.jobRole != null){
            var id = returnedData.jobRole.jobRoleId;
            var name = returnedData.jobRole.jobName;
            $('#jobPostJobRole').tokenize().tokenAdd(id, name);
        }

        console.log(returnedData.jobPostToLocalityList);
        if(returnedData.jobPostToLocalityList != null){
            returnedData.jobPostToLocalityList.forEach(function (locality) {
                var id = locality.locality.localityId;
                var name = locality.locality.localityName;
                $('#jobPostLocality').tokenize().tokenAdd(id, name);
            });
        }

        if(returnedData.jobPostStartTime != null ){
            $('#jobPostStartTime').tokenize().tokenAdd(returnedData.jobPostStartTime, returnedData.jobPostStartTime + ":00 hrs");
        }

        if(returnedData.jobPostEndTime != null ){
            $('#jobPostEndTime').tokenize().tokenAdd(returnedData.jobPostEndTime, returnedData.jobPostEndTime + ":00 hrs");
        }

        if(returnedData.jobPostWorkingDays != null){
            var workingDays = returnedData.jobPostWorkingDays.toString(2);
            var i;
            /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
            if(workingDays.length != 7){
                var x = 7 - workingDays.length;
                var modifiedWorkingDays = "";

                for(i=0;i<x;i++){
                    modifiedWorkingDays += "0";
                }
                modifiedWorkingDays += workingDays;
                workingDays = modifiedWorkingDays;
            }

            for(i=1; i<=7; i++){
                if(workingDays[i-1] == 1){
                    $("#working_" + i).prop('checked', true);
                } else{
                    $("#working_" + i).prop('checked', false);
                }
            }
        }

        if(returnedData.jobPostVacancies != null ){
            $("#jobPostVacancies").val(returnedData.jobPostVacancies);
        }

        if(returnedData.jobPostExperience != null ){
            $('#jobPostExperience').tokenize().tokenAdd(returnedData.jobPostExperience.experienceId, returnedData.jobPostExperience.experienceType);
        }

        if(returnedData.jobPostShift != null ){
            $('#jobPostShift').tokenize().tokenAdd(returnedData.jobPostShift.timeShiftId, returnedData.jobPostShift.timeShiftName);
        }

        if(returnedData.jobPostEducation != null ){
            $('#jobPostEducation').tokenize().tokenAdd(returnedData.jobPostEducation.educationId, returnedData.jobPostEducation.educationName);
        }
        if(returnedData.jobPostAssetRequirements != null){
            returnedData.jobPostAssetRequirements.forEach(function (assets) {
                var id = assets.asset.assetId;
                var name = assets.asset.assetTitle;
                $('#jobPostAsset').tokenize().tokenAdd(id, name);
            });
        }
        if(returnedData.jobPostDocumentRequirements != null){
            returnedData.jobPostDocumentRequirements.forEach(function (document) {
                var id = document.idProof.idProofId;
                var name = document.idProof.idProofName;
                $('#jobPostIdProof').tokenize().tokenAdd(id, name);
            });
        }

    } else{
        notifyError("Job details not available");
        setTimeout(function(){
            window.location = "/recruiter/home";
        }, 2500);
    }

}

function validateJobRoleTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostJobRole').tokenize().tokenRemove(val);
        notifyError("Please select a valid job role from the dropdown list");
    }
}

function validateJobLocalityTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostLocality').tokenize().tokenRemove(val);
        notifyError("Please select a valid location from the dropdown list");
    }
}

function validateShiftTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostShift').tokenize().tokenRemove(val);
        notifyError("Please select a valid time shift from the dropdown list");
    }
}

function validateStartTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostStartTime').tokenize().tokenRemove(val);
        notifyError("Please select a valid start time from the dropdown list");
    }
}
function validateEndTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostEndTime').tokenize().tokenRemove(val);
        notifyError("Please select a valid end time from the dropdown list");
    }
}
function validateEduTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostEducation').tokenize().tokenRemove(val);
        notifyError("Please select a valid education from the dropdown list");
    }
}
function validateExpTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostExperience').tokenize().tokenRemove(val);
        notifyError("Please select a valid experience from the dropdown list");
    }
}
function validateLanguageTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostLanguage').tokenize().tokenRemove(val);
        notifyError("Please select a valid language from the dropdown list");
    }
}
function validateIncentiveTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostIncentives').tokenize().tokenRemove(val);
        notifyError("Please select a valid incentive from the dropdown list");
    }
}
function validateAssetTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostAsset').tokenize().tokenRemove(val);
        notifyError("Please select a valid asset from the dropdown list");
    }
}
function validateDocumentTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostIdProof').tokenize().tokenRemove(val);
        notifyError("Please select a valid asset from the dropdown list");
    }
}

function logoutRecruiter() {
    try {
        $.ajax({
            type: "GET",
            url: "/logoutRecruiter",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataLogoutRecruiter
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataLogoutRecruiter() {
    window.location = "/recruiter";
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}
