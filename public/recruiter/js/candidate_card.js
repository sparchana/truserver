/**
 * Created by dodo on 20/12/16.
 */

var view_search_candidate = 1;
var view_unlocked_candidate = 2;
var view_applied_candidate = 3;
var view_tracking_candidate = 4;

/* variables, flags and lists for applied candidate use case */
var pendingParent = $("#pendingCandidateContainer");
var confirmedParent = $("#confirmedCandidateContainer");
var completedParent = $("#completedCandidateContainer");

var acceptInterviewFlag = false;
var contactCandidatesFlag = false;
var pendingConfirmationFlag = false;
var rejectedListFlag = false;
var interviewTodayListFlag = false;
var upcomingInterviewsFlag = false;
var pastInterviewsFlag = false;
var completedInterviewsFlag = false;

var actionNeeded = false;
var showStatusFlag = false;
var showContact = false;
var showFeedback = false;

var pendingCount = 0;
var confirmedCount = 0;
var completedCount = 0;
var approvalCount = 0;

// Method to generate individual candidate card
function renderIndividualCandidateCard(value, parent, view) {

    showContact = false;
    showFeedback = false;

    //candidate card
    var candidateCard = document.createElement("div");
    candidateCard.className = "card";

    showStatusFlag = view == view_tracking_candidate;

    if(view == view_tracking_candidate){
        showFeedback = true;
    }

    //since applied candidate has 3 different parent to append to, we are computing the parent inside this method only, so else part is for applied candidates
    if(view != view_applied_candidate){

        //for all candidate card render except applied candidates
        parent.append(candidateCard);
    } else{
        //since we have 3 parents for applied candidate card, we are computing it inside this else part
        //segregation of applications
        actionNeeded = false;
        if(value.extraData.workflowStatus != null){
            if(value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_SCHEDULED){

                // actionNeeded section [pending tab]
                if(!acceptInterviewFlag){ //checking if the accept reject hearderRibbon exists or not
                    var actionNeededHeader = document.createElement("div");
                    actionNeededHeader.textContent = "Application(s) awaiting your confirmation : Please confirm below application(s)";
                    actionNeededHeader.className = "headerRibbon";
                    actionNeededHeader.style = "padding: 8px; text-align: center";
                    pendingParent.append(actionNeededHeader);
                    acceptInterviewFlag = true;
                }
                pendingParent.append(candidateCard);
                pendingCount++;
                approvalCount++;
                actionNeeded = true;

            } else if(value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_RESCHEDULE) {

                // pending candidate confirmation for all rescheduled applications [pending tab]
                if(!pendingConfirmationFlag){ //checking if the rescheduled hearderRibbon exists or not
                    var pendingConfirmationHeader = document.createElement("div");
                    pendingConfirmationHeader.textContent = "You have rescheduled below application(s) : Awaiting candidate's response";
                    pendingConfirmationHeader.className = "headerRibbon";
                    pendingConfirmationHeader.style = "padding: 8px; text-align: center";
                    pendingParent.append(pendingConfirmationHeader);
                    pendingConfirmationFlag = true;
                }
                pendingParent.append(candidateCard);
                pendingCount++;
                actionNeeded = false;

            } else if(value.extraData.workflowStatus.statusId > JWF_STATUS_INTERVIEW_RESCHEDULE && value.extraData.workflowStatus.statusId < JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED){
                var todayDay = new Date();
                var interviewDate = new Date(value.extraData.interviewDate);
                var interviewDay = interviewDate.getDate();
                var interviewMonth = interviewDate.getMonth() + 1;

                //checking today's interview, if yes, it should be on top
                // today's lined up applications/interview [confirmed tab]
                if((todayDay.getDate() == interviewDay) && ((todayDay.getMonth() + 1) == interviewMonth)){

                    if(!interviewTodayListFlag){
                        var interviewTodayHeader = document.createElement("div");
                        interviewTodayHeader.textContent = "Today's interview(s)";
                        interviewTodayHeader.className = "headerRibbon";
                        interviewTodayHeader.style = "padding: 8px; text-align: center";
                        confirmedParent.append(interviewTodayHeader);
                        interviewTodayListFlag = true;
                    }

                    showStatusFlag = true;
                    confirmedParent.append(candidateCard);
                    confirmedCount++;
                    approvalCount++;
                    showContact = true;
                    showFeedback = true;
                } else if(todayDay.getTime() < interviewDate.getTime()){

                    // upcoming interviews [confirmed tab]
                    if(!upcomingInterviewsFlag){
                        var upcomingInterviewHeader = document.createElement("div");
                        upcomingInterviewHeader.textContent = "Upcoming interview(s)";
                        upcomingInterviewHeader.className = "headerRibbon";
                        upcomingInterviewHeader.style = "padding: 8px; text-align: center";
                        confirmedParent.append(upcomingInterviewHeader);
                        upcomingInterviewsFlag = true;
                    }

                    showStatusFlag = true;
                    confirmedParent.append(candidateCard);
                    confirmedCount++;
                    approvalCount++;
                    showContact = true;
                } else{
                    // past interviews interviews [confirmed tab]
                    if(!pastInterviewsFlag){
                        var pastInterviewHeader = document.createElement("div");
                        pastInterviewHeader.textContent = "Past interview(s) : Please update your feedback";
                        pastInterviewHeader.className = "headerRibbon";
                        pastInterviewHeader.style = "padding: 8px; text-align: center";
                        confirmedParent.append(pastInterviewHeader);
                        pastInterviewsFlag = true;
                        showFeedback = true;
                    }

                    showStatusFlag = true;
                    confirmedParent.append(candidateCard);
                    confirmedCount++;
                    showContact = true;
                }
                confirmedParent.append(candidateCard);
                confirmedCount++;
            } else if(value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT || value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE){

                // rejected/not shortlisted applications [pending tab]
                if(!rejectedListFlag){
                    var rejectedHeader = document.createElement("div");
                    rejectedHeader.textContent = "You have not shortlisted the below candidates for interview";
                    rejectedHeader.className = "headerRibbon";
                    rejectedHeader.style = "padding: 8px; text-align: center";
                    pendingParent.append(rejectedHeader);
                    rejectedListFlag = true;
                }

                pendingParent.append(candidateCard);
                pendingCount++;
            } else if(value.extraData.workflowStatus.statusId > JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){

                // completed interviews [completed tab]
                if(!completedInterviewsFlag){
                    var completedHeader = document.createElement("div");
                    completedHeader.textContent = "Completed Interview(s)";
                    completedHeader.className = "headerRibbon";
                    completedHeader.style = "padding: 8px; text-align: center";
                    completedParent.append(completedHeader);
                    completedInterviewsFlag = true;
                }
                completedParent.append(candidateCard);
                completedCount++;
                showContact = true;
            } else if(value.extraData.workflowStatus.statusId == JWF_STATUS_PRESCREEN_COMPLETED){

                // interview not scheduled applications. Manual contact section [pending tab]
                if(!contactCandidatesFlag){
                    contactCandidateHeader = document.createElement("div");
                    contactCandidateHeader.textContent = "Candidate has not scheduled interview for below applications: Unlock contact to talk to the candidate(s)";
                    contactCandidateHeader.className = "headerRibbon";
                    contactCandidateHeader.style = "padding: 8px; text-align: center";
                    pendingParent.append(contactCandidateHeader);
                    contactCandidatesFlag = true;
                }
                pendingParent.append(candidateCard);
                pendingCount++;
            } else {

                // [Default applications] interview not scheduled applications. Manual contact section [pending tab]
                if(!contactCandidatesFlag){
                    contactCandidateHeader = document.createElement("div");
                    contactCandidateHeader.textContent = "Candidate has not scheduled interview for below applications: Unlock contact to talk to the candidate(s)";
                    contactCandidateHeader.className = "headerRibbon";
                    contactCandidateHeader.style = "padding: 8px; text-align: center";
                    pendingParent.append(contactCandidateHeader);
                    contactCandidatesFlag = true;
                }

                pendingCount++;
                approvalCount++;
                actionNeeded = true;
            }
        } else{

            // [Default applications] interview not scheduled applications. Manual contact section [pending tab]
            if(!contactCandidatesFlag){
                var contactCandidateHeader = document.createElement("div");
                contactCandidateHeader.textContent = "Candidate has not scheduled interview for below applications: Unlock contact to talk to the candidate(s)";
                contactCandidateHeader.className = "headerRibbon";
                contactCandidateHeader.style = "padding: 8px; text-align: center";
                pendingParent.append(contactCandidateHeader);
                contactCandidatesFlag = true;
            }
            pendingParent.append(candidateCard);
            pendingCount++;
        }
    }


    var candidateCardContent = document.createElement("div");
    candidateCardContent.className = "card-content";
    candidateCardContent.style = "padding: 0";
    candidateCard.appendChild(candidateCardContent);

    var candidateCardRow = document.createElement("div");
    candidateCardRow.className = "row";
    candidateCardRow.style = "padding: 0; margin: 0 8px 0 8px";
    candidateCardContent.appendChild(candidateCardRow);

    var candidateCardRowColOne = document.createElement("div");
    candidateCardRowColOne.className = "col s12 l10";
    candidateCardRowColOne.style = "padding: 8px; margin-top: 8px";
    candidateCardRow.appendChild(candidateCardRowColOne);

    var userAvatar = document.createElement("img");
    userAvatar.className = "tooltipped";
    userAvatar.style = "margin: -6px 8px 0 -6px; cursor: pointer; text-decoration: none";
    userAvatar.setAttribute("data-postiton", "top");
    userAvatar.setAttribute("data-delay", "50");

    userAvatar.setAttribute('height', '36px');
    if(value.candidate.candidateGender != null){
        if(value.candidate.candidateGender == 0){
            userAvatar.src = "/assets/recruiter/img/icons/male.svg";
            userAvatar.setAttribute("data-tooltip", "Male");
        } else if(value.candidate.candidateGender == 1){
            userAvatar.src = "/assets/recruiter/img/icons/female.svg";
            userAvatar.setAttribute("data-tooltip", "Female");
        } else{
            userAvatar.src = "/assets/recruiter/img/icons/user.svg";
            userAvatar.setAttribute("data-tooltip", "Male");
        }
    }

    candidateCardRowColOne.appendChild(userAvatar);

    //candidate name container
    var candidateCardRowColOneFont = document.createElement("font");
    candidateCardRowColOneFont.setAttribute("size", "5");
    candidateCardRowColOneFont.style = "font-size: 18px; font-weight: bold";
    candidateCardRowColOneFont.textContent = toTitleCase(value.candidate.candidateFullName);
    candidateCardRowColOne.appendChild(candidateCardRowColOneFont);

    var ageVal;
    if (value.candidate.candidateDOB != null) {
        var date = JSON.parse(value.candidate.candidateDOB);
        var yr = new Date(date).getFullYear();
        var month = ('0' + parseInt(new Date(date).getMonth() + 1)).slice(-2);
        d = ('0' + new Date(date).getDate()).slice(-2);
        var today = new Date();
        var birthDate = new Date(yr + "-" + month + "-" + d);
        var age = today.getFullYear() - birthDate.getFullYear();
        var m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        ageVal = age + " years";
    } else{
        ageVal = "age not available";
    }

    var candidateAge = document.createElement("font");
    candidateAge.style = "font-size: 14px";
    candidateAge.textContent = ", " + ageVal;
    candidateCardRowColOne.appendChild(candidateAge);

    //calculating experience
    var expVal = "";
    if(value.candidate.candidateTotalExperience != null){
        if(value.candidate.candidateTotalExperience == 0){
            expVal = "Fresher";
        } else{
            var yrs = parseInt(value.candidate.candidateTotalExperience/12);
            var months = (value.candidate.candidateTotalExperience) % 12;

            if(yrs == 0){
                expVal = "Experienced [" + months + " months]";
            } else if(months == 0){
                expVal = "Experienced [" + + yrs + " year(s)]";
            } else{
                expVal = "Experienced [" + + yrs + " year(s) and " + months + " months]";
            }
        }
    } else{
        expVal = "Experience not specified";
    }

    var candidateExperience = document.createElement("font");
    candidateExperience.style = "font-size: 14px";
    candidateExperience.textContent = ", " + expVal;
    candidateCardRowColOne.appendChild(candidateExperience);

    if(view == view_tracking_candidate || view == view == view_applied_candidate){
        //match score col
        var candidateScoreCol = document.createElement("div");
        candidateScoreCol.className = "col s12 l2";
        candidateScoreCol.style = "color: black; margin-top: 9px; text-align: right; padding: 8px 0 8px 8px";
        candidateCardRow.appendChild(candidateScoreCol);

        var matchVal = document.createElement("span");

        if(value.scoreData != null){
            matchVal.className = "tooltipped matchDiv";
            matchVal.setAttribute("data-postiton", "top");
            matchVal.setAttribute("data-delay", "50");
            matchVal.setAttribute("data-html", true);
            matchVal.setAttribute("data-tooltip", value.scoreData.reason);

            if(value.scoreData.band == 1){
                matchVal.style = "background: #2ec866";
                matchVal.textContent = "Good Match";
            } else if(value.scoreData.band == 2){
                matchVal.style = "background: orange";
                matchVal.textContent = "Moderate Match";
            } else{
                matchVal.style = "background: red";
                matchVal.textContent = "Poor Match";
            }
        }

        candidateScoreCol.appendChild(matchVal);
    }

    /* row no. 2 starts*/
    var candidateCardDivider = document.createElement("div");
    candidateCardDivider.className = "divider";
    candidateCardContent.appendChild(candidateCardDivider);

    candidateCardRow = document.createElement("div");
    candidateCardRow.className = "row";
    candidateCardRow.style = "margin: 4px";
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
    iconImg.setAttribute('height', '18px');
    inlineBlockDiv.appendChild(iconImg);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    var innerInlineBlockDiv = document.createElement("div");
    innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 10px";
    innerInlineBlockDiv.textContent = "Home Locality";
    inlineBlockDiv.appendChild(innerInlineBlockDiv);

    var candidateLocalityVal = document.createElement("div");
    candidateLocalityVal.style = "margin-left: 4px; font-size: 12px";
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
    iconImg.src = "/assets/recruiter/img/icons/education.svg";
    iconImg.style = "margin-top: -4px";
    iconImg.setAttribute('height', '18px');
    inlineBlockDiv.appendChild(iconImg);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    innerInlineBlockDiv = document.createElement("div");
    innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 10px";
    innerInlineBlockDiv.textContent = "Education";
    inlineBlockDiv.appendChild(innerInlineBlockDiv);

    var candidateEducationVal = document.createElement("div");
    candidateEducationVal.style = "margin-left: 4px; font-size: 12px";
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
                        eduVal = eduVal + ")";
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
    iconImg.src = "/assets/recruiter/img/icons/language.svg";
    iconImg.style = "margin-top: -4px";
    iconImg.setAttribute('height', '18px');
    inlineBlockDiv.appendChild(iconImg);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    innerInlineBlockDiv = document.createElement("div");
    innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 10px";
    innerInlineBlockDiv.textContent = "Language(s)";
    inlineBlockDiv.appendChild(innerInlineBlockDiv);

    var candidateLanguageVal = document.createElement("div");
    candidateLanguageVal.style = "margin-left: 4px; font-size: 12px";
    var langList = value.candidate.languageKnownList;
    var langListCount = Object.keys(langList).length;
    var langVal = "";
    var toolTipReq = false;
    if(langListCount > 0){
        langList.forEach(function (language){
            langVal += language.language.languageName + ", ";
        });
        candidateLanguageVal.textContent = langVal.substring(0, langVal.length - 2);
        if(langVal.length > 28){
            toolTipReq = true;
            candidateLanguageVal.textContent = langVal.substring(0, 24) + "...";
        }

    } else{
        candidateLanguageVal.textContent = "Not specified";
    }
    inlineBlockDiv.appendChild(candidateLanguageVal);

    if(toolTipReq){
        toolTip = document.createElement("a");
        toolTip.className = "tooltipped";
        toolTip.style = "cursor: pointer; text-decoration: none";
        toolTip.setAttribute("data-postiton", "top");
        toolTip.setAttribute("data-delay", "50");
        toolTip.setAttribute("data-tooltip", langVal.substring(0, langVal.length - 2));
        toolTip.textContent = ", more";
        candidateLanguageVal.appendChild(toolTip);
    }

    candidateCardRow = document.createElement("div");
    candidateCardRow.className = "row";
    candidateCardRow.style = "margin: 4px";
    candidateCardContent.appendChild(candidateCardRow);

    candidateCardRowColOne = document.createElement("div");
    candidateCardRowColOne.className = "col s12 l4";
    candidateCardRowColOne.style = "margin-top: 4px";
    candidateCardRow.appendChild(candidateCardRowColOne);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block; margin: 4px;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    iconImg = document.createElement("img");
    iconImg.src = "/assets/recruiter/img/icons/company.svg";
    iconImg.style = "margin-top: -4px";
    iconImg.setAttribute('height', '18px');
    inlineBlockDiv.appendChild(iconImg);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    innerInlineBlockDiv = document.createElement("div");
    innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 10px";
    innerInlineBlockDiv.textContent = "Companies Worked";
    inlineBlockDiv.appendChild(innerInlineBlockDiv);

    var currentCompanyVal = document.createElement("div");
    currentCompanyVal.style = "margin-left: 4px; font-size: 12px";

    var companyList = "";
    currentCompanyVal.textContent = "Not Specified";
    if(Object.keys(value.candidate.jobHistoryList).length > 0){
        var pastCompanyList = value.candidate.jobHistoryList;
        pastCompanyList.forEach(function (jobHistory){
            companyList += jobHistory.candidatePastCompany + ", ";
        });
        currentCompanyVal.textContent = companyList.substring(0, (companyList.length - 2));
    } else{
        currentCompanyVal.textContent = "Not specified";
    }

    inlineBlockDiv.appendChild(currentCompanyVal);

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
    iconImg.setAttribute('height', '18px');
    inlineBlockDiv.appendChild(iconImg);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    innerInlineBlockDiv = document.createElement("div");
    innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 10px";
    innerInlineBlockDiv.textContent = "Last Withdrawn Salary";
    inlineBlockDiv.appendChild(innerInlineBlockDiv);

    var lastSalary = "Not specified";
    var candidateLastWithdrawnSalaryVal = document.createElement("div");
    candidateLastWithdrawnSalaryVal.style = "margin-left: 4px; font-size: 12px";
    if(value.candidate.candidateLastWithdrawnSalary != null){
        if(value.candidate.candidateLastWithdrawnSalary == 0){
            if(value.candidate.candidateTotalExperience != null){
                if(value.candidate.candidateTotalExperience == 0){
                    lastSalary = " - (Fresher)";
                }
            } else{
                lastSalary = "Not Specified";
            }
        } else{
            lastSalary = "₹" + rupeeFormatSalary(value.candidate.candidateLastWithdrawnSalary);
        }
    } else{
        lastSalary = "Not Specified";
    }
    candidateLastWithdrawnSalaryVal.textContent = lastSalary;

    inlineBlockDiv.appendChild(candidateLastWithdrawnSalaryVal);

    /* second col */

    //skills
    candidateCardRowColOne = document.createElement("div");
    candidateCardRowColOne.className = "col s12 l4";
    candidateCardRowColOne.style = "margin-top: 4px";
    candidateCardRow.appendChild(candidateCardRowColOne);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block; margin: 4px;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    iconImg = document.createElement("img");
    iconImg.src = "/assets/recruiter/img/icons/skills.svg";
    iconImg.style = "margin-top: -4px";
    iconImg.setAttribute('height', '18px');
    inlineBlockDiv.appendChild(iconImg);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    innerInlineBlockDiv = document.createElement("div");
    innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 10px";
    innerInlineBlockDiv.textContent = "Skill(s)";
    inlineBlockDiv.appendChild(innerInlineBlockDiv);

    var candidateSkillVal = document.createElement("div");
    candidateSkillVal.style = "margin-left: 4px; font-size: 12px";
    candidateSkillVal.id = "skill_" + value.candidate.candidateId;
    var skillList = value.candidate.candidateSkillList;
    var skillListCount = Object.keys(skillList).length;
    var toolTipReq = false;
    if(skillListCount > 0){
        var skillVal = "";
        var allSkillVal = "";
        var count = 0;
        skillList.forEach(function (skill){
            count = count + 1;
            if(count < 4){
                if(skill.candidateSkillResponse == true){
                    skillVal += skill.skill.skillName + ", ";
                    allSkillVal += skill.skill.skillName + ", ";
                    skillCount ++;
                }
            } else{
                if(skill.candidateSkillResponse == true){
                    allSkillVal += skill.skill.skillName + ", ";
                }
            }
        });
        candidateSkillVal.textContent = skillVal.substring(0, skillVal.length - 2);
        if(skillVal.length > 25){
            toolTipReq = true;
            candidateSkillVal.textContent = skillVal.substring(0, 22) + "...";
        }

    } else{
        candidateSkillVal.textContent = "Not specified";
    }
    inlineBlockDiv.appendChild(candidateSkillVal);

    if(skillListCount > 3 || toolTipReq){
        var toolTip = document.createElement("a");
        toolTip.className = "tooltipped";
        toolTip.style = "cursor: pointer; text-decoration: none";
        toolTip.setAttribute("data-postiton", "top");
        toolTip.setAttribute("data-delay", "50");
        toolTip.setAttribute("data-tooltip", allSkillVal.substring(0, allSkillVal.length - 2));
        toolTip.textContent = ", more";
        candidateSkillVal.appendChild(toolTip);
    }


    candidateCardRow = document.createElement("div");
    candidateCardRow.className = "row";
    candidateCardRow.style = "margin: 4px";
    candidateCardContent.appendChild(candidateCardRow);

    //documents
    candidateCardRowColOne = document.createElement("div");
    candidateCardRowColOne.className = "col s12 l4";
    candidateCardRowColOne.style = "margin-top: 4px";
    candidateCardRow.appendChild(candidateCardRowColOne);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block; margin: 4px;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    iconImg = document.createElement("img");
    iconImg.src = "/assets/recruiter/img/icons/document.svg";
    iconImg.style = "margin-top: -4px";
    iconImg.setAttribute('height', '18px');
    inlineBlockDiv.appendChild(iconImg);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    innerInlineBlockDiv = document.createElement("div");
    innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 10px";
    innerInlineBlockDiv.textContent = "Document(s)";
    inlineBlockDiv.appendChild(innerInlineBlockDiv);

    var candidateDocumentVal = document.createElement("div");
    candidateDocumentVal.style = "margin-left: 4px; font-size: 12px";
    candidateDocumentVal.id = "document_" + value.candidate.candidateId;

    var documentList = value.candidate.idProofReferenceList;
    var documentListCount = Object.keys(documentList).length;
    toolTipReq = false;

    if(documentListCount > 0){
        var allDocumentVal = "";
        var documentVal = "";
        var count = 0;
        var skillCount = 0;
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
        if(documentVal.length > 28){
            toolTipReq = true;
            candidateDocumentVal.textContent = documentVal.substring(0, 24) + "...";
        }
    } else{
        candidateDocumentVal.textContent = "Not specified";
    }
    inlineBlockDiv.appendChild(candidateDocumentVal);

    if(documentListCount > 3 || toolTipReq){
        var toolTip = document.createElement("a");
        toolTip.className = "tooltipped";
        toolTip.style = "cursor: pointer; text-decoration: none";
        toolTip.setAttribute("data-postiton", "top");
        toolTip.setAttribute("data-delay", "50");
        toolTip.setAttribute("data-tooltip", allDocumentVal.substring(0, allDocumentVal.length - 2));
        toolTip.textContent = ", more";
        candidateDocumentVal.appendChild(toolTip);
    }

    //assets
    var candidateCardRowColTwo = document.createElement("div");
    candidateCardRowColTwo.className = "col s12 l4";
    candidateCardRowColTwo.style = "margin-top: 4px";
    candidateCardRow.appendChild(candidateCardRowColTwo);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block; margin: 4px;";
    candidateCardRowColTwo.appendChild(inlineBlockDiv);

    iconImg = document.createElement("img");
    iconImg.src = "/assets/recruiter/img/icons/document.svg";
    iconImg.style = "margin-top: -4px";
    iconImg.setAttribute('height', '18px');
    inlineBlockDiv.appendChild(iconImg);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block;";
    candidateCardRowColTwo.appendChild(inlineBlockDiv);

    innerInlineBlockDiv = document.createElement("div");
    innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 10px";
    innerInlineBlockDiv.textContent = "Ownership";
    inlineBlockDiv.appendChild(innerInlineBlockDiv);

    var candidateAssetVal = document.createElement("div");
    candidateAssetVal.style = "margin-left: 4px; font-size: 12px";
    candidateAssetVal.id = "document_" + value.candidate.candidateId;

    var assetList = value.candidate.candidateAssetList;
    var assetListCount = Object.keys(assetList).length;
    toolTipReq = false;

    if(assetListCount > 0){
        var allAssetVal = "";
        var assetVal = "";
        count = 0;
        assetList.forEach(function (asset){
            count = count +1;
            if(count < 4){
                if(asset.asset != null){
                    assetVal += asset.asset.assetTitle + ", ";
                    allAssetVal += asset.asset.assetTitle + ", ";
                }
            } else{
                allAssetVal += asset.asset.assetTitle + ", ";
            }
        });
        candidateAssetVal.textContent = assetVal.substring(0, assetVal.length - 2);
        if(assetVal.length > 28){
            toolTipReq = true;
            candidateAssetVal.textContent = assetVal.substring(0, 24) + "...";
        }
    } else{
        candidateAssetVal.textContent = "Not specified";
    }
    inlineBlockDiv.appendChild(candidateAssetVal);

    if(assetListCount > 3 || toolTipReq){
        toolTip = document.createElement("a");
        toolTip.className = "tooltipped";
        toolTip.style = "cursor: pointer; text-decoration: none";
        toolTip.setAttribute("data-postiton", "top");
        toolTip.setAttribute("data-delay", "50");
        toolTip.setAttribute("data-tooltip", allAssetVal.substring(0, allAssetVal.length - 2));
        toolTip.textContent = ", more";
        candidateAssetVal.appendChild(toolTip);
    }

    var candidateCardRowColThree = document.createElement("div");
    candidateCardRowColThree.className = "col s12 l4";
    candidateCardRowColThree.style = "margin-top: 4px";
    candidateCardRow.appendChild(candidateCardRowColThree);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block; margin: 4px;";
    candidateCardRowColThree.appendChild(inlineBlockDiv);

    iconImg = document.createElement("img");
    iconImg.src = "/assets/recruiter/img/icons/timeshift.svg";
    iconImg.style = "margin-top: -4px";
    iconImg.setAttribute('height', '18px');
    inlineBlockDiv.appendChild(iconImg);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block;";
    candidateCardRowColThree.appendChild(inlineBlockDiv);

    innerInlineBlockDiv = document.createElement("div");
    innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 10px";
    innerInlineBlockDiv.textContent = "Work Shift Preference";
    inlineBlockDiv.appendChild(innerInlineBlockDiv);

    var candidateShiftPref = document.createElement("div");
    candidateShiftPref.textContent = "Not Specified";
    if(value.candidate.timeShiftPreference != null){
        if(value.candidate.timeShiftPreference.timeShift != null){
            candidateShiftPref.textContent = value.candidate.timeShiftPreference.timeShift.timeShiftName;
        }
    }
    candidateShiftPref.style = "margin-left: 4px; font-size: 12px";

    inlineBlockDiv.appendChild(candidateShiftPref);

    hr = document.createElement("hr");
    hr.style = "margin: 8px";
    candidateCardContent.appendChild(hr);

    //interview details for recruiter applied candidate card and recruiter track interview
    if(view == view_tracking_candidate || view == view_applied_candidate){
        candidateCardRow = document.createElement("div");
        candidateCardRow.className = "row";
        candidateCardRow.style = "margin: 4px";
        candidateCardContent.appendChild(candidateCardRow);

        //interview date/time slot
        var scheduledInterviewDate = document.createElement("div");
        scheduledInterviewDate.className = "col s12 l6";
        scheduledInterviewDate.style = "color: black; text-align: left; padding: 8px 0px 8px 12px";
        candidateCardRow.appendChild(scheduledInterviewDate);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block; margin: 4px;";
        scheduledInterviewDate.appendChild(inlineBlockDiv);

        iconImg = document.createElement("img");
        iconImg.src = "/assets/recruiter/img/icons/calender.svg";
        iconImg.style = "margin-top: -4px";
        iconImg.setAttribute('height', '18px');
        inlineBlockDiv.appendChild(iconImg);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.id = "interview_div_" + value.candidate.candidateId;
        inlineBlockDiv.style = "display: inline-block; margin-left: 4px";
        scheduledInterviewDate.appendChild(inlineBlockDiv);

        innerInlineBlockDiv = document.createElement("div");
        innerInlineBlockDiv.style = "color: #9f9f9f; font-size: 10px";
        innerInlineBlockDiv.textContent = "Interview Details";
        if(actionNeeded){
            innerInlineBlockDiv.style = "color: red; font-size: 10px; font-weight: bold; margin-bottom: 6px";
            innerInlineBlockDiv.textContent = "Interview Details (Action Needed)";

        }
        inlineBlockDiv.appendChild(innerInlineBlockDiv);

        var candidateInterviewDateVal = document.createElement("span");

        if(value.extraData.interviewDate != null){
            var interviewDate = new Date(value.extraData.interviewDate);
            var interviewDetails = ('0' + interviewDate.getDate()).slice(-2) + '-' + getMonthVal((interviewDate.getMonth()+1)) + " @" + value.extraData.interviewSlot.interviewTimeSlotName;

            candidateInterviewDateVal.id = "interview_date_" + value.candidate.candidateId;
        } else{
            candidateInterviewDateVal.style = "margin-left: 4px; font-size: 12px";
            interviewDetails = "Interview not scheduled. 'Unlock Contact' to talk to candidate";
        }

        candidateInterviewDateVal.style = "font-size: 12px";
        candidateInterviewDateVal.textContent = interviewDetails + ". ";
        inlineBlockDiv.appendChild(candidateInterviewDateVal);

        var candidateInterviewStatusVal = document.createElement("span");
        if(value.extraData.workflowStatus != null){
            if(value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_SCHEDULED) {
                var interviewStatusDiv = document.createElement("span");
                interviewStatusDiv.id = "interview_status_option_" + value.candidate.candidateId;
                inlineBlockDiv.appendChild(interviewStatusDiv);

                var candidateInterviewAcceptParent = document.createElement("span");
                candidateInterviewAcceptParent.style = "display: inline-block; font-size: 12px";
                candidateInterviewAcceptParent.onclick = function () {
                    oldDate = new Date(value.extraData.interviewDate);
                    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                    globalInterviewSlot = value.extraData.interviewSlot.interviewTimeSlotId;
                    globalSchedule = value.extraData.interviewSchedule;
                    confirmInterviewStatus(value.candidate.candidateId);
                };
                interviewStatusDiv.appendChild(candidateInterviewAcceptParent);

                var candidateInterviewAccept = document.createElement("span");
                candidateInterviewAccept.className = "accept";
                candidateInterviewAcceptParent.appendChild(candidateInterviewAccept);

                iconImg = document.createElement("img");
                iconImg.src = "/assets/dashboard/img/reached.svg";
                iconImg.setAttribute('height', '24px');
                candidateInterviewAccept.appendChild(iconImg);

                var actionText = document.createElement("span");
                actionText.textContent = " Accept";
                candidateInterviewAcceptParent.appendChild(actionText);

                var candidateInterviewRejectParent = document.createElement("span");
                candidateInterviewRejectParent.style = "display: inline-block; font-size: 12px";
                candidateInterviewRejectParent.onclick = function () {
                    oldDate = new Date(value.extraData.interviewDate);
                    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                    globalInterviewSlot = value.extraData.interviewSlot.interviewTimeSlotId;
                    globalSchedule = value.extraData.interviewSchedule;
                    rejectInterview(value.candidate.candidateId);
                };
                interviewStatusDiv.appendChild(candidateInterviewRejectParent);

                var candidateInterviewReject = document.createElement("span");
                candidateInterviewReject.className = "reject";
                candidateInterviewRejectParent.appendChild(candidateInterviewReject);

                iconImg = document.createElement("img");
                iconImg.src = "/assets/dashboard/img/not_going.svg";
                iconImg.setAttribute('height', '24px');
                candidateInterviewReject.appendChild(iconImg);

                actionText = document.createElement("span");
                actionText.textContent = " Reject";
                candidateInterviewRejectParent.appendChild(actionText);

                var candidateInterviewRescheduleParent = document.createElement("span");
                candidateInterviewRescheduleParent.style = "display: inline-block";
                candidateInterviewRescheduleParent.onclick = function () {
                    globalCandidateId = value.candidate.candidateId;
                    oldDate = new Date(value.extraData.interviewDate);
                    globalInterviewDay = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                    globalInterviewSlot = value.extraData.interviewSlot.interviewTimeSlotId;
                    globalSchedule = value.extraData.interviewSchedule;

                    showSlotModal();
                };
                interviewStatusDiv.appendChild(candidateInterviewRescheduleParent);

                var candidateInterviewReschedule = document.createElement("span");
                candidateInterviewReschedule.className = "reschedule";
                candidateInterviewRescheduleParent.appendChild(candidateInterviewReschedule);

                iconImg = document.createElement("img");
                iconImg.src = "/assets/dashboard/img/reschedule.svg";
                iconImg.setAttribute('height', '24px');
                candidateInterviewReschedule.appendChild(iconImg);

                actionText = document.createElement("span");
                actionText.textContent = " Reschedule";
                candidateInterviewRescheduleParent.appendChild(actionText);

            } else if(value.extraData.workflowStatus.statusId > JWF_STATUS_INTERVIEW_RESCHEDULE && value.extraData.workflowStatus.statusId < JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED){
                candidateInterviewStatusVal.textContent = "Interview Confirmed";
                candidateInterviewStatusVal.style = "color: green; font-weight: bold; font-size: 12px";
            } else if(value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT){
                candidateInterviewStatusVal.textContent = "Application Not Shortlisted";
                candidateInterviewStatusVal.style = "color: red; font-weight: bold; font-size: 12px";
            } else if(value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE){
                candidateInterviewStatusVal.textContent = "Interview Rejected by Candidate";
                candidateInterviewStatusVal.style = "color: red; font-weight: bold; font-size: 12px";
            } else if(value.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_RESCHEDULE){
                candidateInterviewStatusVal.textContent = "Interview Rescheduled. Awaiting candidate's response";
                candidateInterviewStatusVal.style = "color: orange; font-weight: bold; font-size: 12px";
            } else if(value.extraData.workflowStatus.statusId > JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){
                candidateInterviewStatusVal.textContent = value.extraData.workflowStatus.statusTitle;
                if(value.extraData.workflowStatus.statusId == JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED){
                    candidateInterviewStatusVal.style = "color: green; font-size: 12px; font-weight: 600";
                } else{
                    candidateInterviewStatusVal.style = "color: red; font-size: 12px; font-weight: 600";
                }
            } else{
                candidateInterviewStatusVal.textContent = "";
            }
        }

        inlineBlockDiv.appendChild(candidateInterviewStatusVal);

        var hr = document.createElement("hr");
        hr.style = "margin: 8px";
        candidateCardContent.appendChild(hr);

    }

    var unlockDivRow = document.createElement("div");
    unlockDivRow.className = "row";
    unlockDivRow.style = "padding: 4px 0 4px 8px; margin: 0; text-align: right; color: #fff";
    candidateCardContent.appendChild(unlockDivRow);

    // candidate last active/unlocked date div
    candidateCardRowColOne = document.createElement("div");
    candidateCardRowColOne.className = "col s12 l4";
    candidateCardRowColOne.style = "text-align: left";
    unlockDivRow.appendChild(candidateCardRowColOne);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block; margin: 0px 4px 4px 0;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    iconImg = document.createElement("img");
    iconImg.src = "/assets/recruiter/img/icons/clock.svg";
    iconImg.style = "margin-top: -4px";
    iconImg.setAttribute('height', '18px');
    inlineBlockDiv.appendChild(iconImg);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block;";
    candidateCardRowColOne.appendChild(inlineBlockDiv);

    innerInlineBlockDiv = document.createElement("div");
    innerInlineBlockDiv.style = "margin-left: 4px; color: #9f9f9f; font-size: 10px";
    if(view == view_unlocked_candidate){
        innerInlineBlockDiv.textContent = "Candidate unlocked on";
    } else{
        innerInlineBlockDiv.textContent = "Last Active";
    }
    inlineBlockDiv.appendChild(innerInlineBlockDiv);

    //candidate last active container
    var candidateCardRowColTwoFont = document.createElement("font");

    candidateCardRowColTwoFont.setAttribute("size", "3");
    candidateCardRowColTwoFont.style = "font-size: 12px; color: black; margin-left: 4px";

    if(view == view_unlocked_candidate){
        var postedOn = new Date(value.createTimestamp);
        candidateCardRowColTwoFont.textContent = "Unlocked on: " + ('0' + postedOn.getDate()).slice(-2) + '-' + getMonthVal((postedOn.getMonth()+1)) + '-' + postedOn.getFullYear();
        candidateCardRowColTwo.appendChild(candidateCardRowColTwoFont);
    } else {
        if(value.extraData.lastActive != null){
            candidateCardRowColTwoFont.textContent = value.extraData.lastActive.lastActiveValueName;
        }
    }

    inlineBlockDiv.appendChild(candidateCardRowColTwoFont);

    // candidate interview status
    var candidateCurrentStatus = document.createElement("div");
    candidateCurrentStatus.className = "col s12 l4";
    candidateCurrentStatus.style = "color: black; text-align: left";
    unlockDivRow.appendChild(candidateCurrentStatus);

    inlineBlockDiv = document.createElement("div");
    inlineBlockDiv.style = "display: inline-block; margin-right: 4px;";
    candidateCurrentStatus.appendChild(inlineBlockDiv);

    //we are using this flag because we want to show candidate status only in todays interview and track interview. Hence in both these cases, we are setting the showStatusFlag as true
    if(showStatusFlag){
        iconImg = document.createElement("img");
        iconImg.src = "/assets/recruiter/img/icons/status.svg";
        iconImg.style = "margin-top: -4px";
        iconImg.setAttribute('height', '18px');
        inlineBlockDiv.appendChild(iconImg);

        inlineBlockDiv = document.createElement("div");
        inlineBlockDiv.style = "display: inline-block;";
        candidateCurrentStatus.appendChild(inlineBlockDiv);

        innerInlineBlockDiv = document.createElement("div");
        innerInlineBlockDiv.style = "color: #9f9f9f; font-size: 10px; margin-left: 4px";
        innerInlineBlockDiv.textContent = "Candidate status";
        inlineBlockDiv.appendChild(innerInlineBlockDiv);

        var candidateCurrentStatusVal = document.createElement("span");
        candidateCurrentStatusVal.textContent = "Status not available";
        candidateCurrentStatusVal.style = "margin-left: 4px; font-size: 12px";

        var candidateCurrentLastUpdateVal = document.createElement("div");
        candidateCurrentLastUpdateVal.style = "font-size: 10px; margin-left: 4px";

        if(value.extraData.candidateInterviewStatus != null){
            var reason = "";
            if(value.extraData.reason != null){
                if(value.extraData.workflowStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING) { //not going
                    reason = ' [Reason: ' + value.extraData.reason.reasonName + ']';
                } else{
                    reason = ' [Reaching: ' + value.extraData.reason.reasonName + ']';
                }
            }

            var lastUpdate = new Date(value.extraData.creationTimestamp);
            var timing = "";
            if(lastUpdate.getHours() == 12){
                timing = minuteHourFormat(lastUpdate.getHours()) + ":" + minuteHourFormat(lastUpdate.getMinutes()) + " pm";
            } else if(lastUpdate.getHours() > 12){
                timing = minuteHourFormat(lastUpdate.getHours() - 12) + ":" + minuteHourFormat(lastUpdate.getMinutes()) + " pm";
            } else{
                timing = minuteHourFormat(lastUpdate.getHours()) + ":" + minuteHourFormat(lastUpdate.getMinutes()) + " am";
            }

            var dateAndTime = "(Reported - " + lastUpdate.getDate() + "-" + (lastUpdate.getMonth() + 1) + "-" + lastUpdate.getFullYear() + " " + timing + ')';

            var today = new Date();
            if(lastUpdate.getDate() == today.getDate() && lastUpdate.getMonth() == today.getMonth()){
                dateAndTime = " (Reported - Today at: " + timing + ")";
            } else if(lastUpdate.getDate() == (today.getDate() -1) && lastUpdate.getMonth() == today.getMonth()){
                dateAndTime = " (Reported - Yesterday at: " + timing + ")";
            }

            if(value.extraData.candidateInterviewStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING){
                candidateCurrentStatusVal.textContent = "Not going for interview " + reason ;
                candidateCurrentStatusVal.style = "margin-left: 4px; color: red; font-weight: bold; font-size: 12px";
            } else if(value.extraData.candidateInterviewStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_DELAYED){
                candidateCurrentStatusVal.textContent = "Delayed for Interview " + reason ;
                candidateCurrentStatusVal.style = "margin-left: 4px; color: orange; font-weight: bold; font-size: 12px";
            } else if(value.extraData.candidateInterviewStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_ON_THE_WAY){
                candidateCurrentStatusVal.textContent = "On the way for interview" + reason ;
                candidateCurrentStatusVal.style = "margin-left: 4px; color: green; font-weight: bold; font-size: 12px";
            } else if(value.extraData.candidateInterviewStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){
                candidateCurrentStatusVal.textContent = "Reached for Interview " + reason ;
                candidateCurrentStatusVal.style = "margin-left: 4px; color: green; font-weight: bold; font-size: 12px";
            }

            iconImg.style = "margin-top: -4px; margin-top: -32px";
            candidateCurrentLastUpdateVal.textContent = dateAndTime;
        }

        inlineBlockDiv.appendChild(candidateCurrentStatusVal);
        inlineBlockDiv.appendChild(candidateCurrentLastUpdateVal);

    }

    /* unlock div col */
    var unlockContactCol = document.createElement("div");
    unlockContactCol.className = "col s12 l4 unlockDiv";
    unlockContactCol.style = "text-align: right";
    unlockDivRow.appendChild(unlockContactCol);

    if(view == view_tracking_candidate || view == view_applied_candidate){
        if(value.extraData.workflowStatus != null){
            if(value.extraData.workflowStatus.statusId > JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){
                var feedbackBtnStatus = document.createElement("div");
                feedbackBtnStatus.className = "feedbackVal";
                if(value.extraData.workflowStatus.statusId == JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED){
                    feedbackBtnStatus.style = "background: rgb(46, 200, 102)";
                }
                feedbackBtnStatus.textContent = value.extraData.workflowStatus.statusTitle;
                unlockContactCol.appendChild(feedbackBtnStatus);
                candidateInterviewStatusVal.textContent = "Interview Completed";
                candidateInterviewStatusVal.style = "color: green; font-weight: bold";
            } else{
                var today = new Date();
                var interviewDate = new Date(value.extraData.interviewDate);
                if(showFeedback){
                    if(interviewDate.getTime() <= today.getTime()) { // today's schedule
                        //interview for this job is scheduled today, hence allow to update status
                        var feedbackBtn = document.createElement("a");
                        feedbackBtn.className = "customFeedbackBtn feedbackVal";
                        feedbackBtn.onclick = function () {
                            openFeedbackModal(value.candidate.candidateId);
                        };
                        feedbackBtn.textContent = "Add Feedback";
                        feedbackBtn.style = "font-size: 12px; background: rgb(46, 200, 102)";
                        unlockContactCol.appendChild(feedbackBtn);
                    }
                }
            }
        }
    }

    if(view == view_unlocked_candidate){
        showContact = false;
    }

    //unlock candidate div
    var unlockCandidateBtn = document.createElement("div");
    unlockCandidateBtn.id = "unlock_candidate_" + value.candidate.candidateId;
    if(view == view_search_candidate || view == view_applied_candidate || view == view_tracking_candidate){
        if(!showContact){
            unlockCandidateBtn.onclick = function () {
                unlockContact(value.candidate.candidateId);
            };
        }

        unlockCandidateBtn.style = "margin-top: -1px";

        unlockCandidateBtn.className = "waves-effect waves-light customUnlockBtn";
    } else if(view == view_unlocked_candidate){
        unlockCandidateBtn.className = "contactUnlocked right";
        unlockCandidateBtn.style = "margin-right: 8px";
    }

    unlockContactCol.appendChild(unlockCandidateBtn);

    var candidateUnlockFont = document.createElement("font");
    candidateUnlockFont.id = "candidate_" + value.candidate.candidateId;
    if(view == view_search_candidate || view == view_applied_candidate || view == view_tracking_candidate){
        candidateUnlockFont.textContent = "Unlock Contact";
        if(showContact){
            unlockCandidateBtn.className = "contactUnlocked right";
            unlockCandidateBtn.style = "margin-right: 8px";
            candidateUnlockFont.textContent = value.candidate.candidateMobile;
        }
    } else if(view == view_unlocked_candidate){
        candidateUnlockFont.textContent = value.candidate.candidateMobile;
    }
    candidateUnlockFont.style = "font-weight: bold; font-size: 12px";
    unlockCandidateBtn.appendChild(candidateUnlockFont);
}