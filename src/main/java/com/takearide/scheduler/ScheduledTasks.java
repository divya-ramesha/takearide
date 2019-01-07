package com.takearide.scheduler;

import com.google.common.collect.Maps;
import com.takearide.pojo.GlobalRideGroupsTemplate;
import com.takearide.repositories.GlobalRideGroupsTemplateRepository;
import com.takearide.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by divya-r on 17/12/16.
 */

@Component
public class ScheduledTasks {

    @Autowired
    GlobalRideGroupsTemplateRepository globalRideGroupsTemplateRepository;

    @Autowired
    AdminService adminService;

    @Scheduled(cron = "0 1 0 * * *")
    public void convertGlobalTemplateToRides() throws Exception {
        List<GlobalRideGroupsTemplate> globalRideGroupsTemplates = globalRideGroupsTemplateRepository.findAll();
        for (GlobalRideGroupsTemplate globalRideGroupsTemplate :
                globalRideGroupsTemplates) {
            Map<String, Object> rideDetails = Maps.newHashMap();
            rideDetails.put("driverId", globalRideGroupsTemplate.getDriverId());
            rideDetails.put("batch", globalRideGroupsTemplate.getBatch());
            rideDetails.put("employeeList", globalRideGroupsTemplate.getEmployeeIdList());
            rideDetails.put("rideType", globalRideGroupsTemplate.getRideType());
            adminService.addOrUpdateRide(rideDetails);
        }
    }
}
