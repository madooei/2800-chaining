package set;

/**
 * A Set is a collection of distinct elements, with no order.
 * A set is iterable: an enhanced for loop visits every element, in no particular order.
 *
 * @param <T> the type of elements in this set.
 */
public interface Set<T> extends Iterable<T> {

  /**
   * Adds an item to this set if it is not already present.
   *
   * @param item the item to be added to this set.
   * @return true if the item was added, false if it was already present.
   * @throws IllegalArgumentException if the item is null.
   */
  boolean add(T item);

  /**
   * Removes an item from this set if it is present.
   *
   * @param item the item to be removed from this set.
   * @return true if the item was removed, false if it was not present.
   * @throws IllegalArgumentException if the item is null.
   */
  boolean remove(T item);

  /**
   * Returns true if this set contains the given item.
   *
   * @param item the item to look for.
   * @return true if the item is in this set, false otherwise.
   * @throws IllegalArgumentException if the item is null.
   */
  boolean contains(T item);

  /**
   * Returns the number of items in this set.
   *
   * @return the number of items in this set.
   */
  int size();
}
