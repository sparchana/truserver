/**
 * Created by batcoder1 on 29/6/16.
 */

var recId = 0;

function processDataAddJobPost(returnedData) {
    if(returnedData.status == 1){
        var jobPostLocalities = "";
        var jobPostSalary = "";
        var localities = returnedData.jobPost.jobPostToLocalityList;
        localities.forEach(function (locality) {
           jobPostLocalities += locality.locality.localityName + ", ";
        });

        if(returnedData.jobPost.jobPostMaxSalary == 0){
            jobPostSalary = returnedData.jobPost.jobPostMinSalary;
        } else{
            jobPostSalary = returnedData.jobPost.jobPostMinSalary + " - " + returnedData.jobPost.jobPostMaxSalary;
        }

        var timeShift = "";
        var pricingPlan = "";
        if(returnedData.jobPost.jobPostShift != null){
            timeShift = returnedData.jobPost.jobPostShift.timeShiftName;
        }
        if(returnedData.jobPost.pricingPlanType != null){
            pricingPlan = returnedData.jobPost.pricingPlanType.pricingPlanTypeName;
        }
        try {
            $.ajax({
                url: returnedData.formUrl,
                data: {
                    "entry.790894440": returnedData.jobPost.jobPostId, //jobId
                    "entry.682057856": returnedData.jobPost.company.companyName,
                    "entry.121610050": returnedData.jobPost.jobRole.jobName,
                    "entry.349225135": returnedData.jobPost.recruiterProfile.recruiterProfileName,
                    "entry.243172250": returnedData.jobPost.recruiterProfile.recruiterProfileMobile,
                    "entry.1348583202": returnedData.jobPost.recruiterProfile.recruiterProfileEmail,
                    "entry.499293401": jobPostLocalities,
                    "entry.1169285578": jobPostSalary,
                    "entry.156865881": returnedData.jobPost.jobPostIncentives,
                    "entry.518884370": timeShift,
                    "entry.1610465251": returnedData.jobPost.jobPostDescription,
                    "entry.839049104": returnedData.jobPost.jobPostMinRequirement,
                    "entry.988939191": returnedData.jobPost.jobPostAddress,
                    "entry.731772103": returnedData.jobPost.jobPostVacancies,
                    "entry.599645579": pricingPlan
                },
                type: "POST",
                dataType: "xml"
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
        alert("Job Post Created Successfully");
        window.close();
    } else{
        alert("Job Post Updated Successfully");
        window.close();
    }
}

function processDataAddRecruiterAndUpdateRecId(returnedData) {
    recId = returnedData.recruiterId;
}

// job_post_form ajax script
$(function() {
    $("#job_post_form").submit(function(eventObj) {
        eventObj.preventDefault();
        if($("#jobPostRecruiter").val() == "" && $("#recruiterSection").is(':visible') == true){
            var status = 1;
            var recruiterName = validateName($("#recruiterName").val());
            var recruiterMobile = validateMobile($("#recruiterMobile").val());
            //checking first name
            switch(recruiterName){
                case 0: alert("First name contains number. Please Enter a valid First Name"); status=0; break;
                case 2: alert("First Name cannot be blank spaces. Enter a valid first name"); status=0; break;
                case 3: alert("First name contains special symbols. Enter a valid first name"); status=0; break;
                case 4: alert("Please enter your first name"); status=0; break;
            }
            if(recruiterMobile == 0){
                alert("Enter a valid mobile number");
                status=0;
            } else if(recruiterMobile == 1){
                alert("Enter 10 digit mobile number");
                status=0;
            } else if(recruiterMobile == "") {
                alert("Please Enter recruiter Contact");
                status=0;
            }
            if(status == 1){
                try{
                    var rec = {
                        recruiterName: $("#recruiterName").val(),
                        recruiterMobile: $("#recruiterMobile").val(),
                        recruiterLandline: $("#recruiterLandline").val(),
                        recruiterEmail: $("#recruiterEmail").val(),
                        recruiterCompany: $("#jobPostCompany").val()
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
        }
        var timeSlotCount = 0;
        var interviewDayCount = 0;
        $('#interviewTimeSlot input:checkbox').each(function () {
            if ($(this).is(':checked')) {
                timeSlotCount =+ 1;
            }
        });
        for(i=1;i<=7;i++) {
            if ($("#interview_day_" + i).is(":checked")) {
                interviewDayCount =+ 1;
            }
        }

        var minSalary = $("#jobPostMinSalary").val();
        var maxSalary = $("#jobPostMaxSalary").val();

        var partnerInterviewIncentiveVal = parseInt($("#partnerInterviewIncentive").val());
        var partnerJoiningIncentiveVal = parseInt($("#partnerJoiningIncentive").val());

        var jobPostLocalities = [];
        status = 1;
        var locality = $('#jobPostLocalities').val().split(",");
        if($("#jobPostCompany").val() == ""){
            alert("Please enter Job Post Company");
            $("#jobPostCompany").addClass('selectDropdownInvalid').removeClass('selectDropdown');
            status = 0;
        } else if($("#jobPostRecruiter").val() == "" && recId == 0){
            alert("Please select a recruiter");
            $("#jobPostCompany").addClass('selectDropdown').removeClass('selectDropdownInvalid');
            $("#jobPostRecruiter").addClass('selectDropdownInvalid').removeClass('selectDropdown');
            status = 0;
        } else if($("#jobPostTitle").val() == ""){
            $("#jobPostRecruiter").addClass('selectDropdown').removeClass('selectDropdownInvalid');
            alert("Please enter Job Post Title");
            $("#jobPostTitle").addClass('invalid');
            status = 0;
        } else if($("#jobPostMinSalary").val() == "0"){
            alert("Please enter Job Post Minimum salary");
            $("#jobPostTitle").removeClass('invalid');
            $("#jobPostMinSalary").addClass('invalid');
            status = 0;
        } else if(isValidSalary(minSalary) == false){
            alert("Please enter valid min salary");
            $("#jobPostMinSalary").removeClass('invalid');
            $("#jobPostMinSalary").addClass('invalid');
            status = 0;
        } else if(maxSalary != 0 && (isValidSalary(maxSalary) == false)){
            alert("Please enter valid max salary");
            $("#jobPostMinSalary").removeClass('invalid');
            $("#jobPostMaxSalary").addClass('invalid');
            status = 0;
        } else if(maxSalary != 0 && (maxSalary <= minSalary)){
            alert("Max salary should be greated than min salary");
            $("#jobPostMaxSalary").removeClass('invalid');
            $("#jobPostMaxSalary").addClass('invalid');
            status = 0;
        } else if($("#jobPostJobRole").val() == ""){
            $("#jobPostMinSalary").removeClass('invalid');
            alert("Please enter job roles");
            $("#jobPostJobRole").addClass('invalid');
            status = 0;
        } else if($("#jobPostVacancies").val() == "" || $("#jobPostVacancies").val() == 0){
            alert("Please enter no. of vacancies");
            $("#jobPostJobRole").removeClass('invalid');
            $("#jobPostVacancies").addClass('invalid');
            status = 0;
        } else if($("#jobPostStartTime").val() != -1){
            if($("#jobPostEndTime").val() != -1){
                if(parseInt($("#jobPostStartTime").val()) >= parseInt($("#jobPostEndTime").val())){
                    alert("Start time cannot be more than end time");
                    status = 0;
                }
            } else{
                alert("Please select job end time");
                status = 0;
            }
        } else if(locality == ""){
            $("#jobPostVacancies").removeClass('invalid');
            $("#jobPostLocalities").addClass('invalid');
            alert("Please enter localities");
            status = 0;
        } else if($("#jobPostExperience").val() == ""){
            $("#jobPostLocalities").removeClass('invalid');
            $("#jobPostExperience").addClass('selectDropdownInvalid').removeClass('selectDropdown');
            alert("Please enter Job Post Experience required");
            status = 0;
        }
        if(interviewDayCount > 0 && timeSlotCount == 0){
            $("#jobPostExperience").removeClass('invalid');
            alert("Please select interview time slot");
            status = 0;
        } else if(timeSlotCount > 0 && interviewDayCount == 0){
            alert("Please select interview days");
            status = 0;
        }
        
        //checking partner incentives
        if (partnerInterviewIncentiveVal < 0) {
            alert("Partner interview incentive cannot be negative");
            status = 0;
        }
        else if (partnerJoiningIncentiveVal < 0) {
            alert("Partner joining incentive cannot be negative");
            status = 0;
        } else if (partnerJoiningIncentiveVal < partnerInterviewIncentiveVal){
            alert("Partner interview incentive cannot be greater than partner joining incentive");
            status = 0;
        }

        if(status == 1){
            if($("#jobPostRecruiter").val() != ""){
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
                    interviewTimeSlot: slotArray
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

    }); // end of submit
}); // end of function