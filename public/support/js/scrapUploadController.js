// function to post the reg data to the server
function processData(returnedData) {
    //DoTheDue Here
    console.log(returnedData);
    if(returnedData != '-1') {
        alert("File Successfully rendered " + returnedData + "- new entry");
    } else {
        alert('Error Uploading File!! check csv');
    }

}
$(function() {
    $("#upload_csv").submit(function(eventObj) {
        eventObj.preventDefault();
        try {
            var file = $('#csvfile')[0].files[0];
            var formData = new FormData();
            formData.append('file', file);
            console.log(file.name);

            $.ajax({
                type: "POST",
                url: "/support/administrator/scrapCSV/processBabaJCSV",
                data: formData,
                contentType: false,
                processData: false,
                success: processData
            });
        } catch (exception) {
            console.log("exception occured!!" + exception);
        }
    }); // end of submit
}); // end of function
