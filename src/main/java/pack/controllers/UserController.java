package pack.controllers;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHeaders;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import pack.models.PageView;
import pack.models.User;
import pack.repositories.UserRepository;
import pack.services.OtpService;
import pack.utils.FileUtility;
import pack.utils.SecurityUtility;
import pack.utils.Views;

@RequestMapping("/user")
@Controller
public class UserController {

	@Autowired
	UserRepository rep;

	@Autowired
	OtpService otpService;

	// -------------------- INDEX -------------------- //

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("new_item", new User());
		return Views.USER_SIGNUP;
	}

	@PostMapping("/newUser")
	public String create_user(@ModelAttribute("new_item") User user, HttpServletRequest req, Model model) {
		if (!user.getPhone().matches(Views.PHONE_REGEX)) {
			model.addAttribute("error",
					"Your phone number must only have digits and be at least 10 to 11 digits long.");
			return Views.USER_SIGNUP;
		}
		if (!user.getConfirmPassword().equals(user.getPassword())) {
			model.addAttribute("error", "Password and Confirm Password are not match.");
			return Views.USER_SIGNUP;
		}
		try {
			String result = rep.newUser(user);
			if (result.equals("success")) {
				return "redirect:/user/login";
			}
			model.addAttribute("error", "Failed to create user, please try again.");
			return Views.USER_SIGNUP;
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", "Some information(username, email, phone) may already exists.");
			return Views.USER_SIGNUP;
		} catch (Exception e) {
			System.out.println("System error: " + e.getMessage());
			model.addAttribute("error", "An unexpected error occurred. Please try again later.");
			return Views.USER_SIGNUP;
		}
	}

	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("currentPage", "login");

		return Views.USER_LOGIN;
	}

	@PostMapping("/checkLogin")
	public String check_login(@RequestParam("usrname") String username, @RequestParam("pw") String password,
			HttpServletRequest req, Model model) {
		User user = rep.findUserByUsername(username);
		if (user == null) {
			model.addAttribute("loginError", "Username does not exist.");
			return Views.USER_LOGIN;
		}

		if (!SecurityUtility.compareBcrypt(user.getPassword(), password)) {
			model.addAttribute("loginError", "Incorrect password.");
			return Views.USER_LOGIN;
		}
		req.getSession().setAttribute("usrId", user.getId());
		req.getSession().setAttribute("username", user.getUsername());
		return "redirect:/";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest req) {
		req.getSession().invalidate();
		return "redirect:/user/login";
	}

	// -------------------- ACCOUNTS -------------------- //

//	@GetMapping("/accounts")
//	public String accounts(HttpServletRequest req, Model model) {
//		model.addAttribute("user", rep.findUserByUsername(req.getSession().getAttribute("username").toString()));
//		model.addAttribute("orderDetails", rep.getOrderDetailsForAccount((int) req.getSession().getAttribute("usrId")));
//		model.addAttribute("currentPage", "accounts");
//
//		return Views.USER_ACCOUNTS;
//	}

	// Thái
	@GetMapping("/accounts")
	public String accounts(HttpServletRequest req, Model model) {
		model.addAttribute("user", rep.findUserByUsername(req.getSession().getAttribute("username").toString()));
		model.addAttribute("orders", rep.getOrdersHistory((int) req.getSession().getAttribute("usrId")));
		model.addAttribute("browseMore", rep.countOrdersToBrowseMore((int) req.getSession().getAttribute("usrId")));

		model.addAttribute("currentPage", "accounts");

		return Views.USER_ACCOUNTS;
	}

	@GetMapping("/seeMore")
	public String seeMore(@RequestParam("id") int orderId, Model model) {
		List<Map<String, Object>> details = rep.getSeeMoreOrderDetails(orderId);
		model.addAttribute("seeMore", details);

		if (details.isEmpty()) {
			model.addAttribute("error", "Something went wrong. Please try again later.");
			return "redirect:/user/accounts";
		}
		return Views.USER_SEE_MORE;
	}

	@GetMapping("/browseMore")
	public String browse_more(HttpServletRequest req, Model model,
			@RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageCurrent(cp);
		pv.setPageSize(20);

		model.addAttribute("pv", pv);
		model.addAttribute("browseMore", rep.getAllOrdersHistory(pv, (int) req.getSession().getAttribute("usrId")));

		return Views.USER_BROWSE_MORE;
	}

	@GetMapping("/editProfile")
	public String edit_profile(Model model, HttpServletRequest req) {
		User user = rep.findUserById((int) req.getSession().getAttribute("usrId"));
		model.addAttribute("user", user);
		return Views.USER_EDIT_PROFILE;
	}

	@PostMapping("/updateProflie")
	public String update_profile(@RequestParam(required = false) MultipartFile image, @ModelAttribute User user,
			Model model, RedirectAttributes ra, HttpServletRequest req) {
		try {
			User oldUserInfo = rep.findUserById((int) req.getSession().getAttribute("usrId"));
			if (user.getPassword() != null && !user.getPassword().equals(user.getConfirmPassword())) {
				model.addAttribute("error", "Password and Confirm Password are not match.");
				return Views.USER_EDIT_PROFILE;
			}

			if (user.getPassword() != null
					&& SecurityUtility.compareBcrypt(oldUserInfo.getPassword(), user.getPassword())) {
				model.addAttribute("error", "You have used this password before. Please choose a different one.");
				return Views.USER_EDIT_PROFILE;
			}

			if (image != null && !image.isEmpty()) {
				user.setImage(FileUtility.uploadFileImage(image, "upload"));
			}

			String result = rep.editProfile(user);
			if ("success".equals(result)) {
				ra.addFlashAttribute("message", "Profile updated successfully.");
				return "redirect:/user/accounts";
			} else {
				model.addAttribute("error", "Failed to update profile: " + result);
				return Views.USER_EDIT_PROFILE;
			}
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			return Views.USER_EDIT_PROFILE;
		} catch (Exception e) {
			model.addAttribute("error", "An error occurred: " + e.getMessage());
			return Views.USER_EDIT_PROFILE;
		}
	}

	@GetMapping("/forgotPassword")
	public String forgot_password() {
		return Views.USER_FORGOT_PASSWORD;
	}

	@PostMapping("/getOtp")
	public String get_otp(String email, Model model, HttpServletRequest req) {
		try {
			if (!email.contains("@")) {
				model.addAttribute("pageError", "Invalid email.");
				return Views.USER_FORGOT_PASSWORD;
			}
			User user = rep.checkEmailExists(email);
			if (user == null) {
				model.addAttribute("pageError", "Email is not registered, yet.");
				return Views.USER_FORGOT_PASSWORD;
			}
			otpService.generateOTP(email);
			req.getSession().setAttribute("email", user.getEmail());
			return "redirect:/user/validateOtp";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@GetMapping("/validateOtp")
	public String validate_otp() {
		return Views.USER_VALIDATE;
	}

	@PostMapping("/verification")
	public String verify(@RequestParam String otp, HttpServletRequest req, Model model) {
		String email = req.getSession().getAttribute("email").toString();
		User user = rep.checkEmailExists(email);
		if (!otpService.validateOtp(email, otp)) {
			model.addAttribute("error", "Invalid otp");
			return Views.USER_VALIDATE;
		} else if (otpService.isOtpExpired(email)) {
			model.addAttribute("error", "OTP is expired.");
			return Views.USER_VALIDATE;
		}

		req.getSession().setAttribute("usrId", user.getId());
		req.getSession().setAttribute("username", user.getUsername());
		return "redirect:/user/accounts";
	}

	// -------------------- SERVICES -------------------- //

	@GetMapping("/orders")
	public String orders(Model model, HttpServletRequest request) {
		model.addAttribute("orderDetails", rep.getOrderDetails((int) request.getSession().getAttribute("usrId")));
		model.addAttribute("currentPage", "orders");

		return Views.USER_ORDERS;
	}

	@PostMapping("/confirmOrder")
	public ResponseEntity<String> confirmOrder(@RequestParam int urdId, @RequestParam int serId,
			@RequestParam Date startDate, @RequestParam double price, @RequestParam int staffId) {
		String result = rep.confirmOrder(urdId);
		if (result.equals("success")) {
			String confirm = rep.newOrder(urdId, serId, startDate, price);
			if (confirm.equals("success")) {
				rep.updateStaffStatusToAvailable(staffId);
				return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/user/orders").body("");
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to confirm the order. Please try again.");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to confirm the order. Please try again.");
	}

	// Thái
//	@GetMapping("/confirmOrder")
//	public String confirmOrder(@RequestParam int detailId, Model model) {
//		String confirm = rep.confirmOrder(detailId);
//		if (confirm.equals("success")) {
//			return "redirect:/user/orders";
//		}
//		model.addAttribute("error", "Confirm failed due to some errors");
//		return Views.USER_ORDERS;
//	}

	@GetMapping("/cancelOrder")
	public ResponseEntity<String> cancel_order(@RequestParam int requestId,
			@RequestParam(required = false) Integer staffId, Model model) {
		String result = rep.cancelOrder(requestId);
		if (result.equals("success")) {
			if (staffId != null && staffId != 0) {
				String result1 = rep.updateStaffStatusToAvailable(staffId);
				if (result1.equals("success")) {
					return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/user/orders")
							.body("");
				}
				return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/user/orders").body("");
			}
			return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/user/orders").body("");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to cancel the order. Please try again.");
	}
	
	// Thái
//	@GetMapping("/cancelOrder")
//	public String cancel_order(@RequestParam int id, Model model) {
//		String cancel = rep.cancelOrder(id);
//		if (cancel.equals("success")) {
//			return "redirect:/user/orders";
//		}
//		model.addAttribute("error", "Cancel failed. Please try again.");
//		return Views.USER_ORDERS;
//	}

//	@GetMapping("/orderDetails")
//	public String order_details(Model model) {
//		List<OrderDetail> detail = rep.getOrderDetails();
//		model.addAttribute("details", detail);
//
//		return Views.USER_ORDER_DETAILS;
//	}

	// Service
	@PostMapping("/bookService")
	public ResponseEntity<String> createOrder(@RequestParam int userId, @RequestParam int serviceId,
			@RequestParam Date startDate, @RequestParam double price) {
		try {
			Date currentDate = new Date(System.currentTimeMillis());
			if (startDate.before(currentDate)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date cannot be in the past.");
			}

			if (rep.isServiceInRequest(userId, serviceId)) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Service is already booked.");
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			cal.add(Calendar.YEAR, 5);
			java.util.Date dateLimit = cal.getTime();
			if (startDate.after(dateLimit)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Start date cannot be more than 5 years from now.");
			}

			String result = rep.newRequestDetail(userId, serviceId, startDate, price);
			if (result.equals("success")) {
				return ResponseEntity.ok("Order created successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order.");
		}
	}
}