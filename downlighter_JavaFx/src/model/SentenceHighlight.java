package model;

public class SentenceHighlight extends Highlight {
	private Sentence firstSentence;

	public SentenceHighlight(String highligghtText) {
		super();
		
		this.highligghtText = highligghtText;
		hashCode= Math.abs(highligghtText.hashCode());
		cleanHilightText=cleanEspecialCharacters(highligghtText);
		sentenceSplitter(highligghtText);
		setFirstSentence();
		searchable= createSearchable(cleanHilightText);
		highlightedText=constructHIghlightedText(highligghtText);
	}
	
	@Override
	public void constructHighlightLink() {
		String HighlightLinkBeginning=beginLinkTag+containerFile.getAbsolutePath()+"#"+hashCode+beginLinkTag2;
		String HighlightLink=HighlightLinkBeginning+firstSentence.getHighligghtText()+endLinkTag;
		constructUrl();
		this.highlightLink= HighlightLink;
	}
	
	protected void setFirstSentence() {
		this.firstSentence=sentences.get(1);
		
	}

	private Sentence getFirstSentence() {
		return firstSentence;
	}
}
