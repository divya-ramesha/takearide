/**
 * Created by divyar on 17/12/16.
 */

(function () {
    // Global data access service
    RedApplication.addService('GlobalDataService', function (application) {
        'use strict';
        var redGlobalData = application.getGlobal("RED_GLOBAL");
        return {
            get: function (key) {
                return redGlobalData[key];
            },
            set: function (key, value) {
                return redGlobalData[key] = value;
            }
        };
    });

    RedApplication.addService('RedUIService', function (application) {
        'use strict';
        return {
            show: function (cont) {
                cont ? $(cont).waitMe() : $("#main-container").waitMe();
            },
            hide: function (cont) {
                cont ? $(cont).waitMe("hide") : $("#main-container").waitMe("hide");
            }
        };
    });

    // Loads module javascript files. Caches them in local storage for faster access
    RedApplication.addService('ModuleLoaderService', function (application) {
        'use strict';
        var redUIService = application.getService("RedUIService");

        return {
            load: function (elementType, container, cb, config) {
                try {
                    RedApplication.stopAll(container);
                } catch (e) {
                    console.log(e);
                }

                $(container).empty();
                var moduleKey = elementType + "/module.js";
                redUIService.show();
                $.getScript(moduleKey, function (data) {

                    var configScript = "<script type=\"text/x-config\">";
                    if (config) {
                        configScript = configScript + JSON.stringify(config);
                    } else {
                        configScript = configScript + JSON.stringify({});
                    }
                    configScript = configScript + "</script>";

                    var header = elementType == "red-layout" ? "ui" : "ui basic segment";
                    var appendedEle = $("" +
                        "<div class='" + header + "' data-module=\"" + elementType + "\">" +
                        configScript +
                        "</div>")
                        .appendTo($(container));

                    RedApplication.start($(appendedEle)[0]);
                    redUIService.hide();
                    if (cb) cb();
                });
            },
            loadMainContainerModule: function (elementType, extraData) {
                var config = {};
                if (extraData) {
                    config["extraData"] = extraData;
                }
                this.load(elementType, document.getElementById("main-container"), null, config);
            }
        };
    });

    // Loads template files. Caches them in local storage for faster access
    RedApplication.addService('TemplateLoaderService', function (application) {
        'use strict';
        var redUIService = application.getService("RedUIService");

        var appendStyles = function (styleElement) {
            var styleElementId = $(styleElement).attr("id");
            var $head = $("head");
            if ($head.find("#" + styleElementId).length <= 0) {
                $head.append(styleElement);
            }
        };
        return {
            load: function (template, moduleEl, cb) {
                var templateKey = template + "/template.hbs";
                redUIService.show();
                $.get(templateKey, function (data) {
                    redUIService.hide();
                    if (cb) {
                        var html = $("<div/>").html(data);
                        appendStyles(html.find("#" + template + "-css"));
                        cb(html);
                    }
                });
            },
            compile: function (templateData, source) {
                return Handlebars.compile(templateData)(source);
            }
        };
    });

    // Loads ajax calls
    RedApplication.addService('AjaxLoaderService', function (application) {
        'use strict';

        var redUIService = application.getService("RedUIService");

        var logError = function (jqxhr, textStatus, error) {
            redUIService.hide();
            var err = textStatus + ", " + error;
            console.log("Request Failed: " + err);
        };

        var triggerSuccess = function (cb, data) {
            redUIService.hide();
            if (cb) cb(data);
        };

        return {
            get: function (url, params, cb, errCb) {
                redUIService.show();
                $.getJSON(url, params)
                    .done(function (data) {
                        triggerSuccess(cb, data)
                    })
                    .fail(function (jqxhr, textStatus, error) {
                        logError(jqxhr, textStatus, error);
                        if (errCb) errCb(jqxhr, textStatus, error);
                    });
            },
            getRaw: function (url, params, cb, errCb) {
                redUIService.show();
                $.get(url, params)
                    .done(function (data) {
                        triggerSuccess(cb, data)
                    })
                    .fail(function (jqxhr, textStatus, error) {
                        logError(jqxhr, textStatus, error);
                        if (errCb) errCb(jqxhr, textStatus, error);
                    });
            },
            post: function (url, params, cb, errCb) {
                redUIService.show();
                $.post(url, params)
                    .done(function (data) {
                        triggerSuccess(cb, data)
                    })
                    .fail(function (jqxhr, textStatus, error) {
                        logError(jqxhr, textStatus, error);
                        if (errCb) errCb(jqxhr, textStatus, error);
                    });
            },
            postJSON: function (url, json, cb, errCb) {
                redUIService.show();
                $.ajax({
                    type: "POST",
                    url: url,
                    data: json,
                    dataType: "json",
                    contentType: "application/json; charset=utf-8"
                }).done(function (data) {
                    triggerSuccess(cb, data);
                }).fail(function (jqxhr, textStatus, error) {
                    logError(jqxhr, textStatus, error);
                    if (errCb) errCb(jqxhr, textStatus, error);
                });
            }
        }

    });

    // Define your routes here.
    RedApplication.addService('RoutingService', function (application) {
        'use strict';
        var headerMenu,
            moduleLoaderService,
            globalDataService,
            routes = [
                "driver",
                "admin",
                "user"
            ];

        var resetLink = function (section, extraData) {
            moduleLoaderService.loadMainContainerModule(section, extraData);
            headerMenu.find("a.item").removeClass("active");
            headerMenu.find("a.item[data-type=menu-lk-" + section + "]").addClass("active");
        };

        return {
            initializeRoutes: function () {
                moduleLoaderService = application.getService("ModuleLoaderService");
                globalDataService = application.getService("GlobalDataService");

                _.each(routes, function (route) {
                    Path.map("#/" + route + "(/:data)").to(function () {
                        var redHash = globalDataService.get("hash");
                        if (redHash && redHash != null) {
                            if (redHash.indexOf(route) != 0) {
                                globalDataService.set("hash", route);
                                resetLink(route, this.params["data"]);
                            }
                        } else {
                            globalDataService.set("hash", route);
                            resetLink(route, this.params["data"]);
                        }
                    });
                });

                Path.map("#/").to(function () {
                    window.location.hash = "/";
                });

                Path.root("/");

                Path.rescue(function () {
                    alert("404: Route Not Found");
                    window.location.hash = "/";
                });

                Path.listen();
            }
        };
    });

})();

