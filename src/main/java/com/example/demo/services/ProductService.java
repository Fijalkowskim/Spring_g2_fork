package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.ProductArleadyExistsException;
import com.example.demo.exceptions.ProductIdMustBeGreaterThanZeroExeption;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;


public interface ProductService {
    List<Product> getAllProducts();
    Page<Product> findAllProducts(String keyword,int pageNumber);
    void removeProduct(Long id) throws ProductIdMustBeGreaterThanZeroExeption;
    Optional<Product> findProductById(Long id);
    boolean existProductByName(Product product);
    void insertProduct(Product product) throws ProductArleadyExistsException;
    void updateCurrentProduct(Product product, Long id);
    List<Category> findAllCategories();
    List<Product> findProductByCategoryId(Long categoryId);
}
