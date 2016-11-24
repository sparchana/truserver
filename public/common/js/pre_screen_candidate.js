/**
 * Created by hawk on 14/11/16.
 */
// aux methods start
var langArray = [];
var currentLocationArray = [];
var localityArray = [];
var jobRoleArray = [];
var propertyIdArray = [];
var candidateId;

var shouldShowPSModal = true;

function processTimeShift(returnedData) {
    if (returnedData != null) {
        var data = [{label: "None Selected", value: -1}];

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
}

function processDegree(returnedDegree) {
    if (returnedDegree != null) {
        var data = [{label: "None Selected", value: -1}];

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

function processLanguage(returnedData, idLanguageId) {
    var arrayLang = [];
    var arrayLangId = [];

    returnedData.forEach(function (language) {
        for (var i = 0; i <= idLanguageId.length; i++) {
            if (idLanguageId[i] == language.languageId) {
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
            for (var i = 0; i <= idProofId.length; i++) {
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
                    ip.oninput= validateInput;
                    ip.placeholder = idProof.idProofName + " Number";
                    ip.setAttribute("id", "idProofValue_" + idProof.idProofId);
                    idProofNumberTd.appendChild(ip);
                }
            }
        });
    }
}

function validateInput(idProofId, value) {
    if (idProofId == null || value == null) {
        idProofId = this.id.split("_")[1];
        value = this.value;
    };
    if(!$('input#idProofCheckbox_' + idProofId).is(':checked')) {
        return true;
    }
    console.log(idProofId + " " + value);

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
        console.log(value);
        if (!validateDL(value)) {
            $("#Invalid_" + idProofId).css("display", "block");
            return false;
        } else {
            $("#Invalid_" + idProofId).css("display", "none");
            return true;
        }
    } else if (idProofId == 2) {
        console.log(value);
        if (!validatePASSPORT(value)) {
            $("#Invalid_" + idProofId).css("display", "block");
            return false;
        } else {
            $("#Invalid_" + idProofId).css("display", "none");
            return true;
        }
    } else if (idProofId == 4) {
        console.log(value);
        if (!validatePAN(value)) {
            $("#Invalid_" + idProofId).css("display", "block");
            return false;
        } else {
            $("#Invalid_" + idProofId).css("display", "none");
            return true;
        }
    }
}

function processAllJobRole(returnedData, id) {
    var locArray = [];
    if (returnedData != null && jobRoleArray.length == 0) {
        returnedData.forEach(function (jobRole) {
            var label = jobRole.jobName;
            var value = parseInt(jobRole.jobRoleId);
            var item = {};
            item ["id"] = value;
            item ["name"] = label;
            jobRoleArray.push(item);
            locArray.push(item);
        });
    }
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
}
function getJobRoleArray() {
    return jobRoleArray;

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
function openCandidatePreScreenModal(jobPostId, candidateMobile) {
    console.log("mobile: " + candidateMobile);
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
        console.log("modalOpenAttempt: " + modalOpenAttempt);
        if (modalOpenAttempt == 1) {
            if(shouldShowPSModal){
                $("#preScreenModal").modal();
            } else {
                $("#preScreenModal").modal('hide');
            }
        }
        if (modalOpenAttempt == 0) {
            modalOpenAttempt = 1;
            console.log(base_api_url);
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
            } catch (exception) {
                console.log("exception occured!!" + exception.stack);
            }
        }
        $.notify("Please complete Job Application form", 'success');
    }
}
function processPreScreenData(returnedData) {
    if (returnedData == null || returnedData.status != "SUCCESS") {
        if (returnedData != null && returnedData.status == "INVALID") {
            $.notify("Already Pre Screened", 'error');
        } else {
            $.notify("Request failed. Something went Wrong! Please Refresh", 'error');
        }
        shouldShowPSModal = false;
        return;
    }
    if(returnedData.elementList == null || returnedData.elementList.length == 0) {
        shouldShowPSModal = false;
        console.log("jobpostvscan elementlist is empty. dont open prescreen modal");
        $("#preScreenModal").modal('hide');
        return;
    } else {
        $("html").removeClass("modal-open").addClass("modal-open");
    }

    var parent = $('#missingInfo');
    var mainDiv = document.createElement("div");
    mainDiv.className = "row";
    parent.append(mainDiv);
    var subDivOne = document.createElement("div");
    subDivOne.className = "col-sm-12";
    mainDiv.appendChild(subDivOne);
    var hintMessage = document.createElement("p");
    hintMessage.textContent = "Please provide following details to apply for this job";
    hintMessage.style = "margin:0;font-weight:bold;font-size:18px";
    subDivOne.appendChild(hintMessage);
    var subDivTwo = document.createElement("div");
    subDivTwo.style = "padding:0 4%";
    subDivTwo.className = "col-sm-12";
    mainDiv.appendChild(subDivTwo);
    var orderList = document.createElement("ol");
    orderList.className = "list-group";
    subDivTwo.appendChild(orderList);
    propertyIdArray = [];
    if (returnedData != null) {
        var elementList = returnedData.elementList;
        candidateId = returnedData.candidateId;

        elementList.forEach(function (rowData) {
            var ajax_type = "POST";
            var url;
            var fn;
            if (rowData != null) {
                if (rowData.propertyId == 0 && rowData.isMatching == false) {
                    propertyIdArray.push(rowData.propertyId);

                    var idProofId = [];
                    var jobPostElementList = rowData.jobPostElementList;
                    jobPostElementList.forEach(function (documentData) {
                        idProofId.push(documentData.object.idProofId);
                    });
                    var firstproperty = document.createElement("li");
                    firstproperty.textContent = "Do you have the following document(s) ?";
                    firstproperty.style = "color:#09ac58";
                    firstproperty.id = "property_" + rowData.propertyId;

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
                    propertyIdArray.push(rowData.propertyId);
                    var idLanguageId = [];
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

                    // var langTable = document.createElement("table");
                    // langTable.id = "langTable";
                    // langTable.className = "";
                    // var thead = document.createElement("thead");
                    // var tbody = document.createElement("tbody");
                    // langTable.appendChild(thead);
                    // langTable.appendChild(tbody);
                    // languageDetails.appendChild(langTable);

                    rowBox.appendChild(languageDetails);
                    secondProperty.appendChild(rowBox);
                    url = "/getAllLanguage";
                    fn = function (returnedData) {
                        processLanguage(returnedData, idLanguageId);
                        url = "";
                    };
                    orderList.appendChild(secondProperty);
                }
                else if (rowData.propertyId == 2 && rowData.isMatching == false) {
                    propertyIdArray.push(rowData.propertyId);
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
                    propertyIdArray.push(rowData.propertyId);
                    var thirdproperty = document.createElement("li");
                    thirdproperty.textContent = "Please mention your Date of Birth";
                    thirdproperty.id = "property_" + rowData.propertyId;
                    orderList.appendChild(thirdproperty);

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var ageTitle = document.createElement("div");
                    ageTitle.className = "col-xs-12 col-sm-5 col-sm-offset-1";
                    ageTitle.style = "padding:2%;text-align:left";
                    rowBox.appendChild(ageTitle);

                    var ageResponse = document.createElement("div");
                    ageResponse.className = "col-xs-8 col-sm-6";
                    ageResponse.style = "padding:1%";
                    rowBox.appendChild(ageResponse);

                    var ageText = document.createElement("font");
                    ageText.textContent = ("Date Of Birth");
                    ageText.style = "font-weight:bold";
                    ageTitle.appendChild(ageText);

                    var textAge = document.createElement("input");
                    textAge.className = "form-control";
                    textAge.id = "candidateDob";
                    textAge.type = ("date");
                    textAge.max = '1998-12-31';
                    textAge.placeholder = ("When is your Birthday?");
                    ageResponse.appendChild(textAge);

                    thirdproperty.appendChild(rowBox);
                    orderList.appendChild(thirdproperty);
                    // $('#candidateDob').datetimepicker({
                    //     format: 'DD/MM/YYYY'
                    // });
                    // $("#candidateDob").datepicker({dateFormat: 'yy-mm-dd', changeYear: true});
                }
                else if (rowData.propertyId == 4 && rowData.isMatching == false && rowData.candidateElement == null && rowData.candidateElementList == null) {
                    propertyIdArray.push(rowData.propertyId);

                    var fourthproperty = document.createElement("li");
                    fourthproperty.textContent = "Do you have work experience?";
                    fourthproperty.id = "property_" + rowData.propertyId;

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var rowBoxDetails = document.createElement("div");
                    rowBoxDetails.className = "row";
                    rowBoxDetails.style = "display: none;";
                    rowBoxDetails.id = "companyDetailsCapture";


                    var allworkedCompanyDetails = document.createElement("p");
                    allworkedCompanyDetails.textContent = ("Where all have you worked before? ");
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
                    addCompanyName.onchange = enableAddBtn;
                    allCompanyNameCol.appendChild(addCompanyName);

                    var addJobRole = document.createElement("input");
                    addJobRole.id = "workedJobRole_1";
                    addJobRole.onchange = enableAddBtn;
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
                    addMore.style = "background:#09ac58;color:#fff";
                    addMore.textContent = "Add More";
                    addMore.onclick = addmoreCompany;

                    allWorkedAddMoreCol.appendChild(addMore);
                    addCurrentlyWorking.type = ("radio");
                    addCurrentlyWorking.style = "margin:0 4%";
                    addCurrentlyWorking.id = ("addCurrentlyWorking_" + companyCount);
                    addCurrentlyWorking.name = ("addCurrently_Working");
                    allWorkedCurrentltyCol.appendChild(addCurrentlyWorking);

                    var addCurrentlyWorkingLabel = document.createElement("label");
                    addCurrentlyWorkingLabel.textContent = ("Is this your current company");
                    addCurrentlyWorkingLabel.for = ("addCurrentlyWorking_1");
                    allWorkedCurrentltyCol.appendChild(addCurrentlyWorkingLabel);

                    url = '/getAllJobs ';
                    fn = function (returnedData) {
                        processAllJobRole(returnedData, 1);
                        url = "";
                    };

                    var experienceOption = document.createElement("div");
                    experienceOption.className = "col-xs-12 col-md-12";
                    experienceOption.style = "padding:2%;";
                    rowBox.appendChild(experienceOption);

                    var experienceQuestion= document.createElement("div");
                    experienceQuestion.className = "col-xs-12 col-sm-5 col-sm-offset-1";
                    experienceQuestion.id = "experienceQuestion";
                    experienceQuestion.style = "padding:2%;display:none";
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
                    colDetailsFresher.className = "col-xs-12 col-sm-3 col-sm-offset-1";
                    experienceOption.appendChild(colDetailsFresher);

                    var colDetailsExperience = document.createElement("div");
                    colDetailsExperience.className = "col-xs-12 col-sm-4";
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
                    labelFresher.textContent = ("I'm a Fresher");
                    labelFresher.for = ("candidateFresh");
                    colDetailsFresher.appendChild(labelFresher);

                    var radioExperience = document.createElement("input");
                    radioExperience.type = ("radio");
                    radioExperience.style = "margin:0 8%";
                    radioExperience.id = ("candidateExp");
                    radioExperience.name = ("candidateExperience");
                    radioExperience.value = (1);
                    radioExperience.onchange = showExperienceCaptureDiv;
                    colDetailsExperience.appendChild(radioExperience);

                    var labelExperience = document.createElement("label");
                    labelExperience.textContent = ("I have work experience");
                    labelExperience.for = ("candidateExp");
                    colDetailsExperience.appendChild(labelExperience);


                    var textYear = document.createElement("input");
                    textYear.className = "form-control";
                    textYear.type = ("number");
                    textYear.value = 0;
                    textYear.oninput = showExperienceBox;
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
                    textMonths.oninput = showExperienceBox;
                    experienceDuration.appendChild(textMonths);

                    var titleExpMonths = document.createElement("font");
                    titleExpMonths.textContent = ("Months");
                    titleExpMonths.style = "font-weight:bold";
                    experienceDuration.appendChild(titleExpMonths);

                    var experienceCurrently = document.createElement("div");
                    experienceCurrently.className = "col-xs-12 col-md-10 col-md-offset-1";
                    experienceCurrently.id = "experienceCurrently";
                    experienceCurrently.style = "padding:2%;display:none";
                    rowBox.appendChild(experienceCurrently);

                    var currentlyWorking = document.createElement("p");
                    currentlyWorking.textContent = ("Are you currently working : ");
                    experienceCurrently.appendChild(currentlyWorking);

                    var checkboxCurrentlyWorking = document.createElement("input");
                    checkboxCurrentlyWorking.id = ("currentlyWorking");
                    checkboxCurrentlyWorking.type = ("checkbox");
                    checkboxCurrentlyWorking.style = "margin:0 8%";
                    checkboxCurrentlyWorking.onclick = disableCurrentCompanyOption;
                    currentlyWorking.appendChild(checkboxCurrentlyWorking);

                    fourthproperty.appendChild(rowBox);
                    fourthproperty.appendChild(rowBoxDetails);
                    orderList.appendChild(fourthproperty);
                }
                else if (rowData.propertyId == 5 && rowData.isMatching == false && rowData.candidateElement == null && rowData.candidateElementList == null) {
                    propertyIdArray.push(rowData.propertyId);
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
                    educationDetailsHEQ.className = "col-xs-12 col-sm-5 col-sm-offset-1";
                    educationDetailsHEQ.id = "education_details";
                    educationDetailsHEQ.style = "padding:2%;text-align:left";
                    rowBoxHEQ.appendChild(educationDetailsHEQ);

                    var educationResponseHEQ = document.createElement("div");
                    educationResponseHEQ.className = "col-xs-12 col-sm-6";
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
                    educationDetailsSuccess.className = "col-xs-12 col-sm-5 col-sm-offset-1";
                    educationDetailsSuccess.id = "education_details";
                    educationDetailsSuccess.style = "padding:2%;text-align:left";
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
                    educationDetailsHED.className = "col-xs-12 col-sm-5 col-sm-offset-1";
                    educationDetailsHED.id = "education_details";
                    educationDetailsHED.style = "padding:2%;text-align:left";
                    rowBoxHED.appendChild(educationDetailsHED);

                    var educationResponseHED = document.createElement("div");
                    educationResponseHED.className = "col-xs-12 col-sm-6";
                    educationResponseHED.id = "education_details";
                    educationResponseHED.style = "padding:1%";
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
                    educationDetailsInstitute.className = "col-xs-12 col-sm-5 col-sm-offset-1";
                    educationDetailsInstitute.style = "padding:2%;text-align:left";
                    rowBoxLastInstitute.appendChild(educationDetailsInstitute);
                    var educationResponseInstitute = document.createElement("div");

                    educationResponseInstitute.className = "col-xs-12 col-sm-6";
                    educationResponseInstitute.style = "padding:1%";
                    rowBoxLastInstitute.appendChild(educationResponseInstitute);

                    var educationtitleInstitute = document.createElement("font");
                    educationtitleInstitute.textContent = ("Last attended Education Institute?");
                    educationtitleInstitute.style = "margin-top:8px;font-weight:bold";
                    educationDetailsInstitute.appendChild(educationtitleInstitute);

                    var educationtextInstitute = document.createElement("input");
                    educationtextInstitute.className = "form-control";
                    educationtextInstitute.type = ("text");
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
                    propertyIdArray.push(rowData.propertyId);
                    var sixthproperty = document.createElement("li");
                    sixthproperty.textContent = "Please mention your gender ";
                    sixthproperty.id = "property_" + rowData.propertyId;

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var genderDetails = document.createElement("div");
                    genderDetails.className = "col-sm-12 col-md-12";
                    genderDetails.id = "gender_details";
                    genderDetails.style = "padding:2%;";

                    var colGenderDetailsMale = document.createElement("div");
                    colGenderDetailsMale.className = "col-xs-12 col-sm-3 col-sm-offset-1";
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
                    orderList.appendChild(sixthproperty);
                }
                else if (rowData.propertyId == 7 && rowData.isMatching == false) {
                    propertyIdArray.push(rowData.propertyId);
                    var seventhproperty = document.createElement("li");
                    seventhproperty.textContent = "Please provide your " + rowData.propertyTitle + " details";
                    seventhproperty.id = "property_" + rowData.propertyId;

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var salaryDetails = document.createElement("div");
                    salaryDetails.className = "col-xs-12 col-sm-5 col-sm-offset-1";
                    salaryDetails.id = "salary_details";

                    var salaryResponse = document.createElement("div");
                    salaryResponse.className = "col-xs-12 col-sm-6";
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
                    textSalary.id = ("candidateLastWithdrawnSalary");
                    textSalary.onchange = invalidSalary;
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
                    orderList.appendChild(seventhproperty);
                }
                else if (rowData.propertyId == 8 && rowData.isMatching == false) {
                    propertyIdArray.push(rowData.propertyId);
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
                    propertyIdArray.push(rowData.propertyId);
                    var tenthproperty = document.createElement("li");
                    tenthproperty.textContent = "Time shift preferred";
                    tenthproperty.id = "property_" + rowData.propertyId;

                    var rowBox = document.createElement("div");
                    rowBox.className = "row";

                    var shiftTitle = document.createElement("div");
                    shiftTitle.className = "col-xs-12 col-sm-5 col-sm-offset-1";
                    shiftTitle.style = "padding:2%;text-align:left";
                    rowBox.appendChild(shiftTitle);

                    var shiftResponse = document.createElement("div");
                    shiftResponse.className = "col-xs-12 col-sm-6";
                    shiftResponse.style = "padding:1%";
                    rowBox.appendChild(shiftResponse);

                    var shiftText = document.createElement("font");
                    shiftText.textContent = ("Time Shift");
                    shiftText.style = "font-weight:bold";
                    shiftTitle.appendChild(shiftText);

                    var shiftOption = document.createElement("select");
                    shiftOption.id = "candidateTimeShiftPref";
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
                success: function (returnedData) {
                    processInterviewBtn(returnedData, jobPostId);
                }
            });
        }
        catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function disableCurrentCompanyOption() {
    if (!$("#currentlyWorking").is(":checked")) {
        var radios = document.getElementsByName('addCurrently_Working');
        for (var i = 0; i < radios.length; i++) {
            radios[i].disabled = true;
            radios[i].checked = false;
        }
        // document.getElementsByName("addCurrently_Working").disabled = true;
        //$("#addCurrentlyWorking").prop("disabled",true);
        console.log("IM disable");
    }
    else {
        if ($("#currentlyWorking").is(":checked")) {
            var radios = document.getElementsByName('addCurrently_Working');
            for (var i = 0; i < radios.length; i++) {
                radios[i].disabled = false;
            }
            // $("#addCurrentlyWorking").prop("disabled",false);
            console.log("IM non-disable");
        }
    }
}
function enableAddBtn(){
        var id = this.id.split("_")[1];
        if($("#companyName_" + id).val() == "" || $("#workedJobRole_" + id).val() == "") {
            $("#addCurrentlyWorkingBtn_" + id).prop("disabled",true);
            }
            else {
                $("#addCurrentlyWorkingBtn_" + id).prop("disabled",false);
            }
    }
var companyCount = 1;
function addmoreCompany() {
    var url;
    var fn;
    if (companyCount != 0 && companyCount <= 2) {
        companyCount++;
        console.log(companyCount);
        var allworkedCompanyDetailsDiv = document.createElement("div");
        allworkedCompanyDetailsDiv.className = "row";
        allworkedCompanyDetailsDiv.id = "row_" + companyCount;
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
        addCompanyName.id = ("companyName_" + companyCount);
        allCompanyNameCol.appendChild(addCompanyName);

        var addJobRole = document.createElement("input");
        addJobRole.id = "workedJobRole_" + companyCount;
        addJobRole.onchange = enableAddBtn;
        allworkedJobRoleCol.appendChild(addJobRole);

        var addCurrentlyWorking = document.createElement("input");
        if (!$("#currentlyWorking").is(":checked")) {
            addCurrentlyWorking.disabled = true;
        }
        else {
            addCurrentlyWorking.disabled = false;
        }

        var addMore = document.createElement("button");
        addMore.className = "form-control";
        addMore.type = "button";
        addMore.setAttribute("disabled",true);
        addMore.id = "addCurrentlyWorkingBtn_"+companyCount;
        addMore.value = "Add";
        addMore.style = "background:#09ac58;color:#fff";
        addMore.name = "Add";
        addMore.textContent = "Add More";
        addMore.onclick = addmoreCompany;

        allWorkedAddMoreCol.appendChild(addMore);
        addCurrentlyWorking.type = ("radio");
        addCurrentlyWorking.style = "margin:0 4%";
        addCurrentlyWorking.id = ("addCurrentlyWorking_" + companyCount);
        addCurrentlyWorking.name = ("addCurrently_Working");
        allWorkedCurrentltyCol.appendChild(addCurrentlyWorking);

        var addCurrentlyWorkingLabel = document.createElement("label");
        addCurrentlyWorkingLabel.textContent = ("Is this your current company");
        addCurrentlyWorkingLabel.for = ("addCurrentlyWorking_" + companyCount);
        allWorkedCurrentltyCol.appendChild(addCurrentlyWorkingLabel);

        var previousButton = companyCount - 1;
        $('#companyDetailsCapture').append(allworkedCompanyDetailsDiv)
        $("#addCurrentlyWorkingBtn_"+previousButton).prop("disabled",true);
        url = '/getAllJobs ';
        fn = function (returnedData) {
            processAllJobRole(returnedData, companyCount);
            url = "";
        }
    }
    else {
        $.notify("Max 3 Addition Allowed", 'error');
    }
    if (url != null) {
        if(jobRoleArray == null || jobRoleArray.length == 0){
            try {
                $.ajax({
                    type: "POST",
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
        } else {
            fn(null);
        }

    }
}

function hideExperienceCaptureDiv() {
    if ($("#candidateFresh").is(":checked")) {
        $("#experienceDuration").css("display", "none");
        $("#experienceQuestion").css("display", "none");
        $("#experienceCurrently").css("display", "none");
        $('#companyDetailsCapture').hide();
    }
}
function showExperienceCaptureDiv() {
    if ($("#candidateExp").is(":checked")) {
        $("#experienceDuration").css("display", "block");
        $("#experienceQuestion").css("display", "block");
    }
}

function showExperienceBox() {
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
}

function invalidSalary() {
    var salary = $('#candidateLastWithdrawnSalary').val();
    console.log(parseInt(salary));
    if (!isNaN(salary) && parseInt(salary) >= 1000 && parseInt(salary) <= 50000) {
        $("#invalidSalaryNotification").css("display", "none");

    } else {
        $("#invalidSalaryNotification").css("display", "block");
    }
}

function processInterviewBtn(returnedData, jobPostId) {
    if (returnedData != null) {
        if (returnedData == "INTERVIEW") {
            $("#preScreenInterviewSetBtn").html("Schedule Interview");
        }
        else {
            $("#preScreenInterviewSetBtn").html("Apply");
        }
    }
}

function submitPreScreen() {
    console.log("submit prescreen");
}

(function () {

    $("#preScreenInterviewSetBtn").click(function () {
        var okToSubmit = true;
        var okToSubmitList = [];
        var dobCheck;
        var prevCompanyList = [];
        // all non-matching properId is available in propertyIdArray
        var d = {};
        var msg;

        // prep d
        $.each(propertyIdArray, function (index, propId) {
            okToSubmit = true;
            if (propId == 0) {
                var documentList = [];
                $('#document_details').each(function () {
                    $(this).find('input[type=checkbox]').each(function () {
                        var item = {};
                        var id;
                        console.log($(this).attr('id'));
                        id = $(this).attr('id').split("_").slice(-1).pop();

                        var isChecked = $('input#idProofCheckbox_' + id).is(':checked');
                        var isValid = validateInput(id, $('input#idProofValue_' + id).val().trim());
                        if (  isValid && isChecked) {
                            item["idProofId"] = parseInt(id);
                            item["idNumber"] = $('input#idProofValue_' + id).val().trim();
                        } else if (isChecked && !isValid) {
                            okToSubmit = false;
                            console.log("doc not valid for id: " + id);
                        }

                        if (!jQuery.isEmptyObject(item)) {
                            documentList.push(item);
                        };
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
                if(!okToSubmit){
                    var submit = {
                        propId : propId,
                        message: msg,
                        submissionStatus: okToSubmit
                    };
                    okToSubmitList.push(submit);
                }
            } else if (propId == 1) {
                var check;
                var languageMap = [];
                var languageKnown = $('#language_details input:checked').map(function () {
                    check = 0;
                    var id = this.id;
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
                        else if (name == "rw")
                            item["rw"] = 1;
                        else
                            item["s"] = 1;
                        languageMap.push(item);
                    }
                    else {
                        if (name == "u")
                            languageMap[pos].u = 1;
                        else if (name == "rw")
                            languageMap[pos].rw = 1;
                        else
                            languageMap[pos].s = 1;
                    }
                }).get();

                d ["candidateKnownLanguageList"] = languageMap;

                if(languageMap.length == 0) {
                    okToSubmit = false;
                    $.notify("Please provide all known languages", 'error');
                }
                if(!okToSubmit){
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
                if(!okToSubmit){
                    var submit = {
                        propId : propId,
                        message: msg,
                        submissionStatus: okToSubmit
                    };
                    okToSubmitList.push(submit);
                }
            } else if (propId == 3) {
                // age submission
                var selectedDob = $('#candidateDob').val();
                if(selectedDob == "") {
                    okToSubmit = false;
                }
                var c_dob = String(selectedDob);
                var selectedDate = new Date(c_dob);
                var toDate = new Date();
                var pastDate = new Date(toDate.setFullYear(toDate.getFullYear() - 18));
                var zombieYear = new Date(toDate.setFullYear(toDate.getFullYear() - 70));
                if (selectedDate >= pastDate) {
                    dobCheck = 0;
                    okToSubmit = false;
                }
                if (selectedDate <= zombieYear) {
                    dobCheck = 0;
                    okToSubmit = false;
                }
                d ["candidateDob"] = c_dob;
                if(!okToSubmit){
                    $.notify("Please provide valid Date of birth", 'error');
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
                    okToSubmit = false;
                    $.notify("Please select Fresher/Experienced.", 'error');
                }
                var expMonth = parseInt($('#candidateTotalExperienceMonth').val());
                var expYear = parseInt($('#candidateTotalExperienceYear').val());
                var totalExp = expMonth + (12 * expYear);
                var isExpEmpty = ($('#candidateTotalExperienceMonth').val() == 0) && ($('#candidateTotalExperienceYear').val() == 0);
                if ($('input[id=candidateExp]').is(":checked") && isExpEmpty) {
                    $.notify("Please provide your total years of experience", 'error');
                    okToSubmit = false;
                }

                // are you currently working
                if ($('input[id=candidateExp]').is(":checked") && $('#currentlyWorking').is(":checked")
                    && !$('input[name=addCurrently_Working]').is(":checked")) {
                    $.notify("Please provide your current company details and mark appropriately.", 'error');
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
                        console.log("adding item");
                        prevCompanyList.push(item);
                    }
                }

                console.log("totalExp: " + totalExp);
                d ["candidateTotalExperience"] = totalExp;
                d ["pastCompanyList"] = prevCompanyList;
                d ["candidateIsEmployed"] = $('#currentlyWorking').is(":checked");
                d ["extraDetailAvailable"] = true;

                if(!okToSubmit){
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

               if( $('#candidateHighestEducation').val() == "-1" ||
                   ($('#candidateHighestDegree').val()) == "-1" ||
                   $('#candidateEducationInstitute').val() == "" ||
                   $('input:radio[name="candidateEducationCompletionStatus"]:checked').val() == null) {
                   okToSubmit = false;
                   $.notify("Please provide full education details", 'error');

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
                    okToSubmit = false;
                    $.notify("Please provide your gender details", 'error');
                }
                if(!okToSubmit){
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
                    okToSubmit = false;
                    $.notify("Please enter a valid 'Last Withdrawn Salary' per month. (Min: 1000, Max: 1,00,000)", 'error');
                    if(!okToSubmit){
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
                    okToSubmit = false;
                    $.notify("Please enter a valid Locality", 'error');
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
                    okToSubmit = false;
                    $.notify("Please enter a valid time/shift preference (ex: Part time, Full time)", 'error');
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

        // ajax to submit d
        console.log(d);
        console.log(okToSubmitList);
        var isSupport = false;
        if (okToSubmitList.length == 0) {
            try {
                $.ajax({
                    type: "POST",
                    url: "/updateCandidateDetailsViaPreScreen/?propertyIdList=" + propertyIdArray + "&candidateMobile=" + localStorage.getItem("mobile") + "&jobPostId="+jobPostId,
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: function (returnedData) {
                        console.log(returnedData);
                        if(returnedData == "INTERVIEW"){
                            $("#preScreenModal").modal('hide');
                            initInterviewModal(candidateId, jobPostId, isSupport);
                        } else if(returnedData == "OK") {
                            $("#preScreenModal").modal('hide');
                            $.notify("Successfully Submitted Application form. Thanks !", 'success');
                        } else {
                            $.notify("Something went wrong. Please try again !", 'success');
                        }
                    }
                });
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
            return true;
        }
    });
})();