/**
 * Created by hawk on 3/10/16.
 */

var localityArray = [];
var jobArray = [];

function getLocality(){
    return localityArray;
}

function getJob(){
    return jobArray;
}

function resetPassword() {
    $("#loginModalHeading").html("Reset Password");
    $('#loginModal').hide();
    $('#form_forgot_password').show();
}


///Scroll
$(document).scroll(function(){
    if ($(this).scrollTop() > 80) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});
$(document).scroll(function() {
    if($(this).scrollTop()>=$('#companyHiringBox').position().top){
        $("#candidateStats").css("opacity","1");
        $("#jobRolesStats").css("opacity","1");
    }
});
function processDataCheckLocality(returnedData) {
    returnedData.forEach(function(locality) {
        var id = locality.localityId;
        var name = locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        localityArray.push(item);
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobLocationOption').append(option);
        option = $('<option value=' + id + '></option>').text(name);
        $('#jobLocationOptionModal').append(option);
    });
}

function processDataCheckJobs(returnedData) {
    returnedData.forEach(function(job) {
        var id = job.jobRoleId;
        var name = job.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        jobArray.push(item);
        var option = $('<option value=' + id + '></option>').text(name);
        $('#jobRoleOption').append(option);
        option = $('<option value=' + id + '></option>').text(name);
        $('#jobRoleOptionModal').append(option);
    });
}
///modal
$(document).ready(function(){
    $('select').material_select();
    $('.button-collapse').sideNav({
            menuWidth: 240,
            edge: 'left',
            closeOnClick: true
    });
    $('.parallax').parallax();
    $('.modal-trigger').leanModal({dismissible: true});

    $('.typist')
        .typist({
            text: 'delivery executives'
        })
        .typistPause(1000)
        .typistRemove(19)
        .typistPause(500)
        .typistAdd('drivers')
        .typistPause(1000)
        .typistRemove(7)
        .typistPause(500)
        .typistAdd('telecallers')
        .typistPause(1000)
        .typistRemove(11)
        .typistPause(500)
        .typistAdd('sales executives')
        .typistPause(1000)
        .typistRemove(16)
        .typistPause(500)
        .typistAdd('now!')
        .typistStop();

    try {
        $.ajax({
            type: "POST",
            url: "/getAllLocality",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckLocality
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "POST",
            url: "/getAllJobs",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckJobs
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

$(window).load(function() {
    if(window.location.href.indexOf('#signin') != -1) {
        $("#modalLogIn").openModal();
    }
});
