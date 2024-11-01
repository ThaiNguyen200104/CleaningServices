package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.Order;
import pack.utils.Views;

public class Order_mapper implements RowMapper<Order> {
	@Override
	public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
		Order item = new Order();
		item.setId(rs.getInt(Views.COL_ORDERS_ID));
		item.setUsrId(rs.getInt(Views.COL_ORDERS_USER_ID));

		item.setSerName(rs.getString(Views.COL_SERVICES_NAME));
		item.setBasePrice(rs.getDouble(Views.COL_SERVICES_BASE_PRICE));
		item.setStartDate(rs.getDate(Views.COL_ORDER_DETAIL_STARTDATE));

		return item;
	}
}
