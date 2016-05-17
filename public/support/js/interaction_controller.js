/**
 * Created by batcoder1 on 13/5/16.
 */

function processDataForSupportInteraction(returnedData) {

    var t = $('table#interactionHistory').DataTable({
        "order": [[ 0, "desc" ]]
    });
    //DoTheDue Here
    returnedData.forEach(function (interaction) {
        if(interaction.user_id != null){
            t.row.add( [
                interaction.user_interaction_timestamp,
                interaction.user_id,
                interaction.user_name,
                interaction.user_interactionType,
                interaction.user_note,
                interaction.user_results,
            ] ).draw( false );
        } else {
            console.log("Null obj encountered");
        }
    });
    NProgress.done();
}

$(function(){
    var pathname = window.location.pathname; // Returns path only
    var leadIdUrl = pathname.split('/');
    var leadId = leadIdUrl[(leadIdUrl.length)-1];
    try {
        NProgress.start();
        $.ajax({
            type: "GET",
            url: "/getCandidateInteraction/" + leadId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForSupportInteraction
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

