package pack.controllers;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

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
import pack.models.Order;
import pack.models.OrderDetail;
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

	@GetMapping("/accounts")
	public String accounts(HttpServletRequest req, Model model) {
		User user = rep.findUserByUsername(req.getSession().getAttribute("username").toString());
		int usrId = (int) req.getSession().getAttribute("usrId");
		List<Order> list = rep.getOrders(usrId);

		model.addAttribute("user", user);
		model.addAttribute("orders", list);
		model.addAttribute("currentPage", "accounts");

		return Views.USER_ACCOUNTS;
	}

	@GetMapping("/editProfile")
	public String edit_profile(Model model, HttpServletRequest req) {
		User user = rep.findUserById((int) req.getSession().getAttribute("usrId"));
		model.addAttribute("user", user);
		return Views.USER_EDIT_PROFILE;
	}

	@PostMapping("/updateProflie")
	public String update_profile(@RequestParam(required = false) MultipartFile image, @ModelAttribute User user,
			Model model, RedirectAttributes ra, HttpServletRequest request) {
		try {
			User oldUserInfo = rep.findUserById((int) request.getSession().getAttribute("usrId"));

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
	public String orders(HttpServletRequest req, Model model) {
		int usrId = (int) req.getSession().getAttribute("usrId");
		model.addAttribute("orders", rep.getOrders(usrId));

		return Views.USER_ORDERS;
	}

	@PostMapping("/user/confirmOrder")
	public String confirm_order(int id, HttpServletRequest req, Model model) {
		try {
			OrderDetail get = rep.getDetailById(id);
			if (get != null) {
				rep.confirmOrder(id);
				return "redirect:/";
			}

			model.addAttribute("error", "Failed to confirm order, please try again.");
			return "redirect:/user/orders";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@PostMapping("/user/cancelOrder")
	public String cancel_order(int id, HttpServletRequest req, Model model) {
		try {
			OrderDetail get = rep.getDetailById(id);
			if (get != null) {
				rep.cancelOrder(id);
				return "redirect:/user/accounts";
			}
			model.addAttribute("error", "Failed to confirm order, please try again.");
			return "redirect:/user/orders";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@GetMapping("/orderDetails")
	public String order_details(Model model) {
		List<OrderDetail> detail = rep.getDetails();
		model.addAttribute("details", detail);

		return Views.USER_ORDER_DETAILS;
	}

	@PostMapping("/bookService")
	public ResponseEntity<String> createOrder(@RequestParam int userId, @RequestParam int serviceId,
			@RequestParam Date startDate) {

		Order order = new Order();
		order.setUsrId(userId);
		Date currentDate = new Date(System.currentTimeMillis());
		if (startDate.before(currentDate)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date cannot be in the past.");
		}

		if (rep.isServiceInOrder(userId, serviceId)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Service is already booked.");
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.YEAR, 5);
		Date dateLimit = (Date) cal.getTime();

		if (startDate.after(dateLimit)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Start date cannot be more than 10 years from now.");
		}
		String result = rep.newOrder(order, serviceId, startDate);

		if ("success".equals(result)) {
			return ResponseEntity.ok("Order created successfully.");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order.");
		}
	}
}
