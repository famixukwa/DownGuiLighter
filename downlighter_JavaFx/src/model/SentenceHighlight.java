package model;

public class SentenceHighlight extends Highlight {
	private Sentence firstSentence;

	public SentenceHighlight(String highligghtText) {
		super(highligghtText);
		setFirstSentence();
		constructHIghlightedText(cleanHilightText);
	}
	@Override
	protected String constructHIghlightedText(String cleanHilightText) {
		return beginTagHighlight1+hashCode+beginTagHighlight2+firstSentence.getHighligghtText()+endTagHighlight;
	}
	protected void setFirstSentence() {
		Sentence firstSentence=sentences.get(1);
	}

	private Sentence getFirstSentence() {
		return firstSentence;
	}
}
