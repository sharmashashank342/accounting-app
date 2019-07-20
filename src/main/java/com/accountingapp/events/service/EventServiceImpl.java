package com.accountingapp.events.service;

import com.accountingapp.configuration.EventExecutor;
import com.accountingapp.events.Domain;
import com.accountingapp.events.EventDTO;
import com.accountingapp.events.EventType;
import com.accountingapp.events.publisher.EventPublisher;

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
