package pack.models;

import java.sql.Date;

public class StaffRequest {
	private int id;
	private int staffId;
	private String title;
	private String reason;
	private Date createDate;
	private String status;

	public StaffRequest() {
	}

	public StaffRequest(int id, int staffId, String title, String reason, Date createDate, String status) {
		this.id = id;
		this.staffId = staffId;
		this.title = title;
		this.reason = reason;
		this.createDate = createDate;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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
