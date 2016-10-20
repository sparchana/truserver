/**
 * Created by batcoder1 on 6/7/16.
 */

var companyLocality = [];
var localityArray = [];

var totalAmount = 0;

var interviewCredits = 0;
var contactCredits = 0;

var candidateCreditTypeStatus = 1;
var interviewCreditTypeStatus = 1;

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
    if($('input:radio[name="candidateCreditType"]:checked').val() == 1){
        if(validateContactUnlockCreditValues() == 1){
            candidateCreditTypeStatus = 1;

            if(parseInt($("#candidateContactCredits").val()) < -(parseInt($("#recruiterContactCredits").val()))){
                candidateCreditTypeStatus = 0;
                notifyError("Contact credits should be greater")
            } else{
                contactCredits = parseInt($("#candidateContactCredits").val());
                $("#addCreditInfoDiv").show();
                $("#contactUnlockCreditInfo").html("Adding " + contactCredits + " contact unlock credits ");
            }
        }
    }
    if($('input:radio[name="interviewCreditType"]:checked').val() == 1){
        if(validateInterviewUnlockCreditValues() == 1){
            interviewCreditTypeStatus = 1;

            if(parseInt($("#interviewCredits").val()) < -(parseInt($("#recruiterInterviewCredits").val()))){
                interviewCreditTypeStatus = 0;
                notifyError("Interview credits should be greater");
            } else{
                interviewCredits = parseInt($("#interviewCredits").val());
                $("#addCreditInfoDiv").show();
                $("#interviewUnlockCreditInfo").html("Adding " + interviewCredits + " interview unlock credits ");
            }
        }
    }

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
        contactCredits: contactCredits,
        interviewCredits: interviewCredits
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
    if(returnedData.status == 4){
        alert("Recruiter Update Successful!");
        window.close();
    } else{
        alert("Something went wrong, Please try again");
    }
}

function processDataForCompanyInfo(returnedData) {
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
        $("#recruiterContactCredits").val(0);
        $("#recruiterInterviewCredits").val(0);
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

function closeCreditModal() {
    $("#creditModal").modal("hide");
}

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