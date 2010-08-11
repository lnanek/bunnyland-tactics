package name.nanek.gdwprototype.server;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Holds a global reference to the EntityManagerFactory, which is expensive to create.
 * 
 * @author Lance Nanek
 *
 */
public final class EMF {
	
	private static final EntityManagerFactory emfInstance = Persistence
			.createEntityManagerFactory("gamePlayPersistenceUnit");

	private EMF() {
	}

	public static EntityManagerFactory get() {
		return emfInstance;
	}
}