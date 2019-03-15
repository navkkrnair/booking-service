package com.ams.booking.component;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface BookingSink
{
	public static String CHECKINQ = "checkInQ";

	@Input(BookingSink.CHECKINQ)
	public MessageChannel checkInQ();

}
