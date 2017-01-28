var jobId = 0;
var localityArray = [];
var jobArray = [];

var prefTimeSlot = null;
var scheduledInterviewDate = null;

function getLocality(){
    return localityArray;
}

function getJob(){
    return jobArray;
}

$(document).ready(function(){
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

    /*try {
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
    }*/
});

$(window).load(function() {
    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").delay(1000).fadeOut("slow");
});

$(document).ready(function(){
    try {
            $.ajax({
                type: "GET",
                url: "/job/"+ jobRoleNameRender  + "-jobs-in-"+ jobLocationRender +"-at-"+ jobCompanyRender +"-"+ jobPostIdRender,
                contentType: "application/json; charset=utf-8",
                data: false,
                processData: false,
                success: processDataForHotJobPost,
                error: function (xhr, ajaxOption, throwError) {
                    console.log(xhr.status);
                    if(xhr.status == 400){
                        window.location = '/pageNotFound';
                    }
                }
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
});

function processDataCheckLocality(returnedData) {
    returnedData.forEach(function(locality)
    {
        var id = locality.localityId;
        var name = locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        localityArray.push(item);
    });
}

function addLocalitiesToModal() {
    $("#applyButton").addClass("jobApplyBtnModal").removeClass("appliedBtn").prop('disabled',false).html("Apply");
    try {
        $.ajax({
            type: "POST",
            url: "/getJobPostInfo/" + jobPostId + "/0",
            data: false,
            contentType: false,
            processData: false,
            success: processDataForJobPostLocation
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataForJobPostLocation(returnedData) {

    $('#locality_jobNameConfirmation').html(returnedData.jobPostTitle);
    $('#locality_companyNameConfirmation').html(returnedData.company.companyName);
    

    $('#jobLocality').html('');
    if(returnedData.jobPostToLocalityList.length > 1){
        var defaultOption=$('<option value="-1"></option>').text("Select Preferred Location");
    }
    $('#jobLocality').append(defaultOption);
    var jobLocality = returnedData.jobPostToLocalityList;
    jobLocality.forEach(function (locality) {
        var item = {};
        item ["id"] = locality.locality.localityId;
        item ["name"] = " " + locality.locality.localityName;
        jobLocalityArray.push(item);
        var option=$('<option value=' + locality.locality.localityId + '></option>').text(locality.locality.localityName);
        $('#jobLocality').append(option);
    });

    if (Object.keys(returnedData.interviewDetailsList).length > 0) {
        //slots
        var i;
        $('#interviewSlot').html('');
        var defaultOption = $('<option value="-1"></option>').text("Select Time Slot");
        $('#interviewSlot').append(defaultOption);

        var interviewDetailsList = returnedData.interviewDetailsList;
        if (interviewDetailsList[0].interviewDays != null) {
            var interviewDays = interviewDetailsList[0].interviewDays.toString(2);

            /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
            if (interviewDays.length != 7) {
                x = 7 - interviewDays.length;
                var modifiedInterviewDays = "";

                for (i = 0; i < x; i++) {
                    modifiedInterviewDays += "0";
                }
                modifiedInterviewDays += interviewDays;
                interviewDays = modifiedInterviewDays;
            }
        }
        //slots
        var today = new Date();
        for (i = 2; i < 9; i++) {
            // 0 - > sun 1 -> mon ...
            var x = new Date(today.getFullYear(), today.getMonth(), today.getDate() + i);
            if (checkSlotAvailability(x, interviewDays)) {
                interviewDetailsList.forEach(function (timeSlot) {
                    var dateSlotSelectedId = x.getFullYear() + "-" + (x.getMonth() + 1) + "-" + x.getDate() + "_" + timeSlot.interviewTimeSlot.interviewTimeSlotId;
                    var option = $('<option value="' + dateSlotSelectedId + '"></option>').text(getDayVal(x.getDay()) + ", " + x.getDate() + " " + getMonthVal((x.getMonth() + 1)) + " (" + timeSlot.interviewTimeSlot.interviewTimeSlotName + ")");
                    $('#interviewSlot').append(option);
                });
            }
        }
        $('#interviewSection').show();
    } else{
        $('#interviewSection').hide();
    }
    enableLocalityBtn();
}

function confirmApply() {
    $("#applyButton").removeClass("jobApplyBtnModal").addClass("jobApplied").prop('disabled',true).html("Applying");
    $("#applyButton").click(function(){ return false});
    $("#applyButton").unbind();
    $("#applyButton").removeAttr("onclick");

    applyJobSubmitViaCandidate(jobPostId, prefLocation, prefTimeSlot, scheduledInterviewDate, true);
//    applyJob(jobPostId, prefLocation, true);
}
$("#jobLocality").change(function () {
    enableLocalityBtn();
});
function enableLocalityBtn() {
    if($("#jobLocality").val() != -1 && $("#interviewSlot").val() != -1){
            prefLocation = $("#jobLocality").val();
            prefLocationName = $("#jobLocality option:selected").text();

            try{
                if ($("#interviewSlot").css('display') != 'none'){
                    var combinedValue = $("#interviewSlot").val().split("_");
                    scheduledInterviewDate = combinedValue[0];
                    prefTimeSlot = combinedValue[1];
                }
            } catch(err){}

            $(".jobApplyBtnModal").prop('disabled', false);
            $(".jobApplyBtnModal").css({'background-color':'#09ac58','color':'#ffffff','cursor':'default'});
        } else{
            $(".jobApplyBtnModal").prop('disabled', true);
            $(".jobApplyBtnModal").css({'background-color':'#dde0dd','color':'#000000','cursor':'not-allowed'});
        }

    $("#interviewSlot").change(function (){
        if($(this).val() != -1 && $("#jobLocality").val() != -1){
            var combinedValue = $(this).val().split("_");
            scheduledInterviewDate = combinedValue[0];
            prefTimeSlot = combinedValue[1];

            prefLocation = $("#jobLocality").val();
            prefLocationName = $("#jobLocality option:selected").text();
            $("#applyButton").show();
        } else{
            $("#applyButton").hide();
        }
    });
}

function getDayVal(month){
    switch(month) {
        case 0:
            return "Sun";
            break;
        case 1:
            return "Mon";
            break;
        case 2:
            return "Tue";
            break;
        case 3:
            return "Wed";
            break;
        case 4:
            return "Thu";
            break;
        case 5:
            return "Fri";
            break;
        case 6:
            return "Sat";
            break;
    }
}

function getMonthVal(month){
    switch(month) {
        case 1:
            return "Jan";
            break;
        case 2:
            return "Feb";
            break;
        case 3:
            return "Mar";
            break;
        case 4:
            return "Apr";
            break;
        case 5:
            return "May";
            break;
        case 6:
            return "Jun";
            break;
        case 7:
            return "Jul";
            break;
        case 8:
            return "Aug";
            break;
        case 9:
            return "Sep";
            break;
        case 10:
            return "Oct";
            break;
        case 11:
            return "Nov";
            break;
        case 12:
            return "Dec";
            break;
    }
}

function checkSlotAvailability(x, interviewDays) {
    if(x.getDay() == 1 && interviewDays.charAt(0) == '1'){ // monday
        return true;
    } else if(x.getDay() == 2 && interviewDays.charAt(1) == '1'){ //tue
        return true;
    } else if(x.getDay() == 3 && interviewDays.charAt(2) == '1'){ //wed
        return true;
    } else if(x.getDay() == 4 && interviewDays.charAt(3) == '1'){ //thu
        return true;
    } else if(x.getDay() == 5 && interviewDays.charAt(4) == '1'){ //fri
        return true;
    } else if(x.getDay() == 6 && interviewDays.charAt(5) == '1'){ //sat
        return true;
    } else if(x.getDay() == 0 && interviewDays.charAt(6) == '1'){ //sun
        return true;
    }
}

function openLogin() {
    $("#signInPopup").html("Sign In");
    try{ document.getElementById("resetCheckUserBtn").disabled = false; } catch (e){}
    try{ document.getElementById("resetNewPasswordBtn").disabled = false; } catch (e){}
    $('#form_login_candidate').show();
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_forgot_password').hide();
    $('#errorMsgReset').hide();
    $('#form_password_reset_otp').hide();
    $('#form_password_reset_new').hide();
}

function openSignUp() {
    $("#myLoginModal").modal("hide");
}

function resetPassword() {
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_login_candidate').hide();
    $('#form_forgot_password').show();
}

function processDataCheckJobs(returnedData) {
    returnedData.forEach(function(job)
    {
        var id = job.jobRoleId;
        var name = job.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        jobArray.push(item);
    });
}

function applyJobBtnAction() {
    $('#jobApplyConfirm').modal();
    var candidateMobile = localStorage.getItem("mobile");
    jobPostId = jobId;
    jobLocalityArray = [];
    //openCandidatePreScreenModal(jobPostId, localStorage.getItem("mobile"));
    $('#applyButton').show();
    addLocalitiesToModal();
}

function processJobPostAppliedStatus(status) {
    if(status == "true"){
        $(".jobApplyBtnV2").addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied");
        $('.jobApplyBtnV2').attr('onclick','').unbind('click');
    }
}

function processDataForHotJobPost(returnedData) {
    if (returnedData != "") {
        jobId = returnedData.jobPostId;
        if(returnedData.jobPostPartnerInterviewIncentive != null){
            $("#interviewIncentiveVal").html("₹" + returnedData.jobPostPartnerInterviewIncentive + " interview incentive");
        } else{
            $("#interviewIncentiveVal").html("Interview incentive not specified");
        }
        if(returnedData.jobPostPartnerJoiningIncentive != null){
            $("#joiningIncentiveVal").html("₹" + returnedData.jobPostPartnerJoiningIncentive + " joining incentive");
        } else{
            $("#joiningIncentiveVal").html("Joining incentive not specified");
        }
        if (returnedData.jobPostTitle != null && returnedData.jobPostTitle != "") {
            $("#postedJobTitle").html(returnedData.jobPostTitle + " | " + returnedData.company.companyName);
        }
        if (returnedData.company != null && returnedData.company != "") {
            $("#postedJobCompanyTitle").html(returnedData.company.companyName);
            $("#postedCompanyTitle").html(returnedData.company.companyName);
        }
        if (returnedData.jobPostAddress != null && returnedData.jobPostAddress != "") {
            $("#postedJobLocationAddress").html(returnedData.jobPostAddress);
        }
        if (returnedData.jobPostMinSalary != null && returnedData.jobPostMinSalary != 0) {
            if (returnedData.jobPostMaxSalary == null || returnedData.jobPostMaxSalary == "0") {
                $("#postedJobSalary").html(rupeeFormatSalary(returnedData.jobPostMinSalary) + " monthly");
            }
            else {
                $("#postedJobSalary").html(rupeeFormatSalary(returnedData.jobPostMinSalary) + " - " + rupeeFormatSalary(returnedData.jobPostMaxSalary) + " monthly");
                $("#salaryCondition").html("Salary (Min - Max)");
            }
        }
        if (returnedData.jobPostIncentives != null && returnedData.jobPostIncentives != "") {
            $("#postedJobIncentives").html(returnedData.jobPostIncentives);
        }
        if (returnedData.jobPostVacancies != null && returnedData.jobPostVacancies != 0){
            $("#postedJobNoOfVacancy").html(returnedData.jobPostVacancies);
        }
        //locality
        if (returnedData.jobPostToLocalityList != null && returnedData.jobPostToLocalityList != "") {
            var localityList = returnedData.jobPostToLocalityList;
            var allLocalities = "";
            localityList.forEach(function (locality) {
                if (allLocalities != "") {
                    allLocalities += ", ";
                }
                allLocalities += locality.locality.localityName;

            });
            $("#postedJobLocality").html(allLocalities);
        }
        if (returnedData.jobPostShift != null && returnedData.jobPostShift != "") {
            $("#postedJobShift").html(returnedData.jobPostShift.timeShiftName);
        }

        if (returnedData.jobPostWorkingDays != "" && returnedData.jobPostWorkingDays != null) {
            if(returnedData.jobPostWorkingDays == 127){
                $("#postedJobWorkingDays").html("No - Holiday");
            }else{
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
                $("#postedJobWorkingDays").html(holiday + " - Holiday");
            }

        }
        
        if (returnedData.jobPostStartTime != null && returnedData.jobPostStartTime != -1
            && returnedData.jobPostEndTime != null && returnedData.jobPostEndTime != null != -1)
        {
            var valStart;
            var valEnd;
            if (returnedData.jobPostStartTime > 12) {
                returnedData.jobPostStartTime = returnedData.jobPostStartTime - 12;
                valStart = "PM";
            }
            else {
                valStart = "AM";
            }
            if (returnedData.jobPostEndTime > 12) {
                returnedData.jobPostEndTime = returnedData.jobPostEndTime - 12;
                valEnd = "PM";
            }
            else {
                valEnd = "AM";
            }

            $("#postedJobTiming").html(returnedData.jobPostStartTime + " " + valStart + " - " + returnedData.jobPostEndTime + " " + valEnd);

        }

        if (returnedData.jobPostMinRequirement != null && returnedData.jobPostMinRequirement != "") {
            $("#postedJobMinRequirement").html(returnedData.jobPostMinRequirement);
        }
        if(returnedData.gender != null){
            if(returnedData.gender == 0){
                $("#postedJobGender").html("Male");
            }
            else if (returnedData.gender == 1){
                $("#postedJobGender").html("Female");
            }
            else{
                $("#postedJobGender").html("Any");
            }
        }
        if(returnedData.jobPostMaxAge != null && returnedData.jobPostMaxAge != ""){
            $("#postedJobMaxAge").html(returnedData.jobPostMaxAge + " Yrs");
        }
        if(returnedData.jobPostDocumentRequirements !=null && returnedData.jobPostDocumentRequirements != ""){
            var documentArray = [] ;
            returnedData.jobPostDocumentRequirements.forEach(function (data) {
                var lengthOfDocumentElement =  returnedData.jobPostDocumentRequirements.length;
                if(documentArray.length < lengthOfDocumentElement - 1){
                    documentArray.push(data.idProof.idProofName+" , ");
                }
                else{
                    documentArray.push(data.idProof.idProofName);
                }
            });
            $("#postedJobDocuments").html(documentArray);
        }
         if(returnedData.jobPostAssetRequirements !=null && returnedData.jobPostAssetRequirements != ""){
             var assetArray = [] ;
             returnedData.jobPostAssetRequirements .forEach(function (data) {
                 var lengthOfAssetsElement =  returnedData.jobPostAssetRequirements .length;
                 if(assetArray.length < lengthOfAssetsElement - 1){
                     assetArray.push(data.asset.assetTitle+" , ");
                 }
                 else{
                     assetArray.push(data.asset.assetTitle);
                 }
             });
            $("#postedJobAssets").html(assetArray );
         }
        if (returnedData.jobPostExperience!= null &&
            returnedData.jobPostExperience.experienceType!= null
            && returnedData.jobPostExperience.experienceType!= "") {
            $("#postedJobExperience").html(returnedData.jobPostExperience.experienceType);
        }
        if (returnedData.jobPostEducation != null && returnedData.jobPostEducation != "") {
            $("#postedJobEducation").html(returnedData.jobPostEducation.educationName);
        }
        if(returnedData.jobPostLanguageRequirements !=null && returnedData.jobPostLanguageRequirements != ""){
            var languageArray = [] ;
            returnedData.jobPostLanguageRequirements.forEach(function (data) {
                var lengthOfLanguageElement =  returnedData.jobPostLanguageRequirements.length;
                if(languageArray.length < lengthOfLanguageElement - 1){
                    languageArray.push(data.language.languageName +" , ");
                }
                else{
                    languageArray.push(data.language.languageName);
                }
            });
            $("#postedJobLanguage").html(languageArray);
        }
        if (returnedData.jobPostDescription != null && returnedData.jobPostDescription != "") {
            $("#postedJobDescription").html(returnedData.jobPostDescription);
        }
        //Company Details
        if (returnedData.source == null || returnedData.source == 0) {
            $(".posted_jobs_company_details").show();
            $("div#aboutCompanyTitle").show();

            if (returnedData.company.companyLocality != null) {
                $("#postedJobCompanyLocation").html(returnedData.company.companyLocality.localityName);
            }
            if (returnedData.company.companyLogo != null) {
                document.getElementById("postedJobCompanyLogo").src = returnedData.company.companyLogo;
                document.getElementById("postedCompanyLogo").src = returnedData.company.companyLogo;
            }
            if (returnedData.company.companyWebsite != null && returnedData.company.companyWebsite != "") {
                $("#postedJobCompanyWebsite").html(returnedData.company.companyWebsite);
            }
            if (returnedData.company.companyDescription != null && returnedData.company.companyDescription != "") {
                $("#postedJobCompanyDescriotion").html(returnedData.company.companyDescription);
            }
            if (returnedData.company.compType != null) {
                $("#postedJobCompanyType").html(returnedData.company.compType.companyTypeName);
            }
        } else {
            $(".posted_jobs_company_details").hide();
            $("div#aboutCompanyTitle").hide();
        }

        if(returnedData.applyBtnStatus != null && returnedData.applyBtnStatus != CTA_BTN_APPLY){
            var applyBtn = $(".jobApplyBtnV2");
            if(returnedData.applyBtnStatus == CTA_BTN_INTERVIEW_REQUIRED) {
                applyBtn.html("Book Interview");
            } else if(returnedData.applyBtnStatus == CTA_BTN_DEACTIVE){
                applyBtn.html("Apply");
                applyBtn.css("background", "#ffa726");
                applyBtn.attr('onclick','').unbind('click');
                applyBtn.on("click", function () {
                    jobCardUtil.method.notifyMsg(jobCardUtil.deActivationMessage, 'danger');
                });

            } else if(returnedData.applyBtnStatus == CTA_BTN_INTERVIEW_CLOSED) {
                applyBtn.removeClass("btn-primary").addClass("appliedBtn").prop('disabled',true).html("Application Closed");
                applyBtn.attr('onclick','').unbind('click');
                applyBtn.css("background", "#f4cb6c");
                applyBtn.css("box-shadow", "none");
                applyBtn.css("cursor", "default");

                var nextMonday = new Date();
                nextMonday.setDate(nextMonday.getDate() + (1 + 7 - nextMonday.getDay()) % 7);

                var day = nextMonday.getDate();
                if(day < 10){
                    day = "0" + day;
                }

                var month = nextMonday.getMonth() + 1;
                if(month < 10){
                    month = "0" + month;
                }

                $("#reopenDate").html("Will reopen on " + day + "-" + month + "-" + nextMonday.getFullYear());
            }
        }

        try {
            $.ajax({
                type: "GET",
                url: "/getJobPostAppliedStatus/" + returnedData.jobPostId,
                data: false,
                contentType: false,
                processData: false,
                success: processJobPostAppliedStatus
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    } else {
        window.location.href = "/pageNotFound";
    }
}