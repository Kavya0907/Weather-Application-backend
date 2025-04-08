package com.example.weatherapp.service;

import com.example.weatherapp.dto.WeatherRequestDTO;
import com.example.weatherapp.model.WeatherRequest;
import java.util.List;

public interface WeatherService {
	WeatherRequest getCurrentWeather(String location);
    List<WeatherRequest> getFiveDayForecast(String location);
    WeatherRequest saveWeatherRequest(WeatherRequestDTO dto);
    List<WeatherRequest> getAllWeatherRequests();
    WeatherRequest updateWeatherRequest(Long id, WeatherRequestDTO dto);
    void deleteWeatherRequest(Long id);
    String exportToCSV();
    String getYoutubeVideoUrl(String location);

}
