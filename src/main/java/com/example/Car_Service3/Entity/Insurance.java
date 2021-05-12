package com.example.Car_Service3.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Insurance implements Serializable {
    private int id;
    private String typeofinsurance;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "typeofinsurance", nullable = true, length = 80)
    public String getTypeofinsurance() {
        return typeofinsurance;
    }

    public void setTypeofinsurance(String typeofinsurance) {
        this.typeofinsurance = typeofinsurance;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Insurance insurance = (Insurance) o;

        if (id != insurance.id) return false;
        if (typeofinsurance != null ? !typeofinsurance.equals(insurance.typeofinsurance) : insurance.typeofinsurance != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (typeofinsurance != null ? typeofinsurance.hashCode() : 0);
        return result;
    }
}
