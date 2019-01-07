package com.takearide.repositories;

import com.takearide.pojo.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by divya-r on 17/12/16.
 */

@Repository
public interface RideRepository extends MongoRepository<Ride, Integer> {

    Ride findByRideId(Integer rideId);

    Ride findTopByOrderByRideIdDesc();

    List<Ride> findByEmployeeIdListOrderByTimestamp(Integer employeeId);

    List<Ride> findByDriverIdAndIsComplete(Integer driverId, Boolean isComplete);

    List<Ride> findByEmployeeIdListAndIsCompleteOrderByTimestamp(Integer employeeId, Boolean isComplete);

    List<Ride> findByDriverIdAndDateAndBatchAndRideType(Integer driverId, String date, String batch, String rideType);

    List<Ride> findByDate(String date);

    List<Ride> findByDateAndIsComplete(String date, Boolean isComplete);

    List<Ride> findTop15ByDriverIdOrderByTimestampDesc(Integer driverId);

    Ride findByEmployeeIdListAndInProgress(Integer employeeId, Boolean inProgress);

    List<Ride> findByDriverIdAndIsCompleteOrderByTimestamp(Integer driverId, Boolean isComplete);

    Ride findByDriverIdAndInProgressAndIsComplete(Integer driverId, Boolean inProgress, Boolean isComplete);
}