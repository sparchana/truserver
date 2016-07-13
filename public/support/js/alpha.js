
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

function renderAnalyticsResult(analyticsResult) {
    console.log(JSON.stringify(analyticsResult));
    if(analyticsResult != null){
        $('#totalNumberOfCandidate').text(analyticsResult.totalNumberOfCandidate);
    }
}

function queryForm() {
    var d = {
        fromThisDate: $('#fromThisDate').val(),
        toThisDate: $('#toThisDate').val(),
        queryIndex: $('#selectMultiSelect').val()
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
    var data = [
        {label: "Candidate", value: "All Metrics"},
        {label: "Lead", value: "Support Metrics"},
        {label: "Interaction", value: "Lead Sources"}
    ];

    var selectList = $('#queryMultiSelect');
    selectList.multiselect({
        includeSelectAllOption: true,
        maxHeight: 300
    });
    selectList.multiselect('dataprovider', data);
    selectList.multiselect('rebuild');
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

