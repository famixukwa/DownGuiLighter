package model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public  class  ProcessingStatus {
	private static Highlight pastHighlight;
	private static ObservableList<EBook> ebookObservableList= FXCollections.observableArrayList();
	public static Highlight getPastHighlight() {
		return pastHighlight;
	}

	public static ObservableList<EBook> getEbookObservableList() {
		return ebookObservableList;
	}

	public static void setPastHighlight(Highlight pastHighlight) {
		ProcessingStatus.pastHighlight = pastHighlight;
	}
	public static void addBookToObservable(EBook eBook) {
		ebookObservableList.add(eBook);
	}
	
}
