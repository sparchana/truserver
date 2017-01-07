/**
 * Created by dodo on 22/12/16.
 */

$(window).load(function(){
    try {
        $.ajax({
            type: "POST",
            url: "/getAllRecruiterJobPosts",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetJobPostDetails
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataGetJobPostDetails(returnedData){
    var jobPostList = returnedData;
    var newCount = 0;

    if(jobPostList.length == 0){
        $("#noInterviews").show();
    }

    jobPostList.forEach(function (jobPost) {
        newCount += jobPost.pendingCount;
        newCount += jobPost.upcomingCount;
    });

    if(newCount == 0){
        $(".newNotification").hide();
    } else {
        $(".newNotification").show();
        $("#pendingApproval").html(newCount);
        $("#pendingApprovalMobile").html(newCount);
    }
}
