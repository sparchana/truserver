/**
 * Created by dodo on 13/10/16.
 */

var candidateIdVal;
var localityArray = [];
var candidateSearchResult = [];
var candidateSearchResultAll = [];

var contactCreditUnitPrice;
var interviewCreditUnitPrice;
var contactCredits = 0;

//global variables for lazy load
var maxAge = "";
var minSalary = 0;
var maxSalary = 0;
var experienceIdList = [];
var gender = -1;
var jobPostJobRoleId = 1;
var jobPostEducationIdList = [];
var jobPostLocalityIdList = null;
var jobPostLanguageIdList = [];
var distanceRadius = 10;

var resultCount = 10;

var counter = 0;

var sortByVal = 1;

var endOfResult = false;
var blockApiTrigger = false;

$(document).scroll(function(){
    if ($(this).scrollTop() > 30) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    } else{
        $('nav').css({"background": "transparent"});
    }

    if ($(this).scrollTop() > 500) {
        $("#fixedButton").show();
    } else{
        $("#fixedButton").hide();
    }

    if ($(document).scrollTop() > 150) {
        $("#fixed-tools").css('background-color', 'rgba(228, 228, 228, 0.960784)');
        $("#fixed-tools").css('position', 'fixed');
        $("#fixed-tools").slideDown();
        $(".navbar-default").css('background-color', 'white');
    } else {
        $("#fixed-tools").slideUp(100);
    }

    if($(window).scrollTop() + $(window).height() == $(document).height()) {
        if(!endOfResult && !blockApiTrigger){ //trigger if the search results are still there in server
            blockApiTrigger = true;
            $("#endOfResultsDiv").hide();
            $("#loadingIcon").show();
            counter = counter + resultCount;

            requestServerSearchCall(sortByVal);
        }
    }
});

function requestServerSearchCall(sortBy) {
    var d = {
        maxAge: maxAge,
        minSalary: minSalary,
        maxSalary: maxSalary,
        experienceIdList: experienceIdList,
        gender: gender,
        jobPostJobRoleId: jobPostJobRoleId,
        jobPostEducationIdList: jobPostEducationIdList,
        jobPostLocalityIdList: jobPostLocalityIdList,
        jobPostLanguageIdList: jobPostLanguageIdList,
        distanceRadius: distanceRadius,
        initialValue: counter,
        sortBy: sortBy
    };

    try {
        $.ajax({
            type: "POST",
            url: "/recruiter/api/getMatchingCandidate/",
            async: true,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataMatchCandidate,
            error: function (jqXHR, exception) {
                $("#somethingWentWrong").show();
                $("#loadingIcon").hide();
                NProgress.done();
            }
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }

}

function scrollToTop() {
    $('html, body').animate({scrollTop : 0},800);
    return false;
}

$(document).ready(function(){
    checkRecruiterLogin();
    getRecruiterInfo();

    $(".searchNav").addClass("active");
    $(".searchNavMobile").addClass("active");
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

    try {
        $.ajax({
            type: "POST",
            url: "/getAllExperience",
            data: false,
            contentType: false,
            processData: false,
            success: processDataExperience
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    try {
        $.ajax({
            type: "POST",
            url: "/getAllEducation",
            data: false,
            contentType: false,
            processData: false,
            success: processDataEducation
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "POST",
            url: "/getAllLanguage",
            data: false,
            contentType: false,
            processData: false,
            success: processDataLanguages
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "POST",
            url: "/getAllCreditCategory",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataGetCreditCategory
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }


    $('input[type=radio][name=sortBy]').change(function() {
        if (this.value == 0) {
            sortBySalary(this.value);
        } else if (this.value == 1) {
            sortBySalary(this.value);
        } else if (this.value == 2) {
            sortByLastActive(1);
        } else if (this.value == 3) {
            sortByLastActive(0);
        }
    });

    $("#searchBtn").addClass("disabled");
    $("#filterBtn").addClass("disabled");

    $("#candidateResultContainer").html("");
    $("#searchJobPanel").hide();
    $("#noCandidateDiv").hide();
    $("#endOfResultsDiv").hide();
    $("#loadingIcon").show();

    $("#select_all").change(function() {
        if(this.checked) {
            checkAll();
        } else{
            uncheckAll();
        }
    });

    $("#select_all_floating").change(function() {
        if(this.checked) {
            checkAll();
        } else{
            uncheckAll();
        }
    });

    counter = 0;
    NProgress.start();
    var d = {
        maxAge: "",
        minSalary: 0,
        maxSalary: 0,
        experienceIdList: [],
        gender: "-1",
        jobPostJobRoleId: 1,
        jobPostEducationIdList: [],
        jobPostLocalityIdList: null,
        jobPostLanguageIdList: [],
        distanceRadius: 10,
        initialValue: 0,
        sortBy: 1
    };

    try {
        $.ajax({
            type: "POST",
            url: "/recruiter/api/getMatchingCandidate/",
            async: true,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataMatchCandidate,
            error: function (jqXHR, exception) {
                $("#somethingWentWrong").show();
                $("#loadingIcon").hide();
                NProgress.done();
            }
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
});

function getRecruiterInfo() {
    try {
        $.ajax({
            type: "GET",
            url: "/getRecruiterProfileInfo",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterProfile
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataGetCreditCategory(returnedData) {
    contactCreditUnitPrice = returnedData[0].recruiterCreditUnitPrice;
    interviewCreditUnitPrice = returnedData[1].recruiterCreditUnitPrice;
}


function processDataRecruiterProfile(returnedData) {
    contactCredits = returnedData.contactCreditCount;
    $("#remainingContactCredits").html(returnedData.contactCreditCount);
    $("#remainingContactCreditsMobile").html(returnedData.contactCreditCount);
    $("#remainingInterviewCredits").html(returnedData.interviewCreditCount);
    $("#remainingInterviewCreditsMobile").html(returnedData.interviewCreditCount);
}

function processDataEducation(returnedData) {
    var parent = $("#educationFilterDiv");
    returnedData.forEach(function (education) {
        var mainDiv = document.createElement("div");
        parent.append(mainDiv);

        var educationInput = document.createElement("input");
        educationInput.type = "checkbox";
        educationInput.name = "filterEducation";
        educationInput.id = "edu_" + education.educationId;
        educationInput.setAttribute("value", education.educationId);
        educationInput.setAttribute("onchange", "checkOnFilterChange()");
        mainDiv.appendChild(educationInput);

        var educationLabel = document.createElement("label");
        educationLabel.style = "font-size: 12px";
        educationLabel.setAttribute("for", "edu_" + education.educationId);
        educationLabel.textContent = education.educationName;
        mainDiv.appendChild(educationLabel);
    });
}

function processDataExperience(returnedData) {
    var parent = $("#experienceFilterDiv");
    returnedData.forEach(function (experience) {
        var mainDiv = document.createElement("div");
        parent.append(mainDiv);

        var experienceInput = document.createElement("input");
        experienceInput.type = "checkbox";
        experienceInput.name = "filterExperience";
        experienceInput.id = "exp_" + experience.experienceId;
        experienceInput.setAttribute("onchange", "checkOnFilterChange()");
        experienceInput.setAttribute("value", experience.experienceId);
        mainDiv.appendChild(experienceInput);

        var experienceLabel = document.createElement("label");
        experienceLabel.style = "font-size: 12px";
        experienceLabel.setAttribute("for", "exp_" + experience.experienceId);
        experienceLabel.textContent = experience.experienceType;
        mainDiv.appendChild(experienceLabel);
    });
}

function processDataLanguages(returnedData) {
    var parent = $("#languageFilterDiv");
    returnedData.forEach(function (language) {

        var mainDiv = document.createElement("div");
        parent.append(mainDiv);

        var languageInput = document.createElement("input");
        languageInput.type = "checkbox";
        languageInput.id = "lang_" + language.languageId;
        languageInput.setAttribute("value", language.languageId);
        languageInput.setAttribute("onchange", "checkOnFilterChange()");
        mainDiv.appendChild(languageInput);

        var languageLabel = document.createElement("label");
        languageLabel.style = "font-size: 14px";
        languageLabel.setAttribute("for", "lang_" + language.languageId);
        languageLabel.textContent = language.languageName;
        mainDiv.appendChild(languageLabel);

    });
}

function checkRecruiterLogin() {
    try {
        $.ajax({
            type: "GET",
            url: "/checkRecruiterSession",
            data: false,
            contentType: false,
            processData: false,
            success: processDataRecruiterSession
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataRecruiterSession(returnedData) {
    if(returnedData == 0){
        logoutRecruiter();
    }
}

function validateLocationVal(val, text) {
    if(text.localeCompare("All Bangalore") != 0){
        if(val.localeCompare(text) == 0){
            $('#searchLocality').tokenize().tokenRemove(val);
            notifyError("Please select a valid location from the dropdown list");
        }
    }
}
function validateJobRoleVal(val, text) {
    if(val.localeCompare(text) == 0){
        $('#searchJobRole').tokenize().tokenRemove(val);
        notifyError("Please select a valid job role from the dropdown list");
    }
}

function processDataCheckLocality(returnedData) {
    var option = $('<option value="0"></option>').text("All Bangalore");
    $('#searchLocality').append(option);
    returnedData.forEach(function(locality) {
        var id = locality.localityId;
        var name = locality.localityName;
        option = $('<option value=' + id + '></option>').text(name);
        $('#searchLocality').append(option);
     });
    $('#searchLocality').tokenize().tokenAdd("0", "All Bangalore");
}

function processDataCheckJobs(returnedData) {
    var option;
    returnedData.forEach(function(job) {
        var id = job.jobRoleId;
        var name = job.jobName;
        option = $('<option value=' + id + '></option>').text(name);
        $('#searchJobRole').append(option);
    });
    $('#searchJobRole').tokenize().tokenAdd("1", "Accountant");
}

function resetFilters() {
    $('input:checkbox').removeAttr('checked');
    $('input:radio').removeAttr('checked');
    $("#maxSalaryVal").html("Max Salary: Not specified");
    $("#distanceRadius").html("Within 10 kms");
    $("#filterDistance").val(10);
    $("#filterSalary").val(0);

    $("#gender_filter").hide();
    $("#experience_filter").hide();
    $("#salary_filter").hide();
    $("#education_filter").hide();
    $("#language_filter").hide();

    $("#distance_filter").show();
    $("#distanceRadius").html("Within 10kms");

    //resetting global variables
    maxAge = "";
    minSalary = 0;
    maxSalary = 0;
    experienceIdList = [];
    gender = "-1";
    jobPostEducationIdList = [];
    jobPostLanguageIdList = [];
    distanceRadius = 10;
    counter = 0;

    blockApiTrigger = false;
    endOfResult = false;

    document.getElementById('latestActive').checked = true;
    $("#candidateResultContainer").html("");
    $("#endOfResultsDiv").hide();
    $("#loadingIcon").show();
    requestServerSearchCall(1);
}

function performSearch() {
    var searchLocality;
    var searchJobRole = null;
    var searchGender = "-1";

    //locality
    var selectedLocality = $("#searchLocality").val();
    searchLocality = [];
    if(selectedLocality != null){
        searchLocality = [];
        if(selectedLocality[0] != 0){
            searchLocality.push(parseInt(selectedLocality[0]));
        } else{
            searchLocality = null;
        }
    }

    //jobrole
    if($("#searchJobRole").val() != null ){
        searchJobRole = parseInt($("#searchJobRole").val());
    }

    //gender filter
    if($("input[name='filterGender']:checked").val() != null || $("input[name='filterGender']:checked").val() != undefined){
        searchGender = $("input[name='filterGender']:checked").val();
    }

    //education filter
    var selectedEducation = [];
    $('#educationFilterDiv input:checked').each(function() {
        selectedEducation.push(parseInt($(this).attr('value')));
    });

    //experience filter
    var selectedExperience = [];
    $('#experienceFilterDiv input:checked').each(function() {
        selectedExperience.push(parseInt($(this).attr('value')));
    });

    //language filter
    var selectedLanguage = [];
    $('#languageFilterDiv input:checked').each(function() {
        selectedLanguage.push(parseInt($(this).attr('value')));
    });

    if(searchJobRole == [] || searchJobRole == [null] || searchJobRole == null){
        notifyError("Please select a job role for search");
    } else if(searchLocality != null && Object.keys(searchLocality).length == 0) {
        notifyError("Please select a locality for search");
    } else {
        $("#searchBtn").addClass("disabled");
        $("#filterBtn").addClass("disabled");

        $("#candidateResultContainer").html("");
        $("#candidateTools").show();
        $("#searchJobPanel").hide();
        $("#noCandidateDiv").hide();
        $("#endOfResultsDiv").hide();
        $("#loadingIcon").show();

        counter = 0;
        candidateSearchResultAll = [];

        NProgress.start();
        var d = {
            maxAge: "",
            minSalary: 0,
            maxSalary: parseInt($("#filterSalary").val()),
            experienceIdList: selectedExperience,
            gender: searchGender,
            jobPostJobRoleId: searchJobRole,
            jobPostEducationIdList: selectedEducation,
            jobPostLocalityIdList: searchLocality,
            jobPostLanguageIdList: selectedLanguage,
            distanceRadius: parseFloat($("#filterDistance").val()),
            initialValue: counter,
            sortBy: sortByVal
        };

        //setting global variables
        maxAge = "";
        minSalary = 0;
        maxSalary = parseInt($("#filterSalary").val());
        experienceIdList = selectedExperience;
        gender = searchGender;
        jobPostJobRoleId = searchJobRole;
        jobPostEducationIdList = selectedEducation;
        jobPostLocalityIdList = searchLocality;
        jobPostLanguageIdList = selectedLanguage;
        distanceRadius = parseFloat($("#filterDistance").val());

        try {
            $.ajax({
                type: "POST",
                url: "/recruiter/api/getMatchingCandidate/",
                async: true,
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataMatchCandidate,
                error: function (jqXHR, exception) {
                    $("#somethingWentWrong").show();
                    $("#loadingIcon").hide();
                    NProgress.done();
                }
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    }
}

function updateSliderVal(distanceSlider) {
    $("#distance_filter").show();
    $("#distanceRadius").html("Within " + parseFloat(distanceSlider.value) + "kms");

    //perform search
    performSearch();
}

function updateSalarySliderVal(maxSalarySelected) {
    $("#salary_filter").show();
    $("#maxSalaryVal").html("Max Salary: ₹" + rupeeFormatSalary(parseFloat(maxSalarySelected.value)));

    //perform search
    performSearch();
}

function processDataUnlockedCandidates(returnedData) {
    returnedData.forEach(function (unlockedCandidate){
        try {
            $("#candidate_" + unlockedCandidate.candidate.candidateId).html(unlockedCandidate.candidate.candidateMobile);
            $("#unlock_candidate_" + unlockedCandidate.candidate.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 customUnlockBtn").addClass("contactUnlocked right").removeAttr('onclick');
            var link = unlockedCandidate.candidate.candidateResumeLink;
            if(link != null){
                $("#candidate_resume_" + unlockedCandidate.candidate.candidateId).attr("val", "http://docs.google.com/gview?url=" + link + "&embedded=true");
            }
        } catch (err){}
    });
}

function processDataMatchCandidate(returnedData) {
    NProgress.done();
    blockApiTrigger = false;
    $("#searchBtn").removeClass("disabled");
    $("#filterBtn").removeClass("disabled");
    $("#loadingIcon").hide();
    if(returnedData != "0"){
        var candidateCount = Object.keys(returnedData).length;
        if(candidateCount > 0){
            if(candidateCount < resultCount){
                endOfResult = true;
                $("#endOfResultsDiv").show();
            }

            candidateSearchResult = [];
            $.each(returnedData, function (key, value) {
                candidateSearchResult.push(value);
                candidateSearchResultAll.push(value);
            });

            //render candidate cards with last active filter
            $("#latestActive").attr('checked', true);

            generateCandidateCards(candidateSearchResult);

            try {
                $.ajax({
                    type: "POST",
                    url: "/recruiter/api/getUnlockedCandidates/",
                    async: true,
                    contentType: false,
                    data: false,
                    success: processDataUnlockedCandidates
                });
            } catch (exception) {
                console.log("exception occured!!" + exception.stack);
            }

            $("#candidateTools").show();

        } else{
            $("#candidateTools").hide();
            $("#noCandidateDiv").show();
/*            notifySuccess("No Candidates found!");*/
        }
    } else{
        $("#candidateTools").hide();
        $("#noCandidateDiv").show();
        notifySuccess("Something went wrong! Please try again later!");
    }
}

function generateCandidateCards(candidateSearchResult) {
    var parent = $("#candidateResultContainer");

    candidateSearchResult.forEach(function (value){

        //calling render candidate card method to render candidate card
        renderIndividualCandidateCard(value, parent, view_search_candidate);
    });
    $('.tooltipped').tooltip({delay: 50});

}

function sortBySalary(val){
    counter = 0;
    $("#endOfResultsDiv").hide();
    $("#loadingIcon").show();
    if(val == 1){
        sortByVal = 2;
        requestServerSearchCall(2);
    } else{
        sortByVal = 3;
        requestServerSearchCall(3);
    }
    $("#candidateResultContainer").html("");
}

function sortByLastActive(val){
    counter = 0;
    $("#endOfResultsDiv").hide();
    $("#loadingIcon").show();
    if(val == 1){
        sortByVal = 1;
        requestServerSearchCall(1);
    }
    $("#candidateResultContainer").html("");
    generateCandidateCards(candidateSearchResultAll);
}

function unlockContact(candidateId){
    if(candidateId != null || candidateId != undefined){
        candidateIdVal = candidateId;
        try {
            $.ajax({
                type: "POST",
                url: "/recruiter/unlockCandidateContact/" + candidateId,
                async: false,
                contentType: false,
                processData: false,
                success: processDataUnlockCandidate
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}

function processDataUnlockCandidate(returnedData) {
    if(returnedData.status == 1){
        notifySuccess("Contact successfully unlocked");
        getRecruiterInfo();

        resumeDiv.setAttribute("val", "http://docs.google.com/gview?url=" + returnedData.resumeLink + "&embedded=true");
        $("#candidate_" + candidateIdVal).html(returnedData.candidateMobile);
        $("#unlock_candidate_" + returnedData.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 customUnlockBtn").addClass("contactUnlocked right").removeAttr('onclick');
        if(viewResume == true){
            viewResume = false;
            window.open("http://docs.google.com/gview?url=" + returnedData.resumeLink + "&embedded=true");
        }

    } else if(returnedData.status == 2){
        notifySuccess("You have already unlocked the candidate");
        getRecruiterInfo();
        $("#unlock_candidate_" + returnedData.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 customUnlockBtn").addClass("contactUnlocked right").removeAttr('onclick');
        $("#candidate_" + candidateIdVal).html(returnedData.candidateMobile);
    } else if(returnedData.status == 3){
        notifyError("Out of credits! Please recharge");
        openCreditModal();
    }
}

function logoutRecruiter() {
    try {
        $.ajax({
            type: "GET",
            url: "/logoutRecruiter",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataLogoutRecruiter
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function processDataLogoutRecruiter() {
    window.location = "/recruiter";
}

function closeCreditModal() {
    $("#modalBuyCredits").closeModal();
}

function submitCredits() {
    $("#successMsg").hide();
    var contactCreditStatus = 1;
    var interviewCreditStatus = 1;

    if($("#contactCreditAmount").val() == undefined || $("#contactCreditAmount").val() == null || $("#contactCreditAmount").val() == ""){
        contactCreditStatus = 0;
    }
    if($("#interviewCreditAmount").val() == undefined || $("#interviewCreditAmount").val() == null || $("#interviewCreditAmount").val() == ""){
        interviewCreditStatus = 0;
    }

    if(interviewCreditStatus == 0 && contactCreditStatus == 0){
        notifyError("Please specify no. of credits!");
    } else{
        contactCreditStatus = 1;
        interviewCreditStatus = 1;

        var contactCredits = 0;
        var interviewCredits = 0;
        if($("#contactCreditAmount").val() != ""){
            contactCredits = parseInt($("#contactCreditAmount").val());
            if(contactCredits < 1){
                notifyError("Contact credits cannot be less than 1");
                contactCreditStatus = 0;
            } else if(contactCredits > 10000){
                notifyError("Contact credits cannot be greater than 10000");
                contactCreditStatus = 0;
            }

        }
        if($("#interviewCreditAmount").val() != ""){
            interviewCredits = parseInt($("#interviewCreditAmount").val());
            if(interviewCredits < 1){
                notifyError("Interview credits cannot be less than 1");
                interviewCreditStatus = 0;
            } else if(interviewCredits > 10000){
                notifyError("Interview credits cannot be greater than 10000");
                interviewCreditStatus = 0;
            }
        }

        if(interviewCreditStatus != 0 && contactCreditStatus != 0){
            $("#requestCredits").addClass("disabled");
            var d = {
                noOfContactCredits: contactCredits,
                noOfInterviewCredits: interviewCredits
            };

            $.ajax({
                type: "POST",
                url: "/recruiter/api/requestCredits/",
                async: true,
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: processDataAddCreditRequest
            });
        }
    }
}


function processDataAddCreditRequest(returnedData) {
    $("#requestCredits").removeClass("disabled");
    if(returnedData.status == 1){
        $("#successMsg").show();
        notifySuccess("Thanks! We have received your request to buy more credits. Our business team will contact you within 24hrs")
    } else{
        notifyError("Something went wrong. Please try again later");
    }
}


function openCreditModal(){
    $("#successMsg").hide();
    $("#modalBuyCredits").openModal();
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}
var countSort = 0 ;
function showSort() {
    countSort = countSort + 1;
    if(countSort==1)
    {
        $('#sortMainBox').show();
        $('#filterMainBox').hide();
    }
    if(countSort == 2){
        $('#sortMainBox').hide();
        $('#filterMainBox').hide();
        countSort = 0;
    }
}
var countFilter = 0;
function showFilter() {
    countFilter = countFilter + 1;
    if(countFilter==1){
        $('#sortMainBox').hide();
        $('#filterMainBox').show();
    }
    if(countFilter==2){
        $('#filterMainBox').hide();
        $('#sortMainBox').hide();
        countFilter = 0;
    }

}

//onchange filter to update filter marker
function updateGenderFilter() {
    $("input[name=filterGender]:radio").change(function () {
        $("#gender_filter").show();
    });

    performSearch();
}

function checkOnFilterChange(){

    //scroll to top
    scrollToTop();

    //education filter
    var selectedEducation = [];
    $('#educationFilterDiv input:checked').each(function() {
        selectedEducation.push(parseInt($(this).attr('value')));
    });
    if(selectedEducation.length > 0){
        $("#education_filter").show();
    } else{
        $("#education_filter").hide();
    }

    //experience filter
    var selectedExperience = [];
    $('#experienceFilterDiv input:checked').each(function() {
        selectedExperience.push(parseInt($(this).attr('value')));
    });
    if(selectedExperience.length > 0){
        $("#experience_filter").show();
    } else{
        $("#experience_filter").hide();
    }


    //language filter
    var selectedLanguage = [];
    $('#languageFilterDiv input:checked').each(function() {
        selectedLanguage.push(parseInt($(this).attr('value')));
    });
    if(selectedLanguage.length > 0){
        $("#language_filter").show();
    } else{
        $("#language_filter").hide();
    }

    //start search
    performSearch();
}
