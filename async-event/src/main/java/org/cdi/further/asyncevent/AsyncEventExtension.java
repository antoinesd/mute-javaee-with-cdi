package org.cdi.further.asyncevent;


import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by antoine on 17/09/2016.
 */
public class AsyncEventExtension implements Extension {

    Map<Class<?>, Set<Method>> classWithAsyncObs = new HashMap<>();


    public void processAsyncObs(@Observes @WithAnnotations(AsyncObs.class) ProcessAnnotatedType<?> pat) {
        AnnotatedType<?> at = pat.getAnnotatedType();

        classWithAsyncObs.put(at.getJavaClass(), at.getMethods()
                .stream()
                .filter(m -> m.getJavaMember().getParameters()[0].isAnnotationPresent(AsyncObs.class))
                .map(AnnotatedMethod::getJavaMember)
                .collect(Collectors.toSet()));
    }


    public void addAsyncObservers(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        for (Class<?> clazz : classWithAsyncObs.keySet()) {
            Set<Bean<?>> beans = bm.getBeans(clazz);
            if (!beans.isEmpty()) {
                for (Method m : classWithAsyncObs.get(clazz)) {
                    if (m.getParameterCount() != 1)
                        abd.addDefinitionError(new IllegalArgumentException("An Async Observer method can't have more than one arguments"));
                    else
                        abd.addObserverMethod(new AsyncObserverMethod(bm,clazz, m, m.getParameterTypes()[0], Collections.EMPTY_SET));
                }
            }
        }
    }
}
