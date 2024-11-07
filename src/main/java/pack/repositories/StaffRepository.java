package pack.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.Staff;
import pack.modelviews.Staff_mapper;
import pack.utils.Views;

@Repository
public class StaffRepository {
	@Autowired
	JdbcTemplate db;

	public Staff getStaffByUsernameOrPhone(String acc) {
		try {
			String str_query = String.format("select * from %s where %s = ? or %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_USERNAME, Views.COL_STAFFS_PHONE);
			return db.queryForObject(str_query, new Staff_mapper(), new Object[] { acc, acc});
		} catch (Exception e) {
			return null;
		}
	}
	
	public Staff checkEmailExists(String email) {
		try {
			String str_query = String.format("select * from %s where %s = ?", Views.TBL_STAFFS, Views.COL_STAFFS_EMAIL);
			return db.queryForObject(str_query, new Staff_mapper(), new Object[] { email });
		} catch (Exception e) {
			return null;
		}
	}
}