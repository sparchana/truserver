/**
 * Created by hawk on 21/10/16.
 */
$(document).scroll(function(){
    if ($(this).scrollTop() > 30) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});
$(document).ready(function(){
    checkRecruiterLogin();
    $(".unlockNav").addClass("active");
    $(".unlockNavMobile").addClass("active");

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
            returnedData.reverse();
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
                        var mnths = (value.candidate.candidateTotalExperience) % 12;

                        if(yrs == 0){
                            expVal = "Experienced (" + mnths + " months)";
                        } else if(mnths == 0){
                            expVal = "Experienced (" + + yrs + " year(s))";
                        } else{
                            expVal = "Experienced (" + + yrs + " year(s) and " + mnths + " months)";
                        }
                    }
                } else{
                    expVal = "Experience not specified";
                }

                var candidateExperience = document.createElement("font");
                candidateExperience.style = "font-size: 14px";
                candidateExperience.textContent = ", " + expVal;
                candidateCardRowColOne.appendChild(candidateExperience);



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
                innerInlineBlockDiv.textContent = "Current Company";
                inlineBlockDiv.appendChild(innerInlineBlockDiv);

                var currentCompanyVal = document.createElement("div");
                currentCompanyVal.style = "margin-left: 4px; font-size: 12px";

                var companyList = "";
                var currentCompany = "";
                currentCompanyVal.textContent = "Not Specified";
                if(Object.keys(value.candidate.jobHistoryList).length > 0){
                    var pastCompanyList = value.candidate.jobHistoryList;
                    pastCompanyList.forEach(function (jobHistory){
                        if(jobHistory.currentJob){
                            currentCompany = jobHistory.candidatePastCompany + "*";
                        } else{
                            companyList += jobHistory.candidatePastCompany + ", ";
                        }
                    });
                }

                if(currentCompany == ""){
                    currentCompanyVal.textContent = "Not specified";
                } else if(companyList == ""){
                    currentCompanyVal.textContent = currentCompany;
                } else{
                    currentCompanyVal.textContent = currentCompany + ", " + companyList.substring(0, (companyList.length - 2));
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
                toolTipReq = false;
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
                candidateCardRowColTwo = document.createElement("div");
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

                var hr = document.createElement("hr");
                hr.style = "margin: 8px";
                candidateCardContent.appendChild(hr);

                var unlockDivRow = document.createElement("div");
                unlockDivRow.className = "row";
                unlockDivRow.style = "padding: 0px 0px 8px; margin: 0; text-align: right; color: #fff";
                candidateCardContent.appendChild(unlockDivRow);

                // candidate last active div
                candidateCardRowColOne = document.createElement("div");
                candidateCardRowColOne.className = "col s12 l6";
                candidateCardRowColOne.style = "text-align: left";
                unlockDivRow.appendChild(candidateCardRowColOne);

                inlineBlockDiv = document.createElement("div");
                inlineBlockDiv.style = "display: inline-block; margin: 4px 4px 4px 8px;";
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
                innerInlineBlockDiv.textContent = "Last Active";
                inlineBlockDiv.appendChild(innerInlineBlockDiv);

                //candidate last active container
                var candidateCardRowColTwoFont = document.createElement("font");
                candidateCardRowColTwoFont.setAttribute("size", "3");
                candidateCardRowColTwoFont.style = "font-size: 12px; color: black; margin-left: 4px";
                var postedOn = new Date(value.createTimestamp);
                candidateCardRowColTwoFont.textContent = "Unlocked on: " + ('0' + postedOn.getDate()).slice(-2) + '-' + getMonthVal((postedOn.getMonth()+1)) + '-' + postedOn.getFullYear();
                candidateCardRowColTwo.appendChild(candidateCardRowColTwoFont);

                inlineBlockDiv.appendChild(candidateCardRowColTwoFont);


                /* unlock div col */
                var unlockContactCol = document.createElement("div");
                unlockContactCol.className = "col s12 l6 unlockDiv";
                unlockContactCol.style = "text-align: right";
                unlockDivRow.appendChild(unlockContactCol);

                //unlock candidate div
                var unlockCandidateBtn = document.createElement("div");
                unlockCandidateBtn.id = "unlock_candidate_" + value.candidate.candidateId;
                unlockCandidateBtn.className = "contactUnlocked right";
                unlockCandidateBtn.style = "margin-right: 8px";
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