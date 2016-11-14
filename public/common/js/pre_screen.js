/**
 * Created by hawk on 14/11/16.
 */
var parent = $('#missingInfo');
var mainDiv = document.createElement("div");
mainDiv.className = "row";
parent.append(mainDiv);
var subDivOne = document.createElement("div");
subDivOne.className = "col-sm-12";
mainDiv.appendChild(subDivOne);
var hintMessage = document.createElement("p");
hintMessage.textContent = "Please provide your following details to apply for this job";
subDivOne.appendChild(hintMessage);
var subDivTwo = document.createElement("div");
subDivTwo.className = "col-sm-12";
mainDiv.appendChild(subDivTwo);
var question = document.createElement("p");
question.textContent = "1)" +" Please provide your "+"Adharcard" + " details" ;
subDivTwo.appendChild(question);