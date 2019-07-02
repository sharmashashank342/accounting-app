package com.assignment.events.service;

import com.assignment.configuration.EventExecutor;
import com.assignment.events.Domain;
import com.assignment.events.EventDTO;
import com.assignment.events.EventType;
import com.assignment.events.publisher.EventPublisher;

public class EventServiceImpl implements EventService {

    // Maintain Async in Event Service so that
    // it will not raise throwable and block other services
    private EventExecutor eventExecutor;

    private EventPublisher eventPublisher;

    public EventServiceImpl(EventPublisher eventPublisher, EventExecutor eventExecutor) {
        this.eventExecutor = eventExecutor;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void raiseUserDeactivatedEvent(long userId) {

        eventExecutor.executeRunnable(() -> {
            EventDTO<Long> eventDTO = new EventDTO<>();
            eventDTO.setDomain(Domain.USER);
            eventDTO.setEventType(EventType.USER_DEACTIVATED);
            eventDTO.setEventParam(userId);
            eventPublisher.publishEvent(eventDTO);
        });
    }
}
