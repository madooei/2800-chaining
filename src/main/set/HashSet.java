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
    buckets = (Node<T>[]) new Node[PRIMES[0]];  // start with 7 slots
    size = 0;                                   // the set starts empty
    primeIndex = 0;                             // the capacity is PRIMES[0]
  }

  @Override
  public boolean add(T item) {
    if (item == null) {
      throw new IllegalArgumentException();
    }
    int i = indexFor(item);
    Node<T> curr = buckets[i];
    while (curr != null) {
      if (curr.item.equals(item)) {
        return false;  // already in the set; nothing changes
      }
      curr = curr.next;
    }
    Node<T> node = new Node<>(item);
    node.next = buckets[i];  // the new element goes at the head of the bucket
    buckets[i] = node;
    size++;
    if (size > MAX_LOAD_FACTOR * buckets.length) {  // the load factor passed the threshold
      rehash();
    }
    return true;
  }

  @Override
  public boolean remove(T item) {
    if (item == null) {
      throw new IllegalArgumentException();
    }
    int i = indexFor(item);
    Node<T> prev = null;
    Node<T> curr = buckets[i];
    while (curr != null) {
      if (curr.item.equals(item)) {
        if (prev == null) {
          buckets[i] = curr.next;  // the match was the head of the bucket
        } else {
          prev.next = curr.next;   // unlink the match
        }
        size--;
        return true;
      }
      prev = curr;
      curr = curr.next;
    }
    return false;  // not found; nothing changed
  }

  @Override
  public boolean contains(T item) {
    if (item == null) {
      throw new IllegalArgumentException();
    }
    int i = indexFor(item);
    Node<T> curr = buckets[i];
    while (curr != null) {
      if (curr.item.equals(item)) {
        return true;
      }
      curr = curr.next;
    }
    return false;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public Iterator<T> iterator() {
    return new SetIterator();
  }

  // Turns an element into the index of its bucket's slot: hash, then compress to the array's range.
  private int indexFor(T item) {
    return Math.floorMod(item.hashCode(), buckets.length);
  }

  // Grows the table to the next prime and places every element at the index the
  // new capacity gives it. size does not change. Same cast rationale as the
  // constructor.
  @SuppressWarnings("unchecked")
  private void rehash() {
    Node<T>[] old = buckets;
    int capacity;
    if (primeIndex + 1 < PRIMES.length) {
      capacity = PRIMES[++primeIndex];   // the next prime, already more than double
    } else {
      capacity = 2 * old.length + 1;     // past the array: double and add one to stay odd
    }
    buckets = (Node<T>[]) new Node[capacity];

    for (Node<T> head : old) {        // each old bucket
      Node<T> curr = head;
      while (curr != null) {
        Node<T> next = curr.next;     // save the rest before relinking
        int i = indexFor(curr.item);  // buckets is the new array, so this is the new index
        curr.next = buckets[i];       // move curr to the head of its new bucket
        buckets[i] = curr;
        curr = next;
      }
    }
  }

  // Visits each slot in order and traverses its bucket, returning each element.
  private class SetIterator implements Iterator<T> {
    private Node<T> curr;     // the node that comes next, or null when the traversal is done
    private int slot = 0;     // the next slot to try when the current bucket runs out

    SetIterator() {
      advance();  // position curr on the first element, if there is one
    }

    private void advance() {
      if (curr != null) {
        curr = curr.next;          // the next element in the same bucket
      }
      while (curr == null && slot < buckets.length) {
        curr = buckets[slot];      // the head of the next bucket, or null for an empty slot
        slot++;
      }
    }

    @Override
    public boolean hasNext() {
      return curr != null;
    }

    @Override
    public T next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      T item = curr.item;
      advance();
      return item;
    }
  }
}
