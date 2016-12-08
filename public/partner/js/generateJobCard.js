var jobPostId;
var candidateInfo;
var candidateId;

var appliedJobSection;
var popularJobsSection;

var prefLocation;
var prefLocationName;

var prefTimeSlot;

var scheduledInterviewDate;

var globalJpId;
var globalInterviewStatus;
var rescheduledDate;
var jobRoleName;
var companyName;

var allReasons = [];
var globalStatus = 0;

$(window).resize(function(){
    var w = window.innerWidth;
    if(w < 640){
        $(".candidatePartner").removeClass("row-eq-height");
    } else{
        $(".candidatePartner").removeClass("row-eq-height").addClass("row-eq-height");
    }
});

function scrapeCandidateIdFromUrl(){
    var pathname = window.location.pathname; // Returns path only
    var partnerUrl = pathname.split('/');
    var cId = partnerUrl[(partnerUrl.length) - 2];
    candidateId = parseInt(cId);
}

$(document).ready(function(){
    var w = window.innerWidth;
    appliedJobSection = false;
    popularJobsSection = true;
    $("#appliedJobsSection").hide();

    try {
        $.ajax({
            type: "POST",
            url: "/getAllInterviewNotGoingReasons",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetAllReason
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            url: "/getCandidateMatchingJobs/" + localStorage.getItem("candidateId"),
            type: "POST",
            data: false,
            dataType: "json",
            contentType: false,
            processData: false,
            success: processDataAllJobPosts
        });
    } catch (exception) {}
    if(localStorage.getItem("appliedJobs") == '1'){
        $("#appliedJobsSection").show();
        $("#applyJobs").hide();
        $(".viewPopularJobs").removeClass("white").addClass("white");
        $(".viewAppliedJobs").removeClass("white");
        localStorage.setItem("appliedJobs", "0");
    }
});

function processDataGetAllReason(returnedData) {
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allReasons.push(item);
    });
}

function toggleTabs(index) {
    if(index == 0){
        popularJobsSection = true;
        appliedJobSection = false;
        $("#applyJobs").show();
        $("#appliedJobsSection").hide();
        $(".viewPopularJobs").removeClass("white");
        $(".viewAppliedJobs").removeClass("white").addClass("white");
    } else{
        getAllAppliedJobs();
        popularJobsSection = true;
        appliedJobSection = false;
        $("#applyJobs").hide();
        $("#appliedJobsSection").show();
        $(".viewPopularJobs").removeClass("white").addClass("white");
        $(".viewAppliedJobs").removeClass("white");

    }
}

function getAllAppliedJobs() {
    try {
        var table = $('table#appliedJobs').DataTable({
            "ajax": {
                "url": "/getAppliedJobsByPartnerForCandidate/" + localStorage.getItem("candidateId"),
                "dataSrc": function (returnedData) {
                    var returned_data = new Array();
                    returnedData.forEach(function (jobApplication) {
                        if(jobApplication != '0'){
                            var appliedDateInMillis = new Date(jobApplication.creationTimestamp);
                            var salary;
                            if(jobApplication.jobPost.jobPostMaxSalary != null && jobApplication.jobPost.jobPostMaxSalary != 0){
                                salary = "₹" + rupeeFormatSalary(jobApplication.jobPost.jobPostMinSalary) + " - ₹" + rupeeFormatSalary(jobApplication.jobPost.jobPostMaxSalary);
                            } else{
                                salary = "₹" + rupeeFormatSalary(jobApplication.jobPost.jobPostMinSalary);
                            }
                            //getting interview details
                            var interviewDetails = "Not specified";
                            if(jobApplication.scheduledInterviewDate != null){
                                var interviewDate = new Date(jobApplication.scheduledInterviewDate);
                                interviewDetails = ('0' + interviewDate.getDate()).slice(-2) + '-' + getMonthVal((interviewDate.getMonth()+1)) + " @" + jobApplication.scheduledInterviewTimeSlot.interviewTimeSlotName;
                            }
                            //partner Incentives
                            var interviewIncentive = "Not Specified";
                            var joiningIncentive = "Not Specified";
                            if(jobApplication.jobPost.jobPostPartnerInterviewIncentive != null){
                                interviewIncentive = "₹" + jobApplication.jobPost.jobPostPartnerInterviewIncentive;
                            }

                            if(jobApplication.jobPost.jobPostPartnerJoiningIncentive != null){
                                joiningIncentive = "₹" + jobApplication.jobPost.jobPostPartnerJoiningIncentive;
                            }

                            var currentStatus = "Under Review";
                            if(jobApplication.status.statusId > JWF_STATUS_INTERVIEW_SCHEDULED){
                                if(jobApplication.status.statusId > JWF_STATUS_INTERVIEW_RESCHEDULE && jobApplication.status.statusId < JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED) {
                                    currentStatus = '<div style="width:100%; color: green; text-align: center">Interview Confirmed</div>';
                                    if (jobApplication.interviewLocationLat != null) {
                                        currentStatus += '<div class="navigationBtn" onclick="navigateToLocation(' + jobApplication.interviewLocationLat + ', ' + jobApplication.interviewLocationLng + ')">Directions</div>'
                                    }

                                    var today = new Date();
                                    var interviewDate = new Date(jobApplication.scheduledInterviewDate);
                                    if(interviewDate.getDate() == today.getDate() && interviewDate.getMonth() == today.getMonth() && interviewDate.getFullYear() == today.getFullYear()) { // today's schedule
                                        var candidateStatus ='<div class="mLabel" style="width:100%;">Status not available</div>';

                                        // candidate status
                                        if(jobApplication.status.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){
                                            candidateStatus = '<div class="mLabel" style="width:100%; color: green">Reached</div>';
                                        } else{
                                            if(jobApplication.status.statusId > JWF_STATUS_INTERVIEW_RESCHEDULE && jobApplication.status.statusId < JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){
                                                candidateStatus = '<div style="width:100%; margin-top: 8px; text-align: center">' + jobApplication.status.statusTitle + '</div>';
                                                if(jobApplication.status.statusId == JWF_STATUS_INTERVIEW_CONFIRMED){ // interview confirmed
                                                    candidateStatus = '<div style="width:100%; margin-top: 8px;  text-align: center">Status not available</div>';
                                                }
                                                candidateStatus += '<select id="candidate_interview_status_' + jobApplication.jobPost.jobPostId +'" style="width: 100%">' +
                                                    '<option value = 0>Select a Status</option>';

                                                if(jobApplication.status.statusId == JWF_STATUS_INTERVIEW_CONFIRMED || jobApplication.status.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING){
                                                    if(jobApplication.status.statusId == JWF_STATUS_INTERVIEW_CONFIRMED){
                                                        candidateStatus += '<option value = 1>Not Going</option>';
                                                    }
                                                    candidateStatus += '<option value = 2>Delayed</option>' +
                                                        '<option value = 3>Started</option>' +
                                                        '<option value = 4>Reached</option>';
                                                } else if(jobApplication.status.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_DELAYED){
                                                    candidateStatus += '<option value = 3>Started</option>' +
                                                        '<option value = 4>Reached</option>';
                                                } else if(jobApplication.status.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_STARTED){
                                                    candidateStatus += '<option value = 2>Delayed</option>' +
                                                        '<option value = 4>Reached</option>';
                                                }
                                                candidateStatus +='</select><div class="navigationBtn" style="margin-top: 4px" onclick="updateCandidateStatus(' + jobApplication.jobPost.jobPostId + ')">Update</div>';
                                            }
                                        }
                                        currentStatus += candidateStatus;
                                    }
                                } else if(jobApplication.status.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT){
                                    currentStatus = "Application rejected";
                                } else if(jobApplication.status.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE){
                                    currentStatus = "Application rejected by the Candidate/Partner";
                                } else if(jobApplication.status.statusId == JWF_STATUS_INTERVIEW_RESCHEDULE){
                                    var jpId = jobApplication.jobPost.jobPostId;
                                    rescheduledDate = "Interview Rescheduled on " + new Date(jobApplication.scheduledInterviewDate).getDate() + "/" + (new Date(jobApplication.scheduledInterviewDate).getMonth() + 1) + "/" + new Date(jobApplication.scheduledInterviewDate).getFullYear() + " between " + jobApplication.scheduledInterviewTimeSlot.interviewTimeSlotName;
                                    currentStatus = rescheduledDate;
                                    currentStatus += '<span id="interview_status_option_' + jpId + '">' +
                                        '<span class="accept" onclick="confirmInterview('+ jpId + ', 1);"><img src="/assets/recruiter/img/icons/accept.svg" height="16px" width="14px"></span>' +
                                        '<span class="reject" onclick="confirmInterview('+ jpId + ', 0);"><img src="/assets/recruiter/img/icons/reject.svg" height="16px" width="14px"></span>' +
                                        '</span>';
                                } else if(jobApplication.status.statusId > JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){
                                    currentStatus = jobApplication.status.statusTitle;
                                }
                            }

                            var varColumn = function () {
                                if(jobApplication.preScreenRequired){
                                    if(candidateId == null ) {
                                        scrapeCandidateIdFromUrl();
                                    }
                                    // jpId is jobPostId
                                    var jpId = jobApplication.jobPost.jobPostId;
                                    jobRoleName = jobApplication.jobPost.jobRole.jobName;
                                    companyName = jobApplication.jobPost.company.companyName;
                                    return '<input type="submit" value="Pre-Screen"  style="width:150px" onclick="openPartnerPreScreenModal(' + jpId+ ', ' + candidateId + ');" id="' + candidateInfo.lead.leadId + '" class="btn btn-primary">'
                                } else {
                                    return '<div class="mLabel" style="width:100%" >Completed</div>';
                                }
                            };


                            returned_data.push({
                                'jobPostName' : '<div class="mLabel" style="width:100%" >'+ jobApplication.jobPost.jobPostTitle + '</div>',
                                'jobPostCompany' : '<div class="mLabel" style="width:100%" >'+ jobApplication.jobPost.company.companyName + '</div>',
                                'jobPostSalary' : '<div class="mLabel" style="width:100%" >'+ salary + '</div>',
                                'interviewIncentive' : '<div class="mLabel" style="width:100%" >'+ interviewIncentive + '</div>',
                                'joiningIncentive' : '<div class="mLabel" style="width:100%" >'+ joiningIncentive + '</div>',
                                'interviewDetails' : '<div class="mLabel" style="width:100%" >'+ interviewDetails + '</div>',
                                'jobAppliedOn' : '<div class="mLabel" style="width:100%" >'+ ('0' + appliedDateInMillis.getDate()).slice(-2) + '-' + getMonthVal((appliedDateInMillis.getMonth()+1)) + '-' + appliedDateInMillis.getFullYear() + '</div>',
                                'preScreen' : varColumn,
                                'jobStatus' : '<div class="mLabel" id="status_' + jobApplication.jobPost.jobPostId + '" style="width:100%" >'+ currentStatus + '</div>'
                            });
                            returnedData.forEach(function (jobApplication) {
                                var appliedJob = $("#apply_btn_" + jobApplication.jobPost.jobPostId);
                                appliedJob.addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied");
                                appliedJob.attr('onclick','').unbind('click');
                            });
                        }
                    });
                    return returned_data;
                }
            },

            "deferRender": true,
            "order": [[6, "asc"]],
            "columns": [
                { "data": "jobPostName" },
                { "data": "jobPostCompany" },
                { "data": "jobPostSalary" },
                { "data": "interviewIncentive" },
                { "data": "joiningIncentive" },
                { "data": "interviewDetails" },
                { "data": "jobAppliedOn" },
                { "data": "preScreen" },
                { "data": "jobStatus" }
            ],
            "language": {
                "emptyTable": "Looks like you have applied to any of the jobs yet for this candidate! " + '<a href="/partner/' + localStorage.getItem("candidateId") + '/jobs"><font color="'+ "#2980b9" +'">Apply now!</font></a>',
            },
            "scrollX": true,
            "destroy": true
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
}

function confirmUpdateStatusNotGoing(){
    if($("#notGoingReason").val() > 0){
        globalStatus = 0;
        updateStatusAjax(localStorage.getItem("candidateId"), globalJpId, 1, $("#notGoingReason").val());
    } else{
        alert("Please select a reason for not going for interview");
    }
}

function updateCandidateStatus(jpId) {
    globalJpId = jpId;
    if($("#candidate_interview_status_" + globalJpId).val() > 0){
        globalStatus = $("#candidate_interview_status_" + globalJpId).val();
        var notGoingReason = 0;
            if($("#notGoingReason").val() != null && $("#notGoingReason").val() != 0){
            notGoingReason = $("#notGoingReason").val();
        }
        updateStatusAjax(localStorage.getItem("candidateId"), globalJpId, $("#candidate_interview_status_" + globalJpId).val(), notGoingReason);
    } else {
        alert("Please select a status");
    }
}

function updateStatusAjax(cid, jpId, val, reason) {
    try {
        $.ajax({
            type: "POST",
            url: "/updateStatus/" + cid + "/" + jpId + "/" + val + "/" + reason,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForUpdateStatus
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataForUpdateStatus(returnedData) {
    $("#notGoingModal").modal("hide");
    if(returnedData == 1){
        if(globalStatus == 1){
            $('#notGoingReason').html('');
            var defaultOption = $('<option value="0"></option>').text("Select a reason");
            $('#notGoingReason').append(defaultOption);

            allReasons.forEach(function (reason) {
                var id = reason.id;
                var name = reason.name;
                var option = $('<option value=' + id + '></option>').text(name);
                $('#notGoingReason').append(option);
            });
            $("#notGoingModal").modal("show");
            globalStatus = 0;
        } else{
            alert("Status updated successfully");
            getAllAppliedJobs();
        }
    } else{
        alert("Something went wrong. Please try again later");
    }
}

function createAndAppendDivider(title) {
    var parent = $("#hotJobs");

    var mainDiv = document.createElement("div");
    mainDiv.id = "hotJobItemDivider";
    parent.append(mainDiv);

    var otherJobIcon = document.createElement("img");
    otherJobIcon.src = "/assets/common/img/suitcase.png";
    otherJobIcon.style = "width: 42px; margin: 8px";
    otherJobIcon.setAttribute("display", "inline-block");
    mainDiv.appendChild(otherJobIcon);

    var hotJobItem = document.createElement("span");
    hotJobItem.setAttribute("display", "inline-block");
    hotJobItem.textContent = title;

    mainDiv.appendChild(hotJobItem);
}

function processDataAllJobPosts(returnedData) {
    var jobPostCount = Object.keys(returnedData).length;
    $("#partnerLoader").hide();
    if(jobPostCount > 0){
        var count = 0;
        var parent = $("#hotJobs");
        //returnedData.reverse();
        createAndAppendDivider("Popular Jobs");
        var isDividerPresent = false;
        returnedData.forEach(function (jobPost){
            count++;
            if(count){
                //!* get all localities of the jobPost *!/
                var jobLocality = jobPost.jobPostToLocalityList;
                var localities = "";
                var allLocalities = "";
                var loopCount = 0;

                if(jobPost.source != null && jobPost.source > 0 && !isDividerPresent){
                    createAndAppendDivider("Other Jobs");
                    isDividerPresent = true;
                }

                jobLocality.forEach(function (locality) {
                    loopCount ++;
                    if(loopCount > 2){
                        return false;
                    } else{
                        var name = locality.locality.localityName;
                        localities += name;
                        if(loopCount < Object.keys(jobLocality).length){
                            localities += ", ";
                        }
                    }
                });
                loopCount = 0;
                jobLocality.forEach(function (locality) {
                    loopCount++;
                    var name = locality.locality.localityName;
                    allLocalities += name;
                    if(loopCount < Object.keys(jobLocality).length){
                        allLocalities += ", ";
                    }
                });

                var hotJobItem = document.createElement("div");
                hotJobItem.id = "hotJobItem";
                parent.append(hotJobItem);

                var centreTag = document.createElement("center");
                hotJobItem.appendChild(centreTag);

                var rowDiv = document.createElement("div");
                rowDiv.className = "row";
                rowDiv.style = "margin: 0; padding: 0";
                centreTag.appendChild(rowDiv);

                var col = document.createElement("div");
                col.className = "col-sm-2";
                rowDiv.appendChild(col);

                var jobLogo = document.createElement("img");
                jobLogo.src = jobPost.company.companyLogo;
                jobLogo.setAttribute('width', '80%');
                jobLogo.id = "jobLogo";
                col.appendChild(jobLogo);

                var jobBodyCol = document.createElement("div");
                jobBodyCol.className = "col-sm-8";
                jobBodyCol.id = "jobBody";
                rowDiv.appendChild(jobBodyCol);

                var jobTitle = document.createElement("h4");
                jobTitle.textContent = jobPost.jobPostTitle + " | " + jobPost.company.companyName;
                jobBodyCol.appendChild(jobTitle);

                var hr = document.createElement("hr");
                jobBodyCol.appendChild(hr);

                var jobBodyDetails = document.createElement("div");
                jobBodyDetails.className = "row";
                jobBodyDetails.id = "jobBodyDetails";
                jobBodyCol.appendChild(jobBodyDetails);

                //!*  salary  *!/

                var bodyCol = document.createElement("div");
                bodyCol.className = "col-sm-4";
                bodyCol.id = "jobSalary";
                jobBodyDetails.appendChild(bodyCol);

                var jobBodySubRow = document.createElement("div");
                jobBodySubRow.className = "row";
                bodyCol.appendChild(jobBodySubRow);

                var jobBodySubRowCol = document.createElement("div");
                jobBodySubRowCol.className = "col-sm-12";
                jobBodySubRowCol.style = "padding: 0";
                jobBodySubRow.appendChild(jobBodySubRowCol);

                var salaryIconDiv = document.createElement("div");
                salaryIconDiv.style = "display : inline-block; margin: 4px;top:0";
                jobBodySubRowCol.appendChild(salaryIconDiv);

                var salaryIcon = document.createElement("img");
                salaryIcon.src = "/assets/common/img/salary.svg";
                salaryIcon.setAttribute('height', '15px');
                salaryIcon.style = "margin-top: -4px";
                salaryIconDiv.appendChild(salaryIcon);

                var salaryDiv = document.createElement("div");
                salaryDiv.style = "display: inline-block; font-size: 14px";
                if(jobPost.jobPostMaxSalary == "0"){
                    salaryDiv.textContent = rupeeFormatSalary(jobPost.jobPostMinSalary) + " monthly";
                } else{
                    salaryDiv.textContent = rupeeFormatSalary(jobPost.jobPostMinSalary) + " - " + rupeeFormatSalary(jobPost.jobPostMaxSalary) + " monthly";
                }
                jobBodySubRowCol.appendChild(salaryDiv);

                //!*  experience  *!/

                var bodyColExp = document.createElement("div");
                bodyColExp.className = "col-sm-3";
                bodyColExp.id = "jobExp";
                jobBodyDetails.appendChild(bodyColExp);

                var jobBodySubRowExp = document.createElement("div");
                jobBodySubRowExp.className = "row";
                bodyColExp.appendChild(jobBodySubRowExp);

                var jobBodySubRowColExp = document.createElement("div");
                jobBodySubRowColExp.className = "col-sm-12";
                jobBodySubRowColExp.style = "padding: 0";
                jobBodySubRowExp.appendChild(jobBodySubRowColExp);

                var expIconDiv = document.createElement("div");
                expIconDiv.style = "display : inline-block; margin: 4px;top:0";
                jobBodySubRowColExp.appendChild(expIconDiv);

                var expIcon = document.createElement("img");
                expIcon.src = "/assets/common/img/workExp.svg";
                expIcon.setAttribute('height', '15px');
                expIcon.style = "margin-top: -4px";
                expIconDiv.appendChild(expIcon);

                var expDiv = document.createElement("div");
                expDiv.style = "display: inline-block; font-size: 14px";
                expDiv.textContent = "Exp: " + jobPost.jobPostExperience.experienceType;
                jobBodySubRowColExp.appendChild(expDiv);

                //!*  Location  *!/

                var bodyColLoc = document.createElement("div");
                bodyColLoc.className = "col-sm-5";
                bodyColLoc.id = "jobLocation";
                jobBodyDetails.appendChild(bodyColLoc);

                var jobBodySubRowLoc = document.createElement("div");
                jobBodySubRowLoc.className = "row";
                bodyColLoc.appendChild(jobBodySubRowLoc);

                var jobBodySubRowColLoc = document.createElement("div");
                jobBodySubRowColLoc.className = "col-sm-12";
                jobBodySubRowColLoc.style = "padding: 0";
                jobBodySubRowLoc.appendChild(jobBodySubRowColLoc);

                var locIconDiv = document.createElement("div");
                locIconDiv.style = "display : inline-block; margin: 4px;top:0";
                jobBodySubRowColLoc.appendChild(locIconDiv);

                var locIcon = document.createElement("img");
                locIcon.src = "/assets/common/img/location.svg";
                locIcon.setAttribute('height', '15px');
                locIcon.style = "margin-top: -4px";
                locIconDiv.appendChild(locIcon);

                var locDiv = document.createElement("div");
                locDiv.style = "display: inline-block; font-size: 14px";
                locDiv.textContent = localities;
                jobBodySubRowColLoc.appendChild(locDiv);

                if(((jobLocality.length) - 2) > 0 ){
                    var tooltip = document.createElement("a");
                    tooltip.id = "locationMsg_" + jobPost.jobPostId;
                    tooltip.title = allLocalities;
                    tooltip.style = "color: #2980b9";
                    tooltip.textContent = " more";
                    jobBodySubRowColLoc.appendChild(tooltip);
                }

                $("#locationMsg_" + jobPost.jobPostId).attr("data-toggle", "tooltip");
                $(function () {
                    $('[data-toggle="tooltip"]').tooltip()
                });

                var incentiveDetails = document.createElement("div");
                incentiveDetails.className = "row";
                incentiveDetails.id = "incentiveDetails";
                jobBodyCol.appendChild(incentiveDetails);

                //!*  interview incentive  *!/

                var interviewIncentiveCol = document.createElement("div");
                interviewIncentiveCol.className = "col-sm-4";
                incentiveDetails.appendChild(interviewIncentiveCol);

                var interviewIncentiveRow = document.createElement("div");
                interviewIncentiveRow.className = "row";
                interviewIncentiveCol.appendChild(interviewIncentiveRow);

                var interviewIncentiveRowCol = document.createElement("div");
                interviewIncentiveRowCol.className = "col-sm-12";
                interviewIncentiveRowCol.style = "padding: 0";
                interviewIncentiveRow.appendChild(interviewIncentiveRowCol);

                var incentiveIconDiv = document.createElement("span");
                incentiveIconDiv.style = "display : inline-block;top:0";
                interviewIncentiveRowCol.appendChild(incentiveIconDiv);

                var incentiveIcon = document.createElement("img");
                incentiveIcon.src = "/assets/partner/img/coin.png";
                incentiveIcon.setAttribute('height', '20px');
                incentiveIcon.style = "margin: -4px 0 0 -5px";
                incentiveIconDiv.appendChild(incentiveIcon);

                var interviewIncentiveVal = document.createElement("span");
                interviewIncentiveVal.className = "incentiveEmptyBody";
                interviewIncentiveVal.style = "display: inline-block;";
                if(jobPost.jobPostPartnerInterviewIncentive == null || jobPost.jobPostPartnerInterviewIncentive == 0){
                    interviewIncentiveVal.textContent = "Interview incentive not specified";
                } else{
                    interviewIncentiveVal.textContent = "₹" + rupeeFormatSalary(jobPost.jobPostPartnerInterviewIncentive) + " interview incentive";
                    incentiveIcon.src = "/assets/partner/img/money-bag.png";
                    interviewIncentiveVal.className = "incentiveBody";
                }
                interviewIncentiveRowCol.appendChild(interviewIncentiveVal);

                //!*  joining incentive  *!/

                var joiningIncentiveCol = document.createElement("div");
                joiningIncentiveCol.className = "col-sm-4";
                incentiveDetails.appendChild(joiningIncentiveCol);

                var joiningIncentiveRow = document.createElement("div");
                joiningIncentiveRow.className = "row";
                joiningIncentiveCol.appendChild(joiningIncentiveRow);

                var joiningIncentiveRowCol = document.createElement("div");
                joiningIncentiveRowCol.className = "col-sm-12";
                joiningIncentiveRowCol.style = "padding: 0";
                joiningIncentiveRow.appendChild(joiningIncentiveRowCol);

                incentiveIconDiv = document.createElement("span");
                incentiveIconDiv.style = "display : inline-block;top:0";
                joiningIncentiveRowCol.appendChild(incentiveIconDiv);

                incentiveIcon = document.createElement("img");
                incentiveIcon.src = "/assets/partner/img/coin.png";
                incentiveIcon.setAttribute('height', '20px');
                incentiveIcon.style = "margin: -4px 0 0 -5px";
                incentiveIconDiv.appendChild(incentiveIcon);

                var joiningIncentiveVal = document.createElement("span");
                joiningIncentiveVal.className = "incentiveEmptyBody";
                joiningIncentiveVal.style = "display: inline-block;";
                if(jobPost.jobPostPartnerJoiningIncentive == null || jobPost.jobPostPartnerJoiningIncentive == 0){
                    joiningIncentiveVal.textContent = "Joining Incentive not specified";
                } else{
                    joiningIncentiveVal.textContent =  "₹" + rupeeFormatSalary(jobPost.jobPostPartnerJoiningIncentive) + " joining incentive";
                    incentiveIcon.src = "/assets/partner/img/money-bag.png";
                    joiningIncentiveVal.className = "incentiveBody";
                }
                incentiveIconDiv.appendChild(joiningIncentiveVal);

                //!*  apply button *!/
                var applyBtnDiv = document.createElement("div");
                applyBtnDiv.className = "col-sm-2";
                applyBtnDiv.id = "applyBtnDiv_" + jobPost.jobPostId;
                rowDiv.appendChild(applyBtnDiv);

                var applyBtn = document.createElement("div");
                applyBtn.className = "jobApplyBtn";
                applyBtn.id = "apply_btn_" + jobPost.jobPostId;
                applyBtn.textContent = "Apply";
                applyBtnDiv.appendChild(applyBtn);
                applyBtn.onclick = function () {
                    $('#jobApplyConfirm').modal();
                    jobPostId = jobPost.jobPostId;
                    jobLocalityArray = [];
                    addLocalitiesToModal(jobPostId);
                };

                var infoBtn = document.createElement("div");
                infoBtn.className = "jobInfoBtn";
                infoBtn.textContent = "Job info";
                infoBtn.id = "info_btn_" + jobPost.jobPostId;
                applyBtnDiv.appendChild(infoBtn);
                infoBtn.onclick = function () {
                    $("#job_detail_view_" + jobPost.jobPostId).slideToggle(300);
                    if($(this).text() == 'Hide Info'){
                        $(this).text('Job Info');
                    } else {
                        $(this).text('Hide Info');
                    }
                };

                // job post info view
                var jobDetailDiv = document.createElement("div");
                jobDetailDiv.id = "job_detail_view_" + jobPost.jobPostId;
                jobDetailDiv.className = "jobDetailCardView";
                parent.append(jobDetailDiv);

                var jobDetailHeading = document.createElement("h4");
                jobDetailHeading.textContent = "Job Details";
                jobDetailHeading.style = "font-weight: 600";
                jobDetailDiv.appendChild(jobDetailHeading);

                var jobDetailBody = document.createElement("div");
                jobDetailBody.className = "row";
                jobDetailBody.style = "margin-top: 16px";
                jobDetailDiv.appendChild(jobDetailBody);

                var jobDetailColOne = document.createElement("div");
                jobDetailColOne.className = "col-sm-3";
                jobDetailBody.appendChild(jobDetailColOne);

                // job post time shift
                var jobPostWorkShift = document.createElement("div");
                jobPostWorkShift.style = "display : inline-block; margin: 4px";
                jobDetailColOne.appendChild(jobPostWorkShift);

                var jobShiftIcon = document.createElement("img");
                jobShiftIcon.src = "/assets/common/img/details/time-passing.svg";
                jobShiftIcon.setAttribute('height', '16px');
                jobShiftIcon.style = "margin-top: -4px; display : inline-block";
                jobPostWorkShift.appendChild(jobShiftIcon);

                var jobShiftVal = document.createElement("div");
                jobShiftVal.setAttribute('height', '16px');
                jobShiftVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if(jobPost.jobPostShift != null){
                    jobShiftVal.textContent = "Shift: " + jobPost.jobPostShift.timeShiftName;
                } else{
                    jobShiftVal.textContent = "Time shift not specified";
                }
                jobPostWorkShift.appendChild(jobShiftVal);

                var jobDetailColTwo = document.createElement("div");
                jobDetailColTwo.className = "col-sm-3";
                jobDetailBody.appendChild(jobDetailColTwo);

                // job post time shift
                var jobPostEducation = document.createElement("div");
                jobPostEducation.style = "display : inline-block; margin: 4px";
                jobDetailColTwo.appendChild(jobPostEducation);

                var jobEducationIcon = document.createElement("img");
                jobEducationIcon.src = "/assets/common/img/details/science-book.svg";
                jobEducationIcon.setAttribute('height', '16px');
                jobEducationIcon.style = "margin-top: -4px; display : inline-block";
                jobPostEducation.appendChild(jobEducationIcon);

                var jobEducationVal = document.createElement("div");
                jobEducationVal.setAttribute('height', '16px');
                jobEducationVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if(jobPost.jobPostEducation != null){
                    jobEducationVal.textContent = "Education " + jobPost.jobPostEducation.educationName;
                } else{
                    jobEducationVal.textContent = "Education not specified";
                }
                jobPostEducation.appendChild(jobEducationVal);

                var jobDetailColThree = document.createElement("div");
                jobDetailColThree.className = "col-sm-3";
                jobDetailBody.appendChild(jobDetailColThree);

                // job post working hours
                var jobPostWorkTimings = document.createElement("div");
                jobPostWorkTimings.style = "display : inline-block; margin: 4px";
                jobDetailColThree.appendChild(jobPostWorkTimings);

                var jobWorkTimingsIcon = document.createElement("img");
                jobWorkTimingsIcon.src = "/assets/common/img/details/calendar.svg";
                jobWorkTimingsIcon.setAttribute('height', '16px');
                jobWorkTimingsIcon.style = "margin-top: -4px; display : inline-block";
                jobPostWorkTimings.appendChild(jobWorkTimingsIcon);

                var jobWorkTimingsVal = document.createElement("div");
                jobWorkTimingsVal.setAttribute('height', '16px');
                jobWorkTimingsVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.jobPostStartTime != null && jobPost.jobPostEndTime != null) {
                    var valStart;
                    var valEnd;
                    if (jobPost.jobPostStartTime > 12) {
                        jobPost.jobPostStartTime = jobPost.jobPostStartTime - 12;
                        valStart = "PM";
                    }
                    else {
                        valStart = "AM";
                    }
                    if (jobPost.jobPostEndTime > 12) {
                        jobPost.jobPostEndTime = jobPost.jobPostEndTime - 12;
                        valEnd = "PM";
                    }
                    else {
                        valEnd = "AM";
                    }
                    jobWorkTimingsVal.textContent = jobPost.jobPostStartTime + " " + valStart + " - " + jobPost.jobPostEndTime + " " + valEnd;

                } else{
                    jobWorkTimingsVal.textContent = "Job timing not specified";
                }
                jobPostWorkTimings.appendChild(jobWorkTimingsVal);

                var jobDetailColFour = document.createElement("div");
                jobDetailColFour.className = "col-sm-3";
                jobDetailBody.appendChild(jobDetailColFour);

                // job post holidays
                var jobPostHolidays = document.createElement("div");
                jobPostHolidays.style = "display : inline-block; margin: 4px";
                jobDetailColFour.appendChild(jobPostHolidays);

                var jobHolidaysIcon = document.createElement("img");
                jobHolidaysIcon.src = "/assets/common/img/details/calendar.svg";
                jobHolidaysIcon.setAttribute('height', '16px');
                jobHolidaysIcon.style = "margin-top: -4px; display : inline-block";
                jobPostHolidays.appendChild(jobHolidaysIcon);

                var jobHolidaysVal = document.createElement("div");
                jobHolidaysVal.setAttribute('height', '16px');
                jobHolidaysVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.jobPostWorkingDays != "" && jobPost.jobPostWorkingDays != null) {
                    var workingDays = jobPost.jobPostWorkingDays.toString(2);
                    var i;
                    /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
                    if (workingDays.length != 7) {
                        var x = 7 - workingDays.length;
                        var modifiedWorkingDays = "";

                        for (i = 0; i < x; i++) {
                            modifiedWorkingDays += "0";
                        }
                        modifiedWorkingDays += workingDays;
                        workingDays = modifiedWorkingDays;
                    }
                    var holiday = "";
                    var arryDay = workingDays.split("");
                    if (arryDay[0] != 1) {
                        holiday += "Mon, ";
                    }
                    if (arryDay[1] != 1) {
                        holiday += "Tue, ";
                    }
                    if (arryDay[2] != 1) {
                        holiday += "Wed, ";
                    }
                    if (arryDay[3] != 1) {
                        holiday += "Thu, ";
                    }
                    if (arryDay[4] != 1) {
                        holiday += "Fri, ";
                    }
                    if (arryDay[5] != 1) {

                        holiday += "Sat, ";
                    }
                    if (arryDay[6] != 1) {
                        holiday += "Sun ";
                    }
                    jobHolidaysVal.textContent = holiday + " - Holiday";
                } else{
                    jobHolidaysVal.textContent = "Holidays not specified";
                }
                jobPostHolidays.appendChild(jobHolidaysVal);

                //second line of details
                var jobPostIncentive = document.createElement("div");
                jobPostIncentive.style = "display : inline-block; margin: 6px 4px 6px 4px";
                jobDetailDiv.appendChild(jobPostIncentive);

                var jobPostIncentiveIcon = document.createElement("img");
                jobPostIncentiveIcon.src = "/assets/common/img/details/coins.svg";
                jobPostIncentiveIcon.setAttribute('height', '16px');
                jobPostIncentiveIcon.style = "margin-top: -4px; display : inline-block";
                jobPostIncentive.appendChild(jobPostIncentiveIcon);

                var jobPostIncentiveHeading = document.createElement("div");
                jobPostIncentiveHeading.setAttribute('height', '16px');
                jobPostIncentiveHeading.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                jobPostIncentiveHeading.textContent = "Incentives:";
                jobPostIncentive.appendChild(jobPostIncentiveHeading);

                var jobPostIncentiveVal = document.createElement("div");
                jobPostIncentiveVal.setAttribute('height', '16px');
                jobPostIncentiveVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if(jobPost.jobPostIncentives != ""){
                    jobPostIncentiveVal.textContent = jobPost.jobPostIncentives;
                } else{
                    jobPostIncentiveVal.textContent = "Incentives not specified";
                }
                jobPostIncentive.appendChild(jobPostIncentiveVal);

                var breakTag = document.createElement("br");
                jobDetailDiv.appendChild(breakTag);

                //Third line of details
                var jobPostMinimumRequirement = document.createElement("div");
                jobPostMinimumRequirement.style = "display : inline-block; margin: 6px 4px 6px 4px";
                jobDetailDiv.appendChild(jobPostMinimumRequirement);

                var jobPostMinimumRequirementIcon = document.createElement("img");
                jobPostMinimumRequirementIcon.src = "/assets/common/img/details/list.svg";
                jobPostMinimumRequirementIcon.setAttribute('height', '16px');
                jobPostMinimumRequirementIcon.style = "margin-top: -4px; display : inline-block";
                jobPostMinimumRequirement.appendChild(jobPostMinimumRequirementIcon);

                var jobPostMinimumRequirementHeading = document.createElement("div");
                jobPostMinimumRequirementHeading.setAttribute('height', '16px');
                jobPostMinimumRequirementHeading.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                jobPostMinimumRequirementHeading.textContent = "Minimum Requirement:";
                jobPostMinimumRequirement.appendChild(jobPostMinimumRequirementHeading);

                var jobPostMinimumRequirementVal = document.createElement("div");
                jobPostMinimumRequirementVal.setAttribute('height', '16px');
                jobPostMinimumRequirementVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.jobPostMinRequirement != null && jobPost.jobPostMinRequirement != "") {
                    jobPostMinimumRequirementVal.textContent = jobPost.jobPostMinRequirement;
                } else{
                    jobPostMinimumRequirementVal.textContent = "Minimum requirement not specified";
                }
                jobPostMinimumRequirement.appendChild(jobPostMinimumRequirementVal);

                breakTag = document.createElement("br");
                jobDetailDiv.appendChild(breakTag);

                //Job Description
                var jobPostDescription = document.createElement("div");
                jobPostDescription.style = "display : inline-block; margin: 6px 4px 6px 4px";
                jobDetailDiv.appendChild(jobPostDescription);

                var jobPostDescriptionIcon = document.createElement("img");
                jobPostDescriptionIcon.src = "/assets/common/img/details/job_desc.svg";
                jobPostDescriptionIcon.setAttribute('height', '16px');
                jobPostDescriptionIcon.style = "margin-top: -4px; display : inline-block";
                jobPostDescription.appendChild(jobPostDescriptionIcon);

                var jobPostDescriptionHeading = document.createElement("div");
                jobPostDescriptionHeading.setAttribute('height', '16px');
                jobPostDescriptionHeading.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                jobPostDescriptionHeading.textContent = "Job Description:";
                jobPostDescription.appendChild(jobPostDescriptionHeading);

                var jobPostDescriptionVal = document.createElement("div");
                jobPostDescriptionVal.setAttribute('height', '16px');
                jobPostDescriptionVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.jobPostDescription != null && jobPost.jobPostDescription != "") {
                    jobPostDescriptionVal.style = "margin-top: -4px; display : block; margin: 4px 21px 8px 21px";

                    breakTag = document.createElement("br");
                    jobDetailDiv.appendChild(breakTag);

                    jobPostDescriptionVal.textContent = jobPost.jobPostDescription;
                } else{
                    jobPostDescriptionVal.textContent = "Job description not specified";
                }
                jobPostDescription.appendChild(jobPostDescriptionVal);

                //company details
                var companyDetailHeading = document.createElement("h4");
                companyDetailHeading.textContent = "Company Details";
                companyDetailHeading.style = "font-weight: 600";
                jobDetailDiv.appendChild(companyDetailHeading);

                var companyDetailBody = document.createElement("div");
                companyDetailBody.className = "row";
                companyDetailBody.style = "margin-top: 16px";
                jobDetailDiv.appendChild(companyDetailBody);

                var companyDetailColOne = document.createElement("div");
                companyDetailColOne.className = "col-sm-3";
                companyDetailBody.appendChild(companyDetailColOne);

                //company location
                var companyLocation = document.createElement("div");
                companyLocation.style = "display : inline-block; margin: 4px";
                companyDetailColOne.appendChild(companyLocation);

                var companyLocationIcon = document.createElement("img");
                companyLocationIcon.src = "/assets/common/img/details/buildings.svg";
                companyLocationIcon.setAttribute('height', '16px');
                companyLocationIcon.style = "margin-top: -4px; display : inline-block";
                companyLocation.appendChild(companyLocationIcon);

                var companyLocationVal = document.createElement("div");
                companyLocationVal.setAttribute('height', '16px');
                companyLocationVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.company.companyLocality != null) {
                    companyLocationVal.textContent = "Location: " + jobPost.company.companyLocality.localityName;
                } else{
                    companyLocationVal.textContent = "Company location not specified";
                }
                companyLocation.appendChild(companyLocationVal);

                var companyDetailColTwo = document.createElement("div");
                companyDetailColTwo.className = "col-sm-3";
                companyDetailBody.appendChild(companyDetailColTwo);

                //company type
                var companyType = document.createElement("div");
                companyType.style = "display : inline-block; margin: 4px";
                companyDetailColTwo.appendChild(companyType);

                var companyTypeIcon = document.createElement("img");
                companyTypeIcon.src = "/assets/common/img/details/group.svg";
                companyTypeIcon.setAttribute('height', '16px');
                companyTypeIcon.style = "margin-top: -4px; display : inline-block";
                companyType.appendChild(companyTypeIcon);

                var companyTypeVal = document.createElement("div");
                companyTypeVal.setAttribute('height', '16px');
                companyTypeVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.company.compType != null) {
                    companyTypeVal.textContent = "Company type: " + jobPost.company.compType.companyTypeName;
                } else{
                    companyTypeVal.textContent = "Company type not specified";
                }
                companyType.appendChild(companyTypeVal);

                var companyDetailColThree = document.createElement("div");
                companyDetailColThree.className = "col-sm-3";
                companyDetailBody.appendChild(companyDetailColThree);

                //company website
                var companyWebsite = document.createElement("div");
                companyWebsite.style = "display : inline-block; margin: 4px";
                companyDetailColThree.appendChild(companyWebsite);

                var companyWebsiteIcon = document.createElement("img");
                companyWebsiteIcon.src = "/assets/common/img/details/internet.svg";
                companyWebsiteIcon.setAttribute('height', '16px');
                companyWebsiteIcon.style = "margin-top: -4px; display : inline-block";
                companyWebsite.appendChild(companyWebsiteIcon);

                var companyWebsiteVal = document.createElement("div");
                companyWebsiteVal.setAttribute('height', '16px');
                companyWebsiteVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.company.companyWebsite != null && jobPost.company.companyWebsite != "") {
                    companyWebsiteVal.textContent = jobPost.company.companyWebsite;
                } else{
                    companyWebsiteVal.textContent = "Company type not specified";
                }
                companyWebsite.appendChild(companyWebsiteVal);

                //Job Description
                var companyDescription = document.createElement("div");
                companyDescription.style = "display : inline-block; margin: 6px 4px 6px 4px";
                jobDetailDiv.appendChild(companyDescription);

                var companyDescriptionIcon = document.createElement("img");
                companyDescriptionIcon.src = "/assets/common/img/details/list.svg";
                companyDescriptionIcon.setAttribute('height', '16px');
                companyDescriptionIcon.style = "margin-top: -4px; display : inline-block";
                companyDescription.appendChild(companyDescriptionIcon);

                var companyDescriptionHeading = document.createElement("div");
                companyDescriptionHeading.setAttribute('height', '16px');
                companyDescriptionHeading.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                companyDescriptionHeading.textContent = "Company Description:";
                companyDescription.appendChild(companyDescriptionHeading);

                var companyDescriptionVal = document.createElement("div");
                companyDescriptionVal.setAttribute('height', '16px');
                companyDescriptionVal.style = "margin-top: -4px; display : inline-block; margin-left: 4px";
                if (jobPost.company.companyDescription != null && jobPost.company.companyDescription != "") {
                    companyDescriptionVal.style = "margin-top: -4px; display : block; margin: 4px 21px 8px 21px";

                    breakTag = document.createElement("br");
                    jobDetailDiv.appendChild(breakTag);

                    companyDescriptionVal.textContent = jobPost.company.companyDescription;
                } else{
                    companyDescriptionVal.textContent = "Company description not specified";
                }
                companyDescription.appendChild(companyDescriptionVal);

            }
        });
    }
    //getting all the applied jobs
    getAllAppliedJobs();
    getCandidateInfo();
}

openPartnerPreScreenModal = function (jobPostId, candidateId) {
    // actorId defined which modal to display
    globalPalette.color.main.headerColor= "#26A69A";
    var decoratorPromise = new Promise( function(resolve, reject) {
                resolve(initDecorator(globalPalette));
        });
    decoratorPromise.then(function (decorator) {
        decorator.columnVisible = [1,2,3,4,6];

        // display only Min Requirement
        decorator.textContainers.noteContainer.visibility = false;
        decorator.textContainers.minReqContainer.className = "col-lg-12 form-group remove-padding-left";

        // remove callConnected
        decorator.callYesNoRequired = false;
        if(jobRoleName != null && companyName!= null){
            decorator.preScreen.title = "Job Application Form: "+jobRoleName+" @ "+companyName;
        } else {
            decorator.preScreen.title = "Job Application Form"
        }
        decorator.table.mainTable.title = "Job Requirements : Please verify and update candidate's details ";
        decorator.table.otherTable.title = "Job Details: ";
        decorator.textContainers.minReqContainer.title = "Other Requirements";
        decorator.edit.title = "Update Info";

        // footerMessage
        decorator.modalFooter.footerMessage = " I confirm that the above details are accurate and accept the terms and conditions.";

        var isSupport = false;
        if( !decorator.callYesNoRequired) {
            getPreScreenContent(jobPostId, candidateId, false, decorator, false, isSupport);
        }
    }, function (err) {
        console.log(err);
    });
};

function getCandidateInfo() {
    try {
        $.ajax({
            url: "/checkPartnerCandidate/" + localStorage.getItem("candidateId"),
            type: "POST",
            async: false,
            data: false,
            dataType: "json",
            contentType: false,
            processData: false,
            success: processDataGetCandidateInfo
        });
    } catch (exception) {}
}

function processDataGetCandidateInfo(returnedData){
    candidateInfo = returnedData;
    document.getElementById("userName").innerHTML = returnedData.candidateFirstName;
    document.getElementById("userMobile").innerHTML = returnedData.candidateMobile;
    if (returnedData.candidateDOB != null) {
        var date = JSON.parse(returnedData.candidateDOB);
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
        document.getElementById("userAge").innerHTML = ", " + age + " yrs";
    }
    if (returnedData.candidateGender != null) {
        if (returnedData.candidateGender == 0) {
            try{
                document.getElementById("userGender").innerHTML = ", Male";
                $("#userImgPartner").attr('src', '/assets/dashboard/img/userMale.svg');
            } catch(err){}
        } else {
            try{
                document.getElementById("userGender").innerHTML = ", Female";
                $("#userImg").attr('src', '/assets/dashboard/img/userFemale.svg');
            } catch(err){}
        }
    }

    try {
        var jobRoles = "";
        var count = 0;
        var jobPref = returnedData.jobPreferencesList;
        if(jobPref.length > 0){
            jobPref.forEach(function (job){
                count ++;
                var name = job.jobRole.jobName;
                jobRoles += name;
                if(count < Object.keys(jobPref).length){
                    jobRoles += ", ";
                }
            });
            document.getElementById("userJobs").innerHTML = jobRoles;
        }
    } catch(err){
        console.log(err);
    }
    try {
        if(returnedData.locality != null){
            document.getElementById("userLocality").innerHTML = returnedData.locality.localityName;
        }
    } catch(err){
        console.log("getCandidateLocalityPref error"+err);
    }

    /* Time Shift */
    if (returnedData.timeShiftPreference != null) {
        document.getElementById("userShift").innerHTML = returnedData.timeShiftPreference.timeShift.timeShiftName;
        if(returnedData.timeShiftPreference.timeShift.timeShiftId == 5){
            document.getElementById("userShift").innerHTML = returnedData.timeShiftPreference.timeShift.timeShiftName + " Shift";
        }
    }

    /* candidate Education */
    try{
        if(returnedData.candidateEducation.education != null) {
            document.getElementById("userEducationLevel").innerHTML = returnedData.candidateEducation.education.educationName;
        }
    } catch(err){}

    /* Work Experience */
    if(returnedData.candidateTotalExperience != null){
        if(returnedData.candidateTotalExperience == 0) {
            document.getElementById("userTotalExperience").innerHTML = "Fresher";
            document.getElementById("userCurrentCompany").innerHTML = "Not Applicable";
        }
        else {
            var totalExperience = parseInt(returnedData.candidateTotalExperience);
            var yrs = parseInt((totalExperience / 12)).toString();
            var month = totalExperience % 12;
            if(yrs == 0 && month != 0){
                document.getElementById("userTotalExperience").innerHTML = month + " months";
            } else if(month == 0 && yrs != 0){
                document.getElementById("userTotalExperience").innerHTML = yrs + " years";

            } else{
                document.getElementById("userTotalExperience").innerHTML = yrs + " yrs and " + month + " mnths";
            }

            /* Current Company and Salary */
            if (Object.keys(returnedData.jobHistoryList).length > 0) {
                returnedData.jobHistoryList.forEach(function (pastJob) {
                    if(pastJob.currentJob == true){
                        if(pastJob.candidatePastCompany != null){
                            document.getElementById("userCurrentCompany").innerHTML = pastJob.candidatePastCompany;
                        } else{
                            document.getElementById("userCurrentCompany").innerHTML = "Not Specified";
                        }
                        return false;
                    }
                });
            }

            if(returnedData.candidateLastWithdrawnSalary != null){
                if(returnedData.candidateLastWithdrawnSalary == "0"){
                    document.getElementById("userCurrentSalary").innerHTML = "Not Applicable";
                } else{
                    document.getElementById("userCurrentSalary").innerHTML = "&#x20B9;" + returnedData.candidateLastWithdrawnSalary + "/month";
                }
            }
        }
    }
}

function addLocalitiesToModal(jobPostId) {
    try {
        $.ajax({
            type: "POST",
            url: "/getJobPostInfo/" + jobPostId + "/0",
            data: false,
            contentType: false,
            processData: false,
            success: processDataForJobPostLocation
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataForJobPostLocation(returnedData) {
    $("#applyButton").hide();
    document.getElementById("applyJobCandidateName").innerHTML = candidateInfo.candidateFirstName;
    document.getElementById("applyJobCandidateNameSecond").innerHTML = candidateInfo.candidateFirstName;
    $("#jobNameConfirmation").html(returnedData.jobPostTitle);
    $("#companyNameConfirmation").html(returnedData.company.companyName);
    var i;
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
    });
    // if (Object.keys(returnedData.interviewDetailsList).length > 0) {
    //     //slots
    //     $('#interviewSlot').html('');
    //     var defaultOption = $('<option value="-1"></option>').text("Select Time Slot");
    //     $('#interviewSlot').append(defaultOption);
    //
    //     var interviewDetailsList = returnedData.interviewDetailsList;
    //     if (interviewDetailsList[0].interviewDays != null) {
    //         var interviewDays = interviewDetailsList[0].interviewDays.toString(2);
    //
    //         /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
    //         if (interviewDays.length != 7) {
    //             x = 7 - interviewDays.length;
    //             var modifiedInterviewDays = "";
    //
    //             for (i = 0; i < x; i++) {
    //                 modifiedInterviewDays += "0";
    //             }
    //             modifiedInterviewDays += interviewDays;
    //             interviewDays = modifiedInterviewDays;
    //         }
    //     }
    //     //slots
    //     var today = new Date();
    //     for (i = 2; i < 9; i++) {
    //         // 0 - > sun 1 -> mon ...
    //         var x = new Date(today.getFullYear(), today.getMonth(), today.getDate() + i);
    //         if (checkSlotAvailability(x, interviewDays)) {
    //             interviewDetailsList.forEach(function (timeSlot) {
    //                 var dateSlotSelectedId = x.getFullYear() + "-" + (x.getMonth() + 1) + "-" + x.getDate() + "_" + timeSlot.interviewTimeSlot.interviewTimeSlotId;
    //                 var option = $('<option value="' + dateSlotSelectedId + '"></option>').text(getDayVal(x.getDay()) + ", " + x.getDate() + " " + getMonthVal((x.getMonth() + 1)) + " (" + timeSlot.interviewTimeSlot.interviewTimeSlotName + ")");
    //                 $('#interviewSlot').append(option);
    //             });
    //         }
    //     }
    //     $('#interviewSection').show();
    // } else{
    //     $('#interviewSection').hide();
    // }
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

function confirmApply() {
    $("#applyButton").addClass("jobApplied").removeClass("jobApplyBtnModal").prop('disabled',true).html("Applying");
    // checking if the candidate exists + if the partner has created this particular candidate or not
    var candidateId = localStorage.getItem("candidateId");
    try {
        $.ajax({
            type: "POST",
            url: "/checkPartnerCandidate/" + candidateId,
            data: false,
            contentType: false,
            processData: false,
            success: function (returnedData) {
                processDataCheckCandidate(returnedData, candidateId)
            }
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataCheckCandidate(returnedData, candidateId) {
    if(returnedData != '0'){
        applyJobSubmit(jobPostId, candidateId, returnedData.candidateMobile, prefLocation, prefTimeSlot, scheduledInterviewDate, true);
    } else{
        //Partner doesn't own the candidate
        window.location = "/partner/myCandidates";
    }
    $("#apply_btn_" + jobPostId).addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied").click(false);
    $("#applyBtnDiv_" + jobPostId).prop('disabled',true).click(false);
}

$(function() {
    $("#jobLocality").change(function (){
        prefLocation = $(this).val();
        prefLocationName = $("#jobLocality option:selected").text();
        $("#applyButton").show();

    });

    // $("#interviewSlot").change(function (){
    //     if($(this).val() != -1 && $("#jobLocality").val() != -1){
    //         var combinedValue = $(this).val().split("_");
    //         scheduledInterviewDate = combinedValue[0];
    //         prefTimeSlot = combinedValue[1];
    //
    //         prefLocation = $("#jobLocality").val();
    //         prefLocationName = $("#jobLocality option:selected").text();
    //         $("#applyButton").show();
    //     } else{
    //         $("#applyButton").hide();
    //     }
    // });
});

function confirmInterview(jpId, status) {
    globalJpId = jpId;
    globalInterviewStatus = status;
    try {
        $.ajax({
            type: "POST",
            url: "/partnerConfirmInterview/" + localStorage.getItem("candidateId") + "/" + parseInt(jpId) + "/" + status,
            async: true,
            contentType: false,
            data: false,
            success: processDataConfirmInterview
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
}

function processDataConfirmInterview(returnedData) {
    if(returnedData != 0){
        $("#status_" + globalJpId).html('');
        var divInterviewStatus = document.createElement("span");
        divInterviewStatus.id = "status_val_" + globalJpId;
        if(globalInterviewStatus == 1){
            alert("Job application accepted");
            divInterviewStatus.textContent = "Interview Accepted";
        } else {
            alert("Job application rejected");
            divInterviewStatus.textContent = "Interview recjected by Candidate/Partner";
        }

        $("#status_" + globalJpId).append(divInterviewStatus);
    } else{
        alert("Something went wrong. Please try after sometime")
    }
}

function navigateToLocation(lat, lng){
    window.open('http://maps.google.com/?q='+ lat +',' + lng);
}
