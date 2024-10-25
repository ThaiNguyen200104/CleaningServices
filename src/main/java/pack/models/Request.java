package pack.models;

import java.sql.Date;

public class Request {
	private int id;
	private int scheduleId;
	private Date dateAdjust;
	private double priceAdjust;
	private String reason;
	private String dateStatus;
	private String priceStatus;

	public Request() {
	}

	public Request(int id, int scheduleId, Date dateAdjust, double priceAdjust, String reason, String dateStatus,
			String priceStatus) {
		this.id = id;
		this.scheduleId = scheduleId;
		this.dateAdjust = dateAdjust;
		this.priceAdjust = priceAdjust;
		this.reason = reason;
		this.dateStatus = dateStatus;
		this.priceStatus = priceStatus;
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

	public double getPriceAdjust() {
		return priceAdjust;
	}

	public void setPriceAdjust(double priceAdjust) {
		this.priceAdjust = priceAdjust;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getDateStatus() {
		return dateStatus;
	}

	public void setDateStatus(String dateStatus) {
		this.dateStatus = dateStatus;
	}

	public String getPriceStatus() {
		return priceStatus;
	}

	public void setPriceStatus(String priceStatus) {
		this.priceStatus = priceStatus;
	}

}
