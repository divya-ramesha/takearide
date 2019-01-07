/**
 * Created by chethankumar on 17/12/16.
 */
(function () {
    var moduleName = "admin";
    RedApplication.addModule(moduleName, function (context) {
        'use strict';

        var moduleEl,
            templateData,
            templateLoaderService,
            ajaxLoaderService,
            menuItems,
            pusher,
            newNotifications,
            notifications = [],
            notifyResponse=[],
            smallRefreshTimer,
        smallRefreshTimerInterval = 20000;

        var getUserList = function(){
            $.get("/api/v1/employee/getAllEmployeeDetails", {}, function (response) {
                $(moduleEl).find("#create-group").html(templateLoaderService.compile($(templateData).find("#entry-template").html(), response));
                $(moduleEl).find('.table').dataTable({
                    paging: false
                });
                $(moduleEl).find('.button-add').popup({on: 'click'});
               // $(moduleEl).find('#main-button').popup({on: 'click'});
            });
        };
       
        var showHideMain = function(){
            
            if ($(moduleEl).find('#container').is(":visible")) {
                $(moduleEl).find('#container').html("");
            } 
            if($(moduleEl).find('#create-group').is(":visible")){
                
            }else {
                $(moduleEl).find('#create-group').show();
            }
        };
        
        var todaysRides= function(){
            $.get("/api/v1/admin/getAllRidesForToday", {}, function (response) {
                $(moduleEl).find("#create-group").html(templateLoaderService.compile($(templateData).find("#today-rides-template").html(), response));
                $(moduleEl).find('.table').dataTable({
                    paging: false
                });
                $(moduleEl).find('.button').popup({on: 'click'});
                $(moduleEl).find("#add-ride-template").hide();
            });
        };
        
        var getGroupLists = function(){
            ajaxLoaderService.get("/api/v1/admin/getAllGlobalRideGroupsTemplates", {}, function (response) {
               $(moduleEl).find("#create-group").html(templateLoaderService.compile($(templateData).find("#groups-template").html(), response));
                $(moduleEl).find('.table').dataTable({
                    paging: false
                });
                $(moduleEl).find('.button').popup({on: 'click'});
            }); 
        };
        var getRescheduleRequests = function(){
            ajaxLoaderService.get("/api/v1/admin/getAllUnacknowledgedRescheduleRequests",{},function(res){
                $(moduleEl).find("#create-group").html(templateLoaderService.compile($(templateData).find("#reschedule-template").html(), res));
                $(moduleEl).find('.table').dataTable({
                    paging: false
            });
        });
        };
        
        var getDriversList = function(){
            ajaxLoaderService.get("/api/v1/driver/getAllDriverDetails",{},function(res){
                $(moduleEl).find("#create-group").html(templateLoaderService.compile($(templateData).find("#driver-template").html(), res));
                $(moduleEl).find('.table').dataTable({
                    paging: false
                });
                $(moduleEl).find('.button').popup({on: 'click'});
            });
        };

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
        
        var updateMain = function(){
            ajaxLoaderService.get("/api/v1/driver/getAllDriverDetails",{},function(res){
                var data = {};
                data.drivers= res;
                $.get("/api/v1/employee/getAllEmployeeDetails",{},function(res){
                    data.users = res;
                    $(moduleEl).find("#create-group").html(templateLoaderService.compile($(templateData).find("#create-main").html(),data));
                    $('.ui.dropdown')
                        .dropdown({
                            allowAdditions: true
                        })
                    ;
                    
                });
            });        
        };
        
        var history = function(){
            ajaxLoaderService.get("/api/v1/admin/getAllRides",{},function(res){
                $(moduleEl).find("#create-group").html(templateLoaderService.compile($(templateData).find("#history-template").html(), res));
                $(moduleEl).find('.table').dataTable({
                    paging: false
                });
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
                    updateMain();
                    setupAutoRefresh();
                    Swag.registerHelpers(Handlebars);
                    Handlebars.registerHelper('dateFormatTimeAgo', function (context, block) {
                        if (window.moment) {
                            return moment(context).fromNow();
                        } else {
                            return context;
                        }
                    });
                    eventData();
                    $('.ui.sidebar')
                        .sidebar({
                            context: $('.bottom.segment')
                        })
                        .sidebar('attach events', '.menu .menu-item');
                });
                pusher = $(moduleEl).find("#create-group");
                $(moduleEl).find("#container").hide();

            },
           
            destroy: function () {
                moduleEl = null;
            },
            onclick: function (event, element, elementType) {

                if (elementType == "home") {
                    
                    showHideMain();
                    updateMain();
                }
                if (elementType == "user-lists") {
                    showHideMain();
                    getUserList();
                }
                if (elementType == "group-lists") {
                    showHideMain();
                    getGroupLists();
                }

                if (elementType == "add-employee") {
                    var payload = {};
                    payload.name = $('[name="name"]').val().toString();
                    payload.employeeId = $('[name="employeeId"]').val();
                    payload.address = $('[name="address"]').val().toString();
                    payload.phoneNumber = $('[name="phoneNumber"]').val().toString();
                    payload.team = $('[name="team"]').val().toString();
                    payload.password = "sprinklr";
                    if (!payload.name || !payload.employeeId || !payload.address) {
                        alertify.error('please pass name id and address mandatory');
                    }
                    console.log(JSON.stringify(payload));
                    ajaxLoaderService.postJSON("api/v1/employee/addOrUpdateEmployee", JSON.stringify(payload), function (res) {
                        alertify.success('document successfully added');
                        getUserList();
                    }, function (er) {
                        alertify.error('error adding document please try after some time');
                    });
                }

                if (elementType == "create-group") {
                    var payload = {};
                    payload.driverId = $("#users option:selected").val();
                    payload.batch = $("#time option:selected").val();
                    payload.employeeList = $("#names").val().split(',').map(function (item) {
                        return parseInt(item, 10);
                    });
                    payload.rideType = $("#pickOrDrop option:selected").val();
                    console.log(payload);
                    ajaxLoaderService.postJSON("/api/v1/admin/addOrUpdateGlobalRideGroupsTemplateForDriver", JSON.stringify(payload), function (res) {
                        alertify.success('document successfully added');
                    }, function (er) {
                        alertify.error('error adding document please try after some time');
                    });
                }

                if (elementType == "remove-user") {
                    var employeeId = $(element).attr("id");
                    console.log(employeeId);
                    ajaxLoaderService.post("api/v1/employee/removeEmployee", {employeeId: employeeId}, function (res) {
                        if (res.employeeId == employeeId) {
                            alertify.success('document successfully removed');
                            getUserList();
                        } else {
                            alertify.error('error in removing document');
                        }
                    });
                }

                if (elementType == "get-drivers") {
                    getDriversList();
                }

                if (elementType == "add-driver") {
                    var payload = {};
                    payload.name = $('[name="driverName"]').val().toString();
                    payload.driverId = $('[name="driverId"]').val();
                    payload.phoneNumber = $('[name="phoneNumber"]').val().toString();
                    payload.carNumber = $('[name="carNumber"]').val().toString();
                    payload.password = "driver";

                    if (!payload.name || !payload.driverId || !payload.carNumber) {
                        alertify.error('please pass name, id, car number mandatory');
                    }
                    console.log(JSON.stringify(payload));
                    ajaxLoaderService.postJSON("api/v1/driver/addOrUpdateDriver", JSON.stringify(payload), function (res) {
                        alertify.success('document successfully added');
                        getDriversList();
                    }, function (er) {
                        alertify.error('error adding document please try after some time');
                    });
                }

                if (elementType == "remove-driver") {
                    var driverId = $(element).attr("id");
                    ajaxLoaderService.post("api/v1/driver/removeDriver", {driverId: driverId}, function (res) {
                        if (res.driverId == driverId) {
                            alertify.success('document successfully removed');
                            getDriversList();
                        } else {
                            alertify.error('error in removing document');
                        }
                    });

                }

                if (elementType == "today-rides") {
                    showHideMain();
                    todaysRides();
                }

                if (elementType == "remove-ride") {
                    var rideId = $(element).attr("id");
                    console.log(rideId);
                    ajaxLoaderService.post("api/v1/admin/removeRide", {rideId: rideId}, function (res) {
                        if (res.rideId == rideId) {
                            alertify.success('document successfully removed');
                            todaysRides();
                        } else {
                            alertify.error('error in removing document');
                        }
                    });
                }

                if (elementType == "show-create-ride") {
                    $(moduleEl).find("#add-ride-template").show();
                    $.get("/api/v1/driver/getAllDriverDetails", {}, function (res) {
                        var data = {};
                        data.drivers = res
                        $.get("/api/v1/employee/getAllEmployeeDetails", {}, function (res) {
                            data.users = res;

                            $(moduleEl).find("#add-ride-template").html(templateLoaderService.compile($(templateData).find("#show-in-todays-ride").html(), data));
                            $('.ui.sidebar')
                                .sidebar({
                                    context: $('.bottom.segment')
                                })
                                .sidebar('attach events', '.demo.menu')
                            ;
                            $('.ui.dropdown')
                                .dropdown({
                                    allowAdditions: true
                                })
                            ;
                        });

                    });
                }

                if (elementType == "add-ride") {
                    var payload = {};
                    payload.driverId = $(moduleEl).find("#users option:selected").val();
                    payload.batch = $(moduleEl).find("#time option:selected").val();
                    payload.employeeList = $(moduleEl).find("#names").val().split(',').map(function (item) {
                        return parseInt(item, 10);
                    });
                    payload.rideType = $(moduleEl).find("#pickOrDrop option:selected").val();
                    console.log(payload);
                    ajaxLoaderService.postJSON("/api/v1/admin/addOrUpdateRide", JSON.stringify(payload), function (res) {
                        alertify.success('document successfully added');
                        todaysRides();
                    }, function (er) {
                        alertify.error('error adding document please try after some time');
                    });
                }

                if (elementType == "history") {
                    showHideMain();
                    history();
                }

                if (elementType == "remove-group") {
                    var driverId = $(element).attr("id");
                    ajaxLoaderService.post("/api/v1/admin/removeGlobalRideGroupsTemplateForDriver", {driverId: driverId}, function (res) {
                        if (res.driverId == driverId) {
                            alertify.success('group document successfully removed');
                            getGroupLists();
                        } else {
                            alertify.error('error removing the group');
                        }

                    });
                }
                if (elementType == "updateRideInfo") {
                    var payload = {};
                    payload.driverId = $(element).attr("driverId");
                    payload.batch = $(element).attr("batch");
                    payload.complete = $(element).attr("complete");
                    payload.date = $(element).attr("date");
                    payload.employeeList = $(moduleEl).find('[name="EmployeeId"]').val().split(',').map(function (item) {
                        return parseInt(item, 10);
                    });
                    payload.rideType = $(element).attr("rideType");
                    ajaxLoaderService.postJSON("/api/v1/admin/addOrUpdateRide", JSON.stringify(payload), function (res) {
                        alertify.success('document successfully added');
                        todaysRides();
                    }, function (er) {
                        alertify.error('error adding document please try after some time');
                    });
                }
                if (elementType == "reschedule-requests") {
                    showHideMain();
                    getRescheduleRequests();
                }
                if (elementType == "ack-request") {
                    var payload = {};
                    payload.date = $(element).attr("date");
                    payload.oldBatch = $(element).attr("oldBatch");
                    payload.newBatch = $(element).attr("newBatch");
                    payload.employeeId = $(element).attr("employeeId");
                    payload.rideType = $(element).attr("rideType");
                    ajaxLoaderService.postJSON("/api/v1/admin/acknowledgeRescheduleRequest",JSON.stringify(payload),function(res){
                        if(res.employeeId == payload.employeeId){
                            alertify.success('updated the document successfully');
                            getRescheduleRequests();
                        }
                    },function(er){
                        alertify.error('error updating the document');
                    });
                }
                if(elementType == "update-groups"){
                    var payload = {};
                    payload.driverId =$(element).attr("driverId");
                    payload.employeeList = $(moduleEl).find('[name="EmployeeId"]').val().split(',').map(function (item) {
                        return parseInt(item, 10);
                    });
                    payload.rideType = $(element).attr("rideType");
                    payload.batch=$(element).attr("batch");
                    ajaxLoaderService.postJSON("/api/v1/admin/addOrUpdateGlobalRideGroupsTemplateForDriver", JSON.stringify(payload), function (res) {
                        alertify.success('document successfully added');
                        getGroupLists();
                    }, function (er) {
                        alertify.error('error adding document please try after some time');
                    });
                }
                if(elementType == "update-users"){
                   var payload = {};
                    payload.name = $(moduleEl).find('[name="EmployeeName"]').val() ? $(moduleEl).find('[name="EmployeeName"]').val() : $(element).attr("name");
                    payload.employeeId =$(element).attr("employeeId");
                    payload.phoneNumber = $(moduleEl).find('[name="number"]').val() ? $(moduleEl).find('[name="number"]').val() : $(element).attr("phoneNumber");
                    payload.address = $(moduleEl).find('[name="Address"]').val() ? $(moduleEl).find('[name="Address"]').val() : $(element).attr("address");
                    payload.password = "sprinklr";
                    payload.team = $(moduleEl).find('[name="team"]').val() ? $(moduleEl).find('[name="team"]').val() : $(element).attr("team");
                    ajaxLoaderService.postJSON("api/v1/employee/addOrUpdateEmployee", JSON.stringify(payload), function (res) {
                        alertify.success('document successfully added');
                        getUserList();
                    }, function (er) {
                        alertify.error('error adding document please try after some time');
                    });
                }
                if(elementType == "update-driver"){
                    var payload = {};
                    payload.driverId = $(element).attr("driverId");
                    payload.phoneNumber = $(moduleEl).find('[name="drivernumber"]').val()? $(moduleEl).find('[name="drivernumber"]').val() :  $(element).attr("phoneNumber");
                    payload.carNumber = $(moduleEl).find('[name="carnumber"]').val()? $(moduleEl).find('[name="carnumber"]').val() : $(element).attr("carNumber");
                    payload.name = $(moduleEl).find('[name="drivername"]').val()?$(moduleEl).find('[name="drivername"]').val() : $(element).attr("driverName");
                    payload.password = 'sprinklr';
                    ajaxLoaderService.postJSON("api/v1/driver/addOrUpdateDriver", JSON.stringify(payload), function (res) {
                        alertify.success('document successfully added');
                        getDriversList();
                    }, function (er) {
                        alertify.error('error adding document please try after some time');
                    }); 
                }
                if(elementType == "statistics"){
                    ajaxLoaderService.get("/api/v1/statistics/getAllTotalTravelTimeForToday",{},function(res){
                        console.log(res);
                         Highcharts.chart('container', {
                        chart: {
                            type: 'column'
                        },
                        title: {
                            text: 'Total Travel time for today'
                        },
                        subtitle: {
                        },
                        xAxis: {
                            type: 'category',
                            labels: {
                                rotation: -45,
                                style: {
                                    fontSize: '13px',
                                    fontFamily: 'Verdana, sans-serif'
                                }
                            }
                        },
                        yAxis: {
                            min: 0,
                            title: {
                                text: 'Time Taken (minutes)'
                            }
                        },
                        legend: {
                            enabled: false
                        },
                        tooltip: {
                            pointFormat: 'Time taken <b>{point.y:.1f} minutes</b>'
                        },
                        series: [{
                            name: 'Population',
                            data: res,
                            dataLabels: {
                                enabled: true,
                                rotation: -90,
                                color: '#FFFFFF',
                                align: 'right',
                                format: '{point.y:.1f}', // one decimal
                                y: 10, // 10 pixels down from the top
                                style: {
                                    fontSize: '13px',
                                    fontFamily: 'Verdana, sans-serif'
                                }
                            }
                        }]
                    });
                    $(moduleEl).find("#create-group").hide();
                    $(moduleEl).find("#container").show();
                    });
                   
                }
            }
        };
    });
})();
