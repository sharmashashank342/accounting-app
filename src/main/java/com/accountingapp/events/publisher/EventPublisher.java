package com.accountingapp.events.publisher;

import com.accountingapp.events.EventDTO;
import com.accountingapp.events.listener.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventPublisher {

    private List<EventListener> listeners;

    // TODO: Can add a Pub Sub System so that event will be persisted in queue
    public EventPublisher(List<EventListener> listeners) {
        this.listeners = listeners;
    }

    // TODO: Should be only visible for Testing
    public EventPublisher() {
        this.listeners = new ArrayList<>();
    }

    // TODO: Should be only visible for Testing
    public void addListener(EventListener eventListener) {
        this.listeners.add(eventListener);
    }

    public void publishEvent(EventDTO<?> eventDTO) {

        if (Objects.nonNull(listeners) && (!listeners.isEmpty())) {
            for(EventListener eventListener : listeners) {
                eventListener.handleApplicationEvent(eventDTO);
            }
        }
    }
}
