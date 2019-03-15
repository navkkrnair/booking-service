package com.ams.booking.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(BookingSink.class)
public class Receiver
{
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    BookingComponent bookingComponent;

    @Autowired
    public Receiver(BookingComponent bookingComponent)
    {
        this.bookingComponent = bookingComponent;
    }

    @StreamListener(BookingSink.CHECKINQ)
    public void processMessage(CheckInMessage checkInDetails)
    {

        logger.info("Customer with booking id : " + checkInDetails.getBookingId() + " CheckedIn");
        bookingComponent.updateStatus(BookingStatus.CHECKED_IN,
                                      checkInDetails.getBookingId());
    }

}