package com.example.Car_Service3.Repo;

import com.example.Car_Service3.Entity.Rental;
import com.example.Car_Service3.Entity.Service;
import org.springframework.data.repository.CrudRepository;

public interface RentalRepository extends CrudRepository<Rental, Integer> {
    Rental findRentalByService(Service service);
}
