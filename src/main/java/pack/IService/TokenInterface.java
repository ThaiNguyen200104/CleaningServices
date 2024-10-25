package pack.IService;

import java.time.LocalDateTime;

import pack.models.TokenRecord;

public interface TokenInterface {
	void saveToken(String token, LocalDateTime expirationTime);

	TokenRecord findToken(String token);

	void markTokenAsUsed(String token);
}
