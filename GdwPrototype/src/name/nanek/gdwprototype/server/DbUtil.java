package name.nanek.gdwprototype.server;

import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.AnnotationObjectDatastore;

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