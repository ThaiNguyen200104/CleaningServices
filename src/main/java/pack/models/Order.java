package pack.models;

public class Order {
	private int id;
	private int usrId;

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

}
