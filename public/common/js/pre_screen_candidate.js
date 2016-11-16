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

function processLanguage(returnedData) {
    var arrayLang = [];
    var arrayLangId = [];
    var defaultOption = $('<option value="-1"></option>').text("Select");
    returnedData.forEach(function (language) {
        var id = language.languageId;
        var name = language.languageName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        arrayLang.push(name);
        arrayLangId.push(id);
        var option = $('<option value=' + id + '></option>').text(name);
        langArray.push(item);
    });
    populateLanguages(arrayLang.reverse(), arrayLangId.reverse());
}

function populateLanguages(l, lId) {
    var i;
    var table = document.getElementById("langTable");
    for (i = 0; i < l.length; i++) {
        if (lId[i] == 1 || lId[i] == 2 || lId[i] == 3 || lId[i] == 4 || lId[i] == 5) {
            var row = table.insertRow(0);

            var cell1 = row.insertCell(0);
            cell1.style="padding-top:16px;font-weight:bold";
            var cell2 = row.insertCell(1);
            var cell3 = row.insertCell(2);
            var cell4 = row.insertCell(3);
            var cell5 = row.insertCell(4);

            cell1.innerHTML = '<span style="margin-top:8px">'+ l[i] +'</span>';
            cell2.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
            cell3.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"u\" value=0 >Understand</label></div>";
            cell4.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"s\" value=0 >Speak</label></div>";
            cell5.innerHTML = "<div class=\"btn-group\" data-toggle=\"buttons\">" + "<label class=\"btn btn-custom-check\" style=\"width: 110px\">" + "<input id=" + lId[i] + " type=\"checkbox\" name=\"rw\" value=0 >Read/Write</label></div>";
        }
    }
}

function prefillLanguageTable(languageKnownList) {
    $('table#langTable tr').each(function () {
        $(this).find('input').each(function () {
            //do your stuff, you can use $(this) to get current cell
            var x = document.createElement("INPUT");
            x = $(this).get(0);
            languageKnownList.forEach(function (languageKnown) {
                if (x.id == languageKnown.language.languageId) {
                    if (languageKnown.verbalAbility == "1" && x.name == "s") {
                        x.checked = true;
                        $(x).parent().addClass('active').siblings().removeClass('active');
                    } else if (languageKnown.readWrite == "1" && x.name == "rw") {
                        x.checked = true;
                        $(x).parent().addClass('active').siblings().removeClass('active');
                    } else if (languageKnown.understanding == "1" && x.name == "u") {
                        x.checked = true;
                        $(x).parent().addClass('active').siblings().removeClass('active');
                    }
                }
            });
        });
    });
}

function processExperience(returnedExperience) {
    var data = [];

    returnedExperience.forEach(function (experience) {
        var opt = {
            label: experience.experienceType, value: parseInt(experience.experienceId)
        };
        data.push(opt);
    });

    var selectList = $('#experienceMultiSelect');
    selectList.multiselect({
        nonSelectedText: 'None Selected',
        includeSelectAllOption: true,
        maxHeight: 300
    });
    selectList.multiselect('dataprovider', data);
    selectList.multiselect('rebuild');
}

function processDataGetAssets(returnedAssets) {
    var data = [];

    returnedAssets.forEach(function (asset) {
        var opt = {
            label: asset.assetTitle, value: parseInt(asset.assetId)
        };
        data.push(opt);
    });

    var selectList = $('#assetMultiSelect');
    selectList.multiselect({
        includeSelectAllOption: true,
        enableCaseInsensitiveFiltering: true,
        maxHeight: 300
    });
    selectList.multiselect('dataprovider', data);
    selectList.multiselect('rebuild');
}

function processIdProofsWithNumbers(returnedData, customD) {
    // create table
    if(returnedData != null) {
        // minReqTable
        console.log(" Result : "+ customD);
        console.log(" Result : "+ JSON.stringify(returnedData));
        var minReqTableContainer = $('#document_details');

        var mainTable = document.createElement("table");
        /*mainTable.className =customD.table.mainTable.className;
        mainTable.style = customD.table.mainTable.style;*/
        mainTable.id = "documentTable";

        var tHead = document.createElement("thead");
        /*tHead.style = customD.table.mainTable.tHead.style;*/
        mainTable.appendChild(tHead);

        var heading = document.createElement("tr");
        tHead.appendChild(heading);

        var title1 = document.createElement("th");
        heading.appendChild(title1);

        var title2 = document.createElement("th");
        title2.textContent = "Document Title";
        heading.appendChild(title2);

        var title3 = document.createElement("th");
        title3.textContent = "Document Number";
        heading.appendChild(title3);

        var tBody = document.createElement("tbody");
        mainTable.appendChild(tBody);

        minReqTableContainer.append(mainTable);

        returnedData.forEach(function (idProof) {
            var bodyContentBox = document.createElement("tr");
            bodyContentBox.id = idProof.idProofId;
            tBody.appendChild(bodyContentBox);

            var checkboxTd = document.createElement("td");
            checkboxTd.id = "idProofCheckbox";
            bodyContentBox.appendChild(checkboxTd);

            var checkMatchLabel = document.createElement("label");
            checkMatchLabel.type = "checkbox";
            checkMatchLabel.for = "checkboxLabel_" + idProof.idProofId;
            checkMatchLabel.style = 'text-align:center';
            /*checkMatchLabel.className = customD.table.idProofCheckbox.checkboxLabel.className;*/
            checkboxTd.appendChild(checkMatchLabel);

            var checkMatch = document.createElement("input");
            checkMatch.type = "checkbox";
            checkMatch.id = "idProofCheckbox_" + idProof.idProofId;
            checkMatch.onclick = function () {
                $('#idProofValue_'+idProof.idProofId).val("");
            };
            checkMatch.className = "mdl-checkbox__input";
            checkMatchLabel.appendChild(checkMatch);

            var idProofTitleTd = document.createElement("td");
            idProofTitleTd.textContent = idProof.idProofName;
            bodyContentBox.appendChild(idProofTitleTd);

            var idProofNumberTd = document.createElement("td");
            idProofNumberTd.id = "idProofValueTd";
            bodyContentBox.appendChild(idProofNumberTd);

            var ip = document.createElement("INPUT");
            ip.setAttribute("type", "text");
            ip.setAttribute("id", "idProofValue_"+idProof.idProofId);
            idProofNumberTd.appendChild(ip);

        });
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
            console.log("localityArray: "+localityArray.length);
            console.log("localityArray: "+locArray.length);
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
function openPreScreenModal(jobPostId,candidateMobile){

    var base_api_url ="/support/api/getJobPostVsCandidate/";
    candidateMobile = candidateMobile.substring(3);
    if(base_api_url == null || jobPostId == null) {
        console.log("please provide candidateId && jobPostId");
        return
    } else {
        base_api_url +="?";
        if(jobPostId != null) {
            base_api_url += "jobPostId=" + jobPostId;
        }
        if(candidateMobile != null){
            base_api_url += "&candidateMobile=" + candidateMobile;
        }
    }
    base_api_url +="&rePreScreen="+true;
    console.log(" url link : " + base_api_url);
    if(modalOpenAttempt == 1){
        $("#preScreenModal").modal();
    }
    if(modalOpenAttempt == 0){
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
function processPreScreenData(returnedData) {
    if (returnedData == null || returnedData.status != "SUCCESS") {
        console.log(returnedData);
        if (returnedData != null && returnedData.status == "INVALID") {
            alert("Already Pre Screened");
        } else {
            alert("Request failed. Something went Wrong! Please Refresh");
        }
        return;
    }
    var customD = "0";
    var parent = $('#missingInfo');
    var mainDiv = document.createElement("div");
    mainDiv.className = "row";
    parent.append(mainDiv);
    var subDivOne = document.createElement("div");
    subDivOne.className = "col-sm-12";
    mainDiv.appendChild(subDivOne);
    var hintMessage = document.createElement("p");
    hintMessage.textContent = "Please provide your following details to apply for this job";
    subDivOne.appendChild(hintMessage);
    var subDivTwo = document.createElement("div");
    subDivTwo.style = "padding:0 4%";
    subDivTwo.className = "col-sm-12";
    mainDiv.appendChild(subDivTwo);
    var orderList = document.createElement("ol");
    orderList.className = "list-group";
    subDivTwo.appendChild(orderList);
    if (returnedData != null) {
        var url;
        var ajax_type = "POST";
        var fn;
        var elementList = returnedData.elementList;
        elementList.forEach(function (rowData) {
            if (rowData != null) {
                if(rowData.propertyId == 0 && rowData.isMatching == false){
                    console.log("rowId: "+rowData.propertyId);
                    var firstproperty = document.createElement("li");
                    firstproperty.textContent = "Please provide your "+rowData.propertyTitle+" details";
                    firstproperty.id = "property_"+ rowData.propertyId;
                    var documentDetails = document.createElement("div");
                    documentDetails.id = "document_details";
                    firstproperty.appendChild(documentDetails);
                    url = "/support/api/getDocumentReqForJobRole/?job_post_id="+returnedData.jobPostId;
                    ajax_type = "GET";
                    fn = function (returnedData) {
                        processIdProofsWithNumbers(returnedData, customD);
                        url = "";
                    };
                    orderList.appendChild(firstproperty);
                }
                else if(rowData.propertyId == 1 && rowData.isMatching == false){
                    console.log("rowId: "+rowData.propertyId);
                    var secondProperty = document.createElement("li");
                    secondProperty.textContent = "Please provide your "+rowData.propertyTitle+" details";
                    secondProperty.id = "property_"+ rowData.propertyId;
                    var languageDetails = document.createElement("table");
                    languageDetails.id = "langTable";
                    secondProperty.appendChild(languageDetails);
                    url = "/getAllLanguage";
                    ajax_type = "POST"
                    fn = function (returnedData) {
                        processLanguage(returnedData);
                        url = "";
                    };
                    orderList.appendChild(secondProperty);
                }
                else if(rowData.propertyId == 2 && rowData.isMatching == false){
                    var firstproperty = document.createElement("li");
                    firstproperty.textContent = "Please provide your "+rowData.propertyTitle+" details";
                    firstproperty.id = "property_"+ rowData.propertyId;

                    var assetsDetails = document.createElement("div");
                    var assetsTitle = document.createElement("p");
                    assetsTitle.textContent = ("Asset : ");
                    var assetsOption = document.createElement("select");
                    assetsOption.id="assetMultiSelect";
                    assetsOption.setAttribute("multiple","multiple");

                    ajax_type = "GET";
                    url = "/support/api/getAssetReqForJobRole/?job_post_id="+returnedData.jobPostId;
                    fn = function (returnedData) {
                        processDataGetAssets(returnedData);
                        url = "";
                     };
                    assetsTitle.appendChild(assetsOption);
                    assetsDetails.appendChild(assetsTitle);
                    firstproperty.appendChild(assetsDetails);
                    orderList.appendChild(firstproperty);
                }
                else if(rowData.propertyId == 3 && rowData.isMatching == false){
                    var thirdproperty = document.createElement("li");
                    thirdproperty.textContent = "Please provide your "+rowData.propertyTitle+" details";
                    thirdproperty.id = "property_"+ rowData.propertyId;
                    orderList.appendChild(thirdproperty);
                }
                else if(rowData.propertyId == 4 && rowData.isMatching == false) {
                    var fourthproperty = document.createElement("li");
                    fourthproperty.textContent = "Please provide your " + rowData.propertyTitle + " details";
                    fourthproperty.id = "property_" + rowData.propertyId;

                    var experienceDetial = document.createElement("div");
                    var titleExpYear = document.createElement("p");
                    titleExpYear.textContent = ("Year : ");

                    var textYear = document.createElement("input");
                    textYear.type = ("number");
                    textYear.placeholder = ("Years");
                    textYear.id = ("candidateTotalExperienceYear");

                    var titleExpMonths = document.createElement("p");
                    titleExpMonths.textContent = ("Months : ");
                    var textMonths = document.createElement("input");
                    textMonths.type = ("number");
                    textMonths.placeholder = ("Months");
                    textMonths.id = ("candidateTotalExperienceMonth");

                    experienceDetial.appendChild(titleExpYear);
                    titleExpYear.appendChild(textYear);
                    experienceDetial.appendChild(titleExpMonths);
                    titleExpMonths.appendChild(textMonths);
                    fourthproperty.appendChild(experienceDetial);
                    orderList.appendChild(fourthproperty);
                }
                else if(rowData.propertyId == 5 && rowData.isMatching == false){
                    var fifthproperty = document.createElement("li");
                    fifthproperty.textContent = "Please provide your "+rowData.propertyTitle+" details";
                    fifthproperty.id = "property_"+ rowData.propertyId;
                    var educationDetails = document.createElement("select");
                    educationDetails.id = "candidateHighestEducation";
                    fifthproperty.appendChild(educationDetails);
                    url = "/getAllEducation";
                    fn = function (returnedData) {
                        processEducation(returnedData);
                        url = "";
                    };
                    orderList.appendChild(fifthproperty);
                }
                else if(rowData.propertyId == 6 && rowData.isMatching == false){
                    var sixthproperty = document.createElement("li");
                    sixthproperty.textContent = "Please provide your "+rowData.propertyTitle+" details";
                    sixthproperty.id = "property_"+ rowData.propertyId;

                    var genderDetial = document.createElement("div");
                    var labelMale = document.createElement("label");
                    labelMale.textContent = ("Male");
                    labelMale.for = ("genderMale");
                    var radioMale = document.createElement("input");
                    radioMale.type = ("radio");
                    radioMale.id = ("genderMale");
                    radioMale.name = ("gender");
                    radioMale.value = (0);

                    var labelFemale = document.createElement("label");
                    labelFemale.textContent = ("Female");
                    labelFemale.for = ("genderFemale");
                    var radioFemale = document.createElement("input");
                    radioFemale.type = ("radio");
                    radioFemale.id = ("genderFemale");
                    radioFemale.name = ("gender");
                    radioFemale.value = (1);

                    genderDetial.appendChild(labelMale);
                    genderDetial.appendChild(radioMale);
                    genderDetial.appendChild(labelFemale);
                    genderDetial.appendChild(radioFemale);
                    sixthproperty.appendChild(genderDetial);
                    orderList.appendChild(sixthproperty);
                }
                else if(rowData.propertyId == 7 && rowData.isMatching == false){
                    var seventhproperty = document.createElement("li");
                    seventhproperty.textContent = "Please provide your "+rowData.propertyTitle+" details";
                    seventhproperty.id = "property_"+ rowData.propertyId;

                    var salaryDetial = document.createElement("div");
                    var titleSalary= document.createElement("p");
                    titleSalary.textContent = ("Current/Last Drawn Salary : ");

                    var textSalary = document.createElement("input");
                    textSalary.type = ("number");
                    textSalary.placeholder = ("Salary");
                    textSalary.id = ("candidateSalary");

                    titleSalary.appendChild(textSalary)
                    salaryDetial.appendChild(titleSalary);
                    seventhproperty.appendChild(salaryDetial);
                    orderList.appendChild(seventhproperty);
                }
                else if(rowData.propertyId == 8 && rowData.isMatching == false){
                    var eigthproperty = document.createElement("li");
                    eigthproperty.textContent = "Please provide your "+rowData.propertyTitle+" details";
                    eigthproperty.id = "property_"+ rowData.propertyId;
                    var allLocalityDetail = document.createElement("input");
                    salaryDetail.id="candidateHomeLocality";
                    eigthproperty.appendChild(allLocalityDetail);
                    url = "/getAllLocality";
                    fn = function (returnedData) {
                        processLocality(returnedData);
                        url = "";
                    };
                    orderList.appendChild(eigthproperty);
                }
                else if(rowData.propertyId == 9 && rowData.isMatching == false){
                    var ninthproperty = document.createElement("li");
                    ninthproperty.textContent = "Please provide your "+rowData.propertyTitle+" details";
                    ninthproperty.id = "property_"+ rowData.propertyId;
                    var allShiftDetail = document.createElement("input");
                    salaryDetail.id="candidateTimeShiftPref";
                    ninthproperty.appendChild(allShiftDetail);
                    url = "/getAllShift";
                    fn = function (returnedData) {
                        processTimeShift(returnedData);
                        url = "";
                    };
                    orderList.appendChild(ninthproperty);
                }
                else if(rowData.propertyId == 10 && rowData.isMatching == false){
                    var tenthproperty = document.createElement("li");
                    tenthproperty.textContent = "Please provide your "+rowData.propertyTitle+" details";
                    tenthproperty .id = "property_"+ rowData.propertyId;

                    var shiftDetails = document.createElement("div");
                    var shiftTitle  = document.createElement("p");
                    shiftTitle.textContent = ("Time Shift : ");
                    var shiftOption = document.createElement("select");
                    shiftOption.id="candidateTimeShiftPref";
                    shiftOption.setAttribute("multiple","multiple");

                    url = "/getAllShift";
                    fn = function (returnedData) {
                        processTimeShift(returnedData);
                    };
                    shiftTitle.appendChild(shiftOption);
                    shiftDetails.appendChild(shiftTitle);
                    tenthproperty.appendChild(shiftDetails);
                    orderList.appendChild(tenthproperty );
                }
                if(url != null) {
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
                if(returnedData.propertyId == 5) {
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
