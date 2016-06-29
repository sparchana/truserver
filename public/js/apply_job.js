/**
 * Created by batcoder1 on 17/6/16.
 */

var companyName;
var jobRoleName;
var candidateId;
var candidateName;
var candidateGender;
var totalExp;
var isEmployed;
var isAssessed;
var languagesKnown = "";
var candidateSkills = "";
var jobPref = "";
var localityPref = "";
var salary;
var education;
var motherTongue;
var homeLocality;

function processDataApplyJob(returnedData) {
    $("#messagePromptModal").modal("show");
    if(returnedData.status == 1){
        postToGoogle();
        $('#customMsgIcon').attr('src', "/assets/img/jobApplied.png");
        $("#customMsg").html("Your Job Application is Successful");
        try{
            $("#" + applyJobId).addClass("appliedBtn").removeClass("btn-primary");
            $("#" + applyJobId).prop('disabled',true);
            $("#apply_btn_" + applyJobId).html("Already Applied");
        } catch(err){
            console.log(err);
        }
    } else if(returnedData.status == 2){
        $('#customMsgIcon').attr('src', "/assets/img/jobApplied.png");
        $("#customMsg").html("Oops! Something went Wrong. Unable to apply");
    } else if(returnedData.status == 3){
        $('#customMsgIcon').attr('src', "/assets/img/alreadyApplied.png");
        $("#customMsg").html("Looks like you already applied for this Job. Click My Jobs to view your applied Jobs");
    } else if(returnedData.status == 4){
        $('#customMsgIcon').attr('src', "/assets/img/logo.gif");
        $("#customMsg").html("Oops! Candidate does't Exists");
    } else{
        $('#customMsgIcon').attr('src', "/assets/img/logo.gif");
        $("#customMsg").html("Oops! Looks like the job is no longer available");
    }
}

function processDataAddJobPost(returnedData) {
    alert("Job Post added Successfully");
    window.location = "/support/addCompany"
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

            try {
                var d = {
                    jobPostMinSalary: $("#jobPostMinSalary").val(),
                    jobPostMaxSalary: $("#jobPostMaxSalary").val(),
                    jobPostStartTime: parseInt($("#jobPostStartTime").val()),
                    jobPostEndTime: parseInt($("#jobPostEndTime").val()),
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
                    jobPostEducationId: $("#jobPostEducation").val(),
                    jobPostStatusId: $("#jobPostStatus").val(),
                    pricingPlanTypeId: 1,
                    jobPostExperienceId: $("#jobPostExperience").val()
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

// apply_job ajax script
function applyJob(id){
    applyJobFlag = 1;
    applyJobId = id;
    var phone = localStorage.getItem("mobile");
    if(phone == null){ // not logged in
        openLogin();
        $("#myLoginModal").modal("show");
        $("#signInPopup").html("Sign In to Apply");
    } else{
        try {
            var d = {
                jobId: id,
                candidateMobile: phone
            };
            $.ajax({
                type: "POST",
                url: "/applyJob",
                async: false,
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataApplyJob
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
} // end of submit

function postToGoogle() {
    try {
        $.ajax({
            type: "GET",
            url: "/getJobApplicationDetailsForGoogleSheet/" + applyJobId,
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetJobGoogleSheetDetails
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataGetJobGoogleSheetDetails(returnedData) {
    if(returnedData.candidateGender != null)
        candidateGender = ((returnedData.candidateGender == 0) ? "Male" : "Female");
    if(returnedData.candidateIsEmployed != null)
        isEmployed = ((returnedData.candidateIsEmployed == 0) ? "No" : "Yes");
    if(returnedData.candidateIsAssessed != null)
        isAssessed = ((returnedData.candidateIsAssessed == 0) ? "No" : "Yes");

    var value = returnedData.candidateCreationTimestamp;
    var dateTime = new Date(value).toLocaleDateString() +" "+ new Date(value).getHours() +":"+new Date(value).getMinutes()+":"+new Date(value).getSeconds();
    try {
        $.ajax({
/*            url: "www.test.com",*/
            url: "https://docs.google.com/forms/d/1NIGQC5jmSDuQaGUF0Jw1UG-Dz_3huFtZf9Bo7ncPl4g/formResponse",
            data: {
                "entry.1388755113": applyJobId, //jobId
                "entry.1115234203": ((returnedData.companyName != null) ? returnedData.companyName : ""),
                "entry.1422779518": ((returnedData.jobRoleName != null) ? returnedData.jobRoleName : ""),
                "entry.942294281": ((returnedData.candidateLeadId != null) ? returnedData.candidateLeadId : ""),
                "entry.1345077393": ((returnedData.candidateName != null) ? returnedData.candidateName : ""),
                "entry.1859090779": ((returnedData.candidateMobile != null) ? returnedData.candidateMobile : ""),
                "entry.2079461892": candidateGender,
                "entry.2071290015": ((returnedData.candidateTotalExp != null) ? returnedData.candidateTotalExp : ""),
                "entry.179139422": isEmployed,
                "entry.1488146275": isAssessed,
                "entry.67497584": ((returnedData.languageKnown != null) ? returnedData.languageKnown : ""),
                "entry.441069988": ((returnedData.candidateMotherTongue != null) ? returnedData.candidateMotherTongue : ""),
                "entry.1350761294": ((returnedData.candidateHomeLocality != null) ? returnedData.candidateHomeLocality : ""),
                "entry.2057814300": ((returnedData.candidateLocalityPref != null) ? returnedData.candidateLocalityPref : ""),
                "entry.598773915": ((returnedData.candidateJobPref != null) ? returnedData.candidateJobPref : ""),
                "entry.125850326": ((returnedData.candidateCurrentSalary != null) ? returnedData.candidateCurrentSalary : ""),
                "entry.240702722": ((returnedData.candidateEducation != null) ? returnedData.candidateEducation : ""),
                "entry.190053755": ((returnedData.candidateSkill != null) ? returnedData.candidateSkill : ""),
                "entry.971982828": dateTime
            },
            type: "POST",
            dataType: "xml",
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}