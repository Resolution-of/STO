package com.example.Car_Service3.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Issue implements Serializable {
    private int id;
    private String brand;
    private String problem;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "brand", nullable = true, length = 45)
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Basic
    @Column(name = "problem", nullable = true, length = 45)
    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Issue issue = (Issue) o;

        if (id != issue.id) return false;
        if (brand != null ? !brand.equals(issue.brand) : issue.brand != null) return false;
        if (problem != null ? !problem.equals(issue.problem) : issue.problem != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (brand != null ? brand.hashCode() : 0);
        result = 31 * result + (problem != null ? problem.hashCode() : 0);
        return result;
    }

    private Service service;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "service", referencedColumnName = "service")
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
