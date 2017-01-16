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
            $('#uploadResumeModal').modal('show');
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
            h4.textContent = "Thanks !! Resume has been successfully Uploaded";
            parent.append(h4);

            var h4 = document.createElement("h4");
            h4.textContent = "Please check back in My Candidate Tab to see deatils of added candidate";
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