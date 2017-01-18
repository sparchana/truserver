/**
 * Created by hawk on 12/1/17.
 */

document.getElementById('uploadResumeContent').addEventListener('change', uploadResume, false);

function viewDownloadResume(candidateId) {
    var url = "/fetchResume/?candidateId="+candidateId;
    try {
        $.ajax({
            type: "GET",
            url: url,
            data: false,
            async: true,
            contentType: false,
            processData: false,
            success: processDataForViewResume
        });
    } catch(exception) {
        console.log("Exception Occurred!!" + exception);
        console.log(new Error().stack);
    }

}
function processDataForViewResume(returnedData){
    console.log(JSON.stringify(returnedData));
    if (returnedData.filePath != null) {
            $(".resumeUploadBox").hide();
            $("#userViewResume").html("");
            var parentView = $("#userViewResume");
            var viewLink = document.createElement("a");
            viewLink.href = "http://docs.google.com/gview?url=" + returnedData.filePath + "&embedded=true";
            viewLink.target = "_blank";
            viewLink.id = "viewResume";
            viewLink.textContent = "View |";
            parentView.append(viewLink);

            var parentDownload = $("#userViewDownload");
            var downloadLink = document.createElement("a");
            downloadLink.href = returnedData.filePath;
            downloadLink.id = "downloadResume";
            downloadLink.textContent = "Download";
            parentDownload.append(downloadLink);
    }
    else{
        console.log("No resume Uploaded");
    }
}

function uploadResume(evt){
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
                success: processDataForAddResume
            });
            $('#uploadResumeModalProcess').modal('show');
            $('#mySignUpModal').modal('hide');
        } catch(exception) {
            console.log("Exception Occurred!!" + exception);
            console.log(new Error().stack);
        }
    }else{
        notifyError("Invalid Format. Please select a PDF, Doc or Docx format file");
    }
}
function processDataForAddResume(returnedData) {
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
            image.src = "/assets/common/img/resumeUpload.svg";
            image.style = "height:100px";
            resumeColLeft.appendChild(image);
            
            var h5 = document.createElement("h5");
            h5.textContent = "Thanks !! Your resume has been successfully uploaded";
            resumeColRight.appendChild(h5);

            var h4 = document.createElement("h4");
            h4.textContent = "You will shortly receive a SMS with your login details";
            h4.style = "font-weight:600";
            resumeColRight.appendChild(h4);

            $('#uploadResumeModal').modal('show');
            setTimeout(function(){
                $('#uploadResumeModal').delay(9000).modal('hide');
            }, 3000);
            //condition if upload is done without login
            if(candidateId != null){
                viewDownloadResume(candidateId);
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

            $('#uploadResumeModal').modal('show');
            setTimeout(function(){
                $('#uploadResumeModal').delay(9000).modal('hide');
            }, 3000);

        }
    }
    else{
        notifyWarning("Something went wrong try after sometime");
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