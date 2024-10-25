package pack.models;

import java.time.LocalDateTime;

public class TokenRecord {
	private String token;
	private LocalDateTime expirationTime;
	private boolean used;

	public TokenRecord(String token, LocalDateTime expirationTime, boolean used) {
		super();
		this.token = token;
		this.expirationTime = expirationTime;
		this.used = used;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(LocalDateTime expirationTime) {
		this.expirationTime = expirationTime;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

}
