package com.example.demo.cotroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.exceptions.UserCanNotBeNullException;
import com.example.demo.services.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChangePasswordController {
    final private UserService userService;

    @GetMapping("/changePassword/{id}")
    public String changePassword(@PathVariable Long id, Model model)
{
    model.addAttribute("title", "Change current user password");
    return"/password/change-password";
}

    @PostMapping("/change-password")
    public String changeUserPassword(@RequestParam("password") String oldPassword, @RequestParam("newPassword") String newPassword, RedirectAttributes redirectAttributes){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var optionalUser = userService.findUserByEmail(email);
        // System.out.println(oldPassword);
        // System.out.println(newPassword);
        // System.out.println(userService.isNewPasswordCorrect(newPassword));
      
        if(optionalUser.isPresent()){
            var user = optionalUser.get();
          
            if(userService.arePasswordTheSame(oldPassword, newPassword) || !userService.isNewPasswordCorrect(newPassword)){
                redirectAttributes.addFlashAttribute("message", "Can not change password. Password could be the same or not match with the pattern of password.");
                return"redirect:/changePassword/"+user.getId();
            }
            else{
                try{
                    if( userService.isPasswordValid(email, oldPassword)){
                     userService.changePassword(email, newPassword);
                     // return ResponseEntity.status(HttpStatus.OK).body("Password changed succesfully");
                     // return ResponseEntity.ok("Tresc");
                     redirectAttributes.addFlashAttribute("message", "Password changed succesfully");
                     return"redirect:/myAccount";
                    }else{

                        // toDo
                     // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect");
                     redirectAttributes.addFlashAttribute("message","Old password is incorrect" );
                    }
                 }catch(UserCanNotBeNullException e){
         // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to change password");
                    redirectAttributes.addFlashAttribute("Failed to change password");
                 }
                 return"redirect:/changePassword/"+user.getId();
            }
           
         }else{
            redirectAttributes.addFlashAttribute("error", "Invalid Token");
            return"/error-page";
         }

         
        }
       
}
