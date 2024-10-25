package pack.models;

import java.sql.Date;

public class Blog {
	private int id;
	private String title;
	private String content;
	private String image;
	private Date createDate;
	private Date updateDate;

	public Blog() {
	}

	public Blog(int id, String title, String content, String image, Date createDate, Date updateDate) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.image = image;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
