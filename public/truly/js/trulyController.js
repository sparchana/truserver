/**
 * truly: TruJobs Url shortener
 * Version 1.0.0
 *
 * Copyright (c) 2017 TruJobs.in (http://trujobs.in)
 *
 * Created by zero on 27/01/17.
 *
 */

;(function () {
    'use strict';

    var app = {
        methods: {
            submitURL: function (long_url) {
                var promise = new Promise(function(resolve, reject) {
                    var request = new XMLHttpRequest();
                    var d = {};
                    d["longUrl"] = long_url;
                    request.open('POST', '/truly/api/v1/compress');
                    request.setRequestHeader("Content-Type", "application/json");

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

                    request.send(JSON.stringify(d)); //send the request
                });

                promise.then(function(data) {
                    app.render.shortUrl(data);
                }, function(error) {
                    console.log(error.message);
                });
            },
        },
        render: {
            shortUrl: function (data) {
                var shortUrlDiv = document.getElementById('short_url');
                shortUrlDiv.textContent = data;
                app._notify("link simplified !")
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
                // c.f http://blog.mattheworiordan.com/post/13174566389/url-regular-expression-for-links-with-or-without
                // var expression = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
                var expression;
                if(url != null && url.includes("localhost:9000")) {
                    expression = /((([A-Za-z]{3,9}:(?:\/\/)?)(?:[\-;:&=\+\$,\w]+@)?[A-Za-z0-9\.\-]+|(?:www\.|[\-;:&=\+\$,\w]+@)[A-Za-z0-9\.\-]+)((?:\/[\+~%\/\.\w\-_]*)?\??(?:[\-\+=&;%@\.\w_]*)#?(?:[\.\!\/\\\w]*))?)/;
                } else {
                    expression =  /[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi;
                }
                var regex = new RegExp(expression);
                if (!url.match(regex)) {
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
            app._notify("Invalid URL");
            document.getElementById("short_url").innerHTML = "";
        }
    });

    var clipboard = new Clipboard('#copyBtn');

    clipboard.on('success', function(e) {
        var _short_url = document.getElementById("short_url").innerHTML;
        if(_short_url != null && _short_url != ""){
            app._notify("Copied to Clip Board !")
        } else {
            app._notify("Nothing to copy !")
        }
    });
    clipboard.on('error', function(e) {
        app._notify("Something went wrong. It looks like you will have to  manually copy!");
    });

}());
