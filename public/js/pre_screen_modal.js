/*
*    [dependencies]:
*       - bootbox.min.js
*       - validation.js
*
*
* */

var langArray = [];
var currentLocationArray = [];
var localityArray = [];

// use this object to set style, class, glyphicon
var globalPalette = {
    color: {
        main:{
            headerColor: "rgb(63,81,181)"
        }
    }
};
var decorator;
function initDecorator(colorPalette) {
    if(colorPalette == null) {
        colorPalette = globalPalette;
    }
    decorator = {
        preScreen: {
            title: "",
            className: "mdl-grid"
        },
        container : {
            className: "mdl-cell mdl-cell--12-col"
        },
        bootBoxMain: {
            className: "pre-screen-modal"
        },
        table: {
            mainTable: {
                title: "Min Requirement",
                titleStyle: "margin:0;color:rgb(63, 81, 181);padding: 2%;background-color: rgba(255, 255, 255, 0.99);",
                className: "mdl-data-table mdl-js-data-table mdl-shadow--2dp mdl-cell mdl-cell--12-col",
                style: "margin:0;border:none",
                tHead: {
                    style: "background-color:" + colorPalette.color.main.headerColor,
                    titleText_1:"",
                    titleText_2:"Job Post Info",
                    titleText_3:"Candidate Info",
                    titleText_4:"Match?",
                    titleText_5:"Is candidate Ready",
                    titleText_6:"Edit"
                },
                minReqTable: {
                  className: ""
                },
                tBody: {
                    style: ""
                }
            },
            otherTable: {
                title: "Other Requirement",
                titleStyle: "margin: 30px 0 0 0;color:rgb(63, 81, 181);padding: 2%;background-color: rgba(255, 255, 255, 0.99);",
                className: "mdl-data-table mdl-js-data-table mdl-shadow--2dp mdl-cell mdl-cell--12-col",
                style: "margin:0;border:none",
                tHead: {
                    style: "background-color:" + colorPalette.color.main.headerColor,
                    titleText_1:"",
                    titleText_2:"Job Post Info",
                    titleText_3:"Candidate Info",
                    titleText_4:"Match?",
                    titleText_5:"Is candidate Ready",
                    titleText_6:"Edit"
                },
                tBody: {
                    style: ""
                }
            },
            idProofCheckbox: {
                checkboxLabel: {
                    className : "mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect",
                    style: "text-align:center"

                },
                checkboxMatch: {
                    className : "mdl-checkbox__input"
                }
            },
            glyphIconCorrectClass: "glyphicon glyphicon-ok",
            glyphIconWrongClass: "glyphicon glyphicon-remove",
            rowHeading: {
                post: {
                    title: "Job Post Min Req",
                    isRequired: true,
                    style: "padding:1% 2%;background-color:"+colorPalette.color.main.headerColor+";color:#fff"
                },
                note: {
                    title: "Note",
                    isRequired: true,
                    style: "padding:1% 2%;background-color:"+colorPalette.color.main.headerColor+";color:#fff"
                }
            }
        },
        callYesNo: {
            button : {
                className: "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect"
            },
            visibility: true
        },
        radioBtn:{
            className:"mdl-radio__button"
        },
        finalSubmissionButton :{
            className: "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent modal-submit lg-submit-btn",
            enable: false
        },
        textContainers: {
            visibility: true,
            minReqContainer: {
                title: "",
                visibility: true,
                className: "col-lg-6 form-group remove-padding-left"
            },
            noteContainer: {
                title: "",
                visibility: true,
                className: "col-lg-6 form-group remove-padding-right"
            }
        },
        edit:{
          title: "Edit"
        },
        finalSubmissionBypassRequired: false,
        callYesNoRequired: true,
        columnVisible: [1, 2, 3, 4, 5, 6],
        modalFooter: {
                footerMessage: "Did the candidate pass pre-screen?"
        }
    };
    return decorator;
}


// aux methods start
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
    langArray = [];
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
        var minReqTableContainer = $('#document_details');
        var mainTable = document.createElement("table");
        mainTable.className =customD.table.mainTable.className;
        mainTable.style = customD.table.mainTable.style;
        mainTable.id = "documentTable";

        var tHead = document.createElement("thead");
        tHead.style = customD.table.mainTable.tHead.style;
        mainTable.appendChild(tHead);

        var heading = document.createElement("tr");
        tHead.appendChild(heading);

        var title1 = document.createElement("th");
        title1.style="color:#ffffff";
        heading.appendChild(title1);

        var title2 = document.createElement("th");
        title2.textContent = "Document Title";
        title2.style="color:#ffffff";
        heading.appendChild(title2);

        var title3 = document.createElement("th");
        title3.style="color:#ffffff";
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
            checkMatchLabel.className = customD.table.idProofCheckbox.checkboxLabel.className;
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
            idProofTitleTd.id = "idProofName_"+idProof.idProofId;
            bodyContentBox.appendChild(idProofTitleTd);

            var idProofNumberTd = document.createElement("td");
            idProofNumberTd.id = "idProofValueTd";
            bodyContentBox.appendChild(idProofNumberTd);

            var ip = document.createElement("INPUT");
            ip.setAttribute("type", "text");
            ip.setAttribute("id", "idProofValue_"+idProof.idProofId);
            ip.onchange = validateInput;
            idProofNumberTd.appendChild(ip);

        })
    }
}

function validateInput() {
    var id = this.id.split("_")[1];
    // aadhaar validation
    if(id == 3) {
        if(!validateAadhar(this.value)){
            $('.btn.edit-modal-submit').prop('disabled', true);
            notifyModal("Invalid Input","Invalid Aadhaar Card Number. (Example: 100120023003)");
        } else {
            $('.btn.edit-modal-submit').prop('disabled', false);
        }
        $("#idProofCheckbox_"+id).prop('checked', true);
    }
    if(id == 1){
        if(!validateDL(this.value)){
            $('.btn.edit-modal-submit').prop('disabled', true);
            notifyModal("Invalid Input","Invalid Driving Licence Number. (Example: TN7520130008800 or TN-7520130008800)");
        } else {
            $('.btn.edit-modal-submit').prop('disabled', false);
        }
        $("#idProofCheckbox_"+id).prop('checked', true);
    }
    if(id == 2){
        if(!validatePASSPORT(this.value)){
            $('.btn.edit-modal-submit').prop('disabled', true);
            notifyModal("Invalid Input","Invalid Pass Port Number. (Example: A12 34567)");
        } else {
            $('.btn.edit-modal-submit').prop('disabled', false);
        }
        $("#idProofCheckbox_"+id).prop('checked', true);
    }
    if(id == 4){
        if(!validatePAN(this.value)){
            $('.btn.edit-modal-submit').prop('disabled', true);
            notifyModal("Invalid Input","Invalid PAN Card Number. (Example: ABCDE1234Z)");
        } else {
            $('.btn.edit-modal-submit').prop('disabled', false);
        }
        $("#idProofCheckbox_"+id).prop('checked', true);
    }
}
function processLocality(returnedData) {
    localityArray = [];
    if (returnedData != null) {
        returnedData.forEach(function (locality) {
            var id = locality.localityId;
            var name = locality.localityName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            localityArray.push(item);
        });

        if (localityArray != null) {
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

function updateCallAttempts(cId, jpId, status) {
    if(cId == null || jpId == null) {
        return;
    }
    NProgress.start();
    var base_url = "/support/api/updatePreScreenAttempt/";
    $.ajax({
        type: "GET",
        url: base_url +"?candidateId="+cId+"&jobPostId="+jpId+"&callStatus="+status,
        processData: false,
        success: function (returnedData) {
            if(returnedData == true){
                // notifyModal("Call response saved successfully.");
                // setTimeout(function () {
                //     location.reload();
                //     // window.location = response.redirectUrl + app.jpId + "/?view=" + response.nextView;
                // }, 2000);
                bootbox.hideAll();
            } else if(returnedData == false) {
                // notifyModal("Error while saving call response.");
            }
        }
    });
    NProgress.done();

}
function saveAttempt(candidateId, jobPostId) {
    var value = $('#callResponse').val();
    updateCallAttempts(candidateId, jobPostId, value);
    bootbox.hideAll();
}

function onCallYes(candidateId, jobPostId) {
    $('#callNoClass').hide();
    $('#pre_screen_body').show();
    updateCallAttempts(candidateId, jobPostId, "CONNECTED");
    activateSubmit(true);
}

function activateSubmit(data) {
    if(data){
        if($('input:radio[name="verdict"]:checked').val() != null
            && $('input:radio[name="callConnected"]:checked').val() == "yes"){
            $('.btn.modal-submit').prop('disabled', false);
        }
    } else {
        if($('input:radio[name="verdict"]:checked').val() != null){
            $('.btn.modal-submit').prop('disabled', false);
        }
    }

}
function onCallNo(candidateId, jobPostId) {
    $('#callNoClass').show();
    $('#pre_screen_body').hide();
    $('.btn.modal-submit').prop('disabled', true);
}

function triggerPreScreenResponseSubmission(candidateId, jobPostId, isSupport) {

    var allSelectedValues = $("#pre_screen_body input[type='checkbox']:checked");
    var responseList = [];
    var len = allSelectedValues.size();
    for (var j = 0; j < len; j++) {
        var checkbox = allSelectedValues[j];
        var ids = checkbox.id.split("_");
        var subIds = ids[1].split("-");
        var subLen = subIds.length;
        if(subIds.length > 1){
            for (var k = 0; k < subLen; k++) {
                responseList.push(parseInt(subIds[k]));
            }
        } else {
            responseList.push(parseInt(ids[1]));
        }
    }
    var status = false;
    // we shall remove this null check for passing null to db in candidate self prescreen view
    if($('input:radio[name="verdict"]:checked').val() != null){
        status = true;
    }

    if(status) {
        var d = {
            candidateId: candidateId,
            jobPostId: jobPostId,
            preScreenIdList: responseList,
            pass: $('input:radio[name="verdict"]:checked').val() == 1,
            preScreenNote: $('#pre_screen_note').val()
        };

        try {
            $.ajax({
                type: "POST",
                url: "/submitPreScreen",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: function (returnData) {
                    processPostPreScreenResponse(returnData, candidateId, jobPostId, isSupport);
                }
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function saveEditedResponses(candidateId, propId, jobPostId, isSupport) {
    var d;
    var dobCheck = 1;
    var okToSubmit = true; // validation check before submit | { 1 = ok , 0 = not ok }
    if(propId == 0) {
        var documentList = [];
        $('#documentTable tr').each(function() {
            var item = {};
            var id;
            if($(this).attr('id') != undefined) {
                id = $(this).attr('id').split("_").slice(-1).pop();

                if($('input#idProofCheckbox_'+id).is(':checked')) {
                    item["idProofId"] = parseInt(id);
                    // if( $('input#idProofValue_'+id).val() == null ||  $('input#idProofValue_'+id).val().trim() == ""){
                    //     okToSubmit = false;
                    // }
                    item["idNumber"] = $('input#idProofValue_'+id).val().trim();
                }

                if(!jQuery.isEmptyObject(item)){
                    documentList.push(item);
                };
            }
        });
            // documents
            d = {
                idProofWithIdNumberList: documentList
            }

    } else if(propId == 1) {
        var check;
        var languageMap = [];
        var languageKnown = $('#langTable input:checked').map(function () {
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

        // documents
        d = {
            candidateKnownLanguageList: languageMap
        }

    } else if(propId == 2) {
        // asset
        var assetArrayList = $("#assetMultiSelect").val();

        d = {
            assetIdList: assetArrayList
        }

    } else if(propId == 3) {
        // age submission
        var selectedDob = $('#candidateDob').val();
        var c_dob = String(selectedDob);
        var selectedDate = new Date(c_dob);
        var toDate = new Date();
        var pastDate= new Date(toDate.setFullYear(toDate.getFullYear() - 18)); // ex: if current year: 2016 || pastDate: 1998
        toDate =  new Date(); //reset toDate to current Date
        var zombieYear = new Date(toDate.setFullYear(toDate.getFullYear() - 70)); // ex: if current year: 2016  || zombieYear: 1928
        toDate =  new Date(); //reset toDate to current Date
        if (selectedDate >= pastDate) {
            dobCheck = 0;
        }
        if(selectedDate <= zombieYear ) {
            dobCheck = 0;
        }
        d = {
            candidateDob: c_dob
        }
    } else if(propId == 4) {

        /* calculate total experience in months */
        var expMonth = parseInt($('#candidateTotalExperienceMonth').val());
        var expYear = parseInt($('#candidateTotalExperienceYear').val());
        var totalExp = expMonth + (12 * expYear);

        d = {
            candidateTotalExperience: totalExp,
            pastCompanyList: [],
            candidateIsEmployed: null,
            extraDetailAvailable: false
        }

    } else if(propId == 5) {

        d = {
            candidateEducationLevel: $('#candidateHighestEducation').val(),
            candidateDegree: ($('#candidateHighestDegree').val()),
            candidateEducationInstitute: $('#candidateEducationInstitute').val(),
            candidateEducationCompletionStatus: parseInt($('input:radio[name="candidateEducationCompletionStatus"]:checked').val())

        }
    } else if(propId == 6) {

        d = {
            candidateGender: ($('input:radio[name="gender"]:checked').val())
        }

    } else if(propId == 7) {
        var salary = $('#candidateLastWithdrawnSalary').val();
        if(!isNaN(salary) && parseInt(salary) >= 1000 && parseInt(salary) <= 100000){
            d = {
                candidateLastWithdrawnSalary: parseInt($('#candidateLastWithdrawnSalary').val())
            }
        } else {
            notifyModal("Invalid Salary Input","Please enter a valid 'Last Withdrawn Salary' per month. (Min: 1000, Max: 1,00,000)");
            okToSubmit = false;
        }


    } else if(propId == 8) {

        d = {
            candidateHomeLocality: parseInt($('#candidateHomeLocality').val())
        }

    } else if(propId == 9) {

        d = {
            candidateTimeShiftPref: $('#candidateTimeShiftPref').val()
        }
    }

    if (dobCheck == 0) {
        notifyModal("Invalid DOB","Please enter valid date of birth");
        okToSubmit = false;
    } else if ($('#candidateTotalExperienceYear').val() > 40) {
        notifyModal("Invalid Years of Experience","Please enter valid years of experience. (Min: 0, Max: 40)");
        okToSubmit = false;
    }
    //final submission
    if(okToSubmit){
        try {
            $.ajax({
                type: "POST",
                url: "/support/api/updateCandidateDetailsAtPreScreen/?propertyId="+propId+"&candidateId="+candidateId,
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: function (returnedData) {
                    processFinalSubmitResponse(returnedData, jobPostId, candidateId, propId, isSupport);
                }
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
        return true;
    }

    return false;
}

function notifyModal(title, message){
    bootbox.alert({
        size: "small",
        title: title,
        message: message,
        callback: function () { /* your callback code */
        }
    });
}

function processFinalSubmitResponse(returnedData, jobPostId, candidateId, propId, isSupport) {
    if(returnedData != "error" || returnedData.trim() != ""){
        getPreScreenContent(jobPostId, candidateId, true, null, null, isSupport);
        // get new jobPostVsCandidate data
        // construct new pre_screen_body
        // render it $('#pre_screen_body').html("test")
        //$('#candidateValue_'+propId).html(returnedData);
    }
}

function generateEditModalView(title, message, candidateId, propId, overflow, jobPostId, isSupport) {
    var editDialog = bootbox.dialog({
        className: "pre-screen-modal",
        title: title,
        message: message,
        closeButton: true,
        animate: true,
        onEscape: function() {
            $('body').removeClass('open-edit-modal');
        },
        buttons: {
            "Save": {
                className: "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent edit-modal-submit",
                callback: function () {
                    return saveEditedResponses(candidateId, propId, jobPostId, isSupport);
                }
            }
        }
    });
    editDialog.attr("id", "edit-modal");
    if(overflow){
        $('#edit-modal div.modal-body').attr('style', 'overflow: visible !important');
    }
    //$('.btn.edit-modal-submit').prop('disabled', true);
    $('body').removeClass('modal-open').removeClass('open-edit-modal').addClass('open-edit-modal');
}

function fetchEditModalContent(candidateId, propId, jobPostId, customD, isSupport) {

    // api call and render child modal
    var base_api_url ="/support/api/getCandidateDetails/";
    if(base_api_url == null || candidateId == null) {
        return
    } else {
        base_api_url +="?";
        if(candidateId != null) {
            base_api_url += "candidateId=" + candidateId;
        }
        if(jobPostId != null) {
            base_api_url += "&propertyId=" + propId;
        }
    }
    // work_shift
    var url;
    var ajax_type = "POST";
    var fn; // options creator
    var setter = function(returnedData) {
    }; // this sets the values in the prev created options
    var htmlBodyContent = "test";
    var modalTitle="test";
    var isOverFlowRequired = false;


    if(propId == 0) {
        htmlBodyContent = '<div id="document_details">'+
            '<h4 class="mdl-shadow--2dp" style=";margin: 0;color:rgb(63, 81, 181);padding: 2%;background-color: rgba(255, 255, 255, 0.99);">Document Details:</h4>'+
            '</div>';

        modalTitle = "Candidate Document Edit";
        url = "/support/api/getDocumentReqForJobRole/?job_post_id="+jobPostId;
        ajax_type = "GET";
        fn = function (returnedData) {
            processIdProofsWithNumbers(returnedData, customD);
        };
        setter = function (returnedData) {
            if(returnedData!= null) {
                returnedData.forEach(function (doc) {
                    $('#idProofCheckbox_' + doc.idProof.idProofId).prop('checked', true);
                    $('#idProofValue_' + doc.idProof.idProofId).val(doc.idProofNumber);
                });
            }
        }
    } else if(propId == 1) {
        htmlBodyContent = '<div id="language_details">'+

            '<div class="row" style="margin:0;padding:1%;'+customD.table.mainTable.tHead.style+';color:#fff"><b>Language Details :</b></div>'+
            '<table id="langTable" class="mdl-data-table mdl-js-data-table table table-striped mdl-shadow--2dp" cellspacing="0" width="100%">'+
            '<thead>'+
            '</thead>'+
            '<tbody>'+
            '</tbody>'+
            '</table>'+
            '</div>';

        modalTitle = "Candidate Language Edit";
        url = "/getAllLanguage";
        fn = function (returnedData) {
            processLanguage(returnedData);
        };
        setter = function (returnedData) {
            prefillLanguageTable(returnedData);
        }
    } else if(propId == 2) {

        htmlBodyContent ='<div class="row" style="margin:0;padding:1%;'+customD.table.mainTable.tHead.style+';color:#fff"><b>Assets</b></div>'+
            '<div class="row mdl-shadow--2dp" style="margin: 1px;padding: 2% 1%;background-color:#fff">'+
            '<div class="col-lg-3 col-lg-offset-2" style="margin-top: 8px;text-align: right"><font size="3">Asset : </font></div>'+
            '<div class="col-lg-4" id="assetMultiSelectDiv">'+
            '<select id="assetMultiSelect" multiple="multiple"></select>'+
            '</div>'+
            '</div>';

        ajax_type = "GET";
        modalTitle = "Candidate Asset Edit";
        url = "/support/api/getAssetReqForJobRole/?job_post_id="+jobPostId;
        fn = function (returnedData) {
            processDataGetAssets(returnedData);
        };
        setter = function (returnedData) {
            if(returnedData != null) {
                // list of assets
                var assetIdList = [];
                returnedData.forEach(function (candidateAsset) {
                    assetIdList.push(candidateAsset.asset.assetId);
                });
                $("#assetMultiSelect").val(assetIdList);
                $("#assetMultiSelect").multiselect('rebuild');
            }
        };
        isOverFlowRequired = true;

    } else if(propId == 3) {
        htmlBodyContent = '<div class="row" style="margin:0;padding:1%;'+customD.table.mainTable.tHead.style+';color:#fff"><b>Date Of Birth</b></div>'+
            '<div class="row mdl-shadow--2dp" style="margin: 1px;padding: 2% 1%;background-color:#fff">'+
            '<div class="col-lg-3 col-lg-offset-2" style="margin-top: 8px;text-align: right"><font size="3">DOB : </font></div>'+
            '<div class="col-lg-4">'+
            '<input id="candidateDob" name="candidateDob" placeholder="When is your Birthday?" type="date" class="form-control input-md"  data-date-inline-picker="true"/>'+
            '</div>'+
            '</div>';
        $( "#candidateDob").datepicker({ dateFormat: 'yy-mm-dd', changeYear: true});
        modalTitle = "Candidate DOB Edit";
        setter = function (returnedData) {
            if (returnedData != null && returnedData.length>0) {
                var date = JSON.parse(returnedData);
                var yr = new Date(date).getFullYear();
                var month = ('0' + parseInt(new Date(date).getMonth() + 1)).slice(-2);
                var d = ('0' + new Date(date).getDate()).slice(-2);
                $("#candidateDob").val(yr + "-" + month + "-" + d);
            }
        }
    } else if(propId == 4) {
        htmlBodyContent = '<div class="row">'+
            '<div class="col-sm-3"><h5>Total Experience:</h5></div>'+
            '<div class="col-sm-9">'+
            '<input id="candidateTotalExperienceYear" value="0" style="width: 15%; display: inline-block" placeholder="Years" type="number" class="form-control input-md">&nbsp;Years&nbsp;&nbsp;'+
            '<input id="candidateTotalExperienceMonth" value="0" style="width: 15%; display: inline-block" placeholder="Months" type="number" min="0" max="11" step="1"  class="form-control input-md">&nbsp; Months'+
            '</div>'+
            '</div>';

        modalTitle = "Candidate Experience Edit";
        fn = function (returnedData) {
            processExperience(returnedData);
        };
        setter = function (returnedData) {
            if(returnedData != null) {
                $("#candidateTotalExperienceYear").val(parseInt((returnedData / 12)).toString()); // years
                $("#candidateTotalExperienceMonth").val(returnedData % 12); // month
            }
        }
    } else if(propId == 5) {
        htmlBodyContent =  ''+
            '<div class="row" style="margin:0;padding:1%;'+customD.table.mainTable.tHead.style+';color:#fff"><b>Educational Details</b></div>'+
                '<div class="row mdl-shadow--2dp" id="education_details" style="margin: 1px;padding: 2% 1%;background-color:#fff">'+
                    '<div class="row">'+
                        '<div class="col-lg-5" style="margin-top: 8px;text-align: right"><font size="2">Highest Education Qualification : </font></div>'+
                        '<div class="col-lg-4">'+
                            '<select id="candidateHighestEducation" size="5"></select>'+
                        '</div>'+
                    '</div>'+
                    '<div class="row" style="margin-top: 10px;">'+
                        '<div class="col-lg-5" style="margin-top: 8px;text-align: right"><font size="2">Have you successfully completed this course?</font></div>'+
                        '<div class="col-lg-4" style="padding-top: 8px;">'+
                            '<input type="radio" name="candidateEducationCompletionStatus" id="eduCompleted" '+'value="1">&nbsp;Yes&nbsp;&nbsp;'+
                            '<input type="radio" name="candidateEducationCompletionStatus" id="eduCompletedNot" value="0">&nbsp;No'+
                        '</div>'+
                    '</div>'+
                    '<div class="row" id="educationalInstitute" style="margin-top: 10px;" >'+
                        '<div class="col-lg-5" style="margin-top: 8px;text-align: right"><font size="2">Highest Education Degree? </font></div>'+
                        '<div class="col-lg-4" style="padding-top: 8px;">'+
                            '<select id="candidateHighestDegree" size="5"></select>'+
                        '</div>'+
                    '</div>'+
                    '<div class="row" style="margin-top: 10px">'+
                        '<div class="col-lg-5" style="margin-top: 8px;text-align: right"><font size="2">Last attended Education Institute? </font></div>'+
                        '<div class="col-lg-4">'+
                            '<input id="candidateEducationInstitute" style="width: 326px" placeholder="Which was the last college you went to?" type="text" class="form-control input-md">'+
                        '</div>'+
                    '</div>'+
                '</div>'+
            '</div>';

        modalTitle = "Candidate Education Edit";
        isOverFlowRequired = true;
        url = "/getAllEducation";
        fn = function (returnedData) {
            processEducation(returnedData);
        };
        setter = function (returnedData) {
            if(returnedData != null) {
                if(returnedData.education != null) {
                    $("#candidateHighestEducation").val(returnedData.education.educationId);
                    $("#candidateHighestEducation").multiselect('rebuild');
                }

                if(returnedData.degree != null) {
                    $("#candidateHighestDegree").val(returnedData.degree.degreeId);
                    $("#candidateHighestDegree").multiselect('rebuild');
                }

                if(returnedData.candidateLastInstitute != null) {
                    $("#candidateEducationInstitute").val(returnedData.candidateLastInstitute);
                }

                if (returnedData.candidateEducationCompletionStatus == '1') {
                    // hasCompletedEducation
                    $('input[id=eduCompleted]').attr('checked', true);
                } else {
                    $('input[id=eduCompletedNot]').attr('checked', true);
                }
            }
        }
    } else if(propId == 6) {
        htmlBodyContent = '<div class="row" style="margin:0;padding:1%;'+customD.table.mainTable.tHead.style+';color:#fff"><b>Candidate Gender</b></div>'+
            '<div class="row mdl-shadow--2dp" id="education_details" style="margin: 1px;padding: 2% 1%;background-color:#fff">'+
            '<div class="col-lg-5" style="margin-top: 8px;text-align: right"><font size="3">Candidate Gender : </font></div>'+
            '<div class="col-lg-4" style="margin-top: 8px;">'+
            '<input type="radio" name="gender" id="genderMale" value="0">&nbsp;Male&nbsp;&nbsp;'+
            '<input type="radio" name="gender" id="genderFemale" value="1">&nbsp;Female'+
            '</div>'+
            '</div>';

        modalTitle = "Candidate Gender Edit";
        setter = function (returnedData) {
            if (returnedData != null) {
                if (returnedData == 0) {
                    $('input[id=genderMale]').attr('checked', true);
                } else {
                    $('input[id=genderFemale]').attr('checked', true);
                }
            }
        }
    } else if(propId == 7) {
        htmlBodyContent = '<div class="row" style="margin:0;padding:1%;'+customD.table.mainTable.tHead.style+';color:#fff"><b>Salary Details</b></div>'+
            '<div class="row mdl-shadow--2dp" id="education_details" style="margin: 1px;padding: 2% 1%;background-color:#fff">'+
            '<div class="col-lg-5" style="margin-top: 8px;text-align: right"><font size="3">Current/Last Drawn Salary : </font></div>'+
            '<div class="col-lg-4">'+
            '<input id="candidateLastWithdrawnSalary" placeholder="What was your LastWithdrawn Salary?" type="number" class="form-control input-md">'+
            '</div>'+
            '</div>';
        modalTitle = "Candidate Last Withdrawn Salary Edit";
        setter = function (returnedData) {
            if (returnedData != null) {
                $('#candidateLastWithdrawnSalary').val(returnedData);
            }
        }
    } else if(propId == 8) {
        htmlBodyContent = '<div class="row" style="margin:0;padding:1%;'+customD.table.mainTable.tHead.style+';color:#fff"><b>Current Location</b></div>'+
        '<div class="row mdl-shadow--2dp" id="education_details" style="margin: 1px;padding: 2% 1%;background-color:#fff">'+
        '<div class="col-lg-5" style="margin-top: 8px;text-align: right"><font size="3">Current Location : </font></div>'+
        '<div class="col-lg-4">'+
        '<input type="text" id="candidateHomeLocality">'+
        '</div>'+
        '</div>';

        modalTitle = "Candidate Home Locality Edit";
        url = "/getAllLocality";
        isOverFlowRequired = true;
        fn = function (returnedData) {
            processLocality(returnedData);

        };
        setter = function (returnedData) {

            if(returnedData!= null) {
                try {
                    var item = {};
                    item ["id"] = returnedData.localityId;
                    item ["name"] = returnedData.localityName;
                    currentLocationArray.push(item); // TODO remove this line
                    $("#candidateHomeLocality").tokenInput('add', item);
                } catch (err) {
                    console.log("homeLocality" + err.stack);
                }
            }
        }

    } else if(propId == 9) {
        htmlBodyContent =  '<div class="row" style="margin:0;padding:1%;'+customD.table.mainTable.tHead.style+';color:#fff"><b>Job TimeShift Preferences</b></div>'+
            '<div class="row mdl-shadow--2dp" id="education_details" style="margin: 1px;padding: 2% 1%;background-color:#fff">'+
            '<div class="col-lg-5" style="margin-top: 8px;text-align: right"><font size="3">Preferred work shift? </font></div>'+
            '<div class="col-lg-4">'+
            '<select id="candidateTimeShiftPref" size="5"></select>'+
            '</div>'+
            '</div>'+
            '</div>';

        modalTitle = "Candidate Work-Shift Edit";
        url = "/getAllShift";
        isOverFlowRequired = true;
        fn = function (returnedData) {
            processTimeShift(returnedData);
        };
        setter = function (returnedData) {
            if(returnedData != null) {
                $("#candidateTimeShiftPref").val(returnedData.timeShift.timeShiftId);
                $("#candidateTimeShiftPref").multiselect('rebuild');
            }
        }
    }

    // generates html container
    generateEditModalView(modalTitle, htmlBodyContent, candidateId, propId, isOverFlowRequired, jobPostId, isSupport);

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
    if(propId == 5) {
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

    try {
        $.ajax({
            type: "GET",
            url: base_api_url,
            data: false,
            contentType: false,
            processData: false,
            success: setter
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
}

function constructPreScreenBodyContainer(returnedData, customD, isSupport) {
    var candidateId = returnedData.candidateId;
    var jobPostId = returnedData.jobPostId;
    var container = $('<div class="'+customD.container.className+'" id="pre_screen_container_row"></div>');

    var minReqTableContainer = $('<div id="minReqTable" class="'+customD.table.mainTable.minReqTable.className+'"></div>');
    container.append('<h4 class="mdl-shadow--2dp" data-toggle="collapse" href="#minReqTable"  style="'+customD.table.mainTable.titleStyle+'">'+customD.table.mainTable.title+'<i class="material-icons pull-right">keyboard_arrow_down</i></h4>');
    container.append(minReqTableContainer);

    var otherReqTableContainer = $('<div id="otherReqTable" class="'+customD.table.mainTable.minReqTable.className+'"></div>');
    container.append('<h4 class="mdl-shadow--2dp" data-toggle="collapse" href="#otherReqTable" style="'+customD.table.otherTable.titleStyle+'">'+customD.table.otherTable.title+'<i class="material-icons pull-right">keyboard_arrow_down</i></h4>');
    container.append(otherReqTableContainer);

    // minReqTable
    var mainTable = document.createElement("table");
    mainTable.className = customD.table.mainTable.className;
    mainTable.style = customD.table.mainTable.style;

    var tHead = document.createElement("thead");
    tHead.style = customD.table.mainTable.tHead.style;
    mainTable.appendChild(tHead);

    var heading = document.createElement("tr");
    tHead.appendChild(heading);

    if ($.inArray(1, customD.columnVisible) > -1) {
        var title1 = document.createElement("th");
        title1.textContent = customD.table.mainTable.tHead.titleText_1;
        heading.appendChild(title1);
    }
    if ($.inArray(2, customD.columnVisible) > -1) {
        var title2 = document.createElement("th");
        title2.textContent = customD.table.mainTable.tHead.titleText_2;
        title2.style="color:#ffffff";
        heading.appendChild(title2);
    }
    if ($.inArray(3, customD.columnVisible) > -1) {
        var title3 = document.createElement("th");
        title3.style="color:#ffffff";
        title3.textContent = customD.table.mainTable.tHead.titleText_3;
        heading.appendChild(title3);
    }
    if ($.inArray(4, customD.columnVisible) > -1) {
        // is a match or not
        var title4 = document.createElement("th");
        title4.textContent = customD.table.mainTable.tHead.titleText_4;
        title4.style="color:#ffffff";
        heading.appendChild(title4);
    }
    if ($.inArray(5, customD.columnVisible) > -1) {
        var title5 = document.createElement("th");
        title5.style="color:#ffffff";
        title5.textContent = customD.table.mainTable.tHead.titleText_5;
        heading.appendChild(title5);
    }
    if ($.inArray(6, customD.columnVisible) > -1) {
        var title6 = document.createElement("th");
        title6.style="color:#ffffff";
        title6.textContent = customD.table.mainTable.tHead.titleText_6;
        heading.appendChild(title6);
    }

    var tBody = document.createElement("tbody");
    mainTable.appendChild(tBody);

    minReqTableContainer.append(mainTable);

    //otherTable
    var otherTable = document.createElement("table");
    otherTable.className = customD.table.otherTable.className;
    otherTable.style = customD.table.otherTable.style;

    var tHead = document.createElement("thead");
    tHead.style = customD.table.otherTable.tHead.style;
    otherTable.appendChild(tHead);

    var heading = document.createElement("tr");
    tHead.appendChild(heading);

    if ($.inArray(1, customD.columnVisible) > -1) {
        var title1 = document.createElement("th");
        title1.textContent = customD.table.otherTable.tHead.titleText_1;
        heading.appendChild(title1);

    }
    if ($.inArray(2, customD.columnVisible) > -1) {
        var title2 = document.createElement("th");
        title2.style="color:#ffffff";
        title2.textContent =  customD.table.otherTable.tHead.titleText_2;
        heading.appendChild(title2);
    }
    if ($.inArray(3, customD.columnVisible) > -1) {
        var title3 = document.createElement("th");
        title3.style="color:#ffffff";
        title3.textContent = customD.table.otherTable.tHead.titleText_3;
        heading.appendChild(title3);
    }
    if ($.inArray(4, customD.columnVisible) > -1) {
        // is a match or not
        var title4 = document.createElement("th");
        title4.style="color:#ffffff";
        title4.textContent = customD.table.otherTable.tHead.titleText_4;
        heading.appendChild(title4);

    }
    if ($.inArray(5, customD.columnVisible) > -1) {
        var title5 = document.createElement("th");
        title5.style="color:#ffffff";
        title5.textContent = customD.table.otherTable.tHead.titleText_5;
        heading.appendChild(title5);

    }
    if ($.inArray(6, customD.columnVisible) > -1) {
        var title6 = document.createElement("th");
        title6.style="color:#ffffff";
        title6.textContent = customD.table.otherTable.tHead.titleText_6;
        heading.appendChild(title6);
    }

    var other_tBody = document.createElement("tbody");
    otherTable.appendChild(other_tBody);

    otherReqTableContainer.append(otherTable);


    var splitDiv = $('<div class="mdl-grid--no-spacing " style="margin-top: 20px"></div>');

    if(customD.textContainers.visibility){
        if(customD.textContainers.minReqContainer.visibility){
            // minReq start
            var minReqContainer = document.createElement("div");
            minReqContainer.className = customD.textContainers.minReqContainer.className;
            minReqContainer.id = "minReqContainer";
            minReqContainer.style = "margin:0";

            var minReqTextArea = document.createElement("textarea");
            minReqTextArea.className = "form-control mdl-shadow--2dp";
            minReqTextArea.rows = "5";
            minReqTextArea.type = "text";
            minReqTextArea.style = "border-radius:0;";
            minReqTextArea.id = "job_post_min_req";
            minReqTextArea.disabled = true;

            var data ;
            if(returnedData.jobPostMinReq != null && returnedData.jobPostMinReq != "") {
                data = returnedData.jobPostMinReq;
            } else {
                data = "NA"
            }
            minReqTextArea.textContent = data;

            var rowHeadingPost = document.createElement("div");
            rowHeadingPost.style =  customD.table.rowHeading.post.style;
            minReqContainer.appendChild(rowHeadingPost);

            var label = document.createElement("label");
            label.for= "job_post_min_req";
            label.style = "margin:0";
            label.textContent = customD.textContainers.minReqContainer.title;
            rowHeadingPost.appendChild(label);
            minReqContainer.appendChild(minReqTextArea);
            splitDiv.append(minReqContainer);
            // minReq end
        }
        if(customD.textContainers.noteContainer.visibility){
            // note start
            var noteContainer = document.createElement("div");
            noteContainer.className = customD.textContainers.noteContainer.className;
            noteContainer.id = "noteContainer";
            var textarea = document.createElement("textarea");
            textarea.className = "form-control mdl-shadow--2dp";
            textarea.style = "border-radius:0;";
            textarea.rows = "5";
            textarea.type = "text";
            textarea.placeholder = "Extra Comment";
            textarea.id = "pre_screen_note";

            var rowHeadingNote = document.createElement("div");
            rowHeadingNote.style = customD.table.rowHeading.note.style;
            noteContainer.appendChild(rowHeadingNote);

            var label = document.createElement("label");
            label.for= "pre_screen_note";
            label.textContent =  customD.textContainers.noteContainer.title;
            label.style = "margin :0";
            rowHeadingNote.appendChild(label);
            noteContainer.appendChild(textarea);
            splitDiv.append(noteContainer);
            // note end
        }
        container.append(splitDiv);
    }


    var elementList = returnedData.elementList;
    elementList.forEach(function (rowData) {
        if(rowData!=null) {
            if(rowData.isMinReq) {
                var bodyContentBox = document.createElement("tr");
                bodyContentBox.id = rowData.propertyId;
                tBody.appendChild(bodyContentBox);

                if ($.inArray(1, customD.columnVisible) > -1) {
                    var bodyContentData1 = document.createElement("td");
                    bodyContentData1.style = ("font-weight:600");
                    bodyContentData1.textContent = rowData.propertyTitle;
                    bodyContentBox.appendChild(bodyContentData1);
                }
                if ($.inArray(2, customD.columnVisible) > -1) {
                    var bodyContentData2 = document.createElement("td");
                    bodyContentData2.id = "jobPostValue_"+rowData.propertyId;
                    bodyContentBox.appendChild(bodyContentData2);

                    bodyContentData2.textContent = rowData.jobPostPlaceHolder;
                    // if(rowData.isSingleEntity){
                    //     bodyContentData2.textContent = getPlaceholderValue(rowData.jobPostElement);
                    // } else {
                    //     bodyContentData2.textContent = getPlaceholderArray(rowData.jobPostElementList);
                    // }
                }
                if ($.inArray(3, customD.columnVisible) > -1) {
                    var bodyContentData3 = document.createElement("td");
                    bodyContentData3.id = "candidateValue_"+rowData.propertyId;
                    bodyContentBox.appendChild(bodyContentData3);

                    bodyContentData3.textContent = rowData.candidatePlaceHolder;

                    // if(rowData.isSingleEntity){
                    //     bodyContentData3.textContent = getPlaceholderValue(rowData.candidateElement);
                    // } else {
                    //     bodyContentData3.textContent = getPlaceholderArray(rowData.candidateElementList);
                    // }
                }
                if ($.inArray(4, customD.columnVisible) > -1) {
                    var spanTd = document.createElement("td");
                    var indicatorSpan = document.createElement("span");
                    if(rowData.isMatching){
                        indicatorSpan.setAttribute('class', customD.table.glyphIconCorrectClass);
                    } else {
                        indicatorSpan.setAttribute('class', customD.table.glyphIconWrongClass);
                    }
                    spanTd.appendChild(indicatorSpan);
                    bodyContentBox.appendChild(spanTd);

                }

                if ($.inArray(5, customD.columnVisible) > -1) {
                    var bodyContentData5 = document.createElement("td");
                    bodyContentBox.appendChild(bodyContentData5);

                    var checkMatchLabel = document.createElement("label");
                    checkMatchLabel.type = "checkbox";
                    checkMatchLabel.id = "ready_checkbox_" + rowData.propertyId;
                    checkMatchLabel.for = "checkbox_" + rowData.propertyIdList.join("-");
                    checkMatchLabel.style = 'text-align:center';
                    checkMatchLabel.className = customD.table.idProofCheckbox.checkboxLabel.className;
                    bodyContentData5.appendChild(checkMatchLabel);

                    var checkMatch = document.createElement("input");
                    checkMatch.type = "checkbox";
                    checkMatch.id = "checkbox_" + rowData.propertyIdList.join("-");
                    checkMatch.className = customD.table.idProofCheckbox.checkboxMatch.className;
                    checkMatchLabel.appendChild(checkMatch);
                }

                if ($.inArray(6, customD.columnVisible) > -1) {
                    // edit href
                    var editLink = document.createElement("td");
                    var a = document.createElement('a');
                    if(!((rowData.propertyId == "3") && (bodyContentData3.textContent.length > 0))){
                        var linkText = document.createTextNode(customD.edit.title);
                        a.appendChild(linkText);
                        a.style = "cursor: pointer";
                        a.onclick = function () {
                            fetchEditModalContent(candidateId, rowData.propertyId, jobPostId, customD, isSupport);
                        };
                    }
                    editLink.appendChild(a);
                    bodyContentBox.appendChild(editLink);
                }
            } else {

                var bodyContentBox = document.createElement("tr");
                bodyContentBox.id = rowData.propertyId;
                other_tBody.appendChild(bodyContentBox);

                if ($.inArray(1, customD.columnVisible) > -1) {
                    var bodyContentData1 = document.createElement("td");
                    bodyContentData1.style = ("font-weight:600");
                    bodyContentData1.textContent = rowData.propertyTitle;
                    bodyContentBox.appendChild(bodyContentData1);
                }
                if ($.inArray(2, customD.columnVisible) > -1) {
                    var bodyContentData2 = document.createElement("td");
                    bodyContentData2.id = "jobPostValue_"+rowData.propertyId;
                    bodyContentBox.appendChild(bodyContentData2);

                    bodyContentData2.textContent = rowData.jobPostPlaceHolder;

                    // if(rowData.isSingleEntity){
                    //     bodyContentData2.textContent = getPlaceholderValue(rowData.jobPostElement);
                    // } else {
                    //     bodyContentData2.textContent = getPlaceholderArray(rowData.jobPostElementList);
                    // }
                }
                if ($.inArray(3, customD.columnVisible) > -1) {
                    var bodyContentData3 = document.createElement("td");
                    bodyContentData3.id = "candidateValue_"+rowData.propertyId;
                    bodyContentBox.appendChild(bodyContentData3);

                    bodyContentData3.textContent = rowData.candidatePlaceHolder;

                    // if(rowData.isSingleEntity){
                    //     bodyContentData3.textContent = getPlaceholderValue(rowData.candidateElement);
                    // } else {
                    //     bodyContentData3.textContent = getPlaceholderArray(rowData.candidateElementList);
                    // }
                }
                if ($.inArray(4, customD.columnVisible) > -1) {
                    var spanTd = document.createElement("td");
                    var indicatorSpan = document.createElement("span");
                    if(rowData.isMatching){
                        indicatorSpan.setAttribute('class', customD.table.glyphIconCorrectClass);
                    } else {
                        indicatorSpan.setAttribute('class', customD.table.glyphIconWrongClass);
                    }
                    spanTd.appendChild(indicatorSpan);
                    bodyContentBox.appendChild(spanTd);
                }
                if ($.inArray(5, customD.columnVisible) > -1) {
                    var bodyContentData5 = document.createElement("td");
                    bodyContentBox.appendChild(bodyContentData5);

                    var checkMatchLabel = document.createElement("label");
                    checkMatchLabel.type = "checkbox";
                    checkMatchLabel.for = "checkbox_" + rowData.propertyIdList.join("-");
                    checkMatchLabel.style = 'text-align:center';
                    checkMatchLabel.className = customD.table.idProofCheckbox.checkboxLabel.className;
                    bodyContentData5.appendChild(checkMatchLabel);

                    var checkMatch = document.createElement("input");
                    checkMatch.type = "checkbox";
                    checkMatch.id = "checkbox_" + rowData.propertyIdList.join("-");
                    checkMatch.className = customD.table.idProofCheckbox.checkboxMatch.className;
                    checkMatchLabel.appendChild(checkMatch);
                }
                if ($.inArray(6, customD.columnVisible) > -1) {
                    var editLink = document.createElement("td");
                    var a = document.createElement('a');
                    // edit href
                    if(!(rowData.propertyId == "6" && bodyContentData3.textContent.length > 0)){
                        var linkText = document.createTextNode(customD.edit.title);
                        a.appendChild(linkText);
                        a.style = "cursor: pointer";
                        a.title = customD.edit.title;
                        a.onclick = function () {
                            fetchEditModalContent(candidateId, rowData.propertyId, jobPostId, customD, isSupport);
                        };
                    }
                    editLink.appendChild(a);
                    bodyContentBox.appendChild(editLink);
                }
            }
        }
    });

    return container;
}
//
// function getPlaceholderArray(elementList){
//     var arr = [];
//     if(elementList != null) {
//         elementList.forEach(function (customObject) {
//             if(customObject != null){
//                 arr.push(customObject.placeHolder);
//             }
//         })
//     }
//     return arr;
// }

function getPlaceholderValue(element){
    if(element != null) {
        return element.placeHolder;
    } else {
        return "";
    }
}



// customD : custom Decorator object
function processPreScreenContent(returnedData, customD, isSupport) {
    if(returnedData == null || returnedData.status != "SUCCESS") {
        if (returnedData != null && returnedData.status == "INVALID") {
            notifyModal("Pre Screen Status: Completed", "Pre Screen Already Completed");
        } else {
            notifyModal("Error","Request failed. Something went Wrong! Please Refresh");
        }
        return;
    }
    if(returnedData != null && !returnedData.visible && !isSupport){
        notifyError("Please complete Job Application form", 'success');
        bootbox.hideAll();
        initInterviewModal(returnedData.candidateId, returnedData.jobPostId, false);
        return;
    }

    if(returnedData != null){
        if(returnedData.elementList.length == 0){
            bootbox.hideAll();
            initInterviewModal(returnedData.candidateId, returnedData.jobPostId, false);
            return;
        }
        // if(returnedData == "OK" || returnedData == "NA" ) {
        //     processPostPreScreenResponse(returnedData);
        //     return returnedData;
        // }
        // if($(".pre-screen-modal").size() > 0){
        //     return returnedData;
        // }
        var candidateId = returnedData.candidateId;
        var jobPostId = returnedData.jobPostId;

        var preScreenBody = $('<div id="pre_screen_body" class="'+customD.container.className+'"></div>');
        var container = constructPreScreenBodyContainer(returnedData, customD, isSupport);

        preScreenBody.append(container);

        var titleMessage;
        if(customD.callYesNoRequired){
            titleMessage = $('' +
                '<div class="row">'+
                '<div class="col-sm-6">'+
                '<h5 id="callConfirmation" style="margin:2px">Call Connected? :&nbsp; ' +
                '<input type="radio" name="callConnected" id="callYes" value="yes" onclick="onCallYes('+candidateId+', '+jobPostId+')">&nbsp;Yes&nbsp; ' +
                '<input type="radio" name="callConnected" id="callNo" value="no"  onclick="onCallNo('+candidateId+', '+jobPostId+')">&nbsp;No ' +
                '<div id="callNoClass" style="display: none;">' +
                '<h6>Reason?:' +
                '<select id="callResponse" class="selectDropdown" style="margin: 0 8px;" >' +
                '<option value="busy">Busy</option>' +
                '<option value="not_reachable">Not Reachable</option>' +
                '<option value="not_answering">Not Answering</option>' +
                '<option value="switched_off">Switched Off</option>' +
                '<option value="dnd">DND</option>' +
                '<option value="third_person">Third Person</option>' +
                '<option value="others">Others</option>' +
                '</select>' +
                '<button type="submit" id="responseSaveBtn"  class="'+customD.callYesNo.button.className+'" onclick="saveAttempt('+candidateId+', '+jobPostId+')">Save</button>' +
                '</h6>' +
                '</div>' +
                '</h5>'+
                '</div>'+
                '</div>');
        } else {
            titleMessage = customD.preScreen.title;
        }

        renderParentModal(preScreenBody, titleMessage, jobPostId, candidateId, customD, isSupport);
    }
}

function renderParentModal(preScreenBody, callYesNo, jobPostId, candidateId, customD, isSupport) {
    var bootbox_dialog = bootbox.dialog({
        className: customD.bootBoxMain.className,
        title: callYesNo,
        message: preScreenBody,
        closeButton: true,
        animate: true,
        onEscape: function () {
            $('body').removeClass('open-modal');
        },
        buttons: {
            "Next": {
                className: customD.finalSubmissionButton.className,
                callback: function () {
                    if ($("#pre_screen_body input[type='checkbox']:checked").size() == 0
                        && $('input:radio[name="verdict"]:checked').val() == null) {
                        bootbox.alert({
                            size: "small",
                            title: "Invalid Submission !",
                            message: "If you want to mark the candidate as  'completed_prescreening' irrespective of given criteria, select checkbox present next to submit button, in previous dialog.",
                            callback: function () { /* your callback code */
                            }
                        });
                        return false;
                    } else {
                        $('body').removeClass('open-modal');
                        //if($("#pre_screen_body input[type='checkbox']:checked").size() > 0) {}
                        triggerPreScreenResponseSubmission(candidateId, jobPostId, isSupport);
                        return true;
                    }
                }
            }
        }
    });
    bootbox_dialog.init(function () {
        var forceSetContainer = $('.modal-footer');
        var forceSetDiv = $('' +
            '<div class="col-xs-6" style="text-align: left;padding-top:10px">' +
            '<h5 style="margin:2px; font-size: 12px;">' +
            '<div style="display:inline-block; margin: 0 1px;text-align: left; color: #b9151b" id="footerMessage">*</div>' +
            ''+customD.modalFooter.footerMessage+'&nbsp;:&nbsp;' +
            '<div style="display: inline-block; vertical-align: middle; margin: 0px;">' +
            '<label class="mdl-radio mdl-js-radio mdl-js-ripple-effect" for="pass">'+
            '<input class="'+customD.radioBtn.className+'" type="radio" name="verdict" id="pass" value="1" style="margin: 0 2px" onclick="activateSubmit('+customD.callYesNoRequired+')" checked>' +
            '<span class="mdl-radio__label">Yes</span></label>'+
            '<label class="mdl-radio mdl-js-radio mdl-js-ripple-effect" for="fail">'+
            '<input class="'+customD.radioBtn.className+'" type="radio" name="verdict" id="fail" value="0" style="margin: 0 2px" onclick="activateSubmit('+customD.callYesNoRequired+')">' +
            '<span class="mdl-radio__label">No</span></label>'+
            '</div>' +
            '</h5>' +
            '</div>'
        );
        forceSetContainer.prepend(forceSetDiv);

        $('.btn.modal-submit').prop('disabled', !customD.finalSubmissionButton.enable);

        if(!customD.callYesNoRequired){
            $('#pre_screen_body').show();
        } else {
            $('#pre_screen_body').hide();
        }
        $('body').removeClass('modal-open').removeClass('open-modal').addClass('open-modal');
    });
}

function processPostPreScreenResponse(response, candidateId, jobPostId, isSupport) {
    if(response.status == INTERVIEW_NOT_REQUIRED){
        notifyError("Submitted successfully.", 'success');
        setTimeout(function () {
            location.reload();
        }, 2000);
    } else if(response.status == INTERVIEW_REQUIRED){
        notifyError("Submitted successfully. Please select Interview Slot.", 'success');
        initInterviewModal(candidateId, jobPostId, isSupport);
    } else {
        notifyError("Error! Something Went wrong please try again.", 'danger')
    }
}

function reProcessPreScreenContent(returnedData, customD, isSupport){
    if(returnedData == null || returnedData.status != "SUCCESS") {
        pushToSnackbar("Request failed. Something went Wrong! Please Refresh");
    }
    if(returnedData != null){
        var container = constructPreScreenBodyContainer(returnedData, customD, isSupport);
        var allSelectedCheckboxIdArray = $("#pre_screen_body input[type='checkbox']:checked").parent();
        var tempList = [];
        var len = allSelectedCheckboxIdArray.size();
        for (var j = 0; j < len; j++) {
            var checkbox = allSelectedCheckboxIdArray[j].id;
            var id = checkbox.split("_")[2];
            tempList.push(parseInt(id));
        }
        $('#pre_screen_body').html(container);
        for (var j = 0; j < len; j++) {
            var id = tempList[j];
            $('#ready_checkbox_'+id).children().prop('checked', true);
        }

        // re-render previously checked checkboxes
    }
}

function getPreScreenContent(jobPostId, candidateId, isRebound, customD, rePreScreen, isSupport) {
    if(customD == null ) {
        isSupport = true;
        if(decorator != null){
            customD = decorator;
        } else {
            customD = initDecorator(null);
        }
    }
    var base_api_url ="/support/api/getJobPostVsCandidate/";
    if(base_api_url == null || jobPostId == null) {
        return
    } else {
        base_api_url +="?";
        if(jobPostId != null) {
            base_api_url += "jobPostId=" + jobPostId;
        }
        if(candidateId != null){
            base_api_url += "&candidateId=" + candidateId;
        }
    }
    if(rePreScreen == null || !rePreScreen) {
        base_api_url +="&rePreScreen="+false;
    } else {
        base_api_url +="&rePreScreen="+true;
    }
    base_api_url +="&candidateMobile";

    var processor;
    if(!isRebound) {
        processor = function (returnedData) {
            processPreScreenContent(returnedData, customD, isSupport);
        }
    } else {
        processor = function (returnedData) {
            reProcessPreScreenContent(returnedData, customD, isSupport);
        }
    }
    try {
        $.ajax({
            type: "GET",
            url: base_api_url,
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processor
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
}

function pushToSnackbar(msg) {
    'use strict';
    var snackbarContainer = document.querySelector('#tru-snackbar');

    var data = {
        message: JSON.stringify(msg),
        timeout: 4000
    };

    snackbarContainer.MaterialSnackbar.showSnackbar(data);

}

function notifyError(msg, type) {
    $.notify({
        message: msg,
        animate: {
            enter: 'animated lightSpeedIn',
            exit: 'animated lightSpeedOut'
        }
    }, {
        type: type,
        placement: {
            from: "top",
            align: "center"
        }
    });
};

