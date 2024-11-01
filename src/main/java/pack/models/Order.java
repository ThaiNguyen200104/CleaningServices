package pack.models;

import java.sql.Date;

public class Order {
	private int id;
	private int usrId;

	private String serName;
	private double basePrice;
	private Date startDate;

	public Order() {
	}

	public Order(int id, int usrId, String serName, double basePrice, Date startDate) {
		this.id = id;
		this.usrId = usrId;
		this.serName = serName;
		this.basePrice = basePrice;
		this.startDate = startDate;
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

}
