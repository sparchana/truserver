/**
 * Created by hawk on 12/1/17.
 */
function uploadResume(){
    var ext = $('#uploadResumeContent').val().split('.').pop().toLowerCase();
    if(ext !="pdf"){
        notifyError("Not valid Input")
    }
    else{
        console.log("Call Api");
        uploadResponse(ext);
    }

}
function uploadResponse(returnedData) {
        if(returnedData == "pdf"){
            notifySuccess("Resume Uploaded Successfully !!")
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