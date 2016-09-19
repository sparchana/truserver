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
        $('#customMsgIcon').attr('src', "/assets/common/img/jobApplied.png");
        $("#customMsg").html("Your Job Application is Successful");
        try{
            $("#apply_btn_" + applyJobId).addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Already Applied");
            $("#applyBtnDiv_" + applyJobId).prop('disabled',true);
        } catch(err){
            console.log(err);
        }
    } else if(returnedData.status == 2){
        $('#customMsgIcon').attr('src', "/assets/common/img/jobApplied.png");
        $("#customMsg").html("Oops! Something went Wrong. Unable to apply");
    } else if(returnedData.status == 3){
        $('#customMsgIcon').attr('src', "/assets/common/img/alreadyApplied.png");
        $("#customMsg").html("Looks like you already applied for this Job. Click My Jobs to view your applied Jobs");
    } else if(returnedData.status == 4){
        $('#customMsgIcon').attr('src', "/assets/common/img/logo.gif");
        $("#customMsg").html("Oops! Candidate does't Exists");
    } else{
        $('#customMsgIcon').attr('src', "/assets/common/img/logo.gif");
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