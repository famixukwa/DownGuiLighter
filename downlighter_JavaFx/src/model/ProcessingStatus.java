package model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public  class  ProcessingStatus {
	private static Highlight pastHighlight;
	private ObservableList<EBook> ebookObservableList= FXCollections.observableArrayList();
	public static Highlight getPastHighlight() {
		return pastHighlight;
	}

	public static void setPastHighlight(Highlight pastHighlight) {
		ProcessingStatus.pastHighlight = pastHighlight;
	}
	
}
