package com.example.webfluxrest.controller;

import com.example.webfluxrest.domain.Category;
import com.example.webfluxrest.repository.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    Flux<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    Mono<Category> getById(@PathVariable String id) {
        return categoryRepository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> createCategory(@RequestBody Publisher<Category> category) {
        return categoryRepository.saveAll(category).then();
    }

    @PutMapping("/{id}")
    public Mono<Category> updateCategory(@PathVariable String id, @RequestBody Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }

    @PatchMapping("/{id}")
    public Mono<Category> patchCategory(@PathVariable String id, @RequestBody Category category) {
        Category categoryFound = categoryRepository.findById(id).block();

        if(!categoryFound.getDescription().equals(category.getDescription())) {
            categoryFound.setDescription(category.getDescription());
            return categoryRepository.save(categoryFound);
        }

        return Mono.just(categoryFound);
    }
}
