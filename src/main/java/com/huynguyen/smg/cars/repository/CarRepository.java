package com.huynguyen.smg.cars.repository;

import com.huynguyen.smg.cars.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends ElasticsearchRepository<Car, String> {
    @Query("{\"query_string\": {\"query\": \"?0\"}}")
    Page<Car> searchBy(String query, Pageable pageable);
}