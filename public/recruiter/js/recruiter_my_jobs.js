/**
 * Created by hawk on 21/10/16.
 */
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
            success: processDataGenerateJobPostView
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
        var parent = $('.myJobsRecruiter');

        if(Object.keys(returnedData).length){
            returnedData.forEach(function (jobPost) {
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

                var postedOn = new Date(jobPost.jobPostCreateTimestamp);
                colDatePost.textContent = ('0' + postedOn.getDate()).slice(-2) + '-' + getMonthVal((postedOn.getMonth()+1)) + '-' + postedOn.getFullYear()

                var spanPostedOn  = document.createElement("div");
                spanPostedOn.className = "col s4 hide-on-med-and-up right-align";
                spanPostedOn.textContent= "Date Posted :";
                spanPostedOn.style = "font-weight: 600;font-size:12px";
                colDatePost.appendChild(spanPostedOn);

                var colJobPost = document.createElement("div");
                colJobPost.className = 'col s12 m2 l2';
                colJobPost.style = 'margin-top:8px';
                colJobPost.textContent = jobPost.jobPostTitle;
                outerRow.appendChild(colJobPost);

                var spanJobTitle  = document.createElement("div");
                spanJobTitle.className = "col s4 hide-on-med-and-up right-align";
                spanJobTitle.textContent= "Title :";
                spanJobTitle.style = "font-weight: 600;font-size:12px";
                colJobPost.appendChild(spanJobTitle);

                var localities = "";
                var locationList = jobPost.jobPostToLocalityList;

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

                if(jobPost.jobPostMaxSalary != 0 || jobPost.jobPostMaxSalary != null){
                    colJobSalary.textContent = "₹" + rupeeFormatSalary(jobPost.jobPostMinSalary);
                } else if(jobPost.jobPostMinSalary != 0 || jobPost.jobPostMinSalary != null) {
                    colJobSalary.textContent = "₹" + rupeeFormatSalary(jobPost.jobPostMaxSalary);
                } else{
                    colJobSalary.textContent = "₹" + rupeeFormatSalary(jobPost.jobPostMinSalary) + " - ₹" + rupeeFormatSalary(jobPost.jobPostMaxSalary);
                }

                var spanSalary  = document.createElement("div");
                spanSalary.className = "col s4 hide-on-med-and-up right-align";
                spanSalary.textContent= "Salary :";
                spanSalary.style = "font-weight: 600;font-size:12px";
                colJobSalary.appendChild(spanSalary);

                var colJobWorkShift = document.createElement("div");
                colJobWorkShift.className = 'col s12 m2 l2';
                colJobWorkShift.style = 'margin-top:8px';
                outerRow.appendChild(colJobWorkShift);

                if(jobPost.jobPostShift != null){
                    colJobWorkShift.textContent = jobPost.jobPostShift.timeShiftName;
                } else{
                    colJobWorkShift.textContent = "Not Specified";
                }

                var spanShift  = document.createElement("div");
                spanShift.className = "col s4 hide-on-med-and-up right-align";
                spanShift.textContent= "Time Shift :";
                spanShift.style = "font-weight: 600;font-size:12px";
                colJobWorkShift.appendChild(spanShift);

                var colApplicant = document.createElement("div");
                colApplicant.className = 'col s12 m1 l1';
                outerRow.appendChild(colApplicant);

                var spanApplications  = document.createElement("div");
                spanApplications.className = "col s4 hide-on-med-and-up right-align";
                spanApplications.textContent= "No. of Applications :";
                spanApplications.style = "font-weight: 600;font-size:12px;";
                colApplicant.appendChild(spanApplications);

                var applicantBtn = document.createElement('a');
                applicantBtn.style = "font-weight:600;text-decoration:none";
                colApplicant.appendChild(applicantBtn);

                var colJobStatus = document.createElement("div");
                colJobStatus.className = 'col s12 m1 l1';
                colJobStatus.style = 'margin-top:8px';
                outerRow.appendChild(colJobStatus);

                var spanStatus  = document.createElement("div");
                spanStatus.className = "col s4 hide-on-med-and-up right-align";
                spanStatus.textContent= "Status :";
                spanStatus.style = "font-weight: 600;font-size:12px";
                colJobStatus.appendChild(spanStatus);

                if(jobPost.jobPostStatus != null){
                    if(jobPost.jobPostStatus.jobStatusId == 1){
                        colJobStatus.textContent = "Under review";
                        colJobStatus.style = "color: #F4A407;margin-top:8px;text-align:center";
                    } else if(jobPost.jobPostStatus.jobStatusId == 2){
                        colJobStatus.textContent = "Active";
                        colJobStatus.style = "color: #69CF37;margin-top:8px;text-align:center";
                    } else{
                        colJobStatus.textContent = jobPost.jobPostStatus.jobStatusName;
                    }
                } else{
                    colJobStatus.textContent = "Not specified";
                }
                var colEditView = document.createElement("div");
                colEditView.className = 'col s12 m1 l1';
                colEditView.style = "text-align:center";
                outerRow.appendChild(colEditView);

                var editViewBtn = document.createElement('button');
                editViewBtn.className = 'waves-effect waves-blue-grey lighten-5 btn-flat';
                editViewBtn.style = 'color:#1976d2;padding:0;font-size:12px;';
                editViewBtn.textContent='View/Edit';

                editViewBtn.onclick = function () {
                    openJobPost(jobPost.jobPostId);
                };
                colEditView.appendChild(editViewBtn);

                var hr = document.createElement('hr');
                hr.style='margin:2px 1%';
                mainDiv.appendChild(hr);

                try {
                    $.ajax({
                        type: "POST",
                        url: "/getAllJobApplicants/" + jobPost.jobPostId,
                        data: false,
                        async: false,
                        contentType: false,
                        processData: false,
                        success: function(data) {
                            applicantBtn.textContent = Object.keys(data).length;
                            applicantBtn.style = 'text-align:center';
                            if(Object.keys(data).length > 0){
                                applicantBtn.className = 'btn-floating btn-small waves-effect waves-light green accent-3';
                            }
                            else{
                                applicantBtn.className = 'btn-floating btn-small waves-effect waves-light blue-grey lighten-4';
                            }
                            if(Object.keys(data).length > 0){
                                applicantBtn.onclick = function () {
                                    openAppliedCandidate(jobPost.jobPostId);
                                };
                            }
                        }
                    });
                } catch (exception) {
                    console.log("exception occured!!" + exception);
                }
            });
        } else{
            $("#noJobs").show();
            $("#jobTable").hide();
        }
    }

}

function openJobPost(jobId) {
    window.location = "/recruiter/jobPost/" + jobId;
}

function openAppliedCandidate(jobId) {
    window.location = "/recruiter/jobApplicants/" + jobId;
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