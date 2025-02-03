package pack.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import pack.models.PageView;
import pack.repositories.HomeRepository;
import pack.utils.Views;

@RequestMapping("")
@Controller
public class HomeController {

	@Autowired
	HomeRepository rep;

	// -------------------- INDEX -------------------- //
	@ModelAttribute
	public void addCurrentPath(HttpServletRequest req, Model model) {
		model.addAttribute("currentPath", req.getRequestURI());
	}

	@GetMapping("")
	public String index(Model model) {
		model.addAttribute("services", rep.getTop5Services());
		model.addAttribute("staffs", rep.getTop4Staffs());

		return Views.MAIN_INDEX;
	}

	@GetMapping("/service")
	public String service(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageCurrent(cp);
		pv.setPageSize(8);

		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);

		model.addAttribute("pv", pv);
		model.addAttribute("services", rep.getAllServices(pv));
		model.addAttribute("currentPage", "service");

		return Views.MAIN_SERVICE;
	}

	@GetMapping("/blog")
	public String blog(Model model) {
		model.addAttribute("blogs", rep.getBlogs());
		model.addAttribute("currentPage", "blog");

		return Views.MAIN_BLOG;
	}

	@GetMapping("/blogDetail")
	public String blog_detail(Model model, @RequestParam int id) {
		model.addAttribute("blogDetails", rep.getBlogById(id));
		model.addAttribute("currentPage", "blog");
		
		return Views.MAIN_BLOG_DETAIL;
	}

	@GetMapping("/about")
	public String about(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageCurrent(cp);
		pv.setPageSize(4);

		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);

		model.addAttribute("pv", pv);
		model.addAttribute("staffs", rep.getAllStaffs(pv));
		model.addAttribute("currentPage", "about");

		return Views.MAIN_ABOUT;
	}

	@GetMapping("/contact")
	public String contact(Model model) {
		model.addAttribute("currentPage", "contact");
		return Views.MAIN_CONTACT;
	}

}
