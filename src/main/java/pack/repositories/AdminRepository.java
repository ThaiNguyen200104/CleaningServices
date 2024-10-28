package pack.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.Admin;
import pack.models.Blog;
import pack.models.Order;
import pack.models.PageView;
import pack.models.Service;
import pack.models.Staff;
import pack.modelviews.Admin_mapper;
import pack.modelviews.Blog_mapper;
import pack.modelviews.Order_mapper;
import pack.modelviews.Service_mapper;
import pack.modelviews.Staff_mapper;
import pack.utils.SecurityUtility;
import pack.utils.Views;

@Repository
public class AdminRepository {
	@Autowired
	JdbcTemplate db;

	// Admin Region
	public Admin getAdminByUsername(String username) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_ADMIN, Views.COL_ADMIN_USERNAME);
			return db.queryForObject(str_query, new Admin_mapper(), new Object[] { username });
		} catch (Exception e) {
			return null;
		}
	}

	public Admin getAdminById(int id) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_ADMIN, Views.COL_ADMIN_ID);
			return db.queryForObject(str_query, new Admin_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public String newAdmin(String username, String password) {
		try {
			String str_query = String.format("insert into %s (username, password) values(?,?)", Views.TBL_ADMIN);
			int rowaccept = db.update(str_query, new Object[] { username, SecurityUtility.encryptBcrypt(password) });
			return rowaccept == 1 ? "success" : "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public Admin checkEmailExists(String email) {
		try {
			String str_query = String.format("select * from %s where %s = ?", Views.TBL_ADMIN, Views.COL_ADMIN_EMAIL);
			return db.queryForObject(str_query, new Admin_mapper(), new Object[] { email });
		} catch (Exception e) {
			return null;
		}
	}

	// Service region
	public List<Service> getServices(PageView pageItem) {
		try {
			int count = db.queryForObject("select count(*) from services", Integer.class);
			int total_page = count / pageItem.getPageSize();
			pageItem.setTotalPage(total_page);

			String str_query = String.format("select * from %s order by %s DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
					Views.TBL_SERVICES, Views.COL_SERVICES_ID);
			return db.query(str_query, new Service_mapper(),
					new Object[] { (pageItem.getPageCurrent() - 1) * pageItem.getPageSize(), pageItem.getPageSize() });
		} catch (Exception e) {
			return null;
		}
	}

	public Service getServiceById(int id) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_SERVICES, Views.COL_SERVICES_ID);
			return db.queryForObject(str_query, new Service_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public String newService(Service ser) {
		try {
			StringBuilder queryBuilder = new StringBuilder("insert into ");
			queryBuilder.append(Views.TBL_SERVICES).append(" (service_name, base_price, duration");

			StringBuilder valuesBuilder = new StringBuilder(" values (?, ?, ?");
			List<Object> params = new ArrayList<>();

			params.add(ser.getSerName());
			params.add(ser.getBasePrice());
			params.add(ser.getDuration());

			if (ser.getDescription() != null) {
				queryBuilder.append(", description");
				valuesBuilder.append(", ?");
				params.add(ser.getDescription());
			}

			if (ser.getImage() != null && !ser.getImage().isEmpty()) {
				queryBuilder.append(", image");
				valuesBuilder.append(", ?");
				params.add(ser.getImage());
			}

			queryBuilder.append(")");
			valuesBuilder.append(")");

			queryBuilder.append(valuesBuilder);

			int rowsAffected = db.update(queryBuilder.toString(), params.toArray());
			return rowsAffected == 1 ? "success" : "failed";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	public String editService(Service ser) {
		try {
			String str_query = String.format("update %s set %s=?, %s=?, %s=?, %s=?, %s=? where %s=?",
					Views.TBL_SERVICES, Views.COL_SERVICES_NAME, Views.COL_SERVICES_DESCRIPTION,
					Views.COL_SERVICES_BASE_PRICE, Views.COL_SERVICES_DURATION, Views.COL_SERVICES_ID,
					Views.COL_SERVICES_IMAGES);
			int rowaccept = db.update(str_query, new Service_mapper());
			if (rowaccept == 1) {
				return "success";
			}
			return "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String deleteService(int id) {
		try {
			String str_query = String.format("delete from %s where %s=?", Views.TBL_SERVICES, Views.COL_SERVICES_ID);
			int rowaccept = db.update(str_query, new Object[] { id });
			if (rowaccept == 1) {
				return "success";
			}
			return "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// Blog region
	public List<Blog> getBlogs(PageView pageItem) {
		try {
			int count = db.queryForObject("select count(*) from blogs", Integer.class);
			int totalPage = count / pageItem.getPageSize();
			pageItem.setTotalPage(totalPage);

			String str_query = String.format("select * from %s order by %s desc offset ? rows fetch next ? rows only",
					Views.TBL_BLOG, Views.COL_BLOG_ID);
			return db.query(str_query, new Blog_mapper(),
					new Object[] { (pageItem.getPageCurrent() - 1) * pageItem.getPageSize(), pageItem.getPageSize() });
		} catch (Exception e) {
			return null;
		}
	}

	public Blog getBlogById(int id) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_BLOG, Views.COL_BLOG_ID);
			return db.queryForObject(str_query, new Blog_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public String newBlog(Blog blog) {
		try {
			String str_query = String.format("insert into %s (title, content, images) values(?,?,?)", Views.TBL_BLOG);
			int rowaccept = db.update(str_query, new Object[] { blog.getTitle(), blog.getContent(), blog.getImage() });
			if (rowaccept == 1) {
				return "success";
			}
			return "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String editBlog(Blog blog) {
		try {
			String str_query = String.format("update %s set %s=?, %s=?, %s=?, %s=GETDATE() where id=?", Views.TBL_BLOG,
					Views.COL_BLOG_TITLE, Views.COL_BLOG_CONTENT, Views.COL_BLOG_IMAGES, Views.COL_BLOG_UPDATE_DATE);
			int rowaccept = db.update(str_query,
					new Object[] { blog.getTitle(), blog.getContent(), blog.getImage(), blog.getUpdateDate() });
			if (rowaccept == 1) {
				return "success";
			}
			return "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String deleteBlog(int id) {
		try {
			String str_query = String.format("delete from %s where %s=?", Views.TBL_BLOG, Views.COL_BLOG_ID);
			int rowaccept = db.update(str_query, new Object[] { id });
			if (rowaccept == 1) {
				return "success";
			}
			return "failed";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// Staff region
	public List<Staff> getStaffs(PageView pageItem) {
		try {
			int count = db.queryForObject("select count(*) from staffs", Integer.class);
			int totalPage = count / pageItem.getPageSize();
			pageItem.setTotalPage(totalPage);

			String str_query = String.format("select * from %s order by %s desc offset ? rows fetch next ? rows only",
					Views.TBL_STAFFS, Views.COL_STAFFS_ID);
			return db.query(str_query, new Staff_mapper(),
					new Object[] { (pageItem.getPageCurrent() - 1) * pageItem.getPageSize(), pageItem.getPageSize() });
		} catch (Exception e) {
			return null;
		}
	}

	public Staff getStaffById(int id) {
		try {
			String str_query = String.format("select * from %s where %s=?", Views.TBL_STAFFS, Views.COL_STAFFS_ID);
			return db.queryForObject(str_query, new Staff_mapper(), new Object[] { id });
		} catch (Exception e) {
			return null;
		}
	}

	public String newStaff(Staff staff) {
		try {
			String str_query = String.format("insert into %s (username, password, email) values(?,?,?)",
					Views.TBL_STAFFS);
			String hashpassword = SecurityUtility.encryptBcrypt(staff.getPassword());
			int rowaccept = db.update(str_query, new Object[] { staff.getUsername(), hashpassword, staff.getEmail() });
			return rowaccept == 1 ? "success" : "failed";
		} catch (DuplicateKeyException e) {
			throw new IllegalArgumentException("Some information(username, email, phone) may already exists.");
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// Order region
	public List<Order> getOrders(PageView pageItem) {
		try {
			int count = db.queryForObject("select count(*) from orders", Integer.class);
			int totalPage = count / pageItem.getPageSize();
			pageItem.setTotalPage(totalPage);

			String str_query = String.format("select * from %s order by %s desc offset ? rows fetch next ? rows only",
					Views.TBL_ORDER, Views.COL_ORDERS_ID);
			return db.query(str_query, new Order_mapper(),
					new Object[] { (pageItem.getPageCurrent() - 1) * pageItem.getPageSize(), pageItem.getPageSize() });
		} catch (Exception e) {
			return null;
		}
	}
}
