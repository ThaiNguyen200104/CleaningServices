package pack.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.PageView;
import pack.models.Service;
import pack.models.User;
import pack.modelviews.Service_mapper;
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
			String str_query = String.format("select * from %s where %s=?", Views.TBL_USER, Views.COL_USER_USERNAME);
			return db.queryForObject(str_query, new User_mapper(), new Object[] { username });
		} catch (Exception e) {
			return null;
		}
	}

	public User findUserById(int id) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_USER, Views.COL_USER_ID);
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
			String str_query = String.format("select * from %s where %s=?", Views.TBL_USER, Views.COL_USER_EMAIL);
			return db.queryForObject(str_query, new User_mapper(), new Object[] { email });
		} catch (Exception e) {
			return null;
		}
	}

	public String editProfile(User user) {
		try {
			StringBuilder queryBuilder = new StringBuilder("update " + Views.TBL_USER + " set ");
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
			queryBuilder.append(" where " + Views.COL_USER_ID + " = ?");
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
			String str_query = String.format("select * from %s", Views.TBL_SERVICES);
			return db.query(str_query, new Service_mapper());
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

//	public String newOrder(Order item, int serId, Date startDate, double price) {
//		try {
//			// Insert into order table
//			String order_query = String.format("INSERT INTO %s OUTPUT INSERTED.id VALUES (?)", Views.TBL_ORDER);
//			Integer order_id = db.queryForObject(order_query, Integer.class, new Object[] { item.getUsrId() });
//
//			if (order_id != null) {
//				// Generate unique detail code
//				String detail_code = new Random().ints(10, 0, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length())
//						.mapToObj(i -> "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(i))
//						.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
//
//				// Insert into order_details table
//				String orderDetail_query = String.format(
//						"INSERT INTO %s (order_id, service_id, start_date, detail_code, price) VALUES (?,?,?,?,?)",
//						Views.TBL_ORDER_DETAIL);
//				int rowsAffectedOrderDetail = db.update(orderDetail_query,
//						new Object[] { order_id, serId, startDate, detail_code, price });
//
//				return rowsAffectedOrderDetail == 1 ? "success" : "failed";
//			} else {
//				System.err.println("Order ID retrieval failed.");
//				return "failed";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	public boolean isServiceInOrder(int userId, int serId) {
		String str_query = "SELECT COUNT(*) FROM orders o " + "JOIN order_details od ON o.id = od.order_id "
				+ "WHERE o.user_id = ? AND od.service_id = ?";

		Integer count = db.queryForObject(str_query, Integer.class, new Object[] { userId, serId });
		return count != null && count > 0;
	}

	public String cancelOrder(int id) {
		try {
			String str_query = String.format("update %s set %s = 'canceled' where %s = ?", Views.TBL_ORDER_DETAIL,
					Views.COL_ORDER_DETAIL_STATUS, Views.COL_ORDER_DETAIL_ID);
			int rowaccept = db.update(str_query, new Object[] { id });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return null;
		}
	}

	public String confirmOrder(int detailId) {
		try {
			String str_query = String.format("update %s set %s = 'confirmed' where %s = ?", Views.TBL_ORDER_DETAIL,
					Views.COL_ORDER_DETAIL_STATUS, Views.COL_ORDER_DETAIL_ID);
			int accepted = db.update(str_query, new Object[] { detailId });
			return accepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}