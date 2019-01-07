package com.takearide.repositories;

import com.takearide.pojo.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by divya-r on 17/12/16.
 */

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, Integer> {

    Employee findByEmployeeId(Integer employeeId);
}