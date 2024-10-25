package pack.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import pack.models.Admin;
import pack.models.Blog;
import pack.models.Order;
import pack.models.Service;
import pack.models.Staff;
import pack.repositories.AdminRepository;
import pack.utils.FileUtility;
import pack.utils.SecurityUtility;
import pack.utils.Views;

@RequestMapping("/admin")
@Controller
public class AdminController {

	@Autowired
	AdminRepository rep;
	// -------------------- INDEX & ACCOUNT --------------------//

	@GetMapping("")
	public String index() {
		return Views.ADMIN_INDEX;
	}

	@GetMapping("/login")
	public String login() {
		return Views.ADMIN_LOGIN;
	}

	@PostMapping("/checkLogin")
	public String check_login(@RequestParam("usrname") String username, @RequestParam("pw") String password,
			HttpServletRequest req, Model model) {
		Admin get = rep.getAdminByUsername(username);

		if (get == null) {
			model.addAttribute("loginError", "Account doesn't exists, please check again!");
			return Views.ADMIN_LOGIN;
		}

		if (!SecurityUtility.compareBcrypt(get.getPassword(), password)) {
			model.addAttribute("loginError", "Password incorrect!");
			return Views.ADMIN_LOGIN;
		}

		req.getSession().setAttribute("adminId", get.getId());
		return "redirect:/admin";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest req) {
		req.getSession().invalidate();

		return "redirect:/admin/login";
	}

	// -------------------- BLOGS --------------------//

	@GetMapping("/blogs/list")
	public String blog_list(Model model) {
		List<Blog> list = rep.getBlogs();

		model.addAttribute("blogs", list);

		return Views.ADMIN_BLOGS_LIST;
	}

	@GetMapping("/blogs/create")
	public String create_blog_view(Model model) {
		model.addAttribute("new_item", new Blog());

		return Views.ADMIN_BLOGS_CREATE;
	}

	@PostMapping("/blogs/createBlog")
	public String create_blog(@ModelAttribute("new_item") Blog blog, Model model) {
		try {
			String create = rep.newBlog(blog);

			if (create.equals("success")) {
				return "redirect:/admin/blogs/list";
			}

			model.addAttribute("catchError", "Failed to create blog, please try again.");

			return Views.ADMIN_BLOGS_CREATE;
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");

			return Views.ADMIN_BLOGS_CREATE;
		}
	}

	@GetMapping("/blogs/edit")
	public String edit_blog_view(int id, Model model) {
		try {
			Blog get = rep.getBlogById(id);

			if (get != null) {
				model.addAttribute("service", get);

				return Views.ADMIN_BLOGS_EDIT;
			} else {
				return "redirect:/admin/blogs/list";
			}
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");

			return Views.ADMIN_BLOGS_EDIT;
		}
	}

	@PostMapping("/blogs/editBlog")
	public String edit_blog(@ModelAttribute("edit_item") Blog blog, Model model) {
		try {
			String edit = rep.editBlog(blog);

			if (edit.equals("success")) {
				return "redirect:/admin/blogs/list";
			}

			model.addAttribute("catchError", "Failed to edit blog, please try again.");

			return Views.ADMIN_BLOGS_EDIT;
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");

			return Views.ADMIN_BLOGS_EDIT;
		}
	}

	@PostMapping("/blogs/delete")
	public String delete_blog(int id) {
		rep.deleteBlog(id);

		return "redirect:/admin/blogs/blogList";
	}

	// -------------------- SERVICES --------------------//

	@GetMapping("/services/list")
	public String service_list(Model model) {
		List<Service> list = rep.getServices();

		model.addAttribute("services", list);

		return Views.ADMIN_SERVICES_LIST;
	}

	@GetMapping("/services/create")
	public String create_service_view(Model model) {
		model.addAttribute("new_item", new Service());
		return Views.ADMIN_SERVICES_CREATE;
	}

	@PostMapping("/services/createService")
	public String create_service(@RequestParam String serName, @RequestParam(required = false) String description,
			@RequestParam double basePrice, @RequestParam int duration,
			@RequestParam(required = false) MultipartFile image, Model model) {
		try {
			Service ser = new Service();
			ser.setSerName(serName);
			ser.setDescription(description);
			ser.setBasePrice(basePrice);
			ser.setDuration(duration);
			
			if (image != null && !image.isEmpty()) {
				ser.setImage(FileUtility.uploadFileImage(image, "upload"));
			}else {
				ser.setImage(null);
			}
			String result = rep.newService(ser);

			if (result.equals("success")) {
				return "redirect:/admin/services/list";
			}

			model.addAttribute("catchError", "Failed to create service, please try again.");
			return Views.ADMIN_SERVICES_CREATE;
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");

			return Views.ADMIN_SERVICES_CREATE;
		}
	}

	@GetMapping("/services/edit")
	public String edit_service_view(int id, Model model) {
		try {
			Service get = rep.getServiceById(id);

			if (get != null) {
				model.addAttribute("edit_item", get);

				return Views.ADMIN_SERVICES_EDIT;
			} else {
				return "redirect:/admin/blogs/list";
			}
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");

			return Views.ADMIN_SERVICES_EDIT;
		}
	}

	@PostMapping("/services/editService")
	public String edit_service(@ModelAttribute("edit_item") Service ser, Model model) {
		try {
			String edit = rep.editService(ser);

			if (edit.equals("success")) {
				return "redirect:/admin/services/list";
			}

			model.addAttribute("catchError", "Failed to edit blog, please try again.");

			return Views.ADMIN_SERVICES_EDIT;
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");

			return Views.ADMIN_SERVICES_EDIT;
		}
	}

	// -------------------- ORDERS --------------------//

	@GetMapping("/orders/list")
	public String order_list(Model model) {
		List<Order> order = rep.getOrders();
		List<Staff> staff = rep.getStaffs();

		model.addAttribute("orders", order);
		model.addAttribute("staffs", staff);

		return Views.ADMIN_ORDERS_LIST;
	}

	// -------------------- STAFFS -------------------- //

	@GetMapping("/staffs/list")
	public String staff_list(Model model) {
		List<Staff> list = rep.getStaffs();

		model.addAttribute("staffs", list);

		return Views.ADMIN_STAFFS_LIST;
	}

	@GetMapping("/staffs/create_account")
	public String create_account_view(Model model) {
		model.addAttribute("new_item", new Staff());

		return Views.ADMIN_STAFFS_CREATE_ACCOUNT;
	}

	@PostMapping("/staffs/createAccount")
	public String create_account() {
		return Views.ADMIN_STAFFS_CREATE_ACCOUNT;
	}

	@GetMapping("/staffs/accounts")
	public String staff_accounts(int id, Model model) {
		Staff get = rep.getStaffById(id);

		model.addAttribute("staffs", get);

		return Views.ADMIN_STAFFS_INFO;
	}

	@PostMapping("/staffs/assignJob")
	public String assign_job() {
		return "";
	}

	@PostMapping("/staffs/replaceStaff")
	public String replace_staff() {
		return "";
	}

	@PostMapping("/staffs/disabled")
	public String disabled() {
		return "";
	}
}
