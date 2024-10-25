package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.Blog;
import pack.utils.Views;

public class Blog_mapper implements RowMapper<Blog> {
	@Override
	public Blog mapRow(ResultSet rs, int rowNum) throws SQLException {
		Blog item = new Blog();
		item.setId(rs.getInt(Views.COL_BLOG_ID));
		item.setTitle(rs.getString(Views.COL_BLOG_TITLE));
		item.setContent(rs.getString(Views.COL_BLOG_CONTENT));
		item.setImage(rs.getString(Views.COL_BLOG_IMAGES));
		item.setCreateDate(rs.getDate(Views.COL_BLOG_CREATEDATE));
		item.setUpdateDate(rs.getDate(Views.COL_BLOG_UPDATE_DATE));
		return item;
	}
}
