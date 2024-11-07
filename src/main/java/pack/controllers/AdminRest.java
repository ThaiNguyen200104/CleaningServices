package pack.controllers;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pack.models.TokenRecord;
import pack.repositories.AdminRepository;
import pack.repositories.TokenRepository;
import pack.services.EmailService;

@RestController
@RequestMapping("/api")
public class AdminRest {
	@Autowired
	AdminRepository adminRepository;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	EmailService emailService;

	@Value("${app.admin.api-key}")
	private String apiKey;

	@CrossOrigin
	@GetMapping("/generateAdmin")
	// http://localhost:8080/api/generateAdmin?email=hieuminh091304@gmail.com&apikey=7ed9b7ef600ce7544841cd061cf27b2493b7da5c78644ebe7920ef02c76939d9
	public String generateAdmin(@RequestParam String email, @RequestParam String apikey, RedirectAttributes ra) {
		if (apiKey.equals(apikey)) {
			// Create token & expired time (3 hours)
			String token = UUID.randomUUID().toString();
			LocalDateTime expirationTime = LocalDateTime.now().plusHours(3);

			// Insert token into database
			tokenRepository.saveToken(token, expirationTime);

			// Send email with the link that contains token
			String emailBody = "Username: admin\nPassword: 123\n\nClick here to create your admin account: http://localhost:8080/api/createAdmin?token="
					+ token;
			emailService.SendMail(email, "Admin Account Creation", emailBody);

			return "Token and email sent!";
		} else {
			return "Invalid API key.";
		}
	}

	@CrossOrigin
	@GetMapping("/createAdmin")
	public String createAdmin(@RequestParam("token") String token, RedirectAttributes ra) {
		try {
			TokenRecord tokenRecord = tokenRepository.findToken(token);

			if (tokenRecord == null || tokenRecord.isUsed()) {
				ra.addFlashAttribute("message", "Token is invalid or already used.");
				return "Token is invalid or already used.";
			}

			if (tokenRecord.getExpirationTime().isBefore(LocalDateTime.now())) {
				ra.addFlashAttribute("message", "Token has expired.");
				return "Token has expired.";
			}

			// Create admin account
			String result = adminRepository.newAdmin("admin", "123");

			// Mark used token
			tokenRepository.markTokenAsUsed(token);

			if ("success".equals(result)) {
				return "Enter admin login page: http://www.localhost:8080/admin/login";
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return "failed to create account";
	}
}
