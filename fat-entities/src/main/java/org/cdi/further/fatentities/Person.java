package org.cdi.further.fatentities;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.*;
import java.util.List;

/**
 *
 */

@Dependent //VERY IMPORTANT !!!
@Entity
@Table(name = "PERSON")
@EntityListeners({ ManagingListener.class })
@NamedQueries({
        @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
        @NamedQuery(name = "Person.withName", query = "SELECT p FROM Person p WHERE p.name=:name")
})
public class Person {
    private String name;

    private Long id;

    @Inject
    PersonService service;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Person() {

    }

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String surname) {
        this.name = surname;
    }


    public void persist() {
        service.persist(this);
    }

    public void remove() {
        service.remove(this);
    }


    @Transient
    public List<Person> getPersonWithMyName() {
        return service.getPersonsWithName(name);
    }

    @Transient
    public Person getPersonById(Long id) {
        return service.getPersonById(id);
    }
}
