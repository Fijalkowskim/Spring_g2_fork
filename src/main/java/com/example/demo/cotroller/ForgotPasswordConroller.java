package com.example.demo.cotroller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.services.UserService;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ForgotPasswordConroller {
    final private UserService userService;
    final private JavaMailSender javaMailSender;
    final private static int TOKEN_EXPIRATION_MINUTES=30;

    @GetMapping("/forgotPassword")
    public String forgotPasswordForm( Model model){
        model.addAttribute("title", "Forgot Password");
        return"/password/forgot-password";
 }
 @PostMapping("/forgotPassword")
 public String forgotPasswordProccesss(HttpServletRequest  request, RedirectAttributes redirectAttributes )
 {
    var email = request.getParameter("email");
    UUID randomUuid= UUID.randomUUID();
    var token = randomUuid.toString().replaceAll("-", "");

    // czas wygaśnienia :
    LocalDateTime expirationDateTime= LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);

    try {
        userService.updateResetPassword(token, email,expirationDateTime);
        var resetLinkPassword= ServletUriComponentsBuilder.fromRequestUri(request).build();
        var resetLink= resetLinkPassword +"/resetPassword?token="+token;
        try {
            sendEmail(email,resetLink);
            redirectAttributes.addFlashAttribute("error","Email was send corectly. Check your mail including Spam folder");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error","Email wasn't sent corectly, try again later");
        }


  
    } catch (UserNotFoundException e) {
       redirectAttributes.addFlashAttribute("error", "User with emial: "+ email +" not found. Please register.");
       return"redirect:/forgotPassword";
    }
    return"redirect:/";
 }


 private void sendEmail(String email, String link) throws Exception{
        MimeMessage message= javaMailSender.createMimeMessage();
        MimeMessageHelper helper= new MimeMessageHelper(message);
        helper.setFrom("TestSupport@gamil.com", "Test Support");
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

 @GetMapping("/forgotPassword/{token}")
 public String showResetPasswordForm( @Param( value="token") String token, Model model)
{
    var user= userService.getResetPasswordToken(token);
    if(user==null || !userService.isResetTokenValid(user)){
        model.addAttribute("error", "Token has been expired");
        model.addAttribute("errorAction", "/forgotPassword");
        model.addAttribute("return", "Return to forget password page");
        return"/error-page";
    }else{
        model.addAttribute("token", token);
        return "/password/reset-password";
    }
}    
@PostMapping("/reset-password")
public String changePassword(@RequestParam("token") String token, @RequestParam("password") String password, RedirectAttributes redirectAttributes)
{
    var user= userService.getResetPasswordToken(token);
    if(user== null){
        redirectAttributes.addFlashAttribute("error", "Invalid Token");
        redirectAttributes.addFlashAttribute("errorAction","/forgotPassowrd");
        redirectAttributes.addFlashAttribute("return","Return to forgot page to procede with password reset");
        return "/error-page";
    }else{
        userService.updateUserPassword(user, password);
        redirectAttributes.addFlashAttribute("message", "Password Updated");
    }
    return"redirect:/";
}
}
