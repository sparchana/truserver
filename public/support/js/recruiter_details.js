/**
 * Created by batcoder1 on 6/7/16.
 */

var companyLocality = [];
var localityArray = [];

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
        recruiterCompany: $("#recruiterCompany").val()
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
}