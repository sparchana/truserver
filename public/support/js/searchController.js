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
        return "-";
    } else {
        /*var yr = Math.floor((parseInt(d)/12)).toString();
        var month =  parseInt(d)%12;
        if(yr == 0){
            return month +" Month";
        } else {
            return yr + " Year " + month +" Month";
        }*/
        return d;
    }
}

function getLanguageKnown(languageKnownList){
    var languageString = [];
    try {
        if(languageKnownList == null){
            return "-";
        }
        languageKnownList.forEach(function (languageKnown) {
            languageString.push(" " + languageKnown.language.languageName);
        });
    }catch (err){}
    return languageString;
}

function getCurrentSalary(salary) {
    if(salary == null){
        return "-";
    } else {
        return salary.candidateCurrentSalary;
    }

}

function getGender(gender) {
    if(gender == null){
        return "-";
    } else if(gender == "0"){
        return "Male";
    } else if(gender == "1"){
        return "Female";
    }
}

function getEducation(candidateEducation) {
    if(candidateEducation != null){
        if(candidateEducation.education != null){
            return candidateEducation.education.educationName;
        }
    }
    return "-";
}

function getEmploymentStatus(candidateEmploymentStatus) {
    if(candidateEmploymentStatus != null){
        if(candidateEmploymentStatus == "0"){
            return "No";
        }
         else if(candidateEmploymentStatus == "1"){
            return "Yes";
        }
    }
    return "-";
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
                'candidateEducation' : getEducation(newCandidate.candidateEducation),
                'candidateTimeShiftPref' : timeShiftPref,
                'candidateExperience' :  getInYearMonthFormat(newCandidate.candidateTotalExperience),
                'candidateIsAssessmentComplete' : function(){
                    if(newCandidate.candidateIsAssessed == '0') {
                        return "No";
                    } else {
                        return "yes";
                    }
                },
                'candidateGender' : getGender(newCandidate.candidateGender),
                'candidateIsEmployed' : getEmploymentStatus(newCandidate.candidateIsEmployed)
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
                { "data": "candidateEducation" },
                { "data": "candidateTimeShiftPref" },
                { "data": "candidateExperience" },
                { "data": "candidateIsAssessmentComplete" },
                { "data": "candidateGender" },
                { "data": "candidateIsEmployed" }
            ],
            "deferRender": true,
            "language": {
                "emptyTable": "No data available"
            },
            "destroy": true
        });
        // Setup - add a text input to each footer cell
        $('#candidateSearchResultTable tfoot th').each( function () {
            var title = $(this).text();
            $(this).html( '<input type="text" placeholder="Search '+title+'" />' );
        } );

        // Apply the search
        table.columns().every( function () {
            var that = this;
            $( 'input', this.footer() ).on( 'keyup change', function () {
                if ( that.search() !== this.value ) {
                    that
                        .search( this.value )
                        .draw();
                }
            } );
        } );
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
/* Custom filtering function which will filter data in column */
$.fn.dataTableExt.afnFiltering.push(
    function( oSettings, aData, iDataIndex ) {
        var iMinExp = document.getElementById('minExp').value * 1;
        var iMaxExp = document.getElementById('maxExp').value * 1;
        var iExpColumnVal = aData[10] == "-" ? 0 : aData[10]*1;
        if ( iMinExp == "" && iMaxExp == "" )
        {
            return true;
        }
        else if ( iMinExp == "" && iExpColumnVal <= iMaxExp )
        {
            return true;
        }
        else if ( iMinExp <= iExpColumnVal && "" == iMaxExp )
        {
            return true;
        }
        else if ( iMinExp <= iExpColumnVal && iExpColumnVal <= iMaxExp )
        {
            return true;
        }
        return false;
    },
    function( oSettings, aData, iDataIndex ) {
        var iMinSalary = document.getElementById('minSal').value * 1;
        var iMaxSalary = document.getElementById('maxSal').value * 1;
        var iSalaryColumnVal = aData[4] == "-" ? 0 : aData[4]*1;
        if ( iMinSalary == "" && iMaxSalary == "" )
        {
            return true;
        }
        else if ( iMinSalary == "" && iSalaryColumnVal <= iMaxSalary )
        {
            return true;
        }
        else if ( iMinSalary <= iSalaryColumnVal && "" == iMaxSalary )
        {
            return true;
        }
        else if ( iMinSalary <= iSalaryColumnVal && iSalaryColumnVal <= iMaxSalary )
        {
            return true;
        }
        return false;
    },
    function( oSettings, aData, iDataIndex ) {
        var isAssessed = $('#isAssessed').val();
        if($('#isAssessed').val() == "All") {
            return true;
        }
        var iAssessedColumnVal = aData[11];
        if ( isAssessed == "" && iAssessedColumnVal == "" )
        {
            return true;
        }
        else if ( isAssessed == iAssessedColumnVal)
        {
            return true;
        }
        return false;
    },
    function( oSettings, aData, iDataIndex ) {
        var iGender = $('#gender').val();
        var iGenderColumnVal = aData[12];
        if($('#gender').val() == "All") {
            return true;
        }
        if ( iGender == "" && iGenderColumnVal == "" )
        {
            return true;
        }
        else if ( iGender == iGenderColumnVal)
        {
            return true;
        }
        return false;
    },
    function( oSettings, aData, iDataIndex ) {
        var iEmployed = $('#employed').val();
        var iEmployedColumnVal = aData[13];
        if($('#employed').val() == "All") {
            return true;
        }
        if ( iEmployed == "" && iEmployedColumnVal  == "" )
        {
            return true;
        }
        else if ( iEmployed == iEmployedColumnVal )
        {
            return true;
        }
        return false;
    }
);

$(function() {
    /* Initialise datatables */
    var oTable = $('#candidateSearchResultTable').dataTable();

    /* Add event listeners to the two range filtering inputs */
    $('#minExp').keyup( function() { oTable.fnDraw(); } );
    $('#maxExp').keyup( function() { oTable.fnDraw(); } );
    $('#minSal').keyup( function() { oTable.fnDraw(); } );
    $('#maxSal').keyup( function() { oTable.fnDraw(); } );
    $('#isAssessed').on('change', function() {
        oTable.fnDraw();
    });
    $('#gender').on('change', function() {
            oTable.fnDraw();
    });    
    $('#employed').on('change', function() {
            oTable.fnDraw();
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