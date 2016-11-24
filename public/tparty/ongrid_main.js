/**
 * Created by archana on 11/15/16.
 */

$(function(){
    $( "#testOGAPI" ).click(function() {
        //sendAadharSyncRequest();

        sendAadharVerifyRequestIndirect();
    });
});

function sendAadharVerifyRequestIndirect()
{
    try {
        $.ajax({
         type: "POST",
         crossDomain: true,
         url: "/api/compute/verifyAadhar",
         async: true,
         success: handleAadharVerificationResponse
         });
    }
    catch (exception) {
        console.log("exception occured!!" + exception);
    }

}

function sendAadharVerifyRequestDirect()
{
    var url_prefix = "https://api-staging.ongrid.in/app/v1/aadhaar/";
    var uid = "855022384898";
    var url_suffix = "/verifySync";

    ""

    try {
        var reqParams = {
            "name": "Archana",
            "gender": "Female",
            "city": "Coimbatore North",
            "professionId": "69",
            "otherProfession": "Business",
            "phone": "8197222248",
            "email": "sp.archana@gmail.com",
            "dob": "16/01/1985",
            "age": "",
            "aadhaarAddress": {
            "co": "",
                "line1": "",
                "line2": "",
                "locality": "",
                "landmark": "",
                "vtc": "",
                "district": "",
                "state": "",
                "pincode": "",
            },
            "communityId" : "66095",
    };
    var url = url_prefix + uid + url_suffix;

    /*$.ajax({
        type: "POST",
        crossDomain: true,
        url: url,
        async: true,
        headers: { content-type: application/json, authorization: Basic dHJ1am9iczo4RFI4TkdoTHh3cjBBWmZBd3BHaU0rTGwwM3JqRkhZVHQ2NU04aWdXVm1yM09PVyttamJLVFpmYUpXeHI1RnNW},
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(reqParams),
        success: handleAadharVerificationResponse
    });*/

        var req_body = {
            "async": true,
            "crossDomain": false,
            "url": "https://api-staging.ongrid.in/app/v1/aadhaar/855022384898/verifySync",
            "method": "POST",
            "headers": {
                "content-type": "application/json",
                "authorization": "Basic dHJ1am9iczo4RFI4TkdoTHh3cjBBWmZBd3BHaU0rTGwwM3JqRkhZVHQ2NU04aWdXVm1yM09PVyttamJLVFpmYUpXeHI1RnNW"
            },
            "processData": false,
            "data": JSON.stringify(reqParams)
        }

        $.ajax(req_body).done(function (data) {
            console.log(data);
        });

    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

//  headers: " { \"content-type\": \"application/json\", \"authorization\": \"Basic dHJ1am9iczo4RFI4TkdoTHh3cjBBWmZBd3BHaU0rTGwwM3JqRkhZVHQ2NU04aWdXVm1yM09PVyttamJLVFpmYUpXeHI1RnNW\"}",
function handleAadharVerificationResponse(response) {
    console.log("response " + response);
}
