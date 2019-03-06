package model;

import javafx.concurrent.Task;

public class SearhReplaceProgress extends Task<Void>{
	private int i;

	@Override
	protected Void call() throws Exception {
		ModelInterface.setProgress(i*0.2);
		return null;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

}
