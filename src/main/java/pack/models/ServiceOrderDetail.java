package pack.models;

import java.sql.Date;

public class ServiceOrderDetail {
	private int detailId;
	private int orderId;
	private String serName;
	private double basePrice;
	private Date startDate;
	private String orderStatus;

	public ServiceOrderDetail() {
	}

	public ServiceOrderDetail(int detailId, int orderId, String serName, double basePrice, Date startDate,
			String orderStatus) {
		this.detailId = detailId;
		this.orderId = orderId;
		this.serName = serName;
		this.basePrice = basePrice;
		this.startDate = startDate;
		this.orderStatus = orderStatus;
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

	public double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public int getDetailId() {
		return detailId;
	}

	public void setDetailId(int detailId) {
		this.detailId = detailId;
	}

}
