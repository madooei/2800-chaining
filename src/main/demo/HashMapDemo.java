package demo;

import map.HashMap;
import map.Map;

/** A demo of HashMap on the chapter's trace, where bee, pig, and hen share one bucket. */
public final class HashMapDemo {

  private HashMapDemo() {
    // This class should not be instantiated!
  }

  public static void main(String[] args) {
    Map<String, Integer> counts = new HashMap<>();

    // Each key is new, so expect true four times and size 4.
    // pig and hen land in bee's bucket.
    System.out.println("put cat: " + counts.put("cat", 10));
    System.out.println("put bee: " + counts.put("bee", 40));
    System.out.println("put pig: " + counts.put("pig", 50));
    System.out.println("put hen: " + counts.put("hen", 60));
    System.out.println("size: " + counts.size());

    // bee is already there, so expect false, the new value 99, and size still 4.
    System.out.println("put bee again: " + counts.put("bee", 99));
    System.out.println("get bee: " + counts.get("bee"));
    System.out.println("size: " + counts.size());

    // Expect 60 for hen.
    System.out.println("get hen: " + counts.get("hen"));

    // Expect true for pig and hen, false for fox, which was never put, and size 2.
    System.out.println("remove pig: " + counts.remove("pig"));
    System.out.println("remove hen: " + counts.remove("hen"));
    System.out.println("remove fox: " + counts.remove("fox"));
    System.out.println("size: " + counts.size());

    // Expect the two keys left, cat and bee, in the order the iterator visits them.
    System.out.println("keys:");
    for (String key : counts) {
      System.out.println("  " + key);
    }
  }
}
