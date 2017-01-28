/**
 * Created by hawk on 27/1/17.
 */
function getCandidateInfo(cId) {
    try {
        $.ajax({
            type: "GET",
            url: "/recruiter/getCandidateInfo/" + cId,
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
    renderIndividualCandidateCard(obj, parent, view_candidate_info);
    $('.tooltipped').tooltip({delay: 50});
    $("#candidateInfoModal").openModal();
}