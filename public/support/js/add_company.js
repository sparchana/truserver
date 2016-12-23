/**
 * Created by batcoder1 on 22/6/16.
 */

var f;
var companyId = 0;
var companyStatus = -1;
var recruiterStatus = -1;

var contactCredits = 0;
var interviewCredits = 0;

var candidateCreditTypeStatus = 1;
var interviewCreditTypeStatus = 1;

var logoTitle = "";

$('input[type=file]').change(function () {
    f = this.files[0];
});

function readURL(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            $('#companyLogoOld')
                .attr('src', e.target.result);
        };

        reader.readAsDataURL(input.files[0]);
    }
}

function uploadLogo(){
    var file = $('#companyLogo')[0].files[0];
    var formData = new FormData();
    formData.append('file', file, logoTitle);
    console.log(formData);

    $.ajax({
        type: "POST",
        url: "/addCompanyLogo",
        async: true,
        data: formData,
        cache: false,
        contentType: false,
        processData: false
    });
}

function closeCreditModal() {
    $("#creditModal").modal("hide");
}

function processDataAddCompany(returnedData) {
    companyId = returnedData.companyId;
    companyStatus = returnedData.status;
}

$(document).ready(function() {
    $('input[type=radio][name=interviewCreditType]').change(function() {
        if (this.value == 1) {
            $("#interviewCreditSection").show();
        } else{
            $("#interviewCreditSection").hide();
        }
    });
    $('input[type=radio][name=candidateCreditType]').change(function() {
        if (this.value == 1) {
            $("#candidateCreditSection").show();
        } else{
            $("#candidateCreditSection").hide();
        }
    });
});

function computeCreditValue() {
    if($('input:radio[name="candidateCreditType"]:checked').val() == 1){
        if(validateContactUnlockCreditValues() == 1){
            candidateCreditTypeStatus = 1;
            contactCredits = parseInt($("#candidateContactCredits").val());
            $("#addCreditInfoDiv").show();
            $("#contactUnlockCreditInfo").html("Adding " + contactCredits + " contact unlock credits ");
        }
    }
    if($('input:radio[name="interviewCreditType"]:checked').val() == 1){
        if(validateInterviewUnlockCreditValues() == 1){
            interviewCreditTypeStatus = 1;
            interviewCredits = parseInt($("#interviewCredits").val());
            $("#addCreditInfoDiv").show();
            $("#interviewUnlockCreditInfo").html("Adding " + interviewCredits + " interview unlock credits ");
        }
    }

    if(interviewCreditTypeStatus == 1 && candidateCreditTypeStatus == 1){
        $("#creditModal").modal("hide");
    }
}

function updateForm() {
    var status = 1;
    var logo;
    if(document.getElementById("companyLogo").value != "") {
        if((f.type).substring(0,1) != "i"){
            alert("Please select a valid image for logo");
            status=0;
        }
        else{
            var companyName = $("#companyName").val();
            var combinedName = companyName.split(' ').join('_');
            var ext = "." + f.type.substring(6, f.type.length);
            logoTitle = "TJ_" + combinedName + ext;

            console.log(companyName + combinedName + ext + logoTitle);
            logo = "https://s3.amazonaws.com/trujobs.in/companyLogos/" + logoTitle;
            status = 1;
        }
    } else {
        status = 1;
        logo = $("#companyOldLogo").val();
    }

    if(status == 1){
        var d = {
            companyId: $("#companyId").val(),
            companyName: $("#companyName").val(),
            companyEmployeeCount: $("#companyEmployeeCount").val(),
            companyWebsite: $("#companyWebsite").val(),
            companyDescription: $("#companyDescription").val(),
            companyAddress: $("#companyAddress").val(),
            companyPinCode: $("#companyPinCode").val(),
            companyLogo: logo,
            companyLocality: parseInt($("#companyLocality").val()),
            companyType: $("#companyType").val(),
            companyStatus: $("#companyStatus").val()
        };

        try {
            $.ajax({
                type: "POST",
                url: "/addCompany",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataUpdateCompany
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
        if(document.getElementById("companyLogo").value != "") {
            uploadLogo();
        }
    }
}

function processDataAddRecruiter(returnedData) {
    recruiterStatus = returnedData.status;
}

function processDataUpdateCompany(returnedData) {
    if(returnedData.status == 2){
        alert("Company successfully updated");
        window.close();
    } else{
        alert("Something went wrong! Please try again later");
    }
}

// company_form ajax script
function saveForm(){
    var status = 1;
    if($("#recruiterCompany").val() == "" && $("#companySection").is(':visible') == true) {
        // add a new company
        if($("#companyName").val() == ""){
            alert("Please Enter company Name");
            status=0;
        } else if(document.getElementById("companyLogo").value != "" && (f.type).substring(0,1) != "i"){
            alert("Please select a valid image for logo");
            status=0;
        }
        if(status == 1){
            var d;
            var logo;

            if ($("#companyLogo").val() != "") {
                if (($("#companyLogo").val()).substring(0, 4) == "http") {
                    logo = $("#companyLogo").val();
                } else {
                    var companyName = $("#companyName").val();
                    var combinedName = companyName.split(' ').join('_');
                    var ext = "." + f.type.substring(6, f.type.length);
                    logoTitle = "TJ_" + combinedName + ext;

                    logo = "https://s3.amazonaws.com/trujobs.in/companyLogos/" + logoTitle;
                }
            }
            else {
                logo = "https://s3.amazonaws.com/trujobs.in/companyLogos/default_company_logo.png";
            }

            d = {
                companyId: $("#companyId").val(),
                companyName: $("#companyName").val(),
                companyEmployeeCount: $("#companyEmployeeCount").val(),
                companyWebsite: $("#companyWebsite").val(),
                companyDescription: $("#companyDescription").val(),
                companyAddress: $("#companyAddress").val(),
                companyPinCode: $("#companyPinCode").val(),
                companyLogo: logo,
                companyLocality: parseInt($("#companyLocality").val()),
                companyType: $("#companyType").val(),
                companyStatus: $("#companyStatus").val()
            };
            try {
                $.ajax({
                    type: "POST",
                    url: "/addCompany",
                    async: false,
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataAddCompany
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }

            if(companyStatus == 4){
                companyStatus = -1;
                $("#recruiterCompany").prop('disabled', false);
                $("#recruiterCompany").val(companyId);
                $("#companySection").hide();
            } else {
                uploadLogo();
            }
        }
    }

    status = 1;
    var recruiterName = validateName($("#recruiterName").val());
    var recruiterMobile = validateMobile($("#recruiterMobile").val());

    //checking first name
    switch(recruiterName){
        case 0: alert("Recruiter's name contains number. Please Enter a valid name"); status=0; break;
        case 2: alert("Recruiter's name cannot be blank spaces. Enter a valid name"); status=0; break;
        case 3: alert("Recruiter's name contains special symbols. Enter a valid name"); status=0; break;
        case 4: alert("Please enter recruiter's name"); status=0; break;
    }
    if(recruiterMobile == 0){
        alert("Enter a valid mobile number");
        status=0;
    } else if(recruiterMobile == 1){
        alert("Enter 10 digit mobile number");
        status=0;
    } else if(recruiterMobile == "") {
        alert("Please Enter recruiter Contact");
        status=0;
    }

    if (!validateEmail($("#recruiterEmail").val())) {
        alert("Please Enter Valid Email Address");
        status=0;
    }
    
    if(status == 1 && companyStatus != 4){
        if($("#recruiterCompany").val() != ""){
            companyId = $("#recruiterCompany").val();
        }
        try{
            var rec = {
                recruiterName: $("#recruiterName").val(),
                recruiterMobile: $("#recruiterMobile").val(),
                recruiterLandline: $("#recruiterLandline").val(),
                recruiterEmail: $("#recruiterEmail").val(),
                recruiterCompany: companyId,
                contactCredits: contactCredits,
                interviewCredits: interviewCredits
            };
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        $.ajax({
            type: "POST",
            url: "/addRecruiter",
            async: false,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(rec),
            success: processDataAddRecruiter
        });
    }

    if(companyStatus == 1 && recruiterStatus == 1){
        alert("New Company and New Recruiter Created successfully created");
        window.close();
    } else if(companyStatus == 1 && recruiterStatus == 4){
        alert("Company Created and Existing Recruiter Updated");
        window.close();
    } else if(companyStatus == -1 && recruiterStatus == 4){
        alert("Existing Recruiter Updated");
        window.close();
    } else if(companyStatus == -1 && recruiterStatus == 1){
        alert("New Recruiter Created");
        window.close();
    }
} // end of submit

function notifyError(msg, type){
    $.notify({
        message: msg,
        animate: {
            enter: 'animated lightSpeedIn',
            exit: 'animated lightSpeedOut'
        }
    },{
        type: type
    });
}

function validateContactUnlockCreditValues(){
    var statusCheck = 1;
    if($("#candidateContactCreditAmount").val() == ""){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Please enter the amount paid by the candidate for candidate contact unlock credits!");
    } else if($("#candidateContactCreditUnitPrice").val() == ""){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Please enter the candidate contact unlock credit unit price!");
    } else if(!isValidSalary($("#candidateContactCreditAmount").val())){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Please enter a valid contact unlock credit amount!");
    } else if(!isValidSalary($("#candidateContactCreditUnitPrice").val())){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Please enter a valid contact unlock credit unit price!");
    } else if(parseInt($("#candidateContactCreditAmount").val()) < 0){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Contact unlock amount price cannot be negative!");
    } else if(parseInt($("#candidateContactCreditUnitPrice").val()) < 0){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Contact unlock unit price cannot be negative!");
    } else if(parseInt($("#candidateContactCreditUnitPrice").val()) > parseInt($("#candidateContactCreditAmount").val())){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Contact unlock credit amount should be greater than its credit unit price!");
    }
    return statusCheck;
}

function validateInterviewUnlockCreditValues(){
    var statusCheck = 1;
    if($("#interviewCreditAmount").val() == ""){
        statusCheck = 0;
        interviewCreditTypeStatus = 0;
        notifyError("Please enter the amount paid by the candidate for interview unlock credits!");
    } else if($("#interviewCreditUnitPrice").val() == ""){
        statusCheck = 0;
        interviewCreditTypeStatus = 0;
        notifyError("Please enter the interview unlock credit unit price!");
    } else if(!isValidSalary($("#interviewCreditAmount").val())){
        statusCheck = 0;
        interviewCreditTypeStatus = 0;
        notifyError("Please enter a valid interview unlock credit amount!");
    } else if(!isValidSalary($("#interviewCreditUnitPrice").val())){
        statusCheck = 0;
        interviewCreditTypeStatus = 0;
        notifyError("Please enter a valid interview unlock credit unit price!");
    } else if(parseInt($("#interviewCreditAmount").val()) < 0){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Interview unlock amount price cannot be negative!");
    } else if(parseInt($("#interviewCreditUnitPrice").val()) < 0){
        statusCheck = 0;
        candidateCreditTypeStatus = 0;
        notifyError("Interview unlock unit price cannot be negative!");
    } else if(parseInt($("#interviewCreditUnitPrice").val()) > parseInt($("#interviewCreditAmount").val())){
        statusCheck = 0;
        interviewCreditTypeStatus = 0;
        notifyError("Interview unlock credit amount should be greater than its credit unit price!");
    }
    return statusCheck;
}