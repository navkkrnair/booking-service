package com.ams.booking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ams.booking.component.BookingComponent;
import com.ams.booking.entity.BookingRecord;

@RestController
@RequestMapping("/booking")
public class BookingController
{
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    BookingComponent            bookingComponent;

    @Autowired
    BookingController(BookingComponent bookingComponent)
    {
        this.bookingComponent = bookingComponent;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    long book(@RequestBody BookingRecord record, @CookieValue("XSRF-TOKEN") String xsrftoken, @CookieValue("SESSION") String session, @RequestHeader("BOOKING-ORDER") String bookingOrder)
    {
        logger.info("Booking Request" + record);
        logger.info("Special Booking Order: " + bookingOrder);
        String cookie = "XSRF-TOKEN=" + xsrftoken + "; SESSION=" + session;
        return bookingComponent.book(record,
                                     cookie);
    }

    @RequestMapping("/get/{id}")
    BookingRecord getBooking(@PathVariable long id)
    {
        logger.info("BookingOrder asked for id: " + id);
        return bookingComponent.getBooking(id);
    }
}
