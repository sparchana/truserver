/**
 * Created by hawk on 12/1/17.
 */

/* render options to view and download candidate resume  */
function viewDownloadResume(resumeLink) {
    if(resumeLink != null){
        $("#resumeUploadBoxInner").hide();
        var parentView = $("#userViewResume");
        parentView.html("");
        var viewLink = document.createElement("a");
        viewLink.href = "http://docs.google.com/gview?url=" + resumeLink + "&embedded=true";
        viewLink.target = "_blank";
        viewLink.id = "viewResume";
        viewLink.textContent = "View |";
        parentView.append(viewLink);

        var parentDownload = $("#userViewDownload");
        parentDownload.html("");
        var downloadLink = document.createElement("a");
        downloadLink.href = resumeLink;
        downloadLink.id = "downloadResume";
        downloadLink.textContent = "Download";
        parentDownload.append(downloadLink);
    }
}

/* upload resume by candidate */
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

/* process data after resume is uploaded by candidate */
function processDataForAddResume(returnedData) {
    $('#uploadResumeModalProcess').modal('hide');
    if(returnedData != null){
        if(returnedData.status == 1)
        {
            var parent = $("#uploadResumeModalContent");
            parent.html("");

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

            if(returnedData.candidateId != null ){
                var h3 = document.createElement("h3");
                h3.textContent = "Thanks !! Your resume has been successfully uploaded";
                h3.style = "margin-top:10px";
                resumeColRight.appendChild(h3);
            }
            else{
                var h5 = document.createElement("h5");
                h5.textContent = "Thanks !! Your resume has been successfully uploaded";
                resumeColRight.appendChild(h5);

                var h4 = document.createElement("h4");
                h4.textContent = "You will shortly receive a SMS with your login details";
                h4.style = "font-weight:600";
                resumeColRight.appendChild(h4);
            }

            $('#uploadResumeModal').modal('show');
            setTimeout(function(){
                $('#uploadResumeModal').delay(9000).modal('hide');
            }, 3000);

            if (returnedData.candidateResumeLink != null) {
                viewDownloadResume(returnedData.candidateResumeLink);
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