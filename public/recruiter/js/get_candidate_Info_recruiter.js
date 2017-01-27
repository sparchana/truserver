/**
 * Created by hawk on 27/1/17.
 */
function getCandidateInfo() {
    var candidateId = 100018653;
    try {
        $.ajax({
            type: "GET",
            url: "/recruiter/getCandidateInfo/"+candidateId,
            data: false,
            async: true,
            contentType: false,
            processData: false,
            success: processDataCandidateInfo
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataCandidateInfo(returnedData) {
    $("#candidateInfoModal").html("");
    var parent = $("#candidateInfoModal");

    var obj = {
        candidate: returnedData
    };
     //calling render candidate card method to render candidate card
     renderIndividualCandidateCard(obj, parent, view_unlocked_candidate);
    $("#candidateInfoModal").openModal();
}