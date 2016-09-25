var totalLeftAttempts = 0;
var prevId = 0;

function createRadioButton(name, value, text, id) {
    var label = document.createElement("label");
    label.style.margin = "8px 8px 8px 44px";
    label.style.display= "block";
    label.class = "col-md-4";
    label.style.fontWeight = "normal";
    var radio = document.createElement("input");
    radio.type = "radio";
    radio.style = "margin-left: -20px; margin-right: 7px;";
    radio.name = name;
    radio.value = value;
    radio.id = id;
    radio.onclick = function () {
        var qId = this.id.split("_")[2];
        if(totalLeftAttempts > 0 && prevId != qId){
            --totalLeftAttempts;
            prevId = qId;
        } if(totalLeftAttempts == 0){
            $('.btn-success.btn-modal-submit').prop('disabled', false);
        }
    };

    label.appendChild(radio);

    label.appendChild(document.createTextNode(text));
    if(value == null){
        return null;
    }
    return label;
}

function processAssessmentQuestions(returnedData) {
    if(returnedData != null){
        if(returnedData == "OK" || returnedData == "NA" ) {
            processPostAssessmentResponse(returnedData);
            return returnedData;
        }
        if($(".assessment-modal").size() > 0){
            return returnedData;
        }
        var assessmentBody = $('<div id="assessment_body"></div>');
        var prevJobRole = null;
        var jobRoleContainer;
        var qCount = 0;
        returnedData.forEach(function (assessmentQ) {
            if(assessmentQ != null){
                qCount++;
                if(prevJobRole == null || prevJobRole != assessmentQ.jobRole.jobRoleId){
                    jobRoleContainer = $('<div id="job_role_container_'+assessmentQ.jobRole.jobRoleId+'" class=""><h4 class="asssessmentTitle" >' +
                        'Assessment for '+assessmentQ.jobRole.jobName+'</h4></div>');
                    qCount = 1;
                }
                var questionCard = $('<div id='+"QuestionID_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId+' class= "question_contianer" style="font-weight: bold;">' +
                    '</div>').text("Q"+ qCount +". "+assessmentQ.questionText);
                var optionCardContainer = $('<div id='+"OptionContainerID_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId+' class= "row optionContainer"></div>');

                if(assessmentQ.assessmentQuestionType != null &&  assessmentQ.assessmentQuestionType.assessmentQuestionTypeId == 1) {
                    var optionCard;
                    optionCard = createRadioButton( "RN_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId, assessmentQ.optionA, assessmentQ.optionA, "OID_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId +"_1");
                    if(optionCard != null) optionCardContainer.append(optionCard);
                    optionCard = createRadioButton( "RN_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId, assessmentQ.optionB, assessmentQ.optionB, "OID_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId +"_2");
                    if(optionCard != null) optionCardContainer.append(optionCard);
                    optionCard = createRadioButton( "RN_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId, assessmentQ.optionC, assessmentQ.optionC, "OID_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId +"_3");
                    if(optionCard != null) optionCardContainer.append(optionCard);
                    optionCard = createRadioButton( "RN_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId, assessmentQ.optionD, assessmentQ.optionD, "OID_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId +"_4");
                    if(optionCard != null) optionCardContainer.append(optionCard);
                    optionCard = createRadioButton( "RN_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId, assessmentQ.optionE, assessmentQ.optionE, "OID_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId +"_5");
                    if(optionCard != null) optionCardContainer.append(optionCard);

                    if(optionCardContainer != null) questionCard.append(optionCardContainer);
                }
                jobRoleContainer.append(questionCard);

                if(prevJobRole == null || prevJobRole != assessmentQ.jobRole.jobRoleId ){
                    assessmentBody.append(jobRoleContainer);
                    prevJobRole = assessmentQ.jobRole.jobRoleId;
                }
            }
        });
        totalLeftAttempts = qCount;
        bootbox.dialog({
            className: "assessment-modal",
            title: "<h3 class='assessment-modal-title' style='color: #286ab6'>Job Application Assessment</h3><h5>Complete Assessment to Improve Chance of getting an Interview Call</h5>",
            message: assessmentBody,
            closeButton: true,
            animate: true,
            onEscape: function() {
                $('body').removeClass('open-modal');
            },
            buttons: {
                "Submit": {
                    className: "btn-success btn-modal-submit",
                    callback: function() {
                        $('body').removeClass('open-modal');
                        if($("#assessment_body input[type='radio']:checked").size() > 0){
                            triggerFinalSubmission();
                        }
                    }
                }
            },
            callback: function(result) {
                console.log(result);
            }
        });
        $('.btn-success.btn-modal-submit').prop('disabled', true);
        $('body').removeClass('modal-open').removeClass('open-modal').addClass('open-modal');
    }
}

function processPostAssessmentResponse(response) {
    $('#customMsgIcon').attr('src', "/assets/common/img/jobApplied.png");
    if($('#messagePromptModal').hasClass('in')){
        $("#customMsg").append(" & You have successfully completed assessment for this job.");
    } else{
        $("#customMsg").html(" You have completed assessment for this job.");
    }
    if (response.status == "ALL_ASSESSED"){
        localStorage.setItem("assessed", "1");
        $('#assessmentDivRow span').removeClass("glyphicon-exclamation-sign red").addClass(" glyphicon-star yellow");
        $('#assessmentDivRow a').attr("title", "Completed !");
        $("#messagePromptModal").modal("show");
    } else if (response == "OK"  || response.status == "SUCCESS" || response.status == "ALREADY_ASSESSED") {
        $('#jr_id_'+response.jobRoleId+' span').removeClass("glyphicon-exclamation-sign red").addClass(" glyphicon-star yellow");
        $('#tt_'+response.jobRoleId+'_ic').attr("title", "Completed !");
        $("#messagePromptModal").modal("show");
    }
    return response;
}

function triggerFinalSubmission() {
    var allSelectedValues = $("#assessment_body input[type='radio']:checked");
    var responseList = [];
    var len = allSelectedValues.size();
    for (var j = 0; j < len; j++) {
        var radio = allSelectedValues[j];
        var ids = radio.id.split("_");
        var item = {};
        item ["jobRoleId"] = ids[1];
        item ["assessmentQuestionId"] = ids[2];
        item ["assessmentResponse"] = radio.value;
        responseList.push(item);
    }

    var d = {
        responseList: responseList
    };

    try {
        $.ajax({
            type: "POST",
            url: "/submitAssessment",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(d),
            success: processPostAssessmentResponse
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
}

function getAssessmentQuestions(jobRoleIds, jobPostIds) {
    var base_api_url ="/getAssessmentQuestions/";
    if(base_api_url != null || jobPostId != null) {
        base_api_url +="?";
        if(jobRoleIds != null) {
            base_api_url += "jobRoleIds=" + jobRoleIds;
        }
        if(jobPostIds != null){
            base_api_url += "&jobPostIds=" + jobPostIds;
        }
    }

    try {
        $.ajax({
            type: "GET",
            url: base_api_url,
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processAssessmentQuestions
        });
    } catch (exception) {
        console.log("exception occured!!" + exception.stack);
    }
}
