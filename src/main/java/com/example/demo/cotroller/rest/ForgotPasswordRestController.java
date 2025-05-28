package com.example.demo.cotroller.rest;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.services.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
//

@RestController
@RequestMapping("/api/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordRestController {
    final private UserService userService;
    final private JavaMailSender javaMailSender;
    final private static int TOKEN_EXPIRATION_MINUTES=30;

    @PostMapping
    public ResponseEntity<String> forgotUserPassword(HttpServletRequest request){
        var email = request.getParameter("email");
        UUID randomUuid = UUID.randomUUID();
        var token = randomUuid.toString().replaceAll("-","");
        var expirationDateTime= LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);
        try{
            userService.updateResetPassword(token, email, expirationDateTime);
            var resetLink=ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/forgot-password/").path(token).toUriString();
            sendRestApiEmail(email, resetLink);
            return ResponseEntity.accepted().body("Email was send correctly, check inbox including  the Span folder");
            
        }catch(UserNotFoundException e)
        {
            return ResponseEntity.badRequest().body("User with: "+email+ " not found. Please register");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to send emial. Please try again later");
        }
    }

    private void sendRestApiEmail(String email, String link) throws UnsupportedEncodingException, MessagingException{
        MimeMessage message= javaMailSender.createMimeMessage();
        MimeMessageHelper helper= new MimeMessageHelper(message);
        helper.setFrom("RestSupport@gamil.com","Rest ResetPassword Support");
        helper.setTo(email);
        var subject= "Here's the link to reset your password";
        var userData = userService.findUserByEmail(email);
        var user=userData.get();

        var content = "<p> Hello,"+user.getFirstName()+ " "+ user.getLastName() +"</p>"
        + "<p> You have requested to reset your password.</p>"
        +"<p>Click  the link elow to change your password:</p>"
        +"<p><b> <a href=\""+ link +"\"> Change my password</a></p>"
        +"<br>"
        +"<p> Please ignore above message if you don't remeber to make teh request</p>"
        +"<br>"
        +"<p><b>Best regrets, HelpCenter</p>";
        helper.setSubject(subject);
        helper.setText(content, true);
        javaMailSender.send(message);
    }
    @GetMapping("/{token}")
    public ResponseEntity<String> showResetPasswordToken(@PathVariable String token){
        var user= userService.getResetPasswordToken(token);

        if(Objects.isNull(user) || !userService.isResetTokenValid(user)){
            return ResponseEntity.badRequest().body("Token has been expired or is invalid");
        }else{
            return ResponseEntity.ok("Procced to reset password");
        }
    }
    @PostMapping("/{token}")
    public ResponseEntity<String> changePassword(@PathVariable String token, @RequestParam String password)
{
    var user= userService.getResetPasswordToken(token);
    if(Objects.isNull(user)){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid token");
    }else{
        userService.updateUserPassword(user, password);
        return ResponseEntity.accepted().body("Password Updated");
    }
}
}
