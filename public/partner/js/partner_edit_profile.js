/**
 * Created by adarsh on 12/9/16.
 */

var organizationLocation = [];

$(document).ready(function(){
    checkPartnerLogin();
    try {
        $.ajax({
            type: "GET",
            url: "/getPartnerProfileInfo",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataPartnerProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});


function processDataPartnerProfile(returnedData) {
    if(returnedData != null){
        //name
        if(returnedData.partnerFirstName != null){
            $("#partnerFirstName").val(returnedData.partnerFirstName);
            if(returnedData.partnerLastName != null){
                $("#partnerLastName").val(returnedData.partnerLastName);
            }
        }
        //mobile
        if(returnedData.partnerMobile != null){
            $("#partnerMobile").val(returnedData.partnerMobile);
        }

        //email
        if(returnedData.partnerEmail != null){
            $("#partnerEmail").val(returnedData.partnerEmail);
        }

        //partner company name
        if(returnedData.partnerCompany != null){
            $("#organizationName").val(returnedData.partnerCompany);
        }

        //partner company type
        if(returnedData.partnerType != null){
            $("#partnerType").val(returnedData.partnerType.partnerTypeId);
        }

        //partner company location
        if(returnedData.locality != null){
            var item = {};
            item ["id"] = returnedData.locality.localityId;
            item ["name"] = returnedData.locality.localityName;
            organizationLocation.push(item);
        }

        if($("#organizationLocation").val() == ""){
            $("#organizationLocation").tokenInput(getLocality(), {
                theme: "facebook",
                hintText: "Start typing jobs (eg. Cook, Delivery boy..)",
                placeholder: "Organization Location?",
                minChars: 0,
                tokenLimit: 1,
                prePopulate: organizationLocation,
                preventDuplicates: true
            });
        }
    }
}

// edit partner profile ajax script
$(function() {
    $("#partnerBasicProfile").submit(function(eventObj) {
        eventObj.preventDefault();
        //entered values
        var statusCheck = 1;
        var firstName = $("#partnerFirstName").val();
        var lastName = $("#partnerLastName").val();
        var email = $("#partnerEmail").val();
        var organizationType = $("#partnerType").val();
        var organizationName = $("#organizationName").val();
        var organizationLocality = $("#organizationLocation").val();

        var checkPartnerFirstName = validateName(firstName);
        var checkPartnerLastName = validateName(lastName);

        //checking first name
        switch(checkPartnerFirstName){
            case 0: notifyError("Your first name contains number. Please Enter a valid first name"); statusCheck=0; break;
            case 2: notifyError("Your first name cannot be blank spaces. Enter a valid first name"); statusCheck=0; break;
            case 3: notifyError("Your first name contains special symbols. Enter a valid first name"); statusCheck=0; break;
            case 4: notifyError("Please enter your first name"); statusCheck=0; break;
        }
        //checking last name
        if(lastName != ""){
            switch(checkPartnerLastName){
                case 0: notifyError("Your last name contains number. Please Enter a valid last name"); statusCheck=0; break;
                case 2: notifyError("Your last name cannot be blank spaces. Enter a valid last name"); statusCheck=0; break;
                case 3: notifyError("Your last name contains special symbols. Enter a valid last name"); statusCheck=0; break;
                case 4: notifyError("Please enter your last name"); statusCheck=0; break;
            }
        } else{
            lastName = null;
        }

        if(organizationType == null || organizationType == -1){
            notifyError("Please select organization type");
            statusCheck = 0;
        } else if(!validateEmail(email)){
            notifyError("Enter a valid email");
            statusCheck = 0;
        } else if(organizationLocality == ""){
            notifyError("Enter Organization Locality");
            statusCheck = 0;
        }
        if(statusCheck == 1){
            $("#registerBtnSubmit").addClass("btn-primary").removeClass("appliedBtn").prop('disabled', true).html("Save");
            try {
                var s = {
                    partnerName: firstName,
                    partnerLastName: lastName,
                    partnerLocality: organizationLocality,
                    partnerType: organizationType,
                    partnerEmail: email,
                    partnerOrganizationName : organizationName
                };
                $.ajax({
                    type: "POST",
                    url: "/partnerUpdateBasicProfile",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(s),
                    success: processDataUpdateProfile
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }); // end of submit
}); // end of function

function notifyError(msg){
    $.notify({
        message: msg,
        animate: {
            enter: 'animated lightSpeedIn',
            exit: 'animated lightSpeedOut'
        }
    },{
        type: 'danger'
    });
}

