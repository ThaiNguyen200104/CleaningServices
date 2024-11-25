package pack.models;

import java.sql.Date;

public class ConfirmImage {
	private int id;
	private int detailId;
	private String image;
	private Date captureDate;
	private Date uploadDate;

	public ConfirmImage() {
	}

	public ConfirmImage(int id, int detailId, String image, Date captureDate, Date uploadDate) {
		this.id = id;
		this.detailId = detailId;
		this.image = image;
		this.captureDate = captureDate;
		this.uploadDate = uploadDate;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Date getCaptureDate() {
		return captureDate;
	}

	public void setCaptureDate(Date captureDate) {
		this.captureDate = captureDate;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

}
