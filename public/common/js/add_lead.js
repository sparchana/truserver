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
        var phone = $('#leadMobile').val();
        var res = validateMobile(phone);
        if(res == 0){ // invalid mobile
            alert("Enter a valid mobile number");
        } else if(res == 1){ // mobile no. less than 1 digits
            alert("Enter 10 digit mobile number");
        }
        else{
            try {
                var name = $('#leadName').val();
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
                };
                $.ajax({
                    type: "POST",
                    url: "/addLead",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataRecruiter
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }); // end of submit
}); // end of function
