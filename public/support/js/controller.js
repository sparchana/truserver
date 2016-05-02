/*function processDataForCall(returnedData) {
    (returnedData);

}*/

function cleaModalBackDrop() {
    $('#newLeadEntryModal').modal('hide');
    $('body').removeClass('modal-open');
    $('.modal-backdrop').remove();
}

function clearModal() {

    $("#candidateName").val("");
    $("#candidateMobile").val("");
    $("#candidateJobInterest").val("");
    $("#candidateType").val("");
    $("#candidateNote").val("");
}

function fillModal(returnedData) {

    $("#candidateName").val(returnedData.candidateName);
    $("#candidateMobile").val(returnedData.candidateMobile);

}

function processCandidateReg(returnedData) {

    if(returnedData.candidateId != null ) {
        // existing data hence pre fill form
        alert('Candidate Already Exists');
    } else if(returnedData.status == 1) {
        // success in storing hence show message
        alert('saved');
    } else {
        clearModal();
        alert('Unable to show data');
    }

}
function reportCandidateRegError(returnedData) {

    alert(returnedData);

}
function processDataForSupport(returnedData) {

    var t = $('table#leadTable').DataTable();
    //DoTheDue Here
    returnedData.forEach(function (newLead) {
        if(newLead.leadId != null){
            t.row.add( [
                '<button class="btn btn-default" data-toggle="modal" data-target="#newLeadEntryModal"  onclick="getCandidateInfo('+newLead.leadId+')">'+newLead.leadId+'</button>',
                newLead.leadStatus,
                newLead.leadCreationTimestamp,
                newLead.leadChannel,
                newLead.leadMobile,
                newLead.leadName,
                newLead.leadType,
                function(){
                    if(newLead.leadStatus == 'New') {
                        return '<input type="submit" data-toggle="modal" data-target="#newLeadEntryModal" value="Call" onclick="myHandler('+newLead.leadMobile+', '+newLead.leadId+')" id="'+newLead.leadMobile+'"class="btn btn-primary">'
                    } else {
                        return '<input type="submit" data-toggle="modal" data-target="#newLeadEntryModal" value="Call Back" id="click2call_submitbtn" class="btn btn-default">'
                    }
                }
            ] ).draw( false );
        } else {
            console.log("Null obj encountered");
        }
    });
}
function getCandidateInfo(id) {
    clearModal();
    console.log(id);
    try {

        $.ajax({
            url: "/getCandidateInfo/"+id,
            type: "GET",
            data: false,
            dataType: "json",
            contentType: false,
            processData: false,
            success: fillModal
        });
    } catch (exception) {

    }
}


function myHandler (mobile, id) {

    console.log(mobile + " " +id);
    // changes modal leadId field value
    $("#leadId").val(id);
    $("#candidateMobile").val("+"+mobile);
    var s = {
        api_key: "dae93473-50a6-11e5-bbe8-067cf20e9301",
        agent_number: "+919019672209",
        phone_number: "+919019672209",
        sr_number: "+918880007799"
    };

    try {
        $.ajax({
            url: "https://sr.knowlarity.com/vr/api/click2call/",
            type: "POST",
            data: s,
            contentType: "jsonp",
            dataType: 'jsonp',
            cache: !1,
            success: false,
            error: function (e) {

                alert("error ")
            }
        });

    } catch (exception) {

    }
}

$(function(){

    $("#candidateEntryForm").submit(function(eventObj) {
        eventObj.preventDefault();
        try {

            var name  = $('#fullname').val();
            var phone = $('#mobile').val();
            console.log(name);

            $.ajax({
                type: "POST",
                url: "/addCandidate",
                data: $(this).serializeArray(),
                dataType: "json",
                success: processCandidateReg,
                error: reportCandidateRegError
            });
        } catch (exception) {
        }

    }); // end of submit
    
    try {
        $.ajax({
            type: "GET",
            url: "/getAll",
            data: false,
            contentType: false,
            processData: false,
            success: processDataForSupport
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

