package com.huynguyen.smg.cars.service;

import com.huynguyen.smg.cars.model.Car;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {

    void saveAll(List<Car> car);

    void deleteAll(List<Car> car);

    Page<Car> search(String query, Pageable page);

}
