package com.example.demo.cotroller.rest;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.AuthPasswordConfig;
import com.example.demo.exceptions.UserCanNotBeNullException;
import com.example.demo.mappers.UserSaveDtoToUserMapper;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.dto.UserSaveDto;
import com.example.demo.services.UserService;
import com.example.demo.services.impl.UserServiceImp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserRestController {
    final UserServiceImp userService;

    @GetMapping("/whoami")
    public ResponseEntity<String> whoAmI(Authentication authentication){
        if(authentication == null || !authentication.isAuthenticated() ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }else{
            return ResponseEntity.ok("Authenticated as " + authentication.getName());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUser(Authentication authentication){
        return ResponseEntity.ok(userService.findAllUsers());
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getCurrentUserById(@PathVariable Long id ){
        if(userService.findUserById(id).isPresent()){
            return ResponseEntity.ok().body(userService.findUserById(id).get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> editUserById(@PathVariable Long id, @RequestBody @Valid UserSaveDto userSaveDto){
     var newUser= userService.findUserById(id).orElse(null);
     var user = UserSaveDtoToUserMapper.fromUserDtoToUserEntity(userSaveDto);
     if(Objects.isNull(newUser)){
        return ResponseEntity.badRequest().build();
     }else{
        userService.updateUser(id, user);
        return ResponseEntity.status(HttpStatus.ACCEPTED.value()).build();
     }
    }

    @PostMapping
    public ResponseEntity<?> addUser(@Valid @RequestBody UserSaveDto userSaveDto){
        var user = UserSaveDtoToUserMapper.fromUserDtoToUserEntity(userSaveDto);
        user.setPassword(AuthPasswordConfig.passwordEncoder().encode(user.getPassword()));
        if(userService.existByEmail(user.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with that email arleady exist");      
        }else{
            try {
                userService.saveUser(user);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
            }
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeUser(@PathVariable Long id)
{
    if(userService.findUserById(id).isPresent()){
        userService.removeUserById(id);
        return ResponseEntity.accepted().build();
    }else{
        return ResponseEntity.badRequest().build();
    }
}
// /api/user/123/role?newRole=MANAGER
@PreAuthorize("hasRole('ROLE_ADMIN')")
@PostMapping("/{userId}/{role}")
public ResponseEntity<Void> changeUserRole(@PathVariable Long userId, @PathVariable ("role") String userRole){
    try{
        Role role = Role.valueOf(userRole);
        userService.changeUserRole(userId, role);
        return ResponseEntity.ok().build();
    }catch(IllegalArgumentException e){
        return ResponseEntity.badRequest().build();
    }catch(UserCanNotBeNullException e){
        return ResponseEntity.notFound().build();
    }
}


    
}
