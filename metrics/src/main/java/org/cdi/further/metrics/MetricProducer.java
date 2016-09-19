package org.cdi.further.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;
import java.util.Set;

/**
 *
 */
public class MetricProducer<X extends Metric> implements Producer<X> {

    Producer<X> decorate;
    String metricName;


    public MetricProducer( Producer<X> decorate, String metricName) {
        this.decorate = decorate;
        this.metricName = metricName;
    }



    @Override
    public X produce(CreationalContext<X> ctx) {
        MetricRegistry reg = BeanProvider.getContextualReference(MetricRegistry.class, false);
        if (!reg.getMetrics().containsKey(metricName)) {
            reg.register(metricName,decorate.produce(ctx));
        }

        return (X)reg.getMetrics().get(metricName);
    }

    @Override
    public void dispose(X instance) {
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return decorate.getInjectionPoints();
    }
}
