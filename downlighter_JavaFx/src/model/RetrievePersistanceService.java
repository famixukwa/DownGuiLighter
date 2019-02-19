package model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class RetrievePersistanceService  extends Task{
	EntityManagerFactory emf;
	EntityManager em;
	private ObservableList<EBook> ebookObservableList= FXCollections.observableArrayList();

	public void getListOfBooks () {
		
		emf=Persistence.createEntityManagerFactory("downlighter_JavaFx");
		em=emf.createEntityManager(); 
		em.getTransaction().begin(); 
		Query q = em.createQuery("select e from EBook e");
		List<EBook> EBookList=(List<EBook>)q.getResultList();
		System.out.println(EBookList.size());
		if (EBookList.size()>0) {
			for (EBook eBook : EBookList) {
				EBookPersistenceBean eBookBean= new EBookPersistenceBean(eBook.getEbookId(), eBook.getContainerFolder());
				System.out.println(eBook.getEbookId());
				eBookBean.setBookTitle(eBook.getBookTitle());
				eBookBean.setNumberHighlightsFound(eBook.getNumberHighlightsFound());
				ebookObservableList.add(eBookBean);
				System.out.println(eBookBean.getBookTitle());
			}
		}
		em.close();
		emf.close();
	}

	@Override
	protected ObservableList<EBook> call() throws Exception {
		
		getListOfBooks ();
		return ebookObservableList;
	}

	public ObservableList<EBook> getEbookObservableList() {
		return ebookObservableList;
	}
}
