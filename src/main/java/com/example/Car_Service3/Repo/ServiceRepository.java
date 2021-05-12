package com.example.Car_Service3.Repo;

import com.example.Car_Service3.Entity.Customer;
import com.example.Car_Service3.Entity.Service;
import org.springframework.data.repository.CrudRepository;

public interface ServiceRepository extends CrudRepository<Service, Integer> {
    Service findByCustomer(Customer customer);
    Service findByService(String id);
}
