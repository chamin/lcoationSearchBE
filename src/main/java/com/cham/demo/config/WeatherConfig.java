package com.cham.demo.config;

import com.cham.demo.client.WeatherClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(group = "weather", types = WeatherClient.class)
public class WeatherConfig {
}
