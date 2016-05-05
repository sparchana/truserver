/**
 * Created by zero on 23/4/16.
 */

function processDataModal(returnedData) {
    console.log("returedData :" + returnedData.status);
    $('#addLeadFormModal').hide();
    $('#thanksMsg').show();
    $('#alreadyMsg').hide();
}

function processDataRecruiter(returnedData) {
    console.log("returedData :" + returnedData.status);
    $('#form_recruiter').hide();
    $('#thanksMsgRecruiter').show();
    $('#errorMsgRecruiter').hide();
}

// function to post the reg data to the server
$(function() {
    $("#addLeadFormModal").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var name  = $('#leadNameModal').val();
            var phone = $('#leadMobileModal').val();
            var channel = $('#leadChannelModal').val();
            var type = 2;
            var interested = document.getElementById("interested").innerHTML;
            console.log("phone : " + phone + " " + interested);

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
