/**
 * Created by batcoder1 on 10/6/16.
 */

function validateMobile(mobile) {
    var validMobile = /^[7-9]{1}[0-9]{9}$/i;
    if (mobile.length > 0 && validMobile.test(mobile) === false) {
        return 0;
    } else if (mobile.length == 0) {
        return 1;
    }
    else{
        return 2;
    }
}

function validateName(name) {
    var validName = /^[a-zA-Z]+$/;
    if (!validName.test(name)) {
        return 0;
    }
    else{
        return 1;
    }
}

function validatePassword(password) {
    var inValidPassword = /^[ ]+$/;
    if(password.length < 6){
        return 0; // less than 6 characters
    }
    if (inValidPassword.test(password)) {
        console.log("Password with blank spaces");
        return 1; // password is of blank space(s)
    }
    else{
        return 2; // valid password
    }
}