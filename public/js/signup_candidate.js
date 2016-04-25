/**
 * Created by batcoder1 on 25/4/16.
 */

function processData(returnedData) {
    console.log("returedData :" + returnedData.status);
    if(returnedData.status == 1) {
        $('#candidateName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
    }

    else if(returnedData.status == 3){
        $('#candidateName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
    }
    else {
        $('#candidateName').val('');
        $('#candidateMobile').val('');
        $('#candidateEmail').val('');
    }
}

// form_candidate ajax script
$(function() {
    $("#form_signup_candidate").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var name  = $('#candidateName').val();
            var phone = $('#candidateMobile').val();
            var email = $('#candidateEmail').val();
            console.log("phone: " + phone);
            $.ajax({
                type: "POST",
                url: "/signUpSubmit",
                data: $("#form_signup_candidate").serialize(),
                dataType: "json",
                success: processData
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

    }); // end of submit
}); // end of function

