package com.javatechie.controller;

import com.javatechie.request.BookingRequest;
import com.javatechie.service.MovieBookingOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orchestrator")
@RequiredArgsConstructor
public class MovieBookingOrchestratorController {

    private final MovieBookingOrchestrator orchestrator;

    @PostMapping("/bookings")
    public String startSaga(@RequestBody BookingRequest request) {
        return orchestrator.createBooking(request);
    }
}
