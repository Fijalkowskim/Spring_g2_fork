package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail (String email);
    Optional <User>  findByEmail(String email);
    User findByResetPasswordToken(String token);


}
