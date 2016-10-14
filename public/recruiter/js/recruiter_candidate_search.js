/**
 * Created by dodo on 13/10/16.
 */

var candidateIdVal;
var localityArray = [];

$(document).scroll(function(){
    if ($(this).scrollTop() > 80) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});

$(document).ready(function(){
    checkRecruiterLogin();
    $('.button-collapse').sideNav({
        menuWidth: 240,
        edge: 'left',
        closeOnClick: true
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

});

function processDataEducation(returnedData) {
    var parent = $("#educationFilterDiv");
    returnedData.forEach(function (education) {
        var mainDiv = document.createElement("div");
        parent.append(mainDiv);

        var educationInput = document.createElement("input");
        educationInput.type = "radio";
        educationInput.name = "filterEducation";
        educationInput.id = "edu_" + education.educationId;
        educationInput.setAttribute("value", education.educationId);
        mainDiv.appendChild(educationInput);

        var educationLabel = document.createElement("label");
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
        experienceInput.type = "radio";
        experienceInput.name = "filterExperience";
        experienceInput.id = "exp_" + experience.experienceId;
        experienceInput.setAttribute("value", experience.experienceId);
        mainDiv.appendChild(experienceInput);

        var experienceLabel = document.createElement("label");
        experienceLabel.setAttribute("for", "exp_" + experience.experienceId);
        experienceLabel.textContent = experience.experienceType;
        mainDiv.appendChild(experienceLabel);
    });
}

function processDataLanguages(returnedData) {
    var parent = $("#languageFilterDiv");
    returnedData.forEach(function (language) {
        /*<div>
            <input type="checkbox" class="filled-in" id="filled-in-box" checked="checked" />
            <label for="filled-in-box">Filled in</label>
        </div>*/
        var mainDiv = document.createElement("div");
        parent.append(mainDiv);

        var languageInput = document.createElement("input");
        languageInput.type = "checkbox";
        languageInput.id = "lang_" + language.languageId;
        languageInput.setAttribute("value", language.languageId);
        mainDiv.appendChild(languageInput);

        var languageLabel = document.createElement("label");
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

function processDataCheckLocality(returnedData) {
    returnedData.forEach(function(locality) {
        var id = locality.localityId;
        var name = locality.localityName;
        var option = $('<option value=' + id + '></option>').text(name);
        $('#searchLocality').append(option);
     });
}

function processDataCheckJobs(returnedData) {
    var option;
    returnedData.forEach(function(job) {
        var id = job.jobRoleId;
        var name = job.jobName;
        option = $('<option value=' + id + '></option>').text(name);
        $('#searchJobRole').append(option);
    });
}

function performSearch() {
    var searchLocality = [];
    var searchJobRole = null;
    var searchExpFilter = "0";
    var searchGender = "-1";
    var searchEducation = "0";

    //locality
    var selectedLocality = $("#searchLocality").val();
    if(selectedLocality != null){
        searchLocality = [];
        searchLocality.push(parseInt(selectedLocality[0]));
    }

    //jobrole
    if($("#searchJobRole").val() != null ){
        searchJobRole = parseInt($("#searchJobRole").val());
    }

    //experience filter
    if($("input[name='filterExperience']:checked").val() != null || $("input[name='filterExperience']:checked").val() != undefined){
        searchExpFilter = $("input[name='filterExperience']:checked").val();
    }

    //gender filter
    if($("input[name='filterGender']:checked").val() != null || $("input[name='filterGender']:checked").val() != undefined){
        searchGender = $("input[name='filterGender']:checked").val();
    }

    //education filter
    if($("input[name='filterEducation']:checked").val() != null || $("input[name='filterEducation']:checked").val() != undefined){
        searchEducation = $("input[name='filterEducation']:checked").val();
    }

    //language filter
    var selectedLanguage = [];
    $('#languageFilterDiv input:checked').each(function() {
        selectedLanguage.push(parseInt($(this).attr('value')));
    });


    if(searchJobRole == [] || searchJobRole == [null] || searchJobRole == null){
        notifyError("Please select a job role for search");
    } else if(searchLocality.length == 0) {
        notifyError("Please select a locality");
    } else {
        $("#searchJobPanel").hide();
        $("#loadingIcon").show();
        NProgress.start();
        var d = {
            minAge: "",
            maxAge: "",
            minSalary: 0,
            maxSalary: parseInt($("#filterSalary").val()),
            experienceId: searchExpFilter,
            gender: searchGender,
            jobPostJobRoleId: searchJobRole,
            jobPostEducationId: searchEducation,
            jobPostLocalityIdList: searchLocality,
            jobPostLanguageIdList: selectedLanguage
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
}

function processDataMatchCandidate(returnedData) {
    NProgress.done();
    var candidateCount = Object.keys(returnedData).length;
    notifySuccess(candidateCount + " candidates found!");

    if(candidateCount > 0){
        $("#loadingIcon").hide();
        var parent = $("#candidateResultContainer");
        $("#candidateResultContainer").html("");
        $.each(returnedData, function (key, value) {
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
                            candidateCardRowColOneFont.style = "font-weight:600";
                            candidateCardRowColOneFont.textContent = value.candidate.candidateFirstName;
                            candidateCardRowColOne.appendChild(candidateCardRowColOneFont);

                        var candidateCardRowColTwo = document.createElement("div");
                        candidateCardRowColTwo.className = "col s12 l4";
                        candidateCardRowColTwo.style = "padding-top:10px";
                        candidateCardRow.appendChild(candidateCardRowColTwo);

                            //candidate last active container
                            var candidateCardRowColTwoFont = document.createElement("font");
                            candidateCardRowColTwoFont.setAttribute("size", "3");
                            candidateCardRowColTwoFont.style = "font-weight:600";
                            candidateCardRowColTwoFont.textContent = "Active: " + value.extraData.lastActive;
                            candidateCardRowColTwo.appendChild(candidateCardRowColTwoFont);

                    //end of candidateCardRow

                    var candidateCardDivider = document.createElement("div");
                    candidateCardDivider.className = "divider";
                    candidateCardContent.appendChild(candidateCardDivider);

                    candidateCardRow = document.createElement("div");
                    candidateCardRow.style = "padding: 10px 2%;margin: 0";
                    candidateCardContent.appendChild(candidateCardRow);

                        candidateCardRowColOne = document.createElement("div");
                        candidateCardRowColOne.className = "col s6 l4";
                        candidateCardRowColOne.style = "margin-top: 4px";
                        candidateCardRow.appendChild(candidateCardRowColOne);

                            var inlineBlockDiv = document.createElement("div");
                            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
                            candidateCardRowColOne.appendChild(inlineBlockDiv);

                                var iconImg = document.createElement("img");
                                iconImg.src = "/assets/recruiter/img/icons/envelope.svg";
                                iconImg.style = "margin-top: -4px";
                                iconImg.setAttribute('height', '24px');
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
                        candidateCardRowColOne.className = "col s6 l4";
                        candidateCardRowColOne.style = "margin-top: 4px";
                        candidateCardRow.appendChild(candidateCardRowColOne);

                            inlineBlockDiv = document.createElement("div");
                            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
                            candidateCardRowColOne.appendChild(inlineBlockDiv);

                                iconImg = document.createElement("img");
                                iconImg.src = "/assets/recruiter/img/icons/building.svg";
                                iconImg.style = "margin-top: -4px";
                                iconImg.setAttribute('height', '24px');
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
                        candidateCardRowColOne.className = "col s6 l4";
                        candidateCardRowColOne.style = "margin-top: 4px";
                        candidateCardRow.appendChild(candidateCardRowColOne);

                            inlineBlockDiv = document.createElement("div");
                            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
                            candidateCardRowColOne.appendChild(inlineBlockDiv);

                                iconImg = document.createElement("img");
                                iconImg.src = "/assets/recruiter/img/icons/building.svg";
                                iconImg.style = "margin-top: -4px";
                                iconImg.setAttribute('height', '24px');
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
                        candidateCardRowColOne.className = "col s6 l4";
                        candidateCardRowColOne.style = "margin-top: 4px";
                        candidateCardRow.appendChild(candidateCardRowColOne);

                            inlineBlockDiv = document.createElement("div");
                            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
                            candidateCardRowColOne.appendChild(inlineBlockDiv);

                                iconImg = document.createElement("img");
                                iconImg.src = "/assets/recruiter/img/icons/envelope.svg";
                                iconImg.style = "margin-top: -4px";
                                iconImg.setAttribute('height', '24px');
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
                                            candidateEducationVal.textContent = value.candidate.candidateEducation.education.educationName;
                                        }
                                    }
                                    inlineBlockDiv.appendChild(candidateEducationVal);

                        /* second col */
                        candidateCardRowColOne = document.createElement("div");
                        candidateCardRowColOne.className = "col s6 l4";
                        candidateCardRowColOne.style = "margin-top: 4px";
                        candidateCardRow.appendChild(candidateCardRowColOne);

                            inlineBlockDiv = document.createElement("div");
                            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
                            candidateCardRowColOne.appendChild(inlineBlockDiv);

                                iconImg = document.createElement("img");
                                iconImg.src = "/assets/recruiter/img/icons/building.svg";
                                iconImg.style = "margin-top: -4px";
                                iconImg.setAttribute('height', '24px');
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
                                            candidateExperienceVal.textContent = (parseInt(value.candidate.candidateTotalExperience/12)) + " yrs and " + (value.candidate.candidateTotalExperience)%12 + " months";
                                        }
                                    } else{
                                        candidateExperienceVal.textContent = "Not Specified";
                                    }
                                    inlineBlockDiv.appendChild(candidateExperienceVal);

                        /* second col */
                        candidateCardRowColOne = document.createElement("div");
                        candidateCardRowColOne.className = "col s6 l4";
                        candidateCardRowColOne.style = "margin-top: 4px";
                        candidateCardRow.appendChild(candidateCardRowColOne);

                            inlineBlockDiv = document.createElement("div");
                            inlineBlockDiv.style = "display: inline-block; margin: 4px;";
                            candidateCardRowColOne.appendChild(inlineBlockDiv);

                                iconImg = document.createElement("img");
                                iconImg.src = "/assets/recruiter/img/icons/building.svg";
                                iconImg.style = "margin-top: -4px";
                                iconImg.setAttribute('height', '24px');
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

                    candidateCardRow = document.createElement("div");
                    candidateCardRow.style = "padding: 10px 2%;margin: 0";
                    candidateCardContent.appendChild(candidateCardRow);

                        candidateCardRowColOne = document.createElement("div");
                        candidateCardRowColOne.className = "col s12 l12";
                        candidateCardRowColOne.style = "margin-top: 4px";
                        candidateCardRow.appendChild(candidateCardRowColOne);

                        inlineBlockDiv = document.createElement("div");
                        inlineBlockDiv.style = "display: inline-block; margin: 4px;";
                        candidateCardRowColOne.appendChild(inlineBlockDiv);

                            iconImg = document.createElement("img");
                            iconImg.src = "/assets/recruiter/img/icons/envelope.svg";
                            iconImg.style = "margin-top: -4px";
                            iconImg.setAttribute('height', '24px');
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
                                candidateLastWithdrawnSalaryVal.textContent = "₹" + value.candidate.candidateLastWithdrawnSalary;
                            } else{
                                candidateLastWithdrawnSalaryVal.textContent = "Not Specified";
                            }
                            inlineBlockDiv.appendChild(candidateLastWithdrawnSalaryVal);

            var unlockDivRow = document.createElement("div");
            unlockDivRow.className = "row";
            unlockDivRow.style = "margin: 2%; padding:1%; text-align: right";
            unlockDivRow.onclick = function () {
                unlockContact(value.candidate.candidateId);
            };
            candidateCardContent.appendChild(unlockDivRow);

                var unlockCandidateBtn = document.createElement("a");
                unlockCandidateBtn.className = "waves-effect waves-light ascentGreen lighten-1 btn";
                unlockDivRow.appendChild(unlockCandidateBtn);

                    //candidate last active container
                    var candidateUnlockFont = document.createElement("font");
                    candidateUnlockFont.style = "color: #fff;font-weight: bold";
                    candidateUnlockFont.id = "candidate_" + value.candidate.candidateId;
                    candidateUnlockFont.textContent = "Unlock Contact";
                    unlockCandidateBtn.appendChild(candidateUnlockFont);
        });
    } else{

    }
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
        $("#candidate_" + candidateIdVal).html(returnedData.candidateMobile);
    } else if(returnedData.status == 2){
        notifySuccess("You have already unlocked the candidate");
        $("#candidate_" + candidateIdVal).html(returnedData.candidateMobile);
    } else if(returnedData.status == 3){
        notifyError("Out of credits! Please recharge");
    }
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}