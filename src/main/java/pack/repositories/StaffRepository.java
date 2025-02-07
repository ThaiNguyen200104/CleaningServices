package pack.repositories;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.Staff;
import pack.modelviews.Staff_mapper;
import pack.utils.SecurityUtility;
import pack.utils.Views;

@Repository
public class StaffRepository {
	@Autowired
	JdbcTemplate db;

	/***
	 * fetch staff by id from table staffs
	 * 
	 * @return specific staff
	 */
	public Staff findStaffById(int id) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE  %s = ?", Views.TBL_STAFFS, Views.COL_STAFFS_ID);
			return db.queryForObject(str_query, new Staff_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * fetch staff by username or phone from table staffs
	 * 
	 * @return specific staff
	 */
	public Staff getStaffByUsernameOrPhone(String acc) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE  %s = ? or %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_USERNAME, Views.COL_STAFFS_PHONE);
			return db.queryForObject(str_query, new Staff_mapper(), new Object[] { acc, acc });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * fetch staff's email from table staffs
	 * 
	 * @return specific staff's email
	 */
	public Staff checkEmailExists(String email) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE  %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_EMAIL);
			return db.queryForObject(str_query, new Staff_mapper(), new Object[] { email });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * update account from table staffs
	 * 
	 * @return updated staff's account
	 */
	public String editProfile(Staff st) {
		try {
			StringBuilder queryBuilder = new StringBuilder("UPDATE " + Views.TBL_STAFFS + " SET ");
			List<Object> params = new ArrayList<>();

			if (st.getFullname() != null && !st.getFullname().isEmpty()) {
				queryBuilder.append("fullname = ?, ");
				params.add(st.getFullname());
			}

			if (st.getPassword() != null && !st.getPassword().isEmpty()) {
				queryBuilder.append("password = ?, ");
				String hashPassword = SecurityUtility.encryptBcrypt(st.getPassword());
				params.add(hashPassword);
			}

			if (st.getEmail() != null && !st.getEmail().isEmpty()) {
				queryBuilder.append("email = ?, ");
				params.add(st.getEmail());
			}

			if (st.getPhone() != null && !st.getPhone().isEmpty()) {
				queryBuilder.append("phone = ?, ");
				params.add(st.getPhone());
			}

			if (st.getImage() != null && !st.getImage().isEmpty()) {
				queryBuilder.append("image = ?, ");
				params.add(st.getImage());
			}

			if (params.isEmpty()) {
				return "No field needs to update.";
			}

			queryBuilder.setLength(queryBuilder.length() - 2);
			queryBuilder.append(" WHERE " + Views.COL_STAFFS_ID + " = ?");
			params.add(st.getId());

			int rowsAffected = db.update(queryBuilder.toString(), params.toArray());
			return rowsAffected == 1 ? "success" : "failed";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	// -------------------- CLIENT'S REQUEST -------------------- //

	/***
	 * fetch specific staff been assigned from table user_request_details
	 * 
	 * enable to search for a record
	 * 
	 * @return specific staff
	 */
	public List<Map<String, Object>> getStaffAssignedRequest(int staffId, String search) {
		try {
			List<Object> params = new ArrayList<>();
			String searchQuery = "";

			if (search != null && !search.trim().isEmpty()) {
				searchQuery = " AND (s.service_name LIKE ? OR u.fullname LIKE ? OR u.phone LIKE ? OR urd.status LIKE ?)";
				params.add("%" + search + "%");
				params.add("%" + search + "%");
				params.add("%" + search + "%");
				params.add("%" + search + "%");
			}

			String str_query = String.format(
					"SELECT urd.*, s.service_name, u.fullname, u.phone FROM %s urd JOIN %s s ON urd.service_id = s.id "
							+ "JOIN %s u ON urd.user_id = u.id WHERE urd.%s = ? AND urd.status NOT IN ('canceled', 'confirmed') %s",
					Views.TBL_USER_REQUEST_DETAILS, Views.TBL_SERVICES, Views.TBL_USER, Views.COL_URD_STAFFID,
					searchQuery);
			params.add(0, staffId);

			return db.queryForList(str_query, params.toArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * update price/start_date of an user's request from table user_request_details
	 * 
	 * @return updated price/start_date
	 */
	public String editRequest(double price, Date startDate, int urdId) {
		try {
			String str_query = String.format("UPDATE %s SET %s = ?, %s = ? WHERE  %s = ?",
					Views.TBL_USER_REQUEST_DETAILS, Views.COL_URD_PRICE, Views.COL_URD_STARTDATE, Views.COL_URD_ID);
			int rowaccepted = db.update(str_query, new Object[] { price, startDate, urdId });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	// -------------------- CLIENT'S ORDERS -------------------- //

	/***
	 * fetch specific staff being assigned from table schedules
	 * 
	 * enable to search for a record
	 * 
	 * @return specific staff
	 */
	public List<Map<String, Object>> getAssignedOrders(int staffId, String search) {
		try {
			List<Object> params = new ArrayList<>();
			String searchQuery = "";

			if (search != null && !search.trim().isEmpty()) {
				searchQuery = " AND (s.service_name LIKE ? OR u.fullname LIKE ? OR u.phone LIKE ? OR od.status LIKE ?)";
				params.add("%" + search + "%");
				params.add("%" + search + "%");
				params.add("%" + search + "%");
				params.add("%" + search + "%");
			}

			String str_query = String.format(
					"SELECT od.*, s.service_name, u.fullname, u.phone, sc.id AS scheId FROM %s sc "
							+ "JOIN %s od ON sc.detail_id = od.id JOIN %s o ON od.order_id = o.id "
							+ "JOIN %s ur ON o.usrReq_id = ur.id JOIN %s u ON ur.user_id = u.id "
							+ "JOIN %s s ON od.service_id = s.id WHERE sc.staff_id = ? %s ORDER BY od.create_date ASC",
					Views.TBL_SCHEDULES, Views.TBL_ORDER_DETAIL, Views.TBL_ORDER, Views.TBL_USER_REQUEST,
					Views.TBL_USER, Views.TBL_SERVICES, searchQuery);
			params.add(0, staffId);

			return db.queryForList(str_query, params.toArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * insert into table schedule_requests with type adjustDate
	 * 
	 * @return new schedule_request
	 */
	public String AdjustDate(int scheId, Date date, String reason) {
		try {
			String str_query = String.format("INSERT INTO %s(schedule_id, date_adjust, type, reason) VALUES (?,?,?,?)",
					Views.TBL_SCHEDULE_REQUESTS);
			int rowaccepted = db.update(str_query, new Object[] { scheId, date, "adjustDate", reason });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	/***
	 * insert into table schedule_requests with type cancel
	 * 
	 * @return new schedule_request
	 */
	public String cancelOrder(int scheId, String reason) {
		try {
			String str_query = String.format("INSERT INTO %s(schedule_id, type, reason) VALUES (?,?,?)",
					Views.TBL_SCHEDULE_REQUESTS);
			int rowaccepted = db.update(str_query, new Object[] { scheId, "cancel", reason });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	/***
	 * insert into table order_details
	 * 
	 * @return new schedule_request
	 */
	public String completeOrder(int detailId, String beforeImg, String afterImg) {
		try {
			String str_query = "UPDATE order_details SET status = 'verifying', beforeImage = ?, afterImage = ? WHERE id = ?";
			int rowaccepted = db.update(str_query, new Object[] { beforeImg, afterImg, detailId });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	/***
	 * fetch request from table schedule_requests
	 * 
	 * @return specific schedule_requests
	 */
	public boolean hasScheduleRequestByType(int scheId, String type) {
		try {
			String str_query = "SELECT COUNT(*) FROM schedule_requests WHERE schedule_id = ? AND type = ? AND status = 'pending'";
			int count = db.queryForObject(str_query, Integer.class, new Object[] { scheId, type });
			return count > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}