package com.example.webfluxrest.controller;

import com.example.webfluxrest.domain.Category;
import com.example.webfluxrest.repository.CategoryRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class CategoryControllerTest {

    private WebTestClient webTestClient;
    private CategoryRepository categoryRepository;
    private CategoryController categoryController;

    private static String CONTROLLER_BASE_URL = "/api/v1/categories/";

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
        given(categoryRepository.findAll())
                .willReturn(Flux.just(
                        Category.builder().description(categoryDescription).build(),
                        Category.builder().description(secondDescription).build()
                ));

        webTestClient.get()
                .uri(CONTROLLER_BASE_URL)
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        String categoryId = UUID.randomUUID().toString();
        given(categoryRepository.findById(categoryId))
                .willReturn(Mono.just(Category.builder().id(categoryId).build()));

        webTestClient.get()
                .uri(CONTROLLER_BASE_URL + categoryId)
                .exchange()
                .expectBodyList(Category.class);
    }

    @Test
    public void createCategory() {
        given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().id("id").build()));

        Mono<Category> categoryMono = Mono.just(Category.builder().id("id").build());
        webTestClient.post()
                .uri(CONTROLLER_BASE_URL)
                .body(categoryMono, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void updateCategory() {
        String categoryId = "id";
        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdate = Mono.just(Category.builder().id(categoryId).build());

        webTestClient.put()
                .uri(CONTROLLER_BASE_URL + categoryId)
                .body(categoryToUpdate, Category.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void patchCategory() {
        String description = "description";
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().description(description).build()));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdate = Mono.just(Category.builder().description(description).build());

        webTestClient.patch()
                .uri(CONTROLLER_BASE_URL + "id")
                .body(categoryToUpdate, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository, never()).save(any());
    }

    @Test
    public void patchCategoryChangingDescription() {
        String categoryId = "id";
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().description("Description").build()));

        String newDescription = "new";
        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().description(newDescription).build()));

        Mono<Category> categoryToUpdate = Mono.just(Category.builder().id(categoryId)
                .description(newDescription).build());

        webTestClient.patch()
                .uri(CONTROLLER_BASE_URL + categoryId)
                .body(categoryToUpdate, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository).save(any());
    }
}
