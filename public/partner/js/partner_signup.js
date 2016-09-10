/**
 * Created by adarsh on 10/9/16.
 */
// form_candidate ajax script
$(function() {
    $("#partner_signup_form").submit(function(eventObj) {
        eventObj.preventDefault();
        var statusCheck = 1;
        var partnerName = $('#partnerName').val();
        var phone = $('#partnerMobile').val();
        var checkPartnerName = validateName(partnerName);
        var res = validateMobile(phone);

        //checking first name
        switch(checkPartnerName){
            case 0: alert("Your name contains number. Please Enter a valid Name"); statusCheck=0; break;
            case 2: alert("Your name cannot be blank spaces. Enter a valid name"); statusCheck=0; break;
            case 3: alert("Your name contains special symbols. Enter a valid name"); statusCheck=0; break;
            case 4: alert("Please enter your name"); statusCheck=0; break;
        }

        if(res == 0){
            alert("Enter a valid mobile number");
            statusCheck=0;
        } else if(res == 1){
            alert("Enter 10 digit mobile number");
            statusCheck=0;
        }

        if(statusCheck == 1){
            candidateMobile = phone;
            $("#registerBtnSubmit").addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Please Wait");
            $("#registerBtnSubmit").addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Please Wait");
            document.getElementById("registerBtnSubmit").disabled = true;
        }
        alert(statusCheck);
    }); // end of submit
}); // end of function