/**
 * Created by batcoder1 on 26/4/16.
 */
var candidateMobile;
function processDataLogin(returnedData) {
    console.log("returedData :" + returnedData.status + returnedData.candidateName + returnedData.accountStatus);
    if(returnedData.status == 1) {
        // Store
        localStorage.setItem("mobile", "+91" + candidateMobile);
        localStorage.setItem("name", returnedData.candidateName);
        localStorage.setItem("id", returnedData.candidateId);
        window.location = "/dashboard";
    }

    else if(returnedData.status == 3){
        $('#noUserLogin').show();
        $('#incorrectMsgLogin').hide();
    }

    else {
        $('#noUserLogin').hide();
        $('#incorrectMsgLogin').show();
    }
}

// form_candidate ajax script
$(function() {
    $("#form_login_candidate").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var phone  = $('#candidateLoginMobile').val();
            var password = $('#candidateLoginPassword').val();
            candidateMobile = phone;
            console.log("phone: " + phone);
            var s = {
                candidateLoginMobile: phone,
                candidateLoginPassword : password 
            };
            $.ajax({
                type: "POST",
                url: "/loginSubmit",
                data: s,
                success: processDataLogin
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit
}); // end of function

