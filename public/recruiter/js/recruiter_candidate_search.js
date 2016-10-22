/**
 * Created by dodo on 13/10/16.
 */

var candidateIdVal;
var localityArray = [];
var candidateSearchResult = [];
var candidateSearchResultAll = [];

var contactCreditUnitPrice;
var interviewCreditUnitPrice;

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

var counter = 0;

var sortByVal = 1;

var endOfResult = false;
var blockApiTrigger = false;

$(document).scroll(function(){
    if ($(this).scrollTop() > 20) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    } else{
        $('nav').css({"background": "transparent"});
    }
    if ($(this).scrollTop() > 500) {
        $("#fixedButton").show();
    } else{
        $("#fixedButton").hide();
    }

    if($(window).scrollTop() + $(window).height() == $(document).height()) {
        if(!endOfResult && !blockApiTrigger){ //trigger if the search results are still there in server
            blockApiTrigger = true;
            $("#endOfResultsDiv").hide();
            $("#loadingIcon").show();
            counter = counter +10;

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
            success: processDataMatchCandidate
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }

}

function scrollToTop() {
    $('body').scrollTop(0);
}

$(document).ready(function(){
    checkRecruiterLogin();
    $('.button-collapse').sideNav({
        menuWidth: 240,
        edge: 'left',
        closeOnClick: true
    });

    getRecruiterInfo();

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
            success: processDataMatchCandidate
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
    var creditHistoryList = returnedData.recruiterCreditHistoryList;
    creditHistoryList.reverse();
    var contactCreditCount = 0;
    var interviewCreditCount = 0;
    creditHistoryList.forEach(function (creditHistory){
        if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 1){
            if(contactCreditCount == 0){
                $("#remainingContactCredits").html(creditHistory.recruiterCreditsAvailable);
                $("#remainingContactCreditsMobile").html(creditHistory.recruiterCreditsAvailable);
                contactCreditCount = 1;
            }
        } else{
            if(interviewCreditCount == 0){
                if(creditHistory.recruiterCreditCategory.recruiterCreditCategoryId == 2){
                    $("#remainingInterviewCredits").html(creditHistory.recruiterCreditsAvailable);
                    $("#remainingInterviewCreditsMobile").html(creditHistory.recruiterCreditsAvailable);
                    interviewCreditCount = 1;
                }
            }
        }
        if(contactCreditCount > 0 && interviewCreditCount > 0){
            return false;
        }
    });
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
        mainDiv.appendChild(educationInput);

        var educationLabel = document.createElement("label");
        educationLabel.style = "font-size: 14px";
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
        experienceInput.setAttribute("value", experience.experienceId);
        mainDiv.appendChild(experienceInput);

        var experienceLabel = document.createElement("label");
        experienceLabel.style = "font-size: 14px";
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
        $("#searchJobPanel").hide();
        $("#noCandidateDiv").hide();
        $("#endOfResultsDiv").hide();
        $("#loadingIcon").show();

        counter = 0;
        candidateSearchResultAll = [];
        sortByVal

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
                success: processDataMatchCandidate
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    }
}

function updateSliderVal(distanceSlider) {
    $("#distanceRadius").html("Within " + parseFloat(distanceSlider.value) + "kms");
}

function updateSalarySliderVal(maxSalarySelected) {
    $("#maxSalaryVal").html("Max Salary: ₹" + rupeeFormatSalary(parseFloat(maxSalarySelected.value)));
}

function processDataUnlockedCandidates(returnedData) {
    returnedData.forEach(function (unlockedCandidate){
        try {
            $("#candidate_" + unlockedCandidate.candidate.candidateId).html(unlockedCandidate.candidate.candidateMobile);
            $("#unlock_candidate_" + unlockedCandidate.candidate.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 btn").addClass("contactUnlocked right").removeAttr('onclick');
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
            if(candidateCount < 10){
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

        } else{
            $("#noCandidateDiv").show();
/*            notifySuccess("No Candidates found!");*/
        }
    } else{
        $("#noCandidateDiv").show();
        notifySuccess("Something went wrong! Please try again later!");
    }
}

function generateCandidateCards(candidateSearchResult) {
    var parent = $("#candidateResultContainer");

    candidateSearchResult.forEach(function (value){
        var candidateCard = document.createElement("div");
        candidateCard.className = "card";
        parent.append(candidateCard);

        var candidateCardContent = document.createElement("div");
        candidateCardContent.className = "card-content";
        candidateCardContent.style = "padding: 0";
        candidateCard.appendChild(candidateCardContent);

        var candidateCardRow = document.createElement("div");
        candidateCardRow.className = "row";
        candidateCardRow.style = "padding: 6px 0 6px 0; margin: 0 2%";
        candidateCardContent.appendChild(candidateCardRow);

        var candidateCardRowColOne = document.createElement("div");
        candidateCardRowColOne.className = "col s12 l8";
        candidateCardRowColOne.style = "padding-top:2px";
        candidateCardRow.appendChild(candidateCardRowColOne);

        //candidate name container
        var candidateCardRowColOneFont = document.createElement("font");
        candidateCardRowColOneFont.setAttribute("size", "5");
        candidateCardRowColOneFont.textContent = toTitleCase(value.candidate.candidateFullName);
        candidateCardRowColOne.appendChild(candidateCardRowColOneFont);

        var candidateCardRowColTwo = document.createElement("div");
        candidateCardRowColTwo.className = "col s12 l4";
        candidateCardRowColTwo.style = "padding-top:10px";
        candidateCardRow.appendChild(candidateCardRowColTwo);

        //candidate last active container
        var candidateCardRowColTwoFont = document.createElement("font");
        candidateCardRowColTwoFont.setAttribute("size", "3");
        if(value.extraData.lastActive != null){
            if(value.extraData.lastActive.lastActiveValueName != null){
                candidateCardRowColTwoFont.textContent = "Active: " + value.extraData.lastActive.lastActiveValueName;
            }
        } else{
            candidateCardRowColTwoFont.textContent = "Not Specified";
        }
        candidateCardRowColTwo.appendChild(candidateCardRowColTwoFont);

        //end of candidateCardRow

        var candidateCardDivider = document.createElement("div");
        candidateCardDivider.className = "divider";
        candidateCardContent.appendChild(candidateCardDivider);

        candidateCardRow = document.createElement("div");
        candidateCardRow.style = "padding: 10px 2%;margin: 0";
        candidateCardContent.appendChild(candidateCardRow);

        candidateCardRowColOne = document.createElement("div");
        candidateCardRowColOne.className = "col s12 l4";
        candidateCardRowColOne.style = "margin-top: 4px";
        candidateCardRow.appendChild(candidateCardRowColOne);

        var inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block; margin: 4px;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        var iconImg = document.createElement("img");
        iconImg.src = "/assets/recruiter/img/icons/locality.svg";
        iconImg.style = "margin-top: -4px";
        iconImg.setAttribute('height', '16px');
        inlineBlockDiv.appendChild(iconImg);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        var innerInlineBlockDiv = document.createElement("div");
        innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
        innerInlineBlockDiv.textContent = "Home Locality";
        inlineBlockDiv.appendChild(innerInlineBlockDiv);

        var candidateLocalityVal = document.createElement("div");
        candidateLocalityVal.style = "margin-left: 4px";
        if(value.candidate.locality != null){
            candidateLocalityVal.textContent = value.candidate.locality.localityName;
        } else{
            candidateLocalityVal.textContent = "Not Specified";
        }
        inlineBlockDiv.appendChild(candidateLocalityVal);

        /* second col */
        candidateCardRowColOne = document.createElement("div");
        candidateCardRowColOne.className = "col s12 l4";
        candidateCardRowColOne.style = "margin-top: 4px";
        candidateCardRow.appendChild(candidateCardRowColOne);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block; margin: 4px;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        iconImg = document.createElement("img");
        iconImg.src = "/assets/recruiter/img/icons/gender.svg";
        iconImg.style = "margin-top: -4px";
        iconImg.setAttribute('height', '16px');
        inlineBlockDiv.appendChild(iconImg);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        innerInlineBlockDiv = document.createElement("div");
        innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
        innerInlineBlockDiv.textContent = "Gender";
        inlineBlockDiv.appendChild(innerInlineBlockDiv);

        candidateLocalityVal = document.createElement("div");
        candidateLocalityVal.style = "margin-left: 4px";
        if(value.candidate.candidateGender != null){
            if(value.candidate.candidateGender == 0){
                candidateLocalityVal.textContent = "Male";
            } else{
                candidateLocalityVal.textContent = "Female";
            }
        } else{
            candidateLocalityVal.textContent = "Not Specified";
        }
        inlineBlockDiv.appendChild(candidateLocalityVal);

        /* second col */
        candidateCardRowColOne = document.createElement("div");
        candidateCardRowColOne.className = "col s12 l4";
        candidateCardRowColOne.style = "margin-top: 4px";
        candidateCardRow.appendChild(candidateCardRowColOne);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block; margin: 4px;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        iconImg = document.createElement("img");
        iconImg.src = "/assets/recruiter/img/icons/age.svg";
        iconImg.style = "margin-top: -4px";
        iconImg.setAttribute('height', '16px');
        inlineBlockDiv.appendChild(iconImg);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        innerInlineBlockDiv = document.createElement("div");
        innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
        innerInlineBlockDiv.textContent = "Age";
        inlineBlockDiv.appendChild(innerInlineBlockDiv);

        var candidateAgeVal = document.createElement("div");
        candidateAgeVal.style = "margin-left: 4px";
        if (value.candidate.candidateDOB != null) {
            var date = JSON.parse(value.candidate.candidateDOB);
            var yr = new Date(date).getFullYear();
            var month = ('0' + parseInt(new Date(date).getMonth() + 1)).slice(-2);
            var d = ('0' + new Date(date).getDate()).slice(-2);
            var today = new Date();
            var birthDate = new Date(yr + "-" + month + "-" + d);
            var age = today.getFullYear() - birthDate.getFullYear();
            var m = today.getMonth() - birthDate.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }
            candidateAgeVal.textContent = age + " years";
        } else{
            candidateAgeVal.textContent = "Not Specified";
        }
        inlineBlockDiv.appendChild(candidateAgeVal);

        candidateCardRow = document.createElement("div");
        candidateCardRow.style = "padding: 10px 2%;margin: 0";
        candidateCardContent.appendChild(candidateCardRow);

        candidateCardRowColOne = document.createElement("div");
        candidateCardRowColOne.className = "col s12 l4";
        candidateCardRowColOne.style = "margin-top: 4px";
        candidateCardRow.appendChild(candidateCardRowColOne);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block; margin: 4px;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        iconImg = document.createElement("img");
        iconImg.src = "/assets/recruiter/img/icons/education.svg";
        iconImg.style = "margin-top: -4px";
        iconImg.setAttribute('height', '16px');
        inlineBlockDiv.appendChild(iconImg);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        innerInlineBlockDiv = document.createElement("div");
        innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
        innerInlineBlockDiv.textContent = "Education";
        inlineBlockDiv.appendChild(innerInlineBlockDiv);

        var candidateEducationVal = document.createElement("div");
        candidateEducationVal.style = "margin-left: 4px";
        candidateEducationVal.textContent = "Not Specified";
        if(value.candidate.candidateEducation){
            if(value.candidate.candidateEducation.education != null){
                if(candidateEducationVal.textContent = value.candidate.candidateEducation.education.educationId > 3){
                    var eduVal = value.candidate.candidateEducation.education.educationName;
                    if(value.candidate.candidateEducation.degree != null){
                        eduVal = eduVal + " (" + value.candidate.candidateEducation.degree.degreeName;
                        if(value.candidate.candidateEducation.candidateEducationCompletionStatus != null){
                            if(value.candidate.candidateEducation.candidateEducationCompletionStatus == 1){
                                eduVal = eduVal + ", Completed)";
                            } else{
                                eduVal = eduVal + ", Incomplete)";
                            }
                        } else{
                            eduVal = eduVal + ", Not specified)";
                        }
                    }
                    candidateEducationVal.textContent = eduVal;
                } else{
                    candidateEducationVal.textContent = value.candidate.candidateEducation.education.educationName;
                }

            }
        }
        inlineBlockDiv.appendChild(candidateEducationVal);

        /* second col */
        candidateCardRowColOne = document.createElement("div");
        candidateCardRowColOne.className = "col s12 l4";
        candidateCardRowColOne.style = "margin-top: 4px";
        candidateCardRow.appendChild(candidateCardRowColOne);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block; margin: 4px;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        iconImg = document.createElement("img");
        iconImg.src = "/assets/recruiter/img/icons/exp.svg";
        iconImg.style = "margin-top: -4px";
        iconImg.setAttribute('height', '16px');
        inlineBlockDiv.appendChild(iconImg);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        innerInlineBlockDiv = document.createElement("div");
        innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
        innerInlineBlockDiv.textContent = "Experience";
        inlineBlockDiv.appendChild(innerInlineBlockDiv);

        var candidateExperienceVal = document.createElement("div");
        candidateExperienceVal.style = "margin-left: 4px";
        if(value.candidate.candidateTotalExperience != null){
            if(value.candidate.candidateTotalExperience == 0){
                candidateExperienceVal.textContent = "Fresher";
            } else{
                var yrs = parseInt(value.candidate.candidateTotalExperience/12);
                var mnths = (value.candidate.candidateTotalExperience) % 12;

                if(yrs == 0){
                    candidateExperienceVal.textContent = mnths + " months";
                } else if(mnths == 0){
                    candidateExperienceVal.textContent = yrs + " years";
                } else{
                    candidateExperienceVal.textContent = yrs + " years and " + mnths + " months";
                }
            }
        } else{
            candidateExperienceVal.textContent = "Not Specified";
        }
        inlineBlockDiv.appendChild(candidateExperienceVal);

        /* second col */
        candidateCardRowColOne = document.createElement("div");
        candidateCardRowColOne.className = "col s12 l4";
        candidateCardRowColOne.style = "margin-top: 4px";
        candidateCardRow.appendChild(candidateCardRowColOne);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block; margin: 4px;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        iconImg = document.createElement("img");
        iconImg.src = "/assets/recruiter/img/icons/salary.svg";
        iconImg.style = "margin-top: -4px";
        iconImg.setAttribute('height', '16px');
        inlineBlockDiv.appendChild(iconImg);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        innerInlineBlockDiv = document.createElement("div");
        innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
        innerInlineBlockDiv.textContent = "Last Withdrawn Salary";
        inlineBlockDiv.appendChild(innerInlineBlockDiv);

        var candidateLastWithdrawnSalaryVal = document.createElement("div");
        candidateLastWithdrawnSalaryVal.style = "margin-left: 4px";
        if(value.candidate.candidateLastWithdrawnSalary != null){
            if(value.candidate.candidateLastWithdrawnSalary == 0){
                if(value.candidate.candidateTotalExperience != null){
                    if(value.candidate.candidateTotalExperience == 0){
                        candidateLastWithdrawnSalaryVal.textContent = " - (Fresher)";
                    }
                } else{
                    candidateLastWithdrawnSalaryVal.textContent = "Not Specified";
                }
            } else{
                candidateLastWithdrawnSalaryVal.textContent = "₹" + rupeeFormatSalary(value.candidate.candidateLastWithdrawnSalary);
            }
        } else{
            candidateLastWithdrawnSalaryVal.textContent = "Not Specified";
        }
        inlineBlockDiv.appendChild(candidateLastWithdrawnSalaryVal);

        candidateCardRow = document.createElement("div");
        candidateCardRow.style = "padding: 10px 2%;margin: 0";
        candidateCardContent.appendChild(candidateCardRow);

        candidateCardRowColOne = document.createElement("div");
        candidateCardRowColOne.className = "col s12 l4";
        candidateCardRowColOne.style = "margin-top: 4px";
        candidateCardRow.appendChild(candidateCardRowColOne);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block; margin: 4px;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        iconImg = document.createElement("img");
        iconImg.src = "/assets/recruiter/img/icons/language.svg";
        iconImg.style = "margin-top: -4px";
        iconImg.setAttribute('height', '16px');
        inlineBlockDiv.appendChild(iconImg);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block;";
        candidateCardRowColOne.appendChild(inlineBlockDiv);

        innerInlineBlockDiv = document.createElement("div");
        innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
        innerInlineBlockDiv.textContent = "Language(s)";
        inlineBlockDiv.appendChild(innerInlineBlockDiv);

        var candidateLanguageVal = document.createElement("div");
        candidateLanguageVal.style = "margin-left: 4px";
        var langList = value.candidate.languageKnownList;
        var langListCount = Object.keys(langList).length;
        if(langListCount > null){
            var langVal = "";
            langList.forEach(function (language){
                langVal += language.language.languageName + ", ";
            });
            candidateLanguageVal.textContent = langVal.substring(0, langVal.length - 2);
        } else{
            candidateLanguageVal.textContent = "Not specified";
        }
        inlineBlockDiv.appendChild(candidateLanguageVal);

        //skills
        candidateCardRowColTwo = document.createElement("div");
        candidateCardRowColTwo.className = "col s12 l4";
        candidateCardRowColTwo.style = "margin-top: 4px";
        candidateCardRow.appendChild(candidateCardRowColTwo);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block; margin: 4px;";
        candidateCardRowColTwo.appendChild(inlineBlockDiv);

        iconImg = document.createElement("img");
        iconImg.src = "/assets/recruiter/img/icons/skills.svg";
        iconImg.style = "margin-top: -4px";
        iconImg.setAttribute('height', '16px');
        inlineBlockDiv.appendChild(iconImg);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block;";
        candidateCardRowColTwo.appendChild(inlineBlockDiv);

        innerInlineBlockDiv = document.createElement("div");
        innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
        innerInlineBlockDiv.textContent = "Skills(s)";
        inlineBlockDiv.appendChild(innerInlineBlockDiv);

        var candidateSkillVal = document.createElement("div");
        candidateSkillVal.style = "margin-left: 4px";
        candidateSkillVal.id = "skill_" + value.candidate.candidateId;
        var skillList = value.candidate.candidateSkillList;
        var skillListCount = Object.keys(skillList).length;
        if(skillListCount > 0){
            var skillVal = "";
            var allSkillVal = "";
            var count = 0;
            skillList.forEach(function (skill){
                count = count + 1;
                if(count < 4){
                    if(skill.candidateSkillResponse){
                        skillVal += skill.skill.skillName + ", ";
                        allSkillVal += skill.skill.skillName + ", ";
                    }
                } else{
                    allSkillVal += skill.skill.skillName + ", ";
                }
            });
            candidateSkillVal.textContent = skillVal.substring(0, skillVal.length - 2);
        } else{
            candidateSkillVal.textContent = "Not specified";
        }
        inlineBlockDiv.appendChild(candidateSkillVal);

        if(skillListCount > 3){
            var toolTip = document.createElement("a");
            toolTip.className = "tooltipped";
            toolTip.style = "cursor: pointer; text-decoration: none";
            toolTip.setAttribute("data-postiton", "top");
            toolTip.setAttribute("data-delay", "50");
            toolTip.setAttribute("data-tooltip", allSkillVal.substring(0, allSkillVal.length - 2));
            toolTip.textContent = ", more";
            candidateSkillVal.appendChild(toolTip);
        }

        //documents
        var candidateCardRowColThree = document.createElement("div");
        candidateCardRowColThree.className = "col s12 l4";
        candidateCardRowColThree.style = "margin-top: 4px";
        candidateCardRow.appendChild(candidateCardRowColThree);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block; margin: 4px;";
        candidateCardRowColThree.appendChild(inlineBlockDiv);

        iconImg = document.createElement("img");
        iconImg.src = "/assets/recruiter/img/icons/document.svg";
        iconImg.style = "margin-top: -4px";
        iconImg.setAttribute('height', '16px');
        inlineBlockDiv.appendChild(iconImg);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block;";
        candidateCardRowColThree.appendChild(inlineBlockDiv);

        innerInlineBlockDiv = document.createElement("div");
        innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 11px";
        innerInlineBlockDiv.textContent = "Documents(s)";
        inlineBlockDiv.appendChild(innerInlineBlockDiv);

        var candidateDocumentVal = document.createElement("div");
        candidateDocumentVal.style = "margin-left: 4px";
        candidateDocumentVal.id = "document_" + value.candidate.candidateId;

        var documentList = value.candidate.idProofReferenceList;
        var documentListCount = Object.keys(documentList).length;

        if(documentListCount > 0){
            var allDocumentVal = "";
            var documentVal = "";
            var count = 0;
            documentList.forEach(function (document){
                count = count +1;
                if(count < 4){
                    if(document.idProof != null){
                        documentVal += document.idProof.idProofName + ", ";
                        allDocumentVal += document.idProof.idProofName + ", ";
                    }
                } else{
                    allDocumentVal += document.idProof.idProofName + ", ";
                }
            });
            candidateDocumentVal.textContent = documentVal.substring(0, documentVal.length - 2);
        } else{
            candidateDocumentVal.textContent = "Not specified";
        }
        inlineBlockDiv.appendChild(candidateDocumentVal);

        if(documentListCount > 3){
            var toolTip = document.createElement("a");
            toolTip.className = "tooltipped";
            toolTip.style = "cursor: pointer; text-decoration: none";
            toolTip.setAttribute("data-postiton", "top");
            toolTip.setAttribute("data-delay", "50");
            toolTip.setAttribute("data-tooltip", allDocumentVal.substring(0, allDocumentVal.length - 2));
            toolTip.textContent = ", more";
            candidateSkillVal.appendChild(toolTip);
        }


        var unlockDivRow = document.createElement("div");
        unlockDivRow.className = "row";
        unlockDivRow.style = "margin: 2%; padding: 1%; text-align: right; color: #fff";
        candidateCardContent.appendChild(unlockDivRow);

        //unlock candidate div
        var unlockCandidateBtn = document.createElement("div");
        unlockCandidateBtn.id = "unlock_candidate_" + value.candidate.candidateId;
        unlockCandidateBtn.onclick = function () {
            unlockContact(value.candidate.candidateId);
        };
        unlockCandidateBtn.className = "waves-effect waves-light ascentGreen lighten-1 btn";
        unlockDivRow.appendChild(unlockCandidateBtn);

        //candidate last active container
        var candidateUnlockFont = document.createElement("font");
        candidateUnlockFont.id = "candidate_" + value.candidate.candidateId;
        candidateUnlockFont.textContent = "Unlock Contact";
        candidateUnlockFont.style = "font-weight: bold; font-size: 14px";
        unlockCandidateBtn.appendChild(candidateUnlockFont);
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
        $("#candidate_" + candidateIdVal).html(returnedData.candidateMobile);
        $("#unlock_candidate_" + returnedData.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 btn").addClass("contactUnlocked right").removeAttr('onclick');
    } else if(returnedData.status == 2){
        notifySuccess("You have already unlocked the candidate");
        getRecruiterInfo();
        $("#unlock_candidate_" + returnedData.candidateId).removeClass("waves-effect waves-light ascentGreen lighten-1 btn").addClass("contactUnlocked right").removeAttr('onclick');
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
            }
        }
        if($("#interviewCreditAmount").val() != ""){
            interviewCredits = parseInt($("#interviewCreditAmount").val());
            if(interviewCredits < 1){
                notifyError("Interview credits cannot be less than 1");
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