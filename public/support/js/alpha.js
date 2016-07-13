
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

function constructTable(key, value) {
    console.log("constructTable for Metrics " + JSON.stringify(value));
    if(value != null){
        $.each( value, function( key, value ) {
            console.log( key + ": " + value );
        });
    }
}

function renderAnalyticsResult(analyticsResult) {
    console.log(JSON.stringify(analyticsResult));
    if(analyticsResult != null){
        $.each( analyticsResult, function( key, value ) {
            constructTable(key, value);
        });
    }
}

function queryForm() {
    var d = {
        fromThisDate: $('#fromThisDate').val(),
        toThisDate: $('#toThisDate').val(),
        Metrics: $('#queryMultiSelect').val()
    };
    try {
        $.ajax({
            type: "POST",
            url: "/api/alpha/1",
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

function googleChartplot() {
    google.charts.load("current", {packages:["corechart"]});
    google.charts.setOnLoadCallback(drawChart);
}
function drawChart() {
    var data = google.visualization.arrayToDataTable([
        ['Task', 'Hours per Day'],
        ['Work',     11],
        ['Eat',      2],
        ['Commute',  2],
        ['Watch TV', 2],
        ['Sleep',    7]
    ]);

    var options = {
        title: 'My Daily Activities',
        pieHole: 0.4,
    };

    var chart = new google.visualization.PieChart(document.getElementById('donutchart'));
    chart.draw(data, options);
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
    //<script src="/assets/support/js/google-chart-loader.js" type="text/javascript"></script>
    //googleChartplot();

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

