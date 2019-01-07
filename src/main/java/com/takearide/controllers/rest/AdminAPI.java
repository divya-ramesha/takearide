package com.takearide.controllers.rest;

import com.google.common.collect.Maps;
import com.takearide.constants.GlobalConstants;
import com.takearide.pojo.*;
import com.takearide.repositories.RideRepository;
import com.takearide.services.AdminService;
import com.takearide.services.DriverService;
import com.takearide.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by divya-r on 17/12/16.
 */

@RestController
@RequestMapping(GlobalConstants.Api.ENDPOINT_API_CONTEXT + GlobalConstants.Api.ENDPOINT_API_VERSION + "/admin")
public class AdminAPI {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    DriverService driverService;

    @Autowired
    AdminService adminService;

    @Autowired
    RideRepository rideRepository;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() throws Exception {
        return "test-admin";
    }

    @RequestMapping(value = "/addOrUpdateRide", method = RequestMethod.POST)
    public Ride addOrUpdateRide(@RequestBody Map<String, Object> rideDetails) throws Exception {
        return adminService.addOrUpdateRide(rideDetails);
    }

    @RequestMapping(value = "/removeRide", method = RequestMethod.POST)
    public Ride removeRide(@RequestParam(value = "rideId") Integer rideId) throws Exception {
        return adminService.removeRide(rideId);
    }

    @RequestMapping(value = "/getRide", method = RequestMethod.GET)
    public Ride getRide(@RequestParam(value = "rideId") Integer rideId) throws Exception {
        return adminService.getRide(rideId);
    }

    @RequestMapping(value = "/addOrUpdateGlobalRideGroupsTemplateForDriver", method = RequestMethod.POST)
    public GlobalRideGroupsTemplate addOrUpdateGlobalRideGroupsTemplateForDriver(@RequestBody Map<String, Object> rideDetails) throws Exception {
        return adminService.addOrUpdateGlobalRideGroupsTemplateForDriver(rideDetails);
    }

    @RequestMapping(value = "/removeGlobalRideGroupsTemplateForDriver", method = RequestMethod.POST)
    public GlobalRideGroupsTemplate removeGlobalRideGroupsTemplateForDriver(@RequestParam(value = "driverId") Integer driverId) throws Exception {
        return adminService.removeGlobalRideGroupsTemplateForDriver(driverId);
    }

    @RequestMapping(value = "/getGlobalRideGroupsTemplateForDriver", method = RequestMethod.GET)
    public GlobalRideGroupsTemplate getGlobalRideGroupsTemplateForDriver(@RequestParam(value = "driverId") Integer driverId) throws Exception {
        return adminService.getGlobalRideGroupsTemplateForDriver(driverId);
    }

    @RequestMapping(value = "/getEmployeeAndDriverDetails", method = RequestMethod.GET)
    public Map<String, Object> getEmployeeAndDriverDetails() throws Exception {
        List<Employee> employees = employeeService.getAllEmployeeDetails();
        List<Driver> drivers = driverService.getAllDriverDetails();
        Map<String, Object> allDetails = Maps.newHashMap();
        allDetails.put("employeeDetails", employees);
        allDetails.put("driverDetails", drivers);
        return allDetails;
    }

    @RequestMapping(value = "/getAllGlobalRideGroupsTemplates", method = RequestMethod.GET)
    public List<GlobalRideGroupsTemplate> getAllGlobalRideGroupsTemplates() throws Exception {
        return adminService.getAllGlobalRideGroupsTemplates();
    }

    @RequestMapping(value = "/getAllRidesForToday", method = RequestMethod.GET)
    public List<Ride> getAllRidesForToday() throws Exception {
        return adminService.getAllRidesForToday();
    }

    @RequestMapping(value = "/getAllRides", method = RequestMethod.GET)
    public List<Ride> getAllRides() throws Exception {
        return adminService.getAllRides();
    }

    @RequestMapping(value = "/acknowledgeRescheduleRequest", method = RequestMethod.POST)
    public RescheduleRequests acknowledgeRescheduleRequest(@RequestBody Map<String, Object> rescheduleRequestsDetails) throws Exception {
        return adminService.acknowledgeRescheduleRequest(rescheduleRequestsDetails);
    }

    @RequestMapping(value = "/getAllUnacknowledgedRescheduleRequests", method = RequestMethod.GET)
    public List<RescheduleRequests> getAllUnacknowledgedRescheduleRequests() throws Exception {
        return adminService.getAllUnacknowledgedRescheduleRequests();
    }

}
