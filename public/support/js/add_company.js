/**
 * Created by batcoder1 on 22/6/16.
 */

var f;

$('input[type=file]').change(function () {
    f = this.files[0];
    console.log((f.type).substring(0,1));
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
    if(returnedData.status == 1){
        alert("Creation Successful");
        window.close();
    } else if(returnedData.status == 2){
        alert("Updated Successful");
        window.close();
    } else if(returnedData.status == 3){
        alert("Something went wrong, please try again later!");
        window.close();
    } else if(returnedData.status == 5){
        alert("Company Updated Successfully");
        window.close();
    } else if(returnedData.status == 6){
        alert("Update Successful");
        window.close();
    } else{
        alert("Company already exists");
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
    } else{
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
                success: processDataAddCompany
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
        if(document.getElementById("companyLogo").value != "") {
            uploadLogo();
        }
    }
}

// company_form ajax script
function saveForm(){
    var status = 1;
    if($("#recruiterCompany").val() == ""){
        if($("#companyName").val() == ""){
            alert("Please Enter company Name");
            status=0;
        } else if($("#companyLogo").val() == ""){
            alert("Please Enter company Logo");
            status=0;
        } else if((f.type).substring(0,1) != "i"){
            alert("Please select a valid image for logo");
            status=0;
        }
    } else{
        status=2;
    }

    var statusCheck = 1;

    if(status > 0){
        console.log("this");
        var recruiterName = validateName($("#recruiterName").val());
        var recruiterMobile = validateMobile($("#recruiterMobile").val());
        //checking first name
        switch(recruiterName){
            case 0: alert("First name contains number. Please Enter a valid First Name"); statusCheck=0; break;
            case 2: alert("First Name cannot be blank spaces. Enter a valid first name"); statusCheck=0; break;
            case 3: alert("First name contains special symbols. Enter a valid first name"); statusCheck=0; break;
            case 4: alert("Please enter your first name"); statusCheck=0; break;
        }
        if(recruiterMobile == 0){
            alert("Enter a valid mobile number");
            statusCheck=0;
        } else if(recruiterMobile == 1){
            alert("Enter 10 digit mobile number");
            statusCheck=0;
        } else if(recruiterMobile == "") {
            alert("Please Enter recruiter Contact");
            statusCheck=0;
        }

        if(statusCheck == 1){
            var d;
            if(status == 1){
                var logo;
                if(($("#companyLogo").val()).substring(0,4) == "http"){
                    logo = $("#companyLogo").val();
                } else{
                    logo = "https://s3.amazonaws.com/trujobs.in/companyLogos/" + f.name;
                }
                try {
                    d = {
                        recruiterName: $("#recruiterName").val(),
                        recruiterMobile: $("#recruiterMobile").val(),
                        recruiterLandline: $("#recruiterLandline").val(),
                        recruiterEmail: $("#recruiterEmail").val(),
                        recruiterCompany: -1,
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
                } catch (exception) {
                    console.log("exception occured!!" + exception);
                }
            } else{
                try{
                    d = {
                        recruiterName: $("#recruiterName").val(),
                        recruiterMobile: $("#recruiterMobile").val(),
                        recruiterLandline: $("#recruiterLandline").val(),
                        recruiterEmail: $("#recruiterEmail").val(),
                        recruiterCompany: $("#recruiterCompany").val()
                    };
                } catch (exception) {
                    console.log("exception occured!!" + exception);
                }
            }
            try {
                $.ajax({
                    type: "POST",
                    url: "/addCompany",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataAddCompany
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
            uploadLogo();
        }
    } else{
        
    }
} // end of submit
