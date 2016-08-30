/*Created by hawk on 24/8/16.*/

var localityArray = [];
var jobArray = [];
var prefLocation;
var prefLocationName;

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
    $("#hotJobs").scroll(function() {
        var w = window.innerWidth;
        if(w < 440){
            document.documentElement.scrollTop = document.body.scrollTop = 280;
        } else{
            document.documentElement.scrollTop = document.body.scrollTop = 248;
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
    var allJobDetailPageUrl = $(location).attr('href');
    var allJobDetailPageUrlBreak = allJobDetailPageUrl.split("/");
    allJobDetailPageUrlBreak.reverse();
    $("#sectionOne").css("background-image","linear-gradient(rgba(24, 26, 45, 0.1),rgba(24, 26, 45, 0.1))" +
        ",url(/assets/img/"+allJobDetailPageUrlBreak[0]+".png)");
    try {
        $.ajax({
            type: "GET",
            url: "/jobs/" +allJobDetailPageUrlBreak[1] +"/"+ allJobDetailPageUrlBreak[0],
            contentType: "application/json; charset=utf-8",
            data: false,
            processData: false,
            success: processDataForSelectedJobPost
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataForSelectedJobPost(returnedData) {
    if(returnedData != ""){
        var allJobDetailPageUrl = $(location).attr('href');
        var allJobDetailPageUrlBreak = allJobDetailPageUrl.split("/");
        allJobDetailPageUrlBreak.reverse();
        var conTitle = allJobDetailPageUrlBreak[1].split("_");
        var showTitle = [""];
        var temp;
        for(var i=0;i<conTitle.length;i++){
            temp = conTitle[i].charAt(0).toUpperCase() + conTitle[i].substring(1);
            showTitle +=temp + " ";
        }
            $('#jobTitleLine').html("Register for "+showTitle);
            $('#jobRoleTitle').html("Register for "+showTitle);
            $('#applyToJobsTitle').html("Apply to "+showTitle);
            var jobPostCount = Object.keys(returnedData).length;
                if (jobPostCount > 0) {
                var count = 0;
                var parent = $("#hotJobs");
                returnedData.reverse();
                try {
                    returnedData.forEach(function (jobPost) {
                        count++;
                        if (count) {
                            /* get all localities of the jobPost */
                            var jobLocality = jobPost.jobPostToLocalityList;
                            var localities = "";
                            var allLocalities = ""
                            var loopCount = 0;
                            jobLocality.forEach(function (locality) {
                                loopCount++;
                                if (loopCount > 2) {
                                    return false;
                                } else {
                                    var name = locality.locality.localityName;
                                    localities += name;
                                    if (loopCount < Object.keys(jobLocality).length) {
                                        localities += ", ";
                                    }
                                }
                            });

                            loopCount = 0;
                            jobLocality.forEach(function (locality) {
                                loopCount++;
                                var name = locality.locality.localityName;
                                allLocalities += name;
                                if (loopCount < Object.keys(jobLocality).length) {
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

                            /*  salary  */

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
                            if (jobPost.jobPostMaxSalary == "0") {
                                salaryDiv.textContent = jobPost.jobPostMinSalary + " monthly";
                            } else {
                                salaryDiv.textContent = jobPost.jobPostMinSalary + " - " + jobPost.jobPostMaxSalary + " monthly";
                            }

                            jobBodySubRowCol.appendChild(salaryDiv);

                            /*  experience  */

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

                            /*  Location  */

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

                            if (((jobLocality.length) - 2) > 0) {
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

                            hotJobItem.onclick = function () {
                                window.location.href = "/jobs/" + jobPost.jobPostTitle + "/Bengaluru/" + jobPost.company.companyName + "/" + jobPost.jobPostId;
                            }
                            /*  apply button */
                            var applyBtnDiv = document.createElement("div");
                            applyBtnDiv.className = "col-sm-2";
                            applyBtnDiv.onclick = function () {
                                $('#jobApplyConfirm').modal();
                                jobPostId = jobPost.jobPostId;
                                jobLocalityArray = [];
                                $('#applyButton').hide();
                                addLocalitiesToModal();
                            };
                            rowDiv.appendChild(applyBtnDiv);

                            var applyBtn = document.createElement("div");
                            applyBtn.className = "jobApplyBtn";
                            applyBtn.textContent = "Apply";
                            applyBtnDiv.appendChild(applyBtn);
                        }
                        console.log("show selected jobs Page Js");
                    });
                } catch (exception) {
                    console.log("exception occured!!" + exception);
                }

            }
        }
    else{
            var parent = $("#hotJobs");
            var hotJobItem = document.createElement("div");
            hotJobItem.id = "hotJobItem";
            parent.append(hotJobItem);

            var centreTag = document.createElement("center");
            hotJobItem.appendChild(centreTag);

            var rowDiv = document.createElement("div");
            rowDiv.className = "row";
            centreTag.appendChild(rowDiv);

            var col = document.createElement("div");
            col.className = "col-sm-12";
            rowDiv.appendChild(col);

            var jobLogo = document.createElement("div");
            jobLogo.id = "jobLogo";
            col.appendChild(jobLogo);
            $("#jobLogo").html("No result found");

    }
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
