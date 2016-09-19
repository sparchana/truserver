/**
 * Created by batcoder1 on 20/6/16.
 */
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
    var candidateJobApplication = returnedData;
    $("#jobCount").html(Object.keys(candidateJobApplication).length);
    if(Object.keys(candidateJobApplication).length > 0){
        candidateJobApplication.reverse();
        prePopulateJobSection(candidateJobApplication);
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
        notAppliedMsg.textContent = "Uh oh! Looks like you have not applied to any of the jobs yet!";
        centerDiv.appendChild(notAppliedMsg);
    }
}

function prePopulateJobSection(jobApplication) {
    var parent = $('#myAppliedJobs');
    var count = 0;
    jobApplication.forEach(function (jobPost){
        count++;
        if(count){
            /* get all localities of the jobApplication */
            var jobLocality = jobPost.jobPost.jobPostToLocalityList;
            var localities = "";
            var allLocalities = "";
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
            jobLogo.src = jobPost.jobPost.company.companyLogo;
            jobLogo.setAttribute('width', '80%');
            jobLogo.id = "jobLogo";
            col.appendChild(jobLogo);

            var jobBodyCol = document.createElement("div");
            jobBodyCol.className = "col-sm-10";
            jobBodyCol.id = "jobBody";
            rowDiv.appendChild(jobBodyCol);

            var titleRow = document.createElement("div");
            titleRow.className = "row";
            jobBodyCol.appendChild(titleRow);

            var titleRowOne = document.createElement("div");
            titleRowOne.className = "col-sm-9";
            titleRow.appendChild(titleRowOne);

            var jobTitle = document.createElement("h4");
            jobTitle.textContent = jobPost.jobPost.jobPostTitle + " | " + jobPost.jobPost.company.companyName;
            titleRowOne.appendChild(jobTitle);

            var titleRowTwo = document.createElement("div");
            titleRowTwo.className = "col-sm-3";
            titleRowTwo.id = "appliedOnId";
            titleRow.appendChild(titleRowTwo);

            var fetchedAppliedDate = jobPost.jobApplicationCreateTimeStamp;

            var divAppliedDate = document.createElement("div");
            divAppliedDate.id = "appliedDate";
            divAppliedDate.textContent = "Applied on: " + new Date(fetchedAppliedDate).getDate() + "/" + (new Date(fetchedAppliedDate).getMonth()+1) + "/" + new Date(fetchedAppliedDate).getFullYear();
            titleRowTwo.appendChild(divAppliedDate);

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
            if(jobPost.jobPost.jobPostMaxSalary == "0"){
                salaryDiv.textContent = jobPost.jobPost.jobPostMinSalary + " monthly";
            } else{
                salaryDiv.textContent = jobPost.jobPost.jobPostMinSalary + " - " + jobPost.jobPost.jobPostMaxSalary + " monthly";
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
            expDiv.textContent = "Exp: " + jobPost.jobPost.jobPostExperience.experienceType;
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

            if(((jobLocality.length) - 2) > 0 ){
                var tooltip = document.createElement("a");
                tooltip.id = "locationMsg_" + jobPost.jobPost.jobPostId;
                tooltip.title = allLocalities;
                tooltip.style = "color: #2980b9";
                tooltip.textContent = " more";
                jobBodySubRowColLoc.appendChild(tooltip);
            }
            $("#locationMsg_" + jobPost.jobPost.jobPostId).attr("data-toggle", "tooltip");

        }
    });
}
