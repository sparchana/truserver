/**
 * Created by batcoder1 on 22/6/16.
 */

function processDataAddCompany(returnedData) {
    if(returnedData.status == 1){
        alert("Creation Successful");
    } else if(returnedData.status == 2){
        alert("Updated Successful");
    } else if(returnedData.status == 3){
        alert("Something went wrong, please try again later!");
    } else if(returnedData.status == 5){
        alert("Company Updated Successfully");
    } else if(returnedData.status == 6){
        alert("Update Successful");
    } else{
        alert("Company already exists");
    }
    window.close();
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
        }        
    } else{
        status=2;
    }

    var statusCheck = 1;

    if(status > 0){
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
            alert("Please Enter your Job Localities");
            statusCheck=0;
        }
        else if(recruiterMobile == "") {
            alert("Please Enter the Jobs you are Interested");
            statusCheck=0;
        }

        if(statusCheck == 1){
            var d;
            if(status == 1){
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
                        companyLogo: $("#companyLogo").val(),
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
        }
    } else{
        
    }
} // end of submit
