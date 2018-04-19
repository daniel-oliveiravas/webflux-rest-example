package com.example.webfluxrest.controller;

import com.example.webfluxrest.domain.Category;
import com.example.webfluxrest.repository.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class CategoryControllerTest {

    private WebTestClient webTestClient;
    private CategoryRepository categoryRepository;
    private CategoryController categoryController;


    @Before
    public void setUp() {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    public void getAllCategories() {
        String categoryDescription = "Test category";
        String secondDescription = "Second category";
        BDDMockito.given(categoryRepository.findAll())
                .willReturn(Flux.just(
                        Category.builder().description(categoryDescription).build(),
                        Category.builder().description(secondDescription).build()
                ));

        webTestClient.get()
                .uri("/api/v1/categories/")
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        String categoryId = UUID.randomUUID().toString();
        BDDMockito.given(categoryRepository.findById(categoryId))
                .willReturn(Mono.just(Category.builder().id(categoryId).build()));

        webTestClient.get()
                .uri("/api/v1/categories/" + categoryId)
                .exchange()
                .expectBodyList(Category.class);
    }
}
