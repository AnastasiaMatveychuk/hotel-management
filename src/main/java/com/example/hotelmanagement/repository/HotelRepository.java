package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByNameContaining(String name);
    List<Hotel> findByStars(Integer stars);
}
