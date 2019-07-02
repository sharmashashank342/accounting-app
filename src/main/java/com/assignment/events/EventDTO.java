package com.assignment.events;

import lombok.Data;

import java.util.UUID;

@Data
public class EventDTO<T> {

    private String eventId = UUID.randomUUID().toString();

    private EventType eventType;

    private Domain domain;

    private boolean isHandled;

    private T eventParam;
}
