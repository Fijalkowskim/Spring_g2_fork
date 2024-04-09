package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.model.User;

public interface CartItemRepositiry extends JpaRepository<CartItem, Long>{
    public List<CartItem> findByUser(User user);
    public CartItem findByUserAndProduct(User user, Product product);
    public void deleteById(Long id);
}
