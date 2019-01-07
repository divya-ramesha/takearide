package com.takearide.repositories;

import com.takearide.pojo.Events;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by divya-r on 17/12/16.
 */

@Repository
public interface EventsRepository extends MongoRepository<Events, String> {

    List<Events> findByRideId(Integer rideId);

    List<Events> findTop10ByOrderByTimestampDesc();

    List<Events> findTop10ByAcknowledgedByOrderByTimestampDesc(Integer acknowledgedBy);

    List<Events> findTop10ByRideIdOrderByTimestampDesc(Integer rideId);

    Events findByRideIdAndEventCategoryAndEventType(Integer rideId, String eventCategory, String eventType);

    Integer countByRideIdAndEventCategoryAndEventType(Integer rideId, String eventCategory, String eventType);

    Events findByRideIdAndEventCategoryAndEventTypeAndAcknowledgedBy(Integer rideId, String eventCategory, String eventType, Integer acknowledgedBy);

    Events findByRideIdAndEventCategoryAndEventTypeAndAcknowledgedByAndAcknowledgedTo(Integer rideId, String eventCategory, String eventType, Integer acknowledgedBy, Integer acknowledgedTo);
}
