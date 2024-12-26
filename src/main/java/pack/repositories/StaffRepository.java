package pack.repositories;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.OrderDetail;
import pack.models.Staff;
import pack.models.UserRequestDetail;
import pack.modelviews.OrderDetail_mapper;
import pack.modelviews.Staff_mapper;
import pack.modelviews.UserRequestDetail_mapper;
import pack.utils.Views;

@Repository
public class StaffRepository {
	@Autowired
	JdbcTemplate db;

	public Staff findStaffById(int id) {
		try {
			String str_query = String.format("select * from %s where %s = ?", Views.TBL_STAFFS, Views.COL_STAFFS_ID);
			return db.queryForObject(str_query, new Staff_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public Staff getStaffByUsernameOrPhone(String acc) {
		try {
			String str_query = String.format("select * from %s where %s = ? or %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_USERNAME, Views.COL_STAFFS_PHONE);
			return db.queryForObject(str_query, new Staff_mapper(), new Object[] { acc, acc });
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

	// REQUEST
	public List<Map<String, Object>> getStaffAssginedRequest(int staffId) {
		try {
			String str_query = String.format(
					"select urd.*, s.service_name, u.fullname from %s urd join %s s on urd.service_id = s.id join %s u on urd.user_id = u.id where urd.%s = ? and urd.status != 'canceled' and urd.status != 'confirmed'",
					Views.TBL_USER_REQUEST_DETAILS, Views.TBL_SERVICES, Views.TBL_USER, Views.COL_URD_STAFFID);
			return db.queryForList(str_query, new Object[] { staffId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String editRequest(double price, Date startDate, int urdId) {
		try {
			String str_query = String.format("update %s set %s = ?, %s = ? where %s = ?",
					Views.TBL_USER_REQUEST_DETAILS, Views.COL_URD_PRICE, Views.COL_URD_STARTDATE, Views.COL_URD_ID);
			int rowaccepted = db.update(str_query, new Object[] { price, startDate, urdId });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	// ORDER
	public List<Map<String, Object>> getAssignedOrders(int staffId) {
		try {
			String str_query = "select od.*, s.service_name, u.fullname, sc.id as scheId from schedules sc "
					+ "join order_details od ON sc.detail_id = od.id " + "join orders o ON od.order_id = o.id "
					+ "join user_requests ur ON o.usrReq_id = ur.id " + "join users u ON ur.user_id = u.id "
					+ "join services s ON od.service_id = s.id where sc.staff_id = ? order by od.create_date asc";
			return db.queryForList(str_query, new Object[] { staffId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String AdjustDate(int scheId, Date date, String reason) {
		try {
			String str_query = String.format("insert into %s(schedule_id, date_adjust, type, reason) values(?,?,?,?)",
					Views.TBL_SCHEDULE_REQUESTS);
			int rowaccepted = db.update(str_query, new Object[] { scheId, date, "adjustDate", reason });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	public String cancelOrder(int scheId, String reason) {
		try {
			String str_query = String.format("insert into %s(schedule_id, type, reason) values(?,?,?)",
					Views.TBL_SCHEDULE_REQUESTS);
			int rowaccepted = db.update(str_query, new Object[] { scheId, "cancel", reason });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	public boolean hasScheduleRequestByType(int scheId, String type) {
		try {
			String str_query = "SELECT COUNT(*) FROM schedule_requests "
					+ "WHERE schedule_id = ? AND type = ? AND status = 'pending'";
			int count = db.queryForObject(str_query, Integer.class, new Object[] { scheId, type });
			return count > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String completeOrder(int detailId, String beforeImg, String afterImg) {
		try {
			String str_query = "update order_details set status = 'verifying', beforeImage = ?, afterImage = ? where id = ?";
			int rowaccepted = db.update(str_query, new Object[] { beforeImg, afterImg, detailId });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}
}