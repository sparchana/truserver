
var applyInShort = (function ($) {

    'use strict';
    var appz = {
        isNavBarLoaded : false,
        isInterviewSlotAvailable: false,
        loginStatus: null,
        candidateId: null,
        jobPostId: null,
        missingDataURL: null,
        missingData: null,
        jobTitle: null,
        companyName: null,
        allJobRole: null,
        companyCount: 1,
        factory: {
            parent: null,
            mainDiv: null,
            subDivOne: null,
            subDivTwo: null,
            orderList: null,
            hintMessage: null,

          factory: function () {
              this.parent = $('#missingInfo');
              this.mainDiv = document.createElement("div");
              this.mainDiv.className = "row";
              this.mainDiv.style = "margin:0";
              this.parent.append(this.mainDiv);

              this.subDivOne = document.createElement("div");
              this.subDivOne.className = "col-sm-12";

              this.mainDiv.appendChild(this.subDivOne);

              this.hintMessage = document.createElement("p");
              this.hintMessage.textContent = "(Optional) Give some more details to apply";
              this.hintMessage.style = "margin:10px 0;font-weight:bold;font-size:18px";

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
                appz.render.hideContainer();
                appz.factory.factory();
                appz.validation.checkIsUserLoggedIn();
                appz.method.prepareRequestParam();
                appz.render.applyJobForm();
            },
            ending: function () {
                appz.render.showContainer();
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
                             "/apply/inshort/api/getMissingData?candidateId="+appz.candidateId
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
            messageModal: function (message, sec) {
                var title = "Notice";
                $("#messagePromptModal").modal();
                $("#myModalLabel").html(title);
                $("#customMsg").html(message);

                if(sec != null) {
                    appz.render.hideContainer();
                }
            },
            hideContainer: function () {
                $('#card_container').hide();
            },

            showContainer: function () {
                $('#card_container').show();
            },
            hideUI: function(message, sec){
                appz.render.messageModal(message, null);
                // $.notify(message, 'error');

                $('#card_container').hide();
            },
            applyJobForm: function () {

                var promise = appz.fetch.missingData();

                promise.then(function () {
                    console.log(appz.missingData.statusCode);
                    console.log(appz.missingData.status);
                    $("#finalSubmitBtn").prop("disabled", true);

                    // already applied
                    if(appz.missingData.statusCode == 5) {
                        appz.render.messageModal("You have already applied to this Job.", 4000);
                    }
                    // no job with this id
                    else if(appz.missingData.statusCode == 6) {
                        appz.render.messageModal("Invalid Link. No Job Found.", 4000);
                    }
                    // candidate deactivate
                    else if(appz.missingData.statusCode == 7) {
                        appz.render.messageModal(appz.missingData.message + ". ", 7000);
                    }
                    // success | display ui
                    else if(appz.missingData.statusCode == 4){
                        appz.render.jobBasicDetails();
                        $("#finalSubmitBtn").prop("disabled", false);
                        /* render locality card */
                        if(appz.missingData.localityPopulateResponse != null) {

                            appz.render.jobLocalityCard(appz.missingData.localityPopulateResponse);
                        }

                        if(appz.missingData.shortPSPopulateResponse != null && appz.missingData.shortPSPopulateResponse.visible) {
                            appz.render.preScreenCard(appz.missingData.shortPSPopulateResponse);
                        } else {
                            // hide panel
                            $('#preScreenCardDiv').hide();
                        }

                        /* render interview slot card */
                        if(appz.missingData.interviewSlotPopulateResponse != null
                            && appz.missingData.interviewSlotPopulateResponse.interviewResponse.status == 2
                            && appz.missingData.interviewSlotPopulateResponse.interviewSlotMap != null) {
                            appz.render.interviewSlotCard(appz.missingData.interviewSlotPopulateResponse.interviewSlotMap);
                        } else {
                            $('#jobInterviewSlotCardDiv').hide();
                            appz.isInterviewSlotAvailable = false;
                        }

                        console.log(appz.missingData);
                    } else {
                        // something went wrong
                        // hide container

                    }


                }).catch(function (fromReject) {
                    console.log(fromReject);
                });

            },
            jobBasicDetails:function () {
                var parentSalary = $("#jobSalaryIncentives");
                var parentTime = $("#jobTime");

                var jobTimeString = "";
                var jobSalaryString = "";

                var salaryText = document.createElement("font");
                if (appz.missingData.jobPost.jobPostMinSalary != null && appz.missingData.jobPost.jobPostMinSalary != 0) {
                    if (appz.missingData.jobPost.jobPostMaxSalary == null || appz.missingData.jobPost.jobPostMaxSalary == "0") {
                        jobSalaryString += " ₹ "+rupeeFormatSalary(appz.missingData.jobPost.jobPostMinSalary) + " monthly";
                    }
                    else {
                        jobSalaryString += " ₹ "+rupeeFormatSalary(appz.missingData.jobPost.jobPostMinSalary) +" -  ₹ " + rupeeFormatSalary(appz.missingData.jobPost.jobPostMaxSalary)+ " monthly";
                    }
                }
                if(appz.missingData.jobPost.jobPostIncentives != null && appz.missingData.jobPost.jobPostIncentives != ""){
                    jobSalaryString += " ("+appz.missingData.jobPost.jobPostIncentives+")";
                }
                salaryText.textContent = jobSalaryString;
                parentSalary.append(salaryText);

                if(appz.missingData.jobPost.jobPostStartTime != null && appz.missingData.jobPost.jobPostStartTime != -1
                    && appz.missingData.jobPost.jobPostEndTime != null && appz.missingData.jobPost.jobPostEndTime != null != -1)
                {
                    var valStart;
                    var valEnd;
                    if (appz.missingData.jobPost.jobPostStartTime > 12) {
                        appz.missingData.jobPost.jobPostStartTime = appz.missingData.jobPost.jobPostStartTime - 12;
                        valStart = "PM";
                    }
                    else {
                        valStart = "AM";
                    }
                    if (appz.missingData.jobPost.jobPostEndTime > 12) {
                        appz.missingData.jobPost.jobPostEndTime = appz.missingData.jobPost.jobPostEndTime - 12;
                        valEnd = "PM";
                    }
                    else {
                        valEnd = "AM";
                    }
                    jobTimeString += ", "+appz.missingData.jobPost.jobPostStartTime + " " + valStart + " - " + appz.missingData.jobPost.jobPostEndTime + " " + valEnd;
                }
                if (appz.missingData.jobPost.jobPostWorkingDays != "" && appz.missingData.jobPost.jobPostWorkingDays != null) {
                    if(appz.missingData.jobPost.jobPostWorkingDays == 127){
                        jobTimeString += " (No - Off) ";
                    }else {
                        var workingDays = appz.missingData.jobPost.jobPostWorkingDays.toString(2);
                        var i;
                        /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
                        if (workingDays.length != 7) {
                            var x = 7 - workingDays.length;
                            var modifiedWorkingDays = "";

                            for (i = 0; i < x; i++) {
                                modifiedWorkingDays += "0";
                            }
                            modifiedWorkingDays += workingDays;
                            workingDays = modifiedWorkingDays;
                        }
                        var holiday = "";
                        var arryDay = workingDays.split("");
                        if (arryDay[0] != 1) {
                            holiday += "Mon, ";
                        }
                        if (arryDay[1] != 1) {
                            holiday += "Tue, ";
                        }
                        if (arryDay[2] != 1) {
                            holiday += "Wed, ";
                        }
                        if (arryDay[3] != 1) {
                            holiday += "Thu, ";
                        }
                        if (arryDay[4] != 1) {
                            holiday += "Fri, ";
                        }
                        if (arryDay[5] != 1) {

                            holiday += "Sat, ";
                        }
                        if (arryDay[6] != 1) {
                            holiday += "Sun ";
                        }
                        jobTimeString += " ("+ holiday + " - Off)";
                    }
                }
                var salaryTime = document.createElement("font");
                salaryTime.textContent = appz.missingData.jobPost.jobPostShift.timeShiftName+ jobTimeString;
                parentTime.append(salaryTime);
                if(appz.missingData.jobPost.jobPostDescription != null){
                    if(appz.missingData.jobPost.jobPostDescription.length >65){
                        var description = appz.missingData.jobPost.jobPostDescription;

                        description = description.substr(0,65);

                        description = description.substr(0,Math.min(description.length,description.lastIndexOf(" ")));

                        $("#jobDescription").html(description +'<a id="more">..more</a>');
                        $("#jobDescriptionFull").html(appz.missingData.jobPost.jobPostDescription +'<a id="less">..less</a>');
                        $("#jobDescriptionFull").css("display","none");

                        $("#more").on('click',function () {
                            $("#jobDescription").hide();
                            $("#jobDescriptionFull").show();
                        });
                        $("#less").on('click',function () {
                            $("#jobDescriptionFull").hide();
                            $("#jobDescription").show();
                        });
                    } else{
                        $("#jobDescription").html(appz.missingData.jobPost.jobPostDescription);
                    }
                } else{
                    $("#jobDescriptionContainer").remove();
                }

            },
            jobLocalityCard: function (localityResponse) {
                console.log("rendering jobLocality card");

                var localityMap = localityResponse.localityMap;

                appz.jobTitle = localityResponse.jobTitle +" ";
                appz.companyName = localityResponse.companyName;
                if(appz.missingData.jobPost.gender == 0){
                    $('#genderDetail').html("(Male Only)");
                }else if(appz.missingData.jobPost.gender == 1){
                    $('#genderDetail').html("(Female Only)");
                }else{
                    $('#genderDetail').hide();
                }
                $('#locality_jobNameConfirmation').html(appz.jobTitle);
                $('#locality_companyNameConfirmation').html(appz.companyName);

                if (localityMap != null) {
                    /* add select location hint if locality more the one  */
                    if(Object.keys(localityMap).length > 1){
                        var option = $('<option value=0></option>').text("Select Location");
                        $('#jobLocality').append(option);
                    }
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
            jobOtherDetailsCard: function (otherDetailsResponse) {
                console.log("rendering jobother deatils card");
                var parent = $("#preScreenCardDiv");

            },
            interviewSlotCard: function (slotMap) {
                console.log("rendering interview slot card");

                if(slotMap == null) {
                    appz.isInterviewSlotAvailable = false;
                    return;
                }

                appz.isInterviewSlotAvailable = true;

                $('#interviewJobTitle').html(appz.jobTitle);
                $('#interviewCompanyName').html(appz.companyName);

                if(slotMap != null) {
                    /* add select interview slot hint if slot is more the one  */
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
                if(ps.documentList != null && ps.documentList.length > 0){
                    appz.construct._document(ps.documentList);
                }

                if(ps.languageList != null && ps.languageList.length > 0){
                    appz.construct._language(ps.languageList);
                }

                if(ps.assetList != null && ps.assetList.length > 0) {
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
                var thirdProperty = document.createElement("li");
                thirdProperty.textContent = "Please mention your date of birth";
                appz.factory.orderList.appendChild(thirdProperty);

                var rowBox = document.createElement("div");
                rowBox.className = "row";

                var ageTitle = document.createElement("div");
                ageTitle.className = "col-sm-5 hidden-xs";
                ageTitle.style = "padding:14px 3%";
                rowBox.appendChild(ageTitle);

                var ageResponse = document.createElement("div");
                ageResponse.className = "col-xs-12 col-sm-6 ";
                ageResponse.style = "padding:1% 2%";
                rowBox.appendChild(ageResponse);

                var ageText = document.createElement("font");
                ageText.textContent = ("Date of birth");
                ageText.style = "font-weight:bold";
                ageTitle.appendChild(ageText);

                var dayCandidate = document.createElement("select");
                dayCandidate.className = "selectDropDown";
                dayCandidate.id = "dob_day";
                dayCandidate.style = "margin:0 2%";
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
                monthCandidate.style = "margin:0 2%";
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
                yearCandidate.style = "margin:0 2%";
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

                thirdProperty.appendChild(rowBox);
                appz.factory.orderList.appendChild(thirdProperty);

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
                addCompanyName.type = "text";
                addCompanyName.placeholder = "Company Name";
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
                addMore.onclick = applyInShort.aux.addmoreCompany;

                allWorkedAddMoreCol.appendChild(addMore);
                addCurrentlyWorking.type = ("radio");
                addCurrentlyWorking.style = "margin:0 4%";
                // addCurrentlyWorking.id = ("addCurrentlyWorking_" + appz.companyCount);
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
                textYear.placeholder = "Years";
                textYear.style = "margin-top:1%";
                textYear.oninput = applyInShort.aux.showExperienceBox;
                textYear.id = ("candidateTotalExperienceYear");
                experienceDuration.appendChild(textYear);

                var textMonths = document.createElement("input");
                textMonths.className = "form-control";
                textMonths.type = ("number");
                textMonths.placeholder = "Months";
                textMonths.id = ("candidateTotalExperienceMonth");
                textMonths.oninput = applyInShort.aux.showExperienceBox;
                experienceDuration.appendChild(textMonths);


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

                appz.process.experience(jobRoleList, appz.companyCount);
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
                colGenderDetailsMale.className = "col-xs-6 col-sm-3";
                genderDetails.appendChild(colGenderDetailsMale);

                var colGenderDetailsFemale = document.createElement("div");
                colGenderDetailsFemale.className = "col-xs-6 col-sm-3";
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
                titleSalary.textContent = "Current/last drawn salary";
                titleSalary.style = "margin-top:8px;font-weight:bold";
                salaryDetails.appendChild(titleSalary);

                var textSalary = document.createElement("input");
                textSalary.className = "form-control";
                textSalary.type = "number";
                textSalary.placeholder = "Salary InHand/Month";
                textSalary.id = ("candidateLastWithdrawnSalary");
                textSalary.oninput = applyInShort.validation.salary;
                salaryResponse.appendChild(textSalary);

                var salaryInvalid = document.createElement("p");
                salaryInvalid.id = "invalidSalaryNotification";
                salaryInvalid.textContent = "Invalid Salary Input Please enter a valid 'Last Withdrawn Salary' (Ex: 15000) in a month";
                salaryInvalid.style = "color:#ff1744;display:none;margin:1px 0 1px 2px;font-size:12px";
                salaryResponse.appendChild(salaryInvalid);

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
                assetsDetails.style = "padding:1%";

                rowBox.appendChild(assetsDetails);
                ninthProperty.appendChild(rowBox);

                appz.factory.orderList.appendChild(ninthProperty);

                // render asset here
                appz.process.asset(assetList);
            }
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
                        documentResponseRow1.style = "margin:0";
                        documentResponseCol.appendChild(documentResponseRow1);

                        var documentResponseRow2 = document.createElement("div");
                        documentResponseRow2.className = "row";
                        documentResponseRow2.id = "document_response_input_" + idProof.idProofId;
                        documentResponseRow2.style = "padding:2% 2%;display:none;margin:0";
                        documentResponseCol.appendChild(documentResponseRow2);

                        var documentBtnGroup = document.createElement("div");
                        documentBtnGroup.className = "btn-group";
                        documentBtnGroup.style = "width:100%";
                        documentBtnGroup.setAttribute('data-toggle','buttons');
                        documentResponseRow1.appendChild(documentBtnGroup);

                        var documentLabel = document.createElement("label");
                        documentLabel.className = "btn btn-custom-check";
                        documentLabel.style = "margin:6px 0px;width:100%";
                        documentLabel.id = "label_"+idProof.idProofId;
                        documentLabel.textContent = idProof.idProofName;
                        documentBtnGroup.appendChild(documentLabel);

                        var documentCheckMatch = document.createElement("input");
                        documentCheckMatch.id = "idProofCheckbox_" + idProof.idProofId;
                        documentCheckMatch.type = "checkbox";
                        documentCheckMatch.name = "document";
                        documentCheckMatch.onchange = function () {
                            if ($("#idProofCheckbox_" + idProof.idProofId).prop('checked') == true) {
                                console.log("Show");
                                $("#document_response_input_" + idProof.idProofId).css("display", "block");
                            }
                            else {
                                console.log("Hide");
                                $("#document_response_input_" + idProof.idProofId).css("display", "none");
                                $("#Invalid_" + idProof.idProofId).css("display", "none");
                            }

                        };
                        documentLabel.appendChild(documentCheckMatch);

                        var alertInvalid = document.createElement("p");
                        alertInvalid.id = "Invalid_" + idProof.idProofId;
                        alertInvalid.textContent = "(Invalid Number)";
                        alertInvalid.style = "color:#ff1744;display:none;margin:1px 0 1px 42px;font-size:12px";
                        documentResponseRow1.appendChild(alertInvalid);

                        var idProofNumberTd = document.createElement("p");
                        idProofNumberTd.id = "idProofValueTd_" + idProof.idProofId;
                        documentResponseRow2.appendChild(idProofNumberTd);

                        var ip = document.createElement("input");
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
                    if (lId[i] == 1 || lId[i] == 2 || lId[i] == 3 || lId[i] == 4 || lId[i] == 5)
                    {
                        var colLanguageTitle = document.createElement("div");
                        colLanguageTitle.className = "col-xs-12 col-sm-4";
                        colLanguageTitle.style = "padding-top:8px";
                        table.appendChild(colLanguageTitle);

                        colLanguageTitle.innerHTML = "<div class=\"btn-group\" style=\"margin:6px 0px;width:100%\" data-toggle=\"buttons\">" +
                                          "<label class=\"btn btn-custom-check\"style=\"width:100%\" >" +
                                          "<input id=\"lang_"+ lId[i] +"\" type=\"checkbox\" name=\"u\" value=0 >"+ l[i] +
                                          "</label>" +
                                          "</div>";
                    }
                }
            },

            experience: function (jobRoleArray, id) {
                if (jobRoleArray != null && jobRoleArray.length > 0) {

                    if(appz.allJobRole == null ){
                        appz.allJobRole = [];
                        jobRoleArray.forEach(function (jobRole) {
                            var label = jobRole.jobName;
                            var value = parseInt(jobRole.jobRoleId);
                            var item = {};
                            item ["id"] = value;
                            item ["name"] = label;
                            appz.allJobRole.push(item);
                        });
                    }
                    console.log("id: " + id + " allJobRole size: " + applyInShort.allJobRole.length);
                    $("#workedJobRole_"+id).tokenInput(applyInShort.allJobRole, {
                        theme: "facebook",
                        placeholder: "Job Role?",
                        hintText: "Select job role",
                        minChars: 0,
                        tokenLimit: 1,
                        zindex: 9999,
                        preventDuplicates: true
                    });
                    applyInShort.aux.disableCurrentCompanyOption;
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
                    assetsResponseCol.style = "padding-top:8px";
                    selectList.append(assetsResponseCol);


                    var assetsBtnGroup = document.createElement("div");
                    assetsBtnGroup.className = "btn-group";
                    assetsBtnGroup.style = "margin:6px 0px";
                    assetsBtnGroup.setAttribute('data-toggle','buttons');
                    assetsResponseCol.appendChild(assetsBtnGroup);

                    var assetsLabel = document.createElement("label");
                    assetsLabel.className = "btn btn-custom-check";
                    assetsLabel.textContent = asset.assetTitle;
                    assetsBtnGroup.appendChild(assetsLabel);

                    var assetsCheckMatch = document.createElement("input");
                    assetsCheckMatch.id = "assetsCheckboxId_" + asset.assetId;
                    assetsCheckMatch.type = "checkbox";
                    assetsCheckMatch.name = "document";
                    assetsLabel.appendChild(assetsCheckMatch);

                });
            },

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
                        $(".jobApplyBtnV2").show();
                        $("#incentiveSection").hide();
                    } else if (appz.loginStatus == '2') {
                        $(".jobApplyBtnV2").hide();
                        $("#incentiveSection").show();
                    } else {
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
            },

            addmoreCompany: function () {
                console.log(appz.compyCount);
                if (appz.companyCount < 3) {
                    appz.companyCount++;
                    var allworkedCompanyDetailsDiv = document.createElement("div");
                    allworkedCompanyDetailsDiv.className = "row";
                    allworkedCompanyDetailsDiv.id = "row_" + appz.companyCount;
                    allworkedCompanyDetailsDiv.style = "margin:4px 0";

                    var allCompanyNameCol = document.createElement("div");
                    allCompanyNameCol.className = "col-sm-3";
                    allCompanyNameCol.id = "companyName";
                    allworkedCompanyDetailsDiv.appendChild(allCompanyNameCol);

                    var allworkedJobRoleCol = document.createElement("div");
                    allworkedJobRoleCol.className = "col-sm-3";
                    allworkedJobRoleCol.id = "workedJobRole";
                    allworkedCompanyDetailsDiv.appendChild(allworkedJobRoleCol);

                    var allWorkedCurrentltyCol = document.createElement("div");
                    allWorkedCurrentltyCol.className = "col-sm-4";
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
                    addCompanyName.id = ("companyName_" + appz.companyCount);
                    addCompanyName.onchange = applyInShort.validation.enableAddBtn;
                    allCompanyNameCol.appendChild(addCompanyName);

                    var addJobRole = document.createElement("input");
                    addJobRole.id = "workedJobRole_" + appz.companyCount;
                    addJobRole.onchange = applyInShort.validation.enableAddBtn;
                    allworkedJobRoleCol.appendChild(addJobRole);

                    var addCurrentlyWorking = document.createElement("input");
                    if (!$("#currentlyWorking").is(":checked")) {
                        addCurrentlyWorking.disabled = true;
                    } else {
                        addCurrentlyWorking.disabled = false;
                    }
                    addCurrentlyWorking.type = ("radio");
                    addCurrentlyWorking.style = "margin:0 4%";
                    addCurrentlyWorking.id = ("addCurrentlyWorking_" + appz.companyCount);
                    addCurrentlyWorking.name = ("addCurrently_Working");
                    allWorkedCurrentltyCol.appendChild(addCurrentlyWorking);

                    var addMore = document.createElement("button");
                    addMore.className = "form-control";
                    addMore.type = "button";
                    addMore.setAttribute("disabled", true);
                    addMore.id = "addCurrentlyWorkingBtn_"+appz.companyCount;
                    addMore.value = "Add";
                    addMore.style = "background:#09ac58;color:#fff;font-size:12px";
                    addMore.name = "Add";
                    addMore.textContent = "Add Company";
                    addMore.onclick = applyInShort.aux.addmoreCompany;
                    allWorkedAddMoreCol.appendChild(addMore);

                    var addCurrentlyWorkingLabel = document.createElement("label");
                    addCurrentlyWorkingLabel.textContent = ("Is this your current company");
                    addCurrentlyWorkingLabel.for = ("addCurrentlyWorking_" + appz.companyCount);
                    allWorkedCurrentltyCol.appendChild(addCurrentlyWorkingLabel);

                    var previousButton = appz.companyCount - 1;
                    $('#companyDetailsCapture').append(allworkedCompanyDetailsDiv);
                    $("#addCurrentlyWorkingBtn_"+previousButton).prop("disabled", true);

                    appz.process.experience(appz.allJobRole, appz.companyCount);
                }
                else {
                    $.notify("Max 3 Addition Allowed", 'error');
                }

            }
        },
        do: {
            submit: function (d) {
                console.log("data to submit:"+d);

                try {
                    $.ajax({
                        type: "POST",
                        url: "/updateCandidateDetailViaShortJobApply/",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify(d),
                        success: function (returnedData) {

                        console.log(returnedData);
                            $("#finalSubmitBtn").prop("disabled", true);

                            if(returnedData.statusCode == 3) {

                                appz.render.messageModal("Thanks for applying ! Your will receive interview details in an SMS.");

                                appz.do.hideAllInputFields();
                            } else if(returnedData.statusCode == 4) {

                                appz.render.messageModal("Looks like you have already applied to this job. Closing this tab..");
                                appz.do.hideAllInputFields();
                            } else {
                                $.notify("Something went wrong. Please re-check submission!", 'error');

                                setTimeout(function(){
                                    $("#finalSubmitBtn").prop("disabled", false);
                                },3000);

                            }

                        }
                    });
                } catch (exception) {
                    console.log("exception occured!!" + exception);
                }
            },
            interviewIconChange: function () {
                if($("#jobInterviewPanel").hasClass("in")== true){
                    $("#interviewCollapsePanelIcon").removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down");
                }
                else{
                    $("#interviewCollapsePanelIcon").removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up");
                }
            },
            localityIconChange: function () {
                if($("#jobLocalityPanel").hasClass("in")== true){
                    $("#localityCollapsePanelIcon").removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down");
                }
                else{
                    $("#localityCollapsePanelIcon").removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up");
                }
            },
            detailsIconChange: function () {
                if($("#missingInfo").hasClass("in")== true){
                    $("#detailsCollapsePanelIcon").removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down");
                }
                else{
                    $("#detailsCollapsePanelIcon").removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up");
                }
            },
            basicDetailsIconChange: function () {
                if($("#jobBasicDetailsPanel").hasClass("in")== true){
                    $("#jobBasicDetailsPanelIcon").removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down");
                }
                else{
                    $("#jobBasicDetailsPanelIcon").removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up");
                }
            },
            validateSubmit: function () {

                appz.missingData.shortPSPopulateResponse.propertyIdList;

                // c.f https://github.com/jpillora/notifyjs/issues/64
                $('.notifyjs-container').trigger('notify-hide');

                console.log("validating submit");
                var okToSubmitList = [];
                var okToSubmit = true;

                var dobCheck;
                var prevCompanyList = [];
                var main = {};
                var d = {};
                var msg;

                main["candidateId"] = parseInt(appz.candidateId);
                main["jobPostId"] = parseInt(appz.jobPostId);
                main["propertyIdList"] = appz.missingData.shortPSPopulateResponse.propertyIdList;

                var localityId = $('#jobLocality').val();

                if(localityId == null || localityId == 0){
                    okToSubmit = false;
                    msg = "Please select a valid Interview locality.";

                    $.notify(msg, 'error');

                    var submit = {
                        propId : 11,
                        message: msg,
                        submissionStatus: okToSubmit
                    };
                    okToSubmitList.push(submit);
                } else {
                    main["localityId"] = parseInt(localityId);
                }

                var selectedInterview = $('#interViewSlot').val();

                if(appz.isInterviewSlotAvailable) {

                    if(selectedInterview == null || selectedInterview == "0"){
                        okToSubmit = false;
                        msg = "Please select a valid Interview Slot.";
                        $.notify(msg, 'error');

                        var submit = {
                            propId : 12,
                            message: msg,
                            submissionStatus: okToSubmit
                        };
                        okToSubmitList.push(submit);
                    } else {
                        main["dateInMillis"] = parseInt(selectedInterview.split("_")[0]);
                        main["timeSlotId"] = parseInt(selectedInterview.split("_")[1]);
                    }
                }

                console.log(appz.missingData.shortPSPopulateResponse.propertyIdList);
                $.each(appz.missingData.shortPSPopulateResponse.propertyIdList, function (index, propId) {
                    msg = "";
                    okToSubmit = true;
                    if (propId == 0) {
                        var documentList = [];
                        $('#document_details').each(function () {
                            $(this).find('input[type=checkbox]').each(function () {
                                var item = {};
                                var id;
                                id = $(this).attr('id').split("_").slice(-1).pop();

                                var isChecked = $('input#idProofCheckbox_' + id).is(':checked');
                                var isValid = appz.validation.input(id, $('input#idProofValue_' + id).val().trim());
                                if ( isValid && isChecked) {
                                    item["idProofId"] = parseInt(id);
                                    item["idNumber"] = $('input#idProofValue_' + id).val().trim();
                                } else if (isChecked && !isValid) {
                                    // okToSubmit = false;
                                    // $.notify("Please provide valid document details.", 'error');
                                }

                                if (!jQuery.isEmptyObject(item)) {
                                    documentList.push(item);
                                };

                                if(!okToSubmit){
                                    var submit = {
                                        propId : propId,
                                        message: msg,
                                        submissionStatus: okToSubmit
                                    };
                                    okToSubmitList.push(submit);
                                }
                            });
                        });

                        // documents
                        d ["idProofWithIdNumberList"] = documentList;
                        //
                        // if(documentList.length == 0) {
                        //     // won't allow candidate to make submission without provide alteast one doc
                        //     $.notify("Please provide your document details", 'error');
                        //     okToSubmit = false;
                        // }

                    } else if (propId == 1) {
                        var check;
                        var languageMap = [];
                        var languageKnown = $('#language_details input:checked').map(function () {
                            check = 0;
                            var id = this.id.split("_")[1];
                            var name = this.name;
                            var item = {};
                            var pos;

                            for (var i in languageMap) {
                                if (languageMap[i].id == id) {
                                    pos = i;
                                    check = 1;
                                    break;
                                }
                            }
                            if (check == 0) {
                                item["id"] = id;
                                item["u"] = 0;
                                item["rw"] = 0;
                                item["s"] = 0;
                                if (name == "u")
                                    item["u"] = 1;
                                languageMap.push(item);
                            }
                            else {
                                if (name == "u")
                                    languageMap[pos].u = 1;
                            }
                        }).get();

                        d ["candidateKnownLanguageList"] = languageMap;

                        /*if(languageMap.length == 0) {
                         okToSubmit = false;
                         $.notify("Please provide all known languages", 'error');
                         }*/
                        if(!okToSubmit) {
                            d ["candidateKnownLanguageList"] = [];
                            var submit = {
                                propId : propId,
                                message: msg,
                                submissionStatus: okToSubmit
                            };
                            okToSubmitList.push(submit);
                        }
                    } else if (propId == 2) {
                        // asset
                        var assetArrayList = [];
                        $('#assets_details input:checked').each(function () {
                            var id = parseInt($(this).attr('id').split("_").slice(-1).pop());
                            assetArrayList.push(id);
                        });
                        d ["assetIdList"] = assetArrayList;

                    } else if (propId == 3) {
                        var selectedDob =null;
                        var c_dob= null;
                        // age submission
                        if($('#dob_day').val() == "Day" && $('#dob_month').val() == "Month" && $('#dob_year').val() == "Year") {
                            // okToSubmit = false;
                        } else if($('#dob_day').val() == "Day" || $('#dob_month').val() == "Month" || $('#dob_year').val() == "Year"){
                             okToSubmit = false;
                        } else{
                            selectedDob = $('#dob_year').val() + "-" + $('#dob_month').val() + "-" + $('#dob_day').val();
                        }
                        if(selectedDob == null || selectedDob == "") {
                            // okToSubmit = false;
                        } else {
                            c_dob = String(selectedDob);
                            var selectedDate = new Date(c_dob);
                            var toDate = new Date();
                            var pastDate= new Date(toDate.setFullYear(toDate.getFullYear() - 18)); // ex: if current year: 2016 || pastDate: 1998
                            toDate =  new Date(); //reset toDate to current Date
                            var zombieYear = new Date(toDate.setFullYear(toDate.getFullYear() - 70)); // ex: if current year: 2016  || zombieYear: 1928
                            toDate =  new Date(); //reset toDate to current Date
                            if (selectedDate >= pastDate) {
                                dobCheck = 0;
                                okToSubmit = false;
                            }
                            if(selectedDate <= zombieYear ) {
                                dobCheck = 0;
                                okToSubmit = false;
                            }

                        }

                        d ["candidateDob"] = c_dob;

                        msg = "Please provide valid Date of birth";

                        if(!okToSubmit){
                            $.notify(msg, 'error');
                            d ["candidateDob"] = null;
                            var submit = {
                                propId : propId,
                                message: msg,
                                submissionStatus: okToSubmit
                            };
                            okToSubmitList.push(submit);
                        }
                    } else if (propId == 4) {
                        /* calculate total experience in months */
                        if(($('input:radio[name="candidateExperience"]:checked').length == 0)){
                            // okToSubmit = false;
                            // $.notify("Please select Fresher/Experienced.", 'error');
                        }
                        var expMonth = 0;
                        var expYear = 0;
                        var totalExp;

                        if($('input:radio[name="candidateExperience"]:checked').val() == "0") {
                            totalExp = 0;
                        } else {
                            if($('#candidateTotalExperienceMonth').val() == "" &&
                                $('#candidateTotalExperienceYear').val() == "") {
                                totalExp = null;
                            } else {
                                expMonth = $('#candidateTotalExperienceMonth').val();
                                expYear = $('#candidateTotalExperienceYear').val();
                                expMonth = parseInt(expMonth == ""? 0 :expMonth);
                                expYear = parseInt(expYear == ""? 0: expYear);

                                totalExp = expMonth + (12 * expYear);
                            }
                            var isExpEmpty = ($('#candidateTotalExperienceMonth').val() == 0) && ($('#candidateTotalExperienceYear').val() == 0);
                            if ($('input[id=candidateExp]').is(":checked") && isExpEmpty) {
                                msg = "Please provide your total years of experience";
                                $.notify(msg, 'error');
                                okToSubmit = false;
                            }
                        }

                        // are you currently working
                        if ($('input[id=candidateExp]').is(":checked") && $('#currentlyWorking').is(":checked")
                            && !$('input[name=addCurrently_Working]').is(":checked")) {
                            msg = "Please provide your current company details and mark appropriately.";
                            $.notify(msg, 'error');
                            okToSubmit = false;
                        }

                        var i = 1;
                        prevCompanyList = [];
                        for (i; i<=3; i++) {
                            var item = {};
                            if($('#companyName_'+i).val() == "" && $('#workedJobRole_'+i).val() > 0) {
                                msg = "please provide company name";
                                $.notify("please provide company name", 'error');
                                okToSubmit = false;
                            }
                            if($('#companyName_'+i).val() != "" && $('#workedJobRole_'+i).val() < 0) {
                                msg += " | please provide Job Role";

                                $.notify("please provide Job Role", 'error');
                                okToSubmit = false;
                            }
                            if($('#companyName_'+i).val() != "" && $('#workedJobRole_'+i).val() > 0) {
                                item["companyName"] = $('#companyName_'+i).val();
                                item["jobRoleId"] = $('#workedJobRole_'+i).val();
                                item["current"] = $('#addCurrentlyWorking_'+i).is(":checked");
                            }
                            if (!jQuery.isEmptyObject(item)) {
                                prevCompanyList.push(item);
                            }
                        }

                        d ["candidateTotalExperience"] = totalExp;
                        d ["pastCompanyList"] = prevCompanyList;
                        d ["candidateIsEmployed"] = $('#currentlyWorking').is(":checked");
                        d ["extraDetailAvailable"] = true;

                        if(!okToSubmit){

                            d ["candidateTotalExperience"] = null;
                            d ["pastCompanyList"] = null;
                            d ["candidateIsEmployed"] = null;
                            d ["extraDetailAvailable"] = null;

                            var submit = {
                                propId : propId,
                                message: msg,
                                submissionStatus: okToSubmit
                            };
                            okToSubmitList.push(submit);
                        }

                    } else if (propId == 5) {
                        d ["candidateEducationLevel"] = $('#candidateHighestEducation').val();
                        d ["candidateDegree"] = ($('#candidateHighestDegree').val());
                        d ["candidateEducationInstitute"] = $('#candidateEducationInstitute').val();
                        d ["candidateEducationCompletionStatus"] = parseInt($('input:radio[name="candidateEducationCompletionStatus"]:checked').val());

                        if($('#candidateHighestEducation').val() == "-1" || $('#candidateHighestEducation').val() > 3 ||
                            $('input:radio[name="candidateEducationCompletionStatus"]:checked').val() == null){
                            if(($('#candidateHighestDegree').val()) == "-1" ||
                                $('#candidateEducationInstitute').val() == "") {
                                // okToSubmit = false;
                                // $.notify("Please provide full education details", 'error');
                                d ["candidateEducationLevel"] = null;
                                d ["candidateDegree"] = null;
                                d ["candidateEducationInstitute"] = null;
                                d ["candidateEducationCompletionStatus"] = null;


                            }
                        }

                        if(!okToSubmit){
                            var submit = {
                                propId : propId,
                                message: msg,
                                submissionStatus: okToSubmit
                            };
                            okToSubmitList.push(submit);
                        }
                    } else if (propId == 6) {
                        d ["candidateGender"] = ($('input:radio[name="gender"]:checked').val());
                        if(($('input:radio[name="gender"]:checked').length == 0)) {
                            // okToSubmit = false;
                            // $.notify("Please provide your gender details", 'error');
                        }
                        if(!okToSubmit){
                            d ["candidateGender"] = null;
                            var submit = {
                                propId : propId,
                                message: msg,
                                submissionStatus: okToSubmit
                            };

                            okToSubmitList.push(submit);
                        }
                    } else if (propId == 7) {
                        var salary = $('#candidateLastWithdrawnSalary').val();
                        if (!isNaN(salary) && parseInt(salary) >= 1000 && parseInt(salary) <= 100000) {
                            d ["candidateLastWithdrawnSalary"] = parseInt($('#candidateLastWithdrawnSalary').val());
                        } else {
                            // okToSubmit = false;
                            // $.notify("Please enter a valid 'Last Withdrawn Salary' per month. (Min: 1000, Max: 1,00,000)", 'error');
                            if(!okToSubmit){
                                d ["candidateLastWithdrawnSalary"] = null;
                                var submit = {
                                    propId : propId,
                                    message: "Please enter a valid 'Last Withdrawn Salary' per month. (Min: 1000, Max: 1,00,000)",
                                    submissionStatus: okToSubmit
                                };
                                okToSubmitList.push(submit);
                            }
                        }
                    } else if (propId == 8) {
                        var lId = $('#candidateHomeLocality').val();
                        if(lId == null) {
                            // okToSubmit = false;
                            d ["candidateHomeLocality"] = null;
                            // $.notify("Please enter a valid Locality", 'error');
                        } else{
                            d ["candidateHomeLocality"] = parseInt(lId);
                        }
                        if(!okToSubmit){
                            var submit = {
                                propId : propId,
                                message: "Please enter a valid Home locality",
                                submissionStatus: okToSubmit
                            };
                            okToSubmitList.push(submit);
                        }
                    } else if (propId == 9) {
                        var timeShiftPrefId = $('#candidateTimeShiftPref').val();
                        if(timeShiftPrefId == "-1") {
                            d ["candidateTimeShiftPref"] = null;
                            // okToSubmit = false;
                            // $.notify("Please enter a valid time/shift preference (ex: Part time, Full time)", 'error');
                        } else {
                            d ["candidateTimeShiftPref"] = $('#candidateTimeShiftPref').val();
                        }
                        if(!okToSubmit){
                            var submit = {
                                propId : propId,
                                message: "Please enter a valid time preference",
                                submissionStatus: okToSubmit
                            };
                            okToSubmitList.push(submit);
                        }
                    }
                });

                console.log("OkToSubmit List: " + JSON.stringify(okToSubmitList));
                if (okToSubmitList.length == 0) {
                    main["updateCandidateDetail"] = d;
                    this.submit(main);

                    $("#finalSubmitBtn").prop("disabled", true);

                    // disable apply btn when its okay to submit (i.e submission is validated)..
                    setTimeout(function(){
                        $("#finalSubmitBtn").prop("disabled", false);
                    },7000);

                    return true;
                } else {
                    // okToSubmitList.forEach(function (object) {
                    //     $.notify(object.message, 'error');
                    //     console.log(object);
                    // })
                }

            },
            hideAllInputFields: function () {
                $("#finalSubmitBtn").prop("disabled", true);
                appz.render.hideContainer();
            }
        }

    };

    appz.method.init();

    appz.method.ending();

    // search click listener
    document.getElementById("finalSubmitBtn").addEventListener("click", function () {
        appz.do.validateSubmit();
    });
    //
    document.getElementById("jobInterviewHead").addEventListener("click",function () {
        appz.do.interviewIconChange();
    });
    document.getElementById("jobLocalityHead").addEventListener("click",function () {
        appz.do.localityIconChange();
    });

    document.getElementById("jobdetailsHead").addEventListener("click",function () {
        appz.do.detailsIconChange();
    });

    document.getElementById("jobBasicDetailsHead").addEventListener("click",function () {
        appz.do.basicDetailsIconChange();
    });

    return appz;

}(jQuery));

// $(window).load(function() {
//     $('html, body').css({
//         'overflow': 'auto',
//         'height': 'auto'
//     });
//     $("#status").fadeOut();
//     $("#loaderLogo").fadeOut();
//     $("#preloader").delay(300).fadeOut("slow");
// });