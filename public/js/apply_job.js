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
var motherTongue;
var homeLocality;

function processDataApplyJob(returnedData) {
    $("#messagePromptModal").modal("show");
    if(returnedData.status == 1){
        postToGoogle();
        $('#customMsgIcon').attr('src', "/assets/img/jobApplied.png");
        $("#customMsg").html("Job Applied Successfully!");
    } else if(returnedData.status == 2){
        $('#customMsgIcon').attr('src', "/assets/img/jobApplied.png");
        $("#customMsg").html("Oops! Something went Wrong. Unable to apply");
    } else if(returnedData.status == 3){
        $('#customMsgIcon').attr('src', "/assets/img/alreadyApplied.png");
        $("#customMsg").html("Looks like you already applied for this Job");
    } else if(returnedData.status == 4){
        $('#customMsgIcon').attr('src', "/assets/img/logo.gif");
        $("#customMsg").html("Oops! Candidate does't Exists");
    } else{
        $('#customMsgIcon').attr('src', "/assets/img/logo.gif");
        $("#customMsg").html("Oops! Looks like the job is no longer available");
    }
}

function processDataAddJobPost(returnedData) {
    console.log("returnedData :" + returnedData.status);
}

function addJobPost(){
    var startTime = new Date().getTime();
    var jobPostLocalities = [];

    var locality = "5";
    var i;
    for(i=0;i<locality.length; i++){
        jobPostLocalities.push(parseInt(locality[i]));
    }
    try {
        var d = {
            jobPostMinSalary: 20000,
            jobPostMaxSalary: 25000,
            jobPostStartTime: startTime,
            jobPostEndTime: startTime,
            jobPostIsHot: 1,
            jobPostDescription: "This is a test job",
            jobPostTitle: "Delivery boy executive at test",
            jobPostIncentives: "Incentives free text",
            jobPostMinRequirement: "Minimum requirement free text",
            jobPostAddress: "Address of the company",
            jobPostPinCode: 560035,
            jobPostVacancy: 15,
            jobPostDescriptionAudio: "Delivery boy executive at test",
            jobPostWorkFromHome: 1,
            jobPostStatusId: 1,
            pricingPlanTypeId: 1,
            jobPostJobRoleId: 1,
            jobPostCompanyId: 1,
            jobPostShiftId: 1,
            jobPostExperienceId: 1,
            jobPostEducationId: 1,
            jobPostLocalities: jobPostLocalities
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

// apply_job ajax script
function applyJob(id){
    applyJobFlag = 1;
    applyJobId = id;
    var phone = localStorage.getItem("mobile");
    if(phone == null){ // not logged in
        $("#myLoginModal").modal("show");
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

function processDataJobPostDetails(returnedData){
    companyName = returnedData.company.companyName;
    jobRoleName = returnedData.jobPostTitle;
}

function processDataGetCandidateInfo(returnedData) {
    try {
        $.ajax({
            type: "GET",
            url: "/getJobPostInfo/" + applyJobId,
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataJobPostDetails
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    console.log(returnedData.lead.leadId + " =====");
    candidateId = returnedData.lead.leadId;
    candidateName = returnedData.candidateFirstName + " " + returnedData.candidateLastName;
    candidateGender = returnedData.candidateGender;
    totalExp = returnedData.candidateTotalExperience;
    isEmployed = ((returnedData.candidateIsEmployed == 0) ? "No" : "Yes");
    isAssessed = ((returnedData.candidateIsAssessed == 0) ? "No" : "Yes");
    var lang = returnedData.languageKnownList;
    lang.forEach(function (language) {
       languagesKnown += language.language.languageName + ", ";
    });
    motherTongue = returnedData.motherTongue.languageName;
    homeLocality = returnedData.locality;

    try {
        $.ajax({
            url: "https://docs.google.com/forms/d/1NIGQC5jmSDuQaGUF0Jw1UG-Dz_3huFtZf9Bo7ncPl4g/formResponse",
            data: {
                "entry.1388755113": applyJobId, //jobId
                "entry.1115234203": companyName,
                "entry.1422779518": jobRoleName,
                "entry.942294281": candidateId,
                "entry.1345077393": candidateName,
                "entry.1859090779": localStorage.getItem("mobile"),
                "entry.2079461892": candidateGender,
                "entry.2071290015": totalExp,
                "entry.179139422": isEmployed,
                "entry.1488146275": isAssessed,
                "entry.67497584": languagesKnown,
                "entry.441069988": motherTongue,
                "entry.1350761294": homeLocality
            },
            type: "POST",
            dataType: "xml",
            statusCode: {
                0: function() {
                    //Success message
                },
                200: function() {
                    //Success Message
                }
            }
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function postToGoogle() {
    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateInfoDashboard",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetCandidateInfo
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

