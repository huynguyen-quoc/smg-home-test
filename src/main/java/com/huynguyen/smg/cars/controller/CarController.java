package com.huynguyen.smg.cars.controller;

import com.huynguyen.smg.cars.dto.PageResponse;
import com.huynguyen.smg.cars.model.Car;
import com.huynguyen.smg.cars.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * Controller class for handling car search and pagination operations.
 */
@RestController
@RequestMapping("v1/cars")
@RequiredArgsConstructor
@Slf4j
public class CarController {

    private final CarService carService;
    /**
     * Search for cars based on a query and provide pagination.
     *
     * @param query    The search query (optional).
     * @param pageable The pagination parameters (page number, page size, and sorting).
     * @return A ResponseEntity containing the paginated list of cars and metadata.
     */
    @GetMapping
    public ResponseEntity<PageResponse<Car>> search(@RequestParam(required = false, name = "q") String query,
        @PageableDefault Pageable pageable) {
        log.info("search car by query=[{}] paging=[{}]", query, pageable);
        int pageSize = pageable.getPageSize();
        if (pageSize > 50) {
            pageable = PageRequest.of(pageable.getPageNumber(), 1, pageable.getSort());
        }
        Page<Car> carPage = carService.search(query, pageable);

        return ResponseEntity.ok(
            PageResponse.<Car>builder().content(carPage.getContent()).totalPages(carPage.getTotalPages())
                .totalElements(carPage.getTotalElements()).page(carPage.getNumber()).build());
    }
}
