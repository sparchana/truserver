/**
 * Created by adarsh on 10/9/16.
 */

var localityArray = [];

function getLocality(){
    return localityArray;
}

function openPartnerLogin() {
    $('#partnerLoginMobile').val("");
    $('#partnerLoginPassword').val("");
    $('#form_login_partner').show();
    $('#form_forgot_password').hide();
    $('#partnerLoginModal').modal('show');
    $("#signInPopup").html("Sign In to Trujobs Partner");
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_forgot_password').hide();
    $('#errorMsgReset').hide();
    $('#form_password_reset_otp').hide();
    $('#form_password_reset_new').hide();
}

function openSignUp() {
    $('#partnerLoginModal').modal('hide');
}

function resetPassword() {
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_login_partner').hide();
    $('#form_forgot_password').show();
}

$(document).ready(function() {
    try {
        $.ajax({
            type: "POST",
            url: "/getAllLocality",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckLocality
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataCheckLocality(returnedData) {
    returnedData.forEach(function(locality)
    {
        var id = locality.localityId;
        var name = locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        localityArray.push(item);
    });
}