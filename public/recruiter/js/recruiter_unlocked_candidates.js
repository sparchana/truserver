/**
 * Created by hawk on 21/10/16.
 */
$(document).scroll(function(){
    if ($(this).scrollTop() > 30) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});
$(document).ready(function(){
    checkRecruiterLogin();
    $(".unlockNav").addClass("active");
    $(".unlockNavMobile").addClass("active");

    $('.button-collapse').sideNav({
        menuWidth: 240,
        edge: 'left',
        closeOnClick: true
    });
    try {
        $.ajax({
            type: "POST",
            url: "/recruiter/api/getUnlockedCandidates/",
            data: false,
            contentType: false,
            processData: false,
            success: processDataForUnlockedCandidates
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataForUnlockedCandidates(returnedData) {
    if(Object.keys(returnedData).length > 0){
        var parent = $("#candidateContainer");
        if(returnedData != "0"){
            returnedData.reverse();

            returnedData.forEach(function (value){

                //calling render candidate card method to render candidate card
                renderIndividualCandidateCard(value, parent, view_unlocked_candidate);
            });
            $('.tooltipped').tooltip({delay: 50});
        }
    } else{
        $("#noCandidate").show();
        $("#candidateSection").hide();
    }
}

function checkRecruiterLogin() {
    try {
        $.ajax({
            type: "GET",
            url: "/checkRecruiterSession",
            data: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterSession
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataRecruiterSession(returnedData) {
    if(returnedData == 0){
        logoutRecruiter();
    }
}

function logoutRecruiter() {
    try {
        $.ajax({
            type: "GET",
            url: "/logoutRecruiter",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataLogoutRecruiter
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataLogoutRecruiter() {
    window.location = "/recruiter";
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}

function getMonthVal(month){
    switch(month) {
        case 1:
            return "Jan";
            break;
        case 2:
            return "Feb";
            break;
        case 3:
            return "Mar";
            break;
        case 4:
            return "Apr";
            break;
        case 5:
            return "May";
            break;
        case 6:
            return "Jun";
            break;
        case 7:
            return "Jul";
            break;
        case 8:
            return "Aug";
            break;
        case 9:
            return "Sep";
            break;
        case 10:
            return "Oct";
            break;
        case 11:
            return "Nov";
            break;
        case 12:
            return "Dec";
            break;
    }
}