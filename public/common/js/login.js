/**
 * Created by batcoder1 on 26/4/16.
 */
var candidateMobile;
var applyJobFlag = 0;
var applyJobId = 0;

function postLogin(returnedData) {
    // Store
    localStorage.setItem("gender", returnedData.gender);
    localStorage.setItem("mobile", "+91" + candidateMobile);
    localStorage.setItem("name", returnedData.candidateFirstName);
    localStorage.setItem("lastName", returnedData.candidateLastName);
    localStorage.setItem("assessed", returnedData.isAssessed);
    localStorage.setItem("minProfile", returnedData.minProfile);

    if(applyJobFlag == 1){
        $("#myLoginModal").modal("hide");
        /*applyJobSubmitViaCandidate(applyJobId, prefLocation, prefTimeSlot, scheduledInterviewDate, true);*/
        //applyJob(applyJobId, prefLocation, false);
        $("#customSubMsg").html("Logging in ...");
        $('#customSubMsg').modal({backdrop: 'static', keyboard: false});
        var jp_id = applyJobId;
        applyJobFlag = 0;
        applyJobId = 0;
        window.location = "/dashboard/appliedJobs/?ps=true&jp_id="+jp_id+"&pref_loc="+prefLocation;
        /*setTimeout(function(){
            window.location = "/dashboard/appliedJobs/?ps=true&jp_id="+jp_id+"&prefLoc="+prefLocation;
        }, 3000);*/
    } else{
        window.location = "/dashboard";
    }
}
function processDataLogin(returnedData) {
    if(returnedData.status == 1) {
        if(returnedData.isCandidateVerified == 0){ //candidate has not been verified
            $("#myLoginModal").modal("hide");
            $('#myRegistrationModal').modal('show');
            $('#form_otp').show();
            requestOtp(candidateMobile);
        } else{
            postLogin(returnedData);
        }
    }

    else if(returnedData.status == 3){
        $('#noUserLogin').show();
        $('#noPasswordLogin').hide();
        $('#incorrectMsgLogin').hide();
    }

    else if(returnedData.status == 5){
        $('#noPasswordLogin').show();
        $('#noUserLogin').hide();
        $('#incorrectMsgLogin').hide();
    }

    else {
        $('#noUserLogin').hide();
        $('#noPasswordLogin').hide();
        $('#incorrectMsgLogin').show();
    }
}

// form_candidate ajax script
$(function() {
    $("#form_login_candidate").submit(function(eventObj) {
        eventObj.preventDefault();
        var phone  = $('#candidateLoginMobile').val();
        var password = $('#candidateLoginPassword').val();
        if(phone == null || phone == ""){
            alert("Enter your Phone Number");
        } else if(password == null || password == ""){
            alert("Enter your Password");
        }
        else{
            try {

                candidateMobile = phone;
                var s = {
                    candidateLoginMobile: phone,
                    candidateLoginPassword : password
                };
                $.ajax({
                    type: "POST",
                    url: "/loginSubmit",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(s),
                    success: processDataLogin
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    }); // end of submit
}); // end of function