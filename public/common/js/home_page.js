/**
 * Created by batcoder1 on 7/6/16.
 */

var localityArray = [];
var jobArray = [];
var prefLocation;
var prefLocationName;
var index = 0;

function getLocality(){
    return localityArray;
}

function getJob(){
    return jobArray;
}

function processDataCheckLocality(returnedData) {
    returnedData.forEach(function(locality)
    {
        var id = locality.localityId;
        var name = locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        localityArray.push(item);
    });
}

function processDataCheckJobs(returnedData) {
    returnedData.forEach(function(job)
    {
        var id = job.jobRoleId;
        var name = job.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        jobArray.push(item);
    });
}
$(function() {
    $('a[href*="#"]:not([href="#"])').click(function() {
        if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
            var target = $(this.hash);
            target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
            if (target.length) {
                $('html, body').animate({
                    scrollTop: target.offset().top - 92
                }, 1000);
            }
        }
    });
});
$(function () {
    $('#myRegistrationModal').on('hidden.bs.modal', function () {
        document.getElementById("registerBtn").disabled = false;
        window.location = "/"
    })
});

$(window).load(function(){
    if(window.location.href.indexOf('#signin') != -1) {
        $('#myLoginModal').modal('show');
    }

    var autoPlay = $("#hiringCompanyLogo");
    autoPlay.trigger('owl.play',2200);
});

$(document).ready(function(){

    $(".navbar-nav li a").click(function(event) {
        $(".navbar-collapse").collapse('hide');
    });
    var w = window.innerWidth;
    if(w < 440){
        $(".navbar-default").css('background-color', 'white');
    }
    /*$(window).scroll(function() {
        if ($(document).scrollTop() > 150) {
            $("#fixed-menu").css('background-color', '#2980b9');
            $("#fixed-menu").fadeIn();
            $(".navbar-default").css('background-color', 'white');
        } else {
            $("#fixed-menu").css('background-color', 'rgba(0, 0, 0, 0.175)');
            $("#fixed-menu").fadeOut();
        }
    });*/
    $(window).scroll(function(){
        if ($(this).scrollTop() > 350) {
            $('.registerBox').fadeIn();
            if(w > 400){
                $('.registerBox').css("width","120px");
            }
            else{
                $('.registerBox').css("width","100%");
            }
        } else {
            $('.registerBox').fadeOut();
            $('.registerBox').css("width","50px");
        }
    });
    $(window).scroll(function(){
        if ($(this).scrollTop() > 100) {
            $('.scrollToTop').fadeIn();
        } else {
            $('.scrollToTop').fadeOut();
        }
    });

    //Click event to scroll to top
    $('.scrollToTop').click(function(){
        $('html, body').animate({scrollTop : 0},800);
        return false;
    });

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

    $("#hiringCompanyLogo").owlCarousel({
        items : 4,
        itemsMobile : true,
        jsonPath : '/getAllCompanyLogos',
        jsonSuccess : customDataSuccess
    });
});

function customDataSuccess(data){
    var content = "";
    data.forEach(function (logo) {
        var img = logo;
        content += "<img width='150px' height='50px' src=\"" +img+ "\">"
    });
    $("#hiringCompanyLogo").html(content);

    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").delay(500).fadeOut("slow");
}

function createAndAppendDivider(title) {
    var parent = $("#hotJobs");

    var mainDiv = document.createElement("div");
    mainDiv.id = "hotJobItemDivider";
    parent.append(mainDiv);

    var otherJobIcon = document.createElement("img");
    otherJobIcon.src = "/assets/common/img/suitcase.png";
    otherJobIcon.style = "width: 42px; margin: 8px";
    otherJobIcon.setAttribute("display", "inline-block");
    mainDiv.appendChild(otherJobIcon);

    var hotJobItem = document.createElement("span");
    hotJobItem.setAttribute("display", "inline-block");
    hotJobItem.textContent = title;

    mainDiv.appendChild(hotJobItem);
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
        $("#hotJobs").html("");
        var parent = $("#hotJobs");

        var loaderBackgroundDiv = document.createElement("div");
        loaderBackgroundDiv.id = "backgroundLoader";
        parent.append(loaderBackgroundDiv);


        $("#backgroundLoader").hide();
        $("#jobLoaderDiv").hide();
        cardModule.method.genNewJobCard(_jobPostList, parent);
    }else{
        $("#backgroundLoader").hide();
        $("#jobLoaderDiv").hide();
        var parent = $("#hotJobs");
        var hotJobItem = document.createElement("div");
        hotJobItem.id = "hotJobItem";
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
        $("#jobImageSrc").attr('src', '/assets/common/img/empty-search.svg');

        var jobMsgLine1 = document.createElement("div");
        jobMsgLine1.id = "jobMsgLine1";
        col.appendChild(jobMsgLine1);
        $("#jobMsgLine1").html("Oops!! No relevant jobs found at this moment");

        var jobMsgLine2 = document.createElement("div");
        jobMsgLine2.id = "jobMsgLine2";
        col.appendChild(jobMsgLine2);
        $("#jobMsgLine2").html("Register yourself to get updates when new jobs are posted");
    }

    $('html, body').css({
        'overflow': 'auto',
        'height': 'auto'
    });
    $("#status").fadeOut();
    $("#loaderLogo").fadeOut();
    $("#preloader").delay(500).fadeOut("slow");
}

$(function() {
    $("#jobLocality").change(function (){
        if($(this).val() != -1){
            prefLocation = $(this).val();
            prefLocationName = $("#jobLocality option:selected").text();
            $("#applyButton").show();
        } else{
            $("#applyButton").hide();
        }
    });
});

function processCheckLeadStatus() {
    $("#addLeadMobile").val("");
    $("#messagePromptModal").modal("show");
    $("#customMsg").html("Thanks! We will get back soon!");
}

function addLead() {
    var phone = $('#addLeadMobile').val();
    var res = validateMobile(phone);
    if(res == 0){ // invalid mobile
        alert("Enter a valid mobile number");
    } else if(res == 1){ // mobile no. less than 1 digits
        alert("Enter 10 digit mobile number");
    }
    else{
        try {
            var d = {
                leadName : "",
                leadMobile : $("#addLeadMobile").val(),
                leadChannel : 0,
                leadType : 1,
                leadInterest : "General Registration"
            };
            $.ajax({
                type: "POST",
                url: "/addLead",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processCheckLeadStatus
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function openLogin() {
    $("#signInPopup").html("Sign In");
    document.getElementById("resetCheckUserBtn").disabled = false;
    document.getElementById("resetNewPasswordBtn").disabled = false;
    $('#form_login_candidate').show();
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_forgot_password').hide();
    $('#errorMsgReset').hide();
    $('#form_password_reset_otp').hide();
    $('#form_password_reset_new').hide();
    $('#noPasswordLogin').hide();
}

function openSignUp() {
    $("#myLoginModal").modal("hide");
}

function resetPassword() {
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_login_candidate').hide();
    $('#form_forgot_password').show();
}

function homeLinkPNF() {
    window.location.href = "/";

}