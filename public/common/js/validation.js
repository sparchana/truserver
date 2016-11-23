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


// validator

// validate AADHAAR start
/*
 For more info on the algorithm: http://en.wikipedia.org/wiki/Verhoeff_algorithm
 by Sergey Petushkov, 2014
 */

// multiplication table d
var d=[
    [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
    [1, 2, 3, 4, 0, 6, 7, 8, 9, 5],
    [2, 3, 4, 0, 1, 7, 8, 9, 5, 6],
    [3, 4, 0, 1, 2, 8, 9, 5, 6, 7],
    [4, 0, 1, 2, 3, 9, 5, 6, 7, 8],
    [5, 9, 8, 7, 6, 0, 4, 3, 2, 1],
    [6, 5, 9, 8, 7, 1, 0, 4, 3, 2],
    [7, 6, 5, 9, 8, 2, 1, 0, 4, 3],
    [8, 7, 6, 5, 9, 3, 2, 1, 0, 4],
    [9, 8, 7, 6, 5, 4, 3, 2, 1, 0]
];

// permutation table p
var p=[
    [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
    [1, 5, 7, 6, 2, 8, 3, 0, 9, 4],
    [5, 8, 0, 3, 7, 9, 6, 1, 4, 2],
    [8, 9, 1, 6, 0, 4, 3, 5, 2, 7],
    [9, 4, 5, 3, 1, 2, 6, 8, 7, 0],
    [4, 2, 8, 6, 5, 7, 3, 9, 0, 1],
    [2, 7, 9, 3, 8, 0, 6, 4, 1, 5],
    [7, 0, 4, 6, 9, 1, 3, 2, 5, 8]
];

// inverse table inv
var inv = [0, 4, 3, 2, 1, 5, 6, 7, 8, 9];

// converts string or number to an array and inverts it
function invArray(array){

    if (Object.prototype.toString.call(array) == "[object Number]"){
        array = String(array);
    }

    if (Object.prototype.toString.call(array) == "[object String]"){
        array = array.split("").map(Number);
    }

    return array.reverse();

}

// generates checksum
function generate(array){

    var c = 0;
    var invertedArray = invArray(array);

    for (var i = 0; i < invertedArray.length; i++){
        c = d[c][p[((i + 1) % 8)][invertedArray[i]]];
    }

    return inv[c];
}

// validates checksum
function validateAadhar(array) {
    if(array == null) {
        return false;
    }

    // if(array == "") {
    //     return true;
    // }
    if(isNaN(array)){
        return false;
    }
    array = array.replace(/\s+/g, '');
    array = array.replace(/[-&\/\\#,+()$~%.'":*?<>{}]/g, '');

    var c = 0;
    var invertedArray = invArray(array);

    for (var i = 0; i < invertedArray.length; i++){
        c=d[c][p[(i % 8)][invertedArray[i]]];
    }

    return (c === 0);
}

// validate AADHAAR end

// validate Driving Licence
function validateDL(dlNumber) {
    if (dlNumber == null) {
        console.log("its null");
        return false;
    }
    // if(dlNumber == "") {
    //     return true;
    // }
    dlNumber = dlNumber.replace(/\s+/g, '');
    dlNumber = dlNumber.replace("-", '');
    if(dlNumber.length != 15){
        console.log("dlNumber length is not 15, its: "+dlNumber.length);
        return false;
    }
    var validCount = 0;
    var stateCode = dlNumber.substring(0,2);
    var cityCode = dlNumber.substring(2,4);
    var issueYear = dlNumber.substring(4,8);
    var uid = dlNumber.substring(8,15);
    if(stateCode.match(/[a-z]/i)){
        validCount++;
    }
    if(cityCode.match(/[0-9]/g)){
        validCount++;
    }
    if(issueYear.length == 4 && issueYear.match(/[0-9]/g)){
        validCount++;
    }
    if(uid.match(/[0-9]/g)){
        validCount++;
    }
    if(validCount == 4){
        return true;
    } else {
        return false;
    }
}
// validation DL end

// validation passport

function validatePASSPORT(passPort) {

    if (passPort == null) {
        console.log("passPort null");
        return false;
    }

    // if(passPort == "") {
    //     return true;
    // }
    passPort = passPort.replace(/\s+/g, '');
    passPort = passPort.replace(/[-&\/\\#,+()$~%.'":*?<>{}]/g, '');
    var validCount = 0;
    var letter =  passPort.substring(0,1);
    var code = passPort.substring(1, passPort.length);
    if(letter.match(/[a-z]/i)){
        validCount++;
    }
    if(!isNaN(code) && code.length === 7){
        validCount++;
    }

    console.log(letter + " - " + code);
    if(validCount == 2) {
        return true;
    } else {
        return false;
    }
}
function validatePAN(panNumber) {

    if (panNumber == null) {
        console.log("panNumber null");
        return false;
    }

    if(panNumber == "") {
        return false;
    }
    // if(panNumber == "") {
    //     return true;
    // }
    var pancardPattern = /^([a-zA-Z]{5})(\d{4})([a-zA-Z]{1})$/;
    var patternArray = panNumber.match(pancardPattern);

    if(patternArray == null) {
        return false;
    } else {
        return true;
    }
}