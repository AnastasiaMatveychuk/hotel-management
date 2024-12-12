package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Hotel;
import com.example.hotelmanagement.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> findById(Long id) {
        return hotelRepository.findById(id);
    }

    public Hotel save(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public void delete(Long id) {
        hotelRepository.deleteById(id);
    }

    public List<Hotel> searchHotels(String name, Integer stars, Double price) {
        if (name != null && !name.isEmpty()) {
            return hotelRepository.findByNameContaining(name);
        }
        if (stars != null) {
            return hotelRepository.findByStars(stars);
        }
        return findAll();
    }

    public Double calculateAveragePrice() {
        return hotelRepository.findAll().stream()
                .mapToDouble(Hotel::getPricePerNight)
                .average()
                .orElse(0.0);
    }
}
