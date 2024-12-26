package pack.repositories;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pack.models.Admin;
import pack.models.Blog;
import pack.models.OrderDetail;
import pack.models.PageView;
import pack.models.Schedule;
import pack.models.Service;
import pack.models.Staff;
import pack.models.UserRequestDetail;
import pack.modelviews.Admin_mapper;
import pack.modelviews.Blog_mapper;
import pack.modelviews.OrderDetail_mapper;
import pack.modelviews.Service_mapper;
import pack.modelviews.Staff_mapper;
import pack.modelviews.UserRequestDetail_mapper;
import pack.utils.SecurityUtility;
import pack.utils.Views;

@Repository
public class AdminRepository {
	@Autowired
	JdbcTemplate db;

	// Admin Region
	public Admin getAdminByUsername(String username) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_ADMIN, Views.COL_ADMIN_USERNAME);
			return db.queryForObject(str_query, new Admin_mapper(), new Object[] { username });
		} catch (Exception e) {
			return null;
		}
	}

	public Admin getAdminById(int id) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_ADMIN, Views.COL_ADMIN_ID);
			return db.queryForObject(str_query, new Admin_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public String newAdmin(String username, String password) {
		try {
			String str_query = String.format("insert into %s (username, password) values(?,?)", Views.TBL_ADMIN);
			int rowaccept = db.update(str_query, new Object[] { username, SecurityUtility.encryptBcrypt(password) });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public Admin checkEmailExists(String email) {
		try {
			String str_query = String.format("select * from %s where %s = ?", Views.TBL_ADMIN, Views.COL_ADMIN_EMAIL);
			return db.queryForObject(str_query, new Admin_mapper(), new Object[] { email });
		} catch (Exception e) {
			return null;
		}
	}

	public String changePass(String password) {
		try {
			String str_query = String.format("update %s set %s = ? where %s = ?", Views.TBL_ADMIN,
					Views.COL_ADMIN_PASSWORD, Views.COL_ADMIN_ID);
			String hashpassword = SecurityUtility.encryptBcrypt(password);
			int rowaccept = db.update(str_query, new Object[] { hashpassword });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public List<Service> getServices(PageView pageItem) {
		try {
			int count = db.queryForObject("select count(*) from services", Integer.class);
			int total_page = count / pageItem.getPageSize();
			pageItem.setTotalPage(total_page);

			String str_query = String.format("select * from %s order by %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_SERVICES, Views.COL_SERVICES_ID);
			return db.query(str_query, new Service_mapper(),
					new Object[] { (pageItem.getPageCurrent() - 1) * pageItem.getPageSize(), pageItem.getPageSize() });
		} catch (Exception e) {
			return null;
		}
	}

	public Service getServiceName() {
		try {
			String str_query = String.format("select * from %s", Views.TBL_SERVICES);
			return db.queryForObject(str_query, new Service_mapper());
		} catch (Exception e) {
			return null;
		}
	}

	public Service getServiceById(int id) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_SERVICES, Views.COL_SERVICES_ID);
			return db.queryForObject(str_query, new Service_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public String newService(Service ser) {
		try {
			StringBuilder queryBuilder = new StringBuilder("insert into ");
			queryBuilder.append(Views.TBL_SERVICES).append(" (service_name, base_price, staff_required");
			StringBuilder valuesBuilder = new StringBuilder(" values (?, ?, ?");

			List<Object> params = new ArrayList<>();
			params.add(ser.getSerName());
			params.add(ser.getBasePrice());
			params.add(ser.getStaffRequired());

			if (ser.getDescription() != null) {
				queryBuilder.append(", description");
				valuesBuilder.append(", ?");
				params.add(ser.getDescription());
			}
			if (ser.getImage() != null && !ser.getImage().isEmpty()) {
				queryBuilder.append(", image");
				valuesBuilder.append(", ?");
				params.add(ser.getImage());
			}
			queryBuilder.append(")");
			valuesBuilder.append(")");
			queryBuilder.append(valuesBuilder);
			int rowsAffected = db.update(queryBuilder.toString(), params.toArray());
			return rowsAffected == 1 ? "success" : "failed";
		} catch (DuplicateKeyException e) {
			throw new IllegalArgumentException("Service name may already exists.");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String editService(Service ser) {
		try {
			StringBuilder queryBuilder = new StringBuilder("update " + Views.TBL_SERVICES + " set ");
			List<Object> params = new ArrayList<>();

			if (ser.getSerName() != null && !ser.getSerName().isEmpty()) {
				queryBuilder.append("service_name = ?, ");
				params.add(ser.getSerName());
			}

			if (ser.getImage() != null && !ser.getImage().isEmpty()) {
				queryBuilder.append("image = ?, ");
				params.add(ser.getImage());
			}

			if (ser.getDescription() != null && !ser.getDescription().isEmpty()) {
				queryBuilder.append("description = ?, ");
				params.add(ser.getDescription());
			}

			if (ser.getBasePrice() > 0) {
				queryBuilder.append("base_price = ?, ");
				params.add(ser.getBasePrice());
			}

			if (ser.getStaffRequired() > 0) {
				queryBuilder.append("staff_required = ?, ");
				params.add(ser.getStaffRequired());
			}

			if (queryBuilder.toString().endsWith(", ")) {
				queryBuilder.setLength(queryBuilder.length() - 2);
			}

			queryBuilder.append(" where " + Views.COL_SERVICES_ID + " = ?");
			params.add(ser.getId());

			int rowsAffected = db.update(queryBuilder.toString(), params.toArray());
			return rowsAffected == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String activateServiceStatus(int id) {
		try {
			String str_query = String.format("update %s set %s = 'activated' where %s = ?", Views.TBL_SERVICES,
					Views.COL_SERVICES_STATUS, Views.COL_SERVICES_ID);
			int rowaccept = db.update(str_query, new Object[] { id });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String disableServiceStatus(int id) {
		try {
			String str_query = String.format("update %s set %s = 'disabled' where %s = ?", Views.TBL_SERVICES,
					Views.COL_SERVICES_STATUS, Views.COL_SERVICES_ID);
			int rowaccept = db.update(str_query, new Object[] { id });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// Blog region
	public List<Blog> getBlogs(PageView pageItem) {
		try {
			int count = db.queryForObject("select count(*) from blogs", Integer.class);
			int totalPage = count / pageItem.getPageSize();
			pageItem.setTotalPage(totalPage);

			String str_query = String.format("select * from %s order by %s desc offset ? rows fetch next ? rows only",
					Views.TBL_BLOG, Views.COL_BLOG_ID);
			return db.query(str_query, new Blog_mapper(),
					new Object[] { (pageItem.getPageCurrent() - 1) * pageItem.getPageSize(), pageItem.getPageSize() });
		} catch (Exception e) {
			return null;
		}
	}

	public Blog getBlogById(int id) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_BLOG, Views.COL_BLOG_ID);
			return db.queryForObject(str_query, new Blog_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public String newBlog(Blog blog) {
		try {
			String str_query = String.format("insert into %s (title, content, images) values(?,?,?)", Views.TBL_BLOG);
			int rowaccept = db.update(str_query, new Object[] { blog.getTitle(), blog.getContent(), blog.getImage() });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String editBlog(Blog blog) {
		try {
			String str_query = String.format("update %s set %s=?, %s=?, %s=?, %s=GETDATE() where id=?", Views.TBL_BLOG,
					Views.COL_BLOG_TITLE, Views.COL_BLOG_CONTENT, Views.COL_BLOG_IMAGES, Views.COL_BLOG_UPDATEDATE);
			int rowaccept = db.update(str_query,
					new Object[] { blog.getTitle(), blog.getContent(), blog.getImage(), blog.getUpdateDate() });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String deleteBlog(int id) {
		try {
			String str_query = String.format("delete from %s where %s=?", Views.TBL_BLOG, Views.COL_BLOG_ID);
			int rowaccept = db.update(str_query, new Object[] { id });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// Staff region
	public List<Staff> getStaffs(PageView pageItem) {
		try {
			int count = db.queryForObject("select count(*) from staffs where status != 'disabled'", Integer.class);
			int totalPage = count / pageItem.getPageSize();
			pageItem.setTotalPage(totalPage);

			String str_query = String.format(
					"select * from %s where %s != 'disabled' order by %s desc offset ? rows fetch next ? rows only",
					Views.TBL_STAFFS, Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			return db.query(str_query, new Staff_mapper(),
					new Object[] { (pageItem.getPageCurrent() - 1) * pageItem.getPageSize(), pageItem.getPageSize() });
		} catch (Exception e) {
			return null;
		}
	}

	public List<Staff> getStaffsForOrder(PageView pageItem) {
		try {
			int count = db.queryForObject(
					"select count(*) from staffs where status != 'disabled' and status != 'unavailable'",
					Integer.class);
			int totalPage = count / pageItem.getPageSize();
			pageItem.setTotalPage(totalPage);

			String str_query = String.format(
					"select * from %s where %s != 'disabled' and status != 'unavailable' order by %s desc offset ? rows fetch next ? rows only",
					Views.TBL_STAFFS, Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			return db.query(str_query, new Staff_mapper(),
					new Object[] { (pageItem.getPageCurrent() - 1) * pageItem.getPageSize(), pageItem.getPageSize() });
		} catch (Exception e) {
			return null;
		}
	}

	public Staff getStaffById(int id) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_STAFFS, Views.COL_STAFFS_ID);
			return db.queryForObject(str_query, new Staff_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public String newStaff(Staff staff) {
		try {
			String str_query = String.format(
					"insert into %s (username, password, email, phone, fullname) values(?,?,?,?,?)", Views.TBL_STAFFS);
			String hashpassword = SecurityUtility.encryptBcrypt(staff.getPassword());
			int rowaccept = db.update(str_query, new Object[] { staff.getUsername(), hashpassword, staff.getEmail(),
					staff.getPhone(), staff.getFullname() });
			return rowaccept == 1 ? "success" : "failed";
		} catch (DuplicateKeyException e) {
			throw new IllegalArgumentException("Some information(username, email, phone) may already exists.");
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	public String disableStaff(int id) {
		try {
			String str_query = String.format("update %s set %s = ? where %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			int rowaccepted = db.update(str_query, new Object[] { "disabled", id });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	// assign staff for request
	public List<Map<String, Object>> getRequestDetails() {
		try {
			String str_query = String.format(
					"select urd.*, s.service_name, u.fullname from %s urd join %s s on urd.service_id = s.id "
							+ "join %s u on urd.user_id = u.id where urd.status = 'pending' or urd.status = 'reviewing' order by create_date desc",
					Views.TBL_USER_REQUEST_DETAILS, Views.TBL_SERVICES, Views.TBL_USER);
			return db.queryForList(str_query);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int countAvailableStaff() {
		try {
			String query = "SELECT COUNT(*) FROM staffs WHERE status = 'available'";
			return db.queryForObject(query, Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<Staff> staffListForAssignRequest() {
		try {
			String str_query = String.format("select * from %s where %s = 'available'", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS);
			return db.query(str_query, new Staff_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String assignStaffIntoRequest(int staffId, int urdId) {
		try {
			String request_query = String.format("update %s set %s=?, %s ='reviewing' where %s = ?",
					Views.TBL_USER_REQUEST_DETAILS, Views.COL_URD_STAFFID, Views.COL_URD_STATUS, Views.COL_URD_ID);
			String staff_query = String.format("update %s set %s = 'unavailable' where %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			int requestaccepted = db.update(request_query, new Object[] { staffId, urdId });
			if (requestaccepted == 1) {
				int staffaccepted = db.update(staff_query, new Object[] { staffId });
				return staffaccepted == 1 ? "success" : "failed";
			}
			return "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}
	// end region

	public List<Staff> staffListForAssign(int detailId) {
		try {
			String str_query = String.format(
					"SELECT * FROM %s WHERE %s = ? AND %s < 3 AND id NOT IN (SELECT staff_id FROM %s WHERE detail_id = ?)",
					Views.TBL_STAFFS, Views.COL_STAFFS_STATUS, Views.COL_STAFFS_JOB_OCCUPIED, Views.TBL_SCHEDULES);
			return db.query(str_query, new Staff_mapper(), new Object[] { "available", detailId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String assignStaff(Schedule item) {
		try {
			String latestScheduleQuery = String.format("SELECT MAX(start_date) FROM %s WHERE staff_id = ?",
					Views.TBL_SCHEDULES);

			LocalDateTime latestStartDate = db.queryForObject(latestScheduleQuery, LocalDateTime.class,
					new Object[] { item.getStaffId() });

			if (latestStartDate != null) {
				Duration timeDifference = Duration.between(latestStartDate, item.getStartDate());

				if (timeDifference.toHours() < 5) {
					return "The new start date must be at least 5 hours after the latest assigned schedule for this staff member.";
				}
			}
			String str_query = String.format("insert into %s (staff_id, detail_id, start_date) values(?,?,?)",
					Views.TBL_SCHEDULES);
			String staff_query = String.format("update %s set %s = 'unavailable' where %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			int rowaccepted = db.update(str_query,
					new Object[] { item.getStaffId(), item.getDetailId(), item.getStartDate() });
			if (rowaccepted == 1) {
				int row2accepted = db.update(staff_query, new Object[] { item.getStaffId() });
			}
			return "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	public List<Staff> AssignedStaffList(int id) {
		try {
			String str_query = String.format("select s.* from %s s join %s ts on s.id = ts.staff_id where ts.%s = ?",
					Views.TBL_STAFFS, Views.TBL_SCHEDULES, Views.COL_SCHEDULES_DETAIL_ID);
			return db.query(str_query, new Staff_mapper(), new Object[] { id });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Staff> getCurrentStaff(int detailId) {
		try {
			String str_query = "select s.* from staffs s join schedules sd on s.id = sd.staff_id"
					+ " join order_details od on sd.detail_id = od.id where od.id = ?";
			return db.query(str_query, new Staff_mapper(), new Object[] { detailId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Staff> getAvailableStaffForReplacement(int orderId, int excludeStaffId) {
		String query = "SELECT * FROM staffs WHERE id != ? "
				+ "AND id NOT IN (SELECT staff_id FROM schedules WHERE detail_id = ?)";
		return db.query(query, new Staff_mapper(), new Object[] { excludeStaffId, orderId });
	}

	public String ReplaceStaff(int currentStaff, int newStaff, int orderId) {
		try {
			String schedule_query = String.format("update %s set %s =? where %s = ? and %s = ?", Views.TBL_SCHEDULES,
					Views.COL_SCHEDULES_STAFF_ID, Views.COL_SCHEDULES_STAFF_ID, Views.COL_SCHEDULES_DETAIL_ID);
			String newstaff_query = String.format("update %s set %s = 'unavailable' where %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			String oldstaff_query = String.format("update %s set %s = 'available' where %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			int rowaccepted = db.update(schedule_query, new Object[] { newStaff, currentStaff, orderId });
			if (rowaccepted == 1) {
				int row1accepted = db.update(newstaff_query, new Object[] { newStaff });
				int row2accepted = db.update(oldstaff_query, new Object[] { currentStaff });
				return row1accepted == 1 && row2accepted == 1 ? "success" : "failed";
			}
			return "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	// Order region
	public List<Map<String, Object>> getOrders(PageView pageItem) {
		try {
			int count = db.queryForObject("select count(*) from order_details", Integer.class);
			int totalPage = count / pageItem.getPageSize();
			pageItem.setTotalPage(totalPage);

			String str_query = String.format("SELECT od.*, o.*, u.fullname, s.staff_required, "
					+ "CASE WHEN EXISTS (SELECT 1 FROM schedules s WHERE s.detail_id = od.id) "
					+ "THEN 1 ELSE 0 END AS hasAssignedStaff " + "FROM %s od " + "JOIN %s o ON od.order_id = o.id "
					+ "JOIN %s ur ON o.usrReq_id = ur.id join %s u on ur.user_id = u.id join services s on od.service_id = s.id "
					+ "ORDER BY %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", Views.TBL_ORDER_DETAIL, Views.TBL_ORDER,
					Views.TBL_USER_REQUEST, Views.TBL_USER, Views.COL_ORDER_DETAIL_CREATEDATE);
			return db.queryForList(str_query, (pageItem.getPageCurrent() - 1) * pageItem.getPageSize(),
					pageItem.getPageSize());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public OrderDetail getDetailById(int id) {
		try {
			String str_query = String.format(
					"select * from %s od join services s on od.service_id = s.id where od.%s=?", Views.TBL_ORDER_DETAIL,
					Views.COL_ORDER_DETAIL_ID);
			return db.queryForObject(str_query, new OrderDetail_mapper(), new Object[] { id });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void CheckOrderDetailUpToDate() {
		try {
			String str_query = String.format(
					"update %s set %s = 'progressing' where start_date = convert(date, getdate()) and status = 'confirmed'",
					Views.TBL_ORDER_DETAIL, Views.COL_ORDER_DETAIL_STATUS);
			db.update(str_query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Schedule_Request region
	public List<Map<String, Object>> getRequestList() {
		try {
			String str_query = String.format(
					"select scr.*, scr.id as scrId, scr.status as scrStatus, sc.staff_id as oldStaff, sc.start_date, st.fullname from %s scr join %s sc on scr.schedule_id = sc.id join %s st on sc.staff_id = st.id where scr.status = 'pending' order by create_date asc",
					Views.TBL_SCHEDULE_REQUESTS, Views.TBL_SCHEDULES, Views.TBL_STAFFS);
			return db.queryForList(str_query);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String approveDateRequest(int scheduleId, Date newDate, int scrId) {
		try {
			String sche_query = "update schedules set start_date = ? where id = ?";
			String sr_query = "update schedule_requests set status = ? where id = ?";

			Integer orderId = db.queryForObject(
					"select od.id from schedules s join order_details od on s.detail_id = od.id where s.id = ?",
					Integer.class, scheduleId);

			if (orderId == null) {
				return "failed";
			}

			String order_query = "update order_details set start_date = ? where id = ?";
			int rowaccepted = db.update(sche_query, new Object[] { newDate, scheduleId });

			if (rowaccepted == 1) {
				db.update(sr_query, new Object[] { "approved", scrId });
				db.update(order_query, new Object[] { newDate, orderId });
				return "success";
			}
			return "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	public String denyRequest(int scheduleRequestId) {
		try {
			String str_query = String.format("update %s set %s = 'denied' where %s = ?", Views.TBL_SCHEDULE_REQUESTS,
					Views.COL_SCHEDULE_REQUESTS_STATUS, Views.COL_SCHEDULE_REQUESTS_ID);
			int rowaccepted = db.update(str_query, new Object[] { scheduleRequestId });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	@Transactional
	public String approveCancelRequest(int newStaffId, int scrId, int oldStaffId) {
		try {
			String updateScheduleQuery = "UPDATE schedules SET staff_id = ? " + "FROM schedules s "
					+ "JOIN schedule_requests sr ON s.id = sr.schedule_id " + "WHERE sr.id = ?";
			int rowsUpdated = db.update(updateScheduleQuery, newStaffId, scrId);

			if (rowsUpdated == 1) {
				String updateOldStaffQuery = "UPDATE staffs SET status = 'available' WHERE id = ?";
				db.update(updateOldStaffQuery, oldStaffId);

				String updateNewStaffQuery = "UPDATE staffs SET status = 'unavailable' WHERE id = ?";
				db.update(updateNewStaffQuery, newStaffId);

				String updateScheduleRequestQuery = "UPDATE schedule_requests SET status = 'approved' WHERE id = ?";
				db.update(updateScheduleRequestQuery, scrId);

				return "success";
			}
			return "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	// end Schedule_Request region
}
