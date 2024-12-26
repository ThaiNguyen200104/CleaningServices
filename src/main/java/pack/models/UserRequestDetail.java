package pack.models;

import java.sql.Date;

public class UserRequestDetail {
	private int id;
	private int usrReqId;
	private int userId;
	private int staffId;
	private int serId;
	private double price;
	private Date startDate;
	private Date createDate;
	private String status;
	private String serName;

	public UserRequestDetail() {
	}

	public UserRequestDetail(int id, int usrReqId, int userId, int staffId, int serId, double price, Date startDate,
			Date createDate, String status, String serName) {
		super();
		this.id = id;
		this.usrReqId = usrReqId;
		this.userId = userId;
		this.staffId = staffId;
		this.serId = serId;
		this.price = price;
		this.startDate = startDate;
		this.createDate = createDate;
		this.status = status;
		this.serName = serName;
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

	public String getSerName() {
		return serName;
	}

	public void setSerName(String serName) {
		this.serName = serName;
	}

	public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

}
