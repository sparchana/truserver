/**
 * Created by zero on 23/4/16.
 */
// function to process the returned message from the server.
function processData(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == '1') {

        $('#addLeadForm').hide();
        $('#thanksMsg').show();
        $('#alreadyMsg').hide();
    }

    else if(returnedData.status == '3'){
        $('#alreadyMsg').show();
    }
    else {
        $('#alreadyMsg').hide();
        $('#addLeadForm').hide();
        $('#errorMsg').show();
    }
}

function processDataCandidate(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == '1') {
        $('#form_candidate').hide();
        $('#thanksMsgCandidate').show();
        $('#alreadyMsgCandidate').hide();
    }

    else if(returnedData.status == '3'){
        $('#alreadyMsgCandidate').show();
    }
    else {
        $('#alreadyMsgCandidate').hide();
        $('#form_candidate').hide();
        $('#errorMsgCandidate').show();
    }
}

function processDataRecruiter(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == '1') {
        $('#form_recruiter').hide();
        $('#thanksMsgRecruiter').show();
        $('#errorMsgRecruiter').hide();
    }

    else if(returnedData.status == '3'){
        $('#alreadyMsgRecruiter').show();
    }
    else {
        $('#alreadyMsgRecruiter').hide();
        $('#form_recruiter').hide();
        $('#errorMsgRecruiter').show();
    }
}

// function to post the reg data to the server
$(function() {
    $("#addLeadForm").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var name  = $('#leadName').val();
            var phone = $('#leadMobile').val();
            var channel = $('#leadChannel').val();
            var type = $('#leadType').val();
            var interested = $('#leadInterest').val();
            console.log("phone : " + phone + " || Base64+Serial: " + btoa($("#addLeadForm").serialize()));
            console.log("phone: " + phone);
            $.ajax({
                type: "POST",
                url: "/addLead",
                data: $("#addLeadForm").serialize(),
                dataType: "json",
                success: processData
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit
}); // end of function

// form_candidate ajax script
$(function() {
    $("#form_candidate").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var name  = $('#leadName').val();
            var phone = $('#leadMobile').val();
            var channel = $('#leadChannel').val();
            var type = $('#leadType').val();
            var interested = $('#leadInterest').val();
            console.log("phone: " + phone);
            $.ajax({
                type: "POST",
                url: "/addLead",
                data: $("#form_candidate").serialize(),
                dataType: "json",
                success: processDataCandidate
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit
}); // end of function

// form_recruiter ajax script
$(function() {
    $("#form_recruiter").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var name  = $('#leadName').val();
            var phone = $('#leadMobile').val();
            var channel = $('#leadChannel').val();
            var type = $('#leadType').val();
            var interested = $('#leadInterest').val();
            console.log("phone: " + phone);
            $.ajax({
                type: "POST",
                url: "/addLead",
                data: $("#form_recruiter").serialize(),
                dataType: "json",
                success: processDataRecruiter
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit
}); // end of function
