package pack.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtpService {
	@Autowired
	EmailService mailService;
	
	private final Map<String, String> otpData = new HashMap<>();
	private final Map<String, Long> otpExpiryTime = new HashMap<>();

	public String generateOTP(String email) {
		Random random = new Random();
		String otp = String.format("%04d", random.nextInt(10000));

		otpData.put(email, otp);
		otpExpiryTime.put(email, System.currentTimeMillis() + 5 * 60 * 1000);
		
		mailService.SendMail(email, "Your Validate Code", "Your otp is " + otp);
		return otp;
	}

	public boolean validateOtp(String email, String otp) {
		

		if (!otpData.containsKey(email)) {
			return false;
		}

		String correctOtp = otpData.get(email);
		Long expiryTime = otpExpiryTime.get(email);

		return correctOtp.equals(otp) && System.currentTimeMillis() < expiryTime;
	}

	public boolean isOtpExpired(String email) {
		Long expiryTime = otpExpiryTime.get(email);
		return System.currentTimeMillis() > expiryTime;
	}
}
