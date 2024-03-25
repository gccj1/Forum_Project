package com.example.service;

import com.example.entity.vo.request.WeatherVO;

public interface WeatherService {
    WeatherVO fetchWeatherData(double longitude, double latitude);
}
