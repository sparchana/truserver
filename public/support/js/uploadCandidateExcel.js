
$("#excelfile").on('change',function () {
    $("#uploadBtn").prop("disabled",false);
    $(".textResponse h4").html("Click On Upload");
});

$(function() {
    $("#upload_excel").submit(function(eventObj) {
        eventObj.preventDefault();
        if($('#excelfile')[0].files[0] != null) {
            try {
                var file = $('#excelfile')[0].files[0];
                var formData = new FormData();
                formData.append('file', file);
                console.log("File Name = " + file.name);

                $.ajax({
                    type: "POST",
                    url: "/support/administrator/processCandidates",
                    data: formData,
                    contentType: false,
                    processData: false,
                    success: processData
                });
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
    }); // end of submit
}); // end of function

// function to post the reg data to the server
function processData(returnedData) {
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
    }else{
        var data = {message:'Error Uploading!! check csv'};
        notification(data);
    }
}
function notification(data) {
    var snackbarContainer = document.querySelector('#demo-toast-example');
    snackbarContainer.MaterialSnackbar.showSnackbar(data);
};
