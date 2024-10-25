package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.OrderDetail;
import pack.utils.Views;

public class Detail_mapper implements RowMapper<OrderDetail> {
	public OrderDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
		OrderDetail item = new OrderDetail();
		item.setId(rs.getInt(Views.COL_ORDER_DETAIL_ID));
		item.setOrderId(rs.getInt(Views.COL_ORDER_DETAIL_ORDER_ID));
		item.setSerId(rs.getInt(Views.COL_ORDER_DETAIL_SERVICE_ID));
		item.setDetailCode(rs.getString(Views.COL_ORDER_DETAIL_CODE));
		item.setPrice(rs.getDouble(Views.COL_ORDER_DETAIL_PRICE));
		item.setStartDate(rs.getDate(Views.COL_ORDER_DETAIL_START_DATE));
		item.setCompleteDate(rs.getDate(Views.COL_ORDER_DETAIL_COMPLETED_DATE));
		item.setCreateDate(rs.getDate(Views.COL_ORDER_DETAIL_CREATEDATE));
		item.setStatus(rs.getString(Views.COL_ORDER_DETAIL_STATUS));

		return item;
	}
}
