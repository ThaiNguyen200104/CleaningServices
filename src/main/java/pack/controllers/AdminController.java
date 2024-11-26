package pack.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import pack.models.Admin;
import pack.models.Blog;
import pack.models.PageView;
import pack.models.Schedule;
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

			if (staffRequired <= 0) {
				model.addAttribute("catchError", "Staff required cannot be 0 or less.");
				return Views.ADMIN_SERVICES_CREATE;
			} else {
				ser.setStaffRequired(staffRequired);
			}

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
			e.printStackTrace();
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");
			return Views.ADMIN_SERVICES_CREATE;
		}
	}

	@GetMapping("/services/edit")
	public String edit_service_view(@RequestParam int id, Model model) {
		try {
			Service get = rep.getServiceById(id);
			if (get != null) {
				model.addAttribute("edit_item", get);
				return Views.ADMIN_SERVICES_EDIT;
			}
			return "redirect:/admin/services/list";
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");
			return Views.ADMIN_SERVICES_EDIT;
		}
	}

	@PostMapping("/services/editService")
	public String edit_service(@ModelAttribute("edit_item") Service ser, BindingResult br,
			@RequestParam(required = false) MultipartFile image, @RequestParam(required = false) String status,
			Model model) {
		try {
			if (image != null && !image.isEmpty()) {
				ser.setImage(FileUtility.uploadFileImage(image, "upload"));
			}

			if (ser.getBasePrice() <= 0) {
				model.addAttribute("error", "The price should not be negative or free. Please raise the price.");
				return Views.ADMIN_SERVICES_EDIT;
			}

			if (ser.getStaffRequired() <= 0) {
				model.addAttribute("error", "A minimum of one staff member is necessary. Please increase the number.");
				return Views.ADMIN_SERVICES_EDIT;
			}

			String edit = rep.editService(ser);
			if (edit.equals("success")) {
				return "redirect:/admin/services/list";
			}
			if (br.hasErrors()) {
				model.addAttribute("error", "There was an error with your input.");
				return Views.ADMIN_SERVICES_EDIT;
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

	@GetMapping("/orders/assignStaff")
	public String orderlisttoassign(@RequestParam int id, Model model) {
		model.addAttribute("staffs", rep.staffListForAssign(id));
		model.addAttribute("Ord_id", id);
		model.addAttribute("detail", rep.getDetailById(id));
		return Views.ADMIN_ORDERS_ASSIGN_STAFF;
	}

	@PostMapping("/newSchedule")
	public ResponseEntity<String> assignStaff(@RequestBody Map<String, Object> payload) {
		List<Integer> staffIds = (List<Integer>) payload.get("staffIds");
		String startDateStr = (String) payload.get("startDate");
		Integer orderId = Integer.parseInt(payload.get("orderId").toString());

		if (staffIds == null || staffIds.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No staff selected.");
		}
		if (startDateStr == null || startDateStr.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date is required.");
		}

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
			LocalDateTime startDate = LocalDateTime.parse(startDateStr, formatter);
			for (Integer staffId : staffIds) {
				Schedule schedule = new Schedule();
				schedule.setStaffId(staffId);
				schedule.setStartDate(startDate);
				schedule.setDetailId(orderId);

				String result = rep.assignStaff(schedule);
				if (!"success".equals(result)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
				}
			}
			return ResponseEntity.ok("Staff assigned successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while assigning staff.");
		}
	}

	@GetMapping("/orders/replaceStaff")
	public String editStaffInOrder(@RequestParam int id, Model model) {
		model.addAttribute("staffs", rep.getCurrentStaff(id));
		model.addAttribute("detailId", id);
		return Views.ADMIN_ORDERS_REPLACE_STAFF;
	}

	@GetMapping("/orders/staffToReplaceWith")
	public String editschedultstaff(@RequestParam int detailId, @RequestParam int staffId, Model model) {
		model.addAttribute("staffs", rep.getAvailableStaffForReplacement(detailId, staffId));
		model.addAttribute("currentStaff", staffId);
		model.addAttribute("detailId", detailId);
		return Views.ADMIN_ORDERS_STAFF_FOR_REPLACE;
	}

	@GetMapping("/replaceStaff")
	public String replaceaction(@RequestParam int detailId, @RequestParam int currentStaff, @RequestParam int newStaff,
			Model model) {
		String result = rep.ReplaceStaff(currentStaff, newStaff, detailId);
		if (result.equals("success")) {
			return "redirect:/admin/orders/list";
		}
		model.addAttribute("error", "Replace failed due to some errors");
		return Views.ADMIN_ORDERS_STAFF_FOR_REPLACE;
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
			if (!staff.getPhone().matches(Views.PHONE_REGEX)) {
				model.addAttribute("error",
						"Your phone number must only have digits and be at least 10 to 11 digits long.");
				return Views.ADMIN_STAFFS_CREATE_ACCOUNT;
			}
			if (result.equals("success")) {
				return "redirect:/admin/staffs/list";
			}
			return Views.ADMIN_STAFFS_CREATE_ACCOUNT;
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", "Some information(username, email, phone) may already exists.");
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

	@PostMapping("/staffs/disabledAccount")
	public ResponseEntity<String> disabled_account(@RequestParam int id) {
		String result = rep.disableStaff(id);
		if (result.equals("success")) {
			return ResponseEntity.ok("success");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
	}
}
