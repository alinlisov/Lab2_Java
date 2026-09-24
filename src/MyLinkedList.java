public class MyLinkedList implements MyList {
    private static class Node {
        Object item;
        Node next;
        Node prev;
        Node(Node prev, Object element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
    private Node first;
    private Node last;
    private int size = 0;

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
    private void checkIndexForAddition(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
    private Node node(int index) {
        // Оптимізація пошуку з початку або з кінця
        if (index < (size >> 1)) {
            Node x = first;
            for (int i = 0; i < index; i++) x = x.next;
            return x;
        } else {
            Node x = last;
            for (int i = size - 1; i > index; i--) x = x.prev;
            return x;
        }
    }
    @Override
    public void add(Object e) {
        Node l = last;
        Node newNode = new Node(l, e, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        size++;
    }
    @Override
    public void add(int index, Object element) {
        checkIndexForAddition(index);
        if (index == size) {
            add(element);
        } else {
            Node succ = node(index);
            Node pred = succ.prev;
            Node newNode = new Node(pred, element, succ);
            succ.prev = newNode;
            if (pred == null) {
                first = newNode;
            } else {
                pred.next = newNode;
            }
            size++;
        }
    }
    @Override
    public void addAll(Object[] c) {
        if (c == null) return;
        for (Object item : c) {
            add(item);
        }
    }
    @Override
    public void addAll(int index, Object[] c) {
        checkIndexForAddition(index);
        if (c == null || c.length == 0) return;
        int currentIndex = index;
        for (Object item : c) {
            add(currentIndex++, item);
        }
    }
    @Override
    public Object get(int index) {
        checkIndex(index);
        return node(index).item;
    }
    @Override
    public Object remove(int index) {
        checkIndex(index);
        Node x = node(index);
        Object element = x.item;
        Node next = x.next;
        Node prev = x.prev;
        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
        x.item = null;
        size--;
        return element;
    }
    @Override
    public void set(int index, Object element) {
        checkIndex(index);
        Node x = node(index);
        x.item = element;
    }
    @Override
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node x = first; x != null; x = x.next) {
                if (x.item == null) return index;
                index++;
            }
        } else {
            for (Node x = first; x != null; x = x.next) {
                if (o.equals(x.item)) return index;
                index++;
            }
        }
        return -1;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node x = first; x != null; x = x.next) {
            result[i++] = x.item;
        }
        return result;
    }
}