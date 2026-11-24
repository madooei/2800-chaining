package set;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * The contract test suite for the Set ADT, written against the Set interface
 * alone. A concrete subclass supplies createSet() to pick the implementation.
 */
public abstract class SetTest {

  protected Set<Integer> set;

  protected abstract Set<Integer> createSet();

  @BeforeEach
  public void setup() {
    set = createSet();
  }

  @Test
  public void newSetIsEmpty() {
    assertEquals(0, set.size());
  }

  @Test
  public void containsOnEmptySetReturnsFalse() {
    assertFalse(set.contains(4));
  }

  @Test
  public void removeOnEmptySetReturnsFalse() {
    assertFalse(set.remove(4));
  }

  @Test
  public void removeOnEmptySetLeavesItEmpty() {
    set.remove(4);
    assertEquals(0, set.size());
  }

  @Test
  public void iteratorOnEmptySetHasNoElements() {
    assertFalse(set.iterator().hasNext());
  }

  @Test
  public void addNewItemReturnsTrue() {
    assertTrue(set.add(4));
  }

  @Test
  public void addGrowsSize() {
    set.add(4);
    assertEquals(1, set.size());
  }

  @Test
  public void containsAddedItemReturnsTrue() {
    set.add(4);
    assertTrue(set.contains(4));
  }

  @Test
  public void containsAbsentItemReturnsFalse() {
    set.add(4);
    assertFalse(set.contains(7));
  }

  @Test
  public void containsDoesNotChangeSize() {
    set.add(4);
    set.contains(4);
    assertEquals(1, set.size());
  }

  @Test
  public void addDuplicateReturnsFalse() {
    set.add(4);
    assertFalse(set.add(4));
  }

  @Test
  public void addDuplicateDoesNotGrowSize() {
    set.add(4);
    set.add(4);
    assertEquals(1, set.size());
  }

  @Test
  public void removeOnlyItemReturnsTrue() {
    set.add(4);
    assertTrue(set.remove(4));
  }

  @Test
  public void removeOnlyItemLeavesEmpty() {
    set.add(4);
    set.remove(4);
    assertEquals(0, set.size());
  }

  @Test
  public void containsRemovedItemReturnsFalse() {
    set.add(4);
    set.remove(4);
    assertFalse(set.contains(4));
  }

  @Test
  public void removeAbsentItemReturnsFalse() {
    set.add(4);
    assertFalse(set.remove(7));
  }

  @Test
  public void removeAbsentItemLeavesSizeUnchanged() {
    set.add(4);
    set.remove(7);
    assertEquals(1, set.size());
  }

  @Test
  public void removeAbsentItemKeepsTheExistingItem() {
    set.add(4);
    set.remove(7);
    assertTrue(set.contains(4));
  }

  @Test
  public void addRemovedItemAgainReturnsTrue() {
    set.add(4);
    set.remove(4);
    assertTrue(set.add(4));
  }

  @Test
  public void oneElementIteratorReturnsThatElement() {
    set.add(7);
    Iterator<Integer> it = set.iterator();
    assertEquals(7, it.next());
  }

  @Test
  public void oneElementIteratorHasNoNextAfterIt() {
    set.add(7);
    Iterator<Integer> it = set.iterator();
    it.next();
    assertFalse(it.hasNext());
  }

  @Test
  public void nextPastEndThrows() {
    set.add(1);
    Iterator<Integer> it = set.iterator();
    it.next();
    try {
      it.next();
      fail("expected NoSuchElementException after the last element");
    } catch (NoSuchElementException e) {
      return;
    }
  }

  @Test
  public void hasNextDoesNotAdvance() {
    set.add(42);
    Iterator<Integer> it = set.iterator();
    it.hasNext();
    it.hasNext();
    assertEquals(42, it.next());
  }

  @Test
  public void addSeveralItemsGrowsSize() {
    set.add(4);
    set.add(9);
    set.add(6);
    assertEquals(3, set.size());
  }

  @Test
  public void containsReturnsTrueForEveryAddedItem() {
    set.add(4);
    set.add(9);
    set.add(6);
    assertTrue(set.contains(4));
    assertTrue(set.contains(9));
    assertTrue(set.contains(6));
  }

  @Test
  public void addDuplicateAmongSeveralDoesNotGrowSize() {
    set.add(4);
    set.add(9);
    set.add(6);
    set.add(9);
    assertEquals(3, set.size());
  }

  @Test
  public void removeOneOfSeveralShrinksSize() {
    set.add(4);
    set.add(9);
    set.add(6);
    set.remove(9);
    assertEquals(2, set.size());
  }

  @Test
  public void removeOneOfSeveralTakesItOut() {
    set.add(4);
    set.add(9);
    set.add(6);
    set.remove(9);
    assertFalse(set.contains(9));
  }

  @Test
  public void removeFirstAddedItemKeepsTheOthers() {
    set.add(4);
    set.add(9);
    set.add(6);
    set.remove(4);
    assertTrue(set.contains(9));
    assertTrue(set.contains(6));
  }

  @Test
  public void removeMiddleAddedItemKeepsTheOthers() {
    set.add(4);
    set.add(9);
    set.add(6);
    set.remove(9);
    assertTrue(set.contains(4));
    assertTrue(set.contains(6));
  }

  @Test
  public void removeLastAddedItemKeepsTheOthers() {
    set.add(4);
    set.add(9);
    set.add(6);
    set.remove(6);
    assertTrue(set.contains(4));
    assertTrue(set.contains(9));
  }

  @Test
  public void repeatedContainsKeepsEveryItem() {
    set.add(4);
    set.add(9);
    set.add(6);
    set.contains(4);
    set.contains(4);
    assertTrue(set.contains(4));
    assertTrue(set.contains(9));
    assertTrue(set.contains(6));
  }

  @Test
  public void repeatedContainsDoesNotChangeSize() {
    set.add(4);
    set.add(9);
    set.add(6);
    set.contains(4);
    set.contains(4);
    assertEquals(3, set.size());
  }

  @Test
  public void removeAfterContainsTakesTheItemOut() {
    set.add(4);
    set.add(9);
    set.add(6);
    set.contains(4);
    set.remove(4);
    assertFalse(set.contains(4));
  }

  @Test
  public void removeAfterContainsKeepsTheOthers() {
    set.add(4);
    set.add(9);
    set.add(6);
    set.contains(4);
    set.remove(4);
    assertTrue(set.contains(9));
    assertTrue(set.contains(6));
  }

  @Test
  public void removingEveryItemLeavesSetEmpty() {
    set.add(4);
    set.add(9);
    set.add(6);
    set.remove(9);
    set.remove(4);
    set.remove(6);
    assertEquals(0, set.size());
  }

  @Test
  public void iteratorVisitsEveryElement() {
    set.add(1);
    set.add(2);
    set.add(3);
    List<Integer> seen = new ArrayList<>();
    for (Integer x : set) {
      seen.add(x);
    }
    Collections.sort(seen);
    assertEquals(threeElements(), seen);
  }

  @Test
  public void nextWithoutHasNextWalksAll() {
    set.add(1);
    set.add(2);
    set.add(3);
    Iterator<Integer> it = set.iterator();
    List<Integer> seen = new ArrayList<>();
    seen.add(it.next());
    seen.add(it.next());
    seen.add(it.next());
    Collections.sort(seen);
    assertEquals(threeElements(), seen);
  }

  @Test
  public void twoIteratorsAreIndependent() {
    set.add(1);
    set.add(2);
    Iterator<Integer> first = set.iterator();
    Iterator<Integer> second = set.iterator();
    first.next();
    first.next();
    assertTrue(second.hasNext());
  }

  @Test
  public void fullWalkLeavesSizeUnchanged() {
    set.add(1);
    set.add(2);
    set.add(3);
    for (Integer x : set) {
      // just walk it
    }
    assertEquals(3, set.size());
  }

  @Test
  public void fullWalkKeepsEveryElement() {
    set.add(1);
    set.add(2);
    set.add(3);
    for (Integer x : set) {
      // just walk it
    }
    assertTrue(set.contains(1));
    assertTrue(set.contains(2));
    assertTrue(set.contains(3));
  }

  @Test
  public void addPastInitialCapacityGrowsSize() {
    for (int i = 1; i <= 12; i++) {
      set.add(i);
    }
    assertEquals(12, set.size());
  }

  @Test
  public void addPastInitialCapacityKeepsEveryItem() {
    for (int i = 1; i <= 12; i++) {
      set.add(i);
    }
    for (int i = 1; i <= 12; i++) {
      assertTrue(set.contains(i));
    }
  }

  @Test
  public void addInDescendingOrderPastInitialCapacityKeepsEveryItem() {
    for (int i = 12; i >= 1; i--) {
      set.add(i);
    }
    for (int i = 1; i <= 12; i++) {
      assertTrue(set.contains(i));
    }
  }

  @Test
  public void addNullThrows() {
    try {
      set.add(null);
      fail("expected IllegalArgumentException when adding null");
    } catch (IllegalArgumentException e) {
      return;
    }
  }

  @Test
  public void removeNullThrows() {
    try {
      set.remove(null);
      fail("expected IllegalArgumentException when removing null");
    } catch (IllegalArgumentException e) {
      return;
    }
  }

  @Test
  public void containsNullThrows() {
    try {
      set.contains(null);
      fail("expected IllegalArgumentException when calling contains with null");
    } catch (IllegalArgumentException e) {
      return;
    }
  }

  // The elements 1, 2, 3, in ascending order.
  private List<Integer> threeElements() {
    List<Integer> elements = new ArrayList<>();
    elements.add(1);
    elements.add(2);
    elements.add(3);
    return elements;
  }
}
