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

                        //addFooter();
                        returned_data.push({
                            'jobId': '<a href="'+"/jobPostDetails/"+jobPost.jobPostId+'" id="'+jobPost.jobPostId+'" style="cursor:pointer;" target="_blank">'+jobPost.jobPostId+'</a>',
                            'jobCreationTimestamp' : function() {
                                var returnedCreationDate = new Date(jobPost.jobPostCreateTimestamp);
                                var creationDate = new Date(returnedCreationDate).toLocaleDateString();
                                return creationDate;
                            },
                            'company': '<a href="'+"/companyDetails/"+jobPost.company.companyId+'" id="'+jobPost.company.companyId+'" style="cursor:pointer;" target="_blank">'+jobPost.company.companyName+'</a>',
                            'jobTitle': jobPost.jobPostTitle,
                            'jobSalary' : function () {
                                if(jobPost.jobPostMaxSalary != 0 && jobPost.jobPostMaxSalary != null){
                                    return ((jobPost.jobPostMinSalary != null) ? "₹" + jobPost.jobPostMinSalary : "0") + " - ₹" + ((jobPost.jobPostMaxSalary != null) ? jobPost.jobPostMaxSalary : "0");
                                } else{
                                    return ((jobPost.jobPostMinSalary != null) ? "₹" + jobPost.jobPostMinSalary : "0");
                                }
                            },
                            'jobRecruiter': function () {
                                if(jobPost.recruiterProfile != null){
                                    return '<a href="'+"/recruiterDetails/"+jobPost.recruiterProfile.recruiterProfileId+'" id="'+jobPost.recruiterProfile.recruiterProfileId+'" style="cursor:pointer;" target="_blank">'+jobPost.recruiterProfile.recruiterProfileName+'</a>';
                                } else{
                                    return " - ";
                                }
                            },
                            'jobLocation' : function(){
                                var jobLocality = "";
                                if(jobPost.jobPostToLocalityList){
                                    jobPost.jobPostToLocalityList.forEach(function (locality) {
                                        jobLocality += locality.locality.localityName + ", ";
                                    });
                                }
                                return jobLocality;
                            },
                            'jobRole' : ((jobPost.jobRole.jobName != null) ? jobPost.jobRole.jobName : ""),
                            'jobExperience' : ((jobPost.jobPostExperience != null && jobPost.jobPostExperience.experienceType != null) ? jobPost.jobPostExperience.experienceType : ""),
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
                                if(jobPost.interviewFullAddress != null && jobPost.interviewFullAddress != ""){
                                    return jobPost.interviewFullAddress;
                                } else{
                                    return "Not Available";
                                }
                            },
                            'jobIsHot' : function () {
                                if(jobPost.jobPostIsHot == true){
                                    return "Is Hot";
                                }
                                return "Is not Hot";
                            },
                            'jobType' : function(){
                                if(jobPost.pricingPlanType != null) {
                                    return jobPost.pricingPlanType.pricingPlanTypeName;
                                } else {
                                    return "Not Specified";
                                }
                            },
                            'jobStatus' : function(){
                                if(jobPost.jobPostStatus != null) {
                                    return jobPost.jobPostStatus.jobStatusName;
                                } else{
                                    return "Not Specified";
                                }
                            },
                            'createdBy' : function(){
                                if(jobPost.createdBy != null) {
                                    return jobPost.createdBy;
                                } else {
                                    return "Not Specified";
                                }
                            },
                            'awaitingInterviewSchedule' : function(){
                                if(jobPost.awaitingInterviewScheduleCount != null) {
                                    return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=pending_interview_schedule" style="cursor:pointer;" target="_blank">'+jobPost.awaitingInterviewScheduleCount+'</a>';
                                } else {
                                    return "NA";
                                }
                            },
                            'awaitingRecruiterConfirmation' : function(){
                                if(jobPost.awaitingRecruiterConfirmationCount != null) {
                                    return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=pre_screen_completed_view" style="cursor:pointer;" target="_blank">'+jobPost.awaitingRecruiterConfirmationCount+'</a>';
                                } else {
                                    return "NA";
                                }
                            },
                            'confirmedInterviews' : function(){
                                if(jobPost.confirmedInterviewsCount != null) {
                                    return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=confirmed_interview_view" style="cursor:pointer;" target="_blank">'+jobPost.confirmedInterviewsCount+'</a>';
                                } else {
                                    return "NA";
                                }
                            },
                            'todaysInterview' : function(){
                                if(jobPost.todaysInterviewCount != null) {
                                    return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=confirmed_interview_view" style="cursor:pointer;" target="_blank">'+jobPost.todaysInterviewCount+'</a>';
                                } else {
                                    return "NA";
                                }
                            },
                            'completedInterview' : function(){
                                if(jobPost.completedInterviewCount != null) {
                                    return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=completed_interview_view" style="cursor:pointer;" target="_blank">'+jobPost.completedInterviewCount+'</a>';
                                } else {
                                    return "NA";
                                }
                            },
                            'match' : function () {
                                return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'/?view=match_view" style="cursor:pointer;" target="_blank"><button class="btn btn-success">Match</button></a>'
                            }
                        })
                    });
                    return returned_data;
                }
            },
            "deferRender": true,
            "columns": [
                { "data": "jobId" },
                { "data": "jobCreationTimestamp" },
                { "data": "company" },
                { "data": "jobTitle" },
                { "data": "jobRecruiter" },
                { "data": "jobSalary" },
                { "data": "jobLocation" },
                { "data": "jobRole" },
                { "data": "jobExperience" },
                { "data": "jobInterviewSchedule" },
                { "data": "jobInterviewAddress" },
                { "data": "jobIsHot" },
                { "data": "jobType" },
                { "data": "jobStatus" },
                { "data": "createdBy" },
                { "data": "awaitingInterviewSchedule" },
                { "data": "awaitingRecruiterConfirmation" },
                { "data": "confirmedInterviews" },
                { "data": "todaysInterview" },
                { "data": "completedInterview" },
                { "data": "match" }
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
                    var contactCredits = 0;
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

                        var creditHistoryCount = Object.keys(recruiter.recruiterCreditHistoryList).length;
                        if(creditHistoryCount > 0){
                            var creditHistoryList = recruiter.recruiterCreditHistoryList;
                            creditHistoryList.reverse();
                            var contactCreditCount = 0;
                            var interviewCreditCount = 0;
                            creditHistoryList.forEach(function (creditHistory){
                                if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 1){
                                    if(contactCreditCount == 0){
                                        if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 1){
                                            contactCredits = creditHistory.recruiterCreditsAvailable;
                                            contactCreditCount = 1;
                                        }
                                    }
                                } else {
                                    if(interviewCreditCount == 0){
                                        if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 2){
                                            interviewCredits = creditHistory.recruiterCreditsAvailable;
                                            interviewCreditCount = 1;
                                        }
                                    }
                                }
                                if(contactCreditCount > 0 && interviewCreditCount > 0) {
                                    return false;
                                }
                            });
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
                            'recruiterContactCredit' : contactCredits,
                            'recruiterInterviewCredit' : interviewCredits
                        })

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