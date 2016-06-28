/**
 * Created by batcoder1 on 4/6/16.
 */

var minProfileComplete = 0;
$(document).ready(function(){
    checkUserLogin();
    try {
        $.ajax({
            type: "POST",
            url: "/getAllHotJobPosts",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataAllJobPosts
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateInfoDashboard",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataAndFillMinProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }


    if(localStorage.getItem("assessed") == '0'){
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
        google.setOnLoadCallback(sendAndDraw);
    }
    else{
        document.getElementById("assessedStatusResult").innerHTML = '<font color="#46AB49">Complete</font>';
        document.getElementById("assessedStatusAction").innerHTML = '-';
        $("#assessedIcon").attr('src', '/assets/dashboard/img/right.png');
    }
});

function processDataAllJobPosts(returnedData) {
    var count = 0;
    var jobPostCount = Object.keys(returnedData).length;
    if(jobPostCount > 0){
        var parent = $("#hotJobPosts");
        /* for first 3 active items (slider) */
        var jobItemMain = document.createElement("div");
        jobItemMain.className = "item active";
        parent.append(jobItemMain);
        returnedData.forEach(function (jobPosts){
            count ++;
            if(count > 3){
                return false;
            }
            var jobItem = document.createElement("div");
            jobItem.className = "col-lg-4";
            jobItemMain.appendChild(jobItem);
            var jobItemPanel = document.createElement("div");
            jobItemPanel.className = "panel";
            jobItemPanel.id = "hot_box";
            jobItem.appendChild(jobItemPanel);
            var alreadyApplied = document.createElement("img");
            alreadyApplied.style = "width: 36px; margin-top: -30px; margin-right: -30px; float: right";
            alreadyApplied.id = "already_applied_" + jobPosts.jobPostId;
            jobItemPanel.appendChild(alreadyApplied);
            var jobItemPanelHeading = document.createElement("div");
            jobItemPanelHeading.className = "panel-heading";
            jobItemPanelHeading.id = "hot_box_head";
            jobItemPanel.appendChild(jobItemPanelHeading);
            var jobLogo = document.createElement("img");
            jobLogo.style = "height: 75px";
            jobLogo.src = "/assets/new/img/" + jobPosts.company.companyLogo + ".png";
            jobItemPanelHeading.appendChild(jobLogo);
            var jobItemPanelBody = document.createElement("div");
            jobItemPanelBody.className = "panel-body";
            jobItemPanelBody.id = "hot_box_body";
            jobItemPanel.appendChild(jobItemPanelBody);
            var jobItemRole = document.createElement("div");
            jobItemRole.className = "hot_body_role";
            jobItemRole.textContent = jobPosts.jobPostTitle;
            jobItemPanelBody.appendChild(jobItemRole);

            var jobItemHr1 = document.createElement("div");
            jobItemHr1.style = "height: 1px; background: #0B4063";
            jobItemPanelBody.appendChild(jobItemHr1);
            var jobItemSalary = document.createElement("div");
            jobItemSalary.className = "hot_body_salary";
            if(jobPosts.jobPostMaxSalary == "0"){
                jobItemSalary.textContent = "₹" + jobPosts.jobPostMinSalary + " monthly";
            } else{
                jobItemSalary.textContent = "₹" + jobPosts.jobPostMinSalary + " - ₹" + jobPosts.jobPostMaxSalary + " monthly";
            }
            jobItemPanelBody.appendChild(jobItemSalary);
            var jobItemExperience = document.createElement("div");
            jobItemExperience.className = "hot_body_salary";
            jobItemExperience.textContent = "Experience: " + jobPosts.jobPostExperience.experienceType;
            jobItemPanelBody.appendChild(jobItemExperience);
            var jobItemHr2 = document.createElement("div");
            jobItemHr2.style = "height: 1px; background: #0B4063";
            jobItemPanelBody.appendChild(jobItemHr2);
            var jobItemLocation = document.createElement("div");
            jobItemLocation.className = "hot_body_location";
            var localityList = jobPosts.jobPostToLocalityList;
            var localities = "";
            var loopCount = 0;
            localityList.forEach(function (locality) {
                loopCount ++;
                if(loopCount > 2){
                    return false;
                }
                var name = locality.locality.localityName;
                localities += name;
                if(loopCount < Object.keys(localityList).length){
                    localities += ", ";
                }
            });
            if(((localityList.length) - 2) > 0 ){
                localities += " more";
            }
            jobItemLocation.textContent = localities;
            jobItemPanelBody.appendChild(jobItemLocation);
            var applyBtnDiv = document.createElement("div");
            applyBtnDiv.className = "btn btn-primary";
            applyBtnDiv.id = jobPosts.jobPostId;
            applyBtnDiv.onclick = function () {
                applyJob(jobPosts.jobPostId);
            };
            applyBtnDiv.style = "width: 100%; font-weight: bold";
            jobItemPanelBody.appendChild(applyBtnDiv);
            var btnFont = document.createElement("font");
            btnFont.size = "2";
            btnFont.textContent = "Apply";
            applyBtnDiv.appendChild(btnFont);
        });

        /* for jobs more than 3(active) */
        var totalJob = jobPostCount;
        jobPostCount = jobPostCount - 3;
        if(jobPostCount>0){
            var jobPostSectionCount = Math.floor(jobPostCount/3);
            var i;
            var startIndex = 3;
            for(i=0;i<jobPostSectionCount+1;i+=1){
                setJobs(returnedData,startIndex,totalJob);
                startIndex+=3;
            }
        }
    }
}

function setJobs(returnedData, start, totalJobs){
    var parent = $("#hotJobPosts");
    var jobItemMain = document.createElement("div");
    jobItemMain.className = "item";
    parent.append(jobItemMain);
    var count = 0;
    returnedData.forEach(function (jobPosts){
        count++;
        if(count > start && (count < start+4 && count<= totalJobs)){
            var jobItem = document.createElement("div");
            jobItem.className = "col-lg-4";
            jobItemMain.appendChild(jobItem);
            var jobItemPanel = document.createElement("div");
            jobItemPanel.className = "panel";
            jobItemPanel.id = "hot_box";
            jobItem.appendChild(jobItemPanel);
            var alreadyApplied = document.createElement("img");
            alreadyApplied.style = "width: 36px; margin-top: -30px; margin-right: -30px; float: right";
            alreadyApplied.id = "already_applied_" + jobPosts.jobPostId;
            jobItemPanel.appendChild(alreadyApplied);
            var jobItemPanelHeading = document.createElement("div");
            jobItemPanelHeading.className = "panel-heading";
            jobItemPanelHeading.id = "hot_box_head";
            jobItemPanel.appendChild(jobItemPanelHeading);
            var jobLogo = document.createElement("img");
            jobLogo.style = "margin-top: -20px";
            jobLogo.src = "/assets/new/img/" + jobPosts.company.companyLogo + ".png";
            jobItemPanelHeading.appendChild(jobLogo);
            var jobItemPanelBody = document.createElement("div");
            jobItemPanelBody.className = "panel-body";
            jobItemPanelBody.id = "hot_box_body";
            jobItemPanel.appendChild(jobItemPanelBody);
            var jobItemRole = document.createElement("div");
            jobItemRole.className = "hot_body_role";
            jobItemRole.textContent = jobPosts.jobPostTitle;
            jobItemPanelBody.appendChild(jobItemRole);
            var jobItemHr1 = document.createElement("div");
            jobItemHr1.style = "height: 1px; background: #0B4063";
            jobItemPanelBody.appendChild(jobItemHr1);
            var jobItemSalary = document.createElement("div");
            jobItemSalary.className = "hot_body_salary";
            if(jobPosts.jobPostMaxSalary == "0"){
                jobItemSalary.textContent = "₹" + jobPosts.jobPostMinSalary + " monthly";
            } else{
                jobItemSalary.textContent = "₹" + jobPosts.jobPostMinSalary + " - ₹" + jobPosts.jobPostMaxSalary + " monthly";
            }
            jobItemPanelBody.appendChild(jobItemSalary);
            var jobItemExperience = document.createElement("div");
            jobItemExperience.className = "hot_body_salary";
            jobItemExperience.textContent = "Experience: " + jobPosts.jobPostExperience.experienceType;
            jobItemPanelBody.appendChild(jobItemExperience);
            var jobItemHr2 = document.createElement("div");
            jobItemHr2.style = "height: 1px; background: #0B4063";
            jobItemPanelBody.appendChild(jobItemHr2);
            var jobItemLocation = document.createElement("div");
            jobItemLocation.className = "hot_body_location";
            var localityList = jobPosts.jobPostToLocalityList;
            var localities = "";
            var loopCount = 0;
            localityList.forEach(function (locality) {
                loopCount ++;
                if(loopCount > 2){
                    return false;
                }
                var name = locality.locality.localityName;
                localities += name;
                if(loopCount < Object.keys(localityList).length){
                    localities += ", ";
                }
            });
            if(((localityList.length) - 2) > 0 ){
                localities += " more";
            }
            jobItemLocation.textContent = localities;
            jobItemPanelBody.appendChild(jobItemLocation);
            var applyBtnDiv = document.createElement("div");
            applyBtnDiv.className = "btn btn-primary";
            applyBtnDiv.id = jobPosts.jobPostId;
            applyBtnDiv.onclick = function () {
                applyJob(jobPosts.jobPostId);
            };
            applyBtnDiv.style = "width: 100%; font-weight: bold";
            jobItemPanelBody.appendChild(applyBtnDiv);
            var btnFont = document.createElement("font");
            btnFont.size = "2";
            btnFont.textContent = "Apply";
            applyBtnDiv.appendChild(btnFont);
        }
    });
}

function processDataAndFillMinProfile(returnedData) {
    minProfileComplete = returnedData.isMinProfileComplete;
    if(returnedData.isMinProfileComplete == 0){ // profile not complete
        document.getElementById("profileStatusResult").innerHTML = '<font color="#F26522">Incomplete</font>';
        document.getElementById("profileStatusAction").innerHTML = '<font size="2" color="#F26522">(Complete Profile)</font>';
        var profileStatusParent = document.getElementById("profileStatusParent");
        profileStatusParent.addEventListener("click", completeProfile);
        profileStatusParent.style = "cursor: pointer";
        $("#profileStatusIcon").attr('src', '/assets/dashboard/img/wrong.png');
    }
    else{
        document.getElementById("profileStatusResult").innerHTML = '<font color="#46AB49">Complete</font>';
        document.getElementById("profileStatusAction").innerText = "-";
        $("#profileStatusIcon").attr('src', '/assets/dashboard/img/right.png');
    }
    if (returnedData.candidateGender != null) {
        localStorage.setItem("gender", returnedData.candidateGender);
        if (returnedData.candidateGender == 0) {
            try{
                document.getElementById("userGender").innerHTML = ", Male";
                $("#userGenderIcon").attr('src', '/assets/dashboard/img/male.png');
            } catch(err){
            }
        } else if(returnedData.candidateGender == 1) {
            try{
                document.getElementById("userGender").innerHTML = ", Female";
                $("#userGenderIcon").attr('src', '/assets/dashboard/img/female.png');
            } catch(err){
            }
        } else{
            try{
                $("#userGenderIcon").attr('src', '');
            } catch(err){
            }
        }
        
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
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate()))
        {
            age--;
        }
        document.getElementById("userAge").innerHTML = ", " + age + " years";

    }
    try {
        var jobRoles = "";
        var count = 0;
        var jobPref = returnedData.jobPreferencesList;
        jobPref.forEach(function (job){
            count ++;
            var name = job.jobRole.jobName;
            jobRoles += name;
            if(count < Object.keys(jobPref).length){
                jobRoles += ", ";
            }
        });
        document.getElementById("userJobs").innerHTML = jobRoles;
    } catch(err){
        console.log(err);
    }

    try {
        var localities = "";
        count = 0;
        var localityPref = returnedData.localityPreferenceList;
        localityPref.forEach(function (individualLocality){
            count++;
            var name = individualLocality.locality.localityName;
            localities += name;
            if(count < Object.keys(localityPref).length){
                localities += ", ";
            }
        });
        document.getElementById("userLocality").innerHTML = localities;
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

    /* Current Company and Salary */
    if (returnedData.candidateCurrentJobDetail != null) {
        if(returnedData.candidateCurrentJobDetail.candidateCurrentSalary != null){
            if(returnedData.candidateCurrentJobDetail.candidateCurrentSalary == "0"){
                document.getElementById("userCurrentSalary").innerHTML = "Fresher";
            } else{
                document.getElementById("userCurrentSalary").innerHTML = "&#x20B9;" + returnedData.candidateCurrentJobDetail.candidateCurrentSalary + "/month";
            }
        }
        if(returnedData.candidateCurrentJobDetail.candidateCurrentCompany != null){
            if(returnedData.candidateCurrentJobDetail.candidateCurrentCompany != ""){
                document.getElementById("userCurrentCompany").innerHTML = returnedData.candidateCurrentJobDetail.candidateCurrentCompany;
            }
        }
    }

    /* candidate Education */
    try{
        if(returnedData.candidateEducation.education != null) {
            document.getElementById("userEducationLevel").innerHTML = returnedData.candidateEducation.education.educationName;
        }
    } catch(err){
        console.log("education is null");
    }

    /* Work Experience */
    if(returnedData.candidateTotalExperience != null){
        if(returnedData.candidateTotalExperience == 0) {
            document.getElementById("userTotalExperience").innerHTML = "Fresher";
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
        }
    }

    var appliedJobs = returnedData.jobApplicationList;
    appliedJobs.forEach(function (jobApplication) {
        $("#already_applied_" + jobApplication.jobPost.jobPostId).attr('src', '/assets/dashboard/img/right.png');
    });
    /* /assets/dashboard/img/right.png */

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