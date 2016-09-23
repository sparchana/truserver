/**
 * Created by zero on 23/9/16.
 */

(function(){
    'use strict';
    var app = {
        userId: 0,
        userJobPrefs: [],
        userAssessedJobPrefs: [],
        container: $('#assessmentDiv'),
        limit: 3,
        url: "/getCandidateJobPrefs/?limit=3",
        isIndicatorAvailable: false
    };

    app.updateCandidateJobPrefs = function () {
        if (!app.userJobPrefs){
            app.selectedCities = [];
        }
        // Fetch the latest data.
        var request = new XMLHttpRequest();
        request.onreadystatechange = function() {
            if (request.readyState === XMLHttpRequest.DONE) {
                if (request.status === 200) {
                    var jobPrefList = JSON.parse(request.response);
                    jobPrefList.forEach(function (bundle) {
                        app.userJobPrefs.push(bundle);
                    });
                    // app.saveJobPrefs();
                    app.createAssessmentIndicator(jobPrefList.length);
                }
            } else {
                // Return the localStorage Value.
                // app.updateUserPrefJobs(localStorage.getItem("userJobPrefs"));
            }
        };
        request.open('GET', app.url);
        request.send();
    };

    app.createAssessmentIndicator = function (size) {
        if (!document.querySelector('#assessmentDivRow') && size != null ){
            var noc = 12/size;
            var assessmentDivRow = $('<div id="assessmentDivRow" class="row"></div>');
            app.userJobPrefs.forEach(function (bundle) {
                var isAssessed = bundle.assessed;
                var card = $('<div class="col-xs-'+noc+'" id="jr_id_'+bundle.jobPreference.jobRole.jobRoleId+'" ' +
                    'style="padding: 0px;"></div>');
                var a;
                if(isAssessed){
                    a= $('<a href="#" data-toggle="tooltip" data-placement="bottom" title="Completed !">' +
                        '<span class="glyphicon glyphicon-star yellow" aria-hidden="true"></a>');
                } else {
                    a= $('<a href="#" data-toggle="tooltip" data-placement="bottom" title="Click Now !! ' +
                        ' to increase chances of getting Interview Calls' +
                        ' !"><span class="glyphicon glyphicon-exclamation-sign red" aria-hidden="true"></a>');

                }
                /* red label
                if(isAssessed){
                 a= $('<a href="#"><div><b><font color="#fff">Complete</font></b></div></a>');
                 } else {
                 a= $('<a href="#"><div class="indicatorBtnRed"><b><font color="#fff">Incomplete</font></b></div></a>');

                 }
                 */
                var titleDiv = $('<div id="jr_'+bundle.jobPreference.jobRole.jobRoleId+'" class="assessmentTitle" onclick="getAssessmentQuestions('+bundle.jobPreference.jobRole.jobRoleId+', null)"></span></div>');
                var text = $('<b><font color="#fff">'+bundle.jobPreference.jobRole.jobName+'</font></b>');

                titleDiv.append(text);
                a.append(titleDiv);
                card.append(a);
                assessmentDivRow.append(card);
            });
            app.container.append(assessmentDivRow);
            app.isIndicatorAvailable = true;
        }
    };

    app.saveJobPrefs = function() {
        var userJobPrefs = JSON.stringify(app.userJobPrefs);
        localStorage.userJobPrefs = userJobPrefs;
        console.log("localStorage: "+localStorage.userJobPrefs);
    };

    app.updateCandidateJobPrefs();
})();