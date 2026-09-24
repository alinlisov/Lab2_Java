import java.util.Objects;
public class MyCache {
    private static class CacheNode {
        Object key;
        Object value;
        long expireTime; // мс, коли запис застаріє
        CacheNode hashNext; // для бакета
        CacheNode lruPrev, lruNext; // для LRU списку
        CacheNode(Object key, Object value, long expireTime, CacheNode hashNext) {
            this.key = key;
            this.value = value;
            this.expireTime = expireTime;
            this.hashNext = hashNext;
        }
        boolean isExpired() {
            return expireTime > 0 && System.currentTimeMillis() > expireTime;
        }
    }
    private final int maxCapacity;
    private final long defaultTtlMillis; // 0 якщо без обмежень по часу
    private CacheNode[] table;
    private int size;
    private static final int INITIAL_BUCKETS = 16;
    // LRU Список: head — найдавніший, tail — найновіший
    private CacheNode lruHead;
    private CacheNode lruTail;
    public MyCache(int maxCapacity, long defaultTtlMillis) {
        if (maxCapacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        this.maxCapacity = maxCapacity;
        this.defaultTtlMillis = defaultTtlMillis;
        this.table = new CacheNode[INITIAL_BUCKETS];
        this.size = 0;
    }
    private int hash(Object key) {
        return Math.abs(key.hashCode());
    }
    private int getIndex(int hash, int length) {
        return hash % length;
    }
    public synchronized void put(Object key, Object value) {
        put(key, value, defaultTtlMillis);
    }
    public synchronized void put(Object key, Object value, long ttlMillis) {
        // Вимога 1: NullPointerException при null
        Objects.requireNonNull(key, "Cache key must not be null");
        Objects.requireNonNull(value, "Cache value must not be null");
        long expireTime = (ttlMillis > 0) ? System.currentTimeMillis() + ttlMillis : 0;
        int h = hash(key);
        int index = getIndex(h, table.length);
        // Пошук існуючого запису
        for (CacheNode node = table[index]; node != null; node = node.hashNext) {
            if (node.key.equals(key)) {
                node.value = value;
                node.expireTime = expireTime;
                moveToTail(node); // оновлюємо LRU
                return;
            }
        }

        // Перевірка на витіснення (Eviction)
        if (size >= maxCapacity) {
            evictOldest();
        }
        // Створення нового вузла
        CacheNode newNode = new CacheNode(key, value, expireTime, table[index]);
        table[index] = newNode;
        addToTail(newNode);
        size++;
    }
    public synchronized Object get(Object key) {
        // Вимога 1: NullPointerException при null
        Objects.requireNonNull(key, "Cache key must not be null");
        int h = hash(key);
        int index = getIndex(h, table.length);
        CacheNode prev = null;
        CacheNode node = table[index];
        while (node != null) {
            if (node.key.equals(key)) {
                // Вимога 2: Перевірка Expiry (термін дії)
                if (node.isExpired()) {
                    removeNode(prev, node, index);
                    return null; // Елемент застарів
                }
                // Оновлюємо доступ за LRU
                moveToTail(node);
                return node.value;
            }
            prev = node;
            node = node.hashNext;
        }
        return null;
    }

    public synchronized boolean remove(Object key) {
        Objects.requireNonNull(key, "Cache key must not be null");
        int h = hash(key);
        int index = getIndex(h, table.length);
        CacheNode prev = null;
        CacheNode node = table[index];
        while (node != null) {
            if (node.key.equals(key)) {
                removeNode(prev, node, index);
                return true;
            }
            prev = node;
            node = node.hashNext;
        }
        return false;
    }
    private void removeNode(CacheNode prev, CacheNode node, int index) {
        if (prev == null) {
            table[index] = node.hashNext;
        } else {
            prev.hashNext = node.hashNext;
        }
        removeFromLru(node);
        size--;
    }

    // Витіснення найстарішого елемента (LRU Eviction)
    private void evictOldest() {
        if (lruHead != null) {
            remove(lruHead.key);
        }
    }
    // Методи керування LRU-списком
    private void addToTail(CacheNode node) {
        if (lruTail == null) {
            lruHead = lruTail = node;
        } else {
            lruTail.lruNext = node;
            node.lruPrev = lruTail;
            lruTail = node;
        }
    }

    private void removeFromLru(CacheNode node) {
        if (node.lruPrev != null) {
            node.lruPrev.lruNext = node.lruNext;
        } else {
            lruHead = node.lruNext;
        }
        if (node.lruNext != null) {
            node.lruNext.lruPrev = node.lruPrev;
        } else {
            lruTail = node.lruPrev;
        }

        node.lruPrev = null;
        node.lruNext = null;
    }
    private void moveToTail(CacheNode node) {
        if (node == lruTail) return;
        removeFromLru(node);
        addToTail(node);
    }
    public synchronized int size() {
        return size;
    }
}