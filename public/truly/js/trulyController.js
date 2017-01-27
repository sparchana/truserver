/**
 * truly: TruJobs Url shortener
 * Version 1.0.0
 *
 * Copyright (c) 2016 TruJobs.in (http://trujobs.in)
 *
 * Created by zero on 23/12/16.
 *
 */

;(function () {
    'use strict';

    var app = {
        method: {
            submit: function (long_url) {
                var promise = new Promise(function(resolve, reject) {
                    var request = new XMLHttpRequest();

                    request.open('GET', '/truly/api/long_url='+long_url);
                    request.onload = function() {
                        if (request.status == 200) {
                            resolve(request.response); // we got data here, so resolve the Promise
                        } else {
                            reject(Error(request.statusText)); // status is not 200 OK, so reject
                        }
                    };

                    request.onerror = function() {
                        reject(Error('Error fetching data.')); // error occurred, reject the  Promise
                    };

                    request.send(); //send the request
                });

                promise.then(function(data) {
                    app.render.shortUrl(data);
                }, function(error) {
                    console.log('Promise rejected.');
                    console.log(error.message);
                });
            },
        },
        render: {
            shortUrl: function (data) {
                console.log(data);
                // TODO snack bar with success message
            },
            error: function (message) {
                // TODO snack bar on
            }
        },
        validate: {
            input: function (data) {
                console.log("validate input data: " + data);
                return true
            }
        }
    };

    // submit click listener
    document.getElementById("submitBtn").addEventListener("click", function () {

        var _long_url = document.getElementById("long_url").value;
        if(app.validate.input(_long_url)){

        } else {
            app.render.error("Invalid URL");
        }
    });

}());
