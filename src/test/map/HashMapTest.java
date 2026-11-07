package map;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Runs the Map contract suite against HashMap. */
public class HashMapTest extends MapTest {

  @Override
  protected Map<String, Integer> createMap() {
    return new HashMap<>();
  }

  @Test
  public void newMapHasSevenSlots() {
    assertEquals(7, bucketsOf(new HashMap<String, Integer>()).length);
  }

  @Test
  public void putPlacesAKeyInItsSlot() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("cat", 10);
    assertEquals(listOf("cat"), keysInSlot(table, 3));
  }

  @Test
  public void putAddsCollidingKeysAtTheHeadOfTheBucket() {
    HashMap<String, Integer> table = fourKeys();
    assertEquals(listOf("hen", "pig", "bee"), keysInSlot(table, 5));
  }

  @Test
  public void putLeavesOtherSlotsEmpty() {
    HashMap<String, Integer> table = fourKeys();
    assertNull(bucketsOf(table)[6]);
  }

  @Test
  public void putPlacesANegativeHashCodeInSlotRange() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("elephant", 1);  // hashCode -5491951, and floorMod(-5491951, 7) is 4
    assertEquals(listOf("elephant"), keysInSlot(table, 4));
  }

  @Test
  public void putKeepsDifferentKeysWithTheSameHashCode() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("Aa", 1);  // "Aa" and "BB" both have hashCode 2112
    table.put("BB", 2);
    assertEquals(listOf("BB", "Aa"), keysInSlot(table, 5));
  }

  @Test
  public void getSeparatesDifferentKeysWithTheSameHashCode() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("Aa", 1);
    table.put("BB", 2);
    assertEquals(2, table.get("BB"));
  }

  @Test
  public void containsKeySeparatesDifferentKeysWithTheSameHashCode() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("Aa", 1);
    assertFalse(table.containsKey("BB"));
  }

  @Test
  public void removeSeparatesDifferentKeysWithTheSameHashCode() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("Aa", 1);
    table.put("BB", 2);
    table.remove("BB");
    assertEquals(listOf("Aa"), keysInSlot(table, 5));
  }

  @Test
  public void putMatchesAnExistingKeyByEquals() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("cat", 10);
    assertFalse(table.put(new String("cat"), 20));
  }

  @Test
  public void removeMatchesAKeyByEquals() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("cat", 10);
    assertTrue(table.remove(new String("cat")));
  }

  @Test
  public void getFindsTheLastKeyInABucket() {
    HashMap<String, Integer> table = fourKeys();
    assertEquals(40, table.get("bee"));
  }

  @Test
  public void containsKeyFindsTheLastKeyInABucket() {
    HashMap<String, Integer> table = fourKeys();
    assertTrue(table.containsKey("bee"));
  }

  @Test
  public void putExistingKeyKeepsTheBucketOrder() {
    HashMap<String, Integer> table = fourKeys();
    table.put("bee", 99);
    assertEquals(listOf("hen", "pig", "bee"), keysInSlot(table, 5));
  }

  @Test
  public void removeHeadOfBucketKeepsTheRest() {
    HashMap<String, Integer> table = fourKeys();
    table.remove("hen");
    assertEquals(listOf("pig", "bee"), keysInSlot(table, 5));
  }

  @Test
  public void removeMiddleOfBucketLinksAroundIt() {
    HashMap<String, Integer> table = fourKeys();
    table.remove("pig");
    assertEquals(listOf("hen", "bee"), keysInSlot(table, 5));
  }

  @Test
  public void removeLastOfBucketEndsTheBucketEarly() {
    HashMap<String, Integer> table = fourKeys();
    table.remove("bee");
    assertEquals(listOf("hen", "pig"), keysInSlot(table, 5));
  }

  @Test
  public void removeOnlyKeyInSlotEmptiesTheSlot() {
    HashMap<String, Integer> table = fourKeys();
    table.remove("cat");
    assertNull(bucketsOf(table)[3]);
  }

  @Test
  public void iteratorVisitsTheSlotsInOrderAndEachBucketInOrder() {
    HashMap<String, Integer> table = fourKeys();
    List<String> seen = new ArrayList<>();
    for (String key : table) {
      seen.add(key);
    }
    assertEquals(listOf("cat", "hen", "pig", "bee"), seen);
  }

  @Test
  public void entriesVisitTheSlotsInOrderAndEachBucketInOrder() {
    HashMap<String, Integer> table = fourKeys();
    List<String> seen = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : table.entries()) {
      seen.add(entry.key() + "=" + entry.value());
    }
    assertEquals(listOf("cat=10", "hen=60", "pig=50", "bee=40"), seen);
  }

  @Test
  public void iteratorSkipsEmptySlotsAtTheFront() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("fox", 70);  // slot 6, the last slot
    assertEquals("fox", table.iterator().next());
  }

  @Test
  public void iteratorSkipsEmptySlotsAtTheEnd() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("cat", 10);  // slot 3
    Iterator<String> it = table.iterator();
    it.next();
    assertFalse(it.hasNext());
  }

  @Test
  public void iteratorVisitsEveryKeyInABucket() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("bee", 40);  // bee, pig, and hen all go to slot 5
    table.put("pig", 50);
    table.put("hen", 60);
    assertEquals(listOf("hen", "pig", "bee"), keysOf(table));
  }

  @Test
  public void iteratorOrderChangesAfterTheTableGrows() {
    HashMap<String, Integer> table = sixKeys();  // the sixth put grows the table to 17 slots
    table.remove("fox");
    table.remove("dog");
    assertEquals(listOf("bee", "cat", "hen", "pig"), keysOf(table));
  }

  @Test
  public void fifthPutKeepsSevenSlots() {
    HashMap<String, Integer> table = fourKeys();
    table.put("fox", 70);
    assertEquals(7, bucketsOf(table).length);
  }

  @Test
  public void putExistingKeyAtTheLoadFactorLimitKeepsSevenSlots() {
    HashMap<String, Integer> table = fourKeys();
    table.put("fox", 70);
    table.put("bee", 99);
    assertEquals(7, bucketsOf(table).length);
  }

  @Test
  public void sixthPutGrowsToSeventeenSlots() {
    HashMap<String, Integer> table = sixKeys();
    assertEquals(17, bucketsOf(table).length);
  }

  @Test
  public void growthPlacesEachKeyAtItsNewSlot() {
    HashMap<String, Integer> table = sixKeys();
    assertEquals(listOf("bee"), keysInSlot(table, 0));
    assertEquals(listOf("cat"), keysInSlot(table, 2));
    assertEquals(listOf("hen"), keysInSlot(table, 12));
    assertEquals(listOf("pig"), keysInSlot(table, 14));
  }

  @Test
  public void thirteenthPutGrowsToThirtySevenSlots() {
    HashMap<String, Integer> table = new HashMap<>();
    for (int i = 1; i <= 13; i++) {
      table.put("k" + i, i);
    }
    assertEquals(37, bucketsOf(table).length);
  }

  @Test
  public void putPastTheLastPrimeDoublesAndAddsOne() {
    HashMap<String, Integer> table = new HashMap<>();
    for (int i = 1; i <= 1052731; i++) {  // the first count above 0.75 * 1403641
      table.put("k" + i, i);
    }
    assertEquals(2 * 1403641 + 1, bucketsOf(table).length);
  }

  @Test
  public void manyPutsKeepEveryValue() {
    Map<String, Integer> table = new HashMap<>();
    for (int i = 0; i < 500; i++) {
      table.put("k" + i, i);
    }
    for (int i = 0; i < 500; i++) {
      assertEquals(i, table.get("k" + i));
    }
  }

  @Test
  public void manyPutsKeepTheSize() {
    Map<String, Integer> table = new HashMap<>();
    for (int i = 0; i < 500; i++) {
      table.put("k" + i, i);
    }
    assertEquals(500, table.size());
  }

  // The four keys of the chapter's trace: cat in slot 3, and bee, pig, and hen
  // sharing slot 5 of a seven-slot table.
  private static HashMap<String, Integer> fourKeys() {
    HashMap<String, Integer> table = new HashMap<>();
    table.put("cat", 10);
    table.put("bee", 40);
    table.put("pig", 50);
    table.put("hen", 60);
    return table;
  }

  // The four keys, plus two more: the sixth put passes the 0.75 load factor.
  private static HashMap<String, Integer> sixKeys() {
    HashMap<String, Integer> table = fourKeys();
    table.put("fox", 70);
    table.put("dog", 80);
    return table;
  }

  private static List<String> listOf(String... keys) {
    List<String> list = new ArrayList<>();
    for (String key : keys) {
      list.add(key);
    }
    return list;
  }

  private static List<String> keysOf(HashMap<String, Integer> table) {
    List<String> keys = new ArrayList<>();
    for (String key : table) {
      keys.add(key);
    }
    return keys;
  }

  // The keys in the bucket at the given slot, from the head of the bucket on.
  private static List<String> keysInSlot(HashMap<String, Integer> table, int slot) {
    List<String> keys = new ArrayList<>();
    Object entry = bucketsOf(table)[slot];
    while (entry != null) {
      keys.add((String) readField(entry, "key"));
      entry = readField(entry, "next");
    }
    return keys;
  }

  private static Object[] bucketsOf(HashMap<String, Integer> table) {
    return (Object[]) readField(table, "buckets");
  }

  // Reads a private field of an object by reflection.
  private static Object readField(Object object, String name) {
    try {
      Field field = object.getClass().getDeclaredField(name);
      field.setAccessible(true);
      return field.get(object);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
