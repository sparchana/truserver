/**
 * Created by batcoder1 on 22/6/16.
 */

function addCompany(){
    try {
        var d = {
            companyName: "Swiggy",
            companyEmployeeCount: 50,
            companyWebsite: "www.swiggy.com",
            companyDescription: "Company Description",
            companyAddress: "Bangalore",
            companyPinCode: 560035,
            companyLogo: "swiggy",
            companyLocality: 1,
            companyType: 1,
            companyStatus: 1 
        };
        $.ajax({
            type: "POST",
            url: "/addCompany",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataAddJobPost
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}