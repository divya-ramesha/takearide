(function () {
    var moduleName = "driver";
    RedApplication.addModule(moduleName, function (context) {
        'use strict';

        var moduleEl,
            templateData,
            templateLoaderService,
            ajaxLoaderService,
            rightDiv,
            driverId = 10001,
            currentRideId = 0,
            oldRideId,
            refreshTimer,
            smallRefreshTimer,
            smallRefreshTimerInterval = 8000,
            refreshTimerInterval = 60000,
            currentView = "home",
            notifications = [],
            newNotifications = [],
            notifyResponse = [],
            notified = false,
            notifiedTime,
            notifyNow = false,
            started = false,
            startTime,
            startNow = false,
            ended = false,
            endTime,
            endNow = false;

        var convertSecondsToMillis = function (notifications, field) {
            $.each(notifications, function (key, value) {
                value[field] = value[field] * 1000;
            });
            return notifications;
        };

        var initializeNotificationTooltip = function () {
            if (newNotifications.length > 0) {
                $(moduleEl).find(".tooltip-notify").tooltipster({
                    animation: 'grow',
                    speed: 0,
                    position: 'bottom',
                    delay: 1,
                    onlyOne: true,
                    theme: 'tooltipster-shadow',
                    trigger: 'click',
                    content: 'Loading...',
                    functionBefore: function (instance, helper) {
                        helper();
                        var htmlContent = templateLoaderService.compile($(templateData).find("#notification-popup").html(), {
                            newNotifications: newNotifications
                        });
                        instance.tooltipster('content', htmlContent);
                    },
                    contentAsHTML: true,
                    interactive: true,
                    functionAfter: function (instance, helper) {
                        notifications = _.union(newNotifications, notifications);
                        newNotifications = [];
                        updateNotificationAlarm();
                        instance.tooltipster('destroy');
                        $(moduleEl).find(".tooltip-notify").removeAttr("title");
                    }
                });
            }
        };

        var updateNotificationAlarm = function () {
            var notficationdiv = $(moduleEl).find(".tooltip-notify");
            notficationdiv.html(templateLoaderService.compile($(templateData).find("#notification-number-script").html(), {
                newNotificationsLength: newNotifications.length
            }));
            $('.popup-icon').popup();
            initializeNotificationTooltip();
        };

        var getDifference = function (n, nn) {
            var d = [];
            $.each(nn, function (key, value) {
                if (_.where(n, value).length <= 0) {
                    d.push(value);
                }
            });
            return d;
        };

        var eventData = function () {
            ajaxLoaderService.get("/api/v1/events/getLast10RideEvents", {
                rideId: 1
            }, function (response) {
                response = convertSecondsToMillis(response, "timestamp");
                newNotifications = getDifference(notifications, response);
                notifyResponse = response;
                updateNotificationAlarm();
                var eventDiv = $(rightDiv).find(".events-div");
                eventDiv.html(templateLoaderService.compile($(templateData).find("#event-notification-script-id").html(), {
                    notifications: notifyResponse
                }));
                var dd = $(rightDiv).find('.vticker').easyTicker({
                    direction: 'up',
                    easing: 'easeInOutBack',
                    speed: 'medium',
                    interval: 2000,
                    height: '10000px',
                    visible: 0,
                    mousePause: 1,
                    controls: {
                        up: '.up',
                        down: '.down',
                        toggle: '.toggle',
                        stopText: 'Stop !!!'
                    }
                }).data('easyTicker');
            });
        };

        var showHomePage = function () {
            currentView = "home";
            rightDiv.html(templateLoaderService.compile($(templateData).find("#driver-home-script-id").html(), {}));
            $("#owl-demo").owlCarousel({
                slideSpeed: 20,
                paginationSpeed: 50,
                singleItem: true,
                autoPlay: true
            });
            //$('.ui.sticky-menu').sticky({ context: '.pusher' });
            eventData();
        };

        var showCurrentRides = function () {
            currentView = "rides";
            ajaxLoaderService.get("/api/v1/driver/getNextRideInformationForDriver", {
                driverId: driverId
            }, function (driverData) {
                if (driverData.length > 0) {
                    currentRideId = driverData[0].rideId;
                    rightDiv.html(templateLoaderService.compile($(templateData).find("#driver-current-rides-script-id").html(), {
                        data: driverData,
                        notified: notified,
                        notifiedTime: notifiedTime,
                        notifyNow: notifyNow,
                        started: started,
                        startTime: startTime,
                        startNow: startNow,
                        ended: ended,
                        endTime: endTime,
                        endNow: endNow
                    }));
                    var driverCurrentRidesTable = $(moduleEl).find("#driver-current-rides-tbl");
                    var currentRidesTbl = driverCurrentRidesTable.DataTable({
                        stateSave: true,
                        destroy: true,
                        paging: false,
                        scrollX: true,
                        order: [[0, "asc"]],
                        dom: 'Bfrtip',
                        columnDefs: [
                            {"orderable": false, "targets": -1}
                        ],
                        buttons: [
                            {
                                extend: 'colvis',
                                text: 'Columns',
                                columns: ':not(:last-child)'

                            }
                        ]
                    });
                } else {
                    rightDiv.html(templateLoaderService.compile($(templateData).find("#empty-response-script").html(), {
                        msg: "Currently there are no rides for you."
                    }));
                }
                eventData();
            });
        };

        var showHistoryRides = function () {
            currentView = "history";
            ajaxLoaderService.get("/api/v1/driver/getLast15RidesForDriver", {
                driverId: driverId
            }, function (driverData) {
                if (driverData.length > 0) {
                    var ongoingRides = _.where(driverData, {isComplete: false});
                    var completedRides = _.where(driverData, {isComplete: true});
                    rightDiv.html(templateLoaderService.compile($(templateData).find("#driver-history-script-id").html(), {
                        ongoingRides: ongoingRides,
                        completedRides: completedRides
                    }));
                    console.log(JSON.stringify(ongoingRides));
                    console.log(JSON.stringify(completedRides));
                    var currentRidesTable = $(moduleEl).find("#driver-history-ongoing-rides-tbl");
                    var currentRidesTbl = currentRidesTable.DataTable({
                        stateSave: true,
                        destroy: true,
                        paging: false,
                        scrollX: true,
                        order: [[0, "asc"]],
                        dom: 'Bfrtip',
                        buttons: [
                            {
                                extend: 'colvis',
                                text: 'Columns',
                                columns: ':not(:last-child)'

                            }
                        ]
                    });
                    var completedRidesTable = $(moduleEl).find("#driver-history-completed-rides-tbl");
                    var completedRidesTbl = completedRidesTable.DataTable({
                        stateSave: true,
                        destroy: true,
                        paging: false,
                        scrollX: true,
                        order: [[0, "asc"]],
                        dom: 'Bfrtip',
                        buttons: [
                            {
                                extend: 'colvis',
                                text: 'Columns',
                                columns: ':not(:last-child)'

                            }
                        ]
                    });
                } else {
                    rightDiv.html(templateLoaderService.compile($(templateData).find("#empty-response-script").html(), {
                        msg: "Looks like you haven't taken/completed a single ride !!!"
                    }));
                }
                eventData();
            });
        };

        var showStatistics = function () {
            currentView = "statistics";
            rightDiv.html(templateLoaderService.compile($(templateData).find("#driver-statistics-script-id").html(), {}));
            eventData();
        };

        var refreshCurrentView = function () {
            switch (currentView) {
                case "home":
                    showHomePage();
                    break;
                case "rides":
                    showCurrentRides();
                    break;
                case "history":
                    showHistoryRides();
                    break;
                case "statistics":
                    showStatistics();
                    break;
            }
        };

        var updateNotifyUsersButton = function () {
            var notifyButton = $(rightDiv).find(".notify-ride-button");
            ajaxLoaderService.get("/api/v1/events/driver/checkIfDriverCanAnnounceCabArrival", {
                driverId: driverId
            }, function (response) {
                if (response) {
                    notifyButton.removeClass("disabled");
                    notifyNow = true;
                }
            });
        };

        var updateStartMyRide = function () {
            var startButton = $(rightDiv).find(".start-ride-button");
            ajaxLoaderService.get("/api/v1/events/driver/checkIfDriverCanStartCab", {
                driverId: driverId
            }, function (response) {
                if (response) {
                    startButton.removeClass("disabled");
                    startNow = true;
                }
            });
        };

        var updateEndMyRide = function () {
            var endButton = $(rightDiv).find(".end-ride-button");
            ajaxLoaderService.get("/api/v1/events/driver/checkIfDriverCanEndRide", {
                driverId: driverId
            }, function (response) {
                if (response) {
                    endButton.removeClass("disabled");
                    endNow = true;
                }
            });
        };

        var updateCurrentRideDefaults = function () {
            if (currentRideId == 0) {
                notified = false;
                notifiedTime = null;
                notifyNow = false;
                started = false;
                startTime = null;
                startNow = false;
                ended = false;
                endTime = null;
                endNow = false;
            }
        };

        var updateEmployeeDrop = function () {
            var table = $(rightDiv).find("#driver-current-rides-tbl");
            $(table).find(".tr-field").each(function () {
                var tr = $(this);
                ajaxLoaderService.get("/api/v1/events/driver/checkIfDriverCanAckEmployeeDrop", {
                    driverId: driverId,
                    employeeId: $(this).data("employee")
                }, function (response) {
                    if (response) {
                        var td = $(tr).find(".td-last");
                        $(td).html("<i class='check circle icon large olive icon'></i>" +
                            "<a href='javascript:void(0);' data-type='driver-ack-emp'><i class='check circle outline icon large teal icon'></i></a>");
                    }
                });
            });
        };

        var setupAutoRefresh = function () {
            refreshTimer = $.timer(function () {
                refreshCurrentView();
            });
            smallRefreshTimer = $.timer(function () {
                eventData();
                if (currentView == "rides") {
                    if (currentRideId != 0) {
                        updateNotifyUsersButton();
                        updateStartMyRide();
                        updateEndMyRide();
                        updateEmployeeDrop();
                    }
                    updateCurrentRideDefaults();
                }
            });

            refreshTimer.set({
                time: refreshTimerInterval,
                autostart: true
            });
            smallRefreshTimer.set({
                time: smallRefreshTimerInterval,
                autostart: true
            });
        };

        var destroyTimer = function () {
            refreshTimer.clearTimer();
        };

        var refreshMainPage = function () {
            $(moduleEl).html(templateLoaderService.compile($(templateData).find("#" + moduleName + "-main").html(), {}));
            rightDiv = $(moduleEl).find(".right-div");
            $('.ui.sidebar').sidebar({
                context: $('.bottom.segment')
            }).sidebar('attach events', '.menu .menu-item');
            $('.ui.dropdown').dropdown();
            refreshCurrentView();
        };

        return {
            init: function () {
                moduleEl = context.getElement();
                templateLoaderService = context.getService("TemplateLoaderService");
                ajaxLoaderService = context.getService("AjaxLoaderService");
                templateLoaderService.load(moduleName, moduleEl, function (template) {
                    templateData = template;
                    refreshMainPage();
                    setupAutoRefresh();
                });

                Swag.registerHelpers(Handlebars);
                Handlebars.registerHelper('dateFormatTimeAgo', function (context, block) {
                    if (window.moment) {
                        return moment(context).fromNow();
                    } else {
                        return context;
                    }
                });
            },
            destroy: function () {
                moduleEl = null;
                destroyTimer();
                refreshTimer = null;
                $("#owl-demo").destroy();
            },
            onclick: function (event, element, elementType) {
                switch (elementType) {
                    case "refresh-data":
                        refreshMainPage();
                        break;
                    case "driver-home":
                        showHomePage();
                        break;
                    case "current-ride":
                        showCurrentRides();
                        break;
                    case "driver-history":
                        showHistoryRides();
                        break;
                    case "notify-users":
                        ajaxLoaderService.post("/api/v1/events/driver/cabArrived", {
                            rideId: currentRideId,
                            acknowledgedBy: driverId
                        }, function (response) {
                            alertify.success("You Acknowledged Cab Arrived");
                            notified = true;
                            notifiedTime = new Date().getTime();
                            showCurrentRides();
                        });
                        break;
                    case "start-ride":
                        ajaxLoaderService.post("/api/v1/events/driver/startRideAck", {
                            rideId: currentRideId,
                            acknowledgedBy: driverId
                        }, function (response) {
                            alertify.success("You Started Your Ride Now");
                            started = true;
                            startTime = new Date().getTime();
                            showCurrentRides();
                        });
                        break;
                    case "driver-ack-emp":
                        ajaxLoaderService.post("/api/v1/events/driver/dropRideAck", {
                            rideId: currentRideId,
                            acknowledgedBy: driverId,
                            acknowledgedTo: $(element).closest("tr").data("employee")
                        }, function (response) {
                            alertify.success("You Acknowledged Employee Drop");
                            var td = $(element).closest("td");
                            $(td).html("<i class='check circle icon large olive icon'></i>" +
                                "<i class='check circle icon large teal icon'></i>");
                        });
                        break;
                    case "end-ride":
                        ajaxLoaderService.post("/api/v1/events/driver/endRide", {
                            rideId: currentRideId,
                            acknowledgedBy: driverId
                        }, function (response) {
                            alertify.success("You Ended Your Ride");
                            ended = true;
                            endTime = new Date().getTime();
                            oldRideId = currentRideId;
                            currentRideId = 0;
                            showCurrentRides();
                        });
                        break;
                    case "get-profile":
                        currentView = "profile";
                        rightDiv.html(templateLoaderService.compile($(templateData).find("#driver-profile-script-id").html(), {}));
                        eventData();
                        break;

                }
            }
        };
    });
})();
