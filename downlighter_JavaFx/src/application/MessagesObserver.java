package application;

import java.util.Observable;
import java.util.Observer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class MessagesObserver implements Observer{
	private BookProcess observedProcess=null;
private String message="begin";
	public MessagesObserver(BookProcess observedProcess) {
		this.observedProcess= observedProcess;
	}
	public void update(Observable obs, Object obj) {
		String s=null;
		if (obs==observedProcess) {
			message=observedProcess.getMessages2();
		}
		
	}
	public String getMessage() {
		return message;
	}
}
