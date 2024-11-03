package pack.models;

import java.time.LocalDateTime;

public class Schedule {
	private int id;
	private int staffId;
	private int detailId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String status;

	public Schedule() {
	}

	public Schedule(int id, int staffId, int detailId, LocalDateTime startDate, LocalDateTime endDate, String status) {
		super();
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

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
