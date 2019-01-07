package com.takearide.pojo;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by divya-r on 17/12/16.
 */
public class Employee implements Serializable {
    private static final long serialVersionUID = -1L;

    String name;
    String team;
    @Id
    Integer employeeId;
    String address;
    String phoneNumber;
    String password;
    Map<String, Object> employeeProperties;

    public Employee(String name, String team, Integer employeeId, String address, String phoneNumber, String password, Map<String, Object> employeeProperties) {
        this.name = name;
        this.team = team;
        this.employeeId = employeeId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.employeeProperties = employeeProperties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Map<String, Object> getEmployeeProperties() {
        return employeeProperties;
    }

    public void setEmployeeProperties(Map<String, Object> employeeProperties) {
        this.employeeProperties = employeeProperties;
    }
}
