package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.Staff;
import pack.utils.Views;

public class Staff_mapper implements RowMapper<Staff> {
	@Override
	public Staff mapRow(ResultSet rs, int rowNum) throws SQLException {
		Staff item = new Staff();
		item.setId(rs.getInt(Views.COL_STAFFS_ID));
		item.setFullname(rs.getString(Views.COL_STAFFS_FULLNAME));
		item.setUsername(rs.getString(Views.COL_STAFFS_USERNAME));
		item.setPassword(rs.getString(Views.COL_STAFFS_PASSWORD));
		item.setEmail(rs.getString(Views.COL_STAFFS_EMAIL));
		item.setPhone(rs.getString(Views.COL_STAFFS_PHONE));
		item.setJobOccupied(rs.getInt(Views.COL_STAFFS_JOB_OCCUPIED));
		item.setImage(rs.getString(Views.COL_STAFFS_IMAGES));
		item.setCreateDate(rs.getDate(Views.COL_STAFFS_CREATEDATE));
		item.setStatus(rs.getString(Views.COL_STAFFS_STATUS));
		return item;
	}
}
