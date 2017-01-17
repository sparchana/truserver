
var applyInShort = (function ($) {

    'use strict';
    var appz = {
        isNavBarLoaded : false,
        loginStatus: null,
        candidateId: null,
        jobPostId: null,
        missingDataURL: null,
        missingData: null,
        jobTitle: null,
        companyName: null,
        factory: {
            parent: null,
            mainDiv: null,
            subDivOne: null,
            subDivTwo: null,
            orderList: null,
            hintMessage: null, // mot sure if this is requred any longer

          factory: function () {
              this.parent = $('#missingInfo');
              this.mainDiv = document.createElement("div");
              this.mainDiv.className = "row";
              this.parent.append(this.mainDiv);

              this.subDivOne = document.createElement("div");
              this.subDivOne.className = "col-sm-12";

              this.mainDiv.appendChild(this.subDivOne);

              this.hintMessage = document.createElement("p");
              this.hintMessage.textContent = "Please provide following details to apply for this job";
              this.hintMessage.style = "margin:0;font-weight:bold;font-size:18px";

              this.subDivOne.appendChild(this.hintMessage);

              this.subDivTwo = document.createElement("div");
              this.subDivTwo.style = "padding:0 4%";
              this.subDivTwo.className = "col-sm-12";
              this.mainDiv.appendChild(this.subDivTwo);

              this.orderList = document.createElement("ol");
              this.orderList.className = "list-group";
              this.subDivTwo.appendChild(this.orderList);
          } // constructor
        },

        method: {
            init: function () {
                appz.factory.factory();
                appz.validation.checkIsUserLoggedIn();
                appz.method.prepareRequestParam();
                appz.render.applyJobForm();

            },
            ending: function () {
                $('#footer_inc').load('/footer');
            },
            getUserLogInStatus: function () {
                if (!appz.isNavBarLoaded) {
                    return $.ajax({type: 'GET', url: '/checkNavBar'});
                } else {
                    // new promise says its already there
                    return new Promise(function (resolve, reject) {
                        resolve(null);
                    });
                }
            },
            getMissingData: function () {
                if (appz.missingDataURL != null) {
                    return $.ajax({type: 'GET', url: appz.missingDataURL});
                } else {
                    // new promise says its already there
                    return new Promise(function (resolve, reject) {
                        resolve(null);
                    });
                }
            },
            prepareRequestParam: function () {
                var pathNameList = window.location.pathname.split('-');
                appz.jobPostId = pathNameList[pathNameList.length -1];
                var searchParam = window.location.search.split("&")[0].split("=");
                appz.candidateId = searchParam[searchParam.length -1];

                appz.missingDataURL =
                             "/apply/inshort/api/getMissingDate?candidateId="+appz.candidateId
                            +"&jobPostId="+appz.jobPostId;
            }
        },

        fetch: {
          missingData: function () {
              return new Promise(function (resolve, reject) {
                      appz.method.getMissingData().then(
                          function (returnedData) {
                              if (returnedData != null) {
                                  appz.missingData = returnedData;
                              }
                              resolve();
                          },
                          function (xhr, state, error) {
                              reject(error);
                          }
                      )
                  }
              );
          }
        },

        render: {
            applyJobForm: function () {

                var promise = appz.fetch.missingData();

                promise.then(function () {

                    /* render locality card */
                    if(appz.missingData.localityPopulateResponse != null) {

                        appz.render.jobLocalityCard(appz.missingData.localityPopulateResponse);
                    }

                    /* TODO render prescreen card */
                    if(appz.missingData.shortPSPopulateResponse != null) {
                        appz.render.preScreenCard(appz.missingData.shortPSPopulateResponse);
                    }


                    /* render interview slot card */
                    if(appz.missingData.interviewSlotPopulateResponse != null
                        && appz.missingData.interviewSlotPopulateResponse.interviewResponse.status == 2
                        && appz.missingData.interviewSlotPopulateResponse.interviewSlotMap != null) {

                        appz.render.interviewSlotCard(appz.missingData.interviewSlotPopulateResponse.interviewSlotMap);
                    } else {
                        $('#jobInterviewSlotCardDiv').hide();
                    }

                    console.log(appz.missingData);

                }).catch(function (fromReject) {
                    console.log(fromReject);
                });

            },
            jobLocalityCard: function (localityResponse) {
                console.log("rendering jobLocality card");

                var localityMap = localityResponse.localityMap;

                appz.jobTitle = localityResponse.jobTitle;
                appz.companyName = localityResponse.companyName;

                $('#locality_jobNameConfirmation').html(appz.jobTitle);
                $('#locality_companyNameConfirmation').html(appz.companyName);

                if(localityMap != null) {
                    var option = $('<option value=0></option>').text("Select Location");
                    $('#jobLocality').append(option);
                    for (var value in localityMap) {
                        var id = value;
                        var title = localityMap[value];
                        var option = $('<option value=' + id + '></option>').text(title);
                        $('#jobLocality').append(option);
                    }
                }
                /* clear mem */
                localityMap = null;
            },
            interviewSlotCard: function (slotMap) {
                console.log("rendering interview slot card");

                if(slotMap == null) {
                    return;
                }


                $('#interviewJobTitle').html(appz.jobTitle);
                $('#interviewCompanyName').html(appz.companyName);

                if(slotMap != null) {
                    var option = $('<option value=0></option>').text("Select Interview slot");
                    $('#interViewSlot').append(option);
                    for (var value in slotMap) {
                        var date = slotMap[value].interviewDateMillis;
                        var id = date +"_"+slotMap[value].interviewTimeSlot.slotId;
                        var title = value;
                        var option = $('<option value=' + id + '></option>').text(title);
                        $('#interViewSlot').append(option);
                    }
                }
            },
            preScreenCard: function (ps) {

                // list
                if(ps.documentList != null && ps.documentList.length >= 0){
                    appz.construct._document(ps.documentList);
                }

                if(ps.languageList != null && ps.languageList.length >= 0){
                    appz.construct._language(ps.languageList);
                }

                if(ps.assetList != null && ps.assetList.length >= 0) {
                    appz.construct._asset(ps.assetList);
                }

                // booleans
                if(ps.dobMissing != null && ps.dobMissing) {
                    appz.construct._dob();
                }

                if(ps.genderMissing != null && ps.genderMissing) {
                    appz.construct._gender();
                }

                if(ps.salaryMissing != null && ps.salaryMissing) {
                    appz.construct._salary();
                }

                // object
                if(ps.experienceResponse != null && ps.experienceResponse.experienceMissing) {
                    appz.construct._experience(ps.experienceResponse.jobRoleList);
                }

                if(ps.educationResponse != null && ps.educationResponse.educationMissing) {
                    appz.construct._education(ps.educationResponse.educationList, ps.educationResponse.degreeList);
                }
            }
        },

        construct: {

            _document: function (documentList) {
                var firstproperty = document.createElement("li");
                firstproperty.textContent = "Do you have the following document(s) ?";
                firstproperty.style = "color:#09ac58";

                var rowBox = document.createElement("div");
                rowBox.className = "row";

                var documentDetails = document.createElement("div");
                documentDetails.className = "col-sm-12 col-md-12";
                documentDetails.id = "document_details";
                documentDetails.style = "padding:2%";

                rowBox.appendChild(documentDetails);
                firstproperty.appendChild(rowBox);

                appz.factory.orderList.appendChild(firstproperty);

                // render doc data
                appz.process.document(documentList);
            },

            _language: function (languageList) {
                /* language html */
                var secondProperty = document.createElement("li");
                secondProperty.textContent = "Do you know following language(s) ?";

                var rowBox = document.createElement("div");
                rowBox.className = "row";

                var languageDetails = document.createElement("div");
                languageDetails.className = "col-sm-12";
                languageDetails.id = "language_details";
                languageDetails.style = "padding:1%;";

                rowBox.appendChild(languageDetails);
                secondProperty.appendChild(rowBox);

                appz.factory.orderList.appendChild(secondProperty);

                // now render language
                appz.process.language(languageList);
            },

            _dob: function () {
                //    age html
                var thirdproperty = document.createElement("li");
                thirdproperty.textContent = "Please mention your date of birth";
                appz.factory.orderList.appendChild(thirdproperty);

                var rowBox = document.createElement("div");
                rowBox.className = "row";

                var ageTitle = document.createElement("div");
                ageTitle.className = "col-xs-12 col-sm-5";
                ageTitle.style = "padding:2% 3%";
                rowBox.appendChild(ageTitle);

                var ageResponse = document.createElement("div");
                ageResponse.className = "col-xs-12 col-sm-6";
                ageResponse.style = "padding:1%";
                rowBox.appendChild(ageResponse);

                var ageText = document.createElement("font");
                ageText.textContent = ("Date of birth");
                ageText.style = "font-weight:bold";
                ageTitle.appendChild(ageText);

                var dayCandidate = document.createElement("select");
                dayCandidate.className = "selectDropDown";
                dayCandidate.id = "dob_day";
                dayCandidate.style = "margin:0 1%";
                ageResponse.appendChild(dayCandidate);

                var dayOption = document.createElement("option");
                dayOption.textContent="Day";
                dayCandidate.appendChild(dayOption);

                var i;
                for(i=1;i<=31;i++){
                    var option = document.createElement("option");
                    option.value = ('0' + i).slice(-2);
                    option.textContent = i;
                    dayCandidate.appendChild(option);
                }

                var monthCandidate = document.createElement("select");
                monthCandidate.className = "selectDropDown";
                monthCandidate.id = "dob_month";
                monthCandidate.style = "margin:0 1%";
                ageResponse.appendChild(monthCandidate);

                var monthOption = document.createElement("option");
                monthOption.textContent="Month";
                monthCandidate.appendChild(monthOption);

                for(i=1;i<=12;i++){
                    option = document.createElement("option");
                    option.value = ('0' + i).slice(-2);
                    var monthName;
                    switch(i){
                        case 1: monthName = "January"; break;
                        case 2: monthName = "February"; break;
                        case 3: monthName = "March"; break;
                        case 4: monthName = "April"; break;
                        case 5: monthName = "May"; break;
                        case 6: monthName = "June"; break;
                        case 7: monthName = "July"; break;
                        case 8: monthName = "August"; break;
                        case 9: monthName = "September"; break;
                        case 10: monthName = "October"; break;
                        case 11: monthName = "November"; break;
                        case 12: monthName = "December"; break;
                    }
                    option.textContent = monthName;
                    monthCandidate.appendChild(option);
                }

                var yearCandidate = document.createElement("select");
                yearCandidate.className = "selectDropDown";
                yearCandidate.id = "dob_year";
                yearCandidate.style = "margin:0 1%";
                ageResponse.appendChild(yearCandidate);

                var yearOption = document.createElement("option");
                yearOption.textContent="Year";
                yearCandidate.appendChild(yearOption);

                for(i = new Date().getFullYear() - 19;i>=new Date().getFullYear() - 80;i--){
                    option = document.createElement("option");
                    option.value = i;
                    option.textContent = i;
                    yearCandidate.appendChild(option);
                }

                thirdproperty.appendChild(rowBox);
                appz.factory.orderList.appendChild(thirdproperty);

                // nothing to process
            },

            _experience: function (jobRoleList) {
                // currently working
                var fourthproperty = document.createElement("li");
                fourthproperty.textContent = "Do you have work experience?";

                var rowBox = document.createElement("div");
                rowBox.className = "row";

                var rowBoxDetails = document.createElement("div");
                rowBoxDetails.className = "row";
                rowBoxDetails.style = "display: none;";
                rowBoxDetails.id = "companyDetailsCapture";


                var allworkedCompanyDetails = document.createElement("p");
                allworkedCompanyDetails.textContent = ("Where all have you worked before? ");
                allworkedCompanyDetails.style = "margin-left:10px";
                rowBoxDetails.appendChild(allworkedCompanyDetails);

                var allworkedCompanyDetailsDiv = document.createElement("div");
                allworkedCompanyDetailsDiv.className = "row";
                allworkedCompanyDetailsDiv.id = "row_1";
                allworkedCompanyDetailsDiv.style = "margin:4px 0";
                rowBoxDetails.appendChild(allworkedCompanyDetailsDiv);


                var allCompanyNameCol = document.createElement("div");
                allCompanyNameCol.className = "col-sm-3";
                allCompanyNameCol.id = "companyName";
                allworkedCompanyDetailsDiv.appendChild(allCompanyNameCol);

                var allworkedJobRoleCol = document.createElement("div");
                allworkedJobRoleCol.className = "col-sm-3";
                allworkedJobRoleCol.id = "workedJobRole";
                allworkedCompanyDetailsDiv.appendChild(allworkedJobRoleCol);

                var allWorkedCurrentltyCol = document.createElement("div");
                allWorkedCurrentltyCol.className = "col-sm-4"
                allWorkedCurrentltyCol.id = "workedCurrently";
                allWorkedCurrentltyCol.style = "padding-top:1%;text-align:center";
                allworkedCompanyDetailsDiv.appendChild(allWorkedCurrentltyCol);

                var allWorkedAddMoreCol = document.createElement("div");
                allWorkedAddMoreCol.className = "col-sm-2";
                allworkedCompanyDetailsDiv.appendChild(allWorkedAddMoreCol);

                var addCompanyName = document.createElement("input");
                addCompanyName.className = "form-control";
                addCompanyName.type = ("text");
                addCompanyName.placeholder = ("Company Name");
                addCompanyName.id = "companyName_1";
                addCompanyName.onchange = applyInShort.validation.enableAddBtn;
                allCompanyNameCol.appendChild(addCompanyName);

                var addJobRole = document.createElement("input");
                addJobRole.id = "workedJobRole_1";
                addJobRole.onchange = applyInShort.validation.enableAddBtn;
                allworkedJobRoleCol.appendChild(addJobRole);

                var addCurrentlyWorking = document.createElement("input");
                addCurrentlyWorking.type = ("radio");
                addCurrentlyWorking.style = "margin:0 4%;";
                addCurrentlyWorking.id = "addCurrentlyWorking_1";
                addCurrentlyWorking.name = ("addCurrently_Working");
                addCurrentlyWorking.setAttribute("disabled", true);
                addCurrentlyWorking.value = (0);
                allWorkedCurrentltyCol.appendChild(addCurrentlyWorking);

                var addMore = document.createElement("button");
                addMore.className = "form-control";
                addMore.type = "button";
                addMore.setAttribute("disabled",true);
                addMore.id = "addCurrentlyWorkingBtn_1";
                addMore.value = "Add";
                addMore.name = "Add";
                addMore.style = "background:#09ac58;color:#fff;font-size:12px";
                addMore.textContent = "Add Company";
                // addMore.onclick = addmoreCompany;

                allWorkedAddMoreCol.appendChild(addMore);
                addCurrentlyWorking.type = ("radio");
                addCurrentlyWorking.style = "margin:0 4%";
                // addCurrentlyWorking.id = ("addCurrentlyWorking_" + companyCount);
                addCurrentlyWorking.name = ("addCurrently_Working");
                allWorkedCurrentltyCol.appendChild(addCurrentlyWorking);

                var addCurrentlyWorkingLabel = document.createElement("label");
                addCurrentlyWorkingLabel.textContent = ("Is this your current company");
                addCurrentlyWorkingLabel.for = ("addCurrentlyWorking_1");
                allWorkedCurrentltyCol.appendChild(addCurrentlyWorkingLabel);

                // render all jobs here
                var experienceOption = document.createElement("div");
                experienceOption.className = "col-xs-12 col-md-12";
                experienceOption.style = "padding:2% 0;";
                rowBox.appendChild(experienceOption);

                var experienceQuestion= document.createElement("div");
                experienceQuestion.className = "col-xs-12 col-sm-5";
                experienceQuestion.id = "experienceQuestion";
                experienceQuestion.style = "padding:2% 3%;display:none";
                rowBox.appendChild(experienceQuestion);

                var experienceText = document.createElement("p");
                experienceText.textContent = ("How many year(s) of work experience do you have?");
                experienceQuestion.appendChild(experienceText);

                var experienceDuration = document.createElement("div");
                experienceDuration.className = "col-xs-12 col-md-6";
                experienceDuration.id = "experienceDuration";
                experienceDuration.style = "display:none;padding:1%";
                rowBox.appendChild(experienceDuration);

                var colDetailsFresher = document.createElement("div");
                colDetailsFresher.className = "col-xs-12 col-sm-3 ";
                experienceOption.appendChild(colDetailsFresher);

                var colDetailsExperience = document.createElement("div");
                colDetailsExperience.className = "col-xs-12 col-sm-4";
                experienceOption.appendChild(colDetailsExperience);

                var radioFresher = document.createElement("input");
                radioFresher.type = ("radio");
                radioFresher.id = ("candidateFresh");
                radioFresher.name = ("candidateExperience");
                radioFresher.value = (0);
                radioFresher.onchange = applyInShort.aux.hideExperienceCaptureDiv;
                colDetailsFresher.appendChild(radioFresher);

                var labelFresher = document.createElement("label");
                labelFresher.textContent = ("I'm a fresher");
                labelFresher.for = ("candidateFresh");
                colDetailsFresher.appendChild(labelFresher);

                var radioExperience = document.createElement("input");
                radioExperience.type = ("radio");
                radioExperience.id = ("candidateExp");
                radioExperience.name = ("candidateExperience");
                radioExperience.value = (1);
                radioExperience.onchange = applyInShort.aux.showExperienceCaptureDiv;
                colDetailsExperience.appendChild(radioExperience);

                var labelExperience = document.createElement("label");
                labelExperience.textContent = ("I have work experience");
                labelExperience.for = ("candidateExp");
                colDetailsExperience.appendChild(labelExperience);


                var textYear = document.createElement("input");
                textYear.className = "form-control";
                textYear.type = ("number");
                textYear.value = 0;
                textYear.oninput = applyInShort.aux.showExperienceBox;
                textYear.id = ("candidateTotalExperienceYear");
                experienceDuration.appendChild(textYear);

                var titleExpYear = document.createElement("font");
                titleExpYear.textContent = ("Years");
                titleExpYear.style = "font-weight:bold";
                experienceDuration.appendChild(titleExpYear);

                var textMonths = document.createElement("input");
                textMonths.className = "form-control";
                textMonths.type = ("number");
                textMonths.value = 0;
                textMonths.id = ("candidateTotalExperienceMonth");
                textMonths.oninput = applyInShort.aux.showExperienceBox;
                experienceDuration.appendChild(textMonths);

                var titleExpMonths = document.createElement("font");
                titleExpMonths.textContent = ("Months");
                titleExpMonths.style = "font-weight:bold";
                experienceDuration.appendChild(titleExpMonths);

                var experienceCurrently = document.createElement("div");
                experienceCurrently.className = "col-xs-12 col-md-10";
                experienceCurrently.id = "experienceCurrently";
                experienceCurrently.style = "padding:2% 3%;display:none";
                rowBox.appendChild(experienceCurrently);

                var currentlyWorking = document.createElement("p");
                currentlyWorking.textContent = ("Are you currently working : ");
                experienceCurrently.appendChild(currentlyWorking);

                var checkboxCurrentlyWorking = document.createElement("input");
                checkboxCurrentlyWorking.id = ("currentlyWorking");
                checkboxCurrentlyWorking.type = ("checkbox");
                checkboxCurrentlyWorking.style = "margin:0 2% 0 4%";
                checkboxCurrentlyWorking.onclick = applyInShort.aux.disableCurrentCompanyOption;
                currentlyWorking.appendChild(checkboxCurrentlyWorking);

                var labelYes = document.createElement("label");
                labelYes.textContent = ("Yes");
                labelYes.for = ("currentlyWorking");
                currentlyWorking.appendChild(labelYes);

                fourthproperty.appendChild(rowBox);
                fourthproperty.appendChild(rowBoxDetails);

                appz.factory.orderList.appendChild(fourthproperty);
            },

            _education: function (educationList, degreeList) {
                /// education
                var fifthproperty = document.createElement("li");
                fifthproperty.textContent = "Please provide your education details";

                var rowBoxHEQ = document.createElement("div");
                rowBoxHEQ.className = "row";
                fifthproperty.appendChild(rowBoxHEQ);

                var rowBoxSuccess = document.createElement("div");
                rowBoxSuccess.className = "row";
                fifthproperty.appendChild(rowBoxSuccess);

                var rowBoxHED = document.createElement("div");
                rowBoxHED.className = "row";
                fifthproperty.appendChild(rowBoxHED);

                var rowBoxLastInstitute = document.createElement("div");
                rowBoxLastInstitute.className = "row";
                fifthproperty.appendChild(rowBoxLastInstitute);

                //Highest Education Qualification

                var educationDetailsHEQ = document.createElement("div");
                educationDetailsHEQ.className = "col-xs-12 col-sm-5";
                educationDetailsHEQ.id = "education_details";
                rowBoxHEQ.appendChild(educationDetailsHEQ);

                var educationResponseHEQ = document.createElement("div");
                educationResponseHEQ.className = "col-xs-12 col-sm-6";
                educationResponseHEQ.id = "education_details";
                educationResponseHEQ.style = "padding:1%";
                rowBoxHEQ.appendChild(educationResponseHEQ);

                var educationTitleHEQ = document.createElement("font");
                educationTitleHEQ.textContent = ("Highest education qualification? ");
                educationTitleHEQ.style = "font-weight:bold;";
                educationDetailsHEQ.appendChild(educationTitleHEQ);

                var educationOptionHEQ = document.createElement("select");
                educationOptionHEQ.id = "candidateHighestEducation";
                educationResponseHEQ.appendChild(educationOptionHEQ);

                //Highest Education Successfully Complete

                var educationDetailsSuccess = document.createElement("div");
                educationDetailsSuccess.className = "col-xs-12 col-sm-5";
                educationDetailsSuccess.id = "education_details";
                rowBoxSuccess.appendChild(educationDetailsSuccess);

                var educationResponseSuccess = document.createElement("div");
                educationResponseSuccess.className = "col-xs-12 col-sm-6";
                educationResponseSuccess.id = "education_details";
                educationResponseSuccess.style = "padding-top:3%";
                rowBoxSuccess.appendChild(educationResponseSuccess);

                var educationTitleSuccess = document.createElement("font");
                educationTitleSuccess.textContent = ("Have you successfully completed this course? ");
                educationTitleSuccess.style = "font-weight:bold;";
                educationDetailsSuccess.appendChild(educationTitleSuccess);

                var successYes = document.createElement("input");
                successYes.type = ("radio");
                successYes.style = "margin:0 4% 0 0";
                successYes.id = ("successYes");
                successYes.name = ("candidateEducationCompletionStatus");
                successYes.value = (1);
                educationResponseSuccess.appendChild(successYes);

                var labelYesSuccess = document.createElement("label");
                labelYesSuccess.textContent = ("Yes");
                labelYesSuccess.for = ("successConformationHEQ");
                educationResponseSuccess.appendChild(labelYesSuccess);

                var successNo = document.createElement("input");
                successNo.type = ("radio");
                successNo.style = "margin:0 4% 0 8%";
                successNo.id = ("successNo");
                successNo.name = ("candidateEducationCompletionStatus");
                successNo.value = (0);
                educationResponseSuccess.appendChild(successNo);

                var labelNoSuccess = document.createElement("label");
                labelNoSuccess.textContent = ("No");
                labelNoSuccess.for = ("successConformationHEQ");
                educationResponseSuccess.appendChild(labelNoSuccess);

                //Highest Education Degree

                var educationDetailsHED = document.createElement("div");
                educationDetailsHED.className = "col-xs-12 col-sm-5";
                educationDetailsHED.id = "education_details";
                rowBoxHED.appendChild(educationDetailsHED);

                var educationResponseHED = document.createElement("div");
                educationResponseHED.className = "col-xs-12 col-sm-6";
                educationResponseHED.id = "education_details";
                educationResponseHED.style = "padding:1%";
                rowBoxHED.appendChild(educationResponseHED);

                var educationTitleHED = document.createElement("font");
                educationTitleHED.textContent = ("Highest education degree? ");
                educationTitleHED.style = "font-weight:bold;";
                educationDetailsHED.appendChild(educationTitleHED);

                var educationOptionHED = document.createElement("select");
                educationOptionHED.id = "candidateHighestDegree";
                educationResponseHED.appendChild(educationOptionHED);

                //Last Attended Institute

                var educationDetailsInstitute = document.createElement("div");
                educationDetailsInstitute.className = "col-xs-12 col-sm-5";
                educationDetailsInstitute.style = "padding:2% 3%";
                rowBoxLastInstitute.appendChild(educationDetailsInstitute);
                var educationResponseInstitute = document.createElement("div");

                educationResponseInstitute.className = "col-xs-12 col-sm-6";
                educationResponseInstitute.style = "padding:1%";
                rowBoxLastInstitute.appendChild(educationResponseInstitute);

                var educationtitleInstitute = document.createElement("font");
                educationtitleInstitute.textContent = ("Last attended education institute?");
                educationtitleInstitute.style = "margin-top:8px;font-weight:bold";
                educationDetailsInstitute.appendChild(educationtitleInstitute);

                var educationtextInstitute = document.createElement("input");
                educationtextInstitute.className = "form-control";
                educationtextInstitute.type = ("text");
                educationtextInstitute.placeholder = ("School/Institute Name");
                educationtextInstitute.id = ("candidateEducationInstitute");
                educationResponseInstitute.appendChild(educationtextInstitute);

                appz.factory.orderList.appendChild(fifthproperty);

                // render education
                appz.process.degree(degreeList);
                appz.process.education(educationList);
            },

            _gender: function () {
                var sixthproperty = document.createElement("li");
                sixthproperty.textContent = "Please mention your gender ";

                var rowBox = document.createElement("div");
                rowBox.className = "row";

                var genderDetails = document.createElement("div");
                genderDetails.className = "col-sm-12 col-md-12";
                genderDetails.id = "gender_details";
                genderDetails.style = "padding:2% 0;";

                var colGenderDetailsMale = document.createElement("div");
                colGenderDetailsMale.className = "col-xs-12 col-sm-3";
                genderDetails.appendChild(colGenderDetailsMale);

                var colGenderDetailsFemale = document.createElement("div");
                colGenderDetailsFemale.className = "col-xs-12 col-sm-3";
                genderDetails.appendChild(colGenderDetailsFemale);

                var labelMale = document.createElement("label");
                labelMale.textContent = ("Male");
                labelMale.for = ("genderMale");
                var radioMale = document.createElement("input");
                radioMale.type = ("radio");
                radioMale.style = "margin:0 8%";
                radioMale.id = ("genderMale");
                radioMale.name = ("gender");
                radioMale.value = (0);

                var labelFemale = document.createElement("label");
                labelFemale.textContent = ("Female");
                labelFemale.for = ("genderFemale");
                var radioFemale = document.createElement("input");
                radioFemale.type = ("radio");
                radioFemale.style = "margin:0 8%";
                radioFemale.id = ("genderFemale");
                radioFemale.name = ("gender");
                radioFemale.value = (1);

                colGenderDetailsMale.appendChild(radioMale);
                colGenderDetailsMale.appendChild(labelMale);
                colGenderDetailsFemale.appendChild(radioFemale);
                colGenderDetailsFemale.appendChild(labelFemale);
                rowBox.appendChild(genderDetails);
                sixthproperty.appendChild(rowBox);
                appz.factory.orderList.appendChild(sixthproperty);
            },

            _salary: function () {
                var seventhproperty = document.createElement("li");
                seventhproperty.textContent = "Please provide your salary details";

                var rowBox = document.createElement("div");
                rowBox.className = "row";

                var salaryDetails = document.createElement("div");
                salaryDetails.className = "col-xs-12 col-sm-5";
                salaryDetails.id = "salary_details";

                var salaryResponse = document.createElement("div");
                salaryResponse.className = "col-xs-12 col-sm-6";
                salaryResponse.id = "salary_response";
                salaryResponse.style = "padding:1%";

                var titleSalary = document.createElement("font");
                titleSalary.textContent = ("Current/last drawn salary");
                titleSalary.style = "margin-top:8px;font-weight:bold";
                salaryDetails.appendChild(titleSalary);

                var textSalary = document.createElement("input");
                textSalary.className = "form-control";
                textSalary.type = ("number");
                textSalary.placeholder = ("Salary");
                textSalary.id = ("candidateLastWithdrawnSalary");
                textSalary.oninput = applyInShort.validation.salary;
                salaryResponse.appendChild(textSalary);

                var salaryInvalid = document.createElement("p");
                salaryInvalid.id = "invalidSalaryNotification";
                salaryInvalid.textContent = "Invalid Salary Input Please enter a valid 'Last Withdrawn Salary' (Ex: 15000) in a month";
                salaryInvalid.style = "color:#ff1744;display:none;margin:1px 0 1px 2px;font-size:12px";
                salaryResponse.appendChild(salaryInvalid);

                var inhandText = document.createElement("span");
                inhandText.className = "label label-success";
                inhandText.textContent = ("(InHand/Month)");
                inhandText.style = "margin:8px 16px;font-size:12px";
                salaryDetails.appendChild(inhandText);

                rowBox.appendChild(salaryDetails);
                rowBox.appendChild(salaryResponse);
                seventhproperty.appendChild(rowBox);
                appz.factory.orderList.appendChild(seventhproperty);
            },

            _locality: function () {
                var eigthproperty = document.createElement("li");
                eigthproperty.textContent = "Which is your home locality? ";

                var rowBox = document.createElement("div");
                rowBox.className = "row";

                var localityDetails = document.createElement("div");
                localityDetails.className = "col-sm-12 col-md-12";
                localityDetails.id = "locality_details";
                localityDetails.style = "padding:2%";

                var titleLocality = document.createElement("p");
                titleLocality.textContent = ("Locality");
                titleLocality.style = "margin-left:18px;font-weight:bold";

                var allLocalityDetail = document.createElement("input");
                allLocalityDetail.id = "candidateHomeLocality";
                eigthproperty.appendChild(allLocalityDetail);

                // render locality

                titleLocality.appendChild(allLocalityDetail);
                localityDetails.appendChild(titleLocality);
                rowBox.appendChild(localityDetails);
                eigthproperty.appendChild(rowBox);
                appz.factory.orderList.appendChild(eigthproperty);
            },

            _asset: function (assetList) {
                // asset html
                var ninthProperty = document.createElement("li");
                ninthProperty.textContent = "Do you own any of the following ?";

                var rowBox = document.createElement("div");
                rowBox.className = "row";

                var assetsDetails = document.createElement("div");
                assetsDetails.className = "col-md-12";
                assetsDetails.id = "assets_details";
                assetsDetails.style = "padding:2% 0";

                rowBox.appendChild(assetsDetails);
                ninthProperty.appendChild(rowBox);

                appz.factory.orderList.appendChild(ninthProperty);

                // render asset here
                appz.process.asset(assetList);
            },
        },

        process: {
            document: function (documentList) {

                if(documentList != null && documentList.length > 0) {

                    var responseInput = $('#document_details');

                    documentList.forEach(function (idProof) {
                        console.log(idProof);
                        // to do check idproof obj here
                        var documentResponseCol = document.createElement("div");
                        documentResponseCol.className = "col-sm-6 col-md-4";
                        documentResponseCol.id = "document_response_checkbox";
                        responseInput.append(documentResponseCol);

                        var documentResponseRow1 = document.createElement("div");
                        documentResponseRow1.className = "row";
                        documentResponseRow1.id = "document_response_checkbox";
                        documentResponseRow1.style = "padding:2% 0";
                        documentResponseCol.appendChild(documentResponseRow1);

                        var documentResponseRow2 = document.createElement("div");
                        documentResponseRow2.className = "row";
                        documentResponseRow2.id = "document_response_input_" + idProof.idProofId;
                        documentResponseRow2.style = "padding:2% 2%;display:none;";
                        documentResponseCol.appendChild(documentResponseRow2);

                        var checkMatchLabel = document.createElement("label");
                        checkMatchLabel.type = "checkbox";
                        checkMatchLabel.for = "idProofCheckbox_" + idProof.idProofId;
                        checkMatchLabel.style = 'margin:4px 10px';
                        documentResponseRow1.appendChild(checkMatchLabel);

                        var checkMatch = document.createElement("input");
                        checkMatch.type = "checkbox";
                        checkMatch.id = "idProofCheckbox_" + idProof.idProofId;
                        checkMatch.onclick = function () {
                            if ($("#idProofCheckbox_" + idProof.idProofId).prop('checked') == true) {
                                $("#document_response_input_" + idProof.idProofId).css("display", "block");
                            }
                            else {
                                $("#document_response_input_" + idProof.idProofId).css("display", "none");
                                $("#Invalid_" + idProof.idProofId).css("display", "none");
                            }

                        };
                        checkMatchLabel.appendChild(checkMatch);

                        var idProofTitleTd = document.createElement("font");
                        idProofTitleTd.textContent = idProof.idProofName;
                        idProofTitleTd.style = "margin:8% 4%";
                        documentResponseRow1.appendChild(idProofTitleTd);

                        var alertInvalid = document.createElement("p");
                        alertInvalid.id = "Invalid_" + idProof.idProofId;
                        alertInvalid.textContent = "(Invalid Number)";
                        alertInvalid.style = "color:#ff1744;display:none;margin:1px 0 1px 42px;font-size:12px";
                        documentResponseRow1.appendChild(alertInvalid);

                        var idProofNumberTd = document.createElement("p");
                        idProofNumberTd.id = "idProofValueTd_" + idProof.idProofId;
                        documentResponseRow2.appendChild(idProofNumberTd);

                        var ip = document.createElement("INPUT");
                        ip.className = "form-control";
                        ip.setAttribute("type", "text");
                        ip.oninput = applyInShort.validation.input;
                        ip.placeholder = idProof.idProofName + " Number";
                        ip.setAttribute("id", "idProofValue_" + idProof.idProofId);
                        idProofNumberTd.appendChild(ip);

                    });
                }
            },

            language: function (languageList) {
                var arrayLang = [];
                var arrayLangId = [];

                languageList.forEach(function (language) {
                    console.log(language);
                    var id = language.languageId;
                    var name = language.languageName;
                    var item = {};
                    item ["id"] = id;
                    item ["name"] = name;
                    arrayLang.push(name);
                    arrayLangId.push(id);
                    var option = $('<option value=' + id + '></option>').text(name);
                });
                this.populateLanguages(arrayLang.reverse(), arrayLangId.reverse());
            },

            populateLanguages: function (l, lId) {
                var i;
                var table = document.getElementById("language_details");
                for (i = 0; i < l.length; i++) {
                    if (lId[i] == 1 || lId[i] == 2 || lId[i] == 3 || lId[i] == 4 || lId[i] == 5) {

                        var colLanguageTitle = document.createElement("div");
                        colLanguageTitle.className = "col-xs-12 col-sm-3";
                        colLanguageTitle.style = "padding-top:8px";
                        table.appendChild(colLanguageTitle);

                        var colLanguageUnder = document.createElement("div");
                        colLanguageUnder.className = "col-xs-12 col-sm-3";
                        table.appendChild(colLanguageUnder);

                        var colLanguageRead = document.createElement("div");
                        colLanguageRead.className = "col-xs-12 col-sm-3";
                        table.appendChild(colLanguageRead);

                        var colLanguageWrite = document.createElement("div");
                        colLanguageWrite.className = "col-xs-12 col-sm-3";
                        table.appendChild(colLanguageWrite);

                        var cell1 = document.createElement("font");
                        cell1.style = "font-size:16px;font-weight:bold";
                        colLanguageTitle.appendChild(cell1);

                        var cell2 = document.createElement("div");
                        colLanguageUnder.appendChild(cell2);

                        var cell3 = document.createElement("div");
                        colLanguageRead.appendChild(cell3);

                        var cell4 = document.createElement("div");
                        colLanguageWrite.appendChild(cell4);


                        cell1.innerHTML = '<span style="margin-top:8px">' + l[i] + '</span>';
                        cell2.innerHTML = "<div class=\"btn-group\" style=\"margin:6px 0px\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"u\" value=0 >Understand</label></div>";
                        cell3.innerHTML = "<div class=\"btn-group\" style=\"margin:6px 0px\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"s\" value=0 >Speak</label></div>";
                        cell4.innerHTML = "<div class=\"btn-group\" style=\"margin:6px 0px\"  data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"rw\" value=0 >Read/Write</label></div>";
                    }
                }
            },

            experience: function () {
                if (jobRoleArray != null && jobRoleArray.length > 0) {
                    $("#workedJobRole_"+id).tokenInput(jobRoleArray, {
                        theme: "facebook",
                        placeholder: "Job Role?",
                        hintText: "Select job role",
                        minChars: 0,
                        tokenLimit: 1,
                        zindex: 9999,
                        preventDuplicates: true
                    });
                    disableCurrentCompanyOption;
                }
            },

            education: function (returnedEdu) {
                if (returnedEdu != null) {
                    var data = [{label: "None Selected", value: -1}];

                    returnedEdu.forEach(function (education) {
                        var opt = {
                            label: education.educationName, value: parseInt(education.educationId)
                        };
                        data.push(opt);
                    });

                    var selectList = $('#candidateHighestEducation');
                    selectList.multiselect({
                        nonSelectedText: 'None Selected',
                        includeSelectAllOption: true,
                        maxHeight: 300
                    });
                    selectList.multiselect('dataprovider', data);
                    selectList.multiselect('rebuild');
                }
            },

            degree: function (degreeList) {
                if (degreeList != null) {
                    var data = [{label: "None Selected", value: -1}];

                    degreeList.forEach(function (degree) {
                        var opt = {
                            label: degree.degreeName, value: parseInt(degree.degreeId)
                        };
                        data.push(opt);
                    });

                    var selectList = $('#candidateHighestDegree');
                    selectList.multiselect({
                        nonSelectedText: 'None Selected',
                        includeSelectAllOption: true,
                        maxHeight: 300
                    });
                    selectList.multiselect('dataprovider', data);
                    selectList.multiselect('rebuild');
                }
            },

            asset: function (assetList) {
                var selectList = $('#assets_details');

                assetList.forEach(function (asset) {
                    console.log(asset);

                    var assetsResponseCol = document.createElement("div");
                    assetsResponseCol.className = "col-xs-12 col-sm-3 col-md-3";
                    selectList.append(assetsResponseCol);

                    var checkMatchLabel = document.createElement("label");
                    checkMatchLabel.type = "checkbox";
                    checkMatchLabel.for = "assetsCheckbox_" + asset.assetTitle;
                    checkMatchLabel.style = 'margin:4px 10px';
                    assetsResponseCol.appendChild(checkMatchLabel);

                    var checkMatch = document.createElement("input");
                    checkMatch.type = "checkbox";
                    checkMatch.id = "assetsCheckboxId_" + asset.assetId;

                    var assetsTitle = document.createElement("font");
                    assetsTitle.textContent = asset.assetTitle;
                    assetsTitle.style = "margin:8% 4%";
                    assetsResponseCol.appendChild(assetsTitle);

                    checkMatchLabel.appendChild(checkMatch);
                });
            },
            // locality: function (localityList) {
            //
            // // }

        },

        validation: {
            checkIsUserLoggedIn: function () {
                var promise = new Promise(function (resolve, reject) {
                        appz.method.getUserLogInStatus().then(
                            function (returnedData) {
                                if (returnedData != null) {
                                    appz.loginStatus = returnedData;
                                }
                                resolve();
                            },
                            function (xhr, state, error) {
                                reject(error);
                            }
                        )
                    }
                );
                promise.then(function () {
                    if (appz.loginStatus == '1') {
                        $('#nav_bar_inc').load('/navBarLoggedIn');
                        $(".jobApplyBtnV2").show();
                        $("#incentiveSection").hide();
                    } else if (appz.loginStatus == '2') {
                        $('#nav_bar_inc').load('/partnerNavBarLoggedIn');
                        $(".jobApplyBtnV2").hide();
                        $("#incentiveSection").show();
                    } else {
                        $('#nav_bar_inc').load('/navBar');
                        $("#incentiveSection").hide();
                    }
                    appz.isNavBarLoaded = true;
                }).catch(function (fromReject) {
                    console.log(fromReject);
                });
            },
            input: function (idProofId, value) {
                if (idProofId == null || value == null) {
                    idProofId = this.id.split("_")[1];
                    value = this.value;
                };
                if(!$('input#idProofCheckbox_' + idProofId).is(':checked')) {
                    return true;
                } /*else if($('input#idProofValue_'+idProofId).val().trim() == ""){
                 $("#Invalid_" + idProofId).css("display", "block");
                 return false;
                 } else {
                 $("#Invalid_" + idProofId).css("display", "none");
                 }*/
                // if(value == "") {
                //     $("#Invalid_" + idProofId).css("display", "none");
                //     return true;
                // }
                // aadhaar validation
                if (idProofId == 3) {
                    if (!validateAadhar(value)) {
                        $("#Invalid_" + idProofId).css("display", "block");
                        return false;
                    } else {
                        $("#Invalid_" + idProofId).css("display", "none");
                        return true;
                    }
                } else if (idProofId == 1) {
                    if (!validateDL(value)) {
                        $("#Invalid_" + idProofId).css("display", "block");
                        return false;
                    } else {
                        $("#Invalid_" + idProofId).css("display", "none");
                        return true;
                    }
                } else if (idProofId == 2) {
                    if (!validatePASSPORT(value)) {
                        $("#Invalid_" + idProofId).css("display", "block");
                        return false;
                    } else {
                        $("#Invalid_" + idProofId).css("display", "none");
                        return true;
                    }
                } else if (idProofId == 4) {
                    if (!validatePAN(value)) {
                        $("#Invalid_" + idProofId).css("display", "block");
                        return false;
                    } else {
                        $("#Invalid_" + idProofId).css("display", "none");
                        return true;
                    }
                }else{
                    return true;
                }
            },
            salary: function () {
                var salary = $('#candidateLastWithdrawnSalary').val();
                if (!isNaN(salary) && parseInt(salary) >= 1000 && parseInt(salary) <= 99000) {
                    $("#invalidSalaryNotification").css("display", "none");
                } else {
                    $("#invalidSalaryNotification").css("display", "block");
                }
            },
            enableAddBtn: function () {
                var id = this.id.split("_")[1];
                if($("#companyName_" + id).val() == "" || $("#workedJobRole_" + id).val() == "") {
                    $("#addCurrentlyWorkingBtn_" + id).prop("disabled",true);
                }
                else {
                    $("#addCurrentlyWorkingBtn_" + id).prop("disabled",false);
                }
            }
        },
        aux: {
            hideExperienceCaptureDiv: function () {
                if ($("#candidateFresh").is(":checked")) {
                    $("#experienceDuration").css("display", "none");
                    $("#experienceQuestion").css("display", "none");
                    $("#experienceCurrently").css("display", "none");
                    $('#companyDetailsCapture').hide();
                }
            },
            showExperienceCaptureDiv: function () {
                if ($("#candidateExp").is(":checked")) {
                    $("#experienceDuration").css("display", "block");
                    $("#experienceQuestion").css("display", "block");
                }
            },
            showExperienceBox: function () {
                var yearValue = $("#candidateTotalExperienceYear").val();
                var monthValue = $("#candidateTotalExperienceMonth").val();
                if (!isNaN(yearValue) && parseInt(yearValue) > 0 || !isNaN(monthValue) && parseInt(monthValue) > 0 ) {
                    $("#companyDetailsCapture").css("display", "block");
                    $("#experienceCurrently").css("display", "block");
                }
                else{
                    $("#companyDetailsCapture").css("display", "none");
                    $("#experienceCurrently").css("display", "none");
                }
            },
            disableCurrentCompanyOption: function () {
                if (!$("#currentlyWorking").is(":checked")) {
                    var radios = document.getElementsByName('addCurrently_Working');
                    for (var i = 0; i < radios.length; i++) {
                        radios[i].disabled = true;
                        radios[i].checked = false;
                    }
                    // document.getElementsByName("addCurrently_Working").disabled = true;
                    //$("#addCurrentlyWorking").prop("disabled",true);
                }
                else {
                    if ($("#currentlyWorking").is(":checked")) {
                        var radios = document.getElementsByName('addCurrently_Working');
                        for (var i = 0; i < radios.length; i++) {
                            radios[i].disabled = false;
                        }
                        // $("#addCurrentlyWorking").prop("disabled",false);
                    }
                }
            }
        }

    };

    appz.method.init();

    appz.method.ending();

    return appz;

}(jQuery));