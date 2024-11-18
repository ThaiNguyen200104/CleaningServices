package pack.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import pack.models.Staff;
import pack.repositories.StaffRepository;
import pack.services.OtpService;
import pack.utils.SecurityUtility;
import pack.utils.Views;

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

	// ORDERS

	@GetMapping("/orders/list")
	public String getMethodName(Model model) {
		model.addAttribute("orders", rep.pendingOrderList());
		return Views.STAFF_ORDER_LIST;
	}

}
