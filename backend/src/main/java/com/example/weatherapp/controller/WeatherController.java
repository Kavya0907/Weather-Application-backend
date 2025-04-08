package com.example.weatherapp.controller;

import com.example.weatherapp.dto.WeatherRequestDTO;
import com.example.weatherapp.model.WeatherRequest;
import com.example.weatherapp.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<WeatherRequest> getCurrentWeather(@RequestParam String location) {
        return ResponseEntity.ok(weatherService.getCurrentWeather(location));
    }

    @GetMapping("/forecast")
    public ResponseEntity<List<WeatherRequest>> getFiveDayForecast(@RequestParam String location) {
        return ResponseEntity.ok(weatherService.getFiveDayForecast(location));
    }

    @PostMapping
    public ResponseEntity<WeatherRequest> saveWeatherRequest(@RequestBody WeatherRequestDTO dto) {
        return ResponseEntity.ok(weatherService.saveWeatherRequest(dto));
    }

    @GetMapping
    public ResponseEntity<List<WeatherRequest>> getAllWeatherRequests() {
        return ResponseEntity.ok(weatherService.getAllWeatherRequests());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeatherRequest> updateWeatherRequest(@PathVariable Long id, @RequestBody WeatherRequestDTO dto) {
        return ResponseEntity.ok(weatherService.updateWeatherRequest(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeatherRequest(@PathVariable Long id) {
        weatherService.deleteWeatherRequest(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportToCSV() {
        return ResponseEntity.ok(weatherService.exportToCSV());
    }

    @GetMapping("/video")
    public ResponseEntity<String> getVideoUrl(@RequestParam String location) {
        return ResponseEntity.ok(weatherService.getYoutubeVideoUrl(location));
    }

    @ExceptionHandler({RuntimeException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}