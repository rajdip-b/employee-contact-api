package com.app.employeecontactapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
@ToString
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String jobTitle;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String state;
    @OneToOne
    private Employee primaryEmergencyContact;
    private String primaryEmergencyContactRelation;
    @OneToOne
    private Employee secondaryEmergencyContact;
    private String secondaryEmergencyContactRelation;
    @JsonIgnore
    @OneToOne
    private Employee primaryTo;
    @JsonIgnore
    @OneToOne
    private Employee secondaryTo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Employee employee = (Employee) o;
        return id != null && Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
