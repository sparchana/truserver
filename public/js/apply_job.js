/**
 * Created by batcoder1 on 17/6/16.
 */

var companyName;
var candidateId;
var candidateGender;
var isEmployed;
var isAssessed;
var salary;
var education;
var homeLocality;

function processDataApplyJob(returnedData) {
    $("#messagePromptModal").modal("show");
    $("#jobApplyConfirm").modal("hide");
    if(returnedData.status == 1){
        postToGoogle();
        $('#customMsgIcon').attr('src', "/assets/img/jobApplied.png");
        $("#customMsg").html("Your Job Application is Successful");
        try{
            $("#apply_btn_" + applyJobId).addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Already Applied");
            $("#applyBtnDiv_" + applyJobId).prop('disabled',true);
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

// apply_job ajax script
function applyJob(id, localityId){
    $("#applyButton").addClass("appliedBtn").removeClass("jobApplyBtnModal").prop('disabled',true).html("Applying");
    applyJobFlag = 1;
    applyJobId = id;
    var phone = localStorage.getItem("mobile");
    if(phone == null){ // not logged in
        $("#jobApplyConfirm").modal("hide");
        openLogin();
        $("#myLoginModal").modal("show");
        $("#signInPopup").html("Sign In to Apply");
    } else{
        try {
            var d = {
                jobId: id,
                candidateMobile: phone,
                localityId: localityId
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
    var candidateCreateTimestamp = new Date(value).toLocaleDateString() +" "+ new Date(value).getHours() +":"+new Date(value).getMinutes()+":"+new Date(value).getSeconds();
    value = returnedData.candidateExpiryDate;
    var candidateExpiryDate;
    if(value != null){
        candidateExpiryDate = new Date(value).toLocaleDateString();
    } else{
        candidateExpiryDate = "-";
    }

    var formUrl = returnedData.formUrl;
    var totalExperienceInYrs = "";
    var totalExperience = returnedData.candidateTotalExp;
    if(totalExperience != null || totalExperience != undefined){
        totalExperienceInYrs = getInYearMonthFormat(totalExperience);
    }
    try {
        $.ajax({
            url: formUrl,
            data: {
                "entry.1388755113": applyJobId, //jobId
                "entry.1115234203": ((returnedData.companyName != null) ? returnedData.companyName : ""),
                "entry.1422779518": ((returnedData.jobRoleName != null) ? returnedData.jobRoleName : ""),
                "entry.942294281": ((returnedData.candidateLeadId != null) ? returnedData.candidateLeadId : ""),
                "entry.1345077393": ((returnedData.candidateName != null) ? returnedData.candidateName : ""),
                "entry.1859090779": ((returnedData.candidateMobile != null) ? returnedData.candidateMobile : ""),
                "entry.2079461892": candidateGender,
                "entry.2071290015": totalExperienceInYrs,
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
                "entry.971982828": candidateCreateTimestamp,
                "entry.98308337": prefLocationName,
                "entry.46689276": returnedData.candidateProfileStatus,
                "entry.1180627971": candidateExpiryDate
            },
            type: "POST",
            dataType: "xml",
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function getInYearMonthFormat(d){
    if(d == null) {
        return "-";
    } else {
        var totalYear = Math.round((parseInt(d)/12)*100)/100;
        return totalYear;
    }
}
