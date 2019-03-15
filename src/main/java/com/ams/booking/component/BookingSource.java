package com.ams.booking.component;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface BookingSource
{
	public static String SEARCHQ = "searchQ";

	@Output(BookingSource.SEARCHQ)
	public MessageChannel searchQ();

}
