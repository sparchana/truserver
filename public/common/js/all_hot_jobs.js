/**
 * Created by adarsh on 7/9/16.
 */

var index = 0;

$(document).ready(function(){
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
});
