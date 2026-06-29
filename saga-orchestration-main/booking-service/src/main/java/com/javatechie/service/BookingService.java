package com.javatechie.service;

import com.javatechie.entity.Booking;
import com.javatechie.events.BookingCreatedEvent;
import com.javatechie.repository.BookingRepository;
import com.javatechie.utils.mapper.BookingRequestToEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;


    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    

    public void processBooking(BookingCreatedEvent request) {
        log.info("BookingService:: Processing booking for user {} for show {}", request.userId(), request.showId());
        Booking booking=null;
        Booking existingBooking = bookingRepository
                .findByBookingCode(request.bookingId());

        if(existingBooking==null){
            // CREATE
            log.info("BookingService:: Creating new booking for id {}", request.bookingId());
            booking = BookingRequestToEntityMapper.mapEvents(request);
        }else{
            //UPDATE
            booking=updateExistingBooking(existingBooking, request);
            log.info("BookingService:: Updating existing booking for id {}", request.bookingId());
        }
        // Save the new or updated booking
        var saved = bookingRepository.save(booking);

        log.info("BookingService:: Booking saved: reservation id {} | status {}",
                saved.getBookingCode(), saved.getStatus());
    }

    private Booking updateExistingBooking(Booking existing, BookingCreatedEvent event) {
        // Only update what makes sense for CONFIRMED / FAILED flows
        existing.setStatus(event.status());
        return existing;
    }
}
