/**
 * Created by hawk on 16/1/17.
 */

/* trigger if bulk resume is selected */
function uploadBulkResumes(evt){
    var files = evt.target.files;
    var url = "/addResume/";
    var ext;
    var data = new FormData();
    for (var i = 0, f; f = files[i]; i++) {
        ext = (f.name).split('.').pop().toLowerCase();
        data.append('resume'+(i+1),f);
    }
    if(ext == "pdf" || ext == "docx" || ext == "doc"){
        try {
            $.ajax({
                type: "POST",
                url: url,
                data: data,
                async: true,
                cache: false,
                contentType: false,
                processData: false,
                success: processDataForBulkResumes
            });
            $('#uploadResumeModalProcess').modal('show');
            $('#candidateCreateOptionModal').modal('hide');
        } catch(exception) {
            console.log("Exception Occurred!!" + exception);
            console.log(new Error().stack);
        }
    }else{
        notifyError("Invalid Format. Please upload file with PDF, Doc or Docx format");
    }
}

function processDataForBulkResumes(returnedData) {
    $('#uploadResumeModalProcess').modal('hide');

    //for partner
    $(".textResponse").hide();

    if(returnedData != null){
        if(returnedData.status == 1)
        {
            var parent = $("#uploadResumeModalContent");
            $('#uploadResumeModalContent').html("");

            var resumeRow = document.createElement("div");
            resumeRow.className = "row";
            parent.append(resumeRow);

            var resumeColLeft = document.createElement("div");
            resumeColLeft.className = "col-sm-3";
            resumeRow.appendChild(resumeColLeft);

            var resumeColRight = document.createElement("div");
            resumeColRight.className = "col-sm-9";
            resumeColRight.id = "resumeColMsgSuccess";
            resumeRow.appendChild(resumeColRight);

            var image = document.createElement("img");
            image.src = "/assets/partner/img/tick.svg";
            image.style = "height:100px";
            resumeColLeft.appendChild(image);

            var h5 = document.createElement("h5");
            h5.textContent = "Thanks !! Resume has been successfully uploaded";
            resumeColRight.appendChild(h5);

            var h4 = document.createElement("h4");
            h4.textContent = "You will shortly receive a SMS with more details";
            h4.style = "font-weight:600";
            resumeColRight.appendChild(h4);

            $("#uploadResumeModalContent").show();
            $('#uploadResumeModal').modal('show');
            setTimeout(function(){
                $('#uploadResumeModal').modal('hide');
            }, 3000);
            $(".textResponse").hide();

        }
        else{
            var parent = $("#uploadResumeModalContent");
            $("#uploadResumeModalContent").css("background","rgb(253, 132, 105)");
            $('#uploadResumeModalContent').html("");

            var resumeRow = document.createElement("div");
            resumeRow.className = "row";
            parent.append(resumeRow);

            var resumeColLeft = document.createElement("div");
            resumeColLeft.className = "col-sm-4";
            resumeRow.appendChild(resumeColLeft);

            var resumeColRight = document.createElement("div");
            resumeColRight.className = "col-sm-8";
            resumeColRight.id = "resumeColMsgFail";
            resumeRow.appendChild(resumeColRight);

            var image = document.createElement("img");
            image.src = "/assets/common/img/warning.svg";
            image.style = "height:100px";
            resumeColLeft.appendChild(image);

            var h5 = document.createElement("h5");
            h5.textContent = "Opps!! Something went wrong";
            resumeColRight.appendChild(h5);

            var h4 = document.createElement("h4");
            h4.textContent = "Please try after sometime";
            h4.style = "font-weight:600";
            resumeColRight.appendChild(h4);

            $("#uploadResumeModalContent").show();
            $('#uploadResumeModal').modal('show');
            setTimeout(function(){
                $('#uploadResumeModal').modal('hide');
            }, 3000);
        }
    }
    else{
        notifyWarning("Invalid Format");
    }

}


/* trigger if individual candidate resume upload is selected */
function uploadResumeCandidate(evt,candidateId) {
    var files = evt.target.files;
    var url = "/addResume/?candidateId="+candidateId;
    var ext;
    var data = new FormData();
    for (var i = 0, f; f = files[i]; i++) {
        ext = (f.name).split('.').pop().toLowerCase();
        data.append('resume'+(i+1),f);
    }
    if(ext == "pdf" || ext == "docx" || ext == "doc"){
        try {
            $.ajax({
                type: "POST",
                url: url,
                data: data,
                async: true,
                cache: false,
                contentType: false,
                processData: false,
                success: processDataForSingleResume
            });
            $('#uploadResumeModalProcess').modal('show');
            $('#candidateCreateOptionModal').modal('hide');
        } catch(exception) {
            console.log("Exception Occurred!!" + exception);
            console.log(new Error().stack);
        }
    }else{
        notifyError("Invalid Format. Please upload file with PDF, Doc or Docx format");
    }
}
function processDataForSingleResume(returnedData) {
    $('#uploadResumeModalProcess').modal('hide');
    if(returnedData != null){
        if(returnedData.status == 1)
        {
            var parent = $("#uploadResumeModalContent");
            $('#uploadResumeModalContent').html("");

            var resumeRow = document.createElement("div");
            resumeRow.className = "row";
            parent.append(resumeRow);

            var resumeColLeft = document.createElement("div");
            resumeColLeft.className = "col-sm-3";
            resumeRow.appendChild(resumeColLeft);

            var resumeColRight = document.createElement("div");
            resumeColRight.className = "col-sm-9";
            resumeColRight.id = "resumeColMsgSuccess";
            resumeRow.appendChild(resumeColRight);

            var image = document.createElement("img");
            image.src = "/assets/partner/img/tick.svg";
            image.style = "height:100px";
            resumeColLeft.appendChild(image);

            var h5 = document.createElement("h4");
            h5.textContent = "Thanks !! Resume has been successfully uploaded";
            h5.style = "font-weight:600;padding-top:4%";
            resumeColRight.appendChild(h5);


            $("#uploadResumeModalContent").show();
            $('#uploadResumeModal').modal('show');
            setTimeout(function(){
                $('#uploadResumeModal').modal('hide');
            }, 3000);
            $(".textResponse").hide();

            if(returnedData.candidateResumeLink != null){

                var parent = $("#resumeLink_"+returnedData.candidateId);
                parent.html("");

                var viewBtn = '<a href="http://docs.google.com/gview?url='+returnedData.candidateResumeLink+'&embedded=true" target="_blank">'+
                '<button type="button" class="mBtn blue" id="viewCandidateResumeBtn" >View</button>'+
                '</a>';
                parent.append(viewBtn);
            }
        }
        else{
            var parent = $("#uploadResumeModalContent");
            $("#uploadResumeModalContent").css("background","rgb(253, 132, 105)");
            $('#uploadResumeModalContent').html("");

            var resumeRow = document.createElement("div");
            resumeRow.className = "row";
            parent.append(resumeRow);

            var resumeColLeft = document.createElement("div");
            resumeColLeft.className = "col-sm-4";
            resumeRow.appendChild(resumeColLeft);

            var resumeColRight = document.createElement("div");
            resumeColRight.className = "col-sm-8";
            resumeColRight.id = "resumeColMsgFail";
            resumeRow.appendChild(resumeColRight);

            var image = document.createElement("img");
            image.src = "/assets/common/img/warning.svg";
            image.style = "height:100px";
            resumeColLeft.appendChild(image);

            var h5 = document.createElement("h5");
            h5.textContent = "Opps!! Something went wrong";
            resumeColRight.appendChild(h5);

            var h4 = document.createElement("h4");
            h4.textContent = "Please try after sometime";
            h4.style = "font-weight:600";
            resumeColRight.appendChild(h4);

            $("#uploadResumeModalContent").show();
            $('#uploadResumeModal').modal('show');
            setTimeout(function(){
                $('#uploadResumeModal').modal('hide');
            }, 3000);
        }
    }
    else{
        notifyWarning("Invalid Format");
    }
}

function notifyWarning(msg){
    notify(msg, "info");
}

function notifyError(msg){
    notify(msg, "error");
}

function notifySuccess(msg){
    notify(msg, "success");
}

function notify(msg, style) {
    $.notify(msg, style);
}