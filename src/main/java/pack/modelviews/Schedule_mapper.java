package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.Schedule;
import pack.utils.Views;

public class Schedule_mapper implements RowMapper<Schedule> {
	@Override
	public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
		Schedule item = new Schedule();
		item.setId(rs.getInt(Views.COL_SCHEDULES_ID));
		item.setStaffId(rs.getInt(Views.COL_SCHEDULES_STAFF_ID));
		item.setDetailId(rs.getInt(Views.COL_SCHEDULES_DETAIL_ID));
		item.setStartDate(rs.getTimestamp(Views.COL_SCHEDULES_START_AT).toLocalDateTime());
		item.setEndDate(rs.getTimestamp(Views.COL_SCHEDULES_END_AT).toLocalDateTime());
		item.setStatus(rs.getString(Views.COL_SCHEDULES_STATUS));
		return item;
	}
}
