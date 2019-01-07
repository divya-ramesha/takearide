package com.takearide.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.takearide.pojo.Employee;
import com.takearide.pojo.RescheduleRequests;
import com.takearide.pojo.Ride;
import com.takearide.repositories.DriverRepository;
import com.takearide.repositories.EmployeeRepository;
import com.takearide.repositories.RescheduleRequestsRepository;
import com.takearide.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by divya-r on 17/12/16.
 */

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    RideRepository rideRepository;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    RescheduleRequestsRepository rescheduleRequestsRepository;

    @Autowired
    AdminService adminService;

    public Employee addOrUpdateEmployee(Map<String, Object> employeeDetails) {
        String name = null;
        String team = null;
        Integer employeeId = null;
        String address = null;
        String phoneNumber = null;
        String password = null;
        Map<String, Object> employeeProperties = null;

        for (Map.Entry<String, Object> keyValuePair : employeeDetails.entrySet()) {
            switch (keyValuePair.getKey()) {
                case "name":
                    name = (String) keyValuePair.getValue();
                    break;
                case "team":
                    team = (String) keyValuePair.getValue();
                    break;
                case "employeeId":
                    employeeId = Integer.parseInt(keyValuePair.getValue().toString());
                    break;
                case "address":
                    address = (String) keyValuePair.getValue();
                    break;
                case "phoneNumber":
                    phoneNumber = (String) keyValuePair.getValue();
                    break;
                case "password":
                    password = (String) keyValuePair.getValue();
                    break;
                default:
                    if (employeeProperties == null) {
                        employeeProperties = Maps.newHashMap();
                    }
                    employeeProperties.put(keyValuePair.getKey(), keyValuePair.getValue());
                    break;
            }
        }

        Employee employee = new Employee(name, team, employeeId, address, phoneNumber, password, employeeProperties);
        employeeRepository.save(employee);

        return employee;
    }

    public Employee removeEmployee(Integer employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        employeeRepository.delete(employeeId);
        return employee;
    }

    public Employee getEmployee(Integer employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        return employee;
    }

    public List<Employee> getAllEmployeeDetails() {
        return employeeRepository.findAll();
    }

    public List<Map<String, Object>> getEmployeeRideDetails(Integer employeeId) {
        List<Ride> rides = rideRepository.findByEmployeeIdListOrderByTimestamp(employeeId);
        List<Map<String, Object>> allRideDetails = Lists.newArrayList();
        for (Ride ride : rides) {
            Map<String, Object> rideDetails = Maps.newHashMap();
            Integer driverId = ride.getDriverId();
            String cabNumber = driverRepository.findOne(driverId).getCarNumber();
            String batch = ride.getBatch();
            String[] splitDate = ride.getDate().split("/");
            rideDetails.put("batch", batch);
            rideDetails.put("cabNumber", cabNumber);
            rideDetails.put("date", splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0]);
            rideDetails.put("rideId", ride.getRideId());
            rideDetails.put("rideType", ride.getRideType());
            allRideDetails.add(rideDetails);
        }
        return allRideDetails;
    }

    public Ride getOldestIncompleteRideForEmployee(Integer employeeId) throws Exception {
        List<Ride> rides = rideRepository.findByEmployeeIdListAndIsCompleteOrderByTimestamp(employeeId, false);
        if (rides.size() == 0) {
            return null;
        }
        return rides.get(0);
    }

    public RescheduleRequests addRescheduleRequest(Map<String, Object> rescheduleRequestsDetails) {
        String rideType = null;
        String oldBatch = null;
        String newBatch = null;
        Integer employeeId = null;
        Boolean acknowledged = null;
        String date = null;
        for (Map.Entry<String, Object> keyValuePair : rescheduleRequestsDetails.entrySet()) {
            switch (keyValuePair.getKey()) {
                case "rideType":
                    rideType = (String) keyValuePair.getValue();
                    break;
                case "oldBatch":
                    oldBatch = (String) keyValuePair.getValue();
                    break;
                case "newBatch":
                    newBatch = (String) keyValuePair.getValue();
                    break;
                case "date":
                    date = (String) keyValuePair.getValue();
                    break;
                case "employeeId":
                    employeeId = Integer.parseInt(keyValuePair.getValue().toString());
                    break;
                default:
                    // do nothing
            }
        }

        acknowledged = false;

        RescheduleRequests rescheduleRequest = new RescheduleRequests(rideType, oldBatch, newBatch, employeeId, acknowledged, date);
        rescheduleRequestsRepository.save(rescheduleRequest);

        return rescheduleRequest;
    }

    public List<RescheduleRequests> getPendingRescheduleRequestForEmployee(Integer employeeId) {
        return rescheduleRequestsRepository.findByAcknowledgedAndEmployeeId(false, employeeId);
    }

    public Ride cancelRide(Integer employeeId, Integer rideId) {
        Ride ride = rideRepository.findOne(rideId);
        List<Integer> employeeList = ride.getEmployeeIdList();
        List<Integer> newEmployeeList = Lists.newArrayList();
        for (Integer emp : employeeList) {
            if (emp == employeeId) {
                continue;
            }
            newEmployeeList.add(emp);
        }
        ride.setEmployeeIdList(newEmployeeList);
        if (ride.getEmployeeIdList().size() == 0) {
            rideRepository.delete(ride);
        } else {
            rideRepository.save(ride);
        }

        return ride;
    }
}
