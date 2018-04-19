package com.example.webfluxrest.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Builder
public class Vendor {

    @Id
    private String Id;

    private String firstName;
    private String lastName;
}
