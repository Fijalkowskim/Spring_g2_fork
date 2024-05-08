package com.example.demo.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.CategoryArleadyExistException;
import com.example.demo.model.Category;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.services.CategoryService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl  implements CategoryService{
    
    final CategoryRepository categoryRepository;
    @Override
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }
    @Override

    public Page<Category> showAllCategories(String keyword,int pageNumber) {
         Pageable page= PageRequest.of(pageNumber-1,10);
        if (keyword != null) {
            return categoryRepository.findAll(keyword,page);
        } else {
            return categoryRepository.findAll(page);
        }

    }
    @Override
    public Optional<Category> findByCategoryId(Long id) {
        return categoryRepository.findById(id);
    }
    @Override

    public boolean existCategoryByCategoryName(Category category) {
        return categoryRepository.existsByName(category.getName());
    }
    @Override

    public void insertCategory(Category category) throws CategoryArleadyExistException {
        var categoryExist = existCategoryByCategoryName(category);
        if (!categoryExist) {
            category.setId(null);
            categoryRepository.save(category);
        } else {
            throw new CategoryArleadyExistException();
        }
    }
    @Override

    public void removeCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }
    @Override

    public void updateCurrentCategory(Category category, Long id) {
        category.setId(id);
        categoryRepository.save(category);
    }
}
