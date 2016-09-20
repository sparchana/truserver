/**
 * Created by batcoder1 on 4/6/16.
 */

var jobPostId = 0;
var jobLocalityArray = [];
var minProfileComplete = 0;
var prefLocation;
var prefLocationName;

$(window).load(function() {
    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").delay(1000).fadeOut("slow");
});

$(document).ready(function(){
    checkUserLogin();
    try {
        $.ajax({
            type: "POST",
            url: "/getAllHotJobPosts",
            data: false,
            async: true,
            contentType: false,
            processData: false,
            success: processDataAllJobPosts
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);

    }
    if(localStorage.getItem("assessed") == '0'){
        $(".assessmentComplete").hide();
        $(".assessmentIncomplete").show();
    }
    else{
        localStorage.setItem("assessed", "1");
        $(".assessmentComplete").show();
        $(".assessmentIncomplete").hide();
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateInfoDashboard",
            data: false,
            async: true,
            contentType: false,
            processData: false,
            success: processDataAndFillMinProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataAllJobPosts(returnedData) {
    var jobPostCount = Object.keys(returnedData).length;
    if(jobPostCount > 0){
        var count = 0;
        var parent = $("#hotJobs");
        returnedData.reverse();
        returnedData.forEach(function (jobPost){
            count++;
            if(count){
                /* get all localities of the jobPost */
                var jobLocality = jobPost.jobPostToLocalityList;
                var localities = "";
                var allLocalities = "";
                var loopCount = 0;
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
                /*                jobLogo.src = "/assets/common/img/" + jobPost.company.companyLogo + ".png";*/
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

                /*  salary  */

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
                salaryDiv.style = "display: inline-block; font-size: 13px";
                if(jobPost.jobPostMaxSalary == "0"){
                    salaryDiv.textContent = jobPost.jobPostMinSalary + " monthly";
                } else{
                    salaryDiv.textContent = jobPost.jobPostMinSalary + " - " + jobPost.jobPostMaxSalary + " monthly";
                }

                jobBodySubRowCol.appendChild(salaryDiv);

                /*  experience  */

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
                expDiv.style = "display: inline-block; font-size: 13px";
                expDiv.textContent = "Exp: " + jobPost.jobPostExperience.experienceType;
                jobBodySubRowColExp.appendChild(expDiv);

                /*  Location  */

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
                locDiv.style = "display: inline-block; font-size: 13px";
                locDiv.textContent = localities;
                jobBodySubRowColLoc.appendChild(locDiv);

                if(((jobLocality.length) - 2) > 0 ){
                    var tooltip = document.createElement("a");
                    tooltip.id = "locationMsg_" + jobPost.jobPostId;
                    tooltip.title = allLocalities;
                    tooltip.style = "color: #2980b9; font-size: 13px";
                    tooltip.textContent = " more";
                    jobBodySubRowColLoc.appendChild(tooltip);
                }

                $("#locationMsg_" + jobPost.jobPostId).attr("data-toggle", "tooltip");
                /*  apply button */

                var applyBtnDiv = document.createElement("div");
                applyBtnDiv.className = "col-sm-2";
                applyBtnDiv.id = "applyBtnDiv_" + jobPost.jobPostId;
                rowDiv.appendChild(applyBtnDiv);

                var applyBtn = document.createElement("div");
                applyBtn.className = "jobApplyBtn";
                applyBtn.id = "apply_btn_" + jobPost.jobPostId;
                applyBtn.textContent = "View & Apply";
                applyBtnDiv.appendChild(applyBtn);
                applyBtn.onclick=function(){
                    var jobPostBreak = jobPost.jobPostTitle.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'_');
                    jobPostBreak = jobPostBreak.toLowerCase();
                    var jobCompany = jobPost.company.companyName.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'_');
                    jobCompany = jobPostBreak.toLowerCase();
                    try {
                        window.location.href = "/jobs/" + jobPostBreak + "/bengaluru/" + jobCompany + "/" + jobPost.jobPostId;
                    } catch (exception) {
                        console.log("exception occured!!" + exception);
                    }
                }
            }
        });
    }
}

function confirmApply() {
    applyJob(jobPostId, prefLocation);
}

$(function() {
    $("#jobLocality").change(function (){
        if($(this).val() != -1){
            prefLocation = $(this).val();
            prefLocationName = $("#jobLocality option:selected").text();
            $("#applyButton").show();
        } else{
            $("#applyButton").hide();
        }
    });
});

function processDataAndFillMinProfile(returnedData) {
    console.log(returnedData);
    if(returnedData.candidateLastName == "" || returnedData.candidateLastName == null){
        document.getElementById("userName").innerHTML = returnedData.candidateFirstName;
    } else{
        document.getElementById("userName").innerHTML = returnedData.candidateFirstName + " " + returnedData.candidateLastName;
    }
    document.getElementById("userMobile").innerHTML = returnedData.candidateMobile;

    minProfileComplete = returnedData.isMinProfileComplete;
    if(returnedData.isMinProfileComplete == 0){ // profile not complete
        $(".profileComplete").hide();
        $(".profileIncomplete").show();
        localStorage.setItem("minProfile", 0);
    } else{
        $(".profileComplete").show();
        $(".profileIncomplete").hide();
        localStorage.setItem("minProfile", 1);
    }
    if(returnedData.candidateIsAssessed == 1){
        $(".assessmentIncomplete").hide();
        $(".assessmentComplete").show();
    } else {
        var options = {'showRowNumber': true};
        var data;
        var query = new google.visualization.Query('https://docs.google.com/spreadsheets/d/1HwEWPzZD4BFCyeRf5HO_KqNXyaMporxYQfg5lhOoA2g/edit#gid=496359801');

        function sendAndDraw() {
            var val = localStorage.getItem("mobile");
            query.setQuery('select C where C=' + val.substring(3, 13));
            query.send(handleQueryResponse);
        }

        function handleQueryResponse(response) {
            if (response.isError()) {
                return;
            }
            data = response.getDataTable();
            new google.visualization.Table(document.getElementById('table')).draw(data, options);
            var data2 = document.getElementsByClassName('google-visualization-table-td google-visualization-table-td-number').length;
            if(data2 == 0){
                $(".assessmentIncomplete").show();
                $(".assessmentComplete").hide();
            }
            else{
                $.ajax({
                    type: "GET",
                    url: "/updateIsAssessedToAssessed",
                    processData: false,
                    success: processAssessedStatus
                });
                $(".assessmentIncomplete").hide();
                $(".assessmentComplete").show();
                $.ajax({
                    type: "GET",
                    url: "/updateIsAssessedToAssessed",
                    processData: false,
                    success: processAssessedStatus
                });
            }
        }
        google.setOnLoadCallback(sendAndDraw);
    }

    if (returnedData.candidateGender != null) {
        localStorage.setItem("gender", returnedData.candidateGender);
        if (returnedData.candidateGender == 0) {
            try{
                document.getElementById("userGender").innerHTML = ", Male";
                $("#userImg").attr('src', '/assets/dashboard/img/userMale.svg');
            } catch(err){}
        } else {
            try{
                document.getElementById("userGender").innerHTML = ", Female";
                $("#userImg").attr('src', '/assets/dashboard/img/userFemale.svg');
            } catch(err){}
        }
    } else{
        try{
            $("#userImg").attr('src', '/assets/dashboard/img/userMale.svg');
        } catch(err){}
    }
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
        document.getElementById("userAge").innerHTML = ", " + age + " years";
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
            document.getElementById("userCurrentSalary").innerHTML = "Not Applicable";
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

    var appliedJobs = returnedData.jobApplicationList;
    $("#jobCount").html(Object.keys(appliedJobs).length);
    appliedJobs.forEach(function (jobApplication) {
        $("#apply_btn_" + jobApplication.jobPost.jobPostId).addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied");
        $("#applyBtnDiv_" + jobApplication.jobPost.jobPostId).prop('disabled',true);
    });

}

function sendAndDraw() {
    var val = localStorage.getItem("mobile");
    query.setQuery('select C where C=' + val.substring(3, 13));
    query.send(handleQueryResponse);
}

function processAssessedStatus(returnedData) {
    if(returnedData.leadType != '0' && returnedData.leadType != '1') {
        // existing data hence pre fill form
    } else {
        clearModal();
        alert('Unable to show data');
    }
}

function handleQueryResponse(response) {
    if (response.isError()) {
        return;
    }
    data = response.getDataTable();
    new google.visualization.Table(document.getElementById('table')).draw(data, options);
    var data2 = document.getElementsByClassName('google-visualization-table-td google-visualization-table-td-number').length;
    if(data2>0) {
        document.getElementById("assessedStatusResult").innerHTML = '<font color="#46AB49">Complete</font>';
        document.getElementById("assessedStatusAction").innerHTML = '-';
        $("#assessedIcon").attr('src', '/assets/dashboard/img/right.png');
        // update isAssessed status to '1'
        $.ajax({
            type: "GET",
            url: "/updateIsAssessedToAssessed",
            processData: false,
            success: processAssessedStatus
        });
    }
    else{
        try{
            document.getElementById("assessedStatusResult").innerHTML = '<font color="#F26522">Incomplete</font>';
            document.getElementById("assessedStatusAction").innerHTML = '<font size="2" color="#F26522">(Take assessment)</font></a>';
            var assessedStatusParent = document.getElementById("assessedStatusParent");
            assessedStatusParent.addEventListener("click", completeAssessment);
            assessedStatusParent.style = "cursor: pointer";
            $("#assessedIcon").attr('src', '/assets/dashboard/img/wrong.png');

        }
        catch(err){
        }
    }
}

function completeAssessment() {
    window.open("http://bit.ly/trujobstest");
}

function completeProfile() {
    window.open("/dashboard/editProfile");
}