package com.takearide.pojo;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * Created by divya-r on 17/12/16.
 */
public class GlobalRideGroupsTemplate implements Serializable {
    private static final long serialVersionUID = -1L;

    @Id
    private String id;

    Integer driverId;
    List<Integer> employeeIdList;
    String batch;
    String rideType;

    public GlobalRideGroupsTemplate(Integer driverId, List<Integer> employeeIdList, String batch, String rideType) {
        this.driverId = driverId;
        this.employeeIdList = employeeIdList;
        this.batch = batch;
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

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getRideType() {
        return rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }
}
