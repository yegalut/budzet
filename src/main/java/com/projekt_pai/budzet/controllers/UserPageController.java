package com.projekt_pai.budzet.controllers;

import com.projekt_pai.budzet.entities.Category;
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

import javax.management.Query;
import javax.servlet.http.Cookie;
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
    public String get(@CookieValue(value = "username", required = false)String username,
                      @RequestParam(value = "action", required = false) String action,
                      Model model,
                      HttpServletResponse response){


        User user = userRepository.findByEmail(username);
        if(username==null || username.isEmpty()||user==null||action==null){
            return "redirect:/login";
        }


        switch (action) {
            case "account_info":
                model.addAttribute("user", user);
                break;
            case "finances":
                List<Finance> finances = financeRepository.findAllByUserId(user.getId());
                model.addAttribute("finances", finances);
                break;
            case "add_income": {
                List<Category> categories = categoryRepository.findByType("income");
                model.addAttribute("categories", categories);
                model.addAttribute("finance", new Finance());
                break;
            }
            case "add_expense": {
                List<Category> categories = categoryRepository.findByType("expense");
                model.addAttribute("categories", categories);
                model.addAttribute("finance", new Finance());
                break;
            }
            case "logout":
                //delete cookie
                Cookie cookie = new Cookie("username", null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                return "redirect:/login";

        }

        model.addAttribute("action", action);
        return "user_page";
    }

    public Integer getUserIdFromUsername(String username){
        User user = userRepository.findByEmail(username);
        return user.getId();
    }

    @PostMapping("/add")
    public String addFinance(@ModelAttribute Finance finance,
                             @CookieValue(value = "username") String username,
                             Model model,
                             RedirectAttributes redirectAttributes
                             ){
        finance.setuserId(getUserIdFromUsername(username));

        if(finance.getName().isEmpty()){
            System.out.println("getName is empty");
        }else if(finance.getType().isEmpty()){
            System.out.println("getType is empty");
        }
        else if(finance.getAmount()==null){
            System.out.println("getAmount is empty");
        }
        else if(finance.getDate()==null){
            System.out.println("getDate is empty");
        }
        else if(finance.getcategoryId()==null){
            System.out.println("getcategoryId is empty" + finance.getcategoryId());
        }
        else {
            financeRepository.save(finance);

            System.out.println("finance added");
        }




        redirectAttributes.addAttribute("action","finances");
        return "redirect:/user_page";


    }

}
