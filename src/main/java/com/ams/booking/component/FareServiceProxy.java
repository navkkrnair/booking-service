package com.ams.booking.component;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fare-service")
public interface FareServiceProxy
{
    @RequestMapping(value = "/fares/get", method = RequestMethod.GET)
    Fare getFare(@RequestParam(value = "flightNumber") String flightNumber, @RequestParam(value = "flightDate") String flightDate, @RequestHeader("Cookie") String session);

}
