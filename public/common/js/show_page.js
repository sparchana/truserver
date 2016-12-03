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
    $('#myRegistrationModal').on('hidden.bs.modal', function () {
        document.getElementById("registerBtn").disabled = false;
        window.location = "/"
    })
});

$(window).load(function(){
    var autoPlay = $("#hiringCompanyLogo");
    autoPlay.trigger('owl.play',2200);
});

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

    $("#sectionOne").css("background-image","linear-gradient(rgba(24, 26, 45, 0.4),rgba(24, 26, 45, 0.4))" +
        ",url(/assets/common/img/"+allJobDetailPageUrlBreak[0]+".png)");

    try {
        $.ajax({
            type: "GET",
            url: "/jobs/" + allJobDetailPageUrlBreak[1] +"/"+ allJobDetailPageUrlBreak[0],
            contentType: "application/json; charset=utf-8",
            data: false,
            processData: false,
            success: processDataForSelectedJobPost
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    $("#hiringCompanyLogo").owlCarousel({
        items : 3,
        itemsMobile : true,
        jsonPath : '/getAllCompanyLogos',
        jsonSuccess : customDataSuccess
    });

});

function customDataSuccess(data){
    var content = "";
    data.forEach(function (company) {
        var img = company.companyLogo;
        var alt = company.companyName;
        content += "<img width='228px' height='76px' src=\"" +img+ "\" alt=\"" +alt+ "\">"
    });
    $("#hiringCompanyLogo").html(content);
}

function createAndAppendDivider(title, ifPrepend) {
    var parent = $("#hotJobs");

    var mainDiv = document.createElement("div");
    mainDiv.id = "hotJobItemDivider";
    if(ifPrepend){
        parent.prepend(mainDiv);
    } else{
        parent.append(mainDiv);
    }

    var otherJobIcon = document.createElement("img");
    otherJobIcon.src = "/assets/common/img/suitcase.png";
    otherJobIcon.style = "width: 42px; margin: 8px";
    otherJobIcon.setAttribute("display", "inline-block");
    mainDiv.appendChild(otherJobIcon);

    var hotJobItem = document.createElement("span");
    hotJobItem.setAttribute("display", "inline-block");
    hotJobItem.textContent = title;


    mainDiv.appendChild(hotJobItem);
}

function processDataForSelectedJobPost(returnedData) {
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
    var decodedJobRoleName = showTitle.replace(/%20/g, " ");
        $('#jobTitleLine').html("Register for "+ decodedJobRoleName);
        $('#jobRoleTitle').html("Register for "+ decodedJobRoleName);
        $('#applyToJobsTitle').html("Apply to "+ decodedJobRoleName);
        if(returnedData != ""){
        var jobPostCount = Object.keys(returnedData).length;
            if (jobPostCount > 0) {
                var count = 0;
                var popularJobCount = 0;
                var parent = $("#hotJobs");
                var isDividerPresent = false;
                //returnedData.reverse();
                $("#jobLoaderDiv").hide();
                try {
                    returnedData.forEach(function (jobPost) {
                        count++;
                        if (count) {
                            /* get all localities of the jobPost */
                            var jobLocality = jobPost.jobPostToLocalityList;
                            var localities = "";
                            var allLocalities = "";
                            var loopCount = 0;

                            if(jobPost.source != null && jobPost.source > 0 && !isDividerPresent){
                                createAndAppendDivider("Other Jobs", false);
                                isDividerPresent = true;
                            }

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
                            rowDiv.style = "margin: 0; padding: 0";
                            centreTag.appendChild(rowDiv);

                            var col = document.createElement("div");
                            col.className = "col-sm-2";
                            rowDiv.appendChild(col);

                            var jobLogo = document.createElement("img");
                            if(jobPost.company.companyLogo != null) jobLogo.src = jobPost.company.companyLogo;
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
                            salaryIcon.src = "/assets/common/img/salary.svg";
                            salaryIcon.setAttribute('height', '15px');
                            salaryIcon.style = "margin-top: -4px";
                            salaryIconDiv.appendChild(salaryIcon);


                            var salaryDiv = document.createElement("div");
                            salaryDiv.style = "display: inline-block; font-size: 14px";
                            if(jobPost.jobPostMaxSalary == "0" || jobPost.jobPostMaxSalary == null){
                                salaryDiv.textContent = rupeeFormatSalary(jobPost.jobPostMinSalary) + " monthly";
                            } else{
                                salaryDiv.textContent = rupeeFormatSalary(jobPost.jobPostMinSalary) + " - " + rupeeFormatSalary(jobPost.jobPostMaxSalary) + " monthly";
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
                            expIcon.src = "/assets/common/img/workExp.svg";
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
                            locIcon.src = "/assets/common/img/location.svg";
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

                            if(localStorage.getItem("incentives") == "1"){
                                var incentiveDetails = document.createElement("div");
                                incentiveDetails.className = "row";
                                incentiveDetails.id = "incentiveDetails";
                                jobBodyCol.appendChild(incentiveDetails);

                                //!*  interview incentive  *!/

                                var interviewIncentiveCol = document.createElement("div");
                                interviewIncentiveCol.className = "col-sm-4";
                                incentiveDetails.appendChild(interviewIncentiveCol);

                                var interviewIncentiveRow = document.createElement("div");
                                interviewIncentiveRow.className = "row";
                                interviewIncentiveCol.appendChild(interviewIncentiveRow);

                                var interviewIncentiveRowCol = document.createElement("div");
                                interviewIncentiveRowCol.className = "col-sm-12";
                                interviewIncentiveRow.appendChild(interviewIncentiveRowCol);

                                var incentiveIconDiv = document.createElement("div");
                                incentiveIconDiv.style = "display : inline-block;top:0";
                                interviewIncentiveRowCol.appendChild(incentiveIconDiv);

                                var incentiveIcon = document.createElement("img");
                                incentiveIcon.src = "/assets/partner/img/coin.png";
                                incentiveIcon.setAttribute('height', '20px');
                                incentiveIcon.style = "margin: -4px 0 0 -5px";
                                incentiveIconDiv.appendChild(incentiveIcon);

                                var interviewIncentiveVal = document.createElement("div");
                                interviewIncentiveVal.className = "incentiveEmptyBody";
                                interviewIncentiveVal.style = "display: inline-block;";
                                if(jobPost.jobPostPartnerInterviewIncentive == null || jobPost.jobPostPartnerInterviewIncentive == 0){
                                    interviewIncentiveVal.textContent = "Interview incentive not specified";
                                } else{
                                    interviewIncentiveVal.textContent = "₹" + rupeeFormatSalary(jobPost.jobPostPartnerInterviewIncentive) + " interview incentive";
                                    incentiveIcon.src = "/assets/partner/img/money-bag.png";
                                    interviewIncentiveVal.className = "incentiveBody";
                                }
                                interviewIncentiveRowCol.appendChild(interviewIncentiveVal);

                                //!*  joining incentive  *!/

                                var joiningIncentiveCol = document.createElement("div");
                                joiningIncentiveCol.className = "col-sm-4";
                                incentiveDetails.appendChild(joiningIncentiveCol);

                                var joiningIncentiveRow = document.createElement("div");
                                joiningIncentiveRow.className = "row";
                                joiningIncentiveCol.appendChild(joiningIncentiveRow);

                                var joiningIncentiveRowCol = document.createElement("div");
                                joiningIncentiveRowCol.className = "col-sm-12";
                                joiningIncentiveRow.appendChild(joiningIncentiveRowCol);

                                incentiveIconDiv = document.createElement("div");
                                incentiveIconDiv.style = "display : inline-block;top:0";
                                joiningIncentiveRowCol.appendChild(incentiveIconDiv);

                                incentiveIcon = document.createElement("img");
                                incentiveIcon.src = "/assets/partner/img/coin.png";
                                incentiveIcon.setAttribute('height', '20px');
                                incentiveIcon.style = "margin: -4px 0 0 -5px";
                                incentiveIconDiv.appendChild(incentiveIcon);

                                var joiningIncentiveVal = document.createElement("div");
                                joiningIncentiveVal.className = "incentiveEmptyBody";
                                joiningIncentiveVal.style = "display: inline-block;";
                                if(jobPost.jobPostPartnerJoiningIncentive == null || jobPost.jobPostPartnerJoiningIncentive == 0){
                                    joiningIncentiveVal.textContent = "Joining Incentive not specified";
                                } else{
                                    joiningIncentiveVal.textContent =  "₹" + rupeeFormatSalary(jobPost.jobPostPartnerJoiningIncentive) + " joining incentive";
                                    incentiveIcon.src = "/assets/partner/img/money-bag.png";
                                    joiningIncentiveVal.className = "incentiveBody";
                                }
                                incentiveIconDiv.appendChild(joiningIncentiveVal);
                            }

                            //!*  apply button *!/
                            var applyBtnDiv = document.createElement("div");
                            applyBtnDiv.className = "col-sm-2";
                            rowDiv.appendChild(applyBtnDiv);

                            var applyBtn = document.createElement("div");
                            if(localStorage.getItem("incentives") == "1"){
                                applyBtn.textContent = "View Job";
                            } else{
                                applyBtn.textContent = "View & Apply";
                            }
                            applyBtn.className = "jobApplyBtn";

                            applyBtnDiv.appendChild(applyBtn);
                            applyBtn.onclick=function(){
                                var jobPostBreak = jobPost.jobPostTitle.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'_');
                                jobPostBreak = jobPostBreak.toLowerCase();
                                var jobCompany = jobPost.company.companyName.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g,'_');
                                jobCompany = jobCompany.toLowerCase();
                                try {
                                    window.location.href = "/jobs/" + jobPostBreak + "/bengaluru/" + jobCompany + "/" + jobPost.jobPostId;
                                } catch (exception) {
                                    console.log("exception occured!!" + exception.stack);
                                }
                            };
                            if(jobPost.source == 0){
                                popularJobCount++;
                            }
                        }
                    });
                    if(count<4){
                        document.getElementById("hotJobs").style.height = "54%";
                    }
                    else{
                        document.getElementById("hotJobs").style.height = "72%";
                    }
                } catch (exception) {
                    console.log("exception occured!!" + exception.stack);
                }
                if(popularJobCount > 0){
                    createAndAppendDivider("Popular Jobs", true);
                }
            }
    } else{
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

            var jobImage = document.createElement("div");
            jobImage.id = "jobImage";
            col.appendChild(jobImage);

            var jobImageSrc = document.createElement("img");
            jobImageSrc.id = "jobImageSrc";
            jobImage.appendChild(jobImageSrc);
            $("#jobImageSrc").attr('src', '/assets/common/img/empty-search.svg');

            var jobMsgLine1 = document.createElement("div");
            jobMsgLine1.id = "jobMsgLine1";
            col.appendChild(jobMsgLine1);
            $("#jobMsgLine1").html("Oops!! No relevant jobs found at this movement");

            var jobMsgLine2 = document.createElement("div");
            jobMsgLine2.id = "jobMsgLine2";
            col.appendChild(jobMsgLine2);
            $("#jobMsgLine2").html("Register yourself to get updates when new jobs are posted");
    }
    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").delay(500).fadeOut("slow");
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
            console.log("exception occured!!" + exception.stack);
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
