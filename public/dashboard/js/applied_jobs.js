/**
 * Created by batcoder1 on 20/6/16.
 */

$(document).ready(function(){
    checkUserLogin();
    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateJobApplication",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataAndFetchAppliedJobs
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataAndFetchAppliedJobs(returnedData) {
    /* Check min profile */
    if(localStorage.getItem("minProfile") == 0){
        var profileStatusParent = document.getElementById("profileStatusParentMyJobs");
        profileStatusParent.addEventListener("click", completeProfile);
        profileStatusParent.style = "cursor: pointer";
        document.getElementById("profileStatusResultMyJobs").innerHTML = '<font color="#F26522">Incomplete</font>';
        document.getElementById("profileStatusActionMyJobs").innerHTML = '<font color="#F26522">(Complete Now)</font>';
        $("#profileStatusIconMyJobs").attr('src', '/assets/dashboard/img/wrong.png');
        $("#incompleteProfileMsg").show();
    } else{
        document.getElementById("profileStatusResultMyJobs").innerHTML = '<font color="#46AB49">Complete</font>';
        document.getElementById("profileStatusActionMyJobs").innerHTML = '-';
        $("#profileStatusIconMyJobs").attr('src', '/assets/dashboard/img/right.png');
        $("#incompleteProfileMsg").hide();
    }

    /* check assessment */
    if(localStorage.getItem("assessed") == 0){
        var assessmentStatusParent = document.getElementById("assessmentStatusParentMyJobs");
        assessmentStatusParent.addEventListener("click", completeAssessment);
        assessmentStatusParent.style = "cursor: pointer";
        document.getElementById("assessmentStatusResultMyJobs").innerHTML = '<font color="#F26522">Incomplete</font>';
        document.getElementById("assessmentStatusActionMyJobs").innerHTML = '<font color="#F26522">(Take Assessment)</font>';
        $("#assessmentStatusIconMyJobs").attr('src', '/assets/dashboard/img/wrong.png');
    } else{
        document.getElementById("assessmentStatusResultMyJobs").innerHTML = '<font color="#46AB49">Complete</font>';
        document.getElementById("assessmentStatusActionMyJobs").innerHTML = '-';
        $("#assessmentStatusIconMyJobs").attr('src', '/assets/dashboard/img/right.png');
    }

    var candidateJobApplication = returnedData;
    if(Object.keys(candidateJobApplication).length > 0){
        candidateJobApplication.forEach(function (jobApplication) {
            prePopulateJobSection(jobApplication);
        });
    } else{
        var parent = $('#myAppliedJobs');
        var centerDiv = document.createElement("center");
        parent.append(centerDiv);
        var notAppliedImg = document.createElement("img");
        notAppliedImg.style = "width: 80px; margin-top: 6%";
        notAppliedImg.src = "/assets/dashboard/img/sadFace.png";
        centerDiv.appendChild(notAppliedImg);

        var notAppliedMsg = document.createElement("div");
        notAppliedMsg.style = "padding: 16px";
        notAppliedMsg.textContent = "Uh oh! Looks like you have not applied any of the job.";
        centerDiv.appendChild(notAppliedMsg);
    }
}

function prePopulateJobSection(jobApplication) {
    var parent = $('#myAppliedJobs');

    /*create the cardView*/
    var divCard = document.createElement("div");
    divCard.id = "customCard";
    parent.append(divCard);

    /*Job Title*/
    var divTitle = document.createElement("div");
    divTitle.id = "cardTitle";

    /* bootstrap column */
    var titleRow = document.createElement("div");
    titleRow.className = "row";

    var titleRowOne = document.createElement("div");
    titleRowOne.className = "col-sm-9";

    var divTitleText = document.createElement("div");
    divTitleText.textContent = jobApplication.jobPost.company.companyName + " | " + jobApplication.jobPost.jobPostTitle ;

    var titleRowTwo = document.createElement("div");
    titleRowTwo.className = "col-sm-3";

    var fetchedAppliedDate = jobApplication.jobApplicationCreateTimeStamp;

    var divAppliedDate = document.createElement("div");
    divAppliedDate.style = "font-size: 14px; margin-top: 4px; float: right";
    divAppliedDate.textContent = "Applied on: " + new Date(fetchedAppliedDate).getDay() + "/" + new Date(fetchedAppliedDate).getMonth() + "/" + new Date(fetchedAppliedDate).getFullYear();

    titleRowOne.appendChild(divTitleText);
    titleRowTwo.appendChild(divAppliedDate);
    titleRow.appendChild(titleRowOne);
    titleRow.appendChild(titleRowTwo);
    divTitle.appendChild(titleRow);

    divCard.appendChild(divTitle);

    /*Job Details view*/
    var divJobDetail = document.createElement("div");
    divJobDetail.className = "jobDetails";
    divJobDetail.style = "margin-top: 20px";
    divJobDetail.id = "jd_" + jobApplication.jobPost.jobPostId;

    /* job details section */
    var jobDetailSalaryRow = document.createElement("div");
    /*Min Salary*/
    jobDetailSalaryRow.className = "row";
    jobDetailSalaryRow.id = "jobSubSection";
    divJobDetail.appendChild(jobDetailSalaryRow);

    var divMinSalaryCol = document.createElement("div");
    divMinSalaryCol.className = "col-lg-4";
    divMinSalaryCol.textContent = "Salary: ₹" + jobApplication.jobPost.jobPostMinSalary + " - ₹" + jobApplication.jobPost.jobPostMaxSalary + " monthly";
    jobDetailSalaryRow.appendChild(divMinSalaryCol);

    var divExpCol = document.createElement("div");
    divExpCol.className = "col-lg-3";
    divExpCol.textContent = "Experience Req: " + jobApplication.jobPost.jobPostExperience.experienceType;
    jobDetailSalaryRow.appendChild(divExpCol);

    var divLocCol = document.createElement("div");
    divLocCol.className = "col-lg-5";
    var localityList = jobApplication.jobPost.jobPostToLocalityList;
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
    divLocCol.textContent = "Job Location: " + localities;
    jobDetailSalaryRow.appendChild(divLocCol);


    divCard.appendChild(divJobDetail);
}

function completeAssessment() {
    window.open("http://bit.ly/trujobstest");
}

function completeProfile() {
    window.open("/dashboard/editProfile");
}