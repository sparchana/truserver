var localityArray = [];
var jobArray = [];

function processDataCheckJobs(returnedData) {
    returnedData.forEach(function(job)
    {
        var id = job.jobRoleId;
        var name = job.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        jobArray.push(item);
    });
}

function processDataCheckLocality(returnedData) {
    returnedData.forEach(function(locality)
    {
        var id = locality.localityId;
        var name = locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        localityArray.push(item);
    });
}

function getLocality(){
    return localityArray;
}

function getJob(){
    return jobArray;
}

function recreateDataTable(){
    NProgress.start();
    if ( $.fn.dataTable.isDataTable( 'table#candidateSearchResultTable' ) ) {
        $('table#candidateSearchResultTable').DataTable().clear();
    }
    renderSearchResult();
    NProgress.done();
}

function getJobPref(jobPrefList) {
    var jobString = "";
    try{
        jobPrefList.forEach(function (individualJob){
            var name = individualJob.jobRole.jobName;
            jobString = name + ", ";
        });
    } catch (err){
    }
    return jobString;
}

function getLocalityPref(localityPrefList) {
    var localityString = "";
    try {
        localityPrefList.forEach(function (individualLocality) {
            var name = individualLocality.locality.localityName;
            localityString = name + ", ";
        });
    }catch (err){}
    return localityString;
}

function getLanguageKnown(languageKnownList){
    var languageKnown = "";
    try {
        if(languageKnownList == null){
            return "NA";
        }
        languageKnownList.forEach(function (languageKnown) {
            languageKnown = languageKnown.language.languageId + ",";
        });
    }catch (err){

    }
};

function renderSearchResult(returnedData) {
    var returnedDataArray = new Array();
    try {
        returnedData.forEach(function (newCandidate) {
            // prep strings for display

            var languagePref = [];
            var timeShiftPref = [];
            var jobRolePref = [];

            returnedDataArray.push({
                'cLID': '<a href="/candidateSignupSupport/'+newCandidate.lead.leadId+'">'+newCandidate.lead.leadId+'</a>',
                'candidateName' : newCandidate.candidateName,
                'candidateMobile' : newCandidate.candidateMobile,
                'candidateHomeLocality' : newCandidate.locality,
                'candidateJobPref' :  getJobPref(newCandidate.jobPreferencesList),
                'candidateLocalityPref'  :getLocalityPref(newCandidate.localityPreferenceList),
                'candidateLanguage' : function(){
                    if(returnedData.languageKnownList != null) {
                        return getLanguageKnown(newCandidate.languageKnownList)
                    } else {
                        return ""
                    }
                },
                'candidateTimeShiftPref' : newCandidate.timeShiftPreference.timeShift.timeShiftName,
                'candidateExperience' :  newCandidate.candidateTotalExperience,
                'candidateIsAssessmentComplete' : function(){
                    if(newCandidate.candidateIsAssessed == '0') {
                        return "No";
                    } else {
                        return "yes";
                    }
                }
            })
        });

        console.log("Sandy: render fired with data");

        var table = $('table#candidateSearchResultTable').DataTable({
            "data": returnedDataArray,
            "columns": [
                { "data": "cLID" },
                { "data": "candidateName" },
                { "data": "candidateMobile" },
                { "data": "candidateHomeLocality" },
                { "data": "candidateJobPref" },
                { "data": "candidateLocalityPref" },
                { "data": "candidateLanguage" },
                { "data": "candidateTimeShiftPref" },
                { "data": "candidateExperience" },
                { "data": "candidateIsAssessmentComplete" }
            ],
            "deferRender": true,
            "language": {
                "emptyTable": "No data available"
            },
            "destroy": true
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}


function searchForm(){
    /* ajax commands to fetch all localities and jobs*/
    var d = {
        candidateName: $('#candidateName').val(),
        candidateMobile: $('#candidateMobile').val(),
        candidateLocality: $('#candidateLocalityPref').val(),
        candidateJobInterest: $('#candidateJobPref').val()
    };
    try {
        $.ajax({
            type: "POST",
            url: "/getSearchCandidateResult",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: renderSearchResult
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

$(function() {
    /* ajax commands to fetch all localities and jobs*/
    try {
        $.ajax({
            type: "GET",
            url: "/getAllLocality",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckLocality
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    try {
        $.ajax({
            type: "GET",
            url: "/getAllJobs",
            data: false,
            contentType: false,
            processData: false,
            success: processDataCheckJobs
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    $("#candidateSearchForm").submit(function(eventObj) {
        eventObj.preventDefault();
        searchForm();
    }); // end of submit
});