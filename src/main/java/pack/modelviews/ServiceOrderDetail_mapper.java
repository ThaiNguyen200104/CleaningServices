package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.ServiceOrderDetail;
import pack.utils.Views;

public class ServiceOrderDetail_mapper implements RowMapper<ServiceOrderDetail> {
	@Override
	public ServiceOrderDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
		ServiceOrderDetail item = new ServiceOrderDetail();
		item.setDetailId(rs.getInt("detailId"));
		item.setOrderId(rs.getInt("orderId"));
		item.setSerName(rs.getString(Views.COL_SERVICES_NAME));
		item.setBasePrice(rs.getDouble(Views.COL_SERVICES_BASE_PRICE));
		item.setStartDate(rs.getDate("startDate"));
		item.setOrderStatus(rs.getString("Ordstatus"));
		return item;
	}
}
