$(document).ready(function(){
    getAllJobs(index);
});

function getAllJobs(index) {
    try {
        $.ajax({
            type: "POST",
            url: "/getPartnerViewJobs/?i=" + index,
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

function processDataAllJobPosts(returnedData) {
    var _jobPostList = returnedData.allJobPost;
    var jobPostCount = Object.keys(_jobPostList).length;
    if(jobPostCount > 0){
        var numberOfPages = parseInt(returnedData.totalJobs)/5;
        var rem = parseInt(returnedData.totalJobs) % 5;
        if(rem > 0){
            numberOfPages ++;
        }
        if(index == 0){
            pagination(numberOfPages);
        }
        $("#job_cards_inc").html("");
        var parent = $("#job_cards_inc");

        var loaderBackgroundDiv = document.createElement("div");
        loaderBackgroundDiv.id = "backgroundLoader";
        parent.append(loaderBackgroundDiv);


        $("#backgroundLoader").hide();
        $("#jobLoaderDiv").hide();
        cardModule.method.genNewJobCard(_jobPostList, parent);
    } else{
        $("#backgroundLoader").hide();
        $("#jobLoaderDiv").hide();
        var parent = $("#job_cards_inc");
        var hotJobItem = document.createElement("div");
        hotJobItem.style = "margin: 56px";
        parent.append(hotJobItem);

        var centreTag = document.createElement("center");
        hotJobItem.appendChild(centreTag);

        var rowDiv = document.createElement("div");
        rowDiv.className = "row";
        centreTag.appendChild(rowDiv);

        var col = document.createElement("div");
        col.className = "col-sm-12";
        rowDiv.appendChild(col);

        var jobImage = document.createElement("div");
        jobImage.id = "jobImage";
        col.appendChild(jobImage);

        var jobImageSrc = document.createElement("img");
        jobImageSrc.id = "jobImageSrc";
        jobImage.appendChild(jobImageSrc);
        $("#jobImageSrc").attr('src', '/assets/recruiter/img/empty_box.svg');
        $("#jobImageSrc").attr('width', '96px');

        var jobMsgLine1 = document.createElement("div");
        jobMsgLine1.id = "jobMsgLine1";
        col.appendChild(jobMsgLine1);
        $("#jobMsgLine1").html("No jobs found which were posted by the company");
    }

    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").delay(500).fadeOut("slow");
}

function pagination(noOfPages){
    $('#jobCardControl').twbsPagination({
        totalPages: noOfPages,
        visiblePages: 5,
        onPageClick: function (event, page) {
            if(page > 0 ){
                index = (page - 1)*5;
            }
            else{
                index = 0;
            }
            getAllJobs(index);
            $("#backgroundLoader").show();
            $("#jobLoaderDiv").show();
            $(".page-link").click(function(){
                $('html, body').animate({scrollTop: $("#job_cards_inc").offset().top - 100}, 800);
            });
        }
    });
}
