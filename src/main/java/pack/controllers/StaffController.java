package pack.controllers;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import pack.models.OrderDetail;
import pack.models.Staff;
import pack.repositories.StaffRepository;
import pack.services.OtpService;
import pack.utils.FileUtility;
import pack.utils.SecurityUtility;
import pack.utils.Views;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/staff")
@Controller
public class StaffController {
	@Autowired
	StaffRepository rep;

	@Autowired
	OtpService otpService;

	// -------------------- INDEX --------------------//

	@GetMapping("")
	public String index(HttpServletRequest req, Model model) {
		Staff st = rep.findStaffById((int) req.getSession().getAttribute("staffId"));
		model.addAttribute("staff", st);

		return Views.STAFF_INDEX;
	}

	@GetMapping("/login")
	public String login() {
		return Views.STAFF_LOGIN;
	}

	@PostMapping("/checkLogin")
	public String chklogin(@RequestParam("acc") String acc, @RequestParam("pw") String password,
			HttpServletRequest request, Model model) {
		Staff staff = rep.getStaffByUsernameOrPhone(acc);
		if (staff == null) {
			model.addAttribute("loginError", "Account doesn't exists, please check again!");
			return Views.STAFF_LOGIN;
		}

		if (staff.getStatus().equals("disabled")) {
			model.addAttribute("loginError", "Your account has been disabled.");
			return Views.STAFF_LOGIN;
		}

		if (!SecurityUtility.compareBcrypt(staff.getPassword(), password)) {
			model.addAttribute("loginError", "Password incorrect!");
			return Views.STAFF_LOGIN;
		}

		request.getSession().setAttribute("staffId", staff.getId());
		return "redirect:/staff";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest req) {
		req.getSession().invalidate();
		return "redirect:/staff/login";
	}

	// -------------------- ACCOUNT --------------------//

	@GetMapping("/accounts")
	public String accounts(HttpServletRequest req, Model model) {
		Staff st = rep.findStaffById((int) req.getSession().getAttribute("staffId"));
		model.addAttribute("staff", st);

		return Views.STAFF_ACCOUNTS;
	}

	@GetMapping("/forgotPassword")
	public String forgot_password() {
		return Views.STAFF_FORGOT_PASSWORD;
	}

	@PostMapping("/getOtp")
	public String get_otp(String email, Model model, HttpServletRequest req) {
		try {
			if (!email.contains("@")) {
				model.addAttribute("pageError", "Invalid email.");
				return Views.STAFF_FORGOT_PASSWORD;
			}
			Staff staff = rep.checkEmailExists(email);
			if (staff == null) {
				model.addAttribute("pageError", "Email is not registered, yet.");
				return Views.STAFF_FORGOT_PASSWORD;
			}
			otpService.generateOTP(email);
			req.getSession().setAttribute("email", staff.getEmail());
			return "redirect:/staff/validateOtp";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@GetMapping("/validateOtp")
	public String validate_otp() {
		return Views.STAFF_VALIDATE;
	}

	@PostMapping("/verification")
	public String verify(@RequestParam String otp, HttpServletRequest req, Model model) {
		String email = req.getSession().getAttribute("email").toString();
		Staff staff = rep.checkEmailExists(email);
		if (!otpService.validateOtp(email, otp)) {
			model.addAttribute("error", "Invalid otp");
			return Views.STAFF_VALIDATE;
		} else if (otpService.isOtpExpired(email)) {
			model.addAttribute("error", "OTP is expired.");
			return Views.STAFF_VALIDATE;
		}

		req.getSession().setAttribute("staffId", staff.getId());
		req.getSession().setAttribute("username", staff.getUsername());
		return "redirect:/staff/accounts";
	}

	// REQUEST
	@GetMapping("/request/list")
	public String getRequestlist(HttpServletRequest request, Model model) {
		model.addAttribute("requests", rep.getStaffAssginedRequest((int) request.getSession().getAttribute("staffId")));
		return Views.STAFF_REQUEST_LIST;
	}

	@PostMapping("/request/edit")
	public ResponseEntity<String> editRequest(@RequestParam int urdId, @RequestParam double price,
			@RequestParam Date startDate) {
		try {
			String result = rep.editRequest(price, startDate, urdId);
			if ("success".equals(result)) {
				return ResponseEntity.ok("Succeed.");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to edit request.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed.");
		}
	}

	// ORDER
	@GetMapping("/order/list")
	public String getOrderOfStaff(Model model, HttpServletRequest request) {
		int staffId = (int) request.getSession().getAttribute("staffId");
		List<Map<String, Object>> orders = rep.getAssignedOrders(staffId);

		for (Map<String, Object> order : orders) {
			int scheId = (int) order.get("scheId");

			boolean hasAdjustDateRequest = rep.hasScheduleRequestByType(scheId, "adjustDate");
			boolean hasCancelRequest = rep.hasScheduleRequestByType(scheId, "cancel");

			order.put("showReschedule", !hasAdjustDateRequest);
			order.put("showCancel", !hasCancelRequest);
		}

		model.addAttribute("orders", orders);
		return Views.STAFF_ORDER_LIST;
	}

	@PostMapping("/order/reschedule")
	public ResponseEntity<String> newDate(@RequestParam int scheId, @RequestParam Date date,
			@RequestParam String reason) {
		try {
			String result = rep.AdjustDate(scheId, date, reason);

			Date currentDate = new Date(System.currentTimeMillis());
			if (date.before(currentDate)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date cannot be in the past.");
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			cal.add(Calendar.YEAR, 2);
			java.util.Date dateLimit = cal.getTime();

			if (date.after(dateLimit)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Start date cannot be more than 2 years from now.");
			}

			if (result.equals("success")) {
				return ResponseEntity.ok("Succeed");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to implement action.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed.");
		}
	}

	@PostMapping("/order/cancel")
	public ResponseEntity<String> cancelSchedule(@RequestParam int scheId, @RequestParam String reason) {
		try {
			String result = rep.cancelOrder(scheId, reason);
			if (result.equals("success")) {
				return ResponseEntity.ok("Succeed");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to implement action.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed.");
		}
	}

	@PostMapping("/order/completeOrder")
	public ResponseEntity<String> completeOrder(@RequestParam int detailId, @RequestParam MultipartFile before,
			@RequestParam MultipartFile after) {
		try {
			String beforeImg = FileUtility.uploadFileImage(before, "upload");
			String afterImg = FileUtility.uploadFileImage(after, "upload");
			String result = rep.completeOrder(detailId, beforeImg, afterImg);
			if (result.equals("success")) {
				return ResponseEntity.ok("Succeed");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to implement action.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed.");
		}
	}

}
