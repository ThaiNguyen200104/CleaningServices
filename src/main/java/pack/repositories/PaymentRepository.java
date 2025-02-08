package pack.repositories;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pack.models.Payment;
import pack.modelviews.Payment_mapper;

@Repository
public class PaymentRepository {
	@Autowired
	private JdbcTemplate db;

	public void savePayment(int orderDetailId, int userId, int amount) {
		String sql = "INSERT INTO payments (order_detail_id, user_id, amount, status) VALUES (?, ?, ?, ?)";
		String str_query = "UPDATE order_details SET status = 'paying' WHERE id = ?";
		db.update(sql, new Object[] { orderDetailId, userId, amount, "pending" });
		db.update(str_query, new Object[] { orderDetailId });
	}

	public void updatePaymentStatus(String transactionNo, String bankCode, Date paymentDate, String status,
			String responseCode, String transactionStatus, String orderId) {
		String sql = "UPDATE payments SET transaction_no = ?, bank_code = ?, payment_date = ?, status = ?, "
				+ "response_code = ?, transaction_status = ? WHERE order_detail_id IN (SELECT id FROM order_details WHERE order_id = ?) "
				+ "AND (status = 'pending' OR status IS NULL)";

		int updatedRows = db.update(sql, new Object[] { transactionNo, bankCode, paymentDate, status, responseCode,
				transactionStatus, orderId });

		if (updatedRows == 0) {
			System.out.println("No payment record updated for orderId: " + orderId);
		}
	}

	public Payment getPaymentByOrderDetailId(int orderDetailId) {
		String sql = "SELECT * FROM payments WHERE order_detail_id = ? AND status = 'pending'";
		try {
			return db.queryForObject(sql, new Payment_mapper(), new Object[] { orderDetailId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void updateOrderStatus(String orderId, String status) {
		String sql = "UPDATE order_details SET status = ? WHERE order_id = ? AND (status = 'verifying' OR status = 'paying')";
		String str_query = "UPDATE order_details SET complete_date = GETDATE() WHERE order_id = ?";

		int updatedRows = db.update(sql, new Object[] { status, orderId });
		db.update(str_query, new Object[] { orderId });

		if (updatedRows == 0) {
			System.out.println("No order detail updated for orderId: " + orderId);
		}
	}

	public Map<String, Object> getOrderDetailWithUser(int orderDetailId) {
		String sql = "SELECT od.*, o.id as order_id, u.id as user_id FROM order_details od "
				+ "JOIN orders o ON od.order_id = o.id JOIN user_requests ur ON o.usrReq_id = ur.id "
				+ "JOIN users u ON ur.user_id = u.id WHERE od.id = ?";
		return db.queryForMap(sql, new Object[] { orderDetailId });
	}

	public Map<String, Object> getOrderDetailByOrderId(int orderId) {
		String sql = "SELECT id FROM order_details WHERE order_id = ?";
		return db.queryForMap(sql, orderId);
	}

	public List<Integer> getStaffIdsByOrderId(int orderId) {
		String sql = "SELECT DISTINCT staff_id FROM schedules s JOIN order_details od ON s.detail_id = od.id "
				+ "WHERE od.order_id = ?";
		return db.queryForList(sql, Integer.class, orderId);
	}

	public void updateStaffStatus(List<Integer> staffIds) {
		if (!staffIds.isEmpty()) {
			String sql = "UPDATE staffs SET status = 'available' WHERE id IN ("
					+ String.join(",", Collections.nCopies(staffIds.size(), "?")) + ")";
			db.update(sql, staffIds.toArray());
		}
	}

	public List<Map<String, Object>> getPaymentHistory(int userId) {
		try {
			String sql = "SELECT p.*, s.service_name FROM payments p JOIN order_details ord ON p.order_detail_id = ord.id "
					+ "JOIN services s ON ord.service_id = s.id WHERE p.user_id = ?";
			return db.queryForList(sql, new Object[] { userId });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
