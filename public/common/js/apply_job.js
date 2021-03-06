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
        document.getElementById('apply_btn_' + jobPostId).style.pointerEvents = 'auto';
    } catch (e){}

    if(returnedData.status == 1) {
        if(returnedData.isCandidateDeActive == true && returnedData.deActiveBodyMessage != null) {
            $("#messagePromptModal").modal("show");
            $('body').addClass('open-modal');

            $('#customMsgIcon').attr('src', "/assets/partner/img/wrong.png");
            $("#customMsg").html(returnedData.deActiveBodyMessage);

            try{
                var applyBtn = $('.jobApplyBtnV2');
                applyBtn.css("background", "#ffa726");
                applyBtn.attr('onclick','').unbind('click');

            } catch(err){
                console.log(err);
            }
            return;
        }
        //$('#customMsgIcon').attr('src', "/assets/common/img/jobApplied.png");
        //$("#customMsg").html("Your Job Application is Successful");
        // $.notify("Job Application successfully applied.", 'success');
        try{
            $(".jobApplyBtnV2").addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied");
            $('.jobApplyBtnV2').attr('onclick','').unbind('click');

            document.getElementById('apply_btn_' + jobPostId).style.pointerEvents = 'none';
        } catch(err){
            console.log(err);
        }
        // generate prescreen modal here
        if(isPartner) {
            openPartnerPreScreenModal(jobPostId, candidateId);
        } else {
            openCandidatePreScreenModal(jobPostId, localStorage.getItem("mobile"));
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
    } else if(returnedData.status == 6){
        $("#messagePromptModal").modal("show");
        $('body').addClass('open-modal');

        $('#customMsgIcon').attr('src', "/assets/common/img/warning.svg");
        $("#customMsg").html("Sorry! this job post is not accepting any more applications this week. Please check back again next week");
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
        /*if(triggerModal){
                openCandidatePreScreenModal(id, localStorage.getItem("mobile"));
                interviewButtonCondition(id);
            /!*getAssessmentQuestions(null, id);*!/
        // }*/
        if($('#applyButton')!=null){
            $('#applyButton').attr('disabled', true).html("Applying...");

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