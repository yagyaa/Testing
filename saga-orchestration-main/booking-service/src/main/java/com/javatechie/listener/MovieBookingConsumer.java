package com.javatechie.listener;

import com.javatechie.events.BookingCreatedEvent;
import com.javatechie.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.javatechie.common.KafkaConfigProperties.MOVIE_BOOKING_EVENTS_TOPIC;
import static com.javatechie.common.KafkaConfigProperties.MOVIE_BOOKING_GROUP;

@Component
@Slf4j
@RequiredArgsConstructor
public class MovieBookingConsumer {


    private final BookingService service;

    @KafkaListener(topics = MOVIE_BOOKING_EVENTS_TOPIC, groupId = MOVIE_BOOKING_GROUP)
    public void processBookingRequest(BookingCreatedEvent event){
        try {
            log.info("BookingListener:: Consuming booking event for id : {}", event.bookingId());
            service.processBooking(event);
        } catch (Exception e) {
            log.error("BookingListener:: Error while processing booking event for id : {}", event.bookingId(), e);
        }
    }
}
