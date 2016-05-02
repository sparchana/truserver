/**
 * Created by zero on 23/4/16.
 */
// function to process the returned message from the server.
function processData(returnedData) {

    if(returnedData.status == '1') {
        console.log("returedData :" + returnedData.status);
        $('#addLeadForm').hide();
        $('#thanksMsgMain').show();
        $('#alreadyMsgMain').hide();
    }

    else if(returnedData.status == '3'){
        console.log("returedData :" + returnedData.status);
        $('#alreadyMsg').show();
    }
    else {
        console.log("returedData :" + returnedData.status);
        $('#alreadyMsg').hide();
        $('#addLeadForm').hide();
        $('#errorMsg').show();
    }
}

function processDataModal(returnedData) {

    if(returnedData.status == '1') {
        console.log("returedData :" + returnedData.status);
        $('#addLeadFormModal').hide();
        $('#thanksMsg').show();
        $('#alreadyMsg').hide();
    }

    else if(returnedData.status == '3'){
        console.log("returedData :" + returnedData.status);
        $('#alreadyMsg').show();
    }
    else {
        console.log("returedData :" + returnedData.status);
        $('#alreadyMsg').hide();
        $('#addLeadForm').hide();
        $('#errorMsg').show();
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
            var name  = " ";
            var phone = $('#leadMobile').val();
            var channel = $('#leadChannel').val();
            var type = 0;
            var interested = $('#leadInterest').val();
            console.log("phone : " + phone + " " + name + channel + type );

            var s = {
                leadName : name,
                leadMobile : phone,
                leadChannel : channel,
                leadType : 0,
                leadInterest : interested
            };

            $.ajax({
                type: "POST",
                url: "/addLead",
                data: s,
                success: processData
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }); // end of submit
    $("#addLeadFormModal").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var name  = $('#leadNameModal').val();
            var phone = $('#leadMobileModal').val();
            var channel = $('#leadChannelModal').val();
            var type = 1;
            var interested = $('#leadInterest').val();
            console.log("phone : " + phone + " " + name + channel + type );

            var s = {
                leadName : name,
                leadMobile : phone,
                leadChannel : channel,
                leadType : type,
                leadInterest : interested
            };

            $.ajax({
                type: "POST",
                url: "/addLead",
                data: s,
                success: processDataModal
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
