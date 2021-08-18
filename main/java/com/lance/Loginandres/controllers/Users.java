package com.lance.Loginandres.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lance.Loginandres.models.User;
import com.lance.Loginandres.services.UserService;

@Controller
public class Users {
    private final UserService userService;
    
    public Users(UserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registrationPage.jsp";
    }
    @RequestMapping("/login")
    public String login() {
        return "loginPage.jsp";
    }
    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
        // if result has errors, return the registration page (don't worry about validations just now)
        if(result.hasErrors()) {
        	return "registrationPage.jsp";
        }else {
        	User newUser = userService.registerUser(user);
        	session.setAttribute("user_id", newUser.getId());
        	return "redirect:/home";
        }
    	// else, save the user in the database, save the user id in session, and redirect them to the /home route
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        // if the user is authenticated, save their user id in session
    	if(userService.authenticateUser(email, password)) {
    		session.setAttribute("user_id", userService.findByEmail(email).getId());
    		return "redirect:/home";
    	}else {
    		model.addAttribute("error", "There is an error something is wrong with your password or email ");
    		return "loginPage.jsp";
    	}
        // else, add error messages and return the login page
    }
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        // get user from session, save them in the model and return the home page
    	Long id = (Long) session.getAttribute("user_id");
    	if(id != null) {
    		User thisUser = userService.findUserById(id);
    		model.addAttribute("user", thisUser);
    		return "homePage.jsp";    		
    	}else {
    		return "redirect:/login";
    	}
    }
    
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
