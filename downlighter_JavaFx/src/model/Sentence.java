package model;

import java.io.File;

public class Sentence extends Highlight{

	public Sentence(String highligghtText) {
		super(highligghtText);
	}
	public Sentence(String highligghtText,int hashCode) {
		super(highligghtText);
		this.hashCode=hashCode;
	}

}
