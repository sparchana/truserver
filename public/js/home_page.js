/**
 * Created by batcoder1 on 7/6/16.
 */

var localityArray = [];
var jobArray = [];
var prefLocation;
var prefLocationName;

var jobPostJobRoles = [];

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

$(function() {
    $('a[href*="#"]:not([href="#"])').click(function() {
        if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
            var target = $(this.hash);
            target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
            if (target.length) {
                $('html, body').animate({
                    scrollTop: target.offset().top - 92
                }, 1000);
            }
        }
    });
});
$(function () {
    $('#myRegistrationModal').on('hidden.bs.modal', function () {
        document.getElementById("registerBtn").disabled = false;
        window.location = "/"
    })
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
            $("#fixed-menu").css('background-color', '#2980b9');
            $("#fixed-menu").fadeIn();
            $(".navbar-default").css('background-color', 'white');
        } else {
            $("#fixed-menu").css('background-color', 'rgba(0, 0, 0, 0.175)');
            $("#fixed-menu").fadeOut();
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
    try {
        $.ajax({
            type: "POST",
            url: "/getAllCompanyLogos",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckCompanyLogo
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

});

function processDataCheckCompanyLogo(returnedData) {
    var companyCount = Object.keys(returnedData).length;
    var companyRowCount = Math.floor(companyCount / 6); // 6 because we are showing 6 companies in a row
    var remainingCompanies = companyCount % 6;

    var count = 0;
    var start = 0;
    var parent = $("#hiringCompanies");

    var rowDiv = document.createElement("div");
    rowDiv.className = "item active";
    parent.append(rowDiv);

    returnedData.forEach(function (company) {
        if(count >= start && count < (start+6)){
            var logoDiv = document.createElement("div");
            logoDiv.className = "col-sm-2";
            rowDiv.appendChild(logoDiv);

            var companyLogo = document.createElement("img");
            companyLogo.className = "img-responsive";
            companyLogo.setAttribute('alt', "Companies Hiring");
            companyLogo.src = company.companyLogo;
            logoDiv.appendChild(companyLogo);

        }
        count++;
        //checking when to end the loop
        if(count > 6){ return true; }
    });

    startIndex = 6;
    for(var i=1;i<companyRowCount; i++){
        setCompanyLogos(returnedData, startIndex);
        startIndex = startIndex + 6;
    }
    if(remainingCompanies > 0){
        startIndex = companyCount - remainingCompanies;
        setCompanyLogos(returnedData, startIndex);
    }
}

function setCompanyLogos(returnedData, start){
    var count = 0;
    var parent = $("#hiringCompanies");

    var rowDiv = document.createElement("div");
    rowDiv.className = "item";
    parent.append(rowDiv);

    returnedData.forEach(function (company) {
        if(count >= start && count < (start+6)){
            var logoDiv = document.createElement("div");
            logoDiv.className = "col-sm-2";
            rowDiv.appendChild(logoDiv);

            var companyLogo = document.createElement("img");
            companyLogo.className = "img-responsive";
            companyLogo.setAttribute('alt', "Companies Hiring");
            companyLogo.src = company.companyLogo;
            logoDiv.appendChild(companyLogo);

        }
        count++;
        //checking when to end the loop
        if(count > start + 6){ return true; }
    });
}



function processDataAllJobPosts(returnedData) {
    var jobPostCount = Object.keys(returnedData).length;
    if(jobPostCount > 0){
        var count = 0;
        var parent = $("#hotJobs");
        returnedData.reverse();
        returnedData.forEach(function (jobPost){
            count++;
            if(count){
                //!* get all localities of the jobPost *!/
                var jobLocality = jobPost.jobPostToLocalityList;
                var localities = "";
                var allLocalities = ""
                var loopCount = 0;
                jobLocality.forEach(function (locality) {
                    loopCount ++;
                    if(loopCount > 2){
                        return false;
                    } else{
                        var name = locality.locality.localityName;
                        localities += name;
                        if(loopCount < Object.keys(jobLocality).length){
                            localities += ", ";
                        }
                    }
                });
                loopCount = 0;
                jobLocality.forEach(function (locality) {
                    loopCount++;
                    var name = locality.locality.localityName;
                    allLocalities += name;
                    if(loopCount < Object.keys(jobLocality).length){
                        allLocalities += ", ";
                    }
                });

                var hotJobItem = document.createElement("div");
                hotJobItem.id = "hotJobItem";
                parent.append(hotJobItem);

                var centreTag = document.createElement("center");
                hotJobItem.appendChild(centreTag);

                var rowDiv = document.createElement("div");
                rowDiv.className = "row";
                centreTag.appendChild(rowDiv);

                var col = document.createElement("div");
                col.className = "col-sm-2";
                rowDiv.appendChild(col);

                var jobLogo = document.createElement("img");
                jobLogo.src = jobPost.company.companyLogo;
                jobLogo.setAttribute('width', '80%');
                jobLogo.id = "jobLogo";
                col.appendChild(jobLogo);

                var jobBodyCol = document.createElement("div");
                jobBodyCol.className = "col-sm-8";
                jobBodyCol.id = "jobBody";
                rowDiv.appendChild(jobBodyCol);

                var jobTitle = document.createElement("h4");
                jobTitle.textContent = jobPost.jobPostTitle + " | " + jobPost.company.companyName;
                jobBodyCol.appendChild(jobTitle);

                var hr = document.createElement("hr");
                jobBodyCol.appendChild(hr);

                var jobBodyDetails = document.createElement("div");
                jobBodyDetails.className = "row";
                jobBodyDetails.id = "jobBodyDetails";
                jobBodyCol.appendChild(jobBodyDetails);

                //!*  salary  *!/

                var bodyCol = document.createElement("div");
                bodyCol.className = "col-sm-4";
                bodyCol.id = "jobSalary";
                jobBodyDetails.appendChild(bodyCol);

                var jobBodySubRow = document.createElement("div");
                jobBodySubRow.className = "row";
                bodyCol.appendChild(jobBodySubRow);

                var jobBodySubRowCol = document.createElement("div");
                jobBodySubRowCol.className = "col-sm-12";
                jobBodySubRow.appendChild(jobBodySubRowCol);

                var salaryIconDiv = document.createElement("div");
                salaryIconDiv.style = "display : inline-block; margin: 4px;top:0";
                jobBodySubRowCol.appendChild(salaryIconDiv);

                var salaryIcon = document.createElement("img");
                salaryIcon.src = "/assets/img/salary.svg";
                salaryIcon.setAttribute('height', '15px');
                salaryIcon.style = "margin-top: -4px";
                salaryIconDiv.appendChild(salaryIcon);


                var salaryDiv = document.createElement("div");
                salaryDiv.style = "display: inline-block; font-size: 14px";
                if(jobPost.jobPostMaxSalary == "0"){
                    salaryDiv.textContent = jobPost.jobPostMinSalary + " monthly";
                } else{
                    salaryDiv.textContent = jobPost.jobPostMinSalary + " - " + jobPost.jobPostMaxSalary + " monthly";
                }

                jobBodySubRowCol.appendChild(salaryDiv);

                //!*  experience  *!/

                var bodyColExp = document.createElement("div");
                bodyColExp.className = "col-sm-3";
                bodyColExp.id = "jobExp";
                jobBodyDetails.appendChild(bodyColExp);

                var jobBodySubRowExp = document.createElement("div");
                jobBodySubRowExp.className = "row";
                bodyColExp.appendChild(jobBodySubRowExp);

                var jobBodySubRowColExp = document.createElement("div");
                jobBodySubRowColExp.className = "col-sm-12";
                jobBodySubRowExp.appendChild(jobBodySubRowColExp);

                var expIconDiv = document.createElement("div");
                expIconDiv.style = "display : inline-block; margin: 4px;top:0";
                jobBodySubRowColExp.appendChild(expIconDiv);

                var expIcon = document.createElement("img");
                expIcon.src = "/assets/img/workExp.svg";
                expIcon.setAttribute('height', '15px');
                expIcon.style = "margin-top: -4px";
                expIconDiv.appendChild(expIcon);

                var expDiv = document.createElement("div");
                expDiv.style = "display: inline-block; font-size: 14px";
                expDiv.textContent = "Exp: " + jobPost.jobPostExperience.experienceType;
                jobBodySubRowColExp.appendChild(expDiv);

                //!*  Location  *!/

                var bodyColLoc = document.createElement("div");
                bodyColLoc.className = "col-sm-5";
                bodyColLoc.id = "jobLocation";
                jobBodyDetails.appendChild(bodyColLoc);

                var jobBodySubRowLoc = document.createElement("div");
                jobBodySubRowLoc.className = "row";
                bodyColLoc.appendChild(jobBodySubRowLoc);

                var jobBodySubRowColLoc = document.createElement("div");
                jobBodySubRowColLoc.className = "col-sm-12";
                jobBodySubRowLoc.appendChild(jobBodySubRowColLoc);

                var locIconDiv = document.createElement("div");
                locIconDiv.style = "display : inline-block; margin: 4px;top:0";
                jobBodySubRowColLoc.appendChild(locIconDiv);

                var locIcon = document.createElement("img");
                locIcon.src = "/assets/img/location.svg";
                locIcon.setAttribute('height', '15px');
                locIcon.style = "margin-top: -4px";
                locIconDiv.appendChild(locIcon);

                var locDiv = document.createElement("div");
                locDiv.style = "display: inline-block; font-size: 14px";
                locDiv.textContent = localities;
                jobBodySubRowColLoc.appendChild(locDiv);

                if(((jobLocality.length) - 2) > 0 ){
                    var tooltip = document.createElement("a");
                    tooltip.id = "locationMsg_" + jobPost.jobPostId;
                    tooltip.title = allLocalities;
                    tooltip.style = "color: #2980b9";
                    tooltip.textContent = " more";
                    jobBodySubRowColLoc.appendChild(tooltip);
                }

                $("#locationMsg_" + jobPost.jobPostId).attr("data-toggle", "tooltip");
                $(function () {
                    $('[data-toggle="tooltip"]').tooltip()
                });

                //getting all the job roles
                var isThere = 0;
                for(var x=0; x<jobPostJobRoles.length; x++){
                    if(jobPostJobRoles[x].jobRoleId == jobPost.jobRole.jobRoleId){
                        isThere = 1;
                    }
                }
                if(isThere == 0){
                    var jobRoleId = jobPost.jobRole.jobRoleId;
                    var jobName = jobPost.jobRole.jobName;
                    var item = {};
                    item ["jobRoleId"] = jobRoleId;
                    item ["jobName"] = jobName;
                    jobPostJobRoles.push(item);
                } else{
                    console.log("already there");
                }

                //!*  apply button *!/
                var applyBtnDiv = document.createElement("div");
                applyBtnDiv.className = "col-sm-2";
                rowDiv.appendChild(applyBtnDiv);

                var applyBtn = document.createElement("div");
                applyBtn.className = "jobApplyBtn";
                applyBtn.textContent = "View Job";
                applyBtnDiv.appendChild(applyBtn);
                applyBtn.onclick=function(){
                    var jobPostBreak = jobPost.jobPostTitle.replace("/","-");
                    try {
                        window.location.href = "/jobs/" + jobPostBreak + "/Bengaluru/" + jobPost.company.companyName + "/" + jobPost.jobPostId;
                    } catch (exception) {
                        console.log("exception occured!!" + exception);
                    }
                }
            }
        });
    }
    var jobRoleCount = Object.keys(jobPostJobRoles).length;
    var jobRoleRowCount = Math.floor(jobRoleCount / 6); // 6 because we are showing 6 job roles in a row
    var remainingJobRoles = jobRoleCount % 6;
    var startIndex = 0;

    for(var i=0;i<jobRoleRowCount; i++){
        setJobRoles(jobPostJobRoles, startIndex);
        startIndex = startIndex + 6;
    }
    if(remainingJobRoles > 0){
        startIndex = jobRoleCount - remainingJobRoles;
        setJobRoles(jobPostJobRoles, startIndex);
    }
}

function setJobRoles(returnedData, start){
    var count = 0;
    var parent = $("#jobRoleGrid");
    returnedData.forEach(function (jobRole) {
        if(count >= start && count < (start+6)){
            var rowDiv = document.createElement("div");
            rowDiv.className = "row";
            parent.append(rowDiv);

            var gridDiv = document.createElement("div");
            rowDiv.className = "col-md-2 col-sm-4 col-xs-6";
            rowDiv.style = "padding: 0px";
            rowDiv.appendChild(gridDiv);

            var jobAnchor = document.createElement("a");
            jobAnchor.onclick = function () {
                window.location.href = "/job/" + jobRole.jobName.split("/").join('_') + "_jobs" + "/" + jobRole.jobRoleId;
            };
            gridDiv.appendChild(jobAnchor);

            var innerDiv = document.createElement("div");
            innerDiv.id = "jobRole";
            innerDiv.style = "padding-top: 20%; padding-bottom: 20%";
            jobAnchor.appendChild(innerDiv);

            var jobIcon = document.createElement("img");
            jobIcon.src = "/assets/new/img/icons/" + jobRole.jobRoleId + ".svg";
            jobIcon.setAttribute('width', '50px');
            jobIcon.setAttribute('alt', jobRole.jobName);
            innerDiv.appendChild(jobIcon);

            var subDiv = document.createElement("div");
            subDiv.style = "margin-top: 6px; color: #003557;";
            subDiv.textContent = jobRole.jobName;
            innerDiv.appendChild(subDiv);
        }
        count++;

        //checking when to end the loop
        if(count > start + 6){ return true; }
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
    $("#jobNameConfirmation").html(returnedData.jobPostTitle);
    $("#companyNameConfirmation").html(returnedData.company.companyName);

    $('#jobLocality').html('');
    var defaultOption=$('<option value="-1"></option>').text("Select Preferred Location");
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
}

function confirmApply() {
    applyJob(jobPostId, prefLocation);
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

function processCheckLeadStatus() {
    $("#addLeadMobile").val("");
    $("#messagePromptModal").modal("show");
    $("#customMsg").html("Thanks! We will get back soon!");
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
    $("#signInPopup").html("Sign In");
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

function openSignUp() {
    $("#myLoginModal").modal("hide");
}

function resetPassword() {
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_login_candidate').hide();
    $('#form_forgot_password').show();
}