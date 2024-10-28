package pack.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pack.repositories.HomeRepository;
import pack.utils.Views;

@RequestMapping("")
@Controller
public class HomeController {

	@Autowired
	HomeRepository rep;

	@GetMapping("")
	public String index(Model model) {
		model.addAttribute("services", rep.getServices());

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
