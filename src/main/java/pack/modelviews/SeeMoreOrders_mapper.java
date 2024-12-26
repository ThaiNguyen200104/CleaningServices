package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.SeeMoreOrders;
import pack.utils.Views;

public class SeeMoreOrders_mapper implements RowMapper<SeeMoreOrders> {
	@Override
	public SeeMoreOrders mapRow(ResultSet rs, int rowNum) throws SQLException {
		SeeMoreOrders item = new SeeMoreOrders();
		item.setDetailId(rs.getInt(Views.COL_ORDER_DETAIL_ID));
		item.setSerName(rs.getString(Views.COL_SERVICES_NAME));
		item.setPrice(rs.getDouble(Views.COL_ORDER_DETAIL_PRICE));
		item.setStartDate(rs.getDate(Views.COL_ORDER_DETAIL_STARTDATE));
		item.setCompleteDate(rs.getDate(Views.COL_ORDER_DETAIL_COMPLETEDATE));
		item.setStatus(rs.getString(Views.COL_ORDER_DETAIL_STATUS));
		item.setStaffs(rs.getString(Views.COL_STAFFS_USERNAME));

		return item;
	}
}
