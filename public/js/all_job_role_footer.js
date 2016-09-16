/**
 * Created by hawk on 13/9/16.
 */
$(document).ready(function(){
    try {
        $.ajax({
            type: "POST",
            url: "/getAllJobsRolesWithJobs",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckAllJobRoles
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

});
function processDataCheckAllJobRoles(jobPostJobRoles) {
    var jobRoleCount = Object.keys(jobPostJobRoles).length;
    if(jobRoleCount > 0){ //there are at least one job role to display
        var startIndex = 0;
        //Setting footer links
        var footerRowCount = Object.keys(jobPostJobRoles).length / 4;
        startIndex = 0;
        for(var i = 0; i< 4; i++) {
            var parentFooter = $("#jobRoleFooter_" + (i + 1));
            var itemCount = 0;
            jobPostJobRoles.forEach(function (jobRoleItem) {
                if (itemCount >= startIndex && itemCount < startIndex + footerRowCount) {
                    var jobAnchorFooter = document.createElement("a");
                    jobAnchorFooter.style = "font-size: 12px";
                    jobAnchorFooter.onclick = function () {
                        var jobRoleName = jobRoleItem.jobName;

                        var jobRoleId = jobRoleItem.jobRoleId;
                        var jobPostBreak = jobRoleName.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g, '_');
                        jobPostBreak = jobPostBreak.toLowerCase();
                        window.location.href = "/job/" + jobPostBreak + "_jobs" + "/" + jobRoleId;
                    };
                    var jobRoleNameFooter = document.createElement("div");
                    jobRoleNameFooter.id = "jobRoleNameFooter";
                    jobRoleNameFooter.textContent = jobRoleItem.jobName;
                    jobAnchorFooter.appendChild(jobRoleNameFooter);

                    parentFooter.append(jobAnchorFooter);
                }
                itemCount = itemCount + 1;
            });

            startIndex += footerRowCount;
        }
    }
}
