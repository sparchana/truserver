function createRadioButton(name, value, text, id) {
    var label = document.createElement("label");
    label.style.margin = "8px";
    label.class = "col-md-4";
    var radio = document.createElement("input");
    radio.type = "radio";
    radio.style.margin = "3px";
    radio.name = name;
    radio.value = value;
    radio.id = id;

    label.appendChild(radio);

    label.appendChild(document.createTextNode(text));
    if(value == null){
        return null;
    }
    return label;
}

function processAssessmentQuestions(returnedData) {
    if(returnedData != null){
        if(returnedData == "Already Done" || returnedData == "NA"){
            return;
        }
        if($(".assessment-modal").size() > 0){
            return;
        }
        var assessmentBody = $('<div id="assessment_body"></div>');
        var prevJobRole = null;
        var jobRoleContainer;
        returnedData.forEach(function (assessmentQ) {
            if(assessmentQ != null){
                if(prevJobRole == null || prevJobRole != assessmentQ.jobRole.jobRoleId){
                    jobRoleContainer = $('<div id="job_role_container_'+assessmentQ.jobRole.jobRoleId+'"><h4>'+assessmentQ.jobRole.jobName+'</h4></div>');
                }
                var questionCard = $('<div id='+"QuestionID_" + assessmentQ.jobRole.jobRoleId +"_"+ assessmentQ.assessmentQuestionId+' class= "question_contianer"></div>').text(assessmentQ.questionText);
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
            title: "Assessment Wizard",
            message: assessmentBody,
            closeButton: true,
            animate: true,
            buttons: {
                "Cancel": {
                    className: "btn-default",
                    callback: function() {
                        console.log(false);
                    }
                },
                "Submit": {
                    className: "btn-primary",
                    callback: function() {
                        triggerFinalSubmission();
                    }
                }
            },
            callback: function(result) {
                console.log(result);
            }
        });
    }
}

function processPostAssessmentResponse(status) {
    console.log(JSON.stringify(status));
    if(status == "ok"){
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
        console.log("exception occured!!" + exception);
    }
}