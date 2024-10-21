package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.Service;
import pack.utils.Views;

public class Service_mapper implements RowMapper<Service> {
	public Service mapRow(ResultSet rs, int rowNum) throws SQLException {
		Service item = new Service();
		item.setId(rs.getInt(Views.COL_SERVICE_ID));
		item.setSerName(rs.getString(Views.COL_SERVICE_NAME));
		item.setDescription(rs.getString(Views.COL_SERVICE_DESCRIPTION));
		item.setBasePrice(rs.getDouble(Views.COL_SERVICE_BASE_PRICE));
		item.setDuration(rs.getDouble(Views.COL_SERVICE_DURATION));
		item.setImage(rs.getString(Views.COL_SERVICE_IMAGES));
		return item;
	}
}
