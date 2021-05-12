package com.example.Car_Service3.Repo;

import com.example.Car_Service3.Entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer>{
    Customer findByName(String name);
    List<Customer> findCustomersByName(String name);
    List<Customer> findCustomersByType(String type);
    Customer findByMail(String mail);
}
