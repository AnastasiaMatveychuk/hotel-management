package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Hotel;
import com.example.hotelmanagement.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    // Получение списка всех гостиниц
    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelService.findAll();
    }

    // Получение гостиницы по ID
    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        Optional<Hotel> hotel = hotelService.findById(id);
        return hotel.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создание новой гостиницы
    @PostMapping
    public Hotel createHotel(@RequestBody Hotel hotel) {
        return hotelService.save(hotel);
    }

    // Обновление гостиницы
    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody Hotel hotelDetails) {
        Optional<Hotel> optionalHotel = hotelService.findById(id);
        if (optionalHotel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Hotel hotel = optionalHotel.get();
        hotel.setName(hotelDetails.getName());
        hotel.setPricePerNight(hotelDetails.getPricePerNight());
        hotel.setAvailableRooms(hotelDetails.getAvailableRooms());
        hotel.setStars(hotelDetails.getStars());
        hotel.setContactInfo(hotelDetails.getContactInfo());
        Hotel updatedHotel = hotelService.save(hotel);
        return ResponseEntity.ok(updatedHotel);
    }

    // Удаление гостиницы
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHotel(@PathVariable Long id) {
        if (hotelService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        hotelService.delete(id);
        return ResponseEntity.ok("Hotel deleted successfully");
    }

    // Поиск гостиниц по критериям
    @GetMapping("/search")
    public List<Hotel> searchHotels(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer stars,
            @RequestParam(required = false) Double price
    ) {
        return hotelService.searchHotels(name, stars, price);
    }

    // Статистические данные
    @GetMapping("/statistics/average-price")
    public Double getAveragePrice() {
        return hotelService.calculateAveragePrice();
    }
}
