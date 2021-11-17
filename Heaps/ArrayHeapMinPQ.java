package priorityqueues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ArrayHeapMinPQ<T extends Comparable<T>> implements ExtrinsicMinPQ<T> {
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code. Pull this
    static final int START_INDEX = 1;
    List<PriorityNode<T>> items;
    HashMap<T, Integer> itemToIndexMap;
    int size;

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        itemToIndexMap = new HashMap<>();
        items.add(0, null);
        size = 0;
    }

    private void swap(int a, int b) {
        itemToIndexMap.put(items.get(a).getItem(), b);
        itemToIndexMap.put(items.get(b).getItem(), a);
        PriorityNode temp = items.get(a);
        items.set(a, items.get(b));
        items.set(b, temp);
    }

    @Override
    public void add(T item, double priority) throws IllegalArgumentException {
        if (contains(item) || Objects.equals(item, null)) {
            throw new IllegalArgumentException();
        }

        PriorityNode newNode = new PriorityNode(item, priority);
        itemToIndexMap.put(item, size + 1);
        items.add(newNode);
        size++;
        percolateUp(size, priority);
    }

    public boolean canPercolateUp(int index, double priority) {
        if (index > 1 && priority < items.get(index / 2).getPriority()) {
            return true;
        }
        return false;
    }

    public void percolateUp(int index, double priority) {
        while (canPercolateUp(index, priority)) {
            swap(index, index / 2);
            index = index / 2;
        }
    }

    @Override
    public boolean contains(T item) {
        return itemToIndexMap.containsKey(item);
    }

    @Override
    public T peekMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return items.get(START_INDEX).getItem();
    }

    @Override
    public T removeMin() throws NoSuchElementException {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        T retval = items.get(START_INDEX).getItem();
        if (size > START_INDEX) {
            swap(START_INDEX, size);
            items.remove(size);
            itemToIndexMap.remove(retval);
            size--;
            percolateDown(START_INDEX, items.get(START_INDEX).getPriority());
        } else {
            items.remove(START_INDEX);
            itemToIndexMap.remove(retval);
            size--;
        }
        return retval;
    }

    private double potentialPriority(int index) {
        if (size >= index) {
            return items.get(index).getPriority();
        }
        return Double.MAX_VALUE;
    }

    public boolean canPercolateDown(int index, double priority) {
        double leftPriority = potentialPriority(index * 2);
        double rightPriority = potentialPriority(index * 2 + 1);

        if (priority > leftPriority || priority > rightPriority) {
            return true;
        }
        return false;
    }

    public void percolateDown(int index, double priority) {
        double lChildP = 0;
        double rChildP = 0;

        while (canPercolateDown(index, priority)) {
            lChildP = potentialPriority(index * 2);
            rChildP = potentialPriority(index * 2 + 1);
            if (lChildP < rChildP) {
                swap(index, index * 2);
                index = index * 2;
            } else {
                swap(index, index * 2 + 1);
                index = index * 2 + 1;
            }
        }
    }


    @Override
    public void changePriority(T item, double priority) throws NoSuchElementException {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }

        int index = itemToIndexMap.get(item);
        items.get(index).setPriority(priority);
        percolateUp(index, priority);
        percolateDown(index, priority);

    }

    @Override
    public int size() {
        return size;
    }
}
