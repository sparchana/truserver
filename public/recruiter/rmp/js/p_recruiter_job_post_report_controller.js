/*
 * Created by zero on 25/01/17.
 *
 */


var zapp = (function () {
    'use strict';

    var zapp = {
        rows : [],
        recruiterName: "",
        csv: ["Id, Job Title, Job Posted On, Fulfilment status, Total SMS Sent, Total Applications, Total Interviews, Cycle Time"],
        csvString: "",
        recruiterId: null,
        method: {
            init: function () {
                if(zapp.recruiterId == null) {
                    zapp.method.setRecruiterId();
                }
                zapp.get.allEmployee();
            },
            setRecruiterId: function () {
                zapp.recruiterId = parseInt(window.location.search.split("&")[1].split("=")[1]);
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
            jobPostDataTable: function () {
                try {
                    var table = $('table#jobPostTable').DataTable({
                        "ajax": {
                            "type": "GET",
                            "url": '/recruiter/api/summary/jobpost/'+zapp.recruiterId,
                            "dataSrc": function (returnedData) {

                                zapp.recruiterName = returnedData.recruiterName;
                                zapp.rows = returnedData.jobPostSummaryList;

                                $('#recruiterName').text(zapp.recruiterName);

                                $("#jobPostTable").show();
                                $("#jobPostTableContainer").show();
                                $("#loadingIcon").hide();

                                var returned_data = [];

                                zapp.rows.forEach(function (response) {

                                    returned_data.push({
                                        'jobTitle': function () {
                                            return '<div class="mLabel" style="width:100%"><a href="/recruiter/jobPostTrack/'+response.jobPostId + '">'+response.jobTitle+'</div>';
                                        },
                                        'jobPostedOn': response.jobPostedOn,
                                        'fulfillment': response.percentageFulfillment,
                                        'totalSMS': response.totalSmsSent,
                                        'totalApplication': response.totalApplicants,
                                        'totalInterview': response.totalInterviewConducted,
                                        'cycleTime': response.cycleTime
                                    });
                                });

                                return returned_data;
                            }
                        },
                        "deferRender": true,
                        "columns": [
                            { "data": "jobTitle" },
                            { "data": "jobPostedOn" },
                            { "data": "fulfillment" },
                            { "data": "totalSMS" },
                            { "data": "totalApplication" },
                            { "data": "totalInterview" },
                            { "data": "cycleTime" }

                        ],
                        "language": {
                            "emptyTable": "No Job Post found!"
                        },
                        "order": [[1, "desc"]],
                        responsive: true,
                        "destroy": true,
                        "dom": 'Bfrtip',
                        "buttons": [
                            'copy', 'csv', 'excel'
                        ]
                    });
                } catch (exception) {
                    console.log("exception occured!!" + exception);
                }
            }
        },
        get: {
            allEmployee: function () {
                zapp.render.jobPostDataTable();
            }
        }
    };

    zapp.method.init();

    return zapp;
}());

