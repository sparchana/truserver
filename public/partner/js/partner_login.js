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
    console.log(returnedData);
    if(returnedData.status == 1) {
        // Store
        localStorage.setItem("mobile", "+91" + candidateMobile);
        localStorage.setItem("name", returnedData.candidateFirstName);

        window.location = "/partner/home";
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
    $("#form_login_partner").submit(function(eventObj) {
        eventObj.preventDefault();
        var phone  = $('#partnerLoginMobile').val();
        var password = $('#partnerLoginPassword').val();
        console.log(phone + " " + password);
        if(phone == null || phone == ""){
            alert("Enter your Phone Number");
        } else if(password == null || password == ""){
            alert("Enter your Password");
        }
        else{
            try {

                candidateMobile = phone;
                console.log("phone: " + phone);
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

