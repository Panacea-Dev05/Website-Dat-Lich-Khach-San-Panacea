package com.panacea.hotel.repository;

import com.panacea.hotel.entity.BookingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingServiceRepository extends JpaRepository<BookingService, Long> {
    
    List<BookingService> findByBookingId(Long bookingId);
    
    List<BookingService> findByServiceId(Long serviceId);
    
    void deleteByBookingId(Long bookingId);
    
    void deleteByServiceId(Long serviceId);
} 