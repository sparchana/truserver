/**
 * Created by batcoder1 on 6/7/16.
 */

var shouldAddFooter = true;
var daysArray = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

$(function(){
    NProgress.start();
    if ( $.fn.dataTable.isDataTable( 'table#jobTable' ) ) {
        $('table#jobTable').DataTable().clear();
    }
    renderDashboard();
    NProgress.done();
});


function renderDashboard() {
    try {
        $('table#jobTable').show();
        $('table#companyTable').hide();
        $('table#recruiterTable').hide();

        $('div#companyTable_info.dataTables_info').hide();
        $('div#jobTable_wrapper.dataTables_wrapper').hide();
        $('div#recruiterTable_wrapper.dataTables_wrapper').hide();

        if ( $.fn.dataTable.isDataTable( 'table#jobTable' ) ) {
            $('table#jobTable').DataTable().clear();
        }
        var table = $('table#jobTable').DataTable({
            "ajax": {
                "url": "/getAllJobPosts",
                "dataSrc": function (returnedData) {
                    var returned_data = new Array();
                    returnedData.forEach(function (jobPost) {

                        var remainingContactCredits = jobPost.totalContactCredits;
                        var remainingInterviewCredits = jobPost.totalInterviewCredits;

                        //addFooter();
                        returned_data.push({
                            'jobId': '<a href="'+"/jobPostDetails/"+jobPost.jobPostId+'" id="'+jobPost.jobPostId+'" style="cursor:pointer;" target="_blank">'+jobPost.jobPostId+'</a>',
                            'jobCreationTimestamp' : function() {
                                var returnedCreationDate = new Date(jobPost.creationTimeStamp);
                                return new Date(returnedCreationDate).toLocaleDateString();
                            },
                            'company': '<a href="'+"/companyDetails/"+jobPost.companyId+'" id="'+jobPost.companyId+'" style="cursor:pointer;" target="_blank">'+jobPost.companyName+'</a>',
                            'jobTitle': function () {
                                return jobPost.jobTitle;
                            },
                            'jobSalary' : function () {
                                return jobPost.salary;
                            },
                            'jobRecruiter': function () {
                                return '<a href="'+"/recruiterDetails/"+jobPost.recruiterId+'" id="'+jobPost.recruiterId+'" style="cursor:pointer;" target="_blank">'
                                    + jobPost.recruiterName + '</a>';
                            },
                            'jobLocation' : function(){
                                return jobPost.jobLocation;
                            },
                            'jobRole' : jobPost.jobRole,
                            'jobExperience' : jobPost.jobExperience,
                            'jobInterviewSchedule' : function () {
                                if(jobPost.interviewDetailsList != null && jobPost.interviewDetailsList.length > 0){
                                    var interviewDetailsList = jobPost.interviewDetailsList;
                                    if(interviewDetailsList[0].interviewDays != null) {
                                        var interviewDays = interviewDetailsList[0].interviewDays.toString(2);

                                        // while converting from decimal to binary, preceding zeros are ignored. to fix, follow below
                                        if(interviewDays.length != 7) {
                                            x = 7 - interviewDays.length;
                                            var modifiedInterviewDays = "";

                                            for(i=0;i<x;i++){
                                                modifiedInterviewDays += "0";
                                            }
                                            modifiedInterviewDays += interviewDays;
                                            interviewDays = modifiedInterviewDays;
                                        }

                                        var interviewSchedule = "";
                                        for(i=0; i<=6; i++) {
                                            if(interviewDays[i] == 1){
                                                interviewSchedule += daysArray[i] + ",";
                                            }
                                        }
                                        return interviewSchedule;
                                    }
                                }
                                return "Not Specified";
                            },

                            'jobInterviewAddress' : function () {
                                return jobPost.interviewAddress;
                            },
                            'jobIsHot' : function () {
                                if(jobPost.jobIsHot == true){
                                    return "Is Hot";
                                }
                                return "Is not Hot";
                            },
                            'jobType' : function(){
                                return jobPost.jobPlan;
                            },
                            'jobStatus' : function(){
                                return jobPost.jobStatus;
                            },
                            'createdBy' : function(){
                                return jobPost.createdBy;
                            },
                            'awaitingInterviewSchedule' : function(){
                                return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=pending_interview_schedule" style="cursor:pointer;" target="_blank">'+jobPost.awaitingInterviewSchedule+'</a>';
                            },
                            'awaitingRecruiterConfirmation' : function(){
                                return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=pre_screen_completed_view" style="cursor:pointer;" target="_blank">'+jobPost.awaitingRecruiterConfirmation+'</a>';
                            },
                            'confirmedInterviews' : function(){
                                return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=confirmed_interview_view" style="cursor:pointer;" target="_blank">'+jobPost.confirmedInterviews+'</a>';
                            },
                            'todaysInterview' : function(){
                                return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=confirmed_interview_view" style="cursor:pointer;" target="_blank">'+jobPost.todaysInterviews+'</a>';
                            },
                            'tomorrowsInterview' : function(){
                                return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=confirmed_interview_view" style="cursor:pointer;" target="_blank">'+jobPost.tomorrowsInterviews+'</a>';
                            },
                            'completedInterview' : function(){
                                return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=completed_interview_view" style="cursor:pointer;" target="_blank">'+jobPost.completedInterviews+'</a>';
                            },
                            'match' : function () {
                                return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=match_view" style="cursor:pointer;" target="_blank"><button class="btn btn-success">Dashboard</button></a>'
                            },
                            'contactCredits' : remainingContactCredits,
                            'interviewCredits' : remainingInterviewCredits
                        })
                    });
                    return returned_data;
                }
            },
            "deferRender": true,
            "columns": [
                { "data": "jobId" },
                { "data": "match" },
                { "data": "jobCreationTimestamp" },
                { "data": "company" },
                { "data": "jobTitle" },
                { "data": "jobRecruiter" },
                { "data": "contactCredits" },
                { "data": "interviewCredits" },
                { "data": "jobSalary" },
                { "data": "jobLocation" },
                { "data": "jobRole" },
                { "data": "jobStatus" },
                { "data": "jobIsHot" },
                { "data": "createdBy" },
                { "data": "awaitingInterviewSchedule" },
                { "data": "awaitingRecruiterConfirmation" },
                { "data": "confirmedInterviews" },
                { "data": "todaysInterview" },
                { "data": "tomorrowsInterview" },
                { "data": "completedInterview" },
                { "data": "jobExperience" },
                { "data": "jobInterviewSchedule" },
                { "data": "jobInterviewAddress" },
                { "data": "jobType" }
            ],
            "order": [[0, "desc"]],
            "language": {
                "emptyTable": "No data available"
            },
            "scrollY": '48vh',
            "scrollCollapse": true,
            "scrollX": true,
            "destroy": true,
            "dom": 'Bfrtip',
            "buttons": [
                'copy', 'csv', 'excel'
            ]
        });

        // Apply the search filter
        /*table.columns().every(function () {
            var that = this;
            $('input', this.footer()).on('keyup change', function () {
                if (that.search() !== this.value) {
                    that.search(this.value).draw();
                }
            });
        });*/

    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}


/*function addFooter() {
    // Setup - add a text input to each footer cell
    if (shouldAddFooter) {
        console.log("add footer");
        $('#jobTable tfoot th').each(function () {
            var title = $(this).text();
            console.log("adding");
            $(this).html('<input type="text" name="' + title + '"  id="' + title + '" placeholder="' + title + '" />');
        });
        shouldAddFooter = false;
    }
};*/

function getAllCompany() {
    try {
        $('table#companyTable').show();
        $('table#jobTable').hide();
        $('table#recruiterTable').hide();

        $('div#jobTable_info.dataTables_info').hide();
        $('div#jobTable_wrapper.dataTables_wrapper').hide();
        $('div#recruiterTable_wrapper.dataTables_wrapper').hide();

        if ( $.fn.dataTable.isDataTable( 'table#companyTable' ) ) {
            $('table#companyTable').DataTable().clear();
        }
        var table = $('table#companyTable').DataTable({
            "ajax": {
                "url": "/getAllCompany",
                "dataSrc": function (returnedData) {
                    var returned_data = new Array();
                    returnedData.forEach(function (company) {
                        returned_data.push({
                            'companyId': '<a href="'+"/companyDetails/"+company.companyId+'" id="'+company.companyId+'" style="cursor:pointer;" target="_blank">' + company.companyId + '</a>',
                            'companyCreationTimestamp' : function() {
                                var returnedCreationDate = new Date(company.companyCreateTimestamp);
                                var creationDate = new Date(returnedCreationDate).toLocaleDateString();
                                return creationDate;
                            },
                            'companyName': company.companyName,
                            'companyCode': company.companyCode,
                            'companyWebsite' : ((company.companyWebsite != null) ? '<a href="'+"http://"+company.companyWebsite+'" style="cursor:pointer;" target="_blank">'+company.companyWebsite+'</a>' : ""),
                            'companyAddress' : ((company.companyAddress != null) ? company.companyAddress : ""),
                            'companyType' : ((company.compType != null) ? company.compType.companyTypeName : ""),
                            'companyStatus' : ((company.compStatus != null) ? company.compStatus.companyStatusName : "")
                        })
                    });
                    return returned_data;
                }
            },
            "deferRender": true,
            "columns": [
                { "data": "companyId" },
                { "data": "companyCreationTimestamp" },
                { "data": "companyName" },
                { "data": "companyCode" },
                { "data": "companyWebsite" },
                { "data": "companyAddress" },
                { "data": "companyType" },
                { "data": "companyStatus" }
            ],
            "order": [[2, "asc"]],
            "language": {
                "emptyTable": "No data available"
            },
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

function getAllRecruiters() {
    try {
        $('table#companyTable').hide();
        $('table#jobTable').hide();
        $('table#recruiterTable').show();

        $('div#jobTable_info.dataTables_info').hide();
        $('div#companyTable_wrapper.dataTables_wrapper').hide();
        $('div#jobTable_wrapper.dataTables_wrapper').hide();
        $('div#recruiterTable_wrapper.dataTables_wrapper').hide();

        if ( $.fn.dataTable.isDataTable( 'table#recruiterTable' ) ) {
            $('table#companyTable').DataTable().clear();
        }
        var table = $('table#recruiterTable').DataTable({
            "ajax": {
                "url": "/getAllRecruiters",
                "dataSrc": function (returnedData) {

                    var returned_data = new Array();
                    var contactCredits = returnedData.contactCreditCount;
                    var interviewCredits = 0;
                    var mobileVerificationStatus;
                    returnedData.forEach(function (recruiter) {

                        if (recruiter.recruiterAuth != null) {
                            if (recruiter.recruiterAuth.recruiterAuthStatus != null && recruiter.recruiterAuth.recruiterAuthStatus == 1) {
                                mobileVerificationStatus = "Verified";
                            }
                            else {
                                mobileVerificationStatus = "Not Verified";
                            }
                        }
                        else {
                            mobileVerificationStatus = "Unknown";
                        }
                        returned_data.push({
                            'recruiterId': '<a href="'+"/recruiterDetails/"+recruiter.recruiterProfileId+'" id="'+recruiter.recruiterProfileId+'" style="cursor:pointer;" target="_blank">' + recruiter.recruiterProfileId + '</a>',
                            'creationTimestamp' : function() {
                                var returnedCreationDate = new Date(recruiter.recruiterProfileCreateTimestamp);
                                return new Date(returnedCreationDate).toLocaleDateString();
                            },
                            'recruiterName': recruiter.recruiterProfileName,
                            'recruiterCompany' : ((recruiter.company != null) ? '<a href="'+"/companyDetails/"+recruiter.company.companyId+'" style="cursor:pointer;" target="_blank">'+recruiter.company.companyName+'</a>' : ""),
                            'recruiterMobile' : recruiter.recruiterProfileMobile,
                            'recruiterMobileVerificationStatus' : mobileVerificationStatus,
                            'recruiterEmail' : recruiter.recruiterProfileEmail,
                            'recruiterContactCredit' : recruiter.contactCreditCount,
                            'recruiterInterviewCredit' : recruiter.interviewCreditCount
                        });

                        contactCredits = 0;
                        interviewCredits = 0;
                    });
                    return returned_data;
                }
            },
            "deferRender": true,
            "columns": [
                { "data": "recruiterId" },
                { "data": "creationTimestamp" },
                { "data": "recruiterName" },
                { "data": "recruiterCompany" },
                { "data": "recruiterMobile" },
                { "data": "recruiterMobileVerificationStatus" },
                { "data": "recruiterEmail" },
                { "data": "recruiterContactCredit" },
                { "data": "recruiterInterviewCredit" }
            ],
            "order": [[0, "desc"]],
            "language": {
                "emptyTable": "No data available"
            },
            "scrollY": '48vh',
            "scrollCollapse": true,
            "scrollX": true,
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