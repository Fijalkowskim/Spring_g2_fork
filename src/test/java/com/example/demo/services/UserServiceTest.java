package com.example.demo.services;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.exceptions.UserCanNotBeNullException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.impl.UserServiceImp;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserServiceImp userService;
    @Mock
    UserRepository userRepository;

    private User user1, user2;

    @BeforeEach
    void setUpUser(){
        user1= new User();
        user2= new User();
        user1.setId(1l);
        user1.setEmail("test@gmail.com");
        user1.setPassword("Test1234!");
        user1.setRole(Role.ADMIN);
        user2.setId(2l);
        user2.setEmail("test2@gmail.com");
        user2.setPassword("Test1234!");
    
        user1.setResetPasswordToken("token");
    }

    @Test
    void findAllUsersTest(){
        List<User> userList= new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        when(userRepository.findAll()).thenReturn(userList);
        List<User> result = userService.findAllUsers();
        assertEquals(userList, result);
        assertTrue(result.containsAll(userList));
    }
    @Test
    void findAllUsersWhenNoUserFound(){
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        List<User> result = userService.findAllUsers();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test 
    void findUserByIdWhenUserExists(){
        Long id = 1l;
        when(userRepository.findById(id)).thenReturn(Optional.of(user1));
        var result = userService.findUserById(id);
        assertTrue(result.isPresent());
        assertEquals(user1, result.get());
    }
    @Test
    void findUserByIdWhenUserDoesNotExist(){
        Long id = 1l;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        var result = userService.findUserById(id);
        assertTrue(result.isEmpty());
    }
    @Test
    void existByEmailReturnTrueWhenUserWithThatEmailExists(){
       var existingEmail ="test@gmail.com";
       when(userRepository.existsByEmail(user1.getEmail())).thenReturn(true);
       boolean result = userService.existByEmail(existingEmail);
       assertTrue(result);

    }
    @Test
    void existByEmailReturnFalseWhenUserWithThatEmailDoesNotExist(){
        var notExistingEmail ="not@gmail.com";
        when(userRepository.existsByEmail(notExistingEmail)).thenReturn(false);
        var result = userService.existByEmail(notExistingEmail);
        assertFalse(result);
    }
    @Test
    void findUserByEmailWhenEmailExists(){
        var existingEmail ="test@gmail.com";
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(user1));
        var result = userService.findUserByEmail(existingEmail);
        assertEquals(user1, result.get());
    }
    @Test
    void findUserByEmailWhenEmailDoesNotExist(){
        var existingEmail ="test@gmail.com";
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.empty());
        var result = userService.findUserByEmail(existingEmail);
       assertEquals(Optional.empty(), result);
    }
    @Test
    void saveUserShouldCallSaveMethodWithCorectUser(){
        User user3 = new User();
        user3.setEmail("test3@gmail.com");
        user3.setPassword("Test1234!");
        userService.saveUser(user3);
        verify(userRepository).save(user3);
    }
    @Test
    void updateUserWithCorectData(){
        User user3 = new User();
        user3.setEmail("test3@gmail.com");
        user3.setPassword("Test1234!");
        Long id = 10l;
        userService.updateUser(id, user3);
        verify(userRepository).save(user3);
        assertEquals(user3.getId(), id);
    }
    @Test
    void deleteUserByCorrectId(){
        userService.removeUserById(1l);
        verify(userRepository).deleteById(user1.getId());
    }
    @Test
    void deleteUserByIncorrectId(){
        userService.removeUserById(10l);
        verify(userRepository, never()).deleteById(user1.getId());
    }
    @Test
    void changeUserRoleShouldUpdateUserRoleWhenUserExist(){
        var newRole=Role.CLIENT;
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        assertDoesNotThrow(()-> userService.changeUserRole(1l, newRole));
        assertEquals(newRole, user1.getRole());
        verify(userRepository).save(user1);
    }
    @Test
    void changeUserRoleShouldTrowUserCanNotBeNullExceptionWhenUserDoesNotExist(){
        Long id = 100l;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(UserCanNotBeNullException.class,()-> userService.changeUserRole(id, Role.MANAGER));
    }
    @Test 
    void updateResetPasswordWhenUserExist(){
        var token="randomToken";
        var email = user1.getEmail();
        LocalDateTime expirationDateTime = LocalDateTime.now().plusHours(1);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));
        assertDoesNotThrow(()-> userService.updateResetPassword(token, email, expirationDateTime));
        assertEquals(token, user1.getResetPasswordToken());
        assertEquals( expirationDateTime, user1.getResetPasswordExpiration());
        verify(userRepository).save(user1);
    }
    @Test
    void updateResetPasswordFailedWhenUserDoesNotExist(){
        var token="randomToken";
        LocalDateTime expirationDateTime = LocalDateTime.now().plusHours(1);
        when(userRepository.findByEmail("pas@gmail.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, ()-> userService.updateResetPassword(token, "pas@gmail.com", expirationDateTime));
    }
    @Test
    void isResetTokenValidShouldReturnTrue(){
        LocalDateTime expirationDateTime = LocalDateTime.now().plusHours(1);
        user1.setResetPasswordExpiration(expirationDateTime);
        assertTrue(userService.isResetTokenValid(user1));
    }
    @Test
    void isResetTokenVaildShouldReturnFalse(){
        assertFalse(userService.isResetTokenValid(user1));
    }
    @Test
    void getResetPasswordTokenSouldReturnUserWhenUserExists(){
        String token ="token";
        when(userRepository.findByResetPasswordToken(token)).thenReturn(user1);
        var userResult= userService.getResetPasswordToken(token);
        assertEquals(userResult, user1);

    }
    @Test
    void getResetPasswordTokenwHhenUserDoesNotExist(){
        String token ="token";
        when(userRepository.findByResetPasswordToken(token)).thenReturn(null);
        var result= userService.getResetPasswordToken(token);
        assertNull(result);
    }
    @Order(1)
    @Test
    void updateUserPasswordWhenUserExists(){
        var newPassword= "Test12345";
        userService.updateUserPassword(user1, newPassword);
        assertEquals(null, user1.getResetPasswordToken());
        verify(userRepository).save(user1);
        assertTrue(new BCryptPasswordEncoder().matches(newPassword, user1.getPassword()));
    }
    // to do !!!
    @Test
    void isPasswordValidWhenUserExistsAndPasswordIsCorrectTest(){
        var password= "Test1234!";
        var email= "test@gmail.com";
        var encodedPassword = new BCryptPasswordEncoder().encode(password);
        user1.setPassword(encodedPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));
        assertTrue(userService.isPasswordValid(email, password));
    }
    @Test
    void isPasswordValidWhenUserExistsAndPasswordIsIncorrectTest(){
        var password= "iNCORECTpASSWORD";
        var encodedPassword= new BCryptPasswordEncoder().encode("Test1234!");
        var email= "test@gmail.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        assertFalse(userService.isPasswordValid(email, password));
    }
    @Test
    void isPasswordValidWhenUserDoesNotExist(){
        var email= "test@gmail.com";
        var password= "Test1234!";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertFalse(userService.isPasswordValid(email, password));

    }
    @Test
    void changePasswordWhenUserExists(){
        var email= "test@gmail.com";
        var newPassword= "Test1234!";
        User user = new User();
        user.setEmail(email);
        user.setPassword("OldPassword");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        assertDoesNotThrow(()->{
            userService.changePassword(email, newPassword);
        });
        assertNotEquals("OldPassword", user.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches(newPassword, user.getPassword()));
        verify(userRepository).save(user);

    }
    @Test
    void changepasswordWhenUserDoesNotExistTest(){
        var email= "test@gmail.com";
        var password= "Test1234!";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(UserCanNotBeNullException.class,()-> userService.changePassword(email, password));
        verify(userRepository, never()).save(any());
    }
    @Test
    void arePasswordTheSameWhenPasswardsAreTheSame(){
        var password1= "Test1234!";
        var password= "Test1234!";
        assertTrue(userService.arePasswordTheSame(password, password1));
    }
    @Test
    void arePasswordTheSameWhenPasswardsAreDifferent()
{
    var password1= "Test1234!";
    var password= "Test124!";
    assertFalse(userService.arePasswordTheSame(password, password1));
}
@Test
void isNewPasswordCorrectWhenPasswordMatchesPattern(){
    var newPassword="Test12345";
    assertTrue(userService.isNewPasswordCorrect(newPassword));
}
@Test
void isNewPasswordCorrectWhenPasswordDoesNotMatchPattern(){
    var newPassword="Test";
    assertFalse(userService.isNewPasswordCorrect(newPassword));
}
}
