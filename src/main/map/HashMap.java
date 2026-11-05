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

  private Entry<K, V>[] buckets;  // one bucket per slot, or null when the slot is empty
  private int size;               // how many pairs the map holds
  private int primeIndex;         // the current capacity is PRIMES[primeIndex]

  private static class Entry<K, V> implements Map.Entry<K, V> {
    K key;
    V value;
    Entry<K, V> next;

    Entry(K key, V value) {
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

  // buckets only ever holds Entry<K, V>, so the cast is safe.
  @SuppressWarnings("unchecked")
  public HashMap() {
    buckets = (Entry<K, V>[]) new Entry[PRIMES[0]];  // start with 7 slots
    size = 0;                                        // the map starts empty
    primeIndex = 0;                                  // the capacity is PRIMES[0]
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
    Entry<K, V> previous = null;
    for (Entry<K, V> e = buckets[i]; e != null; e = e.next) {
      if (e.key.equals(key)) {
        e.value = value;  // key present; replace its value
        return false;
      }
      previous = e;
    }
    Entry<K, V> newEntry = new Entry<>(key, value);
    if (previous == null) {
      buckets[i] = newEntry;     // the bucket was empty
    } else {
      previous.next = newEntry;  // append after the last entry
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
    for (Entry<K, V> e = buckets[i]; e != null; e = e.next) {
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
    for (Entry<K, V> e = buckets[i]; e != null; e = e.next) {
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
    Entry<K, V> previous = null;
    for (Entry<K, V> e = buckets[i]; e != null; e = e.next) {
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
    Entry<K, V>[] old = buckets;
    int capacity;
    if (primeIndex + 1 < PRIMES.length) {
      capacity = PRIMES[++primeIndex];   // the next prime, already more than double
    } else {
      capacity = 2 * old.length + 1;     // past the table: double and add one to stay odd
    }
    buckets = (Entry<K, V>[]) new Entry[capacity];

    for (Entry<K, V> head : old) {                          // each old bucket
      Entry<K, V> e = head;
      while (e != null) {
        Entry<K, V> next = e.next;                          // save the rest before relinking
        int i = Math.floorMod(e.key.hashCode(), capacity);  // the entry's index in the new array
        e.next = buckets[i];                                // move e to the front of its new bucket
        buckets[i] = e;
        e = next;
      }
    }
  }

  // Visits each slot in order and traverses its bucket, returning each entry's key.
  private class KeyIterator implements Iterator<K> {
    private Entry<K, V> current;  // the entry whose key comes next, or null when the traversal is done
    private int slot = 0;         // the next slot to try when the current bucket runs out

    KeyIterator() {
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
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      K key = current.key;
      advance();
      return key;
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
    private Entry<K, V> current;  // the entry that comes next, or null when the traversal is done
    private int slot = 0;         // the next slot to try when the current bucket runs out

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
      Entry<K, V> entry = current;
      advance();
      return entry;
    }
  }
}
