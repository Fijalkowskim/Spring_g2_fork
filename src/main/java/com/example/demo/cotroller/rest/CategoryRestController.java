package com.example.demo.cotroller.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.CategoryArleadyExistException;
import com.example.demo.model.Category;
import com.example.demo.model.dto.IdDto;
import com.example.demo.services.CategoryService;
import com.example.demo.services.impl.CategoryServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryRestController {
    
    final CategoryServiceImpl categoryService;

    @GetMapping
    public List<Category> showAllCategories(){
        return categoryService.getAllCategories();
        
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Category>> showCurrentCategoryById(@PathVariable Long id){
        var category= categoryService.findByCategoryId(id);
        if(category.isPresent()){
            return ResponseEntity.ok(category);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<IdDto> addCategory(@Valid @RequestBody Category category) throws CategoryArleadyExistException{
        try{
            categoryService.insertCategory(category);
            return ResponseEntity.ok(
            IdDto.builder().id(category.getId()).build()
        );
        }catch(CategoryArleadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCategoryById(@PathVariable ("id") Long categryId){
        if(categoryService.findByCategoryId(categryId).isPresent()){
            categoryService.removeCategoryById(categryId);
            return ResponseEntity.accepted().build();
        }else{
            return ResponseEntity.badRequest().build();

        }
    
    }
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> editCategoryById(@PathVariable Long id, @Valid  @RequestBody Category category){
        var  newCategory = categoryService.findByCategoryId(id).orElse(null);
        if(newCategory!=null){
            categoryService.updateCurrentCategory(category, id);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    
}
