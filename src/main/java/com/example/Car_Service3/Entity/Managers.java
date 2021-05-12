package com.example.Car_Service3.Entity;

import javax.persistence.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Set;

@Entity
public class Managers {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String login;
    private String password;
    private Byte active;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "login", nullable = true, length = 45)
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Basic
    @Column(name = "password", nullable = true, length = 61)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "active", nullable = true)
    public Byte getActive() {
        return active;
    }

    public void setActive(Byte active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Managers managers = (Managers) o;

        if (id != managers.id) return false;
        if (login != null ? !login.equals(managers.login) : managers.login != null) return false;
        if (password != null ? !password.equals(managers.password) : managers.password != null) return false;
        if (active != null ? !active.equals(managers.active) : managers.active != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        return result;
    }
    public void insertRole()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sto", "root", "Superkillbody12345");
            String sel = "INSERT INTO user_role (login, role) VALUES (?,?)";

            PreparedStatement pst = connection.prepareStatement(sel);
            pst.setString(1, login);
            pst.setString(2, "ADMIN");

            pst.execute();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
