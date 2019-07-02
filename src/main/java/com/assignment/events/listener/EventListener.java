package com.assignment.events.listener;

import com.assignment.events.EventDTO;

public interface EventListener {

    void handleApplicationEvent(EventDTO<?> eventDTO);
}
