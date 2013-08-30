package de.fhg.igd.pcolor.util;

/**
 * This models a predicate on a (colour) type. We could have used guava but
 * just this interface was deemed too little justification.
 * @author Simon Thum
 * @param <T> the predicated type
 */
public interface Predicate<T> {
	/**
	 * @param object the object to inspect
	 * @return the truth value derived from the given object
	 */
	boolean apply(T object);
}
