/**
 * Created by dodo on 22/12/16.
 */

$(window).load(function(){
    try {
        $.ajax({
            type: "POST",
            url: "/getAllRecruiterJobPosts/?view=2",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetJobPostDetails
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/checkPrivateRecruiterPartnerAccount",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckpartnerAccount
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataCheckpartnerAccount(returnedData) {
    if(returnedData == 1){
        $("#accountSwitcher").show();
        $("#accountSwitcherMobile").show();
    } else{
        $("#accountSwitcher").hide();
        $("#accountSwitcherMobile").hide();
    }
}

function switchToPartner() {
    try {
        $.ajax({
            type: "GET",
            url: "/switchToPartner",
            data: false,
            contentType: false,
            processData: false,
            success: processDataPartnerSwitch
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataPartnerSwitch(returnedData) {
    if(returnedData == 1){
        window.location = "/partner";
    } else{
        notifyError("Something went wrong");
    }
}

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
