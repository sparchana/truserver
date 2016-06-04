/**
 * Created by batcoder1 on 16/5/16.
 */

$(document).ready(function(){
    var localityArray = [];
    var candidateLocalityArray = []; //candidate preferred localities
    var jobArray = [];
    var candidateJobArray = []; //candidate preferred Job roles

    try {
        $.ajax({
            type: "GET",
            url: "/checkMinProfile/" + localStorage.getItem("id"),
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckMinProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateLocality/" + localStorage.getItem("id"),
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetCandidateLocality
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    
    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateJob/" + localStorage.getItem("id"),
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetCandidateJobRoles
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

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

    function processDataGetCandidateLocality(returnedData) {
        var localities = "";
        var count = 0;
        returnedData.forEach(function (locality) {
            count ++;
            localities += locality.locality.localityName;
            if(count < Object.keys(returnedData).length){
                localities += ", ";
            }

            var id = locality.localityId;
            var name = locality.locality.localityName;
            var item = {}
            item ["id"] = id;
            item ["name"] = name;
            candidateLocalityArray.push(item);
        });
        document.getElementById("userLocality").innerHTML = localities;
    }

    function processDataCheckMinProfile(returnedData) {
        if(returnedData == 0){ // profile not complete
            document.getElementById("profileStatusResult").innerHTML = '<font color="#F26522">Incomplete</font>';
            document.getElementById("profileStatusAction").innerHTML = '<font size="2" color="#F26522">(Complete Profile Now)</font>';
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
    }

    function completeAssessment() {
        window.open("http://bit.ly/trujobstest");
    }

    function completeProfile() {
        window.open("/dashboard/editProfile/basic");
    }

    function processDataGetCandidateJobRoles(returnedData) {
        var jobRoles = "";
        var count = 0;
        returnedData.forEach(function (job) {
            count++;
            jobRoles += job.jobRole.jobName;
            if(count < Object.keys(returnedData).length){
                jobRoles += ", ";
            }

            var id = job.jobRole.jobRoleId;
            var name = job.jobRole.jobName;
            var item = {}
            item ["id"] = id;
            item ["name"] = name;
            candidateJobArray.push(item);
        });
        document.getElementById("userJobs").innerHTML = jobRoles;
    }

    function processDataCheckAllLocalies(returnedData) {
        returnedData.forEach(function(locality)
        {
            var id = locality.localityId;
            var name = locality.localityName;
            var item = {}
            item ["id"] = id;
            item ["name"] = name;
            localityArray.push(item);
        });
    }
    function processDataCheckAllJobRoles(returnedData) {
        returnedData.forEach(function(job)
        {
            var id = job.jobId;
            var name = job.jobName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            jobArray.push(item);
        });
    }

    var options = {'showRowNumber': true};
    var data;
    var query = new google.visualization.Query('https://docs.google.com/spreadsheets/d/1HwEWPzZD4BFCyeRf5HO_KqNXyaMporxYQfg5lhOoA2g/edit#gid=496359801');

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

    if(localStorage.getItem("assessed") == '0'){
        google.setOnLoadCallback(sendAndDraw);
    }
    else{
        try{
            document.getElementById("assessedStatusResult").innerHTML = '<font color="#46AB49">Complete</font>';
            document.getElementById("assessedStatusAction").innerHTML = '-';
            $("#assessedIcon").attr('src', '/assets/dashboard/img/right.png');
        } catch(err){
            console.log("try catch exception");
        }
    }

    function processDataCandidateSaveBasicProfile(returnedData) {
        if(returnedData.status == 1){ // save successful
            window.location = "/dashboard/editProfile/skill";
        }
        else if(returnedData.status == 2){ // save failed
            console.log("Unable to save the candidate record!");
            window.location = "/dashboard/editProfile/skill";
        }
        else { // candidate exists
            window.location = "/dashboard/editProfile/skill";
        }
    }

    function processDataCandidateExperienceUpdate(returnedData) {
        if(returnedData.status == 1){ // save successful
            window.location = "/dashboard/editProfile/education";
        }
        else if(returnedData.status == 2){ // save failed
            console.log("Unable to save the candidate record!");
            window.location = "/dashboard/editProfile/education";
        }
        else { // candidate exists
            window.location = "/dashboard/editProfile/education";
        }
    }

    function processDataCandidateEducationUpdate(returnedData) {
        if(returnedData.status == 1){ // save successful
            window.location = "/dashboard";
        }
        else if(returnedData.status == 2){ // save failed
            console.log("Unable to save the candidate record!");
            window.location = "/dashboard";
        }
        else { // candidate exists
            window.location = "/dashboard";
        }
    }

    $("#candidateUpdateBasicProfile").submit(function(eventObj) {
        eventObj.preventDefault();
        saveCandidateBasicProfile();
    }); // end of submit

    $("#candidateUpdateLanguageAndSkills").submit(function(eventObj) {
        eventObj.preventDefault();
        saveCandidateExperienceDetails();
    }); // end of submit

    $("#candidateUpdateEducationDetails").submit(function(eventObj) {
        eventObj.preventDefault();
        saveCandidateEducationDetails();
    }); // end of submit


    function saveCandidateBasicProfile(){
        var localitySelected = $('#candidateLocalityPref').val();
        var jobSelected = $('#candidateJobPref').val();
        var isEmployed = $("#isEmployed").val();
        var currentSalary = $("#candidateCurrentJobSalary").val();

        if (localitySelected == "") {
            alert("Please Enter your Job Localities");
        } else if (jobSelected == "") {
            alert("Please Enter the Jobs you are Interested");
        } else if(isEmployed == "1" && currentSalary == ""){
            alert("Please Enter your Current Salary");
        } else if($('#candidateTimeShiftPref').val() == -1){
            alert("Please Enter Your Preferred Work Shift");
        }
        else{
            document.getElementById("saveBtn").disabled = true;
            try {
                var selectedDob = $('#dob_year').val() + "-" + $('#dob_month').val() + "-" + $('#dob_day').val();
                var c_dob = String(selectedDob);

                var d = {
                    //mandatory fields
                    candidateFirstName: $('#candidateFirstName').val(),
                    candidateSecondName: $('#candidateSecondName').val(),
                    candidateMobile: $('#candidateMobile').val(),
                    candidateLocality: $('#candidateLocalityPref').val(),
                    candidateJobInterest: $('#candidateJobPref').val(),

                    //others
                    candidateDob: c_dob,
                    candidateTimeShiftPref: $('#candidateTimeShiftPref').val(),
                    candidateGender: ($('input:radio[name="gender"]:checked').val())
                };

                $.ajax({
                    type: "POST",
                    url: "/candidateUpdateBasicProfile",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataCandidateSaveBasicProfile
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }

    function saveCandidateExperienceDetails(){

        var experienceStatus = $('input:radio[name="workExperience"]:checked').val();
        if(experienceStatus == null){
            alert("Please Select your work experience");
        }
        else{
            /* calculate total experience in months */
            var expMonth = parseInt($('#candidateTotalExperienceMonth').val());
            var expYear = parseInt($('#candidateTotalExperienceYear').val());
            var totalExp = expMonth + (12*expYear);

            if(experienceStatus == 1 && $('input:radio[name="isEmployed"]:checked').val() == null){
                alert("Select Current Employment Status");
            }
            else if(experienceStatus == 1 && totalExp == 0){
                    alert("Select Total Years of Experience");
            }
            else{
                document.getElementById("saveBtn").disabled = true;
                try {
                    var languageKnown = $('#languageTable input:checked').map(function() {
                        var check=0;
                        var id = this.id;
                        var name = this.name;
                        var item = {};
                        var pos;

                        for(var i in languageMap){
                            if(languageMap[i].id == id){
                                pos=i;
                                check=1;
                                break;
                            }
                        }
                        if(check==0){
                            item["id"] = id;
                            item["r"] = 0;
                            item["w"] = 0;
                            item["s"] = 0;
                            if(name == "r")
                                item["r"] = 1;
                            else if(name == "w")
                                item["w"] = 1;
                            else
                                item["s"] = 1;
                            languageMap.push(item);
                        }
                        else{
                            if(name == "r")
                                languageMap[pos].r = 1;
                            else if(name == "w")
                                languageMap[pos].w = 1;
                            else
                                languageMap[pos].s = 1;
                        }
                    }).get();

                    var candidateCurrentCompanyVal = "";
                    var candidateCurrentSalaryVal = "";

                    if($('input:radio[name="isEmployed"]:checked').val() == 0){
                        candidateCurrentCompanyVal = null;
                        candidateCurrentSalaryVal = null;
                    }
                    else{
                        candidateCurrentCompanyVal = $('#candidateCurrentCompany').val();
                        candidateCurrentSalaryVal = $('#candidateCurrentJobSalary').val();
                    }
                    
                    var d = {
                        candidateMobile: localStorage.getItem("mobile"),
                        candidateFirstName: localStorage.getItem("name"),
                        candidateSecondName: localStorage.getItem("lastName"),

                        candidateTotalExperience: totalExp,
                        candidateIsEmployed: $('input:radio[name="isEmployed"]:checked').val(),
                        candidateCurrentCompany: candidateCurrentCompanyVal,
                        candidateCurrentSalary: candidateCurrentSalaryVal,

                        candidateMotherTongue: ($('#candidateMotherTongue').val()),
                        candidateLanguageKnown: languageMap,

                        candidateSkills: skillMap
                    };

                    $.ajax({
                        type: "POST",
                        url: "/candidateUpdateExperienceDetails",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify(d),
                        success: processDataCandidateExperienceUpdate
                    });
                } catch (exception) {
                    console.log("exception occured!!" + exception);
                }
            }

        }
    }

    function saveCandidateEducationDetails(){
        if($('#candidateHighestEducation').val() == -1){
            alert("Select a Degree");
        }
        else{
            if((($('#candidateHighestEducation').val() == 4) || ($('#candidateHighestEducation').val() == 5)) && $('#candidateHighestDegree').val() == -1){
                alert("Please select your Highest Degree");
            }
            else{
                document.getElementById("saveBtn").disabled = true;
                try {
                    var d = {
                        candidateMobile: localStorage.getItem("mobile"),
                        candidateFirstName: localStorage.getItem("name"),
                        candidateSecondName: localStorage.getItem("lastName"),

                        candidateEducationLevel: $('input:radio[name="highestEducation"]:checked').val(),
                        candidateDegree: $('#candidateHighestDegree').val(),
                        candidateEducationInstitute: $('#candidateEducationInstitute').val(),
                    };

                    $.ajax({
                        type: "POST",
                        url: "/candidateUpdateEducationDetails",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify(d),
                        success: processDataCandidateEducationUpdate
                    });
                } catch (exception) {
                    console.log("exception occured!!" + exception);
                }
            }
        }
    }
});

