package pack.repositories;

import java.sql.Date;
import java.util.ArrayList;
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
import pack.models.UserRequestDetail;
import pack.modelviews.OrderDetail_mapper;
import pack.modelviews.Service_mapper;
import pack.modelviews.UserRequestDetail_mapper;
import pack.modelviews.User_mapper;
import pack.utils.SecurityUtility;
import pack.utils.Views;

@Repository
public class UserRepository {

	@Autowired
	JdbcTemplate db;

	// -------------------- ACCOUNTS -------------------- //

	public User findUserByUsername(String username) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_USER, Views.COL_USER_USERNAME);
			return db.queryForObject(str_query, new User_mapper(), new Object[] { username });
		} catch (Exception e) {
			return null;
		}
	}

	public User findUserById(int id) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_USER, Views.COL_USER_ID);
			return db.queryForObject(str_query, new User_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public String newUser(User user) {
		try {
			String str_query = String.format(
					"insert into %s (username, password, email, phone, fullname) values (?,?,?,?,?)", Views.TBL_USER);
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

	public User checkEmailExists(String email) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_USER, Views.COL_USER_EMAIL);
			return db.queryForObject(str_query, new User_mapper(), new Object[] { email });
		} catch (Exception e) {
			return null;
		}
	}

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

	public List<Service> getServices() {
		try {
			String str_query = String.format("SELECT * FROM %s", Views.TBL_SERVICES);
			return db.query(str_query, new Service_mapper());
		} catch (Exception e) {
			return null;
		}
	}

	public Service getServiceById(int id) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s=?", Views.TBL_SERVICES, Views.COL_SERVICES_ID);
			return db.queryForObject(str_query, new Service_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	// -------------------- ORDERS -------------------- //

	public boolean countOrdersToBrowseMore(int userId) {
		try {
			// Đếm số lượng order_details theo user_id
			String countQuery = "SELECT COUNT(*) FROM order_details od JOIN orders o ON od.order_id = o.id "
					+ "JOIN user_requests ur ON o.usrReq_id = ur.id JOIN users u ON ur.user_id = u.id WHERE u.id = ?";
			int count = db.queryForObject(countQuery, Integer.class, new Object[] { userId });

			return count > 4;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Map<String, Object>> getOrdersHistory(int userId) {
		try {
			String str_query = "SELECT o.id AS orderId, od.start_date AS startDate, od.status, s.service_name AS serName "
					+ "FROM services s JOIN order_details od ON s.id = od.service_id JOIN orders o ON od.order_id = o.id "
					+ "JOIN user_requests ur ON o.usrReq_id = ur.id JOIN users u ON ur.user_id = u.id WHERE u.id = ?";
			return db.queryForList(str_query, new Object[] { userId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Map<String, Object>> getAllOrdersHistory(PageView pageItem, int userId) {
		try {
			int count = db.queryForObject("SELECT COUNT(*) FROM order_details", Integer.class);
			int total_page = (int) Math.ceil((double) count / pageItem.getPageSize());
			pageItem.setTotalPage(total_page);

			String str_query = "SELECT o.id AS orderId, od.start_date AS startDate, od.status, s.service_name AS serName "
					+ "FROM services s JOIN order_details od ON s.id = od.service_id JOIN orders o ON od.order_id = o.id "
					+ "JOIN user_requests ur ON o.usrReq_id = ur.id JOIN users u ON ur.user_id = u.id "
					+ "WHERE u.id = ? ORDER BY od.start_date ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
			return db.queryForList(str_query, new Object[] { userId,
					(pageItem.getPageCurrent() - 1) * pageItem.getPageSize(), pageItem.getPageSize() });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Map<String, Object>> getSeeMoreOrderDetails(int orderId) {
		try {
			String str_query = "SELECT o.id AS orderId, od.price, od.start_date AS startDate, od.complete_date AS completeDate, "
					+ "od.status, s.service_name AS serName, st.username AS staffsName FROM orders o "
					+ "JOIN order_details od ON o.id = od.order_id JOIN services s ON od.service_id = s.id "
					+ "LEFT JOIN schedules sch ON od.id = sch.detail_id LEFT JOIN staffs st ON sch.staff_id = st.id "
					+ "WHERE o.id = ?";
			return db.queryForList(str_query, new Object[] { orderId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Map<String, Object>> getOrderDetails(int userId) {
		try {
			String str_query = "SELECT od.id AS detailId, o.id AS orderId, s.service_name AS serName, od.price, od.start_date AS startDate, od.status "
					+ "FROM services s JOIN order_details od ON s.id = od.service_id JOIN orders o ON od.order_id = o.id "
					+ "JOIN user_requests ur ON o.usrReq_id = ur.id JOIN users u ON ur.user_id = u.id WHERE u.id = ?";
			return db.queryForList(str_query, new Object[] { userId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// ORDER - HIẾU
	public List<UserRequestDetail> getUserReqDetailById(int userId) {
		try {
			String str_query = String.format(
					"SELECT urd.*, s.service_name FROM %s urd join %s ur on urd.usrReq_id = ur.id join %s s on urd.service_id = s.id WHERE urd.user_id = ? and urd.status != 'canceled'",
					Views.TBL_USER_REQUEST_DETAILS, Views.TBL_USER_REQUEST, Views.TBL_SERVICES);
			return db.query(str_query, new UserRequestDetail_mapper(), new Object[] { userId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public OrderDetail getOrderDetailById(int requestId) {
		try {
			String str_query = String.format(
					"SELECT ord.*, s.service_name FROM %s ord join %s od on ord.order_id = od.id join %s s on ord.service_id = s.id WHERE od.usrReq_id = ?",
					Views.TBL_ORDER_DETAIL, Views.TBL_ORDER, Views.TBL_SERVICES);
			return db.queryForObject(str_query, new OrderDetail_mapper(), new Object[] { requestId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<OrderDetail> getOrderDetailList() {
		try {
			String str_query = String.format(
					"SELECT s.service_name, ord.* FROM %s ord join %s s on ord.service_id = s.id",
					Views.TBL_ORDER_DETAIL, Views.TBL_SERVICES);
			return db.query(str_query, new OrderDetail_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<OrderDetail> getOrderDetailsForAccount(int usrId) {
		try {
			String str_query = String.format(
					"SELECT top 5 ord.*, s.service_name FROM %s ord " + "join %s o on ord.order_id = o.id "
							+ "join user_requests ur on o.usrReq_id = ur.id " + "join %s s on ord.service_id = s.id "
							+ "WHERE ur.user_id = ? " + "order by ord.create_date desc",
					Views.TBL_ORDER_DETAIL, Views.TBL_ORDER, Views.TBL_SERVICES);
			return db.query(str_query, new OrderDetail_mapper(), new Object[] { usrId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

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

	public boolean isServiceInRequest(int userId, int serId) {
		String str_query = String.format("SELECT count(*) FROM %s WHERE %s = ? and %s = ? and %s != 'canceled'",
				Views.TBL_USER_REQUEST_DETAILS, Views.COL_URD_USRID, Views.COL_URD_SERID, Views.COL_URD_STATUS);
		Integer count = db.queryForObject(str_query, Integer.class, new Object[] { userId, serId });
		return count != null && count > 0;
	}

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
}