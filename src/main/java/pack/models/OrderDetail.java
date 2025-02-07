package pack.models;

import java.sql.Date;

public class OrderDetail {
	private int id;
	private int orderId;
	private int serId;
	private double price;
	private Date startDate;
	private Date completeDate;
	private Date createDate;
	private String status;
	private String serName;
	private String beforeImage;
	private String afterImage;

	public OrderDetail() {
	}

	public OrderDetail(int id, int orderId, int serId, double price, Date startDate, Date completeDate, Date createDate,
			String status, String serName, String beforeImage, String afterImage) {
		this.id = id;
		this.orderId = orderId;
		this.serId = serId;
		this.price = price;
		this.startDate = startDate;
		this.completeDate = completeDate;
		this.createDate = createDate;
		this.status = status;
		this.serName = serName;
		this.beforeImage = beforeImage;
		this.afterImage = afterImage;
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

	public String getSerName() {
		return serName;
	}

	public void setSerName(String serName) {
		this.serName = serName;
	}

	public String getBeforeImage() {
		return beforeImage;
	}

	public void setBeforeImage(String beforeImage) {
		this.beforeImage = beforeImage;
	}

	public String getAfterImage() {
		return afterImage;
	}

	public void setAfterImage(String afterImage) {
		this.afterImage = afterImage;
	}

}
