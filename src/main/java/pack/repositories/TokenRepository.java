package pack.repositories;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.IService.TokenInterface;
import pack.models.TokenRecord;

@Repository
public class TokenRepository implements TokenInterface {
	@Autowired
	JdbcTemplate db;

	@Override
	public void saveToken(String token, LocalDateTime expirationTime) {
		String sql = "INSERT INTO tokens (token_id, expiration_time, used) VALUES (?, ?, 0)";
		db.update(sql, token, expirationTime);
	}

	@Override
	public TokenRecord findToken(String token) {
		String sql = "SELECT token_id, expiration_time, used FROM tokens WHERE token_id = ?";
		return db.queryForObject(sql,
				(rs, rowNum) -> new TokenRecord(rs.getString("token_id"),
						rs.getTimestamp("expiration_time").toLocalDateTime(), rs.getBoolean("used")),
				new Object[] { token });
	}

	@Override
	public void markTokenAsUsed(String token) {
		String sql = "UPDATE tokens SET used = 1 WHERE token_id = ?";
		db.update(sql, token);
	}
}
