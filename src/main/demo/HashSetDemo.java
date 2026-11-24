package demo;

import set.HashSet;
import set.Set;

/** A demo of HashSet on the chapter's keys, where bee, pig, and hen share one bucket. */
public final class HashSetDemo {

  private HashSetDemo() {
    // This class should not be instantiated!
  }

  public static void main(String[] args) {
    Set<String> animals = new HashSet<>();
    animals.add("cat");
    animals.add("bee");
    animals.add("pig");
    animals.add("hen");
    System.out.println("size after adding cat, bee, pig, hen: " + animals.size());

    // pig is already in the set, so expect false and no change in size.
    System.out.println("add(\"pig\") again: " + animals.add("pig"));

    // Expect true for pig, which shares a bucket with bee and hen, and false for fox.
    System.out.println("contains(\"pig\"): " + animals.contains("pig"));
    System.out.println("contains(\"fox\"): " + animals.contains("fox"));

    // Expect true, then a size of 3.
    System.out.println("remove(\"pig\"): " + animals.remove("pig"));
    System.out.println("size: " + animals.size());

    // Slot order, then each bucket from its head. Expect cat hen bee.
    String text = "";
    for (String animal : animals) {
      text += animal + " ";
    }
    System.out.println("elements: " + text.trim());
  }
}
