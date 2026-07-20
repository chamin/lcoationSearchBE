package com.cham.demo.service;

import com.cham.demo.entity.SearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * SearchHistoryService
 */
@Service
public interface SearchHistoryService {
    SearchHistory saveSearchHistory(SearchHistory history);

    Page<SearchHistory> getAllSearchHistory(Pageable pageRequest);

    SearchHistory updateSearchHistory(Long id, SearchHistory history);

}
