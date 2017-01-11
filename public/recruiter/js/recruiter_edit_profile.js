/**
 * Created by dodo on 12/10/16.
 */

var f;
var companyId;
var companyArray = [];

var logoTitle = "";
var returnedCompanyName;

$(document).scroll(function(){
    if ($(this).scrollTop() > 20) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});

$(document).ready(function(){
    $(".button-collapse").sideNav();
    $(".dropdown-button").dropdown();
    checkRecruiterLogin();
    $(".profileNav").addClass("active");
    $(".profileNavMobile").addClass("active");

    try {
        $.ajax({
            type: "GET",
            url: "/getAllCompany",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCompany
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

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
            type: "POST",
            url: "/getAllCompanyType",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCompanyType
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getRecruiterProfileInfo",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

$('input[type=file]').change(function () {
    f = this.files[0];
});

function uploadLogo(companyId){
    var file = $('#companyLogo')[0].files[0];
    var formData = new FormData();

    var combinedName = returnedCompanyName.split(' ').join('_');
    var ext = "." + f.type.substring(6, f.type.length);
    logoTitle = "TJ_" + companyId + "_" + combinedName + ext;

    formData.append('file', file, logoTitle);

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

function processDataCheckLocality(returnedData) {
    returnedData.forEach(function(locality) {
        var id = locality.localityId;
        var name = locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#rec_company_locality').append(option);
    });
}

function processDataCompanyType(returnedData) {
    returnedData.forEach(function(companyType) {
        var id = companyType.companyTypeId;
        var name = companyType.companyTypeName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#rec_company_type').append(option);
    });
}

function logoutRecruiter() {
    try {
        $.ajax({
            type: "GET",
            url: "/logoutRecruiter",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataLogoutRecruiter
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataLogoutRecruiter() {
    window.location = "/recruiter";
}


function checkRecruiterLogin() {
    try {
        $.ajax({
            type: "GET",
            url: "/checkRecruiterSession",
            data: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterSession
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataRecruiterSession(returnedData) {
    if(returnedData == 0){
        logoutRecruiter();
    }
}

function processDataRecruiterProfile(returnedData) {
    if (returnedData == '0') {
        logoutRecruiter();
    } else {
        $("#rec_name").val(returnedData.recruiterProfileName);
        $("#rec_mobile").val(returnedData.recruiterProfileMobile);

        if(returnedData.recruiterProfileEmail != null){
            $("#rec_email").val(returnedData.recruiterProfileEmail);
        }

        if(returnedData.recruiterProfileLandline != null && returnedData.recruiterProfileLandline != "0"){
            $("#rec_landline").val(returnedData.recruiterProfileLandline);
        }

        if(returnedData.recruiterLinkedinProfile != null){
            $("#rec_linkedin").val(returnedData.recruiterLinkedinProfile);
        }

        if(returnedData.recruiterAlternateMobile != null){
            $("#rec_alternate_mobile").val(returnedData.recruiterAlternateMobile);
        }

        if(returnedData.recruiterAlternateMobile != null){
            $("#rec_alternate_mobile").val(returnedData.recruiterAlternateMobile);
        }
        if(returnedData.company != null){
            companyId = returnedData.company.companyId;
            returnedCompanyName = returnedData.company.companyName;

            populateCompanyValues(returnedData.company);
        }
    }
}

function populateCompanyValues(company) {
    companyId = company.companyId;
    returnedCompanyName = company.companyName;

    $('#rec_company_name').tokenize().tokenAdd(company.companyId, company.companyName);

    if(company.companyLocality != null){
        $('#rec_company_locality').tokenize().tokenAdd(company.companyLocality.localityId, company.companyLocality.localityName);
    }
    if(company.compType != null){
        $('#rec_company_type').tokenize().tokenAdd(company.compType.companyTypeId, company.compType.companyTypeName);
    }

    if(company.companyPinCode != null){
        $("#rec_company_pincode").val(company.companyPinCode);
    }
    if(company.companyWebsite != null){
        $("#rec_company_website").val(company.companyWebsite);
    }
    if(company.companyDescription != null){
        $("#rec_company_desc").val(company.companyDescription);
    }
    if(company.companyAddress != null){
        $("#rec_company_address").val(company.companyAddress);
    }
    if(company.companyEmployeeCount != null){
        $("#rec_company_employees").val(company.companyEmployeeCount);
    }
    if(company.companyLogo != null){
        $('#rec_company_logo_old')
            .attr('src', company.companyLogo);
        $("#rec_company_old_logo").val(company.companyLogo);
    }


}

function validateCompanyTypeVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#rec_company_type').tokenize().tokenRemove(val);
        notifyError("Please select a valid company type from the dropdown list");
    }
}

function validateCompanyLocationVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#rec_company_locality').tokenize().tokenRemove(val);
        notifyError("Please select a valid location from the dropdown list");
    }
}

function saveForm() {
    var recruiterStatus = 1;
    var companyStatus = 1;
    var recruiterName = validateName($("#rec_name").val());

    var recruiterLandline = $("#rec_landline").val();
    var recruiterPincode = $("#rec_company_pincode").val();

    //checking first name
    switch(recruiterName){
        case 0: alert("Recruiter's name contains number. Please Enter a valid name"); recruiterStatus=0; break;
        case 2: alert("Recruiter's name cannot be blank spaces. Enter a valid name"); recruiterStatus=0; break;
        case 3: alert("Recruiter's name contains special symbols. Enter a valid name"); recruiterStatus=0; break;
        case 4: alert("Please enter recruiter's name"); recruiterStatus=0; break;
    }

    if(companyId == null){
        notifyError("Please select a company");
        recruiterStatus = 0;
    } else if(recruiterLandline != ""){
        if(!validateInteger(recruiterLandline)){
            notifyError("Please enter a valid landline number");
            recruiterStatus = 0;
        }
    } else if(recruiterPincode != ""){
        if(!validateInteger(recruiterPincode)){
            notifyError("Please enter a valid pincode number");
            recruiterStatus = 0;
        }
    } else if($("#rec_linkedin").val() != ""){
        if(!validateLinkedin($("#rec_linkedin").val())){
            notifyError("Please enter a valid linkedin profile");
            recruiterStatus = 0;
        }
    } else if($("#rec_company_website").val() != ""){
        if(!validateWebsiteLink($("#rec_company_website").val())){
            notifyError("Please enter a valid company website");
            recruiterStatus = 0;
        }
    }

    var logo;

    if ($("#companyLogo").val() != "") {
        if((f.type).substring(0,1) != "i"){
            notifyError("Please select a valid image for logo");
            companyStatus = 0;
        } else if(parseInt(f.size/1024/1024) > 2){
            notifyError("Please select a logo smaller than 2 MBs");
            companyStatus = 0;
        } else{
            logo = "https://s3.amazonaws.com/trujobs.in/companyLogos/default_company_logo.png";
            companyStatus = 1;
        }
    } else {
        companyStatus = 1;
        logo = $("#rec_company_old_logo").val();
    }

    var recruiterEmail = $("#rec_email").val();
    if(!validateEmail(recruiterEmail)){
        notifyError("Enter a valid email");
        companyStatus = 0;
    }


    if(recruiterStatus == 1 && companyStatus == 1){
        try {
            var companyLocalitySelected = $("#rec_company_locality").val();
            var companyTypeSelected = $("#rec_company_type").val();

            var localitySelectedVal = null;
            var typeSelectedVal = null;

            if(companyLocalitySelected != null){
                localitySelectedVal = companyLocalitySelected[0];
            }

            if(companyTypeSelected != null){
                typeSelectedVal = companyTypeSelected[0];
            }

            var d = {
                companyId: companyId,
                companyName: returnedCompanyName,
                companyEmployeeCount: $("#rec_company_employees").val(),
                companyWebsite: $("#rec_company_website").val(),
                companyDescription: $("#rec_company_desc").val(),
                companyPinCode: $("#rec_company_pincode").val(),
                companyAddress: $("#rec_company_address").val(),
                companyLocality: localitySelectedVal,
                companyLogo: logo,
                companyType: typeSelectedVal,
                source: 0 // SOURCE_INTERNAL
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
        } catch (err){}
    }
}

function processDataCompany(returnedData) {
    returnedData.forEach(function(company) {
        var id = company.companyId;
        var name = company.companyName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        companyArray.push(item);
        var option = $('<option value=' + id + '></option>').text(name);
        $('#rec_company_name').append(option);
    });
}


function processDataUpdateCompany(returnedData) {
    companyId = returnedData.companyId;
    if(returnedData.status == 1 || returnedData.status == 2){
        try{
            var rec = {
                recruiterMobile: ($("#rec_mobile").val()).substring(3, 13),
                recruiterName: $("#rec_name").val(),
                recruiterLandline: $("#rec_landline").val(),
                recruiterEmail: $("#rec_email").val(),
                recruiterLinkedinProfile: $("#rec_linkedin").val(),
                recruiterAlternateMobile: $("#rec_alternate_mobile").val(),
                recruiterCompany: companyId
            };
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        $.ajax({
            type: "POST",
            url: "/updateRecruiterProfile",
            async: false,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(rec),
            success: processDataAddRecruiter
        });

        if(document.getElementById("companyLogo").value != "") {
            //uploading logo
            uploadLogo(companyId);
        }

    } else{
        alert("Something went wrong! Please try again later");
    }
}

function readURL(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            $('#rec_company_logo_old')
                .attr('src', e.target.result);
        };

        reader.readAsDataURL(input.files[0]);
    }
}

function processDataAddRecruiter(returnedData) {
    if(returnedData.status == 4){
        notifySuccess("Profile updated successfully!");
    } else{
        notifyError("Something went wrong. Please try again later!");
    }
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}

function validateCompanyVal(val, text) {
    returnedCompanyName = text;
    if(isNaN(parseInt(val))){
        val = 0;
        companyId = 0;
        returnedCompanyName = text;
        clearCompanyDetails();
    } else{
        val = parseInt(val);
    }

    if(val != companyId && val != 0){
        clearCompanyDetails();
        try {
            $.ajax({
                type: "GET",
                url: "/getCompanyInfo/" + parseInt(val),
                data: false,
                contentType: false,
                processData: false,
                success: populateCompanyValues
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function resetCompanyIdval(val, text) {
    companyId = null;
    returnedCompanyName = "";
    clearCompanyDetails();
}

function clearCompanyDetails() {
//    $('#rec_company_name').tokenize().clear();
    $('#rec_company_type').tokenize().clear();
    $('#rec_company_locality').tokenize().clear();

    $("#rec_company_website").val('');
    $("#rec_company_employees").val('');
    $("#rec_company_pincode").val('');
    $("#rec_company_address").val('');
    $("#rec_company_desc").val('');
    $('#rec_company_logo_old')
        .attr('src', "");

    $("#rec_company_old_logo").val('');


}
