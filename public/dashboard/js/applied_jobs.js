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
    if(localStorage.getItem("minProfile") == 0){
        $("#profileIncomplete").show();
    } else{
        $("#profileIncomplete").hide();
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
    var divTitleText = document.createElement("div");
    divTitleText.style = "display: inline-block";
    divTitleText.textContent = jobApplication.jobPost.company.companyName + " | " + jobApplication.jobPost.jobPostTitle ;

    divCard.appendChild(divTitle);
    divTitle.appendChild(divTitleText);

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