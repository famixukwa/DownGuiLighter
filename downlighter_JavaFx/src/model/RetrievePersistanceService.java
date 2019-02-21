package model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class RetrievePersistanceService  extends Task{
	public enum Mode {BEAN,EBOOK};
	public Mode selectedMode;
	EntityManagerFactory emf;
	EntityManager em;
	private ObservableList<EBook> ebookObservableList= FXCollections.observableArrayList();
	private ObservableList<Highlight> highlightObservableList;
	int selectedBook;
	
	public RetrievePersistanceService() {
		super();
	}
	
	public RetrievePersistanceService(Mode selectedMode) {
		super();
		this.selectedMode = selectedMode;
	}
	
	public RetrievePersistanceService(Mode selectedMode, int selectedBook) {
		super();
		this.selectedMode = selectedMode;
		this.selectedBook=selectedBook;
	}
	
	private void getListOfBooks () {
		emf=Persistence.createEntityManagerFactory("downlighter_JavaFx");
		em=emf.createEntityManager(); 
		em.getTransaction().begin(); 
		Query q = em.createQuery("select e from EBook e");
		List<EBook> EBookList=(List<EBook>)q.getResultList();
		System.out.println(EBookList.size());
		if (EBookList.size()>0) {
			for (EBook eBook : EBookList) {
				EBookPersistenceBean eBookBean= new EBookPersistenceBean(eBook.getEbookId(), eBook.getContainerFolder());
				eBookBean.setBookTitle(eBook.getBookTitle());
				eBookBean.setNumberHighlightsFound(eBook.getNumberHighlightsFound());
				ebookObservableList.add(eBookBean);
			}
		}
		em.close();
		emf.close();
	}
	private void getChosenBookHiglights(int ebookId) {
		emf=Persistence.createEntityManagerFactory("downlighter_JavaFx");
		em=emf.createEntityManager(); 
		em.getTransaction().begin(); 
		Query q = em.createQuery("SELECT h FROM Highlight h JOIN h.eBook e WHERE e.ebookId = :ebookId");
		q.setParameter("ebookId",ebookId );
		List<Highlight> highlightList=(List<Highlight>)q.getResultList();
		highlightObservableList =FXCollections.observableList(highlightList);
		em.close();
		emf.close();
	}

	
	@Override
	protected  Void call() throws Exception {
		switch (selectedMode) {
		case BEAN:
			getListOfBooks ();
			break;
		case EBOOK:
			
			getChosenBookHiglights(selectedBook);
			break;
		default:
			break;
		}
		return null;

	}

	public ObservableList<Highlight> getHighlightObservableList() {
		return highlightObservableList;
	}
	public ObservableList<EBook> getEbookObservableList() {
		return ebookObservableList;
	}
}
