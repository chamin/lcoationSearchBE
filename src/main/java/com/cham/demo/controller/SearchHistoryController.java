package com.cham.demo.controller;

import com.cham.demo.entity.SearchHistory;
import com.cham.demo.service.SearchHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
public class SearchHistoryController {
    private final SearchHistoryService searchHistoryService;

    public SearchHistoryController(SearchHistoryService searchHistoryService) {
        this.searchHistoryService = searchHistoryService;
    }

    @PostMapping("/api/search-history")
    public SearchHistory create(@RequestBody SearchHistory history) {

        return searchHistoryService.saveSearchHistory(history);
    }

    @GetMapping("/api/search-history")
    public Page<SearchHistory> getAllHistoryData(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){

        Pageable pageRequest = PageRequest.of(page, size);
        return searchHistoryService.getAllSearchHistory(pageRequest);
    }

    @PutMapping("/api/search-history/{id}")
    public SearchHistory update(@PathVariable Long id, @RequestBody SearchHistory history){
        return searchHistoryService.updateSearchHistory(id, history);
    }



}
