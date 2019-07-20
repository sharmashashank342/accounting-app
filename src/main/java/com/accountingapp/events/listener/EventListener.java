package com.accountingapp.events.listener;

import com.accountingapp.events.EventDTO;

public interface EventListener {

    void handleApplicationEvent(EventDTO<?> eventDTO);
}
