/**
 * Created by batcoder1 on 22/6/16.
 */

function checkUserLogin(){
    var userMobile = localStorage.getItem("mobile");
    var userName = localStorage.getItem("name");
    var userLastName = localStorage.getItem("lastName");
    if(userMobile != null){
        document.getElementById("helloMsg").innerHTML = "Hello " + userName + "!";
        try{
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
        window.location = "/";
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

