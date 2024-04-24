package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.example.demo.exceptions.CategoryArleadyExistException;
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
        category.setId(1l);
        category.setName("test");
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
    @Test
    void assertTrueWhenCategoryExistByNameTest(){
        String categoryName="test";
        
        when(categoryRepository.existsByName(categoryName)).thenReturn(true);
        boolean categoryExist= categoryService.existCategoryByCategoryName(category);
        assertTrue(categoryExist);
        verify(categoryRepository).existsByName(categoryName);
    }
    @Test
    void assertFalseThatCategoryDoesNotExistTest(){
        when(categoryRepository.existsByName("test")).thenReturn(false);
        assertFalse(categoryService.existCategoryByCategoryName(category));

    }
    @Test 
    void insertCategoryWhenCategoryDoesNotExistTest(){
       String name = "test";
       when(categoryRepository.existsByName(name)).thenReturn(false);
       assertDoesNotThrow(()->{
        categoryService.insertCategory(category);
       });
       verify(categoryRepository).save(category);
    }
    @Test
    void throwCategoryExistExceptionWhenCategoryExistOnSaveTest(){
        String name = "test";
        when(categoryRepository.existsByName(name)).thenReturn(true);
        assertThrows(CategoryArleadyExistException.class, ()->categoryService.insertCategory(category));
        verify(categoryRepository,never()).save(category);
    }
    @Test
    void removeCategoryTest(){
        //given 
        Long categoryId=12l;
        //when
        categoryService.removeCategoryById(categoryId);
        //then
        verify(categoryRepository).deleteById(categoryId);
    }
    @Test
    void updateCurrentCategooryTest(){
        // given 
        Long categoryId= 123l;
        //when
        categoryService.updateCurrentCategory(category, categoryId);
        //then
        verify(categoryRepository).save(category);
        assertEquals(categoryId, category.getId());

    }



    
}
