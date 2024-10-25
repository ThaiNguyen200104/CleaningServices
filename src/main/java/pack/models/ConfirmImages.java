package pack.models;

import java.sql.Date;

public class ConfirmImages {
	private int id;
	private int detailId;
	private String image;
	private Date capturedDate;
	private Date uploadDate;

	public ConfirmImages() {
	}

	public ConfirmImages(int id, int detailId, String image, Date capturedDate, Date uploadDate) {
		this.id = id;
		this.detailId = detailId;
		this.image = image;
		this.capturedDate = capturedDate;
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

	public Date getCapturedDate() {
		return capturedDate;
	}

	public void setCapturedDate(Date capturedDate) {
		this.capturedDate = capturedDate;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

}
