package com.takearide.services;

import com.takearide.pojo.GlobalRideGroupsTemplate;
import com.takearide.pojo.RescheduleRequests;
import com.takearide.pojo.Ride;
import com.takearide.repositories.GlobalRideGroupsTemplateRepository;
import com.takearide.repositories.RescheduleRequestsRepository;
import com.takearide.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by divya-r on 17/12/16.
 */

@Service
public class AdminService {

    @Autowired
    RideRepository rideRepository;

    @Autowired
    GlobalRideGroupsTemplateRepository globalRideGroupsTemplateRepository;

    @Autowired
    RescheduleRequestsRepository rescheduleRequestsRepository;

    public Ride addOrUpdateRide(Map<String, Object> rideDetails) throws Exception {
        Integer driverId = null;
        List<Integer> employeeList = null;
        String date = null;
        Integer rideId = null;
        String batch = null;
        Boolean isComplete = null;
        Boolean inProgress = null;
        Long timestamp = null;
        String rideType = null;

        for (Map.Entry<String, Object> keyValuePair : rideDetails.entrySet()) {
            switch (keyValuePair.getKey()) {
                case "driverId":
                    driverId = Integer.parseInt(keyValuePair.getValue().toString());
                    break;
                case "batch":
                    batch = (String) keyValuePair.getValue();
                    break;
                case "employeeList":
                    employeeList = (List<Integer>) keyValuePair.getValue();
                    break;
                case "rideType":
                    rideType = (String) keyValuePair.getValue();
                    break;
                default:
                    // do nothing
            }
        }

        rideId = getNextRideId();
        isComplete = false;
        inProgress = false;

        Date dateTime = new Date();
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        date = sdf.format(dateTime).toString();

        timestamp = getTimestampForDateAndBatch(date, batch);

        List<Ride> previousRide = rideRepository.findByDriverIdAndDateAndBatchAndRideType(driverId, date, batch, rideType);

        Ride ride;
        if (previousRide.size() == 0) {
            ride = new Ride(driverId, employeeList, date, rideId, batch, isComplete, inProgress, timestamp, rideType);
        } else {
            ride = previousRide.get(0);
            ride.setEmployeeIdList(employeeList);
        }

        rideRepository.save(ride);

        return ride;
    }

    public Long getTimestampForDateAndBatch(String date, String batch) throws Exception {
        String[] timeSplit = batch.split(" ");
        String time = timeSplit[0];
        String timeType = timeSplit[1];

        Integer hours = Integer.parseInt(time.split(":")[0]);
        if (timeType.equals("PM")) {
            hours = hours + 12;
        }
        Integer minutes = Integer.parseInt(time.split(":")[1]);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = df.parse(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        cal.add(Calendar.MINUTE, minutes);
        Long timestamp = cal.getTime().getTime() / 1000;

        return timestamp;
    }

    public Ride removeRide(Integer rideId) {
        Ride ride = rideRepository.findByRideId(rideId);
        rideRepository.delete(rideId);
        return ride;
    }

    public Ride getRide(Integer rideId) {
        Ride ride = rideRepository.findByRideId(rideId);
        return ride;
    }

    private Integer getNextRideId() {
        Ride maxRide = rideRepository.findTopByOrderByRideIdDesc();
        Integer maxRideId;
        if (maxRide == null) {
            maxRideId = 1;
        } else {
            maxRideId = maxRide.getRideId();
            maxRideId++;
        }
        return maxRideId;
    }


    public GlobalRideGroupsTemplate addOrUpdateGlobalRideGroupsTemplateForDriver(Map<String, Object> templateDetails) {
        Integer driverId = null;
        List<Integer> employeeIdList = null;
        String batch = null;
        String rideType = null;

        for (Map.Entry<String, Object> keyValuePair : templateDetails.entrySet()) {
            switch (keyValuePair.getKey()) {
                case "driverId":
                    driverId = Integer.parseInt(keyValuePair.getValue().toString());
                    break;
                case "batch":
                    batch = (String) keyValuePair.getValue();
                    break;
                case "employeeList":
                    employeeIdList = (List<Integer>) keyValuePair.getValue();
                    break;
                case "rideType":
                    rideType = (String) keyValuePair.getValue();
                    break;
                default:
                    // do nothing
            }
        }


        List<GlobalRideGroupsTemplate> previousRide = globalRideGroupsTemplateRepository.findByDriverId(driverId);

        GlobalRideGroupsTemplate globalRideGroupsTemplate;
        if (previousRide.size() == 0) {
            globalRideGroupsTemplate = new GlobalRideGroupsTemplate(driverId, employeeIdList, batch, rideType);
        } else {
            globalRideGroupsTemplate = previousRide.get(0);
            globalRideGroupsTemplate.setEmployeeIdList(employeeIdList);
        }

        globalRideGroupsTemplateRepository.save(globalRideGroupsTemplate);

        return globalRideGroupsTemplate;
    }

    public GlobalRideGroupsTemplate removeGlobalRideGroupsTemplateForDriver(Integer driverId) {
        List<GlobalRideGroupsTemplate> globalRideGroupsTemplate = globalRideGroupsTemplateRepository.findByDriverId(driverId);
        globalRideGroupsTemplateRepository.delete(globalRideGroupsTemplate.get(0));
        return globalRideGroupsTemplate.get(0);
    }

    public GlobalRideGroupsTemplate getGlobalRideGroupsTemplateForDriver(Integer driverId) {
        List<GlobalRideGroupsTemplate> globalRideGroupsTemplate = globalRideGroupsTemplateRepository.findByDriverId(driverId);
        return globalRideGroupsTemplate.get(0);
    }

    public List<GlobalRideGroupsTemplate> getAllGlobalRideGroupsTemplates() {
        return globalRideGroupsTemplateRepository.findAll();
    }

    public List<Ride> getAllRidesForToday() {
        Date dateTime = new Date();
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String dateString = sdf.format(dateTime).toString();

        return rideRepository.findByDate(dateString);
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public RescheduleRequests acknowledgeRescheduleRequest(Map<String, Object> rescheduleRequestsDetails) {
        String rideType = (String) rescheduleRequestsDetails.get("rideType");
        String oldBatch = (String) rescheduleRequestsDetails.get("oldBatch");
        String newBatch = (String) rescheduleRequestsDetails.get("newBatch");
        Integer employeeId = Integer.parseInt(rescheduleRequestsDetails.get("employeeId").toString());
        String date = (String) rescheduleRequestsDetails.get("date");

        List<RescheduleRequests> rescheduleRequests = rescheduleRequestsRepository.findByRideTypeAndOldBatchAndNewBatchAndDateAndEmployeeId(rideType, oldBatch, newBatch, date, employeeId);
        RescheduleRequests rescheduleRequest = rescheduleRequests.get(0);
        rescheduleRequest.setAcknowledged(true);
        rescheduleRequestsRepository.save(rescheduleRequest);
        return rescheduleRequest;
    }

    public List<RescheduleRequests> getAllUnacknowledgedRescheduleRequests() {
        return rescheduleRequestsRepository.findByAcknowledged(false);
    }

}
