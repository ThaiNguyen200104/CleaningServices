package pack.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pack.utils.Views;

@RequestMapping("")
@Controller
public class HomeController {
	
	@GetMapping("")
	public String index() {
		return Views.MAIN_INDEX;
	}
	
	@GetMapping("/service")
	public String service() {
		return Views.MAIN_SERVICES;
	}
	
	@GetMapping("/blog")
	public String blog() {
		return Views.MAIN_BLOG;
	}
	
	@GetMapping("/about")
	public String about() {
		return Views.MAIN_ABOUT;
	}
	
	@GetMapping("/contact")
	public String contact() {
		return Views.MAIN_CONTACT;
	}
}
