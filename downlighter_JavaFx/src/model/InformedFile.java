package model;

import java.io.File;
/*
 * extension of file that retains the order that the file has in the book obtained from the spline of the ebook file
 */
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
