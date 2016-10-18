/**
 * Created by dodo on 18/10/16.
 */

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

function processDataGetAllTimeSlots(returnedData) {
    $('#interviewTimeSlot').html('');
    returnedData.forEach(function(timeSlot) {
        var id = timeSlot.interviewTimeSlotId;
        var name = timeSlot.interviewTimeSlotName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
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

function validateTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostExperience').tokenize().tokenRemove(val);
        notifyError("Please select a valid location from the dropdown list");
    }
}

$(document).ready(function () {
/*
    var pathname = window.location.pathname; // Returns path only
    var jobPostIdUrl = pathname.split('/');
    var jobPostId = jobPostIdUrl[(jobPostIdUrl.length)-1];
    if(jobPostId != 0){
        try {
            $.ajax({
                type: "POST",
                url: "/getJobPostInfo/" + jobPostId + "/1",
                data: false,
                contentType: false,
                processData: false,
                success: processDataForJobPost
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
*/

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
            url: "/getAllTimeSlots",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetAllTimeSlots
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


/*parseInt(selectedLocality[0])*/

function saveJob() {
    var status = 1;

    var minSalary = $("#jobPostMinSalary").val();
    var maxSalary = $("#jobPostMaxSalary").val();

    if (minSalary != null) {
        minSalary = parseInt(minSalary);
    }

    if (maxSalary != null) {
        maxSalary = parseInt(maxSalary);
    }

    var jobPostLocalities = [];
    status = 1;
    var locality = $('#jobPostLocalities').val();
    if($("#jobPostTitle").val() == ""){
        notifyError("Please enter Job Post Title");
        status = 0;
    } else if($("#jobPostMinSalary").val() == "0"){
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
    } else if($("#jobPostJobRole").val() == ""){
        notifyError("Please enter job roles");
        status = 0;
    } else if($("#jobPostVacancies").val() == "" || $("#jobPostVacancies").val() == 0){
        notifyError("Please enter no. of vacancies");
        status = 0;
    } else if($("#jobPostStartTime").val() != -1){
        if($("#jobPostEndTime").val() != -1){
            if(parseInt($("#jobPostStartTime").val()) >= parseInt($("#jobPostEndTime").val())){
                notifyError("Start time cannot be more than end time");
                status = 0;
            }
        } else{
            notifyError("Please select job end time");
            status = 0;
        }
    } else if(locality == ""){
        notifyError("Please enter localities");
        status = 0;
    } else if($("#jobPostExperience").val() == ""){
        notifyError("Please enter Job Post Experience required");
        status = 0;
    }

    // checking age, location, gender
    var jobPostLanguage = $('#jobPostLanguage').val();
    var minAge = $("#jobPostMinAge").val();
    var maxAge = $("#jobPostMaxAge").val();
    var jobPostGender = parseInt(document.getElementById("jobPostGender").value);
    if (status !=0 ){
        if (minAge == 0 || !isValidAge(minAge)) {
            $("#jobPostMinAge").removeClass('invalid').addClass('invalid');
            notifyError("Please enter Job Post Min Age Requirement", 'danger');
            status = 0;
        }
        if (!isValidAge(maxAge)) {
            $("#jobPostMaxAge").removeClass('invalid').addClass('invalid');
            notifyError("Please enter Job Post Max Age Requirement", 'danger');
            status = 0;
        }
        if(maxAge !=0 && minAge > maxAge) {
            $("#jobPostMinAge").removeClass('invalid').addClass('invalid');
            $("#jobPostMaxAge").removeClass('invalid').addClass('invalid');
            notifyError("Incorrect Min/Max Age", 'danger');
            status = 0;
        }
        if (! jobPostLanguage && jobPostLanguage == null) {
            var jobPostLanguageSelector = "#job_post_form div.col-sm-9  span div button";
            $(jobPostLanguageSelector).removeClass('invalid').addClass('invalid');

            notifyError("Please enter Job Post Language Requirements", 'danger');
            status = 0;
        }
        if (jobPostGender == null || jobPostGender == -1) {
            $("#jobPostGender").attr('style', "border-color: red;");
            notifyError("Please enter Job Post Gender Requirement", 'danger');
            status = 0;
        }
        if(status == 0){
            scrollTo("#jobPostGender");
        }
    }

    //checking partner incentives
    if (partnerInterviewIncentiveVal < 0) {
        notifyError("Partner interview incentive cannot be negative", 'danger');
        status = 0;
    }
    else if (partnerJoiningIncentiveVal < 0) {
        notifyError("Partner joining incentive cannot be negative", 'danger');
        status = 0;
    } else if (partnerJoiningIncentiveVal < partnerInterviewIncentiveVal){
        notifyError("Partner interview incentive cannot be greater than partner joining incentive", 'danger');
        status = 0;
    }

    if(status == 1){
        if($("#jobPostRecruiter").val() != "" && $("#jobPostRecruiter").val() != null && $("#jobPostRecruiter").val() != undefined){
            recId = $("#jobPostRecruiter").val();
        }
        $("#jobPostExperience").addClass('selectDropdown').removeClass('selectDropdownInvalid');
        var i;
        for(i=0;i<locality.length; i++){
            jobPostLocalities.push(parseInt(locality[i]));
        }
        var jobPostIsHot = 0;
        var jobPostWorkFromHome = 0;
        if ($('#jobPostIsHot').is(":checked"))
        {
            jobPostIsHot = 1;
        }
        if ($('#jobPostWorkFromHome').is(":checked"))
        {
            jobPostWorkFromHome = 1;
        }
        var maxSalary = $("#jobPostMaxSalary").val();
        if(maxSalary == 0 || maxSalary == undefined){
            maxSalary = null;
        }

        var workingDays = "";
        for(i=1;i<=7;i++){
            if($("#working_" + i).is(":checked")){
                workingDays += "1";
            } else{
                workingDays += "0";
            }
        }

        var interviewDays = "";
        for(i=1;i<=7;i++){
            if($("#interview_day_" + i).is(":checked")){
                interviewDays += "1";
            } else{
                interviewDays += "0";
            }
        }

        $('#interviewTimeSlot input:checked').map(function() {
            var slotId = this.value;
            slotArray.push(parseInt(slotId));
        }).get();

        try {
            var d = {
                jobPostId: $("#jobPostId").val(),
                jobPostMinSalary: $("#jobPostMinSalary").val(),
                jobPostMaxSalary: $("#jobPostMaxSalary").val(),
                jobPostStartTime: parseInt($("#jobPostStartTime").val()),
                jobPostEndTime: parseInt($("#jobPostEndTime").val()),
                jobPostWorkingDays: workingDays,
                jobPostIsHot: jobPostIsHot,
                jobPostDescription: $("#jobPostDescription").val(),
                jobPostTitle: $("#jobPostTitle").val(),
                jobPostIncentives: $("#jobPostIncentives").val(),
                jobPostMinRequirement: $("#jobPostMinRequirement").val(),
                jobPostAddress: $("#jobPostAddress").val(),
                jobPostPinCode: $("#jobPostPinCode").val(),
                jobPostVacancies: $("#jobPostVacancies").val(),
                jobPostLocalities: jobPostLocalities,
                jobPostJobRoleId: parseInt($("#jobPostJobRole").val()),
                jobPostCompanyId: $("#jobPostCompany").val(),
                jobPostDescriptionAudio: "",
                jobPostWorkFromHome: jobPostWorkFromHome,
                jobPostShiftId: $("#jobPostWorkShift").val(),
                jobPostPricingPlanId: $("#jobPostPricingPlan").val(),
                jobPostEducationId: $("#jobPostEducation").val(),
                jobPostStatusId: $("#jobPostStatus").val(),
                pricingPlanTypeId: 1,
                jobPostExperienceId: $("#jobPostExperience").val(),
                jobPostRecruiterId: recId,
                partnerInterviewIncentive: $("#partnerInterviewIncentive").val(),
                partnerJoiningIncentive: $("#partnerJoiningIncentive").val(),
                jobPostInterviewDays: interviewDays,
                interviewTimeSlot: slotArray,
                jobPostLanguage: jobPostLanguage,
                jobPostMinAge: minAge,
                jobPostMaxAge: maxAge,
                jobPostGender: jobPostGender

            };
            $.ajax({
                type: "POST",
                url: "/addJobPost",
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
    console.log(returnedData);
}


function processDataForJobPost(returnedData) {
    $("#jobPostId").val(returnedData.jobPostId);
    if(returnedData.company != null ){
        $("#jobPostCompany").val(returnedData.company.companyId);
    }

    getRecruiters(returnedData.company.companyId);
    if(returnedData.recruiterProfile != null){
        $('#jobPostRecruiter').val(returnedData.recruiterProfile.recruiterProfileId);
    }
    $("#jobPostTitle").val(returnedData.jobPostTitle);

    $("#jobPostDescription").val(returnedData.jobPostDescription);

    // gender, language, age
    if (returnedData.jobPostMinAge != null) {
        $("#jobPostMinAge").val(returnedData.jobPostMinAge);
    }
    if (returnedData.jobPostMaxAge != null) {
        $("#jobPostMaxAge").val(returnedData.jobPostMaxAge);
    }
    if (returnedData.gender !=null) {
        $("#jobPostGender").val(returnedData.gender);
    }
    if (returnedData.jobPostLanguageRequirement != null) {
        var arr = [];
        var req = returnedData.jobPostLanguageRequirement;
        req.forEach(function (languageRequirement) {
            if(languageRequirement != null){
                arr.push(languageRequirement.language.languageId);
            }
        });
        $("#jobPostLanguage").val(arr);
        $("#jobPostLanguage").multiselect('rebuild');
    }


    $("#jobPostMinSalary").val(returnedData.jobPostMinSalary);
    $("#jobPostMaxSalary").val(returnedData.jobPostMaxSalary);

    if(returnedData.jobRole != null){
        var item = {};
        item ["id"] = returnedData.jobRole.jobRoleId;
        item ["name"] = returnedData.jobRole.jobName;
        jobPostJobRole.push(item);
    }
    $("#jobPostJobRole").tokenInput(getJob(), {
        theme: "facebook",
        placeholder: "Job Role?",
        hintText: "Start typing jobs (eg. Cook, Delivery boy..)",
        minChars: 0,
        tokenLimit: 1,
        prePopulate: jobPostJobRole,
        preventDuplicates: true
    });

    if(returnedData.jobPostToLocalityList != null){
        returnedData.jobPostToLocalityList.forEach(function (locality) {
            var item = {};
            item ["id"] = locality.locality.localityId;
            item ["name"] = locality.locality.localityName;
            jobPostLocality.push(item);
        });
    }
    if($("#jobPostLocalities").val() == ""){
        $("#jobPostLocalities").tokenInput(getLocality(), {
            theme: "facebook",
            placeholder: "job Localities?",
            hintText: "Start typing Area (eg. BTM Layout, Bellandur..)",
            minChars: 0,
            prePopulate: jobPostLocality,
            preventDuplicates: true
        });
    }
    if(returnedData.jobPostAddress != null ){
        $("#jobPostAddress").val(returnedData.jobPostAddress);
    }

    if(returnedData.jobPostPinCode != null ){
        $("#jobPostPinCode").val(returnedData.jobPostPinCode);
    }

    if(returnedData.jobPostIncentives != null ){
        $("#jobPostIncentives").val(returnedData.jobPostIncentives);
    }

    if(returnedData.jobPostMinRequirement != null ){
        $("#jobPostMinRequirement").val(returnedData.jobPostMinRequirement);
    }

    if(returnedData.jobPostStartTime != null ){
        $("#jobPostStartTime").val(returnedData.jobPostStartTime);
    }

    if(returnedData.jobPostEndTime != null ){
        $("#jobPostEndTime").val(returnedData.jobPostEndTime);
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

    if(returnedData.jobPostIsHot != null ){
        if(returnedData.jobPostIsHot == true){
            $("#jobPostIsHot").prop('checked', true);
        } else{
            $("#jobPostIsHot").prop('checked', false);
        }
    }

    if(returnedData.jobPostWorkFromHome != null ){
        if(returnedData.jobPostWorkFromHome == true){
            $("#jobPostWorkFromHome").prop('checked', true);
        } else{
            $("#jobPostWorkFromHome").prop('checked', false);
        }
    }

    if(returnedData.jobPostVacancies != null ){
        $("#jobPostVacancies").val(returnedData.jobPostVacancies);
    }

    if(returnedData.pricingPlanType != null ){
        $("#jobPostPricingPlan").val(returnedData.pricingPlanType.pricingPlanTypeId);
    }

    if(returnedData.jobPostExperience != null ){
        $("#jobPostExperience").val(returnedData.jobPostExperience.experienceId);
    }

    if(returnedData.jobPostShift != null ){
        $("#jobPostWorkShift").val(returnedData.jobPostShift.timeShiftId);
    }

    if(returnedData.jobPostEducation != null ){
        $("#jobPostEducation").val(returnedData.jobPostEducation.educationId);
    }

    if(returnedData.jobPostStatus != null ){
        $("#jobPostStatus").val(returnedData.jobPostStatus.jobStatusId);
    }

    if(Object.keys(returnedData.interviewDetailsList).length > 0){
        var interviewDetailsList = returnedData.interviewDetailsList;
        if(interviewDetailsList[0].interviewDays != null){
            var interviewDays = interviewDetailsList[0].interviewDays.toString(2);

            /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
            if(interviewDays.length != 7){
                x = 7 - interviewDays.length;
                var modifiedInterviewDays = "";

                for(i=0;i<x;i++){
                    modifiedInterviewDays += "0";
                }
                modifiedInterviewDays += interviewDays;
                interviewDays = modifiedInterviewDays;
            }

            for(i=1; i<=7; i++){
                if(interviewDays[i-1] == 1){
                    $("#interview_day_" + i).prop('checked', true);
                } else{
                    $("#interview_day_" + i).prop('checked', false);
                }
            }
        }

        //prefilling time slots
        interviewDetailsList.forEach(function (timeSlot){
            var slotBtn = $("#interviewSlot_" + timeSlot.interviewTimeSlot.interviewTimeSlotId);
            slotBtn.prop('checked', true);
            slotBtn.parent().addClass('active');
        });
    }
    $("#partnerInterviewIncentive").val(returnedData.jobPostPartnerInterviewIncentive);
    $("#partnerJoiningIncentive").val(returnedData.jobPostPartnerJoiningIncentive);
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}
