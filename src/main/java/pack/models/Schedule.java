package pack.models;

import java.sql.Date;

public class Schedule {
	private int id;
	private int staffId;
	private int detailId;
	private Date startDate;
	private Date endDate;
	private String status;

	public Schedule() {
	}

	public Schedule(int id, int staffId, int detailId, Date startDate, Date endDate, String status) {
		this.id = id;
		this.staffId = staffId;
		this.detailId = detailId;
		this.startDate = startDate;
		this.endDate = endDate;
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

	public int getDetailId() {
		return detailId;
	}

	public void setDetailId(int detailId) {
		this.detailId = detailId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
