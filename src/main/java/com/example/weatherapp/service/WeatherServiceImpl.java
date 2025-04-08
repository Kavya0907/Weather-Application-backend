package com.example.weatherapp.service;

import com.example.weatherapp.dto.WeatherRequestDTO;
import com.example.weatherapp.model.WeatherRequest;
import com.example.weatherapp.repository.WeatherRequestRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    private final WeatherRequestRepository repository;
    private final ObjectMapper objectMapper;
    
    @Value("${weather.api.key}")
    private String weatherApiKey;
    
    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    @Override
    public WeatherRequest getCurrentWeather(String location) {
        try {
            String weatherData = fetchWeatherData(location, false);
            JsonNode json = objectMapper.readTree(weatherData);
            
            WeatherRequest request = new WeatherRequest();
            request.setLocation(json.get("name").asText());
            request.setTemperature(json.get("main").get("temp").asDouble());
            request.setHumidity(json.get("main").get("humidity").asDouble());
            request.setWindSpeed(json.get("wind").get("speed").asDouble());
            request.setWeatherDescription(json.get("weather").get(0).get("description").asText());
            request.setWeatherIcon(json.get("weather").get(0).get("icon").asText());
            return request;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch weather: " + e.getMessage());
        }
    }

    @Override
    public List<WeatherRequest> getFiveDayForecast(String location) {
        try {
            String forecastData = fetchWeatherData(location, true);
            JsonNode json = objectMapper.readTree(forecastData);
            List<WeatherRequest> forecast = new ArrayList<>();
            JsonNode list = json.get("list");
            
            for (int i = 0; i < list.size() && forecast.size() < 5; i += 8) { // 3-hour intervals, 8/day
                JsonNode day = list.get(i);
                WeatherRequest dayForecast = new WeatherRequest();
                dayForecast.setLocation(json.get("city").get("name").asText());
                dayForecast.setTemperature(day.get("main").get("temp").asDouble());
                dayForecast.setHumidity(day.get("main").get("humidity").asDouble());
                dayForecast.setWindSpeed(day.get("wind").get("speed").asDouble());
                dayForecast.setWeatherDescription(day.get("weather").get(0).get("description").asText());
                dayForecast.setWeatherIcon(day.get("weather").get(0).get("icon").asText());
                dayForecast.setRequestDate(LocalDateTime.parse(day.get("dt_txt").asText().replace(" ", "T")));
                forecast.add(dayForecast);
            }
            return forecast;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch forecast: " + e.getMessage());
        }
    }

    @Override
    public WeatherRequest saveWeatherRequest(WeatherRequestDTO dto) {
        validateInput(dto);
        WeatherRequest request = getCurrentWeather(dto.getLocation());
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        return repository.save(request);
    }

    @Override
    public List<WeatherRequest> getAllWeatherRequests() {
        return repository.findAll();
    }

    @Override
    public WeatherRequest updateWeatherRequest(Long id, WeatherRequestDTO dto) {
        WeatherRequest existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Record not found"));
        validateInput(dto);
        WeatherRequest updated = getCurrentWeather(dto.getLocation());
        existing.setLocation(dto.getLocation());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setTemperature(updated.getTemperature());
        existing.setHumidity(updated.getHumidity());
        existing.setWindSpeed(updated.getWindSpeed());
        existing.setWeatherDescription(updated.getWeatherDescription());
        existing.setWeatherIcon(updated.getWeatherIcon());
        return repository.save(existing);
    }

    @Override
    public void deleteWeatherRequest(Long id) {
        repository.deleteById(id);
    }

    @Override
    public String exportToCSV() {
        List<WeatherRequest> requests = repository.findAll();
        String fileName = "weather_data.csv";
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] header = {"ID", "Location", "Start Date", "End Date", "Temperature", "Humidity", "Wind Speed", "Description", "Request Date"};
            writer.writeNext(header);
            for (WeatherRequest r : requests) {
                writer.writeNext(new String[]{
                    String.valueOf(r.getId()), r.getLocation(), r.getStartDate().toString(), r.getEndDate().toString(),
                    String.valueOf(r.getTemperature()), String.valueOf(r.getHumidity()), String.valueOf(r.getWindSpeed()),
                    r.getWeatherDescription(), r.getRequestDate().toString()
                });
            }
            return "Exported to " + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Export failed: " + e.getMessage());
        }
    }

    @Override
    public String getYoutubeVideoUrl(String location) {
        try {
            
            String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            // First attempt: live videos for today
            String liveUrlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" +
                    URLEncoder.encode(location + " weather live", StandardCharsets.UTF_8) +
                    "&type=video&eventType=live&publishedAfter=" + currentDate + "T00:00:00Z" +
                    "&publishedBefore=" + currentDate + "T23:59:59Z" +
                    "&maxResults=1&key=" + youtubeApiKey;
            
            String liveResponse = fetchApiData(liveUrlString);
            JsonNode liveJson = objectMapper.readTree(liveResponse);
            
            // Check if there are any live videos from today
            if (liveJson.get("items").size() > 0) {
                String liveVideoId = liveJson.get("items").get(0).get("id").get("videoId").asText();
                return "https://www.youtube.com/watch?v=" + liveVideoId;
            }
            
            // Fallback: most recent weather video (no date restriction)
            String recentUrlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" +
                    URLEncoder.encode(location + " weather", StandardCharsets.UTF_8) +
                    "&type=video&maxResults=1&order=date&key=" + youtubeApiKey;
            
            String recentResponse = fetchApiData(recentUrlString);
            JsonNode recentJson = objectMapper.readTree(recentResponse);
            
            if (recentJson.get("items").size() > 0) {
                String recentVideoId = recentJson.get("items").get(0).get("id").get("videoId").asText();
                return "https://www.youtube.com/watch?v=" + recentVideoId;
            }
            
            throw new RuntimeException("No weather videos found for location: " + location);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch YouTube video: " + e.getMessage());
        }
    }


    private String fetchWeatherData(String location, boolean isForecast) throws IOException {
        String urlString;
        if (isForecast) {
            urlString = "http://api.openweathermap.org/data/2.5/forecast?q=" +
                    URLEncoder.encode(location, StandardCharsets.UTF_8) + "&appid=" + weatherApiKey + "&units=metric";
        } else if (location.matches("-?\\d+\\.\\d+,-?\\d+\\.\\d+")) {
            String[] coords = location.split(",");
            urlString = "http://api.openweathermap.org/data/2.5/weather?lat=" + coords[0] +
                    "&lon=" + coords[1] + "&appid=" + weatherApiKey + "&units=metric";
        } else if (location.matches("\\d{5}")) {
            urlString = "http://api.openweathermap.org/data/2.5/weather?zip=" +
                    location + "&appid=" + weatherApiKey + "&units=metric";
        } else {
            urlString = "http://api.openweathermap.org/data/2.5/weather?q=" +
                    URLEncoder.encode(location, StandardCharsets.UTF_8) + "&appid=" + weatherApiKey + "&units=metric";
        }
        return fetchApiData(urlString);
    }

    private String fetchApiData(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()))) {
            return reader.lines().collect(Collectors.joining());
        } finally {
            conn.disconnect();
        }
    }

    private void validateInput(WeatherRequestDTO dto) {
        if (dto.getLocation() == null || dto.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
        if (dto.getStartDate() == null || dto.getEndDate() == null || dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Invalid date range");
        }
    }
}