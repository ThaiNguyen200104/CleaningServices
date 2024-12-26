package pack.models;

import java.sql.Date;

public class SeeMoreOrders {
	private int detailId;
	private String serName;
	private double price;
	private Date startDate;
	private Date completeDate;
	private String status;
	private String staffs;

	public SeeMoreOrders() {
	}

	public SeeMoreOrders(int detailId, String serName, double price, Date startDate, Date completeDate, String status,
			String staffs) {
		this.detailId = detailId;
		this.serName = serName;
		this.price = price;
		this.startDate = startDate;
		this.completeDate = completeDate;
		this.status = status;
		this.staffs = staffs;
	}

	public int getDetailId() {
		return detailId;
	}

	public void setDetailId(int detailId) {
		this.detailId = detailId;
	}

	public String getSerName() {
		return serName;
	}

	public void setSerName(String serName) {
		this.serName = serName;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStaffs() {
		return staffs;
	}

	public void setStaffs(String staffs) {
		this.staffs = staffs;
	}

}
