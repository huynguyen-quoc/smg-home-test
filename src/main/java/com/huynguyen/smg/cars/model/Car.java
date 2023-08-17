package com.huynguyen.smg.cars.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

@Data
@Document(indexName = "car_listings")
@NoArgsConstructor
public class Car {

    @Id
    @NotBlank
    private String id;

    @Field(type = FieldType.Text, fielddata = true)
    @Mapping
    @NotBlank
    private String make;

    @Field(type = FieldType.Text, fielddata = true)
    @NotBlank
    private String model;

    @Field(type = FieldType.Integer)
    @NotNull
    private int year;
}