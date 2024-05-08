package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.example.demo.exceptions.ProductArleadyExistsException;
import com.example.demo.exceptions.ProductIdMustBeGreaterThanZeroExeption;
import com.example.demo.model.Product;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.services.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @InjectMocks
    ProductServiceImpl productService;
    
    @Mock
     ProductRepository productRepository;
     
     @Mock
     CategoryRepository categoryRepository;
     
     private Product product;
     @BeforeEach
     void setUpProduct(){
      product = new Product();
      product.setName("ExistName");
     }
     

     @Test
     void getAllProductsTest(){
        productService.getAllProducts();
        Mockito.verify(productRepository).findAll();
      
     }
     @Test
     void findAllProductsByKeywordTest(){
      // given
      //when
      productService.findAllProducts("hello", 1);
      //then
      Mockito.verify(productRepository).findAll("hello",PageRequest.of(0,5));
     }
     @Test
     void findAllProductsByKeywordIfItsNULLTest(){
      // given
      //when
      productService.findAllProducts(null, 1);
      //then
      Mockito.verify(productRepository).findAll(PageRequest.of(0,5));
     }
     @Test
     void removeProductTest(){
      assertDoesNotThrow(()->productService.removeProduct(5l) );
      Mockito.verify(productRepository).deleteById(5l);
     }
     @Test
     void removeProductIfIdIsLessThanZeroTest(){
      assertThrows(ProductIdMustBeGreaterThanZeroExeption.class,()->productService.removeProduct(-5l) );
      Mockito.verify(productRepository,never()).deleteById(-5l);
     }
     
     @Test
     void findProductByIdTest(){
      productService.findProductById(3l);
      Mockito.verify(productRepository).findById(3l);
     }
     @Test
     void productExistByName(){
      Mockito.when(productRepository.existsByName("ExistName")).thenReturn(true);
      assertTrue(productService.existProductByName(product));
     }

   //   Do przeglądniecia
     @Test
     void productDoesntExistByName(){
      product.setName("NonExsistingProduct");
      Mockito.when(productRepository.existsByName("NonExsistingProduct")).thenReturn(false);
      assertFalse(productService.existProductByName(product));
     }

   // @Test
   // void productDoesntExist(){
   //    String name = "NonExistingProduct";
   //    Product product1 = new Product();
   //    product1.setName(name); 
   //    Mockito.when(productRepository.existsByName(name)).thenReturn(false);
   //    assertFalse(productService.existProductByName(product1));
   // }

   @Test
   void insertExistingProductTest(){
      Mockito.when(productRepository.existsByName("ExistName")).thenReturn(true);
      assertThrows(ProductArleadyExistsException.class,()->{
         productService.insertProduct(product);
      });
   }
   @Test
   void insertNonExistingProductTest(){
      Mockito.when(productRepository.existsByName("ExistName")).thenReturn(false);
      assertDoesNotThrow(()->{
         productService.insertProduct(product);
      });
      Mockito.verify(productRepository).save(any(Product.class));
   }

   @Test
   void updateCurrentProductTest(){
      productService.updateCurrentProduct(product, 9l);
      Mockito.verify(productRepository).save(product);
   }
   @Test 
   void findAllCategoriesTest(){
      productService.findAllCategories();
      Mockito.verify(categoryRepository).findAll();
   }
   @Test
   void findProductByCategoryIdTest(){
      productService.findProductByCategoryId(8l);
      Mockito.verify(productRepository).findAllByCategoryId(8l);
   }

   

}
