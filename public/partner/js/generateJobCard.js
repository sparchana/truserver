var jobPostId;
var candidateInfo;

var appliedJobSection;
var popularJobsSection;

var prefLocation;
var prefLocationName;

var prefTimeSlot;

var scheduledInterviewDate;

$(window).resize(function(){
    var w = window.innerWidth;
    if(w < 640){
        $(".candidatePartner").removeClass("row-eq-height");
    } else{
        $(".candidatePartner").removeClass("row-eq-height").addClass("row-eq-height");
    }
});

$(document).ready(function(){
    var w = window.innerWidth;
    appliedJobSection = false;
    popularJobsSection = true;
    $("#appliedJobsSection").hide();

    if(w < 640){
        $(".candidatePartner").removeClass("row-eq-height");
    } else{
        $(".candidatePartner").removeClass("row-eq-height").addClass("row-eq-height");
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
});

function toggleTabs(index) {
    if(index == 0){
        popularJobsSection = true;
        appliedJobSection = false;
        $("#applyJobs").show(500);
        $("#appliedJobsSection").hide(500);
        $(".viewPopularJobs").removeClass("white");
        $(".viewAppliedJobs").removeClass("white").addClass("white");
    } else{
//        getAllAppliedJobs();
        popularJobsSection = true;
        appliedJobSection = false;
        $("#applyJobs").hide(500);
        $("#appliedJobsSection").show(500);
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
                        var appliedDateInMillis = new Date(jobApplication.jobApplicationCreateTimeStamp);
                        var salary;
                        if(jobApplication.jobPost.jobPostMaxSalary != null){
                            salary = "₹" + rupeeFormatSalary(jobApplication.jobPost.jobPostMinSalary) + " - ₹" + rupeeFormatSalary(jobApplication.jobPost.jobPostMaxSalary);
                        } else{
                            salary = "₹" + rupeeFormatSalary(jobApplication.jobPost.jobPostMinSalary);
                        }
                        //getting interview details
                        var interviewDetails = "Not specified";
                        if(jobApplication.interviewTimeSlot != null){
                            var interviewDate = new Date(jobApplication.scheduledInterviewDate);
                            interviewDetails = interviewDate.getDate() + '/' + (interviewDate.getMonth()+1) + '/' + interviewDate.getFullYear() + "@" + jobApplication.interviewTimeSlot.interviewTimeSlotName;
                        }
                        returned_data.push({
                            'jobPostName' : '<div class="mLabel" style="width:100%" >'+ jobApplication.jobPost.jobPostTitle + '</div>',
                            'jobPostCompany' : '<div class="mLabel" style="width:100%" >'+ jobApplication.jobPost.company.companyName + '</div>',
                            'jobPostSalary' : '<div class="mLabel" style="width:100%" >'+ salary + '</div>',
                            'jobPostExperience' : '<div class="mLabel" style="width:100%" >'+ jobApplication.jobPost.jobPostExperience.experienceType + '</div>',
                            'jobPreScreenLocation' : '<div class="mLabel" style="width:100%" >'+ jobApplication.locality.localityName + '</div>',
                            'interviewDetails' : '<div class="mLabel" style="width:100%" >'+ interviewDetails + '</div>',
                            'jobAppliedOn' : '<div class="mLabel" style="width:100%" >'+ appliedDateInMillis.getDate() + '/' + (appliedDateInMillis.getMonth()+1) + '/' + appliedDateInMillis.getFullYear() + '</div>'
                        });
                        returnedData.forEach(function (jobApplication) {
                            $("#apply_btn_" + jobApplication.jobPost.jobPostId).addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied").click(false);
                            $("#applyBtnDiv_" + jobApplication.jobPost.jobPostId).prop('disabled',true).click(false);
                        });
                    });
                    return returned_data;
                }
            },

            "deferRender": true,
            "columns": [
                { "data": "jobPostName" },
                { "data": "jobPostCompany" },
                { "data": "jobPostSalary" },
                { "data": "jobPostExperience" },
                { "data": "jobPreScreenLocation" },
                { "data": "interviewDetails" },
                { "data": "jobAppliedOn" }
            ],
            "language": {
                "emptyTable": "Looks like you have not added any candidates yet! ",
            },
            responsive: true,
            "destroy": true
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
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

                var interviewIncentiveDivMain = document.createElement("div");
                interviewIncentiveDivMain.className = "row";
                interviewIncentiveDivMain.style = "margin: 0; padding: 0";
                interviewIncentiveDivMain.id = "interviewIncentive";
                jobBodyCol.appendChild(interviewIncentiveDivMain);

                var interviewIncentiveVal = document.createElement("div");
                interviewIncentiveVal.className = "col-sm-12";
                interviewIncentiveVal.style = "margin: 0; padding: 0";
                interviewIncentiveDivMain.appendChild(interviewIncentiveVal);

                var interviewIncentiveIconDiv = document.createElement("div");
                interviewIncentiveIconDiv.style = "display : inline-block;";
                interviewIncentiveVal.appendChild(interviewIncentiveIconDiv);

                var interviewIncentiveIcon = document.createElement("img");
                interviewIncentiveIcon.src = "/assets/partner/img/coin.png";
                interviewIncentiveIcon.setAttribute('height', '15px');
                interviewIncentiveIcon.setAttribute('margin', '4px');
                interviewIncentiveIcon.style = "margin-top: -4px; margin-left: -3px";
                interviewIncentiveIconDiv.appendChild(interviewIncentiveIcon);

                var interviewIncentiveDiv = document.createElement("div");
                interviewIncentiveDiv.style = "display: inline-block; font-size: 14px; margin-left: 4px";
                if(jobPost.jobPostPartnerInterviewIncentive == null || jobPost.jobPostPartnerInterviewIncentive == 0){
                    interviewIncentiveDiv.textContent = "Interview incentive not specified";
                } else{
                    interviewIncentiveDiv.textContent = "₹" + rupeeFormatSalary(jobPost.jobPostPartnerInterviewIncentive) + " interview incentive";
                    interviewIncentiveIcon.src = "/assets/partner/img/money-bag.png";
                }
                interviewIncentiveIconDiv.appendChild(interviewIncentiveDiv);

                var joiningIncentiveDivMain = document.createElement("div");
                joiningIncentiveDivMain.className = "row";
                joiningIncentiveDivMain.style = "margin: 0; padding: 0";
                joiningIncentiveDivMain.id = "interviewIncentive";
                jobBodyCol.appendChild(joiningIncentiveDivMain);

                var joiningIncentiveVal = document.createElement("div");
                joiningIncentiveVal.className = "col-sm-12";
                joiningIncentiveVal.style = "margin: 0; padding: 0";
                joiningIncentiveDivMain.appendChild(joiningIncentiveVal);

                var joiningIncentiveIconDiv = document.createElement("div");
                joiningIncentiveIconDiv.style = "display : inline-block;";
                joiningIncentiveVal.appendChild(joiningIncentiveIconDiv);

                var joiningIncentiveIcon = document.createElement("img");
                joiningIncentiveIcon.src = "/assets/partner/img/coin.png";
                joiningIncentiveIcon.setAttribute('height', '15px');
                joiningIncentiveIcon.setAttribute('margin', '4px');
                joiningIncentiveIcon.style = "margin-top: -4px; margin-left: -3px";
                joiningIncentiveIconDiv.appendChild(joiningIncentiveIcon);

                var joiningIncentiveDiv = document.createElement("div");
                joiningIncentiveDiv.style = "display: inline-block; font-size: 14px; margin-left: 4px";
                if(jobPost.jobPostPartnerJoiningIncentive == null || jobPost.jobPostPartnerJoiningIncentive == 0){
                    joiningIncentiveDiv.textContent = "Joining Incentive not specified";
                } else{
                    joiningIncentiveDiv.textContent =  "₹" + rupeeFormatSalary(jobPost.jobPostPartnerJoiningIncentive) + " joining incentive";
                    joiningIncentiveIcon.src = "/assets/partner/img/money-bag.png";
                }
                joiningIncentiveIconDiv.appendChild(joiningIncentiveDiv);

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
                applyBtnDiv.onclick = function () {
                    $('#jobApplyConfirm').modal();
                    jobPostId = jobPost.jobPostId;
                    jobLocalityArray = [];
                    addLocalitiesToModal();
                };

                var incentiveDiv = document.createElement("div");
                incentiveDiv.style = "display: block; text-align: left; margin-top: 4px";
                applyBtnDiv.appendChild(incentiveDiv);
            }
        });
    }
    //getting all the applied jobs
    getAllAppliedJobs();
    getCandidateInfo();
}

function getCandidateInfo() {
    try {
        $.ajax({
            url: "/checkPartnerCandidate/" + localStorage.getItem("candidateId"),
            type: "POST",
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

function addLocalitiesToModal() {
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
    if (Object.keys(returnedData.interviewDetailsList).length > 0) {
        //slots
        $('#interviewSlot').html('');
        var defaultOption = $('<option value="-1"></option>').text("Select Time Slot");
        $('#interviewSlot').append(defaultOption);

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
                    $('#interviewSlot').append(option);
                });
            }
        }
        $('#interviewSlot').show();
    } else{
        $('#interviewSlot').hide();
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

function confirmApply() {
    // checking if the candidate exists + if the partner has created this particular candidate or not
    var candidateId = localStorage.getItem("candidateId");
    try {
        $.ajax({
            type: "POST",
            url: "/checkPartnerCandidate/" + candidateId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckCandidate
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataCheckCandidate(returnedData) {
    if(returnedData != '0'){
        applyJobSubmit(jobPostId, returnedData.candidateMobile, prefLocation, prefTimeSlot, scheduledInterviewDate, true);
    } else{
        console.log("Partner doesn't own the candidate");
    }
}

$(function() {
    $("#jobLocality").change(function (){
        if($(this).val() != -1 && $("#interviewSlot").val() != -1){
            prefLocation = $(this).val();
            prefLocationName = $("#jobLocality option:selected").text();

            if ($("#interviewSlot").css('display') != 'none'){
                var combinedValue = $("#interviewSlot").val().split("_");
                scheduledInterviewDate = combinedValue[0];
                prefTimeSlot = combinedValue[1];
            }

            $("#applyButton").show();
        } else{
            $("#applyButton").hide();
        }
    });

    $("#interviewSlot").change(function (){
        if($(this).val() != -1 && $("#jobLocality").val() != -1){
            var combinedValue = $(this).val().split("_");
            scheduledInterviewDate = combinedValue[0];
            prefTimeSlot = combinedValue[1];

            prefLocation = $("#jobLocality").val();
            prefLocationName = $("#jobLocality option:selected").text();
            $("#applyButton").show();
        } else{
            $("#applyButton").hide();
        }
    });
});