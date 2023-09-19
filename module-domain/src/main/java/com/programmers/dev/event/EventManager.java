package com.programmers.dev.event;

import org.springframework.context.ApplicationEventPublisher;

public class EventManager {

    private static ApplicationEventPublisher publisher;

    public static void setPublisher(ApplicationEventPublisher publisher) {
        EventManager.publisher = publisher;
    }

    public static void publish(Object event) {
        if (publisher != null) {
            publisher.publishEvent(event);
        }
    }
}
