
package com.prueba.pruebaAPI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin
@RequestMapping("/")
public class HomeController {

	@GetMapping
	public String showAPIDocs() {
		return "redirect:/swagger-ui/index.html";
	}
}
