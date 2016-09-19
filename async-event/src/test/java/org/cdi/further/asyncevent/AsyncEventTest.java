package org.cdi.further.asyncevent;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import java.io.FileNotFoundException;

/**
 * Created by antoine on 18/09/2016.
 */
@RunWith(Arquillian.class)
public class AsyncEventTest {

    @Deployment
    public static Archive<?> createTestArchive() throws FileNotFoundException {

        WebArchive ret = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackage("org.cdi.further.asyncevent")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsServiceProvider(Extension.class, AsyncEventExtension.class);
        return ret;
    }

    @Inject
    Event<String> strEvt;

    @Test
    public void shouldTriggerAsyncObs() throws InterruptedException {
        strEvt.fire("Hello !!!");
        System.out.println("World");

        Thread.sleep(2000L);


        Assert.assertEquals(1,AsyncObserverMethod.invoked);

    }
}
