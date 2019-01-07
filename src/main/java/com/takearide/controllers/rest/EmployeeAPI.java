package com.takearide.controllers.rest;

import com.takearide.constants.GlobalConstants;
import com.takearide.pojo.Employee;
import com.takearide.pojo.RescheduleRequests;
import com.takearide.pojo.Ride;
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
@RequestMapping(GlobalConstants.Api.ENDPOINT_API_CONTEXT + GlobalConstants.Api.ENDPOINT_API_VERSION + "/employee")
public class EmployeeAPI {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    DriverService driverService;

    @Autowired
    AdminService adminService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() throws Exception {
        return "test-employee";
    }

    @RequestMapping(value = "/addOrUpdateEmployee", method = RequestMethod.POST)
    public Employee addOrUpdateEmployee(@RequestBody Map<String, Object> employeeDetails) throws Exception {
        return employeeService.addOrUpdateEmployee(employeeDetails);
    }

    @RequestMapping(value = "/removeEmployee", method = RequestMethod.POST)
    public Employee removeEmployee(@RequestParam(value = "employeeId") Integer employeeId) throws Exception {
        return employeeService.removeEmployee(employeeId);
    }

    @RequestMapping(value = "/getEmployee", method = RequestMethod.GET)
    public Employee getEmployee(@RequestParam(value = "employeeId") Integer employeeId) throws Exception {
        return employeeService.getEmployee(employeeId);
    }

    @RequestMapping(value = "/getAllEmployeeDetails", method = RequestMethod.GET)
    public List<Employee> getAllEmployeeDetails() throws Exception {
        return employeeService.getAllEmployeeDetails();
    }

    @RequestMapping(value = "/getEmployeeRideDetails", method = RequestMethod.GET)
    public List<Map<String, Object>> getEmployeeRideDetails(@RequestParam(value = "employeeId") Integer employeeId) throws Exception {
        return employeeService.getEmployeeRideDetails(employeeId);
    }

    @RequestMapping(value = "/addRescheduleRequest", method = RequestMethod.POST)
    public RescheduleRequests addRescheduleRequest(@RequestBody Map<String, Object> rescheduleRequestsDetails) throws Exception {
        return employeeService.addRescheduleRequest(rescheduleRequestsDetails);
    }

    @RequestMapping(value = "/getPendingRescheduleRequestForEmployee", method = RequestMethod.GET)
    public List<RescheduleRequests> getPendingRescheduleRequestForEmployee(@RequestParam(value = "employeeId") Integer employeeId) throws Exception {
        return employeeService.getPendingRescheduleRequestForEmployee(employeeId);
    }

    @RequestMapping(value = "/cancelRide", method = RequestMethod.POST)
    public Ride cancelRide(@RequestParam(value = "employeeId") Integer employeeId,
                           @RequestParam(value = "rideId") Integer rideId) throws Exception {
        return employeeService.cancelRide(employeeId, rideId);
    }

}
