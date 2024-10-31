package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.Service;
import pack.utils.Views;

public class Service_mapper implements RowMapper<Service> {
	@Override
	public Service mapRow(ResultSet rs, int rowNum) throws SQLException {
		Service item = new Service();
		item.setId(rs.getInt(Views.COL_SERVICES_ID));
		item.setSerName(rs.getString(Views.COL_SERVICES_NAME));
		item.setDescription(rs.getString(Views.COL_SERVICES_DESCRIPTION));
		item.setBasePrice(rs.getDouble(Views.COL_SERVICES_BASE_PRICE));
		item.setImage(rs.getString(Views.COL_SERVICES_IMAGES));
		item.setStatus(rs.getString(Views.COL_SERVICES_STATUS));
		item.setStaffRequired(rs.getInt(Views.COL_SERVICES_STAFF_REQUIRED));
		return item;
	}
}
