/**
 * Created by dodo on 17/11/16.
 */

var pathname;
var updateUrl;
var candidateId;
var jpId;
var showNotGoingModal = false;
var allReasons = [];

$(document).ready(function() {
    pathname = window.location.pathname; // Returns path only
    updateUrl = pathname.split('/');
    candidateId = updateUrl[(updateUrl.length) - 1];
    jpId = updateUrl[(updateUrl.length) - 2];

    try {
        $.ajax({
            type: "POST",
            url: "/getJpWfStatus/" + candidateId + "/" + jpId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForGetStatus
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

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
});

function processDataForGetStatus(returnedData) {
    var option1 = $('<option value="1"></option>').text("Not Going");
    var option2 = $('<option value="2"></option>').text("Delayed");
    var option3 = $('<option value="3"></option>').text("Started");
    var option4 = $('<option value="4"></option>').text("Reached");

    var parent = $('#status_val');
    var defaultOption = $('<option value="0" selected></option>').text("Select a status");
    parent.append(defaultOption);

    if(returnedData != 0 && returnedData != null){
        if(returnedData.status.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_DELAYED){ // delayed
            parent.append(option3);
            parent.append(option4);
        } else if(returnedData.status.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_STARTED){ // started
            parent.append(option4);
        } else if(returnedData.status.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){ // reached or not going
            alert("You have already reached!");
        } else {
            parent.append(option1);
            parent.append(option2);
            parent.append(option3);
            parent.append(option4);
        }
    } else{
        alert("Not allowed");
    }
}

function confirmUpdateStatusNotGoing(){
    if($("#notGoingReason").val() > 0){
        document.getElementById("updateNotGoingReasonBtn").disabled = true;
        updateStatus();
    } else{
        alert("Please select a reason for not going for interview");
    }
}


function updateStatus() {
    var notGoingReason = $("#notGoingReason").val();
    if($("#notGoingReason").val() == null || $("#notGoingReason").val() == undefined){
        notGoingReason = 0;
    }

    var statusVal = $("#status_val").val();
    if(statusVal > 0){
        if(statusVal == 1 && showNotGoingModal == false){
            showNotGoingModal = true;
        } else{
            showNotGoingModal = false;
        }
        try {
            $.ajax({
                type: "POST",
                url: "/updateStatus/" + candidateId + "/" + jpId + "/" + statusVal + "/" + notGoingReason,
                data: false,
                contentType: false,
                processData: false,
                success: processDataForUpdateStatus
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    } else {
        alert("Please select a status");
    }
}

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


function processDataForUpdateStatus(returnedData) {
    if(returnedData == 1){
        //disabling button
        document.getElementById("updateStatusBtn").disabled = true;
        $("#updateStatusBtn").val("Updated");

        if(showNotGoingModal){
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
        } else{
            alert("Status updated");
            $("#notGoingModal").modal("hide");
            setTimeout(function(){
                location.reload();
            }, 2000);
        }
    } else{
        alert("Something went wrong");
    }
}