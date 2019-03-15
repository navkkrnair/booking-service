package com.ams.booking.component;

import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ams.booking.entity.BookingRecord;
import com.ams.booking.entity.Inventory;
import com.ams.booking.entity.Passenger;
import com.ams.booking.repository.BookingRepository;
import com.ams.booking.repository.InventoryRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@EnableFeignClients
@Component
public class BookingComponent
{
    private static final Logger logger = LoggerFactory.getLogger(BookingComponent.class);
    BookingRepository           bookingRepository;
    InventoryRepository         inventoryRepository;

    private FareServiceProxy fareservice;
    private RestTemplate     restTemplate;

    BookingSource sender;

    @Autowired
    public BookingComponent(BookingRepository bookingRepository, BookingSource sender, InventoryRepository inventoryRepository, FareServiceProxy fareservice, RestTemplate restTemplate)
    {
        this.bookingRepository   = bookingRepository;
        this.sender              = sender;
        this.inventoryRepository = inventoryRepository;
        this.fareservice         = fareservice;
        this.restTemplate        = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "fareServiceUnavailable", commandProperties= {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")})
    public long book(BookingRecord record, String session)
    {
        logger.info("calling fares to get fare");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", session);
        final HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<Fare>     fare   = restTemplate
                .exchange("http://fare-service/fares/get?flightNumber=" + record.getFlightNumber()
                        + "&flightDate=" + record.getFlightDate(),
                          HttpMethod.GET,
                          entity,
                          Fare.class);
        /*Fare fare = fareservice.getFare(record.getFlightNumber(),
                                        record.getFlightDate(),
                                        session);*/
        logger.info("Got fare " + fare + "\n from fareservice");
        // check fare
        if (!record.getFare()
                .equals(fare.getBody()
                        .getFare()))
            throw new BookingException("fare is tampered");
        logger.info("calling inventory to get inventory");
        // check inventory
        Inventory inventory = inventoryRepository
                .findByFlightNumberAndFlightDate(record.getFlightNumber(),
                                                 record.getFlightDate());
        if (!inventory.isAvailable(record.getPassengers()
                .size()))
        {
            throw new BookingException("No more seats avaialble");
        }
        logger.info("successfully checked inventory" + inventory);
        logger.info("calling inventory to update inventory");
        // update inventory
        inventory.setAvailable(inventory.getAvailable() - record.getPassengers()
                .size());
        inventoryRepository.saveAndFlush(inventory);
        logger.info("sucessfully updated inventory");
        // save booking
        record.setStatus(BookingStatus.BOOKING_CONFIRMED);
        Set<Passenger> passengers = record.getPassengers();
        passengers.forEach(passenger -> passenger.setBookingRecord(record));
        record.setBookingDate(new Date());
        long id = bookingRepository.save(record)
                .getId();
        logger.info("Successfully saved booking");
        // send a message to search to update inventory
        logger.info("sending a booking event");
        BookingConfirmationMessage bookingDetails = new BookingConfirmationMessage();
        bookingDetails.setFlightNumber(record.getFlightNumber());
        bookingDetails.setFlightDate(record.getFlightDate());
        bookingDetails.setInventory(inventory.getBookableInventory());
        sender.searchQ()
                .send(MessageBuilder.withPayload(bookingDetails)
                        .build());
        logger.info("booking event successfully delivered " + bookingDetails);
        return id;
    }

    public BookingRecord getBooking(long id)
    {
        return bookingRepository.findById(id)
                .get();
    }

    public void updateStatus(String status, long bookingId)
    {
        BookingRecord record = bookingRepository.findById(bookingId)
                .get();
        record.setStatus(status);
    }

    public long fareServiceUnavailable(BookingRecord record, String session)
    {
        logger.info("Fare Service is unavailable at the moment");
        return 0;
    }
}
