package com.example.demo.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @InjectMocks
    ProductService productService;
    
    @Mock
     ProductRepository productRepository;
     
     @Mock
     CategoryRepository categoryRepository;
     
     @Test
     void getAllProducts(){
        productService.getAllProducts();
        Mockito.verify(productRepository).findAll();
     }

}
