package com.yahya.stupid.things.model;

import java.util.concurrent.ScheduledExecutorService;

public interface Screen {

    void start();
    ScheduledExecutorService getService();
    void setService(ScheduledExecutorService service);

    default void pause() {
        if (getService() != null && !getService().isShutdown()) {
            System.out.println("Pausing....");
            getService().shutdown();
            setService(null);
        }
    }
    void clear();
}
