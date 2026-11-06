package map;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A hash table implementation of the Map ADT that resolves collisions by
 * separate chaining.
 *
 * @param <K> the type of keys in this map.
 * @param <V> the type of values in this map.
 */
public class HashMap<K, V> implements Map<K, V> {

  private static final int[] PRIMES = {
      7, 17, 37, 79, 163, 331, 673, 1361, 2729, 5471,
      10949, 21911, 43853, 87719, 175447, 350899, 701819, 1403641
  };

  private static final double MAX_LOAD_FACTOR = 0.75;

  private Node<K, V>[] buckets;  // one bucket per slot, or null when the slot is empty
  private int size;              // how many pairs the map holds
  private int primeIndex;        // the current capacity is PRIMES[primeIndex]

  private static class Node<K, V> implements Map.Entry<K, V> {
    K key;
    V value;
    Node<K, V> next;

    Node(K key, V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public K key() {
      return key;
    }

    @Override
    public V value() {
      return value;
    }
  }

  // buckets only ever holds Node<K, V>, so the cast is safe.
  @SuppressWarnings("unchecked")
  public HashMap() {
    buckets = (Node<K, V>[]) new Node[PRIMES[0]];  // start with 7 slots
    size = 0;                                      // the map starts empty
    primeIndex = 0;                                // the capacity is PRIMES[0]
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean put(K key, V value) {
    if (key == null || value == null) {
      throw new IllegalArgumentException();
    }
    int i = indexFor(key);
    Node<K, V> previous = null;
    for (Node<K, V> e = buckets[i]; e != null; e = e.next) {
      if (e.key.equals(key)) {
        e.value = value;  // key present; replace its value
        return false;
      }
      previous = e;
    }
    Node<K, V> newNode = new Node<>(key, value);
    if (previous == null) {
      buckets[i] = newNode;     // the bucket was empty
    } else {
      previous.next = newNode;  // append after the last entry
    }
    size++;
    if (size > MAX_LOAD_FACTOR * buckets.length) {  // the load factor passed the threshold
      rehash();
    }
    return true;
  }

  @Override
  public V get(K key) {
    if (key == null) {
      throw new IllegalArgumentException();
    }
    int i = indexFor(key);
    for (Node<K, V> e = buckets[i]; e != null; e = e.next) {
      if (e.key.equals(key)) {
        return e.value;  // found the key
      }
    }
    return null;  // key not present
  }

  @Override
  public boolean containsKey(K key) {
    if (key == null) {
      throw new IllegalArgumentException();
    }
    int i = indexFor(key);
    for (Node<K, V> e = buckets[i]; e != null; e = e.next) {
      if (e.key.equals(key)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean remove(K key) {
    if (key == null) {
      throw new IllegalArgumentException();
    }
    int i = indexFor(key);
    Node<K, V> previous = null;
    for (Node<K, V> e = buckets[i]; e != null; e = e.next) {
      if (e.key.equals(key)) {
        if (previous == null) {
          buckets[i] = e.next;      // the match was the head of the bucket
        } else {
          previous.next = e.next;   // unlink the match
        }
        size--;
        return true;
      }
      previous = e;
    }
    return false;  // not found; nothing changed
  }

  @Override
  public Iterator<K> iterator() {
    return new KeyIterator();
  }

  @Override
  public Iterable<Map.Entry<K, V>> entries() {
    return new Entries();
  }

  // Turns a key into the index of its bucket's slot: hash, then compress to the array's range.
  private int indexFor(K key) {
    return Math.floorMod(key.hashCode(), buckets.length);
  }

  // Grows the table to the next prime and places every entry at the index the
  // new capacity gives it. size does not change. Same cast rationale as the
  // constructor.
  @SuppressWarnings("unchecked")
  private void rehash() {
    Node<K, V>[] old = buckets;
    int capacity;
    if (primeIndex + 1 < PRIMES.length) {
      capacity = PRIMES[++primeIndex];   // the next prime, already more than double
    } else {
      capacity = 2 * old.length + 1;     // past the table: double and add one to stay odd
    }
    buckets = (Node<K, V>[]) new Node[capacity];

    for (Node<K, V> head : old) {                           // each old bucket
      Node<K, V> e = head;
      while (e != null) {
        Node<K, V> next = e.next;                           // save the rest before relinking
        int i = Math.floorMod(e.key.hashCode(), capacity);  // the entry's index in the new array
        e.next = buckets[i];                                // move e to the front of its new bucket
        buckets[i] = e;
        e = next;
      }
    }
  }

  // Returns the key of each entry, using an EntryIterator for the traversal.
  private class KeyIterator implements Iterator<K> {
    private Iterator<Map.Entry<K, V>> entries;  // the traversal this iterator takes its keys from

    KeyIterator() {
      entries = new EntryIterator();
    }

    @Override
    public boolean hasNext() {
      return entries.hasNext();
    }

    @Override
    public K next() {
      Map.Entry<K, V> entry = entries.next();
      return entry.key();
    }
  }

  // The Iterable that entries() returns. Each call to iterator() starts a new traversal.
  private class Entries implements Iterable<Map.Entry<K, V>> {
    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
      return new EntryIterator();
    }
  }

  // Visits each slot in order and traverses its bucket, returning each entry itself.
  private class EntryIterator implements Iterator<Map.Entry<K, V>> {
    private Node<K, V> current;  // the entry that comes next, or null when the traversal is done
    private int slot = 0;        // the next slot to try when the current bucket runs out

    EntryIterator() {
      advance();  // position current on the first entry, if there is one
    }

    private void advance() {
      if (current != null) {
        current = current.next;    // the next entry in the same bucket
      }
      while (current == null && slot < buckets.length) {
        current = buckets[slot];   // the head of the next bucket, or null for an empty slot
        slot++;
      }
    }

    @Override
    public boolean hasNext() {
      return current != null;
    }

    @Override
    public Map.Entry<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      Node<K, V> node = current;
      advance();
      return node;
    }
  }
}
