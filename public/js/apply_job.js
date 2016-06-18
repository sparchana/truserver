/**
 * Created by batcoder1 on 17/6/16.
 */

function processDataApplyJob(returnedData) {
    $("#messagePromptModal").modal("show");
    if(returnedData.status == 1){
        $("#customMsg").html("Applied Success");
    } else if(returnedData.status == 2){
        $("#customMsg").html("Oops! Something went Wrong. Unable to apply");
    } else if(returnedData.status == 3){
        $("#customMsg").html("Looks like you already applied for this Job");
    } else if(returnedData.status == 4){
        $("#customMsg").html("Oops! Candidate does't Exists");
    } else{
        $("#customMsg").html("Oops! Looks like the job is no longer available");
    }
}

function processDataAddJobPost(returnedData) {
    console.log("returnedData :" + returnedData.status);

}

function addJobPost(){
    var startTime = new Date().getTime();
    var jobPostLocality = [];

    var locality = "5";
    var i;
    for(i=0;i<locality.length; i++){
        jobPostLocality.push(parseInt(locality[i]));
    }
    try {
        var d = {
            jobPostMinSalary: 2500,
            jobPostMaxSalary: 3000,
            jobPostStartTime: startTime,
            jobPostEndTime: startTime,
            jobPostBenefitPf: 1,
            jobPostBenefitFuel: 1,
            jobPostBenefitInsurance: 1,
            jobPostWorkFromHome: 1,
            jobPostDescription: "Asdajsdknasd",
            jobPostTitle: "Title",
            jobPostVacancy: 15,
            jobPostDescriptionAudio: "asdasdas",
            jobPostLocality: jobPostLocality
        };
        $.ajax({
            type: "POST",
            url: "/addJobPost",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataAddJobPost
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

// apply_job ajax script
function applyJob(id){
    localStorage.setItem("applyJobFlag", "1");
    localStorage.setItem("applyJobId", id);
    var phone = localStorage.getItem("mobile");
    var jobId = id;
    if(phone == null){ // not logged in
        $("#myLoginModal").modal("show");
    } else{
        try {
            var d = {
                jobId: jobId,
                candidateMobile: phone
            };
            $.ajax({
                type: "POST",
                url: "/applyJob",
                async: false,
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataApplyJob
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
} // end of submit

