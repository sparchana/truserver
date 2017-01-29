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
var index = 0;

var notSelectedReason = [];

function preformTabChange(tabId) {
    index = 0;
    hideTab1();
    hideTab2();
    hideTab3();
    if(tabId == 1){
        hideTab1();
        tabChange1(index);
    } else if(tabId == 2){
        tabChange2(index);
    } else if(tabId == 3){
        tabChange3(index);
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

    index = 0;
    tabChange1(index);

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

function smsPagination(noOfPages){
    $('#paginationTab').twbsPagination({
        totalPages: noOfPages,
        visiblePages: 5,
        onPageClick: function (event, page) {
            if(page > 0 ){
                index = (page - 1)*10;
            }
            else{
                index = 0;
            }
            hideTab1();
            tabChange1(index);
            $(".page-link").click(function(){
                $('html, body').animate({scrollTop : 0},800);
            });
            $(".first").hide();
            $(".last").hide();
            $(".prev a").html("<");
            $(".next a").html(">");
        }
    });
}

function trackPagination(noOfPages){
    $('#trackPaginationTab').twbsPagination({
        totalPages: noOfPages,
        visiblePages: 5,
        onPageClick: function (event, page) {
            if(page > 0 ){
                index = (page - 1)*10;
            }
            else{
                index = 0;
            }
            hideTab2();
            tabChange2(index);
            $(".page-link").click(function(){
                $('html, body').animate({scrollTop : 0},800);
            });
            $(".first").hide();
            $(".last").hide();
            $(".prev a").html("<");
            $(".next a").html(">");
        }
    });
}

function confirmedPagination(noOfPages){
    $('#confirmedPaginationTab').twbsPagination({
        totalPages: noOfPages,
        visiblePages: 5,
        onPageClick: function (event, page) {
            if(page > 0 ){
                index = (page - 1)*10;
            }
            else{
                index = 0;
            }
            hideTab3();
            tabChange3(index);
            $(".page-link").click(function(){
                $('html, body').animate({scrollTop : 0},800);
            });
            $(".first").hide();
            $(".last").hide();
            $(".prev a").html("<");
            $(".next a").html(">");
        }
    });
}


function processDataGetSmsReport(returnedData) {
    $("#loadingIcon").hide();
    if(returnedData == 0){
        logoutRecruiter();
    } else{
        var smsList = returnedData.smsReportList;
        if(smsList.length > 0){
            $('.allSms').html('');
            var currentCount = 0;
            if((index + 10) > returnedData.totalSms){
                currentCount = returnedData.totalSms;
            } else{
                currentCount = index + 10;
            }
            $("#totalSmsReports").html("Showing " + currentCount + " out of " + returnedData.totalSms + " results");

            var numberOfPages = parseInt(returnedData.totalSms)/10;
            var rem = parseInt(returnedData.totalSms) % 10;
            if(rem > 0){
                numberOfPages ++;
            }
            if(index == 0){
                smsPagination(numberOfPages);
            }

            var parent = $('.allSms');
            smsList.forEach(function (smsObject) {

                var mainDiv =  document.createElement("div");
                parent.append(mainDiv);

                var outerRow = document.createElement("div");
                outerRow.className = 'row';
                outerRow.id="outerBoxMain";
                outerRow.style="font-size: 12px";
                mainDiv.appendChild(outerRow);

                var colDate = document.createElement("div");
                colDate.className = 'col s12 m1 l1';
                colDate.style = 'margin-top:8px';
                outerRow.appendChild(colDate);

                var postedOn = new Date(smsObject.creationTimeStamp);
                colDate.textContent = ('0' + postedOn.getDate()).slice(-2) + '-' + getMonthVal((postedOn.getMonth()+1)) + '-' + postedOn.getFullYear()

                var spanSentOn  = document.createElement("div");
                spanSentOn.className = "col s4 hide-on-med-and-up right-align";
                spanSentOn.textContent= "Date:";
                spanSentOn.style = "font-weight: 600;font-size:12px";
                colDate.appendChild(spanSentOn);

                var colCandidateName= document.createElement("div");
                colCandidateName.className = 'col s12 m2 l2';
                colCandidateName.style = 'margin-top: 8px; color: #55b2ce; font-weight: bold; cursor: pointer';
                colCandidateName.textContent = toTitleCase(smsObject.candidate.candidateFullName);
                colCandidateName.onclick = function () {
                    getCandidateInfo(smsObject.candidate.candidateId);
                };
                outerRow.appendChild(colCandidateName);

                var spanCandidateName = document.createElement("div");
                spanCandidateName.className = "col s4 hide-on-med-and-up right-align";
                spanCandidateName.textContent= "Candidate :";
                spanCandidateName.style = "font-weight: 600;font-size:12px";
                colCandidateName.appendChild(spanCandidateName);

                var colCandidateMobile = document.createElement("div");
                colCandidateMobile.className = 'col s12 m2 l2';
                colCandidateMobile.style = 'margin-top:8px';
                colCandidateMobile.textContent = smsObject.candidate.candidateMobile;
                outerRow.appendChild(colCandidateMobile);

                var spanCandidateMobile  = document.createElement("div");
                spanCandidateMobile.className = "col s4  hide-on-med-and-up right-align";
                spanCandidateMobile.textContent= "Mobile :";
                spanCandidateMobile.style = "font-weight: 600;font-size:12px";
                colCandidateMobile.appendChild(spanCandidateMobile);

                var colSmsStatus = document.createElement("div");
                colSmsStatus.className = 'col s12 m1 l1';
                colSmsStatus.style = 'margin-top: 8px';
                outerRow.appendChild(colSmsStatus);

                colSmsStatus.textContent = "Pending";

                if(smsObject.smsDeliveryStatus != null){
                    if(smsObject.smsDeliveryStatus.statusId == 1){
                        colSmsStatus.style = 'margin-top: 8px; color: orange; font-weight: bold';
                    } else if(smsObject.smsDeliveryStatus.statusId == 2){
                        colSmsStatus.style = 'margin-top: 8px; color: green; font-weight: bold';
                    } else{
                        colSmsStatus.style = 'margin-top: 8px; color: red; font-weight: bold';
                    }
                    colSmsStatus.textContent = smsObject.smsDeliveryStatus.statusName;
                }

                var spanSmsStatus  = document.createElement("div");
                spanSmsStatus.className = "col s4 hide-on-med-and-up right-align";
                spanSmsStatus.textContent= "SMS Status: ";
                spanSmsStatus.style = "font-weight: 600;font-size:12px";
                colSmsStatus.appendChild(spanSmsStatus);

                var colApplicationStatus = document.createElement("div");
                colApplicationStatus.className = 'col s12 m2 l2';
                colApplicationStatus.style = 'margin-top: 8px';
                outerRow.appendChild(colApplicationStatus);

                if(smsObject.hasApplied == 0)
                    colApplicationStatus.textContent = "Not Applied";
                 else{
                    colApplicationStatus.textContent = "Applied";
                }
                var spanApplicationStatus  = document.createElement("div");
                spanApplicationStatus.className = "col s4 hide-on-med-and-up right-align";
                spanApplicationStatus.textContent= "Application Status:";
                spanApplicationStatus.style = "font-weight: 600;font-size:12px;";
                colApplicationStatus.appendChild(spanApplicationStatus);

                var colSmsText = document.createElement("div");
                colSmsText.className = 'col s12 m2 l4';
                colSmsText.style = 'margin-top: 8px';
                outerRow.appendChild(colSmsText);

                colSmsText.textContent = smsObject.smsText;

                var spanSmsText = document.createElement("div");
                spanSmsText.className = "col s4 hide-on-med-and-up right-align";
                spanSmsText.textContent= "SMS Message: ";
                spanSmsText.style = "font-weight: 600;font-size:12px;";
                colApplicationStatus.appendChild(spanSmsText);

            });

            $("#smsReportTable").show();
        } else{
            $("#noSmsSent").show();
        }
    }
}

function processDataJobApplications(returnedData) {
    $("#loadingIcon").hide();

    var applicationList = returnedData.applicationList;
    if(applicationList.length > 0){
        $('.allApplications').html('');
        var parent = $('.allApplications');

        var currentCount = 0;
        if((index + 10) > returnedData.totalCount){
            currentCount = returnedData.totalCount;
        } else{
            currentCount = index + 10;
        }
        $("#totalTrackingCandidates").html("Showing " + currentCount + " out of " + returnedData.totalCount + " results");

        var numberOfPages = parseInt(returnedData.totalCount)/10;
        var rem = parseInt(returnedData.totalCount) % 10;
        if(rem > 0){
            numberOfPages ++;
        }
        if(index == 0){
            trackPagination(numberOfPages);
        }

        applicationList.forEach(function (workflowObj) {
            var mainDiv =  document.createElement("div");
            parent.append(mainDiv);

            var outerRow = document.createElement("div");
            outerRow.className = 'row';
            outerRow.id="outerBoxMain";
            outerRow.style="font-size: 12px";

            mainDiv.appendChild(outerRow);

            var colDateTime= document.createElement("div");
            colDateTime.className = 'col s12 m2 l1';
            colDateTime.style = 'margin-top:8px';

            var creationTimestamp = new Date(workflowObj.extraData.creationTimestamp);

            colDateTime.textContent = validateDateFormat(creationTimestamp);

            outerRow.appendChild(colDateTime);

            var spanDateTime = document.createElement("div");
            spanDateTime.className = "col s4 hide-on-med-and-up right-align";
            spanDateTime.textContent= "Candidate :";
            spanDateTime.style = "font-weight: 600;font-size:12px";
            colDateTime.appendChild(spanDateTime);

            var colCandidateName= document.createElement("div");
            colCandidateName.className = 'col s12 m2 l2';
            colCandidateName.style = 'margin-top: 8px; color: #55b2ce; font-weight: bold; cursor: pointer';
            colCandidateName.textContent = toTitleCase(workflowObj.candidate.candidateFullName);
            colCandidateName.onclick = function () {
                getCandidateInfo(workflowObj.candidate.candidateId);
            };
            outerRow.appendChild(colCandidateName);

            var spanCandidateName = document.createElement("div");
            spanCandidateName.className = "col s4 hide-on-med-and-up right-align";
            spanCandidateName.textContent= "Candidate :";
            spanCandidateName.style = "font-weight: 600;font-size:12px";
            colCandidateName.appendChild(spanCandidateName);

            var colCandidateMobile = document.createElement("div");
            colCandidateMobile.className = 'col s12 m2 l1';
            colCandidateMobile.style = 'margin-top:8px';
            colCandidateMobile.textContent = workflowObj.candidate.candidateMobile;
            outerRow.appendChild(colCandidateMobile);

            var spanCandidateMobile  = document.createElement("div");
            spanCandidateMobile.className = "col s4  hide-on-med-and-up right-align";
            spanCandidateMobile.textContent= "Mobile :";
            spanCandidateMobile.style = "font-weight: 600;font-size:12px";
            colCandidateMobile.appendChild(spanCandidateMobile);

            var colChannel = document.createElement("div");
            colChannel.className = 'col s12 m1 l1';
            colChannel.style = 'margin-top: 8px';
            outerRow.appendChild(colChannel);

            colChannel.textContent = "N/A";

            if(workflowObj.applicationChannel == 1){
                colChannel.textContent = "SMS";
            } else if(workflowObj.applicationChannel == 2){
                colChannel.textContent = "Partner (" + workflowObj.partner.parnterName ;
            } else if(workflowObj.applicationChannel == 3){
                colChannel.textContent = "TruJobs Support";
            }

            var spanChannel  = document.createElement("div");
            spanChannel.className = "col s4 hide-on-med-and-up right-align";
            spanChannel.textContent= "Channel: ";
            spanChannel.style = "font-weight: 600;font-size:12px";
            colChannel.appendChild(spanChannel);

            var colInterviewDate = document.createElement("div");
            colInterviewDate.className = 'col s12 m1 l2';
            colInterviewDate.style = 'margin-top: 8px';
            outerRow.appendChild(colInterviewDate);

            colInterviewDate.textContent = "-";
            if(workflowObj.extraData.interviewDate != null){
                var interviewDate = new Date(workflowObj.extraData.interviewDate);

                colInterviewDate.textContent = validateDateFormat(interviewDate) +
                    " @ " + workflowObj.extraData.interviewSlot.interviewTimeSlotName;
            }

            var spanInterviewDate = document.createElement("div");
            spanInterviewDate.className = "col s4 hide-on-med-and-up right-align";
            spanInterviewDate.textContent= "Channel: ";
            spanInterviewDate.style = "font-weight: 600;font-size:12px";
            colInterviewDate.appendChild(spanInterviewDate);

            var colAction = document.createElement("div");
            colAction.className = 'col s12 m2 l3';
            colAction.id = "interview_status_option_" + workflowObj.candidate.candidateId;
            colAction.style = 'margin-top: 8px';
            colAction.textContent = "Call candidate to schedule interview";

            var colStatus = document.createElement("div");
            colStatus.className = 'col s12 m2 l2';
            colStatus.style = 'margin-top: 8px';
            outerRow.appendChild(colStatus);

            if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_SELECTED){
                colStatus.style = 'margin-top: 8px; color: orange; font-weight: bold';
                colStatus.textContent = "Applied";
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_PRESCREEN_ATTEMPTED){
                colStatus.textContent = "Attempted pre screen";
                colStatus.style = 'margin-top: 8px; color: orange; font-weight: bold';
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_PRESCREEN_FAILED){
                colStatus.textContent = "Pre screen failed";
                colStatus.style = 'margin-top: 8px; color: red; font-weight: bold';
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_PRESCREEN_COMPLETED){
                colStatus.textContent = "Pre screen Complete";
                colStatus.style = 'margin-top: 8px; color: orange; font-weight: bold';
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_SCHEDULED){
                colStatus.textContent = "Interview Scheduled";
                colStatus.style = 'margin-top: 8px; color: green; font-weight: bold';
                colAction.textContent = "";

                //action button
                var candidateInterviewAcceptParent = document.createElement("span");
                candidateInterviewAcceptParent.style = "display: inline-block; font-size: 12px";
                candidateInterviewAcceptParent.onclick = function () {
                    oldDate = new Date(workflowObj.extraData.interviewDate);
                    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                    globalInterviewSlot = workflowObj.extraData.interviewSlot.interviewTimeSlotId;
                    globalSchedule = workflowObj.extraData.interviewSchedule;
                    confirmInterviewStatus(workflowObj.candidate.candidateId);
                };
                colAction.appendChild(candidateInterviewAcceptParent);

                var candidateInterviewAccept = document.createElement("span");
                candidateInterviewAccept.className = "accept";
                candidateInterviewAcceptParent.appendChild(candidateInterviewAccept);

                var iconImg = document.createElement("img");
                iconImg.src = "/assets/dashboard/img/reached.svg";
                iconImg.setAttribute('height', '24px');
                candidateInterviewAccept.appendChild(iconImg);

                var actionText = document.createElement("span");
                actionText.textContent = " Accept";
                candidateInterviewAcceptParent.appendChild(actionText);

                var candidateInterviewRejectParent = document.createElement("span");
                candidateInterviewRejectParent.style = "display: inline-block; font-size: 12px";
                candidateInterviewRejectParent.onclick = function () {
                    oldDate = new Date(workflowObj.extraData.interviewDate);
                    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                    globalInterviewSlot = workflowObj.extraData.interviewSlot.interviewTimeSlotId;
                    globalSchedule = workflowObj.extraData.interviewSchedule;
                    rejectInterview(workflowObj.candidate.candidateId);
                };
                colAction.appendChild(candidateInterviewRejectParent);

                var candidateInterviewReject = document.createElement("span");
                candidateInterviewReject.className = "reject";
                candidateInterviewRejectParent.appendChild(candidateInterviewReject);

                iconImg = document.createElement("img");
                iconImg.src = "/assets/dashboard/img/not_going.svg";
                iconImg.setAttribute('height', '24px');
                candidateInterviewReject.appendChild(iconImg);

                actionText = document.createElement("span");
                actionText.textContent = " Reject";
                candidateInterviewRejectParent.appendChild(actionText);

                var candidateInterviewRescheduleParent = document.createElement("span");
                candidateInterviewRescheduleParent.style = "display: inline-block";
                candidateInterviewRescheduleParent.onclick = function () {
                    globalCandidateId = workflowObj.candidate.candidateId;
                    oldDate = new Date(workflowObj.extraData.interviewDate);
                    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                    globalInterviewSlot = workflowObj.extraData.interviewSlot.interviewTimeSlotId;
                    globalSchedule = workflowObj.extraData.interviewSchedule;

                    showSlotModal();
                };
                colAction.appendChild(candidateInterviewRescheduleParent);

                var candidateInterviewReschedule = document.createElement("span");
                candidateInterviewReschedule.className = "reschedule";
                candidateInterviewRescheduleParent.appendChild(candidateInterviewReschedule);

                iconImg = document.createElement("img");
                iconImg.src = "/assets/dashboard/img/reschedule.svg";
                iconImg.setAttribute('height', '24px');
                candidateInterviewReschedule.appendChild(iconImg);

                actionText = document.createElement("span");
                actionText.textContent = " Reschedule";
                candidateInterviewRescheduleParent.appendChild(actionText);

            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT){
                colStatus.textContent = "Rejected";
                colStatus.style = 'margin-top: 8px; color: red; font-weight: bold';
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE){
                colStatus.textContent = "Rejected by candidate";
                colStatus.style = 'margin-top: 8px; color: red; font-weight: bold';
            } else if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_RESCHEDULE){
                colStatus.textContent = "Rescheduled. Awaiting candidate's response";
                colStatus.style = 'margin-top: 8px; color: orange; font-weight: bold';
            } else{
                colStatus.textContent = "N/A";
            }

            var spanStatus = document.createElement("div");
            spanStatus.className = "col s4 hide-on-med-and-up right-align";
            spanStatus.textContent= "Status:";
            spanStatus.style = "font-weight: 600;font-size:12px;";
            colStatus.appendChild(spanStatus);

            //append action section
            outerRow.appendChild(colAction);


            var spanAction = document.createElement("div");
            spanAction.className = "col s4 hide-on-med-and-up right-align";
            spanAction.textContent= "Action: ";
            spanAction.style = "font-weight: 600;font-size:12px;";
            colAction.appendChild(spanAction);
         });

        $("#trackApplicationTable").show();
    } else{
        $("#noApplications").show();
    }
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

function tabChange1(index) {
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
        $.ajax({
            type: "POST",
            url: "/getSentSms/?jpId=" + jobPostId + "&i=" + index,
            data: false,
            contentType: false,
            processData: false,
            success: processDataGetSmsReport,
            error: function (jqXHR, exception) {
                $("#somethingWentWrong").show();
                $("#loadingIcon").hide();
            }

        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function tabChange2(index) {
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
        $.ajax({
            type: "POST",
            url: "/getAppliedCandidates/?jpId=" + jobPostId + "&i=" + index,
            data: false,
            contentType: false,
            processData: false,
            success: processDataJobApplications,
            error: function (jqXHR, exception) {
                $("#somethingWentWrong").show();
                $("#loadingIcon").hide();
            }

        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function tabChange3(index) {
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
        $.ajax({
            type: "POST",
            url: "/getConfirmedApplication/?jpId=" + jobPostId + "&i=" + index,
            data: false,
            contentType: false,
            processData: false,
            success: processDataConfirmedApplication,
            error: function (jqXHR, exception) {
                $("#somethingWentWrong").show();
                $("#loadingIcon").hide();
            }

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
        $("#interview_status_option_" + globalCandidateId).html('');

        var candidateInterviewStatusVal = document.createElement("span");
        if(globalInterviewStatus == 1){
            notifySuccess("Interview Confirmed"); //accepted
            $("#interview_status_option_" + globalCandidateId).html('Interview Confirmed');
        } else if(globalInterviewStatus == 2){ //rejected by recruiter
            $("#modalRejectReason").closeModal();
            notifySuccess("Interview Rejected");
            $("#interview_status_option_" + globalCandidateId).html('Interview Rejected');
        } else if(globalInterviewStatus == 3){
            notifySuccess("Interview Rescheduled");
            $("#interview_status_option_" + globalCandidateId).html('Interview Rescheduled. Awaiting Candidate\'s confirmation');
            var newDate = new Date(rescheduledDate);
            var i, newSlot;
            for(i=0; i<Object.keys(allTimeSlots).length; i++){
                if(allTimeSlots[i].id == rescheduledSlot){
                    newSlot = allTimeSlots[i].name;
                }
            }
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


function processDataConfirmedApplication(returnedData) {
    $("#loadingIcon").hide();

    var applicationList = returnedData.applicationList;
    if(applicationList.length > 0){
        $('.allConfirmed').html('');
        var parent = $('.allConfirmed');

        var currentCount = 0;
        if((index + 10) > returnedData.totalCount){
            currentCount = returnedData.totalCount;
        } else{
            currentCount = index + 10;
        }
        $("#totalConfirmedCandidates").html("Showing " + currentCount + " out of " + returnedData.totalCount + " results");

        var numberOfPages = parseInt(returnedData.totalCount)/10;
        var rem = parseInt(returnedData.totalCount) % 10;
        if(rem > 0){
            numberOfPages ++;
        }
        if(index == 0){
            confirmedPagination(numberOfPages);
        }

        applicationList.forEach(function (workflowObj) {
            var mainDiv =  document.createElement("div");
            parent.append(mainDiv);

            var outerRow = document.createElement("div");
            outerRow.className = 'row';
            outerRow.id="outerBoxMain";
            outerRow.style="font-size: 12px";
            mainDiv.appendChild(outerRow);

            var colDateTime= document.createElement("div");
            colDateTime.className = 'col s12 m2 l1';
            colDateTime.style = 'margin-top:8px';

            var creationTimestamp = new Date(workflowObj.extraData.creationTimestamp);

            colDateTime.textContent = validateDateFormat(creationTimestamp);

            outerRow.appendChild(colDateTime);

            var colCandidateName= document.createElement("div");
            colCandidateName.className = 'col s12 m2 l2';
            colCandidateName.style = 'margin-top: 8px; color: #55b2ce; font-weight: bold; cursor: pointer';
            colCandidateName.textContent = toTitleCase(workflowObj.candidate.candidateFullName);
            colCandidateName.onclick = function () {
                getCandidateInfo(workflowObj.candidate.candidateId);
            };
            outerRow.appendChild(colCandidateName);

            var spanCandidateName = document.createElement("div");
            spanCandidateName.className = "col s4 hide-on-med-and-up right-align";
            spanCandidateName.textContent= "Candidate :";
            spanCandidateName.style = "font-weight: 600;font-size:12px";
            colCandidateName.appendChild(spanCandidateName);

            var colCandidateMobile = document.createElement("div");
            colCandidateMobile.className = 'col s12 m2 l2';
            colCandidateMobile.style = 'margin-top:8px';
            colCandidateMobile.textContent = workflowObj.candidate.candidateMobile;
            outerRow.appendChild(colCandidateMobile);

            var spanCandidateMobile  = document.createElement("div");
            spanCandidateMobile.className = "col s4  hide-on-med-and-up right-align";
            spanCandidateMobile.textContent= "Mobile :";
            spanCandidateMobile.style = "font-weight: 600;font-size:12px";
            colCandidateMobile.appendChild(spanCandidateMobile);

            var colChannel = document.createElement("div");
            colChannel.className = 'col s12 m2 l1';
            colChannel.style = 'margin-top: 8px';
            outerRow.appendChild(colChannel);

            if(workflowObj.applicationChannel == 1){
                colChannel.textContent = "SMS";
            } else if(workflowObj.applicationChannel == 2){
                colChannel.textContent = "Partner (" + workflowObj.partner.parnterName ;
            } else if(workflowObj.applicationChannel == 3){
                colChannel.textContent = "TruJobs Support";
            }

            var spanChannel = document.createElement("div");
            spanChannel.className = "col s4 hide-on-med-and-up right-align";
            spanChannel.textContent= "Channel:";
            spanChannel.style = "font-weight: 600;font-size:12px;";
            colChannel.appendChild(spanChannel);

            var colInterviewDateAndTime = document.createElement("div");
            colInterviewDateAndTime.className = 'col s12 m2 l3';
            colInterviewDateAndTime.style = 'margin-top: 8px';
            outerRow.appendChild(colInterviewDateAndTime);

            var interviewDate = new Date(workflowObj.extraData.interviewDate);

            colInterviewDateAndTime.textContent = validateDateFormat(interviewDate) +
                " @ " + workflowObj.extraData.interviewSlot.interviewTimeSlotName;

            var spanInterviewDate = document.createElement("div");
            spanInterviewDate.className = "col s4 hide-on-med-and-up right-align";
            spanInterviewDate.textContent= "Interview Date & Slot:";
            spanInterviewDate.style = "font-weight: 600;font-size:12px;";
            colInterviewDateAndTime.appendChild(spanInterviewDate);

            var colFeedback = document.createElement("div");
            colFeedback.className = 'col s12 m2 l3';
            colFeedback.style = 'margin-top: 8px';
            outerRow.appendChild(colFeedback);

            colFeedback.textContent = "-";

            if(workflowObj.extraData.workflowStatus != null){
                if(workflowObj.extraData.workflowStatus.statusId > JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){

                    colFeedback.textContent = workflowObj.extraData.workflowStatus.statusTitle;
                    if(workflowObj.extraData.workflowStatus.statusId == JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED){
                        colFeedback.style = "margin-top: 8px; color: green; font-weight: bold";
                    } else{
                        colFeedback.style = "margin-top: 8px; color: red; font-weight: bold";
                    }
                } else{
                    colFeedback.textContent = "";

                    var addFeedbackBtn = document.createElement("span");
                    colFeedback.appendChild(addFeedbackBtn);
                    addFeedbackBtn.style = "font-weight: bold; cursor: pointer; background: green; padding: 4px; color: white; border-radius: 4px";
                    addFeedbackBtn.textContent = "Add Feedback";
                    addFeedbackBtn.onclick = function () {
                        openFeedbackModal(workflowObj.candidate.candidateId);
                    };
                }
            }

            var spanFeedback = document.createElement("div");
            spanFeedback.className = "col s4 hide-on-med-and-up right-align";
            spanFeedback.textContent= "Interview Feedback:";
            spanFeedback.style = "font-weight: 600;font-size:12px;";
            colFeedback.appendChild(spanFeedback);

        });

        $("#confirmedApplicationTable").show();
    } else{
        $("#noConfirmedApplications").show();
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
        tabChange3(index);
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
    $("#smsReportTable").hide();
}
function hideTab2() {
    $("#noApplications").hide();
    $("#trackApplicationTable").hide();
}

function hideTab3() {
    $("#noConfirmedApplications").hide();
    $("#confirmedApplicationTable").hide();
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