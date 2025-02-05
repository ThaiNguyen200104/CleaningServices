package pack.models;

import java.sql.Date;

public class Payment {
	private int id;
	private int orderDetailId;
	private int userId;
	private int amount;
	private String transactionNo;
	private String bankCode;
	private Date paymentDate;
	private String status;
	private String responseCode;
	private String transactionStatus;
	private Date createDate;

	public Payment() {
	}

	public Payment(int id, int orderDetailId, int userId, int amount, String transactionNo, String bankCode,
			Date paymentDate, String status, String responseCode, String transactionStatus, Date createDate) {
		super();
		this.id = id;
		this.orderDetailId = orderDetailId;
		this.userId = userId;
		this.amount = amount;
		this.transactionNo = transactionNo;
		this.bankCode = bankCode;
		this.paymentDate = paymentDate;
		this.status = status;
		this.responseCode = responseCode;
		this.transactionStatus = transactionStatus;
		this.createDate = createDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(int orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
