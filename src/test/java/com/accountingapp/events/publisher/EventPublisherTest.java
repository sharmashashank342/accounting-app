package com.accountingapp.events.publisher;

import com.accountingapp.events.Domain;
import com.accountingapp.events.EventDTO;
import com.accountingapp.events.EventType;
import com.accountingapp.events.listener.UserEventListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventPublisherTest {

    @Mock
    private UserEventListener userEventListener;

    private EventPublisher eventPublisher;

    @Before
    public void setUp() {
        eventPublisher = new EventPublisher();
        eventPublisher.addListener(userEventListener);
    }

    @Captor
    private ArgumentCaptor<EventDTO<Long>> argumentCaptor;

    @Test
    public void test_publishEvent() {

        EventDTO<Long> eventDTO = new EventDTO<>();
        eventDTO.setHandled(true);
        eventDTO.setEventType(EventType.USER_DEACTIVATED);
        eventDTO.setDomain(Domain.USER);
        eventDTO.setEventParam(1L);

        doNothing().when(userEventListener).handleApplicationEvent(any(EventDTO.class));

        eventPublisher.publishEvent(eventDTO);

        verify(userEventListener).handleApplicationEvent(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(eventDTO);
    }
}
