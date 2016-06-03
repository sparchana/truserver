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
        document.getElementById("userName").innerHTML = userName;
        document.getElementById("userMobile").innerHTML = userMobile;
        $('#userExist').show();
        $('#doneAssessment').hide();
        $('#takeAssessment').hide();
    }
    else{
        logoutUser();
        window.location = "/";
    }

    function processDataGetCandidateLocality(returnedData) {
        var parent = $('.preferredLocation')[0];
        if(returnedData != null) {
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
    }

    function processDataGetCandidateJobRoles(returnedData) {
        var parent = $('.preferredJobs')[0];
        if(returnedData != null){
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
            var item = {}
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

    function handleQueryResponse(response) {
        if (response.isError()) {
            return;
        }
        data = response.getDataTable();
        new google.visualization.Table(document.getElementById('table')).draw(data, options);
        var data2 = document.getElementsByClassName('google-visualization-table-td google-visualization-table-td-number').length;
        if(data2>0) {
            $('#doneAssessment').show();
            $('#takeAssessment').hide();
        }
        else{
            $('#doneAssessment').hide();
            $('#takeAssessment').show();
        }
    }
    google.setOnLoadCallback(sendAndDraw);
});

