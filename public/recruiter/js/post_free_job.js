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
    });
}

function processDataCheckShift(returnedData) {
    if (returnedData != null) {
        returnedData.forEach(function (timeshift) {
            var id = timeshift.timeShiftId;
            var name = timeshift.timeShiftName;
            var option = $('<option value=' + id + '></option>').text(name);
            $('#jobPostWorkShift').append(option);
        });
    }
}

function processDataCheckEducation(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select Education");
    $('#jobPostEducation').append(defaultOption);
    returnedData.forEach(function (education) {
        var id = education.educationId;
        var name = education.educationName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostEducation').append(option);
    });
}

function processDataCheckExperience(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select Experience");
    $('#jobPostExperience').append(defaultOption);
    returnedData.forEach(function (experience) {
        var id = experience.experienceId;
        var name = experience.experienceType;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobPostExperience').append(option);
    });
}

function processDataGetAllLanguage(returnLanguage) {
    var data = [];

    returnLanguage.forEach(function (language) {
        var opt = {
            label: language.languageName, value: parseInt(language.languageId)
        };
        data.push(opt);
    });

    var selectList = $('#jobPostLanguage');
    selectList.multiselect({
        includeSelectAllOption: true,
        maxHeight: 300
    });
    selectList.multiselect('dataprovider', data);
    selectList.multiselect('rebuild');
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
    var defaultOption = $('<option value="-1"></option>').text("Select Job start time");
    $('#jobPostStartTime').append(defaultOption);

    defaultOption = $('<option value="-1"></option>').text("Select Job End time");
    $('#jobPostEndTime').append(defaultOption);
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
    try {
        var d = {
            jobPostId: 0,
            jobPostTitle: "tezst",
            jobPostJobRoleId: 3,
            jobPostVacancies: 20,
            jobPostLocalities: [1,2,3],
            jobPostPinCode: 898989,
            jobPostShiftId: 2,
            jobPostStartTime: 9,
            jobPostEndTime: 20,
            jobPostWorkingDays: "0001110",
            jobPostMinSalary: 15000,
            jobPostMaxSalary: 20000,
            jobPostIncentives: "no",
            jobPostDescription: "desc",
            jobPostGender: 0,
            jobPostMaxAge: 45,
            jobPostLanguage: [1,2,3],
            jobPostEducationId: 2,
            jobPostExperienceId: 4,
            jobPostIsHot: 1,
            jobPostCompanyId: 3,
            jobPostRecruiterId: 46
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
