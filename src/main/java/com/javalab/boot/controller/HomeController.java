package com.javalab.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

/**
 * 컨트롤러
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        System.out.println("HomeController");
        model.addAttribute("date", new Date());
        return "redirect:/item/list";
    }
}
