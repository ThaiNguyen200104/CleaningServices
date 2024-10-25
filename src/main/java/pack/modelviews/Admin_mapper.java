package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.Admin;
import pack.utils.Views;

public class Admin_mapper implements RowMapper<Admin> {
  @Override
	public Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
		Admin item = new Admin();
		item.setId(rs.getInt(Views.COL_ADMIN_ID));
		item.setUsername(rs.getString(Views.COL_ADMIN_USERNAME));
		item.setPassword(rs.getString(Views.COL_ADMIN_PASSWORD));
		item.setEmail(rs.getString(Views.COL_ADMIN_EMAIL));
		item.setCreateDate(rs.getDate(Views.COL_ADMIN_CREATEDATE));
		return item;
	}
}
