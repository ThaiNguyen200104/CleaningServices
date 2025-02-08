package pack.controllers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import pack.IService.TokenInterface;
import pack.models.OrderDetail;
import pack.models.PageView;
import pack.models.User;
import pack.repositories.PaymentRepository;
import pack.repositories.UserRepository;
import pack.services.EmailService;
import pack.services.OtpService;
import pack.services.VNPayService;
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

	@Autowired
	TokenInterface tokenService;

	@Autowired
	EmailService emailService;

	@Autowired
	private VNPayService vnPayService;

	@Autowired
	PaymentRepository paymentRepository;

	// -------------------- INDEX -------------------- //

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("currentPage", "login");
		model.addAttribute("new_item", new User());
		return Views.USER_SIGNUP;
	}

	@PostMapping("/newUser")
	@ResponseBody
	public Map<String, String> create_user(@ModelAttribute("new_item") User user, HttpServletRequest req) {
		Map<String, String> response = new HashMap<>();
		try {
			String result = rep.saveUser(user);
			if (!"success".equals(result)) {
				response.put("status", "failed");
				response.put("message", "User creation failed.");
				return response;
			}
			response.put("status", "success");
			response.put("message", "Please check your email to verify your account.");
		} catch (IllegalArgumentException e) {
			response.put("status", "failed");
			response.put("message", e.getMessage());
		} catch (Exception e) {
			response.put("status", "failed");
			response.put("message", "An unexpected error occurred.");
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping("/verify")
	public String verifyUser(@RequestParam String token, RedirectAttributes redirectAttributes) {
		boolean verified = rep.verifyUser(token);
		if (verified) {
			redirectAttributes.addFlashAttribute("message",
					"Your account has been verified successfully. You can now login.");
			return "redirect:/user/login";
		} else {
			redirectAttributes.addFlashAttribute("message",
					"Invalid or expired verification link. Please try again or contact support.");
		}
		return "redirect:/user/login";
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
		if (user == null || !user.isVerified()) {
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
		model.addAttribute("user", rep.findUserByUsername(req.getSession().getAttribute("username").toString()));
		List<OrderDetail> orderDetail = rep.getOrdersForAccount((int) req.getSession().getAttribute("usrId"));
		model.addAttribute("orderDetails", orderDetail);
		model.addAttribute("browseMore", orderDetail.size() > 4);
		model.addAttribute("currentPage", "accounts");

		return Views.USER_ACCOUNTS;
	}

	@GetMapping("/accounts/seeMore")
	public String see_more(@RequestParam int id, Model model) {
		model.addAttribute("seeMore", rep.getOrderDetailsForAccount(id));
		model.addAttribute("currentPage", "accounts");

		return Views.USER_SEE_MORE;
	}

	@GetMapping("/accounts/browseMore")
	public String browse_more(HttpServletRequest req, Model model,
			@RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageSize(5);
		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);
		model.addAttribute("pv", pv);

		String usrId = req.getSession().getAttribute("usrId").toString();
		List<Map<String, Object>> browseMore = rep.getAllOrderDetailsForAccount(pv, usrId);

		model.addAttribute("browseMore", browseMore);
		return Views.USER_BROWSE_MORE;
	}

	@GetMapping("/editProfile")
	public String edit_profile(Model model, HttpServletRequest req) {
		User user = rep.findUserById((int) req.getSession().getAttribute("usrId"));
		model.addAttribute("user", user);
		return Views.USER_EDIT_PROFILE;
	}

	@PostMapping("/updateProfile")
	public String update_profile(@RequestParam(required = false) MultipartFile file, @ModelAttribute User user,
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

			if (file != null && !file.isEmpty()) {
				user.setImage(FileUtility.uploadFileImage(file, "upload"));
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

		req.getSession().setAttribute("username", user.getUsername());
		return "redirect:/user/changePassword";
	}

	@GetMapping("/changePassword")
	public String changePassPage() {
		return Views.USER_CHANGE_PASSWORD;
	}

	@PostMapping("/changePassAction")
	public String changePassAction(@RequestParam String password, @RequestParam String confirmPassword,
			HttpServletRequest req, Model model) {
		String email = req.getSession().getAttribute("email").toString();
		User user = rep.checkEmailExists(email);

		if (!password.equals(confirmPassword)) {
			model.addAttribute("error", "Confirm Password does not match the password");
			return Views.USER_CHANGE_PASSWORD;
		}

		if (SecurityUtility.compareBcrypt(user.getPassword(), password)) {
			model.addAttribute("error", "Your new password matches your old password");
			return Views.USER_CHANGE_PASSWORD;
		}

		String username = req.getSession().getAttribute("username").toString();
		String result = rep.changePass(username, password);
		if (result.equals("success")) {
			return "redirect:/user/login";
		}

		model.addAttribute("error", "Fail to implement action, please try again later.");

		return Views.USER_CHANGE_PASSWORD;
	}

	// -------------------- SERVICES -------------------- //

	@GetMapping("/orders")
	public String orders(Model model, HttpServletRequest req,
			@RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		PageView pv = new PageView();
		pv.setPageSize(4);
		if (cp < 1) {
			cp = 1;
		}
		pv.setPageCurrent(cp);
		model.addAttribute("pv", pv);

		Integer usrId = (int) req.getSession().getAttribute("usrId");
		List<Map<String, Object>> orders = rep.getUserRequestDetailById(pv, usrId);

		boolean statusCheck = false;
		if (orders != null && !orders.isEmpty()) {
			statusCheck = orders.stream()
					.anyMatch(order -> !order.get("status").toString().equalsIgnoreCase("confirmed"));
		}
		model.addAttribute("statusCheck", statusCheck);
		model.addAttribute("orders", orders);
		model.addAttribute("currentPage", "orders");

		return Views.USER_ORDERS;
	}

	@GetMapping("/orders/orderDetails")
	public String order_details(Model model, @RequestParam("id") int usrReqId) {
		List<Map<String, Object>> details = rep.getOrderDetails(usrReqId);
		model.addAttribute("orderDetails", details);
		model.addAttribute("currentPage", "orders");

		return Views.USER_ORDER_DETAILS;
	}

	@PostMapping("/confirmOrder")
	public ResponseEntity<String> confirmOrder(@RequestParam int urdId, @RequestParam int serId,
			@RequestParam String startDate, @RequestParam double price, @RequestParam int staffId) {
		String result = rep.confirmOrder(urdId);

		LocalDate localDate = LocalDate.parse(startDate);
		Date sqlDate = Date.valueOf(localDate);
		if (result.equals("success")) {
			String confirm = rep.newOrder(urdId, serId, sqlDate, price);
			if (confirm.equals("success")) {
				rep.updateStaffStatusToAvailable(staffId);
				return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/user/orders").body("");
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to confirm the order. Please try again.");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to confirm the order. Please try again.");
	}

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

	@GetMapping("/orderDecline")
	public String orderDecline(@RequestParam int id) {
		String result = rep.orderDecline(id);
		if (result.equals("success")) {
			return "redirect:/user/orders/orderDetails?id=" + id;
		}
		return Views.USER_ORDERS;
	}

	@GetMapping("/changeService")
	public String changeServicePage(Model model, HttpServletRequest req, @RequestParam int serId) {
		model.addAttribute("Services", rep.serviceListForChange((int) req.getSession().getAttribute("usrId")));
		model.addAttribute("oldServiceId", serId);
		return Views.USER_CHANGE_SERVICE;
	}

	@GetMapping("/ServiceChange")
	public String changeServiceAction(@RequestParam int oldSerId, @RequestParam int newSerId, Model model,
			HttpServletRequest req) {
		String result = rep.changeService(oldSerId, newSerId, (int) req.getSession().getAttribute("usrId"));
		if (result.equals("success")) {
			return "redirect:/user/orders";
		}
		model.addAttribute("error", "Fail to implement action, please try again later.");
		return Views.USER_CHANGE_SERVICE;
	}

// PAYMENT SECTION
	@GetMapping("/banking")
	public String processBankingPayment(@RequestParam("id") int orderDetailId) {
		try {
			Map<String, Object> orderDetail = paymentRepository.getOrderDetailWithUser(orderDetailId);

			double price = Double.parseDouble(orderDetail.get("price").toString());
			int amount = (int) price;
			int userId = (int) orderDetail.get("user_id");
			int orderId = (int) orderDetail.get("order_id");

			vnPayService.processInitialPayment(orderDetailId, userId, amount);

			String paymentUrl = vnPayService.createPaymentUrl(orderId, amount);

			return "redirect:" + paymentUrl;
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/user/error";
		}
	}

	@GetMapping("/payment-callback")
	public String paymentCallback(HttpServletRequest request) {
		try {
			if (!vnPayService.validatePaymentResponse(request)) {
				System.out.println("Payment validation failed");
				return "redirect:/user/payment-failed?error=validation-failed";
			}

			String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
			String vnp_TxnRef = request.getParameter("vnp_TxnRef");

			vnPayService.processVNPayCallback(request);

			if ("00".equals(vnp_ResponseCode)) {
				int orderId = Integer.parseInt(vnp_TxnRef);
				Map<String, Object> orderDetail = paymentRepository.getOrderDetailByOrderId(orderId);

				if (orderDetail == null) {
					return "redirect:/user/error";
				}

				int orderDetailId = (int) orderDetail.get("id");
				return "redirect:/user/orders/orderDetails?id=" + orderDetailId;
			} else {
				return "redirect:/user/error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/user/error";
		}
	}

	@GetMapping("/payment-history")
	public String paymentHis(@RequestParam int userId, Model model) {
		model.addAttribute("payments", paymentRepository.getPaymentHistory(userId));
		return Views.USER_PAYMENTS;
	}
	
}