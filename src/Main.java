import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. ТЕСТУВАННЯ MyArrayList та MyLinkedList");
        MyList arrayList = new MyArrayList();
        arrayList.add("Java");
        arrayList.add("C++");
        arrayList.addAll(new Object[]{"Python", "Go"});
        arrayList.add(1, "Kotlin");
        System.out.println("MyArrayList toArray: " + Arrays.toString(arrayList.toArray()));
        System.out.println("Element at 2: " + arrayList.get(2));
        System.out.println("RandomAccess support: " + (arrayList instanceof RandomAccess));

        MyList linkedList = new MyLinkedList();
        linkedList.add("100");
        linkedList.add("200");
        linkedList.add(1, "150");
        System.out.println("MyLinkedList toArray: " + Arrays.toString(linkedList.toArray()));

        System.out.println("\n=== 2. ТЕСТУВАННЯ MyLinkedHashSet ===");
        MyLinkedHashSet set = new MyLinkedHashSet();
        set.add("First");
        set.add("Second");
        set.add("Third");
        set.add("First"); // дублікат не додасться
        System.out.println("Set elements (впорядковані): " + Arrays.toString(set.toArray()));
        System.out.println("\n3. ТЕСТУВАННЯ MyCache (Null-check, TTL, LRU Eviction)");
        MyCache cache = new MyCache(2, 1000); // Max capacity = 2, TTL = 1000ms
        // Перевірка на NullPointerException
        try {
            cache.put(null, "value");
        } catch (NullPointerException e) {
            System.out.println("[УСПІХ] Null key заблоковано: " + e.getMessage());
        }
        // Перевірка LRU Eviction
        cache.put("K1", "V1");
        cache.put("K2", "V2");
        cache.get("K1");
        cache.put("K3", "V3"); // має витіснити K2
        System.out.println("K1 in cache: " + cache.get("K1")); // V1
        System.out.println("K2 in cache (має бути evicted -> null): " + cache.get("K2")); // null
        // Перевірка Expiry (TTL)
        System.out.println("\nЧекаємо закінчення TTL (1 сек)...");
        Thread.sleep(1100);
        System.out.println("K1 in cache after TTL (має бути expired -> null): " + cache.get("K1")); // null
    }
}