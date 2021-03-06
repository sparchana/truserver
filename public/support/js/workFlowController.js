/**
 * Created by zero on 07/10/16.
 */
var globalRecAgentNumber;
var allLocalityArray = [];
var allJobArray = [];
var shouldAddFooter = true;
var jobPostId;
var gJobRoleId;

//interview values
var globalCandidateId;
var globalInterviewStatus;
var globalInterviewDate;
var rescheduledDate;
var rescheduledSlot;

var globalAcceptanceStatus;

var jobPostInfo;

var allTimeSlots = [];
var allReasons = [];
var allNotGoingReasons = [];
var allCandidateEta = [];
var allRejectReason = [];

var triggerNotGoingModal = false;
var triggerEtaModal = false;

var globalCandidateStatus;
var startDate = null;
var endDate = null;

function getLocality() {
    return allLocalityArray;
}

function getJob() {
    return allJobArray;
}

function getAppliedOn(data) {
    if (data != null) {
        return data;
    } else {
        return "NA";
    }
}
function getJobPref(jobPrefList) {
    var jobString = [];
    try {
        jobPrefList.forEach(function (individualJob) {
            var name = individualJob.jobRole.jobName;
            jobString.push(" " + name);
        });
    } catch (err) {
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
    } catch (err) {
    }
    return localityString;
}

function getInYearMonthFormat(d) {
    if (d == null) {
        return "NA";
    } else {
        var totalYear = Math.round((parseInt(d) / 12) * 100) / 100;
        return totalYear;
    }
}

function getLanguageKnown(languageKnownList) {
    var languageString = [];
    try {
        if (languageKnownList == null || languageKnownList.length < 1) {
            return "NA";
        }
        languageKnownList.forEach(function (languageKnown) {
            languageString.push('' + languageKnown.language.languageName + ' (' + languageKnown.understanding + ', ' + languageKnown.verbalAbility + ', ' + languageKnown.readWrite + ')');
        });
    } catch (err) {
    }
    return languageString;

}

function getLastWithdrawnSalary(salary) {
    if (salary == null) {
        return "NA";
    } else {
        return salary;
    }

}

function getGender(gender) {
    if (gender == null) {
        return "NA";
    } else if (gender == "0") {
        return "M";
    } else if (gender == "1") {
        return "F";
    }
}

function getEducation(candidateEducation) {
    if (candidateEducation != null) {
        if (candidateEducation.education != null) {
            return candidateEducation.education.educationName;
        }
    }
    return "NA";
}
function getDegree(candidateDegree) {
    if (candidateDegree != null) {
        if (candidateDegree.degree!= null) {
            return candidateDegree.degree.degreeName;
        }
    }
    return "NA";
}
function getDegreeCompletion(candidateEducation) {
    if (candidateEducation != null) {
        if (candidateEducation.candidateEducationCompletionStatus!= null) {
            if(candidateEducation.candidateEducationCompletionStatus == "1") {
                return "Yes";
            } else {
                return "No";
            }
        }
    }
    return "NA";
}

function getSkills(skillList) {
    var skills = [];
    if (skillList != null) {
        skillList.forEach(function (candidateSkill) {
            if (candidateSkill != null) {
                if (candidateSkill.candidateSkillResponse != null) {
                    var resp;
                    if (candidateSkill.candidateSkillResponse == true) {
                        resp = "Yes"
                    } else {
                        resp = "No"
                    }
                    skills.push(" " + candidateSkill.skill.skillName + " : " + resp);
                }
            }
        });
        if (skills.length > 0) {
            return skills;
        }
    }
    return "NA";
}

function getHomeLocality(locality) {
    if (locality != null) {
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
    var dateTime = new Date(value).getUTCFullYear() + "-" + ("0" + (new Date(value).getUTCMonth() + 1)).slice(-2)
        + "-" + ("0" + new Date(value).getDate()).slice(-2) + " " + ("0" + new Date(value).getHours()).slice(-2) + ":"
        + ("0" + new Date(value).getMinutes()).slice(-2) + ":" + ("0" + new Date(value).getSeconds()).slice(-2);
    return dateTime;
}

function getPastOrCurrentCompanyName(jobHistoryList) {
    var expArray = [];
    if (jobHistoryList != null) {
        jobHistoryList.forEach(function (candidateExp) {
            if (candidateExp != null) {
                if (candidateExp.candidatePastCompany != null) {
                    var hint = candidateExp.currentJob ? " *" : "";
                    expArray.push(" " + candidateExp.candidatePastCompany + hint);
                }
            }
        });
    }
    if (expArray.length > 0) {
        return expArray;
    } else {
        return "NA";
    }
}

function getYesNo(assesment) {
    if (assesment != null) {
        if (assesment == '0' || assesment == false) {
            return "No";
        } else {
            return "Yes";
        }
    }
    return "NA";
}

function getExperience(candidateExpList) {
    var candidateExpArray = [];
    if (candidateExpList != null) {
        candidateExpList.forEach(function (candidateExp) {
            if (candidateExp != null) {
                if (candidateExp.jobExpQuestion != null && candidateExp.jobExpResponse != null) {
                    candidateExpArray.push(" " + candidateExp.jobExpQuestion.jobRole.jobName + ": " + candidateExp.jobExpQuestion.expCategory.expCategoryName + ": " + candidateExp.jobExpResponse.jobExpResponseOption.jobExpResponseOptionName);
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
    if (profileStatus != null) {
        if (profileStatus.profileStatusId == "1") { // new or active return active
            return "Active";
        } else {
            return profileStatus.profileStatusName;
        }
    }
    return "NA";
}
function getExpiry(expiryObject) {
    if (expiryObject != null) {
        return expiryObject.statusExpiryDate;
    }
    return "NA";
}


function getLastName(lastName) {
    if (lastName != null) {
        return lastName;
    }
    return "NA";
}
function getFirstName(firstName) {
    if (firstName != null) {
        return firstName;
    }
    return "NA";
}

function getIdProof(idProofList) {
    var idProofArray = [];
    if (idProofList != null) {
        idProofList.forEach(function (candidateIdProof) {
            if (candidateIdProof != null) {
                if (candidateIdProof.idProof != null && candidateIdProof.idProof.idProofName != null) {
                    idProofArray.push(" " + candidateIdProof.idProof.idProofName);
                }
            }
        });
        if (idProofArray.length > 0) {
            return idProofArray;
        }
    }
    return "NA";
}

openPreScreenModal = function (mobile, candidateId) {
    // pass null to have default modal decorator
    getPreScreenContent(jobPostId, candidateId, false, null, null, true);
};

function showRadiusValue(value){
    document.getElementById("radiusValue").innerHTML = value;
}

callHandler = function (mobile, candidateId) {
    openPreScreenModal(mobile, candidateId);
};

confirmInterview = function (candidateId, date, slot) {
    globalCandidateId = candidateId;
    globalInterviewStatus = "1";

    var inDateFormat = new Date(date);
    var oldDate = inDateFormat.getFullYear() + "-" + (inDateFormat.getMonth() + 1) + "-" + inDateFormat.getDate();

    rescheduledDate = oldDate;
    rescheduledSlot = slot;

    setInterviewStatus(globalCandidateId, globalInterviewStatus, rescheduledDate, rescheduledSlot, null);
};

function confirmInterview(jpId, status) {
}

acceptRescheduledInterview = function (candidateId, status) {
    globalCandidateId = candidateId;
    globalAcceptanceStatus = status;

    try {
        $.ajax({
            type: "POST",
            url: "/confirmInterviewSupport/" + candidateId + "/" + jobPostId + "/" + status,
            async: true,
            contentType: false,
            data: false,
            success: processDataConfirmInterview
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
};

function processDataConfirmInterview(returnedData) {
    if(returnedData != 0){
        notifyError("Request successful. Refreshing view ..", 'success');
        setTimeout(function () {
            location.reload();
        }, 2000);
    } else{
        notifyError("Something went wrong. Please try again later", 'danger');
    }

}

$(function () {
    'use strict';
    var app = {
        jpId: null,
        jpJobRoleId: 0,
        jpMinSalary: null,
        jpMaxSalary: null,
        jpMaxAge: null,
        jpGender: null,
        jpLocalityIdList: null,
        jpExperienceIdList: [],
        jpEducationIdList: [],
        jpLanguageIdList: [],
        jpTableFormattedCandidateList: [],
        jpDocumentIdList: [],
        jpAssetIdList: [],
        jpCandidateList: [],
        jpTotalExpInMonths: null,
        tableContainer: $('#candidateResultTable'),
        tableContainerId: 'candidateResultTable',
        formSubmit: $('#candidateMatchForm'),
        url: "/support/api/getMatchingCandidate/",
        view: "match_view",
        shouldSend: true,
        table: null,
        jpSelectedCandidateList: [],
        urlSaveSelectedCandidate: "/support/api/saveSelectedCandidate/",
        currentView: ""
    };

    app.notify = function notifyError(msg, type) {
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

    app.setCurrentView = function () {
        var pathElement = window.location.search.split('=');
        app.currentView = pathElement[pathElement.length - 1];
    };

    app.setJPId = function () {
        var pathElement = window.location.pathname.split('/');
        app.jpId = pathElement[pathElement.length - 2];
        jobPostId = app.jpId;
    };

    app.init = function () {
        NProgress.start();

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

        /* ajax commands to fetch all localities*/
        try {
            $.ajax({
                type: "POST",
                url: "/getAllLocality",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataCheckLocality
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        /* ajax commands to fetch all jobs */
        try {
            $.ajax({
                type: "POST",
                url: "/getAllJobs",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataCheckJobs
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        /* ajax commands to fetch all education */
        try {
            $.ajax({
                type: "POST",
                url: "/getAllEducation",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataCheckEducation
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        /* ajax commands to fetch all education */
        try {
            $.ajax({
                type: "POST",
                url: "/getAllExperience",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataCheckExperience
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        try {
            $.ajax({
                type: "GET",
                url: "/support/api/getDocumentReqForJobRole/?job_post_id="+jobPostId,
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataGetIdProofs
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
        try {
            $.ajax({
                type: "GET",
                url: "/support/api/getAssetReqForJobRole/?job_post_id="+jobPostId,
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataGetAssets
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        function processDataGetIdProofs(returnedIdProofs) {
            var data = [];

            returnedIdProofs.forEach(function (idProof) {
                var opt = {
                    label: idProof.idProofName, value: parseInt(idProof.idProofId)
                };
                data.push(opt);
            });

            var selectList = $('#documentMultiSelect');
            selectList.multiselect({
                includeSelectAllOption: true,
                enableCaseInsensitiveFiltering: true,
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

        function processDataCheckEducation(returnedEdu) {
            if (returnedEdu != null) {
                var data = [];

                returnedEdu.forEach(function (education) {
                    var opt = {
                        label: education.educationName, value: parseInt(education.educationId)
                    };
                    data.push(opt);
                });

                var selectList = $('#educationMultiSelect');
                selectList.multiselect({
                    nonSelectedText: 'None Selected',
                    includeSelectAllOption: true,
                    maxHeight: 300
                });
                selectList.multiselect('dataprovider', data);
                selectList.multiselect('rebuild');

            }
        }

        function processLanguage(returnLanguage) {
            if (returnLanguage != null) {
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

        function processDataCheckLocality(returnedData) {
            returnedData.forEach(function (locality) {
                var id = locality.localityId;
                var name = locality.localityName;
                var item = {};
                item ["id"] = id;
                item ["name"] = name;
                allLocalityArray.push(item);
            });
        }

        function processDataCheckJobs(returnedData) {
            returnedData.forEach(function (job) {
                var id = job.jobRoleId;
                var name = job.jobName;
                var item = {};
                item ["id"] = id;
                item ["name"] = name;
                allJobArray.push(item);
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

    };

    app.initParams = function () {

        NProgress.start();
        try {
            $.ajax({
                type: "POST",
                url: "/getJobPostInfo/" + app.jpId + "/1",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: app.processParamsAndUpdateUI
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    };

    app.initJobCard = function () {

        NProgress.start();
        try {
            $.ajax({
                type: "POST",
                url: "/getJobPostInfo/" + app.jpId + "/1",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: app.populateJobPostCardUI
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    };

    app.processParamsAndUpdateUI = function (returnedData) {
        if (returnedData != null) {
            jobPostInfo = returnedData;
            app.populateJobPostCardUI(returnedData);

            app.jpId = returnedData.jobPostId;
            // gender, language, age
            if (returnedData.jobPostMaxAge != null) {
                app.jpMaxAge = returnedData.jobPostMaxAge;
                $("#maxAge").val(app.jpMaxAge);
            }
            if (returnedData.gender != null) {
                app.jpGender = returnedData.gender;
                $("#genderSelect").val(app.jpGender);
            } else {
                app.jpGender = -1;
            }

            $("#genderSelect").val(app.jpGender);
            $("#genderSelect").multiselect('rebuild');

            if (returnedData.jobPostDocumentRequirements != null) {
                app.jpDocumentIdList = [];
                var req = returnedData.jobPostDocumentRequirements;
                req.forEach(function (documentRequirement) {
                    if(documentRequirement != null){
                        app.jpDocumentIdList.push(documentRequirement.idProof.idProofId);
                    }
                });
                $("#documentMultiSelect").val(app.jpDocumentIdList);
                $("#documentMultiSelect").multiselect('rebuild');
            }
            if (returnedData.jobPostAssetRequirements != null) {
                app.jpAssetIdList = [];
                var req = returnedData.jobPostAssetRequirements;
                req.forEach(function (assetRequirement) {
                    if(assetRequirement != null){
                        app.jpAssetIdList.push(assetRequirement.asset.assetId);
                    }
                });
                $("#assetMultiSelect").val(app.jpAssetIdList);
                $("#assetMultiSelect").multiselect('rebuild');
            }


            if (returnedData.jobPostExperience != null) {
                app.jpExperienceIdList = [];
                app.jpExperienceIdList.push(returnedData.jobPostExperience.experienceId);
                $("#experienceMultiSelect").val(app.jpExperienceIdList);
                $("#experienceMultiSelect").multiselect('rebuild');
            }
            if (returnedData.jobPostEducation != null) {
                app.jpEducationIdList = [];
                app.jpEducationIdList.push(returnedData.jobPostEducation.educationId);
                $("#educationMultiSelect").val(app.jpEducationIdList);
                $("#educationMultiSelect").multiselect('rebuild');
            }
            if (returnedData.jobPostLanguageRequirements != null) {
                var req = returnedData.jobPostLanguageRequirements;
                app.jpLanguageIdList = [];
                req.forEach(function (languageRequirement) {
                    if (languageRequirement != null) {
                        app.jpLanguageIdList.push(languageRequirement.language.languageId);
                    }
                });

                $("#languageMultiSelect").val(app.jpLanguageIdList);
                $("#languageMultiSelect").multiselect('rebuild');
            }

            if (returnedData.jobPostMinSalary != null) {
                app.jpMinSalary = returnedData.jobPostMinSalary;
                $("#minSalary").val(app.jpMinSalary);
            }
            if (returnedData.jobPostMaxSalary != null) {
                app.jpMaxSalary = returnedData.jobPostMaxSalary;
                $("#maxSalary").val(app.jpMaxSalary);
            }

            var jobPostJobRole = [];

            if (returnedData.jobRole != null) {
                app.jpJobRoleId = null;
                var item = {};
                item ["id"] = returnedData.jobRole.jobRoleId;
                item ["name"] = returnedData.jobRole.jobName;
                jobPostJobRole.push(item);
                app.jpJobRoleId = (returnedData.jobRole.jobRoleId);
            }
            if (jobPostJobRole != null) {
                $("#jobPostJobRole").tokenInput(getJob(), {
                    theme: "facebook",
                    placeholder: "Job Role?",
                    hintText: "Start typing jobs (eg. Cook, Delivery boy..)",
                    minChars: 0,
                    tokenLimit: 1,
                    prePopulate: jobPostJobRole,
                    preventDuplicates: true
                });
            }

            var localityArray;

            if (returnedData.jobPostToLocalityList != null) {
                app.jpLocalityIdList = [];
                localityArray = [];
                returnedData.jobPostToLocalityList.forEach(function (locality) {
                    var item = {};
                    item ["id"] = locality.locality.localityId;
                    item ["name"] = locality.locality.localityName;
                    localityArray.push(item);
                    app.jpLocalityIdList.push(locality.locality.localityId);
                });
            }
            if (localityArray != null) {
                $("#jobPostLocality").tokenInput(getLocality(), {
                    theme: "facebook",
                    placeholder: "job Localities?",
                    hintText: "Start typing Area (eg. BTM Layout, Bellandur..)",
                    minChars: 0,
                    prePopulate: localityArray,
                    preventDuplicates: true
                });
            }

            app.fetchCandidateList();
        }
    };

    app.fetchCandidateList = function () {
        if(!$("#matchBtn").is(":disabled")) {
            $('#matchBtn').attr('disabled', true);
            setTimeout(function () {
                if($("#matchBtn").is(":disabled")) {
                    app.notify("Something went wrong ! Please wait. Reloading page..", 'danger');
                    location.reload(true);
                    $('#matchBtn').attr('disabled', false);
                    NProgress.done();
                }
                // window.location = response.redirectUrl + app.jpId + "/?view=" + response.nextView;
            }, 60000);

        }

        NProgress.start();
        var i;
        var modifiedLocality = $('#jobPostLocality').val().split(",");
        var modifiedLanguageIdList = $('#languageMultiSelect').val();
        var modifiedExpIdList = $('#experienceMultiSelect').val();
        var modifiedEduIdList = $('#educationMultiSelect').val();
        var modifiedDocumentIdList = $('#documentMultiSelect').val();
        var modifiedAssetIdList = $('#assetMultiSelect').val();


        // this also converts string data to integer and sends to server
        app.jpLanguageIdList = [];
        if (modifiedLanguageIdList != null) {
            for (i = 0; i < modifiedLanguageIdList.length; i++) {
                app.jpLanguageIdList.push(parseInt(modifiedLanguageIdList[i]));
            }
        }
        app.jpLocalityIdList = null;
        if (modifiedLocality != null && modifiedLocality.length != 0 && modifiedLocality[0] != "") {
            app.jpLocalityIdList = [];
            for (i = 0; i < modifiedLocality.length; i++) {
                app.jpLocalityIdList.push(parseInt(modifiedLocality[i]));
            }
        }

        app.jpExperienceIdList = [];
        if (modifiedExpIdList != null) {
            for (i = 0; i < modifiedExpIdList.length; i++) {
                app.jpExperienceIdList.push(parseInt(modifiedExpIdList[i]));
            }
        }
        app.jpEducationIdList = [];
        if (modifiedEduIdList!= null) {
            for (i = 0; i < modifiedEduIdList.length; i++) {
                app.jpEducationIdList.push(parseInt(modifiedEduIdList[i]));
            }
        }

        app.jpDocumentIdList = [];
        if (modifiedDocumentIdList!= null) {
            for (i = 0; i < modifiedDocumentIdList.length; i++) {
                app.jpDocumentIdList.push(parseInt(modifiedDocumentIdList[i]));
            }
        }
        app.jpAssetIdList = [];
        if (modifiedAssetIdList!= null) {
            for (i = 0; i < modifiedAssetIdList.length; i++) {
                app.jpAssetIdList.push(parseInt(modifiedAssetIdList[i]));
            }
        }

        app.jpJobRoleId = null;

        app.jpJobRoleId = (parseInt($("#jobPostJobRole").val()));

        app.jpGender = $('#genderSelect').val();

        app.jpMinSalary = $('#minSalary').val();
        app.jpMaxSalary = $('#maxSalary').val();
        app.jpMaxAge = $('#maxAge').val();

        if (app.jpJobRoleId == null) {
            app.shouldSend = false;
        }

        if (app.shouldSend) {
            NProgress.start();
            var d = {
                jobPostId: app.jpId,
                maxAge: app.jpMaxAge,
                minSalary: app.jpMinSalary,
                maxSalary: app.jpMaxSalary,
                gender: app.jpGender,
                jobPostJobRoleId: app.jpJobRoleId,
                experienceIdList: app.jpExperienceIdList,
                jobPostEducationIdList: app.jpEducationIdList,
                jobPostLocalityIdList: app.jpLocalityIdList,
                jobPostLanguageIdList: app.jpLanguageIdList,
                jobPostDocumentIdList: app.jpDocumentIdList,
                jobPostAssetIdList: app.jpAssetIdList,
                distanceRadius: parseInt($('#radiusValue').text())
            };

            try {
                $.ajax({
                    type: "POST",
                    url: app.url,
                    async: true,
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: app.updateMatchTable
                });
            } catch (exception) {
                console.log("exception occured!!" + exception.stack);
            }
        }
    };

    app.updateMatchTable = function (returnedData) {
        // form candidateList.
        app.jpCandidateList = [];
        $.each(returnedData, function (key, value) {
            if (value != null) {
                app.jpCandidateList.push(value);
            }
        });

        app.notify("Total Candidate Matched : " + app.jpCandidateList.length, 'warning');
        // destroy table
        app.renderTable(app.jpCandidateList);
        // construct new table

    };

    app.renderTable = function (candidateList) {
        if (candidateList == null || candidateList.length == 0) {
            console.log("candiateList Empty");
            app.tableContainer.hide();
            app.notify("No Candidates !", 'danger');
            $('#moveSelectedBtn').attr('disabled', true);
            $('#matchBtn').attr('disabled', false);
            NProgress.done();
            return;
        } else {
            $('#moveSelectedBtn').attr('disabled', false);
            $('#matchBtn').attr('disabled', false);
        }
        var returnedDataArray = [];
        try {
            candidateList.forEach(function (newCandidate) {
                // prep strings for display
                app.addFooter();

                var timeShiftPref = "";
                var locality = "";
                if (newCandidate.candidate.timeShiftPreference != null) {
                    timeShiftPref = newCandidate.candidate.timeShiftPreference.timeShift.timeShiftName;
                }
                if (newCandidate.candidate.locality != null) {
                    locality = newCandidate.candidate.locality.localityName;
                }

                var jobApplicationMode ="";
                if(newCandidate.extraData.jobApplicationMode != null) {
                    jobApplicationMode = newCandidate.extraData.jobApplicationMode;
                } else {
                    jobApplicationMode = "NA";
                }

                var preScreenSelectionTimeStamp = "";
                if (app.currentView == "pre_screen_view") {
                    if(newCandidate.extraData.preScreenSelectionTimeStamp != null) {
                        preScreenSelectionTimeStamp = getDateTime(newCandidate.extraData.preScreenSelectionTimeStamp);
                    }
                }

                var preScreenAttemptCount = function () {
                    if (app.currentView == "pre_screen_view") {
                        if(newCandidate.extraData.preScreenCallAttemptCount == null) {
                            return "0";
                        } else {
                            return '<a href="'+"/workflowInteraction/"+newCandidate.extraData.workflowUUId+'" id="'+newCandidate.extraData.workflowId+'" style="cursor:pointer;" target="_blank">'+newCandidate.extraData.preScreenCallAttemptCount+'</a>';
                        }
                    } else if (app.currentView == "pre_screen_completed_view" || app.currentView == "confirmed_interview_view" || app.currentView == "completed_interview_view" || app.currentView == "pending_interview_schedule") {
                        if (newCandidate.extraData.allInteractionCount == null) {
                            return "0";
                        } else {
                            return '<a href="' + "/workflowInteraction/" + newCandidate.extraData.workflowUUId + '" id="' + newCandidate.extraData.workflowId + '" style="cursor:pointer;" target="_blank">' + newCandidate.extraData.allInteractionCount + '</a>';
                        }
                    } else {
                        return "";
                    }
                };

                var varColumn = function () {
                    if (app.currentView == "pre_screen_view") {
                        if(newCandidate.extraData.preScreenCallAttemptCount == null || newCandidate.extraData.preScreenCallAttemptCount == 0) {
                            return '<input type="submit" value="Pre-Screen"  style="width:150px" onclick="callHandler(' + newCandidate.candidate.candidateMobile + ', ' + newCandidate.candidate.candidateId + ');" id="' + newCandidate.candidate.lead.leadId + '" class="btn btn-primary">'
                        } else {
                            return '<input type="submit" value="Pre-Screen Again"  style="width:150px" onclick="callHandler(' + newCandidate.candidate.candidateMobile + ', ' + newCandidate.candidate.candidateId + ');" id="' + newCandidate.candidate.lead.leadId + '" class="btn btn-default">'
                        }
                    } else if (app.currentView == "confirmed_interview_view" || app.currentView == "completed_interview_view"){
                        var interviewDetails = "Date and slot not available";
                        if(newCandidate.extraData.interviewSchedule != null){
                            interviewDetails = newCandidate.extraData.interviewSchedule;
                        }
                        return interviewDetails;
                    }else {
                        return "";
                    }
                };
                var varColumn2 = function () {
                    if (app.currentView == "pre_screen_completed_view") {
                        var interviewAction = "";
                        var interviewDate;

                        if($("#statusVal").val() == 3){ //rescheduled
                            interviewAction = "Rescheduled to " + newCandidate.extraData.interviewSchedule + " ";
                            interviewAction += '<span id="interview_status_val_' + newCandidate.candidate.candidateId + '">' +
                                '<span class="accept" onclick="acceptRescheduledInterview(' + newCandidate.candidate.candidateId + ', 1);"><img src="/assets/recruiter/img/icons/accept.svg" height="16px" width="14px"></span>' +
                                '<span class="reject" onclick="acceptRescheduledInterview(' + newCandidate.candidate.candidateId + ', 0);"><img src="/assets/recruiter/img/icons/reject.svg" height="16px" width="14px"></span>';
                        } else if($("#statusVal").val() == 4 || $("#statusVal").val() == 5){ //rejected
                            interviewAction = "Rejected";
                        } else if($("#statusVal").val() == 2){ //scheduled. need confirmation
                            if(newCandidate.extraData.interviewSchedule != null){
                                var oldDate = new Date(newCandidate.extraData.interviewDate);
                                rescheduledDate = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                                rescheduledSlot = newCandidate.extraData.interviewSlot.interviewTimeSlotId;
                                globalInterviewDate = newCandidate.extraData.interviewSchedule;

                                interviewDate = newCandidate.extraData.interviewSchedule;

                                interviewAction = '<div id="interview_date_' + newCandidate.candidate.candidateId + '">' + interviewDate + '</div>' +
                                    '<span id="interview_status_option_' + newCandidate.candidate.candidateId + '">' +
                                    '<span class="accept" onclick="confirmInterview(' + newCandidate.candidate.candidateId + ', ' + newCandidate.extraData.interviewDate + ', ' + newCandidate.extraData.interviewSlot.interviewTimeSlotId + ');"><img src="/assets/recruiter/img/icons/accept.svg" height="16px" width="14px"></span>' +
                                    '<span class="reject" onclick="rejectInterview(' + newCandidate.candidate.candidateId + ', ' + newCandidate.extraData.interviewDate + ', ' + newCandidate.extraData.interviewSlot.interviewTimeSlotId + ');"><img src="/assets/recruiter/img/icons/reject.svg" height="16px" width="14px"></span>' +
                                    '<span class="reschedule" onclick="showReschedulePopup(' + newCandidate.candidate.candidateId + ', ' + newCandidate.extraData.interviewDate + ', ' + newCandidate.extraData.interviewSlot.interviewTimeSlotId + ');"><img src="/assets/recruiter/img/icons/reschedule.svg" height="18px" width="16px"></span>' +
                                    '</span>';

                            } else{
                                interviewAction = "Slots not available";
                            }
                        } else{
                            if(newCandidate.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_SCHEDULED){
                                var oldDate = new Date(newCandidate.extraData.interviewDate);
                                rescheduledDate = oldDate.getFullYear() + "-" + (oldDate.getMonth() + 1) + "-" + oldDate.getDate();
                                rescheduledSlot = newCandidate.extraData.interviewSlot.interviewTimeSlotId;
                                globalInterviewDate = newCandidate.extraData.interviewSchedule;

                                interviewDate = newCandidate.extraData.interviewSchedule;

                                interviewAction = '<div id="interview_date_' + newCandidate.candidate.candidateId + '">' + interviewDate + '</div>' +
                                    '<span id="interview_status_option_' + newCandidate.candidate.candidateId + '">' +
                                    '<span class="accept" onclick="confirmInterview(' + newCandidate.candidate.candidateId + ', ' + newCandidate.extraData.interviewDate + ', ' + newCandidate.extraData.interviewSlot.interviewTimeSlotId + ');"><img src="/assets/recruiter/img/icons/accept.svg" height="16px" width="14px"></span>' +
                                    '<span class="reject" onclick="rejectInterview(' + newCandidate.candidate.candidateId + ', ' + newCandidate.extraData.interviewDate + ', ' + newCandidate.extraData.interviewSlot.interviewTimeSlotId + ');"><img src="/assets/recruiter/img/icons/reject.svg" height="16px" width="14px"></span>' +
                                    '<span class="reschedule" onclick="showReschedulePopup(' + newCandidate.candidate.candidateId + ', ' + newCandidate.extraData.interviewDate + ', ' + newCandidate.extraData.interviewSlot.interviewTimeSlotId + ');"><img src="/assets/recruiter/img/icons/reschedule.svg" height="18px" width="16px"></span>' +
                                    '</span>';
                            } else if(newCandidate.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT){
                                interviewAction = "Rejected by recruiter/support";
                            } else if(newCandidate.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE){
                                interviewAction = "Rejected by candidate";
                            } else if(newCandidate.extraData.workflowStatus.statusId == JWF_STATUS_INTERVIEW_RESCHEDULE){
                                interviewAction = "Rescheduled to " + newCandidate.extraData.interviewSchedule + " ";
                                interviewAction += '<span id="interview_status_val_' + newCandidate.candidate.candidateId + '">' +
                                    '<span class="accept" onclick="acceptRescheduledInterview(' + newCandidate.candidate.candidateId + ', 1);"><img src="/assets/recruiter/img/icons/accept.svg" height="16px" width="14px"></span>' +
                                    '<span class="reject" onclick="acceptRescheduledInterview(' + newCandidate.candidate.candidateId + ', 0);"><img src="/assets/recruiter/img/icons/reject.svg" height="16px" width="14px"></span>';
                            } else{
                                interviewAction = "";
                            }
                        }
                        return interviewAction;
                    } else if (app.currentView == "confirmed_interview_view") {
                        var candidateStatus = '<b id="current_status_' + newCandidate.candidate.candidateId + '">' + "Data not available" + '</b>';
                        if(newCandidate.extraData.candidateInterviewStatus != null){

                            var reason = "";
                            if(newCandidate.extraData.reason != null){
                                if(newCandidate.extraData.workflowStatus.statusId == JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING) { //not going
                                    reason = ' [reason: ' + newCandidate.extraData.reason.reasonName + ']';
                                } else{
                                    reason = ' [reaching: ' + newCandidate.extraData.reason.reasonName + ']';
                                }
                            }

                            var lastUpdate = new Date(newCandidate.extraData.creationTimestamp);
                            var timing = "";
                            if(lastUpdate.getHours() == 12){
                                timing = lastUpdate.getHours() + ":" + lastUpdate.getMinutes() + " pm";
                            } else if(lastUpdate.getHours() > 12){
                                timing = lastUpdate.getHours() - 12 + ":" + lastUpdate.getMinutes() + " pm";
                            } else{
                                timing = lastUpdate.getHours() + ":" + lastUpdate.getMinutes() + " am";
                            }
                            candidateStatus = '<b id="current_status_' + newCandidate.candidate.candidateId + '">'
                                + newCandidate.extraData.candidateInterviewStatus.statusTitle + '</b>' + reason + '('+ lastUpdate.getDate() + "-"
                                + (lastUpdate.getMonth() + 1) + "-" + lastUpdate.getFullYear() + " " + timing + ')';
                        }

                        candidateStatus +=  '<select style="margin-left: 8px" id="interview_status_' + newCandidate.candidate.candidateId +'">' +
                            '<option value="0">Select a Status</option>' +
                            '<option value="1">Not Going</option>' +
                            '<option value="2">Delayed</option>' +
                            '<option value="3">On the Way</option>' +
                            '<option value="4">Reached</option>' +
                            '</select>' +
                            '<input style="margin-left: 6px" type="button" value="Update" onclick="updateStatus('+ newCandidate.candidate.candidateId + ')">';

                        return candidateStatus;
                    } else if(app.currentView == "pending_interview_schedule"){
                        var candidateStatus;
                        if (Object.keys(jobPostInfo.interviewDetailsList).length > 0) {
                            var availableCredits = jobPostInfo.recruiterProfile.interviewCreditCount;

                            if(jobPostInfo.recruiterProfile != null){
                                if(availableCredits > 0){
                                    candidateStatus = '<input style="margin-left: 6px" type="button" class="btn btn-primary" value="Schedule" onclick="initInterviewModal('+ newCandidate.candidate.candidateId + ', ' + jobPostId + ', '+ true + ')">';
                                } else{
                                    candidateStatus = "No interview credits with the Recruiter";
                                }
                            }
                        } else{
                            candidateStatus = "Slots not available"
                        }
                        return candidateStatus;
                    } else {
                        return "";
                    }
                };

                var varColumn3 = function () {
                    if (app.currentView == "confirmed_interview_view" || app.currentView == "completed_interview_view") {
                        var candidateStatus = '<b>' + "Feedback not available" + '</b>';
                        if(newCandidate.extraData.workflowStatus != null){
                            if(newCandidate.extraData.workflowStatus.statusId > JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED){
                                if(newCandidate.extraData.workflowStatus.statusId == JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED){ //selected
                                    candidateStatus = '<b style="color: green;">' + newCandidate.extraData.workflowStatus.statusTitle + '</b>';
                                } else{
                                    candidateStatus = '<b style="color: red;">' + newCandidate.extraData.workflowStatus.statusTitle + '</b>';
                                }
                            }
                        }
                        if(app.currentView == "confirmed_interview_view"){
                            candidateStatus += '<input style="margin-left: 6px" type="button" value="Update" onclick="openFeedbackModal('+ newCandidate.candidate.candidateId + ')">';
                        }
                        return candidateStatus;
                    } else {
                        return "";
                    }
                };

                var createdBy = function () {
                    if (app.currentView == "pre_screen_view") {
                        if(newCandidate.extraData.createdBy != null){
                            return newCandidate.extraData.createdBy;
                        } else {
                            return "NA";
                        }
                    } else {
                        return "";
                    }
                };

                returnedDataArray.push({
                    'cLID': '<a href="/candidateSignupSupport/' + newCandidate.candidate.lead.leadId + '/false" target="_blank" id="' + newCandidate.candidate.lead.leadId + '">' + newCandidate.candidate.lead.leadId + '</a>',
                    'candidateFirstName': getFirstName(newCandidate.candidate.candidateFirstName) + " " + getLastName(newCandidate.candidate.candidateLastName),
                    'candidateMobile': newCandidate.candidate.candidateMobile,
                    'candidateJobPref': getJobPref(newCandidate.candidate.jobPreferencesList),
                    'candidateLocalityPref': getLocalityPref(newCandidate.candidate.localityPreferenceList),
                    'locality': getHomeLocality(newCandidate.candidate.locality),
                    'matchedLocality': (newCandidate.candidate.matchedLocation),
                    'age': getAge(newCandidate.candidate.candidateDOB),
                    'candidateExperience': getInYearMonthFormat(newCandidate.candidate.candidateTotalExperience),
                    'candidateIsEmployed': getYesNo(newCandidate.candidate.candidateIsEmployed),
                    'candidateLastWithdrawnSalary': getLastWithdrawnSalary(newCandidate.candidate.candidateLastWithdrawnSalary),
                    'candidateLanguage': getLanguageKnown(newCandidate.candidate.languageKnownList),
                    'candidateEducation': getEducation(newCandidate.candidate.candidateEducation),
                    'candidateDegree': getDegree(newCandidate.candidate.candidateEducation),
                    'candidateDegreeCompleted': getDegreeCompletion(newCandidate.candidate.candidateEducation),
                    'candidateSkillList': getSkills(newCandidate.candidate.candidateSkillList),
                    'candidateGender': getGender(newCandidate.candidate.candidateGender),
                    'pastOrCurrentCompanyName': getPastOrCurrentCompanyName(newCandidate.candidate.jobHistoryList),
                    'candidateIsAssessmentComplete': getYesNo(newCandidate.extraData.assessmentAttemptId),
                    'jobAppliedOn': getAppliedOn(newCandidate.extraData.appliedOn),
                    'noOfJobApplication': newCandidate.candidate.jobApplicationList.length,
                    'jobApplicationMode': jobApplicationMode,
                    'candidateExperienceLetter': getYesNo(newCandidate.candidate.candidateExperienceLetter),
                    'candidateIdProofs': getIdProof(newCandidate.candidate.idProofReferenceList),
                    'candidateTimeShiftPref': timeShiftPref,
                    'lastActive': (newCandidate.extraData.lastActive.lastActiveValueName),
                    'candidateCreateTimestamp': getDateTime(newCandidate.candidate.candidateCreateTimestamp),
                    'isMinProfileComplete': getYesNo(newCandidate.candidate.isMinProfileComplete),
                    'experience': getExperience(newCandidate.candidate.candidateExpList),
                    'candidateId': newCandidate.candidate.candidateId,
                    'preScreenAttempt': preScreenAttemptCount,
                    'preScreenSelectionTS': preScreenSelectionTimeStamp,
                    'preScreenCreatedBy': createdBy,
                    'varColumn': varColumn,
                    'varColumn2': varColumn2,
                    'varColumn3': varColumn3
                })
            });

            app.tableContainer.show();

            // instantiated with default values
            var select = false;
            var order = [[24, "asc"]];
            if (app.currentView == "match_view") {
                select = {
                    "style": 'multi'
                };
            } else if (app.currentView == "pre_screen_view") {
                order = [[31, "desc"]];
            } else if (app.currentView == "pre_screen_completed_view"){
            } else {
            }

            app.table = $('table#' + app.tableContainerId).DataTable({
                "data": returnedDataArray,
                "order": order,
                "rowId": "candidateId",
                "columns": [
                    {"data": "cLID"},
                    {"data": "candidateFirstName"},
                    {"data": "candidateMobile"},
                    {"data": "candidateJobPref"},
                    {"data": "candidateLocalityPref"},
                    {"data": "locality"},
                    {"data": "matchedLocality"},
                    {"data": "age"},
                    {"data": "candidateExperience"},
                    {"data": "candidateIsEmployed"},
                    {"data": "candidateLastWithdrawnSalary"},
                    {"data": "candidateLanguage"},
                    {"data": "candidateEducation"},
                    {"data": "candidateDegree"},
                    {"data": "candidateDegreeCompleted"},
                    {"data": "candidateSkillList"},
                    {"data": "candidateGender"},
                    {"data": "pastOrCurrentCompanyName"},
                    {"data": "candidateIsAssessmentComplete"},
                    {"data": "jobAppliedOn"},
                    {"data": "noOfJobApplication"},
                    {"data": "jobApplicationMode"},
                    {"data": "candidateExperienceLetter"},
                    {"data": "candidateIdProofs"},
                    {"data": "candidateTimeShiftPref"},
                    {"data": "lastActive"},
                    {"data": "candidateCreateTimestamp"},
                    {"data": "isMinProfileComplete"},
                    {"data": "experience"},
                    {"data": "candidateId"},
                    {"data": "preScreenAttempt"},
                    {"data": "preScreenSelectionTS"},
                    {"data": "preScreenCreatedBy"},
                    {"data": "varColumn"},
                    {"data": "varColumn2"},
                    {"data": "varColumn3"}
                ],
                "deferRender": true,
                "scrollY": '48vh',
                "scrollCollapse": true,
                "scrollX": true,
                "language": {
                    "emptyTable": "No data available"
                },
                "destroy": true,
                "dom": 'Bfrtip',
                "buttons": [
                    'copy', 'csv', 'excel'
                ],
                "select": select
            });

            // Apply the search filter
            app.table.columns().every(function () {
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

            NProgress.done();
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    };


    app.addFooter = function () {
        // Setup - add a text input to each footer cell
        if (shouldAddFooter) {
            $('#' + app.tableContainerId + ' tfoot th').each(function () {
                var title = $(this).text();
                    $(this).html('<input type="text" name="' + title + '"  id="' + title + '" placeholder="' + title + '" />');
            });
            shouldAddFooter = false;
        }
    };

    app.getSelectionFromTable = function () {
        if(app.jpSelectedCandidateList == null || app.table == null){
            app.notify("No Candidates found !", 'danger');

            return;
        }
        var selectedCandidateIds = app.table.rows({selected: true}).ids();
        var arrayLength = selectedCandidateIds.length;
        app.jpSelectedCandidateList = [];
        for (var i = 0; i < arrayLength; i++) {
            app.jpSelectedCandidateList.push(parseInt(selectedCandidateIds[i]));
        }
    };

    app.submitForm = function () {
        app.formSubmit.submit(function (eventObj) {
            eventObj.preventDefault();
            app.fetchCandidateList();
        });
    };

    app.submitSelectedCandidateList = function () {

        $('#moveSelectedBtn').attr('disabled', true);
        NProgress.start();
        var d = {
            jobPostId: app.jpId,
            selectedCandidateIdList: app.jpSelectedCandidateList
        };
        try {
            $.ajax({
                type: "POST",
                url: app.urlSaveSelectedCandidate,
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(d),
                success: app.responseInterpreter
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    };

    app.responseInterpreter = function (response) {
        console.log(response);
        if (response.status == "SUCCESS") {
            app.currentView = response.nextView;
            app.notify(response.message + " Please wait, Refreshing table", 'success');
            setTimeout(function () {
                app.fetchCandidateList();
                // window.location = response.redirectUrl + app.jpId + "/?view=" + response.nextView;
            }, 3000)
        } else {
            app.notify(response.message, 'danger');
        }
        $('#moveSelectedBtn').attr('disabled', true);
    };

    // pre_screen methods
    app.initPreScreenView = function () {
        NProgress.start();
        try {
            $.ajax({
                type: "POST",
                url: "/support/api/getSelectedCandidate/" + app.jpId,
                data: false,
                contentType: false,
                processData: false,
                success: app.updatePreScreenTable
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    };

    app.updatePreScreenTable = function (returnedData) {
        // form candidateList.
        app.jpSelectedCandidateList = [];
        $.each(returnedData, function (key, value) {
            if (value != null) {
                app.jpSelectedCandidateList.push(value);
            }
        });

        app.notify("Total Candidate Selected : " + app.jpSelectedCandidateList.length, 'warning');

        app.renderTable(app.jpSelectedCandidateList);
    };

    app.getSupportAgent = function () {
        /* ajax commands to fetch supportAgent Info */
        try {
            $.ajax({
                type: "GET",
                url: "/getSupportAgent",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: app.processSupportAgentData
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    };

    app.fetchPreScreenedCandidate = function () {
        NProgress.start();

        try {
            $.ajax({
                type: "POST",
                url: "/getAllTimeSlots",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataGetAllTimeSlots
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        try {
            $.ajax({
                type: "POST",
                url: "/getAllInterviewRejectReasons",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataGetAllReason
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        var base_url = "/support/api/getPreScreenedCandidate/?jpId=" + app.jpId;
        if($("#statusVal").val() == 1){
            app.notify("Fetching all candidate: 'pre-screened-completed'. Please wait..", "warning");
        } else if($("#statusVal").val() == 2){
            app.notify("Fetching all candidate: 'pre-screened-completed with scheduled interview'. Please wait..", "warning");
        } else if($("#statusVal").val() == 3){
            app.notify("Fetching all candidate: 'pre-screened-completed which are rescheduled'. Please wait..", "warning");
        } else if($("#statusVal").val() == 4){
            app.notify("Fetching all candidates: 'pre-screened-completed which are rejected by support/recruiter'. Please wait..", "warning");
        } else {
            app.notify("Fetching all candidates: 'pre-screened-completed which are rejected by candidate'. Please wait..", "warning");
        }

        base_url += "&status="+ $("#statusVal").val();

        try {
            $.ajax({
                type: "POST",
                url: base_url,
                data: false,
                contentType: false,
                processData: false,
                success: app.updatePreScreenTable
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    };

    app.fetchPendingInterviewCandidate = function () {
        NProgress.start();

        var base_url = "/support/api/getPendingInterviewScheduleCandidates/?jpId=" + app.jpId;
        try {
            $.ajax({
                type: "POST",
                url: base_url,
                data: false,
                contentType: false,
                processData: false,
                success: app.updatePreScreenTable
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    };

    app.fetchConfirmedInterviewCandidates = function () {
        NProgress.start();

        try {
            $.ajax({
                type: "POST",
                url: "/getAllInterviewNotGoingReasons",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataGetAllInterviewNotGoingReason
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        try {
            $.ajax({
                type: "POST",
                url: "/getAllCandidateETA",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataGetAllCandidateEta
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }

        try {
            $.ajax({
                type: "POST",
                url: "/getAllNotSelectedReasons",
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: processDataGetAllNotSelectedReason
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }


        var base_url = "/support/api/getConfirmedInterviewCandidates/?jpId=" + app.jpId + "&start=" + startDate + "&end=" + endDate;
        try {
            $.ajax({
                type: "POST",
                url: base_url,
                data: false,
                contentType: false,
                processData: false,
                success: app.updatePreScreenTable
            });
        } catch (exception) {
            console.log("exception occured!!" + exception.stack);
        }
    };

    app.fetchCompletedInterviews = function () {
        var base_url = "/support/api/getAllCompletedInterviews/?jpId=" + app.jpId;
        try {
            $.ajax({
                type: "POST",
                url: base_url,
                data: false,
                async: false,
                contentType: false,
                processData: false,
                success: app.updatePreScreenTable
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    };


    app.populateJobPostCardUI = function (returnedData) {
        jobPostInfo = returnedData;
        NProgress.start();
        var jobPostTitle = returnedData.jobPostTitle;
        var jobPostCompany = returnedData.company.companyName;
        var jobPostLocalityNameList = [];
        if(returnedData.jobPostToLocalityList!=null) {
            returnedData.jobPostToLocalityList.forEach(function(jpToLocality)
            {
                jobPostLocalityNameList.push(jpToLocality.locality.localityName);
            });
        }
        var jobPostEducation;
        if(returnedData.jobPostEducation != null) {
            jobPostEducation = returnedData.jobPostEducation.educationName;
        }
        var jpExperience;
        if(returnedData.jobPostExperience != null) {
            jpExperience = returnedData.jobPostExperience.experienceType;
        }

        var jobPostSalary = "Rs. "+returnedData.jobPostMinSalary;
        if(returnedData.jobPostMaxSalary != 0) {
            jobPostSalary +=" - Rs. " + returnedData.jobPostMaxSalary;
        }
        var jobRoleTitle = returnedData.jobRole.jobName;
        var jobRoleId = returnedData.jobPostId;

        var jobPostInterviewAddress = "Not Available";

        //computing Address
        if(returnedData.interviewFullAddress != null && returnedData.interviewFullAddress != ""){
            jobPostInterviewAddress = returnedData.interviewFullAddress;
        }

        app.renderJobCard(jobPostTitle, jobPostCompany, jobPostSalary, jobRoleTitle, jobRoleId,
            jobPostLocalityNameList, jobPostEducation, jpExperience, jobPostInterviewAddress);
    };

    app.renderJobCard = function (jobPostTitle, jobPostCompany, jobPostSalary, jobRoleTitle, jobRoleId,
                                  jobPostLocalityNameList, jobPostEducation, jpExperience, jobPostInterviewAddress) {
        $('#job_post_title').text(jobPostTitle);
        $('#job_post_company_title').text(jobPostCompany);
        $('#job_post_salary').text(jobPostSalary);
        $('#job_role').text(jobRoleTitle);
        $('#job_post_education').text(jobPostEducation);
        $('#job_post_experience').text(jpExperience);
        $('#job_post_locality').text(jobPostLocalityNameList);
        $('#job_role_id').text("Job Details");
        $('#job_post_interview_address').text(jobPostInterviewAddress);
        gJobRoleId = jobRoleId;
    };

    app.processSupportAgentData = function (returnedData) {
        var mobileNum = returnedData.agentMobileNumber;

        if (mobileNum != null) {
            globalRecAgentNumber = mobileNum;
        }
    };

    app.destroyLists = function () {
        app.jpCandidateList = [];
        app.jpSelectedCandidateList = [];
    };

    // seq of match_view execution on doc ready
    app.destroyLists();
    app.setJPId();
    app.setCurrentView();

    if (app.currentView == "match_view") {
        $('#header_view_title').text("Match Candidates");
        $('.navigation__link').removeClass("mdl-navigation__link--current");
        $('#match_view_drawer').removeClass("mdl-navigation__link--current").addClass("mdl-navigation__link--current");

        app.init();
        app.initParams();

        app.submitForm();

        document.getElementById('moveSelectedBtn').addEventListener("click", function () {
            app.getSelectionFromTable();
            if (app.jpSelectedCandidateList.length > 0) {
                app.submitSelectedCandidateList();
            } else {
                app.notify("Please select candidate(s) to move to pre-screen", "danger");
            }
        });
    } else if (app.currentView == "pre_screen_view") {
        $('#header_view_title').text("Pre-screen Candidates");
        $('.navigation__link').removeClass("mdl-navigation__link--current");
        $('#pre_screen_view_drawer').removeClass("mdl-navigation__link--current").addClass("mdl-navigation__link--current");
        app.initPreScreenView();
        app.initJobCard();
    } else if (app.currentView == "pending_interview_schedule") {
        app.fetchPendingInterviewCandidate();
        app.initJobCard();

        $('#header_view_title').text("Schedule Interviews");
    } else if (app.currentView == "pre_screen_completed_view") {
        app.fetchPreScreenedCandidate();
        app.initJobCard();

        $("#statusVal").change(function (){
            app.fetchPreScreenedCandidate();
        });

        $('#header_view_title').text("Confirm/Reject Interviews");
    } else if (app.currentView == "confirmed_interview_view") {
        startDate = null;
        endDate = null;

        app.fetchConfirmedInterviewCandidates();
        app.initJobCard();
        $('#header_view_title').text("Confirmed Interviews");

        //initiating range picker
        $('#rangePicker').daterangepicker({
            startDate: moment(),
            endDate: moment().add(7, 'days')
        }, function(start, end) {
            app.reshuffleConfirmedView(start, end);
        });
    } else if (app.currentView == "completed_interview_view") {
        app.fetchCompletedInterviews();
        app.initJobCard();
        $('#header_view_title').text("Completed Interviews");

        NProgress.done();

    } else {
        $('#header_view_title').text("Future View");
    }

    app.reshuffleConfirmedView = function (start, end) {
        startDate = start.format('YYYY-MM-DD');
        endDate = end.format('YYYY-MM-DD');
        app.fetchConfirmedInterviewCandidates();
    };

});

function linkToDashboard() {
    window.open('/jobPostDetails/'+gJobRoleId, '_blank');
}

function setInterviewStatus(candidateId, status, rescheduledDate, rescheduledSlot, reason) {
    globalCandidateId = candidateId;
    globalInterviewStatus = status;

    var d = {
        candidateId: candidateId,
        jobPostId: jobPostId,
        interviewStatus: status,
        rescheduledDate: rescheduledDate,
        rescheduledSlot: rescheduledSlot,
        reason: reason,
        interviewSchedule: globalInterviewDate
    };

    try {
        $.ajax({
            type: "POST",
            url: "/recruiter/api/updateInterviewStatus",
            async: false,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processDataInterviewStatus
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
}

function openFeedbackModal(candidateId) {
    globalCandidateId = candidateId;

    var feedbackDialog = document.getElementById("feedbackDialog");
    if (! feedbackDialog.showModal) {
        dialogPolyfill.registerDialog(dialog);
    }

    $("#reasonVal").html('');
    var defaultOption = $('<option value="0" selected></option>').text("Select a reason");
    $('#reasonVal').append(defaultOption);

    allRejectReason.forEach(function (reason) {
        var option = $('<option value=' + reason.id + '></option>').text(reason.name);
        $('#reasonVal').append(option);
    });

    $("#feedbackOption").change(function (){
        if($(this).val() == 2 || $(this).val() == 4){
            $("#otherReason").show();
        } else{
            $("#otherReason").hide();
        }
    });

    feedbackDialog.showModal();
    feedbackDialog.querySelector('.closeFeedback').addEventListener('click', function() {
        feedbackDialog.close();
    });
    feedbackDialog.querySelector('.confirmFeedback').addEventListener('click', function() {
        addFeedback();
    });
}

function addFeedback() {
    if($("#feedbackOption").val() > 0) {
        if(($("#feedbackOption").val() == 2 || $("#feedbackOption").val() == 4) && $("#reasonVal").val() == 0){
            notifyError("Please select a reason");
        } else{
            NProgress.start();
            try {
                var d = {
                    candidateId: globalCandidateId,
                    jobPostId : jobPostId,
                    feedbackStatus : $("#feedbackOption").val(),
                    feedbackComment : $("#feedbackNote").val(),
                    rejectReason: $("#reasonVal").val()
                };

                $.ajax({
                    type: "POST",
                    url: "/updateFeedback",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: processDataUpdateFeedBack
                });
                NProgress.done();
            } catch (exception) {
                console.log("exception occured!!" + exception);
            }
        }
    } else{
        notifyError("Please select a feedback option", "warning");
    }
}

function processDataUpdateFeedBack(returnedData) {
    if(returnedData == 1){
        notifyError("Feedback updated successfully. Refreshing ..", 'success');
        setTimeout(function () {
            location.reload();
        }, 2000);
    } else if(returnedData == -1){
        notifyError("Recruiter doesn't have interview credits. Feedback not allowed", 'danger');
    } else{
        notifyError("Something went wrong", 'danger');
    }
}

function rejectInterview(candidateId, date, slot) {
    globalCandidateId = candidateId;
    globalInterviewStatus = 3;

    var inDateFormat = new Date(date);
    var oldDate = inDateFormat.getFullYear() + "-" + (inDateFormat.getMonth() + 1) + "-" + inDateFormat.getDate();

    rescheduledDate = oldDate;
    rescheduledSlot = slot;

    var reasonDialog = document.getElementById("reasonDialog");
    if (! reasonDialog.showModal) {
        dialogPolyfill.registerDialog(dialog);
    }

    $("#reject_reason").html('');

    var defaultOption = $('<option value="0" selected></option>').text("Select a reason");
    $('#reject_reason').append(defaultOption);

    allReasons.forEach(function (reason) {
        var option = $('<option value=' + reason.id + '></option>').text(reason.name);
        $('#reject_reason').append(option);
    });

    reasonDialog.showModal();
    reasonDialog.querySelector('.close').addEventListener('click', function() {
        reasonDialog.close();
    });
    reasonDialog.querySelector('.confirmRejection').addEventListener('click', function() {
        confirmRejectInterview();
    });
}

function confirmRejectInterview(){
    if($("#reject_reason").val() != 0){
        globalInterviewStatus = 2;
        setInterviewStatus(globalCandidateId, 2, rescheduledDate, rescheduledSlot, $("#reject_reason").val());
    } else{
        alert("Please specify the reason for the job application rejection");
    }
}

function confirmRescheduleInterview(){
    if(rescheduledSlot != null){
        globalInterviewStatus = "3";
        setInterviewStatus(globalCandidateId, globalInterviewStatus, rescheduledDate, rescheduledSlot, null);
    } else{
        alert("Please select a time slot");
    }
}

function showReschedulePopup(candidateId, oldDate, oldSlot) {
    globalCandidateId = candidateId;
    globalInterviewStatus = 3;

    rescheduledDate = null;
    rescheduledSlot = null;

    $("#rescheduleDateAndSlot").change(function (){
        try{
            var combinedValue = $("#rescheduleDateAndSlot").val().split("_");
            rescheduledDate = combinedValue[0];
            rescheduledSlot = combinedValue[1];

        } catch(err){}
    });

    if (Object.keys(jobPostInfo.interviewDetailsList).length > 0) {
        //slots
        var i;
        $('#rescheduleDateAndSlot').html('');
        var defaultOption = $('<option value="-1"></option>').text("Select Time Slot");
        $('#rescheduleDateAndSlot').append(defaultOption);

        var interviewDetailsList = jobPostInfo.interviewDetailsList;
        if (interviewDetailsList[0].interviewDays != null) {
            var interviewDays = interviewDetailsList[0].interviewDays.toString(2);

            /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
            if (interviewDays.length != 7) {
                x = 7 - interviewDays.length;
                var modifiedInterviewDays = "";

                for (i = 0; i < x; i++) {
                    modifiedInterviewDays += "0";
                }
                modifiedInterviewDays += interviewDays;
                interviewDays = modifiedInterviewDays;
            }
        }

        var oldSelectedDate = new Date(oldDate);
        //slots
        var today = new Date();
        for (i = 2; i < 9; i++) {
            // 0 - > sun 1 -> mon ...
            var x = new Date(today.getFullYear(), today.getMonth(), today.getDate() + i);
            if (checkSlotAvailability(x, interviewDays)) {
                interviewDetailsList.forEach(function (timeSlot) {
                    var dateSlotSelectedId = x.getFullYear() + "-" + (x.getMonth() + 1) + "-" + x.getDate() + "_" + timeSlot.interviewTimeSlot.interviewTimeSlotId;
                    var option = $('<option value="' + dateSlotSelectedId + '"></option>').text(getDayVal(x.getDay()) + ", " + x.getDate() + " " + getMonthVal((x.getMonth() + 1)) + " (" + timeSlot.interviewTimeSlot.interviewTimeSlotName + ")");
                    if((oldSelectedDate.getDate() == x.getDate()) && (oldSelectedDate.getMonth() == x.getMonth()) && (oldSlot == timeSlot.interviewTimeSlot.interviewTimeSlotId)){} else{
                        $('#rescheduleDateAndSlot').append(option);
                    }
                });
            }
        }

        //showing popup
        var rescheduleDialog = document.getElementById("rescheduleDialog");
        if (! rescheduleDialog.showModal) {
            dialogPolyfill.registerDialog(dialog);
        }
        rescheduleDialog.showModal();
        rescheduleDialog.querySelector('.closeReschedule').addEventListener('click', function() {
            rescheduleDialog.close();
        });
        rescheduleDialog.querySelector('.confirmReschedule').addEventListener('click', function() {
            confirmRescheduleInterview();
        });

    } else{
        alert("No Slots available!");
    }

}

function processDataInterviewStatus(returnedData) {
    if(returnedData == "1"){
        if(globalInterviewStatus == 1){
            $("#interview_status_option_" + globalCandidateId).html("Interview confirmed");
        } else if(globalInterviewStatus == 2){
            $("#interview_status_option_" + globalCandidateId).html("Interview rejected");
            var reasonDialog = document.getElementById("reasonDialog");
            if (! reasonDialog.showModal) {
                dialogPolyfill.registerDialog(dialog);
            }
            reasonDialog.close();
        } else if(globalInterviewStatus == 3){
            $("#interview_status_option_" + globalCandidateId).html("Interview Rescheduled. Awaiting Candidate response");
            var rescheduleDialog = document.getElementById("rescheduleDialog");
            if (! rescheduleDialog.showModal) {
                dialogPolyfill.registerDialog(dialog);
            }
            rescheduleDialog.close();

            var newDate = new Date(rescheduledDate);
            var i, newSlot;
            for(i=0; i<Object.keys(allTimeSlots).length; i++){
                if(allTimeSlots[i].id == rescheduledSlot){
                    newSlot = allTimeSlots[i].name;
                }
            }
            $("#interview_date_" + globalCandidateId).html(('0' + newDate.getDate()).slice(-2) + '-' + getMonthVal((newDate.getMonth()+1)) + " @" + newSlot);
        }
        notifyError("Submitted successfully. Refreshing ..", 'success');
        setTimeout(function () {
            location.reload();
        }, 2000);
    }
}

function confirmUpdateStatusNotGoing(){
    if($("#notGoingReasonOption").val() > 0){
        confirmUpdateInterviewStatus();
        triggerEtaModal = false;
        triggerNotGoingModal = false;

    } else{
        alert("Please select a reason for not going for interview");
    }
}


function updateStatus(candidateId) {
    if($("#interview_status_" + candidateId).val() > 0){
        globalCandidateId = candidateId;
        globalCandidateStatus = $("#interview_status_" + candidateId).val();

        if(globalCandidateStatus == 1){
            triggerEtaModal = false;
            triggerNotGoingModal = true;
        } else if(globalCandidateStatus == 2 || globalCandidateStatus == 3){
            triggerEtaModal = true;
            triggerNotGoingModal = false;
        }

        confirmUpdateInterviewStatus();
    } else {
        notifyError("Please select a status", 'danger');
    }
}

function confirmUpdateInterviewStatus(){
    var notGoingReasonId = 0;
    if($("#notGoingReasonOption").val() != null){
        notGoingReasonId = $("#notGoingReasonOption").val();
    }
    NProgress.start();
    try {
        $.ajax({
            type: "POST",
            url: "/updateStatus/" + globalCandidateId + "/" + jobPostId + "/" + $("#interview_status_" + globalCandidateId).val() + "/" + notGoingReasonId,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForUpdateStatus
        });
        NProgress.done();
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }

}

function processDataForUpdateStatus(returnedData) {
    if(returnedData == 1){
        console.log(triggerNotGoingModal);
        if(triggerNotGoingModal){
            var reasonDialog = document.getElementById("notGoingReason");

            $("#notGoingReasonOption").html('');

            defaultOption = $('<option value="0" selected></option>').text("Select a reason");
            $('#notGoingReasonOption').append(defaultOption);

            allNotGoingReasons.forEach(function (reason) {
                var option = $('<option value=' + reason.id + '></option>').text(reason.name);
                $('#notGoingReasonOption').append(option);
            });
            $("#modalTitle").html('Reason for not Going for the Interview');
            $("#modalHeading").html('Please specify reason for Not going');
            $("#modalSubHeading").html('Please select a reason for not going:');

            reasonDialog.showModal();
            reasonDialog.querySelector('.closeNotGoing').addEventListener('click', function() {
                reasonDialog.close();
            });
            reasonDialog.querySelector('.confirmNotGoing').addEventListener('click', function() {
                confirmUpdateStatusNotGoing();
            });
        } else if(triggerEtaModal){
            reasonDialog = document.getElementById("notGoingReason");
            $("#notGoingReasonOption").html('');

            var defaultOption = $('<option value="0" selected></option>').text("Select an option");
            $('#notGoingReasonOption').append(defaultOption);

            allCandidateEta.forEach(function (reason) {
                var option = $('<option value=' + reason.id + '></option>').text(reason.name);
                $('#notGoingReasonOption').append(option);
            });

            $("#modalTitle").html('Reaching in?');
            $("#modalHeading").html('Please tell your estimate reaching time');
            $("#modalSubHeading").html('Please select a time:');

            reasonDialog.showModal();
            reasonDialog.querySelector('.closeNotGoing').addEventListener('click', function() {
                reasonDialog.close();
            });
            reasonDialog.querySelector('.confirmNotGoing').addEventListener('click', function() {
                confirmUpdateStatusNotGoing();
            });
        } else{
            notifyError("Status updated successfully. Refreshing ..", 'success');
            setTimeout(function () {
                location.reload();
            }, 2000);
        }
    } else{
        notifyError("Something went wrong", 'danger');
    }
}

//helper functions

function processDataGetAllTimeSlots(returnedData) {
    returnedData.forEach(function(timeSlot) {
        var id = timeSlot.interviewTimeSlotId;
        var name = timeSlot.interviewTimeSlotName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allTimeSlots.push(item);
    });
}

function processDataGetAllReason(returnedData) {
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allReasons.push(item);
    });
}

function processDataGetAllInterviewNotGoingReason(returnedData) {
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allNotGoingReasons.push(item);
    });
}

function processDataGetAllCandidateEta(returnedData) {
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allCandidateEta.push(item);
    });
}

function processDataGetAllNotSelectedReason(returnedData) {
    returnedData.forEach(function(reason) {
        var id = reason.reasonId;
        var name = reason.reasonName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        allRejectReason.push(item);
    });
}

function getDayVal(month){
    switch(month) {
        case 0:
            return "Sun";
            break;
        case 1:
            return "Mon";
            break;
        case 2:
            return "Tue";
            break;
        case 3:
            return "Wed";
            break;
        case 4:
            return "Thu";
            break;
        case 5:
            return "Fri";
            break;
        case 6:
            return "Sat";
            break;
    }
}

function getMonthVal(month){
    switch(month) {
        case 1:
            return "Jan";
            break;
        case 2:
            return "Feb";
            break;
        case 3:
            return "Mar";
            break;
        case 4:
            return "Apr";
            break;
        case 5:
            return "May";
            break;
        case 6:
            return "Jun";
            break;
        case 7:
            return "Jul";
            break;
        case 8:
            return "Aug";
            break;
        case 9:
            return "Sep";
            break;
        case 10:
            return "Oct";
            break;
        case 11:
            return "Nov";
            break;
        case 12:
            return "Dec";
            break;
    }
}

function checkSlotAvailability(x, interviewDays) {
    if(x.getDay() == 1 && interviewDays.charAt(0) == '1'){ // monday
        return true;
    } else if(x.getDay() == 2 && interviewDays.charAt(1) == '1'){ //tue
        return true;
    } else if(x.getDay() == 3 && interviewDays.charAt(2) == '1'){ //wed
        return true;
    } else if(x.getDay() == 4 && interviewDays.charAt(3) == '1'){ //thu
        return true;
    } else if(x.getDay() == 5 && interviewDays.charAt(4) == '1'){ //fri
        return true;
    } else if(x.getDay() == 6 && interviewDays.charAt(5) == '1'){ //sat
        return true;
    } else if(x.getDay() == 0 && interviewDays.charAt(6) == '1'){ //sun
        return true;
    }
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

