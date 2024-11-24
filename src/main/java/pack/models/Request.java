package pack.models;

import java.sql.Date;

public class Request {
	private int id;
	private int scheduleId;
	private Date dateAdjust;
	private String reason;
	private String status;

	public Request() {
	}

	public Request(int id, int scheduleId, Date dateAdjust, String reason, String status) {
		this.id = id;
		this.scheduleId = scheduleId;
		this.dateAdjust = dateAdjust;
		this.reason = reason;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public Date getDateAdjust() {
		return dateAdjust;
	}

	public void setDateAdjust(Date dateAdjust) {
		this.dateAdjust = dateAdjust;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
