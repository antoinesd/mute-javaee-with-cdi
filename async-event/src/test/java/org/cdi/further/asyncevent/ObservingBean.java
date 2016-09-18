package org.cdi.further.asyncevent;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by antoine on 18/09/2016.
 */

@ApplicationScoped
public class ObservingBean {

    public void anaSyncObserver(@AsyncObs String str) throws InterruptedException {
        Thread.sleep(1000L);
        System.out.println(str);
    }


}
