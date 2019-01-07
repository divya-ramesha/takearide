package com.takearide.services;

import com.google.common.collect.Lists;
import com.takearide.constants.GlobalConstants;
import com.takearide.pojo.Driver;
import com.takearide.pojo.Ride;
import com.takearide.repositories.DriverRepository;
import com.takearide.repositories.EventsRepository;
import com.takearide.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by divya-r on 18/12/16.
 */

@Service
public class StatisticsService {

    @Autowired
    EventsRepository eventsRepository;

    @Autowired
    RideRepository rideRepository;

    @Autowired
    DriverRepository driverRepository;

    public List<List<Object>> getAllTotalTravelTimeForToday() {

        Date dateTime = new Date();
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String date = sdf.format(dateTime).toString();

        List<Ride> rides = rideRepository.findByDateAndIsComplete(date, true);
        List<List<Object>> allResults = Lists.newArrayList();
        for (Ride ride : rides) {
            List<Object> result = Lists.newArrayList();
            Long startTime = eventsRepository.findByRideIdAndEventCategoryAndEventType(ride.getRideId(), GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.CAB_ARRIVED).getTimestamp();
            Long endTime = eventsRepository.findByRideIdAndEventCategoryAndEventType(ride.getRideId(), GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.DRIVER_END_RIDE).getTimestamp();
            Long timeTaken = endTime - startTime;
            Integer driverId = ride.getDriverId();
            Driver driver = driverRepository.findOne(driverId);

            result.add(driver.getName() + " | " + driver.getCarNumber() + " | " + ride.getRideType());
            result.add(timeTaken / 60);

            allResults.add(result);

        }

        return allResults;
    }
}
