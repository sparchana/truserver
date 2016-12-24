var localityArray = [];
var jobArray = [];
var companyTypeArray = [];
var localityIdToLocalityKeyMap = {};

const message_info = 'I';
const message_warning = 'W';
const message_error = 'E';

function getLocality(){
    return localityArray;
}

function getJob(){
    return jobArray;
}

function getCompanyType(){
	return companyTypeArray;
}

$(document).ready(function () {
    var pathname = window.location.pathname; // Returns path only
    var leadId = pathname.split('/');
    leadId = leadId[(leadId.length)-1];
        if(parseInt(leadId) > 0){
	        try {
	            $.ajax({
	                type: "POST",
	                url: "/showRecruiterLead/" + leadId,
	                data: false,
	                async: false,
	                contentType: false,
	                processData: false,
	                success: processDataForReadRecruiterLead
	            });
	        } catch(exception) {
	            console.log("Exception Occurred!!" + exception);
	            console.log(new Error().stack);
	        }
        }
        else{processDataForReadRecruiterLead(null);}

    /* pre-fetch recruiter lead status*/
    try {
        $.ajax({
            type: "GET",
            url: "/showRecruiterLeadStatus",
            data: false,
            contentType: false,
            processData: false,
            success: processDataForShowRecruiterLeadStatus
        });
    } catch (exception) {
        console.log("exception occurred!!" + exception);
    }

});

function processDataForShowRecruiterLeadStatus(returnedData) {
    if (returnedData != null) {
        returnedData.forEach(function (response) {
            var id = response.entity.recruiterLeadStatusId;
            var name = response.entity.recruiterLeadStatusName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            var option = $('<option value=' + id + '></option>').text(name);
            $('#recruiterLeadStatus').append(option);
        });
    }
}

function processDataForGetAllCompanyType(returnedData) {
    if (returnedData != null) {
        //console.log(returnedData);
        returnedData.forEach(function (response) {
            var id = response.companyTypeId;
            var name = response.companyTypeName;
            var item = {};
            item ["id"] = id;
            item ["name"] = name;
            companyTypeArray.push(item);
            var option = $('<option value=' + id + '></option>').text(name);
            $('#companyLeadType').append(option);
        });
    }
}

function processDataForGetAllLocality(returnedData) {
    returnedData.forEach(function(locality) {
        var id = locality.localityId;
        var name = locality.localityName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        localityArray.push(item);
    });
}

function processDataForGetAllJobs(returnedData) {
    returnedData.forEach(function(job) {
        var id = job.jobRoleId;
        var name = job.jobName;
        var item = {};
        item ["id"] = id;
        item ["name"] = name;
        jobArray.push(item);
    });
}

function processDataForReadRecruiterLead(returnData) {

	/* pre fetch localities */
    try {
        $.ajax({
            type: "POST",
            url: "/getAllLocality",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataForGetAllLocality
        });
    } catch (exception) {
        console.log("exception occured!!" + exception);
    }
	//console.log("getAllLocality executed");

	/* pre fetch job roles*/
    try {
        $.ajax({
            type: "POST",
            url: "/getAllJobs",
            data: false,
            async: false,
            contentType: false,
            processData: false,
            success: processDataForGetAllJobs
        });
    } catch (exception) {
        console.log("exception occurred!!" + exception);
    }
	//console.log("getAllJobs executed");

    /* pre-fetch company type*/
    try {
        $.ajax({
            type: "POST",
            url: "/getAllCompanyType",
			async: false,
            data: false,
            contentType: false,
            processData: false,
            success: processDataForGetAllCompanyType
        });
    } catch (exception) {
        console.log("exception occurred!!" + exception);
    }
	//console.log("getAllCompanyType executed");

	// map for locality id:name and recruiter_lead_locality_to_recruiter_lead_jobrole:locality_to_recruiter_lead_locality
	var thisJobLocality = {};

	if((returnData) && (returnData[0].entity)){

		// Recruiter Details
		$('#recruiterLeadId').val(returnData[0].entity.recruiterLeadId);
		$('#recruiterLeadUUId').val(returnData[0].entity.recruiterLeadUUId);
		$('#recruiterLeadStatus').val(returnData[0].entity.recruiterLeadStatus);
		$('#recruiterLeadName').val(returnData[0].entity.recruiterLeadName);
		$('#recruiterLeadMobile').val(returnData[0].entity.recruiterLeadMobile);
		$('#recruiterLeadAltNumber').val(returnData[0].entity.recruiterLeadAltNumber);
		$('#recruiterLeadEmail').val(returnData[0].entity.recruiterLeadEmail);
		$('#recruiterLeadChannel').val(returnData[0].entity.recruiterLeadChannel);
		$('#recruiterLeadRequirement').val(returnData[0].entity.recruiterLeadRequirement);
		$('#recruiterLeadSourceType').val(returnData[0].entity.recruiterLeadSourceType);
		$('#recruiterLeadSourceName').val(returnData[0].entity.recruiterLeadSourceName);
		$('#recruiterLeadSourceDate').val(returnData[0].entity.recruiterLeadSourceDate);

		// Company Details
		$('#companyLeadId').val(returnData[0].entity.companyLead.companyLeadId);
		$('#companyLeadName').val(returnData[0].entity.companyLead.companyLeadName);
		$('#companyLeadWebsite').val(returnData[0].entity.companyLead.companyLeadWebsite);

		// get company types
		var allCompanyTypes = getCompanyType();
		// get the value for the company type id
		allCompanyTypes.every(function(each){
			//console.log("each.id="+each.id+" compared against "+returnData[0].entity.companyLead.companyLeadType.companyTypeId);
			if(each.id == returnData[0].entity.companyLead.companyLeadType.companyTypeId){
				$('#companyLeadType').val(each.id);
				return false;
			}
			return true;
		});

		$('#companyLeadIndustry').val(returnData[0].entity.companyLead.companyLeadIndustry);

		// fill job role table
		if((returnData[0].entity.recruiterLeadToJobRoleList != null) && (returnData[0].entity.recruiterLeadToJobRoleList.length > 0)) {

			//console.log("returnData[0].entity.recruiterLeadToJobRoleList.length="+returnData[0].entity.recruiterLeadToJobRoleList.length);

			// for each job role row
			returnData[0].entity.recruiterLeadToJobRoleList.forEach(function(jobRole) {

				//console.log("jobRole: "+JSON.stringify(jobRole));

				// get job roles
				var allJobRoles = getJob();
				//console.log("allJobRoles: "+allJobRoles);
				// get job locations
				var allLocations = getLocality();
				//console.log("allLocations: "+allLocations);

				var appendString = '<tr name="jobRoleRow">';
				appendString += '<td style="display:none;"> <input type="number" name="recruiterLeadToJobRoleId" style="display:none;" value="'+((typeof jobRole.recruiterLeadToJobRoleId !== "undefined")? ((jobRole.recruiterLeadToJobRoleId)?jobRole.recruiterLeadToJobRoleId:0):'')+'"></td>';

				// build job role dropdown list
				var jobRoleDropDown = '<select name="jobRoleidSelect" style="display:block;">';
				allJobRoles.forEach(function(each){
					//console.log("Entered allJobRoles.forEach: "+JSON.stringify(each));
					if((jobRole.jobRole) && (jobRole.jobRole.jobRoleId == each.id)){
						jobRoleDropDown += '<option id="'+each.id+'" value="'+each.name+'" selected="selected">'+each.name+'</option>';
					}
					else {jobRoleDropDown += '<option id="'+each.id+'" value="'+each.name+'">'+each.name+'</option>';}
				});
				jobRoleDropDown += '</select>';
				//$('#jobRoleTable tbody').append('<tr class="jobRoleRow"><td>'+jobRoleDropDown+'</td>');
				appendString += '<td name="jobRoleid"   >'+jobRoleDropDown+'</td>';

				// get all localities for this job role
				var c = 0;
				jobRole.recruiterLeadToLocalityList.forEach(function (locality) {
					if(locality.locality != null){
						thisJobLocality[jobRole.recruiterLeadToJobRoleId+"_"+(c++)] = locality.recruiterLeadToLocalityId;
						thisJobLocality[locality.locality.localityId] = locality.locality.localityName;
					}
				});
				//console.log("thisJobLocality:"+JSON.stringify(thisJobLocality));

				// build localities dropdown list
				var jobLocDropDown = '<select name="jobLocationidSelect" style="display:block;" multiple>';
				allLocations.forEach(function(eachLoc){
					if(thisJobLocality.hasOwnProperty(eachLoc.id)){
						jobLocDropDown += '<option id="'+eachLoc.id+'" value="'+eachLoc.name+'" selected="selected">'+eachLoc.name+'</option>';
					}
					else {jobLocDropDown += '<option id="'+eachLoc.id+'" value="'+eachLoc.name+'">'+eachLoc.name+'</option>';}
				});
				jobLocDropDown += '</select>';
				appendString += '<td name="localityId">'+jobLocDropDown+'</td>';
				//$('#jobRoleTable tbody').append('<tr class="jobRoleRow"><td>'+jobRoleDropDown+'</td></tr>');

				appendString += '<td> <input required type="number" name="jobVacancies" value="'+((typeof jobRole.jobVacancies !== "undefined")? jobRole.jobVacancies:'')+'"></td>';
				appendString += '<td> <input type="number" name="jobSalaryMin" value="'+((typeof jobRole.jobSalaryMin !== "undefined")? jobRole.jobSalaryMin:'')+'"></td>';
				appendString += '<td> <input type="number" name="jobSalaryMax" value="'+((typeof jobRole.jobSalaryMax !== "undefined")? jobRole.jobSalaryMax:'')+'"></td>';
				appendString += '<td> <input type="text" name="jobGender" value="'+((typeof jobRole.jobGender !== "undefined")? ((jobRole.jobGender)?jobRole.jobGender:"MF"):'')+'"></td>';
				appendString += '<td> <input type="text" name="jobInterviewAddress" value="'+((typeof jobRole.jobInterviewAddress != "undefined")? jobRole.jobInterviewAddress:'')+'"></td>';
				appendString += '<td> <input type="text" name="jobDetailRequirement" value="'+((typeof jobRole.jobDetailRequirement != "undefined")? jobRole.jobDetailRequirement:'')+'"></td>';
				appendString += '</tr>'
				$('#jobRoleTable tbody').append(appendString);
				//console.log(appendString);

			});
		}
		// fill empty job role row
		else{appendEmptyRow();}

	}
	else{appendEmptyRow();}

	var $form = $('form');
	var begin = convertSerializedArrayToHash($form.serializeArray());
	//for(k in begin) {console.log("begin["+k+"]="+begin[k]);}

	// At Submit
	$('form').submit(function(event) {
		event.preventDefault();

		// identify what has changed
		var now = convertSerializedArrayToHash($form.serializeArray());
		console.log("begin: "+JSON.stringify(begin));
		console.log("now: "+JSON.stringify(now));
		var itemsToSubmit = hashDiff(begin, now);
		console.log("itemsToSubmit: "+JSON.stringify(itemsToSubmit));

		// Check for changes
        if(itemsToSubmit.length > 0) {

        var itemsToDelete = [];

		var recruiterLeadObj = {};
		var companyLeadObj = {};

		// Build Recruiter Lead Object
		$("#recruiterLeadDetailsForm").find("[id^='recruiter']").each(function() {
        	recruiterLeadObj[$(this).attr("id")] = $(this).val();
		});

		// Build Company Lead Object
		$("#recruiterLeadDetailsForm").find("[id^='company']").each(function() {
        	companyLeadObj[$(this).attr("id")] = $(this).val();
		});
		recruiterLeadObj['companyLeadRequest'] = companyLeadObj;

        var jobObjArray = [];
		// Build related job object
        $('tr[name=jobRoleRow]').each(function(){
        	var jobObject = {};
        	var locObjArray = [];
			jobObject['recruiterLeadId'] = $('#recruiterLeadId').val();
        	jobObject['jobRole'] = parseInt(jobArray.getKeyByValue($(this).find('select[name=jobRoleidSelect] option:selected').val()));
        	jobObject[$(this).find('input[name=recruiterLeadToJobRoleId]').attr("name")] = $(this).find('input[name=recruiterLeadToJobRoleId]').val();
        	jobObject[$(this).find('input[name=jobInterviewAddress]').attr("name")] = $(this).find('input[name=jobInterviewAddress]').val();
        	jobObject[$(this).find('input[name=jobVacancies]').attr("name")] = $(this).find('input[name=jobVacancies]').val();
        	jobObject[$(this).find('input[name=jobSalaryMin]').attr("name")] = $(this).find('input[name=jobSalaryMin]').val();
        	jobObject[$(this).find('input[name=jobSalaryMax]').attr("name")] = $(this).find('input[name=jobSalaryMax]').val();
        	jobObject[$(this).find('input[name=jobGender]').attr("name")] = $(this).find('input[name=jobGender]').val();
        	jobObject[$(this).find('input[name=jobDetailRequirement]').attr("name")] = $(this).find('input[name=jobDetailRequirement]').val();
			// Build related locality object
			var localityArr = $(this).find('select[name=jobLocationidSelect]').val();
			if(localityArr) {
				for(i=0;i<localityArr.length;i++) {
	                var locObject = {};
					locObject['locality'] = localityArray.getKeyByValue(localityArr[i]);
					locObject['recruiterLeadToLocalityId'] = ((thisJobLocality.hasOwnProperty(jobObject['recruiterLeadToJobRoleId']+"_"+i))?thisJobLocality[jobObject['recruiterLeadToJobRoleId']+"_"+i]:0);
					//console.log("locObject="+JSON.stringify(locObject));
					locObjArray.push(locObject);
				}

				// need to check if any locality has been unselected
                var c = 0;
                for(var each in thisJobLocality) {
                    if(thisJobLocality.hasOwnProperty(jobObject['recruiterLeadToJobRoleId']+"_"+c)){
                        var found = false;
                        for(var j=0;j<locObjArray.length;j++){
                            if(locObjArray[j].recruiterLeadToLocalityId == thisJobLocality[jobObject['recruiterLeadToJobRoleId']+"_"+c]){
                                found = true;
                                break;
                            }
                        }
                        if(found == false){
                            //var map = 'recruiterLeadToLocality'+','+parseInt(thisJobLocality[jobObject['recruiterLeadToJobRoleId']+"_"+c]);
                            var map = {};
                            map['recruiterLeadToLocality'] = parseInt(thisJobLocality[jobObject['recruiterLeadToJobRoleId']+"_"+c]);
                            itemsToDelete.push(map);
                        }
                    }
                    c++;
                }

			}
        	jobObject['recruiterLeadToLocalityRequestList'] = locObjArray;
        	jobObjArray.push(jobObject);
        });

		recruiterLeadObj['recruiterLeadToJobRoleRequestList'] = jobObjArray;
		recruiterLeadObj['changedFields'] = itemsToSubmit;
		recruiterLeadObj['deleteFields'] = itemsToDelete;

        } // End of check for changes

		console.log("recruiterLeadObj: "+JSON.stringify(recruiterLeadObj));

		if(parseInt($('#recruiterLeadId').val(),10) > 0) {

			if((recruiterLeadObj.deleteFields) && (recruiterLeadObj.deleteFields.length > 0)){
				console.log("Calling Recruiter Lead "+parseInt($('#recruiterLeadId').val(),10)+" Delete");
		        $.ajax({
		            type: "POST",
		            async: false,
		            url: "/removeRecruiterLead",
		            contentType: "application/json; charset=utf-8",
		            data: JSON.stringify(recruiterLeadObj),
		            success: processDataForModifyRecruiterLead
		        });
			}

			console.log("Calling Recruiter Lead "+parseInt($('#recruiterLeadId').val(),10)+" Update");
	        $.ajax({
	            type: "POST",
	            url: "/modifyRecruiterLead",
	            contentType: "application/json; charset=utf-8",
	            data: JSON.stringify(recruiterLeadObj),
	            success: processDataForModifyRecruiterLead
	        });

		}
		else {
			console.log("Calling Recruiter Lead Create");
		    $.ajax({
		        type: "POST",
		        url: "/addRecruiterLead",
		        contentType: "application/json; charset=utf-8",
		        data: JSON.stringify(recruiterLeadObj),
		        success: processDataForAddRecruiterLead
		    });
		}

	});

}

function appendEmptyRow() {

	// get job roles
	var allJobRoles = getJob();
	//console.log("allJobRoles: "+allJobRoles);
	// get job locations
	var allLocations = getLocality();
	//console.log("allLocations: "+allLocations);

	var appendString = '<tr name="jobRoleRow">';
	appendString += '<td style="display:none;"> <input type="number" style="display:none;" name="recruiterLeadToJobRoleId" value=""></td>';

	// build job role dropdown list
	var jobRoleDropDown = '<select name="jobRoleidSelect" style="display:block;">';
	allJobRoles.forEach(function(each){
		//console.log("Entered allJobRoles.forEach: "+JSON.stringify(each));
		jobRoleDropDown += '<option id="'+each.id+'" value="'+each.name+'">'+each.name+'</option>';
	});
	jobRoleDropDown += '</select>';
	//$('#jobRoleTable tbody').append('<tr class="jobRoleRow"><td>'+jobRoleDropDown+'</td>');
	appendString += '<td name="jobRoleid"   >'+jobRoleDropDown+'</td>';

	// build localities dropdown list
	var jobLocDropDown = '<select name="jobLocationidSelect" style="display:block;" multiple>';
	allLocations.forEach(function(eachLoc){
		jobLocDropDown += '<option id="'+eachLoc.id+'" value="'+eachLoc.name+'">'+eachLoc.name+'</option>';
	});
	jobLocDropDown += '</select>';
	appendString += '<td name="localityId">'+jobLocDropDown+'</td>';
	//$('#jobRoleTable tbody').append('<tr class="jobRoleRow"><td>'+jobRoleDropDown+'</td></tr>');

	appendString += '<td> <input type="number" name="jobVacancies" value=""></td>';
	appendString += '<td> <input type="number" name="jobSalaryMin" value=""></td>';
	appendString += '<td> <input type="number" name="jobSalaryMax" value=""></td>';
	appendString += '<td> <input type="number" name="jobGender" value=""></td>';
	appendString += '<td> <input type="text" name="jobInterviewAddress" value=""></td>';
	appendString += '<td> <input type="text" name="jobDetailRequirement" value=""></td>';
	appendString += '</tr>'
	$('#jobRoleTable tbody').append(appendString);
	//console.log(appendString);

}

function hashDiff(h1, h2) {
  //var d = {};
  var d = [];
  for (k in h2) {
    if (h1[k] !== h2[k]) {
        if(k == 'jobRoleidSelect') d.push('jobRole');
        else if (k == 'jobLocationidSelect') d.push('locality');
        else d.push(k);//d[k] = h2[k];
    }
  }
  return d;
}

function convertSerializedArrayToHash(data) {
  var r = {};
  for (var i = 0;i<data.length;i++) {
    if(r.hasOwnProperty(data[i].name)){r[data[i].name] += ','+data[i].value;}
    else{r[data[i].name] = data[i].value;}
  }
  return r;
}

Object.defineProperty(Object.prototype, 'getKeyByValue',{
  value : function(value) {
	//console.log("getKeyByValue called with "+value);
    for( var prop in this ) {
        if( this.hasOwnProperty( prop ) ) {
             //console.log("prop="+prop+" value="+JSON.stringify(this[prop]));
             if( this[ prop ].name === value ) {
                 return this[prop].id;
             }
        }
    }
  },
  enumerable : false
});

function processDataForAddRecruiterLead(returnedData) {
   	if(returnedData.hasOwnProperty("messages")) {
   		returnedData.messages.forEach(function (message) {
   			if(message.type == message_error) {notifyError(message.text);}
   			else if(message.type == message_info) {notifySuccess(message.text);}
   			else if(message.type == message_warning) {notifyWarning(message.text);}
       		});
   	}
   	if(returnedData.hasOwnProperty("entity") &&
   	   returnedData.entity.hasOwnProperty("recruiterLeadId") &&
   	   parseInt(returnedData.entity.recruiterLeadId,10) > 0) {
   	        console.log("returnedData.entity.recruiterLeadId: "+returnedData.entity.recruiterLeadId);
			window.location.href = ("/showRecruiterLead/"+parseInt(returnedData.entity.recruiterLeadId,10))
   	   }
   	else {console.log("returnedData.entity.recruiterLeadId not found")}
   	//if(returnedData.status == 1) {notifySuccess("Updated Successfully");}
}

function processDataForModifyRecruiterLead(returnedData) {
   	if(returnedData.hasOwnProperty("messages")) {
   		returnedData.messages.forEach(function (message) {
   			if(message.type == message_error) {notifyError(message.text);}
   			else if(message.type == message_info) {notifySuccess(message.text);}
   			else if(message.type == message_warning) {notifyWarning(message.text);}
       		});
   	}
}

function notifyError(msg){
    Materialize.toastError(msg, 3000, 'rounded');
}

function notifyWarning(msg) {
	Materialize.toast(msg, 3000, 'rounded', null, '#f7a000');
}

function notifySuccess(msg){
    Materialize.toastSuccess(msg, 3000, 'rounded');
}
