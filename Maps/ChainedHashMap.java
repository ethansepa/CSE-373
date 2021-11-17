package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 1;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 7;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 8;

    /*
    Warning:
    DO NOT rename this `chains` field or change its type.
    We will be inspecting it in our Gradescope-only tests.

    An explanation of this field:
    - `chains` is the main array where you're going to store all of your data (see the [] square bracket notation)
    - The other part of the type is the AbstractIterableMap<K, V> -- this is saying that `chains` will be an
    array that can store an AbstractIterableMap<K, V> object at each index.
       - AbstractIterableMap represents an abstract/generalized Map. The ArrayMap you wrote in the earlier part
       of this project qualifies as one, as it extends the AbstractIterableMap class.  This means you can
       and should be creating ArrayMap objects to go inside your `chains` array as necessary. See the instructions on
       the website for diagrams and more details.
        (To jump to its details, middle-click or control/command-click on AbstractIterableMap below)
     */
    double resizingLoadFactorThreshold;
    int chainInitialCapacity;
    AbstractIterableMap<K, V>[] chains;
    int size;

    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        this.resizingLoadFactorThreshold = resizingLoadFactorThreshold;
        this.chainInitialCapacity = chainInitialCapacity;
        this.size = 0;
        this.chains = new AbstractIterableMap[initialChainCount];
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        int index = getIndex(key);
        if (chains[index] == null) {
            return null;
        }
        return chains[index].get(key);
    }

    @Override
    public V put(K key, V value) {
        //Next three lines caused two errors
        if (((size + 1.0) / chains.length) >= resizingLoadFactorThreshold) {
            chains = resize();
        }

        int index = getIndex(key);

        if (chains[index] == null) {
            chains[index] = new ArrayMap<>(chainInitialCapacity);
        }

        if (!containsKey(key)) {
            size++;
        }
        return chains[index].put(key, value);
    }

    private int getIndex(Object key) {
        return getIndex(key, chains.length);
    }

    private int getIndex(Object key, int length) {
        int index;
        if (key == null) {
            index = 0;
        } else {
            index = key.hashCode();
        }

        if (index < 0) {
            index = index * -1;
        }

        return index % length;
    }

    private AbstractIterableMap<K, V>[] resize() {
        AbstractIterableMap<K, V>[] retval = new AbstractIterableMap[chains.length * 2];

        Iterator<Map.Entry<K, V>> iter = this.iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> data = iter.next();
            int newIndex = getIndex(data.getKey(), chains.length * 2);
            if (retval[newIndex] == null) {
                retval[newIndex] = new ArrayMap<>(chainInitialCapacity);
            }
            retval[newIndex].put(data.getKey(), data.getValue());
        }

        return retval;
    }

    @Override
    public V remove(Object key) {
        int index = getIndex(key);

        if (chains[index] == null) {
            return null;
        }

        if (containsKey(key)) {
            size--;
        }

        return chains[index].remove(key);
    }

    @Override
    public void clear() {
        for (int i = 0; i < chains.length; i++) {
            chains[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        int index = getIndex(key);

        if (chains[index] == null) {
            return false;
        }

        return chains[index].containsKey(key);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    /*
    // Doing so will give you a better string representation for assertion errors the debugger.
    @Override
    public String toString() {
        return super.toString();
    }
    */

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        //Each index in the array of chains is null iff that chain has no entries.
        //The currentChain field of the iterator always references the current chain being iterated through
        //  (the chain which contains the next entry that next will return).

        private AbstractIterableMap<K, V>[] chains;
        private int currentChain;
        private Iterator<Entry<K, V>> chainIterator;

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) throws NoSuchElementException {
            this.chains = chains;
            this.currentChain = 0;
            if (chains[currentChain] == null) {
                findNewCurrentChain();
            }
            if (currentChain < chains.length) {
                this.chainIterator = chains[currentChain].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            while (currentChain < chains.length) {
                if (chainIterator.hasNext()) {
                    return true;
                }
                findNewCurrentChain();
                if (currentChain < chains.length) {
                    chainIterator = chains[currentChain].iterator();
                }
            }
            return false;
        }

        public void findNewCurrentChain() {
            currentChain++;
            while (currentChain < chains.length) {
                if (chains[currentChain] != null) {
                    break;
                }
                currentChain++;
            }
        }

        @Override
        public Map.Entry<K, V> next() throws NoSuchElementException {
            if (hasNext()) {
                return chainIterator.next();
            }
            throw new NoSuchElementException();
        }
    }
}
