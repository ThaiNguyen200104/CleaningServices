package pack.controllers;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public String index(HttpServletRequest req, Model model) {
		Admin admin = rep.getAdminById((int) req.getSession().getAttribute("adminId"));
		model.addAttribute("admin", admin);

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

		rep.checkOrderDetailUpToDate();

		req.getSession().setAttribute("adminId", get.getId());
		return "redirect:/admin";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest req) {
		req.getSession().invalidate();

		return "redirect:/admin/login";
	}

	@GetMapping("/editProfile")
	public String edit_profile(Model model, HttpServletRequest req) {
		try {
			Admin admin = rep.getAdminById((int) req.getSession().getAttribute("adminId"));
			if (admin != null) {
				model.addAttribute("edit_item", admin);
				return Views.ADMIN_EDIT_PROFILE;
			}
			return "redirect:/admin/edit_profile";
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");
			return Views.ADMIN_EDIT_PROFILE;
		}
	}

	@PostMapping("/updateProfile")
	public String update_profile(@ModelAttribute("edit_item") Admin admin, Model model, RedirectAttributes ra,
			HttpServletRequest req) {
		try {
			Admin oldAdminInfo = rep.getAdminById((int) req.getSession().getAttribute("adminId"));

			if (admin.getEmail() != null && admin.getEmail() == oldAdminInfo.getEmail()) {
				model.addAttribute("error", "You have used this email before. Please choose a different one.");
				return Views.ADMIN_EDIT_PROFILE;
			}

			if (admin.getPassword() != null
					&& SecurityUtility.compareBcrypt(oldAdminInfo.getPassword(), admin.getPassword())) {
				model.addAttribute("error", "You have used this password before. Please choose a different one.");
				return Views.ADMIN_EDIT_PROFILE;
			}

			String result = rep.editProfile(admin);
			if ("success".equals(result)) {
				ra.addFlashAttribute("message", "Profile updated successfully.");
				return "redirect:/admin";
			} else {
				model.addAttribute("error", "Failed to update profile: " + result);
				return Views.ADMIN_EDIT_PROFILE;
			}
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			return Views.ADMIN_EDIT_PROFILE;
		} catch (Exception e) {
			model.addAttribute("error", "An error occurred: " + e.getMessage());
			return Views.ADMIN_EDIT_PROFILE;
		}
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
		return "redirect:/admin";
	}

	// -------------------- BLOGS --------------------//

	@GetMapping("/blogs/list")
	public String blog_list(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageCurrent(cp);
		pv.setPageSize(4);

		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);

		model.addAttribute("blogs", rep.getBlogs(pv));
		model.addAttribute("pv", pv);

		return Views.ADMIN_BLOGS_LIST;
	}

	@GetMapping("/blogs/create")
	public String create_blog_view(Model model) {
		return Views.ADMIN_BLOGS_CREATE;
	}

	@PostMapping("/blogs/createBlog")
	public String create_blog(@RequestParam String title, @RequestParam String content,
			@RequestParam(required = false) MultipartFile image, Model model) {
		try {
			Blog blog = new Blog();
			blog.setTitle(title);
			blog.setContent(content);

			if (image != null && !image.isEmpty()) {
				blog.setImage(FileUtility.uploadFileImage(image, "upload"));
			} else {
				blog.setImage(null);
			}

			String create = rep.newBlog(blog);
			if (create.equals("success")) {
				return "redirect:/admin/blogs/list";
			}
			model.addAttribute("catchError", "Failed to create blog, please try again.");
			return Views.ADMIN_BLOGS_CREATE;

		} catch (IllegalArgumentException e) {
			model.addAttribute("catchError", "Title may already exists.");
			return Views.ADMIN_BLOGS_CREATE;
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");
			return Views.ADMIN_BLOGS_CREATE;
		}
	}

	@GetMapping("/blogs/edit")
	public String edit_blog_view(@RequestParam int id, Model model) {
		try {
			Blog get = rep.getBlogById(id);
			if (get != null) {
				model.addAttribute("edit_item", get);
				return Views.ADMIN_BLOGS_EDIT;
			}
			return "redirect:/admin/blogs/list";
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");
			return Views.ADMIN_BLOGS_EDIT;
		}
	}

	@PostMapping("/blogs/editBlog")
	public String edit_blog(@ModelAttribute("edit_item") Blog blog, BindingResult br,
			@RequestParam(required = false) MultipartFile image, Model model) {
		try {
			if (blog.getTitle() == null && blog.getTitle().isEmpty()) {
				model.addAttribute("catchError", "You must enter title.");
				return Views.ADMIN_BLOGS_EDIT;
			}

			if (blog.getContent() == null && blog.getContent().isEmpty()) {
				model.addAttribute("catchError", "You must enter content.");
				return Views.ADMIN_BLOGS_EDIT;
			}

			if (image != null && !image.isEmpty()) {
				blog.setImage(FileUtility.uploadFileImage(image, "upload"));
			}

			String edit = rep.editBlog(blog);
			if (edit.equals("success")) {
				return "redirect:/admin/blogs/list";
			}

			if (br.hasErrors()) {
				model.addAttribute("catchError", "There was an error with your input.");
				return Views.ADMIN_BLOGS_EDIT;
			}
			model.addAttribute("catchError", "Failed to edit blog, please try again.");
			return Views.ADMIN_BLOGS_EDIT;

		} catch (IllegalArgumentException e) {
			model.addAttribute("catchError", "The title may already exists.");
			return Views.ADMIN_BLOGS_EDIT;
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");
			return Views.ADMIN_BLOGS_EDIT;
		}
	}

	@GetMapping("blogs/delete")
	public String delete_blog(int id, Model model) {
		try {
			String result = rep.deleteBlog(id);
			if (result.equals("succeed")) {
				return "redirect:/admin/blogs/list";
			}
			model.addAttribute("catchError", "Failed to delete blog, please try again.");

			return Views.ADMIN_BLOGS_LIST;
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("catchError", "An unexpected error occurred. Please try again later.");
			return Views.ADMIN_BLOGS_LIST;
		}
	}

	// -------------------- SERVICES --------------------//

	@GetMapping("/services/list")
	public String service_list(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageSize(4);
		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);

		List<Service> services = rep.getServices(pv);
		if (services != null) {
			model.addAttribute("services", services);
			model.addAttribute("pv", pv);
		}
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
			@RequestParam(required = false) MultipartFile image, Model model) {
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

	// -------------------- STAFFS -------------------- //

	@GetMapping("/staffs/list")
	public String staff_list(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageSize(4);
		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);

		model.addAttribute("staffs", rep.getStaffs(pv));
		model.addAttribute("pv", pv);

		return Views.ADMIN_STAFFS_LIST;
	}

	@GetMapping("/staffs/create")
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

	@PostMapping("/staffs/disabledAccount")
	public ResponseEntity<String> disabled_account(@RequestParam int id) {
		String result = rep.disableStaff(id);
		if (result.equals("success")) {
			return ResponseEntity.ok("success");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
	}

	// -------------------- ORDERS --------------------//

	@GetMapping("/orders/list")
	public String order_list(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageSize(4);
		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);

		model.addAttribute("pv", pv);
		model.addAttribute("orders", rep.getOrders(pv));
		model.addAttribute("staffs", rep.getStaffsForOrder(pv));

		return Views.ADMIN_ORDERS_LIST;
	}

	@GetMapping("/orders/assignStaff")
	public String order_list_to_assign(@RequestParam int id, Model model) {
		model.addAttribute("staffs", rep.staffListForAssign(id));
		model.addAttribute("Ord_id", id);
		model.addAttribute("detail", rep.getDetailById(id));
		model.addAttribute("formatedDate", rep.getDetailById(id).getStartDate().toLocalDate());
		return Views.ADMIN_ORDERS_ASSIGN_STAFF;
	}

	@PostMapping("/newSchedule")
	public ResponseEntity<String> assign_staff(@RequestBody Map<String, Object> payload) {
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
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
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
	public String edit_staff_in_order(@RequestParam int id, Model model) {
		model.addAttribute("staffs", rep.getCurrentStaff(id));
		model.addAttribute("detailId", id);
		return Views.ADMIN_ORDERS_REPLACE_STAFF;
	}

	@GetMapping("/orders/availableStaffs")
	public String available_staffs(@RequestParam int detailId, @RequestParam int staffId, Model model) {
		model.addAttribute("staffs", rep.getAvailableStaffToReplace(detailId, staffId));
		model.addAttribute("currentStaff", staffId);
		model.addAttribute("detailId", detailId);
		return Views.ADMIN_ORDERS_STAFF_FOR_REPLACE;
	}

	@GetMapping("/replaceStaff")
	public String replace_staff(@RequestParam int detailId, @RequestParam int currentStaff, @RequestParam int newStaff,
			Model model) {
		String result = rep.replaceStaff(currentStaff, newStaff, detailId);
		if (result.equals("success")) {
			return "redirect:/admin/orders/list";
		}
		model.addAttribute("error", "Replace failed due to some errors");
		return Views.ADMIN_ORDERS_STAFF_FOR_REPLACE;
	}

	// -------------------- SCHEDULE REQUESTS -------------------- //

	@PostMapping("/orders/request/approveDate")
	public ResponseEntity<String> approveDate(@RequestParam int scheduleId,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate newDate, @RequestParam int scrId) {
		try {
			Date newParseDate = Date.valueOf(newDate);

			String result = rep.approveDateRequest(scheduleId, newParseDate, scrId);
			if ("success".equals(result)) {
				return ResponseEntity.ok("Request approved successfully.");
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to implement action, please try again.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while processing the request");
		}
	}

	@PostMapping("/orders/request/denyRequest")
	public ResponseEntity<String> denyRequest(@RequestParam int scrId) {
		try {
			String result = rep.denyRequest(scrId);
			if (result.equals("success")) {
				return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/admin/orders/request")
						.body("");
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to implement action, please try again.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while processing the request.");
		}
	}

	@GetMapping("/orders/request")
	public String order_request(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageSize(4);
		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);

		model.addAttribute("pv", pv);
		model.addAttribute("requests", rep.getRequestList());

		return Views.ADMIN_ORDERS_REQUEST;
	}

	@GetMapping("/orders/request/replaceStaff")
	public String approve_cancel_request(@RequestParam int scrId, @RequestParam int staffId, @RequestParam int oldStaff,
			Model model) {
		String result = rep.approveCancelRequest(staffId, scrId, oldStaff);
		if (result.equals("success")) {
			return "redirect:/admin/orders/request";
		}
		model.addAttribute("error", "Fail to implement action, please try again later.");
		return Views.ADMIN_REQUESTS_STAFF_FOR_REPLACE;
	}

	@GetMapping("/orders/request/staffForReplace")
	public String replace_staff_view(@RequestParam int scrId, @RequestParam int oldStaff, Model model,
			@RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageSize(4);
		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);

		model.addAttribute("pv", pv);
		model.addAttribute("scrId", scrId);
		model.addAttribute("oldStaff", oldStaff);
		model.addAttribute("staffs", rep.staffListForAssignRequest(pv));
		return Views.ADMIN_REQUESTS_STAFF_FOR_REPLACE;
	}

	// -------------------- CLIENTS REQUESTS -------------------- //

	@GetMapping("/request/list")
	public String requestList(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageSize(4);
		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);

		model.addAttribute("pv", pv);
		model.addAttribute("requests", rep.getRequestDetails(pv));
		model.addAttribute("staffCheck", rep.countAvailableStaff());

		return Views.ADMIN_REQUEST_LIST;
	}

	@GetMapping("/request/staffsForAssign")
	public String staffList(@RequestParam int urdId, Model model,
			@RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageSize(4);
		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);

		model.addAttribute("urdId", urdId);
		model.addAttribute("pv", pv);
		model.addAttribute("staffs", rep.staffListForAssignRequest(pv));

		return Views.ADMIN_REQUEST_ASSIGN;
	}

	@PostMapping("/request/assign")
	public ResponseEntity<String> assignForRequest(@RequestParam int staffId, @RequestParam int urdId) {
		String result = rep.assignStaffIntoRequest(staffId, urdId);
		if (result.equals("success")) {
			return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/admin/request/list").body("");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to assign, please try again.");
	}

}
