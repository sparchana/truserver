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
        methods: {
            submitURL: function (long_url) {
                var promise = new Promise(function(resolve, reject) {
                    var request = new XMLHttpRequest();

                    request.open('GET', '/truly/api/compress?url='+long_url);
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
        _notify: function (message) {
            var notification = document.querySelector('.mdl-js-snackbar');
            notification.MaterialSnackbar.showSnackbar(
                {
                    message: message
                }
            );
        },
        validate: {
            input: function (data) {
                return app.validate.url(data);
            },
            url: function (url) {
                // c.f http://stackoverflow.com/questions/5717093/check-if-a-javascript-string-is-a-url
                //
                // var pattern = new RegExp('^(https?:\/\/)?'+ // protocol
                //     '((([a-z\d]([a-z\d-]*[a-z\d])*)\.)+[a-z]{2,}|'+ // domain name
                //     '((\d{1,3}\.){3}\d{1,3}))'+ // OR ip (v4) address
                //     '(\:\d+)?(\/[-a-z\d%_.~+]*)*'+ // port and path
                //     '(\?[;&a-z\d%_.~+=-]*)?'+ // query string
                //     '(\#[-a-z\d_]*)?$','i'); // fragment locater
                // if(!pattern.test(str)) {
                //     // alert("Please enter a valid URL.");
                //     console.log("validated failed");
                //     return false;
                // } else {
                //     console.log("validation success");
                //     return true;
                // }

                // return /^(?:(?:(?:https?|ftp):)?\/\/)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})).?)(?::\d{2,5})?(?:[/?#]\S*)?$/i.test( str );

                // c.f http://stackoverflow.com/questions/4314741/url-regex-validation
                var re = /^(http[s]?:\/\/){0,1}(www\.){0,1}[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,5}[\.]{0,1}/;
                if (!re.test(url)) {
                    console.log("invalid validating input data: " + url);

                    return false;
                } else {
                    return true;
                }
            }
        }
    };

    // submit click listener
    document.getElementById("submitBtn").addEventListener("click", function () {

        var _long_url = document.getElementById("long_url").value;
        if(app.validate.input(_long_url)){
            app.methods.submitURL(_long_url);
        } else {
            app.render.error("Invalid URL");
        }
    });

    var clipboard = new Clipboard('#copyBtn');

    clipboard.on('success', function(e) {
        var _long_url = document.getElementById("long_url").value;
        if(app.validate.input(_long_url)){
            app._notify("Copied Successfully");
        } else {
            app._notify("Nothing to copy");
        }
    });
    clipboard.on('error', function(e) {
        app._notify("Something went wrong. It looks like you will have to  manually copy!");
    });

}());
