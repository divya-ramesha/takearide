package com.takearide.repositories;

import com.takearide.pojo.GlobalRideGroupsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by divya-r on 17/12/16.
 */
@Repository
public interface GlobalRideGroupsTemplateRepository extends MongoRepository<GlobalRideGroupsTemplate, String> {

    List<GlobalRideGroupsTemplate> findByDriverId(Integer driverId);

}
