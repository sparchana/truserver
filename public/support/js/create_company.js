/**
 * Created by batcoder1 on 12/7/16.
 */

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
    $("#companyLocality").tokenInput(getLocality(), {
        theme: "facebook",
        hintText: "Start typing jobs (eg. Cook, Delivery boy..)",
        minChars: 0,
        tokenLimit: 1,
        preventDuplicates: true
    });
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
});

function processDataGetCreditCategory(returnedData) {
    $("#candidateContactCreditUnitPrice").val(returnedData[0].recruiterCreditUnitPrice);
    $("#interviewCreditUnitPrice").val(returnedData[1].recruiterCreditUnitPrice);
}