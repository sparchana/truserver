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
    NProgress.done();
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
    NProgress.done();
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
    var jobString = [];
    try{
        jobPrefList.forEach(function (individualJob){
            var name = individualJob.jobRole.jobName;
            jobString.push(" " + name);
        });
    } catch (err){
    }
    return jobString;
}

function getLocalityPref(localityPrefList) {
    var localityString = [];
    try {
        localityPrefList.forEach(function (individualLocality) {
            var name = individualLocality.locality.localityName;
            localityString.push(" " + name);
        });
    }catch (err){}
    return localityString;
}

function getInYearMonthFormat(d){
    if(d == null) {
        return "NA";
    } else if(d == 0 ){
        return "0 Month";
    } else {
        var yr = Math.floor((parseInt(d)/12)).toString();
        var month =  parseInt(d)%12;
        if(yr == 0){
            return month +" Month";
        } else {
            return yr + " Year " + month +" Month";
        }
    }
}

function getLanguageKnown(languageKnownList){
    var languageString = [];
    try {
        if(languageKnownList == null){
            return "NA";
        }
        languageKnownList.forEach(function (languageKnown) {
            languageString.push(" " + languageKnown.language.languageName);
        });
    }catch (err){}
    return languageString;
}

function getCurrentSalary(salary) {
    if(salary == null){
        return "NA";
    } else {
        return salary.candidateCurrentSalary;
    }

}

function renderSearchResult(returnedData) {
    var returnedDataArray = new Array();
    try {
        returnedData.forEach(function (newCandidate) {
            // prep strings for display

            var timeShiftPref = "";
            var locality = "";
            if(newCandidate.timeShiftPreference != null){
                timeShiftPref = newCandidate.timeShiftPreference.timeShift.timeShiftName;
            }
            if(newCandidate.locality != null){
                locality = newCandidate.locality.localityName;
            }

            returnedDataArray.push({
                'cLID': '<a href="/candidateSignupSupport/'+newCandidate.lead.leadId+'">'+newCandidate.lead.leadId+'</a>',
                'candidateName' : newCandidate.candidateName,
                'candidateMobile' : newCandidate.candidateMobile,
                'candidateHomeLocality' : locality,
                'candidateCurrentSalary' : getCurrentSalary(newCandidate.candidateCurrentJobDetail),
                'candidateJobPref' :  getJobPref(newCandidate.jobPreferencesList),
                'candidateLocalityPref'  :getLocalityPref(newCandidate.localityPreferenceList),
                'candidateLanguage' : getLanguageKnown(newCandidate.languageKnownList),
                'candidateTimeShiftPref' : timeShiftPref,
                'candidateExperience' :  getInYearMonthFormat(newCandidate.candidateTotalExperience),
                'candidateIsAssessmentComplete' : function(){
                    if(newCandidate.candidateIsAssessed == '0') {
                        return "No";
                    } else {
                        return "yes";
                    }
                }
            })
        });

        var table = $('table#candidateSearchResultTable').DataTable({
            "data": returnedDataArray,
            "columns": [
                { "data": "cLID" },
                { "data": "candidateName" },
                { "data": "candidateMobile" },
                { "data": "candidateHomeLocality" },
                { "data": "candidateCurrentSalary" },
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
            "destroy": true,
            "paging": false
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    NProgress.done();
}


function searchForm(){
    /* ajax commands to fetch all localities and jobs*/
    var d = {
        candidateName: $('#candidateName').val(),
        candidateMobile: $('#candidateMobile').val(),
        candidateLocality: $('#candidateLocalityPref').val(),
        candidateJobInterest: $('#candidateJobPref').val()
    };
    NProgress.start();
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
    $("#filterColumn").click(function() {
        if ( $.fn.dataTable.isDataTable( 'table#candidateSearchResultTable' ) ) {
            var aTable = $('table#candidateSearchResultTable').DataTable();
            if ($("#filterColumn").is(":checked")) {
                aTable.filter(function ( value, index ) {
                });
            } else {
                aTable.filter(function ( value, index ) {
                });
            }
        }
    });
    /* ajax commands to fetch all localities and jobs*/
    NProgress.start();
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
    $("#candidateSearchForm").change(function(eventObj) {
        eventObj.preventDefault();
        searchForm();
    }); // end of auto search

    $("#candidateSearchForm").submit(function(eventObj) {
        eventObj.preventDefault();
        searchForm();
    }); // end of submit
});