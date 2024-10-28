package pack.models;

public class PageView {
	private int pageSize = 5;
	private int pageCurrent = 1;
	private int totalPage = 1;

	public PageView(int pageSize, int pageCurrent, int totalPage) {
		this.pageSize = pageSize;
		this.pageCurrent = pageCurrent;
		this.totalPage = totalPage;
	}
	
	public PageView() {}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageCurrent() {
		return pageCurrent;
	}

	public void setPageCurrent(int pageCurrent) {
		this.pageCurrent = pageCurrent;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
}
