package pack.models;

import java.sql.Date;

public class Payment {
	private int id;
	private int detailId;
	private int usrId;
	private double amount;
	private String transactionNo;
	private String bankCode;
	private String responseCode;
	private String status;
	private String transactionStatus;
	private Date paymentDate;
	private Date createDate;

	public Payment() {
	}

	public Payment(int id, int detailId, int usrId, double amount, String transactionNo, String bankCode,
			String responseCode, String status, String transactionStatus, Date paymentDate, Date createDate) {
		this.id = id;
		this.detailId = detailId;
		this.usrId = usrId;
		this.amount = amount;
		this.transactionNo = transactionNo;
		this.bankCode = bankCode;
		this.responseCode = responseCode;
		this.status = status;
		this.transactionStatus = transactionStatus;
		this.paymentDate = paymentDate;
		this.createDate = createDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDetailId() {
		return detailId;
	}

	public void setDetailId(int detailId) {
		this.detailId = detailId;
	}

	public int getUsrId() {
		return usrId;
	}

	public void setUsrId(int usrId) {
		this.usrId = usrId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
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

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
