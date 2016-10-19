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
            console.log("updateCallAttempt: " + returnedData);
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
    $('.btn-success.btn-modal-submit').prop('disabled', false);
    $('#pre_screen_body').show();
    updateCallAttempts(candidateId, jobPostId, "CONNECTED");
}
function onCallNo(candidateId, jobPostId) {
    $('#callNoClass').show();
    $('#pre_screen_body').hide();
    $('.btn-success.btn-modal-submit').prop('disabled', true);
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

    var d = {
        candidateId: candidateId,
        jobPostId: jobPostId,
        preScreenIdList: responseList
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
        title2.textContent = "Job Post";
        title2.style="color:#ffffff";
        heading.appendChild(title2);

        var title3 = document.createElement("th");
        title3.textContent = "Candidate";
        title3.style="color:#ffffff";
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
        title2.textContent = "Job Post";
        heading.appendChild(title2);

        var title3 = document.createElement("th");
        title3.style="color:#ffffff";
        title3.textContent = "Candidate";
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

        var other_tBody = document.createElement("tbody");
        otherTable.appendChild(other_tBody);

        otherReqTableContainer.append(otherTable);


        var elementList = returnedData.elementList;
        elementList.forEach(function (rowData) {
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
            '<div class="col-sm-4" style="text-align: right">'+
            '<h5 style="margin:2px">&nbsp;Force Set&nbsp;:&nbsp;'+
            '<input type="checkbox" name="" id="pass" value="yes" onclick="">'+
            '</h5>'+
            '</div>'+
            '</div>');

        bootbox.dialog({
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
                    className: "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent",
                    callback: function() {
                        $('body').removeClass('open-modal');
                        if($("#pre_screen_body input[type='checkbox']:checked").size() > 0) {
                            console.log("final prescreen submission triggered");
                            triggerPreScreenResponseSubmission(candidateId, jobPostId);
                        }
                    }
                }
            },
            callback: function(result) {
                console.log(result);
            }
        });
        $('.btn-success.btn-modal-submit').prop('disabled', true);
        $('#pre_screen_body').hide();
        $('body').removeClass('modal-open').removeClass('open-modal').addClass('open-modal');
    }
}

function processPostPreScreenResponse(response) {
    console.log(response);
    return response;
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