# Location Search BE

A Spring Boot REST API that saves and retrieves location "search history" records. Each saved
record is enriched with the current temperature at its coordinates, fetched from the
[Open-Meteo](https://open-meteo.com) weather API.

## Tech Stack

- Java 17
- Spring Boot 4.1.0 (Web, Data JPA, Actuator, RestClient)
- H2 in-memory database
- Log4j2 (file + console logging)
- Lombok
- JUnit 5, Mockito, AssertJ (tests)

## Prerequisites

- JDK 17+
- Maven (or use the bundled `./mvnw` wrapper)

## Running the app

```bash
./mvnw spring-boot:run
```

The app starts on port `8081` by default (see `server.port` in
[application.properties](src/main/resources/application.properties)).

## API

| Method | Path                       | Description                                              |
|--------|----------------------------|------------------------------------------------------------|
| POST   | `/api/search-history`      | Create a search history record (fetches temperature)     |
| GET    | `/api/search-history`      | List records, paginated (`?page=0&size=10`)               |
| PUT    | `/api/search-history/{id}` | Update an existing record                                  |

### Example: create a record or Use given postman collection

```bash
curl -X POST http://localhost:8081/api/search-history \
  -H "Content-Type: application/json" \
  -d '{
        "googlePlaceId": "ChIJ...",
        "latitude": 6.9271,
        "longitude": 79.8612,
        "address": "KLCC, Malaysia",
        "placeName": "Malaysia",
        "status": true
      }'
```

The response includes a `temperature` field populated from Open-Meteo based on the given
coordinates.

### SearchHistory fields

| Field         | Notes                                    |
|---------------|-------------------------------------------|
| `id`          | Auto-generated                             |
| `googlePlaceId` | Required                                 |
| `latitude` / `longitude` | Required                        |
| `address`     | Required                                   |
| `placeName`   | Required                                   |
| `temperature` | Server-populated from Open-Meteo, read-only |
| `status`      | Required                                   |
| `createdDt` / `updatedDt` | Managed by JPA                  |

## Configuration

Key properties in `application.properties`:

```properties
server.port=8081
spring.datasource.url=jdbc:h2:mem:TESTDB
spring.http.serviceclient.weather.base-url=https://api.open-meteo.com/v1
```

The H2 console is available at `http://localhost:8081/h2-console` while the app is running.

## Error handling

All exceptions are handled centrally by `GlobalExceptionHandler`, returning a consistent JSON
error body:

```json
{ "timestamp": "...", "status": 404, "error": "Not Found", "message": "..." }
```

## Logging

Logging is configured via Log4j2 (`log4j2-spring.xml`). Logs are written to console and to
`logs/app.log` (daily/size-based rotation). Every request and response is logged by
`RequestResponseLoggingFilter`, and the service layer logs key operations (save/update/fetch).

## Testing

```bash
./mvnw test
```

## Project Structure

```
src/main/java/com/cham/demo
├── client       # WeatherClient (declarative HTTP interface) + response DTO
├── config       # RestClient / HTTP service registration
├── controller   # REST endpoints
├── entity       # JPA entities
├── exception    # Global exception handler + custom exceptions
├── filter       # Request/response logging filter
├── repository   # Spring Data JPA repositories
└── service      # Business logic
```
