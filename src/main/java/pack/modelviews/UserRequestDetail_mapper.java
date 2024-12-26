package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.UserRequestDetail;
import pack.utils.Views;

public class UserRequestDetail_mapper implements RowMapper<UserRequestDetail> {
	@Override
	public UserRequestDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserRequestDetail item = new UserRequestDetail();
		item.setId(rs.getInt(Views.COL_URD_ID));
		item.setUsrReqId(rs.getInt(Views.COL_URD_USRREQID));
		item.setUserId(rs.getInt(Views.COL_URD_USRID));
		item.setStaffId(rs.getInt(Views.COL_URD_STAFFID));
		item.setSerId(rs.getInt(Views.COL_URD_SERID));
		item.setPrice(rs.getDouble(Views.COL_URD_PRICE));
		item.setStartDate(rs.getDate(Views.COL_URD_STARTDATE));
		item.setCreateDate(rs.getDate(Views.COL_URD_CREATEDATE));
		item.setStatus(rs.getString(Views.COL_URD_STATUS));
		item.setSerName(rs.getString(Views.COL_SERVICES_NAME));
		return item;
	}
}
