/**
 * Created by batcoder1 on 29/6/16.
 */


function processDataAddJobPost(returnedData) {
    console.log(returnedData);
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
                    "entry.518884370": returnedData.jobPost.jobPostShift.timeShiftName,
                    "entry.1610465251": returnedData.jobPost.jobPostDescription,
                    "entry.839049104": returnedData.jobPost.jobPostMinRequirement,
                    "entry.988939191": returnedData.jobPost.jobPostAddress,
                    "entry.731772103": returnedData.jobPost.jobPostVacancies,
                    "entry.599645579": returnedData.jobPost.pricingPlanType.pricingPlanTypeName
                },
                type: "POST",
                dataType: "xml",
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
        alert("Job Post Created Successfully");
    } else{
        alert("Job Post Updated Successfully");

    }
    window.close();
}

// job_post_form ajax script
$(function() {
    $("#job_post_form").submit(function(eventObj) {
        eventObj.preventDefault();
        var jobPostLocalities = [];
        var status = 1;
        var locality = $('#jobPostLocalities').val().split(",");
        if($("#jobPostCompany").val() == ""){
            alert("Please enter Job Post Company");
            status = 0;
        } else if($("#jobPostTitle").val() == ""){
            alert("Please enter Job Post Title");
            status = 0;
        } else if($("#jobPostMinSalary").val() == "0"){
            alert("Please enter Job Post Minimum salary");
            status = 0;
        } else if($("#jobPostJobRole").val() == ""){
            alert("Please enter job roles");
            status = 0;
        }
        else if(locality == ""){
            alert("Please enter localities");
            status = 0;
        } else if($("#jobPostExperience").val() == ""){
            alert("Please enter Job Post Experience required");
            status = 0;
        }
        if(status == 1){
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
                    jobPostRecruiterId: $("#jobPostRecruiter").val()
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