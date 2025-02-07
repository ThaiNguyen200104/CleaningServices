package pack.repositories;

import java.sql.Date;
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
import pack.modelviews.Admin_mapper;
import pack.modelviews.Blog_mapper;
import pack.modelviews.OrderDetail_mapper;
import pack.modelviews.Service_mapper;
import pack.modelviews.Staff_mapper;
import pack.utils.SecurityUtility;
import pack.utils.Views;

@Repository
public class AdminRepository {
	@Autowired
	JdbcTemplate db;

	// -------------------- ACCOUNTS -------------------- //

	/***
	 * fetch specific admin by username from table admin
	 * 
	 * @return admin's username
	 */
	public Admin getAdminByUsername(String username) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_ADMIN, Views.COL_ADMIN_USERNAME);
			return db.queryForObject(str_query, new Admin_mapper(), new Object[] { username });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * fetch specific admin by id from table admin
	 * 
	 * @return admin's id
	 */
	public Admin getAdminById(int id) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_ADMIN, Views.COL_ADMIN_ID);
			return db.queryForObject(str_query, new Admin_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * insert into table admin
	 * 
	 * @return new admin
	 */
	public String newAdmin(String username, String password) {
		try {
			String str_query = String.format("INSERT INTO %s (username, password) VALUES (?,?)", Views.TBL_ADMIN);
			int rowaccept = db.update(str_query, new Object[] { username, SecurityUtility.encryptBcrypt(password) });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	/***
	 * fetch email from table admin for duplication
	 * 
	 * @return admin's email
	 */
	public Admin checkEmailExists(String email) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s = ?", Views.TBL_ADMIN, Views.COL_ADMIN_EMAIL);
			return db.queryForObject(str_query, new Admin_mapper(), new Object[] { email });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * update admin's password from table admin
	 * 
	 * @return updated admin's password
	 */
	public String changePass(String password) {
		try {
			String str_query = String.format("UPDATE %s SET %s = ? WHERE %s = ?", Views.TBL_ADMIN,
					Views.COL_ADMIN_PASSWORD, Views.COL_ADMIN_ID);
			String hashpassword = SecurityUtility.encryptBcrypt(password);
			int rowaccept = db.update(str_query, new Object[] { hashpassword });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	/***
	 * update account from table admin
	 * 
	 * @return updated admin's account
	 */
	public String editProfile(Admin admin) {
		try {
			StringBuilder queryBuilder = new StringBuilder("UPDATE " + Views.TBL_ADMIN + " SET ");
			List<Object> params = new ArrayList<>();

			if (admin.getEmail() != null && !admin.getEmail().isEmpty()) {
				queryBuilder.append("email = ?, ");
				params.add(admin.getEmail());
			}

			if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
				queryBuilder.append("password = ?, ");
				String hashPassword = SecurityUtility.encryptBcrypt(admin.getPassword());
				params.add(hashPassword);
			}

			if (params.isEmpty()) {
				return "No field needs to update.";
			}

			queryBuilder.setLength(queryBuilder.length() - 2);
			queryBuilder.append(" WHERE " + Views.COL_ADMIN_ID + " = ?");
			params.add(admin.getId());

			int rowsAffected = db.update(queryBuilder.toString(), params.toArray());
			return rowsAffected == 1 ? "success" : "failed";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}
	// -------------------- SERVICES -------------------- //

	/***
	 * fetch all data from table services
	 * 
	 * @return list of services
	 */
	public List<Service> getServices(PageView pageItem) {
		try {
			int count = db.queryForObject("SELECT COUNT(*) FROM services", Integer.class);
			int total_page = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(total_page);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), total_page);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());

			String str_query = String.format("SELECT * FROM %s ORDER BY %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_SERVICES, Views.COL_SERVICES_ID);
			return db.query(str_query, new Service_mapper(), offset, pageItem.getPageSize());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch a service from table services with id
	 * 
	 * @return specific service
	 */
	public Service getServiceById(int id) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_SERVICES, Views.COL_SERVICES_ID);
			return db.queryForObject(str_query, new Service_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * insert into table services
	 * 
	 * @return new service
	 */
	public String newService(Service ser) {
		try {
			StringBuilder queryBuilder = new StringBuilder("INSERT INTO ");
			queryBuilder.append(Views.TBL_SERVICES).append(" (service_name, base_price, staff_required");
			StringBuilder valuesBuilder = new StringBuilder(" VALUES (?, ?, ?");

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

	/***
	 * update for table services
	 * 
	 * @return updated service
	 */
	public String editService(Service ser) {
		try {
			StringBuilder queryBuilder = new StringBuilder("UPDATE " + Views.TBL_SERVICES + " SET ");
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

			queryBuilder.append(" WHERE " + Views.COL_SERVICES_ID + " = ?");
			params.add(ser.getId());

			int rowsAffected = db.update(queryBuilder.toString(), params.toArray());
			return rowsAffected == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	/***
	 * update status for table services
	 * 
	 * @return updated status = 'activated'
	 */
	public String activateServiceStatus(int id) {
		try {
			String str_query = String.format("UPDATE %s SET %s = 'activated' WHERE %s = ?", Views.TBL_SERVICES,
					Views.COL_SERVICES_STATUS, Views.COL_SERVICES_ID);
			int rowaccept = db.update(str_query, new Object[] { id });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	/***
	 * update status for table services
	 * 
	 * @return updated services's status = 'disabled'
	 */
	public String disableServiceStatus(int id) {
		try {
			String str_query = String.format("UPDATE %s SET %s = 'disabled' WHERE %s = ?", Views.TBL_SERVICES,
					Views.COL_SERVICES_STATUS, Views.COL_SERVICES_ID);
			int rowaccept = db.update(str_query, new Object[] { id });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// -------------------- BLOGS -------------------- //

	/***
	 * fetch blog from table blogs
	 * 
	 * @return list of blog
	 */
	public List<Blog> getBlogs(PageView pageItem) {
		try {
			int count = db.queryForObject("SELECT COUNT(*) FROM blogs", Integer.class);
			int total_page = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(total_page);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), total_page);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());

			String str_query = String.format("SELECT * FROM %s ORDER BY %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_BLOG, Views.COL_BLOG_ID);
			return db.query(str_query, new Blog_mapper(), offset, pageItem.getPageSize());
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * fetch specific blog by id from table blogs
	 * 
	 * @return a specific blog
	 */
	public Blog getBlogById(int id) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_BLOG, Views.COL_BLOG_ID);
			return db.queryForObject(str_query, new Blog_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * insert into table blogs
	 * 
	 * @return a new blog
	 */
	public String newBlog(Blog blog) {
		try {
			StringBuilder queryBuilder = new StringBuilder("INSERT INTO ");
			queryBuilder.append(Views.TBL_BLOG).append(" (title, content");
			StringBuilder valuesBuilder = new StringBuilder(" VALUES (?, ?");

			List<Object> params = new ArrayList<>();
			params.add(blog.getTitle());
			params.add(blog.getContent());

			if (blog.getImage() != null && !blog.getImage().isEmpty()) {
				queryBuilder.append(", image");
				valuesBuilder.append(", ?");
				params.add(blog.getImage());
			}

			if (blog.getUpdateDate() != null) {
				queryBuilder.append(", update_date");
				valuesBuilder.append(", ?");
				params.add(blog.getUpdateDate());
			}

			queryBuilder.append(")");
			valuesBuilder.append(")");
			queryBuilder.append(valuesBuilder);

			int rowsAffected = db.update(queryBuilder.toString(), params.toArray());
			return rowsAffected == 1 ? "success" : "failed";
		} catch (DuplicateKeyException e) {
			throw new IllegalArgumentException("Title may already exists.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * update for table blogs
	 * 
	 * @return updated blog
	 */
	public String editBlog(Blog blog) {
		try {
			StringBuilder queryBuilder = new StringBuilder("UPDATE blogs SET ");
			List<Object> params = new ArrayList<>();
			if (blog.getTitle() != null && !blog.getTitle().isEmpty()) {
				queryBuilder.append("title = ?, ");
				params.add(blog.getTitle());
			}

			if (blog.getContent() != null && !blog.getContent().isEmpty()) {
				queryBuilder.append("content = ?, ");
				params.add(blog.getContent());
			}

			if (blog.getImage() != null && !blog.getImage().isEmpty()) {
				queryBuilder.append("image = ?, ");
				params.add(blog.getImage());
			}

			queryBuilder.append("update_date = GETDATE(), ");
			if (queryBuilder.toString().endsWith(", ")) {
				queryBuilder.setLength(queryBuilder.length() - 2);
			}

			queryBuilder.append(" WHERE id = ?");
			params.add(blog.getId());

			int rowsAffected = db.update(queryBuilder.toString(), params.toArray());
			return rowsAffected == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	/***
	 * delete a specific blog from table blogs
	 * 
	 * @return deleted blog by id
	 */
	public String deleteBlog(int id) {
		try {
			String str_query = String.format("DELETE FROM %s WHERE %s = ?", Views.TBL_BLOG, Views.COL_BLOG_ID);

			int rowAccepted = db.update(str_query, new Object[] { id });
			return rowAccepted == 1 ? "succeed" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	// -------------------- STAFFS -------------------- //

	/***
	 * fetch specific staff by status == 'available' from table staffs
	 * 
	 * @return a specific available staff
	 */
	public List<Staff> getStaffs(PageView pageItem, String search) {
		try {
			String searchQuery = "";
			List<Object> params = new ArrayList<>();

			if (search != null && !search.trim().isEmpty()) {
				searchQuery = " AND (fullname LIKE ? OR phone LIKE ? OR email LIKE ?)";
				String searchPattern = "%" + search + "%";
				params.add(searchPattern);
				params.add(searchPattern);
				params.add(searchPattern);
			}

			String countQuery = String.format("SELECT COUNT(*) FROM %s WHERE %s != 'disabled' %s", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, searchQuery);

			int count = db.queryForObject(countQuery, Integer.class, params.toArray());

			int totalPage = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(totalPage);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), totalPage);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());

			String query = String.format(
					"SELECT * FROM %s WHERE %s != 'disabled' %s ORDER BY %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_STAFFS, Views.COL_STAFFS_STATUS, searchQuery, Views.COL_STAFFS_ID);

			params.add(offset);
			params.add(pageItem.getPageSize());

			return db.query(query, new Staff_mapper(), params.toArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch specific staff by status != 'disabled, unavailable' from table staffs
	 * 
	 * @return a specific staff
	 */
	public List<Staff> getStaffsForOrder(PageView pageItem) {
		try {
			int count = db.queryForObject(
					"SELECT count(*) FROM staffs WHERE status != 'disabled' AND status != 'unavailable'",
					Integer.class);

			int total_page = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(total_page);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), total_page);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());

			String str_query = String.format(
					"SELECT * FROM %s WHERE %s != 'disabled' AND status != 'unavailable' ORDER BY %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_STAFFS, Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			return db.query(str_query, new Staff_mapper(), offset, pageItem.getPageSize());
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * fetch specific staff by id from table staffs
	 * 
	 * @return staff's id
	 */
	public Staff getStaffById(int id) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_STAFFS, Views.COL_STAFFS_ID);
			return db.queryForObject(str_query, new Staff_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * insert into table staffs
	 * 
	 * @return new staff's account
	 */
	public String newStaff(Staff staff) {
		try {
			String str_query = String.format(
					"INSERT INTO %s (username, password, email, phone, fullname) VALUES (?,?,?,?,?)", Views.TBL_STAFFS);
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

	/***
	 * update status for table staffs
	 * 
	 * @return updated status = 'disabled'
	 */
	public String disableStaff(int id) {
		try {
			String str_query = String.format("UPDATE %s SET %s = ? WHERE %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			int rowaccepted = db.update(str_query, new Object[] { "disabled", id });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	// -------------------- REQUEST DETAILS -------------------- //

	/***
	 * fetch all staffs by status = 'available' from table staffs
	 * 
	 * @return list of available staffs
	 */
	public List<Staff> staffListForAssignRequest(PageView pageItem) {
		try {
			int count = db.queryForObject("SELECT COUNT(*) FROM staffs WHERE status = 'available'", Integer.class);
			int total_page = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(total_page);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), total_page);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());

			String str_query = String.format(
					"SELECT * FROM %s WHERE %s = 'available' ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_STAFFS, Views.COL_STAFFS_STATUS);
			return db.query(str_query, new Staff_mapper(), offset, pageItem.getPageSize());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch specific client's request by id from table user_request_details
	 * 
	 * @return a specific client's request
	 */
	public List<Map<String, Object>> getRequestDetails(PageView pageItem, String search) {
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

			String countQuery = String.format(
					"SELECT COUNT(*) FROM %s urd JOIN %s s ON urd.service_id = s.id JOIN %s u ON urd.user_id = u.id "
							+ "WHERE (urd.status = 'pending' OR urd.status = 'reviewing') %s",
					Views.TBL_USER_REQUEST_DETAILS, Views.TBL_SERVICES, Views.TBL_USER, searchQuery);

			int count = db.queryForObject(countQuery, Integer.class, params.toArray());

			int totalPage = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(totalPage);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), totalPage);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());

			String str_query = String.format(
					"SELECT urd.*, s.service_name, u.fullname, u.phone FROM %s urd "
							+ "JOIN %s s ON urd.service_id = s.id JOIN %s u ON urd.user_id = u.id "
							+ "WHERE (urd.status = 'pending' OR urd.status = 'reviewing') %s "
							+ "ORDER BY create_date DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_USER_REQUEST_DETAILS, Views.TBL_SERVICES, Views.TBL_USER, searchQuery);

			params.add(offset);
			params.add(pageItem.getPageSize());

			return db.queryForList(str_query, params.toArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch all available staffs by status from table staffs
	 * 
	 * @return an amount of staffs
	 */
	public int countAvailableStaff() {
		try {
			String query = "SELECT COUNT(*) FROM staffs WHERE status = 'available'";
			return db.queryForObject(query, Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/***
	 * update status = 'reviewing' that specific staff(s) are from table
	 * user_request_details
	 * 
	 * update status = 'unavailable' of that specific staff(s) from table staffs
	 * 
	 * @return assigned staff(s) into table user_request_details along update both
	 *         tables's status
	 */
	public String assignStaffIntoRequest(int staffId, int urdId) {
		try {
			String request_query = String.format("UPDATE %s SET %s = ?, %s = 'reviewing' WHERE %s = ?",
					Views.TBL_USER_REQUEST_DETAILS, Views.COL_URD_STAFFID, Views.COL_URD_STATUS, Views.COL_URD_ID);

			String staff_query = String.format("UPDATE %s SET %s = 'unavailable' WHERE %s = ?", Views.TBL_STAFFS,
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

	// -------------------- SCHEDULES -------------------- //

	/***
	 * fetch all staffs by status = 'available' from table staffs
	 * 
	 * @return list of available staffs for assigning staff into an order
	 */
	public List<Staff> staffListForAssign(int detailId) {
		try {
			String str_query = String.format(
					"SELECT * FROM %s WHERE %s = ? AND id NOT IN (SELECT staff_id FROM %s WHERE detail_id = ?)",
					Views.TBL_STAFFS, Views.COL_STAFFS_STATUS, Views.TBL_SCHEDULES);
			return db.query(str_query, new Staff_mapper(), new Object[] { "available", detailId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch all staffs by status = 'available' from table staffs
	 * 
	 * @return list of available staffs
	 */
	public String assignStaff(Schedule item) {
		try {
			String str_query = String.format("INSERT INTO %s (staff_id, detail_id, start_date) VALUES (?,?,?)",
					Views.TBL_SCHEDULES);
			String staff_query = String.format("UPDATE %s SET %s = 'unavailable' WHERE %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			int rowaccepted = db.update(str_query,
					new Object[] { item.getStaffId(), item.getDetailId(), item.getStartDate() });
			if (rowaccepted == 1) {
				db.update(staff_query, new Object[] { item.getStaffId() });
				return "success";
			}
			return "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	/***
	 * fetch all assigned staffs from table staffs & schedules
	 * 
	 * @return list of assigned staffs
	 */
	public List<Staff> assignedStaffList(int id) {
		try {
			String str_query = String.format("SELECT s.* FROM %s s JOIN %s ts ON s.id = ts.staff_id WHERE ts.%s = ?",
					Views.TBL_STAFFS, Views.TBL_SCHEDULES, Views.COL_SCHEDULES_DETAIL_ID);
			return db.query(str_query, new Staff_mapper(), new Object[] { id });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch all assigned staffs from table staffs, schedules & order_details in
	 * order to replace
	 * 
	 * @return list of assigned staffs
	 */
	public List<Staff> getCurrentStaff(int detailId) {
		try {
			String str_query = "SELECT s.* FROM staffs s JOIN schedules sd ON s.id = sd.staff_id"
					+ " JOIN order_details od ON sd.detail_id = od.id WHERE od.id = ?";
			return db.query(str_query, new Staff_mapper(), new Object[] { detailId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch all available staffs from table staffs from order to be replaced
	 * 
	 * @return list of available staffs
	 */
	public List<Staff> getAvailableStaffToReplace(int orderId, int excludeStaffId) {
		String query = "SELECT * FROM staffs WHERE id != ? AND id NOT IN (SELECT staff_id FROM schedules WHERE detail_id = ?) "
				+ "AND id NOT IN (select staff_id from user_request_details where staff_id is not null) AND status = 'available'";
		return db.query(query, new Staff_mapper(), new Object[] { excludeStaffId, orderId });
	}

	/***
	 * update staff_id from table schedules
	 * 
	 * @return updated staff_id
	 */
	public String replaceStaff(int currentStaff, int newStaff, int orderId) {
		try {
			String schedule_query = String.format("UPDATE %s SET %s = ? WHERE %s = ? and %s = ?", Views.TBL_SCHEDULES,
					Views.COL_SCHEDULES_STAFF_ID, Views.COL_SCHEDULES_STAFF_ID, Views.COL_SCHEDULES_DETAIL_ID);

			String newstaff_query = String.format("UPDATE %s SET %s = 'unavailable' WHERE %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);

			String oldstaff_query = String.format("UPDATE %s SET %s = 'available' WHERE %s = ?", Views.TBL_STAFFS,
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

	// -------------------- ORDERS -------------------- //

	/***
	 * fetch all from table orders
	 * 
	 * @return list of orders
	 */
	public List<Map<String, Object>> getOrders(PageView pageItem, String search) {
		try {
			List<Object> params = new ArrayList<>();
			String searchQuery = "";

			if (search != null && !search.trim().isEmpty()) {
				searchQuery = " WHERE u.fullname LIKE ? OR u.phone LIKE ? OR od.status LIKE ?";
				params.add("%" + search + "%");
				params.add("%" + search + "%");
				params.add("%" + search + "%");
			}

			String countQuery = String.format(
					"SELECT COUNT(*) FROM %s od JOIN %s o ON od.order_id = o.id JOIN %s ur ON o.usrReq_id = ur.id "
							+ "JOIN %s u ON ur.user_id = u.id JOIN %s s ON od.service_id = s.id %s",
					Views.TBL_ORDER_DETAIL, Views.TBL_ORDER, Views.TBL_USER_REQUEST, Views.TBL_USER, Views.TBL_SERVICES,
					searchQuery);

			int count = db.queryForObject(countQuery, Integer.class, params.toArray());

			int totalPage = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(totalPage);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), totalPage);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());

			String str_query = String.format(
					"SELECT od.*, o.*, u.fullname, u.phone, STRING_AGG(st.username, ', ') AS staff_username, "
							+ "CASE WHEN EXISTS (SELECT 1 FROM schedules s WHERE s.detail_id = od.id) "
							+ "THEN 1 ELSE 0 END AS hasAssignedStaff FROM %s od JOIN %s o ON od.order_id = o.id "
							+ "JOIN %s ur ON o.usrReq_id = ur.id JOIN %s u ON ur.user_id = u.id "
							+ "JOIN %s s ON od.service_id = s.id LEFT JOIN %s urd ON urd.usrReq_id = ur.id "
							+ "LEFT JOIN %s st ON urd.staff_id = st.id %s "
							+ "GROUP BY od.id, od.order_id, od.service_id, od.create_date, od.start_date, od.complete_date, od.status, "
							+ "od.beforeImage, od.afterImage, o.id, o.usrReq_id, od.price, u.fullname, u.phone "
							+ "ORDER BY %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_ORDER_DETAIL, Views.TBL_ORDER, Views.TBL_USER_REQUEST, Views.TBL_USER, Views.TBL_SERVICES,
					Views.TBL_USER_REQUEST_DETAILS, Views.TBL_STAFFS, searchQuery, Views.COL_ORDER_DETAIL_CREATEDATE);

			params.add(offset);
			params.add(pageItem.getPageSize());

			return db.queryForList(str_query, params.toArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch specific order_details by id from table order_details
	 * 
	 * @return a specific order_details
	 */
	public OrderDetail getDetailById(int id) {
		try {
			String str_query = String.format(
					"SELECT * FROM %s od JOIN services s ON od.service_id = s.id WHERE od.%s = ?",
					Views.TBL_ORDER_DETAIL, Views.COL_ORDER_DETAIL_ID);
			return db.queryForObject(str_query, new OrderDetail_mapper(), new Object[] { id });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * update status & new start_date from table order_details
	 * 
	 * @return updated status & start_date
	 */
	public void checkOrderDetailUpToDate() {
		try {
			String str_query = String.format(
					"UPDATE %s SET %s = 'progressing' WHERE start_date = convert(date, getdate()) AND status = 'confirmed'",
					Views.TBL_ORDER_DETAIL, Views.COL_ORDER_DETAIL_STATUS);
			db.update(str_query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// -------------------- SCHEDULE_REQUESTS -------------------- //

	/***
	 * fetch all from table schedule_requests
	 * 
	 * @return list of schedule_requests
	 */
	public List<Map<String, Object>> getRequestList(String search) {
		try {
			List<Object> params = new ArrayList<>();
			String searchQuery = "";

			if (search != null && !search.trim().isEmpty()) {
				searchQuery = " AND (st.fullname LIKE ? OR st.phone LIKE ? OR schr.status LIKE ?)";
				params.add("%" + search + "%");
				params.add("%" + search + "%");
				params.add("%" + search + "%");
			}

			String str_query = String.format(
					"SELECT schr.*, schr.id AS schrId, schr.status AS schrStatus, sc.staff_id AS oldStaff, sc.start_date, st.fullname, st.phone "
							+ "FROM %s schr JOIN %s sc ON schr.schedule_id = sc.id JOIN %s st ON sc.staff_id = st.id "
							+ "WHERE schr.status = 'pending' %s ORDER BY create_date",
					Views.TBL_SCHEDULE_REQUESTS, Views.TBL_SCHEDULES, Views.TBL_STAFFS, searchQuery);

			return db.queryForList(str_query, params.toArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * update new staff into table schedules update both old & new staff's status
	 * from table staffs update schedule_request's status from table
	 * schedule_requests
	 * 
	 * @return updated fields from schedules, staffs & schedule_requests
	 */
	@Transactional
	public String approveCancelRequest(int newStaffId, int schrId, int oldStaffId) {
		try {
			String updateScheduleQuery = "UPDATE schedules SET staff_id = ? FROM schedules sch "
					+ "JOIN schedule_requests schr ON sch.id = schr.schedule_id WHERE schr.id = ?";

			int rowsUpdated = db.update(updateScheduleQuery, newStaffId, schrId);
			if (rowsUpdated == 1) {
				String updateOldStaffQuery = "UPDATE staffs SET status = 'available' WHERE id = ?";
				db.update(updateOldStaffQuery, oldStaffId);

				String updateNewStaffQuery = "UPDATE staffs SET status = 'unavailable' WHERE id = ?";
				db.update(updateNewStaffQuery, newStaffId);

				String updateScheduleRequestQuery = "UPDATE schedule_requests SET status = 'approved' WHERE id = ?";
				db.update(updateScheduleRequestQuery, schrId);

				return "success";
			}
			return "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	/***
	 * update new staff for table schedules update both old & new staff's status for
	 * table staffs update schedule_request's status from for schedule_requests
	 * 
	 * @return updated fields for schedules, staffs & schedule_requests
	 */
	public String approveDateRequest(int scheduleId, Date newDate, int schrId) {
		try {
			String sche_query = "UPDATE schedules SET start_date = ? WHERE id = ?";
			String sr_query = "UPDATE schedule_requests SET status = ? WHERE id = ?";
			Integer orderId = db.queryForObject(
					"SELECT od.id FROM schedules sch JOIN order_details od ON sch.detail_id = od.id WHERE sch.id = ?",
					Integer.class, scheduleId);

			if (orderId == null) {
				return "failed";
			}

			String order_query = "UPDATE order_details SET start_date = ? WHERE id = ?";
			int rowaccepted = db.update(sche_query, new Object[] { newDate, scheduleId });
			if (rowaccepted == 1) {
				db.update(sr_query, new Object[] { "approved", schrId });
				db.update(order_query, new Object[] { newDate, orderId });
				return "success";
			}
			return "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	/***
	 * update status for table schedule_requests
	 * 
	 * @return updated status = 'denied'
	 */
	public String denyRequest(int scheduleRequestId) {
		try {
			String str_query = String.format("UPDATE %s SET %s = 'denied' WHERE %s = ?", Views.TBL_SCHEDULE_REQUESTS,
					Views.COL_SCHEDULE_REQUESTS_STATUS, Views.COL_SCHEDULE_REQUESTS_ID);

			int rowaccepted = db.update(str_query, new Object[] { scheduleRequestId });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

}
