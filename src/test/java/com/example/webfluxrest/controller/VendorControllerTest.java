package com.example.webfluxrest.controller;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;

public class VendorControllerTest {

    private static final String CONTOLLER_BASE_URL = "/api/v1/vendors/";
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
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorMono = Mono.just(Vendor.builder().id("id").build());
        webTestClient.post()
                .uri(CONTOLLER_BASE_URL)
                .body(vendorMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void updateVendor() {
        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMono = Mono.just(Vendor.builder().id("id").build());
        webTestClient.put()
                .uri(CONTOLLER_BASE_URL + "id")
                .body(vendorMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void patchVendorNoChanges() {
        String firstName = "firstName";
        String lastName = "lastName";

        BDDMockito.given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().firstName(firstName).lastName(lastName).build()));

        Mono<Vendor> vendorMono = Mono.just(Vendor.builder().firstName(firstName).lastName(lastName).build());

        webTestClient.patch()
                .uri(CONTOLLER_BASE_URL + "id")
                .body(vendorMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        BDDMockito.verify(vendorRepository, never()).save(any());
    }

    @Test
    public void patchVendorChangingFirstName() {
        String firstName = "firstName";
        String lastName = "lastName";

        BDDMockito.given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().firstName(firstName).lastName(lastName).build()));

        String newFirstName = "new first name";
        Mono<Vendor> vendorMono = Mono.just(Vendor.builder().firstName(newFirstName).lastName(lastName).build());

        webTestClient.patch()
                .uri(CONTOLLER_BASE_URL + "id")
                .body(vendorMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        BDDMockito.verify(vendorRepository).save(any());
    }
}
