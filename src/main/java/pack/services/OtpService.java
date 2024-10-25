package pack.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtpService {
	@Autowired
	private TwilioService twilioService;

	private final Map<String, String> otpData = new HashMap<>();
	private final Map<String, Long> otpExpiryTime = new HashMap<>();

	public String generateOTP(String phoneNumber) {
		Random random = new Random();
		String otp = String.format("%04d", random.nextInt(10000));

		String e164PhoneNumber = formatPhoneNumberToE164(phoneNumber);

		otpData.put(e164PhoneNumber, otp);
		otpExpiryTime.put(e164PhoneNumber, System.currentTimeMillis() + 5 * 60 * 1000);

		twilioService.sendSms(e164PhoneNumber, "Your otp code is: " + otp);
		return otp;
	}

	public boolean validateOtp(String phoneNumber, String otp) {
		String e164PhoneNumber = formatPhoneNumberToE164(phoneNumber);

		if (!otpData.containsKey(e164PhoneNumber)) {
			return false;
		}

		String correctOtp = otpData.get(e164PhoneNumber);
		Long expiryTime = otpExpiryTime.get(e164PhoneNumber);

		return correctOtp.equals(otp) && System.currentTimeMillis() < expiryTime;
	}

	public boolean isOtpExpired(String phoneNumber) {
		String e164PhoneNumber = formatPhoneNumberToE164(phoneNumber);
		Long expiryTime = otpExpiryTime.get(e164PhoneNumber);
		return System.currentTimeMillis() > expiryTime;
	}

	private String formatPhoneNumberToE164(String phoneNumber) {
		if (phoneNumber.startsWith("0")) {
			return "+84" + phoneNumber.substring(1);
		}
		return phoneNumber;
	}
}
