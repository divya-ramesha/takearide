(function () {
    var moduleName = "user";
    RedApplication.addModule(moduleName, function (context) {
        'use strict';

        var moduleEl,
            templateData,
            templateLoaderService,
            ajaxLoaderService,
            rightDiv,
            newEvent,
            pickup,
            drop,
            cancel_pickup,
            cancel_drop,
            day_attached,
            notifications_data,
            pickup_index = -1,
            drop_index = -1,
            employeeId = 1,
            events = [
                { date: '2016-12-10', batch : "7 AM", cabNumber : "KA 01 R-50 1122" },
                { date: '2016-12-09', batch : "5 PM", cabNumber : "KA 01 R-50 1122" },
                { date: '2016-12-12', batch : "5 PM", cabNumber : "KA 01 R-50 1122" }
            ],
            newNotifications,
            notifications = [],
            notifyResponse=[],
            smallRefreshTimer,
            smallRefreshTimerInterval = 5000;

        var initializeNotificationTooltip = function () {
            if (newNotifications.length > 0) {
                $(moduleEl).find(".tooltip-notify").tooltipster({
                    animation: 'grow',
                    speed: 0,
                    position: 'left',
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
                var eventDiv = $(moduleEl).find("#events-div");
                eventDiv.html(templateLoaderService.compile($(templateData).find("#event-notification-script-id").html(), {
                    notifications: notifyResponse
                }));
                var dd = $(moduleEl).find('.vticker').easyTicker({
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

        var convertSecondsToMillis = function (notifications, field) {
            $.each(notifications, function (key, value) {
                value[field] = value[field] * 1000;
            });
            return notifications;
        };

        var setupAutoRefresh = function () {
            smallRefreshTimer = $.timer(function () {
                eventData();
            });
            smallRefreshTimer.set({
                time: smallRefreshTimerInterval,
                autostart: true
            });
        };

        return {
            init: function () {
                moduleEl = context.getElement();
                templateLoaderService = context.getService("TemplateLoaderService");
                ajaxLoaderService = context.getService("AjaxLoaderService");
                templateLoaderService.load(moduleName, moduleEl, function (template) {
                    templateData = template;
                    $(moduleEl).html(templateLoaderService.compile($(templateData).find("#" + moduleName + "-main").html(), {}));
                    rightDiv = $(moduleEl).find(".right-div");
                    setupAutoRefresh();
                    Swag.registerHelpers(Handlebars);
                    Handlebars.registerHelper('dateFormatTimeAgo', function (context, block) {
                        if (window.moment) {
                            return moment(context).fromNow();
                        } else {
                            return context;
                        }
                    });
                    function getCalendar(){
                        ajaxLoaderService.getRaw("/api/v1/employee/getEmployeeRideDetails", {
                            employeeId: employeeId,
                        }, function (userData) {
                            console.log(userData);
                            newEvent = userData;
                            $('.cal1').clndr({
                                ready: function() {
                                    var self = this;

                                    $(this.element).on('mouseover', '.day', function(e) {
                                        var target = self.buildTargetObject(e.currentTarget, true);
                                        var schEvents = "";
                                        if(target.events.length){
                                            for(var i in target.events){
                                                schEvents += "Cab "+target.events[i].rideType +" at "+target.events[i].batch+", cabNo - "+target.events[i].cabNumber+" | ";
                                                if(target.events[i].rideType == "PICKUP"){
                                                    pickup_index = i;
                                                }
                                                if(target.events[i].rideType == "DROP"){
                                                    drop_index = i;
                                                }


                                            }
                                            $(this).attr("data-tooltip",schEvents);
                                            $(this).attr("data-position","right center");
                                        }
                                    });
                                },
                                events: newEvent,
                                clickEvents: {
                                    click: function(target) {
                                        day_attached = target.events;
                                        console.log("attched day " + day_attached);
                                        $('.ui.sidebar').sidebar('toggle');
                                        if (target.events.length){
                                            $("form").each(function (index) {
                                                $(this).css("display", "inline-block");
                                            });
                                        }
                                        else {
                                            day_attached=[];
                                            $("form").each(function (index) {
                                                $(this).css("display", "none");
                                            });
                                        }

                                    }
                                }
                            });
                            $('.ui.sidebar').sidebar({
                                context: $('.bottom.segment')
                            }).sidebar('attach events', '.menu .menu-item');
                            $('.ui.accordion').accordion();
                            $('.ui.dropdown').dropdown();
                            $('.ui.checkbox').checkbox();


                        });
                    }
                    getCalendar();
                    window.setInterval(checkCabArrival, 5000);
                    window.setInterval(checkCabRideEnd, 5000);
                    function checkCabArrival() {
                        ajaxLoaderService.get("/api/v1/events/employee/checkIfCabHasArrived", {
                            employeeId: employeeId,
                        }, function (data) {
                            console.log("cab arrived",data);
                            if(data){
                                $("#start-ride").css("opacity","1");
                                $("#start-ride").css("pointer-events","auto");
                            }else{
                                $("#start-ride").css("opacity","0.6");
                                $("#start-ride").css("pointer-events","none");
                            }
                        });
                    }
                    function checkCabRideEnd() {
                        ajaxLoaderService.get("/api/v1/events/employee/checkIfEmployeeStartedRide", {
                            employeeId: employeeId,
                        }, function (data) {
                            console.log("cab drop done",data);
                            if(data){
                                $("#end-ride").css("opacity","1");
                                $("#end-ride").css("pointer-events","auto");
                            }
                            else{
                                $("#end-ride").css("opacity","0.6");
                                $("#end-ride").css("pointer-events","none");
                            }
                        });
                    }



                });
            },
            destroy: function () {
                moduleEl = null;
            },
            onclick: function (event, element, elementType) {
                switch (elementType) {
                    case "refresh-data":
                        alert("refresh");
                        break;
                    case "notification":
                        alert("notification");
                        break;
                    case "get-profile":
                        rightDiv.html("Profile Info");
                        break;
                    case "get-user-settings":
                        rightDiv.html("User Settings");
                        break;
                    case "user-notifications":
                        ajaxLoaderService.get("/api/v1/employee/getPendingRescheduleRequestForEmployee", {
                            employeeId: employeeId,
                        }, function (data) {
                            console.log($("#notifications-container").html());
                            $("#notifications-container").html(templateLoaderService.compile($(templateData).find("#pending-notification").html(),data));
                            $("#notifications-container").css("display","inline");
                            $("#calender1").css("display","none");
                            console.log(data);
                        });

                        break;
                    case "user-home":
                        $("#calender1").css("display","inline");
                        $("#notifications-container").css("display","none");
                        break;
                    case "user-start":
                        console.log(day_attached);
                        var data = {"acknowledgedBy" : employeeId };
                        ajaxLoaderService.post("/api/v1/events/employee/startRideAck",data,function(res){
                            alertify.success('You Started Your Ride Now!');
                            console.log("result = " + res);
                        },function(er){
                            alertify.error('Error Updating The Document');
                        });
                        break;
                    case "user-end":
                        var param = {"acknowledgedBy" : employeeId };
                        ajaxLoaderService.post("/api/v1/events/employee/dropRideAck",param,function(res){
                            console.log("result = " + res);
                            alertify.success('Your Ride Has Been Successfully Completed!');
                        },function(er){
                            alertify.error('error updating the document');
                        });
                        break;
                    case "reschedule-submit":
                        pickup = $("#pickup :selected").val();
                        drop = $("#drop :selected").val();
                        if(pickup == "PICKUP" && drop == "DROP"){
                            alert("Please select the time to reschedule!");
                        }
                        else {
                            if(pickup !== "PICKUP" && pickup_index !=-1 ) {
                                var newDate_format = day_attached[pickup_index].date.split("-");
                                newDate_format = newDate_format[2]+"/"+newDate_format[1]+"/"+newDate_format[0];

                                var param = { "rideType":"PICKUP","oldBatch":day_attached[pickup_index].batch,"newBatch":pickup,"date": newDate_format, "employeeId":employeeId};

                                ajaxLoaderService.postJSON("/api/v1/employee/addRescheduleRequest",JSON.stringify(param),function(res){
                                   console.log("result = " + res);
                                    alertify.success('Reschedule Request Sent.');
                                },function(er){
                                    alertify.error('error updating the document');
                                });
                            }
                            if(drop !== "DROP" && drop_index !=-1 ) {
                                var newDate_format = day_attached[drop_index].date.split("-");
                                newDate_format = newDate_format[2]+"/"+newDate_format[1]+"/"+newDate_format[0];
                                var param = { "rideType":"DROP","oldBatch":day_attached[drop_index].batch,"newBatch":drop,"date": newDate_format, "employeeId":employeeId};
                                ajaxLoaderService.postJSON("/api/v1/employee/addRescheduleRequest",JSON.stringify(param),function(res){
                                    console.log("result = " + res);
                                    alertify.success('Reschedule Request Sent.');
                                },function(er){
                                    alertify.error('error updating the document');
                                });
                            }
                        }

                        break;
                    case "cancel-ride-submit":
                        cancel_pickup = $("#cancel_pickup").is(':checked');
                        cancel_drop = $("#cancel_drop").is(':checked');
                        if(cancel_pickup || cancel_drop){
                            if(cancel_pickup &&  pickup_index !=-1){
                                var param = {"employeeId":employeeId, "rideId":day_attached[pickup_index].rideId};
                                ajaxLoaderService.post("/api/v1/employee/cancelRide",param,function(res){
                                    console.log("result = " + res);
                                    alertify.success('You Cancelled Your Pickup On '+ day_attached[pickup_index].date + " at "+day_attached[pickup_index].batch);
                                },function(er){
                                    alertify.error('error updating the document');
                                });
                            }
                            else if(cancel_drop &&  drop_index !=-1){
                                var param = {"employeeId":employeeId, "rideId":day_attached[drop_index].rideId};
                                ajaxLoaderService.post("/api/v1/employee/cancelRide",param,function(res){
                                    console.log("result = " + res);
                                    alertify.success('You Cancelled Your Drop On '+ day_attached[drop_index].date + " at "+day_attached[drop_index].batch);
                                },function(er){
                                    alertify.error('error updating the document');
                                });
                            }
                        }
                        else{
                            alert("No ride type to cancel!");
                        }
                        break;
                }
            }
        };
    });
})();
