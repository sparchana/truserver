/*
 * Created by zero on 25/01/17.
 *
 */


var zapp = (function () {
    'use strict';

    var zapp = {
        rows : [],
        method: {
            init: function () {
                zapp.get.allJobPosts();
            }
        },
        render: {
            recruiterTable: function () {
                $("#recTable").show();
                $("#loadingIcon").hide();

                var parent = $('#recruiterTable');
                zapp.rows.forEach(function (recObject) {

                    var mainDiv =  document.createElement("div");
                    parent.append(mainDiv);

                    var outerRow = document.createElement("div");
                    outerRow.className = 'row';
                    outerRow.id="outerBoxMain";
                    mainDiv.appendChild(outerRow);

                    // column #1
                    var colRecruiterName = document.createElement("div");
                    colRecruiterName.className = 'col s12 l1';
                    colRecruiterName.textContent= recObject.recruiterName;
                    colRecruiterName.style = 'margin-top:8px';
                    outerRow.appendChild(colRecruiterName);

                    // column #2
                    var colRecruiterMobile = document.createElement("div");
                    colRecruiterMobile.className = 'col s12 l2';
                    colRecruiterMobile.style = 'margin-top:8px';
                    colRecruiterMobile.textContent = recObject.recruiterMobile;
                    outerRow.appendChild(colRecruiterMobile);

                    // column #3
                    var colTotalJobs  = document.createElement("div");
                    var aTag  = document.createElement("a");

                    var linkText = document.createTextNode(recObject.noOfJobPosted);
                    aTag.appendChild(linkText);
                    aTag.href = "/recruiter/report/?summary=job_post&rid=" + recObject.recruiterId;

                    colTotalJobs.className = "col s12 l2";
                    colTotalJobs.style = "font-weight: 600;font-size:12px";
                    colTotalJobs.appendChild(aTag);
                    outerRow.appendChild(colTotalJobs);

                    // column #4
                    var colTotalApplicants  = document.createElement("div");
                    colTotalApplicants.className = "col s12 l2";
                    colTotalApplicants.textContent= recObject.totalCandidatesApplied;
                    colTotalApplicants.style = "font-weight: 600;font-size:12px";
                    outerRow.appendChild(colTotalApplicants);

                    // column #5
                    var colTotalInterviewConducted = document.createElement("div");
                    colTotalInterviewConducted.className = "col s12 l2";
                    colTotalInterviewConducted.textContent= recObject.totalInterviewConducted;
                    colTotalInterviewConducted.style = "font-weight: 600;font-size:12px";
                    outerRow.appendChild(colTotalInterviewConducted);

                    // column #6
                    var colTotalSelected = document.createElement("div");
                    colTotalSelected.className = "col s12 l2";
                    colTotalSelected.textContent= recObject.totalSelected;
                    colTotalSelected.style = "font-weight: 600;font-size:12px";
                    outerRow.appendChild(colTotalSelected);

                    // column #7
                    var colPercentFulfilled = document.createElement("div");
                    colPercentFulfilled.className = "col s12 l1";
                    if(recObject.percentageFulfilled != null) {
                        colPercentFulfilled.textContent= parseFloat(Math.round(recObject.percentageFulfilled  * 100) / 100).toFixed(1)+ " %";
                    } else {
                        colPercentFulfilled.textContent= "NA";
                    }
                    colPercentFulfilled.style = "font-weight: 600;font-size:12px";
                    outerRow.appendChild(colPercentFulfilled);

                });
            }
        },
        get: {
            allJobPosts: function () {
                $.ajax({
                    url: '/recruiter/api/summary/recruiter',
                    type: 'GET',
                    dataType: 'json'
                }).success(function (returnedData) {
                    zapp.rows = returnedData;
                    zapp.render.recruiterTable();
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

