package name.nanek.gdwprototype.server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class DbUtil {
	private static final String PERSISTENCE_UNIT = "gamePlayPersistenceUnit";

	private static final EntityManagerFactory cachedEntityManagerFactory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT);

	private DbUtil() {
	}

	public static EntityManagerFactory getEntityManagerFactory() {
		return cachedEntityManagerFactory;
	}

	public static EntityManager createEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}
}