/**
 * Created by hawk on 21/10/16.
 */
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
            url: "/recruiter/api/getUnlockedCandidates/",
            data: false,
            contentType: false,
            processData: false,
            success: processDataForUnlockedCandidates
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
});

function processDataForUnlockedCandidates(returnedData) {
    if(Object.keys(returnedData).length > 0){
        var parent = $("#candidateContainer");
        if(returnedData != "0"){
            returnedData.forEach(function (value){
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
                var postedOn = new Date(value.createTimestamp);

                candidateCardRowColTwoFont.textContent = "Unlocked on: " + ('0' + postedOn.getDate()).slice(-2) + '-' + getMonthVal((postedOn.getMonth()+1)) + '-' + postedOn.getFullYear();
                candidateCardRowColTwo.appendChild(candidateCardRowColTwoFont);

                //end of candidateCardRow

                var candidateCardDivider = document.createElement("div");
                candidateCardDivider.className = "divider";
                candidateCardContent.appendChild(candidateCardDivider);

                candidateCardRow = document.createElement("div");
                candidateCardRow.className = "row";
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
                candidateCardRowColOne.className = "col s12 l4";
                candidateCardRowColOne.style = "margin-top: 4px";
                candidateCardRow.appendChild(candidateCardRowColOne);

                inlineBlockDiv = document.createElement("div");
                inlineBlockDiv.style = "display: inline-block; margin: 4px;";
                candidateCardRowColOne.appendChild(inlineBlockDiv);

                iconImg = document.createElement("img");
                iconImg.src = "/assets/recruiter/img/icons/gender.svg";
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
                candidateCardRowColOne.className = "col s12 l4";
                candidateCardRowColOne.style = "margin-top: 4px";
                candidateCardRow.appendChild(candidateCardRowColOne);

                inlineBlockDiv = document.createElement("div");
                inlineBlockDiv.style = "display: inline-block; margin: 4px;";
                candidateCardRowColOne.appendChild(inlineBlockDiv);

                iconImg = document.createElement("img");
                iconImg.src = "/assets/recruiter/img/icons/age.svg";
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
                candidateCardRow.className = "row";
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
                candidateCardRow.className = "row";
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
                iconImg.setAttribute('height', '24px');
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
                iconImg.setAttribute('height', '24px');
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
                unlockDivRow.style = "margin: 6px; padding: 1%; text-align: right; color: #fff";
                candidateCardContent.appendChild(unlockDivRow);

                //unlock candidate div
                var unlockCandidateBtn = document.createElement("div");
                unlockCandidateBtn.id = "unlock_candidate_" + value.candidate.candidateId;
                unlockCandidateBtn.className = "contactUnlocked right";
                unlockDivRow.appendChild(unlockCandidateBtn);

                //candidate last active container
                var candidateUnlockFont = document.createElement("font");
                candidateUnlockFont.id = "candidate_" + value.candidate.candidateId;
                candidateUnlockFont.textContent = value.candidate.candidateMobile;
                candidateUnlockFont.style = "font-weight: bold; font-size: 14px";
                unlockCandidateBtn.appendChild(candidateUnlockFont);
            });
            $('.tooltipped').tooltip({delay: 50});
        }
    } else{
        $("#noCandidate").show();
        $("#candidateSection").hide();
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

function getMonthVal(month){
    switch(month) {
        case 1:
            return "Jan";
            break;
        case 2:
            return "Feb";
            break;
        case 3:
            return "Mar";
            break;
        case 4:
            return "Apr";
            break;
        case 5:
            return "May";
            break;
        case 6:
            return "Jun";
            break;
        case 7:
            return "Jul";
            break;
        case 8:
            return "Aug";
            break;
        case 9:
            return "Sep";
            break;
        case 10:
            return "Oct";
            break;
        case 11:
            return "Nov";
            break;
        case 12:
            return "Dec";
            break;
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

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}

function getMonthVal(month){
    switch(month) {
        case 1:
            return "Jan";
            break;
        case 2:
            return "Feb";
            break;
        case 3:
            return "Mar";
            break;
        case 4:
            return "Apr";
            break;
        case 5:
            return "May";
            break;
        case 6:
            return "Jun";
            break;
        case 7:
            return "Jul";
            break;
        case 8:
            return "Aug";
            break;
        case 9:
            return "Sep";
            break;
        case 10:
            return "Oct";
            break;
        case 11:
            return "Nov";
            break;
        case 12:
            return "Dec";
            break;
    }
}
