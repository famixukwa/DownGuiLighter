package model;

import java.util.regex.Pattern;

public class SentenceHighlight extends Highlight {
	private Sentence firstSentence;

	public SentenceHighlight(String highligghtText) {
		super();

		this.highligghtText = highligghtText;
		hashCode= Math.abs(highligghtText.hashCode());
		//		cleanHilightText=cleanEspecialCharacters(highligghtText);
		sentenceSplitter(highligghtText);
		setFirstSentence();
		SPECIAL_REGEX_CHARS = Pattern.compile("[\\{\\}\\(\\)\\[\\]\\?\\+\\*\\^$\\|\\\\\\-]");
		searchable= createSearchable(highligghtText);
		highlightedText=constructHIghlightedText(highligghtText);
	}

	@Override
	public void constructHighlightLink() {
		String HighlightLinkBeginning=beginLinkTag+containerFile.getAbsolutePath()+"#"+hashCode+beginLinkTag2;
		String HighlightLink=HighlightLinkBeginning+firstSentence.getHighligghtText()+endLinkTag;
		constructUrl();
		this.highlightLink= HighlightLink;
	}
	@Override
	protected void sentenceSplitter(String s)
	{
		String[] parts = s.split("(?<=\\.)");
		if (parts.length>1) {
			for (int i = 0; i < parts.length; i++) {
				if (i==0) {
					Sentence sentence=new Sentence(s,hashCode);
					sentences.add(sentence);
				}
				else {
					Sentence sentence=new Sentence(s);
					sentences.add(sentence);
				}
			}
		}
	}

	protected void setFirstSentence() {
		this.firstSentence=sentences.get(1);

	}

	public Sentence getFirstSentence() {
		return firstSentence;
	}
}
