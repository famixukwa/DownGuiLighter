package model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;




public class DatabasePersistanceService {
	EntityManagerFactory emf;
	EntityManager em;
	BookProcess processedBook;
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
	public void listStudents () {
		emf=Persistence.createEntityManagerFactory("database_test");
		em=emf.createEntityManager(); 
		em.getTransaction().begin(); 
		Query q = em.createQuery("select m from StudentEntity m");
		List studentList=q.getResultList();
		for (Object student : studentList) {
			
		}
		em.close();
		emf.close();
	}
}
