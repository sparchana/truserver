/**
 * Created by batcoder1 on 20/6/16.
 */

$(document).ready(function(){
    var userMobile = localStorage.getItem("mobile");
    var userName = localStorage.getItem("name");
    var userLastName = localStorage.getItem("lastName");
    console.log("userName: " + userName + " mobile:" + userMobile);

    if(userMobile != null){
        document.getElementById("helloMsg").innerHTML = "Hello " + userName + "!";
        try{
            if(userLastName == "null" || userLastName == null){
                document.getElementById("userName").innerHTML = userName;
            } else{
                document.getElementById("userName").innerHTML = userName + " " + userLastName;
            }
            document.getElementById("userMobile").innerHTML = userMobile;
        } catch(err){
        }

        $('#userExist').show();
    }
    else{
        logoutUser();
        window.location = "/new";
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateInfo/" + localStorage.getItem("leadId"),
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
    var candidateJobApplication = returnedData.jobApplicationList;
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
    divCard.className = "demo-card-wide mdl-card mdl-shadow--2dp";
    parent.append(divCard);

    /*Job Title*/
    var divTitle = document.createElement("div");
    divTitle.className = "mdl-card__title";
    var divTitleText = document.createElement("div");
    divTitleText.style = "display: inline-block";
    divTitleText.className = "mdl-card__title-text";
    /*Job Apply date*/
    var returnedDate = jobApplication.jobApplicationCreateTimeStamp;
    var appliedDate = new Date(returnedDate).getDate() + "/" + new Date(returnedDate).getMonth() + "/" + new Date(returnedDate).getFullYear();
    divTitleText.textContent = jobApplication.jobPost.jobPostTitle + " ";
    var divAppliedOn = document.createElement("div");
    divAppliedOn.style = "display: inline-block;font-size: 14px";
    divAppliedOn.className = "mdl-card__title-text";
    divAppliedOn.textContent = " | Applied on: " + appliedDate;
    divCard.appendChild(divTitle);
    divTitle.appendChild(divTitleText);
    divTitle.appendChild(divAppliedOn);

    /*Job Description*/
    var divDescription = document.createElement("div");
    divDescription.className = "mdl-card__supporting-text";
    divDescription.textContent = jobApplication.jobPost.jobDescription;
    divCard.appendChild(divDescription);

    /*Job Details view*/
    var divJobDetail = document.createElement("div");
    divJobDetail.className = "jobDetails";
    divJobDetail.id = "jd_" + jobApplication.jobPost.jobPostId;
    divJobDetail.style = "display: none";

    /* job details section */
    var jobDetailSalaryRow = document.createElement("div");
    /*Min Salary*/
    jobDetailSalaryRow.className = "row";
    jobDetailSalaryRow.id = "jobSubSection";
    divJobDetail.appendChild(jobDetailSalaryRow);
    var divMinSalaryCol = document.createElement("div");
    divMinSalaryCol.className = "col-lg-6";
    divMinSalaryCol.textContent = "Minimum Salary: ₹" + jobApplication.jobPost.jobPostMinSalary;
    jobDetailSalaryRow.appendChild(divMinSalaryCol);
    /*Max Salary*/
    var divMaxSalaryCol = document.createElement("div");
    divMaxSalaryCol.className = "col-lg-6";
    divMaxSalaryCol.textContent = "Maximum Salary: ₹" + jobApplication.jobPost.jobPostMaxSalary;
    jobDetailSalaryRow.appendChild(divMaxSalaryCol);

    var jobDetailJobTimeRow = document.createElement("div");
    jobDetailJobTimeRow.className = "row";
    jobDetailJobTimeRow.id = "jobSubSection";
    divJobDetail.appendChild(jobDetailJobTimeRow);
    /*Job start time*/
    var divStartTimeCol = document.createElement("div");
    divStartTimeCol.className = "col-lg-6";

    var time = jobApplication.jobPost.jobPostStartTime;
    var startTime = new Date(time).getHours();
    time = jobApplication.jobPost.jobPostEndTime;
    var endTime = new Date(time).getHours();
    divStartTimeCol.textContent = "Job Start Time: " + startTime;
    jobDetailJobTimeRow.appendChild(divStartTimeCol);
    /*Job End time*/
    var divEndTimeCol = document.createElement("div");
    divEndTimeCol.className = "col-lg-6";
    divEndTimeCol.textContent = "Job End Time: " + endTime;
    jobDetailJobTimeRow.appendChild(divEndTimeCol);

    var jobDetailIncentiveARow = document.createElement("div");
    jobDetailIncentiveARow.className = "row";
    jobDetailIncentiveARow.id = "jobSubSection";
    divJobDetail.appendChild(jobDetailIncentiveARow);
    /*Insurance covered?*/
    var divInsuranceCol = document.createElement("div");
    divInsuranceCol.className = "col-lg-6";
    var val = jobApplication.jobPost.jobPostBenefitInsurance;
    if(val == "true"){
        divInsuranceCol.textContent = "Insurance Covered? Yes" ;
    } else{
        divInsuranceCol.textContent = "Insurance Covered? No";
    }

    jobDetailIncentiveARow.appendChild(divInsuranceCol);
    /*Work from home?*/
    var divWorkFromHomeCol = document.createElement("div");
    val = jobApplication.jobPost.jobWorkFromHome;
    divWorkFromHomeCol.className = "col-lg-6";
    if(val == "true"){
        divWorkFromHomeCol.textContent = "Work from home? yes" + jobApplication.jobPost.jobWorkFromHome;
    } else{
        divWorkFromHomeCol.textContent = "Work from home? yes" + jobApplication.jobPost.jobWorkFromHome;
    }
    jobDetailIncentiveARow.appendChild(divWorkFromHomeCol);

    var jobDetailIncentiveBRow = document.createElement("div");
    jobDetailIncentiveBRow.className = "row";
    jobDetailIncentiveBRow.id = "jobSubSection";
    divJobDetail.appendChild(jobDetailIncentiveBRow);
    /*PF covered?*/
    var divPfCol = document.createElement("div");
    divPfCol.className = "col-lg-6";
    val = jobApplication.jobPost.jobPostBenefitPF;
    if(val == "true"){
        divPfCol.textContent = "PF Covered? Yes";
    } else{
        divPfCol.textContent = "PF Covered? No";
    }
    divPfCol.textContent = "PF Covered? " + jobApplication.jobPost.jobPostBenefitPF;
    jobDetailIncentiveBRow.appendChild(divPfCol);
    /*Fuel covered?*/
    var divFuelCol = document.createElement("div");
    divFuelCol.className = "col-lg-6";
    val = jobApplication.jobPost.jobPostBenefitFuel;
    if(val == "true"){
        divFuelCol.textContent = "Fuel Covered? Yes";
    } else{
        divFuelCol.textContent = "Fuel Covered? No";
    }
    jobDetailIncentiveBRow.appendChild(divFuelCol);

    var jobDetailVacancyRow = document.createElement("div");
    jobDetailVacancyRow.className = "row";
    jobDetailVacancyRow.id = "jobSubSection";
    divJobDetail.appendChild(jobDetailVacancyRow);
    /*Total vacancies*/
    var divVacancyCol = document.createElement("div");
    divVacancyCol.className = "col-lg-6";
    divVacancyCol.textContent = "Total Vacancy(s): " + jobApplication.jobPost.jobPostVacancy;
    jobDetailVacancyRow.appendChild(divVacancyCol);
    /*Job Shift*/
    var divShiftCol = document.createElement("div");
    divShiftCol.className = "col-lg-6";
    divShiftCol.textContent = "Job Time Shift: " + jobApplication.jobPost.jobPostShift.timeShiftName;
    jobDetailVacancyRow.appendChild(divShiftCol);

    var jobDetailExpRow = document.createElement("div");
    jobDetailExpRow.className = "row";
    jobDetailExpRow.id = "jobSubSection";
    divJobDetail.appendChild(jobDetailExpRow);
    /*Experience required*/
    var divExpCol = document.createElement("div");
    divExpCol.className = "col-lg-6";
    divExpCol.textContent = "Minimum Experience Required: " + jobApplication.jobPost.jobPostExperience.experienceType;
    jobDetailExpRow.appendChild(divExpCol);
    /*Education required*/
    var divEduCol = document.createElement("div");
    divEduCol.className = "col-lg-6";
    divEduCol.textContent = "Minimum Education Required: " + jobApplication.jobPost.jobPostEducation.educationName;
    jobDetailExpRow.appendChild(divEduCol);

    divCard.appendChild(divJobDetail);
    /* "more" btn section */
    var divBorder = document.createElement("div");
    divBorder.className = "mdl-card__actions mdl-card--border";
    var divMoreBtn = document.createElement("a");
    divMoreBtn.className = "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect";
    divMoreBtn.textContent = "Job Details";
    divMoreBtn.onclick = function () {
        console.log("jd_" + jobApplication.jobPost.jobPostId);
        $("#jd_" + jobApplication.jobPost.jobPostId).slideToggle();
    };
    divCard.appendChild(divBorder);
    divBorder.appendChild(divMoreBtn);

    /*share button*/
    var divMenu = document.createElement("div");
    divMenu.className = "mdl-card__menu";
    var divShareBtn = document.createElement("button");
    divShareBtn.className = "mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect";
    var divShareBtnIcon = document.createElement("i");
    divShareBtnIcon.className = "material-icons";
    divShareBtnIcon.textContent = "share";
    divCard.appendChild(divMenu);
    divMenu.appendChild(divShareBtn);
    divShareBtn.appendChild(divShareBtnIcon);
}