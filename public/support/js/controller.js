
var globalAgentNumber;

function cleaModalBackDrop() {
    $('#newLeadEntryModal').modal('hide');
    $('body').removeClass('modal-open');
    $('.modal-backdrop').remove();
}

function clearModal() {

    $("#candidateFirstName").val("");
    $("#candidateMobile").val("");
    $("#candidateJobInterest").val("");
    $("#candidateType").val("");
    $("#candidateNote").val("");
}

function fillModal(returnedData) {

    $("#candidateFirstName").val(returnedData.candidateFirstName);
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
    window.location="/candidateSignupSupport/"+id+"/true";
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

function renderDashboard(viewType) {
    try {
    var table = $('table#leadTable').DataTable({
        "ajax": {
            "url": "/getAll/" + viewType,
            "dataSrc": function (returnedData) {
                var returned_data = new Array();
                returnedData.forEach(function (newLead) {
                    returned_data.push({
                        'leadId': newLead.leadId,
                        'leadType' : newLead.leadType,
                        'leadStatus' : newLead.leadStatus,
                        'leadCreationTimestamp' : newLead.leadCreationTimestamp,
                        'totalInBounds' :  '<a href="'+"/candidateInteraction/"+newLead.leadId+'" id="'+newLead.leadId+'" style="cursor:pointer;" target="_blank">'+newLead.totalInBounds+'</a>',
                        'lastIncomingCallTimestamp'  :  newLead.lastIncomingCallTimestamp,
                        'leadChannel' : newLead.leadChannel,
                        'leadMobile' : newLead.leadMobile,
                        'leadName' :  newLead.leadName,
                        'leadFollowUpTimestamp' :  function() {
                            if (newLead.followUpStatus != null || newLead.followUpStatus == true){
                                return newLead.followUpTimeStamp
                            } else {
                                return ""
                            }
                        },
                        'btnFUA' : function(){
                            if(newLead.leadStatus == 'New') {
                                return '<input type="submit" value="Call"  style="width:100px" onclick="myHandler('+newLead.leadMobile+', '+newLead.leadId+');" id="'+newLead.leadId+'" class="btn btn-primary">'
                            } else if (newLead.followUpStatus == true){
                                return '<input title="'+newLead.followUpTimeStamp+'" type="submit" value="Follow Up"  style="width:100px" onclick="myHandler('+newLead.leadMobile+', '+newLead.leadId+'); " id="'+newLead.leadId+'"  class="btn btn-warning">'
                            } else {
                                return '<input type="submit" value="Call Back"  style="width:100px" onclick="myHandler('+newLead.leadMobile+', '+newLead.leadId+'); " id="'+newLead.leadId+'"  class="btn btn-default">'
                            }
                        }
                    })
                });
                return returned_data;
            }
        },
        "deferRender": true,
        "columns": [
            { "data": "leadId" },
            { "data": "leadType" },
            { "data": "leadStatus" },
            { "data": "leadCreationTimestamp" },
            { "data": "totalInBounds" },
            { "data": "lastIncomingCallTimestamp" },
            { "data": "leadChannel" },
            { "data": "leadMobile" },
            { "data": "leadName" },
            { "data": "leadFollowUpTimestamp" },
            { "data": "btnFUA" }
        ],
        "order": [[5, "desc"]],
        "language": {
            "emptyTable": "No data available"
        },
        "destroy": true
    });
} catch (exception) {
    console.log("exception occured!!" + exception);
}
}

function getThis(viewType){
        NProgress.start();
        if ( $.fn.dataTable.isDataTable( 'table#leadTable' ) ) {
            $('table#leadTable').DataTable().clear();
        }
        renderDashboard(viewType);
        NProgress.done();
}


function processSupportAgentData(returnedData) {
    var mobileNum = returnedData.agentMobileNumber;

    if(mobileNum != null){
        globalAgentNumber = mobileNum;
    }
}

function myHandler (mobile, id) {
    if(typeof globalAgentNumber != 'undefined'){
        console.log("Call Initiated for " +"+"+ mobile + " by " + globalAgentNumber);
        var s = {
            api_key: "dae93473-50a6-11e5-bbe8-067cf20e9301",
            agent_number: globalAgentNumber,
            phone_number: "+"+mobile,
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
                success: function (returnedData) {
                    console.log("KW Response : " + JSON.stringify(returnedData));
                    openCreateCandidate(id);
                },
                error: function (error) {
                    JSON.stringify(error);
                    console.log("Response E: " + JSON.stringify(error));
                    openCreateCandidate(id);
                }
            });
        } catch (exception) {
        }
    } else {
        openCreateCandidate(id);
    }

}

$(function(){
    /* ajax commands to fetch supportAgent Info */
    try {
        $.ajax({
            type: "GET",
            url: "/getSupportAgent",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processSupportAgentData
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    getThis(1);
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

