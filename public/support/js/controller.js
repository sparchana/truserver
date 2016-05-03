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
function processLeadUpdate(returnedData) {
    if(returnedData.leadType != '0' && returnedData.leadType != '1') {
        // existing data hence pre fill form
    } else {
        clearModal();
        alert('Unable to show data');
    }
    window.location="/support";
}



function processCandidateReg(returnedData) {

    if(returnedData.leadId != null ) {
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
                newLead.leadId,
                newLead.leadType,
                newLead.leadStatus,
                newLead.leadCreationTimestamp,
                newLead.totalInBounds,
                newLead.lastIncomingCallTimestamp,
                newLead.leadChannel,
                newLead.leadMobile,
                newLead.leadName,
                function(){
                    if(newLead.leadStatus == 'New') {
                        return '<input type="submit" data-toggle="modal" data-target="#callResponseModal" value="Call" onclick="myHandler('+newLead.leadMobile+', '+newLead.leadId+')" id="'+newLead.leadId+'" class="btn btn-primary">'
                    } else {
                        return '<input type="submit" data-toggle="modal" data-target="#callResponseModal" value="Call Back" onclick="myHandler('+newLead.leadMobile+', '+newLead.leadId+')" id="'+newLead.leadId+'"  class="btn btn-default">'
                    }
                }
            ] ).draw( false );
        } else {
            console.log("Null obj encountered");
        }
    });
    NProgress.done();
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
        agent_number: "+919980303169",
        phone_number: mobile,
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
        $.ajax({
            type: "GET",
            url: "/updateLeadStatus/"+id,
            processData: false,
            success: function (e) {
                if(e == '1'){
                    var table = $('#leadTable').DataTable();
                    $("input#"+id).removeClass();
                    $("input#"+id).addClass("btn btn-default");
                    $('input#'+id).prop('value', 'Call back');
                }
            }
        });

    } catch (exception) {

    }
}
function refreshDashBoard() {
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
}


$(function(){
    $("#callResponseForm").submit(function(eventObj) {
        eventObj.preventDefault();
        try {

            var leadId = $("#leadId").val();
            var answer = document.querySelector('input[name="answer"]:checked').value;
            var updateType = document.querySelector('input[name="updateType"]:checked').value;
            if(answer == 'yes') {
                NProgress.start();
                $.ajax({
                    type: "GET",
                    url: "/updateLeadType/"+leadId+"/"+updateType,
                    processData: false,
                    success: processLeadUpdate
                });
                NProgress.done();
            }
        } catch (exception) {
        }

    }); // end of submit

    
    try {
        NProgress.start();
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

