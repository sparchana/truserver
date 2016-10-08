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

        $('div#companyTable_info.dataTables_info').hide();
        $('div#companyTable_wrapper.dataTables_wrapper').hide();

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
                                return '<a href="'+"/support/workflow/"+jobPost.jobPostId+'" style="cursor:pointer;" target="_blank"><button class="btn btn-success">Match</button></a>'
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
        $('div#jobTable_info.dataTables_info').hide();
        $('div#jobTable_wrapper.dataTables_wrapper').hide();

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