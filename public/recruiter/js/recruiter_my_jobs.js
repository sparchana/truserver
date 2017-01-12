/**
 * Created by hawk on 21/10/16.
 */

var newCount = 0;
var jobPostObj;

$(window).load(function() {
    setTimeout(function(){
        if(newCount == 0){
            $(".newNotification").hide();
        } else{
            $(".newNotification").show();
            $("#pendingApproval").html(newCount);
            $("#pendingApprovalMobile").html(newCount);
        }
        $(".jobNav").addClass("active");
        $(".jobNavMobile").addClass("active");
    }, 100);
});

$(document).scroll(function(){
    if ($(this).scrollTop() > 30) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});

$(document).ready(function(){
    checkRecruiterLogin();
    try {
        $.ajax({
            type: "POST",
            url: "/getAllRecruiterJobPosts",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGenerateJobPostView,
            error: function (jqXHR, exception) {
                $("#somethingWentWrong").show();
                $("#loadingIcon").hide();
            }

        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function checkRecruiterLogin() {
    try {
        $.ajax({
            type: "GET",
            url: "/checkRecruiterSession",
            data: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterSession
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataRecruiterSession(returnedData) {
    if(returnedData == 0){
        logoutRecruiter();
    }
}

function processDataGenerateJobPostView(returnedData) {
    if(returnedData == "0"){
        logoutRecruiter();
    } else{
        $("#jobTable").show();
        newCount = 0;
        var jobPostList = returnedData;
        var parent = $('.myJobsRecruiter');
        if(Object.keys(jobPostList).length){
            jobPostList.reverse();
            jobPostList.forEach(function (jobPost) {

                var mainDiv =  document.createElement("div");
                parent.append(mainDiv);

                var outerRow = document.createElement("div");
                outerRow.className = 'row';
                outerRow.id="outerBoxMain";
                mainDiv.appendChild(outerRow);

                var colDatePost = document.createElement("div");
                colDatePost.className = 'col s12 m1 l1';
                colDatePost.style = 'margin-top:8px';
                outerRow.appendChild(colDatePost);

                var postedOn = new Date(jobPost.jobPost.jobPostCreateTimestamp);
                colDatePost.textContent = ('0' + postedOn.getDate()).slice(-2) + '-' + getMonthVal((postedOn.getMonth()+1)) + '-' + postedOn.getFullYear()

                var spanPostedOn  = document.createElement("div");
                spanPostedOn.className = "col s4 hide-on-med-and-up right-align";
                spanPostedOn.textContent= "Date Posted :";
                spanPostedOn.style = "font-weight: 600;font-size:12px";
                colDatePost.appendChild(spanPostedOn);

                var colJobPost = document.createElement("div");
                colJobPost.className = 'col s12 m2 l2';
                colJobPost.style = 'margin-top:8px';
                colJobPost.textContent = jobPost.jobPost.jobPostTitle;
                outerRow.appendChild(colJobPost);

                var spanJobTitle  = document.createElement("div");
                spanJobTitle.className = "col s4 hide-on-med-and-up right-align";
                spanJobTitle.textContent= "Title :";
                spanJobTitle.style = "font-weight: 600;font-size:12px";
                colJobPost.appendChild(spanJobTitle);

                var localities = "";
                var locationList = jobPost.jobPost.jobPostToLocalityList;

                locationList.forEach(function (locality) {
                    localities += locality.locality.localityName + ", ";
                });

                var colJobLocation = document.createElement("div");
                colJobLocation.className = 'col s12 m2 l2';
                colJobLocation.style = 'margin-top:8px';
                colJobLocation.textContent = localities.substring(0, localities.length - 2);
                outerRow.appendChild(colJobLocation);

                var spanJobLocality  = document.createElement("div");
                spanJobLocality.className = "col s4  hide-on-med-and-up right-align";
                spanJobLocality.textContent= "Location :";
                spanJobLocality.style = "font-weight: 600;font-size:12px";
                colJobLocation.appendChild(spanJobLocality);

                var colJobSalary = document.createElement("div");
                colJobSalary.className = 'col s12 m1 l2';
                colJobSalary.style = 'margin-top:8px';
                outerRow.appendChild(colJobSalary);

                if(jobPost.jobPost.jobPostMaxSalary == 0 || jobPost.jobPost.jobPostMaxSalary == null){
                    colJobSalary.textContent = "₹" + rupeeFormatSalary(jobPost.jobPost.jobPostMinSalary);
                } else{
                    colJobSalary.textContent = "₹" + rupeeFormatSalary(jobPost.jobPost.jobPostMinSalary) + " - ₹" + rupeeFormatSalary(jobPost.jobPost.jobPostMaxSalary);
                }

                var spanSalary  = document.createElement("div");
                spanSalary.className = "col s4 hide-on-med-and-up right-align";
                spanSalary.textContent= "Salary :";
                spanSalary.style = "font-weight: 600;font-size:12px";
                colJobSalary.appendChild(spanSalary);

                var colApplicant = document.createElement("div");
                colApplicant.className = 'col s12 m2 l2';
                outerRow.appendChild(colApplicant);

                var spanApplications  = document.createElement("div");
                spanApplications.className = "col s4 hide-on-med-and-up right-align";
                spanApplications.textContent= "No. of Applications :";
                spanApplications.style = "font-weight: 600;font-size:12px;";
                colApplicant.appendChild(spanApplications);

                var applicantBtn = document.createElement('a');
                applicantBtn.style = "font-weight: bold; text-decoration:none";
                colApplicant.appendChild(applicantBtn);

                var newApplication = document.createElement('div');
                newApplication.style = "margin-top: 4px";
                newApplication.className = "newCounter";
                colApplicant.appendChild(newApplication);

                var upcomingCounter = document.createElement('div');
                upcomingCounter.style = "margin-top: 4px";
                upcomingCounter.className = "newCounter";
                colApplicant.appendChild(upcomingCounter);

                var colJobStatus = document.createElement("div");
                colJobStatus.className = 'col s12 m2 l2';
                colJobStatus.style = 'margin-top: 8px';
                outerRow.appendChild(colJobStatus);

                var br = document.createElement("br");

                var spanStatus  = document.createElement("div");
                spanStatus.className = "col s4 hide-on-med-and-up right-align";
                spanStatus.textContent= "Status :";
                spanStatus.style = "font-weight: 600;font-size:12px";
                colJobStatus.appendChild(spanStatus);

                var pauseIconImg = document.createElement("img");
                pauseIconImg.src = "/assets/recruiter/img/icons/pause.svg";
                pauseIconImg.style = "cursor: pointer; text-decoration: none; margin: 4px";
                pauseIconImg.setAttribute('height', '24px');
                pauseIconImg.className = "tooltipped";
                pauseIconImg.setAttribute("data-postiton", "top");
                pauseIconImg.setAttribute("data-delay", "50");
                pauseIconImg.setAttribute("data-tooltip", "Pause Interviews");
                pauseIconImg.onclick = function () {
                    jobPostObj = jobPost.jobPost;
                    openPauseInterviewModal();
                };

                var resumeIconImg = document.createElement("img");
                resumeIconImg.src = "/assets/recruiter/img/icons/resume.svg";
                resumeIconImg.style = "cursor: pointer; text-decoration: none; margin: 4px";
                resumeIconImg.setAttribute('height', '24px');
                resumeIconImg.className = "tooltipped";
                resumeIconImg.setAttribute("data-postiton", "top");
                resumeIconImg.setAttribute("data-delay", "50");
                resumeIconImg.setAttribute("data-tooltip", "Resume Interviews");
                resumeIconImg.onclick = function () {
                    jobPostObj = jobPost.jobPost;
                    resumeJobApplication();
                };

                var stopIconImg = document.createElement("img");
                stopIconImg.src = "/assets/recruiter/img/icons/stop.svg";
                stopIconImg.style = "cursor: pointer; text-decoration: none; margin: 4px";
                stopIconImg.setAttribute('height', '24px');
                stopIconImg.className = "tooltipped";
                stopIconImg.setAttribute("data-postiton", "top");
                stopIconImg.setAttribute("data-delay", "50");
                stopIconImg.setAttribute("data-tooltip", "Close job applications");
                stopIconImg.onclick = function () {
                    jobPostObj = jobPost.jobPost;
                    stopJobApplication();
                };

                var statusName = document.createElement("div");
                colJobStatus.appendChild(statusName);

                if(jobPost.jobPost.jobPostStatus != null){
                    if(jobPost.jobPost.jobPostStatus.jobStatusId == 1){
                        statusName.textContent = "Under review";
                        statusName.style = "color: #F4A407; margin-bottom: 2px; text-align: center";
                        colJobStatus.appendChild(stopIconImg);

                        var optionName = document.createElement("span");
                        optionName.textContent = "Close Job";
                        colJobStatus.appendChild(optionName);


                    } else if(jobPost.jobPost.jobPostStatus.jobStatusId == 2){
                        statusName.textContent = "Active";
                        statusName.style = "color: #69CF37; margin-bottom: 2px; text-align: center";
                        colJobStatus.appendChild(pauseIconImg);

                        optionName = document.createElement("span");
                        optionName.textContent = "Pause Job";
                        colJobStatus.appendChild(optionName);

                        colJobStatus.appendChild(br);

                        colJobStatus.appendChild(stopIconImg);
                        optionName = document.createElement("span");
                        optionName.textContent = "Close Job";
                        colJobStatus.appendChild(optionName);

                    } else if(jobPost.jobPost.jobPostStatus.jobStatusId == 5){
                        statusName.textContent = jobPost.jobPost.jobPostStatus.jobStatusName;
                        colJobStatus.appendChild(resumeIconImg);
                        optionName = document.createElement("span");
                        optionName.textContent = "Resume Job";
                        colJobStatus.appendChild(optionName);

                        colJobStatus.appendChild(br);

                        colJobStatus.appendChild(stopIconImg);
                        optionName = document.createElement("span");
                        optionName.textContent = "Close Job";
                        colJobStatus.appendChild(optionName);

                    } else{
                        statusName.textContent = jobPost.jobPost.jobPostStatus.jobStatusName;
                    }
                } else{
                    statusName.textContent = "Not specified";
                }

                applicantBtn.textContent = jobPost.totalCount;
                if(jobPost.pendingCount > 0){
                    newApplication.textContent = " (" + jobPost.pendingCount + " Action Required)";
                }
                if(jobPost.upcomingCount > 0){
                    upcomingCounter.textContent = " (" + jobPost.upcomingCount + " Upcoming)";
                }
                newCount += jobPost.pendingCount;
                newCount += jobPost.upcomingCount;
                applicantBtn.style = 'text-align: center; font-weight: bold';
                if(jobPost.totalCount > 0){
                    applicantBtn.className = 'btn-floating btn-small waves-effect waves-light green accent-3';
                }
                else{
                    applicantBtn.className = 'btn-floating btn-small waves-effect waves-light blue-grey lighten-4';
                }
                if(jobPost.totalCount > 0){
                    applicantBtn.onclick = function () {
                        openAppliedCandidate(jobPost.jobPost.jobPostId);
                    };
                }

                var colEditView = document.createElement("div");
                colEditView.className = 'col s12 m1 l1';
                colEditView.style = "text-align:center";
                outerRow.appendChild(colEditView);

                var editViewBtn = document.createElement('button');
                editViewBtn.className = 'waves-effect waves-blue-grey lighten-5 btn-flat';
                editViewBtn.style = 'color:#1976d2; padding:0; font-size:12px;';
                editViewBtn.textContent='View/Edit';

                editViewBtn.onclick = function () {
                    openJobPost(jobPost.jobPost.jobPostId);
                };
                colEditView.appendChild(editViewBtn);

                var hr = document.createElement('hr');
                hr.style='margin:2px 1%';
                mainDiv.appendChild(hr);
            });

            $('.tooltipped').tooltip({delay: 50});
        } else{
            $("#noJobs").show();
            $("#jobTable").hide();
        }
        $("#loadingIcon").hide();
    }
}

function openJobPost(jobId) {
    window.location = "/recruiter/jobPost/" + jobId;
}

function openAppliedCandidate(jobId) {
    window.location = "/recruiter/jobApplicants/" + jobId;
}

function openPauseInterviewModal() {
    $("#jobPostName").html(jobPostObj.jobPostTitle);
    $("#pauseInterviewModal").openModal();
}

function resumeJobApplication() {
    changeJobStatus(1, null);
}

function stopJobApplication() {
    changeJobStatus(4, null);
}

function confirmPauseAction() {

    if($("#resume_date").val() == ''){
        notifyError("Please select a date ");
    } else{
        var selectedDate = new Date($("#resume_date").val());
        var todaysDate = new Date();

        if(selectedDate < todaysDate){
            notifyError("Please select a date greater than today");
        } else{
            var jobPostResumeDate = selectedDate.getFullYear() + "-" + (selectedDate.getMonth() + 1) + "-" + selectedDate.getDate();
            changeJobStatus(5, jobPostResumeDate);
        }
    }
}

function changeJobStatus(jobStatus, jobPostResumeDate) {
    try {
        var d = {
            jobPostId: jobPostObj.jobPostId,
            jobPostStatusId: jobStatus,
            resumeApplicationDate: jobPostResumeDate
        };

        $.ajax({
            type: "POST",
            url: "/recruiter/api/addJobPost",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataAddJobPost
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

}
function processDataAddJobPost(returnedData) {
    if(returnedData.status == 2 ){
        $("#pauseInterviewModal").closeModal();
        notifySuccess("Job Status updated successfully!");
        setTimeout(function(){
            window.location = "/recruiter/allRecruiterJobPosts";
        }, 2500);
    } else{
        notifyError("Something went wrong. Please try again later!");
    }
}

function closePauseModal() {
    $("#pauseInterviewModal").closeModal();
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

function logoutRecruiter() {
    try {
        $.ajax({
            type: "GET",
            url: "/logoutRecruiter",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataLogoutRecruiter
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataLogoutRecruiter() {
    window.location = "/recruiter";
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}
