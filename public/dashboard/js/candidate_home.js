/**
 * Created by batcoder1 on 4/6/16.
 */
$(document).ready(function(){
    var userMobile = localStorage.getItem("mobile");
    var userName = localStorage.getItem("name");
    var userLastName = localStorage.getItem("lastName");

    if(userMobile != null){
        document.getElementById("helloMsg").innerHTML = "Hello " + userName + "!";
        try{
            if(userLastName == "null" || userLastName == null){
                document.getElementById("userName").innerHTML = userName;
            } else{
                document.getElementById("userName").innerHTML = userName + " " + userLastName;
            }
            document.getElementById("userMobile").innerHTML = userMobile;
        } catch(err){
        }

        $('#userExist').show();
    }
    else{
        logoutUser();
        window.location = "/new";
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateInfo/" + localStorage.getItem("leadId"),
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
                    url: "/updateIsAssessedToAssessed/" + localStorage.getItem("id"),
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

function processDataAndFillMinProfile(returnedData) {
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

}

function logoutUser() {
    localStorage.clear();
    window.location = "/";
    try {
        $.ajax({
            type: "GET",
            url: "/logoutUser",
            data: false,
            contentType: false,
            processData: false,
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
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
            url: "/updateIsAssessedToAssessed/" + localStorage.getItem("id"),
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