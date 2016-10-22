/**
 * Created by hawk on 21/10/16.
 */
$(document).scroll(function(){
    if ($(this).scrollTop() > 80) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});
$(document).ready(function(){

    $('.material-tooltip').tooltip({delay: 50});

    var parent = $('.myJobsRecruiter');

    var mainDiv =  document.createElement("div");
    parent.append(mainDiv);

    var outerRow = document.createElement("div");
    outerRow.className = 'row';
    outerRow.id="outerBoxMain";
    mainDiv.appendChild(outerRow);

    var colJobPost = document.createElement("div");
    colJobPost.className = 'col s6 l2';
    colJobPost.style = 'margin-top:8px';
    colJobPost.textContent = "Posted Job";
    outerRow.appendChild(colJobPost);

    var colJobLocation = document.createElement("div");
    colJobLocation.className = 'col s6 l2';
    colJobLocation.style = 'margin-top:8px';
    colJobLocation.textContent = "Location";
    outerRow.appendChild(colJobLocation);

    var colJobWorkShift = document.createElement("div");
    colJobWorkShift.className = 'col s6 l2';
    colJobWorkShift.style = 'margin-top:8px';
    colJobWorkShift.textContent = "Work Shift";
    outerRow.appendChild(colJobWorkShift);

    var colJobSalary = document.createElement("div");
    colJobSalary.className = 'col s6 l1';
    colJobSalary.style = 'margin-top:8px';
    colJobSalary.textContent = "Salary";
    outerRow.appendChild(colJobSalary);

    var colDatePost = document.createElement("div");
    colDatePost.className = 'col s6 l2';
    colDatePost.style = 'margin-top:8px';
    colDatePost.textContent = "16-Dec-2016";
    outerRow.appendChild(colDatePost);

    var colApplicant = document.createElement("div");
    colApplicant.className = 'col s6 l2';
    outerRow.appendChild(colApplicant);

    var sampleValue = 5;

    var applicantBtn = document.createElement('a');
    applicantBtn.style = "font-weight:600;text-decoration:none";
    if( sampleValue >= 10){
        applicantBtn.className = 'btn-floating btn-small waves-effect waves-light green accent-3';
    }
    else{
        applicantBtn.className = 'btn-floating btn-small waves-effect waves-light red accent-3';
    }
    applicantBtn.textContent= '9';
    colApplicant.appendChild(applicantBtn);

    var colEditView = document.createElement("div");
    colEditView.className = 'col s6 l1';
    outerRow.appendChild(colEditView);

    var editViewBtn = document.createElement('button');
    editViewBtn.className = 'waves-effect waves-blue-grey lighten-5 btn-flat';
    editViewBtn.style = 'color:#1976d2';
    editViewBtn.textContent='Edit View';
    colEditView.appendChild(editViewBtn);

    var hr = document.createElement('hr');
    hr.style='margin:2px 1%';
    mainDiv.appendChild(hr);

    //You can remove this code

    var outerRow = document.createElement("div");
    outerRow.className = 'row';
    outerRow.id="outerBoxMain";
    mainDiv.appendChild(outerRow);

    var colJobPost = document.createElement("div");
    colJobPost.className = 'col s6 l2';
    colJobPost.style = 'margin-top:8px';
    colJobPost.textContent = "Posted Job";
    outerRow.appendChild(colJobPost);

    var colJobLocation = document.createElement("div");
    colJobLocation.className = 'col s6 l2';
    colJobLocation.style = 'margin-top:8px';
    colJobLocation.textContent = "Location";
    outerRow.appendChild(colJobLocation);

    var colJobWorkShift = document.createElement("div");
    colJobWorkShift.className = 'col s6 l2';
    colJobWorkShift.style = 'margin-top:8px';
    colJobWorkShift.textContent = "Work Shift";
    outerRow.appendChild(colJobWorkShift);

    var colJobSalary = document.createElement("div");
    colJobSalary.className = 'col s6 l1';
    colJobSalary.style = 'margin-top:8px';
    colJobSalary.textContent = "Salary";
    outerRow.appendChild(colJobSalary);

    var colDatePost = document.createElement("div");
    colDatePost.className = 'col s6 l2';
    colDatePost.style = 'margin-top:8px';
    colDatePost.textContent = "16-Dec-2016";
    outerRow.appendChild(colDatePost);

    var colApplicant = document.createElement("div");
    colApplicant.className = 'col s6 l2';
    outerRow.appendChild(colApplicant);

    var sampleValue = 10;

    var applicantBtn = document.createElement('a');
    applicantBtn.style = "font-weight:600;text-decoration:none";
    if( sampleValue >= 10){
        applicantBtn.className = 'btn-floating btn-small waves-effect waves-light green accent-3';
    }
    else{
        applicantBtn.className = 'btn-floating btn-small waves-effect waves-light red accent-3';
    }
    applicantBtn.textContent= '20';
    colApplicant.appendChild(applicantBtn);

    var colEditView = document.createElement("div");
    colEditView.className = 'col s6 l1';
    outerRow.appendChild(colEditView);

    var editViewBtn = document.createElement('button');
    editViewBtn.className = 'waves-effect waves-blue-grey lighten-5 btn-flat';
    editViewBtn.style = 'color:#1976d2';
    editViewBtn.textContent='Edit View';
    colEditView.appendChild(editViewBtn);



});