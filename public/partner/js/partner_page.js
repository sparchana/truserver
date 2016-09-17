/**
 * Created by adarsh on 10/9/16.
 */

var localityArray = [];
var jobArray = [];

function getLocality(){
    return localityArray;
}

function openPartnerLogin() {
    $('#partnerLoginMobile').val("");
    $('#partnerLoginPassword').val("");
    $('#form_login_partner').show();
    $('#form_forgot_password').hide();
    $('#partnerLoginModal').modal('show');
    $("#signInPopup").html("Sign In to Trujobs Partner");
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_forgot_password').hide();
    $('#errorMsgReset').hide();
    $('#form_password_reset_otp').hide();
    $('#form_password_reset_new').hide();
}

function openSignUp() {
    $('#partnerLoginModal').modal('hide');
}

function resetPassword() {
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_login_partner').hide();
    $('#form_forgot_password').show();
}

$(window).load(function() {
    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").delay(500).fadeOut("slow");
});

function checkPartnerLogin() {
    try {
        $.ajax({
            type: "GET",
            url: "/checkPartnerSession",
            data: false,
            contentType: false,
            processData: false,
            success: processDataPartnerSession
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataPartnerSession(returnedData) {
    if(returnedData == 0){
        logoutUser();
    }
}


function processDataUpdateProfile(returnedData) {
    if(returnedData.status == 1){
        window.location = "/partner/home";
    } else{
        $("#registerBtnSubmit").addClass("btn-primary").removeClass("appliedBtn").prop('disabled', false).html("Save");
        notifyError("Something went wrong while updating profile");
    }
}


$(document).ready(function() {
    //getting all localities
    try {
        $.ajax({
            type: "POST",
            url: "/getAllLocality",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckLocality
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    //getting all partner types
    try {
        $.ajax({
            type: "POST",
            url: "/getAllPartnerType",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckPartnerType
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

});

function getJob() {
    //getting all jobRoles
    try {
        $.ajax({
            type: "POST",
            url: "/getAllJobs",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckJobs
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    } return jobArray
}

function processDataCheckJobs(returnedData) {
    returnedData.forEach(function(jobRole)
    {
        var id = jobRole.jobRoleId;
        var name = jobRole.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        jobArray.push(item);
    });
}

function processDataCheckLocality(returnedData) {
    returnedData.forEach(function(locality) {
        var id = locality.localityId;
        var name = locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        localityArray.push(item);
    });
    try{
        $("#partnerLocality").tokenInput(getLocality(), {
            theme: "facebook",
            placeholder: "Your Locality",
            minChars: 3,
            tokenLimit: 1,
            hintText: "Start Typing Area (eg: Whitefield, Agara, etc..)",
            preventDuplicates: true
        });
    } catch(e){}
}

function processDataCheckPartnerType(returnedData) {
    try{
        var defaultOption=$('<option value="-1"></option>').text("Select Organization Type");
        $('#partnerType').append(defaultOption);
        returnedData.forEach(function(partnerType) {
            var id = partnerType.partnerTypeId;
            var name = partnerType.partnerTypeName;
            var option=$('<option value=' + id + '></option>').text(name);
            $('#partnerType').append(option);
        });
    } catch (e){
        console.log("exception!");
    }
}

function logoutPartner() {
    localStorage.clear();
    window.location = "/partner";
    try {
        $.ajax({
            type: "GET",
            url: "/logoutPartner",
            data: false,
            contentType: false,
            processData: false,
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

// rendering data table in partner/myCandidates
function viewCandidate(leadId) {
    window.open(
        '/partner/candidate/' + leadId,
        '_blank' // <- This is what makes it open in a new window.
    );
/*    window.location = "/partner/candidate/" + leadId;*/
}

function verifyCandidate(mobile) {
    notifyWarning("Sending verification SMS to candidate with mobile: " + ("+" + mobile));
    try {
        $.ajax({
            type: "POST",
            url: "/sendCandidateVerificationSMS/" + ("+" + mobile),
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataVerificationMsgCheck
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataVerificationMsgCheck(returnedData) {
    if(returnedData == '1'){
        notifySuccess("Verification SMS sent!")
    } else{
        notifyError("Verification SMS sending failed!")
    }
}

function renderCandidateTable() {
    try {
        var table = $('table#leadTable').DataTable({
            "ajax": {
                "url": "/getMyCandidates",
                "dataSrc": function (returnedData) {
                    var returned_data = new Array();
                    returnedData.forEach(function (candidate) {
                        returned_data.push({
                            'candidateId': candidate.candidateId,
                            'candidateName' : candidate.candidateName,
                            'candidateMobile' :  candidate.candidateMobile,
                            'candidateCreationTimestamp' : candidate.creationTimestamp,
                            'candidateStatus' : function() {
                                if (candidate.candidateStatus != null){
                                    if(candidate.candidateStatus == "1"){
                                        var statusVal;
                                        if(candidate.candidateActiveDeactive == '1'){
                                            statusVal = "Active";
                                            return '<img src=\"/assets/partner/img/verified.png\" width=\"22px\" style=\"display: inline-block\" /><div style=\"display: inline-block; \" ><font color="#00b334" size=\"2\">&nbsp;&nbsp;' + statusVal +'</font></div>';
                                        } else{
                                            statusVal = "Deactivated";
                                            return '<img src=\"/assets/partner/img/not_verified.svg\" width=\"22px\" style=\"display: inline-block\" /><div style=\"display: inline-block; \" ><font size=\"2\">&nbsp;&nbsp;' + statusVal +'</font></div>';
                                        }
                                    } else{
                                        return '<img src=\"/assets/partner/img/not_verified.svg\" width=\"22px\" style=\"display: inline-block\" /><div style=\"display: inline-block; cursor: hand\" onclick=\"verifyCandidate('+ candidate.candidateMobile+')\"><font size=\"2\">&nbsp;&nbsp;Verify via SMS</font></div>';
                                    }
                                } else {
                                    return "-";
                                }
                            },
                            'btnView' : '<input type="submit" value="View/Edit" style="width:100%" onclick="viewCandidate('+candidate.leadId+')" id="viewCandidateBtn" class="btn btn-primary">'
                        })
                    });
                    return returned_data;
                }
            },
            "deferRender": true,
            "columns": [
                { "data": "candidateId" },
                { "data": "candidateName" },
                { "data": "candidateMobile" },
                { "data": "candidateCreationTimestamp" },
                { "data": "candidateStatus" },
                { "data": "btnView" }
            ],
            rowReorder: {
                selector: 'td:nth-child(2)'
            },
            "order": [[3, "desc"]],
            responsive: true,
            "destroy": true
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function notifyWarning(msg){
    $.notify(msg, "info");
}

function notifyError(msg){
    $.notify(msg, "error");
}

function notifySuccess(msg){
    $.notify(msg, "success");
}