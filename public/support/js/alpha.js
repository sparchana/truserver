
var gTableDataContainer = [];

function processAlphaResponse(alphaResponse){
    var dialog = document.querySelector('dialog');
    if (!dialog.showModal) {
        dialogPolyfill.registerDialog(dialog);
    }
    dialog.close();
    $("#perishable-spinner").removeClass("is-active");

    'use strict';
    var snackbarContainer = document.querySelector('#perish-snackbar');

    var data = {
        message: JSON.stringify(alphaResponse),
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

function constructDataForTable(tableName, row) {
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

    if(row != null){
        $.each( row, function( rName, postData) {
            var formatedDate = new Date(rName).toLocaleDateString();
            var rValue = {"FormattedDate": formatedDate};
            $.extend(rValue, postData);
            rowArray.push(rValue);
            var googleRowOneRow = [];
            $.each( rValue  , function( cName, value ) {
                //console.log( "cValue:"+value );
                googleRowOneRow.push(""+value);
                /* Add the column name once */
                if(f){
                    data.addColumn('string', cName);
                }
            });
            googleTableRows.push(googleRowOneRow);
            f=false;
        });
        data.addRows(googleTableRows);

        var csv = google.visualization.dataTableToCsv(data);
        var csvString = csv;
        var a         = document.createElement('a');
        a.href        = 'data:attachment/csv,' +  encodeURIComponent(csvString);
        a.target      = '_blank';
        a.download    = 'truAnalytics_'+tableName+'.csv';
        a.textContent = tableName;
        console.log("data: " + JSON.stringify(data) + " csvString: " + JSON.stringify(csv))
        $('div[id="csv_'+tableName+'"]').append(a);
/*        var components = [
            {type: 'csv', datasource: 'https://spreadsheets.google.com/tq?key=pCQbetd-CptHnwJEfo8tALA'}
        ];
        var container = document.getElementById('toolbar_div_'+tableDivId);
        google.visualization.drawToolbar(container, components);*/

        var table = new google.visualization.Table(document.getElementById(tableDivId ));
        table.draw(data, {showRowNumber: true, width: '100%', height: '100%'});

    }
}


function renderAnalyticsResult(analyticsResult) {
    if(analyticsResult != null){
        $('#tabular-content').empty();
        $.each( analyticsResult, function( key, value ) {
            //console.log("ar: " + key);
            constructDataForTable(key, value);
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

function constructMultiSelect(){
    // Callback that creates and populates a data table,
    // instantiates the pie chart, passes in the data and
    // draws it.

    var data = [
        {label: "All Metrics", value: "All Metrics"},
        {label: "Support Metrics", value: "Support Metrics"},
        {label: "Lead Sources", value: "Lead Sources"}
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

$(function(){

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

    $("#perish-form").submit(function(eventObj) {
        eventObj.preventDefault();
        var mobile = $('#perishableMobile').val();
        if (mobile != null && mobile.length == 10) {
            executeAlphaRequest(mobile);
        }
    });
    document.querySelector('#queryBtn').addEventListener('click', function () {
        document.querySelector('.mdl-layout__obfuscator').classList.remove('is-visible');
        document.querySelector('.mdl-layout__drawer').classList.remove('is-visible');
    }, false);

    $("#globalStatsForm").submit(function(eventObj) {
        eventObj.preventDefault();
        queryForm();
    });
});

