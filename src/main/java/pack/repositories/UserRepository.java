package pack.repositories;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pack.IService.TokenInterface;
import pack.models.OrderDetail;
import pack.models.PageView;
import pack.models.Service;
import pack.models.TokenRecord;
import pack.models.User;
import pack.modelviews.OrderDetail_mapper;
import pack.modelviews.Service_mapper;
import pack.modelviews.User_mapper;
import pack.services.EmailService;
import pack.utils.SecurityUtility;
import pack.utils.Views;

@Repository
public class UserRepository {

	@Autowired
	private JdbcTemplate db;

	@Autowired
	private EmailService emailService;

	@Autowired
	private TokenInterface tokenService;

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
	@Transactional
	public String saveUser(User user) {
		try {
			String sql = "INSERT INTO users (fullname, username, password, email, phone) VALUES (?, ?, ?, ?, ?)";
			String hashPassword = SecurityUtility.encryptBcrypt(user.getPassword());

			int rowaccepted = db.update(sql, user.getFullname(), user.getUsername(), hashPassword, user.getEmail(),
					user.getPhone());

			String token = UUID.randomUUID().toString();
			LocalDateTime expirationTime = LocalDateTime.now().plusHours(3);
			tokenService.saveUserToken(token, expirationTime, user.getEmail());

			String verificationLink = "http://localhost:8080/user/verify?token=" + token;
			String emailText = String.format(
					"Dear %s,\n\nThank you for registering. Please click the link below to verify your account:\n%s\n\n"
							+ "This link will expire in 3 hours.\n\nBest regards,\nCleanex Team",
					user.getFullname(), verificationLink);

			emailService.SendMail(user.getEmail(), "Verify Your Cleanex Account", emailText);
			return rowaccepted == 1 ? "success" : "failed";
		} catch (DuplicateKeyException e) {
			throw new IllegalArgumentException("Some information(username, email, phone) may already exists.");
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	public boolean verifyUser(String token) {
		try {
			TokenRecord tokenRecord = tokenService.findUserToken(token);

			if (tokenRecord == null || tokenRecord.getExpirationTime().isBefore(LocalDateTime.now())
					|| tokenRecord.isUsed()) {
				return false;
			}

			tokenService.markTokenAsUsed(token);

			String updateUserSql = "UPDATE users SET is_verified = 1 WHERE email = ?";
			int updatedRows = db.update(updateUserSql, tokenRecord.getEmail());

			return updatedRows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/***
	 * update account from table users
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
				queryBuilder.append("image = ?, ");
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
					"SELECT top 5 ord.*, s.service_name FROM %s ord " + "join %s o on ord.order_id = o.id "
							+ "join user_requests ur on o.usrReq_id = ur.id " + "join %s s on ord.service_id = s.id "
							+ "WHERE ur.user_id = ? " + "order by ord.create_date desc",
					Views.TBL_ORDER_DETAIL, Views.TBL_ORDER, Views.TBL_SERVICES);
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
	public List<Map<String, Object>> getAllOrderDetailsForAccount(PageView pageItem, String userId) {
		try {
			int count = db.queryForObject("SELECT COUNT(*) FROM order_details", Integer.class);
			int total_page = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(total_page);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), total_page);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());

			String str_query = "SELECT o.id, od.start_date, od.status, s.service_name FROM services s "
					+ "JOIN order_details od ON s.id = od.service_id JOIN orders o ON od.order_id = o.id "
					+ "JOIN user_requests ur ON o.usrReq_id = ur.id JOIN users u ON ur.user_id = u.id "
					+ "WHERE u.id = ? ORDER BY od.start_date OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
			return db.queryForList(str_query, offset, new Object[] { userId }, pageItem.getPageSize());
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
	public List<Map<String, Object>> getOrderDetailsForAccount(int id) {
		try {
			String str_query = "select ord.*, s.service_name from order_details ord join services s on ord.service_id = s.id where ord.id = ?";
			return db.queryForList(str_query, new Object[] { id });
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
	public List<Map<String, Object>> getUserRequestDetailById(PageView pageItem, int userId) {
		try {
			String countQuery = String.format(
					"SELECT COUNT(*) FROM %s urd JOIN %s ur ON urd.usrReq_id = ur.id JOIN %s s ON urd.service_id = s.id "
							+ "WHERE urd.user_id = ? AND urd.status != 'canceled'",
					Views.TBL_USER_REQUEST_DETAILS, Views.TBL_USER_REQUEST, Views.TBL_SERVICES);
			int count = db.queryForObject(countQuery, new Object[] { userId }, Integer.class);

			int totalPage = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(totalPage);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), totalPage);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());
			int limit = pageItem.getPageSize();

			String str_query = String.format(
					"SELECT urd.*, s.id AS serId, s.service_name, s.base_price FROM %s urd JOIN %s ur ON urd.usrReq_id = ur.id "
							+ "JOIN %s s ON urd.service_id = s.id WHERE urd.user_id = ? AND urd.status != 'canceled' "
							+ "ORDER BY urd.id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_USER_REQUEST_DETAILS, Views.TBL_USER_REQUEST, Views.TBL_SERVICES);
			return db.queryForList(str_query, new Object[] { userId, offset, limit });
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
	public List<Map<String, Object>> getOrderDetails(int orderId) {
		try {
			String str_query = String.format(
					"SELECT od.*, s.service_name FROM %s od JOIN %s s ON od.service_id = s.id WHERE od.id = ?",
					Views.TBL_ORDER_DETAIL, Views.TBL_SERVICES);
			return db.queryForList(str_query, orderId);
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
	 * update status from table user_request_details
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
	 * update status from table user_request_details
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
	 * update status from table staffs
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
	 * update status = 'completed' from table order_details update status =
	 * 'available'in table staffs
	 * 
	 * @return updated order_details & staffs status
	 */
	public String orderApprove(int detailId) {
		try {
			String str_query = "update order_details set status = 'completed' where id = ?";
			String staffs_query = "update staffs set status = 'available' where id in (select s.staff_id from schedules s where s.detail_id = ?)";

			int rowaccepted = db.update(str_query, new Object[] { detailId });
			if (rowaccepted == 1) {
				db.update(staffs_query, new Object[] { detailId });
				return "success";
			}
			return "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	/***
	 * update status = 'progressing' from table order_details
	 * 
	 * @return updated order_details status
	 */
	public String orderDecline(int detailId) {
		try {
			int rowaccepted = db.update("update order_details set status = 'progressing' where id = ?",
					new Object[] { detailId });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	public List<Service> serviceListForChange(int userId) {
		try {
			String str_query = String.format(
					"select * from %s where id not in (SELECT service_id FROM %s WHERE user_id = ? and status != 'canceled' ) and status != 'disabled'",
					Views.TBL_SERVICES, Views.TBL_USER_REQUEST_DETAILS);
			return db.query(str_query, new Service_mapper(), new Object[] { userId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String changeService(int oldSerId, int newSerId, int userId) {
		try {
			String str_query = "update user_request_details set service_id = ? where user_id = ? and service_id = ? and status = 'pending'";
			int rowaccepted = db.update(str_query, new Object[] { newSerId, userId, oldSerId });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	public String changePass(String username, String password) {
		try {
			String str_query = "update users set password = ? where username = ?";
			String hashPass = SecurityUtility.encryptBcrypt(password);
			int rowaccepted = db.update(str_query, new Object[] { hashPass, username });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

}