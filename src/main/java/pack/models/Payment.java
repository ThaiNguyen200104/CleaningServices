package pack.models;

import java.sql.Date;

public class Payment {
	private int id;
	private int payAccId;
	private int orderId;
	private double amount;
	private Date paidDate;

	public Payment() {
	}

	public Payment(int id, int payAccId, int orderId, double amount, Date paidDate) {
		this.id = id;
		this.payAccId = payAccId;
		this.orderId = orderId;
		this.amount = amount;
		this.paidDate = paidDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPayAccId() {
		return payAccId;
	}

	public void setPayAccId(int payAccId) {
		this.payAccId = payAccId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

}
