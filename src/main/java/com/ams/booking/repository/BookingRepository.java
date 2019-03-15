package com.ams.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ams.booking.entity.BookingRecord;

public interface BookingRepository extends JpaRepository<BookingRecord, Long>
{

}
