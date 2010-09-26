package name.nanek.gdwprototype.server.support;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

public class IteratorUtil {

	public static <T> T findOrNull(Iterator<T> iterator, Predicate<? super T> predicate) {
		try {
			return Iterators.find(iterator, predicate);
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
}
