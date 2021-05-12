package com.example.Car_Service3.Repo;

import com.example.Car_Service3.Entity.Service;
import com.example.Car_Service3.Entity.Vacancies;
import org.springframework.data.repository.CrudRepository;

public interface VacanciesRepository extends CrudRepository<Vacancies, Integer> {
    Vacancies findVacanciesByService(Service service);
}
