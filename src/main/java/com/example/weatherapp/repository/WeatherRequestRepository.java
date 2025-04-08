package com.example.weatherapp.repository;

import com.example.weatherapp.model.WeatherRequest;

import org.springframework.data.jpa.repository.JpaRepository;


public interface WeatherRequestRepository extends JpaRepository<WeatherRequest, Long> {
}
