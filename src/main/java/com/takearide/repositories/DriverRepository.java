package com.takearide.repositories;

import com.takearide.pojo.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by divya-r on 17/12/16.
 */

@Repository
public interface DriverRepository extends MongoRepository<Driver, Integer> {

    Driver findByDriverId(Integer driverId);
}