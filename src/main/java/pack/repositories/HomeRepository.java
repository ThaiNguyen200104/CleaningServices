package pack.repositories;

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

	// -------------------- ORDERS -------------------- //

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

	public List<Service> getAllServices(PageView pageItem) {
		try {
			int count = db.queryForObject("SELECT COUNT(*) FROM services WHERE status = 'activated'", Integer.class);
			int total_page = count / pageItem.getPageSize();
			pageItem.setTotalPage(total_page);

			String str_query = String.format(
					"SELECT * FROM %s WHERE %s = 'activated' ORDER BY %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_SERVICES, Views.COL_SERVICES_STATUS, Views.COL_SERVICES_ID);
			return db.query(str_query, new Service_mapper(),
					new Object[] { (pageItem.getPageCurrent() - 1) * pageItem.getPageSize(), pageItem.getPageSize() });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

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

	public List<Staff> getAllStaffs(PageView pageItem) {
		try {
			int count = db.queryForObject("SELECT COUNT(*) FROM staffs WHERE status != 'disabled'", Integer.class);
			int total_page = Math.max(1, (int) Math.ceil((double) count / pageItem.getPageSize()));
			pageItem.setTotalPage(total_page);

			int currentPage = Math.min(Math.max(1, pageItem.getPageCurrent()), total_page);
			pageItem.setPageCurrent(currentPage);

			int offset = Math.max(0, (currentPage - 1) * pageItem.getPageSize());

			String str_query = String.format(
					"SELECT * FROM %s WHERE %s != 'disabled' ORDER BY %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_STAFFS, Views.COL_STAFFS_STATUS, Views.COL_STAFFS_ID);
			return db.query(str_query, new Staff_mapper(), offset, pageItem.getPageSize());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Blog> getBlogs() {
		try {
			String str_query = String.format("SELECT * FROM %s", Views.TBL_BLOG, Views.COL_BLOG_ID);
			return db.query(str_query, new Blog_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Blog getBlogById(int id) {
		try {
			String str_query = String.format("SELECT * FROM %s WHERE %s = ?", Views.TBL_BLOG, Views.COL_BLOG_ID);
			return db.queryForObject(str_query, new Blog_mapper(), new Object[] { id });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean getOrderedOrders() {
		try {
			String str_query = String.format("SELECT COUNT(*) > 0 FROM %s WHERE %s != 'canceled' AND %s = 'completed'",
					Views.TBL_ORDER_DETAIL, Views.COL_ORDER_DETAIL_STATUS);
			return db.queryForObject(str_query, Boolean.class);
		} catch (Exception e) {
			return false;
		}
	}

}
