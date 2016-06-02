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
            url: "/getAllLocality",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckAllLocalies
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    try {
        $.ajax({
            type: "GET",
            url: "/getAllJobs",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckAllJobRoles
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

    if(userMobile != null){
        document.getElementById("helloMsg").innerHTML = "Hello " + userName + "!";
        $('#userExist').show();
    }
    else{
        logoutUser();
        window.location = "/new";
    }

    function processDataGetCandidateLocality(returnedData) {
        var parent = $('.preferredLocation')[0];
        returnedData.forEach(function (locality) {
            var l = document.createElement("li");
            l.textContent = locality.locality.localityName;
            parent.appendChild(l);

            var id = locality.localityId;
            var name = locality.locality.localityName;
            var item = {}
            item ["id"] = id;
            item ["name"] = name;
            candidateLocalityArray.push(item);
        });
    }

    function processDataGetCandidateJobRoles(returnedData) {
        var parent = $('.preferredJobs')[0];
        returnedData.forEach(function (job) {
            var l = document.createElement("li");
            l.textContent = job.jobRole.jobName;
            parent.appendChild(l);

            var id = job.jobRole.jobRoleId;
            var name = job.jobRole.jobName;
            var item = {}
            item ["id"] = id;
            item ["name"] = name;
            candidateJobArray.push(item);
        });
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

    $('#profileStatusResult').text("Profile Incomplete");

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
            document.getElementById("assessedStatusResult").innerHTML = "Assessed";
            $("#assessedStatus").css("background","#137d00");
            $("#assessedStatus").css("color","#ffffff");
            // update isAssessed status to '1'
            $.ajax({
                type: "GET",
                url: "/updateIsAssessedToAssessed/" + localStorage.getItem("id"),
                processData: false,
                success: processAssessedStatus
            });
        }
        else{
            document.getElementById("assessedStatusResult").innerHTML = "Not Assessed ";
            $("#assessedStatus").css("background","#F77526");
            $("#assessedStatus").css("color","#ffffff");
        }
    }

    if(localStorage.getItem("assessed") == '0'){
        google.setOnLoadCallback(sendAndDraw);
    }
    else{
        try{
            document.getElementById("assessedStatusResult").innerHTML = "Assessed";
            $("#assessedStatus").css("background","#137d00");
            $("#assessedStatus").css("color","#ffffff");
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

                /* calculate total experience in months */
                var expMonth = parseInt($('#candidateTotalExperienceMonth').val());
                var expYear = parseInt($('#candidateTotalExperienceYear').val());
                var totalExp = expMonth + (12*expYear);

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

                /* calculate current job duration in months */
                var currentJobMonth = parseInt($('#candidateCurrentJobDurationMonth').val());
                var currentJobYear = parseInt($('#candidateCurrentJobDurationYear').val());
                var currentJobDuration = currentJobMonth + (12 * currentJobYear);

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

    function saveCandidateEducationDetails(){
        document.getElementById("saveBtn").disabled = true;
        try {

            var d = {
                candidateMobile: localStorage.getItem("mobile"),
                candidateFirstName: localStorage.getItem("name"),
                candidateSecondName: localStorage.getItem("lastName"),

                candidateEducationLevel: ($('input:radio[name="highestEducation"]:checked').val()),
                candidateDegree: ($('#candidateHighestDegree').val()),
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
});

