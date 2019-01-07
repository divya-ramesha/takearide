package com.takearide.pojo;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * Created by divya-r on 18/12/16.
 */
public class RescheduleRequests implements Serializable {
    private static final long serialVersionUID = -1L;

    @Id
    private String id;

    String rideType;
    String oldBatch;
    String newBatch;
    Integer employeeId;
    Boolean acknowledged;
    String date;

    public RescheduleRequests(String rideType, String oldBatch, String newBatch, Integer employeeId, Boolean acknowledged, String date) {
        this.rideType = rideType;
        this.oldBatch = oldBatch;
        this.newBatch = newBatch;
        this.employeeId = employeeId;
        this.acknowledged = acknowledged;
        this.date = date;
    }

    public String getRideType() {
        return rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }

    public String getOldBatch() {
        return oldBatch;
    }

    public void setOldBatch(String oldBatch) {
        this.oldBatch = oldBatch;
    }

    public String getNewBatch() {
        return newBatch;
    }

    public void setNewBatch(String newBatch) {
        this.newBatch = newBatch;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Boolean getAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(Boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
