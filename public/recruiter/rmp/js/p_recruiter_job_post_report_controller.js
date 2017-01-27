/*
 * Created by zero on 25/01/17.
 *
 */


var zapp = (function () {
    'use strict';

    var zapp = {
        rows : [],
        recruiterId: null,
        method: {
            init: function () {
                if(zapp.recruiterId == null) {
                    zapp.method.setRecruiterId();
                }
                zapp.get.allJobPosts();
            },
            setRecruiterId: function () {
                zapp.recruiterId = parseInt(window.location.search.split("&")[1].split("=")[1]);
            }
        },
        render: {
            jobPostTable: function () {
                $("#jpTable").show();
                $("#loadingIcon").hide();

                var parent = $('#jobPostTable');
                zapp.rows.forEach(function (response) {

                    var mainDiv =  document.createElement("div");
                    parent.append(mainDiv);

                    var outerRow = document.createElement("div");
                    outerRow.className = 'row';
                    outerRow.id="outerBoxMain";
                    mainDiv.appendChild(outerRow);

                    // column #1
                    var colJobTitle = document.createElement("div");
                    colJobTitle.className = 'col s12 l1';
                    colJobTitle.textContent= response.jobTitle;
                    colJobTitle.style = 'margin-top:8px';
                    outerRow.appendChild(colJobTitle);

                    // column #2
                    var colPostedOn = document.createElement("div");
                    colPostedOn.className = 'col s12 l2';
                    colPostedOn.style = 'margin-top:8px';
                    colPostedOn.textContent = response.jobPostedOn;
                    outerRow.appendChild(colPostedOn);

                    // column #3
                    var colFulfillmentStatus = document.createElement("div");
                    colFulfillmentStatus.className = 'col s12 l2';
                    colFulfillmentStatus.style = 'margin-top:8px';
                    colFulfillmentStatus.textContent = response.fulfillmentStatus + " %";
                    outerRow.appendChild(colFulfillmentStatus);

                    // column #4
                    var colTotalSmsSent  = document.createElement("div");
                    colTotalSmsSent.className = "col s12 l2";
                    colTotalSmsSent.textContent= response.totalSmsSent;
                    colTotalSmsSent.style = "font-weight: 600;font-size:12px";
                    outerRow.appendChild(colTotalSmsSent);

                    // column #5
                    var colTotalApplicants = document.createElement("div");
                    colTotalApplicants.className = "col s12 l2";
                    colTotalApplicants.textContent= response.totalApplicants;
                    colTotalApplicants.style = "font-weight: 600;font-size:12px";
                    outerRow.appendChild(colTotalApplicants);

                    // column #6
                    var colTotalInterviewConducted = document.createElement("div");
                    colTotalInterviewConducted.className = "col s12 l2";
                    colTotalInterviewConducted.textContent= response.totalInterviewConducted;
                    colTotalInterviewConducted.style = "font-weight: 600;font-size:12px";
                    outerRow.appendChild(colTotalInterviewConducted);

                    // column #7
                    var colCycleTime = document.createElement("div");
                    colCycleTime.className = "col s12 l1";
                    if(response.cycleTime < 0) {
                        colCycleTime.textContent = "NA";
                    } else {
                        colCycleTime.textContent = response.cycleTime + " Day(s)";
                    }
                    colCycleTime.style = "font-weight: 600;font-size:12px";
                    outerRow.appendChild(colCycleTime);

                });
            }
        },
        get: {
            allJobPosts: function () {
                $.ajax({
                    url: '/recruiter/api/summary/jobpost/'+zapp.recruiterId,
                    type: 'GET',
                    dataType: 'json'
                }).success(function (returnedData) {
                    zapp.rows = returnedData;
                    zapp.render.jobPostTable();
                }).error(function (jqXHR, exception) {
                    $("#somethingWentWrong").show();
                    $("#loadingIcon").hide();
                });
            }
        }
    };

    zapp.method.init();

    return zapp;
}());

