package com.example.demo.cotroller.rest;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.UserCanNotBeNullException;
import com.example.demo.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/change-password")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ChangePasswordRestController {
    final UserService userService;

    @PostMapping("/{id}")
    public ResponseEntity<String> changeUserPassword(@PathVariable Long id, @RequestParam ("password") String oldPassword, @RequestParam ("newPassword") String newPassword){
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        var email = auth.getName();
        var optionalUser = userService.findUserByEmail(email);

        if(optionalUser.isPresent()){
            if(userService.arePasswordTheSame(oldPassword, newPassword) ||  !userService.isNewPasswordCorrect(newPassword) ){
                return ResponseEntity.badRequest().body("Can not change password. Password could be the same or might not maatch the password pattern");
            }else{
                try{
                    if(userService.isPasswordValid(email, oldPassword)){
                        userService.changePassword(email, newPassword);
                        return ResponseEntity.ok().body("Password changed successfully");
                    }else{
                        return ResponseEntity.badRequest().body("Old password is incorrect");
                    }
                }catch(UserCanNotBeNullException exception){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to chnage password");
                }
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Token - user not found");
        }
    }





    
}
