
function processJobPostInterviewSlot(returnedData) {
    if(returnedData.interviewDetailsList == null || returnedData.interviewDetailsList.length == 0) {
        $('body').removeClass('open-interview-selector-modal');
        bootbox.hideAll();
        return;
    }
    // document.getElementById("applyJobCandidateName").innerHTML = candidateInfo.candidateFirstName;
    $("#jobNameConfirmation").html(returnedData.jobPostTitle);
    $("#companyNameConfirmation").html(returnedData.company.companyName);
    /*var i;
    $('#jobLocality').html('');
    var defaultOption = $('<option value="-1"></option>').text("Select Preferred Location");
    $('#jobLocality').append(defaultOption);
    var jobLocality = returnedData.jobPostToLocalityList;
    jobLocality.forEach(function (locality) {
        var item = {};
        item ["id"] = locality.locality.localityId;
        item ["name"] = " " + locality.locality.localityName;
        jobLocalityArray.push(item);
        var option = $('<option value=' + locality.locality.localityId + '></option>').text(locality.locality.localityName);
        $('#jobLocality').append(option);
    });*/
    if (Object.keys(returnedData.interviewDetailsList).length > 0) {
        //slots
        $('#interViewSlot').html('');
        var defaultOption = $('<option value="-1"></option>').text("Select Time Slot");
        $('#interViewSlot').append(defaultOption);

        var interviewDetailsList = returnedData.interviewDetailsList;
        if (interviewDetailsList[0].interviewDays != null) {
            var interviewDays = interviewDetailsList[0].interviewDays.toString(2);

            /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
            if (interviewDays.length != 7) {
                x = 7 - interviewDays.length;
                var modifiedInterviewDays = "";

                for (i = 0; i < x; i++) {
                    modifiedInterviewDays += "0";
                }
                modifiedInterviewDays += interviewDays;
                interviewDays = modifiedInterviewDays;
            }
        }
        //slots
        var today = new Date();
        for (i = 2; i < 9; i++) {
            // 0 - > sun 1 -> mon ...
            var x = new Date(today.getFullYear(), today.getMonth(), today.getDate() + i);
            if (checkSlotAvailability(x, interviewDays)) {
                interviewDetailsList.forEach(function (timeSlot) {
                    var dateSlotSelectedId = x.getFullYear() + "-" + (x.getMonth() + 1) + "-" + x.getDate() + "_" + timeSlot.interviewTimeSlot.interviewTimeSlotId;
                    var option = $('<option value="' + dateSlotSelectedId + '"></option>').text(getDayVal(x.getDay()) + ", " + x.getDate() + " " + getMonthVal((x.getMonth() + 1)) + " (" + timeSlot.interviewTimeSlot.interviewTimeSlotName + ")");
                    $('#interViewSlot').append(option);
                });
            }
        }
        $('#interViewSection').show();
    } else{
        $('#interViewSection').hide();
    }
}

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


function initInterviewModal(candidateId, jobPostId) {
    var htmlBodyContent = ''+
        '<div id="confirmationMsg">'+
        '<center>'+
        '<img src="/assets/common/img/jobApply.png" width="48px">'+
        '</center>'+
        '<center>'+
        '<h4>You are applying to <b><div style="display: inline-block" id="jobNameConfirmation"></div></b>'+
        ' job at <b><div style="display: inline-block" id="companyNameConfirmation"></div></b>'+
        '<h5 style="margin-top: 16px">Please fill the form below to complete the application process</h5>'+
        '<div class="materialDash"></div>'+
        '<div class="interview_container" style="margin-top: 16px">'+
        '<div class="row" style="margin-top: 4px" id="interViewSection">'+
        '<div class="col-sm-6">'+
        '<h5 style="text-align: right">Please select the interview date and time:</h5></div>'+
        '<div class="col-sm-6">'+
        '<select id="interViewSlot" class="selectDropdown" style="width: 184px; float: left; margin-top: 8px"></select>'+
        '</div>'+
        '</div>'+
        '</div>'+
        '</center>'+
        '</div>';
    var title = "Interview Slot Selector";

    generateInterviewSlotModal(title, htmlBodyContent, candidateId, jobPostId);

    try {
        $.ajax({
            type: "POST",
            url: "/getJobPostInfo/" + jobPostId + "/0",
            data: false,
            contentType: false,
            processData: false,
            success: processJobPostInterviewSlot
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function generateInterviewSlotModal(title, message, candidateId, jobPostId) {
    console.log("rendering modal");
    var interviewDialog = bootbox.dialog({
        className: "interview-slot-selector-modal",
        title: title,
        message: message,
        closeButton: true,
        animate: true,
        onEscape: function() {
            $('body').removeClass('open-interview-selector-modal');
            notifyError("Submitted successfully. Refreshing page.", 'success');

            setTimeout(function () {
                location.reload();
                // window.location = response.redirectUrl + app.jpId + "/?view=" + response.nextView;
            }, 2000);
        },
        buttons: {
            "Submit": {
                className: "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent interview-selector-submit",
                callback: function () {
                    finalInterviewSlotSubmission(candidateId, jobPostId);
                }
            }
        }
    });
    interviewDialog.attr("id", "interview-selector-modal");

    $("#interViewSlot").change(function (){
        if($("#interViewSlot").val() != -1){
            $(".btn.interview-selector-submit").prop('disabled', false);
        } else {
            $(".btn.interview-selector-submit").prop('disabled', true);
        }
    });

    $('#interview-slot-selector-modal div.modal-body').attr('style', 'overflow: visible !important');
    $('.btn.interview-selector-submit').prop('disabled', true);
    $('body').removeClass('modal-open').removeClass('open-interview-selector-modal').addClass('open-interview-selector-modal');
}

function finalInterviewSlotSubmission(candidateId, jobPostId) {
    if($("#interViewSlot").val() != -1 ){
        var combinedValue = $("#interViewSlot").val().split("_");
        scheduledInterviewDate = combinedValue[0];
        prefTimeSlot = combinedValue[1];

        var d = {
            timeSlot: prefTimeSlot,
            scheduledInterviewDate: scheduledInterviewDate
        };
        var base_api_url ="/support/api/updateCandidateInterviewDetail/";
        if(base_api_url == null || jobPostId == null) {
            console.log("please provide candidateId && jobPostId");
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
    console.log(returnData);
    // window.location = response.redirectUrl + app.jpId + "/?view=" + response.nextView;
    notifyError("Interview Submitted successfully. Refreshing ..", 'success');
    setTimeout(function () {
        location.reload();
    }, 2000);
}

function interviewSubmitResponse(returnData){
    if(returnData == "ok"){
        setTimeout(function () {
            location.reload();
            // window.location = response.redirectUrl + app.jpId + "/?view=" + response.nextView;
        }, 2000);
    }
}