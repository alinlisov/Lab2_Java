import java.util.Arrays;
public class MyArrayList implements MyList, RandomAccess {
    private Object[] elementData;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;
    public MyArrayList() {
        this.elementData = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }
    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        this.elementData = new Object[initialCapacity];
        this.size = 0;
    }
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elementData.length) {
            int newCapacity = elementData.length + (elementData.length >> 1); // збільшення в 1.5 раза
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = Arrays.copyOf(elementData, newCapacity);
        }
    }
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

    @Override
    public void add(Object e) {
        ensureCapacity(size + 1);
        elementData[size++] = e;
    }
    @Override
    public void add(int index, Object element) {
        checkIndexForAddition(index);
        ensureCapacity(size + 1);
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        elementData[index] = element;
        size++;
    }
    @Override
    public void addAll(Object[] c) {
        if (c == null || c.length == 0) return;
        ensureCapacity(size + c.length);
        System.arraycopy(c, 0, elementData, size, c.length);
        size += c.length;
    }
    @Override
    public void addAll(int index, Object[] c) {
        checkIndexForAddition(index);
        if (c == null || c.length == 0) return;
        ensureCapacity(size + c.length);
        int numMoved = size - index;
        if (numMoved > 0) {
            System.arraycopy(elementData, index, elementData, index + c.length, numMoved);
        }
        System.arraycopy(c, 0, elementData, index, c.length);
        size += c.length;
    }
    @Override
    public Object get(int index) {
        checkIndex(index);
        return elementData[index];
    }
    @Override
    public Object remove(int index) {
        checkIndex(index);
        Object oldValue = elementData[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }
        elementData[--size] = null; // очищення для GC
        return oldValue;
    }
    @Override
    public void set(int index, Object element) {
        checkIndex(index);
        elementData[index] = element;
    }
    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (elementData[i] == null) return i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(elementData[i])) return i;
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
        return Arrays.copyOf(elementData, size);
    }
}