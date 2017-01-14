/**
 * Created by hawk on 12/1/17.
 */

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
    if (returnedData.length > 0) {
            $(".resumeUploadBox").hide();
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
        console.log("Entered");
            $("#userViewResume").innerHTML = "No Resume Uploaded";
            document.getElementById('uploadResumeContent').addEventListener('change', uploadResume, false);
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
            $('#uploadResumeModal').modal('show');
        } catch(exception) {
            console.log("Exception Occurred!!" + exception);
            console.log(new Error().stack);
        }
    }else{
        notifyError("Invalid Format. Please Resume in PDF Doc Docx file Format");
    }
}
function processDataForAddResume(returnedData) {
    console.log(JSON.stringify(returnedData));
    if(returnedData != null){
        if(returnedData.status == 1)
        {
            var parent = $("#uploadResumeModalContent");

            $("#uploadResumeModalContent").html("");

            var h1 = document.createElement("h2");
            h1.textContent = "SuccessFully Uploaded";
            parent.append(h1);

            var h4 = document.createElement("h4");
            h4.textContent = "Thanks !! Your Resume has been successfully Uploaded";
            parent.append(h4);

            var h4 = document.createElement("h4");
            h4.textContent = "You will shortly receive details for login";
            parent.append(h4);
            console.log("Uploaded Successfully");
        }
        else{
            console.log("Upload Fail");
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