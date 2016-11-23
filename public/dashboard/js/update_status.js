/**
 * Created by dodo on 17/11/16.
 */

var pathname;
var updateUrl;
var candidateId;
var jpId;

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
        if(returnedData.status.statusId == 11){ // delayed
            parent.append(option3);
            parent.append(option4);
        } else if(returnedData.status.statusId == 12){ // started
            parent.append(option4);
        } else if(returnedData.status.statusId == 10 || returnedData.status.statusId == 13){ // reached or not going
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

function updateStatus() {
    if($("#status_val").val() > 0){
        try {
            $.ajax({
                type: "POST",
                url: "/updateStatus/" + candidateId + "/" + jpId + "/" + $("#status_val").val(),
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

function processDataForUpdateStatus(returnedData) {
    if(returnedData == 1){
        alert("Status updated");
    } else{
        alert("Something went wrong");
    }
}