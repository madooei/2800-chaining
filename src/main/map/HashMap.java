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
    Node<K, V> curr = buckets[i];
    while (curr != null) {
      if (curr.key.equals(key)) {
        curr.value = value;  // key present; replace its value
        return false;
      }
      curr = curr.next;
    }
    Node<K, V> node = new Node<>(key, value);
    node.next = buckets[i];  // the new entry goes at the head of the bucket
    buckets[i] = node;
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
    Node<K, V> curr = buckets[i];
    while (curr != null) {
      if (curr.key.equals(key)) {
        return curr.value;  // found the key
      }
      curr = curr.next;
    }
    return null;  // key not present
  }

  @Override
  public boolean containsKey(K key) {
    if (key == null) {
      throw new IllegalArgumentException();
    }
    int i = indexFor(key);
    Node<K, V> curr = buckets[i];
    while (curr != null) {
      if (curr.key.equals(key)) {
        return true;
      }
      curr = curr.next;
    }
    return false;
  }

  @Override
  public boolean remove(K key) {
    if (key == null) {
      throw new IllegalArgumentException();
    }
    int i = indexFor(key);
    Node<K, V> prev = null;
    Node<K, V> curr = buckets[i];
    while (curr != null) {
      if (curr.key.equals(key)) {
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
      capacity = 2 * old.length + 1;     // past the array: double and add one to stay odd
    }
    buckets = (Node<K, V>[]) new Node[capacity];

    for (Node<K, V> head : old) {     // each old bucket
      Node<K, V> curr = head;
      while (curr != null) {
        Node<K, V> next = curr.next;  // save the rest before relinking
        int i = indexFor(curr.key);   // buckets is the new array, so this is the new index
        curr.next = buckets[i];       // move curr to the head of its new bucket
        buckets[i] = curr;
        curr = next;
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
    private Node<K, V> curr;     // the entry that comes next, or null when the traversal is done
    private int slot = 0;        // the next slot to try when the current bucket runs out

    EntryIterator() {
      advance();  // position curr on the first entry, if there is one
    }

    private void advance() {
      if (curr != null) {
        curr = curr.next;          // the next entry in the same bucket
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
    public Map.Entry<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      Node<K, V> node = curr;
      advance();
      return node;
    }
  }
}
