package model;

import java.util.regex.Pattern;

public class SentenceHighlight extends Highlight {
	private Sentence firstSentence;

	public SentenceHighlight(String highligghtText) {
		super();
		this.highligghtText = highligghtText;
		hashCode= Math.abs(highligghtText.hashCode());
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
	
	protected void sentenceSplitter(String s)
	{
		String[] parts = s.split("(?<=\\.)");
		if (parts.length>1) {
			for (int i = 0; i < parts.length; i++) {
				if (i==0) {
					System.out.println("hashcode: "+hashCode);
					Sentence sentence=new Sentence(parts[i],hashCode);
					sentences.add(sentence);
					System.out.println("first sentence:  "+sentence.getSearchable());
				}
				else {
					s=parts[i].replaceAll("^ ", "");
					Sentence sentence=new Sentence(s);
					sentences.add(sentence);
					System.out.println("other sentences:  "+sentence.getSearchable());
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
