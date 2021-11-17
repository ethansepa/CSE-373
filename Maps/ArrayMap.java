package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 8;
    SimpleEntry<K, V>[] entries;
    int size;

    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        size = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        Iterator<Entry<K, V>> iter = this.iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> i = iter.next();
            if (Objects.equals(key, i.getKey())) {
                return i.getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (size() + 1 == entries.length) {
            entries = resize(entries.length);
        }

        int index = 0;
        Iterator<Entry<K, V>> iter = this.iterator();
        while (iter.hasNext()) {
            Entry<K, V> i = iter.next();
            if (Objects.equals(key, i.getKey())) {
                V retval = entries[index].getValue();
                entries[index].setValue(value);
                return retval;
            }
            index++;
        }
        entries[index] = new SimpleEntry<>(key, value);
        size++;
        return null;
    }

    private SimpleEntry<K, V>[] resize(int oldSize) {
        int newSize = oldSize * 2;
        SimpleEntry<K, V>[] retval = new SimpleEntry[newSize];

        for (int i = 0; i < oldSize; i++) {
            retval[i] = entries[i];
        }

        return retval;
    }

    @Override
    public V remove(Object key) {
        int index = 0;

        if (!containsKey(key)) {
            return null;
        }

        Iterator<Entry<K, V>> iter = this.iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> i = iter.next();
            if (Objects.equals(key, i.getKey())) {
                break;
            }
            index++;
        }

        V retval = entries[index].getValue();
        if (size != 1) {
            entries[index] = entries[size - 1];
            entries[size - 1] = null;
        } else {
            entries[index] = null;
        }
        size--;
        return retval;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            entries[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (size == 0) {
            return false;
        }

        Iterator<Entry<K, V>> iter = this.iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> i = iter.next();
            if (Objects.equals(key, i.getKey())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ArrayMapIterator<>(this.entries);
    }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        int i;

        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            this.entries = entries;
            this.i = 0;
        }

        @Override
        public boolean hasNext() {
            if (entries[i] != null) {
                return true;
            }
            return false;
        }

        @Override
        public Map.Entry<K, V> next() throws NoSuchElementException {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            i++;
            return entries[i - 1];
        }
    }
}
