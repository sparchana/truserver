/**
 * Created by batcoder1 on 26/4/16.
 */

function processDataLogin(returnedData) {
    console.log("returedData :" + returnedData.status + returnedData.candidateName + returnedData.accountStatus);
    if(returnedData.status == 1) {
        // Store
        localStorage.setItem("mobile", $('#candidateLoginMobile').val());
        localStorage.setItem("name", returnedData.candidateName);
        window.location = "/dashboard";
    }

    else if(returnedData.status == 2){
        $('#incorrectMsg').show();
        $('#errorMsg').hide();
    }

    else {
        $('#noUserMsg').show();
        $('#incorrectMsg').hide();
        $('#errorMsg').hide();
    }
}

// form_candidate ajax script
$(function() {
    $("#form_login_candidate").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var phone  = $('#candidateLoginMobile').val();
            var password = $('#candidateLoginPassword').val();
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

