var jobId = 0;

$(document).ready(function(){
    localStorage.clear();
    $(".navbar-nav li a").click(function(event) {
        $(".navbar-collapse").collapse('hide');
    });

    var w = window.innerWidth;
    if(w < 440){
        $(".navbar-default").css('background-color', 'white');
    }
    $(window).scroll(function() {
        if ($(document).scrollTop() > 150) {
            $("#fixed-menu-shadow").css('background-color', '#2980b9');
            $("#fixed-menu-shadow").fadeIn();
            $(".navbar-default").css('background-color', 'white');
        } else {
            $("#fixed-menu-shadow").css('background-color', 'rgba(0, 0, 0, 0.175)');
            $("#fixed-menu-shadow").fadeOut();
        }
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
            url: "/getAllJobs",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckJobs
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "POST",
            url: "/getAllHotJobPosts",
            data: false,
            contentType: false,
            processData: false,
            success: processDataAllJobPosts
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

$(document).ready(function(){
    var jobDetailPageUrl = $(location).attr('href');
    var jobDetailPageUrlBreak = jobDetailPageUrl.split("/");
    jobDetailPageUrlBreak.reverse();
    try {
            $.ajax({
                type: "GET",
                url: "/job/" + jobDetailPageUrlBreak[3] + "/"+ jobDetailPageUrlBreak[2] + "/"+jobDetailPageUrlBreak[1] +"/"+jobDetailPageUrlBreak[0],
                contentType: "application/json; charset=utf-8",
                data: false,
                processData: false,
                success: processDataForHotJobPost
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
});

function applyJobBtnAction() {
    $('#jobApplyConfirm').modal();
    jobPostId = jobId;
    jobLocalityArray = [];
    $('#applyButton').hide();
    addLocalitiesToModal();
}
function processDataForHotJobPost(returnedData) {
    jobId = returnedData.jobPostId;

    if (returnedData.jobPostTitle != null) {
        $("#postedJobTitle").html(returnedData.jobPostTitle+" | "+returnedData.company.companyName);
    }
    if (returnedData.company != null) {
        $("#postedJobCompanyTitle").html(returnedData.company.companyName);
        $("#postedCompanyTitle").html(returnedData.company.companyName);
    }
    if (returnedData.jobPostAddress != null) {
        $("#postedJobLocationAddress").html(returnedData.jobPostAddress);
    }
    if (returnedData.jobPostMinSalary != null && returnedData.jobPostMaxSalary != null) {
        if (returnedData.jobPostMaxSalary == null || returnedData.jobPostMaxSalary == "0") {
            $("#postedJobSalary").html(returnedData.jobPostMinSalary);
        }else if (returnedData.jobPostMinSalary == null || returnedData.jobPostMinSalary == "0") {
            $("#postedJobSalary").html(returnedData.jobPostMinSalary);
        }
        else {
            $("#postedJobSalary").html(returnedData.jobPostMinSalary + " - " + returnedData.jobPostMaxSalary);
            $("#salaryCondition").html("Salary (Min - Max)");
        }
    }
    if (returnedData.jobPostIncentives != "") {
        $("#postedJobIncentives").html(returnedData.jobPostIncentives);
    }
    //locality
    if (returnedData.jobPostToLocalityList != null) {
        var localityList = returnedData.jobPostToLocalityList;
        var allLocalities = "";
        localityList.forEach(function (locality) {
            if(allLocalities !="") {
                allLocalities += ", ";
            }
            allLocalities += locality.locality.localityName;

        });
        $("#postedJobLocality").html(allLocalities);
    }
    if (returnedData.jobPostShift != null){
        $("#postedJobShift").html(returnedData.jobPostShift.timeShiftName);
    }

    if (returnedData.jobPostWorkingDays != "" && returnedData.jobPostWorkingDays != null) {
        var workingDays = returnedData.jobPostWorkingDays.toString(2);
        var i;
        /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
        if (workingDays.length != 7) {
            var x = 7 - workingDays.length;
            var modifiedWorkingDays = "";

            for (i = 0; i < x; i++) {
                modifiedWorkingDays += "0";
            }
            modifiedWorkingDays += workingDays;
            workingDays = modifiedWorkingDays;
        }
        var holiday = "";
        var arryDay = workingDays.split("");
        if (arryDay[0] != 1) {
            holiday += "Mon, ";
        }
        if (arryDay[1] != 1) {
            holiday += "Tue, ";
        }
        if (arryDay[2] != 1) {
            holiday += "Wed, ";
        }
        if (arryDay[3] != 1) {
            holiday += "Thu, ";
        }
        if (arryDay[4] != 1) {
            holiday += "Fri, ";
        }
        if (arryDay[5] != 1) {

            holiday += "Sat, ";
        }
        if (arryDay[6] != 1) {
            holiday += "Sun ";
        }
            $("#postedJobWorkingDays").html(holiday +" - Holiday");
    }
    if (returnedData.jobPostStartTime != null && returnedData.jobPostEndTime!= null) {
        var valStart;
        var valEnd;
        if(returnedData.jobPostStartTime > 12){
            returnedData.jobPostStartTime = returnedData.jobPostStartTime - 12;
            valStart ="PM";
        }
        else{
             valStart = "AM";
        }
        if(returnedData.jobPostEndTime > 12){
            returnedData.jobPostEndTime = returnedData.jobPostEndTime - 12;
            valEnd ="PM";
        }
        else{
            valEnd = "AM";
        }
        $("#postedJobTiming").html(returnedData.jobPostStartTime + " "+valStart+ " - "+ returnedData.jobPostEndTime +" "+valEnd);

    }
    if (returnedData.jobPostMinRequirement != "") {
        $("#postedJobMinRequirement").html(returnedData.jobPostMinRequirement);
    }

    if (returnedData.jobPostExperience  != null) {
        $("#postedJobExperience").html(returnedData.jobPostExperience.experienceType);
    }
    if (returnedData.jobPostEducation != null ) {
        $("#postedJobEducation").html(returnedData.jobPostEducation.educationName);
    }
    if (returnedData.jobPostDescription != "") {
        $("#postedJobDescription").html(returnedData.jobPostDescription);
    }
    //Company Details
    if (returnedData.company.companyLocality != null ) {
        $("#postedJobCompanyLocation").html(returnedData.company.companyLocality.localityName);
    }
    if(returnedData.company.companyLogo != null){
     document.getElementById("postedJobCompanyLogo").src=returnedData.company.companyLogo;
        document.getElementById("postedCompanyLogo").src=returnedData.company.companyLogo;
    }
    if (returnedData.company.companyWebsite != null ) {
        $("#postedJobCompanyWebsite").html(returnedData.company.companyWebsite);
    }
    if (returnedData.company.companyDescription != "" ) {
        $("#postedJobCompanyDescriotion").html(returnedData.company.companyDescription);
    }
    if (returnedData.company.compType != null ) {
        $("#postedJobCompanyType").html(returnedData.company.compType.companyTypeName);
    }

}