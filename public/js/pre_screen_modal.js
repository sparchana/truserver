var languageArray = [];
var currentLocationArray = [];
var localityArray = [];

function updateCallAttempts(cId, jpId, status) {
    if(cId == null || jpId == null) {
        console.log("invalid candidateId and jobPostId");
        return;
    }
    NProgress.start();
    var base_url = "/support/api/updatePreScreenAttempt/";
    $.ajax({
        type: "GET",
        url: base_url +"?candidateId="+cId+"&jobPostId="+jpId+"&callStatus="+status,
        processData: false,
        success: function (returnedData) {
            if(returnedData == "OK"){
                notifyError("Call response saved successfully. Refreshing..", 'success');
                setTimeout(function () {
                    location.reload();
                    // window.location = response.redirectUrl + app.jpId + "/?view=" + response.nextView;
                }, 2000);
                bootbox.hideAll();
            } else if(returnedData == "NA") {
                notifyError("Error while saving call response.", 'success');
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
    console.log("connected: " + candidateId +" "+ jobPostId);
    $('#callNoClass').hide();
    $('#pre_screen_body').show();
    updateCallAttempts(candidateId, jobPostId, "CONNECTED");
    activateSubmit();
}

function activateSubmit() {
    if($('input:radio[name="verdict"]:checked').val() != null
    && $('input:radio[name="callConnected"]:checked').val() == "yes"){
        $('.btn.modal-submit').prop('disabled', false);
    }
}
function onCallNo(candidateId, jobPostId) {
    $('#callNoClass').show();
    $('#pre_screen_body').hide();
    $('.btn.modal-submit').prop('disabled', true);
}

function triggerPreScreenResponseSubmission(candidateId, jobPostId) {

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
                success: processPostPreScreenResponse
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function generateEditModalView(title, message, candidateId, propId, overflow) {
    console.log("rendering modal");
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

                }
            }
        }
    });
    editDialog.attr("id", "edit-modal");
    if(overflow){
        $('#edit-modal div.modal-body').attr('style', 'overflow: visible !important');
    }
    $('.btn.edit-modal-submit').prop('disabled', true);
    $('body').removeClass('modal-open').removeClass('open-edit-modal').addClass('open-edit-modal');
}

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

function processDataCheckEducation(returnedEdu) {
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

function processDataCheckDegree(returnedDegree) {
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

function processDataCheckLanguage(returnedData) {
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
        languageArray.push(item);
    });
    populateLanguages(arrayLang.reverse(), arrayLangId.reverse());
}

function populateLanguages(l, lId) {
    var i;
    var table = document.getElementById("languageTable");
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
    $('table#languageTable tr').each(function () {
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

function processDataCheckExperience(returnedExperience) {
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

function processIdProofsWithNumbers(returnedData) {
    // create table
    if(returnedData != null) {
        // minReqTable
        var minReqTableContainer = $('#document_details');
        console.log(minReqTableContainer);
        var mainTable = document.createElement("table");
        mainTable.className ="mdl-data-table mdl-js-data-table mdl-shadow--2dp mdl-cell mdl-cell--12-col";
        mainTable.style="margin:0;border:none";
        mainTable.id = "documentTable";

        var tHead = document.createElement("thead");
        tHead.style="background-color:rgb(63,81,181)";
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
            console.log(idProof);
            var bodyContentBox = document.createElement("tr");
            bodyContentBox.id = idProof.idProofId;
            tBody.appendChild(bodyContentBox);

                var checkboxTd = document.createElement("td");
                bodyContentBox.appendChild(checkboxTd);
                var checkMatchLabel = document.createElement("label");
                checkMatchLabel.type = "checkbox";
                checkMatchLabel.for = "checkbox_" + idProof.idProofId;
                checkMatchLabel.style = 'text-align:center';
                checkMatchLabel.className = "mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect";
                checkboxTd.appendChild(checkMatchLabel);
                var checkMatch = document.createElement("input");
                checkMatch.type = "checkbox";
                checkMatch.id = "checkbox_" + idProof.idProofId;
                checkMatch.className = "mdl-checkbox__input";
                checkMatchLabel.appendChild(checkMatch);

                var idProofTitleTd = document.createElement("td");
                idProofTitleTd.textContent = idProof.idProofName;
                bodyContentBox.appendChild(idProofTitleTd);

                var idProofNumberTd = document.createElement("td");
                bodyContentBox.appendChild(idProofNumberTd);

                var ip = document.createElement("INPUT");
                ip.setAttribute("type", "text");
                ip.setAttribute("id", "idProofValue_"+idProof.idProofId);
                idProofNumberTd.appendChild(ip);

        })
    }
}
function processDataCheckLocality(returnedData) {
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

function fetchEditModalContent(candidateId, propId, jobPostId) {

    // api call and render child modal
    var base_api_url ="/support/api/getCandidateDetails/";
    if(base_api_url == null || candidateId == null) {
        console.log("please provide candidateId && propertyId");
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
        console.log("candidate's info:" + JSON.stringify(returnedData));
    }; // this sets the values in the prev created options
    var htmlBodyContent = "test";
    var modalTitle="test";
    var isOverFlowRequired = false;


    if(propId == 0) {
        htmlBodyContent = '<div id="document_details">'+
            '<h5>Document Details:</h5>'+
            '</div>';

        url = "/support/api/getDocumentReqForJobRole/?job_post_id="+jobPostId;
        ajax_type = "GET";
        fn = function (returnedData) {
            processIdProofsWithNumbers(returnedData);
        }
    } else if(propId == 1) {
        htmlBodyContent = '<div id="language_details">'+

            '<div class="row" style="margin:0;padding:1%;background-color:rgb(63, 81, 181);color:#fff"><b>Language Details :</b></div>'+
            '<table id="languageTable" class="mdl-data-table mdl-js-data-table table table-striped mdl-shadow--2dp" cellspacing="0" width="100%">'+
            '<thead>'+
            '</thead>'+
            '<tbody>'+
            '</tbody>'+
            '</table>'+
            '</div>';


        modalTitle = "Candidate Language Edit";
        url = "/getAllLanguage";
        fn = function (returnedData) {
            processDataCheckLanguage(returnedData);
        };
        setter = function (returnedData) {

        }
    } else if(propId == 2) {

        htmlBodyContent ='<div class="row" style="margin:0;padding:1%;background-color:rgb(63, 81, 181);color:#fff"><b>Assets</b></div>'+
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

        };
        isOverFlowRequired = true;

    } else if(propId == 3) {
        htmlBodyContent = '<div class="row" style="margin:0;padding:1%;background-color:rgb(63, 81, 181);color:#fff"><b>Date Of Birth</b></div>'+
            '<div class="row mdl-shadow--2dp" style="margin: 1px;padding: 2% 1%;background-color:#fff">'+
            '<div class="col-lg-3 col-lg-offset-2" style="margin-top: 8px;text-align: right"><font size="3">DOB : </font></div>'+
            '<div class="col-lg-4">'+
            '<input id="candidateDob" name="candidateDob" placeholder="When is your Birthday?" type="date" class="form-control input-md"  data-date-inline-picker="true"/>'+
            '</div>'+
            '</div>';

        modalTitle = "Candidate DOB Edit";

    } else if(propId == 4) {
        htmlBodyContent = '<div class="row" style="margin:0;padding:1%;background-color:rgb(63, 81, 181);color:#fff"><b>Experience</b></div>'+
            '<div class="row mdl-shadow--2dp" style="margin: 1px;padding: 2% 1%;background-color:#fff">'+
            '<div class="col-lg-3 col-lg-offset-2" style="margin-top: 8px;text-align: right"><font size="3">Experience : </font></div>'+
            '<div class="col-lg-4" id="experienceMultiSelectDiv">'+
            '<select id="experienceMultiSelect" multiple="multiple"></select>'+
            '</div>'+
            '</div>';

        modalTitle = "Candidate Experience Edit";
        url = "/getAllExperience";
        fn = function (returnedData) {
            processDataCheckExperience(returnedData);
        };

        isOverFlowRequired = true;
    } else if(propId == 5) {
        htmlBodyContent = '<div class="row" style="margin:0;padding:1%;background-color:rgb(63, 81, 181);color:#fff"><b>Educational Details</b></div>'+
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
            '</div>';

        modalTitle = "Candidate Education Edit";
        isOverFlowRequired = true;
        url = "/getAllEducation";
        fn = function (returnedData) {
            processDataCheckEducation(returnedData);
        }
    } else if(propId == 6) {
        htmlBodyContent = '<div class="row" style="margin:0;padding:1%;background-color:rgb(63, 81, 181);color:#fff"><b>Candidate Gender</b></div>'+
            '<div class="row mdl-shadow--2dp" id="education_details" style="margin: 1px;padding: 2% 1%;background-color:#fff">'+
            '<div class="col-lg-5" style="margin-top: 8px;text-align: right"><font size="3">Candidate Gender : </font></div>'+
            '<div class="col-lg-4" style="margin-top: 8px;">'+
            '<input type="radio" name="gender" id="genderMale" value="0">&nbsp;Male&nbsp;&nbsp;'+
            '<input type="radio" name="gender" id="genderFemale" value="1">&nbsp;Female'+
            '</div>'+
            '</div>';

        modalTitle = "Candidate Gender Edit";
    } else if(propId == 7) {
        htmlBodyContent = '<div class="row" style="margin:0;padding:1%;background-color:rgb(63, 81, 181);color:#fff"><b>Salary Details</b></div>'+
            '<div class="row mdl-shadow--2dp" id="education_details" style="margin: 1px;padding: 2% 1%;background-color:#fff">'+
            '<div class="col-lg-5" style="margin-top: 8px;text-align: right"><font size="3">Current/Last Drawn Salary : </font></div>'+
            '<div class="col-lg-4">'+
            '<input id="candidateLastWithdrawnSalary" placeholder="What was your LastWithdrawn Salary?" type="number" class="form-control input-md">'+
            '</div>'+
            '</div>';
        modalTitle = "Candidate Last Withdrawn Salary Edit";
    } else if(propId == 8) {
        htmlBodyContent = '<div class="row" style="margin:0;padding:1%;background-color:rgb(63, 81, 181);color:#fff"><b>Current Location</b></div>'+
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
            processDataCheckLocality(returnedData);
        };
        setter = function (returnedData) {
            if(returnedData!= null) {
                try {
                    var item = {};
                    item ["id"] = returnedData.localityId;
                    item ["name"] = returnedData.localityName;
                    currentLocationArray.push(item); // TODO remove this line
                    $("#candidateHomeLocality").tokenInput("add", item);
                } catch (err) {
                    console.log("homeLocality" + err);
                }
            }
        }

    } else if(propId == 9) {
        htmlBodyContent =  '<div class="row" style="margin:0;padding:1%;background-color:rgb(63, 81, 181);color:#fff"><b>Job TimeShift Preferences</b></div>'+
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
        }
    }

    // generates html container
    generateEditModalView(modalTitle, htmlBodyContent, candidateId, propId, isOverFlowRequired);

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
                success: processDataCheckDegree
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

function processPreScreenContent(returnedData) {
    if(returnedData == null || returnedData.status != "SUCCESS") {
        console.log(returnedData);
        pushToSnackbar("Request failed. Something went Wrong! Please Refresh");
    }
    if(returnedData != null){
        // if(returnedData == "OK" || returnedData == "NA" ) {
        //     processPostPreScreenResponse(returnedData);
        //     return returnedData;
        // }
        // if($(".pre-screen-modal").size() > 0){
        //     return returnedData;
        // }
        var candidateId = returnedData.candidateId;
        var jobPostId = returnedData.jobPostId;
        var preScreenBody = $('<div id="pre_screen_body" class="mdl-grid"></div>');
        var container = $('<div class="row mdl-cell mdl-cell--12-col" id="pre_screen_container_row"></div>');
        preScreenBody.append(container);

        var minReqTableContainer = $('<div id="minReqTable"></div>');
        container.append('<h4 style="margin-top: 0">Min Requirement</h4>');
        container.append(minReqTableContainer);

        var otherReqTableContainer = $('<div id="otherReqTable"></div>');
        container.append('<h4>Other Requirement</h4>');
        container.append(otherReqTableContainer);

        // minReqTable
        var mainTable = document.createElement("table");
        mainTable.className ="mdl-data-table mdl-js-data-table mdl-shadow--2dp mdl-cell mdl-cell--12-col";
        mainTable.style="margin:0;border:none";

        var tHead = document.createElement("thead");
        tHead.style="background-color:rgb(63,81,181)";
        mainTable.appendChild(tHead);

        var heading = document.createElement("tr");
        tHead.appendChild(heading);

        var title1 = document.createElement("th");
        title1.textContent = "";
        heading.appendChild(title1);

        var title2 = document.createElement("th");
        title2.textContent = "Job Post Info";
        title2.style="color:#ffffff";
        heading.appendChild(title2);

        var title3 = document.createElement("th");
        title3.style="color:#ffffff";
        title3.textContent = "Candidate Info";
        heading.appendChild(title3);

        // is a match or not
        var isAMatch = document.createElement("th");
        isAMatch.textContent = "Match?";
        isAMatch.style="color:#ffffff";
        heading.appendChild(isAMatch);

        var title4 = document.createElement("th");
        title4.style="color:#ffffff";
        title4.textContent = "Is candidate Ready";
        heading.appendChild(title4);

        var title5 = document.createElement("th");
        title5.style="color:#ffffff";
        title5.textContent = "Edit";
        heading.appendChild(title5);

        var tBody = document.createElement("tbody");
        mainTable.appendChild(tBody);

        minReqTableContainer.append(mainTable);

        //otherTable
        var otherTable = document.createElement("table");
        otherTable.className ="mdl-data-table mdl-js-data-table mdl-shadow--2dp mdl-cell mdl-cell--12-col";
        otherTable.style ="margin:0;border:none";

        var tHead = document.createElement("thead");
        tHead.style="background-color:rgb(63,81,181)";
        otherTable.appendChild(tHead);

        var heading = document.createElement("tr");
        tHead.appendChild(heading);

        var title1 = document.createElement("th");
        title1.textContent = "";
        heading.appendChild(title1);

        var title2 = document.createElement("th");
        title2.style="color:#ffffff";
        title2.textContent =  "Job Post Info";
        heading.appendChild(title2);

        var title3 = document.createElement("th");
        title3.style="color:#ffffff";
        title3.textContent = "Candidate Info";
        heading.appendChild(title3);

        // is a match or not
        var isAMatch = document.createElement("th");
        isAMatch.style="color:#ffffff";
        isAMatch.textContent = "Match?";
        heading.appendChild(isAMatch);

        var title4 = document.createElement("th");
        title4.style="color:#ffffff";
        title4.textContent = "Is candidate Ready";
        heading.appendChild(title4);

        var title5 = document.createElement("th");
        title5.style="color:#ffffff";
        title5.textContent = "Edit";
        heading.appendChild(title5);

        var other_tBody = document.createElement("tbody");
        otherTable.appendChild(other_tBody);

        otherReqTableContainer.append(otherTable);

        var splitDiv = $('<div class="row" style="margin-top: 20px"></div>');
        var noteContainer = document.createElement("div");
        noteContainer.className = "col-lg-6 form-group";
        var textarea = document.createElement("textarea");app.imn
        textarea.className = "form-control mdl-shadow--2dp";
        textarea.style = "border-radius:0;";
        textarea.rows = "5";
        textarea.type = "text";
        textarea.placeholder = "Extra Comment";
        textarea.id = "pre_screen_note";

        var minReqContainer = document.createElement("div");
        minReqContainer.className = "col-lg-6 form-group";
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
        rowHeadingPost.style = "padding:1% 2%;background-color:rgb(63, 81, 181);color:#fff";
        minReqContainer.appendChild(rowHeadingPost);

        var rowHeadingNote = document.createElement("div");
        rowHeadingNote.style = "padding:1% 2%;background-color:rgb(63, 81, 181);color:#fff";
        noteContainer.appendChild(rowHeadingNote);

        var label = document.createElement("label");
        label.for= "job_post_min_req";
        label.style = "margin:0";
        label.textContent = "Job Post Min Req";
        rowHeadingPost.appendChild(label);
        minReqContainer.appendChild(minReqTextArea);
        splitDiv.append(minReqContainer);


        var label = document.createElement("label");
        label.for= "pre_screen_note";
        label.textContent = "Note";
        label.style = "margin :0";
        rowHeadingNote.appendChild(label);
        noteContainer.appendChild(textarea);
        splitDiv.append(noteContainer);
        container.append(splitDiv);


        var elementList = returnedData.elementList;
        elementList.forEach(function (rowData) {
            console.log("ptitle:"+ rowData.propertyTitle);
            console.log("pId:"+ rowData.propertyId);
            if(rowData!=null){
                if(rowData.isMinReq) {
                    var bodyContentBox = document.createElement("tr");
                    bodyContentBox.id = rowData.propertyId;
                    tBody.appendChild(bodyContentBox);

                    var bodyContentData1 = document.createElement("td");
                    bodyContentData1.textContent = rowData.propertyTitle;
                    bodyContentBox.appendChild(bodyContentData1);

                    var bodyContentData3 = document.createElement("td");
                    bodyContentBox.appendChild(bodyContentData3);

                    var bodyContentData2 = document.createElement("td");
                    bodyContentBox.appendChild(bodyContentData2);

                    if(rowData.isSingleEntity){
                        bodyContentData2.textContent = rowData.candidateElement;
                        bodyContentData3.textContent = rowData.jobPostElement;
                    } else {
                        bodyContentData2.textContent = rowData.candidateElementList;
                        bodyContentData3.textContent = rowData.jobPostElementList;
                    }

                    var spanTd = document.createElement("td");
                    var indicatorSpan = document.createElement("span");
                    if(rowData.isMatching){
                        indicatorSpan.setAttribute('class', 'glyphicon glyphicon-ok');
                    } else {
                        indicatorSpan.setAttribute('class', 'glyphicon glyphicon-remove');
                    }
                    spanTd.appendChild(indicatorSpan);
                    bodyContentBox.appendChild(spanTd);

                    var bodyContentData4 = document.createElement("td");
                    bodyContentBox.appendChild(bodyContentData4);

                    var checkMatchLabel = document.createElement("label");
                    checkMatchLabel.type = "checkbox";
                    checkMatchLabel.for = "checkbox_" + rowData.propertyIdList.join("-");
                    checkMatchLabel.style = 'text-align:center';
                    checkMatchLabel.className = "mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect";
                    bodyContentData4.appendChild(checkMatchLabel);

                    var checkMatch = document.createElement("input");
                    checkMatch.type = "checkbox";
                    checkMatch.id = "checkbox_" + rowData.propertyIdList.join("-");
                    checkMatch.className = "mdl-checkbox__input";
                    checkMatchLabel.appendChild(checkMatch);

                    // edit href
                    var editLink = document.createElement("td");
                    var a = document.createElement('a');
                    var linkText = document.createTextNode("Edit");
                    a.appendChild(linkText);
                    a.style = "cursor: pointer";
                    a.onclick = function () {
                        fetchEditModalContent(candidateId, rowData.propertyId, jobPostId);
                    };
                    editLink.appendChild(a);
                    bodyContentBox.appendChild(editLink);
                } else {

                    var bodyContentBox = document.createElement("tr");
                    bodyContentBox.id = rowData.propertyId;
                    other_tBody.appendChild(bodyContentBox);

                    var bodyContentData1 = document.createElement("td");
                    bodyContentData1.textContent = rowData.propertyTitle;
                    bodyContentBox.appendChild(bodyContentData1);

                    var bodyContentData3 = document.createElement("td");
                    bodyContentBox.appendChild(bodyContentData3);

                    var bodyContentData2 = document.createElement("td");
                    bodyContentBox.appendChild(bodyContentData2);

                    if(rowData.isSingleEntity){
                        bodyContentData2.textContent = rowData.candidateElement;
                        bodyContentData3.textContent = rowData.jobPostElement;
                    } else {
                        bodyContentData2.textContent = rowData.candidateElementList;
                        bodyContentData3.textContent = rowData.jobPostElementList;
                    }

                    var spanTd = document.createElement("td");
                    var indicatorSpan = document.createElement("span");
                    if(rowData.isMatching){
                        indicatorSpan.setAttribute('class', 'glyphicon glyphicon-ok');
                    } else {
                        indicatorSpan.setAttribute('class', 'glyphicon glyphicon-remove');
                    }
                    spanTd.appendChild(indicatorSpan);
                    bodyContentBox.appendChild(spanTd);

                    var bodyContentData4 = document.createElement("td");
                    bodyContentBox.appendChild(bodyContentData4);

                    var checkMatchLabel = document.createElement("label");
                    checkMatchLabel.type = "checkbox";
                    checkMatchLabel.for = "checkbox_" + rowData.propertyIdList.join("-");
                    checkMatchLabel.style = 'text-align:center';
                    checkMatchLabel.className = "mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect";
                    bodyContentData4.appendChild(checkMatchLabel);

                    var checkMatch = document.createElement("input");
                    checkMatch.type = "checkbox";
                    checkMatch.id = "checkbox_" + rowData.propertyIdList.join("-");
                    checkMatch.className = "mdl-checkbox__input";
                    checkMatchLabel.appendChild(checkMatch);
                    
                    // edit href
                    var editLink = document.createElement("td");
                    var a = document.createElement('a');
                    var linkText = document.createTextNode("Edit");
                    a.appendChild(linkText);
                    a.style = "cursor: pointer";
                    a.title = "Edit";
                    a.onclick = function () {
                        fetchEditModalContent(candidateId, rowData.propertyId, jobPostId);
                    };
                    editLink.appendChild(a);
                    bodyContentBox.appendChild(editLink);
                }
            }

        });


        var callYesNo = $('' +
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
            '<button type="submit" id="responseSaveBtn"  class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" onclick="saveAttempt('+candidateId+', '+jobPostId+')">Save</button>' +
            '</h6>' +
            '</div>' +
            '</h5>'+
            '</div>'+
            '</div>');

        renderParentModal(preScreenBody, callYesNo, jobPostId, candidateId);
    }
}

function renderParentModal(preScreenBody, callYesNo, jobPostId, candidateId) {
    var dialog = bootbox.dialog({
        className: "pre-screen-modal",
        title: callYesNo,
        message: preScreenBody,
        closeButton: true,
        animate: true,
        onEscape: function() {
            $('body').removeClass('open-modal');
        },
        buttons: {
            "Submit": {
                className: "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent modal-submit",
                callback: function () {
                    if($("#pre_screen_body input[type='checkbox']:checked").size() == 0
                        && $('input:radio[name="verdict"]:checked').val() == null) {
                        bootbox.alert({
                            size: "small",
                            title: "Invalid Submission !",
                            message: "If you want to mark the candidate as  'completed_prescreening' irrespective of given criteria, select checkbox present next to submit button, in previous dialog.",
                            callback: function(){ /* your callback code */ }
                        });
                        return false;
                    } else {
                        $('body').removeClass('open-modal');
                        //if($("#pre_screen_body input[type='checkbox']:checked").size() > 0) {}
                        triggerPreScreenResponseSubmission(candidateId, jobPostId);
                        console.log("final prescreen submission triggered");
                        return true;
                    }
                }
            }
        }
    });
    dialog.init(function(){
        var forceSetContainer = $('.modal-footer');
        var forceSetDiv = $('' +
            '<div class="col-xs-11" style="text-align: left">'+
            '<h5 style="margin:2px; font-size: 12px;">' +
            '<div style="display:inline-block; margin: 0 1px;text-align: left; color: #b9151b">*</div>'+
            'Did the candidate pass pre-screen?&nbsp;:&nbsp;'+
            '<div style="display: inline-block; vertical-align: middle; margin: 0px;">' +
            '<input type="radio" name="verdict" id="pass" value="1" style="margin: 0 2px" onclick="activateSubmit()">Yes' +
            '<input type="radio" name="verdict" id="fail" value="0" style="margin: 0 2px" onclick="activateSubmit()">No' +
            '</div>'+
            '</h5>'+
            '</div>'
        );
        forceSetContainer.prepend(forceSetDiv);

        $('.btn.modal-submit').prop('disabled', true);
        $('#pre_screen_body').hide();
        $('body').removeClass('modal-open').removeClass('open-modal').addClass('open-modal');
    });
}

function processPostPreScreenResponse(response) {
    console.log(response);
    if(response == "OK"){
        notifyError("Submitted successfully. Refreshing ..", 'success');
        setTimeout(function () {
            location.reload();
            // window.location = response.redirectUrl + app.jpId + "/?view=" + response.nextView;
        }, 2000);
    } else {
        notifyError("Error! Something Went wrong please try again.", 'danger')
    }
}

function getPreScreenContent(jobPostId, candidateId) {
    var base_api_url ="/support/api/getJobPostVsCandidate/";
    if(base_api_url == null || jobPostId == null) {
        console.log("please provide candidateId && jobPostId");
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

    try {
        $.ajax({
            type: "GET",
            url: base_api_url,
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processPreScreenContent
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