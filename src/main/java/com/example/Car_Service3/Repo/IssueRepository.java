package com.example.Car_Service3.Repo;

import com.example.Car_Service3.Entity.Issue;
import com.example.Car_Service3.Entity.Service;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IssueRepository extends CrudRepository<Issue, Integer> {
    Issue findIssueByService(Service service);
}
