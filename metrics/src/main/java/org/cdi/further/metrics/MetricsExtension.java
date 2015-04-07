package org.cdi.further.metrics;


import com.codahale.metrics.annotation.Metric;
import com.codahale.metrics.annotation.Timed;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Annotation;

/**
 * @author Antoine Sabot-Durand
 */
public class MetricsExtension implements Extension {

    void addMetricAsQualifier(@Observes BeforeBeanDiscovery bdd) {
        bdd.addQualifier(Metric.class);
    }

    void addTimedBinding(@Observes BeforeBeanDiscovery bdd) throws Exception {

        Annotation nonBinding = new AnnotationLiteral<Nonbinding>() {};

        bdd.addInterceptorBinding(new AnnotatedTypeBuilder<Timed>()
                .readFromType(Timed.class)
                .addToMethod(Timed.class.getMethod("name"), nonBinding)
                .addToMethod(Timed.class.getMethod("absolute"),nonBinding)
                .create());
    }
}
