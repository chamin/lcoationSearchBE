package com.cham.demo.exception;

public class WeatherLookupException extends RuntimeException {
    public WeatherLookupException(String message, Throwable cause) {
        super(message, cause);
    }
}
