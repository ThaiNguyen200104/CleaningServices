package pack.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import pack.models.Service;
import pack.repositories.AdminRepository;
import pack.utils.Views;

@RequestMapping("")
@Controller
public class HomeController {

	@Autowired
	AdminRepository rep;

	@ModelAttribute
	public void addCurrentPath(HttpServletRequest req, Model model) {
		model.addAttribute("currentPath", req.getRequestURI());
	}

	@GetMapping("")
	public String index(Model model) {
		List<Service> list = rep.getServices();

		int limit = 8;
		if (list.size() > limit) {
			list = list.subList(0, limit);
		}
		model.addAttribute("services", list);

		return Views.MAIN_INDEX;
	}

	@GetMapping("/service")
	public String service(Model model) {
		List<Service> list = rep.getServices();

		int limit = 12;
		if (list.size() > limit) {
			list = list.subList(0, limit);
		}
		model.addAttribute("services", list);
        model.addAttribute("currentPage", "service");

		return Views.MAIN_SERVICES;
	}

	@GetMapping("/blog")
	public String blog(Model model) {
		
        model.addAttribute("currentPage", "blog");
        
		return Views.MAIN_BLOG;
	}

	@GetMapping("/about")
	public String about(Model model) {
        model.addAttribute("currentPage", "about");

		return Views.MAIN_ABOUT;
	}

	@GetMapping("/contact")
	public String contact(Model model) {
        model.addAttribute("currentPage", "contact");

		return Views.MAIN_CONTACT;
	}
}
