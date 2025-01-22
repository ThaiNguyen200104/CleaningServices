package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.User;
import pack.utils.Views;

public class User_mapper implements RowMapper<User> {
	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User item = new User();
		item.setId(rs.getInt(Views.COL_USER_ID));
		item.setFullname(rs.getString(Views.COL_USER_FULLNAME));
		item.setUsername(rs.getString(Views.COL_USER_USERNAME));
		item.setPassword(rs.getString(Views.COL_USER_PASSWORD));
		item.setEmail(rs.getString(Views.COL_USER_EMAIL));
		item.setPhone(rs.getString(Views.COL_USER_PHONE));
		item.setAddress(rs.getString(Views.COL_USER_ADDRESS));
		item.setImage(rs.getString(Views.COL_USER_IMAGES));
		item.setCreateDate(rs.getDate(Views.COL_USER_CREATEDATE));
		item.setFullname(rs.getString(Views.COL_USER_FULLNAME));
		item.setVerified(rs.getBoolean("is_verified"));
		return item;
	}
}
