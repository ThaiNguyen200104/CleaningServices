package pack.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class TwilioService {
	private final String accountSid;
	private final String authToken;
	private final String fromPhoneNumber;

	@Autowired
	public TwilioService(@Value("${twilio.account_sid}") String accountSid,
			@Value("${twilio.auth_token}") String authToken, @Value("${twilio.phone_number}") String fromPhoneNumber) {
		this.accountSid = accountSid;
		this.authToken = authToken;
		this.fromPhoneNumber = fromPhoneNumber;
		Twilio.init(accountSid, authToken);
	}

	public void sendSms(String toPhoneNumber, String messageContent) {
		Message message = Message
				.creator(new PhoneNumber(toPhoneNumber), new PhoneNumber(fromPhoneNumber), messageContent).create();

		System.out.println("Sent message SID: " + message.getSid());
	}
}
