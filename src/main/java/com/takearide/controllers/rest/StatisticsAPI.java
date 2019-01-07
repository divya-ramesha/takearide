package com.takearide.controllers.rest;

import com.takearide.constants.GlobalConstants;
import com.takearide.repositories.GlobalRideGroupsTemplateRepository;
import com.takearide.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by divya-r on 18/12/16.
 */

@RestController
@RequestMapping(GlobalConstants.Api.ENDPOINT_API_CONTEXT + GlobalConstants.Api.ENDPOINT_API_VERSION + "/statistics")
public class StatisticsAPI {

    @Autowired
    StatisticsService statisticsService;

    @Autowired
    GlobalRideGroupsTemplateRepository globalRideGroupsTemplateRepository;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() throws Exception {
        return "test-statistics";
    }

    @RequestMapping(value = "/getAllTotalTravelTimeForToday", method = RequestMethod.GET)
    public List<List<Object>> getAllTotalTravelTimeForToday() throws Exception {
        return statisticsService.getAllTotalTravelTimeForToday();
    }

}
