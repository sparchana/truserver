
$(function() {
    $("#upload_excel").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var file = $('#excelfile')[0].files[0];
            var formData = new FormData();
            formData.append('file', file);
            console.log("File Name = "+file.name);

            $.ajax({
                type: "POST",
                url: "/support/administrator/processCandidates",
                data: formData,
                contentType: false,
                processData: false,
                success: processData
            });
            $("#loadSpinner").show();
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }); // end of submit
}); // end of function

// function to post the reg data to the server
function processData(returnedData) {
    $("#loadSpinner").hide();
    console.log(returnedData);
    if(returnedData != '-1') {
        alert("Bulk upload done. "+returnedData+" candidates created");
    } else {
        alert('Error Uploading!! check csv');
    }

}
