/**
 * Created by hawk on 21/10/16.
 */
var jobPostId;
var globalCandidateId;
var globalInterviewStatus;
var rescheduledDate;
var rescheduledSlot;

var globalInterviewDay = null;
var globalInterviewSlot = null;
var globalSchedule = null;

var allTimeSlots = [];
var allReason = [];

var oldDate = null;

function openTrackInterview() {
    window.location = "/recruiter/job/track/" + jobPostId;
}

$(document).scroll(function(){
    if ($(this).scrollTop() > 80) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});
$(document).ready(function(){
    checkRecruiterLogin();
    getRecruiterInfo();
    $('ul.tabs').tabs();
    var pathname = window.location.pathname; // Returns path only
    var jobPostIdUrl = pathname.split('/');
    jobPostId = jobPostIdUrl[(jobPostIdUrl.length)-1];
    getAllCandidates();

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

function getAllCandidates() {
    try {
        $.ajax({
            type: "POST",
            url: "/getAllJobApplicants/" + parseInt(jobPostId),
            data: false,
            contentType: false,
            processData: false,
            success: processDataForJobApplications
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
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

function processDataUnlockedCandidates(returnedData) {
    returnedData.forEach(function (unlockedCandidate){
        try {
            $("#candidate_" + unlockedCandidate.candidate.candidateId).html(unlockedCandidate.candidate.candidateMobile);
            $("#unlock_candidate_" + unlockedCandidate.candidate.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 btn").addClass("contactUnlocked right").removeAttr('onclick');
        } catch (err){}
    });
}

function tabChange1() {
    $("#tab1").addClass("activeTab");
    $("#tab2").removeClass("activeTab");
    $("#tab3").removeClass("activeTab");
    $("#tab4").removeClass("activeTab");
}

function tabChange2() {
    $("#tab1").removeClass("activeTab");
    $("#tab2").addClass("activeTab");
    $("#tab3").removeClass("activeTab");
    $("#tab4").removeClass("activeTab");
}

function tabChange3() {
    $("#tab1").removeClass("activeTab");
    $("#tab2").removeClass("activeTab");
    $("#tab3").addClass("activeTab");
    $("#tab4").removeClass("activeTab");
}

function tabChange4() {
    $("#tab1").removeClass("activeTab");
    $("#tab2").removeClass("activeTab");
    $("#tab3").removeClass("activeTab");
    $("#tab4").addClass("activeTab");
}


function processDataForJobApplications(returnedData) {
    var pendingCount = 0;
    var confirmedCount = 0;
    var rescheduledCount = 0;
    var rejectedCount = 0;

    var pendingParent = $("#pendingCandidateContainer");
    var confirmedParent = $("#confirmedCandidateContainer");
    var rescheduledParent = $("#rescheduledCandidateContainer");
    var rejectedParent = $("#rejectedCandidateContainer");

    pendingParent.html('');
    confirmedParent.html('');
    rescheduledParent.html('');
    rejectedParent.html('');

    if(returnedData != "0"){
        var candidateList = [];
        $.each(returnedData, function (key, value) {
            if (value != null) {
                candidateList.push(value);
            }
        });

        candidateList.reverse();
        candidateList.forEach(function (value){
            var candidateCard = document.createElement("div");
            candidateCard.className = "card";
            candidateCard.style = "border-radius: 6px";

            if((value.extraData.workflowStatus.statusId == 6) || (value.extraData.workflowStatus.statusId > 9 && value.extraData.workflowStatus.statusId < 14)){
                confirmedParent.append(candidateCard);
                confirmedCount++;
            } else if(value.extraData.workflowStatus.statusId == 7){
                rejectedParent.append(candidateCard);
                rejectedCount++;
            } else if(value.extraData.workflowStatus.statusId == 8){
                rejectedParent.append(candidateCard);
                rejectedCount++;
            } else if(value.extraData.workflowStatus.statusId == 9){
                rescheduledParent.append(candidateCard);
                rescheduledCount++;
            } else{
                pendingParent.append(candidateCard);
                pendingCount++;
            }

            var candidateCardContent = document.createElement("div");
            candidateCardContent.className = "card-content";
            candidateCardContent.style = "padding: 0";
            candidateCard.appendChild(candidateCardContent);

            var candidateCardRow = document.createElement("div");
            candidateCardRow.className = "row";
            candidateCardRow.style = "padding: 6px 0 6px 0; margin: 0 2%";
            candidateCardContent.appendChild(candidateCardRow);

            var candidateCardRowColOne = document.createElement("div");
            candidateCardRowColOne.className = "col s12 l4";
            candidateCardRowColOne.style = "padding: 8px";
            candidateCardRow.appendChild(candidateCardRowColOne);

            //candidate name container
            var candidateCardRowColOneFont = document.createElement("font");
            candidateCardRowColOneFont.setAttribute("size", "5");
            candidateCardRowColOneFont.textContent = toTitleCase(value.candidate.candidateFullName);
            candidateCardRowColOne.appendChild(candidateCardRowColOneFont);

            //interview date/time slot
            var scheduledInterviewDate = document.createElement("div");
            scheduledInterviewDate.className = "col s12 l8";
            scheduledInterviewDate.style = "color: black; text-align: left; padding: 8px";
            candidateCardRow.appendChild(scheduledInterviewDate);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
            scheduledInterviewDate.appendChild(inlineBlockDiv);

            iconImg = document.createElement("img");
            iconImg.src = "/assets/recruiter/img/icons/calender.svg";
            iconImg.style = "margin-top: -4px";
            iconImg.setAttribute('height', '24px');
            inlineBlockDiv.appendChild(iconImg);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.id = "interview_div_" + value.candidate.candidateId;
            inlineBlockDiv.style = "display: inline-block;";
            scheduledInterviewDate.appendChild(inlineBlockDiv);

            innerInlineBlockDiv = document.createElement("div");
            innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px; margin-bottom: 6px";
            innerInlineBlockDiv.textContent = "Interview Details (Action Needed)";
            inlineBlockDiv.appendChild(innerInlineBlockDiv);

            var candidateInterviewDateVal = document.createElement("span");

            if(value.extraData.interviewDate != null){
                var interviewDate = new Date(value.extraData.interviewDate);
                var interviewDetails = ('0' + interviewDate.getDate()).slice(-2) + '-' + getMonthVal((interviewDate.getMonth()+1)) + " @" + value.extraData.interviewSlot.interviewTimeSlotName;

                candidateInterviewDateVal.id = "interview_date_" + value.candidate.candidateId;
            } else{
                candidateInterviewDateVal.style = "margin-left: 4px";
                interviewDetails = "Schedule not available";
            }

            candidateInterviewDateVal.textContent = interviewDetails + ". ";
            inlineBlockDiv.appendChild(candidateInterviewDateVal);

            var candidateInterviewStatusVal = document.createElement("span");
            if(value.extraData.workflowStatus != null){
                if(value.extraData.workflowStatus.statusId == 5) {
                    var interviewStatusDiv = document.createElement("span");
                    interviewStatusDiv.id = "interview_status_option_" + value.candidate.candidateId;
                    inlineBlockDiv.appendChild(interviewStatusDiv);

                    var candidateInterviewAcceptParent = document.createElement("span");
                    candidateInterviewAcceptParent.style = "display: inline-block";
                    candidateInterviewAcceptParent.onclick = function () {
                        oldDate = new Date(value.extraData.interviewDate);
                        globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                        globalInterviewSlot = value.extraData.interviewSlot.interviewTimeSlotId;
                        globalSchedule = value.extraData.interviewSchedule;
                        confirmInterviewStatus(value.candidate.candidateId);
                    };
                    interviewStatusDiv.appendChild(candidateInterviewAcceptParent);

                    var candidateInterviewAccept = document.createElement("span");
                    candidateInterviewAccept.className = "accept";
                    candidateInterviewAcceptParent.appendChild(candidateInterviewAccept);

                    iconImg = document.createElement("img");
                    iconImg.src = "/assets/recruiter/img/icons/accept.svg";
                    iconImg.setAttribute('height', '16px');
                    iconImg.setAttribute('width', '14px');
                    candidateInterviewAccept.appendChild(iconImg);

                    var actionText = document.createElement("span");
                    actionText.textContent = " Accept";
                    candidateInterviewAcceptParent.appendChild(actionText);

                    var candidateInterviewRejectParent = document.createElement("span");
                    candidateInterviewRejectParent.style = "display: inline-block";
                    candidateInterviewRejectParent.onclick = function () {
                        oldDate = new Date(value.extraData.interviewDate);
                        globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                        globalInterviewSlot = value.extraData.interviewSlot.interviewTimeSlotId;
                        globalSchedule = value.extraData.interviewSchedule;
                        rejectInterview(value.candidate.candidateId);
                    };
                    interviewStatusDiv.appendChild(candidateInterviewRejectParent);

                    var candidateInterviewReject = document.createElement("span");
                    candidateInterviewReject.className = "reject";
                    candidateInterviewRejectParent.appendChild(candidateInterviewReject);

                    iconImg = document.createElement("img");
                    iconImg.src = "/assets/recruiter/img/icons/reject.svg";
                    iconImg.setAttribute('height', '16px');
                    iconImg.setAttribute('width', '14px');
                    candidateInterviewReject.appendChild(iconImg);

                    actionText = document.createElement("span");
                    actionText.textContent = " Reject";
                    candidateInterviewRejectParent.appendChild(actionText);

                    var candidateInterviewRescheduleParent = document.createElement("span");
                    candidateInterviewRescheduleParent.style = "display: inline-block";
                    candidateInterviewRescheduleParent.onclick = function () {
                        globalCandidateId = value.candidate.candidateId;
                        oldDate = new Date(value.extraData.interviewDate);
                        globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                        globalInterviewSlot = value.extraData.interviewSlot.interviewTimeSlotId;
                        globalSchedule = value.extraData.interviewSchedule;

                        showSlotModal();
                    };
                    interviewStatusDiv.appendChild(candidateInterviewRescheduleParent);

                    var candidateInterviewReschedule = document.createElement("span");
                    candidateInterviewReschedule.className = "reschedule";
                    candidateInterviewRescheduleParent.appendChild(candidateInterviewReschedule);

                    iconImg = document.createElement("img");
                    iconImg.src = "/assets/recruiter/img/icons/reschedule.svg";
                    iconImg.setAttribute('height', '18px');
                    iconImg.setAttribute('width', '16px');
                    candidateInterviewReschedule.appendChild(iconImg);

                    actionText = document.createElement("span");
                    actionText.textContent = " Reject";
                    candidateInterviewRescheduleParent.appendChild(actionText);

                } else if((value.extraData.workflowStatus.statusId == 6) || (value.extraData.workflowStatus.statusId > 9 && value.extraData.workflowStatus.statusId < 14)){
                    candidateInterviewStatusVal.textContent = "Interview Confirmed";
                    candidateInterviewStatusVal.style = "color: green; font-weight: bold";
                } else if(value.extraData.workflowStatus.statusId == 7){
                    candidateInterviewStatusVal.textContent = "Interview Rejected";
                    candidateInterviewStatusVal.style = "color: red; font-weight: bold";
                } else if(value.extraData.workflowStatus.statusId == 8){
                    candidateInterviewStatusVal.textContent = "Interview Rejected by Candidate";
                    candidateInterviewStatusVal.style = "color: red; font-weight: bold";
                } else if(value.extraData.workflowStatus.statusId == 9){
                    candidateInterviewStatusVal.textContent = "Interview Rescheduled. Awaiting candidate's response";
                    candidateInterviewStatusVal.style = "color: orange; font-weight: bold";
                } else{
                    candidateInterviewStatusVal.textContent = "TODO! No Slots availabe.";
                }
            }

            inlineBlockDiv.appendChild(candidateInterviewStatusVal);

            //end of candidateCardRow

            var candidateCardDivider = document.createElement("div");
            candidateCardDivider.className = "divider";
            candidateCardContent.appendChild(candidateCardDivider);

            candidateCardRow = document.createElement("div");
            candidateCardRow.className = "row";
            candidateCardRow.style = "padding: 10px 2%;margin: 0";
            candidateCardContent.appendChild(candidateCardRow);

            candidateCardRowColOne = document.createElement("div");
            candidateCardRowColOne.className = "col s12 l4";
            candidateCardRowColOne.style = "margin-top: 4px";
            candidateCardRow.appendChild(candidateCardRowColOne);

            var inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            var iconImg = document.createElement("img");
            iconImg.src = "/assets/recruiter/img/icons/locality.svg";
            iconImg.style = "margin-top: -4px";
            iconImg.setAttribute('height', '24px');
            inlineBlockDiv.appendChild(iconImg);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            var innerInlineBlockDiv = document.createElement("div");
            innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
            innerInlineBlockDiv.textContent = "Home Locality";
            inlineBlockDiv.appendChild(innerInlineBlockDiv);

            var candidateLocalityVal = document.createElement("div");
            candidateLocalityVal.style = "margin-left: 4px";
            if(value.candidate.locality != null){
                candidateLocalityVal.textContent = value.candidate.locality.localityName;
            } else{
                candidateLocalityVal.textContent = "Not Specified";
            }
            inlineBlockDiv.appendChild(candidateLocalityVal);

            /* second col */
            candidateCardRowColOne = document.createElement("div");
            candidateCardRowColOne.className = "col s12 l4";
            candidateCardRowColOne.style = "margin-top: 4px";
            candidateCardRow.appendChild(candidateCardRowColOne);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            iconImg = document.createElement("img");
            iconImg.src = "/assets/recruiter/img/icons/gender.svg";
            iconImg.style = "margin-top: -4px";
            iconImg.setAttribute('height', '24px');
            inlineBlockDiv.appendChild(iconImg);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            innerInlineBlockDiv = document.createElement("div");
            innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
            innerInlineBlockDiv.textContent = "Gender";
            inlineBlockDiv.appendChild(innerInlineBlockDiv);

            candidateLocalityVal = document.createElement("div");
            candidateLocalityVal.style = "margin-left: 4px";
            if(value.candidate.candidateGender != null){
                if(value.candidate.candidateGender == 0){
                    candidateLocalityVal.textContent = "Male";
                } else{
                    candidateLocalityVal.textContent = "Female";
                }
            } else{
                candidateLocalityVal.textContent = "Not Specified";
            }
            inlineBlockDiv.appendChild(candidateLocalityVal);

            /* second col */
            candidateCardRowColOne = document.createElement("div");
            candidateCardRowColOne.className = "col s12 l4";
            candidateCardRowColOne.style = "margin-top: 4px";
            candidateCardRow.appendChild(candidateCardRowColOne);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            iconImg = document.createElement("img");
            iconImg.src = "/assets/recruiter/img/icons/age.svg";
            iconImg.style = "margin-top: -4px";
            iconImg.setAttribute('height', '24px');
            inlineBlockDiv.appendChild(iconImg);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            innerInlineBlockDiv = document.createElement("div");
            innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
            innerInlineBlockDiv.textContent = "Age";
            inlineBlockDiv.appendChild(innerInlineBlockDiv);

            var candidateAgeVal = document.createElement("div");
            candidateAgeVal.style = "margin-left: 4px";
            if (value.candidate.candidateDOB != null) {
                var date = JSON.parse(value.candidate.candidateDOB);
                var yr = new Date(date).getFullYear();
                var month = ('0' + parseInt(new Date(date).getMonth() + 1)).slice(-2);
                var d = ('0' + new Date(date).getDate()).slice(-2);
                var today = new Date();
                var birthDate = new Date(yr + "-" + month + "-" + d);
                var age = today.getFullYear() - birthDate.getFullYear();
                var m = today.getMonth() - birthDate.getMonth();
                if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
                    age--;
                }
                candidateAgeVal.textContent = age + " years";
            } else{
                candidateAgeVal.textContent = "Not Specified";
            }
            inlineBlockDiv.appendChild(candidateAgeVal);

            candidateCardRow = document.createElement("div");
            candidateCardRow.className = "row";
            candidateCardRow.style = "padding: 10px 2%;margin: 0";
            candidateCardContent.appendChild(candidateCardRow);

            candidateCardRowColOne = document.createElement("div");
            candidateCardRowColOne.className = "col s12 l4";
            candidateCardRowColOne.style = "margin-top: 4px";
            candidateCardRow.appendChild(candidateCardRowColOne);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            iconImg = document.createElement("img");
            iconImg.src = "/assets/recruiter/img/icons/education.svg";
            iconImg.style = "margin-top: -4px";
            iconImg.setAttribute('height', '24px');
            inlineBlockDiv.appendChild(iconImg);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            innerInlineBlockDiv = document.createElement("div");
            innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
            innerInlineBlockDiv.textContent = "Education";
            inlineBlockDiv.appendChild(innerInlineBlockDiv);

            var candidateEducationVal = document.createElement("div");
            candidateEducationVal.style = "margin-left: 4px";
            candidateEducationVal.textContent = "Not Specified";
            if(value.candidate.candidateEducation){
                if(value.candidate.candidateEducation.education != null){
                    if(candidateEducationVal.textContent = value.candidate.candidateEducation.education.educationId > 3){
                        var eduVal = value.candidate.candidateEducation.education.educationName;
                        if(value.candidate.candidateEducation.degree != null){
                            eduVal = eduVal + " (" + value.candidate.candidateEducation.degree.degreeName;
                            if(value.candidate.candidateEducation.candidateEducationCompletionStatus != null){
                                if(value.candidate.candidateEducation.candidateEducationCompletionStatus == 1){
                                    eduVal = eduVal + ", Completed)";
                                } else{
                                    eduVal = eduVal + ", Incomplete)";
                                }
                            } else{
                                eduVal = eduVal + ", Not specified)";
                            }
                        }
                        candidateEducationVal.textContent = eduVal;
                    } else{
                        candidateEducationVal.textContent = value.candidate.candidateEducation.education.educationName;
                    }

                }
            }
            inlineBlockDiv.appendChild(candidateEducationVal);

            /* second col */
            candidateCardRowColOne = document.createElement("div");
            candidateCardRowColOne.className = "col s12 l4";
            candidateCardRowColOne.style = "margin-top: 4px";
            candidateCardRow.appendChild(candidateCardRowColOne);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            iconImg = document.createElement("img");
            iconImg.src = "/assets/recruiter/img/icons/exp.svg";
            iconImg.style = "margin-top: -4px";
            iconImg.setAttribute('height', '24px');
            inlineBlockDiv.appendChild(iconImg);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            innerInlineBlockDiv = document.createElement("div");
            innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
            innerInlineBlockDiv.textContent = "Experience";
            inlineBlockDiv.appendChild(innerInlineBlockDiv);

            var candidateExperienceVal = document.createElement("div");
            candidateExperienceVal.style = "margin-left: 4px";
            if(value.candidate.candidateTotalExperience != null){
                if(value.candidate.candidateTotalExperience == 0){
                    candidateExperienceVal.textContent = "Fresher";
                } else{
                    var yrs = parseInt(value.candidate.candidateTotalExperience/12);
                    var mnths = (value.candidate.candidateTotalExperience) % 12;

                    if(yrs == 0){
                        candidateExperienceVal.textContent = mnths + " months";
                    } else if(mnths == 0){
                        candidateExperienceVal.textContent = yrs + " years";
                    } else{
                        candidateExperienceVal.textContent = yrs + " years and " + mnths + " months";
                    }
                }
            } else{
                candidateExperienceVal.textContent = "Not Specified";
            }
            inlineBlockDiv.appendChild(candidateExperienceVal);

            /* second col */
            candidateCardRowColOne = document.createElement("div");
            candidateCardRowColOne.className = "col s12 l4";
            candidateCardRowColOne.style = "margin-top: 4px";
            candidateCardRow.appendChild(candidateCardRowColOne);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            iconImg = document.createElement("img");
            iconImg.src = "/assets/recruiter/img/icons/salary.svg";
            iconImg.style = "margin-top: -4px";
            iconImg.setAttribute('height', '24px');
            inlineBlockDiv.appendChild(iconImg);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            innerInlineBlockDiv = document.createElement("div");
            innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
            innerInlineBlockDiv.textContent = "Last Withdrawn Salary";
            inlineBlockDiv.appendChild(innerInlineBlockDiv);

            var candidateLastWithdrawnSalaryVal = document.createElement("div");
            candidateLastWithdrawnSalaryVal.style = "margin-left: 4px";
            if(value.candidate.candidateLastWithdrawnSalary != null){
                if(value.candidate.candidateLastWithdrawnSalary == 0){
                    if(value.candidate.candidateTotalExperience != null){
                        if(value.candidate.candidateTotalExperience == 0){
                            candidateLastWithdrawnSalaryVal.textContent = " - (Fresher)";
                        }
                    } else{
                        candidateLastWithdrawnSalaryVal.textContent = "Not Specified";
                    }
                } else{
                    candidateLastWithdrawnSalaryVal.textContent = "₹" + rupeeFormatSalary(value.candidate.candidateLastWithdrawnSalary);
                }
            } else{
                candidateLastWithdrawnSalaryVal.textContent = "Not Specified";
            }
            inlineBlockDiv.appendChild(candidateLastWithdrawnSalaryVal);

            candidateCardRow = document.createElement("div");
            candidateCardRow.className = "row";
            candidateCardRow.style = "padding: 10px 2%;margin: 0";
            candidateCardContent.appendChild(candidateCardRow);

            candidateCardRowColOne = document.createElement("div");
            candidateCardRowColOne.className = "col s12 l4";
            candidateCardRowColOne.style = "margin-top: 4px";
            candidateCardRow.appendChild(candidateCardRowColOne);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            iconImg = document.createElement("img");
            iconImg.src = "/assets/recruiter/img/icons/language.svg";
            iconImg.style = "margin-top: -4px";
            iconImg.setAttribute('height', '24px');
            inlineBlockDiv.appendChild(iconImg);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block;";
            candidateCardRowColOne.appendChild(inlineBlockDiv);

            innerInlineBlockDiv = document.createElement("div");
            innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
            innerInlineBlockDiv.textContent = "Language(s)";
            inlineBlockDiv.appendChild(innerInlineBlockDiv);

            var candidateLanguageVal = document.createElement("div");
            candidateLanguageVal.style = "margin-left: 4px";
            var langList = value.candidate.languageKnownList;
            var langListCount = Object.keys(langList).length;
            if(langListCount > null){
                var langVal = "";
                langList.forEach(function (language){
                    langVal += language.language.languageName + ", ";
                });
                candidateLanguageVal.textContent = langVal.substring(0, langVal.length - 2);
            } else{
                candidateLanguageVal.textContent = "Not specified";
            }
            inlineBlockDiv.appendChild(candidateLanguageVal);

            //skills
            candidateCardRowColTwo = document.createElement("div");
            candidateCardRowColTwo.className = "col s12 l4";
            candidateCardRowColTwo.style = "margin-top: 4px";
            candidateCardRow.appendChild(candidateCardRowColTwo);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
            candidateCardRowColTwo.appendChild(inlineBlockDiv);

            iconImg = document.createElement("img");
            iconImg.src = "/assets/recruiter/img/icons/skills.svg";
            iconImg.style = "margin-top: -4px";
            iconImg.setAttribute('height', '24px');
            inlineBlockDiv.appendChild(iconImg);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block;";
            candidateCardRowColTwo.appendChild(inlineBlockDiv);

            innerInlineBlockDiv = document.createElement("div");
            innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
            innerInlineBlockDiv.textContent = "Skills(s)";
            inlineBlockDiv.appendChild(innerInlineBlockDiv);

            var candidateSkillVal = document.createElement("div");
            candidateSkillVal.style = "margin-left: 4px";
            candidateSkillVal.id = "skill_" + value.candidate.candidateId;
            var skillList = value.candidate.candidateSkillList;
            var skillListCount = Object.keys(skillList).length;
            if(skillListCount > 0){
                var skillVal = "";
                var allSkillVal = "";
                var count = 0;
                var skillCount = 0;
                skillList.forEach(function (skill){
                    count = count + 1;
                    if(count < 4){
                        if(skill.candidateSkillResponse == true){
                            skillVal += skill.skill.skillName + ", ";
                            allSkillVal += skill.skill.skillName + ", ";
                            skillCount ++;
                        }
                    } else{
                        if(skill.candidateSkillResponse == true){
                            allSkillVal += skill.skill.skillName + ", ";
                        }
                    }
                });
                candidateSkillVal.textContent = skillVal.substring(0, skillVal.length - 2);
            } else{
                candidateSkillVal.textContent = "Not specified";
            }
            inlineBlockDiv.appendChild(candidateSkillVal);

            if(skillListCount > 3){
                var toolTip = document.createElement("a");
                toolTip.className = "tooltipped";
                toolTip.style = "cursor: pointer; text-decoration: none";
                toolTip.setAttribute("data-postiton", "top");
                toolTip.setAttribute("data-delay", "50");
                toolTip.setAttribute("data-tooltip", allSkillVal.substring(0, allSkillVal.length - 2));
                toolTip.textContent = ", more";
                candidateSkillVal.appendChild(toolTip);
            }

            //documents
            var candidateCardRowColThree = document.createElement("div");
            candidateCardRowColThree.className = "col s12 l4";
            candidateCardRowColThree.style = "margin-top: 4px";
            candidateCardRow.appendChild(candidateCardRowColThree);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
            candidateCardRowColThree.appendChild(inlineBlockDiv);

            iconImg = document.createElement("img");
            iconImg.src = "/assets/recruiter/img/icons/document.svg";
            iconImg.style = "margin-top: -4px";
            iconImg.setAttribute('height', '24px');
            inlineBlockDiv.appendChild(iconImg);

            inlineBlockDiv = document.createElement("div");
            inlineBlockDiv.style = "display: inline-block;";
            candidateCardRowColThree.appendChild(inlineBlockDiv);

            innerInlineBlockDiv = document.createElement("div");
            innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
            innerInlineBlockDiv.textContent = "Documents(s)";
            inlineBlockDiv.appendChild(innerInlineBlockDiv);

            var candidateDocumentVal = document.createElement("div");
            candidateDocumentVal.style = "margin-left: 4px";
            candidateDocumentVal.id = "document_" + value.candidate.candidateId;

            var documentList = value.candidate.idProofReferenceList;
            var documentListCount = Object.keys(documentList).length;

            if(documentListCount > 0){
                var allDocumentVal = "";
                var documentVal = "";
                var count = 0;
                documentList.forEach(function (document){
                    count = count +1;
                    if(count < 4){
                        if(document.idProof != null){
                            documentVal += document.idProof.idProofName + ", ";
                            allDocumentVal += document.idProof.idProofName + ", ";
                        }
                    } else{
                        allDocumentVal += document.idProof.idProofName + ", ";
                    }
                });
                candidateDocumentVal.textContent = documentVal.substring(0, documentVal.length - 2);
            } else{
                candidateDocumentVal.textContent = "Not specified";
            }
            inlineBlockDiv.appendChild(candidateDocumentVal);

            if(documentListCount > 3){
                var toolTip = document.createElement("a");
                toolTip.className = "tooltipped";
                toolTip.style = "cursor: pointer; text-decoration: none";
                toolTip.setAttribute("data-postiton", "top");
                toolTip.setAttribute("data-delay", "50");
                toolTip.setAttribute("data-tooltip", allDocumentVal.substring(0, allDocumentVal.length - 2));
                toolTip.textContent = ", more";
                candidateSkillVal.appendChild(toolTip);
            }

            var hr = document.createElement("hr");
            candidateCardContent.appendChild(hr);

            var unlockDivRow = document.createElement("div");
            unlockDivRow.className = "row";
            unlockDivRow.style = "padding: 0 2% 1% 2%; margin: 0; text-align: right; color: #fff";
            candidateCardContent.appendChild(unlockDivRow);

            var candidateCardRowColTwo = document.createElement("div");
            candidateCardRowColTwo.className = "col s12 l8";
            candidateCardRowColTwo.style = "text-align: left; color: black";
            unlockDivRow.appendChild(candidateCardRowColTwo);

            //candidate last active container
            var candidateCardRowColTwoFont = document.createElement("font");
            candidateCardRowColTwoFont.setAttribute("size", "3");

            candidateCardRowColTwoFont.textContent = "Last Active: " + value.extraData.lastActive.lastActiveValueName;
            candidateCardRowColTwo.appendChild(candidateCardRowColTwoFont);

            var unlockContactCol = document.createElement("div");
            unlockContactCol.className = "col s12 l4";
            unlockDivRow.appendChild(unlockContactCol);

            //unlock candidate div
            var unlockCandidateBtn = document.createElement("div");
            unlockCandidateBtn.id = "unlock_candidate_" + value.candidate.candidateId;
            unlockCandidateBtn.onclick = function () {
                unlockContact(value.candidate.candidateId);
            };
            unlockCandidateBtn.className = "waves-effect waves-light ascentGreen lighten-1 btn";
            unlockContactCol.appendChild(unlockCandidateBtn);

            //candidate unlock container
            var candidateUnlockFont = document.createElement("font");
            candidateUnlockFont.id = "candidate_" + value.candidate.candidateId;
            candidateUnlockFont.textContent = "Unlock Contact";
            candidateUnlockFont.style = "font-weight: bold; font-size: 14px";
            unlockCandidateBtn.appendChild(candidateUnlockFont);
        });
        $('.tooltipped').tooltip({delay: 50});

        if(pendingCount == 0){
            $("#noPendingApplication").show();
        } else{
            $("#noPendingApplication").hide();
        }

        if(confirmedCount == 0){
            $("#noConfirmedApplication").show();
        } else{
            $("#noConfirmedApplication").hide();
        }

        if(rejectedCount == 0){
            $("#noRejectedApplication").show();
        } else{
            $("#noRejectedApplication").hide();
        }

        if(rescheduledCount == 0){
            $("#noRescheduledApplication").show();
        } else{
            $("#noRescheduledApplication").hide();
        }

        try {
            $.ajax({
                type: "POST",
                url: "/recruiter/api/getUnlockedCandidates/",
                async: true,
                contentType: false,
                data: false,
                success: processDataUnlockedCandidates
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }

    } else{
        logoutRecruiter();
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

                    if((oldSelectedDate.getDate() == x.getDate()) && (oldSelectedDate.getMonth() == x.getMonth()) && (oldSlot == timeSlot.interviewTimeSlot.interviewTimeSlotId)){} else{
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

function confirmRejectInterview(){
    if($("#reject_reason").val() != 0){
        globalInterviewStatus = 2;
        setInterviewStatus(globalCandidateId, 2, globalInterviewDay, globalInterviewSlot, $("#reject_reason").val());
    } else{
        notifyError("Please specify the reason for the job application rejection");
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

function processDataInterviewStatus(returnedData) {
    $('.tooltipped').tooltip('remove');
    $("#modalRescheduleSlot").closeModal();
    if(returnedData == "1"){
        $("#interview_status_option_" + globalCandidateId).remove();

        var candidateInterviewStatusVal = document.createElement("span");
        if(globalInterviewStatus == 1){
            notifySuccess("Interview Confirmed"); //accepted
            candidateInterviewStatusVal.textContent = "Interview Confirmed";
            candidateInterviewStatusVal.style = "margin-left: 8px; color: green; font-weight: 600";
        } else if(globalInterviewStatus == 2){ //rejected by recruiter
            $("#modalRejectReason").closeModal();
            notifySuccess("Interview Rejected");
            candidateInterviewStatusVal.textContent = "Interview Rejected";
            candidateInterviewStatusVal.style = "margin-left: 8px; color: red; font-weight: 600";
        } else if(globalInterviewStatus == 3){
            notifySuccess("Interview Rescheduled");
            candidateInterviewStatusVal.textContent = "Interview Rescheduled. Awaiting Candidate's confirmation";
            candidateInterviewStatusVal.style = "margin-left: 8px; color: orange; font-weight: 600";
            var newDate = new Date(rescheduledDate);
            var i, newSlot;
            for(i=0; i<Object.keys(allTimeSlots).length; i++){
                if(allTimeSlots[i].id == rescheduledSlot){
                    newSlot = allTimeSlots[i].name;
                }
            }
            $("#interview_date_" + globalCandidateId).html(('0' + newDate.getDate()).slice(-2) + '-' + getMonthVal((newDate.getMonth()+1)) + " @" + newSlot);
        }
        $("#interview_div_" + globalCandidateId).append(candidateInterviewStatusVal);
        getAllCandidates();
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

function closeCreditModal() {
    $("#modalBuyCredits").closeModal();
}

function closeRescheduleModal() {
    $("#modalRescheduleSlot").closeModal();
}

function closeRejectModal() {
    $("#modalRejectReason").closeModal();
}

function openCreditModal(){
    $("#successMsg").hide();
    $("#modalBuyCredits").openModal();
}

function submitCredits() {
    $("#successMsg").hide();
    var contactCreditStatus = 1;
    var interviewCreditStatus = 1;

    if($("#contactCreditAmount").val() == undefined || $("#contactCreditAmount").val() == null || $("#contactCreditAmount").val() == ""){
        contactCreditStatus = 0;
    }
    if($("#interviewCreditAmount").val() == undefined || $("#interviewCreditAmount").val() == null || $("#interviewCreditAmount").val() == ""){
        interviewCreditStatus = 0;
    }

    if(interviewCreditStatus == 0 && contactCreditStatus == 0){
        notifyError("Please specify no. of credits!");
    } else{
        contactCreditStatus = 1;
        interviewCreditStatus = 1;

        var contactCredits = 0;
        var interviewCredits = 0;
        if($("#contactCreditAmount").val() != ""){
            contactCredits = parseInt($("#contactCreditAmount").val());
            if(contactCredits < 1){
                notifyError("Contact credits cannot be less than 1");
                contactCreditStatus = 0;
            }
        }
        if($("#interviewCreditAmount").val() != ""){
            interviewCredits = parseInt($("#interviewCreditAmount").val());
            if(interviewCredits < 1){
                notifyError("Interview credits cannot be less than 1");
                interviewCreditStatus = 0;
            }
        }

        if(interviewCreditStatus != 0 && contactCreditStatus != 0){
            $("#requestCredits").addClass("disabled");
            var d = {
                noOfContactCredits: contactCredits,
                noOfInterviewCredits: interviewCredits
            };

            $.ajax({
                type: "POST",
                url: "/recruiter/api/requestCredits/",
                async: true,
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataAddCreditRequest
            });
        }
    }
}

function processDataAddCreditRequest(returnedData) {
    $("#requestCredits").removeClass("disabled");
    if(returnedData.status == 1){
        $("#successMsg").show();
        notifySuccess("Thanks! We have received your request to buy more credits. Our business team will contact you within 24hrs")
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

function unlockContact(candidateId){
    if(candidateId != null || candidateId != undefined){
        candidateIdVal = candidateId;
        try {
            $.ajax({
                type: "POST",
                url: "/recruiter/unlockCandidateContact/" + candidateId,
                async: false,
                contentType: false,
                processData: false,
                success: processDataUnlockCandidate
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

function processDataUnlockCandidate(returnedData) {
    if(returnedData.status == 1){
        notifySuccess("Contact successfully unlocked");
        getRecruiterInfo();
        $("#candidate_" + candidateIdVal).html(returnedData.candidateMobile);
        $("#unlock_candidate_" + returnedData.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 btn").addClass("contactUnlocked right").removeAttr('onclick');
    } else if(returnedData.status == 2){
        notifySuccess("You have already unlocked the candidate");
        getRecruiterInfo();
        $("#unlock_candidate_" + returnedData.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 btn").addClass("contactUnlocked right").removeAttr('onclick');
        $("#candidate_" + candidateIdVal).html(returnedData.candidateMobile);
    } else if(returnedData.status == 3){
        notifyError("Out of credits! Please recharge");
        openCreditModal();
    }
}

function getRecruiterInfo() {
    try {
        $.ajax({
            type: "GET",
            url: "/getRecruiterProfileInfo",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataRecruiterProfile(returnedData) {
    var creditHistoryList = returnedData.recruiterCreditHistoryList;
    creditHistoryList.reverse();
    var contactCreditCount = 0;
    var interviewCreditCount = 0;
    creditHistoryList.forEach(function (creditHistory){
        if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 1){
            if(contactCreditCount == 0){
                $("#remainingContactCredits").html(creditHistory.recruiterCreditsAvailable);
                $("#remainingContactCreditsMobile").html(creditHistory.recruiterCreditsAvailable);
                contactCreditCount = 1;
            }
        } else{
            if(interviewCreditCount == 0){
                if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 2){
                    $("#remainingInterviewCredits").html(creditHistory.recruiterCreditsAvailable);
                    $("#remainingInterviewCreditsMobile").html(creditHistory.recruiterCreditsAvailable);
                    interviewCreditCount = 1;
                }
            }
        }
        if(contactCreditCount > 0 && interviewCreditCount > 0){
            return false;
        }
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

function validateTimeSlot(val, text) {
    if(val.localeCompare(text) == 0){
        $('#rescheduleDateAndSlot').tokenize().tokenRemove(val);
        notifyError("Please select a valid date and time from the dropdown list");
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

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}