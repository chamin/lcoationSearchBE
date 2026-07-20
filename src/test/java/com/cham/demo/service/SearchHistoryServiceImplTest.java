package com.cham.demo.service;

import com.cham.demo.client.WeatherClient;
import com.cham.demo.entity.SearchHistory;
import com.cham.demo.repository.SearchHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchHistoryServiceImplTest {

    @Mock
    private SearchHistoryRepository repository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private SearchHistoryServiceImpl searchHistoryService;

    @Test
    void saveSearchHistory_resolvesTemperatureAndPersists() {
        SearchHistory history = new SearchHistory();
        history.setGooglePlaceId("place-1");
        history.setLatitude(6.9271);
        history.setLongitude(79.8612);

        when(weatherClient.getCurrentTemperature(6.9271, 79.8612)).thenReturn(29.5);
        when(repository.save(any(SearchHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SearchHistory saved = searchHistoryService.saveSearchHistory(history);

        assertThat(saved.getTemperature()).isEqualTo(29.5);
        verify(weatherClient).getCurrentTemperature(6.9271, 79.8612);

        ArgumentCaptor<SearchHistory> captor = ArgumentCaptor.forClass(SearchHistory.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getTemperature()).isEqualTo(29.5);
    }

    @Test
    void getAllSearchHistory_returnsPageFromRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        SearchHistory history = new SearchHistory();
        Page<SearchHistory> page = new PageImpl<>(List.of(history), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<SearchHistory> result = searchHistoryService.getAllSearchHistory(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).containsExactly(history);
    }

    @Test
    void updateSearchHistory_updatesFieldsAndPersists() {
        SearchHistory existing = new SearchHistory();
        existing.setId(1L);

        SearchHistory update = new SearchHistory();
        update.setGooglePlaceId("place-2");
        update.setLatitude(1.1);
        update.setLongitude(2.2);
        update.setAddress("New Address");
        update.setPlaceName("New Place");
        update.setStatus(false);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(SearchHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SearchHistory result = searchHistoryService.updateSearchHistory(1L, update);

        assertThat(result.getGooglePlaceId()).isEqualTo("place-2");
        assertThat(result.getLatitude()).isEqualTo(1.1);
        assertThat(result.getLongitude()).isEqualTo(2.2);
        assertThat(result.getAddress()).isEqualTo("New Address");
        assertThat(result.getPlaceName()).isEqualTo("New Place");
        assertThat(result.getStatus()).isFalse();
    }

    @Test
    void updateSearchHistory_throwsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> searchHistoryService.updateSearchHistory(99L, new SearchHistory()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).save(any(SearchHistory.class));
        verify(weatherClient, never()).getCurrentTemperature(anyDouble(), anyDouble());
    }
}
