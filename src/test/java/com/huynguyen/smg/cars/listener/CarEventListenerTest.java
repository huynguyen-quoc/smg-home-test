package com.huynguyen.smg.cars.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huynguyen.smg.cars.model.Car;
import com.huynguyen.smg.cars.model.EventMessage;
import com.huynguyen.smg.cars.repository.CarRepository;
import com.huynguyen.smg.cars.service.CarService;
import com.huynguyen.smg.cars.service.impl.CarServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class CarEventListenerTest {

    private CarEventListener carEventListener;

    @Mock
    private CarRepository carRepository;

    @Mock
    private Validator validator;

    @BeforeEach
    void beforeEach() {
        CarService carService = new CarServiceImpl(validator, carRepository);
        carEventListener = new CarEventListener(carService);
    }

    @Test
    void onMessage() {
        EventMessage eventMessage = mock(EventMessage.class);
        when(eventMessage.getEventType()).thenReturn("car_listing_created");
        Car car = new Car();
        car.setId("1");
        car.setMake("Honda");
        car.setYear(2023);
        car.setModel("Civic");
        when(eventMessage.getCar()).thenReturn(car);
        EventMessage eventMessage2 = mock(EventMessage.class);
        when(eventMessage2.getEventType()).thenReturn("car_listing_updated");
        Car car2 = new Car();
        car2.setId("2");
        car2.setMake("Honda2");
        car2.setYear(2023);
        car2.setModel("Civic2");
        when(eventMessage2.getCar()).thenReturn(car2);
        EventMessage eventMessage3 = mock(EventMessage.class);
        when(eventMessage3.getEventType()).thenReturn("car_listing_deleted");
        Car car3 = new Car();
        car3.setId("3");
        car3.setMake("Honda2");
        car3.setYear(2023);
        car3.setModel("Civic2");
        when(eventMessage3.getCar()).thenReturn(car3);
        Acknowledgment acknowledgment = mock(Acknowledgment.class);
        carEventListener.onMessage(Arrays.asList(eventMessage, eventMessage2, eventMessage3), acknowledgment);

        verify(acknowledgment, timeout(1)).acknowledge();
        ArgumentCaptor<List<Car>> captor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Car>> captorDeleted = ArgumentCaptor.forClass(List.class);
        verify(carRepository, times(1)).saveAll(captor.capture());
        verify(carRepository, times(1)).deleteAll(captorDeleted.capture());

        assertThat(captor.getValue()).containsExactly(car, car2);
        assertThat(captorDeleted.getValue()).containsExactly(car3);
    }

    @Test
    void onMessageWithInvalidData() {
        EventMessage eventMessage = mock(EventMessage.class);
        when(eventMessage.getEventType()).thenReturn("car_listing_created");
        Car car = new Car();
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        ConstraintViolation<Object> carConstraintViolation = mock(ConstraintViolation.class);
        violations.add(carConstraintViolation);
        when(carConstraintViolation.getMessage()).thenReturn("invalid_data");
        when(carConstraintViolation.getPropertyPath()).thenReturn(mock(Path.class));
        when(validator.validate(any())).thenReturn(violations);
        when(eventMessage.getCar()).thenReturn(car);
        Acknowledgment acknowledgment = mock(Acknowledgment.class);
        carEventListener.onMessage(Collections.singletonList(eventMessage), acknowledgment);

        verify(acknowledgment, times(1)).acknowledge();
        verify(carRepository, never()).saveAll(any());
    }
}