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
		item.setUsrReqId(rs.getInt(Views.COL_ORDERS_USRREQID));

		return item;
	}
}
