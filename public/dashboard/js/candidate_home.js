/**
 * Created by batcoder1 on 4/6/16.
 */

var jobPostId = 0;
var jobLocalityArray = [];
var minProfileComplete = 0;
var prefLocation;
var prefLocationName;
var candidateId = 0;

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
    checkUserLogin();

    if(localStorage.getItem("assessed") == '0'){
        $(".assessmentComplete").hide();
        $(".assessmentIncomplete").show();
    }
    else{
        localStorage.setItem("assessed", "1");
        $(".assessmentComplete").show();
        $(".assessmentIncomplete").hide();
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateInfoDashboard",
            data: false,
            async: true,
            contentType: false,
            processData: false,
            success: processDataAndFillMinProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function getJobsForCandidate() {

    try {
        $.ajax({
            type: "POST",
            url: "/getRelevantJobsPostsForCandidate/" + candidateId,
            data: false,
            async: true,
            contentType: false,
            processData: false,
            success: processDataAllJobPosts
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);

    }

}

function processDataAllJobPosts(returnedData) {
    var jobPostCount = Object.keys(returnedData).length;
    if(jobPostCount > 0){
        var count = 0;
        var _parent = $("#hotJobs");

        $("#jobLoaderDiv").hide();

        genNewJobCard(returnedData, _parent);

        if(count<4){
            document.getElementById("hotJobs").style= ("overflow:scroll;height:51%")
        }
        else{
            document.getElementById("hotJobs").style= ("overflow:scroll;height:100%");

        }
    } else {
        $("#jobLoaderDiv").hide();
    }
}

$(function() {
    $("#jobLocality").change(function (){
        if($(this).val() != -1){
            prefLocation = $(this).val();
            prefLocationName = $("#jobLocality option:selected").text();
            $("#applyButton").show();
        } else{
            $("#applyButton").hide();
        }
    });
});

function processDataAndFillMinProfile(returnedData) {

    candidateId = returnedData.candidateId;
    //viewDownloadResume(candidateId);
    console.log(returnedData);
    if(returnedData.candidateResumeLink != null){
        $("#resumeUploadBoxInner").hide();
        $("#userViewResume").html("");
        var parentView = $("#userViewResume");
        var viewLink = document.createElement("a");
        viewLink.href = "http://docs.google.com/gview?url=" + returnedData.candidateResumeLink + "&embedded=true";
        viewLink.target = "_blank";
        viewLink.id = "viewResume";
        viewLink.textContent = "View |";
        parentView.append(viewLink);

        $("#userViewDownload").html("");
        var parentDownload = $("#userViewDownload");
        var downloadLink = document.createElement("a");
        downloadLink.href = returnedData.candidateResumeLink;
        downloadLink.id = "downloadResume";
        downloadLink.textContent = "Download";
        parentDownload.append(downloadLink);
    }
    if(returnedData.candidateLastName == "" || returnedData.candidateLastName == null){
        document.getElementById("userName").innerHTML = returnedData.candidateFirstName;
    } else{
        document.getElementById("userName").innerHTML = returnedData.candidateFirstName + " " + returnedData.candidateLastName;
    }
    document.getElementById("userMobile").innerHTML = returnedData.candidateMobile;

    minProfileComplete = returnedData.isMinProfileComplete;
    if(returnedData.isMinProfileComplete == 0){ // profile not complete
        $(".profileComplete").hide();
        $(".profileIncomplete").show();
        localStorage.setItem("minProfile", 0);
    } else{
        $(".profileComplete").show();
        $(".profileIncomplete").hide();
        localStorage.setItem("minProfile", 1);
    }
    if(returnedData.candidateIsAssessed == 1){
        localStorage.setItem("assessed", "1");
        $(".assessmentIncomplete").hide();
        $(".assessmentComplete").show();
    } else {
        localStorage.setItem("assessed", "0");
        $(".assessmentIncomplete").show();
        $(".assessmentComplete").hide();
    }

    if (returnedData.candidateGender != null) {
        localStorage.setItem("gender", returnedData.candidateGender);
        if (returnedData.candidateGender == 0) {
            try{
                document.getElementById("userGender").innerHTML = " , Male";
                $("#userImg").attr('src', '/assets/dashboard/img/userMale.svg');
            } catch(err){}
        } else {
            try{
                document.getElementById("userGender").innerHTML = " , Female";
                $("#userImg").attr('src', '/assets/dashboard/img/userFemale.svg');
            } catch(err){}
        }
    } else{
        try{
            $("#userImg").attr('src', '/assets/dashboard/img/userMale.svg');
        } catch(err){}
    }
    if (returnedData.candidateDOB != null) {
        var date = JSON.parse(returnedData.candidateDOB);
        var yr = new Date(date).getFullYear();
        var month = ('0' + parseInt(new Date(date).getMonth() + 1)).slice(-2);
        var d = ('0' + new Date(date).getDate()).slice(-2);
        var today = new Date();
        var birthDate = new Date(yr + "-" + month + "-" + d);
        var age = today.getFullYear() - birthDate.getFullYear();
        var m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        document.getElementById("userAge").innerHTML = " , " + age + " years";
    }
    try {
        var jobRoles = "";
        var count = 0;
        var jobPref = returnedData.jobPreferencesList;
        if(jobPref.length > 0){
            jobPref.forEach(function (job){
                count ++;
                var name = job.jobRole.jobName;
                jobRoles += name;
                if(count < Object.keys(jobPref).length){
                    jobRoles += ", ";
                }
            });
            document.getElementById("userJobs").innerHTML = jobRoles;
        }
    } catch(err){
        console.log(err);
    }

    try {
        if(returnedData.locality != null){
            document.getElementById("userLocality").innerHTML = returnedData.locality.localityName;
        }
    } catch(err){
        console.log("getCandidateLocalityPref error"+err);
    }

    /* Time Shift */
    if (returnedData.timeShiftPreference != null) {
        document.getElementById("userShift").innerHTML = returnedData.timeShiftPreference.timeShift.timeShiftName;
        if(returnedData.timeShiftPreference.timeShift.timeShiftId == 5){
            document.getElementById("userShift").innerHTML = returnedData.timeShiftPreference.timeShift.timeShiftName + " Shift";
        }
    }

    /* candidate Education */
    try{
        if(returnedData.candidateEducation.education != null) {
            document.getElementById("userEducationLevel").innerHTML = returnedData.candidateEducation.education.educationName;
        }
    } catch(err){}

    /* Work Experience */
    if(returnedData.candidateTotalExperience != null){
        if(returnedData.candidateTotalExperience == 0) {
            document.getElementById("userTotalExperience").innerHTML = "Fresher";
            document.getElementById("userCurrentSalary").innerHTML = "Not Applicable";
            document.getElementById("userCurrentCompany").innerHTML = "Not Applicable";
        }
        else {
                var totalExperience = parseInt(returnedData.candidateTotalExperience);
                var yrs = parseInt((totalExperience / 12)).toString();
                var month = totalExperience % 12;
                if(yrs == 0 && month != 0){
                    document.getElementById("userTotalExperience").innerHTML = month + " months";
                } else if(month == 0 && yrs != 0){
                document.getElementById("userTotalExperience").innerHTML = yrs + " years";

            } else{
                document.getElementById("userTotalExperience").innerHTML = yrs + " yrs and " + month + " mnths";
            }

            /* Current Company and Salary */
            if (Object.keys(returnedData.jobHistoryList).length > 0) {
                returnedData.jobHistoryList.forEach(function (pastJob) {
                    if(pastJob.currentJob == true){
                        if(pastJob.candidatePastCompany != null && pastJob.candidatePastCompany != ""){
                            document.getElementById("userCurrentCompany").innerHTML = pastJob.candidatePastCompany;
                        } else{
                            document.getElementById("userCurrentCompany").innerHTML = "Not Specified";
                        }
                        return false;
                    }
                });
            }

            if(returnedData.candidateLastWithdrawnSalary != null){
                if(returnedData.candidateLastWithdrawnSalary == "0"){
                    document.getElementById("userCurrentSalary").innerHTML = "Not Applicable";
                } else{
                    document.getElementById("userCurrentSalary").innerHTML = "&#x20B9;" + returnedData.candidateLastWithdrawnSalary + "/month";
                }
            }
        }
    }

    var appliedJobs = returnedData.jobApplicationList;
    if(appliedJobs != null) {
        appliedJobs.forEach(function (jobApplication) {
            $("#apply_btn_" + jobApplication.jobPost.jobPostId).addClass("appliedBtn").removeClass("btn-primary").prop('disabled',true).html("Applied");
            $("#applyBtnDiv_" + jobApplication.jobPost.jobPostId).prop('disabled',true);
        });
    }

    getJobsForCandidate();

}