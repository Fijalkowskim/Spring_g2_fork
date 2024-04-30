package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.h2.command.dml.MergeUsing.When;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.exceptions.CartItemCanNotBeNull;
import com.example.demo.exceptions.MaxValueException;
import com.example.demo.exceptions.ProductCanNotBeNullException;
import com.example.demo.exceptions.ProductNotFoundExeption;
import com.example.demo.exceptions.QuantityMinException;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repositories.CartItemRepositiry;
import com.example.demo.repositories.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {
    @InjectMocks
    CartItemService cartItemService;
    @Mock
    CartItemRepositiry cartItemRepositiry;
    @Mock
    ProductRepository productRepository;
    
    private Product product;
    private User user;
    private CartItem cartItem;
    @BeforeEach
    void setUpProduct(){
        product= new Product();
        product.setId(10l);
        product.setPrice(BigDecimal.valueOf(10));
        product.setMaxValue(10);
    }
    @BeforeEach
    void setUpUser(){
        user =new User();
        user.setId(9l);
    }
    @BeforeEach
    void setUpCartItem(){
        cartItem = new CartItem();
      
    }

    @Test
    void cartItemListIfExistTest(){
        CartItem cartItem1= new CartItem(1l,user,product,6);
        CartItem cartItem2= new CartItem(2l,user,new Product(),5);

        List<CartItem> cartItemList= Arrays.asList(cartItem1,cartItem2);
        when(cartItemRepositiry.findByUser(user)).thenReturn(cartItemList);
        List<CartItem> result = cartItemService.listCartItems(user);
        assertNotNull(result);
        assertEquals(cartItemList.size(), result.size());
        assertTrue(result.containsAll(cartItemList));

    }
    @Test
    void cartIstemIsEmptyTest(){
        when(cartItemRepositiry.findByUser(user)).thenReturn(Collections.emptyList());
        assertNull(cartItemService.listCartItems(user));
        verify(cartItemRepositiry).findByUser(user);
    }

    @Test
    void addItemToTheCartWithNegativeQuantityTest(){
        var nagativeQuantity= -10;
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        assertThrows(QuantityMinException.class, 
        ()-> cartItemService.addItemToTheCart(product.getId(), nagativeQuantity, user)
        );
        verify(cartItemRepositiry,never()).save(Mockito.any(CartItem.class));

    }
    @Test
    void addItemToTheCartWithPositiveQuantityTest(){
        var quantity = 10;
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepositiry.findByUserAndProduct(user, product)).thenReturn(null);
        assertDoesNotThrow(()-> cartItemService.addItemToTheCart(product.getId(),quantity, user));
        verify(cartItemRepositiry).save(Mockito.any(CartItem.class));
    }
    @Test
    void decreaseQunatityWhenProductIsNullTest(){
        when(productRepository.findById(10l)).thenReturn(Optional.empty());
        assertThrows(ProductCanNotBeNullException.class, ()->cartItemService.decreaseQuantity(10l, 10, user));
    }
    @Test
    void decreaseQunatityWhenCartItemIsNullTest(){
        when(productRepository.findById(10l)).thenReturn(Optional.of(product));
        when(cartItemRepositiry.findByUserAndProduct(user, product)).thenReturn(null);
        assertThrows(CartItemCanNotBeNull.class, ()-> cartItemService.decreaseQuantity(10l, 10, user));
    }
    @Test
    void decreaseQuantityWithNewQuantityLessOrEqualToZeroTest(){
        Integer initialQuantity = 5;
        Integer decreaseQuantity=5;

        CartItem existingCartItem= new CartItem();
        existingCartItem.setProduct(product);
        existingCartItem.setUser(user);
        existingCartItem.setQuantity(initialQuantity);

        when(productRepository.findById(10l)).thenReturn(Optional.of(product));
        when(cartItemRepositiry.findByUserAndProduct(user, product)).thenReturn(existingCartItem);
        assertDoesNotThrow(()->cartItemService.decreaseQuantity(10l, decreaseQuantity, user));
        verify(cartItemRepositiry).delete(existingCartItem);

    }
    @Test
    void decreaseQuantityWithNewQuantityGreaterThanZeroTest(){
        Integer initialQuantity = 5;
        Integer decreaseQuantity=3;

        CartItem existingCartItem= new CartItem();
        existingCartItem.setProduct(product);
        existingCartItem.setUser(user);
        existingCartItem.setQuantity(initialQuantity);

        when(productRepository.findById(10l)).thenReturn(Optional.of(product));
        when(cartItemRepositiry.findByUserAndProduct(user, product)).thenReturn(existingCartItem);
        assertDoesNotThrow(()->cartItemService.decreaseQuantity(10l, decreaseQuantity, user));
        verify(cartItemRepositiry).save(existingCartItem);
    }
    @Test
    void increaseQuantityWhenProductIsNullTest(){
        when(productRepository.findById(5l)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundExeption.class, ()->{
            cartItemService.increaseQuantity(5l, 20, user);
        });
        verify(cartItemRepositiry,never()).save(Mockito.any(CartItem.class));
    }
    @Test
    void increaseQuantityWhenCartItemIsNullTest(){
        when(productRepository.findById(10l)).thenReturn(Optional.of(product) );
        when(cartItemRepositiry.findByUserAndProduct(user, product)).thenReturn(null);
        assertThrows(ProductCanNotBeNullException.class, ()-> cartItemService.increaseQuantity(10l, 10, user));
    }
    @Test
    void increaseQuantityWhenExccedsMaxValueTest(){
    // given
        var quantity= 3;
        product.setMaxValue(10);
        when(productRepository.findById(10l)).thenReturn(Optional.of(product));
  
        cartItem.setQuantity(8);
        when(cartItemRepositiry.findByUserAndProduct(user, product)).thenReturn(cartItem);
        assertThrows(MaxValueException.class, ()->cartItemService.increaseQuantity(10l, quantity, user));
    }
    @Test
    void increaseQuantityWhenQuantityLessThanOrEqualToMaxValueTest(){
        //given 
        var quantity = 2;
        var expectedQuantity = 10;
       
        // when 
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        
        cartItem.setQuantity(8);
        when(cartItemRepositiry.findByUserAndProduct(user, product)).thenReturn(cartItem);
        assertDoesNotThrow(()->cartItemService.increaseQuantity(10l, quantity, user));
        assertEquals(expectedQuantity, cartItem.getQuantity());
        verify(cartItemRepositiry).save(cartItem);
    }
    @Test
    void removeCartItemTest(){
        // given
   
        //when
        when(cartItemRepositiry.findById(5l)).thenReturn(Optional.of(cartItem));
        cartItemService.deleteCartItem(5l);
        //then 
        verify(cartItemRepositiry).delete(cartItem);
    }
    @Test
    void getSubtotalWhenCartItemIsNullTest(){
        BigDecimal subtotalForNULLItem= cartItemService.getSubtotal(null);
        assertEquals(BigDecimal.ZERO, subtotalForNULLItem);
    }
    @Test 
    void getSubtotalWhenCartItemIsNotNullTest(){
        // given 
        cartItem.setQuantity(3);
        cartItem.setProduct(product);
    
        BigDecimal subtotal = cartItemService.getSubtotal(cartItem);
        BigDecimal expectedSubtotal = BigDecimal.valueOf(30);
        assertEquals(expectedSubtotal, subtotal);
    }
    @Test
    void getSubtotalWhenCartItemProductIsNullTest(){
        cartItem.setProduct(null);
        BigDecimal subtotalForNullProduct= cartItemService.getSubtotal(cartItem);
        assertEquals(BigDecimal.ZERO, subtotalForNullProduct);
    }
    @Test
    void getSubtotalWhenCartItemProductPriceIsNullTest(){
        product.setPrice(null);
        cartItem.setProduct(product);
        BigDecimal subtotalForNullProductPrice= cartItemService.getSubtotal(cartItem);
        assertEquals(BigDecimal.ZERO, subtotalForNullProductPrice);
    }
    @Test
    void calculateTotalPriceWhenCartItemIsNotNullTest(){
        //given 
        CartItem item1= new CartItem(1L, user, product, 2);
        CartItem item2= new CartItem(2L, user, product, 3);
        List<CartItem> list = Arrays.asList(item1,item2);
        BigDecimal totalPrice = cartItemService.calculateTotalPrice(list);
        assertEquals(BigDecimal.valueOf(50), totalPrice);
    }
    @Test
    void calculateTotalPriceWhencCartItemIsNull(){
        List<CartItem> nullCartItems= null;
        BigDecimal totalPriceForNullCartItems= cartItemService.calculateTotalPrice(nullCartItems);
        assertEquals(BigDecimal.ZERO, totalPriceForNullCartItems);


    }



}
