/**
 * Created by batcoder1 on 6/7/16.
 */

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
                                if(jobPost.jobPostMaxSalary == 0){
                                    return ((jobPost.jobPostMinSalary != null) ? "₹" + jobPost.jobPostMinSalary : "0");
                                } else{
                                    return ((jobPost.jobPostMinSalary != null) ? "₹" + jobPost.jobPostMinSalary : "0") + " - ₹" + ((jobPost.jobPostMaxSalary != null) ? jobPost.jobPostMaxSalary : "0");
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
                            'jobExperience' : ((jobPost.jobPostExperience.experienceType != null) ? jobPost.jobPostExperience.experienceType : ""),
                            'jobIsHot' : function () {
                                if(jobPost.jobPostIsHot == true){
                                    return "Is Hot";
                                }
                                return "Is not Hot";
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
                { "data": "jobIsHot" },
                { "data": "match" }
            ],
            "order": [[0, "desc"]],
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
                    returnedData.forEach(function (recruiter) {
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
                                } else{
                                    if(interviewCreditCount == 0){
                                        if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 2){
                                            interviewCredits = creditHistory.recruiterCreditsAvailable;
                                            interviewCreditCount = 1;
                                        }
                                    }
                                }
                                if(contactCreditCount > 0 && interviewCreditCount > 0){
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
                            'recruiterEmail' : recruiter.recruiterProfileEmail,
                            'recruiterContactCredit' : contactCredits,
                            'recruiterInterviewCredit' : interviewCredits
                        })
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
                { "data": "recruiterEmail" },
                { "data": "recruiterContactCredit" },
                { "data": "recruiterInterviewCredit" }
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