$(document).ready(function() {
    $(window).scroll(function(e) {
        if ($(document).scrollTop() > 50) {
            $(".navbar-default").addClass("fade-background");
        } else{
            $(".navbar-default").removeClass("fade-background");
        }
    });
});

function loadDashboard(){
    NProgress.start();
    $(".startBody").load("/assets/dashboard/material/templates/dashboard.scala.html");
    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateInfoDashboard",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataAndFillMinProfile
        });
        NProgress.done();
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

/*
function loadEditProfile(){
    NProgress.start();
    $(".startBody").load("/assets/dashboard/material/templates/editProfile.scala.html");
    try {
        $.ajax({
            type: "GET",
            url: "/getCandidateInfoDashboard",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataAndFillMinProfile
        });
        NProgress.done();
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataAndFillMinProfile(returnedData) {
}*/
