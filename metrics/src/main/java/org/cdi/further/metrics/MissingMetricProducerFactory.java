package org.cdi.further.metrics;

import com.codahale.metrics.Metric;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;
import javax.enterprise.inject.spi.ProducerFactory;
import java.util.Set;

/**
 * Created by antoine on 22/10/2015.
 */
public class MissingMetricProducerFactory<X extends Metric> implements ProducerFactory<X> {

    private InjectionPoint ip;

    public MissingMetricProducerFactory(InjectionPoint ip) {
        this.ip = ip;
    }

    @Override
    public <T> Producer<T> createProducer(Bean<T> bean) {
        return new Producer<T>() {

            @Override
            public T produce(CreationalContext<T> ctx) {
                return null;
            }

            @Override
            public void dispose(T instance) {

            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return null;
            }
        };
    }
}
