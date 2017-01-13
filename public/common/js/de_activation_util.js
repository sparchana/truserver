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
                if(jobCardUtil.deActivationMessage == null) {
                    jobCardUtil.method.getDeActivateMessage();
                }

            },
            getDeActivateMessage: function () {
                //ajax call || its a promise
                $.ajax({type: 'POST', url: '/getDeActivationMessage'}).then(function (returnedData) {
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
                } else {
                    alert(msg);
                }
            }
        }
    };

    jobCardUtil.method.init();

    return jobCardUtil;
}(jQuery));