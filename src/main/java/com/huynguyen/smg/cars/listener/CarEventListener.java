package com.huynguyen.smg.cars.listener;

import com.huynguyen.smg.cars.model.Car;
import com.huynguyen.smg.cars.model.EventMessage;
import com.huynguyen.smg.cars.service.CarService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
@KafkaListener(topics = "${event.listener.car.topic}")
public class CarEventListener {

    private final CarService carService;


    // Other fields and dependencies

    /**
     * Kafka message listener that processes a batch of EventMessage records.
     * This method groups events by their eventType, extracts associated Car objects,
     * and performs appropriate actions such as saving and deleting cars based on the event type.
     *
     * @param records       The list of EventMessage records received from Kafka.
     * @param acknowledgment The Kafka acknowledgment object to manually acknowledge the message processing.
     */
    @KafkaHandler
    @CacheEvict(value = "car-caches", allEntries = true)
    public void onMessage(List<EventMessage> records, Acknowledgment acknowledgment) {
        try {
            Map<String, List<Car>> mapEvent = records.stream()
                .filter(s -> Objects.nonNull(s.getEventType()))
                .collect(Collectors.groupingBy(
                    EventMessage::getEventType,
                    Collectors.mapping(EventMessage::getCar, Collectors.toList())
                ));
            List<Car> createdList = mapEvent.getOrDefault("car_listing_created", new ArrayList<>());
            List<Car> updatedList = mapEvent.getOrDefault("car_listing_updated", new ArrayList<>());
            List<Car> deletedList = mapEvent.getOrDefault("car_listing_deleted", new ArrayList<>());
            List<Car> saveList = new ArrayList<>();
            saveList.addAll(createdList);
            saveList.addAll(updatedList);
            carService.saveAll(saveList);
            carService.deleteAll(deletedList);
        } catch (Exception e) {
            log.error("got problems to process car from kafka", e);
        } finally {
            acknowledgment.acknowledge();
        }
    }


}
