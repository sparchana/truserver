/**
 * Created by hawk on 21/10/16.
 */

var jobPostObj;

$(document).scroll(function(){
    if ($(this).scrollTop() > 30) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});

$(document).ready(function(){
    $("#loadingIcon").show();
    try{
        var table = $("table#postedJobTable").DataTable({
            "ajax": {
               "type":"POST",
                "url":"/getAllRecruiterJobPosts",
                "dataSrc": function (returnedData) {
                        $("#loadingIcon").hide();
                        if(returnedData == "0"){
                            console.log("Data not receive");
                            logoutRecruiter();
                        } else{
                            var postedJobList = returnedData;
                            var returned_data  = new Array();
                            if(postedJobList.length > 0){
                                $("#postedJobTable").show();
                                postedJobList.forEach(function (jobPost) {
                                    returned_data.push({
                                        'datePosted': function() {
                                            var postedOn = new Date(jobPost.jobPost.jobPostCreateTimestamp);
                                            var dateValue = ('0' + postedOn.getDate()).slice(-2) + '-' + getMonthVal((postedOn.getMonth() + 1)) + '-' + postedOn.getFullYear()
                                            return '<div class="mLabel" style="width:100%" >' + dateValue + '</div>'
                                        },
                                        'jobTitle':'<div class="mLabel" style="width:100%">'+jobPost.jobPost.jobPostTitle+' </div>',
                                        'recruiterName':function () {
                                            if(jobPost.jobPost.recruiterProfile !=null){
                                                return '<div class="mLabel" style="width:100%">'+ jobPost.jobPost.recruiterProfile.recruiterProfileName +' </div>'
                                            }else{
                                                return '<div class="mLabel" style="width:100%"> Recruiter </div>'
                                            }
                                        },
                                        'location': function () {
                                            var localities = "";
                                            var locationList = jobPost.jobPost.jobPostToLocalityList;

                                            locationList.forEach(function (locality) {
                                                localities += locality.locality.localityName + ", ";
                                            });

                                            return '<div class="mLabel" style="width:100%">'+localities.substring(0, localities.length - 2)+'</div>';
                                        },
                                        'salary': function () {

                                            if(jobPost.jobPost.jobPostMaxSalary == 0 || jobPost.jobPost.jobPostMaxSalary == null){

                                                return '<div class="mLabel" style="width:100%"> ₹ '+ rupeeFormatSalary(jobPost.jobPost.jobPostMinSalary)+'</div>'
                                                ;
                                            } else{
                                                return '<div class="mLabel" style="width:100%"> ₹ '+ rupeeFormatSalary(jobPost.jobPost.jobPostMinSalary) + ' - ₹ ' + rupeeFormatSalary(jobPost.jobPost.jobPostMaxSalary)+'</div>';
                                            }
                                        },
                                        'track': function () {
                                            if (jobPost.pendingCount > 0 && jobPost.upcomingCount > 0) {

                                                return '<button type="button" class="mBtn" style="width: 94%" onclick="openJobPosttrackView(' + jobPost.jobPost.jobPostId + ')" id="viewCandidateBtn" >Applications</button>'+
                                                    '<div style="width:100%;font-size:11px">' + jobPost.upcomingCount + ' Upcoming</div>'+
                                                    '<div style="width:100%;font-size:11px">' + jobPost.pendingCount + ' Action Required</div>';

                                            } else {
                                                if(jobPost.upcomingCount > 0 && jobPost.pendingCount == 0 ){

                                                    return '<button type="button" class="mBtn" style="width: 94%" onclick="openJobPosttrackView(' + jobPost.jobPost.jobPostId + ')" id="viewCandidateBtn" >Applications</button>'+
                                                        '<div style="width:100%;font-size:11px">' + jobPost.upcomingCount + ' Upcoming</div>';

                                                } else if(jobPost.upcomingCount == 0 && jobPost.pendingCount > 0 ){

                                                    return '<button type="button" class="mBtn" style="width: 94%" onclick="openJobPosttrackView(' + jobPost.jobPost.jobPostId + ')" id="viewCandidateBtn" >Applications</button>'+
                                                        '<div style="width:100%;font-size:11px">' + jobPost.pendingCount + ' Action Required</div>';

                                                } else{
                                                    return '<button type="button" class="mBtn" style="width: 94%" onclick="openJobPosttrackView(' + jobPost.jobPost.jobPostId + ')" id="viewCandidateBtn" >Applications</button>';
                                                }
                                            }
                                        },
                                        'status': function () {
                                             jobPostApplicationStatus(jobPost);
                                            return '<div id="jobPostStatusDiv_'+ jobPost.jobPost.jobPostId +'" style="text-align: center"></div>'
                                            },
                                        'editJob':'<button type="button" class="mBtn" style="width: 94%" onclick="openJobPost(' + jobPost.jobPost.jobPostId + ')" id="viewCandidateBtn" >Edit</button>',
                                        'candidates':'<button type="button" class="mBtn" style="width: 94%" onclick="openCandidateView('+jobPost.jobPost.jobPostId+')" id="viewCandidateBtn" >Find</button>'
                                    })
                                });
                            }else{
                                $("#noJobs").show();
                            }
                            return returned_data
                        }
                    }
                },
            "deferRender":true,
            "columns": [
                { "data": "datePosted" },
                { "data": "jobTitle" },
                { "data": "recruiterName" },
                { "data": "location" },
                { "data": "salary" },
                { "data": "track" },
                { "data": "status" },
                { "data": "editJob" },
                { "data": "candidates" },
            ],
            "order": [[2, "desc"]],
            responsive: true,
            "destroy": true,
            "dom":'Bfrtip',
            "buttons": [
                'copy','csv','excel'
            ]
        });
    } catch (exception){
        console.log("exception occured !!" + exception);
    }
});

function jobPostApplicationStatus(jobPost) {
    var parent = $("#jobPostStatusDiv_"+jobPost.jobPost.jobPostId);
    parent.html("");

    var colJobStatus = document.createElement("div");
    parent.append(colJobStatus);

    var br = document.createElement("br");

    var pauseDiv = document.createElement("div");
    pauseDiv.style = "display: inline-block; margin: 0 2px 0 2px";

    var pauseIconImg = document.createElement("img");
    pauseIconImg.id = jobPost.jobPost.jobPostId + "_pause";
    pauseIconImg.src = "/assets/recruiter/img/icons/pause.svg";
    pauseIconImg.style = "cursor: pointer; text-decoration: none; margin: 4px";
    pauseIconImg.setAttribute('height', '18px');
    pauseIconImg.className = "tooltipped";
    pauseIconImg.setAttribute("data-postiton", "top");
    pauseIconImg.setAttribute("data-delay", "50");
    pauseIconImg.setAttribute("data-tooltip", "Pause Interviews");
    pauseIconImg.onclick = function () {
        jobPostObj = jobPost.jobPost;
        openPauseInterviewModal();
    };

    pauseDiv.appendChild(pauseIconImg);
    pauseDiv.appendChild(br);

    var optionName = document.createElement("span");
    optionName.style = "font-size: 11px;";
    optionName.textContent = "Pause";
    pauseDiv.appendChild(optionName);

    var resumeDiv = document.createElement("div");
    resumeDiv.style = "display: inline-block; margin: 0 2px 0 2px";

    var resumeIconImg = document.createElement("img");
    resumeIconImg.src = "/assets/recruiter/img/icons/resume.svg";
    resumeIconImg.id = jobPost.jobPost.jobPostId + "_resume";
    resumeIconImg.style = "cursor: pointer; text-decoration: none; margin: 4px";
    resumeIconImg.setAttribute('height', '18px');
    resumeIconImg.className = "tooltipped";
    resumeIconImg.setAttribute("data-postiton", "top");
    resumeIconImg.setAttribute("data-delay", "50");
    resumeIconImg.setAttribute("data-tooltip", "Resume Interviews");
    resumeIconImg.onclick = function () {
        jobPostObj = jobPost.jobPost;
        resumeJobApplication();
    };

    resumeDiv.appendChild(resumeIconImg);
    br = document.createElement("br");
    resumeDiv.appendChild(br);

    optionName = document.createElement("span");
    optionName.style = "font-size: 11px";
    optionName.textContent = "Resume";
    resumeDiv.appendChild(optionName);


    var stopDiv = document.createElement("div");
    stopDiv.style = "display: inline-block; margin: 0 2px 0 2px";

    var stopIconImg = document.createElement("img");
    stopIconImg.src = "/assets/recruiter/img/icons/stop.svg";
    stopIconImg.id = jobPost.jobPost.jobPostId + "_stop";
    stopIconImg.style = "cursor: pointer; text-decoration: none; margin: 4px";
    stopIconImg.setAttribute('height', '18px');
    stopIconImg.className = "tooltipped";
    stopIconImg.setAttribute("data-postiton", "top");
    stopIconImg.setAttribute("data-delay", "50");
    stopIconImg.setAttribute("data-tooltip", "Close job applications");
    stopIconImg.onclick = function () {
        jobPostObj = jobPost.jobPost;
        stopJobApplication();
    };


    stopDiv.appendChild(stopIconImg);
    br = document.createElement("br");
    stopDiv.appendChild(br);

    optionName = document.createElement("span");
    optionName.style = "font-size: 11px";
    optionName.textContent = "Close";
    stopDiv.appendChild(optionName);

    var statusName = document.createElement("div");
    colJobStatus.appendChild(statusName);

    if(jobPost.jobPost.jobPostStatus != null){
        if(jobPost.jobPost.jobPostStatus.jobStatusId == JOB_STATUS_NEW){
            statusName.textContent = "Under review";
            statusName.style = "color: orange; margin-bottom: 2px; text-align: center;font-weight:bold";

            colJobStatus.appendChild(stopDiv);

        } else if(jobPost.jobPost.jobPostStatus.jobStatusId == JOB_STATUS_ACTIVE){
            statusName.textContent = "Active";
            statusName.style = "color: #69CF37; margin-bottom: 2px; text-align: center;font-weight:bold";

            colJobStatus.appendChild(pauseDiv);
            colJobStatus.appendChild(stopDiv);

        } else if(jobPost.jobPost.jobPostStatus.jobStatusId == JOB_STATUS_PAUSED){
            statusName.textContent = "Paused";
            statusName.style = "color: orange; margin-bottom: 2px; text-align: center;font-weight:bold";

            colJobStatus.appendChild(resumeDiv);
            colJobStatus.appendChild(stopDiv);

        } else if(jobPost.jobPost.jobPostStatus.jobStatusId == JOB_STATUS_CLOSED){
            statusName.textContent = "Closed";
            statusName.style = "color: red; margin-bottom: 2px; text-align: center;font-weight:bold";

        } else{
            statusName.textContent = jobPost.jobPost.jobPostStatus.jobStatusName;
        }
    } else{
        statusName.textContent = "Not specified";
    }
}

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

function openJobPost(jobId) {
    window.location = "/recruiter/jobPost/" + jobId;
}
function openCandidateView(jpId) {
    window.location = "/recruiter/candidateSearch?jpId="+jpId;
}
function openJobPosttrackView(jpId) {
    window.location = "/recruiter/jobPostTrack/" + jpId;
}

function openAppliedCandidate(jobId) {
    window.location = "/recruiter/jobApplicants/" + jobId;
}

function openPauseInterviewModal() {
    $("#jobPostName").html(jobPostObj.jobPostTitle);
    $("#pauseInterviewModal").openModal();
}

function resumeJobApplication() {
    document.getElementById(jobPostObj.jobPostId + '_resume').style.pointerEvents = 'none';

    jobPostObj.jobPostStatus.jobStatusId = JOB_STATUS_ACTIVE;
    changeJobStatus(JOB_STATUS_NEW, null);
}

function stopJobApplication() {
    document.getElementById(jobPostObj.jobPostId + '_stop').style.pointerEvents = 'none';

    jobPostObj.jobPostStatus.jobStatusId = JOB_STATUS_CLOSED;
    changeJobStatus(JOB_STATUS_CLOSED, null);
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
            document.getElementById(jobPostObj.jobPostId + '_pause').style.pointerEvents = 'none';
            jobPostObj.jobPostStatus.jobStatusId = JOB_STATUS_PAUSED;
            changeJobStatus(JOB_STATUS_PAUSED, jobPostResumeDate);
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
        var customObj = {
            jobPost: jobPostObj
        };
        jobPostApplicationStatus(customObj);
        notifySuccess("Job Status updated successfully!");
    } else{
        notifyError("Something went wrong. Please try again later!");
    }
/*
    setTimeout(function(){
        window.location = "/recruiter/allRecruiterJobPosts";
    }, 2500);
*/
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
