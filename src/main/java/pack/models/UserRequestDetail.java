package pack.models;

import java.sql.Date;

public class UserRequestDetail {
	private int id;
	private int usrReqId;
	private int userId;
	private int serId;
	private String serName;
	private double price;
	private Date startDate;
	private Date createDate;
	private String status;

	public UserRequestDetail() {
	}

	public UserRequestDetail(int id, int usrReqId, int userId, int serId, String serName, double price, Date startDate,
			Date createDate, String status) {
		this.id = id;
		this.usrReqId = usrReqId;
		this.userId = userId;
		this.serId = serId;
		this.serName = serName;
		this.price = price;
		this.startDate = startDate;
		this.createDate = createDate;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUsrReqId() {
		return usrReqId;
	}

	public void setUsrReqId(int usrReqId) {
		this.usrReqId = usrReqId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getSerId() {
		return serId;
	}

	public void setSerId(int serId) {
		this.serId = serId;
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
