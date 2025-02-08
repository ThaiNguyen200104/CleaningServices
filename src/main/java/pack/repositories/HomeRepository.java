package pack.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.Blog;
import pack.models.PageView;
import pack.models.Service;
import pack.models.Staff;
import pack.modelviews.Blog_mapper;
import pack.modelviews.Service_mapper;
import pack.modelviews.Staff_mapper;
import pack.utils.Views;

@Repository
public class HomeRepository {
	@Autowired
	JdbcTemplate db;

	/***
	 * fetch top 5 data from table services
	 * 
	 * @return list of services
	 */
	public List<Service> getTop5Services() {
		try {
			String str_query = String.format("SELECT TOP 5 * FROM %s WHERE %s = 'activated'", Views.TBL_SERVICES,
					Views.COL_SERVICES_STATUS);
			return db.query(str_query, new Service_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch all data from table services
	 * 
	 * @return list of services
	 */
	public List<Service> getAllServices(PageView pageItem, String search) {
		try {
			String searchQuery = "";
			List<Object> params = new ArrayList<>();

			if (search != null && !search.trim().isEmpty()) {
				searchQuery = " AND LOWER(service_name) LIKE LOWER(?)";
				params.add("%" + search + "%");
			}

			String countQuery = String.format("SELECT COUNT(*) FROM %s WHERE %s = 'activated' %s", Views.TBL_SERVICES,
					Views.COL_SERVICES_STATUS, searchQuery);
			int count = db.queryForObject(countQuery, Integer.class, params.toArray());

			int totalPage = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(totalPage);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), totalPage);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());

			String query = String.format(
					"SELECT * FROM %s WHERE %s = 'activated' %s ORDER BY %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_SERVICES, Views.COL_SERVICES_STATUS, searchQuery, Views.COL_SERVICES_ID);

			params.add(offset);
			params.add(pageItem.getPageSize());

			return db.query(query, new Service_mapper(), params.toArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch top 4 data from table staffs
	 * 
	 * @return list of staffs
	 */
	public List<Staff> getTop4Staffs() {
		try {
			String str_query = String.format("SELECT TOP 4 * FROM %s WHERE %s != 'disabled'", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS);
			return db.query(str_query, new Staff_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch all data from table staffs
	 * 
	 * @return list of staffs
	 */
	public List<Staff> getAllStaffs(PageView pageItem, String search) {
		try {
			String searchQuery = "";
			List<Object> params = new ArrayList<>();

			if (search != null && !search.trim().isEmpty()) {
				searchQuery = " AND (LOWER(fullname) LIKE LOWER(?) OR phone LIKE ?)";
				String searchPattern = "%" + search + "%";
				params.add(searchPattern);
				params.add(searchPattern);
			}

			String countQuery = String.format("SELECT COUNT(*) FROM %s WHERE %s != 'disabled' %s", Views.TBL_STAFFS,
					Views.COL_STAFFS_STATUS, searchQuery);

			int count = db.queryForObject(countQuery, Integer.class, params.toArray());

			int totalPage = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(totalPage);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), totalPage);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());

			String query = String.format(
					"SELECT * FROM %s WHERE %s != 'disabled' %s ORDER BY %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_STAFFS, Views.COL_STAFFS_STATUS, searchQuery, Views.COL_STAFFS_ID);

			params.add(offset);
			params.add(pageItem.getPageSize());

			return db.query(query, new Staff_mapper(), params.toArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/***
	 * fetch all data from table blogs
	 * 
	 * @return list of blogs
	 */
	public List<Blog> getBlogs() {
		try {
			String str_query = String.format("SELECT * FROM %s", Views.TBL_BLOG, Views.COL_BLOG_ID);
			return db.query(str_query, new Blog_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * fetch blog by id from table blogs
	 * 
	 * @return specific blog
	 */
	public Blog getBlogById(int id) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s = ?", Views.TBL_BLOG, Views.COL_BLOG_ID);
			return db.queryForObject(str_query, new Blog_mapper(), new Object[] { id });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
