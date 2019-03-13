package model;



import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javafx.concurrent.Task;



/**
 * 
 * saves data in a database for persistence purposes it takes an ebook as a parameter
 * @param eBook is the ebook from which the different parameters are taken to be saved in the database
 * 
 */

public class SavePersistanceService extends Task<Object>{
	private EntityManagerFactory emf;
	private EntityManager em;
	private EBook eBook;

	public SavePersistanceService(EBook eBook) {
		this.eBook= eBook;
	}
	public void  saveEbookWithHighlightsFound (EBook eBook) throws Exception{
		
		emf=Persistence.createEntityManagerFactory("downlighter_JavaFx");  
		em=emf.createEntityManager(); 
		em.getTransaction().begin(); 
		em.persist(eBook);
		for (Highlight highlight : eBook.getHighlightsFound()) {
			em.persist(highlight);
		}
		
		em.getTransaction().commit();
		em.close();
		emf.close();
	}

	@Override
	protected Void call() throws Exception {
		saveEbookWithHighlightsFound (eBook);
		return null;
	}
}
