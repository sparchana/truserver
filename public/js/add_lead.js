/**
 * Created by zero on 23/4/16.
 */

function processDataRecruiter(returnedData) {
    console.log("returedData :" + returnedData.status);
    $('#form_recruiter').hide();
    $('#thanksMsgRecruiter').show();
    $('#errorMsgRecruiter').hide();
}

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
            var d = {
                leadName: name,
                leadMobile: phone,
                leadChannel: channel,
                leadType: type,
                leadInterest: interested
            }
            $.ajax({
                type: "POST",
                url: "/addLead",
                data: d,
                success: processDataRecruiter
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }); // end of submit
}); // end of function
