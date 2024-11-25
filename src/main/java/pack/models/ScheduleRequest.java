package pack.models;

import java.sql.Date;

public class ScheduleRequest {
	private int id;
	private int scheduleId;
	private Date dateAdjust;
	private String type;
	private String reason;
	private Date createDate;
	private String status;

	public ScheduleRequest() {
	}

	public ScheduleRequest(int id, int scheduleId, Date dateAdjust, String type, String reason, Date createDate,
			String status) {
		this.id = id;
		this.scheduleId = scheduleId;
		this.dateAdjust = dateAdjust;
		this.type = type;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
