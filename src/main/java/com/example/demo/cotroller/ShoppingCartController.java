package com.example.demo.cotroller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.exceptions.CartItemCanNotBeNull;
import com.example.demo.exceptions.MaxValueException;
import com.example.demo.exceptions.ProductCanNotBeNullException;
import com.example.demo.exceptions.ProductNotFoundExeption;
import com.example.demo.exceptions.QuantityMinException;
import com.example.demo.services.CartItemService;
import com.example.demo.services.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ShoppingCartController {
    final UserService userService;
    final CartItemService cartItemService;

    @GetMapping("/shoppingCart")
    public String showShoppingCart(Model model, Authentication authentication){
        var user = userService.findUserByEmail(authentication.getName()).get();
    model.addAttribute("items", cartItemService.listCartItems(user));
    model.addAttribute("totalPrice", cartItemService.calculateTotalPrice(cartItemService.listCartItems(user)));
        return"/cart/shopping-cart";
    }
    

    @PostMapping("/addToShoppingCart/{id}")
    public String addProductToTheShoppingCart(@PathVariable Long id, @RequestParam Integer quantity, Authentication authentication, RedirectAttributes redirectAttributes ){
        try {
            cartItemService.addItemToTheCart(id, quantity, userService.findUserByEmail(authentication.getName()).get());
            redirectAttributes.addFlashAttribute("message"," Product has been added corectly ");
        } catch (QuantityMinException e) {
            redirectAttributes.addFlashAttribute("message", "Quantity mas be gretter than 0 ");
        }
       
        return"redirect:/product";
    }
    @PostMapping("/cart/removeQuatity/{id}")
    public String removeQuantity(@PathVariable Long id, Authentication authentication,RedirectAttributes redirectAttributes){
        try {
            cartItemService.decreaseQuantity(id, 1, userService.findUserByEmail(authentication.getName()).get());
        } catch (ProductCanNotBeNullException | CartItemCanNotBeNull e) {
            redirectAttributes.addFlashAttribute("message", "Quantity must be positive");
        }
        return"redirect:/shoppingCart";
    }
    @PostMapping("/cart/addQuantity/{id}")
    public String addQuantity(@PathVariable Long id, Authentication authentication,RedirectAttributes redirectAttributes) throws ProductNotFoundExeption{
        try {
            cartItemService.increaseQuantity(id, 1, userService.findUserByEmail(authentication.getName()).get());
        } catch (MaxValueException | ProductCanNotBeNullException e) {
            redirectAttributes.addFlashAttribute("message", "Quantity can not be gretter that max value of current product");
        }
        return"redirect:/shoppingCart";
    }

    @PostMapping("/cart/delete/{id}")
    public String deleteCurrentItem(@PathVariable Long id){
        cartItemService.deleteCartItem(id);
        return"redirect:/shoppingCart";
    }
}
