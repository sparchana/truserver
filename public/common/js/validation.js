/**
 * Created by batcoder1 on 10/6/16.
 */

function validateMobile(mobile) {
    if(mobile == undefined){
        return 1;
    }
    var validMobile = /^[7-9]{1}[0-9]{9}$/i;
    if (mobile.length > 0 && validMobile.test(mobile) === false) {
        return 0; // format is wrong
    } else if (mobile.length == 0) {
        return 1; // not 10 digits
    }
    else{
        return 2; // passed
    }
}

function toTitleCase(str) {
    return str.replace(/\w\S*/g, function(txt){
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    });
}

function rupeeFormatSalary(sal){
    if(sal != null){
        sal = sal.toString();
        var lastThree = sal.substring(sal.length-3);
        var otherNumbers = sal.substring(0, sal.length-3);
        if(otherNumbers != '')
            lastThree = ',' + lastThree;
        return otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree;
    }
    return "";
}

function validateName(name) {
    var validName = /^[a-zA-Z]+$/;
    var spacing = /^[ ]+$/;
    var specialChars = /[^\w\s]/gi;
    var numberChar = /^[a-zA-Z0-9]+$/;

    if (validName.test(name)) {
        return 1; //correct
    } else if (numberChar.test(name)) {
        return 0; //name contains integer
    } else {
        if(spacing.test(name) == true){
            return 2; // blank spaces
        } else if(name == ""){
            return 4;
        } else{
            if(specialChars.test(name)) {
                return 3; //name has special characters
            } else{
                return 1; // name valid with space in between
            }
        }
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

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function validateInteger(val) {
    var reg = /^\d+$/;
    return reg.test(val);
}

function validateWebsiteLink(url) {
    var re = /[a-z0-9-\.]+\.[a-z]{2,4}\/?([^\s<>\#%"\,\{\}\\|\\\^\[\]`]+)?$/;
    return re.test(url);
}

function validateLinkedin(url) {
    return /(ftp|http|https):\/\/?(?:www\.)?linkedin.com(\w+:{0,1}\w*@)?(\S+)(:([0-9])+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/.test(url);
}

function validateOtp(otp) {
    if(otp == undefined){
        return 0;
    }
    var validOtp = /^[0-9]{4}$/i;
    if(otp.length == 4){
        if (validOtp.test(otp) === false) {
            return 0; // format is wrong
        } else{
            return 1; // success
        }
    } else{
        return 0; // otp is not 4 digits
    }
}

function isValidSalary(salary){
    return !/[~`!#$%\^&*.+=\-\[\]\\';,/{}|\\":<>\?]/g.test(salary);
}
function isValidAge(age){
    return !/[~`!#$%\^&*.+=\-\[\]\\';,/{}|\\":<>\?]/g.test(age);
}
