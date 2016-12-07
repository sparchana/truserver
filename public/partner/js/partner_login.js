/**
 * Created by adarsh on 10/9/16.
 */
/**
 * Created by batcoder1 on 26/4/16.
 */
var candidateMobile;
var applyJobFlag = 0;
var applyJobId = 0;

function processDataLogin(returnedData) {
    if(returnedData.status == 1) {
        // Store
        window.location = "/partner/home";
    } else if(returnedData.status == 3){
        $('#errorMsgReset').show();
        $('#incorrectMsgLogin').hide();
    }

    else {
        $('#errorMsgReset').hide();
        $('#incorrectMsgLogin').show();
    }
}

// form_candidate ajax script
$(function() {
    $("#form_login_partner").submit(function(eventObj) {
        eventObj.preventDefault();
        var phone  = $('#partnerLoginMobile').val();
        var password = $('#partnerLoginPassword').val();
        if(phone == null || phone == ""){
            alert("Enter your Phone Number");
        } else if(password == null || password == ""){
            alert("Enter your Password");
        }
        else{
            try {
                candidateMobile = phone;
                var s = {
                    candidateLoginMobile: phone,
                    candidateLoginPassword : password
                };
                $.ajax({
                    type: "POST",
                    url: "/partnerLoginSubmit",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(s),
                    success: processDataLogin
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }); // end of submit
}); // end of function

