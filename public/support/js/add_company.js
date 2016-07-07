/**
 * Created by batcoder1 on 22/6/16.
 */

function processDataAddCompany(returnedData) {
    if(returnedData.status == 1){
        alert("Company Added Successfully");
    } else if(returnedData.status == 2){
        alert("Company Updated Successfully");
    } else{
        alert("Company already exists");
    }
    window.close();
}

// company_form ajax script
$(function() {
    $("#company_form").submit(function(eventObj) {
        eventObj.preventDefault();
        var status = 1;
        if($("#companyName").val() == ""){
            alert("Please Enter company Name");
            status=0;
        } else if($("#companyLogo").val() == ""){
            alert("Please Enter company Logo");
            status=0;
        }
        if(status == 1){
            console.log("inside js : " + $("#companyId").val());
            try {
                var d = {
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

    }); // end of submit
}); // end of function
