//trigger if CSV file selected
function uploadCSV(event) {
    var file = event.target.files;
    var formData = new FormData();
    var ext;
    for (var i = 0, f; f = file[i]; i++) {
        ext = (f.name).split('.').pop().toLowerCase();
        formData.append('file', f);
    }
    if( ext == 'csv' ){
        $("#uploadCsvModalProcess").openModal();
        try {
            $.ajax({
                type: "POST",
                url: "/recruiter/processJobCSV",
                data: formData,
                contentType: false,
                processData: false,
                success: processDataForCSV,
                error: function (jqXHR, exception) {
                    $("#errorModal").openModal();
                    $("#uploadCsvModalProcess").closeModal();
                }
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
        $("#uploadedFile").val('');
    } else{
        notifyError("Please select file to upload");
    }
}

// function to post the reg data to the server
function processDataForCSV(returnedData) {

    $("#uploadCsvModalProcess").closeModal();
    //for support
    $("#loadSpinner").hide();
    $(".textResponse").html("");

    if(returnedData.totalJobsCreated == 0) {
        notifyError("0 jobs uploaded");
    } else if(returnedData.totalJobsCreated > 0) {

        $("#uploadCount").html(returnedData.totalJobsUploaded);
        $("#successCount").html(returnedData.totalJobsCreated);
        $("#failedCount").html(returnedData.totalJobsUploaded - returnedData.totalJobsCreated);

        notifySuccess(returnedData.totalJobsCreated + " new job(s) successfully created!");

        if(returnedData.totalJobsCreated < returnedData.totalJobsUploaded){
            $("#invalidDiv").show();
            $("#errorReport").html('');
            notifyError(returnedData.totalJobsUploaded - returnedData.totalJobsCreated + " jobs failed!");
            var invalidField = returnedData.invalidFields;
            var invalidFieldList = invalidField.split(",");
            invalidFieldList.forEach(function (invalid) {
                if(invalid != ""){
                    $("#errorReport").append("- " + invalid);
                    $("#errorReport").append("<br>");
                }
            });
        }
        $("#csvErrorModal").openModal();

    } else{
        notifyError("Something went wrong. Please try after sometime")
    }
}

function closeErrorReportModal() {
    $("#csvErrorModal").closeModal();
    var table = $("table#postedJobTable").DataTable();
    table.destroy();
    getRecruiterJobPost();
//    location.reload();
}
