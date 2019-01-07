package com.takearide.pojo;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * Created by divya-r on 17/12/16.
 */
public class Ride implements Serializable {
    private static final long serialVersionUID = -1L;

    Integer driverId;
    List<Integer> employeeIdList;
    String date;
    @Id
    Integer rideId;
    String batch;
    Boolean isComplete;
    Boolean inProgress;
    Long timestamp;
    String rideType;

    public Ride(Integer driverId, List<Integer> employeeIdList, String date, Integer rideId, String batch, Boolean isComplete, Boolean inProgress, Long timestamp, String rideType) {
        this.driverId = driverId;
        this.employeeIdList = employeeIdList;
        this.date = date;
        this.rideId = rideId;
        this.batch = batch;
        this.isComplete = isComplete;
        this.inProgress = inProgress;
        this.timestamp = timestamp;
        this.rideType = rideType;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public List<Integer> getEmployeeIdList() {
        return employeeIdList;
    }

    public void setEmployeeIdList(List<Integer> employeeIdList) {
        this.employeeIdList = employeeIdList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Boolean getComplete() {
        return isComplete;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }

    public Boolean getInProgress() {
        return inProgress;
    }

    public void setInProgress(Boolean inProgress) {
        this.inProgress = inProgress;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRideType() {
        return rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }
}
