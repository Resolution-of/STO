package com.example.Car_Service3.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Service implements Serializable {
    private String service;
    private String status;

    @Id
    @Column(name = "service", nullable = false, length = 45)
    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Basic
    @Column(name = "status", nullable = true, length = 45)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private Customer customer;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "mail", referencedColumnName = "mail")
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Service service1 = (Service) o;

        if (service != null ? !service.equals(service1.service) : service1.service != null) return false;
        if (status != null ? !status.equals(service1.status) : service1.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = service != null ? service.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
