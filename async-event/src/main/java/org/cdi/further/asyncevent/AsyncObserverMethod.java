package org.cdi.further.asyncevent;

import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Created by antoine on 18/09/2016.
 */
public class AsyncObserverMethod<T> implements ObserverMethod<T> {


    public static int invoked = 0;
    private BeanManager bm;
    private Class<?> beanClass;
    private Method method;
    private Class<T> observedType;
    private Set<Annotation> observedQualifiers;


    public AsyncObserverMethod(BeanManager bm, Class<?> beanClass, Method method, Class<T> observedType, Set<Annotation> observedQualifiers) {
        this.bm = bm;
        this.beanClass = beanClass;
        this.method = method;
        this.observedType = observedType;
        this.observedQualifiers = observedQualifiers;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public Type getObservedType() {
        return observedType;
    }

    @Override
    public Set<Annotation> getObservedQualifiers() {
        return observedQualifiers;
    }

    @Override
    public Reception getReception() {
        return Reception.ALWAYS;
    }

    @Override
    public TransactionPhase getTransactionPhase() {
        return TransactionPhase.IN_PROGRESS;
    }

    @Override
    public void notify(T event) {

        Bean bean =bm.resolve(bm.getBeans(beanClass));
        Object beanInstance = bm.getReference(bean,beanClass,bm.createCreationalContext(bean));

        CompletableFuture.runAsync(() -> {
            try {
               method.invoke(beanInstance,event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

        invoked++;
    }
}
