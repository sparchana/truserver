/*
 * Created by zero on 25/01/17.
 *
 */


var zapp = (function () {
    'use strict';

    var zapp = {
        rows : [],
        csv: ["Id, Recruiter Name, Recruiter Mobile, No. of Jobs Posted, Total Applications, Total Interviews, Total Selections, Percentage fulfilled"],
        csvString: "",
        startDate: null,
        endDate: null,
        companyName: "",

        method: {
            init: function () {
                zapp.render.dateTimeRange();
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
            dateTimeRange: function () {
                //initiating range picker
                $('input[name="datefilter"]').daterangepicker({
                    autoUpdateInput: false,
                    locale: {
                        cancelLabel: 'Clear',
                        format: 'DD-MM-YYYY'
                    },
                    maxDate: moment()
                });
            },
            recruiterDataTable: function () {
                try {
                    var table = $('table#recruiterTable').DataTable({
                        "ajax": {
                            "type": "GET",
                            "url": '/recruiter/api/summary/recruiter?from='+zapp.startDate+'&to='+zapp.endDate,
                            "dataSrc": function (returnedData) {

                                zapp.companyName = returnedData.companyName;

                                $('#companyName').text(zapp.companyName);

                                zapp.rows = returnedData.recruiterSummaryList;

                                $("#recruiterTable").show();
                                $("#recruiterTableContainer").show();
                                $("#loadingIcon").hide();

                                var returned_data = [];

                                zapp.rows.forEach(function (recObject) {

                                    returned_data.push({
                                        'recruiter': recObject.recruiterName,
                                        'recruiterMobile': recObject.recruiterMobile,
                                        'totalActiveJobs': function () {
                                            return '<div class="mLabel" style="width:100%"><a href="/recruiter/report/?summary=job_post&rid='+recObject.recruiterId + '">'+recObject.noOfJobPosted+'</div>';
                                        },
                                        'totalApplication': recObject.totalCandidatesApplied,
                                        'totalInterview': recObject.totalInterviewConducted,
                                        'totalSelection': recObject.totalSelected,
                                        'percentageFulfillment': recObject.percentageFulfillment
                                    });
                                });

                                return returned_data;
                            }
                        },
                        "deferRender": true,
                        "columns": [
                            { "data": "recruiter" },
                            { "data": "recruiterMobile" },
                            { "data": "totalActiveJobs" },
                            { "data": "totalApplication" },
                            { "data": "totalInterview" },
                            { "data": "totalSelection" },
                            { "data": "percentageFulfillment" }

                        ],
                        "language": {
                            "emptyTable": "No Recruiter found!"
                        },
                        "order": [[2, "desc"]],
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
            allJobPosts: function () {
                zapp.render.recruiterDataTable();
            }
        }
    };

    zapp.method.init();

    // date time picker listener

    $('input[name="datefilter"]').on('apply.daterangepicker', function(ev, picker) {
        $(this).val(picker.startDate.format('DD-MM-YYYY') + ' - ' + picker.endDate.format('DD-MM-YYYY'));
        zapp.startDate = picker.startDate.format('YYYY-MM-DD');
        zapp.endDate = picker.endDate.format('YYYY-MM-DD');

        zapp.render.recruiterDataTable();
    });

    $('input[name="datefilter"]').on('cancel.daterangepicker', function(ev, picker) {
        $(this).val('');

        zapp.startDate = null;
        zapp.endDate = null;

        zapp.render.recruiterDataTable();
    });

    return zapp;
}());
