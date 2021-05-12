package com.example.Car_Service3.Entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
public class Rental implements Serializable {
    private int id;
    private String car;
    private Date beginningDate;
    private Date endDate;

    @Id
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "car", nullable = true, length = 45)
    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    @Basic
    @Column(name = "beginning_date", nullable = true)
    public Date getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(Date beginningDate) {
        this.beginningDate = beginningDate;
    }

    @Basic
    @Column(name = "end_date", nullable = true)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    private Service service;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "serviceID", referencedColumnName = "service")
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rental rental = (Rental) o;

        if (id != rental.id) return false;
        if (car != null ? !car.equals(rental.car) : rental.car != null) return false;
        if (beginningDate != null ? !beginningDate.equals(rental.beginningDate) : rental.beginningDate != null)
            return false;
        if (endDate != null ? !endDate.equals(rental.endDate) : rental.endDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (car != null ? car.hashCode() : 0);
        result = 31 * result + (beginningDate != null ? beginningDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }
}
