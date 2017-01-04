/**
 * Created by batcoder1 on 6/7/16.
 */

var companyLocality = [];
var localityArray = [];

function getLocality() {
    return localityArray;
}

function processDataCheckLocality(returnedData) {
    if (returnedData != null) {
        returnedData.forEach(function (locality) {
            var id = locality.localityId;
            var name = locality.localityName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            localityArray.push(item);
        });
    }
}

function processDataCheckCompanyStatus(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select Company status");
    $('#companyStatus').append(defaultOption);
    returnedData.forEach(function (status) {
        var id = status.companyStatusId;
        var name = status.companyStatusName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#companyStatus').append(option);
    });
}

function processDataCheckCompanyType(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select Company type");
    $('#companyType').append(defaultOption);
    returnedData.forEach(function (type) {
        var id = type.companyTypeId;
        var name = type.companyTypeName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#companyType').append(option);
    });
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
    var companyIdUrl = pathname.split('/');
    var companyId = companyIdUrl[(companyIdUrl.length)-1];

    /* ajax commands to fetch all localities and jobs*/
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
            url: "/getAllCompanyType",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckCompanyType
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "POST",
            url: "/getAllCompanyStatus",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckCompanyStatus
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getCompanyRecruiters/" + recruiterId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForCompanyInfo
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getCompanyInfo/" + companyId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForCompanyInfo
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataForCompanyInfo(returnedData) {
    $("#companyId").val(returnedData.companyId);
    $("#companyName").val(returnedData.companyName);
    if(returnedData.companyLocality != null){
        var item = {};
        item ["id"] = returnedData.companyLocality.localityId;
        item ["name"] = returnedData.companyLocality.localityName;
        companyLocality.push(item);
    }
    if($("#companyLocality").val() == ""){
        $("#companyLocality").tokenInput(getLocality(), {
            theme: "facebook",
            hintText: "Start typing Locality (eg. Marathahalli, Whitefield..)",
            minChars: 0,
            tokenLimit: 1,
            prePopulate: companyLocality,
            preventDuplicates: true
        });
    }
    if(returnedData.companyAddress != null ){
        $("#companyAddress").val(returnedData.companyAddress);
    }

    if(returnedData.companyPinCode != null ){
        $("#companyPinCode").val(returnedData.companyPinCode);
    }

    if(returnedData.companyWebsite != null ){
        $("#companyWebsite").val(returnedData.companyWebsite);
    }

    if(returnedData.companyLogo != null ){
        $("#companyOldLogo").val(returnedData.companyLogo);
        $('#companyLogoOld').attr("src",returnedData.companyLogo);
    }

    if(returnedData.companyDescription != null ){
        $("#companyDescription").val(returnedData.companyDescription);
    }
    
    if(returnedData.companyEmployeeCount != null ){
        $("#companyEmployeeCount").val(returnedData.companyEmployeeCount);
    }

    if(returnedData.compType != null ){
        $("#companyType").val(returnedData.compType.companyTypeId);
    }

    if(returnedData.compStatus != null ){
        $("#companyStatus").val(returnedData.compStatus.companyStatusId);
    }

    if(returnedData.source == null || returnedData.source == 0) {
        var source = returnedData.source;
        if(returnedData.source == null) {
            source = 0; // internal jobs
        }
        $("#companySource").val(source);
    }
}