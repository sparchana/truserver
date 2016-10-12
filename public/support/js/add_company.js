/**
 * Created by batcoder1 on 22/6/16.
 */

var f;
var companyId = 0;
var companyStatus = -1;
var recruiterStatus = -1;

var totalAmount = 0;
var candidateContactCreditAmount = 0;
var candidateContactCreditUnitPrice = 0;
var interviewCreditAmount = 0;
var interviewCreditUnitPrice = 0;

var paymentMode = 0;

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
    var x = document.getElementById("companyLogo");
    if ('files' in x) {
        if (x.files.length == 0) {
        } else {
            for (var i = 0; i < x.files.length; i++) {
                var file = x.files[i];

                var data = new FormData();
                data.append('picture', file);
                $.ajax({
                    type: "POST",
                    url: "/addCompanyLogo",
                    async: true,
                    data: data,
                    cache: false,
                    contentType: false,
                    processData: false
                });
            }
        }
    }
}

function processDataAddCompany(returnedData) {
    console.log(returnedData);
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
    var candidateCreditTypeStatus = 1;
    var interviewCreditTypeStatus = 1;
    if($('input:radio[name="candidateCreditType"]:checked').val() == 1){
        var statusCheck = 1;
        //the recruiter has paid for candidate unlock credits
        if($("#candidateContactCreditAmount").val() == ""){
            statusCheck = 0;
            candidateCreditTypeStatus = 0;
            notifyError("Please enter the amount paid by the candidate for candidate contact unlock credits!");
        } else if($("#candidateContactCreditUnitPrice").val() == ""){
            statusCheck = 0;
            candidateCreditTypeStatus = 0;
            notifyError("Please enter the candidate contact unlock credit unit price!");
        }
        if(statusCheck == 1){
            candidateCreditTypeStatus = 1;
            candidateContactCreditAmount = parseInt($("#candidateContactCreditAmount").val());
            totalAmount += candidateContactCreditAmount;
            candidateContactCreditUnitPrice = parseInt($("#candidateContactCreditUnitPrice").val());
        }
    }
    if($('input:radio[name="interviewCreditType"]:checked').val() == 1){
        statusCheck = 1;
        //the recruiter has paid for interview unlock credits
        if($("#interviewCreditAmount").val() == ""){
            statusCheck = 0;
            interviewCreditTypeStatus = 0;
            notifyError("Please enter the amount paid by the candidate for interview unlock credits!");
        } else if($("#interviewCreditUnitPrice").val() == ""){
            statusCheck = 0;
            interviewCreditTypeStatus = 0;
            notifyError("Please enter the interview unlock credit unit price!");
        }
        if(statusCheck == 1){
            interviewCreditTypeStatus = 1;
            interviewCreditAmount = parseInt($("#interviewCreditAmount").val());
            totalAmount += interviewCreditAmount;
            interviewCreditUnitPrice = parseInt($("#interviewCreditUnitPrice").val());
        }
    }
    paymentMode = $("#creditMode").val();
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
            logo = "https://s3.amazonaws.com/trujobs.in/companyLogos/" + f.name;
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
                    logo = "https://s3.amazonaws.com/trujobs.in/companyLogos/" + f.name;
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
                recruiterInterviewCreditAmount: interviewCreditAmount,
                recruiterContactCreditAmount: candidateContactCreditAmount,
                recruiterInterviewCreditUnitPrice: interviewCreditUnitPrice,
                recruiterContactCreditUnitPrice: candidateContactCreditUnitPrice,
                recruiterCreditMode: paymentMode
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
