package map;

/**
 * Runs the chapter's trace: cat, bee, pig, and hen go into a seven-slot table,
 * where bee, pig, and hen share one bucket. Then bee is overwritten, hen is
 * looked up, pig is removed, and fox is not found.
 */
public class Main {

  public static void main(String[] args) {
    Map<String, Integer> counts = new HashMap<>();
    System.out.println("put cat: " + counts.put("cat", 10));  // true, the key is new
    System.out.println("put bee: " + counts.put("bee", 40));  // true
    System.out.println("put pig: " + counts.put("pig", 50));  // true, shares bee's bucket
    System.out.println("put hen: " + counts.put("hen", 60));  // true, shares it too
    System.out.println("size: " + counts.size());             // 4

    System.out.println("put bee again: " + counts.put("bee", 99));  // false, the key existed
    System.out.println("get bee: " + counts.get("bee"));            // 99
    System.out.println("size: " + counts.size());                   // still 4

    System.out.println("get hen: " + counts.get("hen"));      // 60
    System.out.println("remove pig: " + counts.remove("pig"));  // true
    System.out.println("remove fox: " + counts.remove("fox"));  // false, never put
    System.out.println("size: " + counts.size());               // 3

    System.out.println("keys:");
    for (String key : counts) {
      System.out.println("  " + key);
    }
  }
}
