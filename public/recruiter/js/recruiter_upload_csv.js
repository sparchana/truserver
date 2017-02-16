/**
 * Created by hawk on 16/2/17.
 */

function openCsvModal(){
    $("#csvUploadModal").openModal();
    $("#uploadContainer").html("");
}
function UploadCSVFile(event){
    var file = event.target.files;
    var ext;
    var data = new FormData();
    ext = (file[0].name).split('.').pop().toLowerCase();
    data.append('file', file);
    var fileSize = (file[0].size/1024)/1024;
    if(ext == "csv"){
        if(fileSize < 10){
            var d = {
                numberOfSent:10,
                numberOfSuccess:5,
            }
            processDataForUploadCSVFile(d);
            /*try {
                $.ajax({
                 type: "POST",
                 url:"",
                 data: data,
                 contentType: false,
                 processData:false,
                 success: processDataForUploadCSVFile
                });
            } catch(exception){
             console.log("Exception occured!!"+ exception);
            }*/
        } else{
            notifyError("File size should not be greater then 10MB");
        }
    } else{
        notifyError("Please upload a CSV file");
    }
}

function processDataForUploadCSVFile(returnedData) {
    if( returnedData.numberOfSuccess != 0){
        notifySuccess("Successfully Uploaded CSV!");
        var parent = $("#uploadContainer");
        parent.html(" ");

        var successStatus = document.createElement("h6");
        successStatus.textContent = "No. of Employee created "+returnedData.numberOfSuccess+" out of "+returnedData.numberOfSent;
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