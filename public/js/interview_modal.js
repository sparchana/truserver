/* Global Constant */
var INTERVIEW_ERROR = 0;
var INTERVIEW_NOT_REQUIRED = 1; // "OK"
var INTERVIEW_REQUIRED = 2;     // "INTERVIEW"

var jobPostInfo;

var jpTitle;
var compName;
var isPartnerVal;
function processJobPostInterviewSlot(returnedData, isSupport) {

    jobPostInfo = returnedData.jobPost;
    if(jobPostInfo.interviewDetailsList == null || jobPostInfo.interviewDetailsList.length == 0) {
        $('body').removeClass('open-interview-selector-modal');
        bootbox.hideAll();

        var showModal = true;
        if(jobPostInfo.recruiterProfile != null){

            if(jobPostInfo.recruiterProfile.contactCreditCount > 0){
                showModal = true;
            }

            if(jobPostInfo.recruiterProfile.interviewCreditCount > 0){
                showModal = false;
            }
        }  else{
            showModal = false;
        }

        if(showModal){
            $("#postApplyMsg").html("We have received your job application. You can contact the recruiter directly on " +
                jobPostInfo.recruiterProfile.recruiterProfileMobile);

            $("#confirmationModal").modal("show");
        }

        nfy("Submitted Successfully.", 'success');
        return;
    }

    // document.getElementById("applyJobCandidateName").innerHTML = candidateInfo.candidateFirstName;
    jpTitle = jobPostInfo.jobPostTitle;
    compName = jobPostInfo.company.companyName;
    $("#jobTitle").html(jobPostInfo.jobPostTitle);
    $("#compName").html(jobPostInfo.company.companyName);

    if (returnedData.interviewSlotMap!= null && Object.keys(returnedData.interviewSlotMap).length > 0) {
        //slots
        $('#interViewSlot').html('');



        $.each( returnedData.interviewSlotMap, function( key, value ) {
            var slotValue = value.interviewDateMillis +"_"+value.interviewTimeSlot.slotId;
            var defaultOption = $('<option value="'+slotValue+'"></option>').text(key);
            $('#interViewSlot').append(defaultOption);
        });

        // loop into this interviewSlotMap
        $('#interViewSection').show();
    } else{
        $('#interViewSection').hide();
    }
}

/* TODO remove below mentions 3 methods */
function getDayVal(month){
    switch(month) {
        case 0:
            return "Sun";
            break;
        case 1:
            return "Mon";
            break;
        case 2:
            return "Tue";
            break;
        case 3:
            return "Wed";
            break;
        case 4:
            return "Thu";
            break;
        case 5:
            return "Fri";
            break;
        case 6:
            return "Sat";
            break;
    }
}

function getMonthVal(month){
    switch(month) {
        case 1:
            return "Jan";
            break;
        case 2:
            return "Feb";
            break;
        case 3:
            return "Mar";
            break;
        case 4:
            return "Apr";
            break;
        case 5:
            return "May";
            break;
        case 6:
            return "Jun";
            break;
        case 7:
            return "Jul";
            break;
        case 8:
            return "Aug";
            break;
        case 9:
            return "Sep";
            break;
        case 10:
            return "Oct";
            break;
        case 11:
            return "Nov";
            break;
        case 12:
            return "Dec";
            break;
    }
}

function checkSlotAvailability(x, interviewDays) {
    if(x.getDay() == 1 && interviewDays.charAt(0) == '1'){ // monday
        return true;
    } else if(x.getDay() == 2 && interviewDays.charAt(1) == '1'){ //tue
        return true;
    } else if(x.getDay() == 3 && interviewDays.charAt(2) == '1'){ //wed
        return true;
    } else if(x.getDay() == 4 && interviewDays.charAt(3) == '1'){ //thu
        return true;
    } else if(x.getDay() == 5 && interviewDays.charAt(4) == '1'){ //fri
        return true;
    } else if(x.getDay() == 6 && interviewDays.charAt(5) == '1'){ //sat
        return true;
    } else if(x.getDay() == 0 && interviewDays.charAt(6) == '1'){ //sun
        return true;
    }
}


function initInterviewModal(candidateId, jobPostId, isSupport, isPartner) {
    isPartnerVal = isPartner;
    console.log("interview Modal init");
    var htmlBodyContent = ''+
        '<div id="confirmationMsg">'+
        '<center>'+
        '<img src="/assets/common/img/jobApply.png" width="48px">'+
        '</center>'+
        '<center>'+
        '<h6> For <b><div style="display: inline-block" id="jobTitle"></div></b>'+
        ' job at <b><div style="display: inline-block" id="compName"></div></b>'+
        '.</h6>'+
        '<h6 style="margin-top: 16px">Please select the interview date and time</h6>'+
        '<div class="materialDash"></div>'+
        '<div class="interview_container" style="margin-top: 16px">'+
        '<div class="row" style="margin-top: 4px;text-align: center" id="interViewSection">'+
        '<div class="col-md-12">'+
        '<center><select id="interViewSlot" class="selectDropdown" style="width: 210px;padding:1%;margin-top: 8px"></select></center>'+
        '</div>'+
        '</div>'+
        '</div>'+
        '</center>'+
        '</div>';
    var title = "Last step! Select your interview time slot";

    generateInterviewSlotModal(title, htmlBodyContent, candidateId, jobPostId);

    try {
        $.ajax({
            type: "POST",
            url: "/getInterviewSlots/" + jobPostId,
            data: false,
            contentType: false,
            processData: false,
            success: function (returnedData) {
                processJobPostInterviewSlot(returnedData, isSupport);
            }
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function generateInterviewSlotModal(title, message, candidateId, jobPostId) {
    var interviewDialog = bootbox.dialog({
        className: "interview-slot-selector-modal",
        title: title,
        message: message,
        closeButton: true,
        animate: true,
        onEscape: function() {
            $('body').removeClass('open-interview-selector-modal');
            if(window.location.pathname == "/recruiter/jobPostTrack/" + jobPostId){

            } else{
                if(isPartnerVal == false || isPartnerVal == null ){
                    nfy("Submitted successfully. Refreshing page.", 'success');

                    setTimeout(function () {
                        if(window.location.pathname == "/dashboard/appliedJobs/"){
                            window.location.href = "/dashboard/appliedJobs/";
                        } else {
                            location.reload();
                        }
                        // window.location = response.redirectUrl + app.jpId + "/?view=" + response.nextView;
                    }, 2000);
                } else{
                    nfy("Submitted successfully.", 'success');
                }
            }
        },
        buttons: {
            "Submit": {
                id:"interviewModalBtn",
                className: "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent interview-selector-submit",
                callback: function () {
                    /*$(".btn.interview-selector-submit").prop('disabled', true);*/
                    finalInterviewSlotSubmission(candidateId, jobPostId);
                }
            }
        }
    });
    interviewDialog.attr("id", "interview-selector-modal");

    /*$("#interViewSlot").click(function (){
        if($("#interViewSlot").val() != -1){
            $(".btn.interview-selector-submit").prop('disabled', false);
            $(".btn.interview-selector-submit").css({'background-color':'#09ac58','color':'#ffffff'});
        } else {
            $(".btn.interview-selector-submit").prop('disabled', true);
        }
    });*/
    $(".btn.interview-selector-submit").css({'background-color':'#09ac58','color':'#ffffff'});
    $('#interview-slot-selector-modal div.modal-body').attr('style', 'overflow: visible !important');
    /*$('.btn.interview-selector-submit').prop('disabled', true);*/
    $('body').removeClass('modal-open').removeClass('open-interview-selector-modal').addClass('open-interview-selector-modal');
}

function finalInterviewSlotSubmission(candidateId, jobPostId) {
    if($("#interViewSlot").val() != -1 ){
        var combinedValue = $("#interViewSlot").val().split("_");
        scheduledInterviewDate = combinedValue[0];
        prefTimeSlot = combinedValue[1];

        var d = {
            timeSlot: prefTimeSlot,
            scheduledInterviewDateInMillis: parseInt(scheduledInterviewDate)
        };
        var base_api_url ="/support/api/updateCandidateInterviewDetail/";
        if(base_api_url == null || jobPostId == null) {
            return
        } else {
            base_api_url +="?";
            if(jobPostId != null) {
                base_api_url += "jobPostId=" + jobPostId;
            }
            if(candidateId != null){
                base_api_url += "&candidateId=" + candidateId;
            }
        }
        $.ajax({
            type: "POST",
            url: base_api_url,
            async: false,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processInterviewSubmissionResponse
        });
    }
}
function processInterviewSubmissionResponse(returnData) {
    // window.location = response.redirectUrl + app.jpId + "/?view=" + response.nextView;
    if(returnData == "OK"){

        var showModal = true;
        if(jobPostInfo.recruiterProfile != null){

            if(jobPostInfo.recruiterProfile.contactCreditCount > 0){
                showModal = true;
            }

            if(jobPostInfo.recruiterProfile.interviewCreditCount > 0){
                showModal = false;
            }
        }  else{
            showModal = false;
        }

        if(showModal){
            nfy("Interview Submitted successfully.", 'success');

            $("#postApplyMsg").html("We have received your job application. You can contact the recruiter directly on " +
                jobPostInfo.recruiterProfile.recruiterProfileMobile);

            $("#confirmationModal").modal("show");
        } else {
            if(isPartnerVal == false || isPartnerVal == null ){

                if(window.location.pathname == "/recruiter/jobPostTrack/" + jobPostId){
                    nfy("Interview scheduled!", 'success');
                    hideTab1();
                    hideTab2();
                    hideTab3();

                    tabChange2();
                } else{
                    nfy("Submitted successfully. Refreshing page.", 'success');

                    setTimeout(function () {
                        if(window.location.pathname == "/dashboard/appliedJobs/"){
                            window.location.href = "/dashboard/appliedJobs/";
                        } else {
                            location.reload();
                        }
                    }, 2000);
                }
            } else{
                nfy("Submitted successfully.", 'success');
            }
        }

    } else {
        nfy("Something went wrong. Refreshing page. After refresh please try again.", 'error');
        setTimeout(function () {
            if(window.location.pathname == "/dashboard/appliedJobs/"){
                window.location.href = "/dashboard/appliedJobs/";
            } else {
                location.reload();
            }
        }, 2000);
    }
}

function nfy(msg, style) {
    $.notify(msg, style);
}