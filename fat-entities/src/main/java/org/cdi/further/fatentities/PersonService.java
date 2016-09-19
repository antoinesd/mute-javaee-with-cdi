package org.cdi.further.fatentities;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by antoine on 17/09/2016.
 */
@ApplicationScoped
@Transactional
public class PersonService {

    @PersistenceContext
    EntityManager em;

    public List<Person> getAll() {
        return em.createNamedQuery("Person.findAll", Person.class).getResultList();
    }

    public void persist(Person person) {
        em.persist(person);
    }

    public void remove(Person person) {
        em.remove(em.merge(person));
    }

    public List<Person> getPersonsWithName(String name) {
        return em.createNamedQuery("Person.withName", Person.class)
                .setParameter("name",name)
                .getResultList();
    }

    public Person getPersonById(Long id) {
        return em.find(Person.class,id);
    }
}
