package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.OrdersHistory;

public class OrdersHistory_mapper implements RowMapper<OrdersHistory> {
	@Override
	public OrdersHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
		OrdersHistory item = new OrdersHistory();
		item.setOrderId(rs.getInt("orderId"));
		item.setSerName(rs.getString("serName"));
		item.setStartDate(rs.getDate("startDate"));
		item.setStatus(rs.getString("status"));

		return item;
	}

}
