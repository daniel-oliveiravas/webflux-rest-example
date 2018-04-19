package com.example.webfluxrest.bootstrap;

import com.example.webfluxrest.domain.Category;
import com.example.webfluxrest.domain.Vendor;
import com.example.webfluxrest.repository.CategoryRepository;
import com.example.webfluxrest.repository.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;

    public Bootstrap(CategoryRepository categoryRepository, VendorRepository vendorRepository) {
        this.categoryRepository = categoryRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (categoryRepository.count().block() == 0) {
            categoryRepository.save(Category.builder().description("First Category").build()).block();
        }

        if (vendorRepository.count().block() == 0) {
            vendorRepository.save(Vendor.builder().firstName("Daniel").lastName("Vasconcelos").build()).block();
        }
    }
}
