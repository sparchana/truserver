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
    try {
        $.ajax({
            type: "POST",
            url: "/support/administrator/processCandidates",
            data: formData,
            contentType: false,
            processData: false,
            success: processDataForCSV
        });
        $('#uploadResumeModalProcess').modal('show');
        $('#candidateCreateOptionModal').modal('hide');
        $("#loadSpinner").show();
        $(".textResponse").hide();
        $("#uploadBtn").prop("disabled",true);
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
    }else{
        var data = {message:'Please select file to upload'};
        notification(data);
    }
}

// function to post the reg data to the server
function processDataForCSV(returnedData) {
    //for partner
    $('#uploadResumeModalProcess').modal('hide');
    $("#uploadResumeModalContent").hide();

    //for support
    $("#loadSpinner").hide();
    $(".textResponse").html("");

    if(returnedData.totalNumberOfCandidateCreated == 0) {

        var parent = $(".textResponse");

        var result = document.createElement("h1");
        result.textContent = returnedData.totalNumberOfCandidateCreated+" / "+returnedData.totalNumberOfCandidateUploaded;
        parent.append(result);

        var hint = document.createElement("font");
        hint.textContent = "Created / Uploaded";
        hint.style = "color:#bdbdbd";
        parent.append(hint);

        var message = document.createElement("h4");
        message.textContent = "No new candidate found !!";
        parent.append(message);

        $(".textResponse").show();
        $('#uploadResumeModal').modal('show');
        setTimeout(function(){
            $('#uploadResumeModal').modal('hide');
        }, 3000);
    }
    else if(returnedData.totalNumberOfCandidateCreated > 0) {
        var parent = $(".textResponse");

        var result = document.createElement("h1");
        result.textContent = returnedData.totalNumberOfCandidateCreated+" / "+returnedData.totalNumberOfCandidateUploaded;
        parent.append(result);

        var hint = document.createElement("font");
        hint.textContent = "Created / Uploaded";
        hint.style = "color:#bdbdbd";
        parent.append(hint);

        var message = document.createElement("h4");
        message.textContent = returnedData.totalNumberOfCandidateCreated+" new candidate created !!";
        parent.append(message);

        $(".textResponse").show();
        $('#uploadResumeModal').modal('show');
        setTimeout(function(){
            $('#uploadResumeModal').modal('hide');
        }, 3000);
    }else{
        var data = {message:'Error Uploading!! check csv'};
        notification(data);
    }
}
function notification(data) {
    var snackbarContainer = document.querySelector('#demo-toast-example');
    snackbarContainer.MaterialSnackbar.showSnackbar(data);
};
