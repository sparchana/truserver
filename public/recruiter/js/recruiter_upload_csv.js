/**
 * Created by hawk on 16/2/17.
 */

function openCsvModal(){
    $("#csvUploadModal").openModal();
    $("#uploadContainer").html("");
}
function UploadCSVFile(event){
    var file = event.target.files[0];
    var ext;
    var data = new FormData();
    ext = (file.name).split('.').pop().toLowerCase();
    data.append('file', file);
    var fileSize = (file.size/1024)/1024;
    if(ext == "csv"){
        if(fileSize < 10){
            try {
                $.ajax({
                 type: "POST",
                 url:"/recruiter/uploadEmployee",
                 data: data,
                 contentType: false,
                 processData:false,
                 success: processDataForUploadCSVFile
                });
            } catch(exception){
             console.log("Exception occured!!"+ exception);
            }
        } else{
            notifyError("File size should not be greater then 10MB");
        }
    } else{
        notifyError("Please upload a CSV file");
    }
}

function processDataForUploadCSVFile(returnedData) {
    if( returnedData != -1){
        notifySuccess("Successfully Uploaded CSV!");
        var parent = $("#uploadContainer");
        parent.html(" ");

        var successStatus = document.createElement("h6");
        successStatus.textContent = "No. of Employee created "+ returnedData.successCount+" out of "+ returnedData.totalCount;
        parent.append(successStatus);

        $("#uploadCSVBtn").prop("disabled",true);
    }
    else{
        notifyError("Please try after sometime !!");
    }
}
function closeCSVModal() {
    $("#csvUploadModal").closeModal();
}