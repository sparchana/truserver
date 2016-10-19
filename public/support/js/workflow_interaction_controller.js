
function processDataForSupportInteraction(returnedData) {
    var t = $('table#interactionHistory').DataTable({
        "order": [[ 1, "desc" ]]
    });
    if(returnedData == null) {
        console.log("error");
        return;
    }
    returnedData.forEach(function (interaction) {
        if(interaction.interactionId != null){
            t.row.add( [
                interaction.userInteractionTimestamp,
                interaction.interactionId,
                interaction.userInteractionType,
                interaction.userResults,
                interaction.userCreatedBy,
                interaction.channel
            ] ).draw( false );
        } else {
            console.log("Null obj encountered");
        }
    });
    NProgress.done();
}

$(function(){
    var pathname = window.location.pathname; // Returns path only
    var workflowUrl = pathname.split('/');
    var workflowUUId = workflowUrl[(workflowUrl.length)-1];
    try {
        NProgress.start();
        $.ajax({
            type: "GET",
            url: "/support/api/getWorkflowInteraction/?job_post_workflow_uuid=" + workflowUUId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForSupportInteraction
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

