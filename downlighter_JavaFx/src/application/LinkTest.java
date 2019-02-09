package application;

public class LinkTest {

	
	public static void main(String[] args) {
		Highlight high= new Highlight("some text");
		System.out.println(high.getHighlightLink());
		System.out.println(high.getHighlightedText());
	}

}
