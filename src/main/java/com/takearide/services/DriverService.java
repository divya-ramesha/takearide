package com.takearide.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.takearide.pojo.Driver;
import com.takearide.pojo.Employee;
import com.takearide.pojo.Ride;
import com.takearide.repositories.DriverRepository;
import com.takearide.repositories.EmployeeRepository;
import com.takearide.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by divya-r on 17/12/16.
 */

@Service
public class DriverService {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    RideRepository rideRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    public Driver addOrUpdateDriver(Map<String, Object> driverDetails) {
        String name = null;
        Integer driverId = null;
        String phoneNumber = null;
        String password = null;
        Map<String, Object> driverProperties = null;
        String carNumber = null;

        for (Map.Entry<String, Object> keyValuePair : driverDetails.entrySet()) {
            switch (keyValuePair.getKey()) {
                case "name":
                    name = (String) keyValuePair.getValue();
                    break;
                case "driverId":
                    driverId = Integer.parseInt(keyValuePair.getValue().toString());
                    break;
                case "phoneNumber":
                    phoneNumber = (String) keyValuePair.getValue();
                    break;
                case "password":
                    password = (String) keyValuePair.getValue();
                    break;
                case "carNumber":
                    carNumber = (String) keyValuePair.getValue();
                    break;
                default:
                    if (driverProperties == null) {
                        driverProperties = Maps.newHashMap();
                    }
                    driverProperties.put(keyValuePair.getKey(), keyValuePair.getValue());
                    break;
            }
        }

        Driver driver = new Driver(name, driverId, phoneNumber, password, driverProperties, carNumber);
        driverRepository.save(driver);

        return driver;
    }

    public Driver removeDriver(Integer driverId) {
        Driver driver = driverRepository.findByDriverId(driverId);
        driverRepository.delete(driverId);
        return driver;
    }

    public Driver getDriver(Integer driverId) {
        Driver driver = driverRepository.findByDriverId(driverId);
        return driver;
    }

    public List<Driver> getAllDriverDetails() {
        return driverRepository.findAll();
    }

    public List<Map<String, Object>> getNextRideInformationForDriver(Integer driverId) throws Exception {
        List<Ride> rides = rideRepository.findByDriverIdAndIsComplete(driverId, false);
        Ride oldestIncompleteRide = getOldestIncompleteRide(rides);
        if (oldestIncompleteRide == null) {
            return Lists.newArrayList();
        }
        List<Integer> employeeIdList = oldestIncompleteRide.getEmployeeIdList();
        List<Map<String, Object>> rideInfo = Lists.newArrayList();
        for (Integer employeeId : employeeIdList) {
            Map<String, Object> employeeInfo = Maps.newHashMap();
            Employee employee = employeeRepository.findOne(employeeId);
            employeeInfo.put("employeeName", employee.getName());
            employeeInfo.put("employeeId", employee.getEmployeeId());
            employeeInfo.put("address", employee.getAddress());
            employeeInfo.put("batch", oldestIncompleteRide.getBatch());
            employeeInfo.put("phoneNumber", employee.getPhoneNumber());
            employeeInfo.put("team", employee.getTeam());
            employeeInfo.put("rideId", oldestIncompleteRide.getRideId());
            rideInfo.add(employeeInfo);
        }
        return rideInfo;
    }

    private Ride getOldestIncompleteRide(List<Ride> rides) throws Exception {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Long minRideTime = 99999999999999L;
        Ride oldestIncompleteRide = null;
        for (Ride ride : rides) {
            Date startDate = df.parse(ride.getDate());
            Long rideDate = startDate.getTime();
            if (rideDate <= minRideTime) {
                minRideTime = rideDate;
                oldestIncompleteRide = ride;
            }
        }

        return oldestIncompleteRide;
    }

    public void finishRide(Integer rideId) {
        Ride ride = rideRepository.findOne(rideId);
        ride.setComplete(true);
        ride.setInProgress(false);
        rideRepository.save(ride);
    }

    public void startRide(Integer rideId) {
        Ride ride = rideRepository.findOne(rideId);
        ride.setInProgress(true);
        rideRepository.save(ride);
    }

    public List<Map<String, Object>> getLast15RidesForDriver(Integer driverId) {
        List<Ride> rides = rideRepository.findTop15ByDriverIdOrderByTimestampDesc(driverId);
        List<Map<String, Object>> allRideDetails = Lists.newArrayList();
        for (Ride ride : rides) {
            Map<String, Object> ridesDetails = Maps.newHashMap();
            ridesDetails.put("date", ride.getDate());
            ridesDetails.put("batch", ride.getBatch());
            ridesDetails.put("isComplete", ride.getComplete());

            List<Map<String, String>> allEmployeeDetails = Lists.newArrayList();
            List<Integer> employeeIds = ride.getEmployeeIdList();
            for (Integer employeeId : employeeIds) {
                Map<String, String> employeeDetails = Maps.newHashMap();
                Employee employee = employeeRepository.findOne(employeeId);
                employeeDetails.put("name", employee.getName());
                employeeDetails.put("phoneNumber", employee.getPhoneNumber());
                employeeDetails.put("team", employee.getTeam());
                employeeDetails.put("address", employee.getAddress());

                allEmployeeDetails.add(employeeDetails);
            }
            ridesDetails.put("employeeDetails", allEmployeeDetails);
            allRideDetails.add(ridesDetails);
        }
        return allRideDetails;
    }
}
