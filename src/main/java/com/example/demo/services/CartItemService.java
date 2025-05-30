package com.example.demo.services;

import com.example.demo.exceptions.*;
import com.example.demo.model.CartItem;
import com.example.demo.model.User;
import com.example.demo.repositories.CartItemRepositiry;
import com.example.demo.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CartItemService {
    final CartItemRepositiry cartItemRepositiry;
    final ProductRepository productRepository;
    
    public List<CartItem> listCartItems(User user){
        if( cartItemRepositiry.findByUser(user).size()>0){
            return cartItemRepositiry.findByUser(user);
        }else{
            return null;
        }
       
    }

    public Integer addItemToTheCart(Long productId,Integer quantity,User user) throws QuantityMinException{
        var endQuantity= quantity;
        var product= productRepository.findById(productId).get();
        var cartItem=cartItemRepositiry.findByUserAndProduct(user, product);
        if(quantity>=0){
            if(cartItem!=null){
                endQuantity=cartItem.getQuantity()+quantity;
                cartItem.setQuantity(endQuantity);
            }else{
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setUser(user);
                cartItem.setQuantity(quantity);
            }
            cartItemRepositiry.save(cartItem);
            return endQuantity;
        }else{
            throw new QuantityMinException();
        }
        
       
    }
    public Integer decreaseQuantity(Long productId, Integer quantity, User user) throws ProductCanNotBeNullException, CartItemCanNotBeNull{
        var product= productRepository.findById(productId).orElse(null);
        var carItem = cartItemRepositiry.findByUserAndProduct(user, product);
        if(product==null){
            throw new ProductCanNotBeNullException();
        }
        else{
         if (carItem!=null) {
            var  currentQunatity = carItem.getQuantity();
            var newQuantity= currentQunatity - quantity;
            if(newQuantity<=0){
                cartItemRepositiry.delete(carItem);
                return 0;
            }else{
                carItem.setQuantity(newQuantity);
                cartItemRepositiry.save(carItem);
                return newQuantity;
            }
         
         }else{
            throw new CartItemCanNotBeNull();
         }
        }
    

    }
    public Integer increaseQuantity(Long productId, Integer quantity, User user) throws  MaxValueException, ProductCanNotBeNullException, ProductNotFoundExeption
{
    var optionalProduct=productRepository.findById(productId);
    if(optionalProduct.isPresent()){
        var product = optionalProduct.get();
        var cartItem= cartItemRepositiry.findByUserAndProduct(user, product);
        if(cartItem!=null){
            var currentQuantity=cartItem.getQuantity();
            var newQuantity= currentQuantity+ quantity;
            if(newQuantity <= product.getMaxValue()){
                cartItem.setQuantity(newQuantity);
                cartItemRepositiry.save(cartItem);
                return newQuantity;
            }else{
                throw new MaxValueException();
            }    
        }
        else{
            throw new ProductCanNotBeNullException();
        }
    }else{
        throw new ProductNotFoundExeption();
    }
}    

public void deleteCartItem(Long id){
    // cartItemRepositiry.deleteById(id);
    cartItemRepositiry.delete(cartItemRepositiry.findById(id).get());
}

public BigDecimal getSubtotal(CartItem cartItem){
    if(cartItem !=null && cartItem.getProduct()!=null && cartItem.getProduct().getPrice()!=null){
        BigDecimal price = cartItem.getProduct().getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        return subtotal;
    }else{
        return BigDecimal.ZERO;
    }
}

public BigDecimal calculateTotalPrice(List<CartItem> cartItems){
    var total = BigDecimal.ZERO;
    if(cartItems!=null){
        for(CartItem cartItem : cartItems){
            total= total.add(cartItem.getSubtotal());
        }
    }
   
    return total;
}
}
