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
        $('.btn-success.btn-modal-submit').prop('disabled', false);
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
        if(returnedData == "assessed" || returnedData == "NA"){
            processPostAssessmentResponse(returnedData);
            return;
        }
        if($(".assessment-modal").size() > 0){
            return;
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

function processPostAssessmentResponse(status) {
    console.log(JSON.stringify(status));
    if(status == "assessed") {
        localStorage.setItem("assessed", "1");
        $(".assessmentIncomplete").hide();
        $(".assessmentComplete").show();
    }
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

function getAssessmentQuestions(jobRoleId, jobPostId) {

    var base_api_url ="/getAssessmentQuestions/";
    if(base_api_url != null || jobPostId != null) {
        base_api_url +="?";
        if(jobRoleId != null) {
            base_api_url += "jobRoleIds=" + jobRoleId;
        }
        if(jobPostId != null){
            base_api_url += "jobPostIds=" + jobPostId;
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
