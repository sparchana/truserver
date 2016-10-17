/**
 * Created by dodo on 12/10/16.
 */

var f;
var companyId;

$('input[type=file]').change(function () {
    f = this.files[0];
});

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

$(document).scroll(function(){
    if ($(this).scrollTop() > 80) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});

$(document).ready(function(){
    checkRecruiterLogin();
    $('.button-collapse').sideNav({
        menuWidth: 240,
        edge: 'left',
        closeOnClick: true
    });
    try {
        $.ajax({
            type: "POST",
            url: "/getAllLocality",
            data: false,
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
    console.log(returnedData);
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

            if(returnedData.company.companyLocality != null){
                $('#rec_company_locality').tokenize().tokenAdd(returnedData.company.companyLocality.localityId, returnedData.company.companyLocality.localityName);
            }
            if(returnedData.company.compType != null){
                $('#rec_company_type').tokenize().tokenAdd(returnedData.company.compType.companyTypeId, returnedData.company.compType.companyTypeName);
            }

            if(returnedData.company.companyName != null){
                $("#rec_company_name").val(returnedData.company.companyName);
            }
            if(returnedData.company.companyPinCode != null){
                $("#rec_company_pincode").val(returnedData.company.companyPinCode);
            }
            if(returnedData.company.companyWebsite != null){
                $("#rec_company_website").val(returnedData.company.companyWebsite);
            }
            if(returnedData.company.companyDescription != null){
                $("#rec_company_desc").val(returnedData.company.companyDescription);
            }
            if(returnedData.company.companyAddress != null){
                $("#rec_company_address").val(returnedData.company.companyAddress);
            }
            if(returnedData.company.companyEmployeeCount != null){
                $("#rec_company_employees").val(returnedData.company.companyEmployeeCount);
            }
            if(returnedData.company.companyLogo != null){
                $('#rec_company_logo_old')
                    .attr('src', returnedData.company.companyLogo);
                $("#rec_company_old_logo").val(returnedData.company.companyLogo);
            }
        }
    }

}

function validateCompanyTypeVal(val, text) {
/*    var isValidTokenVal = /^\d+$/.test(val);
    if(!isValidTokenVal){
        $('#rec_company_type').tokenize().tokenRemove(val);
        notifyError("Please select a valid company type from the dropdown list");
    }*/
    console.log(val + " -- " + text);
    if(val.localeCompare(text) == 0){
        $('#rec_company_type').tokenize().tokenRemove(val);
        notifyError("Please select a valid company type from the dropdown list");
    }

}

function validateCompanyLocationVal(val, text) {
    console.log(val + " -- " + text);
    if(val.localeCompare(text) == 0){
        $('#rec_company_locality').tokenize().tokenRemove(val);
        notifyError("Please select a valid location from the dropdown list");
    }
/*    var isValidTokenVal = /^\d+$/.test(val);
    if(!isValidTokenVal){
        $('#rec_company_locality').tokenize().tokenRemove(val);

    }*/
}

function saveForm() {
    var recruiterStatus = 1;
    var companyStatus = 1;
    var recruiterName = validateName($("#rec_name").val());

    //checking first name
    switch(recruiterName){
        case 0: alert("Recruiter's name contains number. Please Enter a valid name"); recruiterStatus=0; break;
        case 2: alert("Recruiter's name cannot be blank spaces. Enter a valid name"); recruiterStatus=0; break;
        case 3: alert("Recruiter's name contains special symbols. Enter a valid name"); recruiterStatus=0; break;
        case 4: alert("Please enter recruiter's name"); recruiterStatus=0; break;
    }

    var logo;

    if ($("#companyLogo").val() != "") {
        if((f.type).substring(0,1) != "i"){
            notifyError("Please select a valid image for logo");
            companyStatus = 0;
        } else{
            logo = "https://s3.amazonaws.com/trujobs.in/companyLogos/" + f.name;
            companyStatus = 1;
        }
    } else {
        companyStatus = 1;
        logo = $("#rec_company_old_logo").val();
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
                companyName: $("#rec_company_name").val(),
                companyEmployeeCount: $("#rec_company_employees").val(),
                companyWebsite: $("#rec_company_website").val(),
                companyDescription: $("#rec_company_desc").val(),
                companyPinCode: $("#rec_company_pincode").val(),
                companyLocality: localitySelectedVal,
                companyLogo: logo,
                companyType: typeSelectedVal
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
        } catch (err){}
    }
}

function processDataUpdateCompany(returnedData) {
    if(returnedData.status == 2){
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
            url: "/addRecruiter",
            async: false,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(rec),
            success: processDataAddRecruiter
        });
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