package com.accountingapp.events.service;

import com.accountingapp.configuration.EventExecutor;
import com.accountingapp.events.publisher.EventPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceImplTest {

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private EventExecutor eventExecutor;

    @Captor
    private ArgumentCaptor<Runnable> runnableArgumentCaptor;

    @InjectMocks
    private EventService eventService = new EventServiceImpl(eventPublisher, eventExecutor);

    @Test
    public void test_raiseUserDeactivatedEvent() {

        doNothing().when(eventExecutor).executeRunnable(any(Runnable.class));

        eventService.raiseUserDeactivatedEvent(1L);

        verify(eventExecutor).executeRunnable(runnableArgumentCaptor.capture());

        // TODO: Somehow assert calling of eventPublisher.publishEvent from runnableArgumentCaptor
    }
}
