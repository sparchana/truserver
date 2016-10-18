/**
 * Created by zero on 07/10/16.
 */
var globalRecAgentNumber;
var allLocalityArray = [];
var allJobArray = [];
var shouldAddFooter = true;
var jobPostId;


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
    console.log("will open pre screen modal in future for candidate_id : " + mobile);
    getPreScreenContent(jobPostId, candidateId);
};


callHandler = function (mobile, candidateId) {
    console.log("agentMobileNumber:" + globalRecAgentNumber);
    if (typeof globalRecAgentNumber != 'undefined') {
        console.log("Call Initiated for " + "+" + mobile + " by " + globalRecAgentNumber);
        var s = {
            api_key: "dae93473-50a6-11e5-bbe8-067cf20e9301",
            agent_number: globalRecAgentNumber,
            phone_number: "+" + mobile,
            sr_number: "+918880007799"
        };

        try {
            $.ajax({
                url: "https://sr.knowlarity.com/vr/api/click2call/",
                async: false,
                type: "POST",
                data: s,
                contentType: "jsonp",
                dataType: 'jsonp',
                cache: !1,
                success: function (returnedData) {
                    console.log("KW Response : " + JSON.stringify(returnedData));
                },
                error: function (error) {
                    console.log("Response Error: " + JSON.stringify(error));
                }
            });
        } catch (exception) {
            console.log("exception:" + exception.stack);
        }
    }
    openPreScreenModal(mobile, candidateId);
};

$(function () {
    'use strict';
    var app = {
        jpId: null,
        jpJobRoleId: 0,
        jpLocalityIdList: [],
        jpMinSalary: null,
        jpMaxSalary: null,
        jpMaxAge: null,
        jpExperienceId: null,
        jpGender: null,
        jpEducationId: null,
        jpLanguageIdList: [],
        jpTableFormattedCandidateList: [],
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
            type: type
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

        function processDataCheckEducation(returnedEdu) {
            if (returnedEdu != null) {
                var data = [
                    {label: "None Selected", value: "0"}
                ];

                returnedEdu.forEach(function (education) {
                    var opt = {
                        label: education.educationName, value: parseInt(education.educationId)
                    };
                    data.push(opt);
                });

                var selectList = $('#educationSelect');
                selectList.multiselect({
                    nonSelectedText: 'None Selected',
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
            var data = [
                {label: "Select None", value: "0"}
            ];

            returnedExperience.forEach(function (experience) {
                var opt = {
                    label: experience.experienceType, value: parseInt(experience.experienceId)
                };
                data.push(opt);
            });

            var selectList = $('#experienceSelect');
            selectList.multiselect({
                nonSelectedText: 'None Selected',
                maxHeight: 300
            });
            selectList.multiselect('dataprovider', data);
            selectList.multiselect('rebuild');
        }

        NProgress.done();
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
        NProgress.done();
    };

    app.processParamsAndUpdateUI = function (returnedData) {
        if (returnedData != null) {
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

            if (returnedData.jobPostExperience != null) {
                app.jpExperienceId = returnedData.jobPostExperience.experienceId;
                $("#experienceSelect").val(app.jpExperienceId);
                $("#experienceSelect").multiselect('rebuild');
            }
            if (returnedData.jobPostEducation != null) {
                app.jpEducationId = returnedData.jobPostEducation.educationId;
                $("#educationSelect").val(app.jpEducationId);
                $("#educationSelect").multiselect('rebuild');
            }
            if (returnedData.jobPostLanguageRequirement != null) {
                var req = returnedData.jobPostLanguageRequirement;
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
        NProgress.start();

        var i;
        var modifiedLocality = $('#jobPostLocality').val().split(",");
        var modifiedLanguageIdList = $('#languageMultiSelect').val();


        // this also converts string data to integer and sends to server
        app.jpLocalityIdList = [];
        if (modifiedLanguageIdList != null) {
            app.jpLanguageIdList = [];
            for (i = 0; i < modifiedLanguageIdList.length; i++) {
                app.jpLanguageIdList.push(parseInt(modifiedLanguageIdList[i]));
            }
        }
        if (modifiedLocality != null) {
            for (i = 0; i < modifiedLocality.length; i++) {
                app.jpLocalityIdList.push(parseInt(modifiedLocality[i]));
            }
        }

        app.jpJobRoleId = null;

        app.jpJobRoleId = (parseInt($("#jobPostJobRole").val()));

        app.jpExperienceId = $('#experienceSelect').val();

        app.jpEducationId = $('#educationSelect').val();

        app.jpGender = $('#genderSelect').val();

        app.jpMinSalary = $('#minSalary').val();
        app.jpMaxSalary = $('#maxSalary').val();
        app.jpMaxAge = $('#maxAge').val();

        if (app.jpJobRoleId == null || app.jpLocalityIdList == null || app.jpLocalityIdList.length == 0) {
            app.shouldSend = false;
        }

        if (app.shouldSend) {

            var d = {
                jobPostId: app.jpId,
                maxAge: app.jpMaxAge,
                minSalary: app.jpMinSalary,
                maxSalary: app.jpMaxSalary,
                experienceId: app.jpExperienceId,
                gender: app.jpGender,
                jobPostJobRoleId: app.jpJobRoleId,
                jobPostEducationId: app.jpEducationId,
                jobPostLocalityIdList: app.jpLocalityIdList,
                jobPostLanguageIdList: app.jpLanguageIdList
            };

            NProgress.start();
            try {
                $.ajax({
                    type: "POST",
                    url: app.url,
                    async: false,
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(d),
                    success: app.updateMatchTable
                });
            } catch (exception) {
                console.log("exception occured!!" + exception.stack);
                NProgress.done();
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
            NProgress.done();
            return;
        } else {
            $('#moveSelectedBtn').attr('disabled', false);
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

                var preScreenAttemptCount = function () {
                    if (app.currentView == "pre_screen_view" && newCandidate.extraData.preScreenCallAttemptCount != null) {
                        return newCandidate.extraData.preScreenCallAttemptCount
                    } else {
                        return "0";
                    }
                };
                var varColumn = function () {
                    if (app.currentView == "pre_screen_view") {
                        if(newCandidate.extraData.preScreenCallAttemptCount == null) {
                            return '<input type="submit" value="Call"  style="width:100px" onclick="callHandler(' + newCandidate.candidate.candidateMobile + ', ' + newCandidate.candidate.candidateId + ');" id="' + newCandidate.candidate.lead.leadId + '" class="btn btn-primary">'
                        } else {
                            return '<input type="submit" value="Call Back"  style="width:100px" onclick="callHandler(' + newCandidate.candidate.candidateMobile + ', ' + newCandidate.candidate.candidateId + ');" id="' + newCandidate.candidate.lead.leadId + '" class="btn btn-default">'
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
                    'candidateSkillList': getSkills(newCandidate.candidate.candidateSkillList),
                    'candidateGender': getGender(newCandidate.candidate.candidateGender),
                    'pastOrCurrentCompanyName': getPastOrCurrentCompanyName(newCandidate.candidate.jobHistoryList),
                    'candidateIsAssessmentComplete': getYesNo(newCandidate.extraData.assessmentAttemptId),
                    'jobAppliedOn': getAppliedOn(newCandidate.extraData.appliedOn),
                    'noOfJobApplication': newCandidate.candidate.jobApplicationList.length,
                    'candidateExperienceLetter': getYesNo(newCandidate.candidate.candidateExperienceLetter),
                    'candidateIdProofs': getIdProof(newCandidate.candidate.idProofReferenceList),
                    'candidateTimeShiftPref': timeShiftPref,
                    'lastActive': (newCandidate.extraData.lastActive.lastActiveValueName),
                    'candidateCreateTimestamp': getDateTime(newCandidate.candidate.candidateCreateTimestamp),
                    'isMinProfileComplete': getYesNo(newCandidate.candidate.isMinProfileComplete),
                    'experience': getExperience(newCandidate.candidate.candidateExpList),
                    'candidateId': newCandidate.candidate.candidateId,
                    'preScreenAttempt': preScreenAttemptCount,
                    'varColumn': varColumn
                })
            });

            app.tableContainer.show();

            var select;
            if (app.currentView == "match_view") {
                select = {
                    "style": 'multi'
                };
            } else {
                select = false;
            }

            app.table = $('table#' + app.tableContainerId).DataTable({
                "data": returnedDataArray,
                "order": [[22, "desc"]],
                "scrollX": true,
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
                    {"data": "candidateSkillList"},
                    {"data": "candidateGender"},
                    {"data": "pastOrCurrentCompanyName"},
                    {"data": "candidateIsAssessmentComplete"},
                    {"data": "jobAppliedOn"},
                    {"data": "noOfJobApplication"},
                    {"data": "candidateExperienceLetter"},
                    {"data": "candidateIdProofs"},
                    {"data": "candidateTimeShiftPref"},
                    {"data": "lastActive"},
                    {"data": "candidateCreateTimestamp"},
                    {"data": "isMinProfileComplete"},
                    {"data": "experience"},
                    {"data": "candidateId"},
                    {"data": "preScreenAttempt"},
                    {"data": "varColumn"}
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
        console.log(app.jpSelectedCandidateList);
    };

    app.submitForm = function () {
        app.formSubmit.submit(function (eventObj) {
            eventObj.preventDefault();
            app.fetchCandidateList();
        });
    };

    app.submitSelectedCandidateList = function () {
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
        NProgress.done();

    };

    app.responseInterpreter = function (response) {
        console.log(response);
        if (response.status == "SUCCESS") {
            app.currentView = response.nextView;
            app.notify(response.message + " Redirecting to " + response.nextView, 'success');
            setTimeout(function () {
                window.location = response.redirectUrl + app.jpId + "/?view=" + response.nextView;
            }, 3000)
        } else {
            app.notify(response.message, 'danger');
        }
    };

    // pre_screen methods
    app.initPreScreenView = function () {
        NProgress.start();
        var d = {};

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
    }
    else if (app.currentView == "pre_screen_view") {
        app.getSupportAgent();
        app.initPreScreenView();
    }
});