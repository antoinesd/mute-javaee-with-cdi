package org.cdi.further.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Metric;
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

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import java.io.FileNotFoundException;

/**
 * @author Antoine Sabot-Durand
 */

@RunWith(Arquillian.class)
public class MetricsIntegrationTest {

    @Deployment
    public static Archive<?> createTestArchive() throws FileNotFoundException {

        WebArchive ret = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackage("org.cdi.further.metrics")
                .addAsLibraries(Maven.resolver()
                        .loadPomFromFile("pom.xml")
                        .resolve("org.apache.deltaspike.core:deltaspike-core-api",
                                "io.dropwizard.metrics:metrics-core",
                                "io.dropwizard.metrics:metrics-annotation")
                        .withTransitivity().as(JavaArchive.class))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsServiceProvider(Extension.class, MetricsExtension.class);

        return ret;
    }



    @Inject
    MetricRegistry registry;

    @Inject
    @Metric(name = "myTimer")
    Timer timer;

    @Inject
    MetricsTestBean mtb;

    @Test
    public void shouldTimedInterceptorBeCalled() {
        mtb.timedMethod();
        Assert.assertEquals(1, registry.timer("myTimer").getCount());
    }


    @Test
    public void shouldMetricsBeTheSame() {
        Timer t = registry.timer("myTimer");
        Assert.assertSame(t, timer);
    }

    @Test
    public void shouldProducedMetricsBeInRegistry() {
        Assert.assertEquals(2, registry.getMetrics().size());
    }


}
