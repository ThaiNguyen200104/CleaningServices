package pack.IService;

import java.time.LocalDateTime;

import pack.models.TokenRecord;

public interface TokenInterface {
	void saveToken(String token, LocalDateTime expirationTime);

	void saveUserToken(String token, LocalDateTime expirationTime, String email);
	
	TokenRecord findUserToken(String token);
	
	TokenRecord findToken(String token);

	void markTokenAsUsed(String token);
	
	
}
