/*
 *
 * Created by zero on 13/01/17.
 *
 */


var jobCardUtil = (function ($) {
    'use strict';

    var jobCardUtil = {
        deActivationMessage: null,
        method: {
            init: function () {
                console.log("jobcard util init");
                if(jobCardUtil.deActivationMessage == null) {
                    jobCardUtil.method.getDeActivateMessage(localStorage.getItem("candidateId"));
                }

            },
            getDeActivateMessage: function (candidateId) {
                //ajax call || its a promise
                var url = '/getDeActivationMessage';
                if(candidateId != null) {
                    url += "?candidateId="+candidateId;
                }
                $.ajax({type: 'POST', url: url}).then(function (returnedData) {
                        if (returnedData != null
                            && returnedData.deActivationMessage != null
                            && jobCardUtil.deActivationMessage == null) {

                            jobCardUtil.deActivationMessage = returnedData.deActivationMessage;
                        } else {
                            jobCardUtil.deActivationMessage = "";
                        }
                    },
                    function (xhr, state, error) {
                    });
            },
            notifyMsg: function(msg, type) {
                if(typeof $.notify == 'function'){
                    console.log("type: " + type);
                    if( type == 'error'){
                        $.notify(msg, type);
                    } else {
                        $.notify({
                            message: msg,
                            animate: {
                                enter: 'animated lightSpeedIn',
                                exit: 'animated lightSpeedOut'
                            }
                        }, {
                            type: type,
                            placement: {
                                from: "top",
                                align: "center"
                            }
                        });
                    }
                } else {
                    alert(msg);
                }
            }
        }
    };

    jobCardUtil.method.init();

    return jobCardUtil;
}(jQuery));