package map;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Runs the Map contract suite against HashMap, then reads its buckets by
 * reflection to check the slots and bucket order the notes draw.
 */
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
  public void putAppendsCollidingKeysToTheBucket() {
    HashMap<String, Integer> table = fourKeys();
    assertEquals(listOf("bee", "pig", "hen"), keysInSlot(table, 5));
  }

  @Test
  public void putLeavesOtherSlotsEmpty() {
    HashMap<String, Integer> table = fourKeys();
    assertNull(bucketsOf(table)[6]);
  }

  @Test
  public void getFindsTheLastKeyInABucket() {
    HashMap<String, Integer> table = fourKeys();
    assertEquals(60, table.get("hen"));
  }

  @Test
  public void putExistingKeyKeepsTheBucketOrder() {
    HashMap<String, Integer> table = fourKeys();
    table.put("bee", 99);
    assertEquals(listOf("bee", "pig", "hen"), keysInSlot(table, 5));
  }

  @Test
  public void removeHeadOfBucketKeepsTheRest() {
    HashMap<String, Integer> table = fourKeys();
    table.remove("bee");
    assertEquals(listOf("pig", "hen"), keysInSlot(table, 5));
  }

  @Test
  public void removeMiddleOfBucketLinksAroundIt() {
    HashMap<String, Integer> table = fourKeys();
    table.remove("pig");
    assertEquals(listOf("bee", "hen"), keysInSlot(table, 5));
  }

  @Test
  public void removeLastOfBucketEndsTheBucketEarly() {
    HashMap<String, Integer> table = fourKeys();
    table.remove("hen");
    assertEquals(listOf("bee", "pig"), keysInSlot(table, 5));
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
    assertEquals(listOf("cat", "bee", "pig", "hen"), seen);
  }

  @Test
  public void entriesVisitTheSlotsInOrderAndEachBucketInOrder() {
    HashMap<String, Integer> table = fourKeys();
    List<String> seen = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : table.entries()) {
      seen.add(entry.key());
    }
    assertEquals(listOf("cat", "bee", "pig", "hen"), seen);
  }

  @Test
  public void fifthPutKeepsSevenSlots() {
    HashMap<String, Integer> table = fourKeys();
    table.put("fox", 70);
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
