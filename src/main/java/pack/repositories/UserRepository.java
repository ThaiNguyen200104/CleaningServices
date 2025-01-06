package pack.repositories;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.OrderDetail;
import pack.models.PageView;
import pack.models.Service;
import pack.models.User;
import pack.modelviews.OrderDetail_mapper;
import pack.modelviews.Service_mapper;
import pack.modelviews.User_mapper;
import pack.utils.SecurityUtility;
import pack.utils.Views;

@Repository
public class UserRepository {

	@Autowired
	JdbcTemplate db;

	// -------------------- ACCOUNTS -------------------- //

	/***
	 * fetch data by id from table users
	 * 
	 * @return user
	 */
	public User findUserById(int id) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_USER, Views.COL_USER_ID);
			return db.queryForObject(str_query, new User_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * fetch data by username from table users
	 * 
	 * @return user
	 */
	public User findUserByUsername(String username) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_USER, Views.COL_USER_USERNAME);
			return db.queryForObject(str_query, new User_mapper(), new Object[] { username });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * fetch data by email from table users
	 * 
	 * @return user
	 */
	public User checkEmailExists(String email) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_USER, Views.COL_USER_EMAIL);
			return db.queryForObject(str_query, new User_mapper(), new Object[] { email });
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * insert data into table users
	 * 
	 * @return new user account
	 */
	public String newUser(User user) {
		try {
			String str_query = String.format(
					"INSERT INTO %s (username, password, email, phone, fullname) VALUES (?,?,?,?,?)", Views.TBL_USER);
			String hashPassword = SecurityUtility.encryptBcrypt(user.getPassword());
			int rowaccept = db.update(str_query, new Object[] { user.getUsername(), hashPassword, user.getEmail(),
					user.getPhone(), user.getFullname() });
			return rowaccept == 1 ? "success" : "failed";
		} catch (DuplicateKeyException e) {
			throw new IllegalArgumentException("Some information(username, email, phone) may already exists.");
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	/***
	 * update account in table users
	 * 
	 * @return updated user's account
	 */
	public String editProfile(User user) {
		try {
			StringBuilder queryBuilder = new StringBuilder("UPDATE " + Views.TBL_USER + " SET ");
			List<Object> params = new ArrayList<>();

			if (user.getFullname() != null && !user.getFullname().isEmpty()) {
				queryBuilder.append("fullname = ?, ");
				params.add(user.getFullname());
			}
			if (user.getPassword() != null && !user.getPassword().isEmpty()) {
				queryBuilder.append("password = ?, ");
				String hashPassword = SecurityUtility.encryptBcrypt(user.getPassword());
				params.add(hashPassword);
			}
			if (user.getEmail() != null && !user.getEmail().isEmpty()) {
				queryBuilder.append("email = ?, ");
				params.add(user.getEmail());
			}
			if (user.getPhone() != null && !user.getPhone().isEmpty()) {
				queryBuilder.append("phone = ?, ");
				params.add(user.getPhone());
			}
			if (user.getAddress() != null && !user.getAddress().isEmpty()) {
				queryBuilder.append("address = ?, ");
				params.add(user.getAddress());
			}
			if (user.getImage() != null && !user.getImage().isEmpty()) {
				queryBuilder.append("images = ?, ");
				params.add(user.getImage());
			}

			if (params.isEmpty()) {
				return "No field needs to update.";
			}

			queryBuilder.setLength(queryBuilder.length() - 2);
			queryBuilder.append(" WHERE " + Views.COL_USER_ID + " = ?");
			params.add(user.getId());

			int rowsAffected = db.update(queryBuilder.toString(), params.toArray());
			return rowsAffected == 1 ? "success" : "failed";
		} catch (DuplicateKeyException e) {
			throw new IllegalArgumentException("Some information(username, email, phone) may already exists.");
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	// -------------------- SERVICES -------------------- //

	/***
	 * fetch data from table services
	 * 
	 * @return list of services
	 */
	public List<Service> getServices() {
		try {
			String str_query = String.format("SELECT * FROM %s", Views.TBL_SERVICES);
			return db.query(str_query, new Service_mapper());
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * fetch data by id from table services
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

	// -------------------- ORDERS -------------------- //

	/***
	 * fetch top 5 specific data from table order_details and services
	 * 
	 * @return order list for accounts.html
	 */
	public List<OrderDetail> getOrdersForAccount(int usrId) {
		try {
			String str_query = String.format(
					"SELECT TOP 5 od.*, s.service_name AS serName FROM %s s JOIN %s od ON s.id = od.service_id "
							+ "JOIN %s o ON od.order_id = o.id JOIN %s ur ON o.usrReq_id = ur.id "
							+ "WHERE ur.user_id = ? ORDER BY od.create_date DESC",
					Views.TBL_SERVICES, Views.TBL_ORDER_DETAIL, Views.TBL_ORDER, Views.TBL_USER_REQUEST);
			return db.query(str_query, new OrderDetail_mapper(), new Object[] { usrId });
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	/***
	 * fetch data from table orders, order_details and services
	 * 
	 * @return order list for browse_more.html
	 */
	public List<Map<String, Object>> getAllOrderDetailsForAccount(PageView pageItem, int userId) {
		try {
			int count = db.queryForObject("SELECT COUNT(*) FROM order_details", Integer.class);
			int total_page = (int) Math.ceil((double) count / pageItem.getPageSize());
			pageItem.setTotalPage(total_page);

			String str_query = "SELECT o.id, od.start_date, od.status, s.service_name FROM services s "
					+ "JOIN order_details od ON s.id = od.service_id JOIN orders o ON od.order_id = o.id "
					+ "JOIN user_requests ur ON o.usrReq_id = ur.id JOIN users u ON ur.user_id = u.id "
					+ "WHERE u.id = ? ORDER BY od.start_date OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
			return db.queryForList(str_query, new Object[] { userId,
					(pageItem.getPageCurrent() - 1) * pageItem.getPageSize(), pageItem.getPageSize() });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


  /***
	 * fetch data from table services, staffs and order_details
	 * 
	 * @return order list for see_more.html
	 */
	public List<Map<String, Object>> getOrderDetailsForAccount(int usrReqId) {
		try {
			String str_query = String.format(
					"SELECT od.*, s.service_name FROM %s od JOIN %s s ON s.id = od.service_id JOIN %s sch ON sch.detail_id = od.id "
							+ "JOIN %s st ON sch.staff_id = st.id WHERE od.id = ? ORDER BY od.create_date DESC",
					Views.TBL_SERVICES, Views.TBL_ORDER_DETAIL, Views.TBL_SCHEDULES, Views.TBL_STAFFS);
			return db.queryForList(str_query, new Object[] { usrReqId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch data from table user_request_details and services
	 * 
	 * @return order list for orders.html
	 */
	public List<Map<String, Object>> getUserRequestDetailById(int userId) {
		try {
			String str_query = String.format(
					"SELECT urd.*, urd.price, s.service_name, s.base_price FROM %s urd JOIN %s ur ON urd.usrReq_id = ur.id "
							+ "JOIN %s s ON urd.service_id = s.id WHERE urd.user_id = ? AND urd.status != 'canceled'",
					Views.TBL_USER_REQUEST_DETAILS, Views.TBL_USER_REQUEST, Views.TBL_SERVICES);
			return db.queryForList(str_query, new Object[] { userId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch data from table user_request_details, order_details and services
	 * 
	 * @return order list for order_details.html
	 */
	public List<Map<String, Object>> getOrderDetails(int userId) {
		try {
			String str_query = String.format(
					"SELECT urd.*, urd.price, s.service_name, s.base_price, od.before_image, od.after_image FROM %s urd "
							+ "JOIN %s ur ON urd.usrReq_id = ur.id JOIN %s o ON ur.id = o.usrReq_id JOIN %s od ON o.id = od.order_id "
							+ "JOIN %s s ON urd.service_id = s.id WHERE urd.user_id = ? AND urd.status != 'canceled'",
					Views.TBL_USER_REQUEST_DETAILS, Views.TBL_USER_REQUEST, Views.TBL_ORDER, Views.TBL_ORDER_DETAIL,
					Views.TBL_SERVICES);
			return db.queryForList(str_query, new Object[] { userId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * insert data into table orders
	 * 
	 * @return a new start_date for orders
	 */
	public String newOrder(int urdId, int serId, Date startDate, double price) {
		try {
			String order_query = String.format("INSERT INTO %s OUTPUT INSERTED.id VALUES (?)", Views.TBL_ORDER);
			Integer order_id = db.queryForObject(order_query, Integer.class, new Object[] { urdId });

			if (order_id != null) {
				String orderDetail_query = String.format(
						"INSERT INTO %s (order_id, service_id, start_date, price) VALUES (?,?,?,?)",
						Views.TBL_ORDER_DETAIL);
				int rowsAffectedOrderDetail = db.update(orderDetail_query,
						new Object[] { order_id, serId, startDate, price });

				return rowsAffectedOrderDetail == 1 ? "success" : "failed";
			} else {
				System.err.println("Order ID retrieval failed.");
				return "failed";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * update status in table user_request_details
	 * 
	 * @return updated status = 'canceled'
	 */
	public String cancelOrder(int requestId) {
		try {
			String str_query = String.format("UPDATE %s SET %s = 'canceled' WHERE %s = ?",
					Views.TBL_USER_REQUEST_DETAILS, Views.COL_URD_STATUS, Views.COL_URD_ID);
			int rowaccept = db.update(str_query, new Object[] { requestId });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return "failed";
		}
	}

	/***
	 * update status in table user_request_details
	 * 
	 * @return updated status = 'confirmed'
	 */
	public String confirmOrder(int requestId) {
		try {
			String str_query = String.format("UPDATE %s SET %s = 'confirmed' WHERE %s = ?",
					Views.TBL_USER_REQUEST_DETAILS, Views.COL_URD_STATUS, Views.COL_URD_ID);
			int accepted = db.update(str_query, new Object[] { requestId });
			return accepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * update status in table staffs
	 * 
	 * @return updated status = 'available'
	 */
	public String updateStaffStatusToAvailable(int staffId) {
		try {
			String str_query = String.format("UPDATE %s SET %s = 'available' WHERE %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			int rowaccepted = db.update(str_query, new Object[] { staffId });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	/***
	 * fetch data from table user_request_details
	 * 
	 * @return a specific service
	 */
	public boolean isServiceInRequest(int userId, int serId) {
		String str_query = String.format("SELECT count(*) FROM %s WHERE %s = ? and %s = ? and %s != 'canceled'",
				Views.TBL_USER_REQUEST_DETAILS, Views.COL_URD_USRID, Views.COL_URD_SERID, Views.COL_URD_STATUS);
		Integer count = db.queryForObject(str_query, Integer.class, new Object[] { userId, serId });
		return count != null && count > 0;
	}

	/***
	 * insert data into user_request_details
	 * 
	 * @return new user_request_details
	 */
	public String newRequestDetail(int userId, int serId, Date startDate, double price) {
		try {
			String ur_query = String.format("INSERT INTO %s OUTPUT INSERTED.id VALUES (?)", Views.TBL_USER_REQUEST);
			Integer ur_id = db.queryForObject(ur_query, Integer.class, new Object[] { userId });

			if (ur_id != null) {
				String requestDetail_query = String.format(
						"INSERT INTO %s (usrReq_id, user_id, service_id, price, start_date) VALUES (?,?,?,?,?)",
						Views.TBL_USER_REQUEST_DETAILS);
				int rowsAffectedOrderDetail = db.update(requestDetail_query,
						new Object[] { ur_id, userId, serId, price, startDate });

				return rowsAffectedOrderDetail == 1 ? "success" : "failed";
			} else {
				System.err.println("userRequestId retrieval failed.");
				return "failed";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}
	
  /***
	 * update status = 'completed' in table order_details
	 * update status = 'available'in table staffs
	 * 
	 * @return updated order_details & staffs status
	 */
	public String orderApprove(int detailId) {
		try {
			String str_query = "update order_details set status = 'completed' where id = ?";
			String staffs_query = "update staffs set status = 'available' where id in (select s.staff_id from schedules s where s.detail_id = ?)";
			int rowaccepted = db.update(str_query, new Object[] {detailId});
			if(rowaccepted == 1) {
				db.update(staffs_query, new Object[] {detailId});
				return "success";
			}
			return "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}
	
  /***
	 * update status = 'progressing' in table order_details
	 * 
	 * @return updated order_details status
	 */
	public String orderDecline(int detailId) {
		try {
			int rowaccepted = db.update("update order_details set status = 'progressing' where id = ?", new Object[] {detailId});
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}
}