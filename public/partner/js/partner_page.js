/**
 * Created by adarsh on 10/9/16.
 */

var localityArray = [];
var jobArray = [];

var candidateUnVerifiedMobile;

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
        logoutPartner();
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

// rendering data table in partner/myCandidates
function viewCandidate(leadId) {
    window.open(
        '/partner/candidate/' + leadId,
        '_blank'
    );
}

function verifyCandidate(mobile) {
    candidateUnVerifiedMobile = ("+" + mobile);
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

function myCandidates(){
    window.location = "/partner/myCandidates";
}

function processDataVerificationMsgCheck(returnedData) {
    if(returnedData == '1'){
        notifySuccess("Verification SMS sent!")
        $("#messagePromptModal").modal("show");
        $('#customMsgIcon').attr('src', "/assets/partner/img/applied.png");
        $("#customMsg").html("Please verify the candidate by entering the OTP received by the candidate");
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
                            'candidateId': '<div class="mLabel" style="width:100%" >'+ candidate.candidateId + '</div>',
                            'candidateName' : '<div class="mLabel" style="width:100%" >'+ candidate.candidateName + '</div>',
                            'candidateMobile' : '<div class="mLabel" style="width:100%" >'+ candidate.candidateMobile + '</div>',
                            'candidateCreationTimestamp' : '<div class="mLabel" style="width:100%" >'+ candidate.creationTimestamp + '</div>',
                            'candidateStatus' : function() {
                                if (candidate.candidateStatus != null){
                                    if(candidate.candidateStatus == "1"){
                                        var statusVal;
                                        if(candidate.candidateActiveDeactive == '1'){
                                            statusVal = "Active";
                                            return '<div class="mLabel" style="width:100%" >'+ '<img src=\"/assets/partner/img/verified.png\" width=\"22px\" style=\"display: inline-block\" /><div style=\"display: inline-block; \" ><font color="#00b334" size=\"2\">&nbsp;&nbsp;' + statusVal +'</font></div>' +'</div>';
                                        } else{
                                            statusVal = "Deactivated";
                                            return '<div class="mLabel" style="width:100%" >'+ '<img src=\"/assets/partner/img/not_verified.svg\" width=\"22px\" style=\"display: inline-block\" /><div style=\"display: inline-block; \" ><font size=\"2\">&nbsp;&nbsp;' + statusVal +'</font></div>' +'</div>';
                                        }
                                    } else{
                                        return '<button type="button" class="mBtn orange" style="width:100%" onclick=\"verifyCandidate('+ candidate.candidateMobile+')\" >'+ '<img src=\"/assets/partner/img/warning.png\" width=\"22px\" style=\"display: inline-block\" /><div style=\"display: inline-block; cursor: hand\" >&nbsp;&nbsp;Verify</div>' +'</button>';
                                    }
                                } else {
                                    return "-";
                                }
                            },
                            'btnView' : '<button type="button" class="mBtn blue" style="width:100%" onclick="viewCandidate('+candidate.leadId+')" id="viewCandidateBtn" >'+ 'View/Edit' +'</button>'
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

function verifyCandidateOtp(){
    var candidateOtp = $("#candidateOtp").val();
    var candidateMobile = candidateUnVerifiedMobile;
    var d = {
        candidateMobile: candidateMobile,
        userOtp: candidateOtp
    };
    $("#verifyOtp").prop('disabled',true);
    $.ajax({
        type: "POST",
        url: "/verifyCandidateUsingOtp",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(d),
        success: processDataVerifyCandidate
    });
}

function processDataVerifyCandidate(returnedData) {
    $("#verifyOtp").prop('disabled', false);
    if(returnedData.status == 1){
        $('#customMsgIcon').attr('src', "/assets/partner/img/correct.png");
        $("#customMsg").html("candidate Verified");
        $("#candidateOtp").hide();
        $("#verifyOtp").hide();
        $("#homeBtn").show();
    } else if(returnedData.status == 2){
        $('#customMsgIcon').attr('src', "/assets/partner/img/wrong.png");
        $("#customMsg").html("Incorrect OTP. Please enter correct OTP!");
    } else{
        $("#customMsg").html("Something went wrong! Please try again");
        $('#customMsgIcon').attr('src', "/assets/partner/img/wrong.png");
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