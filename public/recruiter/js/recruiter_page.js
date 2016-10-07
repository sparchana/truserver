/**
 * Created by hawk on 3/10/16.
 */

var localityArray = [];
var jobArray = [];
var requirementArray = [];

function getLocality(){
    return localityArray;
}

function getJob(){
    return jobArray;
}

function getRequirement(){
    return requirementArray;
}

///Scroll
$(document).scroll(function(){
    if ($(this).scrollTop() > 80) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.5)"});
    }
    else{
        $('nav').css({"background": "transparent"});
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
    });
}
///modal
$(document).ready(function(){
    $('select').material_select();
    $(".button-collapse").sideNav();
    $('.parallax').parallax();
    $('.modal-trigger').leanModal({dismissible: true});

    $('.typist')
        .typist({
            text: 'delivery boys'
        })
        .typistPause(1000)
        .typistRemove(13)
        .typistPause(500)
        .typistAdd('drivers')
        .typistPause(1000)
        .typistRemove(7)
        .typistPause(500)
        .typistAdd('telecallers')
        .typistPause(1000)
        .typistRemove(11)
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

    // setting requirements
    var id = "Full Time";
    var name = "Full Time";
    var item = {};
    item ["id"] = id;
    item ["name"] = name;
    requirementArray.push(item);

    id = "Part Time";
    name = "part Time";
    item = {};
    item ["id"] = id;
    item ["name"] = name;
    requirementArray.push(item);

    id = "Contract";
    name = "Contract";
    item = {};
    item ["id"] = id;
    item ["name"] = name;
    requirementArray.push(item);
});