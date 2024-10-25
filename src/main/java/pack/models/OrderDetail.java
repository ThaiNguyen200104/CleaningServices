package pack.models;

import java.sql.Date;

public class OrderDetail {
	private int id;
	private int orderId;
	private int serId;
	private String detailCode;
	private double price;
	private Date startDate;
	private Date completeDate;
	private Date createDate;
	private String status;

	public OrderDetail() {
	}

	public OrderDetail(int id, int orderId, int serId, String detailCode, double price, Date startDate,
			Date completeDate, Date createDate, String status) {
		this.id = id;
		this.orderId = orderId;
		this.serId = serId;
		this.detailCode = detailCode;
		this.price = price;
		this.startDate = startDate;
		this.completeDate = completeDate;
		this.createDate = createDate;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getSerId() {
		return serId;
	}

	public void setSerId(int serId) {
		this.serId = serId;
	}

	public String getDetailCode() {
		return detailCode;
	}

	public void setDetailCode(String detailCode) {
		this.detailCode = detailCode;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
