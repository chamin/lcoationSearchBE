package com.cham.demo.client;

import com.cham.demo.exception.WeatherLookupException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.service.annotation.GetExchange;

public interface WeatherClient {

    @GetExchange("/forecast")
    WeatherResponse getForecast(@RequestParam double latitude, @RequestParam double longitude, @RequestParam String current);

    default Double getCurrentTemperature(double latitude, double longitude) {
        try {
            WeatherResponse response = getForecast(latitude, longitude, "temperature_2m");
            return response != null && response.getCurrent() != null
                    ? response.getCurrent().getTemperature2m()
                    : null;
        } catch (RestClientException ex) {
            throw new WeatherLookupException(
                    "Failed to fetch temperature for latitude=" + latitude + ", longitude=" + longitude, ex);
        }
    }
}
