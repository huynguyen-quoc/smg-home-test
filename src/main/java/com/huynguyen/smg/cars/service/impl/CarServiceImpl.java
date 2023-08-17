package com.huynguyen.smg.cars.service.impl;

import com.huynguyen.smg.cars.model.Car;
import com.huynguyen.smg.cars.repository.CarRepository;
import com.huynguyen.smg.cars.service.CarService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final Validator validator;
    private final CarRepository carRepository;
    /**
     * Saves a list of cars after validating them using the provided Validator.
     *
     * @param cars The list of cars to be saved.
     * @throws IllegalArgumentException If validation errors are detected in the car objects.
     */
    @Override
    public void saveAll(List<Car> cars) {
        for (Car car : cars) {
            Set<ConstraintViolation<Car>> violations = validator.validate(car);
            if (!violations.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (ConstraintViolation<Car> constraintViolation : violations) {
                    sb.append(constraintViolation.getPropertyPath().toString());
                    sb.append(":");
                    sb.append(constraintViolation.getMessage());
                }
                throw new IllegalArgumentException("Error occurred: " + sb);
            }
        }

        carRepository.saveAll(cars);
    }

    /**
     * Deletes a list of cars.
     *
     * @param cars The list of cars to be deleted.
     */
    @Override
    public void deleteAll(List<Car> cars) {
        carRepository.deleteAll(cars);
    }
    /**
     * Searches for cars based on a query and provides pagination.
     * If the query is blank, returns all cars using pagination.
     *
     * @param query The search query (optional).
     * @param page  The pagination parameters (page number, page size, and sorting).
     * @return A Page containing the search results.
     */
    @Override
    @Cacheable(value = "car-caches", key = "{ #query, #page.pageNumber, #page.pageSize, #page.sort }")
    public Page<Car> search(String query, Pageable page) {
        if (StringUtils.isBlank(query)) {
            return carRepository.findAll(page);
        }
        return carRepository.searchBy(query, page);
    }
}
