/**
 * Created by hawk on 16/1/17.
 */

function uploadResume(evt){
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
                success: processDataForAddResume
            });
            $('#uploadResumeModalProcess').modal('show');
            $('#candidateCreateOptionModal').modal('hide');
        } catch(exception) {
            console.log("Exception Occurred!!" + exception);
            console.log(new Error().stack);
        }
    }else{
        notifyError("Invalid Format. Please Resume in PDF Doc Docx file Format");
    }
}

function processDataForAddResume(returnedData) {
    $('#uploadResumeModalProcess').modal('hide');
    console.log(JSON.stringify(returnedData));
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
            h4.textContent = "You will short receive a SMS with more details";
            h4.style = "font-weight:600";
            resumeColRight.appendChild(h4);

            $('#uploadResumeModal').modal('show');
            //condition if upload is done without login
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

            $('#uploadResumeModal').modal('show');
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