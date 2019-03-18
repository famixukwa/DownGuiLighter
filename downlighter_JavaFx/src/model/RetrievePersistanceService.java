package model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
/**
 * 
 * acts as a handler between the model, the gui and the database
 *
 */
public class RetrievePersistanceService  extends Task<Void>{
	public enum Mode {BEAN,EBOOK};
	public Mode selectedMode;
	EntityManagerFactory emf;
	EntityManager em;
	private ObservableList<EBook> ebookObservableList= FXCollections.observableArrayList();
	private ObservableList<Highlight> highlightObservableList;
	private EBook eBook;
	int selectedBook;
	
	public RetrievePersistanceService() {
		super();
	}
	/**
	 * retrieves data from the database
	 * @param selectedMode there are two modes EBOOK retrieves the highlights of the selected book and bean retrieves the list of archived books
	 */
	public RetrievePersistanceService(Mode selectedMode) {
		super();
		this.selectedMode = selectedMode;
	}
	/**
	 * 
	 * @param selectedMode there are two modes EBOOK retrieves the highlights of the selected book and bean retrieves the list of archived books
	 * @param selectedBook is the bool selected on the table.
	 */
	public RetrievePersistanceService(Mode selectedMode, int selectedBook) {
		super();
		this.selectedMode = selectedMode;
		this.selectedBook=selectedBook;
	}
	/**
	 * this method starts the thread
	 */
	public void start () {
		Thread th = new Thread(this);
		th.setDaemon(true);
		th.start();	
		if (selectedMode==Mode.EBOOK) {
			this.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					ModelConnector.popupWindowView(eBook);	
				}
			});
		}
	}
	/**
	 * this method retrieve a list of the archived books from the database that later on are served to the archive tab
	 */
	private void getListOfBooks () {
		emf=Persistence.createEntityManagerFactory("downlighter_JavaFx");
		em=emf.createEntityManager(); 
		em.getTransaction().begin(); 
		Query q = em.createQuery("select e from EBook e");
		List<EBook> EBookList=(List<EBook>)q.getResultList();
		if (EBookList.size()>0) {
			for (EBook eBook : EBookList) {
				ModelConnector.addBookToObservable(eBook);
			}
		}
		em.close();
		emf.close();
	}
	/**
	 * retrieves the highlights of the chosen book from the book archive tab to be shown in the popup window
	 * @param ebookId id of the book clicked by the user in the table of the archive tab
	 */
	private void getChosenBookHiglights(int ebookId) {
		emf=Persistence.createEntityManagerFactory("downlighter_JavaFx");
		em=emf.createEntityManager(); 
		em.getTransaction().begin(); 
		Query q = em.createQuery("SELECT h FROM Highlight h JOIN h.eBook e WHERE e.ebookId = :ebookId");
		Query qb = em.createQuery("SELECT e FROM EBook e WHERE e.ebookId = :ebookId");
		q.setParameter("ebookId",ebookId );
		qb.setParameter("ebookId",ebookId );
		List<Highlight> highlightList=(List<Highlight>)q.getResultList();
		highlightObservableList =FXCollections.observableList(highlightList);
		System.out.println("exiting retrieve persistence");
		System.out.println("highlights retrieved: "+highlightList.size());
		ModelConnector.setHighlightsFound(highlightObservableList);
		eBook=(EBook)qb.getSingleResult();
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
	
	public EBook geteBook() {
		return eBook;
	}
	public void seteBook(EBook eBook) {
		this.eBook = eBook;
	}

	public ObservableList<Highlight> getHighlightObservableList() {
		return highlightObservableList;
	}
	public ObservableList<EBook> getEbookObservableList() {
		return ebookObservableList;
	}
}
