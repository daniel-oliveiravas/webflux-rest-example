package com.example.webfluxrest.controller;

import com.example.webfluxrest.domain.Vendor;
import com.example.webfluxrest.repository.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class VendorControllerTest {

    private WebTestClient webTestClient;
    private VendorRepository vendorRepository;
    private VendorController vendorController;

    @Before
    public void setUp() {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void getAllVendors() {
        String firstVendorName = "first vendor name";
        String secondVendorName = "second vendor name";

        BDDMockito.given(vendorRepository.findAll())
                .willReturn(Flux.just(
                        Vendor.builder().firstName(firstVendorName).build(),
                        Vendor.builder().firstName(secondVendorName).build()
                ));

        webTestClient.get()
                .uri("/api/v1/vendors/")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void getVendorById() {
        String vendorId = UUID.randomUUID().toString();

        BDDMockito.given(vendorRepository.findById(vendorId))
                .willReturn(Mono.just(Vendor.builder().id(vendorId).build()));

        webTestClient.get()
                .uri("/api/v1/vendors/" + vendorId)
                .exchange()
                .expectBodyList(Vendor.class);
    }
}
