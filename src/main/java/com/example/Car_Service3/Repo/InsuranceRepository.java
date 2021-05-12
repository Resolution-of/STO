package com.example.Car_Service3.Repo;

import com.example.Car_Service3.Entity.Insurance;
import com.example.Car_Service3.Entity.Service;
import org.springframework.data.repository.CrudRepository;

public interface InsuranceRepository extends CrudRepository<Insurance, Integer> {
    Insurance findInsuranceByService(Service service);
}