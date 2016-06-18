/**
 * Created by batcoder1 on 7/6/16.
 */

var localityArray = [];
var jobArray = [];

function getLocality(){
    return localityArray;
}

function getJob(){
    return jobArray;
}

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

$(function () {
    $('#myRegistrationModal').on('hidden.bs.modal', function () {
        document.getElementById("registerBtn").disabled = false;
        window.location = "/"
    })
});

$(document).ready(function(){
    console.log(applyJobFlag);

    $(".navbar-nav li a").click(function(event) {
        $(".navbar-collapse").collapse('hide');
    });

    var w = window.innerWidth;
    if(w < 440){
        $(".navbar-default").css('background-color', 'white');
    }
    $(window).scroll(function() {
        if ($(document).scrollTop() > 50) {
            $("#fixed-menu").css('background-color', '#2980b9');
            var w = window.innerWidth;
            if(w > 440){
                $(".navbar-fixed-top").removeClass('fade-transparent').addClass("fade-background"); // if yes, then change the color of class "navbar-fixed-top" to white (#f8f8f8)
                $("#navItem1").css('color', '#747474');
                $("#navItem2").css('color', '#747474');
                $("#navItem3").css('color', '#747474');
                $("#navItem4").css('color', '#747474');
                $("#navItem5").css('color', '#747474');

                var image = document.getElementById('navLogo');
                image.src = "/assets/new/img/logo-color.gif";
            }
        } else {
            $("#fixed-menu").css('background-color', 'rgba(0, 0, 0, 0.175)');
            var w = window.innerWidth;
            if(w > 480){

                $(".navbar-fixed-top").removeClass("fade-background").addClass('fade-transparent'); // if not, change it back to transparent
                $("#navItem1").css('color', '#ffffff');
                $("#navItem2").css('color', '#ffffff');
                $("#navItem3").css('color', '#ffffff');
                $("#navItem4").css('color', '#ffffff');
                $("#navItem5").css('color', '#ffffff');
                var image = document.getElementById('navLogo');
                image.src = "/assets/new/img/logo-main.gif";
                var w = window.innerWidth;
                if (w < 480) {
                    $("#fixed-menu").css('background-color', 'transparent');
                    $("#navItem1").css('color', '#747474');
                    $("#navItem2").css('color', '#747474');
                    $("#navItem3").css('color', '#747474');
                    $("#navItem4").css('color', '#747474');
                    $("#navItem5").css('color', '#747474');
                }
            }
        }
    });
    
    try {
        $.ajax({
            type: "GET",
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
            url: "/getAllJobs",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckJobs
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getAllJobPosts",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataAllJobPosts
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataAllJobPosts(returnedData) {
    var count = 0;
    var jobPostCount = Object.keys(returnedData).length;
    var parent = $("#hotJobPosts");
    /* for first 3 active items (slider) */
    var jobItemMain = document.createElement("div");
    jobItemMain.className = "item active";
    parent.append(jobItemMain);
    returnedData.forEach(function (jobPosts){
        count ++;
        if(count > 3){
            return false;
        }
        var jobItem = document.createElement("div");
        jobItem.className = "col-lg-4";
        jobItemMain.appendChild(jobItem);
        var jobItemPanel = document.createElement("div");
        jobItemPanel.className = "panel";
        jobItemPanel.id = "hot_box";
        jobItemPanel.style = "margin: 10%";
        jobItem.appendChild(jobItemPanel);
        var jobItemPanelHeading = document.createElement("div");
        jobItemPanelHeading.className = "panel-heading";
        jobItemPanelHeading.id = "hot_box_head";
        jobItemPanel.appendChild(jobItemPanelHeading);
        var jobLogo = document.createElement("img");
        jobLogo.src = "/assets/new/img/company4.jpg";
        jobItemPanelHeading.appendChild(jobLogo);
        var jobItemPanelBody = document.createElement("div");
        jobItemPanelBody.className = "panel-body";
        jobItemPanelBody.id = "hot_box_body";
        jobItemPanel.appendChild(jobItemPanelBody);
        var jobItemRole = document.createElement("div");
        jobItemRole.className = "hot_body_role";
        jobItemRole.textContent = jobPosts.jobPostTitle;
        jobItemPanelBody.appendChild(jobItemRole);
        var jobItemSalary = document.createElement("div");
        jobItemSalary.className = "hot_body_salary";
        jobItemSalary.textContent = jobPosts.jobPostMinSalary + " - " + jobPosts.jobPostMaxSalary + " monthly";
        jobItemPanelBody.appendChild(jobItemSalary);
        var jobItemExperience = document.createElement("div");
        jobItemExperience.className = "hot_body_salary";
        jobItemExperience.textContent = "Experience: " + jobPosts.jobPostExperience.experienceType;
        jobItemPanelBody.appendChild(jobItemExperience);
        var jobItemLocation = document.createElement("div");
        jobItemLocation.className = "hot_body_location";
        var localityList = jobPosts.jobPostToLocalityList;
        var localities = "";
        var loopCount = 0;
        localityList.forEach(function (locality) {
            loopCount ++;
            var name = locality.locality.localityName;
            localities += name;
            if(loopCount < Object.keys(localityList).length){
                localities += ", ";
            }
        });
        jobItemLocation.textContent = localities;
        jobItemPanelBody.appendChild(jobItemLocation);
        var jobHr = document.createElement("hr");
        jobItemPanelBody.appendChild(jobHr);
        var applyBtnDiv = document.createElement("div");
        applyBtnDiv.className = "btn jobApplyBtn";
        applyBtnDiv.id = jobPosts.jobPostId;
        applyBtnDiv.onclick = function () {
            applyJob(jobPosts.jobPostId);
        };
        applyBtnDiv.style = "width: 100%; font-weight: bold";
        jobItemPanelBody.appendChild(applyBtnDiv);
        var btnFont = document.createElement("font");
        btnFont.size = "2";
        btnFont.textContent = "Apply";
        applyBtnDiv.appendChild(btnFont);
    });

    /* for jobs more than 3(active) */
    var totalJob = jobPostCount;
    jobPostCount = jobPostCount - 3;
    var jobPostSectionCount = Math.floor(jobPostCount/3);
    var i;
    var startIndex = 3;
    for(i=0;i<jobPostSectionCount+1;i+=1){
        setJobs(returnedData,startIndex,totalJob);
        startIndex+=3;
    }
}

function setJobs(returnedData, start, totalJobs){
    var parent = $("#hotJobPosts");
    var jobItemMain = document.createElement("div");
    jobItemMain.className = "item";
    parent.append(jobItemMain);
    var count = 0;
    returnedData.forEach(function (jobPosts){
        count++;
        if(count > start && (count < start+4 && count<= totalJobs)){
            var jobItem = document.createElement("div");
            jobItem.className = "col-lg-4";
            jobItemMain.appendChild(jobItem);
            var jobItemPanel = document.createElement("div");
            jobItemPanel.className = "panel";
            jobItemPanel.id = "hot_box";
            jobItemPanel.style = "margin: 10%";
            jobItem.appendChild(jobItemPanel);
            var jobItemPanelHeading = document.createElement("div");
            jobItemPanelHeading.className = "panel-heading";
            jobItemPanelHeading.id = "hot_box_head";
            jobItemPanel.appendChild(jobItemPanelHeading);
            var jobLogo = document.createElement("img");
            jobLogo.src = "/assets/new/img/company4.jpg";
            jobItemPanelHeading.appendChild(jobLogo);
            var jobItemPanelBody = document.createElement("div");
            jobItemPanelBody.className = "panel-body";
            jobItemPanelBody.id = "hot_box_body";
            jobItemPanel.appendChild(jobItemPanelBody);
            var jobItemRole = document.createElement("div");
            jobItemRole.className = "hot_body_role";
            jobItemRole.textContent = jobPosts.jobPostTitle;
            jobItemPanelBody.appendChild(jobItemRole);
            var jobItemSalary = document.createElement("div");
            jobItemSalary.className = "hot_body_salary";
            jobItemSalary.textContent = jobPosts.jobPostMinSalary + " - " + jobPosts.jobPostMaxSalary + " monthly";
            jobItemPanelBody.appendChild(jobItemSalary);
            var jobItemExperience = document.createElement("div");
            jobItemExperience.className = "hot_body_salary";
            jobItemExperience.textContent = "Experience: " + jobPosts.jobPostExperience.experienceType;
            jobItemPanelBody.appendChild(jobItemExperience);
            var jobItemLocation = document.createElement("div");
            jobItemLocation.className = "hot_body_location";
            var localityList = jobPosts.jobPostToLocalityList;
            var localities = "";
            var loopCount = 0;
            localityList.forEach(function (locality) {
                loopCount ++;
                var name = locality.locality.localityName;
                localities += name;
                if(loopCount < Object.keys(localityList).length){
                    localities += ", ";
                }
            });
            jobItemLocation.textContent = localities;
            jobItemPanelBody.appendChild(jobItemLocation);
            var jobHr = document.createElement("hr");
            jobItemPanelBody.appendChild(jobHr);
            var applyBtnDiv = document.createElement("div");
            applyBtnDiv.className = "btn jobApplyBtn";
            applyBtnDiv.id = jobPosts.jobPostId;
            applyBtnDiv.onclick = function () {
                applyJob(jobPosts.jobPostId);
            };
            applyBtnDiv.style = "width: 100%; font-weight: bold";
            jobItemPanelBody.appendChild(applyBtnDiv);
            var btnFont = document.createElement("font");
            btnFont.size = "2";
            btnFont.textContent = "Apply";
            applyBtnDiv.appendChild(btnFont);
        }
    });
}

function processCheckLeadStatus() {
    alert("Thanks! We will get back soon!");
    $("#addLeadMobile").val('');

}
function addLead() {
    var phone = $('#addLeadMobile').val();
    var res = validateMobile(phone);
    if(res == 0){ // invalid mobile
        alert("Enter a valid mobile number");
    } else if(res == 1){ // mobile no. less than 1 digits
        alert("Enter 10 digit mobile number");
    }
    else{
        try {
            var d = {
                leadName : "",
                leadMobile : $("#addLeadMobile").val(),
                leadChannel : 0,
                leadType : 1,
                leadInterest : "General Registration"
            };
            $.ajax({
                type: "POST",
                url: "/addLead",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processCheckLeadStatus
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function openLogin() {
    document.getElementById("resetCheckUserBtn").disabled = false;
    document.getElementById("resetNewPasswordBtn").disabled = false;
    $('#form_login_candidate').show();
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_forgot_password').hide();
    $('#errorMsgReset').hide();
    $('#form_password_reset_otp').hide();
    $('#form_password_reset_new').hide();
}

function resetPassword() {
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_login_candidate').hide();
    $('#form_forgot_password').show();
}