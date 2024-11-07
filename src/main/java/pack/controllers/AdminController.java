package pack.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import pack.models.PageView;
import pack.models.Service;
import pack.models.Staff;
import pack.repositories.AdminRepository;
import pack.services.EmailService;
import pack.services.OtpService;
import pack.utils.FileUtility;
import pack.utils.SecurityUtility;
import pack.utils.Views;

@RequestMapping("/admin")
@Controller
public class AdminController {

	@Autowired
	AdminRepository rep;

	@Autowired
	OtpService otpService;

	@Autowired
	EmailService emailService;

	// -------------------- INDEX --------------------//

	@GetMapping("")
	public String index() {
		return Views.ADMIN_INDEX;
	}

	// -------------------- ACCOUNTS --------------------//

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

	@GetMapping("/forgotPassword")
	public String forgot_password() {
		return Views.ADMIN_FORGOT_PASSWORD;
	}

	@PostMapping("/getOtp")
	public String get_otp(String email, Model model, HttpServletRequest req) {
		try {
			if (!email.contains("@")) {
				model.addAttribute("pageError", "Invalid email.");
				return Views.ADMIN_FORGOT_PASSWORD;
			}
			Admin admin = rep.checkEmailExists(email);
			if (admin == null) {
				model.addAttribute("pageError", "Email is not registered, yet.");
				return Views.ADMIN_FORGOT_PASSWORD;
			}
			otpService.generateOTP(email);
			req.getSession().setAttribute("email", admin.getEmail());
			return "redirect:/admin/validateOtp";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@GetMapping("/validateOtp")
	public String validate_otp() {
		return Views.ADMIN_VALIDATE;
	}

	@PostMapping("/verification")
	public String verify(@RequestParam String otp, HttpServletRequest req, Model model) {
		String email = req.getSession().getAttribute("email").toString();
		Admin admin = rep.checkEmailExists(email);
		if (!otpService.validateOtp(email, otp)) {
			model.addAttribute("error", "Invalid otp");
			return Views.ADMIN_VALIDATE;
		} else if (otpService.isOtpExpired(email)) {
			model.addAttribute("error", "OTP is expired.");
			return Views.ADMIN_VALIDATE;
		}

		req.getSession().setAttribute("adminId", admin.getId());
		req.getSession().setAttribute("username", admin.getUsername());
		return "redirect:/admin/accounts";
	}

	// -------------------- BLOGS --------------------//

	@GetMapping("/blogs/list")
	public String blog_list(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageCurrent(cp);
		pv.setPageSize(20);

		model.addAttribute("blogs", rep.getBlogs(pv));
		model.addAttribute("pv", pv);

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

		return "redirect:/admin/blogs/list";
	}

	// -------------------- SERVICES --------------------//

	@GetMapping("/services/list")
	public String service_list(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageCurrent(cp);
		pv.setPageSize(20);

		model.addAttribute("services", rep.getServices(pv));
		model.addAttribute("pv", pv);
		return Views.ADMIN_SERVICES_LIST;
	}

	@GetMapping("/services/create")
	public String create_service_view(Model model) {
		model.addAttribute("new_item", new Service());
		return Views.ADMIN_SERVICES_CREATE;
	}

	@PostMapping("/services/createService")
	public String create_service(@RequestParam String serName, @RequestParam(required = false) String description,
			@RequestParam double basePrice, @RequestParam int staffRequired,
			@RequestParam(required = false) MultipartFile image, Model model) {
		try {
			Service ser = new Service();
			ser.setSerName(serName);
			ser.setDescription(description);
			ser.setBasePrice(basePrice);
			ser.setStaffRequired(staffRequired);

			if (image != null && !image.isEmpty()) {
				ser.setImage(FileUtility.uploadFileImage(image, "upload"));
			} else {
				ser.setImage(null);
			}

			String result = rep.newService(ser);
			if (result.equals("success")) {
				return "redirect:/admin/services/list";
			}
			model.addAttribute("catchError", "Failed to create service, please try again.");

			return Views.ADMIN_SERVICES_CREATE;
		} catch (IllegalArgumentException e) {
			model.addAttribute("catchError", "Service name may already exists.");
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
				model.addAttribute("service", get);
				return Views.ADMIN_SERVICES_EDIT;
			} else {
				return "redirect:/admin/services/list";
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
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", "Service name may already exists.");
			return Views.ADMIN_SERVICES_EDIT;
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");

			return Views.ADMIN_SERVICES_EDIT;
		}
	}

	@PostMapping("/services/activate")
	public String activate_service(@RequestParam("id") int id, Model model) {
		try {
			Service get = rep.getServiceById(id);
			if (get != null) {
				rep.activateServiceStatus(id);
				return "redirect:/admin/services/list";
			}
			return Views.ADMIN_SERVICES_LIST;
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			return "redirect:/admin/services/list?error=activateFail";
		}
	}

	@PostMapping("/services/disable")
	public String disable_service(@RequestParam("id") int id, Model model) {
		try {
			Service get = rep.getServiceById(id);
			if (get != null) {
				rep.disableServiceStatus(id);
				return "redirect:/admin/services/list";
			}
			return Views.ADMIN_SERVICES_LIST;
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			return "redirect:/admin/services/list?error=disableFail";
		}
	}

	// -------------------- ORDERS --------------------//

	@GetMapping("/orders/list")
	public String order_list(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageCurrent(cp);
		pv.setPageSize(20);

		model.addAttribute("pv", pv);
		model.addAttribute("orders", rep.getOrders(pv));
		model.addAttribute("staffs", rep.getStaffs(pv));

		return Views.ADMIN_ORDERS_LIST;
	}
	
	@GetMapping("/orders/request")
	public String order_request() {
		
		return Views.ADMIN_ORDERS_REQUEST;
	}

	// -------------------- STAFFS -------------------- //

	@GetMapping("/staffs/list")
	public String staff_list(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageCurrent(cp);
		pv.setPageSize(20);

		model.addAttribute("staffs", rep.getStaffs(pv));
		model.addAttribute("pv", pv);

		return Views.ADMIN_STAFFS_LIST;
	}

	@GetMapping("/staffs/create_account")
	public String create_account_view(Model model) {
		model.addAttribute("new_item", new Staff());

		return Views.ADMIN_STAFFS_CREATE_ACCOUNT;
	}

	@PostMapping("/staffs/createAccount")
	public String create_account(@ModelAttribute("new_item") Staff staff, Model model) {
		try {
			String result = rep.newStaff(staff);
			if (result.equals("success")) {
				emailService.SendMail(staff.getEmail(), "Your staff Account",
						"Username: " + staff.getUsername() + "\n Password: " + staff.getPassword());
				return Views.ADMIN_STAFFS_LIST;
			}
			return Views.ADMIN_STAFFS_CREATE_ACCOUNT;
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", "Some information(username, email) may already exists.");
			return Views.ADMIN_STAFFS_CREATE_ACCOUNT;
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("error", "An unexpected error occurred. Please try again later.");
			return Views.ADMIN_STAFFS_CREATE_ACCOUNT;
		}
	}

	@GetMapping("/staffs/accounts")
	public String staff_accounts(int id, Model model) {
		model.addAttribute("staffs", rep.getStaffById(id));
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

	@PostMapping("/staffs/disabledAccount")
	public ResponseEntity<String> disabled_account(@RequestParam int id) {
		String result = rep.disableStaff(id);
		if (result.equals("success")) {
			return ResponseEntity.ok("success");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
	}
}
