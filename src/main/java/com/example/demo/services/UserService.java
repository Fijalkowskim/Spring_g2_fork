package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.UserCanNotBeNullException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    final UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    public boolean existByEmail(String email){
        return userRepository.existsByEmail(email);

    }
    public Optional<User> findUserByEmail(String email)
{
    return userRepository.findByEmail(email);
}
    public void saveUser(User user) {
        userRepository.save(user);
    }
    public  void updateUser(Long id, User user){
        user.setId(id);
        userRepository.save(user);
    }
    public void removeUserById(Long id){
        userRepository.deleteById(id);
    }
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

    public void updateResetPassword(String token, String emial) throws UserNotFoundException{
        var optionalUser= userRepository.findByEmail(emial);
        if(optionalUser.isPresent()){
            var user= optionalUser.get();
            user.setResetPasswordToken(token);
            userRepository.save(user);
        }else{
            throw new UserNotFoundException();
        }

    }
    public User getResetPasswordToken(String token){
        return userRepository.findByResetPasswordToken(token);
    }
    public void updateUserPassword(User user, String newPassword){
        BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
        var edcodedNewPassword=passwordEncoder.encode(newPassword);
        
        user.setPassword(edcodedNewPassword);
        user.setResetPasswordToken(null);

        userRepository.save(user);


    }

}
