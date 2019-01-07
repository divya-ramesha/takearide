package com.takearide.services;

import com.takearide.constants.GlobalConstants;
import com.takearide.pojo.Events;
import com.takearide.pojo.Ride;
import com.takearide.repositories.DriverRepository;
import com.takearide.repositories.EmployeeRepository;
import com.takearide.repositories.EventsRepository;
import com.takearide.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by divya-r on 17/12/16.
 */

@Service
public class EventsService {

    @Autowired
    EventsRepository eventsRepository;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    RideRepository rideRepository;

    public Events addEvent(Integer rideId, Integer acknowledgedBy, String eventCategory, String eventType, List<Integer> ackowledgedTo) {
        Long timestamp = System.currentTimeMillis() / 1000;
        String acknowledger = null;
        if (eventCategory.equals(GlobalConstants.EventCategories.DRIVER_EVENT)) {
            acknowledger = driverRepository.findOne(acknowledgedBy).getName();
        } else {
            acknowledger = employeeRepository.findOne(acknowledgedBy).getName();
        }
        Events events = new Events(eventCategory, eventType, timestamp, rideId, acknowledgedBy, acknowledger, ackowledgedTo);
        eventsRepository.save(events);
        return events;
    }

    public List<Events> getLast10Events() {
        return eventsRepository.findTop10ByOrderByTimestampDesc();
    }

    public List<Events> getLast10DriverEvents(Integer driverId) {
        return eventsRepository.findTop10ByAcknowledgedByOrderByTimestampDesc(driverId);
    }

    public List<Events> getLast10EmployeeEvents(Integer employeeId) {
        return eventsRepository.findTop10ByAcknowledgedByOrderByTimestampDesc(employeeId);
    }

    public List<Events> getLast10RideEvents(Integer rideId) {
        return eventsRepository.findTop10ByRideIdOrderByTimestampDesc(rideId);
    }

    public Boolean checkIfCabHasArrived(Integer employeeId) throws Exception {
        Ride ride = employeeService.getOldestIncompleteRideForEmployee(employeeId);
        if (ride == null) {
            return false;
        }

        Integer rideId = ride.getRideId();
        Events event = eventsRepository.findByRideIdAndEventCategoryAndEventType(rideId, GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.CAB_ARRIVED);
        if (event == null) {
            return false;
        }

        Events alreadyAck = eventsRepository.findByRideIdAndEventCategoryAndEventTypeAndAcknowledgedBy(rideId, GlobalConstants.EventCategories.EMPLOYEE_EVENT, GlobalConstants.EventTypes.EMPLOYEE_START_RIDE_ACK, employeeId);
        if (alreadyAck != null) {
            return false;
        }

        return true;
    }

    public Boolean checkIfEmployeeStartedRide(Integer employeeId) {
        Ride ride = rideRepository.findByEmployeeIdListAndInProgress(employeeId, true);
        if (ride == null) {
            return false;
        }

        Integer rideId = ride.getRideId();
        Events event = eventsRepository.findByRideIdAndEventCategoryAndEventTypeAndAcknowledgedBy(rideId, GlobalConstants.EventCategories.EMPLOYEE_EVENT, GlobalConstants.EventTypes.EMPLOYEE_START_RIDE_ACK, employeeId);
        if (event == null) {
            return false;
        }

        Events alreadyAck = eventsRepository.findByRideIdAndEventCategoryAndEventTypeAndAcknowledgedBy(rideId, GlobalConstants.EventCategories.EMPLOYEE_EVENT, GlobalConstants.EventTypes.EMPLOYEE_DROP_ACK, employeeId);
        if (alreadyAck != null) {
            return false;
        }

        return true;
    }

    public Boolean checkIfDriverCanAnnounceCabArrival(Integer driverId) {
        List<Ride> rides = rideRepository.findByDriverIdAndIsCompleteOrderByTimestamp(driverId, false);
        if (rides.size() == 0) {
            return false;
        }

        Ride ride = rides.get(0);

        Events alreadyAck = eventsRepository.findByRideIdAndEventCategoryAndEventTypeAndAcknowledgedBy(ride.getRideId(), GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.CAB_ARRIVED, driverId);
        if (alreadyAck != null) {
            return false;
        }


        Long rideTimestamp = ride.getTimestamp();
        Long currentTimestamp = System.currentTimeMillis() / 1000;
        Long difference = Math.abs(currentTimestamp - rideTimestamp);
        if (difference > 1800L) {
            return false;
        }
        return true;
    }

    public Boolean checkIfDriverCanStartCab(Integer driverId) {
        Ride ride = rideRepository.findByDriverIdAndInProgressAndIsComplete(driverId, true, false);
        if (ride == null) {
            return false;
        }
        Integer rideId = ride.getRideId();

        Events alreadyAck = eventsRepository.findByRideIdAndEventCategoryAndEventTypeAndAcknowledgedBy(ride.getRideId(), GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.DRIVER_START_RIDE, driverId);
        if (alreadyAck != null) {
            return false;
        }

        Integer numberOfEmployeesInRide = ride.getEmployeeIdList().size();

        Integer numberOfEmployeeStartRideAck = eventsRepository.countByRideIdAndEventCategoryAndEventType(rideId, GlobalConstants.EventCategories.EMPLOYEE_EVENT, GlobalConstants.EventTypes.EMPLOYEE_START_RIDE_ACK);

        if (numberOfEmployeesInRide == numberOfEmployeeStartRideAck) {
            return true;
        }

        return false;
    }

    public Boolean checkIfDriverCanAckEmployeeDrop(Integer driverId, Integer employeeId) {

        Ride ride = rideRepository.findByDriverIdAndInProgressAndIsComplete(driverId, true, false);
        if (ride == null) {
            return false;
        }
        Integer rideId = ride.getRideId();

        Events eventByEmployee = eventsRepository.findByRideIdAndEventCategoryAndEventTypeAndAcknowledgedByAndAcknowledgedTo(rideId, GlobalConstants.EventCategories.EMPLOYEE_EVENT, GlobalConstants.EventTypes.EMPLOYEE_DROP_ACK, employeeId, driverId);
        Events eventByDriver = eventsRepository.findByRideIdAndEventCategoryAndEventTypeAndAcknowledgedByAndAcknowledgedTo(rideId, GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.DRIVER_ACK_EMPLOYEE_DROP, driverId, employeeId);

        if (eventByEmployee == null) {
            return false;
        }

        if (eventByDriver != null) {
            return false;
        }

        return true;
    }

    public Boolean checkIfDriverCanEndRide(Integer driverId) {
        Ride ride = rideRepository.findByDriverIdAndInProgressAndIsComplete(driverId, true, false);
        if (ride == null) {
            return false;
        }
        Integer rideId = ride.getRideId();

        Events alreadyAck = eventsRepository.findByRideIdAndEventCategoryAndEventTypeAndAcknowledgedBy(ride.getRideId(), GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.DRIVER_END_RIDE, driverId);
        if (alreadyAck != null) {
            return false;
        }

        Integer numberOfEmployeesInRide = ride.getEmployeeIdList().size();

        Integer numberOfEmployeeEndRideAckByDriver = eventsRepository.countByRideIdAndEventCategoryAndEventType(rideId, GlobalConstants.EventCategories.DRIVER_EVENT, GlobalConstants.EventTypes.DRIVER_ACK_EMPLOYEE_DROP);

        if (numberOfEmployeesInRide == numberOfEmployeeEndRideAckByDriver) {
            return true;
        }

        return false;
    }
}
