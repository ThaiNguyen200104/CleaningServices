package pack.models;

public class Order {
	private int id;
	private int usrId;
	private String orderStatus;

	public Order() {
	}

	public Order(int id, int usrId) {
		this.id = id;
		this.usrId = usrId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUsrId() {
		return usrId;
	}

	public void setUsrId(int usrId) {
		this.usrId = usrId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String status) {
		this.orderStatus = status;
	}

}
