package pack.modelviews;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import pack.models.Payment;

public class Payment_mapper implements RowMapper<Payment> {
	@Override
	public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
		Payment payment = new Payment();
		payment.setId(rs.getInt("id"));
		payment.setOrderDetailId(rs.getInt("order_detail_id"));
		payment.setUserId(rs.getInt("user_id"));
		payment.setAmount(rs.getInt("amount"));
		payment.setTransactionNo(rs.getString("transaction_no"));
		payment.setBankCode(rs.getString("bank_code"));
		payment.setPaymentDate(rs.getDate("payment_date"));
		payment.setStatus(rs.getString("status"));
		payment.setResponseCode(rs.getString("response_code"));
		payment.setTransactionStatus(rs.getString("transaction_status"));
		payment.setCreateDate(rs.getDate("create_date"));
		return payment;
	}
}
