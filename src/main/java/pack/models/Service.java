package pack.models;

public class Service {
	private int id;
	private String serName;
	private String description;
	private double basePrice;
	private String image;
	private String status;

	public Service() {
	}

	public Service(int id, String serName, String description, double basePrice, String image, String status) {
		this.id = id;
		this.serName = serName;
		this.description = description;
		this.basePrice = basePrice;
		this.image = image;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSerName() {
		return serName;
	}

	public void setSerName(String serName) {
		this.serName = serName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
