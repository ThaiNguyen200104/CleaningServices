package pack.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.PageView;
import pack.models.Service;
import pack.modelviews.Service_mapper;
import pack.utils.Views;

@Repository
public class HomeRepository {
	@Autowired
	JdbcTemplate db;

	// -------------------- ORDERS -------------------- //
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
