package com.ams.booking.component;

public class BookingException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public BookingException(String message)
    {
        super(message);
    }
}
