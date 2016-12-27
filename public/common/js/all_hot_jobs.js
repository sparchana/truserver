/**
 * Created by adarsh on 7/9/16.
 */

$(document).ready(function(){
    getAllJobs(index);
});

function getAllJobs(index) {
    try {
        $.ajax({
            type: "POST",
            url: "/getAllHotJobPosts/?i=" + index,
            data: false,
            async: true,
            contentType: false,
            processData: false,
            success: processDataAllJobPosts
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    $(".first").hide();
    $(".last").hide();
    $(".prev a").html("<<");
    $(".next a").html(">>");
}