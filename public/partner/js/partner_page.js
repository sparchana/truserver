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

$(window).load(function() {
    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").delay(500).fadeOut("slow");
});

$(document).ready(function() {
    //getting all localities
    try {
        $.ajax({
            type: "POST",
            url: "/getAllLocality",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckLocality
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }


    //getting all partner types
    try {
        $.ajax({
            type: "POST",
            url: "/getAllPartnerType",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckPartnerType
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
    $("#partnerLocality").tokenInput(getLocality(), {
        theme: "facebook",
        placeholder: "Your Locality",
        minChars: 3,
        tokenLimit: 1,
        hintText: "Start Typing Area (eg: Whitefield, Agara, etc..)",
        preventDuplicates: true
    });
}

function processDataCheckPartnerType(returnedData) {
    var defaultOption=$('<option value="-1"></option>').text("Select Organization Type");
    $('#partnerType').append(defaultOption);
    returnedData.forEach(function(partnerType) {
        var id = partnerType.partnerTypeId;
        var name = partnerType.partnerTypeName;
        var option=$('<option value=' + id + '></option>').text(name);
        $('#partnerType').append(option);
    });
}