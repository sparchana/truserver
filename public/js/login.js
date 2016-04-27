/**
 * Created by batcoder1 on 26/4/16.
 */

function processData(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        // Store
        localStorage.setItem("mobile", $('#candidateLoginMobile').val());
        window.location = "/dashboard";
    }

    else if(returnedData.status == 2){
        $('#incorrectMsg').show();
        $('#errorMsg').hide();
    }

    else {
        $('#incorrectMsg').hide();
        $('#errorMsg').show();
    }
}

// form_candidate ajax script
$(function() {
    $("#form_login_candidate").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var name  = $('#candidateLoginMobile').val();
            var phone = $('#candidateLoginPassword').val();
            console.log("phone: " + phone);
            $.ajax({
                type: "POST",
                url: "/loginSubmit",
                data: $("#form_login_candidate").serialize(),
                dataType: "json",
                success: processData
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit
}); // end of function
