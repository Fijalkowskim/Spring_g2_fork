package com.example.demo.cotroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CokkieController {
    @GetMapping("/set-cookies")
    public String setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("new_cookie", "cookie_value");
        cookie.setPath("/");
        cookie.setMaxAge(60);
        response.addCookie(cookie);
        return "redirect:/";
    }
    
}
