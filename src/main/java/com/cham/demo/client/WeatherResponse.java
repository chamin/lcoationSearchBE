package com.cham.demo.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeatherResponse {
    private Current current;

    @Data
    public static class Current {
        @JsonProperty("temperature_2m")
        private Double temperature2m;
    }
}
