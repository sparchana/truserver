/*
 * Created by zero on 25/01/17.
 *
 */


var zapp = (function () {
    'use strict';

    var zapp = {
        rows : [],
        csv: ["Id, Recruiter Name, Recruiter, Mobile, No. of Jobs Posted, Total Applications, Total Interviews, Total Selections, Percentage fulfilled"],
        csvString: "",

        method: {
            init: function () {
                zapp.get.allJobPosts();
            },
            generateCSVString: function () {
                var json = zapp.rows;
                var fields = Object.keys(json[0]);
                var replacer = function(key, value) { return value === null ? '' : value };
                var csv = json.map(function(row){
                    return fields.map(function(fieldName){
                        return JSON.stringify(row[fieldName], replacer)
                    }).join(',')
                });
                csv.unshift(zapp.csv[0]); // add header column
                zapp.csvString = csv.join('\r\n');
            }
        },
        render: {
            csvDownloadBtn: function () {
                zapp.method.generateCSVString();

                // modify btn
                var a         = document.getElementById('downloadBtn');
                a.href        = 'data:attachment/csv,' +  encodeURIComponent(zapp.csvString);
                a.target      = '_blank';
                a.download    = 'recruiter_summary.csv';

            },
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
                    colPercentFulfilled.textContent= recObject.percentageFulfillment;
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
                    zapp.render.csvDownloadBtn();
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
