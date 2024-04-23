package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.example.demo.model.Category;
import com.example.demo.repositories.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @InjectMocks
    CategoryService categoryService;
    @Mock
    CategoryRepository categoryRepository;
    private Category category;

    @BeforeEach
    void setUpCategory(){
        category=new Category();
    }

    @Test
    void getExistingListOfCategoriesTest(){
        // given
        List<Category> extepextedCategories= List.of(
            Category.builder().id(1l).build(),
            Category.builder().id(3l).build(),
            Category.builder().id(5l).build()
        );
        Mockito.when(categoryRepository.findAll()).thenReturn(extepextedCategories);
        //when
        List<Category> actualList= categoryService.getAllCategories();
        //then
        Mockito.verify(categoryRepository).findAll();
       
        Assertions.assertEquals(extepextedCategories.size(), actualList.size());
        Assertions.assertEquals(extepextedCategories, actualList);

        for(int i =0 ; i<extepextedCategories.size();i++){
            Assertions.assertEquals(extepextedCategories.get(i).getId(), actualList.get(i).getId());
        }
    }
    @Test
    void findAllProductsByKeywordTest(){
     // given
     //when
     categoryService.showAllCategories("test", 1);
     //then
     Mockito.verify(categoryRepository).findAll("test",PageRequest.of(0,10));
    }
    @Test
    void findAllProductsByKeywordIfItsNULLTest(){
     // given
     //when
     categoryService.showAllCategories(null, 1);
     //then
     Mockito.verify(categoryRepository).findAll(PageRequest.of(0,10));
    }
    @Test
    void findAllProductWhenPageNaumerIsNegativeTest(){
        Integer negativePage= -1;
        assertThrows(IllegalArgumentException.class, ()->{
            categoryService.showAllCategories(null, negativePage);
        });
    }
    @Test
    void  getCategoryWhenCategoryExistTest(){
        //given

        Optional<Category> optionalCategory = Optional.of(
            Category.builder().id(9l).product(List.of()).build()
        );
        //when
        Mockito.when(categoryRepository.findById(9l)).thenReturn(optionalCategory);
        var response =  categoryService.findByCategoryId(9l);
        Mockito.verify(categoryRepository).findById(9l);

        //then
        assertEquals(optionalCategory.get().getId(), response.get().getId());
    }

    @Test
    void findCategoryWhenCategoryDoesntExistTest(){
        //given
        Long id = 2l;
        Optional<Category> optionalCategory = Optional.empty();
        Mockito.when(categoryRepository.findById(id)).thenReturn(optionalCategory);
        //when
        var actuallCategory= categoryService.findByCategoryId(id);
        // then
        Mockito.verify(categoryRepository).findById(id);
        // assertFalse(actuallCategory.isPresent());
        assertTrue(actuallCategory.isEmpty());

    }
    // do dokończenia 

    
}
