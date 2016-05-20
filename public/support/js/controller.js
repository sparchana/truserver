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

function openCreateCandidate(id) {
    window.location="/candidateSignupSupport/"+id;
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
function renderKWResponse(returnedData) {
    console.log(returnedData.error.message);

}
function processDataForSupport(returnedData) {
    if ( $.fn.dataTable.isDataTable( '#leadTable' ) ) {
        $('#leadTable').DataTable().clear();
    }

    var t = $('table#leadTable').DataTable({
        "order": [[5, "desc"]],
        "deferRender": true,
        retrieve: true
    });
    //DoTheDue Here
    returnedData.forEach(function (newLead) {
        if(newLead.leadId != null){
            t.row.add( [
                newLead.leadId,
                newLead.leadType,
                newLead.leadStatus,
                newLead.leadCreationTimestamp,
                /*newLead.totalInBounds,*/
                '<a id="'+newLead.leadId+'" style="cursor:pointer;" onclick="showInteraction(this);">'+newLead.totalInBounds+'</a>',
                newLead.lastIncomingCallTimestamp,
                newLead.leadChannel,
                newLead.leadMobile,
                newLead.leadName,
                function(){
                    if(newLead.leadStatus == 'New') {
                        return '<input type="submit" value="Call"  style="width:100px" onclick="myHandler('+newLead.leadMobile+', '+newLead.leadId+');" id="'+newLead.leadId+'" class="btn btn-primary">'
                    } else {
                        return '<input type="submit" value="Call Back"  style="width:100px" onclick="myHandler('+newLead.leadMobile+', '+newLead.leadId+'); " id="'+newLead.leadId+'"  class="btn btn-default">'
                    }
                }
            ] ).draw( false );
        } else {
            console.log("Null obj encountered");
        }
    });
    NProgress.done();
}

function showInteraction(x) {
    window.location = "/candidateInteraction/" + x.id;
}

function getCandidateInfo(id) {
    clearModal();
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

function getThis(id){
    try {
        NProgress.start();
        $.ajax({
            type: "GET",
            url: "/getAll/" + id,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForSupport
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}


function myHandler (mobile, id) {
    $("#leadId").val(id);
    $("#leadMobileNumber").val("+"+mobile);
    var s = {
        api_key: "dae93473-50a6-11e5-bbe8-067cf20e9301",
        agent_number: "+919980303169",
        phone_number: "+",
        sr_number: "+918880007799"
    };

    try {
        $.ajax({
            url: "https://sr.knowlarity.com/vr/api/click2call/",
            async: false,
            type: "POST",
            data: s,
            contentType: "jsonp",
            dataType: 'jsonp',
            cache: !1,
            success: renderKWResponse,
            error: false
        });
        openCreateCandidate(id);
    } catch (exception) {

    }
}

$(function(){
    $("#callResponseForm").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var leadId = $("#leadId").val();
            var answer = document.querySelector('input[name="answer"]:checked').value;
            var updateType = "";
            var doSend = "no";
            if(answer == 'candidate') {
                updateType = '4';
                doSend = "yes";
            } else if (answer == 'recruiter'){
                updateType = '5';
                doSend = "yes";
            } else if (answer == 'lost'){
                NProgress.start();
                $.ajax({
                    type: "GET",
                    url: "/updateLeadStatus/"+leadId+"/"+"3",
                    processData: false,
                    success: processLeadUpdate
                });
                NProgress.done();

            } else {
                doSend = "no";
            }

            if(doSend == "yes") {
                NProgress.start();
                $.ajax({
                    type: "GET",
                    url: "/updateLeadType/"+leadId+"/"+updateType,
                    processData: false,
                    success: processLeadUpdate
                });
                $.ajax({
                    type: "GET",
                    url: "/updateLeadStatus/"+leadId+"/"+"2",
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
        var leadView = "1";
        $.ajax({
            type: "GET",
            url: "/getAll/" + leadView,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForSupport
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    $('#showAll').click(function() {
        $(this).toggleClass("active");
        $("#showLead").removeClass("active");
        $("#showCandidate").removeClass("active");
    });
    $('#showLead').click(function() {
        $(this).toggleClass("active");
        $("#showAll").removeClass("active");
        $("#showCandidate").removeClass("active");
    });
    $('#showCandidate').click(function() {
        $(this).toggleClass("active");
        $("#showLead").removeClass("active");
        $("#showAll").removeClass("active");
    });
});

