package com.takearide.pojo;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by divya-r on 17/12/16.
 */
public class Driver implements Serializable {
    private static final long serialVersionUID = -1L;

    String name;
    @Id
    Integer driverId;
    String phoneNumber;
    String password;
    Map<String, Object> driverProperties;
    String carNumber;

    public Driver(String name, Integer driverId, String phoneNumber, String password, Map<String, Object> driverProperties, String carNumber) {
        this.name = name;
        this.driverId = driverId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.driverProperties = driverProperties;
        this.carNumber = carNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getDriverProperties() {
        return driverProperties;
    }

    public void setDriverProperties(Map<String, Object> driverProperties) {
        this.driverProperties = driverProperties;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }
}
