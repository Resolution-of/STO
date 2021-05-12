package com.example.Car_Service3.Entity;

import javax.persistence.*;

@Entity
public class Vacancies {
    private int id;
    private String vacancy;
    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "vacancy", nullable = true, length = 45)
    public String getVacancy() {
        return vacancy;
    }

    public void setVacancy(String vacancy) {
        this.vacancy = vacancy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vacancies vacancies = (Vacancies) o;

        if (id != vacancies.id) return false;
        if (vacancy != null ? !vacancy.equals(vacancies.vacancy) : vacancies.vacancy != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (vacancy != null ? vacancy.hashCode() : 0);
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
