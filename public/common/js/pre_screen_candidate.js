/**
 * Created by hawk on 14/11/16.
 */
// aux methods start
var langArray = [];
var currentLocationArray = [];
var localityArray = [];
function processTimeShift(returnedData) {
    if (returnedData != null) {
        var data = [];

        returnedData.forEach(function (timeshift) {
            var opt = {
                label: timeshift.timeShiftName, value: parseInt(timeshift.timeShiftId)
            };
            data.push(opt);
        });

        var selectList = $('#candidateTimeShiftPref');
        selectList.multiselect({
            nonSelectedText: 'None Selected',
            includeSelectAllOption: true,
            maxHeight: 300
        });
        selectList.multiselect('dataprovider', data);
        selectList.multiselect('rebuild');
    }
}

function processEducation(returnedEdu) {
    if (returnedEdu != null) {
        var data = [];

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
}

function processDegree(returnedDegree) {
    if (returnedDegree != null) {
        var data = [];

        returnedDegree.forEach(function (degree) {
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
}

function processLanguage(returnedData,idLanguageId) {
    var arrayLang = [];
    var arrayLangId = [];

    returnedData.forEach(function (language) {
        for(var i=0;i<=idLanguageId.length;i++) {
            if(idLanguageId[i]==language.languageId) {
                var id = language.languageId;
                var name = language.languageName;
                var item = {};
                item ["id"] = id;
                item ["name"] = name;
                arrayLang.push(name);
                arrayLangId.push(id);
                var option = $('<option value=' + id + '></option>').text(name);
                langArray.push(item);
                }
            }
            });
    populateLanguages(arrayLang.reverse(), arrayLangId.reverse());
        }

function populateLanguages(l, lId) {
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
            cell2.innerHTML = "<div class=\"btn-group\" style=\"margin:4px 10px\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"u\" value=0 >Understand</label></div>";
            cell3.innerHTML = "<div class=\"btn-group\" style=\"margin:4px 10px\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"s\" value=0 >Speak</label></div>";
            cell4.innerHTML = "<div class=\"btn-group\" style=\"margin:4px 10px\"  data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"rw\" value=0 >Read/Write</label></div>";
        }
    }
}

function processDataGetAssets(returnedAssets) {
    var selectList = $('#assets_details');

    returnedAssets.forEach(function (asset) {

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
        checkMatch.id = "idProofCheckbox_" + asset.assetId;

        var assetsTitle = document.createElement("font");
        assetsTitle.textContent = asset.assetTitle;
        assetsTitle.style = "margin:8% 4%";
        assetsResponseCol.appendChild(assetsTitle);

        checkMatchLabel.appendChild(checkMatch);
    });
}

function processIdProofsWithNumbers(returnedData, idProofId) {
            if (returnedData != null) {
                // minReqTable
                var responseInput = $('#document_details');

                returnedData.forEach(function (idProof) {
                    for( var i=0;i<=idProofId.length;i++) {
                        if (idProofId[i] == idProof.idProofId) {
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
                            documentResponseRow2.id = "document_response_input_"+ idProof.idProofId;
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
                                if($("#idProofCheckbox_" + idProof.idProofId).prop('checked') == true){
                                    $("#document_response_input_"+idProof.idProofId).css("display","block");
                                }
                                else{
                                    $("#document_response_input_"+idProof.idProofId).css("display","none");
                                    $("#Invalid_"+idProof.idProofId).css("display","none");
                                }

                            };
                            checkMatchLabel.appendChild(checkMatch);

                            var idProofTitleTd = document.createElement("font");
                            idProofTitleTd.textContent = idProof.idProofName;
                            idProofTitleTd.style = "margin:8% 4%";
                            documentResponseRow1.appendChild(idProofTitleTd);

                            var alertInvalid = document.createElement("p");
                            alertInvalid.id = "Invalid_"+idProof.idProofId;
                            alertInvalid.textContent="(Invalid Number)";
                            alertInvalid.style="color:#ff1744;display:none;margin:1px 0 1px 42px;font-size:12px";
                            documentResponseRow1.appendChild(alertInvalid);

                            var idProofNumberTd = document.createElement("p");
                            idProofNumberTd.id = "idProofValueTd_"+idProof.idProofId;
                            documentResponseRow2.appendChild(idProofNumberTd);

                            var ip = document.createElement("INPUT");
                            ip.className = "form-control";
                            ip.setAttribute("type", "text");
                            ip.onchange = validateInput;
                            ip.placeholder= idProof.idProofName +" Number";
                            ip.setAttribute("id", "idProofValue_" + idProof.idProofId);
                            idProofNumberTd.appendChild(ip);
                        }
                    }
                });
            }
}
function validateInput() {
    console.log(this.id);
    var id = this.id.split("_")[1];
    // aadhaar validation
    if(id == 3) {
        console.log(this.value);
        if(!validateAadhar(this.value)){
            $("#Invalid_"+id).css("display","block");
        } else {
            $("#Invalid_"+id).css("display","none");
        }
    }
    if(id == 1){
        console.log(this.value);
        if(!validateDL(this.value)){
            $("#Invalid_"+id).css("display","block");
        } else {
            $("#Invalid_"+id).css("display","none");
        }
    }
    if(id == 2){
        console.log(this.value);
        if(!validatePASSPORT(this.value)){
            $("#Invalid_"+id).css("display","block");
        } else {
            $("#Invalid_"+id).css("display","none");
        }
    }
    if(id == 4){
        console.log(this.value);
        if(!validatePAN(this.value)){
            $("#Invalid_"+id).css("display","block");
        } else {
            $("#Invalid_"+id).css("display","none");
        }
    }
    console.log(this.parentNode);
}
function processAllJobRole(returnedData) {
    if (returnedData != null) {
        var data = [];
    for(var i=0;i<=3;i++){
        returnedData.forEach(function (jobrole) {
            var opt = {
                label: jobrole.jobName, value: parseInt(jobrole.jobRoleId)
            };
            data.push(opt);
        });

        var selectList = $('#workedJobRole_'+[i]);
        selectList.multiselect({
            nonSelectedText: 'None Selected',
            includeSelectAllOption: true,
            maxHeight: 300
        });
        selectList.multiselect('dataprovider', data);
        selectList.multiselect('rebuild');
        }
    }
}
function processLocality(returnedData) {
    console.log("fetched all locality. now rendering locality token input");

    var locArray = [];
    if (returnedData != null) {
        returnedData.forEach(function (locality) {
            var id = locality.localityId;
            var name = locality.localityName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            localityArray.push(item);
            locArray.push(item);
        });

        if (localityArray != null) {
            console.log("localityArray: " + localityArray.length);
            console.log("localityArray: " + locArray.length);
            $("#candidateHomeLocality").tokenInput(getLocalityArray(), {
                theme: "facebook",
                placeholder: "job Localities?",
                hintText: "Start typing Area (eg. BTM Layout, Bellandur..)",
                minChars: 0,
                tokenLimit: 1,
                zindex: 9999,
                preventDuplicates: true
            });
        }
    }
}

function getLocalityArray() {
    return localityArray;
}
// aux methods end
var modalOpenAttempt = 0;
function openPreScreenModal(jobPostId, candidateMobile) {
    if (candidateMobile != null) {
        var base_api_url = "/support/api/getJobPostVsCandidate/";
        candidateMobile = candidateMobile.substring(3);
        if (base_api_url == null || jobPostId == null) {
            console.log("please provide candidateId && jobPostId");
            return
        } else {
            base_api_url += "?";
            if (jobPostId != null) {
                base_api_url += "jobPostId=" + jobPostId;
            }
            if (candidateMobile != null) {
                base_api_url += "&candidateMobile=" + candidateMobile;
            }
        }
        base_api_url += "&rePreScreen=" + true;
        if (modalOpenAttempt == 1) {
            $("#preScreenModal").modal();
        }
        if (modalOpenAttempt == 0) {
            modalOpenAttempt = 1;
            try {
                $.ajax({
                    type: "GET",
                    url: base_api_url,
                    data: false,
                    async: false,
                    contentType: false,
                    processData: false,
                    success: processPreScreenData
                });
                $("#preScreenModal").modal();
            } catch (exception) {
                console.log("exception occured!!" + exception.stack);
            }
        }
    }
    $("#myLoginModal").modal();
}
function processPreScreenData(returnedData) {
    if (returnedData == null || returnedData.status != "SUCCESS") {
        if (returnedData != null && returnedData.status == "INVALID") {
            alert("Already Pre Screened");
        } else {
            alert("Request failed. Something went Wrong! Please Refresh");
        }
        return;
    }
    var parent = $('#missingInfo');
    var mainDiv = document.createElement("div");
    mainDiv.className = "row";
    parent.append(mainDiv);
    var subDivOne = document.createElement("div");
    subDivOne.className = "col-sm-12";
    mainDiv.appendChild(subDivOne);
    var hintMessage = document.createElement("p");
    hintMessage.textContent = "Please provide your following details to apply for this job";
    hintMessage.style="margin:0";
    subDivOne.appendChild(hintMessage);
    var subDivTwo = document.createElement("div");
    subDivTwo.style = "padding:0 4%";
    subDivTwo.className = "col-sm-12";
    mainDiv.appendChild(subDivTwo);
    var orderList = document.createElement("ol");
    orderList.className = "list-group";
    subDivTwo.appendChild(orderList);
    if (returnedData != null) {
        var elementList = returnedData.elementList;
        elementList.forEach(function (rowData) {
            var ajax_type = "POST";
            var url;
            var fn;
            if (rowData != null) {
                if (rowData.propertyId == 0 && rowData.isMatching == false) {
                    var idProofId = [];
                    var jobPostElementList = rowData.jobPostElementList;
                    jobPostElementList.forEach(function (documentData) {
                            idProofId.push(documentData.object.idProofId);
                    });
                    var firstproperty = document.createElement("li");

                    var rowBoxHeading = document.createElement("div");
                    rowBoxHeading.className = "row";
                    firstproperty.appendChild(rowBoxHeading);

                    var heading = document.createElement("font");
                    heading.textContent = "Do you have the following document(s) ?";
                    heading.id = "property_" + rowData.propertyId;
                    rowBoxHeading.appendChild(heading);

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var documentDetails = document.createElement("div");
                    documentDetails.className = "col-sm-12 col-md-12";
                    documentDetails.id = "document_details";

                    rowBox.appendChild(documentDetails);
                    firstproperty.appendChild(rowBox);

                    url = "/support/api/getDocumentReqForJobRole/?job_post_id=" + returnedData.jobPostId;
                    ajax_type = "GET";
                    fn = function (returnedData) {
                        processIdProofsWithNumbers(returnedData, idProofId);
                        url = "";
                    };
                    orderList.appendChild(firstproperty);
                }
                else if (rowData.propertyId == 1 && rowData.isMatching == false) {
                    var idLanguageId = [] ;
                    var jobPostElementList = rowData.jobPostElementList;
                    jobPostElementList.forEach(function (languageData) {
                        idLanguageId.push(languageData.object.languageId);
                    });

                    var secondProperty = document.createElement("li");
                    secondProperty.textContent = "Do you know following language(s) ?";
                    secondProperty.id = "property_" + rowData.propertyId;

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var languageDetails = document.createElement("div");
                    languageDetails.className = "col-sm-12";
                    languageDetails.id = "language_details";
                    languageDetails.style = "padding:2%;text-align:center";


                    rowBox.appendChild(languageDetails);
                    secondProperty.appendChild(rowBox);
                    url = "/getAllLanguage";
                    fn = function (returnedData) {
                        processLanguage(returnedData,idLanguageId);
                        url = "";
                    };
                    orderList.appendChild(secondProperty);
                }
                else if (rowData.propertyId == 2 && rowData.isMatching == false) {
                    var firstproperty = document.createElement("li");
                    firstproperty.textContent = "Do you own any of the following ?";
                    firstproperty.id = "property_" + rowData.propertyId;

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var assetsDetails = document.createElement("div");
                    assetsDetails.className = "col-md-12";
                    assetsDetails.id = "assets_details";
                    assetsDetails.style = "padding:2%";

                    ajax_type = "GET";
                    url = "/support/api/getAssetReqForJobRole/?job_post_id=" + returnedData.jobPostId;
                    fn = function (returnedData) {
                        processDataGetAssets(returnedData);
                        url = "";
                    };

                    rowBox.appendChild(assetsDetails);
                    firstproperty.appendChild(rowBox);
                    orderList.appendChild(firstproperty);
                }
                else if (rowData.propertyId == 3 && rowData.isMatching == false) {
                    var thirdproperty = document.createElement("li");
                    thirdproperty.textContent = "Please mention your Date of Birth";
                    thirdproperty.id = "property_" + rowData.propertyId;
                    orderList.appendChild(thirdproperty);

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var ageTitle = document.createElement("div");
                    ageTitle.className = "col-xs-4 col-sm-4 col-sm-offset-2";
                    ageTitle.style = "padding:2%;text-align:left";
                    rowBox.appendChild(ageTitle);

                    var ageResponse = document.createElement("div");
                    ageResponse.className = "col-xs-8 col-sm-4";
                    ageResponse.style = "padding:1%";
                    rowBox.appendChild(ageResponse);

                    var ageText = document.createElement("font");
                    ageText.textContent = ("Date Of Birth");
                    ageText.style = "font-weight:bold";
                    ageTitle.appendChild(ageText);

                    var textAge = document.createElement("input");
                    textAge.className = "form-control";
                    textAge.id="candidateDob";
                    textAge.type = ("date");
                    textAge.setAttribute("data-date-inline-picker","true");
                    textAge.placeholder = ("When is your Birthday?");
                    ageResponse.appendChild(textAge);

                    thirdproperty.appendChild(rowBox);
                    orderList.appendChild(thirdproperty);
                    $( "#candidateDob").datepicker({ dateFormat: 'yy-mm-dd', changeYear: true});

                }
                else if (rowData.propertyId == 4 && rowData.isMatching == false) {
                    var fourthproperty = document.createElement("li");
                    fourthproperty.textContent = "Do you have work experience?";
                    fourthproperty.id = "property_" + rowData.propertyId;

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var rowBoxDetails = document.createElement("div");
                    rowBoxDetails.className = "row";
                    rowBoxDetails.id = "companyDetailsCapture";

                    var currentlyWorking = document.createElement("p");
                    currentlyWorking.textContent = ("Are you currently working : ");
                    rowBoxDetails.appendChild(currentlyWorking);

                    var checkboxCurrentlyWorking = document.createElement("input");
                    checkboxCurrentlyWorking.id=("currentlyWorking");
                    checkboxCurrentlyWorking.type = ("checkbox");
                    checkboxCurrentlyWorking.style = "margin:0 8%";
                    checkboxCurrentlyWorking.onchange = disableCurrentCompanyOption;
                    currentlyWorking.appendChild(checkboxCurrentlyWorking);

                    var allworkedCompanyDetails = document.createElement("p");
                    allworkedCompanyDetails.textContent = ("Where all have you worked before? ");
                    rowBoxDetails.appendChild(allworkedCompanyDetails);

                        var allworkedCompanyDetailsDiv = document.createElement("div");
                        allworkedCompanyDetailsDiv.className = "row";
                        allworkedCompanyDetailsDiv.style= "margin:4px 0";
                        rowBoxDetails.appendChild(allworkedCompanyDetailsDiv);

                        var allWorkedAddMoreCol = document.createElement("div");
                        allWorkedAddMoreCol.className = "col-sm-2";
                        allworkedCompanyDetailsDiv.appendChild(allWorkedAddMoreCol);

                        var allCompanyNameCol = document.createElement("div");
                        allCompanyNameCol.className = "col-sm-4";
                        allworkedCompanyDetailsDiv.appendChild(allCompanyNameCol);

                        var allworkedJobRoleCol = document.createElement("div");
                        allworkedJobRoleCol.className = "col-sm-3";
                        allworkedCompanyDetailsDiv.appendChild(allworkedJobRoleCol);

                        var allWorkedCurrentltyCol = document.createElement("div");
                        allWorkedCurrentltyCol.className = "col-sm-3";
                        allWorkedCurrentltyCol.style ="padding-top:1%;text-align:center";
                        allworkedCompanyDetailsDiv.appendChild(allWorkedCurrentltyCol);

                        var addMore = document.createElement("button");
                        addMore.className="form-control";
                        addMore.type="button";
                        addMore.value = "Add";
                        addMore.name = "Add";
                        addMore.textContent="Add";
                        addMore.onclick = addmoreCompany;
                        allWorkedAddMoreCol.appendChild(addMore);

                        var addCompanyName = document.createElement("input");
                        addCompanyName.className = "form-control";
                        addCompanyName.type = ("text");
                        addCompanyName.placeholder = ("Company Name");
                        addCompanyName.id = ("companyName");
                        allCompanyNameCol.appendChild(addCompanyName);

                        var addJobRole = document.createElement("select");
                        addJobRole.id = "workedJobRole_0";
                        allworkedJobRoleCol.appendChild(addJobRole);

                        var addCurrentlyWorking = document.createElement("input");
                        addCurrentlyWorking.type = ("radio");
                        addCurrentlyWorking.style = "margin:0 4%";
                        addCurrentlyWorking.id = ("addCurrentlyWorking");
                        addCurrentlyWorking.name = ("addCurrently_Working");
                        addCurrentlyWorking.setAttribute("disabled",true);
                        addCurrentlyWorking.value = (0);
                        allWorkedCurrentltyCol.appendChild(addCurrentlyWorking);

                        var addCurrentlyWorkingLabel = document.createElement("label");
                        addCurrentlyWorkingLabel.textContent = ("Current Company");
                        addCurrentlyWorkingLabel.for = ("addCurrentlyWorking");
                        allWorkedCurrentltyCol.appendChild(addCurrentlyWorkingLabel);

                        url='/getAllJobs ';
                        fn = function(returnedData){
                            processAllJobRole(returnedData);
                            url="";
                        }

                    var experienceOption = document.createElement("div");
                    experienceOption.className = "col-xs-12 col-md-6";
                    experienceOption.style = "padding:2%;text-align:center";
                    rowBox.appendChild(experienceOption);

                    var experienceDuration = document.createElement("div");
                    experienceDuration.className = "col-xs-12 col-md-6";
                    experienceDuration.id ="experienceDuration";
                    experienceDuration.style="display:none;padding:1%";
                    rowBox.appendChild(experienceDuration);

                    var colDetailsFresher = document.createElement("div");
                    colDetailsFresher.className= "col-xs-12 col-sm-6";
                    experienceOption.appendChild(colDetailsFresher);

                    var colDetailsExperience = document.createElement("div");
                    colDetailsExperience.className= "col-xs-12 col-sm-6";
                    experienceOption.appendChild(colDetailsExperience);

                    var radioFresher = document.createElement("input");
                    radioFresher.type = ("radio");
                    radioFresher.style = "margin:0 8%";
                    radioFresher.id = ("candidateFresh");
                    radioFresher.name = ("candidateExperience");
                    radioFresher.value = (0);
                    radioFresher.onchange = hideExperienceCaptureDiv;
                    colDetailsFresher.appendChild(radioFresher);

                    var labelFresher = document.createElement("label");
                    labelFresher.textContent = ("Fresher");
                    labelFresher.for = ("candidateFresh");
                    colDetailsFresher.appendChild(labelFresher);

                    var radioExperience = document.createElement("input");
                    radioExperience.type = ("radio");
                    radioExperience.style = "margin:0 8%";
                    radioExperience.id = ("candidateExper");
                    radioExperience.name = ("candidateExperience");
                    radioExperience.value = (1);
                    radioExperience.onchange = showExperienceCaptureDiv;
                    colDetailsExperience.appendChild(radioExperience);

                    var labelExperience = document.createElement("label");
                    labelExperience.textContent = ("Experience");
                    labelExperience.for = ("candidateExper");
                    colDetailsExperience.appendChild(labelExperience);

                    var titleExpYear = document.createElement("font");
                    titleExpYear.textContent = ("Years");
                    titleExpYear.style = "font-weight:bold";
                    experienceDuration.appendChild(titleExpYear);

                    var textYear = document.createElement("input");
                    textYear.className="form-control";
                    textYear.type = ("number");
                    textYear.placeholder = ("Years");
                    textYear.onchange = showExperienceBox;
                    textYear.id = ("candidateTotalExperienceYear");
                    experienceDuration.appendChild(textYear);

                    var titleExpMonths = document.createElement("font");
                    titleExpMonths.textContent = ("Months");
                    titleExpMonths.style = "font-weight:bold";
                    experienceDuration.appendChild(titleExpMonths);

                    var textMonths = document.createElement("input");
                    textMonths.className="form-control";
                    textMonths.type = ("number");
                    textMonths.placeholder = ("Months");
                    textMonths.id = ("candidateTotalExperienceMonth");
                    experienceDuration.appendChild(textMonths);

                    fourthproperty.appendChild(rowBox);
                    fourthproperty.appendChild(rowBoxDetails);
                    orderList.appendChild(fourthproperty);
                }
                else if (rowData.propertyId == 5 && rowData.isMatching == false) {
                    var fifthproperty = document.createElement("li");
                    fifthproperty.textContent = "Please provide your " + rowData.propertyTitle + " details";
                    fifthproperty.id = "property_" + rowData.propertyId;

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
                    educationDetailsHEQ.className = "col-xs-12 col-sm-4 col-sm-offset-2";
                    educationDetailsHEQ.id = "education_details";
                    educationDetailsHEQ.style = "padding:2%;text-align:left";
                    rowBoxHEQ.appendChild(educationDetailsHEQ);

                    var educationResponseHEQ = document.createElement("div");
                    educationResponseHEQ.className = "col-xs-12 col-sm-4";
                    educationResponseHEQ.id = "education_details";
                    educationResponseHEQ.style = "padding:1%";
                    rowBoxHEQ.appendChild(educationResponseHEQ);

                    var educationTitleHEQ = document.createElement("font");
                    educationTitleHEQ.textContent = ("Highest Education Qualification? ");
                    educationTitleHEQ.style = "font-weight:bold;";
                    educationDetailsHEQ.appendChild(educationTitleHEQ);

                    var educationOptionHEQ = document.createElement("select");
                    educationOptionHEQ.id = "candidateHighestEducation";
                    educationResponseHEQ.appendChild(educationOptionHEQ);

                    //Highest Education Successfully Complete

                    var educationDetailsSuccess = document.createElement("div");
                    educationDetailsSuccess.className = "col-xs-12 col-sm-4 col-sm-offset-2";
                    educationDetailsSuccess.id = "education_details";
                    educationDetailsSuccess.style = "padding:2%;text-align:left";
                    rowBoxSuccess.appendChild(educationDetailsSuccess);

                    var educationResponseSuccess = document.createElement("div");
                    educationResponseSuccess.className = "col-xs-12 col-sm-4";
                    educationResponseSuccess.id = "education_details";
                    educationResponseSuccess.style = "padding-top:3%";
                    rowBoxSuccess.appendChild(educationResponseSuccess);

                    var educationTitleSuccess = document.createElement("font");
                    educationTitleSuccess.textContent = ("Have you successfully completed this course? ");
                    educationTitleSuccess.style = "font-weight:bold;";
                    educationDetailsSuccess.appendChild(educationTitleSuccess);

                    var successYes = document.createElement("input");
                    successYes.type = ("radio");
                    successYes.style = "margin:0 4% 0 8%";
                    successYes.id = ("successYes");
                    successYes.name = ("successConformationHEQ");
                    successYes.value = (0);
                    educationResponseSuccess.appendChild(successYes);

                    var  labelYesSuccess = document.createElement("label");
                    labelYesSuccess.textContent = ("Yes");
                    labelYesSuccess.for = ("successConformationHEQ");
                    educationResponseSuccess.appendChild(labelYesSuccess);

                    var successNo = document.createElement("input");
                    successNo.type = ("radio");
                    successNo.style = "margin:0 4% 0 8%";
                    successNo.id = ("successNo");
                    successNo.name = ("successConformationHEQ");
                    successNo.value = (0);
                    educationResponseSuccess.appendChild(successNo);

                    var  labelNoSuccess = document.createElement("label");
                    labelNoSuccess.textContent = ("No");
                    labelNoSuccess.for = ("successConformationHEQ");
                    educationResponseSuccess.appendChild(labelNoSuccess);

                    //Highest Education Degree

                    var educationDetailsHED = document.createElement("div");
                    educationDetailsHED.className = "col-xs-12 col-sm-4 col-sm-offset-2";
                    educationDetailsHED.id = "education_details";
                    educationDetailsHED.style = "padding:2%;text-align:left";
                    rowBoxHED.appendChild(educationDetailsHED);

                    var educationResponseHED = document.createElement("div");
                    educationResponseHED.className = "col-xs-12 col-sm-4";
                    educationResponseHED .id = "education_details";
                    educationResponseHED .style = "padding:1%";
                    rowBoxHED.appendChild(educationResponseHED);

                    var educationTitleHED = document.createElement("font");
                    educationTitleHED.textContent = ("Highest Education Degree? ");
                    educationTitleHED.style = "font-weight:bold;";
                    educationDetailsHED.appendChild(educationTitleHED);

                    var educationOptionHED = document.createElement("select");
                    educationOptionHED.id = "candidateHighestDegree";
                    educationResponseHED.appendChild(educationOptionHED);

                    //Last Attended Institute

                    var educationDetailsInstitute = document.createElement("div");
                    educationDetailsInstitute.className = "col-xs-12 col-sm-4 col-sm-offset-2";
                    educationDetailsInstitute.style = "padding:2%;text-align:left";
                    rowBoxLastInstitute.appendChild(educationDetailsInstitute);
                    var educationResponseInstitute = document.createElement("div");

                    educationResponseInstitute.className = "col-xs-12 col-sm-4";
                    educationResponseInstitute.style = "padding:1%";
                    rowBoxLastInstitute.appendChild(educationResponseInstitute);

                    var educationtitleInstitute = document.createElement("font");
                    educationtitleInstitute.textContent = ("Last attended Education Institute?");
                    educationtitleInstitute.style = "margin-top:8px;font-weight:bold";
                    educationDetailsInstitute.appendChild(educationtitleInstitute);

                    var educationtextInstitute = document.createElement("input");
                    educationtextInstitute.className = "form-control";
                    educationtextInstitute.type = ("number");
                    educationtextInstitute.placeholder = ("School/Institute Name");
                    educationtextInstitute.id = ("candidateEducationInstitute");
                    educationResponseInstitute.appendChild(educationtextInstitute);

                    url = "/getAllEducation";
                    fn = function (returnedData) {
                        processEducation(returnedData);
                        url = "";
                    };
                    orderList.appendChild(fifthproperty);
                }
                else if (rowData.propertyId == 6 && rowData.isMatching == false) {
                    var sixthproperty = document.createElement("li");
                    sixthproperty.textContent = "Please mention your gender ";
                    sixthproperty.id = "property_" + rowData.propertyId;

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var genderDetails = document.createElement("div");
                    genderDetails.className = "col-sm-12 col-md-12";
                    genderDetails.id = "gender_details";
                    genderDetails.style = "padding:2%;text-align:center";

                    var colGenderDetailsMale = document.createElement("div");
                    colGenderDetailsMale.className= "col-xs-12 col-sm-3";
                    genderDetails.appendChild(colGenderDetailsMale);

                    var colGenderDetailsFemale = document.createElement("div");
                    colGenderDetailsFemale.className= "col-xs-12 col-sm-3";
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
                    orderList.appendChild(sixthproperty);
                }
                else if (rowData.propertyId == 7 && rowData.isMatching == false) {
                    var seventhproperty = document.createElement("li");
                    seventhproperty.textContent = "Please provide your "+rowData.propertyTitle+" details";
                    seventhproperty.id = "property_" + rowData.propertyId;

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var salaryDetails = document.createElement("div");
                    salaryDetails.className = "col-xs-12 col-sm-5 col-sm-offset-1";
                    salaryDetails.id = "salary_details";

                    var salaryResponse = document.createElement("div");
                    salaryResponse.className = "col-xs-12 col-sm-4";
                    salaryResponse.id = "salary_response";
                    salaryResponse.style = "padding:1%";

                    var titleSalary = document.createElement("font");
                    titleSalary.textContent = ("Current/Last Drawn Salary");
                    titleSalary.style = "margin-top:8px;font-weight:bold";
                    salaryDetails.appendChild(titleSalary);

                    var textSalary = document.createElement("input");
                    textSalary.className = "form-control";
                    textSalary.type = ("number");
                    textSalary.placeholder = ("Salary");
                    textSalary.id = ("candidateSalary");
                    textSalary.onchange = invalidSalary;
                    salaryResponse.appendChild(textSalary);

                    var salaryInvalid = document.createElement("p");
                    salaryInvalid.id = "invalidSalaryNotification";
                    salaryInvalid.textContent="Invalid Salary Input Please enter a valid 'Last Withdrawn Salary' (Ex: 15000) in a month";
                    salaryInvalid.style="color:#ff1744;display:none;margin:1px 0 1px 2px;font-size:12px";
                    salaryResponse.appendChild(salaryInvalid);

                    var inhandText = document.createElement("span");
                    inhandText.className ="label label-success";
                    inhandText.textContent = ("(InHand/Month)");
                    inhandText.style = "margin:8px 16px;font-size:12px";
                    salaryDetails.appendChild(inhandText);

                    rowBox.appendChild(salaryDetails);
                    rowBox.appendChild(salaryResponse);
                    seventhproperty.appendChild(rowBox);
                    orderList.appendChild(seventhproperty);
                }
                else if (rowData.propertyId == 8 && rowData.isMatching == false) {
                    var eigthproperty = document.createElement("li");
                    eigthproperty.textContent = "Which is your home locality? ";
                    eigthproperty.id = "property_" + rowData.propertyId;

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

                    url = "/getAllLocality";
                    fn = function (returnedData) {
                        processLocality(returnedData);
                        url = "";
                    };

                    titleLocality.appendChild(allLocalityDetail);
                    localityDetails.appendChild(titleLocality);
                    rowBox.appendChild(localityDetails);
                    eigthproperty.appendChild(rowBox);
                    orderList.appendChild(eigthproperty);
                }
                else if (rowData.propertyId == 9 && rowData.isMatching == false) {
                    var tenthproperty = document.createElement("li");
                    tenthproperty.textContent = "Time shift preferred";
                    tenthproperty.id = "property_" + rowData.propertyId;

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var shiftTitle = document.createElement("div");
                    shiftTitle.className = "col-xs-4 col-sm-4 col-sm-offset-2";
                    shiftTitle.style = "padding:2%;text-align:left";
                    rowBox.appendChild(shiftTitle);

                    var shiftResponse = document.createElement("div");
                    shiftResponse.className = "col-xs-8 col-sm-4";
                    shiftResponse.style = "padding:1%";
                    rowBox.appendChild(shiftResponse);

                    var shiftText = document.createElement("font");
                    shiftText.textContent = ("Time Shift");
                    shiftText.style = "font-weight:bold";
                    shiftTitle.appendChild(shiftText);

                    var shiftOption = document.createElement("select");
                    shiftOption.id = "candidateTimeShiftPref";
                    shiftOption.setAttribute("multiple", "multiple");
                    shiftResponse.appendChild(shiftOption);

                    url = "/getAllShift";
                    fn = function (returnedData) {
                        processTimeShift(returnedData);
                    };

                    tenthproperty.appendChild(rowBox);
                    orderList.appendChild(tenthproperty);
                }
                if (url != null) {
                    try {
                        $.ajax({
                            type: ajax_type,
                            url: url,
                            data: false,
                            async: false,
                            contentType: false,
                            processData: false,
                            success: fn
                        });
                    } catch (exception) {
                        console.log("exception occured!!" + exception);
                    }
                }
                if (rowData.propertyId == 5) {
                    try {
                        $.ajax({
                            type: "POST",
                            url: "/getAllDegree",
                            data: false,
                            contentType: false,
                            processData: false,
                            success: processDegree
                        });
                    } catch (exception) {
                        console.log("exception occured!!" + exception);
                    }
                }
            }
        });

    }
}
function interviewButtonCondition(jobPostId) {
    if (jobPostId != null) {
        var interview_api_url = "/support/api/shouldShowInterview/";
        if (interview_api_url == null || jobPostId == null) {
            console.log("please provide jobPostId");
            return
        } else {
            interview_api_url += "?";
            if (jobPostId != null) {
                interview_api_url += "jobPostId=" + jobPostId;
            }
        }
        try {
            $.ajax({
                type: "GET",
                url: interview_api_url,
                data: false,
                content: false,
                processData: false,
                success: processInterviewBtn
            });
        }
        catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}
function disableCurrentCompanyOption() {
    if(!$("#currentlyWorking").attr("checked",true)){
        $("#addCurrentlyWorking").prop("disabled",true);
        console.log("IM disable");
    }
    else
        if($("#currentlyWorking").attr("checked",true)){
        $("#addCurrentlyWorking").prop("disabled",false);
            console.log("IM non-disable");
    }
}
var companyCount=1;
function addmoreCompany() {
    var url;
    var fn;
    if(companyCount!=0 && companyCount <=2){
        companyCount++;
        console.log(companyCount);
        var allworkedCompanyDetailsDiv = document.createElement("div");
        allworkedCompanyDetailsDiv.className = "row";
        allworkedCompanyDetailsDiv.style = "margin:4px 0";

        var allWorkedAddMoreCol = document.createElement("div");
        allWorkedAddMoreCol.className = "col-sm-2";
        allworkedCompanyDetailsDiv.appendChild(allWorkedAddMoreCol);

        var allCompanyNameCol = document.createElement("div");
        allCompanyNameCol.className = "col-sm-4";
        allworkedCompanyDetailsDiv.appendChild(allCompanyNameCol);

        var allworkedJobRoleCol = document.createElement("div");
        allworkedJobRoleCol.className = "col-sm-3";
        allworkedCompanyDetailsDiv.appendChild(allworkedJobRoleCol);

        var allWorkedCurrentltyCol = document.createElement("div");
        allWorkedCurrentltyCol.className = "col-sm-3";
        allWorkedCurrentltyCol.style = "padding-top:1%;text-align:center";
        allworkedCompanyDetailsDiv.appendChild(allWorkedCurrentltyCol);

        var addMore = document.createElement("button");
        addMore.className = "form-control";
        addMore.type = "button";
        addMore.value = "Add";
        addMore.name = "Add";
        addMore.textContent = "Add";
        addMore.onclick = addmoreCompany;
        allWorkedAddMoreCol.appendChild(addMore);

        var addCompanyName = document.createElement("input");
        addCompanyName.className = "form-control";
        addCompanyName.type = ("text");
        addCompanyName.placeholder = ("Company Name");
        addCompanyName.id = ("companyName");
        addCompanyName.onchange = invalidSalary;
        allCompanyNameCol.appendChild(addCompanyName);

        var addJobRole = document.createElement("select");
        addJobRole.id = "workedJobRole_"+companyCount;
        allworkedJobRoleCol.appendChild(addJobRole);

        var addCurrentlyWorking = document.createElement("input");
        if(!$("#currentlyWorking").attr("checked",true)){
            addCurrentlyWorking.setAttribute("disabled",true);
            console.log("IM disable Add");
        }
        else{
            console.log("IM non-disable Add");
            addCurrentlyWorking.setAttribute("disabled",false);
        }
        addCurrentlyWorking.type = ("radio");
        addCurrentlyWorking.style = "margin:0 4%";
        addCurrentlyWorking.id = ("addCurrentlyWorking");
        addCurrentlyWorking.name = ("addCurrently_Working");
        allWorkedCurrentltyCol.appendChild(addCurrentlyWorking);

        var addCurrentlyWorkingLabel = document.createElement("label");
        addCurrentlyWorkingLabel.textContent = ("Current Company");
        addCurrentlyWorkingLabel.for = ("addCurrentlyWorking");
        allWorkedCurrentltyCol.appendChild(addCurrentlyWorkingLabel);

        $('#companyDetailsCapture').append(allworkedCompanyDetailsDiv);

        url='/getAllJobs ';
        fn = function(returnedData){
            processAllJobRole(returnedData);
            url="";
        }
    }
    else{
        alert("Enought is Enough");
    }
    if (url != null) {
        try {
            $.ajax({
                type:"POST",
                url: url,
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: fn
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}
function hideExperienceCaptureDiv() {
    if($("#candidateFresh").prop("checked",true)){
        $("#experienceDuration").css("display","none");
    }
}
function showExperienceCaptureDiv(){
    if(!$("#candidateExper").prop("checked",true)){
        $("#experienceDuration").css("display","none");
    }
    else{
        $("#experienceDuration").css("display","block");
    }
}
function showExperienceBox() {
    var yearValue = $("#candidateTotalExperienceYear").val();
    if(!isNaN(yearValue) && parseInt(yearValue)>0){
        $("#").css("display","block");
    }
}
function invalidSalary() {
    var salary = $('#candidateSalary').val();
    console.log(parseInt(salary));
    if(!isNaN(salary) && parseInt(salary) >= 1000 && parseInt(salary) <= 50000){
        $("#invalidSalaryNotification").css("display","none");
    } else {
        $("#invalidSalaryNotification").css("display","block");
    }
}
function processInterviewBtn(returnedData){
    if(returnedData !=null){
        if(returnedData == "INTERVIEW"){
            $("#preScreenInterviewSetBtn").html("Schedule Interview");
            $("#preScreenInterviewSetBtn").click(processInterviewSlotVew);

        }
        else{
            $("#preScreenInterviewSetBtn").html("Just Apply");
        }
    }
}
function processInterviewSlotVew() {
    
}