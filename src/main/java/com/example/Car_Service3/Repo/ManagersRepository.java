package com.example.Car_Service3.Repo;

import com.example.Car_Service3.Entity.Managers;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagersRepository extends CrudRepository<Managers, Integer> {
}