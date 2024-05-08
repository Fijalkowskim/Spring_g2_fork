package com.example.demo.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.UserCanNotBeNullException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
     final UserRepository userRepository;

     @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
@Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    @Override
    public boolean existByEmail(String email){
        return userRepository.existsByEmail(email);

    }
    @Override
    public Optional<User> findUserByEmail(String email)
{
    return userRepository.findByEmail(email);
}
@Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
    @Override
    public  void updateUser(Long id, User user){
        user.setId(id);
        userRepository.save(user);
    }
    @Override
    public void removeUserById(Long id){
        userRepository.deleteById(id);
    }
    @Override
    public void changeUserRole(Long id, Role role) throws UserCanNotBeNullException
    {
        User  user = userRepository.findById(id).orElse(null);
        if(user!= null){
            user.setRole(role);
            userRepository.save(user);
        }else{
            throw new UserCanNotBeNullException();
        }
    }
@Override
    public void updateResetPassword(String token, String emial, LocalDateTime expirationDateTime) throws UserNotFoundException{
        var optionalUser= userRepository.findByEmail(emial);
        if(optionalUser.isPresent()){
            var user= optionalUser.get();
            user.setResetPasswordToken(token);
            user.setResetPasswordExpiration(expirationDateTime);
            userRepository.save(user);
        }else{
            throw new UserNotFoundException();
        }
    }
@Override
    public boolean isResetTokenValid(User user){
        LocalDateTime expiratioinDateTime= user.getResetPasswordExpiration();
        return expiratioinDateTime!=null && expiratioinDateTime.isAfter(LocalDateTime.now());
    }
@Override
    public User getResetPasswordToken(String token){
        return userRepository.findByResetPasswordToken(token);
    }
@Override
    public void updateUserPassword(User user, String newPassword){
        BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
        var edcodedNewPassword=passwordEncoder.encode(newPassword);
        
        user.setPassword(edcodedNewPassword);
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiration(null);
        userRepository.save(user);

    }
    @Override
    public boolean isPasswordValid(String email, String password){
        BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
        var optionalUser= userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            var user = optionalUser.get();
            return passwordEncoder.matches(password, user.getPassword());

        }else{
            return false;
        }

    }
@Override
    public void changePassword(String email, String newPassword) throws UserCanNotBeNullException{
        var user= userRepository.findByEmail(email).orElse(null);
        BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
        if(user!=null){
            var encodedPassword= passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);
        }else{
            throw new UserCanNotBeNullException();
        }
    }

    // old password == new password
    @Override
    public boolean arePasswordTheSame(String oldPassword, String newPassword){
        return oldPassword.equals(newPassword);

    }
    // new password contains regex pattern "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$"
    @Override
    public boolean isNewPasswordCorrect(String newPassword){
        var passwordPattern="^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher= pattern.matcher(newPassword);
        return matcher.matches();
    }
}
