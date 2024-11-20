package pack.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.OrderDetail;
import pack.models.Request;
import pack.models.Staff;
import pack.modelviews.Detail_mapper;
import pack.modelviews.Staff_mapper;
import pack.utils.Views;

@Repository
public class StaffRepository {
	@Autowired
	JdbcTemplate db;

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

	// orders
	public List<OrderDetail> pendingOrderList() {
		try {
			String str_query = String.format("select * from %s where %s = 'pending'", Views.TBL_ORDER_DETAIL,
					Views.COL_ORDER_DETAIL_STATUS);
			return db.query(str_query, new Detail_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String priceAdjust(OrderDetail item) {
		try {
			String str_query = String.format("update %s set %s = ? where %s = ?", Views.TBL_ORDER_DETAIL, Views.COL_ORDER_DETAIL_PRICE, Views.COL_ORDER_DETAIL_ID);
			int rowaccepted = db.update(str_query, new Object[] {item.getPrice(), item.getId()});
			return rowaccepted == 1 ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}