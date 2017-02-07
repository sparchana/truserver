/**
 * Created by batcoder1 on 6/7/16.
 */

var companyLocality = [];
var localityArray = [];

var interviewCredits = 0;
var contactCredits = 0;
var ctaCredits = 0;

var availableCredits = 0;
var availableContactCredits = 0;
var availableInterviewCredits = 0;

var candidateCreditTypeStatus = 1;
var interviewCreditTypeStatus = 1;
var ctaCreditTypeStatus = 1;

var isNewPack = true;
var pack;

var recruiterCreditPack = [];

function getLocality() {
    return localityArray;
}

function processDataGetCompanies(returnedData) {
    var defaultOption = $('<option value=""></option>').text("Select a company");
    $('#recruiterCompany').append(defaultOption);
    returnedData.forEach(function (company) {
        var id = company.companyId;
        var name = company.companyName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#recruiterCompany').append(option);
    });
}

function contactType(val) {
    if(val == 1){
        $("#candidateCreditSection").show();
    } else{
        $("#candidateCreditSection").hide();
    }
}

function interviewType(val) {
    if(val == 1){
        $("#interviewCreditSection").show();
    } else{
        $("#interviewCreditSection").hide();
    }
}

// CTA Begins
function callType(val) {
    if(val == 1){
        $("#callCreditSection").show();
    } else{
        $("#callCreditSection").hide();
    }
}
// CTA Ends

function computeCreditValue() {
    var hasContactCredit = false;
    var hasInterviewCredit = false;
// CTA Begins
    var hasCTACredit = false;
// CTA Ends

    if($('input:radio[name="candidateCreditType"]:checked').val() == 1){
        candidateCreditTypeStatus = 1;

        if(parseInt($("#candidateContactCredits").val()) < availableInterviewCredits){
            candidateCreditTypeStatus = 0;
            notifyError("Contact credits should be greater than existing credits")
        } else{
            contactCredits = parseInt($("#candidateContactCredits").val());
            hasContactCredit = true;
        }
    }

    if($('input:radio[name="interviewCreditType"]:checked').val() == 1){
        interviewCreditTypeStatus = 1;

        if(parseInt($("#interviewCredits").val()) < availableInterviewCredits){
            interviewCreditTypeStatus = 0;
            notifyError("Interview credits should be greater than existing credits");
        } else{
            interviewCredits = parseInt($("#interviewCredits").val());
            hasInterviewCredit = true;
        }
    }

    if($('input:radio[name="callCreditType"]:checked').val() == 1){
        ctaCreditTypeStatus = 1;
        ctaCredits = parseInt($("#ctaCredits").val());
		hasCTACredit = true;
/*
        if(parseInt($("#interviewCredits").val()) < availableInterviewCredits){
            interviewCreditTypeStatus = 0;
            notifyError("Interview credits should be greater than existing credits");
        } else{
            interviewCredits = parseInt($("#interviewCredits").val());
            hasInterviewCredit = true;
        }
*/
    }

    var expiryStatus = 1;
    var selectedDate = null;

    if(hasContactCredit || hasInterviewCredit || hasCTACredit){
        if($("#expiry_date").val() != ""){
            selectedDate = new Date($("#expiry_date").val());
            var todaysDate = new Date();

            if(selectedDate < todaysDate){
                alert("Please select expiry date greater than today");
                expiryStatus = 0;
            } else{
                $("#expiringInfo").html(selectedDate.getDate() + "-" + (selectedDate.getMonth() + 1) + "-" + selectedDate.getFullYear());
                expiryStatus = 1;
            }
        } else{
            alert("Please select expiry date ");
            expiryStatus = 0;
        }
    }

    if(interviewCreditTypeStatus == 1 && candidateCreditTypeStatus == 1 && ctaCreditTypeStatus == 1 && expiryStatus == 1){
        if(hasContactCredit){
            $("#addCreditInfoDiv").show();
            $("#contactUnlockCreditInfo").html("Adding a new contact unlock credit pack with " + contactCredits + " new credits");
        }

        if(hasInterviewCredit){
            $("#addCreditInfoDiv").show();
            $("#interviewUnlockCreditInfo").html("Adding a new interview unlock credit pack with " + interviewCredits + " new credits");
        }

        if(hasCTACredit){
            $("#addCreditInfoDiv").show();
            $("#ctaCreditInfo").html("Adding a new CTA credit pack with " + ctaCredits + " new credits");
        }

        $("#creditModal").modal("hide");
    }
}

function computeNewCreditValue() {
    var checkStatus = 1;

    if($("#recruiterAddCredit").val() == ""){
        $("#recruiterAddCredit").val(0);
    }

    if(parseInt($("#recruiterAddCredit").val()) < -(availableCredits)){
        checkStatus = 0;
        notifyError("Credits should be greater than available credits")
    }

    var selectedDate = null;
    var expirydate = null;

    if($("#expiry_date_edit_pack").val() != ""){
        selectedDate = new Date($("#expiry_date_edit_pack").val());
        var todaysDate = new Date();

        if(selectedDate < todaysDate){
            alert("Please select expiry date greater than today");
            checkStatus = 0;
        } else{
            expirydate = selectedDate.getFullYear() + "-" + (selectedDate.getMonth() + 1) + "-" + selectedDate.getDate();
            checkStatus = 1;
        }
    } else{
        checkStatus = 1;
    }

    if(checkStatus == 1){
        if((expirydate == null) && ($("#recruiterAddCredit").val() == 0)){
            $("#editCreditModal").modal("hide");
        } else{
            var d = {
                recruiterMobile: $("#recruiterMobile").val(),
                creditCount: parseInt($("#recruiterAddCredit").val()),
                packId: pack.recruiterCreditPackNo,
                expiryDate: expirydate
            };

            try {
                $.ajax({
                    type: "POST",
                    url: "/updateRecruiterCreditPack",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataUpdatePack
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }

        }
    }
}

function processDataUpdatePack(returnedData) {
    if(returnedData.status == 1){
        alert("Pack Updated successfully!");
    } else{
        alert("Something went wrong, Please try again");
    }
    location.reload();

}

/*
function processDataGetCreditCategory(returnedData) {
}
*/

$(function(){
    var pathname = window.location.pathname; // Returns path only
    var recruiterIdUrl = pathname.split('/');
    var recruiterId = recruiterIdUrl[(recruiterIdUrl.length)-1];
    
    try {
        $.ajax({
            type: "GET",
            url: "/getAllCompany",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetCompanies
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
/*
    try {
        $.ajax({
            type: "POST",
            url: "/getAllCreditCategory",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetCreditCategory
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
*/

    try {
        $.ajax({
            type: "GET",
            url: "/getRecruiterInfo/" + recruiterId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForRecruiterInfo
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    
});

function saveRecruiter() {
    var selectedDate = null;
    var creditExpiryDate = null;
    var status = 1;

    if(contactCredits > 0 || interviewCredits > 0){

        if($("#expiry_date").val() != ""){
            selectedDate = new Date($("#expiry_date").val());
            var todaysDate = new Date();

            if(selectedDate < todaysDate){
                alert("Please select expiry date greater than today");
                status = 0;
            } else{
                creditExpiryDate = selectedDate.getFullYear() + "-" + (selectedDate.getMonth() + 1) + "-" + selectedDate.getDate();
            }

        }

    }


    if(status == 1){
        var d = {
            recruiterName: $("#recruiterName").val(),
            recruiterMobile: $("#recruiterMobile").val(),
            recruiterLandline: $("#recruiterLandline").val(),
            recruiterEmail: $("#recruiterEmail").val(),
            recruiterCompany: $("#recruiterCompany").val(),
            contactCredits: contactCredits,
            interviewCredits: interviewCredits,
            ctaCredits: ctaCredits,
            expiryDate: creditExpiryDate
        };

        try {
            $.ajax({
                type: "POST",
                url: "/addRecruiter",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataAddRecruiter
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function processDataAddRecruiter(returnedData) {
    if(returnedData.status == 4){
        alert("Recruiter Update Successful!");
        location.reload();
    } else{
        alert("Something went wrong, Please try again");
    }
}

function processDataForRecruiterInfo(returnedData) {

    $("#recruiterName").val(returnedData.recruiterProfileName);
    $("#recruiterMobile").val(returnedData.recruiterProfileMobile);
    if(returnedData.recruiterProfileLandline != null ){
        $("#recruiterLandline").val(returnedData.recruiterProfileLandline);
    }

    if(returnedData.recruiterProfileEmail != null ){
        $("#recruiterEmail").val(returnedData.recruiterProfileEmail);
    }

    if(returnedData.company != null ){
        $("#recruiterCompany").val(returnedData.company.companyId);
    }

    $("#recruiterContactCredits").val(returnedData.contactCreditCount);
    $("#recruiterInterviewCredits").val(returnedData.interviewCreditCount);

    var list = returnedData.recruiterCreditHistoryList;
    var i, pos = -1;
    list.forEach(function (history) {
        pos = -1;
        for(i=0; i<recruiterCreditPack.length; i++){
            if(recruiterCreditPack[i].recruiterCreditPackNo == history.recruiterCreditPackNo){
                pos = i;
            }
        }
        if(pos != -1){
            recruiterCreditPack[pos] = history;
        } else{
            recruiterCreditPack.push(history);
        }
    });

    //rendering datatable
    var t = $('table#creditHistory').DataTable();

    var packIndex = -1;
    recruiterCreditPack.forEach(function (pack) {
        packIndex++;
        t.row.add( [
            pack.recruiterCreditPackNo,
            function(){
                if(pack.recruiterCreditCategory != null){
                    return pack.recruiterCreditCategory.recruiterCreditType;
                } else {
                    return " Not Specified";
                }
            },
            pack.recruiterCreditsAvailable,
            function() {
                if(pack.creditIsExpired == false){
                    return "Not Expired" + '<span onclick="expireCreditPack(' + packIndex + ')" style="padding: 4px; cursor: pointer; background: #d9534f; margin-left: 6px; color: white">Expire now</span>';
                } else{
                    return "Expired";
                }
            },
            function() {
                if(pack.expiryDate != null)
                    return getDateTime(pack.expiryDate);
                else
                    return "Not available";
            },
            function() {
                if(pack.creditIsExpired == false){
                    return '<div class="btn btn-default" onclick="editExistingPack(' + packIndex + ')" style="background: green; color: white">Add/Remove/Edit Pack</div>';
                } else{
                    return "Pack is expired";
                }
            }
        ] ).order([[0, "asc"]]).draw( false );

    });
}

function editExistingPack(pos) {
    isNewPack = false;
    pack = recruiterCreditPack[pos];
    availableCredits = pack.recruiterCreditsAvailable;

    $("#packInfo").html("Add more credits to pack no: " + pack.recruiterCreditPackNo);
    $("#packCredits").html("Available Credits: " + availableCredits);

    $("#editCreditModal").modal("show");
}

function expireCreditPack(pos) {
    pack = recruiterCreditPack[pos];

    var d = {
        recruiterMobile: $("#recruiterMobile").val(),
        packId: pack.recruiterCreditPackNo
    };

    try {
        $.ajax({
            type: "POST",
            url: "/expireCreditPack",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataExpirePack
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataExpirePack(returnedData) {
    if(returnedData.status == 1){
        alert("Pack expired successfully!");
    } else{
        alert("Something went wrong, Please try again");
    }
    location.reload();
}

function closeCreditModal() {
    $("#creditModal").modal("hide");
}

function closeEditCreditModal() {
    $("#editCreditModal").modal("hide");
}

function openCreditModal() {
    isNewPack = true;
    availableContactCredits = 0;
    availableInterviewCredits = 0;

    $("#creditModal").modal("show");
}

function notifyError(msg){
    $.notify({
        message: msg,
        animate: {
            enter: 'animated lightSpeedIn',
            exit: 'animated lightSpeedOut'
        }
    },{
        type: 'danger'
    });
}

function getDateTime(value) {
    // 2016-07-20 21:18:07
    /*
     * getUTCMonth(): Returns the month according to the UTC (0 - 11).
     * getUTCFullYear(): Returns the four-digit year according to the UTC.
     */
    var dateTime = new Date(value).getUTCFullYear() + "-" + ("0" + (new Date(value).getUTCMonth() + 1)).slice(-2)
        + "-" + ("0" + new Date(value).getDate()).slice(-2);
    return dateTime;
}
