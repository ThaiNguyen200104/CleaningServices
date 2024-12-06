package pack.controllers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;

@RestController
@RequestMapping("/ckbox")
public class CKBoxController {
	@Value("${environment_id}")
	private String environmentId;

	@Value("${access_key}")
	private String accessKey;

	@GetMapping("/auth")
	public String get_auth_token() throws Exception {
		Map<String, Object> authClaim = new HashMap<>() {
			{
				put("ckbox", new HashMap<>() {
					{
						put("role", "user");
					}
				});
			}
		};

		Algorithm algorithm = Algorithm.HMAC256(this.accessKey.getBytes("ASCII"));

		String token = JWT.create().withAudience(this.environmentId).withIssuedAt(Instant.now()).withSubject("userId")
				.withClaim("auth", authClaim).sign(algorithm);

		return token;
	}

}
