package com.example.webfluxrest.controller;

import com.example.webfluxrest.domain.Category;
import com.example.webfluxrest.domain.Vendor;
import com.example.webfluxrest.repository.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

public class VendorControllerTest {

    public static final String CONTOLLER_BASE_URL = "/api/v1/vendors/";
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
                .uri(CONTOLLER_BASE_URL)
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
                .uri(CONTOLLER_BASE_URL + vendorId)
                .exchange()
                .expectBodyList(Vendor.class);
    }

    @Test
    public void createVendor() {
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().build()));

        Mono<Vendor> vendorMono = Mono.just(Vendor.builder().id("id").build());
        webTestClient.post()
                .uri(CONTOLLER_BASE_URL)
                .body(vendorMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }
}
