import java.util.Arrays;

public class MyLinkedHashSet {
    private static class Node {
        Object key;
        int hash;
        Node nextBucket; // наступний у бакеті хеш-таблиці (для колізій)

        Node before, after; // зв'язки двозв'язного списку для збереження порядку

        Node(Object key, int hash, Node nextBucket) {
            this.key = key;
            this.hash = hash;
            this.nextBucket = nextBucket;
        }
    }
    private Node[] table;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private Node head; // початок списку впорядкованості
    private Node tail; // кінець
    public MyLinkedHashSet() {
        table = new Node[DEFAULT_CAPACITY];
        size = 0;
    }
    private int hash(Object key) {
        return (key == null) ? 0 : Math.abs(key.hashCode());
    }
    private int getBucketIndex(int hash, int length) {
        return hash % length;
    }
    public boolean add(Object element) {
        int h = hash(element);
        int index = getBucketIndex(h, table.length);
        // Перевіряємо чи елемент уже існує
        for (Node e = table[index]; e != null; e = e.nextBucket) {
            if (e.hash == h && (e.key == element || (element != null && element.equals(e.key)))) {
                return false; // вже є в сеті
            }
        }
        // Створюємо новий вузол
        Node newNode = new Node(element, h, table[index]);
        table[index] = newNode;
        // Додаємо в кінець двозв'язного списку
        linkNodeAtEnd(newNode);
        size++;
        if ((float) size / table.length >= LOAD_FACTOR) {
            resize();
        }
        return true;
    }
    private void linkNodeAtEnd(Node node) {
        Node last = tail;
        tail = node;
        if (last == null) {
            head = node;
        } else {
            node.before = last;
            last.after = node;
        }
    }
    public boolean contains(Object element) {
        int h = hash(element);
        int index = getBucketIndex(h, table.length);
        for (Node e = table[index]; e != null; e = e.nextBucket) {
            if (e.hash == h && (e.key == element || (element != null && element.equals(e.key)))) {
                return true;
            }
        }
        return false;
    }
    public boolean remove(Object element) {
        int h = hash(element);
        int index = getBucketIndex(h, table.length);
        Node prev = null;
        Node e = table[index];
        while (e != null) {
            if (e.hash == h && (e.key == element || (element != null && element.equals(e.key)))) {
                if (prev == null) {
                    table[index] = e.nextBucket;
                } else {
                    prev.nextBucket = e.nextBucket;
                }
                // Видаляємо зі списку порядку
                unlinkNode(e);
                size--;
                return true;
            }
            prev = e;
            e = e.nextBucket;
        }
        return false;
    }
    private void unlinkNode(Node node) {
        Node p = node.before;
        Node n = node.after;
        if (p == null) {
            head = n;
        } else {
            p.after = n;
            node.before = null;
        }
        if (n == null) {
            tail = p;
        } else {
            n.before = p;
            node.after = null;
        }
    }
    private void resize() {
        int newCapacity = table.length * 2;
        Node[] newTable = new Node[newCapacity];
        // Перебудовуємо бакети
        for (Node curr = head; curr != null; curr = curr.after) {
            int index = getBucketIndex(curr.hash, newCapacity);
            curr.nextBucket = newTable[index];
            newTable[index] = curr;
        }
        table = newTable;
    }
    public int size() {
        return size;
    }
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node x = head; x != null; x = x.after) {
            result[i++] = x.key;
        }
        return result;
    }
}