/**
 * Created by batcoder1 on 13/5/16.
 */

function processDataForSupportInteraction(returnedData) {

    var t = $('table#interactionHistory').DataTable();
    //DoTheDue Here
    returnedData.forEach(function (interaction) {
        if(interaction.user_id != null){
            t.row.add( [
                interaction.user_id,
                interaction.user_name,
                interaction.user_interaction_timestamp,
                interaction.user_note
            ] ).draw( false );
        } else {
            console.log("Null obj encountered");
        }
    });
    NProgress.done();
}

$(function(){
    try {
        NProgress.start();
        $.ajax({
            type: "GET",
            url: "/getCandidateInteraction/" + 1,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForSupportInteraction
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

