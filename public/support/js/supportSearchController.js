var localityArray = [];
var jobArray = [];
var shouldAddFooter = true;

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
    } else {
        var totalYear = Math.round((parseInt(d)/12)*100)/100;
        /*var yr = Math.floor((parseInt(d)/12)).toString();
        var month =  parseInt(d)%12;
        if(yr == 0){

            return month +" Month";
        } else {
            return yr + " Year " + month +" Month";
        }*/
        return totalYear;
    }
}

function getLanguageKnown(languageKnownList){
    var languageString = [];
    try {
        if(languageKnownList == null || languageKnownList.length<1){
            return "NA";
        }
        languageKnownList.forEach(function (languageKnown) {
            languageString.push('' + languageKnown.language.languageName + ' ('+languageKnown.understanding + ', ' + languageKnown.verbalAbility + ', ' + languageKnown.readWrite+')');
        });
    }catch (err){}
    return languageString;

}

function getLastWithdrawnSalary(salary) {
    if(salary == null){
        return "NA";
    } else {
        return salary;
    }

}

function getGender(gender) {
    if(gender == null){
        return "NA";
    } else if(gender == "0"){
        return "M";
    } else if(gender == "1"){
        return "F";
    }
}

function getEducation(candidateEducation) {
    if(candidateEducation != null){
        if(candidateEducation.education != null){
            return candidateEducation.education.educationName;
        }
    }
    return "NA";
}

function getSkills(skillList) {
    var skills = [];
    if(skillList != null){
        skillList.forEach(function(candidateSkill)
        {
            if(candidateSkill != null) {
                if(candidateSkill.candidateSkillResponse != null) {
                    var resp;
                    if(candidateSkill.candidateSkillResponse == true){
                        resp = "Yes"
                    } else {
                        resp = "No"
                    }
                    skills.push(" " + candidateSkill.skill.skillName + " : " + resp);
                }
            }
        });
        if(skills.length > 0){
            return skills;
        }
    }
    return "NA";
}

function getHomeLocality(locality) {
    if(locality != null){
        return locality.localityName;
    } else {
        return "NA";
    }
}

function getDateTime(value) {
    // 2016-07-20 21:18:07
    /*
     * getUTCMonth(): Returns the month according to the UTC (0 - 11).
     * getUTCFullYear(): Returns the four-digit year according to the UTC.
     */
    var dateTime = new Date(value).getUTCFullYear() +"-"+("0" +(new Date(value).getUTCMonth() + 1)).slice(-2)
        +"-"+("0" +new Date(value).getDate()).slice(-2)+" "+ ("0" + new Date(value).getHours()).slice(-2) + ":"
        + ("0" + new Date(value).getMinutes()).slice(-2) +":"+("0" + new Date(value).getSeconds()).slice(-2) ;
    return dateTime;
}

function getPastOrCurrentCompanyName(jobHistoryList) {
    var expArray = [];
    if(jobHistoryList != null){
        jobHistoryList.forEach(function(candidateExp)
        {
            if(candidateExp != null) {
                if(candidateExp.candidatePastCompany != null) {
                    var hint = candidateExp.currentJob ? " *" : "";
                    expArray.push(" " + candidateExp.candidatePastCompany + hint);
                }
            }
        });
    }
    if(expArray.length > 0){
        return expArray;
    } else {
        return "NA";
    }
}

function getYesNo(assesment) {
    if(assesment != null) {
        if (assesment == '0' || assesment == false) {
            return "No";
        } else {
            return "yes";
        }
    }
    return "NA";
}

function getExperience(candidateExpList) {
    var candidateExpArray = [];
    if(candidateExpList !=  null){
        candidateExpList.forEach(function(candidateExp) {
            if(candidateExp != null) {
                if(candidateExp.jobExpQuestion != null && candidateExp.jobExpResponse != null) {
                    candidateExpArray.push(" " + candidateExp.jobExpQuestion.jobRole.jobName + ": " + candidateExp.jobExpQuestion.expCategory.expCategoryName + ": "+ candidateExp.jobExpResponse.jobExpResponseOption.jobExpResponseOptionName);
                }
            }
        });
        return candidateExpArray;
    }
    return "NA";
}
function getAge(birthday) { // birthday is in milisec
    var ageDifMs = Date.now() - birthday;
    var ageDate = new Date(ageDifMs); // miliseconds from epoch
    return Math.abs(ageDate.getUTCFullYear() - 1970);
}

function getProperProfileStatus(profileStatus) {
    if(profileStatus != null){
        if(profileStatus.profileStatusId == "1"){ // new or active return active
            return "Active";
        } else {
            return profileStatus.profileStatusName;
        }
    }
    return "NA";
}
function getExpiry(expiryObject) {
    if(expiryObject != null){
        return expiryObject.statusExpiryDate;
    }
    return "NA";
}


function getLastName(lastName) {
    if(lastName != null){
        return lastName;
    }
    return "NA";
}
function getFirstName(firstName) {
    if(firstName != null){
        return firstName;
    }
    return "NA";
}

function getIdProof(idProofList) {
    var idProofArray = [];
    if(idProofList !=  null){
        idProofList.forEach(function(candidateIdProof) {
            if(candidateIdProof != null) {
                if(candidateIdProof.idProof != null && candidateIdProof.idProof.idProofName != null) {
                    idProofArray.push(" " + candidateIdProof.idProof.idProofName);
                }
            }
        });
        if(idProofArray.length > 0){
            return idProofArray;
        }
    }
    return "NA";
}


function renderSearchResult(returnedData) {
    var status = returnedData.status;
    var candidateList = returnedData.candidateList;

    if (status == 3) {
        $('#searchErrorMsg').text("Looks like you have exhausted your search limit. Please contact your administrator.");
        $('#searchError').show();
        $('#candidateSearchResultTable').hide();
    }
    else {
        $('#searchError').hide();
        var returnedDataArray = [];

        try {
            candidateList.forEach(function (newCandidate) {
                // prep strings for display

                var timeShiftPref = "";
                var locality = "";
                if (newCandidate.timeShiftPreference != null) {
                    timeShiftPref = newCandidate.timeShiftPreference.timeShift.timeShiftName;
                }
                if (newCandidate.locality != null) {
                    locality = newCandidate.locality.localityName;
                }

                var verificationMap = getVerificationStatus(newCandidate);

                returnedDataArray.push({
                    'cLID': '<a href="/candidateSignupSupport/' + newCandidate.lead.leadId + '/false" target="_blank">' + newCandidate.lead.leadId + '</a>',
                    'candidateFirstName': getFirstName(newCandidate.candidateFirstName) + " " + getLastName(newCandidate.candidateLastName),
                    'candidateMobile': newCandidate.candidateMobile,
                    'candidateLastWithdrawnSalary': getLastWithdrawnSalary(newCandidate.candidateLastWithdrawnSalary),
                    'candidateJobPref': getJobPref(newCandidate.jobPreferencesList),
                    'candidateLocalityPref': getLocalityPref(newCandidate.localityPreferenceList),
                    'locality': getHomeLocality(newCandidate.locality),
                    'candidateLanguage': getLanguageKnown(newCandidate.languageKnownList),
                    'candidateEducation': getEducation(newCandidate.candidateEducation),
                    'candidateSkillList': getSkills(newCandidate.candidateSkillList),
                    'candidateTimeShiftPref': timeShiftPref,
                    'candidateExperience': getInYearMonthFormat(newCandidate.candidateTotalExperience),
                    'candidateIsAssessmentComplete': getYesNo(newCandidate.candidateIsAssessed),
                    'candidateGender': getGender(newCandidate.candidateGender),
                    'candidateIsEmployed': getYesNo(newCandidate.candidateIsEmployed),
                    'candidateCreateTimestamp': getDateTime(newCandidate.candidateCreateTimestamp),
                    'pastOrCurrentCompanyName': getPastOrCurrentCompanyName(newCandidate.jobHistoryList),
                    'isMinProfileComplete': getYesNo(newCandidate.isMinProfileComplete),
                    'followUp': getYesNo(newCandidate.lead.followUp),
                    'noOfJobApplication': newCandidate.jobApplicationList.length,
                    'experience': getExperience(newCandidate.candidateExpList),
                    'age': getAge(newCandidate.candidateDOB),
                    'candidateExperienceLetter': getYesNo(newCandidate.candidateExperienceLetter),
                    'isActive': getProperProfileStatus(newCandidate.candidateprofilestatus),
                    'candidateExpiry': getExpiry(newCandidate.candidateStatusDetail),
                    'candidateIdProofs': getIdProof(newCandidate.idProofReferenceList),
                    'candidateAadhaarNumber': getAadhaarNumber(newCandidate.idProofReferenceList),
                    'candidateNameVerified': verificationMap["Name"],
                    'candidatePhoneVerified': verificationMap["Phone"],
                    'candidateDOBVerified': verificationMap["DOB"],
                    'candidateGenderVerified': verificationMap["Gender"],
                    'candidateVerifyNow': '<input style="margin-left: 6px" type="button" value="Verify" onclick="verifyAadhaar('+ newCandidate.candidateMobile + ')">'
                })
            });

            $('#candidateSearchResultTable').show();

            var table = $('table#candidateSearchResultTable').DataTable({
                "data": returnedDataArray,
                "order": [[15, "desc"]],
                "scrollX": true,
                "columns": [
                    {"data": "cLID"},
                    {"data": "candidateFirstName"},
                    {"data": "candidateMobile"},
                    {"data": "candidateJobPref"},
                    {"data": "candidateLocalityPref"},
                    {"data": "locality"},
                    {"data": "candidateExperience"},
                    {"data": "candidateIsEmployed"},
                    {"data": "candidateLastWithdrawnSalary"},
                    {"data": "candidateLanguage"},
                    {"data": "candidateEducation"},
                    {"data": "candidateSkillList"},
                    {"data": "candidateGender"},
                    {"data": "candidateIsAssessmentComplete"},
                    {"data": "candidateTimeShiftPref"},
                    {"data": "candidateCreateTimestamp"},
                    {"data": "pastOrCurrentCompanyName"},
                    {"data": "isMinProfileComplete"},
                    {"data": "followUp"},
                    {"data": "noOfJobApplication"},
                    {"data": "experience"},
                    {"data": "age"},
                    {"data": "candidateExperienceLetter"},
                    {"data": "isActive"},
                    {"data": "candidateExpiry"},
                    {"data": "candidateIdProofs"},
                    {"data": "candidateAadhaarNumber"},
                    {"data": "candidateNameVerified"},
                    {"data": "candidatePhoneVerified"},
                    {"data": "candidateDOBVerified"},
                    {"data": "candidateGenderVerified"},
                    {"data": "candidateVerifyNow"}
                ],
                "deferRender": true,
                "scroller": true,
                "scrollY": '48vh',
                "scrollCollapse": true,
                "language": {
                    "emptyTable": "No data available"
                },
                "destroy": true,
                "dom": 'Bfrtip',
                "buttons": [
                    'copy', 'csv', 'excel'
                ]
            });

            // Apply the search filter
            table.columns().every(function () {
                var that = this;
                $('input', this.footer()).on('keyup change', function () {
                    if (that.search() !== this.value) {
                        that
                            .search(this.value)
                            .draw();
                    }
                });
            });

            /* Initialise datatables */
            $.fn.dataTable.moment('dd/MM/YYYY HH:mm:ss');

            var oTable = $('#candidateSearchResultTable').dataTable();

            /* Add event listeners to the two range filtering inputs */
            $('#minExp').keyup(function () {
                oTable.fnDraw();
            });
            $('#maxExp').keyup(function () {
                oTable.fnDraw();
            });
            $('#minSal').keyup(function () {
                oTable.fnDraw();
            });
            $('#maxSal').keyup(function () {
                oTable.fnDraw();
            });
            $('#minJobApplication').keyup(function () {
                oTable.fnDraw();
            });
            $('#maxJobApplication').keyup(function () {
                oTable.fnDraw();
            });
            $('#minAge').keyup(function () {
                oTable.fnDraw();
            });
            $('#maxAge').keyup(function () {
                oTable.fnDraw();
            });

        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }
    NProgress.done();
}

function searchForm(){
    var localityArray = [];
    var jobArray = [];
    
    var i;
    var searchJob = $('#candidateJobPref').val().split(",");
    var searchLocality = $('#candidateLocalityPref').val().split(",");
    
    for(i=0;i<searchJob.length;i++){
        if (searchJob[i] != '') {
            jobArray.push(searchJob[i]);
        }
    }

    for(i=0;i<searchLocality.length;i++){
        if (searchLocality[i] != '') {
            localityArray.push(searchLocality[i]);
        }
    }
    /* ajax commands to fetch all localities and jobs*/
    var d = {
        candidateFirstName: $('#candidateFirstName').val(),
        candidateMobile: $('#candidateMobile').val(),
        candidateLocality: localityArray,
        candidateJobInterest: jobArray,
        fromThisDate: $('#fromThisDate').val(),
        toThisDate: $('#toThisDate').val(),
        languageKnown: $('#languageMultiSelect').val(),
        idProofs: $('#idProofMultiSelect').val()
    };
    if(d.fromThisDate > d.toThisDate){
        $.notify({
            title: "Invalid Date Input: ",
            message: "Selected Date Range is Incorrect. Please Select valid (from/to) Date",
            animate: {
                enter: 'animated lightSpeedIn',
                exit: 'animated lightSpeedOut'
            }
        },{
            type: 'danger'
        });

        shouldAddFooter = false;
    } else {
        shouldAddFooter = true;
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
}

/* Custom filtering function which will filter data in column */
$.fn.dataTableExt.afnFiltering.push(
    function( oSettings, aData, iDataIndex ) {
        var iMinExp = document.getElementById('minExp').value * 1;
        var iMaxExp = document.getElementById('maxExp').value * 1;
        var iExpColumnVal = aData[6] == "-" ? 0 : aData[6]*1;
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
        var iSalaryColumnVal = aData[8] == "-" ? 0 : aData[8]*1;
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
    function (oSettings, aData, iDataIndex) {
        var iMinJobApp = $('#minJobApplication').val();
        var iMaxJobApp = $('#maxJobApplication').val();
        var iJobAppColumnVal = aData[19] == "-" ? 0 : aData[19]*1;
        if ( iMinJobApp == "" && iMaxJobApp == "" ){
           return true;
        }
        else if ( iMinJobApp == "" && iJobAppColumnVal <= iMaxJobApp ) {
            return true;
        }
        else if ( iMinJobApp <= iJobAppColumnVal && "" == iMaxJobApp ) {
            return true;
        }
        else if ( iMinJobApp <= iJobAppColumnVal && iJobAppColumnVal <= iMaxJobApp ) {
            return true;
        }
    },
    function (oSettings, aData, iDataIndex) {
        var iMinAge = $('#minAge').val();
        var iMaxAge = $('#maxAge').val();
        var iAgeColumnVal = aData[21] == "-" ? 0 : aData[21]*1;
        if ( iMinAge == "" && iMaxAge == "" ){
           return true;
        }
        else if ( iMinAge == "" && iAgeColumnVal <= iMaxAge ) {
            return true;
        }
        else if ( iMinAge <= iAgeColumnVal && "" == iMaxAge ) {
            return true;
        }
        else if ( iMinAge <= iAgeColumnVal && iAgeColumnVal <= iMaxAge ) {
            return true;
        }
    }
);

function processLanguage(returnLanguage) {
    if(returnLanguage != null){
        var data = [];

        returnLanguage.forEach(function (language) {
            var opt = {
                label: language.languageName, value: parseInt(language.languageId)
            };
            data.push(opt);
        });

        var selectList = $('#languageMultiSelect');
        selectList.multiselect({
            includeSelectAllOption: true,
            maxHeight: 300
        });
        selectList.multiselect('dataprovider', data);
        selectList.multiselect('rebuild');
    }
}

function processIdProof(responseData) {
    if(responseData != null){
        var data = [];

        responseData.forEach(function (idproof) {
            var opt = {
                label: idproof.idProofName, value: parseInt(idproof.idProofId)
            };
            data.push(opt);
        });

        var selectList = $('#idProofMultiSelect');
        selectList.multiselect({
            includeSelectAllOption: true,
            maxHeight: 300
        });
        selectList.multiselect('dataprovider', data);
        selectList.multiselect('rebuild');
    }
}

function getAadhaarNumber(idProofList) {
    var returnVal = "N/A"
    try {
        if (idProofList != null) {
            idProofList.forEach(function (idProofRef) {
                if (idProofRef.idProof.idProofId == 3) {
                    if (idProofRef.idProofNumber != 'undefined') {
                        returnVal = idProofRef.idProofNumber;
                    }
                }
            });
        }
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    return returnVal;
}

function getVerificationStatus(candidate) {

    var verificationList = candidate.candidateVerificationList;
    var resultMap = new Object();
    resultMap["Name"] = "N/A";
    resultMap["Phone"] = "N/A";
    resultMap["DOB"] = "N/A";
    resultMap["Gender"] = "N/A";

    if (verificationList != null && verificationList.length > 0) {

        verificationList.forEach(function (verificationResult) {
            if (verificationResult.ongridField.fieldId == 3) {
                resultMap["Name"] = verificationResult.ongridVerificationStatus.statusName;
            }
            if (verificationResult.ongridField.fieldId == 4) {
                resultMap["Phone"] = verificationResult.ongridVerificationStatus.statusName;
            }
            if (verificationResult.ongridField.fieldId == 5) {
                resultMap["DOB"] = verificationResult.ongridVerificationStatus.statusName;
            }
            if (verificationResult.ongridField.fieldId == 9) {
                resultMap["Gender"] = verificationResult.ongridVerificationStatus.statusName;
            }
        });
    }

    return resultMap;
}

function verifyAadhaar(candidateMobile) {
    NProgress.start();
    try {
        $.ajax({
            type: "POST",
            crossDomain: true,
            url: "/api/compute/verifyAadhar/" + candidateMobile,
            async: true,
            success: handleAadharVerificationResponse
        });
    }
    catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function handleAadharVerificationResponse(response) {
    var msg = response.responseMessage;
    $.notify({
        title: "Verification response: ",
        message: msg + ". Refreshing search results",
        animate: {
            enter: 'animated lightSpeedIn',
            exit: 'animated lightSpeedOut'
        }
    },{
        type: 'danger'
    });

    $("#searchBtn").trigger("click");

    NProgress.done();
}

$(function() {
    // create multiselect for languages
    try {
        $.ajax({
            type: "POST",
            url: "/getAllLanguage",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processLanguage
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    // create multiselect for idproof
    try {
        $.ajax({
            type: "POST",
            url: "/getAllIdProof",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processIdProof
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

    /* ajax commands to fetch all localities and jobs*/
    NProgress.start();
    try {
        $.ajax({
            type: "POST",
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
            type: "POST",
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
        // Setup - add a text input to each footer cell
        if(shouldAddFooter){
            $('#candidateSearchResultTable tfoot th').each( function () {
                var title = $(this).text();
                $(this).html( '<input type="text" name="'+title+'"  id="'+title+'" placeholder="'+title+'" />' );
            });
        }
    }); // end of submit


    $( "#toThisDate").datepicker({ dateFormat: 'yy-mm-dd', changeYear: true});
    $( "#fromThisDate").datepicker({ dateFormat: 'yy-mm-dd', changeYear: true});

});