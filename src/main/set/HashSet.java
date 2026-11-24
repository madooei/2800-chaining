package set;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A hash table implementation of the Set ADT that resolves collisions by
 * separate chaining.
 *
 * @param <T> the type of elements in this set.
 */
public class HashSet<T> implements Set<T> {

  private static final int[] PRIMES = {
      7, 17, 37, 79, 163, 331, 673, 1361, 2729, 5471,
      10949, 21911, 43853, 87719, 175447, 350899, 701819, 1403641
  };

  private static final double MAX_LOAD_FACTOR = 0.75;

  private Node<T>[] buckets;  // one bucket per slot, or null when the slot is empty
  private int size;           // how many elements the set holds
  private int primeIndex;     // the current capacity is PRIMES[primeIndex]

  private static class Node<T> {
    T item;
    Node<T> next;

    Node(T item) {
      this.item = item;
    }
  }

  // buckets only ever holds Node<T>, so the cast is safe.
  @SuppressWarnings("unchecked")
  public HashSet() {
    // TODO: Implement me
    throw new UnsupportedOperationException("TODO: Implement me");
  }

  @Override
  public boolean add(T item) {
    // TODO: Implement me
    throw new UnsupportedOperationException("TODO: Implement me");
  }

  @Override
  public boolean remove(T item) {
    // TODO: Implement me
    throw new UnsupportedOperationException("TODO: Implement me");
  }

  @Override
  public boolean contains(T item) {
    // TODO: Implement me
    throw new UnsupportedOperationException("TODO: Implement me");
  }

  @Override
  public int size() {
    // TODO: Implement me
    throw new UnsupportedOperationException("TODO: Implement me");
  }

  @Override
  public Iterator<T> iterator() {
    // TODO: Implement me
    throw new UnsupportedOperationException("TODO: Implement me");
  }

  // Turns an element into the index of its bucket's slot: hash, then compress to the array's range.
  private int indexFor(T item) {
    // TODO: Implement me
    throw new UnsupportedOperationException("TODO: Implement me");
  }

  // Grows the table to the next prime and places every element at the index the
  // new capacity gives it. size does not change. Same cast rationale as the
  // constructor.
  @SuppressWarnings("unchecked")
  private void rehash() {
    // TODO: Implement me
    throw new UnsupportedOperationException("TODO: Implement me");
  }

  // Visits each slot in order and traverses its bucket, returning each element.
  private class SetIterator implements Iterator<T> {

    SetIterator() {
      // TODO: Implement me
      throw new UnsupportedOperationException("TODO: Implement me");
    }


    @Override
    public boolean hasNext() {
      // TODO: Implement me
      throw new UnsupportedOperationException("TODO: Implement me");
    }

    @Override
    public T next() {
      // TODO: Implement me
      throw new UnsupportedOperationException("TODO: Implement me");
    }
  }
}
