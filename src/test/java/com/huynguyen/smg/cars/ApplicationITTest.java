package com.huynguyen.smg.cars;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.huynguyen.smg.cars.model.Car;
import com.huynguyen.smg.cars.service.CarService;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ApplicationITTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    CarService carService;

    @Test
    @DisplayName("search car with multiple states")
    public void searchCars() throws Exception {
        Car car = new Car();
        car.setId("1");
        car.setMake("Honda");
        car.setYear(2023);
        car.setModel("Civic");
        carService.saveAll(Collections.singletonList(car));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/cars?q=make:h* &page=0&size=10&sort=make,asc"))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().json(
                "{\"content\":[{\"id\":\"1\",\"make\":\"Honda\",\"model\":\"Civic\",\"year\":2023}],\"page\":0,\"totalPages\":1,\"totalElements\":1}"));

        Car car2 = new Car();
        car2.setId("2");
        car2.setMake("Toyota");
        car2.setYear(2022);
        car2.setModel("Camry");
        carService.saveAll(Collections.singletonList(car2));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/cars?page=0&size=10"))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().json(
                "{\"content\":[{\"id\":\"1\",\"make\":\"Honda\",\"model\":\"Civic\",\"year\":2023},{\"id\":\"2\",\"make\":\"Toyota\",\"model\":\"Camry\",\"year\":2022}],\"page\":0,\"totalPages\":1,\"totalElements\":2}"));

    }


}