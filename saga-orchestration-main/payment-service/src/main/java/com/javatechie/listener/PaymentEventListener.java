package com.javatechie.listener;

import com.javatechie.events.BookingPaymentEvent;
import com.javatechie.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.javatechie.common.KafkaConfigProperties.PAYMENT_EVENTS_CMD_TOPIC;
import static com.javatechie.common.KafkaConfigProperties.PAYMENT_EVENT_GROUP;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = PAYMENT_EVENTS_CMD_TOPIC, groupId =PAYMENT_EVENT_GROUP)
    public void onPaymentEvents(BookingPaymentEvent event){
        try {
            log.info("PaymentEventListener:: Processing payment events");
            paymentService.processPayment(event);
        }catch (Exception e){
            log.error("Error processing payment event for bookingId {}: {}", event.bookingId(), e.getMessage());
        }
    }


}
