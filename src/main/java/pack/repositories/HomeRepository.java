package pack.repositories;

import java.sql.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.Order;
import pack.models.Service;
import pack.modelviews.Service_mapper;
import pack.utils.Views;

@Repository
public class HomeRepository {
	@Autowired
	JdbcTemplate db;

	public List<Service> getServices() {
		try {
			String str_query = String.format("select * from %s", Views.TBL_SERVICES);
			return db.query(str_query, new Service_mapper());
		} catch (Exception e) {
			return null;
		}
	}

	public String newOrder(Order item, int serviceId, Date startDate) {
		try {
			String str_query = String.format("INSERT INTO %s (user_id) VALUES (?)", Views.TBL_ORDER);
			int rowsAffectedOrder = db.update(str_query, new Object[] {item.getUsrId()});
			String detailCode = new Random().ints(10, 0, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length())
			        .mapToObj(i -> "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(i))
			        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
			        .toString();
			
			if (rowsAffectedOrder == 1) {
				Integer orderId = db.queryForObject("SELECT SCOPE_IDENTITY()", Integer.class);

				if (orderId != null) {
					String orderDetailQuery = String.format("INSERT INTO %s (order_id, service_id, start_date, detail_code) VALUES (?, ?, ?, ?)",
							Views.TBL_ORDER_DETAIL);
					int rowsAffectedOrderDetail = db.update(orderDetailQuery, new Object[] {orderId, serviceId, startDate, detailCode});

					return rowsAffectedOrderDetail == 1 ? "success" : "failed";
				}
			}
			return "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isServiceInOrder(int userId, int serviceId) {
	    String sql = "SELECT COUNT(*) FROM orders o " +
	                 "JOIN order_details od ON o.id = od.order_id " +
	                 "WHERE o.user_id = ? AND od.service_id = ?";
	    
	    Integer count = db.queryForObject(sql, Integer.class, new Object[]{userId, serviceId});
	    return count != null && count > 0;
	}
}
