package name.nanek.gdwprototype.server;

import com.vercer.engine.persist.ObjectDatastore;
import com.vercer.engine.persist.annotation.AnnotationObjectDatastore;

/**
 * Creates Twig-persist ORM library data stores.
 * 
 * @author Lance Nanek
 *
 */
public final class DbUtil {
	
	public static ObjectDatastore createObjectDatastore() {
		return new AnnotationObjectDatastore();
	}
	
}