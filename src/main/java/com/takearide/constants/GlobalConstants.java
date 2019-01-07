package com.takearide.constants;

/**
 * Created by divya-r on 16/12/16.
 */
public class GlobalConstants {
    public static class Api {
        public static final String ENDPOINT_API_CONTEXT = "/api";
        public static final String ENDPOINT_API_VERSION = "/v1";
    }

    public static class EventCategories {
        public static final String EMPLOYEE_EVENT = "EMPLOYEE_EVENT";
        public static final String DRIVER_EVENT = "DRIVER_EVENT";
    }

    public static class EventTypes {
        public static final String CAB_ARRIVED = "CAB_ARRIVED";
        public static final String EMPLOYEE_START_RIDE_ACK = "EMPLOYEE_START_RIDE_ACK";
        public static final String DRIVER_START_RIDE = "DRIVER_START_RIDE";
        public static final String EMPLOYEE_DROP_ACK = "EMPLOYEE_DROP_ACK";
        public static final String DRIVER_ACK_EMPLOYEE_DROP = "DRIVER_ACK_EMPLOYEE_DROP";
        public static final String DRIVER_END_RIDE = "DRIVER_END_RIDE";
    }

    public static class RideTypes {
        public static final String PICKUP = "PICKUP";
        public static final String DROP = "DROP";
    }
}
