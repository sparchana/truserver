/**
 *
 * Created by zero on 28/12/16.
 *
 * Detailed Job Card View
 *
 * @Dependency: has
 *      externald dependency on bootstrap.js
 *      internal dependency on validation.js,
 * @placeHolder : '<div id="detailHotJobs> </div>', add this div wherever the job post need to be displayed
 *
 * @param: pass a Array<JobPost> to the exposed methods and, it will render job card
 *
 *
 * */

var jc = (function () {
    'use strict';
    var jc= {
        allJobPost: [],
        page: 1,
        isPaginationEnabled: false,

        init: function () {

            var _parent = $("#detailHotJobs");
            var contianer = $('' +
            +'<center id="jobLoaderDiv" style="position: relative; text-align: center; width: 100%">'
            +'<h4>Loading Jobs</h4>'
            +'<svg viewBox="0 0 32 32" width="32" height="32">'
            +'<circle id="loadingSpinner" cx="16" cy="16" r="14" fill="none"></circle>'
            +'</svg>'
            +'</center>'
            +'<div id="job_cards_inc"><div id="detail-hot-jobs"></div>'
            +'<div id="jobCardControl"></div>'
            +'<div id="loadingIcon" style="display: none; padding-top: 6%">'
            +'<center>'
            +'<h6>Searching Jobs</h6>'
            +'<svg viewBox="0 0 32 32" width="24" height="24">'
            +'<circle id="loadingSpinner" cx="16" cy="16" r="14" fill="none"></circle>'
            +'</svg>'
            +'</center>'
            +'</div>'
            +'<div id="noJobsDiv" style="padding: 120px 6% 0 6%; display: none">'
            +'<center>'
            +'<img src="/assets/recruiter/img/empty_box.svg" width="96px">'
            +'<h5>No Jobs found!</h5>'
            +'<h6>Looks like there are no jobs matching your search. Please edit search/filter options and try again</h6>'
            +'</center>'
            +'</div>'
            +'<div id="endOfResultsDiv" style="padding: 20px 6% 0 6%; display: none">'
            +'<center>'
            +'<img src="/assets/recruiter/img/empty_box.svg" width="96px">'
            +'<h5>That\'s all we have for now!</h5>'
            +'<h6>Showing all the jobs according to your filter preferences</h6>'
            +'</center>'
            +'</div>'
            +'</div>');
            _parent.append(contianer);

        },
        do: {
            createAndAppendDivider: function (title) {
                var parent = $("#detailHotJobs");

                var mainDiv = document.createElement("div");
                mainDiv.id = "hotJobItemDivider";
                parent.append(mainDiv);

                var otherJobIcon = document.createElement("img");
                otherJobIcon.src = "/assets/common/img/suitcase.png";
                otherJobIcon.style = "width: 42px; margin: 8px";
                otherJobIcon.setAttribute("display", "inline-block");
                mainDiv.appendChild(otherJobIcon);

                var hotJobItem = document.createElement("span");
                hotJobItem.setAttribute("display", "inline-block");
                hotJobItem.textContent = title;

                mainDiv.appendChild(hotJobItem);
            },
            renderDetailJobCard: function (jobPostList) {
                if(jobPostList == null){
                    /* show something went wrong */
                    return;
                } else if(jobPostList.length == 0){
                    /* show no jobs available */
                    return;
                }

                var _jobPostList = jobPostList;
                var _jobPostCount = Object.keys(_jobPostList).length;
                if (_jobPostCount > 0) {

                    $('#job_cards_inc').show();
                    $('#jobCardControl').show();

                    $("#detailHotJobs").html("");
                    var _count = 0;
                    var _parent = $("#detailHotJobs");

                    $("#jobLoaderDiv").hide();
                    $('#noJobsDiv').hide();


                    jc.do.createAndAppendDivider("Popular Jobs");

                    var _isDividerPresent = false;
                    _jobPostList.forEach(function (jobPost) {
                        _count++;
                        if (_count) {
                            //!* get all localities of the jobPost *!/
                            var _jobLocality = jobPost.jobPostToLocalityList;
                            var _localities = "";
                            var _allLocalities = "";
                            var _loopCount = 0;

                            if (jobPost.source != null && jobPost.source > 0 && !_isDividerPresent) {
                                jc.do.createAndAppendDivider("Other Jobs");
                                _isDividerPresent = true;
                            }

                            _jobLocality.forEach(function (locality) {
                                _loopCount++;
                                if (_loopCount > 2) {
                                    return false;
                                } else {
                                    var name = locality.locality.localityName;
                                    _localities += name;
                                    if (_loopCount < Object.keys(_jobLocality).length) {
                                        _localities += ", ";
                                    }
                                }
                            });
                            _loopCount = 0;
                            _jobLocality.forEach(function (locality) {
                                _loopCount++;
                                var name = locality.locality.localityName;
                                _allLocalities += name;
                                if (_loopCount < Object.keys(_jobLocality).length) {
                                    _allLocalities += ", ";
                                }
                            });

                            var hotJobItem = document.createElement("div");
                            hotJobItem.id = "hotJobItem";
                            _parent.append(hotJobItem);

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
                            salaryIcon.src = "/assets/common/img/salary.svg";
                            salaryIcon.setAttribute('height', '15px');
                            salaryIcon.style = "margin-top: -4px";
                            salaryIconDiv.appendChild(salaryIcon);


                            var salaryDiv = document.createElement("div");
                            salaryDiv.style = "display: inline-block; font-size: 14px";
                            if (jobPost.jobPostMaxSalary == "0" || jobPost.jobPostMaxSalary == null) {
                                salaryDiv.textContent = rupeeFormatSalary(jobPost.jobPostMinSalary) + " monthly";
                            } else {
                                salaryDiv.textContent = rupeeFormatSalary(jobPost.jobPostMinSalary) + " - " + rupeeFormatSalary(jobPost.jobPostMaxSalary) + " monthly";
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
                            expIcon.src = "/assets/common/img/workExp.svg";
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
                            locIcon.src = "/assets/common/img/location.svg";
                            locIcon.setAttribute('height', '15px');
                            locIcon.style = "margin-top: -4px";
                            locIconDiv.appendChild(locIcon);

                            var locDiv = document.createElement("div");
                            locDiv.style = "display: inline-block; font-size: 14px";
                            locDiv.textContent = _localities;
                            jobBodySubRowColLoc.appendChild(locDiv);

                            if (((_jobLocality.length) - 2) > 0) {
                                var tooltip = document.createElement("a");
                                tooltip.id = "locationMsg_" + jobPost.jobPostId;
                                tooltip.title = _allLocalities;
                                tooltip.style = "color: #2980b9";
                                tooltip.textContent = " more";
                                jobBodySubRowColLoc.appendChild(tooltip);
                            }

                            $("#locationMsg_" + jobPost.jobPostId).attr("data-toggle", "tooltip");
                            $(function () {
                                $('[data-toggle="tooltip"]').tooltip()
                            });

                            //!*  apply button *!/
                            var applyBtnDiv = document.createElement("div");
                            applyBtnDiv.className = "col-sm-2";
                            rowDiv.appendChild(applyBtnDiv);

                            var applyBtn = document.createElement("div");
                            applyBtn.className = "jobApplyBtn";
                            applyBtn.textContent = "View & Apply";
                            applyBtnDiv.appendChild(applyBtn);
                            applyBtn.onclick = function () {
                                var jobPostBreak = jobPost.jobPostTitle.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g, '_');
                                jobPostBreak = jobPostBreak.toLowerCase();
                                var jobCompany = jobPost.company.companyName.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g, '_');
                                jobCompany = jobCompany.toLowerCase();
                                try {
                                    window.location.href = "/jobs/" + jobPostBreak + "/bengaluru/" + jobCompany + "/" + jobPost.jobPostId;
                                } catch (exception) {
                                    console.log("exception occured!!" + exception);
                                }
                            }
                        }
                    });
                } else {
                    // no jobs found
                    // reset current job card and navigator

                    $('#job_cards_inc').hide();
                    $('#jobCardControl').hide();

                    $("#jobLoaderDiv").hide();
                    $('#noJobsDiv').show();
                }
            }
        }
    };

    jc.init();

    return jc;
}());


// exposed methods
function generateDetailJobCardFor(jobPostList) {
    jc.do.renderDetailJobCard(jobPostList);
}