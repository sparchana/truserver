/**
 * Created by batcoder1 on 22/6/16.
 */

function checkUserLogin(){
    var userMobile = localStorage.getItem("mobile");
    var userName = localStorage.getItem("name");
    var userLastName = localStorage.getItem("lastName");
    if(userMobile != null){
        try{
            if(localStorage.getItem("gender") == 0){
                $("#userImg").attr('src', '/assets/dashboard/material/img/userMale.png');
            } else{
                $("#userImg").attr('src', '/assets/dashboard/material/img/userFemale.png');
            }
            if(userLastName == "null" || userLastName == null){
                document.getElementById("userName").innerHTML = userName;
            } else{
                document.getElementById("userName").innerHTML = userName + " " + userLastName;
            }
            document.getElementById("userMobile").innerHTML = userMobile;
        } catch(err){
        }
    }
    else{
        logoutUser();
    }
    if(localStorage.getItem("assessed") == 0){
        $(".assessmentComplete").hide();
        $(".assessmentIncomplete").show();
    } else{
        $(".assessmentIncomplete").hide();
        $(".assessmentComplete").show();
    }

    if(localStorage.getItem("minProfile") == 1){ // profile complete
        $(".profileComplete").show();
        $(".profileIncomplete").hide();
    } else{
        $(".profileIncomplete").show();
        $(".profileComplete").hide();
    }
}

function logoutUser() {
    localStorage.clear();
    window.location = "/";
    try {
        $.ajax({
            type: "GET",
            url: "/logoutUser",
            data: false,
            contentType: false,
            processData: false,
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

