
var gTableDataContainer = [];

function processAlphaResponse(alphaResponse){
    var dialog = document.querySelector('dialog');
    if (!dialog.showModal) {
        dialogPolyfill.registerDialog(dialog);
    }
    dialog.close();
    $("#perishable-spinner").removeClass("is-active");

    pushToSnackbar(alphaResponse);
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

function executeAlphaRequest(mobile){
    if(mobile != null && mobile!= ""){
        /* ajax commands to delete lead/Candidate */
        $("#perishable-spinner").addClass("is-active");

        try {
            $.ajax({
                type: "GET",
                url: "/support/administrator/alphaRequest/" + mobile,
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processAlphaResponse
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
}

function constructTableForRelevantJobs(rows) {

    var data = new google.visualization.DataTable();
    var tableDivId = 'job_relevancy_table_div';
    if($('#job-relevancy-table-content')){
        $(tableDivId).empty();
    }

    if(rows != null){
        data.addColumn('string', "Job Role");
        data.addColumn('string', "Relevant Job Role");

        var googleTableRows = [];

        $.each( rows, function( key, value) {
            var googleRowOneRow = [];
            googleTableRows.push([key, value.join(", ")]);
        });
        data.addRows(googleTableRows);

        var table = new google.visualization.Table(document.getElementById(tableDivId ));
        table.draw(data, {showRowNumber: true, width: '100%', height: '100%'});

        pushToSnackbar("Relevant Job Roles Fetched Successfully !!");
    }
}

function constructTableForData(tableName, row) {
    // generates every thing only for one table
    var data = new google.visualization.DataTable();

    var rowArray = [];
    var f = true;
    var tableDivId ='tableDiv_'+tableName;
    var googleTableRows = [];
    $('div[id="csv_'+tableName+'"]').remove();
    $('div[id="'+tableDivId+'"]').remove();
    $('#tabular-content').append($('<div id="csv_'+tableName+'"></div><div id="'+tableDivId+'"></div>'));
    $('#chart_container').append($('<div id="chart_'+tableName+'"></div>'));

    if (row != null) {
        $.each( row, function( rName, postData) {
            var formatedDate = new Date(rName).toLocaleDateString();
            var rValue = {"FormattedDate": formatedDate};

            $.each ( postData, function (rowIndex, innerData) {
                $.extend(rValue, innerData);
                //rowArray.push(rValue);
                var googleRowOneRow = [];
                $.each( rValue  , function( cName, value ) {
                    //console.log( "cValue:"+value );
                    googleRowOneRow.push(""+value);
                    /* Add the column name once */
                    if (f) {
                        data.addColumn('string', cName);
                    }
                });
                googleTableRows.push(googleRowOneRow);
                f=false;
            })
        });
        data.addRows(googleTableRows);

        var csv = google.visualization.dataTableToCsv(data);
        var csvString = csv;
        var a         = document.createElement('a');
        a.href        = 'data:attachment/csv,' +  encodeURIComponent(csvString);
        a.target      = '_blank';
        a.download    = 'truAnalytics_'+tableName+'.csv';
        a.textContent = tableName;
        $('div[id="csv_'+tableName+'"]').append(a);
/*        var components = [
            {type: 'csv', datasource: 'https://spreadsheets.google.com/tq?key=pCQbetd-CptHnwJEfo8tALA'}
        ];
        var container = document.getElementById('toolbar_div_'+tableDivId);
        google.visualization.drawToolbar(container, components);*/

        var table = new google.visualization.Table(document.getElementById(tableDivId));
        table.draw(data, {showRowNumber: true, width: '100%', height: '100%'});

    }
}


function renderAnalyticsResult(analyticsResult) {
    if(analyticsResult != null){
        $('#tabular-content').empty();
        $.each( analyticsResult, function( key, value ) {
            //console.log("ar: " + key);
            constructTableForData(key, value);
        });
    }
}

function checkall(id) {
    if($('#AllCheck').is(":checked")){
        $('#deactivatedCandidateTable input[type=checkbox]').prop('checked', true);
    } else {
        $('#deactivatedCandidateTable input[type=checkbox]').prop('checked', false);
    }
}

function constructCheckBox(leadId, profileStatus) {
    if(leadId != null){
        if(profileStatus.profileStatusId == "2"){ // new or active return active
            return  '<input type="checkbox" id="'+leadId+'" name="cb" value="1" checked><br>';
        } else {
            return  '<input type="checkbox" name="cb" value="0"><br>';
        }
    }
    return "-";
}

function getExpiry(expiryObject) {
    if(expiryObject != null){
        return expiryObject.statusExpiryDate;
    }
    return "-";
}

function renderConvertedData(returnedData) {
    if(returnedData.status == "1"){
        pushToSnackbar("Updated Successfully. Loading Changes...");
        renderDeactivatedCandidateResult(returnedData.candidateList);
    } else {
        pushToSnackbar("Opps.. Something went wrong. Failed to Update changes. Try Again");
    }
}

function renderDeactivatedCandidateResult(deactivatedCandidateList) {
    if(deactivatedCandidateList != null){

        var returnedDataArray = [];

        console.log(JSON.stringify(deactivatedCandidateList));
        deactivatedCandidateList.forEach(function (candidate) {
            returnedDataArray.push({
                'cLID': '<a href="/candidateSignupSupport/'+candidate.lead.leadId+'" target="_blank">'+candidate.lead.leadId+'</a>',
                'candidateFirstName' : candidate.candidateFirstName +" "+candidate.candidateLastName,
                'candidateMobile' : candidate.candidateMobile,
                'isActive' : constructCheckBox(candidate.lead.leadId, candidate.candidateprofilestatus),
                'candidateExpiry' : getExpiry(candidate.candidateStatusDetail)
            });
        });
        $('#deactivatedCandidateTable').show();

        var table = $('table#deactivatedCandidateTable').DataTable({
            "data": returnedDataArray,
            "order": [[4, "desc"]],
            "scrollX": true,
            "columns": [
                { "data": "cLID" },
                { "data": "candidateFirstName" },
                { "data": "candidateMobile" },
                { "data": "isActive" },
                { "data": "candidateExpiry" }
            ],
            "deferRender": true,
            "scroller": true,
            "scrollY":'48vh',
            "scrollCollapse": true,
            "language": {
                "emptyTable": "No data available"
            },
            "paging":false,
            "destroy": true,
            "dom": 'Bfrtip',
            "buttons": [
                'copy', 'csv', 'excel'
            ]
        });

    }
}

function queryForm() {
    var d = {
        fromThisDate: $('#fromThisDate').val(),
        toThisDate: $('#toThisDate').val(),
        Metrics: $('#queryMultiSelect').val(),
        updateGoogleSheet: $('#pushToGoogleSheet').is(":checked")
    };
    try {
        $.ajax({
            type: "POST",
            url: "/api/alpha/2",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: renderAnalyticsResult
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function fetchDeactivatedCandidateList() {
    var d = {
        fromThisDate: $('#fromThisDate').val(),
        toThisDate: $('#toThisDate').val()
    };
    try {
        $.ajax({
            type: "POST",
            url: "/api/getDeactivatedCandidateList",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: renderDeactivatedCandidateResult
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function constructMultiSelect(){
    // Callback that creates and populates a data table,
    // instantiates the pie chart, passes in the data and
    // draws it.

    var data = [
        {label: "Summary Metrics", value: "Summary Metrics"},
        {label: "Support Metrics", value: "Support Metrics"},
        {label: "Lead Sources", value: "Lead Sources"},
        {label: "Active Candidates", value: "Active Candidates"}
        ];

    var selectList = $('#queryMultiSelect');
    selectList.multiselect({
        includeSelectAllOption: true,
        maxHeight: 300
    });
    selectList.multiselect('dataprovider', data);
    selectList.multiselect('rebuild');
}

function googleTableplot() {
    google.charts.load('current', {'packages':['table']});
    google.charts.setOnLoadCallback(queryForm());

}

function hideDrawerElements() {
    $('#queryMultiSelect-grid').hide();
    $('#pushToGoogleSheet-lbl').hide();
}
function showDrawerElements() {
    $('#queryMultiSelect-grid').show();
    $('#pushToGoogleSheet-lbl').show();
}
function saveDeactivationChanges() {
    var deactiveToActiveList = [];
    $('#deactivatedCandidateTable input[type=checkbox]').each(function(){
        if(!$(this).is(":checked") && $(this).attr('id') != null){
            deactiveToActiveList.push(parseInt($(this).attr('id')));
        }
    });
    console.log("deactiveToActiveList" + deactiveToActiveList);
    if(deactiveToActiveList.length > 0){
        var d = {
            deactiveToActiveList: deactiveToActiveList
        };
        try {
            $.ajax({
                type: "POST",
                url: "/api/deactiveToActive",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: renderConvertedData
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    } else {
        pushToSnackbar("Invalid Selection!");
    }
}

function getJSON(url, type) {
    // Return a new promise.
    if (type == null) {
        return "Error! GET/POST not specified.";
    }
    if (url == null) {
        return "Error! URL not specified.";
    }
    return new Promise(function(resolve, reject) {
        // Do the usual XHR stuff
        var req = new XMLHttpRequest();
        req.open(type, url);

        req.onload = function() {
            // This is called even on 404 etc
            // so check the status
            if (req.status == 200) {
                // Resolve the promise with the response text
                resolve(JSON.parse(req.response));
            }
            else {
                // Otherwise reject with the status text
                // which will hopefully be a meaningful error
                reject(Error(req.statusText));
            }
        };

        // Handle network errors
        req.onerror = function() {
            reject(Error("Network Error"));
        };

        // Make the request
        req.send();
    });
}

function renderUpdateRelevantJobRoles(data) {
    if(data != null) {
        var totalUpdates = data.length;
        pushToSnackbar("Relevant JobRoles Re-computed and Updated ("+totalUpdates+") Successfully !");
        fetchAndDisplayRelevantJobs();
    } else {
        pushToSnackbar("Unsuccessful operation. Check Logs !!");
    }
}

function updateRelevantJobRoles() {
    try {
        $.ajax({
            type: "GET",
            url: "/api/compute/updateAllRelevantJobCategories",
            data: false,
            success: renderUpdateRelevantJobRoles
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function fetchAndDisplayRelevantJobs() {
    getJSON('/api/getRelatedJobRole/?format=only_name', 'POST').then(function(response) {
        constructTableForRelevantJobs(response)
    }, function(error) {
        console.error("Failed!", error);
    }).catch(function() {
        pushToSnackbar('Could not update Relevant JobRoles Table !!');
    });
}

$(function(){
    $("#btnDeActiveToActive").click(function(){
        saveDeactivationChanges();
    });
    $( "#deactivatedCandidateTab" ).click(function() {
        pushToSnackbar("Select expiry time period from left Drawer and hit search");
        hideDrawerElements();
    });
    $( "#tabularTab" ).click(function() {
        showDrawerElements();
    });
    $( "#updateRelevantJobRoles" ).click(function() {
        updateRelevantJobRoles();
    });
    $( "#jobRelevancyTab" ).click(function() {
        fetchAndDisplayRelevantJobs();
    });

    var dialog = document.querySelector('dialog');
    var showModalButton = document.querySelector('.show-modal');
    if (!dialog.showModal) {
        dialogPolyfill.registerDialog(dialog);
    }
    showModalButton.addEventListener('click', function () {
        dialog.showModal();
    });
    dialog.querySelector('.close').addEventListener('click', function () {
        dialog.close();
    });

    constructMultiSelect();
    googleTableplot();
    //drawerManipulator();

    $("#perish-form").submit(function(eventObj) {
        eventObj.preventDefault();
        var mobile = $('#perishableMobile').val();
        if (mobile != null && mobile.length == 10) {
            executeAlphaRequest(mobile);
        }
    });
    $("#deactivatedCandidate-form").submit(function(eventObj) {
        eventObj.preventDefault();
        fetchDeactivatedCandidateList();
    });
    document.querySelector('#queryBtn').addEventListener('click', function () {
        document.querySelector('.mdl-layout__obfuscator').classList.remove('is-visible');
        document.querySelector('.mdl-layout__drawer').classList.remove('is-visible');
    }, false);

    $("#drawerInputForm").submit(function(eventObj) {
        eventObj.preventDefault();
        /* identify query type */
        if($( "#deactivatedCandidateTab" ).hasClass("is-active")){
            fetchDeactivatedCandidateList();
        } else if( $( "#tabularTab" ).hasClass("is-active") ){
            queryForm();
        }
    });
});

