/**
 * Created by batcoder1 on 29/6/16.
 */

var recId = 0;

var contactCredits = 0;
var interviewCredits = 0;

var addressLandmark;
var addressBuildingNo;

function processDataAddJobPost(returnedData) {
    if(returnedData.status == 1){
        notifyError("Job Post Created Successfully. Closing this window...", 'success');
        setTimeout(function(){ window.close()}, 2000);
    } else if(returnedData.status == 2){
        notifyError("Job Post Updated Successfully. Closing this window...", 'success');
        setTimeout(function(){window.close()}, 2000);
    } else{
        $('#saveButton').prop('disabled', false);
        notifyError("Something went wrong. Please try again later", 'success');
    }
}

function processDataAddRecruiterAndUpdateRecId(returnedData) {
    recId = returnedData.recruiterId;
}
function scrollTo(selector) {
    $('html, body').animate({
        scrollTop: $(selector).offset().top
    }, 1000);
}

function notifyError(msg, type){
    $.notify({
        message: msg,
        animate: {
            enter: 'animated lightSpeedIn',
            exit: 'animated lightSpeedOut'
        }
    },{
        type: type
    });
}

function computeCreditValue() {
    if($('input:radio[name="candidateCreditType"]:checked').val() == 1){
        if(validateContactUnlockCreditValues() == 1){
            candidateCreditTypeStatus = 1;
            contactCredits = parseInt($("#candidateContactCredits").val());
            $("#addCreditInfoDiv").show();
            $("#contactUnlockCreditInfo").html("Adding " + contactCredits + " contact unlock credits ");
        }
    }
    if($('input:radio[name="interviewCreditType"]:checked').val() == 1){
        if(validateInterviewUnlockCreditValues() == 1){
            interviewCreditTypeStatus = 1;
            interviewCredits = parseInt($("#interviewCredits").val());
            $("#addCreditInfoDiv").show();
            $("#interviewUnlockCreditInfo").html("Adding " + interviewCredits + " interview unlock credits ");
        }
    }

    if(interviewCreditTypeStatus == 1 && candidateCreditTypeStatus == 1){
        $("#creditModal").modal("hide");
    }
}


function closeCreditModal() {
    $("#creditModal").modal("hide");
}

// job_post_form ajax script
$(function() {
    $("#job_post_form").submit(function(eventObj) {
        eventObj.preventDefault();
        status = 1;
        if(($("#jobPostRecruiter").val() == "" || $("#jobPostRecruiter").val() == -1 || $("#jobPostRecruiter").val() == null) && $("#recruiterSection").is(':visible') == true){
            var status = 1;
            var recruiterName = validateName($("#recruiterName").val());
            var recruiterMobile = validateMobile($("#recruiterMobile").val());

            //checking first name
            switch(recruiterName){
                case 0: notifyError("First name contains number. Please Enter a valid First Name", 'danger');
                    status=0; break;
                case 2: notifyError("First Name cannot be blank spaces. Enter a valid first name", 'danger');
                    status=0; break;
                case 3: notifyError("First name contains special symbols. Enter a valid first name", 'danger');
                    status=0; break;
                case 4: notifyError("Please enter your first name", 'danger');
                    status=0; break;
            }
            if(recruiterMobile == 0){
                notifyError("Enter a valid mobile number", 'danger');
                status=0;
            } else if(recruiterMobile == 1){
                notifyError("Enter 10 digit mobile number", 'danger');
                status=0;
            } else if(recruiterMobile == "") {
                notifyError("Please Enter recruiter Contact", 'danger');
                status=0;
            }

            if(status == 1){
                try{
                    var rec = {
                        recruiterName: $("#recruiterName").val(),
                        recruiterMobile: $("#recruiterMobile").val(),
                        recruiterLandline: $("#recruiterLandline").val(),
                        recruiterEmail: $("#recruiterEmail").val(),
                        recruiterCompany: $("#jobPostCompany").val(),
                        contactCredits: contactCredits,
                        interviewCredits: interviewCredits
                    };
                } catch (exception) {
                    console.log("exception occured!!" + exception);
                }

                $.ajax({
                    type: "POST",
                    url: "/addRecruiter",
                    async: false,
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(rec),
                    success: processDataAddRecruiterAndUpdateRecId
                });
            }
        } else if($("#recruiterSection").is(':visible') == false && ($("#jobPostRecruiter").val() == "" || $("#jobPostRecruiter").val() == -1 || $("#jobPostRecruiter").val() == null) == true){
            notifyError("Please select a recruiter", 'danger');
            $("#jobPostRecruiter").addClass('selectDropdownInvalid').removeClass('selectDropdown');
            status = 0;
        }

        var timeSlotCount = 0;
        var interviewDayCount = 0;
        $('#interviewTimeSlot input:checkbox').each(function () {
            if ($(this).is(':checked')) {
                timeSlotCount = timeSlotCount + 1;
            }
        });
        for(i=1;i<=7;i++) {
            if ($("#interview_day_" + i).is(":checked")) {
                interviewDayCount = interviewDayCount + 1;
            }
        }

        interviewLat = $("#jp_lat").val();
        interviewLng = $("#jp_lon").val();

        var minSalary = $("#jobPostMinSalary").val();
        var maxSalary = $("#jobPostMaxSalary").val();

        if (minSalary != null) {
            minSalary = parseInt(minSalary);
        }

        if (maxSalary != null) {
            maxSalary = parseInt(maxSalary);
        }

        var partnerInterviewIncentiveVal = parseInt($("#partnerInterviewIncentive").val());
        var partnerJoiningIncentiveVal = parseInt($("#partnerJoiningIncentive").val());

        var jobPostLocalities = [];
        var locality = $('#jobPostLocalities').val().split(",");

        if($("#jobPostCompany").val() == ""){
            notifyError("Please enter Job Post Company", 'danger');
            $("#jobPostCompany").addClass('selectDropdownInvalid').removeClass('selectDropdown');
            status = 0;
        } else if($("#jobPostRecruiter").val() == "" || $("#jobPostRecruiter").val() == -1 || recId < 0){
            notifyError("Please select a recruiter", 'danger');
            $("#jobPostCompany").addClass('selectDropdown').removeClass('selectDropdownInvalid');
            $("#jobPostRecruiter").addClass('selectDropdownInvalid').removeClass('selectDropdown');
            status = 0;
        } else if($("#jobPostTitle").val() == ""){
            $("#jobPostRecruiter").addClass('selectDropdown').removeClass('selectDropdownInvalid');
            notifyError("Please enter Job Post Title", 'danger');
            $("#jobPostTitle").addClass('invalid');
            status = 0;
        } else if($("#jobPostMinSalary").val() == "0"){
            notifyError("Please enter Job Post Minimum salary", 'danger');
            $("#jobPostTitle").removeClass('invalid');
            $("#jobPostMinSalary").addClass('invalid');
            status = 0;
        } else if(isValidSalary(minSalary) == false){
            notifyError("Please enter valid min salary", 'danger');
            $("#jobPostMinSalary").removeClass('invalid');
            $("#jobPostMinSalary").addClass('invalid');
            status = 0;
        } else if(maxSalary != 0 && (isValidSalary(maxSalary) == false)){
            notifyError("Please enter valid max salary", 'danger');
            $("#jobPostMinSalary").removeClass('invalid');
            $("#jobPostMaxSalary").addClass('invalid');
            status = 0;
        } else if(maxSalary != 0 && (maxSalary <= minSalary)){
            notifyError("Max salary should be greated than min salary", 'danger');
            $("#jobPostMaxSalary").removeClass('invalid');
            $("#jobPostMaxSalary").addClass('invalid');
            status = 0;
        } else if($("#jobPostJobRole").val() == ""){
            $("#jobPostMinSalary").removeClass('invalid');
            notifyError("Please enter job roles", 'danger');
            $("#jobPostJobRole").addClass('invalid');
            status = 0;
        } else if($("#jobPostVacancies").val() == "" || $("#jobPostVacancies").val() == 0){
            notifyError("Please enter no. of vacancies", 'danger');
            $("#jobPostJobRole").removeClass('invalid');
            $("#jobPostVacancies").addClass('invalid');
            status = 0;
        } else if($("#jobPostStartTime").val() != -1){
            if($("#jobPostEndTime").val() != -1){
                if(parseInt($("#jobPostStartTime").val()) >= parseInt($("#jobPostEndTime").val())){
                    if($("#jobPostWorkShift").val() == 1 || $("#jobPostWorkShift").val() == 3 || $("#jobPostWorkShift").val() == null || $("#jobPostWorkShift").val() == ''){
                        notifyError("Start time cannot be more than end time. If night shift, please specify evening shift", 'danger');
                        status = 0;
                    } //else its a night shift job
                }
            } else{
                notifyError("Please select job end time", 'danger');
                status = 0;
            }
        } else if($("#jobPostExperience").val() == ""){
            $("#jobPostLocalities").removeClass('invalid');
            $("#jobPostExperience").addClass('selectDropdownInvalid').removeClass('selectDropdown');
            notifyError("Please enter Job Post Experience required", 'danger');
            status = 0;
        }

        if(interviewDayCount > 0 && timeSlotCount == 0){
            $("#jobPostExperience").removeClass('invalid');
            notifyError("Please select interview time slot", 'danger');
            status = 0;
        } else if(timeSlotCount > 0 && interviewDayCount == 0){
            notifyError("Please select interview days", 'danger');
            status = 0;
        }

        if(locality == ""){
            $("#jobPostVacancies").removeClass('invalid');
            $("#jobPostLocalities").addClass('invalid');
            notifyError("Please enter localities", 'danger');
            status = 0;
        }

        // checking age, location, gender
        var jobPostLanguage = $('#jobPostLanguage').val();
        var jobPostDocument = $('#jobPostDocument').val();
        var jobPostAsset = $('#jobPostAsset').val();
        var maxAge = $("#jobPostMaxAge").val();
        var jobPostGender = parseInt(document.getElementById("jobPostGender").value);

        if (status !=0 ) {
            if (!isValidAge(maxAge) || maxAge < 18) {
                $("#jobPostMaxAge").removeClass('invalid').addClass('invalid');
                if(maxAge < 18) {
                    notifyError("Max Age should be greater than 18 years", 'danger');
                } else {
                    notifyError("Please enter Job Post Max Age Requirement", 'danger');
                }
                status = 0;
            }
            if (! jobPostLanguage && jobPostLanguage == null) {
                var jobPostLanguageSelector = "#jdRequirementPanel div.panel-container span div button";
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
                scrollTo("#jdRequirementPanel");
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

        var jobStatusId = $("#jobPostStatus").val();
        if(jobStatusId == null || jobStatusId == ""){
            notifyError("Please select a job status", 'danger');
            status = 0;
        }

        var interviewDays = "";
        for(i=1;i<=7;i++){
            if($("#interview_day_" + i).is(":checked")){
                interviewDays += "1";
            } else{
                interviewDays += "0";
            }
        }

        slotArray = [];
        $('#interviewTimeSlot input:checked').map(function() {
            var slotId = this.value;
            slotArray.push(parseInt(slotId));
        }).get();

        var pauseApplication = $('#jobPostStatus').val();
        var jobPostResumeDate = "";


        if(interviewDays == "0000000"){
            notifyError("Please specify interview days", 'danger');
            status = 0;
        } else if(slotArray == []){
            notifyError("Please specify interview slots", 'danger');
            status = 0;
        } else if(interviewLat == null){
            notifyError("Please enter interview address", "danger");
            status = 0;
            $('#interviewAddress').val('');
        } else if(interviewLat == 12.975568542471832){ //if address is by default
            notifyError("Please enter interview address", "danger");
            status = 0;
            $('#interviewAddress').val('');
        } else if((pauseApplication == 5) && $("#resume_date").val() == ""){
            notifyError("Please select application resume date", "danger");
            status = 0;
        } else if((pauseApplication == 5) && $("#resume_date").val() != ""){
            var selectedDate = new Date($("#resume_date").val());
            var todaysDate = new Date();

            if(selectedDate < todaysDate){
                notifyError("Please select a date greater than today", "danger");
                status = 0;
            } else{
                jobStatusId = 5;
                jobPostResumeDate = selectedDate.getFullYear() + "-" + (selectedDate.getMonth() + 1) + "-" + selectedDate.getDate();
            }
        }

        if(status == 1){
            console.log(recId);
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

            fullAddress = $('#interviewAddress').val();
            addressLandmark = $('#interviewLandmark').val();
            addressBuildingNo = $('#interviewBuildingNo').val();

            var reviewApplication;
            if($('#check_applications').is(':checked')){
                reviewApplication = 1;
            } else{
                reviewApplication = 0;
            }

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
                    jobPostAddress: fullAddress,
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
                    jobPostStatusId: jobStatusId,
                    pricingPlanTypeId: 1,
                    jobPostExperienceId: $("#jobPostExperience").val(),
                    jobPostRecruiterId: recId,
                    partnerInterviewIncentive: $("#partnerInterviewIncentive").val(),
                    partnerJoiningIncentive: $("#partnerJoiningIncentive").val(),
                    jobPostInterviewDays: interviewDays,
                    interviewTimeSlot: slotArray,
                    jobPostLanguage: jobPostLanguage,
                    jobPostDocument: jobPostDocument,
                    jobPostAsset: jobPostAsset,
                    jobPostMaxAge: maxAge,
                    jobPostGender: jobPostGender,
                    jobPostInterviewLocationLat: interviewLat,
                    jobPostInterviewLocationLng: interviewLng,
                    reviewApplications: reviewApplication,
                    jobPostAddressBuildingNo: addressBuildingNo,
                    jobPostAddressLandmark: addressLandmark,
                    resumeApplicationDate: jobPostResumeDate
                };

                $('#saveButton').prop('disabled', true);
                $.ajax({
                    type: "POST",
                    url: "/addJobPost",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataAddJobPost,
                    error: function (jqXHR, exception) {
                        $('#saveButton').prop('disabled', false);
                        notifyError("Something went wrong. Please try again later", 'danger');
                    }
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }); // end of submit
}); // end of function

function validateContactUnlockCreditValues(){
    var statusCheck = 1;
    if($("#candidateContactCreditAmount").val() == ""){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Please enter the amount paid by the candidate for candidate contact unlock credits!");
    } else if($("#candidateContactCreditUnitPrice").val() == ""){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Please enter the candidate contact unlock credit unit price!");
    } else if(!isValidSalary($("#candidateContactCreditAmount").val())){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Please enter a valid contact unlock credit amount!");
    } else if(!isValidSalary($("#candidateContactCreditUnitPrice").val())){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Please enter a valid contact unlock credit unit price!");
    } else if(parseInt($("#candidateContactCreditAmount").val()) < 0){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Contact unlock amount price cannot be negative!");
    } else if(parseInt($("#candidateContactCreditUnitPrice").val()) < 0){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Contact unlock unit price cannot be negative!");
    } else if(parseInt($("#candidateContactCreditUnitPrice").val()) > parseInt($("#candidateContactCreditAmount").val())){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Contact unlock credit amount should be greater than its credit unit price!");
    }
    return statusCheck;
}

function validateInterviewUnlockCreditValues(){
    var statusCheck = 1;
    if($("#interviewCreditAmount").val() == ""){
        statusCheck = 0;
        interviewCreditTypeStatus = 0;
        notifyError("Please enter the amount paid by the candidate for interview unlock credits!");
    } else if($("#interviewCreditUnitPrice").val() == ""){
        statusCheck = 0;
        interviewCreditTypeStatus = 0;
        notifyError("Please enter the interview unlock credit unit price!");
    } else if(!isValidSalary($("#interviewCreditAmount").val())){
        statusCheck = 0;
        interviewCreditTypeStatus = 0;
        notifyError("Please enter a valid interview unlock credit amount!");
    } else if(!isValidSalary($("#interviewCreditUnitPrice").val())){
        statusCheck = 0;
        interviewCreditTypeStatus = 0;
        notifyError("Please enter a valid interview unlock credit unit price!");
    } else if(parseInt($("#interviewCreditAmount").val()) < 0){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Interview unlock amount price cannot be negative!");
    } else if(parseInt($("#interviewCreditUnitPrice").val()) < 0){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Interview unlock unit price cannot be negative!");
    } else if(parseInt($("#interviewCreditUnitPrice").val()) > parseInt($("#interviewCreditAmount").val())){
        statusCheck = 0;
        interviewCreditTypeStatus = 0;
        notifyError("Interview unlock credit amount should be greater than its credit unit price!");
    }
    return statusCheck;
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

