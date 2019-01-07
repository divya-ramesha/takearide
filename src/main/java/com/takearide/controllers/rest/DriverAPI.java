package com.takearide.controllers.rest;

import com.takearide.constants.GlobalConstants;
import com.takearide.pojo.Driver;
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
@RequestMapping(GlobalConstants.Api.ENDPOINT_API_CONTEXT + GlobalConstants.Api.ENDPOINT_API_VERSION + "/driver")
public class DriverAPI {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    DriverService driverService;

    @Autowired
    AdminService adminService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() throws Exception {
        return "test-driver";
    }

    @RequestMapping(value = "/addOrUpdateDriver", method = RequestMethod.POST)
    public Driver addOrUpdateDriver(@RequestBody Map<String, Object> driverDetails) throws Exception {
        return driverService.addOrUpdateDriver(driverDetails);
    }

    @RequestMapping(value = "/removeDriver", method = RequestMethod.POST)
    public Driver removeDriver(@RequestParam(value = "driverId") Integer driverId) throws Exception {
        return driverService.removeDriver(driverId);
    }

    @RequestMapping(value = "/getDriver", method = RequestMethod.GET)
    public Driver getDriver(@RequestParam(value = "driverId") Integer driverId) throws Exception {
        return driverService.getDriver(driverId);
    }

    @RequestMapping(value = "/getAllDriverDetails", method = RequestMethod.GET)
    public List<Driver> getAllDriverDetails() throws Exception {
        return driverService.getAllDriverDetails();
    }

    @RequestMapping(value = "/getNextRideInformationForDriver", method = RequestMethod.GET)
    public List<Map<String, Object>> getNextRideInformationForDriver(@RequestParam(value = "driverId") Integer driverId) throws Exception {
        return driverService.getNextRideInformationForDriver(driverId);
    }

    @RequestMapping(value = "/getLast15RidesForDriver", method = RequestMethod.GET)
    public List<Map<String, Object>> getLast15RidesForDriver(@RequestParam(value = "driverId") Integer driverId) throws Exception {
        return driverService.getLast15RidesForDriver(driverId);
    }
}
