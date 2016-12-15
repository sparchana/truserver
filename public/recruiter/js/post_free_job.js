/**
 * Created by dodo on 18/10/16.
 */

var jpId = 0;
var jpCompanyId;
var jpRecruiterId;

var fullAddress;
var addressLandmark;
var addressBuildingNo;

var interviewLat = null;
var interviewLng = null;

var timeSlotTotalCount = 0;

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
    $('#timeSlotSection').html('');
    returnedData.forEach(function(timeSlot) {
        timeSlotTotalCount++;
        var id = timeSlot.interviewTimeSlotId;
        var name = timeSlot.interviewTimeSlotName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option ='<input type="checkbox" id=\"interview_slot_' + id + '\" /><label for=\"interview_slot_' + id + '\">' + name + ' </label><span style="margin-left:10px"></span>&nbsp;';
        $('#timeSlotSection').append(option);
    });
}

function processDataGetIdProofs(returnedIdProofs) {
    $('#jobPostIdProof')
        .find('option')
        .remove();
    $('#jobPostIdProof').tokenize().clear();
    if(Object.keys(returnedIdProofs).length == 0){
        $("#documentSection").hide();
    } else{
        $("#documentSection").show();
        returnedIdProofs.forEach(function (idProof) {
            var id = idProof.idProofId;
            var name = idProof.idProofName;
            var option = $('<option value=' + id + '></option>').text(name);
            $('#jobPostIdProof').append(option);
        });

    }
}

function processDataGetAssets(returnedAssets) {
    $('#jobPostAsset').tokenize().clear();
    $('#jobPostAsset')
        .find('option')
        .remove();
    if(Object.keys(returnedAssets).length == 0){
        $("#assetSection").hide();
    } else{
        $("#assetSection").show();
        returnedAssets.forEach(function (asset) {
            var id = asset.assetId;
            var name = asset.assetTitle;
            var option = $('<option value=' + id + '></option>').text(name);
            $('#jobPostAsset').append(option);
        });

    }
}

function changeJobDescClass() {
    $("font#jobReqTabHead").removeClass("activeTab");
    $("font#jobDescTabHead").addClass("activeTab");
}

function changeJobReqClass() {
    renderMap();
    $("font#jobReqTabHead").addClass("activeTab");
    $("font#jobDescTabHead").removeClass("activeTab");
}
$(document).scroll(function(){
    if ($(this).scrollTop() > 30) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});
function toJobDetail(){
    $("font#jobReqTabHead").removeClass("activeTab");
    $("font#jobDescTabHead").addClass("activeTab");

    $('ul.tabs').tabs('select_tab', 'jobDetails');
    $('body').scrollTop(0);
}

function clearField(){
    $('#interviewAddress').val('');
}

function renderMap(){
    if(interviewLat == null){
        //default values of MG Road
        interviewLat = 12.975568542471832;
        interviewLng = 77.60660031434168;
    }

    $('#map_parent').locationpicker({
        location: {
            latitude: interviewLat,
            longitude: interviewLng
        },
        radius: 80,
        inputBinding: {
            latitudeInput: $('#jp_lat'),
            longitudeInput: $('#jp_lon'),
            locationNameInput: $('#interviewAddress')
        },
        enableAutocomplete: true,
        onchanged: function (currentLocation, radius, isMarkerDropped) {
            //add method if we want to perform any action
            $("#jp_lat").val(currentLocation.latitude);
            $("#jp_lon").val(currentLocation.longitude);

            $("#landmarkDetails").show();
            $("#interviewBuildingNo").val('');
            $("#interviewLandmark").val('');

            //TODO: address box to capture building name, street name etc
        }
    });
}

function toJobRequirement(){
    var status = 1;
    var vacancies = $("#jobPostVacancies").val();

    var minSalary = $("#jobPostMinSalary").val();
    var maxSalary = $("#jobPostMaxSalary").val();

    var jobLocalitySelected = $("#jobPostLocality").val();
    var jobPostWorkShift = $("#jobPostShift").val();

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

    var preferredJobLocationList = [];

    var k;
    if(jobLocalitySelected != null){
        for (k = 0; k < Object.keys(jobLocalitySelected).length; k++) {
            preferredJobLocationList.push(parseInt(jobLocalitySelected[k]));
        }
    }

    var startTime = $("#jobPostStartTime").val();
    var endTime = $("#jobPostEndTime").val();

    if($("#jobPostTitle").val() == ""){
        notifyError("Please enter Job Post Title");
        status = 0;
    } else if($("#jobPostJobRole").val() == null){
        notifyError("Please enter job role");
        status = 0;
    } else if(jobLocalitySelected == null){
        notifyError("Please enter job localities");
        status = 0;
    } else if(vacancies == ""){
        notifyError("Please enter no. of vacancies");
        status = 0;
    } else if(vacancies < 1){
        notifyError("Please enter valid number for no. of vacancies");
        status = 0;
    } else if(minSalary == ""){
        notifyError("Please mention minimum salary");
        status = 0;
    } else if(isValidSalary(minSalary) == false){
        notifyError("Please enter valid minimum salary");
        status = 0;
    } else if(minSalary > 100000){
        notifyError("Please enter minimum salary less tha 99,999");
        status = 0;
    } else if(maxSalary != 0 && (isValidSalary(maxSalary) == false)){
        notifyError("Please enter valid maximum salary");
        status = 0;
    } else if(maxSalary != 0 && (maxSalary <= minSalary)){
        notifyError("Maximum salary should be greater than minimum salary");
        status = 0;
    } else if($("input[name='jobPostGender']:checked").val() == null || $("input[name='jobPostGender']:checked").val() == undefined){
        notifyError("Please specify gender");
        status = 0;
    } else if(startTime != null){
        if(endTime != null){
            if(parseInt(startTime) >= parseInt(endTime)){
                if(jobPostWorkShift == 1 || jobPostWorkShift == 3 || jobPostWorkShift == null){
                    notifyError("Start time cannot be more than end time. If night shift, please specify evening shift");
                    status = 0;
                } //else its a night shift job
            }
        } else{
            notifyError("Please select job end time");
            status = 0;
        }
    } else if(jobPostWorkShift == null){
        notifyError("Please specify work shift");
        status = 0;
    }

    if(status == 1){
        $("font#jobReqTabHead").addClass("activeTab");
        $("font#jobDescTabHead").removeClass("activeTab");

        $('ul.tabs').tabs('select_tab', 'jobRequirement');
        $('body').scrollTop(0);

        renderMap();
    }

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
    $(".postJobNav").addClass("active");
    $(".postJobNavMobile").addClass("active");

    var pathname = window.location.pathname; // Returns path only
    var jobPostIdUrl = pathname.split('/');
    var jobPostId = jobPostIdUrl[(jobPostIdUrl.length)-1];

    $( "#check_applications" ).prop( "checked", true );
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
    } else{
        $('#jobPostExperience').tokenize().tokenAdd(5, "Any");
        $('#jobPostEducation').tokenize().tokenAdd(6, "Any");

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


    var i;

    for(i=0; i<=24; i++){
        var option = document.createElement("option");
        option.value = i;
        if(i == 0){
            option.textContent = "12 AM";
        } else{
            if(i >= 12){
                if((i-12) == 0){
                    option.textContent = "12 PM";
                } else{
                    option.textContent = (i - 12) + " PM";
                }
            } else{
                option.textContent = i + " AM";
            }
        }
        $('#jobPostStartTime').append(option);
    }

    for(i = 0; i <= 24; i++){
        option = document.createElement("option");
        option.value = i;
        if(i == 0){
            option.textContent = "12 AM";
        } else{
            if(i >= 12){
                if((i-12) == 0){
                    option.textContent = "12 PM";
                } else{
                    option.textContent = (i - 12) + " PM";
                }
            } else{
                option.textContent = i + " AM";
            }
        }
        $('#jobPostEndTime').append(option);
    }

    $('input[type=radio][name=jobPostAgeReq]').change(function() {
        if (this.value == 1) {
            notifySuccess("Please specify maximum age required");
            $("#jobPostMaxAge").show();
        } else{
            $("#jobPostMaxAge").hide();
        }
    });

    //checkbox change action
    $('#check_applications').change(function() {
        if($(this).is(":checked")) {
            $("#reviewApplicationLabel").html('Confirm interviews for all applications (uncheck this option if you want to review applications before confirming interviews)');
        } else{
            $("#reviewApplicationLabel").html('Confirm interviews for all applications');
        }
    });
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

    interviewLat = $("#jp_lat").val();
    interviewLng = $("#jp_lon").val();

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
        notifyError("Please enter work locations");
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
                if(jobPostWorkShift == 1 || jobPostWorkShift == 3 || jobPostWorkShift == null){
                    notifyError("Start time cannot be more than end time. If night shift, please specify evening shift");
                    status = 0;
                } //else its a night shift job
            }
        } else{
            notifyError("Please select job end time");
            status = 0;
        }
    } else if(jobPostExperience == null){
        notifyError("Please select min experience");
        status = 0;
    } else if($('#jobPostEducation').val() == null){
        notifyError("Please select min education");
        status = 0;
    }

    // checking age, location, gender
    var maxAge = $("#jobPostMaxAge").val();
    var jobPostAgeReq = parseInt($("input[name='jobPostAgeReq']:checked").val());
    var jobPostGender = parseInt($("input[name='jobPostGender']:checked").val());
    if (status != 0 ){
        if(jobPostAgeReq == 1){
            if(maxAge == ""){
                notifyError("Specify max age limitation");
                status = 0;
            } else if (!isValidAge(maxAge)) {
                $("#jobPostMaxAge").removeClass('invalid').addClass('invalid');
                notifyError("Specify max age limitation");
                status = 0;
            } else if(maxAge < 18 || maxAge > 65){
                notifyError("Max age cannot be less than 18 and greater than 65");
                status = 0;
            }
        }

        if (jobPostGender == null || jobPostGender == -1) {
            notifyError("Please enter Job Post Gender Requirement");
            status = 0;
        }

        var timeSlotCount = 0;
        var interviewDayCount = 0;
        for(i=1; i<= timeSlotTotalCount; i++){
            if($("#interview_slot_" + i).is(":checked")){
                timeSlotCount = timeSlotCount + 1;
            }
        }
        for(i=1;i<=7;i++) {
            if ($("#interview_day_" + i).is(":checked")) {
                interviewDayCount = interviewDayCount + 1;
            }
        }
        if(interviewDayCount > 0 && timeSlotCount == 0){
            notifyError("Please select interview time slot");
            status = 0;
        } else if(timeSlotCount > 0 && interviewDayCount == 0){
            notifyError("Please select interview days");
            status = 0;
        }
    }

    var interviewDays = "";
    for(i=1; i<=7; i++){
        if($("#interview_day_" + i).is(":checked")){
            interviewDays += "1";
        } else{
            interviewDays += "0";
        }
    }

    var slotArray = [];
    for(i = 1; i <= timeSlotTotalCount; i++){
        if($("#interview_slot_" + i).is(":checked")){
            slotArray.push(parseInt(i));
        }
    }

    if(interviewDays == "0000000"){
        notifyError("Please specify interview days");
        status = 0;
    } else if(slotArray == []){
        notifyError("Please specify interview slots");
        status = 0;
    } else if(interviewLat == null){
        notifyError("Please enter interview address");
        status = 0;
        $('#interviewAddress').val('');
    } else if(interviewLat == 12.975568542471832){ //if address is by default
        notifyError("Please enter interview address");
        status = 0;
        $('#interviewAddress').val('');
    }

    if(status == 1){
        console.log("Submitting: " + interviewLat + " --- " + interviewLng);
        var i;
        var workingDays = "";

        for(i=1;i<=7;i++){
            if($("#working_" + i).is(":checked")){
                workingDays += "1";
            } else{
                workingDays += "0";
            }
        }

        var reviewApplication;
        if($('#check_applications').is(':checked')){
            reviewApplication = 1;
        } else{
            reviewApplication = 0;
        }

        fullAddress = $('#interviewAddress').val();
        addressLandmark = $('#interviewLandmark').val();
        addressBuildingNo = $('#interviewBuildingNo').val();

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
                jobPostIncentives: $("#jobPostIncentives").val(),
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
                jobPostAsset: jobPostAsset,
                jobPostInterviewDays: interviewDays,
                interviewTimeSlot: slotArray,
                jobPostInterviewLocationLat: interviewLat,
                jobPostInterviewLocationLng: interviewLng,
                jobPostAddress: fullAddress,
                reviewApplications: reviewApplication,
                jobPostAddressBuildingNo: addressBuildingNo,
                jobPostAddressLandmark: addressLandmark
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
        notifySuccess("Excellent! We have received your job details. You will receive a notification once the job is made live!");
        setTimeout(function(){
            window.location = "/recruiter/allRecruiterJobPosts";
        }, 2500);
    } else if(returnedData.status == 2){
        notifySuccess("Job post successfully updated!");
        setTimeout(function(){
            window.location = "/recruiter/allRecruiterJobPosts";
        }, 2500);
    } else{
        notifyError("Something went wrong. Please try again later!");
    }
}


function processDataForJobPost(returnedData) {
    if(returnedData != "0"){
        jpId = returnedData.jobPostId;
        if(returnedData.company != null ){
            jpCompanyId = returnedData.company.companyId;
        }

        $("#jobPostTitle").val(returnedData.jobPostTitle);

        $("#jobPostDescription").val(returnedData.jobPostDescription);

        $("#jobPostIncentives").val(returnedData.jobPostIncentives);

        // gender, language, age
        if (returnedData.jobPostMaxAge != null) {
            $("#jobPostMaxAge").val(returnedData.jobPostMaxAge);
            $("#jobPostMaxAge").show();
            $("#yes").prop('checked', true);
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
        if (returnedData.jobPostLanguageRequirements != null) {
            var req = returnedData.jobPostLanguageRequirements;
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

        if(returnedData.jobPostToLocalityList != null){
            returnedData.jobPostToLocalityList.forEach(function (locality) {
                var id = locality.locality.localityId;
                var name = locality.locality.localityName;
                $('#jobPostLocality').tokenize().tokenAdd(id, name);
            });
        }

        if(returnedData.jobPostStartTime != null && returnedData.jobPostStartTime != -1){
            if(returnedData.jobPostStartTime == 0){
                $('#jobPostStartTime').tokenize().tokenAdd(returnedData.jobPostStartTime, "12 AM");
            } else {
                if(returnedData.jobPostStartTime >= 12){
                    if((returnedData.jobPostStartTime - 12) == 0){
                        $('#jobPostStartTime').tokenize().tokenAdd(returnedData.jobPostStartTime, "12 PM");
                    } else{
                        $('#jobPostStartTime').tokenize().tokenAdd(returnedData.jobPostStartTime, returnedData.jobPostStartTime - 12 + " PM");
                    }
                } else{
                    $('#jobPostStartTime').tokenize().tokenAdd(returnedData.jobPostStartTime, returnedData.jobPostStartTime + " AM");
                }
            }
        }

        if(returnedData.jobPostEndTime != null && returnedData.jobPostEndTime!= -1){
            if(returnedData.jobPostEndTime== 0){
                $('#jobPostEndTime').tokenize().tokenAdd(returnedData.jobPostEndTime, "12 AM");
            } else {
                if(returnedData.jobPostEndTime>= 12){
                    if((returnedData.jobPostEndTime- 12) == 0){
                        $('#jobPostEndTime').tokenize().tokenAdd(returnedData.jobPostEndTime, "12 PM");
                    } else{
                        $('#jobPostEndTime').tokenize().tokenAdd(returnedData.jobPostEndTime, returnedData.jobPostEndTime- 12 + " PM");
                    }
                } else{
                    $('#jobPostEndTime').tokenize().tokenAdd(returnedData.jobPostEndTime, returnedData.jobPostEndTime+ " AM");
                }
            }
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
        } else{
            $('#jobPostExperience').tokenize().tokenAdd(5, "Any");
        }

        if(returnedData.jobPostShift != null ){
            $('#jobPostShift').tokenize().tokenAdd(returnedData.jobPostShift.timeShiftId, returnedData.jobPostShift.timeShiftName);
        }

        if(returnedData.jobPostEducation != null ){
            $('#jobPostEducation').tokenize().tokenAdd(returnedData.jobPostEducation.educationId, returnedData.jobPostEducation.educationName);
        } else{
            $('#jobPostEducation').tokenize().tokenAdd(6, "Any");
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

        if(Object.keys(returnedData.interviewDetailsList).length > 0){
            var interviewDetailsList = returnedData.interviewDetailsList;
            if(interviewDetailsList[0].interviewDays != null){

                //interview details
                if(returnedData.interviewDetailsList[0].lat != null){
                    interviewLat = returnedData.interviewDetailsList[0].lat;
                    interviewLng = returnedData.interviewDetailsList[0].lng;
                }

                //interview address and landmark
                $("#landmarkDetails").show();
                if(returnedData.interviewDetailsList[0].interviewBuildingNo){
                    $("#interviewBuildingNo").val(returnedData.interviewDetailsList[0].interviewBuildingNo);
                }

                if(returnedData.interviewDetailsList[0].interviewLandmark){
                    $("#interviewLandmark").val(returnedData.interviewDetailsList[0].interviewLandmark);
                }

                //interview days and slots
                var interviewDays = interviewDetailsList[0].interviewDays.toString(2);

                if(interviewDetailsList[0].reviewApplication == null || interviewDetailsList[0].reviewApplication == 1){
                    $("#check_applications" ).prop( "checked", true);
                    $("#reviewApplicationLabel").html('Confirm interviews for all applications (uncheck this option if you want to review applications before confirming interviews)');
                } else{
                    $("#check_applications" ).prop( "checked", false);
                    $("#reviewApplicationLabel").html('Confirm interviews for all applications');
                }

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
                var slotBtn = $("#interview_slot_" + timeSlot.interviewTimeSlot.interviewTimeSlotId);
                slotBtn.prop('checked', true);
                slotBtn.parent().addClass('active');
            });
        }
    } else{
        notifyError("Job details not available");
        setTimeout(function(){
            window.location = "/recruiter/home";
        }, 2500);
    }

}
function generateDocument() {
    var jobRoleId = parseInt(($("#jobPostJobRole").val())[0]);
    if(jobRoleId != 0){
        try {
            $.ajax({
                type: "GET",
                url: "/support/api/getDocumentReqForJobRole/?job_role_id="+jobRoleId,
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataGetIdProofs
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}
function generateAsset() {
    var jobRoleId = parseInt(($("#jobPostJobRole").val())[0]);
    if(jobRoleId != 0){
        try {
            $.ajax({
                type: "GET",
                url: "/support/api/getAssetReqForJobRole/?job_role_id="+jobRoleId,
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


function validateJobRoleTokenVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#jobPostJobRole').tokenize().tokenRemove(val);
        notifyError("Please select a valid job role from the dropdown list");
    } else{
        generateDocument();
        generateAsset();
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
