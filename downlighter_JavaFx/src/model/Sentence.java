package model;

import java.io.File;

public class Sentence extends Highlight{
	private Highlight progenitorHighlight;
	public Sentence(String sentenceText,Highlight highlight) {
		super();
		progenitorHighlight=highlight;
	}
	

}
