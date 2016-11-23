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

function processDataApplyJob(returnedData, jobPostId, candidateId, isPartner) {
    $("#jobApplyConfirm").modal("hide");

    // hiding below divs in partner page
    try{
        $("#candidateOtp").hide();
        $("#verifyOtp").hide();
    } catch (e){}

    // enabling apply btn in partner
    try{
        $("#applyButton").addClass("jobApplyBtnModal").removeClass("jobApplied").prop('disabled',false).html("Apply");
    } catch (e){}

    if(returnedData.status == 1){
        //$('#customMsgIcon').attr('src', "/assets/common/img/jobApplied.png");
        //$("#customMsg").html("Your Job Application is Successful");
        $.notify("Job Application successfully applied.", 'success');
        try{
            $(".jobApplyBtnV2").addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied");
            $('.jobApplyBtnV2').attr('onclick','').unbind('click');
        } catch(err){
            console.log(err);
        }
        // generate prescreen modal here
        if(!isPartner){
            $.notify("Please complete Job Application form", 'success');
            openCandidatePreScreenModal(jobPostId, localStorage.getItem("mobile"));
            console.log("success: generate modal");
        } else {
            $.notify("Please complete Job Application form", 'success');
            openPartnerPreScreenModal(jobPostId, candidateId);
        }
    } else if(returnedData.status == 2){
        $("#messagePromptModal").modal("show");
        $('body').addClass('open-modal');

        $('#customMsgIcon').attr('src', "/assets/common/img/jobApplied.png");
        $("#customMsg").html("Oops! Something went Wrong. Unable to apply");
    } else if(returnedData.status == 3) {
        $("#messagePromptModal").modal("show");
        $('body').addClass('open-modal');

        $('#customMsgIcon').attr('src', "/assets/common/img/alreadyApplied.png");
        $("#customMsg").html("Looks like you already applied for this Job. Click My Jobs to view your applied Jobs");
        try{
            $(".jobApplyBtnV2").addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied");
        } catch(err){
            console.log(err);
        }
    } else if(returnedData.status == 4){
        $("#messagePromptModal").modal("show");
        $('body').addClass('open-modal');

        $('#customMsgIcon').attr('src', "/assets/common/img/logo.gif");
        $("#customMsg").html("Oops! Candidate does't Exists");
    } else{
        $("#messagePromptModal").modal("show");
        $('body').addClass('open-modal');

        $('#customMsgIcon').attr('src', "/assets/common/img/logo.gif");
        $("#customMsg").html("Oops! Looks like the job is no longer available");
    }
}

// apply_job ajax script
function applyJobSubmitViaCandidate(id, localityId, prefTimeSlot, scheduledInterviewDate, triggerModal){
    applyJobFlag = 1;
    applyJobId = id;
    var phone = localStorage.getItem("mobile");

    if(phone == null){ // not logged in
        $("#jobApplyConfirm").modal("hide");
        openLogin();
        $("#myLoginModal").modal("show");
        $("#signInPopup").html("Sign In to Apply");
    } else{
        console.log("shouldTriggerModal: "+triggerModal);
        if(triggerModal){
            console.log("opening prescreen modal for : " + id + " candidate: " + localStorage.getItem("mobile"));
                openCandidatePreScreenModal(id, localStorage.getItem("mobile"));
                interviewButtonCondition(id);
            /*getAssessmentQuestions(null, id);*/
        };
        if($('#applyButton')!=null){
            $('#applyButton').attr('disabled', true);
        }
        applyJobSubmit(id, localStorage.getItem("candidateId"), phone, localityId, null, null, false);
    }
} // end of submit

function applyJobSubmit(jobPostId, candidateId, phone, localityId, prefTimeSlot, scheduledInterviewDate, isPartner) {
    try {
        var partner = false;
        var prefTimeSlotVal;
        var scheduledInterviewDateVal;
        // TODO: remove interview_schedule and interview-slot as now there exists a interview js module
        if(isPartner){
            partner = true;
            prefTimeSlotVal = prefTimeSlot;
            scheduledInterviewDateVal = scheduledInterviewDate;
        } else {
            prefTimeSlotVal = null;
            scheduledInterviewDateVal = null;
        }
        var d = {
            jobId: jobPostId,
            candidateMobile: phone,
            localityId: localityId,
            timeSlot: prefTimeSlot,
            scheduledInterviewDate: scheduledInterviewDate,
            isPartner: isPartner
        };
        $.ajax({
            type: "POST",
            url: "/applyJob",
            async: false,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: function (returnedData) {
                processDataApplyJob(returnedData, jobPostId, candidateId, isPartner);
            }
        });

    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}