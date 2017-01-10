// function to post the reg data to the server
function processData(returnedData) {
   console.log(returnedData);
   if(returnedData != '-1') {
       alert(returnedData);
   } else {
       alert('Error Uploading!! check csv');
   }

}

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
                url: "/support/administrator/processCandidateExcel",
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
