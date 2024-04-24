package com.example.demo.cotroller.rest;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.CartItemCanNotBeNull;
import com.example.demo.exceptions.MaxValueException;
import com.example.demo.exceptions.ProductCanNotBeNullException;
import com.example.demo.exceptions.ProductNotFoundExeption;
import com.example.demo.exceptions.QuantityMinException;
import com.example.demo.services.CartItemService;
import com.example.demo.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
 @PreAuthorize("isAuthenticated()")
@RequestMapping("/api/cart")
public class ShoppingCartRestController {
    final UserService userService;
    final CartItemService cartItemService;

    @GetMapping
    public ResponseEntity<?> showShoppingCart(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        var user = userService.findUserByEmail(authentication.getName()).orElse(null);
        if(Objects.isNull(user)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }else{
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(cartItemService.listCartItems(user));
        }
        
    }

    @PostMapping("/add/{id}/{quantity}")
    public ResponseEntity<?> addProductToTheShoppingCart(@PathVariable ("id") Long productId, @PathVariable Integer quantity,Authentication authentication){
        try {
            cartItemService.addItemToTheCart(productId, quantity, userService.findUserByEmail(authentication.getName()).orElse(null));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Product has been added correctly");
        } catch (QuantityMinException e) {
            return ResponseEntity.badRequest().body("Quantity must be greater than 0 ");
        }
    }

    // id dla produkty nie dla produktu co jest w koszyku -> klucz obcy nie glucz głowny 
    @PostMapping("/removeQuntity/{id}")
    public ResponseEntity<?> decreaseQuantityforProduct(@PathVariable Long id,Authentication authentication ){
        try{
            cartItemService.decreaseQuantity(id, 1, userService.findUserByEmail(authentication.getName()).orElse(null));
            return ResponseEntity.accepted().body("Product with id :"+ id +" has been decrased correctly");
        }catch(ProductCanNotBeNullException | CartItemCanNotBeNull e){
            return ResponseEntity.badRequest().body("Quantity must be positive");
        }
    }
    // jezeli ilosc produktu ma byc podawana o ile zmniejszać
    @PostMapping("/removeQuntity/{id}/{q}")
    public ResponseEntity<?> decreaseQuantityforProduct(@PathVariable Long id,Authentication authentication , @PathVariable Integer q){
        try{
            cartItemService.decreaseQuantity(id, q, userService.findUserByEmail(authentication.getName()).orElse(null));
            return ResponseEntity.accepted().body("Product with id :"+ id +" has been decrased correctly");
        }
        catch(ProductCanNotBeNullException | CartItemCanNotBeNull e){
            return ResponseEntity.badRequest().body("Quantity must be positive");
        }
    }
    @PostMapping("/addQuantity/{productId}")
    public ResponseEntity<?> addQuantityForProduct(@PathVariable Long productId, Authentication authentication) throws ProductNotFoundExeption{
        try{
            cartItemService.increaseQuantity(productId, 1, userService.findUserByEmail(authentication.getName()).orElse(null));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Product with id :"+ productId +" has been increase correctly");
        }catch(MaxValueException| ProductCanNotBeNullException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantity cannot exceed the maximum value of the current product");
    
        }
    }
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> deleteCurrentItemFromCart(@PathVariable Long cartItemId){
        cartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.accepted().body("Item with id "+ cartItemId+ "has been removed");
    }


}
