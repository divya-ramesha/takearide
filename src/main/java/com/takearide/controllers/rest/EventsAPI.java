package com.takearide.controllers.rest;

import com.google.common.collect.Lists;
import com.takearide.constants.GlobalConstants;
import com.takearide.pojo.Events;
import com.takearide.pojo.Ride;
import com.takearide.repositories.EventsRepository;
import com.takearide.repositories.RideRepository;
import com.takearide.services.DriverService;
import com.takearide.services.EmployeeService;
import com.takearide.services.EventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by divya-r on 17/12/16.
 */

@RestController
@RequestMapping(GlobalConstants.Api.ENDPOINT_API_CONTEXT + GlobalConstants.Api.ENDPOINT_API_VERSION + "/events")
public class EventsAPI {

    @Autowired
    EventsRepository eventsRepository;

    @Autowired
    EventsService eventsService;

    @Autowired
    DriverService driverService;

    @Autowired
    RideRepository rideRepository;

    @Autowired
    EmployeeService employeeService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() throws Exception {
        return "test-event";
    }

    @RequestMapping(value = "/driver/cabArrived", method = RequestMethod.POST)
    public Events cabArrived(@RequestParam(value = "rideId") Integer rideId,
                             @RequestParam(value = "acknowledgedBy") Integer acknowledgedBy) throws Exception {
        driverService.startRide(rideId);
        List<Integer> ackowledgedTo = rideRepository.findOne(rideId).getEmployeeIdList();
        return eventsService.addEvent(rideId, acknowledgedBy, GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.CAB_ARRIVED, ackowledgedTo);
    }

    @RequestMapping(value = "/employee/startRideAck", method = RequestMethod.POST)
    public Events employeeStartRideAck(@RequestParam(value = "acknowledgedBy") Integer acknowledgedBy) throws Exception {
        Ride ride = employeeService.getOldestIncompleteRideForEmployee(acknowledgedBy);
        List<Integer> ackowledgedTo = Lists.newArrayList(ride.getDriverId());
        return eventsService.addEvent(ride.getRideId(), acknowledgedBy, GlobalConstants.EventCategories.EMPLOYEE_EVENT, GlobalConstants.EventTypes.EMPLOYEE_START_RIDE_ACK, ackowledgedTo);
    }

    @RequestMapping(value = "/driver/startRideAck", method = RequestMethod.POST)
    public Events driverStartRideAck(@RequestParam(value = "rideId") Integer rideId,
                                     @RequestParam(value = "acknowledgedBy") Integer acknowledgedBy) throws Exception {
        Ride ride = rideRepository.findOne(rideId);
        List<Integer> ackowledgedTo = ride.getEmployeeIdList();
        return eventsService.addEvent(ride.getRideId(), acknowledgedBy, GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.DRIVER_START_RIDE, ackowledgedTo);
    }

    @RequestMapping(value = "/employee/dropRideAck", method = RequestMethod.POST)
    public Events employeeDropRideAck(@RequestParam(value = "acknowledgedBy") Integer acknowledgedBy) throws Exception {
        Ride ride = employeeService.getOldestIncompleteRideForEmployee(acknowledgedBy);
        List<Integer> ackowledgedTo = Lists.newArrayList(ride.getDriverId());
        return eventsService.addEvent(ride.getRideId(), acknowledgedBy, GlobalConstants.EventCategories.EMPLOYEE_EVENT, GlobalConstants.EventTypes.EMPLOYEE_DROP_ACK, ackowledgedTo);
    }

    @RequestMapping(value = "/driver/dropRideAck", method = RequestMethod.POST)
    public Events driverDropRideAck(@RequestParam(value = "rideId") Integer rideId,
                                    @RequestParam(value = "acknowledgedBy") Integer acknowledgedBy,
                                    @RequestParam(value = "acknowledgedTo") Integer employeeId) throws Exception {
        List<Integer> ackowledgedTo = Lists.newArrayList();
        ackowledgedTo.add(employeeId);
        return eventsService.addEvent(rideId, acknowledgedBy, GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.DRIVER_ACK_EMPLOYEE_DROP, ackowledgedTo);
    }

    @RequestMapping(value = "/driver/endRide", method = RequestMethod.POST)
    public Events endRide(@RequestParam(value = "rideId") Integer rideId,
                          @RequestParam(value = "acknowledgedBy") Integer acknowledgedBy) throws Exception {
        driverService.finishRide(rideId);
        return eventsService.addEvent(rideId, acknowledgedBy, GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.DRIVER_END_RIDE, Lists.newArrayList());
    }

    @RequestMapping(value = "/getLast10AllEvents", method = RequestMethod.GET)
    public List<Events> getLast10Events() throws Exception {
        return eventsService.getLast10Events();
    }

    @RequestMapping(value = "/getLast10DriverEvents", method = RequestMethod.GET)
    public List<Events> getLast10DriverEvents(@RequestParam(value = "driverId") Integer driverId) throws Exception {
        return eventsService.getLast10DriverEvents(driverId);
    }

    @RequestMapping(value = "/getLast10EmployeeEvents", method = RequestMethod.GET)
    public List<Events> getLast10EmployeeEvents(@RequestParam(value = "employeeId") Integer employeeId) throws Exception {
        return eventsService.getLast10EmployeeEvents(employeeId);
    }

    @RequestMapping(value = "/getLast10RideEvents", method = RequestMethod.GET)
    public List<Events> getLast10RideEvents(@RequestParam(value = "rideId") Integer rideId) throws Exception {
        return eventsService.getLast10RideEvents(rideId);
    }

    @RequestMapping(value = "/employee/checkIfCabHasArrived", method = RequestMethod.GET)
    public Boolean checkIfCabHasArrived(@RequestParam(value = "employeeId") Integer employeeId) throws Exception {
        return eventsService.checkIfCabHasArrived(employeeId);
    }

    @RequestMapping(value = "/employee/checkIfEmployeeStartedRide", method = RequestMethod.GET)
    public Boolean checkIfEmployeeStartedRide(@RequestParam(value = "employeeId") Integer employeeId) throws Exception {
        return eventsService.checkIfEmployeeStartedRide(employeeId);
    }

    @RequestMapping(value = "/driver/checkIfDriverCanAnnounceCabArrival", method = RequestMethod.GET)
    public Boolean checkIfDriverCanAnnounceCabArrival(@RequestParam(value = "driverId") Integer driverId) throws Exception {
        return eventsService.checkIfDriverCanAnnounceCabArrival(driverId);
    }

    @RequestMapping(value = "/driver/checkIfDriverCanStartCab", method = RequestMethod.GET)
    public Boolean checkIfDriverCanStartCab(@RequestParam(value = "driverId") Integer driverId) throws Exception {
        return eventsService.checkIfDriverCanStartCab(driverId);
    }

    @RequestMapping(value = "/driver/checkIfDriverCanAckEmployeeDrop", method = RequestMethod.GET)
    public Boolean checkIfDriverCanAckEmployeeDrop(@RequestParam(value = "driverId") Integer driverId,
                                                   @RequestParam(value = "employeeId") Integer employeeId) throws Exception {
        return eventsService.checkIfDriverCanAckEmployeeDrop(driverId, employeeId);
    }

    @RequestMapping(value = "/driver/checkIfDriverCanEndRide", method = RequestMethod.GET)
    public Boolean checkIfDriverCanEndRide(@RequestParam(value = "driverId") Integer driverId) throws Exception {
        return eventsService.checkIfDriverCanEndRide(driverId);
    }
}
