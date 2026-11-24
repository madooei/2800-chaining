# Separate Chaining

A hash table that resolves collisions by separate chaining: `HashMap` keeps a bucket, a linked list of entries, in each slot, and rehashes into a larger, prime-sized table when the load factor passes 0.75. `HashSet` applies the same strategy to the Set ADT. The `adaptiveheap` package uses this `HashMap` as the index map of the adaptive priority queue. JUnit suites check the map contract and the queue contract, and read the buckets and the heap through reflection to check the states the notes draw.

## Prerequisites

- JDK 17+
- The JUnit jar is already vendored in `lib/`; there is nothing to download.

## Repository layout

```plaintext
code/
  README.md
  .gitignore
  lib/
    junit-platform-console-standalone-6.1.0.jar
  src/
    main/
      map/
        Map.java                        # the Map ADT
        HashMap.java                    # a hash table with separate chaining
      set/
        Set.java                        # the Set ADT
        HashSet.java                    # the same hash table, holding elements instead of pairs
      adaptiveheap/
        AdaptivePriorityQueue.java      # the AdaptivePriorityQueue ADT
        IndexedArrayHeap.java           # a binary heap with a HashMap as its index map
      demo/                             # one demo per class you implement
        HashMapDemo.java
        HashSetDemo.java
    test/
      map/
        MapTest.java                    # abstract: the Map contract suite
        HashMapTest.java                # runs it against HashMap, then checks the buckets by reflection
      set/
        SetTest.java                    # abstract: the Set contract suite
        HashSetTest.java                # runs it against HashSet, then checks the buckets by reflection
      adaptiveheap/
        AdaptivePriorityQueueTest.java  # abstract: the AdaptivePriorityQueue contract suite
        IndexedArrayHeapTest.java       # runs it against IndexedArrayHeap, then checks the heap and map by reflection
  scripts/
    run.sh                              # compile and run one demo
    test.sh                             # compile and run the JUnit tests
```

## How to compile and run

- `scripts/test.sh` runs the full JUnit suite.
- `scripts/test.sh map.HashMapTest` runs only that test class.
- `scripts/run.sh demo.HashMapDemo` compiles everything and runs that one demo. Every class you implement has its own demo, `<Class>Demo`.

## Contents

- `map.Map<K, V>`: the Map ADT, copied unchanged from the earlier chapters.
- `map.HashMap<K, V>`: the chapter's hash table. It has `put`, `get`, `containsKey`, `remove`, and `size`, and the two iterators the `Map` interface asks for, `iterator()` over the keys and `entries()` over the pairs.
- `set.Set<T>`: the Set ADT, copied unchanged from the earlier chapters.
- `set.HashSet<T>`: a hash set built the same way as `HashMap`, with a bucket of elements in each slot, the same prime capacities, and the same rehash. It has `add`, `remove`, `contains`, `size`, and `iterator()`. The notes do not show this code; it applies the chapter's separate chaining to the Set ADT.
- `adaptiveheap.AdaptivePriorityQueue<T>` and `adaptiveheap.IndexedArrayHeap<T>`: copied from the adaptive priority queue chapter. The only change is the index map, now a `HashMap`, so the items no longer need to be `Comparable`.
- `demo.HashMapDemo`: runs the chapter's trace on `HashMap`, with `bee`, `pig`, and `hen` sharing one bucket.
- `demo.HashSetDemo`: adds the same keys to a `HashSet`, as elements.
- `map.MapTest` and `map.HashMapTest`: the Map contract suite, run against `HashMap`, then the slots and bucket order the notes draw, read through reflection, and the order the iterators visit the keys.
- `set.SetTest` and `set.HashSetTest`: the Set contract suite, run against `HashSet`, then the same bucket and growth checks as `HashMapTest`.
- `adaptiveheap.AdaptivePriorityQueueTest` and `adaptiveheap.IndexedArrayHeapTest`: the test suites from the adaptive priority queue chapter, plus one test that the items need not be `Comparable`.
