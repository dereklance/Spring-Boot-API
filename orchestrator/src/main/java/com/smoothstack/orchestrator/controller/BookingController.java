package com.smoothstack.orchestrator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.smoothstack.orchestrator.entity.Booking;
import com.smoothstack.orchestrator.entity.BookingRequest;
import com.smoothstack.orchestrator.security.SecurityUtils;

@RestController
@RequestMapping("/booking")
public class BookingController {

	@Autowired
	RestTemplate restTemplate;

	private final String URL = "http://booking-service/booking";
	@GetMapping("/{bookingId}")
	public ResponseEntity<Booking> getBookingById(@PathVariable Integer bookingId, Authentication auth) {
		String userRole = SecurityUtils.getRole(auth);
		RequestEntity<Void> request = RequestEntity.get(URL + "/" + bookingId)
				.header("user-id", auth.getPrincipal().toString())
				.header( "user-role", userRole)
				.accept(MediaType.APPLICATION_JSON).build();
		return restTemplate.exchange(request, Booking.class);
	}

	@GetMapping
	public ResponseEntity<Booking[]> getAllBookings(Authentication auth) {
		String userRole = SecurityUtils.getRole(auth);
		RequestEntity<Void> request = RequestEntity.get(URL)
				.header("user-id", auth.getPrincipal().toString())
				.header("user-role", userRole)
				.accept(MediaType.APPLICATION_JSON).build();
		return restTemplate.exchange(request, Booking[].class);
	}

	@PostMapping("")
	public ResponseEntity<Booking> createBooking(@RequestBody Booking booking, Authentication auth) {
		booking.setBookerId(Integer.parseInt(auth.getPrincipal().toString()));
		RequestEntity<Booking> request = RequestEntity.post(URL)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.accept(MediaType.APPLICATION_JSON).body(booking);
		return restTemplate.exchange(request, Booking.class);
	}

	@PutMapping
	public ResponseEntity<Booking> updateBooking(@RequestBody Booking booking, Authentication auth) {
		String userRole = SecurityUtils.getRole(auth);
		RequestEntity<Booking> request = RequestEntity.put(URL)
				.header("user-id", auth.getPrincipal().toString())
				.header("user-role", userRole)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.accept(MediaType.APPLICATION_JSON).body(booking);
		return restTemplate.exchange(request, Booking.class);
	}
	
	@DeleteMapping("/{bookingId}")
	public ResponseEntity<String> deleteBooking(@PathVariable Integer bookingId, Authentication auth) {
		String userRole = SecurityUtils.getRole(auth);
		RequestEntity<Void> request = RequestEntity.delete("%s/%d".formatted(URL, bookingId))
				.header("user-id", auth.getPrincipal().toString())
				.header( "user-role", userRole)
				.accept(MediaType.APPLICATION_JSON).build();
		return restTemplate.exchange(request, String.class);
	}
}
