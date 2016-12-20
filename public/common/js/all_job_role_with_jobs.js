/**
 * Created by adarsh on 7/9/16.
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
        var jobRoleRowCount = Math.floor(jobRoleCount / 4); // 4 because we are showing 4 job roles in a row
        var remainingJobRoles = jobRoleCount % 4;
        var startIndex = 0;

        for(var i=0;i<jobRoleRowCount; i++){
            setJobRoles(jobPostJobRoles, startIndex);
            startIndex = startIndex + 4;
        }
        if(remainingJobRoles > 0){
            startIndex = jobRoleCount - remainingJobRoles;
            setJobRoles(jobPostJobRoles, startIndex);
        }
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
                        window.location.href = "/job/" + jobPostBreak + "_jobs" + "-" + jobRoleId;
                    };

                    var jobRoleNameFooter = document.createElement("div");
                    jobRoleNameFooter.id = "jobRoleNameFooter";
                    jobRoleNameFooter.textContent = jobRoleItem.jobName + " Jobs";
                    jobAnchorFooter.appendChild(jobRoleNameFooter);

                    parentFooter.append(jobAnchorFooter);
                }
                itemCount = itemCount + 1;
            });

            startIndex += footerRowCount;
        }
    }
}

function setJobRoles(returnedData, start){
    var count = 0;
    var parent = $("#jobRoleGrid");
    returnedData.forEach(function (jobRole) {
        if(count >= start && count < (start + 4)) {
            var rowDiv = document.createElement("div");
            rowDiv.style = "margin-right: 0%;margin-left: 0%;";
            rowDiv.className = "row";
            parent.append(rowDiv);

            var gridDiv = document.createElement("div");
            rowDiv.className = "col-md-3 col-sm-4 col-xs-6";
            rowDiv.id="gridDiv";
            rowDiv.style = "padding: 0px;margin-right: 0%;margin-left: 0%;";
            rowDiv.appendChild(gridDiv);

            var jobAnchor = document.createElement("a");
            jobAnchor.style = "margin-right: 0%;margin-left: 0%;";
           /* jobAnchor.style= "margin:0px";*/
            jobAnchor.onclick = function () {
                var jobPostBreak = jobRole.jobName.replace(/[&\/\\#,+()$~%. '":*?<>{}]/g, '_');
                jobPostBreak = jobPostBreak.toLowerCase();
                window.location.href = "/job/" + jobPostBreak + "_jobs" + "-" + jobRole.jobRoleId;
            };
            gridDiv.appendChild(jobAnchor);

            var innerDiv = document.createElement("div");
            innerDiv.id = "jobRole";
            jobAnchor.appendChild(innerDiv);

            var jobIcon = document.createElement("img");
            jobIcon.src = "/assets/common/img/icons/" + jobRole.jobRoleId + ".svg";
            jobIcon.setAttribute('width', '50px');
            jobIcon.setAttribute('alt', jobRole.jobName);
            innerDiv.appendChild(jobIcon);

            var subDiv = document.createElement("div");
            subDiv.id = "jobRoleName";
            subDiv.style = "margin-top: 6px; color: #003557;";
            subDiv.textContent = jobRole.jobName;
            innerDiv.appendChild(subDiv);
        }
        count++;
        //checking when to end the loop
        if(count > start + 4){ return true; }
    });
}
