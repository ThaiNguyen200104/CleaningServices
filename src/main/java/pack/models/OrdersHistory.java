package pack.models;

import java.sql.Date;

public class OrdersHistory {
	private int orderId;
	private String serName;
	private Date startDate;
	private String status;

	public OrdersHistory() {
	}

	public OrdersHistory(int orderId, String serName, Date startDate, String status) {
		this.orderId = orderId;
		this.serName = serName;
		this.startDate = startDate;
		this.status = status;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getSerName() {
		return serName;
	}

	public void setSerName(String serName) {
		this.serName = serName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
