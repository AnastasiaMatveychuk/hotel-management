package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "price_per_night", nullable = false)
    private Double pricePerNight;

    @Column(name = "available_rooms", nullable = false)
    private Integer availableRooms;

    @Column(nullable = false)
    private Integer stars;

    @Column(name = "contact_info", nullable = false)
    private String contactInfo;
}
