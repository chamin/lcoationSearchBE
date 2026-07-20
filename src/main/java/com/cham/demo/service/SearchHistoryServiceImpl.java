package com.cham.demo.service;

import com.cham.demo.client.WeatherClient;
import com.cham.demo.entity.SearchHistory;
import com.cham.demo.repository.SearchHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SearchHistoryService implementation
 */
@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {
    private static final Logger log = LogManager.getLogger(SearchHistoryServiceImpl.class);

    private final SearchHistoryRepository repository;
    private final WeatherClient weatherClient;

    public SearchHistoryServiceImpl(SearchHistoryRepository repository, WeatherClient weatherClient) {
        this.repository = repository;
        this.weatherClient = weatherClient;
    }

    /**
     * Resolves the current temperature for the given coordinates via WeatherClient and
     * save SearchHistory
     */
    @Override
    public SearchHistory saveSearchHistory(SearchHistory history) {
        log.info("Saving search history for googlePlaceId={} lat={} lon={}",
                history.getGooglePlaceId(), history.getLatitude(), history.getLongitude());

        Double temperature = weatherClient.getCurrentTemperature(history.getLatitude(), history.getLongitude());
        log.info("Resolved temperature={} for lat={} lon={}", temperature, history.getLatitude(), history.getLongitude());
        history.setTemperature(temperature);

        SearchHistory saved = repository.save(history);
        log.info("Saved search history id={}", saved.getId());
        return saved;
    }

    /**
     * Returns a page of search history records ordered as stored, with no filtering.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SearchHistory> getAllSearchHistory(Pageable pageRequest) {
        log.info("Fetching search history page={} size={}", pageRequest.getPageNumber(), pageRequest.getPageSize());
        Page<SearchHistory> result = repository.findAll(pageRequest);
        log.info("Fetched {} of {} search history records", result.getNumberOfElements(), result.getTotalElements());
        return result;
    }

    /**
     * Updates an existing search history record in place.
     *
     * @throws EntityNotFoundException if no record exists
     */
    @Override
    @Transactional
    public SearchHistory updateSearchHistory(Long id, SearchHistory history) {
        log.info("Updating search history id={}", id);
        SearchHistory existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SearchHistory not found with id " + id));
        existing.setGooglePlaceId(history.getGooglePlaceId());
        existing.setLongitude(history.getLongitude());
        existing.setLatitude(history.getLatitude());
        existing.setAddress(history.getAddress());
        existing.setPlaceName(history.getPlaceName());
        existing.setStatus(history.getStatus());
        SearchHistory saved = repository.save(existing);
        log.info("Updated search history id={}", saved.getId());
        return saved;
    }
}
