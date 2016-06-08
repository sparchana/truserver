/**
 * Created by batcoder1 on 7/6/16.
 */

var localityArray = [];
var jobArray = [];

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

$(function () {
    $('#myRegistrationModal').on('hidden.bs.modal', function () {
        document.getElementById("registerBtn").disabled = false;
        $('#form_signup_candidate').trigger("reset");
    })
});

$(document).ready(function(){
    $(".navbar-nav li a").click(function(event) {
        $(".navbar-collapse").collapse('hide');
    });

    var w = window.innerWidth;
    if(w < 440){
        $(".navbar-default").css('background-color', 'white');
    }
    $(window).scroll(function() {
        if ($(document).scrollTop() > 50) {
            $("#fixed-menu").css('background-color', '#2980b9');
            var w = window.innerWidth;
            if(w > 440){
                $("#leadFormText").css('margin-left', '-420px');

                $(".navbar-fixed-top").removeClass('fade-transparent').addClass("fade-background"); // if yes, then change the color of class "navbar-fixed-top" to white (#f8f8f8)
                $("#navItem1").css('color', '#747474');
                $("#navItem2").css('color', '#747474');
                $("#navItem3").css('color', '#747474');
                $("#navItem4").css('color', '#747474');
                $("#navItem5").css('color', '#747474');

                var image = document.getElementById('navLogo');
                image.src = "/assets/new/img/logo-color.gif";
            }
        } else {
            $("#fixed-menu").css('background-color', 'rgba(0, 0, 0, 0.175)');
            var w = window.innerWidth;
            if(w > 480){
                $("#leadFormText").css('margin-left', '-420px');

                $(".navbar-fixed-top").removeClass("fade-background").addClass('fade-transparent'); // if not, change it back to transparent
                $("#navItem1").css('color', '#ffffff');
                $("#navItem2").css('color', '#ffffff');
                $("#navItem3").css('color', '#ffffff');
                $("#navItem4").css('color', '#ffffff');
                $("#navItem5").css('color', '#ffffff');
                var image = document.getElementById('navLogo');
                image.src = "/assets/new/img/logo-main.gif";
                var w = window.innerWidth;
                if (w < 480) {
                    $("#fixed-menu").css('background-color', 'transparent');
                    $("#navItem1").css('color', '#747474');
                    $("#navItem2").css('color', '#747474');
                    $("#navItem3").css('color', '#747474');
                    $("#navItem4").css('color', '#747474');
                    $("#navItem5").css('color', '#747474');
                }
            }
        }
    });
    
    try {
        $.ajax({
            type: "GET",
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

    try {
        $.ajax({
            type: "GET",
            url: "/getAllJobs",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataCheckJobs
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processCheckLeadStatus() {
    alert("Thanks! We will get back soon!");
    $("#addLeadMobile").val('');

}
function addLead() {
    var phone = $('#addLeadMobile').val();
    var validPhone = /^[7-9]{1}[0-9]{9}$/i;
    if (phone.length > 0 && validPhone.test(phone) === false) {
        alert("Please enter valid 10 digit mobile number");
    } else if (phone.length == 0) {
        alert("Please enter your mobile number");
    }
    else{
        try {
            var d = {
                leadName : " ",
                leadMobile : $("#addLeadMobile").val(),
                leadChannel : 0,
                leadType : 1,
                leadInterest : "General Registration"
            };
            $.ajax({
                type: "POST",
                url: "/addLead",
                data: d,
                success: processCheckLeadStatus
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function openLogin() {
    document.getElementById("resetCheckUserBtn").disabled = false;
    document.getElementById("resetNewPasswordBtn").disabled = false;
    $('#form_login_candidate').show();
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_forgot_password').hide();
    $('#errorMsgReset').hide();
    $('#form_password_reset_otp').hide();
    $('#form_password_reset_new').hide();
}

function resetPassword() {
    $('#noUserLogin').hide();
    $('#incorrectMsgLogin').hide();
    $('#form_login_candidate').hide();
    $('#form_forgot_password').show();
}