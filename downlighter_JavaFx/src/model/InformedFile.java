package model;

import java.io.File;

public class InformedFile extends File {
private int orderIndex;
	public InformedFile(String pathname) {
		super(pathname);
		// TODO Auto-generated constructor stub
	}
	public int getOrderIndex() {
		return orderIndex;
	}
	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

}
