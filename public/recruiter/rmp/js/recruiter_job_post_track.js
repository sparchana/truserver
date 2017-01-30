var jobPostId;
var oldDate = null;
var globalCandidateId = null;
var globalInterviewStatus = null;
var rescheduledDate;
var rescheduledSlot;

var globalInterviewDay = null;
var globalInterviewSlot = null;
var globalSchedule = null;
var allTimeSlots = [];
var allReason = [];

var notSelectedReason = [];

function preformTabChange(tabId) {
    hideTab1();
    hideTab2();
    hideTab3();
    if(tabId == 1){
        hideTab1();
        tabChange1();
    } else if(tabId == 2){
        tabChange2();
    } else if(tabId == 3){
        tabChange3();
    }
}
$(document).scroll(function(){
    if ($(this).scrollTop() > 30) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});

$(document).ready(function(){
    checkRecruiterLogin();

    var pathname = window.location.pathname; // Returns path only
    var jobPostIdUrl = pathname.split('/');
    jobPostId = jobPostIdUrl[(jobPostIdUrl.length)-1];

    tabChange1();

    try {
        $.ajax({
            type: "POST",
            url: "/getAllNotSelectedReasons",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataNotSelectedReason
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "POST",
            url: "/getAllTimeSlots",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetAllTimeSlots
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "POST",
            url: "/getAllInterviewRejectReasons",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetAllReason
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    $("#rescheduleDateAndSlot").change(function (){
        if($(this).val() != -1){
            try{
                var combinedValue = $("#rescheduleDateAndSlot").val().split("_");
                rescheduledDate = combinedValue[0];
                rescheduledSlot = combinedValue[1];

            } catch(err){}

            $("#rescheduleInterviewDateBtn").show();
        } else{
            $("#rescheduleInterviewDateBtn").hide();
        }
    });
});

function processDataNotSelectedReason(returnedData) {
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        notSelectedReason.push(item);
    });
}

function processDataGetAllTimeSlots(returnedData) {
    returnedData.forEach(function(timeSlot) {
        var id = timeSlot.interviewTimeSlotId;
        var name = timeSlot.interviewTimeSlotName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allTimeSlots.push(item);
    });
}

function processDataGetAllReason(returnedData) {
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allReason.push(item);
    });
}

function checkRecruiterLogin() {
    try {
        $.ajax({
            type: "GET",
            url: "/checkRecruiterSession",
            data: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterSession
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataRecruiterSession(returnedData) {
    if(returnedData == 0){
        logoutRecruiter();
    }
}

function tabChange1() {
    $("#tab1").addClass("activeTab");
    $("#tab2").removeClass("activeTab");
    $("#tab3").removeClass("activeTab");

    $("#tab1Parent").addClass("activeParent");
    $("#tab2Parent").removeClass("activeParent");
    $("#tab3Parent").removeClass("activeParent");

    $("#loaderText").html("Fetching SMS Reports");
    $("#loadingIcon").show();

    hideTab2();
    hideTab3();

    try {
        var table = $('table#smsReportTable').DataTable({
            "ajax": {
                "type": "POST",
                "url": "/getSentSms/?jpId=" + jobPostId,
                "dataSrc": function (returnedData) {
                    $("#loadingIcon").hide();
                    if(returnedData == 0){
                        logoutRecruiter();
                    } else{
                        var smsList = returnedData.smsReportList;
                        var returned_data = new Array();
                        if(Object.keys(smsList).length > 0) {
                            smsList.forEach(function (smsObject) {
                                returned_data.push({
                                    'date' : function() {
                                        var postedOn = new Date(smsObject.creationTimeStamp);
                                        var dateVal = ('0' + postedOn.getDate()).slice(-2) + '-' + getMonthVal((postedOn.getMonth()+1)) + '-' + postedOn.getFullYear();
                                        return '<div class="mLabel" style="width:100%" >'+ dateVal + '</div>'
                                    },
                                    'candidateName' : '<div class="mLabel" style="cursor: pointer; color: #029eda; font-weight: bold; width:100%" onclick="getCandidateInfo(' + smsObject.candidate.candidateId + ')" >'+ smsObject.candidate.candidateFullName + '</div>',
                                    'candidateMobile' : '<div class="mLabel" style="width:100%" >'+ smsObject.candidate.candidateMobile + '</div>',
                                    'smsStatus' : function() {
                                        if (smsObject.smsDeliveryStatus != null){
                                            if(smsObject.smsDeliveryStatus.statusId == 1){
                                                return '<div class="mLabel" style="width:100%; color: orange; font-weight: bold" >'+ smsObject.smsDeliveryStatus.statusName + '</div>';
                                            } else if(smsObject.smsDeliveryStatus.statusId == 2){
                                                return '<div class="mLabel" style="width:100%; color: #2ec866; font-weight: bold" >'+ smsObject.smsDeliveryStatus.statusName + '</div>';
                                            } else{
                                                return '<div class="mLabel" style="width:100%; color: red; font-weight: bold" >'+ smsObject.smsDeliveryStatus.statusName + '</div>';
                                            }
                                        } else {
                                            return "-";
                                        }
                                        return "-";
                                    },
                                    'jobStatus' : function() {
                                        if(smsObject.hasApplied == 0)
                                            return '<div class="mLabel" style="width:100%" >Not Applied</div>';
                                        else{
                                            return '<div class="mLabel" style="width:100%" >Applied</div>';
                                        }
                                        return "-";
                                    },
                                    'smsText' : '<div class="mLabel" style="width:100%" >'+ smsObject.smsText + '</div>',
                                })
                            });
                            $("#smsReportContainer").show();
                        } else{
                            $("#noSmsSent").show();
                        }
                        return returned_data;
                    }
                }
            },
            "deferRender": true,
            "columns": [
                { "data": "date" },
                { "data": "candidateName" },
                { "data": "candidateMobile" },
                { "data": "smsStatus" },
                { "data": "jobStatus" },
                { "data": "smsText" }
            ],
            "language": {
                "emptyTable": "Looks like you have not sent any SMS to any of the candidates!"
            },
            "order": [[2, "desc"]],
            responsive: true,
            "destroy": true,
            "dom": 'Bfrtip',
            "buttons": [
                'copy', 'csv', 'excel'
            ]
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function tabChange2() {
    $("#tab1").removeClass("activeTab");
    $("#tab2").addClass("activeTab");
    $("#tab3").removeClass("activeTab");

    $("#tab1Parent").removeClass("activeParent");
    $("#tab2Parent").addClass("activeParent");
    $("#tab3Parent").removeClass("activeParent");

    $("#loaderText").html("Fetching Job Applicants");
    $("#loadingIcon").show();

    hideTab1();
    hideTab3();

    try {
        var table = $('table#trackApplicationTable').DataTable({
            "ajax": {
                "type": "POST",
                "url": "/getAppliedCandidates/?jpId=" + jobPostId,
                "dataSrc": function (returnedData) {
                    $("#loadingIcon").hide();
                    if(returnedData == 0){
                        logoutRecruiter();
                    } else{
                        var applicationList = returnedData.applicationList;
                        var returned_data = new Array();
                        if(applicationList.length > 0) {
                            applicationList.forEach(function (workflowObj) {
                                returned_data.push({
                                    'date' : function() {
                                        var postedOn = new Date(workflowObj.extraData.creationTimestamp);
                                        var dateVal = ('0' + postedOn.getDate()).slice(-2) + '-' + getMonthVal((postedOn.getMonth()+1)) + '-' + postedOn.getFullYear()
                                        return '<div class="mLabel" style="width:100%" >'+ dateVal + '</div>'
                                    },
                                    'candidateName' : '<div class="mLabel" style="cursor: pointer; color: #029eda; font-weight: bold; width:100%" onclick="getCandidateInfo(' + workflowObj.candidate.candidateId + ')" >'+ workflowObj.candidate.candidateFullName + '</div>',
                                    'candidateMobile' : '<div class="mLabel" style="width:100%" >'+ workflowObj.candidate.candidateMobile + '</div>',
                                    'applicationStatus' : function() {
                                        var retVal = "-";
                                        if(workflowObj.extraData.workflowStatus != null){
                                            if(workflowObj.candidate.candidateId == 99985571){
                                                console.log(workflowObj.extraData.workflowStatus.statusId);
                                            }
                                            if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_SELECTED){
                                                retVal = '<div class="mLabel" style="width:100%; color: orange; font-weight: bold">Applied</div>';
                                            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_PRESCREEN_ATTEMPTED){
                                                retVal = '<div class="mLabel" style="width:100%; color: orange; font-weight: bold">Attempted pre screen</div>';
                                            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_PRESCREEN_FAILED){
                                                retVal = '<div class="mLabel" style="width:100%; color: red; font-weight: bold">Pre screen failed</div>';
                                            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_PRESCREEN_COMPLETED){
                                                retVal = '<div class="mLabel" style="width:100%; color: orange; font-weight: bold">Pre screen Complete</div>';
                                            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_SCHEDULED) {
                                                retVal = '<div class="mLabel" style="width:100%; color: #2ec866; font-weight: bold">Interview Scheduled</div>';
                                            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT) {
                                                retVal = '<div class="mLabel" style="width:100%; color: red; font-weight: bold">Rejected</div>';
                                            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE) {
                                                retVal = '<div class="mLabel" style="width:100%; color: red; font-weight: bold">Candidate Rejected</div>';
                                            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_RESCHEDULE) {
                                                retVal = '<div class="mLabel" style="width:100%; color: orange; font-weight: bold">Rescheduled</div>';
                                            }

                                            console.log(retVal);
                                        }

                                        return retVal;
                                    },
                                    'channel' : function() {
                                        if(workflowObj.applicationChannel == 1){
                                            return '<div class="mLabel" style="width:100%" >SMS</div>';
                                        } else if(workflowObj.applicationChannel == 2){
                                            return '<div class="mLabel" style="width:100%" >Partner - ' + workflowObj.partner.partnerFirstName + '</div>';
                                        } else if(workflowObj.applicationChannel == 3){
                                            return '<div class="mLabel" style="width:100%" >TruJobs Support</div>';
                                        }

                                    },
                                    'interviewDate' : function() {
                                        if(workflowObj.extraData.interviewDate != null){
                                            var interviewDate = new Date(workflowObj.extraData.interviewDate);

                                            var interviewDateVal = validateDateFormat(interviewDate) +
                                                " @ " + workflowObj.extraData.interviewSlot.interviewTimeSlotName;
                                            return '<div class="mLabel" style="width:100%" >' + interviewDateVal + '</div>';
                                        } else{
                                            return '-';
                                        }
                                    },
                                    'action' : function() {
                                        var actionBtn = '-';
                                        if(workflowObj.extraData.workflowStatus != null && workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_SCHEDULED){
                                            actionBtn = '<div class="mLabel"  style="width:100%" >'
                                                + '<span class="customBtn btnGreen" onclick="acceptAction(' + workflowObj.candidate.candidateId + ', ' + workflowObj.extraData.interviewDate + ', ' + workflowObj.extraData.interviewSlot.interviewTimeSlotId  + ');" >Accept</span>'
                                                + '<span class="customBtn btnRed" onclick="rejectAction(' + workflowObj.candidate.candidateId + ', ' + workflowObj.extraData.interviewDate + ', ' + workflowObj.extraData.interviewSlot.interviewTimeSlotId  + ');" >Reject</span>'
                                                + '<span class="customBtn btnOrange" onclick="rescheduleAction(' + workflowObj.candidate.candidateId + ', ' + workflowObj.extraData.interviewDate + ', ' + workflowObj.extraData.interviewSlot.interviewTimeSlotId  + ');">Reschedule</span>'
                                                + '</span>';

                                        }

                                        return actionBtn;
                                    }
                                })
                            });

                            $("#trackApplicationContainer").show();
                        } else{
                            $("#noApplications").show();
                        }
                        return returned_data;
                    }
                }
            },

            "deferRender": true,
            "columns": [
                { "data": "date" },
                { "data": "candidateName" },
                { "data": "candidateMobile" },
                { "data": "applicationStatus" },
                { "data": "channel" },
                { "data": "interviewDate" },
                { "data": "action" }
            ],
            "language": {
                "emptyTable": "Looks like no one has applied to the job!"
            },
            "order": [[2, "desc"]],
            responsive: true,
            "destroy": true,
            "dom": 'Bfrtip',
            "buttons": [
                'copy', 'csv', 'excel'
            ]
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function acceptAction(candidateId, interviewDate, timeSlotId) {
    oldDate = new Date(interviewDate);
    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
    globalInterviewSlot = timeSlotId;
    globalSchedule = " ";
    globalCandidateId = candidateId;
    confirmInterviewStatus(candidateId);
}

function rejectAction(candidateId, interviewDate, timeSlotId) {
    oldDate = new Date(interviewDate);
    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
    globalInterviewSlot = timeSlotId;
    globalSchedule = " ";
    globalCandidateId = candidateId;
    rejectInterview(candidateId);
}

function rescheduleAction(candidateId, interviewDate, timeSlotId) {
    globalCandidateId = candidateId;
    oldDate = new Date(interviewDate);
    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
    globalInterviewSlot = timeSlotId;
    globalSchedule = " ";
    showSlotModal();
}

function tabChange3() {
    $("#tab1").removeClass("activeTab");
    $("#tab2").removeClass("activeTab");
    $("#tab3").addClass("activeTab");

    $("#tab1Parent").removeClass("activeParent");
    $("#tab2Parent").removeClass("activeParent");
    $("#tab3Parent").addClass("activeParent");

    $("#loaderText").html("Fetching Confirmed Interviews");
    $("#loadingIcon").show();

    hideTab1();
    hideTab2();

    try {
        var table = $('table#confirmedApplicationTable').DataTable({
            "ajax": {
                "type": "POST",
                "url": "/getConfirmedApplication/?jpId=" + jobPostId,
                "dataSrc": function (returnedData) {
                    $("#loadingIcon").hide();
                    if(returnedData == 0){
                        logoutRecruiter();
                    } else{
                        var applicationList = returnedData.applicationList;
                        if(applicationList.length > 0) {
                            var returned_data = new Array();
                            applicationList.forEach(function (workflowObj) {
                                returned_data.push({
                                    'date' : function() {
                                        var postedOn = new Date(workflowObj.extraData.creationTimestamp);
                                        var dateVal = ('0' + postedOn.getDate()).slice(-2) + '-' + getMonthVal((postedOn.getMonth()+1)) + '-' + postedOn.getFullYear()
                                        return '<div class="mLabel" style="width:100%" >'+ dateVal + '</div>'
                                    },
                                    'candidateName' : '<div class="mLabel" style="cursor: pointer; color: #029eda; font-weight: bold; width:100%" onclick="getCandidateInfo(' + workflowObj.candidate.candidateId + ')" >'+ workflowObj.candidate.candidateFullName + '</div>',
                                    'candidateMobile' : '<div class="mLabel" style="width:100%" >'+ workflowObj.candidate.candidateMobile + '</div>',
                                    'channel' : function() {
                                        if(workflowObj.applicationChannel == 1){
                                            return '<div class="mLabel" style="width:100%" >SMS</div>';
                                        } else if(workflowObj.applicationChannel == 2){
                                            return '<div class="mLabel" style="width:100%" >Partner - ' + workflowObj.partner.partnerFirstName + '</div>';
                                        } else if(workflowObj.applicationChannel == 3){
                                            return '<div class="mLabel" style="width:100%" >TruJobs Support</div>';
                                        }
                                    },
                                    'interviewDate' : function() {
                                        if(workflowObj.extraData.interviewDate != null){
                                            var interviewDate = new Date(workflowObj.extraData.interviewDate);

                                            var interviewDateVal = validateDateFormat(interviewDate) +
                                                " @ " + workflowObj.extraData.interviewSlot.interviewTimeSlotName;
                                            return '<div class="mLabel" style="width:100%" >' + interviewDateVal + '</div>';
                                        } else{
                                            return '<div class="mLabel" style="width:100%" >-</div>';
                                        }

                                    },
                                    'feedback' : function() {
                                        if(workflowObj.extraData.workflowStatus != null){
                                            if(workflowObj.extraData.workflowStatus.statusId > JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){
                                                var feedbackVal = workflowObj.extraData.workflowStatus.statusTitle;
                                                return '<div class="mLabel" style="width:100%" >' + feedbackVal + '</div>';
                                            } else{

                                                return '<div class="mLabel" >'
                                                    + '<span class="customBtn btnGreen" style="cursor: pointer" onclick="openFeedbackModal('+ workflowObj.candidate.candidateId +')" >Add Feedback</span>'
                                                    + '</div>';
                                            }
                                        } else{
                                            return '<div class="mLabel" style="width:100%" >-</div>'
                                        }


                                    }
                                })
                            });

                            $("#confirmedApplicationContainer").show();
                            return returned_data;
                        } else{
                            $("#noConfirmedApplications").show();
                        }
                    }
                }
            },

            "deferRender": true,
            "columns": [
                { "data": "date" },
                { "data": "candidateName" },
                { "data": "candidateMobile" },
                { "data": "channel" },
                { "data": "interviewDate" },
                { "data": "feedback" }
            ],
            "language": {
                "emptyTable": "Looks like there no confirmed applications!"
            },
            "order": [[2, "desc"]],
            responsive: true,
            "destroy": true,
            "dom": 'Bfrtip',
            "buttons": [
                'copy', 'csv', 'excel'
            ]
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function setInterviewStatus(candidateId, status, rescheduledDate, rescheduledSlot, reason) {
    globalCandidateId = candidateId;
    globalInterviewStatus = status;

    var d = {
        candidateId: candidateId,
        jobPostId: jobPostId,
        interviewStatus: status,
        rescheduledDate: rescheduledDate,
        rescheduledSlot: rescheduledSlot,
        reason: reason,
        interviewSchedule: globalSchedule
    };

    try {
        $.ajax({
            type: "POST",
            url: "/recruiter/api/updateInterviewStatus",
            async: false,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataInterviewStatus
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
}

function processDataInterviewStatus(returnedData) {
    $('.tooltipped').tooltip('remove');
    $("#modalRescheduleSlot").closeModal();
    if(returnedData == "1"){
        if(globalInterviewStatus == 1){
            notifySuccess("Interview Confirmed"); //accepted
            tabChange2();
        } else if(globalInterviewStatus == 2){ //rejected by recruiter
            $("#modalRejectReason").closeModal();
            notifySuccess("Interview Rejected");
            tabChange2();
        } else if(globalInterviewStatus == 3){
            notifySuccess("Interview Rescheduled");
            tabChange2();
        }
    } else{
        notifyError("Something went wrong. Please try again later. Refreshing page..");
        setTimeout(function(){
            location.reload(true); // hard refresh set 'ture' for current page to reload latest js, css
        }, 2000);
    }
}

function confirmInterviewStatus(candidateId) {
    globalCandidateId = candidateId;
    globalInterviewStatus = "1";
    setInterviewStatus(globalCandidateId, globalInterviewStatus, globalInterviewDay, globalInterviewSlot, null);
}

function rejectInterview(candidateId) {
    globalCandidateId = candidateId;
    globalInterviewStatus = 3;

    $("#reject_reason").html('');

    var defaultOption = $('<option value="0" selected></option>').text("Select a reason");
    $('#reject_reason').append(defaultOption);

    allReason.forEach(function (reason) {
        var option = $('<option value=' + reason.id + '></option>').text(reason.name);
        $('#reject_reason').append(option);
    });
    $("#modalRejectReason").openModal();
}

function rescheduleInterviewStatus() {
    globalInterviewStatus = "3";
    setInterviewStatus(globalCandidateId, globalInterviewStatus, rescheduledDate, rescheduledSlot, null);
}

function confirmRejectInterview(){
    if($("#reject_reason").val() != 0){
        globalInterviewStatus = 2;
        setInterviewStatus(globalCandidateId, 2, globalInterviewDay, globalInterviewSlot, $("#reject_reason").val());
    } else{
        notifyError("Please specify the reason for the job application rejection");
    }
}

function showSlotModal() {
    $("#modalRescheduleSlot").openModal();
    $("#rescheduleInterviewDateBtn").hide();
    try {
        $.ajax({
            type: "POST",
            url: "/getJobPostInfo/" + parseInt(jobPostId) + "/0",
            data: false,
            contentType: false,
            processData: false,
            success: processDataForJobPostInfo
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataForJobPostInfo(returnedData) {
    if (Object.keys(returnedData.interviewDetailsList).length > 0) {
        //slots
        var i;
        $('#rescheduleDateAndSlot').html('');
        var defaultOption = $('<option value="-1"></option>').text("Select Time Slot");
        $('#rescheduleDateAndSlot').append(defaultOption);

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

        var oldSelectedDate = new Date(oldDate);
        //slots
        var today = new Date();
        for (i = 2; i < 9; i++) {
            // 0 - > sun 1 -> mon ...
            var x = new Date(today.getFullYear(), today.getMonth(), today.getDate() + i);
            if (checkSlotAvailability(x, interviewDays)) {
                interviewDetailsList.forEach(function (timeSlot) {
                    var dateSlotSelectedId = x.getFullYear() + "-" + (x.getMonth() + 1) + "-" + x.getDate() + "_" + timeSlot.interviewTimeSlot.interviewTimeSlotId;
                    var option = $('<option value="' + dateSlotSelectedId + '"></option>').text(getDayVal(x.getDay()) + ", " + x.getDate() + " " + getMonthVal((x.getMonth() + 1)) + " (" + timeSlot.interviewTimeSlot.interviewTimeSlotName + ")");

                    if((oldSelectedDate.getDate() == x.getDate()) && (oldSelectedDate.getMonth() == x.getMonth()) && (globalInterviewSlot == timeSlot.interviewTimeSlot.interviewTimeSlotId)){} else{
                        $('#rescheduleDateAndSlot').append(option);
                    }
                });
            }
        }
    } else{
        $("#modalRescheduleSlot").closeModal();
        notifyError("No Slots available!");
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

//feedback
function openFeedbackModal(candidateId) {
    globalCandidateId = candidateId;
    $("#reasonVal").html('');
    var defaultOption = $('<option value="0" selected></option>').text("Select a reason");
    $('#reasonVal').append(defaultOption);

    notSelectedReason.forEach(function (reason) {
        var option = $('<option value=' + reason.id + '></option>').text(reason.name);
        $('#reasonVal').append(option);
    });

    $("#otherReason").hide();
    $("#feedbackOption").val(0);
    $("#reasonVal").val(0);
    $("#feedbackNote").val('');

    $("#addFeedback").openModal();

    $("#feedbackOption").change(function (){
        if($(this).val() == 2 || $(this).val() == 4){
            $("#otherReason").show();
        } else{
            $("#otherReason").hide();
        }
    });
}

function confirmAddFeedback() {
    if($("#feedbackOption").val() > 0){
        if(($("#feedbackOption").val() == 2 || $("#feedbackOption").val() == 4) && $("#reasonVal").val() == 0){
            notifyError("Please select a reason");
        } else{
            try {
                var d = {
                    candidateId: globalCandidateId,
                    jobPostId : jobPostId,
                    feedbackStatus : $("#feedbackOption").val(),
                    feedbackComment : $("#feedbackNote").val(),
                    rejectReason: $("#reasonVal").val()
                };

                $.ajax({
                    type: "POST",
                    url: "/updateFeedback",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataUpdateFeedBack
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    } else{
        notifyError("Please select a feedback option");
    }
}

function processDataUpdateFeedBack(returnedData) {
    if(returnedData == 1){
        tabChange3();
        notifySuccess("Feedback updated successfully");

        $("#addFeedback").closeModal();

    } else if(returnedData == -1){
        notifyError("You are out of interview credits. Please purchase interview credits!");
        openCreditModal();
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

function hideTab1() {
    $("#noSmsSent").hide();
    $("#smsReportContainer").hide();
}
function hideTab2() {
    $("#noApplications").hide();
    $("#trackApplicationContainer").hide();
}

function hideTab3() {
    $("#noConfirmedApplications").hide();
    $("#confirmedApplicationContainer").hide();
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

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}

function logoutRecruiter() {
    try {
        $.ajax({
            type: "GET",
            url: "/logoutRecruiter",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataLogoutRecruiter
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataLogoutRecruiter() {
    window.location = "/recruiter";
}

function closeFeedbackModal() {
    $("#addFeedback").closeModal();
}