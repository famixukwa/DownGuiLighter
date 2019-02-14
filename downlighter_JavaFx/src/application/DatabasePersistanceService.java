package application;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;



public class DatabasePersistanceService {
	EntityManagerFactory emf;
	EntityManager em;
	public DatabasePersistanceService() {

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
}
