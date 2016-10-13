/**
 * Created by batcoder1 on 6/7/16.
 */

var companyLocality = [];
var localityArray = [];

var totalAmount = 0;
var candidateContactCreditAmount = 0;
var candidateContactCreditUnitPrice = 0;
var interviewCreditAmount = 0;
var interviewCreditUnitPrice = 0;

var paymentMode = 0;

function getLocality() {
    return localityArray;
}

function processDataGetCompanies(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select a company");
    $('#recruiterCompany').append(defaultOption);
    returnedData.forEach(function (company) {
        var id = company.companyId;
        var name = company.companyName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#recruiterCompany').append(option);
    });
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
            $("#addCreditInfoDiv").show();
            $("#contactUnlockCreditInfo").html("₹" + candidateContactCreditAmount + " @ ₹" + candidateContactCreditUnitPrice + " unit price per credit");
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
            $("#addCreditInfoDiv").show();
            $("#interviewUnlockCreditInfo").html("₹" + interviewCreditAmount + " @ ₹" + interviewCreditUnitPrice + " unit price per credit");
        }
    }
    paymentMode = $("#creditMode").val();
    if(interviewCreditTypeStatus == 1 && candidateCreditTypeStatus == 1){
        $("#creditModal").modal("hide");
    }
}

function processDataGetCreditCategory(returnedData) {
    $("#candidateContactCreditUnitPrice").val(returnedData[0].recruiterCreditUnitPrice);
    $("#interviewCreditUnitPrice").val(returnedData[1].recruiterCreditUnitPrice);
}

$(function(){
    var pathname = window.location.pathname; // Returns path only
    var recruiterIdUrl = pathname.split('/');
    var recruiterId = recruiterIdUrl[(recruiterIdUrl.length)-1];
    
    try {
        $.ajax({
            type: "GET",
            url: "/getAllCompany",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetCompanies
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    try {
        $.ajax({
            type: "POST",
            url: "/getAllCreditCategory",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetCreditCategory
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getRecruiterInfo/" + recruiterId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForCompanyInfo
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    
});

function saveRecruiter() {
    var d = {
        recruiterName: $("#recruiterName").val(),
        recruiterMobile: $("#recruiterMobile").val(),
        recruiterLandline: $("#recruiterLandline").val(),
        recruiterEmail: $("#recruiterEmail").val(),
        recruiterCompany: $("#recruiterCompany").val(),
        recruiterInterviewCreditAmount: interviewCreditAmount,
        recruiterContactCreditAmount: candidateContactCreditAmount,
        recruiterInterviewCreditUnitPrice: interviewCreditUnitPrice,
        recruiterContactCreditUnitPrice: candidateContactCreditUnitPrice,
        recruiterCreditMode: paymentMode
    };
         
    try {
        $.ajax({
            type: "POST",
            url: "/addRecruiter",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataAddRecruiter
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataAddRecruiter(returnedData) {
    console.log(returnedData);
    if(returnedData.status == 4){
        alert("Recruiter Update Successful!");
        window.close();
    } else{
        alert("Something went wrong, Please try again");
    }
}

function processDataForCompanyInfo(returnedData) {
    console.log(returnedData);
    $("#recruiterName").val(returnedData.recruiterProfileName);
    $("#recruiterMobile").val(returnedData.recruiterProfileMobile);
    if(returnedData.recruiterProfileLandline != null ){
        $("#recruiterLandline").val(returnedData.recruiterProfileLandline);
    }

    if(returnedData.recruiterProfileEmail != null ){
        $("#recruiterEmail").val(returnedData.recruiterProfileEmail);
    }

    if(returnedData.company != null ){
        $("#recruiterCompany").val(returnedData.company.companyId);
    }

    if(returnedData.recruiterCreditHistoryList != null){
        var creditHistoryList = returnedData.recruiterCreditHistoryList;
        creditHistoryList.reverse();
        var contactCreditCount = 0;
        var interviewCreditCount = 0;
        creditHistoryList.forEach(function (creditHistory){
            if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 1){
                if(contactCreditCount == 0){
                    if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 1){
                        $("#recruiterContactCredits").val(creditHistory.recruiterCreditsAvailable);
                        contactCreditCount = 1;
                    }
                }
            } else{
                if(interviewCreditCount == 0){
                    console.log("i");
                    if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 2){
                        $("#recruiterInterviewCredits").val(creditHistory.recruiterCreditsAvailable);
                        interviewCreditCount = 1;
                    }
                }
            }
            if(contactCreditCount > 0 && interviewCreditCount > 0){
                return false;
            }
        });
    }
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}