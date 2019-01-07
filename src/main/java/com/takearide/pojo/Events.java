package com.takearide.pojo;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * Created by divya-r on 17/12/16.
 */
public class Events implements Serializable {
    private static final long serialVersionUID = -1L;

    @Id
    private String id;

    String eventCategory;
    String eventType;
    Long timestamp;
    Integer rideId;
    Integer acknowledgedBy;
    String ackowledger;
    List<Integer> acknowledgedTo;

    public Events(String eventCategory, String eventType, Long timestamp, Integer rideId, Integer acknowledgedBy, String ackowledger, List<Integer> acknowledgedTo) {
        this.eventCategory = eventCategory;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.rideId = rideId;
        this.acknowledgedBy = acknowledgedBy;
        this.ackowledger = ackowledger;
        this.acknowledgedTo = acknowledgedTo;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public Integer getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public void setAcknowledgedBy(Integer acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }

    public String getAckowledger() {
        return ackowledger;
    }

    public void setAckowledger(String ackowledger) {
        this.ackowledger = ackowledger;
    }

    public List<Integer> getAcknowledgedTo() {
        return acknowledgedTo;
    }

    public void setAcknowledgedTo(List<Integer> acknowledgedTo) {
        this.acknowledgedTo = acknowledgedTo;
    }
}
