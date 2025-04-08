package com.example.weatherapp.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class WeatherRequestDTO {
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
    
}