package com.example.demo.controller;

import static java.lang.StringTemplate.STR;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.h2.command.dml.MergeUsing.When;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.cotroller.ProductController;
import com.example.demo.exceptions.ProductArleadyExistsException;
import com.example.demo.exceptions.ProductIdMustBeGreaterThanZeroExeption;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.services.ProductService;

import lombok.var;

@ExtendWith(MockitoExtension.class)
public class ProductControllerIntegrationTest {
    @InjectMocks
    ProductController productController;

    @Mock
    ProductService productService;
    @Mock
    Page <Product>page;
    @Mock
    BindingResult bindingResult;
    @Mock
    Model model;
    @Mock
    RedirectAttributes redirectAttributes;
    String keyword;
    Long productId;
    Product product;
    @BeforeEach
    void setUp(){
        product=new Product();
         keyword="test";
         productId=1l;
    }

    @Test
    void showAllProductsReturnCorrectViewAndPassesKeywordToNextMethod(){
        when(page.getTotalPages()).thenReturn(1);
        when(productService.findAllProducts(anyString(), anyInt())).thenReturn(page);
        var resultView= productController.showAllProducts(model, keyword);
        assertEquals("/product/products", resultView);
        verify(productService).findAllProducts(keyword, 1);

    }

    @Test
    void showAllProductsByPageNumberReturnCorrectViewAndSetModelAttributes(){
        int pageNumber= 1;
        when(page.getTotalPages()).thenReturn(1);
        when(page.getContent()).thenReturn(Collections.emptyList());
        when(page.getTotalElements()).thenReturn( 0l);
        when(productService.findAllProducts(keyword, pageNumber)).thenReturn(page);

        String resultView= productController.showAllProductsByPageNumber(model, keyword, pageNumber);
        assertEquals(resultView, "/product/products");
        verify(model).addAttribute("productList",Collections.emptyList() );
        verify(model).addAttribute("title", "List of products");
       verify( model).addAttribute("action", "/saveProduct");
       verify(model).addAttribute("categories", productService.findAllCategories());
       verify(model).addAttribute("keyword", keyword);
       verify(model).addAttribute("searchAction", "/product");
       verify(model).addAttribute("items", 0l);
       verify(model).addAttribute("pages", 1);
      verify(model).addAttribute("currentPage", pageNumber);

    }
    @Test
    void showProductDetailsByIDReturnProductViewWhenProductExists(){
        when(productService.findProductById(productId)).thenReturn(Optional.of(product));
        String resultView= productController.showProductDetailByID(productId, model);
        verify(model).addAttribute("product", product);
        assertEquals("/product/product", resultView);
    }
    @Test
    void showProductDetailsByIDReturnProductViewWhenProductDoesNotExist(){
        when(productService.findProductById(productId)).thenReturn(Optional.empty());
        String resultView= productController.showProductDetailByID(productId, model);
        assertEquals("/error-page", resultView);
        verify(model).addAttribute("error", "Current product with id=" + productId+ " doesn't exist");
       verify(model).addAttribute("errorAction", "/product");
       verify(model).addAttribute("return", "Return to list of products");

    }
@Test
void removeProductReturnCorrectRedirectViewForAuthUserWithcorrectId()throws ProductIdMustBeGreaterThanZeroExeption{
    var redirectView = "redirect:/product";
    var result= productController.removeProduct(productId);
    assertEquals(redirectView, result);
    verify(productService).removeProduct(productId);
}
@Test
void removeProductReturnCorrectRedirectViewForAuthUserWithProductIdLessThanZero() throws ProductIdMustBeGreaterThanZeroExeption{
    var invalidId= -10L;
    assertThrows(ProductIdMustBeGreaterThanZeroExeption.class, ()->{
        productController.removeProduct(invalidId);
    });
    verify(productService,never()).removeProduct(anyLong());
}
    @Test
    void  saveProductReturnRedirectToProductViewWhenValidationPasses() throws ProductArleadyExistsException{
        var result= productController.saveProduct(product, bindingResult, redirectAttributes);
        assertEquals(result, "redirect:/product");
        verify(productService).insertProduct(product);
    }
    @Test
    void saveProductReturnRedirectToProductViewWhenValidationFails(){
        when(bindingResult.hasErrors()).thenReturn(true);
        String result = productController.saveProduct(product, bindingResult, redirectAttributes);
        assertEquals( "redirect:/product", result);
        verify(redirectAttributes).addFlashAttribute(eq("errors"),any());
        verify(redirectAttributes).addFlashAttribute(eq("product"),eq(product));
        verifyNoInteractions(productService);
    }
    // to do !!
    @Test
    void saveProductReturnRedirectToProductViewWhenProductArleadyExists() throws ProductArleadyExistsException{
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(ProductArleadyExistsException.class).when(productService).insertProduct(product);
        var result = productController.saveProduct(product, bindingResult, redirectAttributes);
        assertEquals("redirect:/product", result);
        verify(redirectAttributes).addFlashAttribute("error","Product arleady exists");
        verify(redirectAttributes).addFlashAttribute("product", product);
        verify(productService).insertProduct(product);
    }
    @Test
    void editProductWhenUserIsUnauthorizes(){
        List<Category> categories= new ArrayList<>();
        categories.add(new Category(1l, "Category1",null, null));
        categories.add(new Category(2l, "Category2",null, null));
        when(productService.findAllCategories()).thenReturn(categories);
        String view= productController.editProduct(productId, model);
        assertEquals("/error-page", view);
    }
    @Test
    void saveEditedProductWithErrors(){
        when(bindingResult.hasErrors()).thenReturn(true);
        var result = productController.saveEditedProdutc(productId, product, bindingResult, redirectAttributes);
        assertEquals("redirect:/editProduct/" + productId, result);
        verify(bindingResult).hasErrors();
        verify(redirectAttributes).addFlashAttribute(eq("errors"),any());
        verify(redirectAttributes).addFlashAttribute(eq("product"), eq(product));
    }

    @Test
    void saveEditedProductWithSuccess(){
        when(bindingResult.hasErrors()).thenReturn(false);
        var result = productController.saveEditedProdutc(productId, product, bindingResult, redirectAttributes);
        assertEquals("redirect:/productDetail/"+ productId, result);
        verify(bindingResult).hasErrors();
        verify(productService).updateCurrentProduct(product, productId);
    }
    @Test
    void bindProductToModelReturnErrorPageWhenProductProductDoesNotExist(){
        when(productService.findProductById(productId)).thenReturn(Optional.empty());
        var view= productController.bindProductToModel(productId, model);
        assertEquals("/error-page", view);
        verify(model).addAttribute("error", "Product doesn't exist");
        verify(model).addAttribute("errorAction", "/product");
        verify(model).addAttribute("return", "Return to list of products");
    }
    @Test
    void bindProductToModelReturnCorrectViewWhenProductProductExists(){
        when(productService.findProductById(productId)).thenReturn(Optional.of(product));
        var view= productController.bindProductToModel(productId, model);
        assertEquals("/product/edit-product", view);
        verify(model).addAttribute("product", product);
    }
}
