/**
 * Created by adarsh on 10/9/16.
 *
 * dependencies :
 *          <script type="text/javascript" src="/assets/partner/js/notify.min.js"></script>
 */

var localityArray = [];
var jobArray = [];

var candidateUnVerifiedMobile;

function getLocality(){
    return localityArray;
}

var index = 0;

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
    if(window.location.href.indexOf('#signin') != -1) {
        $('#partnerLoginModal').modal('show');
    }

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
        logoutPartner();
    } else{
        if(returnedData.partnerType.partnerTypeId == 7){
            $("#jobs").remove();
            $("#openPartner").hide();
            $("#footer_inc").remove();
            $("#privatePartner").show();

            checkPartnerSwitcher();
        } else{
            $("#openPartner").show();
            $("#privatePartner").hide();
        }
    }
}

function checkPartnerSwitcher(){
    try {
        $.ajax({
            type: "GET",
            url: "/checkPrivatePartnerRecruiterAccount",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckRecruiterAccount
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataCheckRecruiterAccount(returnedData) {
    if(returnedData == 1){
        $("#accountSwitcher").show();
    } else{
        $("#accountSwitcher").hide();
    }
}

function switchToRecruiter() {
    try {
        $.ajax({
            type: "GET",
            url: "/switchToRecruiter",
            data: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterSwitch
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataRecruiterSwitch(returnedData) {
    if(returnedData == 1){
        window.location = "/recruiter/home";
    } else{
        notifyError("Something went wrong");
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

function applyJobForCandidate(candidateId) {
    localStorage.setItem("appliedJobs", "0");
    localStorage.setItem("candidateId", candidateId);
    window.location = "/partner/"+ candidateId + "/jobs";
}

function viewAppliedJobs(candidateId) {
    localStorage.setItem("appliedJobs", "1");
    localStorage.setItem("candidateId", candidateId);
    window.location = "/partner/"+ candidateId + "/jobs";
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
        var statusVal;
        var table = $('table#myCandidates').DataTable({
            "ajax": {
                "url": "/getMyCandidates",
                "dataSrc": function (returnedData) {
                    var returned_data = new Array();
                    returnedData.forEach(function (candidate) {
                        returned_data.push({
                            'candidateName' : '<div class="mLabel" style="width:100%" >'+ candidate.candidateName + '</div>',
                            'candidateMobile' : '<div class="mLabel" style="width:100%" >'+ candidate.candidateMobile + '</div>',
                            'candidateCreationTimestamp' : '<div class="mLabel" style="width:100%" >'+ candidate.creationTimestamp + '</div>',
                            'candidateStatus' : function() {
                                if (candidate.candidateStatus != null){
                                    if(candidate.candidateStatus == "1"){
                                        if(candidate.candidateActiveDeactive == '1'){
                                            statusVal = "Active";
                                            return '<div class="mLabel" style="width:100%" >'+ '<img src=\"/assets/partner/img/correct.png\" width=\"22px\" style=\"display: inline-block\" /><div style=\"display: inline-block; \" ><font color="#00b334" size=\"2\">&nbsp;&nbsp;' + statusVal +'</font></div>' +'</div>';
                                        } else{
                                            statusVal = "Deactivated";
                                            return '<div class="mLabel" style="width:100%" >'+ '<img src=\"/assets/partner/img/wrong.png\" width=\"22px\" style=\"display: inline-block\" /><div style=\"display: inline-block; \" ><font color="#dc143c" size=\"2\">&nbsp;&nbsp;' + statusVal +'</font></div>' +'</div>';
                                        }
                                    } else{
                                        statusVal = "Not Active";
                                        return '<button type="button" id="viewCandidateBtn" class="mBtn orange" onclick=\"verifyCandidate('+ candidate.candidateMobile+')\" >'+ '<img src=\"/assets/partner/img/warning.png\" width=\"16px\" style=\"display: inline-block\" /><div style=\"display: inline-block; cursor: hand\" >' +
                                            '<font color="#fff" size=\"2\">&nbsp;&nbsp;Verify</font></div>' +'</button>';
                                    }
                                } else {
                                    statusVal = "Not Active";
                                    return "-";
                                }
                            },
                            'resume': function () {
                                if(candidate.candidateResumeLink == null){
                                    return '<div id="resumeLink_'+candidate.candidateId+'" style="width:100%" >' +
                                    '<label class="mBtn blue btn-file" style="text-align: center;font-weight:100">Upload'+
                                    '<input type="file" accept=".pdf,.doc,.docx" onchange="uploadResumeCandidate(event,'+candidate.candidateId+')" style="display: none">'+
                                    '</label>'+
                                    '</div>'
                                }else{
                                    return '<a href="http://docs.google.com/gview?url='+candidate.candidateResumeLink+'&embedded=true" target="_blank">'+
                                    '<button type="button" class="mBtn blue" id="viewCandidateResumeBtn" >View</button>'+
                                    '</a>'
                                }
                            },
                            'btnView' : '<button type="button" class="mBtn blue" onclick="viewCandidate('+candidate.leadId+')" id="viewCandidateBtn" >'+ 'View/Edit' +'</button>',
                            'apply' :  function() {
                                if (statusVal.localeCompare("Active") == 0){
                                    return '<button type="button" class="mBtn" onclick="applyJobForCandidate('+candidate.candidateId+')" id="viewCandidateBtn" >'+ 'View/Apply' +'</button>';
                                } else {
                                    return '<div class="mLabel" style="width:100%" >'+ '<div style=\"display: inline-block; \" ><font color="#777773" size=\"2\">&nbsp;&nbsp;' + 'Not Allowed' +'</font></div>' +'</div>';
                                }
                            },
                            'appliedJobs' :  function() {
                                return '<div class="mCircleLabel" onclick="viewAppliedJobs('+candidate.candidateId+')">'+ candidate.candidateAppliedJobs + '</div>';
                            }
                        })
                    });
                    return returned_data;
                }
            },

            "deferRender": true,
            "columns": [
                { "data": "candidateName" },
                { "data": "candidateMobile" },
                { "data": "candidateCreationTimestamp" },
                { "data": "candidateStatus" },
                { "data": "btnView" },
                { "data": "resume" },
                { "data": "apply" },
                { "data": "appliedJobs" }
            ],
            "language": {
                "emptyTable": "Looks like you have not added any candidates yet! " + '<a data-target="#candidateCreateOptionModal" data-toggle="modal" style="color: #26A69A"> '+"Add Now!" +'</a>'
            },
            "order": [[2, "desc"]],
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

    if(validateOtp(candidateOtp) == 0){
        notifyError("Please enter a valid 4 digit otp!");
    } else{
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
}

function processDataVerifyCandidate(returnedData) {
    $("#verifyOtp").prop('disabled', false);
    if(returnedData.status == 1){
        $('#customMsgIcon').attr('src', "/assets/partner/img/correct.png");
        $("#customMsg").html("Verification completed! Candidate is successfully registered.");
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
    notify(msg, "info");
}

function notifyError(msg){
    notify(msg, "error");
}

function notifySuccess(msg){
    notify(msg, "success");
}

function notify(msg, style) {
    $.notify(msg, style);
}