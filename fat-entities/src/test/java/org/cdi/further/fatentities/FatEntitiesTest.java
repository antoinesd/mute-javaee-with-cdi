package org.cdi.further.fatentities;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.FileNotFoundException;

/**
 * @author Antoine Sabot-Durand
 */

@RunWith(Arquillian.class)
public class FatEntitiesTest {

    @Inject
    Instance<Person> instPers;

    @Deployment
    public static Archive<?> createTestArchive() throws FileNotFoundException {

        return  ShrinkWrap
                .create(WebArchive.class,"test.war")
                .addPackage(Person.class.getPackage())
                .addAsWebInfResource("META-INF/beans.xml")
                .addAsWebInfResource("META-INF/load.sql","classes/META-INF/load.sql")
                .addAsWebInfResource("META-INF/persistence.xml","classes/META-INF/persistence.xml");


    }

    @Test
    public void shouldPersonBePersisted() {
        Person p = instPers.get();
        p.setName("Antoine");
        p.persist();

        Assert.assertEquals(1,p.getPersonWithMyName().size());

    }

    @Test
    public void shouldPersonBeRemoved() {
        Person p = instPers.get();
        Person p2 = p.getPersonById(1L);
        Assert.assertNotNull(p2.getName());

        p2.remove();

        Assert.assertEquals(0,p2.getPersonWithMyName().size());

    }

}
