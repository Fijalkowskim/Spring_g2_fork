package com.example.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;
    
@Transient
public BigDecimal getSubtotal(){
    if(product!=null && product.getPrice()!=null)
{
    BigDecimal price = product.getPrice();
    BigDecimal subtotal= price.multiply(BigDecimal.valueOf(quantity));
    return subtotal;
}else{
    return BigDecimal.ZERO;
}
}
    
}
