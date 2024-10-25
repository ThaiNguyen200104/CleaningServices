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

	public Staff getStaffByUsername(String username) {
		try {
			String str_query = String.format("select * from %s where %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_USERNAME);
			return db.queryForObject(str_query, new Staff_mapper(), new Object[] { username });
		} catch (Exception e) {
			return null;
		}
	}
}