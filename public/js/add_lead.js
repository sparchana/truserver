/**
 * Created by zero on 23/4/16.
 */
// function to process the returned message from the server.
function processData(returnedData) {
    console.log("returedData :" + returnedData.status);
}

// function to post the reg data to the server
$(function() {
    $("#addLeadForm").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var name  = $('#leadName').val();
            var phone = $('#leadMobile').val();
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