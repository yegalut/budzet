package com.projekt_pai.budzet.controllers;

import com.projekt_pai.budzet.entities.Finance;
import com.projekt_pai.budzet.entities.User;
import com.projekt_pai.budzet.repositories.CategoryRepository;
import com.projekt_pai.budzet.repositories.FinanceRepository;
import com.projekt_pai.budzet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class UserPageController {
    private final UserRepository userRepository;
    private final FinanceRepository financeRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public UserPageController(UserRepository userRepository, FinanceRepository financeRepository, CategoryRepository categoryRepository){
        this.userRepository=userRepository;
        this.financeRepository=financeRepository;
        this.categoryRepository=categoryRepository;
    }

    @GetMapping("/user_page")
    public String get(@CookieValue(value = "username", required = true)String username,
                      @RequestParam(value = "action", required = true) String action,
                      Model model,
                      HttpServletResponse response){

        if(username==null || username.isEmpty()){
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(username);

        if(user==null){
            return "redirect:/login";
        }
        if(action.equals("account_info")){
            model.addAttribute("user", user);
        }else if(action.equals("finances")){
            List<Finance> finances= financeRepository.findAllByUserId(user.getId());
            model.addAttribute("finances",finances);
        }else if(action.equals("add")){
            model.addAttribute("finance", new Finance());
        }







        model.addAttribute("action", action);
        return "user_page";
    }

    @PostMapping("/add_product")
    public String addFinance(@ModelAttribute Finance finance,
                             @CookieValue(value = "username", required = true) String username,
                             Model model,
                             RedirectAttributes redirectAttributes
                             ){
        redirectAttributes.addAttribute("action","finances");
        return "redirect:/user_page";


    }

}
