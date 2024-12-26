package pack.repositories;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.Order;
import pack.models.OrderDetail;
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

	// SERVICE
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

	// ORDER
	public List<UserRequestDetail> getUserReqDetailById(int userId) {
		try {
			String str_query = String.format(
					"select urd.*, s.service_name from %s urd join %s ur on urd.usrReq_id = ur.id join %s s on urd.service_id = s.id where urd.user_id = ? and urd.status != 'canceled'",
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
					"select ord.*, s.service_name from %s ord join %s od on ord.order_id = od.id join %s s on ord.service_id = s.id where od.usrReq_id = ?",
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
					"select s.service_name, ord.* from %s ord join %s s on ord.service_id = s.id",
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
					"select top 5 ord.*, s.service_name from %s ord " + "join %s o on ord.order_id = o.id "
							+ "join user_requests ur on o.usrReq_id = ur.id " + "join %s s on ord.service_id = s.id "
							+ "where ur.user_id = ? " + "order by ord.create_date desc",
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
			String str_query = String.format("update %s set %s = 'canceled' where %s = ?",
					Views.TBL_USER_REQUEST_DETAILS, Views.COL_URD_STATUS, Views.COL_URD_ID);
			int rowaccept = db.update(str_query, new Object[] { requestId });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return "failed";
		}
	}

	public String confirmOrder(int requestId) {
		try {
			String str_query = String.format("update %s set %s = 'confirmed' where %s = ?",
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
			String str_query = String.format("update %s set %s = 'available' where %s = ?", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			int rowaccepted = db.update(str_query, new Object[] { staffId });
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	public boolean isServiceInRequest(int userId, int serId) {
		String str_query = String.format("select count(*) from %s where %s = ? and %s = ? and %s != 'canceled'",
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