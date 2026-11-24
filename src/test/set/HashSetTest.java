package set;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Runs the Set contract suite against HashSet, then checks how it uses its buckets. */
public class HashSetTest extends SetTest {

  @Override
  protected Set<Integer> createSet() {
    return new HashSet<>();
  }

  @Test
  public void newSetHasSevenSlots() {
    assertEquals(7, bucketsOf(new HashSet<String>()).length);
  }

  @Test
  public void addPlacesAnElementInItsSlot() {
    HashSet<String> table = new HashSet<>();
    table.add("cat");
    assertEquals(listOf("cat"), itemsInSlot(table, 3));
  }

  @Test
  public void addPutsCollidingElementsAtTheHeadOfTheBucket() {
    HashSet<String> table = fourElements();
    assertEquals(listOf("hen", "pig", "bee"), itemsInSlot(table, 5));
  }

  @Test
  public void addLeavesOtherSlotsEmpty() {
    HashSet<String> table = fourElements();
    assertNull(bucketsOf(table)[6]);
  }

  @Test
  public void addPlacesANegativeHashCodeInSlotRange() {
    HashSet<String> table = new HashSet<>();
    table.add("elephant");  // hashCode -5491951, and floorMod(-5491951, 7) is 4
    assertEquals(listOf("elephant"), itemsInSlot(table, 4));
  }

  @Test
  public void addKeepsDifferentElementsWithTheSameHashCode() {
    HashSet<String> table = new HashSet<>();
    table.add("Aa");  // "Aa" and "BB" both have hashCode 2112
    table.add("BB");
    assertEquals(listOf("BB", "Aa"), itemsInSlot(table, 5));
  }

  @Test
  public void containsSeparatesDifferentElementsWithTheSameHashCode() {
    HashSet<String> table = new HashSet<>();
    table.add("Aa");
    assertFalse(table.contains("BB"));
  }

  @Test
  public void removeSeparatesDifferentElementsWithTheSameHashCode() {
    HashSet<String> table = new HashSet<>();
    table.add("Aa");
    table.add("BB");
    table.remove("BB");
    assertEquals(listOf("Aa"), itemsInSlot(table, 5));
  }

  @Test
  public void addMatchesAnExistingElementByEquals() {
    HashSet<String> table = new HashSet<>();
    table.add("cat");
    assertFalse(table.add(new String("cat")));
  }

  @Test
  public void removeMatchesAnElementByEquals() {
    HashSet<String> table = new HashSet<>();
    table.add("cat");
    assertTrue(table.remove(new String("cat")));
  }

  @Test
  public void containsFindsTheLastElementInABucket() {
    HashSet<String> table = fourElements();
    assertTrue(table.contains("bee"));
  }

  @Test
  public void addExistingElementKeepsTheBucketOrder() {
    HashSet<String> table = fourElements();
    table.add("bee");
    assertEquals(listOf("hen", "pig", "bee"), itemsInSlot(table, 5));
  }

  @Test
  public void removeHeadOfBucketKeepsTheRest() {
    HashSet<String> table = fourElements();
    table.remove("hen");
    assertEquals(listOf("pig", "bee"), itemsInSlot(table, 5));
  }

  @Test
  public void removeMiddleOfBucketLinksAroundIt() {
    HashSet<String> table = fourElements();
    table.remove("pig");
    assertEquals(listOf("hen", "bee"), itemsInSlot(table, 5));
  }

  @Test
  public void removeLastOfBucketEndsTheBucketEarly() {
    HashSet<String> table = fourElements();
    table.remove("bee");
    assertEquals(listOf("hen", "pig"), itemsInSlot(table, 5));
  }

  @Test
  public void removeOnlyElementInSlotEmptiesTheSlot() {
    HashSet<String> table = fourElements();
    table.remove("cat");
    assertNull(bucketsOf(table)[3]);
  }

  @Test
  public void iteratorVisitsTheSlotsInOrderAndEachBucketInOrder() {
    HashSet<String> table = fourElements();
    List<String> seen = new ArrayList<>();
    for (String item : table) {
      seen.add(item);
    }
    assertEquals(listOf("cat", "hen", "pig", "bee"), seen);
  }

  @Test
  public void iteratorSkipsEmptySlotsAtTheFront() {
    HashSet<String> table = new HashSet<>();
    table.add("fox");  // slot 6, the last slot
    assertEquals("fox", table.iterator().next());
  }

  @Test
  public void iteratorSkipsEmptySlotsAtTheEnd() {
    HashSet<String> table = new HashSet<>();
    table.add("cat");  // slot 3
    Iterator<String> it = table.iterator();
    it.next();
    assertFalse(it.hasNext());
  }

  @Test
  public void iteratorVisitsEveryElementInABucket() {
    HashSet<String> table = new HashSet<>();
    table.add("bee");  // bee, pig, and hen all go to slot 5
    table.add("pig");
    table.add("hen");
    assertEquals(listOf("hen", "pig", "bee"), itemsOf(table));
  }

  @Test
  public void iteratorOrderChangesAfterTheTableGrows() {
    HashSet<String> table = sixElements();  // the sixth add grows the table to 17 slots
    table.remove("fox");
    table.remove("dog");
    assertEquals(listOf("bee", "cat", "hen", "pig"), itemsOf(table));
  }

  @Test
  public void fifthAddKeepsSevenSlots() {
    HashSet<String> table = fourElements();
    table.add("fox");
    assertEquals(7, bucketsOf(table).length);
  }

  @Test
  public void addExistingElementAtTheLoadFactorLimitKeepsSevenSlots() {
    HashSet<String> table = fourElements();
    table.add("fox");
    table.add("bee");
    assertEquals(7, bucketsOf(table).length);
  }

  @Test
  public void sixthAddGrowsToSeventeenSlots() {
    HashSet<String> table = sixElements();
    assertEquals(17, bucketsOf(table).length);
  }

  @Test
  public void growthPlacesEachElementAtItsNewSlot() {
    HashSet<String> table = sixElements();
    assertEquals(listOf("bee"), itemsInSlot(table, 0));
    assertEquals(listOf("cat"), itemsInSlot(table, 2));
    assertEquals(listOf("hen"), itemsInSlot(table, 12));
    assertEquals(listOf("pig"), itemsInSlot(table, 14));
  }

  @Test
  public void thirteenthAddGrowsToThirtySevenSlots() {
    HashSet<String> table = new HashSet<>();
    for (int i = 1; i <= 13; i++) {
      table.add("k" + i);
    }
    assertEquals(37, bucketsOf(table).length);
  }

  @Test
  public void addPastTheLastPrimeDoublesAndAddsOne() {
    HashSet<String> table = new HashSet<>();
    for (int i = 1; i <= 1052731; i++) {  // the first count above 0.75 * 1403641
      table.add("k" + i);
    }
    assertEquals(2 * 1403641 + 1, bucketsOf(table).length);
  }

  @Test
  public void manyAddsKeepEveryElement() {
    Set<String> table = new HashSet<>();
    for (int i = 0; i < 500; i++) {
      table.add("k" + i);
    }
    for (int i = 0; i < 500; i++) {
      assertTrue(table.contains("k" + i));
    }
  }

  @Test
  public void manyAddsKeepTheSize() {
    Set<String> table = new HashSet<>();
    for (int i = 0; i < 500; i++) {
      table.add("k" + i);
    }
    assertEquals(500, table.size());
  }

  // The four keys of the chapter's trace, used as elements: cat in slot 3, and
  // bee, pig, and hen sharing slot 5 of a seven-slot table.
  private static HashSet<String> fourElements() {
    HashSet<String> table = new HashSet<>();
    table.add("cat");
    table.add("bee");
    table.add("pig");
    table.add("hen");
    return table;
  }

  // The four elements, plus two more: the sixth add passes the 0.75 load factor.
  private static HashSet<String> sixElements() {
    HashSet<String> table = fourElements();
    table.add("fox");
    table.add("dog");
    return table;
  }

  private static List<String> listOf(String... items) {
    List<String> list = new ArrayList<>();
    for (String item : items) {
      list.add(item);
    }
    return list;
  }

  private static List<String> itemsOf(HashSet<String> table) {
    List<String> items = new ArrayList<>();
    for (String item : table) {
      items.add(item);
    }
    return items;
  }

  // The elements in the bucket at the given slot, from the head of the bucket on.
  private static List<String> itemsInSlot(HashSet<String> table, int slot) {
    List<String> items = new ArrayList<>();
    Object entry = bucketsOf(table)[slot];
    while (entry != null) {
      items.add((String) readField(entry, "item"));
      entry = readField(entry, "next");
    }
    return items;
  }

  private static Object[] bucketsOf(HashSet<String> table) {
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
