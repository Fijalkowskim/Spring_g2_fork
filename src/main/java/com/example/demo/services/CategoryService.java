package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.CategoryArleadyExistException;
import com.example.demo.model.Category;
import com.example.demo.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;


public interface CategoryService {
     List<Category> getAllCategories();
     Page<Category> showAllCategories(String keyword,int pageNumber);
     Optional<Category> findByCategoryId(Long id);
     boolean existCategoryByCategoryName(Category category);
     void insertCategory(Category category) throws CategoryArleadyExistException;
     void removeCategoryById(Long id);
     void updateCurrentCategory(Category category, Long id);
} 
