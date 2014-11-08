package org.cdi.further.metrics;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Metric;
import com.codahale.metrics.annotation.Timed;
import org.apache.deltaspike.core.api.literal.AnyLiteral;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.AnnotationUtils;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.enterprise.inject.spi.Producer;
import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;

/**
 * @author Antoine Sabot-Durand
 */
public class MetricExtension implements Extension {

    public static class MetricProducer implements Producer<com.codahale.metrics.Metric> {

        private Producer<com.codahale.metrics.Metric> decorated;
        private BeanManager bm;
        private String name;

        public MetricProducer(Producer<com.codahale.metrics.Metric> decorated, String name, BeanManager bm) {
            this.decorated = decorated;
            this.bm = bm;
            this.name = name;
        }


        MetricRegistry getRegistry() {
            return BeanProvider.getContextualReference(bm, MetricRegistry.class, false);
        }

        @Override
        public com.codahale.metrics.Metric produce(CreationalContext<com.codahale.metrics.Metric> ctx) {
            MetricRegistry reg = getRegistry();
            com.codahale.metrics.Metric res;
            if (!reg.getMetrics().containsKey(name))
                reg.register(name, decorated.produce(ctx));
            res = reg.getMetrics().get(name);
            return res;
        }

        @Override
        public void dispose(com.codahale.metrics.Metric instance) {

        }

        @Override
        public Set<InjectionPoint> getInjectionPoints() {
            return decorated.getInjectionPoints();
        }
    }

    void addTimedBinding(@Observes BeforeBeanDiscovery bbd, BeanManager bm) throws Exception {
        AnnotatedType<Timed> at = createTimedAnnotatedType(bm);
        bbd.addQualifier(at);
        bbd.addInterceptorBinding(at);
    }

    private AnnotatedType createTimedAnnotatedType(BeanManager bm) throws Exception {
        Annotation nonBinding = new AnnotationLiteral<Nonbinding>() {
        };
        return new AnnotatedTypeBuilder().readFromType(bm.createAnnotatedType(Timed.class))
                .addToMethod(Timed.class.getMethod("name"), nonBinding)
                .addToMethod(Timed.class.getMethod("absolute"), nonBinding)
                .create();
    }


    void reproduceMetric(@Observes ProcessProducer<?, ? extends com.codahale.metrics.Metric> pp, BeanManager bm) {
        Metric qual = pp.getAnnotatedMember().getAnnotation(Metric.class);
        if (qual != null) {
            String name = qual.name();
            ProcessProducer<?, com.codahale.metrics.Metric> ppCast = (ProcessProducer<?, com.codahale.metrics.Metric>) pp;
            ppCast.setProducer(new MetricProducer(ppCast.getProducer(), name, bm));
        }

    }

    void addMetricQualifier(@Observes BeforeBeanDiscovery bbd, BeanManager bm) {
        bbd.addQualifier(bm.createAnnotatedType(Metric.class));
    }

    void regMetrics(@Observes AfterDeploymentValidation adv, BeanManager bm) {
        Set<Bean<?>> metricBeans = bm.getBeans(com.codahale.metrics.Metric.class, new AnyLiteral());
        for (Bean<?> bean : metricBeans) {
            Metric qual = AnnotationUtils.findAnnotation(bm, bean.getQualifiers().toArray(new Annotation[0]), Metric.class);
            if (qual != null) {
                String name = qual.name();
                BeanProvider.getContextualReference(bm, com.codahale.metrics.Metric.class, false, qual);
            }
        }
    }
}
