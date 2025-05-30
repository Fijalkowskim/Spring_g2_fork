package com.example.demo.services.impl;

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
import com.example.demo.services.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    final ProductRepository productRepository;
    final CategoryRepository categoryRepository;

    @Override
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
@Override
    public Page<Product> findAllProducts(String keyword,int pageNumber) {
        Pageable page= PageRequest.of(pageNumber-1,5);
        if (keyword != null) {
            return productRepository.findAll(keyword,page);
        } else {
            return productRepository.findAll(page);
        }

    }
@Override
    public void removeProduct(Long id) throws ProductIdMustBeGreaterThanZeroExeption {
        if(id>0){
            productRepository.deleteById(id);
        }else{
            throw new ProductIdMustBeGreaterThanZeroExeption();
        }
      
    }
@Override
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);

    }
@Override
    public boolean existProductByName(Product product) {
        return productRepository.existsByName(product.getName());
    }
@Override
    public void insertProduct(Product product) throws ProductArleadyExistsException {
        var productExist = existProductByName(product);
        if (productExist) {
            throw new ProductArleadyExistsException();
        } else {
            product.setId(null);
            productRepository.save(product);
        }

    }
@Override
    public void updateCurrentProduct(Product product, Long id) {
        product.setId(id);
        productRepository.save(product);

    }
@Override
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }
@Override
    public List<Product> findProductByCategoryId(Long categoryId) {
        return productRepository.findAllByCategoryId(categoryId);
    }

}
