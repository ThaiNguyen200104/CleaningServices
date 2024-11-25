package pack.models;

public class Order {
	private int id;
	private int usrReqId;

	public Order() {
	}

	public Order(int id, int usrReqId) {
		this.id = id;
		this.usrReqId = usrReqId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUsrReqId() {
		return usrReqId;
	}

	public void setUsrReqId(int usrReqId) {
		this.usrReqId = usrReqId;
	}

}
