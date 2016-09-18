package org.cdi.further.fatentities;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * @author Antoine Sabot-Durand
 */
public class EntityInjector<T> {

    private final InjectionTarget<T> injectionTarget;
    private final CreationalContext<T> ctx;

    public EntityInjector(BeanManager beanManager, Class<T> clazz) {
        AnnotatedType<T> type = beanManager.createAnnotatedType(clazz);
        this.injectionTarget = beanManager.getInjectionTargetFactory(type).createInjectionTarget(null);
        this.ctx = beanManager.createCreationalContext(null);
    }


    public T inject(T instance) {
        injectionTarget.inject(instance, ctx);
        injectionTarget.postConstruct(instance);
        return instance;
    }

}
