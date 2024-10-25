package pack.models;

import java.sql.Date;

public class Order {
	private int id;
	private int usrId;
	private double price;
	private Date startDate;
	private String status;

	public Order() {
	}

	public Order(int id, int usrId, double price, Date startDate, String status) {
		this.id = id;
		this.usrId = usrId;
		this.price = price;
		this.startDate = startDate;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUsrId() {
		return usrId;
	}

	public void setUsrId(int usrId) {
		this.usrId = usrId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
