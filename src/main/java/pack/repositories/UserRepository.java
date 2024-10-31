package pack.repositories;

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
import pack.modelviews.Detail_mapper;
import pack.modelviews.Order_mapper;
import pack.modelviews.Service_mapper;
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

	public List<Order> getOrderList(int id) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_ORDER, Views.COL_ORDERS_USER_ID);
			return db.query(str_query, new Order_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public String newOrder(Order order) {
		try {
			String str_query = String.format(
					"insert into %s (user_id, price, expected_startDate, status) values (?,?,?,?)", Views.TBL_ORDER);
			int rowaccept = db.update(str_query,
					new Object[] { order.getUsrId(), order.getPrice(), order.getStartDate(), order.getStatus() });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	public List<OrderDetail> getDetailList(int id) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_ORDER_DETAIL,
					Views.COL_ORDERS_ID);
			return db.query(str_query, new Detail_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public String newDetail(OrderDetail detail) {
		try {
			String str_query = String.format(
					"insert into %s (order_id, service_id, detail_code, price, start_date, complete_date, status) values (?,?,?,?,?)",
					Views.TBL_ORDER_DETAIL);
			int rowaccept = db.update(str_query, new Object[] { detail.getOrderId(), detail.getSerId(),
					detail.getPrice(), detail.getStartDate(), detail.getStatus() });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

}