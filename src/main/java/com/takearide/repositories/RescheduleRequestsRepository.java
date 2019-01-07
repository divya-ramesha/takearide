package com.takearide.repositories;

import com.takearide.pojo.RescheduleRequests;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by divya-r on 18/12/16.
 */

@Repository
public interface RescheduleRequestsRepository extends MongoRepository<RescheduleRequests, String> {

    List<RescheduleRequests> findByAcknowledged(Boolean ackowledged);

    List<RescheduleRequests> findByAcknowledgedAndEmployeeId(Boolean acknowlegded, Integer employeeId);

    List<RescheduleRequests> findByRideTypeAndOldBatchAndNewBatchAndDateAndEmployeeId(String rideType, String oldBatch, String newBatch, String date, Integer employeeId);

}