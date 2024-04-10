package com.example.demo.cotroller.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.CategoryArleadyExistException;
import com.example.demo.model.Category;
import com.example.demo.model.dto.IdDto;
import com.example.demo.services.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryRestController {
    
    final CategoryService categoryService;

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
    @PostMapping
    public ResponseEntity<IdDto> addCategory(@Valid @RequestBody Category category) throws CategoryArleadyExistException{
        categoryService.insertCategory(category);
        return ResponseEntity.ok(
            IdDto.builder().id(category.getId()).build()
        );
    }
    
}
