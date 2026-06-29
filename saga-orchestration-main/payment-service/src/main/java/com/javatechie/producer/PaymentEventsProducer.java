package com.javatechie.producer;

import com.javatechie.common.KafkaConfigProperties;
import com.javatechie.events.BookingPaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentEventsProducer {

    private KafkaTemplate<String,Object> template;

    public PaymentEventsProducer(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    public void publishPaymentSuccessEvent(BookingPaymentEvent event){
        log.info("Publishing payment success event ...");
        BookingPaymentEvent paymentEvent=new BookingPaymentEvent(event.bookingId(),event.showId(),event.seatIds(),true,event.amount());
        template
                .send(KafkaConfigProperties.PAYMENT_EVENTS_TOPIC, event.bookingId(),paymentEvent);

    }

    public void publishPaymentFailureEvent(BookingPaymentEvent event){
        log.info("Publishing payment failure event ...");
        BookingPaymentEvent paymentEvent=new BookingPaymentEvent(event.bookingId(),event.showId() ,event.seatIds(),false,event.amount());
        template
                .send(KafkaConfigProperties.PAYMENT_EVENTS_TOPIC, event.bookingId(),paymentEvent);

    }
}
