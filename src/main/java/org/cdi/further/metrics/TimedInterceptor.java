package org.cdi.further.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * @author Antoine Sabot-Durand
 */
@Interceptor
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
@Timed
//@TimedBinding
class TimedInterceptor {
    @Inject
    MetricRegistry registry;
    @AroundInvoke
    Object timeMethod(InvocationContext context) throws Exception {
       // String name = context.getMethod().getAnnotation(Timed.class).name();
        System.out.println("here");
        //Timer timer = registry.timer(name);
        //Timer.Context time = timer.time();
        try { return context.proceed();}
        finally { //time.stop(); 
         }
    }
}
